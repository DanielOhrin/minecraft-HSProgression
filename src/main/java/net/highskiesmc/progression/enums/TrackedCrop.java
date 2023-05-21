package net.highskiesmc.progression.enums;

import org.bukkit.Material;

public enum TrackedCrop {
    WHEAT(Material.WHEAT),
    BEETROOT(Material.BEETROOTS),
    CARROT(Material.CARROTS),
    POTATO(Material.POTATOES),
    SUGAR_CANE(Material.SUGAR_CANE),
    KELP(Material.KELP),
    NETHER_WART(Material.NETHER_WART),
    SWEET_BERRIES(Material.SWEET_BERRY_BUSH),
    BAMBOO(Material.BAMBOO),
    CHORUS_FLOWER(Material.CHORUS_FLOWER),
    CACTUS(Material.CACTUS),
    MELON(Material.MELON),
    PUMPKIN(Material.PUMPKIN),
    COCOA_BEANS(Material.COCOA_BEANS);
    private final Material MATERIAL;
    TrackedCrop(Material material) {
        this.MATERIAL = material;
    }
    public Material getMaterial() {
        return this.MATERIAL;
    }
    public String getValue() {
        return this.name().replace('_', '-').toLowerCase();
    }

}
