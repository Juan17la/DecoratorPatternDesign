package com.naruto.tower;

import java.util.ArrayList;
import java.util.List;

public class ShurikenModule extends TowerDecorator {

    public static final int STACK_LIMIT  = 2;
    private static final int EXTRA_TARGETS = 2; // hits up to 3 total

    public ShurikenModule(Tower tower) {
        super(tower);
    }

    @Override
    public int getTargetCount() {
        return wrappedTower.getTargetCount() + EXTRA_TARGETS;
    }

    @Override
    public List<String> getAppliedModules() {
        List<String> m = new ArrayList<>(wrappedTower.getAppliedModules());
        m.add("Shuriken");
        return m;
    }

    @Override
    public String getDescription() {
        return wrappedTower.getDescription() + " [Shuriken]";
    }
}
