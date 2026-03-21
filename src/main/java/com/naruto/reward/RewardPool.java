package com.naruto.reward;

import com.naruto.jutsu.JutsuCatalog;
import com.naruto.jutsu.JutsuDefinition;
import com.naruto.jutsu.JutsuRank;
import com.naruto.mission.MissionRank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Draws reward cards using mission-rank-based probabilities
 * as defined in 06_rewards_and_progression.md.
 *
 * Reward type weights per mission rank:
 *   D: Jutsu 60% | Kunai 20% | Shuriken 15% | Chakra 5%  | Module 0%
 *   C: Jutsu 55% | Kunai 20% | Shuriken 15% | Chakra 10% | Module 0%
 *   B: Jutsu 50% | Kunai 15% | Shuriken 15% | Chakra 15% | Module 5%
 *   A: Jutsu 45% | Kunai 15% | Shuriken 15% | Chakra 20% | Module 5%
 *
 * Jutsu rank weights per mission rank:
 *   D: D 80% | B 20% | A  0% | S  0%
 *   C: D 60% | B 35% | A  5% | S  0%
 *   B: D 40% | B 35% | A 20% | S  5%
 *   A: D 20% | B 35% | A 35% | S 10%
 */
public class RewardPool {

    private static final int CARDS_PER_DRAW = 3;

    // ── Reward-type probability tables [D, C, B, A] ──────────────────────────
    // Each row: { jutsu%, kunai%, shuriken%, chakra%, module% }

    private static final float[][] TYPE_PROBS = {
        // D      C      B      A
        { 0.60f, 0.55f, 0.50f, 0.45f },   // JUTSU
        { 0.20f, 0.20f, 0.15f, 0.15f },   // KUNAI_UPGRADE
        { 0.15f, 0.15f, 0.15f, 0.15f },   // SHURIKEN_UPGRADE
        { 0.05f, 0.10f, 0.15f, 0.20f },   // CHAKRA_UPGRADE
        { 0.00f, 0.00f, 0.05f, 0.05f },   // DECORATOR_MODULE
    };

    // ── Jutsu-rank probability tables [D, C, B, A] ───────────────────────────
    // Each row: { D%, B%, A%, S% }

    private static final float[][] JUTSU_RANK_PROBS = {
        // D      C      B      A
        { 0.80f, 0.60f, 0.40f, 0.20f },   // JutsuRank.D
        { 0.20f, 0.35f, 0.35f, 0.35f },   // JutsuRank.B
        { 0.00f, 0.05f, 0.20f, 0.35f },   // JutsuRank.A
        { 0.00f, 0.00f, 0.05f, 0.10f },   // JutsuRank.S
    };

    private static final String[] DECORATOR_MODULE_NAMES = {
        "RapidFire", "Shuriken", "FireStyle", "LightningStyle",
        "WaterStyle", "WindStyle", "EarthStyle", "Freeze", "ChakraRegen"
    };

    // Chakra bonus amounts per upgrade card
    private static final int[] CHAKRA_AMOUNTS = { 20, 20, 30, 30, 30, 30, 30, 30 };

    private final Random random    = new Random();
    private int          idCounter = 1;
    private int          chakraUpgradeCount = 0;

    private List<RewardCard> pendingCards = new ArrayList<>();

    // ── Public API ────────────────────────────────────────────────────────────

    /** Draw CARDS_PER_DRAW cards scaled to the given mission rank. */
    public List<RewardCard> drawCards(MissionRank missionRank) {
        pendingCards.clear();
        int col = rankIndex(missionRank);
        for (int i = 0; i < CARDS_PER_DRAW; i++) {
            pendingCards.add(drawOne(col));
        }
        return Collections.unmodifiableList(pendingCards);
    }

    /** Kept for callers that don't know the mission rank (defaults to D). */
    public List<RewardCard> drawCards() {
        return drawCards(MissionRank.D);
    }

    public RewardCard getCard(String cardId) {
        return pendingCards.stream()
            .filter(c -> c.getId().equals(cardId))
            .findFirst().orElse(null);
    }

    public List<RewardCard> getPendingCards() { return pendingCards; }

    // ── Internal ──────────────────────────────────────────────────────────────

    private RewardCard drawOne(int col) {
        RewardType type = rollType(col);
        String id = "r" + idCounter++;

        return switch (type) {
            case JUTSU -> drawJutsuCard(id, col);
            case KUNAI_UPGRADE -> new RewardCard(id,
                "Kunai Upgrade", RewardType.KUNAI_UPGRADE, "1");
            case SHURIKEN_UPGRADE -> new RewardCard(id,
                "Shuriken Upgrade", RewardType.SHURIKEN_UPGRADE, "1");
            case CHAKRA_UPGRADE -> {
                int bonus = CHAKRA_AMOUNTS[Math.min(chakraUpgradeCount, CHAKRA_AMOUNTS.length - 1)];
                yield new RewardCard(id,
                    "Chakra Upgrade (+" + bonus + ")", RewardType.CHAKRA_UPGRADE, String.valueOf(bonus));
            }
            case DECORATOR_MODULE -> {
                String module = DECORATOR_MODULE_NAMES[random.nextInt(DECORATOR_MODULE_NAMES.length)];
                yield new RewardCard(id,
                    module + " Module", RewardType.DECORATOR_MODULE, module);
            }
            default -> drawJutsuCard(id, col);
        };
    }

    private RewardCard drawJutsuCard(String id, int col) {
        JutsuRank rank = rollJutsuRank(col);
        List<JutsuDefinition> pool = JutsuCatalog.byRank(rank);
        JutsuDefinition def = pool.get(random.nextInt(pool.size()));
        return new RewardCard(id, def.name(), RewardType.JUTSU, def.name());
    }

    private RewardType rollType(int col) {
        float roll = random.nextFloat();
        float cumulative = 0;
        RewardType[] types = {
            RewardType.JUTSU, RewardType.KUNAI_UPGRADE,
            RewardType.SHURIKEN_UPGRADE, RewardType.CHAKRA_UPGRADE,
            RewardType.DECORATOR_MODULE
        };
        for (int i = 0; i < types.length; i++) {
            cumulative += TYPE_PROBS[i][col];
            if (roll < cumulative) return types[i];
        }
        return RewardType.JUTSU;
    }

    private JutsuRank rollJutsuRank(int col) {
        float roll = random.nextFloat();
        float cumulative = 0;
        JutsuRank[] ranks = { JutsuRank.D, JutsuRank.B, JutsuRank.A, JutsuRank.S };
        for (int i = 0; i < ranks.length; i++) {
            cumulative += JUTSU_RANK_PROBS[i][col];
            if (roll < cumulative) return ranks[i];
        }
        return JutsuRank.D;
    }

    private static int rankIndex(MissionRank rank) {
        return switch (rank) {
            case D    -> 0;
            case C    -> 1;
            case B    -> 2;
            case A, S -> 3;
        };
    }
}
