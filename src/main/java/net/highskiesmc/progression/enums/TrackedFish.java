package net.highskiesmc.progression.enums;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum TrackedFish {
    COD(null, 10),
    SALMON(null, 50),
    TROPICAL_FISH(null, 150),
    PUFFERFISH(null, 250),
    BLACK_MOOR_GOLDFISH("highskiesmc:black_moor_goldfish", 500),
    BLUE_PARROTFISH("highskiesmc:blue_parrotfish", 1000),
    GOLDFISH("highskiesmc:goldfish", 2500),
    GREEN_SUNFISH("highskiesmc:green_sunfish", 5000),
    TUNA("highskiesmc:tuna", 10000);

    private final String ID;
    final double XP;

    TrackedFish(String id, double xp) {
        this.ID = id;
        this.XP = xp;
    }

    public String getValue() {
        return this.name().toLowerCase().replace("_", "-");
    }

    public static @Nullable TrackedFish fromValue(@NonNull String value) {
        return Arrays.stream(TrackedFish.values()).filter(x -> x.getValue().equalsIgnoreCase(value)).findFirst().orElse(null);
    }

    public ItemStack getItemStack() {
        if (this.ID == null) {
            return new ItemStack(Material.valueOf(name()));
        }

        return CustomStack.getInstance(this.ID).getItemStack();
    }

    public double getXp() {
        return this.XP;
    }
}
