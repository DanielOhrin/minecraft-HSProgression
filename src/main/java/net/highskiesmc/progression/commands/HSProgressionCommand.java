package net.highskiesmc.progression.commands;

import net.highskiesmc.progression.HSProgression;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class HSProgressionCommand implements CommandExecutor {
    private final HSProgression MAIN;
    public HSProgressionCommand(HSProgression main) {
        this.MAIN = main;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 2) {
            if (sender.hasPermission("hsprogression.reload")) {
                this.MAIN.reloadConfig();
                if (sender instanceof Player) {
                    sender.sendMessage(ChatColor.GREEN + "Successfully reloaded hsprogression!");
                } else if (sender instanceof ConsoleCommandSender) {
                    System.out.println("Successfully reloaded hsprogression!");
                }
            }
        } else {
            if (sender instanceof Player) {
                sender.sendMessage(ChatColor.RED + "/hsprogression <reload>");
            } else if (sender instanceof ConsoleCommandSender) {
                System.out.println("/hsprogression <reload>");
            }
        }

        return false;
    }
}
