package net.highskiesmc.progression.commands.tabcompleters;

import net.highskiesmc.progression.enums.IslandDataType;
import net.highskiesmc.progression.enums.TrackedCrop;
import net.highskiesmc.progression.enums.TrackedEntity;
import net.highskiesmc.progression.enums.TrackedNode;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

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
            if (sender.hasPermission("hsprogression.get-recipe")) {
                result.add("get-recipe");
            }
            if (sender.hasPermission("hsprogression.unlock")) {
                result.add("unlock");
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("get-recipe") && sender.hasPermission("hsprogression" +
                    ".get-recipe")) {
                result.addAll(Arrays.stream(TrackedCrop.values()).map(TrackedCrop::getValue).collect(Collectors.toList()));
                result.remove(0);
            }
            if (args[0].equalsIgnoreCase("unlock") && sender.hasPermission("hsprogression.unlock")) {
                result.addAll(Arrays.stream(IslandDataType.values()).map(IslandDataType::getValue).collect(Collectors.toList()));
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("unlock") && sender.hasPermission("hsprogression.unlock")) {
                switch (args[1].toLowerCase()) {
                    case "farming":
                        result.addAll(Arrays.stream(TrackedCrop.values()).map(TrackedCrop::getValue).collect(Collectors.toList()));
                        break;
                    case "slayer":
                        result.addAll(Arrays.stream(TrackedEntity.values()).map(TrackedEntity::getValue).collect(Collectors.toList()));
                        break;
                    case "mining":
                        result.addAll(Arrays.stream(TrackedNode.values()).map(TrackedNode::getValue).collect(Collectors.toList()));
                        break;
                    case "fishing":
                    default:
                        break;
                }
            }
        } else {
            result.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
        }

        return result;
    }
}
