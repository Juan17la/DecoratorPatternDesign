package com.naruto.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.naruto.game.ClanType;
import com.naruto.game.GameStateManager;
import com.naruto.server.util.HandlerUtil;
import com.naruto.tower.Tower;
import com.naruto.tower.TowerFactory;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class PlaceTowerHandler implements HttpHandler {

    private static final Gson GSON = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            HandlerUtil.sendError(exchange, 405, "Method Not Allowed");
            return;
        }
        try {
            String body = HandlerUtil.readBody(exchange);
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();
            ClanType clan = ClanType.valueOf(json.get("clan").getAsString().toUpperCase());
            Tower tower = TowerFactory.createTower(clan);
            GameStateManager.getInstance().addTower(tower);
            JsonObject resp = new JsonObject();
            resp.addProperty("status",      "placed");
            resp.addProperty("description", tower.getDescription());
            HandlerUtil.sendJson(exchange, 200, GSON.toJson(resp));
        } catch (Exception e) {
            HandlerUtil.sendError(exchange, 400, e.getMessage());
        }
    }
}
