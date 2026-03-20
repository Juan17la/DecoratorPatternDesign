package com.naruto.tower;

import java.util.ArrayList;
import java.util.List;

public class GoldGeneratorModule extends TowerDecorator {

    public static final int STACK_LIMIT        = 2;
    private static final int EXTRA_COINS_BONUS = 2; // extra coins per kill

    public GoldGeneratorModule(Tower tower) {
        super(tower);
    }

    public int getExtraCoinsPerKill() {
        int base = (wrappedTower instanceof GoldGeneratorModule g) ? g.getExtraCoinsPerKill() : 0;
        return base + EXTRA_COINS_BONUS;
    }

    @Override
    public List<String> getAppliedModules() {
        List<String> m = new ArrayList<>(wrappedTower.getAppliedModules());
        m.add("GoldGenerator");
        return m;
    }

    @Override
    public String getDescription() {
        return wrappedTower.getDescription() + " [GoldGenerator]";
    }
}
