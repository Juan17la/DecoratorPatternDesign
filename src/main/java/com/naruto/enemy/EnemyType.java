package com.naruto.enemy;

import java.util.Set;

/**
 * Catalog of every enemy in the game with base (wave-1) stats.
 * Stats are scaled by EnemyFactory based on the current wave number.
 */
public enum EnemyType {

    // ── D-rank enemies ────────────────────────────────────────────────────────

    LOST_CAT(
        "Lost Cat", 15, 1.2f, EnemyRank.CIVILIAN, 1,
        Set.of(EnemyBehavior.ERRATIC)),

    STRAY_DOG(
        "Stray Dog", 25, 0.8f, EnemyRank.CIVILIAN, 1,
        Set.of()),

    EVIL_FLOWER(
        "Evil Flower", 30, 0.5f, EnemyRank.CIVILIAN, 2,
        Set.of()),

    STUBBORN_ELDER(
        "Stubborn Elder", 40, 0.3f, EnemyRank.CIVILIAN, 3,
        Set.of()),

    ROGUE_BUILDER(
        "Rogue Builder", 35, 0.8f, EnemyRank.CIVILIAN, 2,
        Set.of()),

    // ── C-rank enemies ────────────────────────────────────────────────────────

    BANDIT(
        "Bandit", 60, 0.8f, EnemyRank.GENIN, 4,
        Set.of()),

    CRIMINAL(
        "Criminal", 80, 0.8f, EnemyRank.GENIN, 5,
        Set.of(EnemyBehavior.RAPID_ATTACK)),

    SPY(
        "Spy (Non-ninja)", 70, 1.1f, EnemyRank.GENIN, 6,
        Set.of(EnemyBehavior.INVISIBLE)),

    MERCENARY(
        "Mercenary", 100, 0.5f, EnemyRank.CHUNIN, 7,
        Set.of(EnemyBehavior.ARMOR)),

    GANG_LEADER(
        "Gang Leader", 150, 0.8f, EnemyRank.CHUNIN, 12,
        Set.of(EnemyBehavior.SPAWNER, EnemyBehavior.MINI_BOSS)),

    // ── B-rank enemies ────────────────────────────────────────────────────────

    LEAF_NINJA(
        "Leaf Village Ninja", 120, 0.8f, EnemyRank.GENIN, 8,
        Set.of(EnemyBehavior.JUTSU_USER)),

    STONE_NINJA(
        "Stone Village Ninja", 140, 0.5f, EnemyRank.CHUNIN, 10,
        Set.of(EnemyBehavior.ARMOR, EnemyBehavior.JUTSU_USER)),

    SAND_NINJA(
        "Sand Village Ninja", 110, 1.1f, EnemyRank.GENIN, 10,
        Set.of(EnemyBehavior.SAND_BARRIER)),

    MISSING_NIN(
        "Missing-Nin (Low)", 160, 0.8f, EnemyRank.CHUNIN, 12,
        Set.of(EnemyBehavior.RANDOM_JUTSU)),

    ROGUE_SQUAD_MEMBER(
        "Rogue Squad", 100, 0.8f, EnemyRank.GENIN, 7,
        Set.of()),

    // ── A-rank enemies ────────────────────────────────────────────────────────

    JONIN_KIRI(
        "Jonin from Kiri", 250, 1.1f, EnemyRank.JONIN, 20,
        Set.of(EnemyBehavior.MIST_CLONE)),

    JONIN_IWA(
        "Jonin from Iwa", 300, 0.5f, EnemyRank.JONIN, 25,
        Set.of(EnemyBehavior.ARMOR, EnemyBehavior.FIRE_IMMUNE)),

    CHUNIN_SQUAD_MEMBER(
        "Chunin Squad", 150, 0.8f, EnemyRank.CHUNIN, 9,
        Set.of()),

    HIGH_MISSING_NIN(
        "High-Level Missing-Nin", 280, 0.8f, EnemyRank.JONIN, 30,
        Set.of(EnemyBehavior.S_RANK_SHIELD)),

    ELITE_BOSS(
        "Elite Boss", 500, 0.4f, EnemyRank.KAGE, 50,
        Set.of(EnemyBehavior.AOE_IMMUNE, EnemyBehavior.SLOW_FREEZE_IMMUNE, EnemyBehavior.MINI_BOSS));

    // ── fields ────────────────────────────────────────────────────────────────

    public final String             displayName;
    public final int                baseHp;
    public final float              baseSpeed;
    public final EnemyRank          enemyRank;
    public final int                coinReward;
    public final Set<EnemyBehavior> behaviors;

    EnemyType(String displayName, int baseHp, float baseSpeed,
              EnemyRank enemyRank, int coinReward, Set<EnemyBehavior> behaviors) {
        this.displayName = displayName;
        this.baseHp      = baseHp;
        this.baseSpeed   = baseSpeed;
        this.enemyRank   = enemyRank;
        this.coinReward  = coinReward;
        this.behaviors   = behaviors;
    }
}
