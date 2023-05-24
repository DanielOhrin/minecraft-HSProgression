package net.highskiesmc.progression.commands.tabcompleters;

import net.highskiesmc.progression.enums.TrackedCrop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HSProgressionTabComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> result = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("hsprogression.reload")) {
                result.add("reload");
            }
            if (sender.hasPermission("hsprogression.getrecipe")) {
                result.add("getRecipe");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("getRecipe") && sender.hasPermission("hsprogression" +
                ".getrecipe")) {
            result.addAll(Arrays.stream(TrackedCrop.values()).map(c -> c.getValue()).collect(Collectors.toList()));
            result.remove(0);
        }

        return result;
    }
}
