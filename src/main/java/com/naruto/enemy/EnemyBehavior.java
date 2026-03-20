package com.naruto.enemy;

/**
 * Special behavioral flags attached to enemy instances.
 * The game loop checks these to apply unique mechanics during combat.
 */
public enum EnemyBehavior {
    ERRATIC,          // hard to target (Lost Cat)
    ARMOR,            // takes reduced damage from physical hits
    INVISIBLE,        // briefly untargetable (Spy)
    RAPID_ATTACK,     // attacks tower twice per second (Criminal)
    JUTSU_USER,       // can use jutsus to deal tower damage (Leaf/Stone Ninja)
    SAND_BARRIER,     // immune to physical damage for 2 seconds periodically (Sand Ninja)
    SPAWNER,          // spawns 2 Bandits on death (Gang Leader)
    RANDOM_JUTSU,     // unpredictable jutsu behavior (Missing-Nin)
    MIST_CLONE,       // doubles health briefly once (Jonin from Kiri)
    FIRE_IMMUNE,      // immune to BURN / fire damage (Jonin from Iwa)
    S_RANK_SHIELD,    // absorbs one hit completely (High-Level Missing-Nin)
    AOE_IMMUNE,       // absorbs first AoE hit (Elite Boss)
    SLOW_FREEZE_IMMUNE, // immune to SLOW and FREEZE (Elite Boss)
    MINI_BOSS         // counts as a mini-boss spawn
}
