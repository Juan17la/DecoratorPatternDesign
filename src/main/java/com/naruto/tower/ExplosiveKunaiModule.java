package com.naruto.tower;

import com.naruto.enemy.Enemy;

import java.util.ArrayList;
import java.util.List;

public class ExplosiveKunaiModule extends TowerDecorator {

    public static final int STACK_LIMIT  = 1;
    private static final int SPLASH_DAMAGE = 15;

    public ExplosiveKunaiModule(Tower tower) {
        super(tower);
    }

    @Override
    public void attack(Enemy target) {
        wrappedTower.attack(target);
        target.takeDamage(SPLASH_DAMAGE); // splash represented as bonus hit on primary target
    }

    @Override
    public List<String> getAppliedModules() {
        List<String> m = new ArrayList<>(wrappedTower.getAppliedModules());
        m.add("ExplosiveKunai");
        return m;
    }

    @Override
    public String getDescription() {
        return wrappedTower.getDescription() + " [ExplosiveKunai]";
    }
}
