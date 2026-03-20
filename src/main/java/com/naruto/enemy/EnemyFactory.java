package com.naruto.enemy;


/**
 * Creates Enemy instances from EnemyType definitions,
 * applying per-wave HP and speed multipliers from the spec.
 *
 * Wave scaling table:
 *   Wave 1: HP ×1.0,  Speed ×1.00
 *   Wave 2: HP ×1.15, Speed ×1.05
 *   Wave 3: HP ×1.30, Speed ×1.10
 *   Wave 4: HP ×1.50, Speed ×1.15
 *   Wave 5: HP ×1.70, Speed ×1.20
 *   Wave 6: HP ×2.00, Speed ×1.30
 */
public final class EnemyFactory {

    private static int counter = 0;

    /** HP multipliers indexed by wave number (1-based; index 0 unused). */
    private static final float[] HP_MULT    = { 0f, 1.0f, 1.15f, 1.30f, 1.50f, 1.70f, 2.00f };
    /** Speed multipliers indexed by wave number (1-based). */
    private static final float[] SPEED_MULT = { 0f, 1.0f, 1.05f, 1.10f, 1.15f, 1.20f, 1.30f };

    /**
     * Creates a single enemy of the given type scaled for the specified wave.
     * Wave values beyond 6 use the wave-6 multipliers.
     */
    public static Enemy create(EnemyType type, int waveNumber) {
        int   w    = Math.min(waveNumber, HP_MULT.length - 1);
        int   hp   = Math.max(1, (int)(type.baseHp   * HP_MULT[w]));
        float spd  = type.baseSpeed * SPEED_MULT[w];
        String id  = "e" + ++counter;
        return new Enemy(id, type.displayName, hp, spd,
                         type.enemyRank, type.coinReward,
                         type, type.behaviors);
    }

    /**
     * Legacy convenience method — kept for backwards compatibility.
     * Prefer {@link #create(EnemyType, int)} instead.
     */
    public static Enemy create(String name, int hp, float speed,
                               EnemyRank rank, int coinReward) {
        String id = "e" + ++counter;
        return new Enemy(id, name, hp, speed, rank, coinReward);
    }

    private EnemyFactory() {}
}
