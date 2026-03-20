package com.naruto.tower;

import com.naruto.enemy.Enemy;
import com.naruto.enemy.StatusEffect;

import java.util.ArrayList;
import java.util.List;

public class FreezeModule extends TowerDecorator {

    public static final int STACK_LIMIT = 2;
    private static final double FREEZE_CHANCE   = 0.3;
    private static final int    FREEZE_DURATION = 1000;

    public FreezeModule(Tower tower) {
        super(tower);
    }

    @Override
    public void attack(Enemy target) {
        wrappedTower.attack(target);
        if (Math.random() < FREEZE_CHANCE) {
            target.applyEffect(StatusEffect.FREEZE, FREEZE_DURATION);
        }
    }

    @Override
    public List<String> getAppliedModules() {
        List<String> m = new ArrayList<>(wrappedTower.getAppliedModules());
        m.add("Freeze");
        return m;
    }

    @Override
    public String getDescription() {
        return wrappedTower.getDescription() + " [Freeze]";
    }
}
