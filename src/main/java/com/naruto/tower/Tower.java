package com.naruto.tower;

import com.naruto.enemy.Enemy;

import java.util.List;

public interface Tower {
    int getDamage();
    float getAttackSpeed();   // attacks per second
    int getRange();
    int getChakra();
    int getMaxChakra();
    float getChakraRegen();   // chakra per second
    void attack(Enemy target);
    List<String> getAppliedModules();
    String getDescription();

    default int getTargetCount()       { return 1; }
    default boolean isPiercing()       { return false; }
    default boolean isArmorPiercing()  { return false; }
    default float getCritChance()      { return 0f; }
    default float getSlowOnHitChance() { return 0f; }
    default float getConfuseChance()   { return 0f; }
}
