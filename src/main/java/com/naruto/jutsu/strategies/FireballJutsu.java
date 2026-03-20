package com.naruto.jutsu.strategies;

import com.naruto.enemy.Enemy;
import com.naruto.enemy.StatusEffect;
import com.naruto.jutsu.JutsuRank;
import com.naruto.jutsu.JutsuStrategy;
import com.naruto.tower.Tower;

import java.util.List;

/**
 * Fireball Jutsu — B-rank, Uchiha clan, Fire element.
 * Classic fireball with medium AoE damage and burn.
 * Kept as a named class; the full catalog entry is "Fireball Jutsu" in JutsuCatalog.
 */
public class FireballJutsu implements JutsuStrategy {

    private static final int BASE_DAMAGE = 80;

    @Override public String    getName()       { return "Fireball Jutsu"; }
    @Override public JutsuRank getRank()       { return JutsuRank.B; }
    @Override public int       getChakraCost() { return 35; }

    @Override
    public void execute(Tower source, List<Enemy> enemies) {
        for (Enemy enemy : enemies) {
            enemy.takeDamage(BASE_DAMAGE);
            enemy.applyEffect(StatusEffect.BURN, 1500);
        }
    }
}
