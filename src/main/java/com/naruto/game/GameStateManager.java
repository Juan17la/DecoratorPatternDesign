package com.naruto.game;

import com.google.gson.Gson;
import com.naruto.jutsu.JutsuInventory;
import com.naruto.tower.Tower;

import java.util.ArrayList;
import java.util.List;

public class GameStateManager {

    public static final int MAX_TOWERS = 4;

    private static GameStateManager instance;

    private ClanType playerClan;
    private NinjaRank playerRank;
    private int totalMissions;
    private int successfulMissions;
    private JutsuInventory inventory;
    private List<Tower> activeTowers;
    private int lives;
    private int coins;

    // Global weapon / chakra upgrade levels (apply to all towers)
    private int kunaiLevel    = 0;   // +8 dmg, +0.5 range per level
    private int shurikenLevel = 0;   // +6 dmg per level, +1 target per 3 levels
    private int chakraBonus   = 0;   // flat bonus added to every tower's maxChakra

    private GameStateManager() {
        playerClan = ClanType.UCHIHA;
        playerRank = NinjaRank.GENIN;
        totalMissions = 0;
        successfulMissions = 0;
        inventory = new JutsuInventory();
        activeTowers = new ArrayList<>();
        lives = 20;
        coins = 200;
    }

    public static GameStateManager getInstance() {
        if (instance == null) {
            instance = new GameStateManager();
        }
        return instance;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    // --- Getters ---

    public ClanType getPlayerClan() { return playerClan; }
    public NinjaRank getPlayerRank() { return playerRank; }
    public int getTotalMissions() { return totalMissions; }
    public int getSuccessfulMissions() { return successfulMissions; }
    public JutsuInventory getInventory() { return inventory; }
    public List<Tower> getActiveTowers() { return activeTowers; }
    public int getLives() { return lives; }
    public int getCoins() { return coins; }

    public double getSuccessRate() {
        return totalMissions == 0 ? 0.0 : (double) successfulMissions / totalMissions;
    }

    // --- Setters ---

    public void setPlayerClan(ClanType clan) { this.playerClan = clan; }
    public void setPlayerRank(NinjaRank rank) { this.playerRank = rank; }

    public void addMission(boolean success) {
        totalMissions++;
        if (success) successfulMissions++;
    }

    public void addCoins(int amount) { this.coins += amount; }
    public void spendCoins(int amount) { this.coins -= amount; }
    public void loseLife() { this.lives = Math.max(0, this.lives - 1); }

    public void addTower(Tower tower) {
        if (activeTowers.size() >= MAX_TOWERS) {
            throw new IllegalStateException("Maximum of " + MAX_TOWERS + " towers allowed on the field");
        }
        activeTowers.add(tower);
    }
    public boolean isGameOver() { return lives <= 0; }

    // ── Upgrade accessors ───────────────────────────────────────────────────

    public int  getKunaiLevel()    { return kunaiLevel; }
    public int  getShurikenLevel() { return shurikenLevel; }
    public int  getChakraBonus()   { return chakraBonus; }

    public void incrementKunaiLevel()    { kunaiLevel++; }
    public void incrementShurikenLevel() { shurikenLevel++; }
    public void addChakraBonus(int amount) { chakraBonus += amount; }
}
