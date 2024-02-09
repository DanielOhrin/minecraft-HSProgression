package net.highskiesmc.hsprogression.api;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import net.highskiesmc.hscore.configuration.Config;
import net.highskiesmc.hscore.utils.ColorUtils;
import net.highskiesmc.hscore.utils.TextUtils;
import net.highskiesmc.hsprogression.HSProgression;
import net.highskiesmc.hsprogression.events.events.IslandSlayerLevelUpEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Island {
    private final Integer id;
    private UUID leaderUuid;
    private final UUID islandUuid;
    private final Map<IslandProgressionType, Integer> levels;
    private final Map<EntityType, Integer> slayer;
    private boolean isDeleted;

    Island(int id, @NonNull UUID leaderUuid, @NonNull UUID islandUuid, int level, int slayerLevel, boolean isDeleted) {
        this.id = id;
        this.leaderUuid = leaderUuid;
        this.islandUuid = islandUuid;
        this.levels = new HashMap<>() {{
            put(IslandProgressionType.ISLAND, level);
            put(IslandProgressionType.SLAYER, slayerLevel);
        }};

        this.slayer = new HashMap<>();
        this.isDeleted = isDeleted;
    }

    Island(@NonNull UUID leaderUuid, @NonNull UUID islandUuid, int level, int slayerLevel, boolean isDeleted) {
        this.id = null;
        this.leaderUuid = leaderUuid;
        this.islandUuid = islandUuid;
        this.levels = new HashMap<>() {{
            put(IslandProgressionType.ISLAND, level);
            put(IslandProgressionType.SLAYER, slayerLevel);
        }};

        this.slayer = new HashMap<>();
        this.isDeleted = isDeleted;
    }

    @Nullable
    Integer getId() {
        return id;
    }

    public UUID getIslandUuid() {
        return islandUuid;
    }

    public int getLevel(@NonNull IslandProgressionType type) {
        return levels.get(type);
    }

    public UUID getLeaderUuid() {
        return leaderUuid;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void setLeaderUuid(UUID leaderUuid) {
        this.leaderUuid = leaderUuid;
    }

    public void setLevel(IslandProgressionType type, int newLevel) {
        this.levels.put(type, newLevel);
    }

    public int getSlayerAmount(EntityType type) {
        return slayer.getOrDefault(type, 0);
    }

    public void contributeSlayer(EntityType type, int amount, Config config) {
        if (!this.slayer.containsKey(type)) {
            this.slayer.put(type, 0);
        }

        int oldAmount = this.slayer.get(type);
        this.slayer.put(type, oldAmount + amount);

        // Check if slayer leveled up
        SlayerLevel nextSlayerLevel = HSProgression.getApi().getSlayerLevel(getLevel(IslandProgressionType.SLAYER) + 1);
        int levelUpRequirement = (int) nextSlayerLevel.getPreviousRequired();

        if (oldAmount + amount >= levelUpRequirement && getLevel(IslandProgressionType.SLAYER) + 1 == nextSlayerLevel.getLevel()) {
            IslandSlayerLevelUpEvent event = new IslandSlayerLevelUpEvent(this, nextSlayerLevel);
            Bukkit.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                setLevel(IslandProgressionType.SLAYER, nextSlayerLevel.getLevel());

                Sound sound = Sound.valueOf(config.get("events.island-upgraded.sound", String.class,
                        "UI_TOAST_CHALLENGE_COMPLETE"));
                String msg = config.get("events.island-upgraded.message", String.class, "&e" +
                        "[&6&l!&e] &6&l/is {data-type}&7 upgraded! {current} &7unlocked!");
                String title = config.get("events.island-upgraded.title.title", String.class, "&6&l/is {data-type} " +
                        "&7Upgraded!");
                String subTitle = config.get("events.island-upgraded.title.subtitle", String.class);
                int fadeIn = config.get("events.island-upgraded.title.fade-in", int.class, 20);
                int persist = config.get("events.island-upgraded.title.persist", int.class, 40);
                int fadeOut = config.get("events.island-upgraded.title.fade-out", int.class, 20);

                HSProgressionApi api = HSProgression.getApi();
                // TODO: Genericize so don't have to repeat for every fucking thing...
                // Feedback to players
                String current =
                        TextUtils.translateColor(TextUtils.toTitleCase(api.getSlayerLevel(getLevel(IslandProgressionType.SLAYER)).getEntity().name().replace("_", " ")));
                title = TextUtils.translateColor(
                        title.replace("{data-type}", "slayer")
                                .replace("{current}", current)
                                .replace("{current-no-color}", ColorUtils.removeChatColors(current))
                );

                if (subTitle != null) {
                    subTitle = TextUtils.translateColor(
                            subTitle.replace("{data-type}", "slayer")
                                    .replace("{current}", current)
                                    .replace("{current-no-color}", ColorUtils.removeChatColors(current))
                    );
                }

                msg = TextUtils.translateColor(
                        msg.replace("{data-type}", "slayer")
                                .replace("{current}", current)
                                .replace("{current-no-color}", ColorUtils.removeChatColors(current))
                );

                List<SuperiorPlayer> members =
                        SuperiorSkyblockAPI.getIslandByUUID(getIslandUuid()).getIslandMembers(true);
                for (SuperiorPlayer member : members) {
                    Player player = member.asPlayer();

                    player.playSound(player.getLocation(), sound, 1, 1);
                    player.sendTitle(title, subTitle, fadeIn, persist, fadeOut);
                    player.sendMessage(msg);
                }
            }
        }

    }

    void setSlayerNum(EntityType entity, int amount) {
        this.slayer.put(entity, amount);
    }
}
