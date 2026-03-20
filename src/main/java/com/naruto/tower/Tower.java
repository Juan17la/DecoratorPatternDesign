package com.naruto.tower;

import com.naruto.enemy.Enemy;
import com.naruto.game.TargetingMode;

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

    // Targeting and chakra management (implemented by BaseTower, delegated by decorators)
    default TargetingMode getTargetingMode()            { return TargetingMode.FIRST; }
    default void setTargetingMode(TargetingMode mode)   { /* override in BaseTower */ }
    default void setChakra(int chakra)                  { /* override in BaseTower */ }
}
