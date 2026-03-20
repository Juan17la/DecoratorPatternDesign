// renderer.js — DOM rendering helpers

const Renderer = (() => {

    function renderHud(state) {
        const p = state.player || state;
        document.getElementById('lives').textContent = p.lives ?? state.lives ?? '--';
        document.getElementById('coins').textContent = p.coins ?? state.coins ?? '--';
        document.getElementById('clan').textContent  = p.clan  ?? state.playerClan ?? '--';
        document.getElementById('rank').textContent  = p.rank  ?? state.playerRank ?? '--';
    }

    function renderTowers(towers) {
        const container = document.getElementById('tower-list');
        if (!towers.length) { container.innerHTML = '<p style="opacity:.5">No towers placed.</p>'; return; }
        container.innerHTML = towers.map((t, i) => `
            <div class="tower-card">
                <strong>Slot ${i}</strong> — ${t.description || 'Tower'}<br/>
                DMG: ${t.damage ?? '--'} | SPD: ${(t.attackSpeed ?? 0).toFixed(2)} | RNG: ${t.range ?? '--'}<br/>
                Chakra: ${t.chakra ?? 0}/${t.maxChakra ?? 0}<br/>
                <span class="modules">${(t.appliedModules || []).join(', ') || 'No modules'}</span><br/>
                <button onclick="upgradePrompt(${i})">+ Module</button>
            </div>
        `).join('');
    }

    function renderEnemies(enemies) {
        const canvas = document.getElementById('game-canvas');
        const ctx = canvas.getContext('2d');
        ctx.clearRect(0, 0, canvas.width, canvas.height);

        // draw path
        ctx.strokeStyle = '#f5a623';
        ctx.lineWidth = 6;
        ctx.beginPath();
        ctx.moveTo(0, canvas.height / 2);
        ctx.lineTo(canvas.width, canvas.height / 2);
        ctx.stroke();

        // draw enemies
        enemies.forEach(e => {
            const x = e.pathProgress * canvas.width;
            const y = canvas.height / 2;
            const ratio = e.currentHp / e.maxHp;

            ctx.fillStyle = ratio > 0.5 ? '#e94560' : '#f5a623';
            ctx.beginPath();
            ctx.arc(x, y, 10, 0, Math.PI * 2);
            ctx.fill();

            // HP bar
            ctx.fillStyle = '#333';
            ctx.fillRect(x - 12, y - 20, 24, 4);
            ctx.fillStyle = '#4caf50';
            ctx.fillRect(x - 12, y - 20, 24 * ratio, 4);
        });
    }

    const SPECIAL_TYPES = new Set(['CHUNIN_EXAM_1','CHUNIN_EXAM_2','CHUNIN_EXAM_3','JONIN_PROMOTION']);

    function renderMissions(missions, onStart) {
        const container = document.getElementById('mission-list');
        if (!missions.length) {
            container.innerHTML = '<p style="opacity:.5">No missions available at your rank.</p>';
            return;
        }
        container.innerHTML = missions.map(m => {
            const waves    = m.totalWaves ?? m.waves ?? '?';
            const livesTag = m.livesLostLimit != null && m.livesLostLimit >= 0
                ? `<span class="tag" style="background:#9b2e2e">≤${m.livesLostLimit} lives lost</span>` : '';
            const examTag  = SPECIAL_TYPES.has(m.type)
                ? `<span class="tag" style="background:#4b3f8f">Special</span>` : '';
            return `
                <div class="mission-card">
                    <strong>${m.name}</strong>
                    <span class="tag">Rank ${m.rank}</span>${examTag}${livesTag}<br/>
                    Waves: ${waves} &nbsp;|&nbsp; Reward: ${m.coinReward ?? 0} coins
                    <br/><button class="btn-start-mission" data-id="${m.id}" data-waves="${waves}">Start</button>
                </div>
            `;
        }).join('');
        container.querySelectorAll('.btn-start-mission').forEach(btn => {
            btn.addEventListener('click', () => onStart(btn.dataset.id, btn.dataset.waves));
        });
    }

    function renderJutsu(inventory) {
        const container = document.getElementById('jutsu-list');
        const names = Object.keys(inventory.copies || {});
        if (!names.length) { container.innerHTML = '<p style="opacity:.5">No jutsus.</p>'; return; }
        container.innerHTML = names.map(name => {
            const copies = inventory.copies[name] || 0;
            const level  = inventory.levels[name]  || 1;
            return `
                <div class="jutsu-card">
                    <strong>${name}</strong> Lv.${level}<br/>
                    Copies: ${copies}
                    ${copies >= 2 ? `<button class="btn-merge" data-name="${name}">Merge</button>` : ''}
                </div>
            `;
        }).join('');
        container.querySelectorAll('.btn-merge').forEach(btn => {
            btn.addEventListener('click', () => mergeJutsu(btn.dataset.name));
        });
    }

    function renderRewardPanel(cards) {
        const panel = document.getElementById('reward-panel');
        const container = document.getElementById('reward-cards');
        panel.classList.remove('hidden');
        container.innerHTML = cards.map(c => `
            <div class="reward-card" data-id="${c.id}">
                <strong>${c.name}</strong>
                <span class="tag">${c.type}</span><br/>
                <small>${c.value}</small>
            </div>
        `).join('');
        container.querySelectorAll('.reward-card').forEach(card => {
            card.addEventListener('click', () => collectReward(card.dataset.id));
        });
    }

    return { renderHud, renderTowers, renderEnemies, renderMissions, renderJutsu, renderRewardPanel };
})();

async function upgradePrompt(slot) {
    const module = prompt('Enter module name (RapidFire / Freeze / FireStyle):');
    if (!module) return;
    const data = await API.upgradeTower(slot, module);
    if (handleError(data)) return;
    await refreshState();
}

async function mergeJutsu(name) {
    const data = await API.mergeJutsu(name);
    if (handleError(data)) return;
    alert(`${name} merged to level ${data.level}!`);
    await refreshState();
}

async function collectReward(cardId) {
    const data = await API.collectReward(cardId);
    if (handleError(data)) return;
    document.getElementById('reward-panel').classList.add('hidden');
    await refreshState();
}
