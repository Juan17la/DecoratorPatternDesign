package com.naruto.server;

import com.google.gson.Gson;
import com.naruto.game.CombatEngine;
import com.naruto.game.GameStateManager;
import com.naruto.game.TickResult;
import com.naruto.mission.MissionManager;
import com.naruto.server.util.HandlerUtil;
import com.naruto.wave.WaveManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

/**
 * POST /wave/tick
 *
 * Advances the combat simulation by one tick (~100 ms).
 * The frontend calls this in a setInterval loop while a wave is active.
 *
 * Response: {@link TickResult} serialised as JSON.
 * When {@code waveComplete} is true the caller should stop polling and
 * present the reward panel (or end-of-mission screen if no waves remain).
 */
public class WaveTickHandler implements HttpHandler {

    private static final Gson GSON = new Gson();

    private final MissionManager missionManager;

    public WaveTickHandler(MissionManager missionManager) {
        this.missionManager = missionManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            HandlerUtil.sendError(exchange, 405, "Method Not Allowed");
            return;
        }

        WaveManager wm = missionManager.getWaveManager();
        if (wm == null || !wm.isWaveActive()) {
            HandlerUtil.sendError(exchange, 400, "No active wave");
            return;
        }

        TickResult result;
        // Synchronise on the shared game state to prevent concurrent tick races
        synchronized (GameStateManager.getInstance()) {
            result = CombatEngine.tick(
                GameStateManager.getInstance().getActiveTowers(),
                wm,
                GameStateManager.getInstance()
            );

            if (result.waveComplete) {
                wm.endWave();
                if (!wm.hasMoreWaves()) {
                    missionManager.completeMission(true);
                }
            }
        }

        HandlerUtil.sendJson(exchange, 200, GSON.toJson(result));
    }
}
