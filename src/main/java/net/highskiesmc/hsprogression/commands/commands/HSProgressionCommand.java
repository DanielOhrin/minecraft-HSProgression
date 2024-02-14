package net.highskiesmc.hsprogression.commands.commands;

import net.highskiesmc.hscore.commands.HSCommand;
import net.highskiesmc.hscore.highskies.HSPlugin;
import net.highskiesmc.hscore.utils.PlayerUtils;
import net.highskiesmc.hscore.utils.TextUtils;
import net.highskiesmc.hsprogression.HSProgression;
import net.highskiesmc.hsprogression.api.FarmingRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class HSProgressionCommand extends HSCommand {
    public HSProgressionCommand(HSPlugin main) {
        super(main);
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
            }
        }

        sender.sendMessage(TextUtils.translateColor(usage).replace("{usage}", "Check tab completion."));
        return false;

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
