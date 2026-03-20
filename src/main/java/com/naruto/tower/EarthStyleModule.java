package com.naruto.tower;

import com.naruto.enemy.Enemy;
import com.naruto.enemy.StatusEffect;

import java.util.ArrayList;
import java.util.List;

public class EarthStyleModule extends TowerDecorator {

    public static final int STACK_LIMIT = 1;

    public EarthStyleModule(Tower tower) {
        super(tower);
    }

    @Override
    public void attack(Enemy target) {
        wrappedTower.attack(target);
        target.applyEffect(StatusEffect.SLOW, 2000);
    }

    @Override
    public List<String> getAppliedModules() {
        List<String> m = new ArrayList<>(wrappedTower.getAppliedModules());
        m.add("EarthStyle");
        return m;
    }

    @Override
    public String getDescription() {
        return wrappedTower.getDescription() + " [EarthStyle]";
    }
}
