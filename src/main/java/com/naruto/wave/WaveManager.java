package com.naruto.wave;

import com.naruto.enemy.Enemy;
import com.naruto.mission.MissionRank;
import com.naruto.mission.MissionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WaveManager {

    private final List<WaveObserver> observers    = new ArrayList<>();
    private final WaveGenerator      generator    = new WaveGenerator();
    private final MissionRank        missionRank;
    private final MissionType        missionType;
    private final int                totalWaves;

    private List<Enemy> currentWave       = new ArrayList<>();
    private int         currentWaveNumber = 0;

    // ── Combat state ─────────────────────────────────────────────────────────
    /** True while a wave is actively running (enemies still on the field). */
    private boolean waveActive = false;

    /**
     * Per-tower-slot attack cooldown in seconds.
     * Cleared at the start of each wave so towers can attack immediately.
     */
    private final Map<Integer, Float>   towerCooldowns = new HashMap<>();

    /**
     * Per-tower-slot current chakra.
     * Cleared at the start of each wave; initialized lazily to maxChakra on
     * first use in {@link com.naruto.game.CombatEngine}.
     */
    private final Map<Integer, Integer> towerChakra    = new HashMap<>();

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
        waveActive  = true;
        // Clear combat state; chakra is lazily initialised to maxChakra in CombatEngine
        towerCooldowns.clear();
        towerChakra.clear();
        observers.forEach(o -> o.onWaveStart(currentWave));
    }

    public void endWave() {
        waveActive = false;
        boolean allDefeated = currentWave.stream().noneMatch(Enemy::isAlive);
        observers.forEach(o -> o.onWaveEnd(allDefeated));
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public List<Enemy>          getCurrentWave()       { return currentWave; }
    public int                  getCurrentWaveNumber() { return currentWaveNumber; }
    public int                  getTotalWaves()        { return totalWaves; }
    public boolean              hasMoreWaves()         { return currentWaveNumber < totalWaves; }
    public boolean              isWaveActive()         { return waveActive; }
    public Map<Integer, Float>  getTowerCooldowns()    { return towerCooldowns; }
    public Map<Integer, Integer> getTowerChakra()      { return towerChakra; }
}
