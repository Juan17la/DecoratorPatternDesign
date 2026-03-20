package com.naruto.wave;

import com.naruto.enemy.Enemy;
import com.naruto.mission.MissionRank;
import com.naruto.mission.MissionType;

import java.util.ArrayList;
import java.util.List;

public class WaveManager {

    private final List<WaveObserver> observers    = new ArrayList<>();
    private final WaveGenerator      generator    = new WaveGenerator();
    private final MissionRank        missionRank;
    private final MissionType        missionType;
    private final int                totalWaves;

    private List<Enemy> currentWave       = new ArrayList<>();
    private int         currentWaveNumber = 0;

    public WaveManager(int totalWaves, MissionRank missionRank, MissionType missionType) {
        this.totalWaves  = totalWaves;
        this.missionRank = missionRank;
        this.missionType = missionType;
    }

    public void addObserver(WaveObserver observer)    { observers.add(observer); }
    public void removeObserver(WaveObserver observer) { observers.remove(observer); }

    public void startNextWave() {
        if (currentWaveNumber >= totalWaves) return;
        currentWaveNumber++;
        currentWave = generator.generate(missionRank, missionType, currentWaveNumber);
        observers.forEach(o -> o.onWaveStart(currentWave));
    }

    public void endWave() {
        boolean allDefeated = currentWave.stream().noneMatch(Enemy::isAlive);
        observers.forEach(o -> o.onWaveEnd(allDefeated));
    }

    public List<Enemy> getCurrentWave()    { return currentWave; }
    public int         getCurrentWaveNumber() { return currentWaveNumber; }
    public int         getTotalWaves()     { return totalWaves; }
    public boolean     hasMoreWaves()      { return currentWaveNumber < totalWaves; }
}
