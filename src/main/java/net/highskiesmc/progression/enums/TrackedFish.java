package net.highskiesmc.progression.enums;

import java.util.HashMap;
import java.util.Map;

public enum TrackedFish {
    ONE(null),
    TWO(new HashMap<IslandFishingBuff, Double>() {{
        put(IslandFishingBuff.DOUBLE_XP, 0.005D);
    }}),
    THREE(new HashMap<IslandFishingBuff, Double>() {{
        put(IslandFishingBuff.DOUBLE_XP, 0.01D);
        put(IslandFishingBuff.DOUBLE_DROPS, 0.005D);
    }}),
    FOUR(new HashMap<IslandFishingBuff, Double>() {{
        put(IslandFishingBuff.DOUBLE_XP, 0.025D);
        put(IslandFishingBuff.DOUBLE_DROPS, 0.01D);
    }}),
    FIVE(new HashMap<IslandFishingBuff, Double>() {{
        put(IslandFishingBuff.DOUBLE_XP, 0.05D);
        put(IslandFishingBuff.DOUBLE_DROPS, 0.025D);
    }});
    private static final String VALUE = "Treasure Hunter";
    private final Map<IslandFishingBuff, Double> BUFFS;

    TrackedFish(Map<IslandFishingBuff, Double> buffs) {
        this.BUFFS = buffs;
    }

    public String getValue() {
        return String.join("-", VALUE.toLowerCase().split(" ")) + '-' + this.name().toLowerCase();
    }

    public Map<IslandFishingBuff, Double> getBuffs() {
        return this.BUFFS;
    }
}
