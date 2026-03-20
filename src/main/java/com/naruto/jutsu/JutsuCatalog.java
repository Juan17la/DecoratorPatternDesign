package com.naruto.jutsu;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Registry of all 80 jutsus: 20 D-rank, 30 B-rank, 25 A-rank, 5 S-rank.
 * Use {@link #get(String)} to look up a definition by name,
 * or {@link #create(String)} to obtain a ready-to-use CatalogJutsu.
 */
public final class JutsuCatalog {

    // ── shorthand constructor ─────────────────────────────────────────────────

    private static JutsuDefinition j(String name, JutsuRank rank, String clan, String element,
                                     String desc, EffectType et,
                                     int dmg, int dur, int tgt, int cost, int restore) {
        return new JutsuDefinition(name, rank, clan, element, desc, et, dmg, dur, tgt, cost, restore);
    }

    // ── D-RANK (20) ───────────────────────────────────────────────────────────

    private static final List<JutsuDefinition> RANK_D = List.of(
        j("Shadow Clone Basics",  JutsuRank.D, null,      "General",   "Spawns 1 decoy that distracts 1 enemy",              EffectType.DECOY_CLONE,  0,  0,    1, 15, 0),
        j("Leaf Gust",            JutsuRank.D, null,      "Wind",      "Small knockback on one enemy",                        EffectType.KNOCKBACK,   10,  0,    1, 10, 0),
        j("Mud Wall",             JutsuRank.D, null,      "Earth",     "Creates a short obstacle on the path",                EffectType.OBSTACLE,     0,  2000, 0, 15, 0),
        j("Water Splash",         JutsuRank.D, null,      "Water",     "Minor AoE slow on a small area",                     EffectType.SLOW_AOE,     0,  1000, 0, 15, 0),
        j("Fire Spark",           JutsuRank.D, null,      "Fire",      "Ignites one enemy briefly",                          EffectType.BURN_SINGLE, 20,  1000, 1, 15, 0),
        j("Static Touch",         JutsuRank.D, null,      "Lightning", "Single enemy takes bonus damage",                    EffectType.DAMAGE_SINGLE,30, 0,    1, 15, 0),
        j("Chakra Needle",        JutsuRank.D, null,      "General",   "High-speed single-target piercing attack",           EffectType.DAMAGE_SINGLE,35, 0,    1, 15, 0),
        j("Stone Throw Jutsu",    JutsuRank.D, null,      "Earth",     "Throws rocks, minor AoE damage",                     EffectType.DAMAGE_AOE,  15,  0,    0, 15, 0),
        j("Gust Slice",           JutsuRank.D, null,      "Wind",      "Hits all enemies in a short line",                   EffectType.DAMAGE_LINE, 20,  0,    3, 15, 0),
        j("Bubble Trap",          JutsuRank.D, null,      "Water",     "Encases one enemy briefly (stop 1 sec)",              EffectType.STOP_SINGLE,  0,  1000, 1, 15, 0),
        j("Burning Brush",        JutsuRank.D, "UZUMAKI", "Fire",      "Leaves a burning trail on the path",                 EffectType.BURN_PATH,   15,  3000, 0, 20, 0),
        j("Chain Wrap",           JutsuRank.D, "UZUMAKI", "General",   "Slows one enemy with chakra chains",                 EffectType.SLOW_SINGLE,  0,  2000, 1, 15, 0),
        j("Sharingan Glance",     JutsuRank.D, "UCHIHA",  "General",   "Reveals the strongest enemy in range",               EffectType.REVEAL,       0,  0,    1, 15, 0),
        j("Gentle Palm Push",     JutsuRank.D, "HYUGA",   "General",   "Pushes one enemy back on the path",                  EffectType.KNOCKBACK,   15,  0,    1, 15, 0),
        j("Shadow Imitation Lv1", JutsuRank.D, "NARA",    "General",   "Briefly stops one enemy (0.5 sec)",                  EffectType.STOP_SINGLE,  0,  500,  1, 15, 0),
        j("Fang Strike",          JutsuRank.D, "INUZUKA", "General",   "Quick multi-hit on nearest enemy",                   EffectType.DAMAGE_MULTI,40,  0,    1, 15, 0),
        j("Mind Needle",          JutsuRank.D, "INOICHI", "General",   "Reduces one enemy's speed for 2 sec",                EffectType.SLOW_SINGLE,  0,  2000, 1, 15, 0),
        j("Kunai Rain",           JutsuRank.D, null,      "General",   "Drops 5 kunais over a small area",                   EffectType.DAMAGE_MULTI,10,  0,    5, 20, 0),
        j("Leaf Shuriken",        JutsuRank.D, null,      "Wind",      "Single shuriken with extended range",                EffectType.DAMAGE_SINGLE,25, 0,    1, 10, 0),
        j("Healing Pulse",        JutsuRank.D, "UZUMAKI", "General",   "Restores 10 chakra to this tower instantly",         EffectType.CHAKRA_RESTORE,0, 0,    0, 10, 10)
    );

    // ── B-RANK (30) ───────────────────────────────────────────────────────────

    private static final List<JutsuDefinition> RANK_B = List.of(
        j("Fireball Jutsu",             JutsuRank.B, "UCHIHA",  "Fire",      "Classic fireball, medium AoE",                            EffectType.BURN_AOE,         80, 1500, 0, 35, 0),
        j("Shadow Shuriken",            JutsuRank.B, null,      "General",   "Throws 2 shurikens at once",                              EffectType.DAMAGE_MULTI,     45, 0,    2, 25, 0),
        j("Water Dragon Bullet",        JutsuRank.B, null,      "Water",     "Passes through multiple enemies in a line",               EffectType.PIERCE_LINE,      70, 0,    5, 35, 0),
        j("Earth Golem Wall",           JutsuRank.B, null,      "Earth",     "Blocks path for 3 seconds",                               EffectType.OBSTACLE,          0, 3000, 0, 30, 0),
        j("Lightning Straight Line",    JutsuRank.B, null,      "Lightning", "Hits all enemies in one row",                             EffectType.DAMAGE_LINE,      65, 0,   10, 35, 0),
        j("Wind Scalpel",               JutsuRank.B, null,      "Wind",      "Precision high-damage single hit",                        EffectType.DAMAGE_SINGLE,   100, 0,    1, 30, 0),
        j("Shadow Imitation Lv2",       JutsuRank.B, "NARA",    "General",   "Stops 2 enemies for 1.5 sec",                             EffectType.STOP_MULTI,        0, 1500, 2, 30, 0),
        j("Eight Trigrams Rotation",    JutsuRank.B, "HYUGA",   "General",   "AoE chakra burst around tower (close range)",             EffectType.CHAKRA_AOE_BURST, 70, 0,    0, 35, 0),
        j("Fang Over Fang",             JutsuRank.B, "INUZUKA", "General",   "Spiraling multi-hit on single target",                    EffectType.DAMAGE_MULTI,     90, 0,    1, 30, 0),
        j("Mind Transfer",              JutsuRank.B, "INOICHI", "General",   "Confuses one enemy for 2 sec",                            EffectType.STOP_SINGLE,       0, 2000, 1, 30, 0),
        j("Chakra Chain Bind",          JutsuRank.B, "UZUMAKI", "General",   "Stops one enemy completely for 2 sec",                    EffectType.STOP_SINGLE,       0, 2000, 1, 30, 0),
        j("Clone Swarm",                JutsuRank.B, null,      "General",   "3 clones distract 3 separate enemies",                    EffectType.DECOY_CLONE,       0, 0,    3, 35, 0),
        j("Phoenix Flower Jutsu",       JutsuRank.B, "UCHIHA",  "Fire",      "Hits 3 enemies simultaneously with fireballs",            EffectType.DAMAGE_MULTI,     60, 0,    3, 35, 0),
        j("Water Prison Sphere",        JutsuRank.B, null,      "Water",     "Encases one strong enemy for 2 sec",                      EffectType.STOP_SINGLE,       0, 2000, 1, 35, 0),
        j("Mud Wave",                   JutsuRank.B, null,      "Earth",     "Slows all enemies in a large area",                       EffectType.SLOW_AOE,          0, 2000, 0, 35, 0),
        j("Tornado of Wind",            JutsuRank.B, null,      "Wind",      "AoE area, knocks back all enemies in range",              EffectType.KNOCKBACK_AOE,    50, 0,    0, 35, 0),
        j("Thunderclap Fist",           JutsuRank.B, null,      "Lightning", "Releases lightning burst around tower",                   EffectType.DAMAGE_AOE,       75, 0,    0, 35, 0),
        j("Gentle Fist Art: One Blow",  JutsuRank.B, "HYUGA",   "General",   "Deals high damage and disables chakra armor",             EffectType.DAMAGE_SINGLE,    90, 0,    1, 35, 0),
        j("Shadow Neck Bind",           JutsuRank.B, "NARA",    "General",   "Immobilizes one enemy for 3 sec",                         EffectType.STOP_SINGLE,       0, 3000, 1, 35, 0),
        j("Beast Mimicry",              JutsuRank.B, "INUZUKA", "General",   "Greatly increases tower attack speed for 3 sec",          EffectType.SPEED_BOOST_SELF,  0, 3000, 0, 30, 0),
        j("Psychic Wave",               JutsuRank.B, "INOICHI", "General",   "AoE confusion, affects 3 enemies at once",                EffectType.STOP_MULTI,        0, 2000, 3, 35, 0),
        j("Sealing Barrier",            JutsuRank.B, "UZUMAKI", "General",   "Creates a barrier that blocks enemies briefly",           EffectType.OBSTACLE,          0, 2000, 0, 30, 0),
        j("Sharingan Copy",             JutsuRank.B, "UCHIHA",  "General",   "Tower copies the last jutsu used by a nearby tower",      EffectType.REVEAL,            0, 0,    0, 30, 0),
        j("Earth Spike Field",          JutsuRank.B, null,      "Earth",     "Places spikes on path, damages walking enemies",          EffectType.BURN_PATH,        30, 4000, 0, 30, 0),
        j("Flame Whip",                 JutsuRank.B, null,      "Fire",      "Continuous fire beam for 1 second",                       EffectType.DAMAGE_LINE,      80, 1000, 5, 35, 0),
        j("Lightning Clone",            JutsuRank.B, null,      "Lightning", "A clone that explodes on enemy contact",                  EffectType.DECOY_CLONE,      60, 0,    1, 30, 0),
        j("Water Shark Bomb",           JutsuRank.B, null,      "Water",     "Homing water projectile toward strongest enemy",          EffectType.HOMING,           85, 0,    1, 35, 0),
        j("Leaf Strong Whirlwind",      JutsuRank.B, null,      "Wind",      "Hits all enemies in a wide arc",                          EffectType.KNOCKBACK_AOE,    55, 0,    0, 30, 0),
        j("Smoke Clone Barrage",        JutsuRank.B, null,      "General",   "Multiple kunais with smoke on impact (mini slow)",        EffectType.DAMAGE_SLOW_AOE,  40, 800,  0, 30, 0),
        j("Chakra Surge",               JutsuRank.B, null,      "General",   "Instantly restores 50 chakra to this tower",              EffectType.CHAKRA_RESTORE,    0, 0,    0, 20, 50)
    );

    // ── A-RANK (25) ───────────────────────────────────────────────────────────

    private static final List<JutsuDefinition> RANK_A = List.of(
        j("Great Fireball Jutsu",               JutsuRank.A, "UCHIHA",  "Fire",      "Huge AoE fireball, high damage",                                     EffectType.BURN_AOE,         150, 2000, 0, 60, 0),
        j("Eight Trigrams 64 Palms",            JutsuRank.A, "HYUGA",   "General",   "Hits all enemies in range with rapid strikes",                       EffectType.DAMAGE_AOE,       120, 0,    0, 60, 0),
        j("Shadow Imitation Shuriken",          JutsuRank.A, "NARA",    "General",   "Throws blade that binds all enemies it touches",                     EffectType.DAMAGE_STOP_AOE,   30, 2000, 0, 55, 0),
        j("Dual Wolf Fang",                     JutsuRank.A, "INUZUKA", "General",   "AoE spin attack hitting all nearby enemies",                         EffectType.DAMAGE_AOE,       130, 0,    0, 60, 0),
        j("Mind Explosion Wave",                JutsuRank.A, "INOICHI", "General",   "AoE confusion + damage burst",                                       EffectType.DAMAGE_STOP_AOE,   80, 2000, 0, 60, 0),
        j("Chakra Chain Barrage",               JutsuRank.A, "UZUMAKI", "General",   "Chains and damages 4 enemies simultaneously",                        EffectType.CHAIN_BIND,        70, 2000, 4, 60, 0),
        j("Amaterasu Fragment",                 JutsuRank.A, "UCHIHA",  "Fire",      "Burns one enemy continuously; cannot be extinguished",               EffectType.BURN_SINGLE,       50, 5000, 1, 65, 0),
        j("Kirin",                              JutsuRank.A, null,      "Lightning", "Massive single-target lightning strike",                             EffectType.DAMAGE_SINGLE,    200, 0,    1, 65, 0),
        j("Water Style: Giant Vortex",          JutsuRank.A, null,      "Water",     "Pulls all enemies toward center, then damages",                      EffectType.DAMAGE_AOE,       110, 0,    0, 60, 0),
        j("Earth Style: Rock Avalanche",        JutsuRank.A, null,      "Earth",     "Massive earth wave, damages and buries enemies",                     EffectType.DAMAGE_SLOW_AOE,  120, 2000, 0, 60, 0),
        j("Great Wind Scythe",                  JutsuRank.A, null,      "Wind",      "Multiple invisible blades, high AoE",                                EffectType.DAMAGE_AOE,       130, 0,    0, 60, 0),
        j("Shadow Clone Army",                  JutsuRank.A, null,      "General",   "6 clones each distract a separate enemy",                            EffectType.DECOY_CLONE,        0, 0,    6, 65, 0),
        j("Barrier Matrix",                     JutsuRank.A, "UZUMAKI", "General",   "Sealing barrier blocks path for 5 seconds",                          EffectType.OBSTACLE,           0, 5000, 0, 55, 0),
        j("Heavenly Spin",                      JutsuRank.A, "HYUGA",   "General",   "Reflects projectiles and damages all nearby enemies",                EffectType.REFLECT,          100, 0,    0, 60, 0),
        j("Black Panther Lightning",            JutsuRank.A, null,      "Lightning", "Zigzag lightning hits up to 5 enemies",                              EffectType.DAMAGE_MULTI,     120, 0,    5, 60, 0),
        j("Lava Style: Rubber Flames",          JutsuRank.A, null,      "Fire",      "Sticky lava area slows + burns enemies",                             EffectType.DAMAGE_SLOW_AOE,   90, 2500, 0, 60, 0),
        j("Shadow Bind Lv3",                    JutsuRank.A, "NARA",    "General",   "Freezes all enemies on screen for 2 sec",                            EffectType.STOP_ALL,           0, 2000, 0, 65, 0),
        j("Twin Fang Cyclone",                  JutsuRank.A, "INUZUKA", "General",   "Massive AoE spin, hits all enemies in range",                        EffectType.DAMAGE_AOE,       140, 0,    0, 65, 0),
        j("Earth Style: Hiding in Rock",        JutsuRank.A, null,      "Earth",     "Tower becomes untargetable for 3 sec (immune)",                      EffectType.SHIELD,             0, 3000, 0, 55, 0),
        j("Wind Release: Rasenshuriken",        JutsuRank.A, null,      "Wind",      "High-damage shuriken with AoE explosion",                            EffectType.DAMAGE_AOE,       160, 0,    0, 65, 0),
        j("Uzumaki Sealing Chains",             JutsuRank.A, "UZUMAKI", "General",   "Chains all enemies on screen, stops for 3 sec",                      EffectType.STOP_ALL,           0, 3000, 0, 70, 0),
        j("Mind Destruction",                   JutsuRank.A, "INOICHI", "General",   "Destroys one enemy (boss excluded) instantly",                       EffectType.INSTANT_KILL,    9999, 0,    1, 70, 0),
        j("Water Style: Hydration",             JutsuRank.A, null,      "Water",     "Heals tower (restores 30 chakra)",                                   EffectType.CHAKRA_RESTORE,     0, 0,    0, 50, 30),
        j("Sharingan: Future Vision",           JutsuRank.A, "UCHIHA",  "General",   "+25% damage for all towers for 5 sec",                               EffectType.DAMAGE_BOOST,       0, 5000, 0, 60, 0),
        j("Eight Trigrams: Mountain Crusher",   JutsuRank.A, "HYUGA",   "General",   "Sends shockwave down entire path",                                   EffectType.DAMAGE_LINE,      140, 0,   20, 65, 0)
    );

    // ── S-RANK (5) ────────────────────────────────────────────────────────────

    private static final List<JutsuDefinition> RANK_S = List.of(
        j("Susanoo Strike",              JutsuRank.S, "UCHIHA",  "General", "Summons partial Susanoo arm that sweeps the entire path",                    EffectType.DAMAGE_AOE,   350, 0,    0, 100, 0),
        j("Uzumaki: Truth-Seeking Seal", JutsuRank.S, "UZUMAKI", "General", "Giant seal on path; enemies that step on it are stopped and heavily damaged",EffectType.DAMAGE_STOP_AOE,300,5000,0, 100, 0),
        j("Shadow: Dark Domain",         JutsuRank.S, "NARA",    "General", "All enemies frozen 5 sec; tower deals 3x damage during this time",           EffectType.STOP_ALL,       0,  5000, 0, 100, 0),
        j("Byakugan: Vacuum Palm Storm", JutsuRank.S, "HYUGA",   "General", "Fires 64 chakra blasts tracking individual targets",                         EffectType.DAMAGE_MULTI, 250, 0,   10, 100, 0),
        j("Rasenshuriken: Full Release", JutsuRank.S, null,      "Wind",    "Entire screen AoE, highest damage in the game",                              EffectType.DAMAGE_AOE,   400, 0,    0, 100, 0)
    );

    // ── Index ─────────────────────────────────────────────────────────────────

    private static final Map<String, JutsuDefinition> INDEX;

    static {
        INDEX = java.util.stream.Stream
            .of(RANK_D, RANK_B, RANK_A, RANK_S)
            .flatMap(List::stream)
            .collect(Collectors.toMap(JutsuDefinition::name, Function.identity()));
    }

    /** Returns the definition for the given jutsu name, or null if not found. */
    public static JutsuDefinition get(String name) {
        return INDEX.get(name);
    }

    /** Returns a ready-to-use CatalogJutsu, or null if the name is unknown. */
    public static CatalogJutsu create(String name) {
        JutsuDefinition def = INDEX.get(name);
        return def == null ? null : new CatalogJutsu(def);
    }

    /** All definitions grouped by rank. */
    public static List<JutsuDefinition> byRank(JutsuRank rank) {
        return switch (rank) {
            case D -> RANK_D;
            case B -> RANK_B;
            case A -> RANK_A;
            case S -> RANK_S;
        };
    }

    /** Total catalog size. */
    public static int size() { return INDEX.size(); }

    private JutsuCatalog() {}
}
