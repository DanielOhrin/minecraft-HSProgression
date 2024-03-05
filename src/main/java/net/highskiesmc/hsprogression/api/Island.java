package net.highskiesmc.hsprogression.api;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import net.highskiesmc.hscore.configuration.Config;
import net.highskiesmc.hscore.utils.ColorUtils;
import net.highskiesmc.hscore.utils.TextUtils;
import net.highskiesmc.hsprogression.HSProgression;
import net.highskiesmc.hsprogression.events.events.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
    private final Map<Material, Integer> farming;
    private final Map<String, Integer> mining;
    private final Map<String, Integer> fishing;
    private boolean isDeleted;

    Island(int id, @NonNull UUID leaderUuid, @NonNull UUID islandUuid, int level, int slayerLevel,
           int farmingLevel, int miningLevel, int fishingLevel, boolean isDeleted) {
        this.id = id;
        this.leaderUuid = leaderUuid;
        this.islandUuid = islandUuid;
        this.levels = new HashMap<>() {{
            put(IslandProgressionType.ISLAND, level);
            put(IslandProgressionType.SLAYER, slayerLevel);
            put(IslandProgressionType.FARMING, farmingLevel);
            put(IslandProgressionType.MINING, miningLevel);
            put(IslandProgressionType.FISHING, fishingLevel);
        }};

        this.farming = new HashMap<>();
        this.slayer = new HashMap<>();
        this.mining = new HashMap<>();
        this.fishing = new HashMap<>();
        this.isDeleted = isDeleted;
    }

    Island(@NonNull UUID leaderUuid, @NonNull UUID islandUuid, int level, int slayerLevel,
           int farmingLevel, int miningLevel, int fishingLevel, boolean isDeleted) {
        this.id = null;
        this.leaderUuid = leaderUuid;
        this.islandUuid = islandUuid;
        this.levels = new HashMap<>() {{
            put(IslandProgressionType.ISLAND, level);
            put(IslandProgressionType.SLAYER, slayerLevel);
            put(IslandProgressionType.FARMING, farmingLevel);
            put(IslandProgressionType.MINING, miningLevel);
            put(IslandProgressionType.FISHING, fishingLevel);
        }};

        this.farming = new HashMap<>();
        this.slayer = new HashMap<>();
        this.mining = new HashMap<>();
        this.fishing = new HashMap<>();
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

    public int getFarmingAmount(Material type) {
        return farming.getOrDefault(type, 0);
    }
    public int getMiningAmount(String nodeId) {
        return mining.getOrDefault(nodeId, 0);
    }
    public int getFishingAmount(String fishId) {
        return fishing.getOrDefault(fishId, 0);
    }

    public void contributeSlayer(EntityType type, int amount, Config config) {
        if (!this.slayer.containsKey(type)) {
            this.slayer.put(type, 0);
        }

        int oldAmount = this.slayer.get(type);
        this.slayer.put(type, oldAmount + amount);

        // Return if already max level
        int newLevel = getLevel(IslandProgressionType.SLAYER) + 1;
        if (HSProgression.getApi().getSlayerLevels().size() == newLevel) {
            return;
        }

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

    public void contributeFarming(Material crop, int amount, Config config) {
        if (!this.farming.containsKey(crop)) {
            this.farming.put(crop, 0);
        }

        int oldAmount = this.farming.get(crop);
        this.farming.put(crop, oldAmount + amount);

        // Return if already max level
        int newLevel = getLevel(IslandProgressionType.FARMING) + 1;
        if (HSProgression.getApi().getFarmingLevels().size() == newLevel) {
            return;
        }

        // Check if farming leveled up
        FarmingLevel nextFarmingLevel =
                HSProgression.getApi().getFarmingLevel(newLevel);
        int levelUpRequirement = (int) nextFarmingLevel.getPreviousRequired();

        if (oldAmount + amount >= levelUpRequirement && getLevel(IslandProgressionType.FARMING) + 1 == nextFarmingLevel.getLevel()) {
            IslandFarmingLevelUpEvent event = new IslandFarmingLevelUpEvent(this, nextFarmingLevel);
            Bukkit.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                setLevel(IslandProgressionType.FARMING, nextFarmingLevel.getLevel());

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

                // Feedback to players
                String current =
                        TextUtils.translateColor(TextUtils.toTitleCase(api.getFarmingLevel(getLevel(IslandProgressionType.FARMING)).getCrop().name().replace("_", " ")));
                title = TextUtils.translateColor(
                        title.replace("{data-type}", "farming")
                                .replace("{current}", current)
                                .replace("{current-no-color}", ColorUtils.removeChatColors(current))
                );

                if (subTitle != null) {
                    subTitle = TextUtils.translateColor(
                            subTitle.replace("{data-type}", "farming")
                                    .replace("{current}", current)
                                    .replace("{current-no-color}", ColorUtils.removeChatColors(current))
                    );
                }

                msg = TextUtils.translateColor(
                        msg.replace("{data-type}", "farming")
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

    public void contributeMining(String nodeId, int amount, Config config) {
        if (!this.mining.containsKey(nodeId)) {
            this.mining.put(nodeId, 0);
        }

        int oldAmount = this.mining.get(nodeId);
        this.mining.put(nodeId, oldAmount + amount);

        // Return if already max level
        int newLevel = getLevel(IslandProgressionType.MINING) + 1;
        if (HSProgression.getApi().getMiningLevels().size() == newLevel) {
            return;
        }

        // Check if mining leveled up
        MiningLevel nextMiningLevel = HSProgression.getApi().getMiningLevel(newLevel);
        int levelUpRequirement = (int) nextMiningLevel.getPreviousRequired();

        if (oldAmount + amount >= levelUpRequirement && getLevel(IslandProgressionType.MINING) + 1 == nextMiningLevel.getLevel()) {
            IslandMiningLevelUpEvent event = new IslandMiningLevelUpEvent(this, nextMiningLevel);
            Bukkit.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                setLevel(IslandProgressionType.MINING, nextMiningLevel.getLevel());

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

                // Feedback to players
                String current =
                        TextUtils.translateColor(TextUtils.toTitleCase(api.getMiningLevel(getLevel(IslandProgressionType.MINING)).getNodeId().replace("-", " ")));
                title = TextUtils.translateColor(
                        title.replace("{data-type}", "mining")
                                .replace("{current}", current)
                                .replace("{current-no-color}", ColorUtils.removeChatColors(current))
                );

                if (subTitle != null) {
                    subTitle = TextUtils.translateColor(
                            subTitle.replace("{data-type}", "mining")
                                    .replace("{current}", current)
                                    .replace("{current-no-color}", ColorUtils.removeChatColors(current))
                    );
                }

                msg = TextUtils.translateColor(
                        msg.replace("{data-type}", "mining")
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
    public void contributeFishing(String fishId, int amount, Config config) {
        if (!this.fishing.containsKey(fishId)) {
            this.fishing.put(fishId, 0);
        }

        int oldAmount = this.fishing.get(fishId);
        this.fishing.put(fishId, oldAmount + amount);

        // Return if already max level
        int newLevel = getLevel(IslandProgressionType.FISHING) + 1;
        if (HSProgression.getApi().getFishingLevels().size() == newLevel) {
            return;
        }

        // Check if fishing leveled up
        FishingLevel nextFishingLevel =
                HSProgression.getApi().getFishingLevel(newLevel);
        int levelUpRequirement = (int) nextFishingLevel.getPreviousRequired();

        if (oldAmount + amount >= levelUpRequirement && getLevel(IslandProgressionType.FISHING) + 1 == nextFishingLevel.getLevel()) {
            IslandFishingLevelUpEvent event = new IslandFishingLevelUpEvent(this, nextFishingLevel);
            Bukkit.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                setLevel(IslandProgressionType.FISHING, nextFishingLevel.getLevel());

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

                // Feedback to players
                String current =
                        TextUtils.translateColor(TextUtils.toTitleCase(api.getFishingLevel(getLevel(IslandProgressionType.FISHING)).getLabel()));
                title = TextUtils.translateColor(
                        title.replace("{data-type}", "fishing")
                                .replace("{current}", current)
                                .replace("{current-no-color}", ColorUtils.removeChatColors(current))
                );

                if (subTitle != null) {
                    subTitle = TextUtils.translateColor(
                            subTitle.replace("{data-type}", "fishing")
                                    .replace("{current}", current)
                                    .replace("{current-no-color}", ColorUtils.removeChatColors(current))
                    );
                }

                msg = TextUtils.translateColor(
                        msg.replace("{data-type}", "fishing")
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

    void setFarmingNum(Material crop, int amount) {
        this.farming.put(crop, amount);
    }
    void setMiningNum(String nodeId, int amount) {
        this.mining.put(nodeId, amount);
    }
    void setFishingNum(String fishId, int amount) {
        this.fishing.put(fishId, amount);
    }

    public void claimRecipe(Player player, Material crop, Config config) {
        if (canClaimRecipe(crop)) {
            IslandFarmingLevelUpEvent event = new IslandFarmingLevelUpEvent(this,
                    HSProgression.getApi().getFarmingLevel(getLevel(IslandProgressionType.FARMING) + 1));
            Bukkit.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                this.setLevel(IslandProgressionType.FARMING, getLevel(IslandProgressionType.FARMING) + 1);

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

                // Feedback to players
                String current =
                        TextUtils.translateColor(TextUtils.toTitleCase(api.getFarmingLevel(getLevel(IslandProgressionType.FARMING)).getCrop().name().replace("_", " ")));
                title = TextUtils.translateColor(
                        title.replace("{data-type}", "farming")
                                .replace("{current}", current)
                                .replace("{current-no-color}", ColorUtils.removeChatColors(current))
                );

                if (subTitle != null) {
                    subTitle = TextUtils.translateColor(
                            subTitle.replace("{data-type}", "farming")
                                    .replace("{current}", current)
                                    .replace("{current-no-color}", ColorUtils.removeChatColors(current))
                    );
                }

                msg = TextUtils.translateColor(
                        msg.replace("{data-type}", "farming")
                                .replace("{current}", current)
                                .replace("{current-no-color}", ColorUtils.removeChatColors(current))
                );

                String recipeClaimed = TextUtils.translateColor(
                        config.get("island.recipe-claimed", String.class, "&f{player} &6claimed {recipe} &6on " +
                                        "&f{leader}'s &fisland!")
                                .replace("{player}", player.getName())
                                .replace("{recipe}",
                                        FarmingRecipe.getRecipe(crop, config).getItemMeta().getDisplayName())
                                .replace("{leader}", Bukkit.getOfflinePlayer(leaderUuid).getName())
                );
                List<SuperiorPlayer> members =
                        SuperiorSkyblockAPI.getIslandByUUID(getIslandUuid()).getIslandMembers(true);
                for (SuperiorPlayer member : members) {
                    Player playr = member.asPlayer();

                    playr.sendMessage(recipeClaimed);
                    playr.sendMessage();
                    playr.playSound(playr.getLocation(), sound, 1, 1);
                    playr.sendTitle(title, subTitle, fadeIn, persist, fadeOut);
                    playr.sendMessage(msg);
                }
            }
        }
    }

    public boolean canClaimRecipe(Material crop) {
        List<FarmingLevel> levels = HSProgression.getApi().getFarmingLevels();
        List<Material> crops =
                new java.util.ArrayList<>(levels.stream().map(FarmingLevel::getCrop).toList());
        Material firstCrop = crops.remove(0);

        if (crops.contains(crop)) {
            int i = crops.indexOf(crop);
            int levelToUnlock = i + 2;

            int amountPreviousFarmed = 0;
            if (i == 0) {
                amountPreviousFarmed = farming.getOrDefault(firstCrop, 0);
            } else {
                farming.getOrDefault(crops.get(i - 1), 0);
            }

            // Get the farmed requirement for unlocking the crop
            int halfOfNormalRequirement = (int)levels.get(i + 1).getPreviousRequired() / 2;

            return (levelToUnlock - getLevel(IslandProgressionType.FARMING) == 1) && amountPreviousFarmed >= halfOfNormalRequirement;
        }

        return false;
    }
}
