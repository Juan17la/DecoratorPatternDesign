package com.naruto.tower;

import com.naruto.enemy.Enemy;
import com.naruto.enemy.StatusEffect;

import java.util.ArrayList;
import java.util.List;

public class WaterStyleModule extends TowerDecorator {

    public static final int STACK_LIMIT  = 1;
    private static final int BONUS_DAMAGE = 8;

    public WaterStyleModule(Tower tower) {
        super(tower);
    }

    @Override
    public void attack(Enemy target) {
        wrappedTower.attack(target);
        target.takeDamage(BONUS_DAMAGE);
        target.applyEffect(StatusEffect.SLOW, 1500);
    }

    @Override
    public List<String> getAppliedModules() {
        List<String> m = new ArrayList<>(wrappedTower.getAppliedModules());
        m.add("WaterStyle");
        return m;
    }

    @Override
    public String getDescription() {
        return wrappedTower.getDescription() + " [WaterStyle]";
    }
}
