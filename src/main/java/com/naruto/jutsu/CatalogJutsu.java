package com.naruto.jutsu;

import com.naruto.enemy.Enemy;
import com.naruto.enemy.StatusEffect;
import com.naruto.tower.Tower;

import java.util.Comparator;
import java.util.List;

/**
 * JutsuStrategy implementation backed by a JutsuDefinition from JutsuCatalog.
 * Damage and duration scale +20% / +15% per level above 1.
 */
public class CatalogJutsu implements JutsuStrategy {

    private final JutsuDefinition def;
    private int level = 1;

    public CatalogJutsu(JutsuDefinition def) {
        this.def = def;
    }

    // ── JutsuStrategy ─────────────────────────────────────────────────────────

    @Override public String    getName()      { return def.name(); }
    @Override public JutsuRank getRank()      { return def.rank(); }
    @Override public int       getChakraCost(){ return def.chakraCost(); }
    @Override public int       getLevel()     { return level; }

    public void setLevel(int level) { this.level = Math.max(1, Math.min(5, level)); }

    public JutsuDefinition getDefinition() { return def; }

    // ── execute ───────────────────────────────────────────────────────────────

    @Override
    public void execute(Tower source, List<Enemy> enemies) {
        int dmg = scaledDamage(def.damage());
        int dur = scaledDuration(def.duration());

        switch (def.effectType()) {
            case DAMAGE_SINGLE -> {
                if (!enemies.isEmpty()) enemies.get(0).takeDamage(dmg);
            }
            case DAMAGE_MULTI -> enemies.stream()
                    .limit(targets()).forEach(e -> e.takeDamage(dmg));
            case DAMAGE_AOE -> enemies.forEach(e -> e.takeDamage(dmg));
            case DAMAGE_LINE, PIERCE_LINE -> enemies.stream()
                    .limit(targets()).forEach(e -> e.takeDamage(dmg));
            case STOP_SINGLE -> {
                if (!enemies.isEmpty()) enemies.get(0).applyEffect(StatusEffect.STUN, dur);
            }
            case STOP_MULTI -> enemies.stream()
                    .limit(targets()).forEach(e -> e.applyEffect(StatusEffect.STUN, dur));
            case STOP_ALL, CHAIN_BIND ->
                    enemies.forEach(e -> e.applyEffect(StatusEffect.STUN, dur));
            case SLOW_SINGLE -> {
                if (!enemies.isEmpty()) enemies.get(0).applyEffect(StatusEffect.SLOW, dur);
            }
            case SLOW_AOE -> enemies.forEach(e -> e.applyEffect(StatusEffect.SLOW, dur));
            case BURN_SINGLE -> {
                if (!enemies.isEmpty()) {
                    enemies.get(0).takeDamage(dmg);
                    enemies.get(0).applyEffect(StatusEffect.BURN, dur);
                }
            }
            case BURN_AOE -> enemies.forEach(e -> {
                e.takeDamage(dmg);
                e.applyEffect(StatusEffect.BURN, dur);
            });
            case DAMAGE_STOP_AOE -> enemies.forEach(e -> {
                e.takeDamage(dmg);
                e.applyEffect(StatusEffect.STUN, dur);
            });
            case DAMAGE_SLOW_AOE -> enemies.forEach(e -> {
                e.takeDamage(dmg);
                e.applyEffect(StatusEffect.SLOW, dur);
            });
            case KNOCKBACK -> {
                if (!enemies.isEmpty()) enemies.get(0).takeDamage(dmg);
            }
            case KNOCKBACK_AOE, CHAKRA_AOE_BURST, REFLECT ->
                    enemies.forEach(e -> e.takeDamage(dmg));
            case INSTANT_KILL -> {
                if (!enemies.isEmpty()) enemies.get(0).takeDamage(Integer.MAX_VALUE);
            }
            case HOMING -> enemies.stream()
                    .max(Comparator.comparingInt(Enemy::getMaxHp))
                    .ifPresent(e -> e.takeDamage(dmg));
            // Effects resolved by the game loop:
            case CHAKRA_RESTORE, DAMAGE_BOOST, SPEED_BOOST_SELF,
                 SHIELD, REVEAL, DECOY_CLONE, OBSTACLE, BURN_PATH -> { /* no-op here */ }
        }
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    /** +20% per level above 1. */
    private int scaledDamage(int base) {
        if (base == 0 || level <= 1) return base;
        return (int)(base * (1.0 + 0.20 * (level - 1)));
    }

    /** +15% per level above 1. */
    private int scaledDuration(int base) {
        if (base == 0 || level <= 1) return base;
        return (int)(base * (1.0 + 0.15 * (level - 1)));
    }

    private long targets() {
        return def.targets() > 0 ? def.targets() : Long.MAX_VALUE;
    }
}
