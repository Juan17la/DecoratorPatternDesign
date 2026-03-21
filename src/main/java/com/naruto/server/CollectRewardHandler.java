package com.naruto.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.naruto.game.GameStateManager;
import com.naruto.jutsu.CatalogJutsu;
import com.naruto.jutsu.JutsuCatalog;
import com.naruto.reward.RewardCard;
import com.naruto.reward.RewardPool;
import com.naruto.server.util.HandlerUtil;
import com.naruto.tower.Tower;
import com.naruto.tower.TowerModuleFactory;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;

/**
 * POST /reward/collect
 * Body: { "cardId": "r1" }
 *
 * Applies the chosen reward card to the global game state:
 *   JUTSU            — added to the player's jutsu inventory
 *   KUNAI_UPGRADE    — increments the global kunaiLevel (+8 dmg, +0.5 range per level)
 *   SHURIKEN_UPGRADE — increments the global shurikenLevel (+6 dmg, +1 target per 3 lvls)
 *   CHAKRA_UPGRADE   — adds the card's bonus value to all towers' max chakra
 *   DECORATOR_MODULE — applies the named module to the first tower that can accept it
 *   COINS            — adds coins directly
 */
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
            String body   = HandlerUtil.readBody(exchange);
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
            case JUTSU -> {
                CatalogJutsu jutsu = JutsuCatalog.create(card.getValue());
                if (jutsu != null) state.getInventory().add(jutsu);
            }
            case KUNAI_UPGRADE    -> state.incrementKunaiLevel();
            case SHURIKEN_UPGRADE -> state.incrementShurikenLevel();
            case CHAKRA_UPGRADE -> {
                int bonus = Integer.parseInt(card.getValue());
                state.addChakraBonus(bonus);
                // Refill tower chakra to the new (higher) maximum
                for (Tower t : state.getActiveTowers()) {
                    t.setChakra(t.getMaxChakra());
                }
            }
            case DECORATOR_MODULE -> {
                // Apply the module to the first tower that has capacity
                List<Tower> towers = state.getActiveTowers();
                for (int i = 0; i < towers.size(); i++) {
                    try {
                        Tower upgraded = TowerModuleFactory.apply(
                            towers.get(i), card.getValue(), state.getPlayerRank());
                        towers.set(i, upgraded);
                        break;   // only apply to one tower
                    } catch (IllegalStateException ignored) {
                        // tower is full — try the next slot
                    }
                }
            }
            case COINS -> state.addCoins(Integer.parseInt(card.getValue()));
        }
    }
}
