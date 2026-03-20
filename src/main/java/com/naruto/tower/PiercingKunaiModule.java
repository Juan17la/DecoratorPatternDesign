package com.naruto.tower;

import java.util.ArrayList;
import java.util.List;

public class PiercingKunaiModule extends TowerDecorator {

    public static final int STACK_LIMIT = 2;

    public PiercingKunaiModule(Tower tower) {
        super(tower);
    }

    @Override
    public boolean isPiercing() { return true; }

    @Override
    public int getDamage() {
        return (int) (wrappedTower.getDamage() * 1.2f);
    }

    @Override
    public List<String> getAppliedModules() {
        List<String> m = new ArrayList<>(wrappedTower.getAppliedModules());
        m.add("PiercingKunai");
        return m;
    }

    @Override
    public String getDescription() {
        return wrappedTower.getDescription() + " [PiercingKunai]";
    }
}
