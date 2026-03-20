package com.naruto.mission;

public class Mission {

    /** Sentinel value meaning no lives-lost restriction applies. */
    public static final int NO_LIMIT = -1;

    private final String      id;
    private final String      name;
    private final MissionRank rank;
    private final MissionType type;
    private final int         totalWaves;
    private final int         coinReward;
    private final int         livesLostLimit; // -1 = no limit
    private boolean           completed;

    /** Convenience constructor for ordinary missions (no lives limit). */
    public Mission(String id, String name, MissionRank rank, int totalWaves, int coinReward) {
        this(id, name, rank, MissionType.NORMAL, totalWaves, coinReward, NO_LIMIT);
    }

    public Mission(String id, String name, MissionRank rank, MissionType type,
                   int totalWaves, int coinReward, int livesLostLimit) {
        this.id             = id;
        this.name           = name;
        this.rank           = rank;
        this.type           = type;
        this.totalWaves     = totalWaves;
        this.coinReward     = coinReward;
        this.livesLostLimit = livesLostLimit;
        this.completed      = false;
    }

    public void complete() { this.completed = true; }

    public String      getId()            { return id; }
    public String      getName()          { return name; }
    public MissionRank getRank()          { return rank; }
    public MissionType getType()          { return type; }
    public int         getTotalWaves()    { return totalWaves; }
    public int         getCoinReward()    { return coinReward; }
    public int         getLivesLostLimit(){ return livesLostLimit; }
    public boolean     isCompleted()      { return completed; }
    public boolean     hasLivesLimit()    { return livesLostLimit != NO_LIMIT; }
}
