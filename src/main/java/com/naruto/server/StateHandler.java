package com.naruto.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.naruto.game.GameStateManager;
import com.naruto.game.TowerView;
import com.naruto.mission.MissionManager;
import com.naruto.server.util.HandlerUtil;
import com.naruto.wave.WaveManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * GET /state
 *
 * Returns the full game state in the shape defined in 08_technical_architecture.md:
 * {
 *   "player":  { clan, rank, totalMissions, successRate, lives, coins,
 *                kunaiLevel, shurikenLevel, chakraBonus },
 *   "towers":  [ TowerView… ],
 *   "wave":    { current, total, waveActive, enemies }   // omitted if no mission active
 *   "inventory": { copies, levels, … }
 * }
 */
public class StateHandler implements HttpHandler {

    private static final Gson GSON = new Gson();

    private final MissionManager missionManager;

    public StateHandler(MissionManager missionManager) {
        this.missionManager = missionManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            HandlerUtil.sendError(exchange, 405, "Method Not Allowed");
            return;
        }

        GameStateManager state = GameStateManager.getInstance();

        // ── player ────────────────────────────────────────────────────────────
        JsonObject player = new JsonObject();
        player.addProperty("clan",             state.getPlayerClan().name());
        player.addProperty("rank",             state.getPlayerRank().name());
        player.addProperty("totalMissions",    state.getTotalMissions());
        player.addProperty("successfulMissions", state.getSuccessfulMissions());
        player.addProperty("successRate",      state.getSuccessRate());
        player.addProperty("lives",            state.getLives());
        player.addProperty("coins",            state.getCoins());
        player.addProperty("kunaiLevel",       state.getKunaiLevel());
        player.addProperty("shurikenLevel",    state.getShurikenLevel());
        player.addProperty("chakraBonus",      state.getChakraBonus());

        // ── towers ────────────────────────────────────────────────────────────
        List<TowerView> towerViews = new ArrayList<>();
        var towers = state.getActiveTowers();
        for (int i = 0; i < towers.size(); i++) {
            towerViews.add(new TowerView(i, towers.get(i)));
        }

        // ── wave ──────────────────────────────────────────────────────────────
        JsonObject wave = null;
        WaveManager wm = missionManager.getWaveManager();
        if (wm != null) {
            wave = new JsonObject();
            wave.addProperty("current",    wm.getCurrentWaveNumber());
            wave.addProperty("total",      wm.getTotalWaves());
            wave.addProperty("waveActive", wm.isWaveActive());
            wave.add("enemies", GSON.toJsonTree(wm.getCurrentWave()));
        }

        // ── assemble ──────────────────────────────────────────────────────────
        JsonObject resp = new JsonObject();
        resp.add("player",       player);
        resp.add("activeTowers", GSON.toJsonTree(towerViews));   // top-level alias kept for JS compat
        resp.add("towers",       GSON.toJsonTree(towerViews));
        if (wave != null) resp.add("wave", wave);
        resp.add("inventory",    GSON.toJsonTree(state.getInventory()));

        // Flat aliases so existing HUD renderer still works without changes
        resp.addProperty("playerClan",  state.getPlayerClan().name());
        resp.addProperty("playerRank",  state.getPlayerRank().name());
        resp.addProperty("lives",       state.getLives());
        resp.addProperty("coins",       state.getCoins());

        HandlerUtil.sendJson(exchange, 200, GSON.toJson(resp));
    }
}
