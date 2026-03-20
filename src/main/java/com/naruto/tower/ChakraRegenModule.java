package com.naruto.tower;

import java.util.ArrayList;
import java.util.List;

public class ChakraRegenModule extends TowerDecorator {

    public static final int STACK_LIMIT     = 3;
    private static final float REGEN_BONUS  = 5f; // chakra per second

    public ChakraRegenModule(Tower tower) {
        super(tower);
    }

    @Override
    public float getChakraRegen() {
        return wrappedTower.getChakraRegen() + REGEN_BONUS;
    }

    @Override
    public List<String> getAppliedModules() {
        List<String> m = new ArrayList<>(wrappedTower.getAppliedModules());
        m.add("ChakraRegen");
        return m;
    }

    @Override
    public String getDescription() {
        return wrappedTower.getDescription() + " [ChakraRegen]";
    }
}
