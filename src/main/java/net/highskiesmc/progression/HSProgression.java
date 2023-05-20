package net.highskiesmc.progression;

import net.highskiesmc.progression.commands.MiningCommand;
import net.highskiesmc.progression.events.handlers.InventoryClickHandler;
import net.highskiesmc.progression.events.handlers.PlayerFishHandler;
import net.highskiesmc.nodes.HSNodes;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

public final class HSProgression extends JavaPlugin {
    private final HSNodes hsNodes = HSNodes.getInstance();

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        getCommand("mining").setExecutor(new MiningCommand(this));
        Bukkit.getPluginManager().registerEvents(new PlayerFishHandler(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClickHandler(), this);
    }

    @Override
    public void onDisable() {

    }
}
