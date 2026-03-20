package com.naruto.wave;

import com.naruto.enemy.Enemy;
import com.naruto.enemy.EnemyFactory;
import com.naruto.enemy.EnemyType;
import com.naruto.mission.MissionRank;
import com.naruto.mission.MissionType;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates enemy waves based on mission rank, mission type, and wave number.
 *
 * Wave structures (per spec):
 *   D-rank — 3 waves: waves 1-2 = cats/dogs/flowers; wave 3 = + elders/builders
 *   C-rank — 4 waves: waves 1-3 = bandits/criminals/spies/mercenaries; wave 4 = Gang Leader guaranteed
 *   B-rank — 5 waves: waves 1-2 = ninjas; waves 3-4 = + Missing-Nin; wave 5 = Rogue Squad guaranteed
 *   A-rank — 6 waves: waves 1-3 = jonin/squads; waves 4-6 = elite threats; wave 6 = Elite Boss guaranteed
 */
public class WaveGenerator {

    public List<Enemy> generate(MissionRank rank, MissionType type, int waveNumber) {
        return switch (type) {
            case CHUNIN_EXAM_1  -> generateDRankWave(waveNumber);          // D enemies, 6 waves
            case CHUNIN_EXAM_2  -> generateChunin2Wave(waveNumber);        // Mixed D+C, 5 waves
            case CHUNIN_EXAM_3  -> generateCRankWave(waveNumber);          // C enemies, 4 waves
            case JONIN_PROMOTION -> generateJoninPromoWave(waveNumber);    // A enemies + promo final
            case NORMAL         -> switch (rank) {
                case D -> generateDRankWave(waveNumber);
                case C -> generateCRankWave(waveNumber);
                case B -> generateBRankWave(waveNumber);
                case A, S -> generateARankWave(waveNumber);
            };
        };
    }

    // ── D-rank waves ──────────────────────────────────────────────────────────

    private List<Enemy> generateDRankWave(int wave) {
        List<Enemy> enemies = new ArrayList<>();
        if (wave <= 2) {
            // Waves 1-2: mixed cats, dogs, flowers
            repeat(enemies, EnemyType.LOST_CAT,      wave, 4);
            repeat(enemies, EnemyType.STRAY_DOG,     wave, 3);
            repeat(enemies, EnemyType.EVIL_FLOWER,   wave, 3);
            if (wave == 2) {
                repeat(enemies, EnemyType.STUBBORN_ELDER, wave, 2);
            }
        } else {
            // Wave 3+: include elders and builders
            repeat(enemies, EnemyType.LOST_CAT,       wave, 2);
            repeat(enemies, EnemyType.STRAY_DOG,      wave, 2);
            repeat(enemies, EnemyType.EVIL_FLOWER,    wave, 2);
            repeat(enemies, EnemyType.STUBBORN_ELDER, wave, 3);
            repeat(enemies, EnemyType.ROGUE_BUILDER,  wave, 2);
        }
        return enemies;
    }

    // ── C-rank waves ──────────────────────────────────────────────────────────

    private List<Enemy> generateCRankWave(int wave) {
        List<Enemy> enemies = new ArrayList<>();
        if (wave < 4) {
            repeat(enemies, EnemyType.BANDIT,    wave, 3);
            repeat(enemies, EnemyType.CRIMINAL,  wave, 2);
            repeat(enemies, EnemyType.SPY,       wave, 2);
            if (wave >= 2) {
                repeat(enemies, EnemyType.MERCENARY, wave, 2);
            }
        } else {
            // Wave 4: Gang Leader guaranteed + supporting cast
            repeat(enemies, EnemyType.BANDIT,      wave, 3);
            repeat(enemies, EnemyType.MERCENARY,   wave, 2);
            enemies.add(EnemyFactory.create(EnemyType.GANG_LEADER, wave));
        }
        return enemies;
    }

    // ── Mixed D+C waves (Chunin Exam 2) ──────────────────────────────────────

