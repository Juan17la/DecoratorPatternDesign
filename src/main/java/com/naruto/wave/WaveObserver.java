package com.naruto.wave;

import com.naruto.enemy.Enemy;

import java.util.List;

public interface WaveObserver {
    void onWaveStart(List<Enemy> enemies);
    void onWaveEnd(boolean allEnemiesDefeated);
}
