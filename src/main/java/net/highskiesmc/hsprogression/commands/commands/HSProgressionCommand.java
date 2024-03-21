package net.highskiesmc.hsprogression.commands.commands;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import net.highskiesmc.hscore.commands.HSCommand;
import net.highskiesmc.hscore.highskies.HSPlugin;
import net.highskiesmc.hscore.utils.PlayerUtils;
import net.highskiesmc.hscore.utils.TextUtils;
import net.highskiesmc.hsprogression.HSProgression;
import net.highskiesmc.hsprogression.api.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class HSProgressionCommand extends HSCommand {
    private final HSProgressionApi api;
    public HSProgressionCommand(HSPlugin main, HSProgressionApi api) {
        super(main);

        this.api = api;
    }

    @Override
    protected String getPermissionToReload() {
        return "hsprogression.cmd.reload";
    }

    @Override
    public boolean executeCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String noPermissionMsg = config.get("commands.no-permission", String.class, "&cInsufficient permission.");
        String reloadMsg = config.get("commands.reload.success", String.class, "&aReloaded HSProgression!");
        String usage = config.get("commands.usage", String.class, "&cUnknown command. Usage: {usage}");

        if (args.length >= 1) {
            switch (args[0].toLowerCase()) {
                case "reload" -> {
                    return reload(sender,
                            TextUtils.translateColor(reloadMsg),
                            TextUtils.translateColor(noPermissionMsg)
                    );
                }
                case "giverecipe" -> {
                    return giveRecipe(sender, args);
                }
                case "unlock" -> {
                    return unlock(sender, args);
                }
            }
        }

        sender.sendMessage(TextUtils.translateColor(usage).replace("{usage}", "Check tab completion."));
        return false;

    }

    private boolean unlock(CommandSender sender, String[] args) {
        if (!hasPermission(sender, "hsprogression.cmd.unlock",
                TextUtils.translateColor(config.get("commands.no-permission", String.class,
                        "&cInsufficient permission.")))) {
            return false;
        }

        String usage = config.get("commands.usage", String.class, "&cUnknown command. Usage: {usage}");

        if (args.length != 4) {
            sender.sendMessage(TextUtils.translateColor(
                    usage.replace("{usage}", "/hsp unlock <player> <skill> <level>")
            ));
            return false;
        }

        Player player = Bukkit.getPlayer(args[1]);

        if (player == null || !player.isOnline()) {
            sender.sendMessage(TextUtils.translateColor(
                    config.get("commands.no-player", String.class, "&cPlayer not found or is offline: {player}")
                            .replace("{player}", args[1])
            ));
            return false;
        }

        Island sIsland = SuperiorSkyblockAPI.getPlayer(player).getIsland();

        if (sIsland == null) {
            player.sendMessage(TextUtils.translateColor(
                    config.get("common.no-island", String.class, "&c&lError | &7You don't have an island.")
                            .replace("{You don't}", "Player does not")
            ));
            return false;
        }

        IslandProgressionType levelType;

        try {
            levelType = IslandProgressionType.valueOf(args[2].toUpperCase());
        } catch (EnumConstantNotPresentException ignore) {
            sender.sendMessage(TextUtils.translateColor(
                    usage.replace("{usage}", "/hsp unlock <player> <skill> <level>")
            ));
            return false;
        }

        String unlockedLabel = args[3];

        net.highskiesmc.hsprogression.api.Island island = api.getIsland(sIsland);
        int level = getLevel(levelType, unlockedLabel);

        island.setLevel(levelType, level);
        sender.sendMessage(TextUtils.translateColor(
                config.get("commands.unlock.success", String.class, "&aUnlocked {label} for {player}'s island!")
                        .replace("{label}", unlockedLabel)
                        .replace("{player}", player.getName())
        ));
        return true;
    }

    private int getLevel(IslandProgressionType levelType, String label) {
        int level = -1;

        switch (levelType) {
            case ISLAND -> {
                try {
                    level = Math.min(api.getIslandLevels().size(), Math.max(1, Integer.parseInt(label)));
                } catch (NumberFormatException ignore) {

                }
            }
            case SLAYER -> {
                List<SlayerLevel> sLevels = api.getSlayerLevels();
                SlayerLevel sLevel =
                        sLevels.stream().filter(x -> x.getEntity().name().equalsIgnoreCase(label)).findFirst().orElse(sLevels.get(0));

                level = sLevels.indexOf(sLevel) + 1;
            }
            case MINING -> {
                List<MiningLevel> mLevels = api.getMiningLevels();
                MiningLevel mLevel =
                        mLevels.stream().filter(x -> x.getNodeId().equalsIgnoreCase(label)).findFirst().orElse(mLevels.get(0));

                level = mLevels.indexOf(mLevel) + 1;
            }
            case FARMING -> {
                List<FarmingLevel> fLevels = api.getFarmingLevels();
                FarmingLevel fLevel =
                        fLevels.stream().filter(x -> x.getCrop().name().equalsIgnoreCase(label)).findFirst().orElse(fLevels.get(0));

                level = fLevels.indexOf(fLevel) + 1;
            }
            case FISHING -> {
                List<FishingLevel> fLevels = api.getFishingLevels();
                FishingLevel fLevel =
                        fLevels.stream().filter(x -> x.getLabel().equalsIgnoreCase(label)).findFirst().orElse(fLevels.get(0));

                level = fLevels.indexOf(fLevel) + 1;
            }
        }

        return level;
    }

    private boolean giveRecipe(CommandSender sender, String[] args) {
        // /hsprogression giverecipe Crop IGN [Amount]

        if (!hasPermission(sender, "hsprogression.cmd.giverecipe", TextUtils.translateColor(config.get("commands" +
                ".no-permission", String.class, "&cInsufficient permission.")))
        ) {
            return false;
        }

        String usage = config.get("commands.usage", String.class, "&cUnknown command. Usage: {usage}");

        if (args.length != 4 && args.length != 3) {
            sender.sendMessage(TextUtils.translateColor(
                    usage.replace("{usage}", "/hsp giverecipe <crop> <name> [amount]")
            ));
            return false;
        }

        String cropStr = args[1];
        String ign = args[2];

        if (Material.getMaterial(cropStr) == null) {
            sender.sendMessage(TextUtils.translateColor(
                    config.get("commands.giverecipe.invalid-crop", String.class,
                            "&cInvalid crop type: {crop}").replace("{crop}", cropStr)
            ));
            return false;
        }

        Material crop = Material.valueOf(cropStr);
        List<String> crops =
                new java.util.ArrayList<>(HSProgression.getApi().getFarmingLevels().stream().map(x -> x.getCrop().name()).toList());
        crops.remove(0);
        if (!crops.contains(crop.name())) {
            sender.sendMessage(TextUtils.translateColor(
                    config.get("commands.giverecipe.invalid-crop", String.class,
                            "&cInvalid crop type: {crop}").replace("{crop}", cropStr)
            ));
            return false;
        }

        Player player = Bukkit.getPlayer(ign);
        if (player == null || !player.isOnline()) {
            sender.sendMessage(TextUtils.translateColor(
                    config.get("commands.no-player", String.class, "&cPlayer not found or is offline: {player}")
                            .replace("{player}", ign)
            ));
            return false;
        }

        // Crop and IGN is validated now
        ItemStack recipe = FarmingRecipe.getRecipe(crop, config);

        if (args.length == 4) {
            // Try to get/set amount from last argument
            try {
                int amount = Integer.parseInt(args[3]);

                recipe.setAmount(amount);
            } catch (NumberFormatException e) {
                sender.sendMessage(TextUtils.translateColor(
                        config.get("commands.number-exception", String.class, "&cNumber format exception occurred. " +
                                        "Defaulted to value {value}")
                                .replace("{value}", String.valueOf(1))
                ));
            }
        }

        PlayerUtils.giveItem(player, recipe, config.get("common.inventory-overflow", String.class, "&cInventory " +
                "overflow. Dropped extra items."));

        String successMsg = config.get("commands.giverecipe.success", String.class, "&aGave player {amount} {crop} " +
                        "recipe")
                .replace("{amount}", String.valueOf(recipe.getAmount()))
                .replace("{crop}", crop.name()
                );

        sender.sendMessage(TextUtils.translateColor(successMsg));
        return true;
    }
}
