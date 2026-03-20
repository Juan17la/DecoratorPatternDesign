package com.naruto.game;

import com.naruto.enemy.Enemy;
import com.naruto.tower.Tower;
import com.naruto.wave.WaveManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Stateless combat tick engine.
 *
 * One call to {@link #tick} advances the simulation by {@link #TICK_DELTA_S} seconds:
 *   1. Enemies move forward along the path.
 *   2. Enemies that reach the base reduce the player's lives.
 *   3. Towers with expired cooldowns select targets and attack.
 *   4. Defeated enemies yield coins.
 *
 * Attack priority (per 03_combat_system.md):
 *   a) Shuriken  — if the tower has a ShurikenModule (targetCount > 1) AND
 *                  chakra ≥ SHURIKEN_CHAKRA_COST
 *   b) Kunai     — single-target fallback, always free
 *
 * Speed scaling: raw EnemyType.baseSpeed values represent relative speed on a
 * tile-based grid (~20 tiles).  SPEED_SCALE converts them to path-fraction/sec
 * so a mid-speed enemy crosses the 0→1 path in roughly 20 seconds.
 */
public final class CombatEngine {

    /** Simulated seconds advanced per tick (100 ms real time). */
    public static final float TICK_DELTA_S = 0.1f;

    /**
     * Converts raw EnemyType speed to path-fraction per second.
     * speed=1.0 → 0.05 path/sec → ~20 s to cross.
     */
    private static final float SPEED_SCALE = 0.05f;

    /** Chakra cost for a shuriken (multi-target) attack. */
    private static final int SHURIKEN_CHAKRA_COST = 5;

    // ── public API ────────────────────────────────────────────────────────────

    /**
     * Advances combat by one tick.
     *
     * @param towers      currently placed towers (ordered by slot index)
     * @param wm          the active WaveManager (holds enemy list + combat maps)
     * @param state       global game state (lives, coins)
     * @return a snapshot of the game state after this tick
     */
    public static TickResult tick(List<Tower> towers, WaveManager wm, GameStateManager state) {
        List<Enemy>          enemies   = wm.getCurrentWave();
        Map<Integer, Float>  cooldowns = wm.getTowerCooldowns();
        Map<Integer, Integer> chakraMap = wm.getTowerChakra();

        // 1. Advance enemies and detect base breaches
        for (Enemy e : enemies) {
            if (!e.isAlive() || e.hasReachedBase()) continue;
            e.advance(TICK_DELTA_S * SPEED_SCALE);
            if (e.hasReachedBase()) {
                state.loseLife();
                e.collectCoinReward();   // no coins for enemies that breach
            }
        }

        // 2. Tower attacks
        for (int slot = 0; slot < towers.size(); slot++) {
            Tower tower = towers.get(slot);

            // Reduce cooldown; fire if ready
            float cd = cooldowns.getOrDefault(slot, 0.0f) - TICK_DELTA_S;
            if (cd > 0.0f) {
                cooldowns.put(slot, cd);
                continue;
            }

            // Chakra: initialise lazily on first tick of the wave
            int chakra = chakraMap.computeIfAbsent(slot, k -> tower.getMaxChakra());

            // Apply chakra regeneration
            float regen = tower.getChakraRegen();
            if (regen > 0) {
                chakra = Math.min(tower.getMaxChakra(), chakra + (int)(regen * TICK_DELTA_S));
            }

            List<Enemy> targets = selectTargets(tower, enemies);
            if (targets.isEmpty()) {
                // No valid targets — still reset cooldown so we keep checking
                cooldowns.put(slot, 1.0f / tower.getAttackSpeed());
                chakraMap.put(slot, chakra);
                continue;
            }

            // Attack priority: shuriken (multi) if module equipped and chakra available
            if (tower.getTargetCount() > 1 && chakra >= SHURIKEN_CHAKRA_COST) {
                // Shuriken: hit up to targetCount enemies
                int hits = Math.min(tower.getTargetCount(), targets.size());
                for (int t = 0; t < hits; t++) {
                    targets.get(t).takeDamage(tower.getDamage());
                }
                chakra -= SHURIKEN_CHAKRA_COST;
            } else {
                // Kunai: single-target (uses tower.attack so clan passives fire)
                tower.attack(targets.get(0));
            }

            // Sync chakra back into the tower and the map
            chakraMap.put(slot, Math.max(0, chakra));
            tower.setChakra(chakra);

            // Reset attack cooldown
            cooldowns.put(slot, 1.0f / tower.getAttackSpeed());
        }

        // 3. Collect coins from newly defeated enemies
        for (Enemy e : enemies) {
            if (!e.isAlive()) {
                int coins = e.collectCoinReward();
                if (coins > 0) state.addCoins(coins);
            }
        }

        // 4. Check wave-complete condition
        boolean waveComplete = enemies.stream()
            .allMatch(e -> !e.isAlive() || e.hasReachedBase());

        return new TickResult(
            waveComplete,
            wm.getCurrentWaveNumber(),
            wm.getTotalWaves(),
            state.getLives(),
            state.getCoins(),
            enemies
        );
    }

    // ── targeting ─────────────────────────────────────────────────────────────

    /**
     * Returns the enemy list sorted by the tower's targeting priority.
     * Invisible enemies are excluded (they cannot be targeted).
     */
    private static List<Enemy> selectTargets(Tower tower, List<Enemy> enemies) {
        List<Enemy> alive = enemies.stream()
            .filter(e -> e.isAlive() && !e.hasReachedBase() && !e.isInvisible())
            .collect(Collectors.toCollection(ArrayList::new));

        if (alive.isEmpty()) return alive;

        Comparator<Enemy> cmp = switch (tower.getTargetingMode()) {
            // FIRST: farthest along the path = closest to the base
            case FIRST -> Comparator.comparingDouble(Enemy::getPathProgress).reversed();
            // STRONGEST: most remaining HP
            case STRONGEST -> Comparator.comparingInt(Enemy::getCurrentHp).reversed();
            // FASTEST: highest speed stat
            case FASTEST -> Comparator.comparingDouble(Enemy::getSpeed).reversed();
            // CLUSTERED: proximity to other enemies (most neighbours within 0.1 path units)
            case CLUSTERED -> Comparator.comparingInt(
                (Enemy e) -> clusterScore(e, alive)).reversed();
        };

        alive.sort(cmp);
        return alive;
    }

    /** Counts how many other enemies are within 0.1 path-units of this one. */
    private static int clusterScore(Enemy e, List<Enemy> all) {
        int count = 0;
        for (Enemy other : all) {
            if (other != e && Math.abs(other.getPathProgress() - e.getPathProgress()) <= 0.1f) {
                count++;
            }
        }
        return count;
    }

    private CombatEngine() {}
}
