package net.highskiesmc.progression.enums;

public enum IslandDataType {
    MINING("mining"),
    SLAYER("slayer"),
    FARMING("farming"),
    FISHING("fishing");
    private final String VALUE;
    IslandDataType(String value) {
        this.VALUE = value;
    }
    public String getValue() {
        return this.VALUE;
    }
}
