package com.naruto.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.naruto.game.GameStateManager;
import com.naruto.game.TargetingMode;
import com.naruto.server.util.HandlerUtil;
import com.naruto.tower.Tower;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;

/**
 * POST /tower/targeting
 * Body: { "slot": 0, "mode": "FIRST" }
 *
 * Sets the targeting priority of the tower in the given slot.
 * Valid modes: FIRST | STRONGEST | FASTEST | CLUSTERED
 */
public class SetTargetingHandler implements HttpHandler {

    private static final Gson GSON = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            HandlerUtil.sendError(exchange, 405, "Method Not Allowed");
            return;
        }
        try {
            String     body = HandlerUtil.readBody(exchange);
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();
            int    slot = json.get("slot").getAsInt();
            String mode = json.get("mode").getAsString().toUpperCase();

            List<Tower> towers = GameStateManager.getInstance().getActiveTowers();
            if (slot < 0 || slot >= towers.size()) {
                HandlerUtil.sendError(exchange, 400, "Invalid slot");
                return;
            }

            TargetingMode tm = TargetingMode.valueOf(mode);
            towers.get(slot).setTargetingMode(tm);

            JsonObject resp = new JsonObject();
            resp.addProperty("status", "ok");
            resp.addProperty("slot",   slot);
            resp.addProperty("mode",   tm.name());
            HandlerUtil.sendJson(exchange, 200, GSON.toJson(resp));

        } catch (IllegalArgumentException e) {
            HandlerUtil.sendError(exchange, 400,
                "Unknown targeting mode. Use: FIRST, STRONGEST, FASTEST, CLUSTERED");
        } catch (Exception e) {
            HandlerUtil.sendError(exchange, 500, e.getMessage());
        }
    }
}
