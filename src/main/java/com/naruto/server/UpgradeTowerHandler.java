package com.naruto.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.naruto.game.GameStateManager;
import com.naruto.game.NinjaRank;
import com.naruto.server.util.HandlerUtil;
import com.naruto.tower.Tower;
import com.naruto.tower.TowerModuleFactory;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;

public class UpgradeTowerHandler implements HttpHandler {

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
            int slot = json.get("slot").getAsInt();
            String module = json.get("module").getAsString();

            List<Tower> towers = GameStateManager.getInstance().getActiveTowers();
            if (slot < 0 || slot >= towers.size()) {
                HandlerUtil.sendError(exchange, 400, "Invalid slot");
                return;
            }

            NinjaRank rank = GameStateManager.getInstance().getPlayerRank();
            Tower current = towers.get(slot);
            Tower upgraded = TowerModuleFactory.apply(current, module, rank);

            towers.set(slot, upgraded);
            JsonObject resp = new JsonObject();
            resp.addProperty("status",      "upgraded");
            resp.addProperty("description", upgraded.getDescription());
            HandlerUtil.sendJson(exchange, 200, GSON.toJson(resp));

        } catch (IllegalArgumentException | IllegalStateException e) {
            HandlerUtil.sendError(exchange, 400, e.getMessage());
        } catch (Exception e) {
            HandlerUtil.sendError(exchange, 500, e.getMessage());
        }
    }
}
