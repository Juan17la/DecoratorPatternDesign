package com.naruto.game;

import com.naruto.tower.Tower;

import java.util.List;

/**
 * Serialisation-safe snapshot of a Tower's computed state.
 * Tower decorators use methods, not fields, so Gson cannot serialize
 * them directly — this DTO captures every value we need in the frontend.
 */
public class TowerView {

    public final int          slot;
    public final String       description;
    public final int          damage;
    public final float        attackSpeed;
    public final int          range;
    public final int          chakra;
    public final int          maxChakra;
    public final float        chakraRegen;
    public final int          targetCount;
    public final boolean      armorPiercing;
    public final float        critChance;
    public final float        slowOnHitChance;
    public final float        confuseChance;
    public final String       targetingMode;
    public final List<String> appliedModules;

    public TowerView(int slot, Tower t) {
        this.slot            = slot;
        this.description     = t.getDescription();
        this.damage          = t.getDamage();
        this.attackSpeed     = t.getAttackSpeed();
        this.range           = t.getRange();
        this.chakra          = t.getChakra();
        this.maxChakra       = t.getMaxChakra();
        this.chakraRegen     = t.getChakraRegen();
        this.targetCount     = t.getTargetCount();
        this.armorPiercing   = t.isArmorPiercing();
        this.critChance      = t.getCritChance();
        this.slowOnHitChance = t.getSlowOnHitChance();
        this.confuseChance   = t.getConfuseChance();
        this.targetingMode   = t.getTargetingMode().name();
        this.appliedModules  = t.getAppliedModules();
    }
}
