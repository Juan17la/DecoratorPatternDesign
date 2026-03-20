package com.naruto.reward;

public class RewardCard {

    private String id;
    private String name;
    private RewardType type;
    private String value;
    private boolean collected;

    public RewardCard(String id, String name, RewardType type, String value) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.value = value;
        this.collected = false;
    }

    public void collect() { this.collected = true; }

    public String getId() { return id; }
    public String getName() { return name; }
    public RewardType getType() { return type; }
    public String getValue() { return value; }
    public boolean isCollected() { return collected; }
}
