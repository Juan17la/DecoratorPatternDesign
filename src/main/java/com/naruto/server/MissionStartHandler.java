package com.naruto.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.naruto.mission.Mission;
import com.naruto.mission.MissionManager;
import com.naruto.server.util.HandlerUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class MissionStartHandler implements HttpHandler {

    private static final Gson GSON = new Gson();

    private final MissionManager missionManager;

    public MissionStartHandler(MissionManager missionManager) {
        this.missionManager = missionManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            HandlerUtil.sendError(exchange, 405, "Method Not Allowed");
            return;
        }
        try {
            String body = HandlerUtil.readBody(exchange);
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();
            String missionId = json.get("missionId").getAsString();

            boolean started = missionManager.startMission(missionId);
            if (!started) {
                HandlerUtil.sendError(exchange, 403, "Mission not found or rank insufficient");
                return;
            }

            Mission m = missionManager.getActiveMission();
            JsonObject resp = new JsonObject();
            resp.addProperty("status",      "started");
            resp.addProperty("missionId",   missionId);
            resp.addProperty("totalWaves",  m.getTotalWaves());
            resp.addProperty("rank",        m.getRank().name());
            resp.addProperty("type",        m.getType().name());
            if (m.hasLivesLimit()) {
                resp.addProperty("livesLimit", m.getLivesLostLimit());
            }
            HandlerUtil.sendJson(exchange, 200, GSON.toJson(resp));
        } catch (Exception e) {
            HandlerUtil.sendError(exchange, 400, e.getMessage());
        }
    }
}
