package com.naruto.jutsu.strategies;

import com.naruto.enemy.Enemy;
import com.naruto.enemy.StatusEffect;
import com.naruto.jutsu.JutsuRank;
import com.naruto.jutsu.JutsuStrategy;
import com.naruto.tower.Tower;

import java.util.List;

/**
 * Shadow Imitation Lv1 — D-rank, Nara clan.
 * Briefly stops one enemy for 0.5 seconds.
 */
public class ShadowImitationJutsu implements JutsuStrategy {

    private static final int STOP_DURATION_MS = 500;

    @Override public String    getName()       { return "Shadow Imitation Lv1"; }
    @Override public JutsuRank getRank()       { return JutsuRank.D; }
    @Override public int       getChakraCost() { return 15; }

    @Override
    public void execute(Tower source, List<Enemy> enemies) {
        if (!enemies.isEmpty()) {
            enemies.get(0).applyEffect(StatusEffect.STUN, STOP_DURATION_MS);
        }
    }
}
