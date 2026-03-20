package com.naruto.jutsu;

import com.naruto.enemy.Enemy;
import com.naruto.tower.Tower;

import java.util.List;

public interface JutsuStrategy {
    String getName();
    JutsuRank getRank();
    int getChakraCost();
    void execute(Tower source, List<Enemy> enemies);
    default int getLevel() { return 1; }
}
