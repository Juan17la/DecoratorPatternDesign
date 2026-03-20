package com.naruto.enemy;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class Enemy {

    private String id;
    private String name;
    private int maxHp;
    private int currentHp;
    private float speed;
    private float pathProgress;
    private List<StatusEffect> activeEffects;
    private EnemyRank rank;
    private int coinReward;

    // Catalog type and behaviors
    private EnemyType type;
    private Set<EnemyBehavior> behaviors;

    // Behavior state
    private boolean shieldActive;   // S_RANK_SHIELD / AOE_IMMUNE: absorbs one hit
    private boolean invisible;      // currently invisible (untargetable)

    public Enemy(String id, String name, int maxHp, float speed,
                 EnemyRank rank, int coinReward) {
        this(id, name, maxHp, speed, rank, coinReward, null, Set.of());
    }

    public Enemy(String id, String name, int maxHp, float speed,
                 EnemyRank rank, int coinReward,
                 EnemyType type, Set<EnemyBehavior> behaviors) {
        this.id           = id;
        this.name         = name;
        this.maxHp        = maxHp;
        this.currentHp    = maxHp;
        this.speed        = speed;
        this.pathProgress = 0.0f;
        this.activeEffects = new ArrayList<>();
        this.rank         = rank;
        this.coinReward   = coinReward;
        this.type         = type;
        this.behaviors    = behaviors.isEmpty() ? Set.of() : EnumSet.copyOf(behaviors);
        this.shieldActive = hasBehavior(EnemyBehavior.S_RANK_SHIELD)
                         || hasBehavior(EnemyBehavior.AOE_IMMUNE);
        this.invisible    = false;
    }

    // ── combat ────────────────────────────────────────────────────────────────

    public void takeDamage(int amount) {
        // Elite Boss absorbs first AoE hit via shieldActive (AOE_IMMUNE)
        // High Missing-Nin absorbs one hit via shieldActive (S_RANK_SHIELD)
        if (shieldActive) {
            shieldActive = false;
            return;
        }
        // Armor: reduce incoming damage by 40%
        if (hasBehavior(EnemyBehavior.ARMOR)) {
            amount = (int)(amount * 0.6f);
        }
        currentHp = Math.max(0, currentHp - amount);
    }

    public void applyEffect(StatusEffect effect, int durationMs) {
        // Elite Boss is immune to SLOW and FREEZE
        if (hasBehavior(EnemyBehavior.SLOW_FREEZE_IMMUNE)
                && (effect == StatusEffect.SLOW || effect == StatusEffect.FREEZE)) {
            return;
        }
        if (!activeEffects.contains(effect)) {
            activeEffects.add(effect);
        }
    }

    public void advance(float delta) {
        float effectiveSpeed = speed;
        if (activeEffects.contains(StatusEffect.FREEZE)
                || activeEffects.contains(StatusEffect.STUN)) {
            effectiveSpeed = 0;
        } else if (activeEffects.contains(StatusEffect.SLOW)) {
            effectiveSpeed *= 0.5f;
        }
        pathProgress = Math.min(1.0f, pathProgress + effectiveSpeed * delta);
    }

    // ── queries ───────────────────────────────────────────────────────────────

    public boolean isAlive()          { return currentHp > 0; }
    public boolean hasReachedBase()   { return pathProgress >= 1.0f; }
    public boolean hasBehavior(EnemyBehavior b) { return behaviors.contains(b); }

    // ── getters ───────────────────────────────────────────────────────────────

    public String              getId()            { return id; }
    public String              getName()          { return name; }
    public int                 getMaxHp()         { return maxHp; }
    public int                 getCurrentHp()     { return currentHp; }
    public float               getSpeed()         { return speed; }
    public float               getPathProgress()  { return pathProgress; }
    public List<StatusEffect>  getActiveEffects() { return activeEffects; }
    public EnemyRank           getRank()          { return rank; }
    public int                 getCoinReward()    { return coinReward; }
    public EnemyType           getType()          { return type; }
    public Set<EnemyBehavior>  getBehaviors()     { return behaviors; }
    public boolean             isShieldActive()   { return shieldActive; }
    public boolean             isInvisible()      { return invisible; }

    public void setInvisible(boolean invisible)   { this.invisible = invisible; }
}
