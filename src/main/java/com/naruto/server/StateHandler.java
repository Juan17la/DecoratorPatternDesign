package com.naruto.server;

import com.naruto.game.GameStateManager;
import com.naruto.server.util.HandlerUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class StateHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            HandlerUtil.sendError(exchange, 405, "Method Not Allowed");
            return;
        }
        String json = GameStateManager.getInstance().toJson();
        HandlerUtil.sendJson(exchange, 200, json);
    }
}
