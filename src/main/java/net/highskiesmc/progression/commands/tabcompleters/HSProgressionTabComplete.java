package net.highskiesmc.progression.commands.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class HSProgressionTabComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> result = new ArrayList<>();

        if (args.length == 1 && sender.hasPermission("hsprogression.reload")) {
            result.add("reload");
        }

        return result;
    }
}
