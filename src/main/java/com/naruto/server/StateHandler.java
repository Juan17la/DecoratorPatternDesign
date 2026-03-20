package com.naruto.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.naruto.game.GameStateManager;
import com.naruto.game.TowerView;
import com.naruto.server.util.HandlerUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * GET /state
 *
 * Returns a JSON snapshot of the current game state.
 * Towers are serialised as {@link TowerView} objects so the frontend
 * receives computed values (damage, attackSpeed, etc.) rather than the
 * raw Decorator chain that Gson cannot flatten automatically.
 */
public class StateHandler implements HttpHandler {

    private static final Gson GSON = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            HandlerUtil.sendError(exchange, 405, "Method Not Allowed");
            return;
        }

        GameStateManager state = GameStateManager.getInstance();

        // Build TowerView list with slot indices
        List<TowerView> towerViews = new ArrayList<>();
        var towers = state.getActiveTowers();
        for (int i = 0; i < towers.size(); i++) {
            towerViews.add(new TowerView(i, towers.get(i)));
        }

        // Assemble the response as a plain JsonObject so we can mix
        // primitive fields with the TowerView list without a separate DTO class.
        JsonObject resp = new JsonObject();
        resp.addProperty("playerClan",           state.getPlayerClan().name());
        resp.addProperty("playerRank",           state.getPlayerRank().name());
        resp.addProperty("lives",                state.getLives());
        resp.addProperty("coins",                state.getCoins());
        resp.addProperty("totalMissions",        state.getTotalMissions());
        resp.addProperty("successfulMissions",   state.getSuccessfulMissions());
        resp.addProperty("successRate",          state.getSuccessRate());
        resp.add("activeTowers",  GSON.toJsonTree(towerViews));
        resp.add("inventory",     GSON.toJsonTree(state.getInventory()));

        HandlerUtil.sendJson(exchange, 200, GSON.toJson(resp));
    }
}
