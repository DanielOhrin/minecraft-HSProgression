package net.highskiesmc.progression;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import net.highskiesmc.progression.commands.HSProgressionCommand;
import net.highskiesmc.progression.commands.IsFarmingCommand;
import net.highskiesmc.progression.commands.IsMiningCommand;
import net.highskiesmc.progression.commands.IsSlayerCommand;
import net.highskiesmc.progression.commands.tabcompleters.HSProgressionTabComplete;
import net.highskiesmc.progression.events.handlers.*;
import net.highskiesmc.nodes.HSNodes;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class HSProgression extends JavaPlugin {
    private final HSNodes hsNodes = HSNodes.getInstance();
    private File ISLANDS_FILE;
    private FileConfiguration ISLANDS;
    private final HSProgressionAPI API = new HSProgressionAPI(this);

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        reloadIslands();

        getCommand("hsprogression").setExecutor(new HSProgressionCommand(this, this.API));
        getCommand("hsprogression").setTabCompleter(new HSProgressionTabComplete());

        SuperiorSkyblockAPI.registerCommand(new IsMiningCommand(this));
        SuperiorSkyblockAPI.registerCommand(new IsSlayerCommand(this));
        SuperiorSkyblockAPI.registerCommand(new IsFarmingCommand(this));

        Bukkit.getPluginManager().registerEvents(new PlayerFishHandler(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClickHandler(), this);
        Bukkit.getPluginManager().registerEvents(new IslandCreateHandler(this.API), this);
        Bukkit.getPluginManager().registerEvents(new EntityDeathHandler(this.API), this);
        Bukkit.getPluginManager().registerEvents(new SpawnerPlaceHandler(this.API), this);
        Bukkit.getPluginManager().registerEvents(new IslandNodePlaceHandler(this.API), this);
        Bukkit.getPluginManager().registerEvents(new IslandNodeMineHandler(this.API), this);
        Bukkit.getPluginManager().registerEvents(new IslandProgressedHandler(), this);
    }

    @Override
    public void onDisable() {

    }

    public FileConfiguration getIslands() {
        return ISLANDS;
    }

    public void saveIslands() {
        try {
            this.ISLANDS.save(this.ISLANDS_FILE);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void reloadIslands() {
        File dir = getDataFolder();
        File file = new File(dir, "islands.yml");

        if (!dir.exists()) dir.mkdir();

        try {
            if (!file.exists()) file.createNewFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        this.ISLANDS_FILE = file;
        this.ISLANDS = YamlConfiguration.loadConfiguration(file);
    }
}
