package com.naruto.game;

/** How a tower selects its primary target during combat. */
public enum TargetingMode {
    FIRST,      // Farthest along the path (closest to the base) — default
    STRONGEST,  // Enemy with the most remaining HP
    FASTEST,    // Fastest-moving enemy
    CLUSTERED   // Enemy in the largest local group (best for AoE attacks)
}
