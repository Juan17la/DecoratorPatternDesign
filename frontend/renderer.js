// renderer.js — DOM rendering helpers

const Renderer = (() => {

    // ── HUD ──────────────────────────────────────────────────────────────────

    function renderHud(state) {
        const lives = state.lives  ?? '--';
        const coins = state.coins  ?? '--';
        const clan  = state.playerClan ?? state.clan  ?? '--';
        const rank  = state.playerRank ?? state.rank  ?? '--';
        document.getElementById('lives').textContent = lives;
        document.getElementById('coins').textContent = coins;
        document.getElementById('clan').textContent  = clan;
        document.getElementById('rank').textContent  = rank;
    }

    // ── Tower panel ───────────────────────────────────────────────────────────

    function renderTowers(towers) {
        const container = document.getElementById('tower-list');
        if (!towers.length) {
            container.innerHTML = '<p style="opacity:.5">No towers placed.</p>';
            return;
        }
        container.innerHTML = towers.map((t, i) => {
            const slot         = t.slot ?? i;
            const chakraBar    = renderChakraBar(t.chakra ?? 0, t.maxChakra ?? 100);
            const moduleList   = (t.appliedModules || []).join(', ') || 'None';
            const currentMode  = t.targetingMode || 'FIRST';

            return `
            <div class="tower-card">
                <strong>Slot ${slot}</strong> — ${t.description || 'Tower'}<br/>
                DMG: ${t.damage ?? '--'} &nbsp;|&nbsp;
                SPD: ${(t.attackSpeed ?? 0).toFixed(2)} APS &nbsp;|&nbsp;
                RNG: ${t.range ?? '--'} &nbsp;|&nbsp;
                Targets: ${t.targetCount ?? 1}
                <br/>
                Chakra: ${chakraBar}
                <br/>
                <span class="modules">Modules: ${moduleList}</span>
                <br/>
                <label style="font-size:.75rem">Target:
                    <select class="targeting-select" data-slot="${slot}"
                            style="font-size:.75rem;padding:2px 4px;margin-left:4px">
                        ${['FIRST','STRONGEST','FASTEST','CLUSTERED'].map(m =>
                            `<option value="${m}"${m === currentMode ? ' selected' : ''}>${m}</option>`
                        ).join('')}
                    </select>
                </label>
                &nbsp;
                <button onclick="upgradePrompt(${slot})" style="padding:3px 8px;font-size:.75rem">+ Module</button>
            </div>`;
        }).join('');

        // Wire targeting selects
        container.querySelectorAll('.targeting-select').forEach(sel => {
            sel.addEventListener('change', async () => {
                const data = await API.setTargeting(parseInt(sel.dataset.slot), sel.value);
                if (data.error) alert(data.error);
            });
        });
    }

    function renderChakraBar(current, max) {
        const pct   = max > 0 ? Math.round((current / max) * 100) : 0;
        const color = pct > 50 ? '#4caf50' : pct > 20 ? '#f5a623' : '#e94560';
        return `<span title="${current}/${max} chakra"
            style="display:inline-block;width:80px;height:8px;
                   background:#333;border-radius:4px;vertical-align:middle">
            <span style="display:block;width:${pct}%;height:100%;
                         background:${color};border-radius:4px"></span>
        </span> ${current}/${max}`;
    }

    // ── Canvas / Enemy rendering ───────────────────────────────────────────────

    function clearCanvas() {
        const canvas = document.getElementById('game-canvas');
        const ctx    = canvas.getContext('2d');
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        drawPath(ctx, canvas);
    }

    function drawPath(ctx, canvas) {
        const mid = canvas.height / 2;
        // Background track
        ctx.fillStyle = '#1a3a1a';
        ctx.fillRect(0, mid - 15, canvas.width, 30);
        // Centre line
        ctx.strokeStyle = '#f5a623';
        ctx.lineWidth = 2;
        ctx.setLineDash([8, 6]);
        ctx.beginPath();
        ctx.moveTo(0, mid);
        ctx.lineTo(canvas.width, mid);
        ctx.stroke();
        ctx.setLineDash([]);
        // Start / End markers
        ctx.fillStyle = '#4caf50';
        ctx.fillRect(0, mid - 15, 6, 30);
        ctx.fillStyle = '#e94560';
        ctx.fillRect(canvas.width - 6, mid - 15, 6, 30);
    }

    function renderEnemies(enemies) {
        const canvas = document.getElementById('game-canvas');
        const ctx    = canvas.getContext('2d');
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        drawPath(ctx, canvas);

        const mid = canvas.height / 2;

        enemies.forEach(e => {
            if (!e || (e.currentHp !== undefined && e.currentHp <= 0)) return;
            const x     = (e.pathProgress ?? 0) * canvas.width;
            const hp    = e.currentHp ?? e.maxHp ?? 1;
            const maxHp = e.maxHp ?? 1;
            const ratio = hp / maxHp;

            // Enemy body (circle — radius scales with HP ratio)
            const radius = 10 + (1 - ratio) * 2;
            ctx.beginPath();
            ctx.arc(x, mid, radius, 0, Math.PI * 2);
            ctx.fillStyle = enemyColor(e, ratio);
            ctx.fill();
            ctx.strokeStyle = 'rgba(255,255,255,0.3)';
            ctx.lineWidth = 1;
            ctx.stroke();

            // HP bar (above the circle)
            const barW  = 28;
            const barH  = 4;
            const barX  = x - barW / 2;
            const barY  = mid - radius - 8;
            ctx.fillStyle = '#222';
            ctx.fillRect(barX, barY, barW, barH);
            ctx.fillStyle = ratio > 0.5 ? '#4caf50' : ratio > 0.2 ? '#f5a623' : '#e94560';
            ctx.fillRect(barX, barY, barW * ratio, barH);

            // Status effect indicator
            const effects = e.activeEffects || [];
            if (effects.length) {
                ctx.font = '9px sans-serif';
                ctx.fillStyle = '#fff';
                ctx.fillText(effectIcon(effects[0]), x - 4, mid - radius - 10);
            }
        });
    }

    function enemyColor(e, ratio) {
        const rank = (e.rank || '').toString();
        if (rank === 'KAGE')   return `rgba(220,20,60,${0.7 + ratio * 0.3})`;
        if (rank === 'JONIN')  return `rgba(200,100,0,${0.7 + ratio * 0.3})`;
        if (rank === 'CHUNIN') return `rgba(180,160,0,${0.7 + ratio * 0.3})`;
        return `rgba(100,180,${Math.round(ratio * 200)},${0.7 + ratio * 0.3})`;
    }

    function effectIcon(effect) {
        const icons = { FREEZE:'❄', SLOW:'🐢', STUN:'⚡', BURN:'🔥', CONFUSION:'?' };
        return icons[effect] || '·';
    }

    // ── Mission panel ─────────────────────────────────────────────────────────

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

    // ── Jutsu panel ───────────────────────────────────────────────────────────

    function renderJutsu(inventory) {
        const container = document.getElementById('jutsu-list');
        const names     = Object.keys(inventory.copies || {});
        if (!names.length) {
            container.innerHTML = '<p style="opacity:.5">No jutsus.</p>';
            return;
        }
        container.innerHTML = names.map(name => {
            const copies = (inventory.copies || {})[name] || 0;
            const level  = (inventory.levels || {})[name]  || 1;
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

    // ── Reward panel ──────────────────────────────────────────────────────────

    function renderRewardPanel(cards) {
        const panel     = document.getElementById('reward-panel');
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

    return {
        renderHud,
        renderTowers,
        renderEnemies,
        clearCanvas,
        renderMissions,
        renderJutsu,
        renderRewardPanel
    };
})();

// ── Global helpers (called from inline onclick / game.js) ────────────────

async function upgradePrompt(slot) {
    const module = prompt(
        'Enter module name:\n' +
        'RapidFire | Shuriken | PiercingKunai | ExplosiveKunai\n' +
        'FireStyle | LightningStyle | WaterStyle | WindStyle | EarthStyle\n' +
        'Freeze | ChakraRegen | GoldGenerator | ClanAbility | ChakraArmor'
    );
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
