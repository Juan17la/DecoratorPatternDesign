package com.naruto.tower;

import com.naruto.enemy.Enemy;

import java.util.List;

public abstract class TowerDecorator implements Tower {

    protected Tower wrappedTower;

    public TowerDecorator(Tower tower) {
        this.wrappedTower = tower;
    }

    @Override public int getDamage()          { return wrappedTower.getDamage(); }
    @Override public float getAttackSpeed()   { return wrappedTower.getAttackSpeed(); }
    @Override public int getRange()           { return wrappedTower.getRange(); }
    @Override public int getChakra()          { return wrappedTower.getChakra(); }
    @Override public int getMaxChakra()       { return wrappedTower.getMaxChakra(); }
    @Override public float getChakraRegen()   { return wrappedTower.getChakraRegen(); }
    @Override public void attack(Enemy t)     { wrappedTower.attack(t); }
    @Override public List<String> getAppliedModules() { return wrappedTower.getAppliedModules(); }
    @Override public String getDescription()  { return wrappedTower.getDescription(); }
    @Override public int getTargetCount()       { return wrappedTower.getTargetCount(); }
    @Override public boolean isPiercing()       { return wrappedTower.isPiercing(); }
    @Override public boolean isArmorPiercing()  { return wrappedTower.isArmorPiercing(); }
    @Override public float getCritChance()      { return wrappedTower.getCritChance(); }
    @Override public float getSlowOnHitChance() { return wrappedTower.getSlowOnHitChance(); }
    @Override public float getConfuseChance()   { return wrappedTower.getConfuseChance(); }
}
