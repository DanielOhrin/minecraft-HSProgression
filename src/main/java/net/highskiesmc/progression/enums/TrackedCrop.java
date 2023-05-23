package net.highskiesmc.progression.enums;

import org.bukkit.Material;

public enum TrackedCrop {
    WHEAT(Material.WHEAT, Material.WHEAT),
    BEETROOT(Material.BEETROOTS, Material.BEETROOT),
    CARROT(Material.CARROTS, Material.CARROT),
    POTATO(Material.POTATOES, Material.POTATO),
    SUGAR_CANE(Material.SUGAR_CANE, Material.SUGAR_CANE),
    KELP(Material.KELP, Material.KELP),
    NETHER_WART(Material.NETHER_WART, Material.NETHER_WART),
    SWEET_BERRIES(Material.SWEET_BERRY_BUSH, Material.SWEET_BERRIES),
    BAMBOO(Material.BAMBOO_SAPLING, Material.BAMBOO),
    CHORUS_FLOWER(Material.CHORUS_FLOWER, Material.CHORUS_FLOWER),
    CACTUS(Material.CACTUS, Material.CACTUS),
    MELON(Material.MELON_STEM, Material.MELON_SLICE),
    PUMPKIN(Material.PUMPKIN_STEM, Material.PUMPKIN),
    COCOA_BEANS(Material.COCOA, Material.COCOA_BEANS);
    private final Material MATERIAL;
    private final Material DROPPED_MATERIAL;
    TrackedCrop(Material material, Material droppedMaterial) {
        this.MATERIAL = material;
        this.DROPPED_MATERIAL = droppedMaterial;
    }
    public Material getMaterial() {
        return this.MATERIAL;
    }
    public Material getDroppedMaterial() {
        return this.DROPPED_MATERIAL;
    }
    public String getValue() {
        return this.name().replace('_', '-').toLowerCase();
    }

}
