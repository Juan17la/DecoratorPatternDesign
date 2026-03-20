package com.naruto.server;

import com.google.gson.Gson;
import com.naruto.reward.RewardPool;
import com.naruto.server.util.HandlerUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class RewardsHandler implements HttpHandler {

    private final RewardPool rewardPool;
    private final Gson gson = new Gson();

    public RewardsHandler(RewardPool rewardPool) {
        this.rewardPool = rewardPool;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            HandlerUtil.sendError(exchange, 405, "Method Not Allowed");
            return;
        }
        var cards = rewardPool.drawCards();
        HandlerUtil.sendJson(exchange, 200, gson.toJson(cards));
    }
}
