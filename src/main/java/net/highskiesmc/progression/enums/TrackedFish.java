package net.highskiesmc.progression.enums;

public enum TrackedFish {
    ONE,
    TWO,
    THREE,
    FOUR,
    FIVE;
    private static final String VALUE = "Treasure Hunter";

    public String getValue() {
        return String.join("-", VALUE.toLowerCase().split(" ")) + '-' + this.name().toLowerCase();
    }
}
