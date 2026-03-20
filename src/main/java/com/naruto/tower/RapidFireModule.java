package com.naruto.tower;

import java.util.ArrayList;
import java.util.List;

public class RapidFireModule extends TowerDecorator {

    public static final int STACK_LIMIT = 3;

    public RapidFireModule(Tower tower) {
        super(tower);
    }

    @Override
    public float getAttackSpeed() {
        return wrappedTower.getAttackSpeed() * 1.5f;
    }

    @Override
    public List<String> getAppliedModules() {
        List<String> m = new ArrayList<>(wrappedTower.getAppliedModules());
        m.add("RapidFire");
        return m;
    }

    @Override
    public String getDescription() {
        return wrappedTower.getDescription() + " [RapidFire]";
    }
}