    private List<Enemy> generateChunin2Wave(int wave) {
        List<Enemy> enemies = new ArrayList<>();
        // Early waves: D-rank enemies
        if (wave <= 2) {
            repeat(enemies, EnemyType.STRAY_DOG,  wave, 3);
            repeat(enemies, EnemyType.EVIL_FLOWER, wave, 3);
            repeat(enemies, EnemyType.BANDIT,      wave, 2);
        } else if (wave <= 4) {
            // Later waves: C-rank mixed in
            repeat(enemies, EnemyType.BANDIT,    wave, 3);
            repeat(enemies, EnemyType.CRIMINAL,  wave, 2);
            repeat(enemies, EnemyType.MERCENARY, wave, 2);
        } else {
            // Wave 5: C-rank heavy + mini-boss
            repeat(enemies, EnemyType.BANDIT,      wave, 3);
            repeat(enemies, EnemyType.MERCENARY,   wave, 2);
            enemies.add(EnemyFactory.create(EnemyType.GANG_LEADER, wave));
        }
        return enemies;
    }

    // ── B-rank waves ──────────────────────────────────────────────────────────

    private List<Enemy> generateBRankWave(int wave) {
        List<Enemy> enemies = new ArrayList<>();
        if (wave <= 2) {
            // Waves 1-2: basic ninja types
            repeat(enemies, EnemyType.LEAF_NINJA, wave, 3);
            repeat(enemies, EnemyType.STONE_NINJA, wave, 2);
            repeat(enemies, EnemyType.SAND_NINJA,  wave, 2);
        } else if (wave <= 4) {
            // Waves 3-4: Missing-Nin added
            repeat(enemies, EnemyType.LEAF_NINJA,  wave, 2);
            repeat(enemies, EnemyType.STONE_NINJA, wave, 2);
            repeat(enemies, EnemyType.SAND_NINJA,  wave, 2);
            repeat(enemies, EnemyType.MISSING_NIN, wave, 2);
        } else {
            // Wave 5: Rogue Squad (3-pack) guaranteed
            repeat(enemies, EnemyType.LEAF_NINJA,          wave, 2);
            repeat(enemies, EnemyType.MISSING_NIN,         wave, 2);
            repeat(enemies, EnemyType.ROGUE_SQUAD_MEMBER,  wave, 3);
        }
        return enemies;
    }

    // ── A-rank waves ──────────────────────────────────────────────────────────

    private List<Enemy> generateARankWave(int wave) {
        List<Enemy> enemies = new ArrayList<>();
        if (wave <= 3) {
            // Waves 1-3: jonin + chunin squads
            repeat(enemies, EnemyType.JONIN_KIRI,         wave, 2);
            repeat(enemies, EnemyType.CHUNIN_SQUAD_MEMBER, wave, 4);
            if (wave >= 2) {
                enemies.add(EnemyFactory.create(EnemyType.JONIN_IWA, wave));
            }
        } else if (wave <= 5) {
            // Waves 4-5: elite threats
            enemies.add(EnemyFactory.create(EnemyType.JONIN_KIRI, wave));
            enemies.add(EnemyFactory.create(EnemyType.JONIN_IWA,  wave));
            repeat(enemies, EnemyType.HIGH_MISSING_NIN,   wave, 2);
        } else {
            // Wave 6: Elite Boss always present + 2 Jonin
            enemies.add(EnemyFactory.create(EnemyType.ELITE_BOSS, wave));
            enemies.add(EnemyFactory.create(EnemyType.JONIN_KIRI, wave));
            enemies.add(EnemyFactory.create(EnemyType.JONIN_IWA,  wave));
        }
        return enemies;
    }

    // ── Jounin Promotion waves ────────────────────────────────────────────────

    private List<Enemy> generateJoninPromoWave(int wave) {
        List<Enemy> enemies = new ArrayList<>();
        if (wave <= 5) {
            // Similar to A-rank but harder
            repeat(enemies, EnemyType.JONIN_KIRI, wave, 2);
            repeat(enemies, EnemyType.HIGH_MISSING_NIN, wave, 2);
            if (wave >= 3) {
                enemies.add(EnemyFactory.create(EnemyType.JONIN_IWA, wave));
            }
        } else {
            // Final wave: Elite Boss + 2 Jonin (spec requirement)
            enemies.add(EnemyFactory.create(EnemyType.ELITE_BOSS, wave));
            enemies.add(EnemyFactory.create(EnemyType.JONIN_KIRI,  wave));
            enemies.add(EnemyFactory.create(EnemyType.JONIN_IWA,   wave));
        }
        return enemies;
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private void repeat(List<Enemy> list, EnemyType type, int wave, int count) {
        for (int i = 0; i < count; i++) {
            list.add(EnemyFactory.create(type, wave));
        }
    }
}
