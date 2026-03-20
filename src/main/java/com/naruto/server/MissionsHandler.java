package com.naruto.server;

import com.google.gson.Gson;
import com.naruto.mission.MissionManager;
import com.naruto.server.util.HandlerUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

/**
 * GET /missions — returns the list of missions available to the player at their current rank,
 * including any unlocked special missions (Chunin Exam, Jounin Promotion).
 */
public class MissionsHandler implements HttpHandler {

    private final MissionManager missionManager;
    private final Gson gson = new Gson();

    public MissionsHandler(MissionManager missionManager) {
        this.missionManager = missionManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            HandlerUtil.sendError(exchange, 405, "Method Not Allowed");
            return;
        }
        HandlerUtil.sendJson(exchange, 200,
            gson.toJson(missionManager.getAvailableMissions()));
    }
}
