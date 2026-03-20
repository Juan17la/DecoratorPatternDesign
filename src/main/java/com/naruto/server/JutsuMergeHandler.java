package com.naruto.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.naruto.game.GameStateManager;
import com.naruto.jutsu.JutsuInventory;
import com.naruto.jutsu.JutsuStrategy;
import com.naruto.server.util.HandlerUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class JutsuMergeHandler implements HttpHandler {

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
            String jutsuName = json.get("jutsuName").getAsString();

            JutsuInventory inventory = GameStateManager.getInstance().getInventory();
            JutsuStrategy jutsu = inventory.getJutsu(jutsuName);
            if (jutsu == null) {
                HandlerUtil.sendError(exchange, 404, "Jutsu not found");
                return;
            }
            boolean merged = inventory.merge(jutsu);
            if (merged) {
                int newLevel     = inventory.getLevel(jutsu);
                int mergesNeeded = inventory.getMergesNeeded(jutsuName);
                JsonObject resp = new JsonObject();
                resp.addProperty("status",       "merged");
                resp.addProperty("jutsu",        jutsuName);
                resp.addProperty("level",        newLevel);
                resp.addProperty("mergesNeeded", mergesNeeded);
                HandlerUtil.sendJson(exchange, 200, GSON.toJson(resp));
            } else {
                HandlerUtil.sendError(exchange, 400, "Not enough copies to merge");
            }
        } catch (Exception e) {
            HandlerUtil.sendError(exchange, 400, e.getMessage());
        }
    }
}
