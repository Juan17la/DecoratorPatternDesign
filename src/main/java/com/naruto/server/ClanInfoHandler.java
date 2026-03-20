package com.naruto.server;

import com.google.gson.Gson;
import com.naruto.game.ClanAppearance;
import com.naruto.server.util.HandlerUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;

public class ClanInfoHandler implements HttpHandler {

    private static final Gson GSON = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            HandlerUtil.sendError(exchange, 405, "Method Not Allowed");
            return;
        }
        String json = GSON.toJson(Map.of(
            "fixed", ClanAppearance.FIXED,
            "clans", ClanAppearance.ALL
        ));
        HandlerUtil.sendJson(exchange, 200, json);
    }
}
