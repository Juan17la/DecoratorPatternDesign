package com.naruto.reward;

import com.naruto.jutsu.JutsuCatalog;
import com.naruto.jutsu.JutsuDefinition;
import com.naruto.jutsu.JutsuRank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Draws reward cards using rank-based probability:
 *   D-rank jutsu  ~50%
 *   B-rank jutsu  ~30%
 *   A-rank jutsu  ~15%
 *   S-rank jutsu   ~5%
 */
public class RewardPool {

    private static final int CARDS_PER_DRAW = 3;

    private final Random random = new Random();
    private int idCounter = 1;

    private List<RewardCard> pendingCards = new ArrayList<>();

    public List<RewardCard> drawCards() {
        pendingCards.clear();
        for (int i = 0; i < CARDS_PER_DRAW; i++) {
            pendingCards.add(drawOne());
        }
        return Collections.unmodifiableList(pendingCards);
    }

    public RewardCard getCard(String cardId) {
        return pendingCards.stream()
            .filter(c -> c.getId().equals(cardId))
            .findFirst().orElse(null);
    }

    public List<RewardCard> getPendingCards() { return pendingCards; }

    // ── internal ──────────────────────────────────────────────────────────────

    private RewardCard drawOne() {
        JutsuRank rank  = rollRank();
        List<JutsuDefinition> pool = JutsuCatalog.byRank(rank);
        JutsuDefinition def = pool.get(random.nextInt(pool.size()));
        String id = "r" + idCounter++;
        return new RewardCard(id, def.name(), RewardType.JUTSU, def.name());
    }

    /**
     * Rolls a jutsu rank based on drop probabilities:
     *   S ≤ 5%  |  A ≤ 20%  |  B ≤ 50%  |  D otherwise
     */
    private JutsuRank rollRank() {
        float roll = random.nextFloat();
        if (roll < 0.05f) return JutsuRank.S;
        if (roll < 0.20f) return JutsuRank.A;
        if (roll < 0.50f) return JutsuRank.B;
        return JutsuRank.D;
    }
}
