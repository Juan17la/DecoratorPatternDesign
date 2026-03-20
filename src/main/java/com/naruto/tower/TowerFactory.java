package com.naruto.tower;

import com.naruto.game.ClanType;

public class TowerFactory {

    public static Tower createTower(ClanType clan) {
        return new BaseTower(clan);
    }
}
