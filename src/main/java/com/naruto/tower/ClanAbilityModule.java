package com.naruto.tower;

import java.util.ArrayList;
import java.util.List;

public class ClanAbilityModule extends TowerDecorator {

    public static final int STACK_LIMIT      = 1;
    private static final float DAMAGE_BOOST  = 1.15f;

    public ClanAbilityModule(Tower tower) {
        super(tower);
    }

    @Override
    public int getDamage() {
        return (int) (wrappedTower.getDamage() * DAMAGE_BOOST);
    }

    @Override
    public List<String> getAppliedModules() {
        List<String> m = new ArrayList<>(wrappedTower.getAppliedModules());
        m.add("ClanAbility");
        return m;
    }

    @Override
    public String getDescription() {
        return wrappedTower.getDescription() + " [ClanAbility]";
    }
}
