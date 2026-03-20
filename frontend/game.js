// game.js — API calls and state management

const POST_HEADERS = { 'Content-Type': 'application/json' };

async function apiCall(url, body) {
    const res = body !== undefined
        ? await fetch(url, { method: 'POST', headers: POST_HEADERS, body: JSON.stringify(body) })
        : await fetch(url);
    return res.json();
}

function handleError(data, msg) {
    if (data.error) { alert(msg ? `${msg}: ${data.error}` : data.error); return true; }
    return false;
}

const API = {
    getState()                 { return apiCall('/state'); },
    getClans()                 { return apiCall('/clans'); },
    getMissions()              { return apiCall('/missions'); },
    getRewards()               { return apiCall('/rewards'); },
    setPlayerClan(clan)        { return apiCall('/player/clan',    { clan }); },
    placeTower(clan)           { return apiCall('/tower/place',    { clan }); },
    upgradeTower(slot, module) { return apiCall('/tower/upgrade',  { slot, module }); },
    setTargeting(slot, mode)   { return apiCall('/tower/targeting',{ slot, mode }); },
    startMission(missionId)    { return apiCall('/mission/start',  { missionId }); },
    nextWave()                 { return apiCall('/wave/next', {}); },
    tickWave()                 { return apiCall('/wave/tick', {}); },
    collectReward(cardId)      { return apiCall('/reward/collect', { cardId }); },
    mergeJutsu(jutsuName)      { return apiCall('/jutsu/merge',    { jutsuName }); },
};

// ── Combat loop ────────────────────────────────────────────────────────────

let combatInterval = null;

function startCombatLoop() {
    if (combatInterval) return;
    document.getElementById('btn-next-wave').disabled = true;

    combatInterval = setInterval(async () => {
        let result;
        try { result = await API.tickWave(); }
        catch { return; }   // network hiccup — try again next tick

        if (result.error) {
            stopCombatLoop();
            return;
        }

        // Animate enemies on canvas
        Renderer.renderEnemies(result.enemies || []);

        // Update HUD
        document.getElementById('lives').textContent = result.lives ?? '--';
        document.getElementById('coins').textContent = result.coins ?? '--';

        if (result.waveComplete) {
            stopCombatLoop();

            if (!result.missionComplete && result.wave < result.totalWaves) {
                // Between waves: re-enable the Next Wave button
                document.getElementById('wave-current').textContent = result.wave;
                document.getElementById('btn-next-wave').disabled = false;
                await refreshState();   // update tower chakra display
            } else {
                // Mission over (all waves done or lives = 0)
                document.getElementById('wave-current').textContent = result.wave;
                await refreshState();
            }

            // Always offer a reward draw after a completed wave
            const cards = await API.getRewards();
            if (cards && cards.length) Renderer.renderRewardPanel(cards);
        }
    }, 100);   // 10 ticks per second
}

function stopCombatLoop() {
    if (combatInterval) {
        clearInterval(combatInterval);
        combatInterval = null;
    }
}

// ── UI wiring ─────────────────────────────────────────────────────────────

async function refreshState() {
    const state = await API.getState();
    Renderer.renderHud(state);
    Renderer.renderTowers(state.activeTowers || []);
    Renderer.renderJutsu(state.inventory || {});
    const count = (state.activeTowers || []).length;
    document.getElementById('tower-count').textContent = `(${count}/4)`;
}

document.getElementById('btn-place-tower').addEventListener('click', async () => {
    const clan = document.getElementById('clan-select').value;
    const data = await API.placeTower(clan);
    if (handleError(data)) return;
    await refreshState();
});

document.getElementById('btn-next-wave').addEventListener('click', async () => {
    const data = await API.nextWave();
    if (handleError(data)) return;
    document.getElementById('wave-current').textContent = data.wave;
    document.getElementById('wave-total').textContent   = data.totalWaves || data.wave;
    startCombatLoop();
});

// ── Clan selection screen ─────────────────────────────────────────────────

async function initClanSelection() {
    const clanData = await API.getClans();
    const clans = clanData.clans || [];
    const grid  = document.getElementById('clan-cards-grid');
    grid.innerHTML = '';

    clans.forEach(clan => {
        const card = document.createElement('div');
        card.className = 'clan-card';
        card.dataset.clan = clan.clan;

        card.innerHTML = `
            <div class="clan-cube" data-clan="${clan.clan.toLowerCase()}">
                <div class="cube-head"></div>
                <div class="cube-torso"></div>
                <div class="cube-legs"></div>
            </div>
            <h3>${formatClanName(clan.clan)}</h3>
            <p class="clan-passive"><strong>Passive:</strong> ${clan.passive}</p>
            <p class="clan-jutsu"><em>${clan.jutsuTheme}</em></p>
        `;

        card.addEventListener('click', async () => {
            const result = await API.setPlayerClan(clan.clan);
            if (handleError(result)) return;
            document.getElementById('clan-selection-screen').classList.add('hidden');
            document.getElementById('game-wrapper').classList.remove('hidden');
            await startGame();
        });

        grid.appendChild(card);
    });
}

function formatClanName(clanKey) {
    const names = {
        UZUMAKI: 'Uzumaki',
        UCHIHA:  'Uchiha',
        HYUGA:   'Hyuga',
        NARA:    'Nara',
        INUZUKA: 'Inuzuka',
        INOICHI: 'Inoichi (Yamanaka)'
    };
    return names[clanKey] || clanKey;
}

// ── Game init (called after clan selection) ───────────────────────────────

async function startGame() {
    await loadMissions();
    await refreshState();
}

async function loadMissions() {
    const missions = await API.getMissions();
    Renderer.renderMissions(missions, async (id, totalWaves) => {
        stopCombatLoop();
        const data = await API.startMission(id);
        if (handleError(data)) return;
        document.getElementById('wave-total').textContent   = data.totalWaves || totalWaves;
        document.getElementById('wave-current').textContent = '0';
        document.getElementById('btn-next-wave').disabled   = false;
        // Clear the canvas for the new mission
        Renderer.clearCanvas();
        document.getElementById('reward-panel').classList.add('hidden');
        await refreshState();
    });
}

// Boot
initClanSelection();
