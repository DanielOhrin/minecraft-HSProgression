package net.highskiesmc.progression.enums;

public enum TrackedNode {
    COBBLESTONE("cobblestone"),
    COAL("coal"),
    COPPER("copper"),
    NETHER_GOLD("nether-gold"),
    NETHER_QUARTZ("nether-quartz"),
    IRON("iron"),
    RUBY("ruby"),
    LAPIS("lapis"),
    DIAMOND("diamond"),
    EMERALD("emerald"),
    ANCIENT_DEBRIS("ancient-debris");
    private final String VALUE;

    TrackedNode(String value) {
        this.VALUE = value;
    }

    public String getValue() {
        return this.VALUE;
    }
}
