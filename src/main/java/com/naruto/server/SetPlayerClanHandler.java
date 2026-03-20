package com.naruto.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.naruto.game.ClanType;
import com.naruto.game.GameStateManager;
import com.naruto.server.util.HandlerUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class SetPlayerClanHandler implements HttpHandler {

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
            GameStateManager.getInstance().setPlayerClan(clan);
            JsonObject resp = new JsonObject();
            resp.addProperty("status", "ok");
            resp.addProperty("clan",   clan.name());
            HandlerUtil.sendJson(exchange, 200, GSON.toJson(resp));
        } catch (IllegalArgumentException e) {
            HandlerUtil.sendError(exchange, 400, "Unknown clan: " + e.getMessage());
        } catch (Exception e) {
            HandlerUtil.sendError(exchange, 500, e.getMessage());
        }
    }
}
