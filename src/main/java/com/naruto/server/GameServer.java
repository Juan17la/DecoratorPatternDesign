package com.naruto.server;

import com.naruto.mission.MissionManager;
import com.naruto.reward.RewardPool;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;

public class GameServer {

    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        MissionManager missionManager = new MissionManager();
        RewardPool rewardPool = new RewardPool();

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/", exchange -> {
            try {
                String uri = exchange.getRequestURI().getPath();
                Path base = Path.of("frontend");
                Path file = uri.equals("/") ? base.resolve("index.html") : base.resolve(uri.substring(1));

                if (Files.exists(file) && !Files.isDirectory(file)) {
                    byte[] bytes = Files.readAllBytes(file);
                    String contentType = guessContentType(file.toString());
                    exchange.getResponseHeaders().set("Content-Type", contentType);
                    exchange.sendResponseHeaders(200, bytes.length);
                    exchange.getResponseBody().write(bytes);
                } else {
                    byte[] body = "404 Not Found".getBytes();
                    exchange.sendResponseHeaders(404, body.length);
                    exchange.getResponseBody().write(body);
                }
            } finally {
                exchange.close();
            }
        });

        server.createContext("/clans",           new ClanInfoHandler());
        server.createContext("/player/clan",    new SetPlayerClanHandler());
        server.createContext("/state",          new StateHandler(missionManager));
        server.createContext("/tower/place",     new PlaceTowerHandler());
        server.createContext("/tower/upgrade",   new UpgradeTowerHandler());
        server.createContext("/missions",         new MissionsHandler(missionManager));
        server.createContext("/mission/start",   new MissionStartHandler(missionManager));
        server.createContext("/wave/next",       new NextWaveHandler(missionManager));
        server.createContext("/rewards",         new RewardsHandler(rewardPool, missionManager));
        server.createContext("/reward/collect",  new CollectRewardHandler(rewardPool));
        server.createContext("/jutsu/merge",     new JutsuMergeHandler());
        server.createContext("/wave/tick",       new WaveTickHandler(missionManager));
        server.createContext("/tower/targeting", new SetTargetingHandler());

        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();

        System.out.println("Naruto Tower Defense server running at http://localhost:" + PORT);
    }

    private static String guessContentType(String filename) {
        if (filename.endsWith(".html")) return "text/html; charset=UTF-8";
        if (filename.endsWith(".css"))  return "text/css; charset=UTF-8";
        if (filename.endsWith(".js"))   return "application/javascript; charset=UTF-8";
        return "application/octet-stream";
    }
}
