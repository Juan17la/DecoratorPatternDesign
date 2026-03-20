package com.naruto.tower;

import com.naruto.enemy.Enemy;
import com.naruto.enemy.StatusEffect;

import java.util.ArrayList;
import java.util.List;

public class FireStyleModule extends TowerDecorator {

    public static final int STACK_LIMIT   = 1;
    private static final int BONUS_DAMAGE = 10;
    private static final double BURN_CHANCE = 0.25;

    public FireStyleModule(Tower tower) {
        super(tower);
    }

    @Override
    public int getDamage() {
        return wrappedTower.getDamage() + BONUS_DAMAGE;
    }

    @Override
    public void attack(Enemy target) {
        wrappedTower.attack(target);
        target.takeDamage(BONUS_DAMAGE);
        if (Math.random() < BURN_CHANCE) {
            target.applyEffect(StatusEffect.BURN, 2000);
        }
    }

    @Override
    public List<String> getAppliedModules() {
        List<String> m = new ArrayList<>(wrappedTower.getAppliedModules());
        m.add("FireStyle");
        return m;
    }

    @Override
    public String getDescription() {
        return wrappedTower.getDescription() + " [FireStyle]";
    }
}
