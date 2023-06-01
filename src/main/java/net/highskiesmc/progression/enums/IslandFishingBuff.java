package net.highskiesmc.progression.enums;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum IslandFishingBuff {
    DOUBLE_XP,
    DOUBLE_DROPS;

    public String getFormattedName() {
        return Arrays.stream(name().toLowerCase().split("_")).map(s -> s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase()).collect(Collectors.joining(" "));
    }
}
