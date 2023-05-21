package net.highskiesmc.progression.enums;

import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.List;

public enum TrackedEntity {
    RABBIT(EntityType.RABBIT),
    CHICKEN(EntityType.CHICKEN),
    SHEEP(EntityType.SHEEP),
    PIG(EntityType.PIG),
    COW(EntityType.COW),
    ZOMBIE(EntityType.ZOMBIE),
    SPIDER(EntityType.SPIDER),
    CREEPER(EntityType.CREEPER),
    SKELETON(EntityType.SKELETON),
    SLIME(EntityType.SLIME),
    GUARDIAN(EntityType.GUARDIAN),
    ENDERMAN(EntityType.ENDERMAN),
    MAGMA_CUBE(EntityType.MAGMA_CUBE),
    BLAZE(EntityType.BLAZE),
    GHAST(EntityType.GHAST),
    WITHER_SKELETON(EntityType.WITHER_SKELETON),
    IRON_GOLEM(EntityType.IRON_GOLEM);
    private final EntityType ENTITY_TYPE;

    TrackedEntity(EntityType entityType) {
        this.ENTITY_TYPE = entityType;
    }

    public String getValue() {
        return this.ENTITY_TYPE.name().replace('_', '-').toLowerCase();
    }

    public EntityType getEntityType() {
        return this.ENTITY_TYPE;
    }
}
