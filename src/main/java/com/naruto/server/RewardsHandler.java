package com.naruto.server;

import com.google.gson.Gson;
import com.naruto.mission.Mission;
import com.naruto.mission.MissionManager;
import com.naruto.mission.MissionRank;
import com.naruto.reward.RewardPool;
import com.naruto.server.util.HandlerUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

/**
 * GET /rewards
 *
 * Draws 3 reward cards scaled to the active mission's rank.
 * Falls back to D-rank probabilities if no mission is active.
 */
public class RewardsHandler implements HttpHandler {

    private final RewardPool     rewardPool;
    private final MissionManager missionManager;
    private final Gson           gson = new Gson();

    public RewardsHandler(RewardPool rewardPool, MissionManager missionManager) {
        this.rewardPool     = rewardPool;
        this.missionManager = missionManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            HandlerUtil.sendError(exchange, 405, "Method Not Allowed");
            return;
        }
        Mission active = missionManager.getActiveMission();
        MissionRank rank = (active != null) ? active.getRank() : MissionRank.D;
        var cards = rewardPool.drawCards(rank);
        HandlerUtil.sendJson(exchange, 200, gson.toJson(cards));
    }
}
