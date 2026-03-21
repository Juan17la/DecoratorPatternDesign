package com.naruto.tower;

import com.naruto.enemy.Enemy;
import com.naruto.enemy.StatusEffect;
import com.naruto.game.ClanType;
import com.naruto.game.GameStateManager;
import com.naruto.game.TargetingMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BaseTower implements Tower {

    private static final int   BASE_DAMAGE       = 10;
    private static final int   BASE_RANGE        = 3;
    private static final float BASE_ATTACK_SPEED = 0.5f;  // 1 attack / 2 sec
    private static final int   BASE_MAX_CHAKRA   = 100;

    private static final Random RANDOM = new Random();

    private final ClanType clan;
    private int chakra;
    private TargetingMode targetingMode = TargetingMode.FIRST;

    public BaseTower(ClanType clan) {
        this.clan   = clan;
        this.chakra = getMaxChakra();   // start each wave with a full chakra pool
    }

    @Override
    public int getDamage() {
        // Kunai upgrade: +8 damage per level (06_rewards_and_progression.md)
        return BASE_DAMAGE + GameStateManager.getInstance().getKunaiLevel() * 8;
    }

    @Override
    public float getAttackSpeed() {
        // Inuzuka passive: attacks 50% faster at base level
        return clan == ClanType.INUZUKA ? BASE_ATTACK_SPEED * 1.5f : BASE_ATTACK_SPEED;
    }

    @Override
    public int getRange() {
        // Kunai upgrade: +0.5 range per level (stored as int; multiply by 10 to avoid float field)
        return BASE_RANGE + (int)(GameStateManager.getInstance().getKunaiLevel() * 0.5f);
    }

    @Override
    public int getChakra() { return chakra; }

    @Override
    public int getMaxChakra() {
        // Uzumaki passive: +30% maximum chakra; plus global chakra upgrade bonus
        int base = clan == ClanType.UZUMAKI ? (int)(BASE_MAX_CHAKRA * 1.3f) : BASE_MAX_CHAKRA;
        return base + GameStateManager.getInstance().getChakraBonus();
    }

    @Override
    public float getChakraRegen() {
        // Uzumaki passive: slowly regenerates chakra between waves
        return clan == ClanType.UZUMAKI ? 5f : 0f;
    }

    @Override
    public boolean isArmorPiercing() {
        // Hyuga passive: attacks ignore enemy chakra armor
        return clan == ClanType.HYUGA;
    }

    @Override
    public float getCritChance() {
        // Uchiha passive: +15% critical hit chance
        return clan == ClanType.UCHIHA ? 0.15f : 0f;
    }

    @Override
    public float getSlowOnHitChance() {
        // Nara passive: chance to slow enemies on hit (shadow binding)
        return clan == ClanType.NARA ? 0.25f : 0f;
    }

    @Override
    public float getConfuseChance() {
        // Inoichi passive: chance to confuse enemies (briefly stop moving)
        return clan == ClanType.INOICHI ? 0.20f : 0f;
    }

    @Override
    public TargetingMode getTargetingMode() { return targetingMode; }

    @Override
    public void setTargetingMode(TargetingMode mode) { this.targetingMode = mode; }

    @Override
    public void setChakra(int value) {
        this.chakra = Math.max(0, Math.min(getMaxChakra(), value));
    }

    @Override
    public void attack(Enemy target) {
        int damage = getDamage();

        // Uchiha passive: crits deal 2x damage
        if (clan == ClanType.UCHIHA && RANDOM.nextFloat() < getCritChance()) {
            damage *= 2;
        }

        target.takeDamage(damage);

        // Nara passive: shadow binding — chance to slow on hit
        if (clan == ClanType.NARA && RANDOM.nextFloat() < getSlowOnHitChance()) {
            target.applyEffect(StatusEffect.SLOW, 1500);
        }

        // Inoichi passive: mind technique — chance to confuse (stun briefly)
        if (clan == ClanType.INOICHI && RANDOM.nextFloat() < getConfuseChance()) {
            target.applyEffect(StatusEffect.STUN, 800);
        }
    }

    @Override
    public int getTargetCount() {
        // Shuriken upgrade: +1 extra target per 3 upgrade levels (stacks with ShurikenModule)
        return 1 + GameStateManager.getInstance().getShurikenLevel() / 3;
    }

    @Override
    public List<String> getAppliedModules() {
        return new ArrayList<>();
    }

    @Override
    public String getDescription() {
        return clan.name() + " Tower";
    }

    public ClanType getClan() { return clan; }
}
