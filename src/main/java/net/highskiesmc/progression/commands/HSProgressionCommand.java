package net.highskiesmc.progression.commands;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import net.highskiesmc.progression.HSProgression;
import net.highskiesmc.progression.HSProgressionAPI;
import net.highskiesmc.progression.enums.*;
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
        if (args.length != 0) {
            switch (args[0].toLowerCase()) {
                case "reload":
                    return this.reload(sender);
                case "unlock":
                    return this.unlock(sender, args);
                case "fixislands":
                    if (sender instanceof ConsoleCommandSender) {
                        return this.fixIslands();
                    } else if (sender instanceof Player) {
                        sender.sendMessage(ChatColor.RED + "This command must be used by a console!");
                    }
                case "get-recipe":
                    if (sender instanceof ConsoleCommandSender) {
                        Bukkit.getLogger().warning("This command must be used by a player!");
                    } else if (sender instanceof Player) {
                        return getRecipe(sender, args);
                    }
                default:
                    if (sender instanceof ConsoleCommandSender) {
                        Bukkit.getLogger().warning("/hsp <reload/fixislands/unlock>");
                    } else if (sender instanceof Player && sender.hasPermission("hsprogression.commands")) {
                        sender.sendMessage(ChatColor.RED + "/hsp <reload/get-recipe/unlock>");
                    }
            }
        } else {
            if (sender instanceof ConsoleCommandSender) {
                Bukkit.getLogger().warning("/hsp <reload/fixislands/unlock>");
            } else if (sender instanceof Player) {
                sender.sendMessage(ChatColor.RED + "/hsp <reload/get-recipe/unlock>");
            }
        }
        return false;
    }

    private boolean reload(CommandSender sender) {
        if (sender.hasPermission("hsprogression.reload")) {
            this.MAIN.reloadConfig();
            this.MAIN.reloadSlayerConfig();
            this.MAIN.reloadMiningConfig();
            this.MAIN.reloadFarmingConfig();
            if (sender instanceof Player) {
                sender.sendMessage(ChatColor.GREEN + "Successfully reloaded hsprogression!");
            } else if (sender instanceof ConsoleCommandSender) {
                Bukkit.getLogger().info("Successfully reloaded hsprogression!");
            }
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Insufficient permission.");
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
        if (sender.hasPermission("hsprogression.get-recipe")) {
            Optional<TrackedCrop> optionalCrop =
                    Arrays.stream(TrackedCrop.values()).filter(c -> c.getValue().equals(args[1].toLowerCase())).findFirst();

            if (optionalCrop.isPresent() && optionalCrop.get() != TrackedCrop.values()[0]) {
                ((Player) sender).getInventory().addItem(this.API.getFullRecipe(optionalCrop.get()));
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid crop-type given.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Insufficient permission.");
        }

        return true;
    }

    private boolean unlock(CommandSender sender, String[] args) {
        // UNLOCK = 0
        // ISLANDDATATYPE = 1
        // TRACKEDTHING = 2
        // PLAYERNAME -- REQUIRED!!!
        if (sender.hasPermission("hsprogression.unlock")) {
            if (args.length == 4) {
                Optional<IslandDataType> optionalDataType =
                        Arrays.stream(IslandDataType.values()).filter(dt -> dt.getValue().equalsIgnoreCase(args[1])).findFirst();
                Optional<String> optionalTrackedThing = Optional.empty();

                if (optionalDataType.isPresent()) {
                    IslandDataType dataType = optionalDataType.get();
                    switch (dataType) {
                        case FARMING:
                            optionalTrackedThing =
                                    Arrays.stream(TrackedCrop.values()).map(TrackedCrop::getValue).filter(value -> value.equalsIgnoreCase(args[2])).findFirst();
                            break;
                        case SLAYER:
                            optionalTrackedThing =
                                    Arrays.stream(TrackedEntity.values()).map(TrackedEntity::getValue).filter(value -> value.equalsIgnoreCase(args[2])).findFirst();
                            break;
                        case MINING:
                            optionalTrackedThing =
                                    Arrays.stream(TrackedNode.values()).map(TrackedNode::getValue).filter(value -> value.equalsIgnoreCase(args[2])).findFirst();
                            break;
                        case FISHING:
                            optionalTrackedThing =
                                    Arrays.stream(TrackedFish.values()).map(TrackedFish::getValue).filter(value -> value.equalsIgnoreCase(args[2])).findFirst();
                        default:
                            break;
                    }

                    if (optionalTrackedThing.isPresent()) {
                        String trackedThing = optionalTrackedThing.get();
                        Optional<? extends Player> optionalPlayer =
                                Bukkit.getOnlinePlayers().stream().filter(p -> p.getName().equalsIgnoreCase(args[3])).findFirst();

                        if (optionalPlayer.isPresent()) {
                            Player player = optionalPlayer.get();

                            Island island = SuperiorSkyblockAPI.getPlayer(player.getUniqueId()).getIsland();

                            if (island != null) {
                                final ConfigurationSection ISLAND_DATA =
                                        this.API.getIslandData(island.getUniqueId(), dataType, trackedThing);

                                if (!ISLAND_DATA.getBoolean("unlocked")) {
                                    // Unlock everything up to and including itself.
                                    this.API.fullyUnlockIslandDataUpTo(island.getUniqueId(), dataType, trackedThing);
                                    if (sender instanceof Player) {
                                        sender.sendMessage(ChatColor.GREEN + "Success!");
                                    } else if (sender instanceof ConsoleCommandSender) {
                                        this.MAIN.getLogger().finest("Success!");
                                    }
                                    return true;
                                } else {
                                    if (sender instanceof Player) {
                                        sender.sendMessage(ChatColor.RED + "Island has already unlocked that");
                                    } else if (sender instanceof ConsoleCommandSender) {
                                        this.MAIN.getLogger().warning("Island has already unlocked that");
                                    }
                                }
                            } else {
                                if (sender instanceof Player) {
                                    sender.sendMessage(ChatColor.RED + "Provided player has no island");
                                } else if (sender instanceof ConsoleCommandSender) {
                                    this.MAIN.getLogger().warning("Provided player has no island");
                                }
                            }
                        } else {
                            if (sender instanceof Player) {
                                sender.sendMessage(ChatColor.RED + "Provided player is not online");
                            } else if (sender instanceof ConsoleCommandSender) {
                                this.MAIN.getLogger().warning("Provided player is not online");
                            }
                        }
                    } else {
                        if (sender instanceof Player) {
                            sender.sendMessage(ChatColor.RED + "Invalid tracked item");
                        } else if (sender instanceof ConsoleCommandSender) {
                            this.MAIN.getLogger().warning("Invalid tracked item");
                        }
                    }
                } else {
                    if (sender instanceof Player) {
                        sender.sendMessage(ChatColor.RED + "Invalid progression-type");
                    } else if (sender instanceof ConsoleCommandSender) {
                        this.MAIN.getLogger().warning("Invalid progression-type");
                    }
                }
            } else {
                if (sender instanceof Player) {
                    sender.sendMessage(ChatColor.RED + "Usage: /hsp unlock <progression-type> <tracked-thing> " +
                            "<online-player-name>");
                } else if (sender instanceof ConsoleCommandSender) {
                    this.MAIN.getLogger().warning("Usage: /hsp unlock <progression-type> <tracked-thing> " +
                            "<online-player-name>");
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Insufficient permission.");
        }

        return false;
    }
}
