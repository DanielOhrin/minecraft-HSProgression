package net.highskiesmc.progression.commands;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import net.highskiesmc.progression.HSProgression;
import net.highskiesmc.progression.HSProgressionAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class HSProgressionCommand implements CommandExecutor {
    private final HSProgression MAIN;
    private HSProgressionAPI API;

    public HSProgressionCommand(HSProgression main, HSProgressionAPI api) {
        this.MAIN = main;
        this.API = api;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 2) {
            if (args[0].equalsIgnoreCase("reload")) {
                return this.reload(sender);
            } else if (sender instanceof ConsoleCommandSender && args[0].equalsIgnoreCase("fixislands")) {
                return this.fixIslands();
            }
        } else {
            if (sender instanceof Player) {
                sender.sendMessage(ChatColor.RED + "/hsprogression <reload>");
            } else if (sender instanceof ConsoleCommandSender) {
                Bukkit.getLogger().warning("/hsprogression <reload/fixislands>");
            }
        }

        return false;
    }

    private boolean reload(CommandSender sender) {
        if (sender.hasPermission("hsprogression.reload")) {
            this.MAIN.reloadConfig();
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
}
