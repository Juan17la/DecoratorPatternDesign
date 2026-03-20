package com.naruto.tower;

import com.naruto.enemy.Enemy;
import com.naruto.enemy.StatusEffect;

import java.util.ArrayList;
import java.util.List;

public class LightningStyleModule extends TowerDecorator {

    public static final int STACK_LIMIT   = 1;
    private static final int STUN_DURATION_MS = 500;

    public LightningStyleModule(Tower tower) {
        super(tower);
    }

    @Override
    public void attack(Enemy target) {
        wrappedTower.attack(target);
        target.applyEffect(StatusEffect.STUN, STUN_DURATION_MS);
    }

    @Override
    public List<String> getAppliedModules() {
        List<String> m = new ArrayList<>(wrappedTower.getAppliedModules());
        m.add("LightningStyle");
        return m;
    }

    @Override
    public String getDescription() {
        return wrappedTower.getDescription() + " [LightningStyle]";
    }
}
