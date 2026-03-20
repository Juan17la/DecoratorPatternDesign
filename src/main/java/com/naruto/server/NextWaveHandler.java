package com.naruto.server;

import com.google.gson.Gson;
import com.naruto.mission.MissionManager;
import com.naruto.server.util.HandlerUtil;
import com.naruto.wave.WaveManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class NextWaveHandler implements HttpHandler {

    private final MissionManager missionManager;
    private final Gson gson = new Gson();

    public NextWaveHandler(MissionManager missionManager) {
        this.missionManager = missionManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            HandlerUtil.sendError(exchange, 405, "Method Not Allowed");
            return;
        }
        WaveManager wm = missionManager.getWaveManager();
        if (wm == null) {
            HandlerUtil.sendError(exchange, 400, "No active mission");
            return;
        }
        if (!wm.hasMoreWaves()) {
            HandlerUtil.sendError(exchange, 400, "No more waves");
            return;
        }
        wm.startNextWave();
        String json = "{\"wave\":" + wm.getCurrentWaveNumber() + ",\"enemies\":" + gson.toJson(wm.getCurrentWave()) + "}";
        HandlerUtil.sendJson(exchange, 200, json);
    }
}
