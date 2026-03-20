package com.naruto.jutsu;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Tracks owned jutsus, their copy counts, levels, and merge progress.
 *
 * Merge thresholds (merges needed to advance one level):
 *   L1 → L2 :  1 merge
 *   L2 → L3 :  3 merges
 *   L3 → L4 :  6 merges
 *   L4 → L5 :  8 merges   (L5 = max)
 */
public class JutsuInventory {

    public static final int MAX_LEVEL = 5;

    /** Merges required to advance from level (index+1) to (index+2). */
    private static final int[] MERGE_THRESHOLDS = {1, 3, 6, 8};

    private final Map<String, Integer>      copies   = new HashMap<>();
    private final Map<String, Integer>      levels   = new HashMap<>();
    private final Map<String, Integer>      progress = new HashMap<>(); // merges done at current level
    private final Map<String, JutsuStrategy> registry = new HashMap<>();

    // ── mutation ──────────────────────────────────────────────────────────────

    public void add(JutsuStrategy jutsu) {
        String key = jutsu.getName();
        registry.put(key, jutsu);
        copies.merge(key, 1, Integer::sum);
        levels.putIfAbsent(key, 1);
        progress.putIfAbsent(key, 0);
    }

    /**
     * Consumes one duplicate copy and advances merge progress.
     * Levels up automatically when the threshold for the current level is reached.
     *
     * @return true if the merge was performed (false = not enough copies or already max level)
     */
    public boolean merge(JutsuStrategy jutsu) {
        String key = jutsu.getName();
        int level  = levels.getOrDefault(key, 1);
        int owned  = copies.getOrDefault(key, 0);

        if (owned < 2 || level >= MAX_LEVEL) return false;

        // Consume one spare copy
        copies.put(key, owned - 1);

        // Advance progress
        int newProgress = progress.merge(key, 1, Integer::sum);
        int threshold   = MERGE_THRESHOLDS[level - 1];

        if (newProgress >= threshold) {
            int newLevel = level + 1;
            levels.put(key, newLevel);
            progress.put(key, 0);
            // Propagate level to CatalogJutsu instance if applicable
            if (registry.get(key) instanceof CatalogJutsu c) {
                c.setLevel(newLevel);
            }
        }
        return true;
    }

    // ── queries ───────────────────────────────────────────────────────────────

    public boolean canMerge(JutsuStrategy jutsu) {
        String key = jutsu.getName();
        return copies.getOrDefault(key, 0) >= 2
            && levels.getOrDefault(key, 1) < MAX_LEVEL;
    }

    /** Merges still needed to reach the next level (0 if at max level). */
    public int getMergesNeeded(String name) {
        int level = levels.getOrDefault(name, 1);
        if (level >= MAX_LEVEL) return 0;
        return MERGE_THRESHOLDS[level - 1] - progress.getOrDefault(name, 0);
    }

    public int getCopies(JutsuStrategy jutsu) {
        return copies.getOrDefault(jutsu.getName(), 0);
    }

    public int getLevel(JutsuStrategy jutsu) {
        return levels.getOrDefault(jutsu.getName(), 1);
    }

    public Set<String>    getJutsuNames()          { return Collections.unmodifiableSet(registry.keySet()); }
    public JutsuStrategy  getJutsu(String name)    { return registry.get(name); }
}
