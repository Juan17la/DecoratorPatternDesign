package com.naruto.tower;

import java.util.ArrayList;
import java.util.List;

public class ChakraArmorModule extends TowerDecorator {

    public static final int STACK_LIMIT = 2;

    public ChakraArmorModule(Tower tower) {
        super(tower);
    }

    /** True when this tower has chakra armor resistance active. */
    public boolean hasChakraArmor() { return true; }

    @Override
    public List<String> getAppliedModules() {
        List<String> m = new ArrayList<>(wrappedTower.getAppliedModules());
        m.add("ChakraArmor");
        return m;
    }

    @Override
    public String getDescription() {
        return wrappedTower.getDescription() + " [ChakraArmor]";
    }
}
