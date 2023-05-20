package net.highskiesmc.progression;

import net.highskiesmc.progression.events.handlers.PlayerFishHandler;
import net.highskiesmc.nodes.HSNodes;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class HSProgression extends JavaPlugin {
    private final HSNodes hsNodes = HSNodes.getInstance();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new PlayerFishHandler(), this);
    }

    @Override
    public void onDisable() {

    }
}
