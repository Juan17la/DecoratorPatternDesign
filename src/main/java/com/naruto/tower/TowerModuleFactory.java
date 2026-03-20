package com.naruto.tower;

import com.naruto.game.NinjaRank;

import java.util.Map;

/**
 * Factory that applies a named module to a tower, enforcing:
 *  - Stack limits per module type
 *  - Rank-based maximum total modules per tower
 */
public class TowerModuleFactory {

    /** Max modules allowed per tower, indexed by NinjaRank. */
    private static final Map<NinjaRank, Integer> MAX_MODULES = Map.of(
        NinjaRank.ACADEMY_STUDENT, 1,
        NinjaRank.GENIN,           2,
        NinjaRank.CHUNIN,          4,
        NinjaRank.JONIN,           6,
        NinjaRank.ELITE_JONIN,     7,
        NinjaRank.ANBU,            6,
        NinjaRank.KAGE,            8
    );

    /** Stack limits for each module key. */
    private static final Map<String, Integer> STACK_LIMITS = Map.ofEntries(
        Map.entry("rapidfire",      RapidFireModule.STACK_LIMIT),
        Map.entry("shuriken",       ShurikenModule.STACK_LIMIT),
        Map.entry("piercingkunai",  PiercingKunaiModule.STACK_LIMIT),
        Map.entry("explosivekunai", ExplosiveKunaiModule.STACK_LIMIT),
        Map.entry("firestyle",      FireStyleModule.STACK_LIMIT),
        Map.entry("lightningstyle", LightningStyleModule.STACK_LIMIT),
        Map.entry("waterstyle",     WaterStyleModule.STACK_LIMIT),
        Map.entry("windstyle",      WindStyleModule.STACK_LIMIT),
        Map.entry("earthstyle",     EarthStyleModule.STACK_LIMIT),
        Map.entry("freeze",         FreezeModule.STACK_LIMIT),
        Map.entry("chakraregen",    ChakraRegenModule.STACK_LIMIT),
        Map.entry("goldgenerator",  GoldGeneratorModule.STACK_LIMIT),
        Map.entry("clanability",    ClanAbilityModule.STACK_LIMIT),
        Map.entry("chakraarmor",    ChakraArmorModule.STACK_LIMIT)
    );

    /**
     * Applies the named module to the given tower.
     *
     * @throws IllegalArgumentException if the module name is unknown
     * @throws IllegalStateException    if the rank cap or stack limit would be exceeded
     */
    public static Tower apply(Tower tower, String moduleName, NinjaRank rank) {
        String key = moduleName.toLowerCase().replaceAll("[^a-z]", "");

        if (!STACK_LIMITS.containsKey(key)) {
            throw new IllegalArgumentException("Unknown module: " + moduleName);
        }

        // Rank-based cap
        int maxAllowed = MAX_MODULES.getOrDefault(rank, 2);
        if (tower.getAppliedModules().size() >= maxAllowed) {
            throw new IllegalStateException(
                rank.name() + " rank allows at most " + maxAllowed + " module(s) per tower");
        }

        // Stack limit
        long currentCount = tower.getAppliedModules().stream()
            .filter(m -> m.toLowerCase().replaceAll("[^a-z]", "").equals(key))
            .count();
        int stackLimit = STACK_LIMITS.get(key);
        if (currentCount >= stackLimit) {
            throw new IllegalStateException(
                moduleName + " can only be stacked " + stackLimit + " time(s)");
        }

        return switch (key) {
            case "rapidfire"      -> new RapidFireModule(tower);
            case "shuriken"       -> new ShurikenModule(tower);
            case "piercingkunai"  -> new PiercingKunaiModule(tower);
            case "explosivekunai" -> new ExplosiveKunaiModule(tower);
            case "firestyle"      -> new FireStyleModule(tower);
            case "lightningstyle" -> new LightningStyleModule(tower);
            case "waterstyle"     -> new WaterStyleModule(tower);
            case "windstyle"      -> new WindStyleModule(tower);
            case "earthstyle"     -> new EarthStyleModule(tower);
            case "freeze"         -> new FreezeModule(tower);
            case "chakraregen"    -> new ChakraRegenModule(tower);
            case "goldgenerator"  -> new GoldGeneratorModule(tower);
            case "clanability"    -> new ClanAbilityModule(tower);
            case "chakraarmor"    -> new ChakraArmorModule(tower);
            default               -> throw new IllegalArgumentException("Unknown module: " + moduleName);
        };
    }
}
