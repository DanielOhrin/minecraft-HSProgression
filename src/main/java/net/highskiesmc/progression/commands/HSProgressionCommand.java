package net.highskiesmc.progression.commands;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import net.highskiesmc.progression.HSProgression;
import net.highskiesmc.progression.HSProgressionAPI;
import net.highskiesmc.progression.enums.TrackedCrop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;

public class HSProgressionCommand implements CommandExecutor {
    private final HSProgression MAIN;
    private HSProgressionAPI API;

    public HSProgressionCommand(HSProgression main, HSProgressionAPI api) {
        this.MAIN = main;
        this.API = api;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                return this.reload(sender);
            } else if (args[0].equalsIgnoreCase("fixislands")) {
                if (sender instanceof ConsoleCommandSender) {
                    return this.fixIslands();
                } else if (sender instanceof Player) {
                    sender.sendMessage(ChatColor.RED + "This can only be used from console!");
                }
            } else if (args[0].equalsIgnoreCase("getRecipe")) {
                if (sender instanceof ConsoleCommandSender) {
                    Bukkit.getLogger().warning("This command must be used by a player!");
                } else if (sender instanceof Player) {
                    sender.sendMessage(ChatColor.RED + "/hsp getRecipe <crop-name>");
                }
            } else {
                if (sender instanceof ConsoleCommandSender) {
                    Bukkit.getLogger().warning("/hsp <reload/fixislands>");
                } else if (sender instanceof Player) {
                    sender.sendMessage(ChatColor.RED + "/hsp <reload/getRecipe>");
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("getRecipe")) {
                if (sender instanceof Player) {
                    return this.getRecipe(sender, args);
                } else if (sender instanceof ConsoleCommandSender) {
                    Bukkit.getLogger().warning("This command must be used by a player!");
                }
            } else {
                if (sender instanceof ConsoleCommandSender) {
                    Bukkit.getLogger().warning("/hsp <reload/fixislands>");
                } else if (sender instanceof Player) {
                    sender.sendMessage(ChatColor.RED + "/hsp <reload/getRecipe>");
                }
            }
        } else {
            if (sender instanceof Player) {
                sender.sendMessage(ChatColor.RED + "/hsp <reload/getRecipe>");
            } else if (sender instanceof ConsoleCommandSender) {
                Bukkit.getLogger().warning("/hsp <reload/fixislands>");
            }
        }

        return false;
    }

    private boolean reload(CommandSender sender) {
        if (sender.hasPermission("hsprogression.reload")) {
            this.MAIN.reloadConfig();
            this.MAIN.reloadIslands();
            if (sender instanceof Player) {
                sender.sendMessage(ChatColor.GREEN + "Successfully reloaded hsprogression!");
            } else if (sender instanceof ConsoleCommandSender) {
                Bukkit.getLogger().info("Successfully reloaded hsprogression!");
            }
            return true;
        }
        return false;
    }

    private boolean fixIslands() {
        final ConfigurationSection ISLANDS = this.MAIN.getIslands();

        Bukkit.getLogger().info("Creating missing island data...");

        for (Island island : SuperiorSkyblockAPI.getGrid().getIslands()) {
            if (!ISLANDS.isSet(island.getUniqueId().toString())) {
                this.API.createIslandData(island);
            }
        }

        Bukkit.getLogger().info("Done!");
        return true;
    }

    private boolean getRecipe(CommandSender sender, String[] args) {
        if (sender.hasPermission("hsprogression.getrecipe")) {
            Optional<TrackedCrop> optionalCrop =
                    Arrays.stream(TrackedCrop.values()).filter(c -> c.getValue().equals(args[1].toLowerCase())).findFirst();

            if (optionalCrop.isPresent() && optionalCrop.get() != TrackedCrop.values()[0]) {
                ((Player) sender).getInventory().addItem(this.API.getFullRecipe(optionalCrop.get()));
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid crop-type given.");
            }
        }

        return true;
    }
}
