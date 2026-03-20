package com.naruto.tower;

import java.util.ArrayList;
import java.util.List;

public class WindStyleModule extends TowerDecorator {

    public static final int STACK_LIMIT  = 1;
    private static final int RANGE_BONUS  = 2;
    private static final float SPEED_MULT = 1.3f;

    public WindStyleModule(Tower tower) {
        super(tower);
    }

    @Override
    public int getRange() {
        return wrappedTower.getRange() + RANGE_BONUS;
    }

    @Override
    public float getAttackSpeed() {
        return wrappedTower.getAttackSpeed() * SPEED_MULT;
    }

    @Override
    public List<String> getAppliedModules() {
        List<String> m = new ArrayList<>(wrappedTower.getAppliedModules());
        m.add("WindStyle");
        return m;
    }

    @Override
    public String getDescription() {
        return wrappedTower.getDescription() + " [WindStyle]";
    }
}
