package com.naruto.game;

import com.naruto.enemy.Enemy;

import java.util.List;

/** Snapshot returned by {@link CombatEngine#tick} after each 100 ms game step. */
public class TickResult {

    public final boolean     waveComplete;
    public final int         wave;
    public final int         totalWaves;
    public final int         lives;
    public final int         coins;
    public final List<Enemy> enemies;

    public TickResult(boolean waveComplete, int wave, int totalWaves,
                      int lives, int coins, List<Enemy> enemies) {
        this.waveComplete = waveComplete;
        this.wave         = wave;
        this.totalWaves   = totalWaves;
        this.lives        = lives;
        this.coins        = coins;
        this.enemies      = enemies;
    }
}
