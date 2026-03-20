package com.naruto.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.naruto.game.GameStateManager;
import com.naruto.jutsu.CatalogJutsu;
import com.naruto.jutsu.JutsuCatalog;
import com.naruto.jutsu.JutsuInventory;
import com.naruto.reward.RewardCard;
import com.naruto.reward.RewardPool;
import com.naruto.server.util.HandlerUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class CollectRewardHandler implements HttpHandler {

    private static final Gson GSON = new Gson();

    private final RewardPool rewardPool;

    public CollectRewardHandler(RewardPool rewardPool) {
        this.rewardPool = rewardPool;
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
            String cardId = json.get("cardId").getAsString();

            RewardCard card = rewardPool.getCard(cardId);
            if (card == null) {
                HandlerUtil.sendError(exchange, 404, "Card not found");
                return;
            }

            applyReward(card);
            card.collect();
            JsonObject resp = new JsonObject();
            resp.addProperty("status", "collected");
            resp.addProperty("card", card.getName());
            HandlerUtil.sendJson(exchange, 200, GSON.toJson(resp));
        } catch (Exception e) {
            HandlerUtil.sendError(exchange, 400, e.getMessage());
        }
    }

    private void applyReward(RewardCard card) {
        GameStateManager state = GameStateManager.getInstance();
        switch (card.getType()) {
            case COINS -> state.addCoins(Integer.parseInt(card.getValue()));
            case JUTSU -> {
                CatalogJutsu jutsu = JutsuCatalog.create(card.getValue());
                if (jutsu != null) {
                    state.getInventory().add(jutsu);
                }
            }
            default -> { /* TOWER_MODULE and UPGRADE applied at upgrade time */ }
        }
    }
}
