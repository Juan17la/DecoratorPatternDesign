package com.naruto.jutsu;

/**
 * Gameplay effect categories used by CatalogJutsu.execute().
 * Each constant maps to a specific combination of damage, status, and targeting behaviour.
 */
public enum EffectType {
    DAMAGE_SINGLE,      // damage 1 enemy
    DAMAGE_MULTI,       // damage N enemies (targets field)
    DAMAGE_AOE,         // damage all enemies in range
    DAMAGE_LINE,        // damage up to N enemies in a line
    PIERCE_LINE,        // pierce through a line of enemies (same as DAMAGE_LINE)
    STOP_SINGLE,        // STUN 1 enemy
    STOP_MULTI,         // STUN N enemies
    STOP_ALL,           // STUN all enemies on screen
    SLOW_SINGLE,        // SLOW 1 enemy
    SLOW_AOE,           // SLOW all enemies in area
    BURN_SINGLE,        // damage + BURN 1 enemy
    BURN_AOE,           // damage + BURN all enemies in area
    BURN_PATH,          // place burning trap on path
    DAMAGE_STOP_AOE,    // damage + STUN all in area
    DAMAGE_SLOW_AOE,    // damage + SLOW all in area
    CHAIN_BIND,         // STUN N enemies (chakra chain variant)
    KNOCKBACK,          // push 1 enemy back
    KNOCKBACK_AOE,      // push all enemies back
    CHAKRA_RESTORE,     // restore chakra to the caster tower
    CHAKRA_AOE_BURST,   // close-range chakra burst (damage)
    DECOY_CLONE,        // spawn N decoy clones to distract enemies
    OBSTACLE,           // create temporary path obstacle
    REVEAL,             // reveal / informational effect
    INSTANT_KILL,       // remove 1 non-boss enemy
    DAMAGE_BOOST,       // temporarily boost tower damage
    SPEED_BOOST_SELF,   // temporarily boost tower attack speed
    SHIELD,             // make tower temporarily untargetable / immune
    REFLECT,            // reflect projectiles + damage nearby enemies
    HOMING              // homing projectile toward strongest enemy
}
