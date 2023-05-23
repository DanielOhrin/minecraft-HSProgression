package net.highskiesmc.progression;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import net.highskiesmc.nodes.HSNodes;
import net.highskiesmc.progression.commands.HSProgressionCommand;
import net.highskiesmc.progression.commands.IsFarmingCommand;
import net.highskiesmc.progression.commands.IsMiningCommand;
import net.highskiesmc.progression.commands.IsSlayerCommand;
import net.highskiesmc.progression.commands.tabcompleters.HSProgressionTabComplete;
import net.highskiesmc.progression.events.handlers.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public final class HSProgression extends JavaPlugin {
    private final HSNodes hsNodes = HSNodes.getInstance();
    private File ISLANDS_FILE;
    private FileConfiguration ISLANDS;
    private final HSProgressionAPI API = new HSProgressionAPI(this);
    private static Economy econ = null;

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!",
                    getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getConfig().options().copyDefaults();
        saveDefaultConfig();
        reloadIslands();

        getCommand("hsprogression").setExecutor(new HSProgressionCommand(this, this.API));
        getCommand("hsprogression").setTabCompleter(new HSProgressionTabComplete());

        SuperiorSkyblockAPI.registerCommand(new IsMiningCommand(this.API));
        SuperiorSkyblockAPI.registerCommand(new IsSlayerCommand(this.API));
        SuperiorSkyblockAPI.registerCommand(new IsFarmingCommand(this.API));

        Bukkit.getPluginManager().registerEvents(new PlayerFishHandler(), this);
        Bukkit.getPluginManager().registerEvents(new GUIEventHandlers(this.API), this);
        Bukkit.getPluginManager().registerEvents(new IslandCreateHandler(this.API), this);
        Bukkit.getPluginManager().registerEvents(new EntityDeathHandler(this.API), this);
        Bukkit.getPluginManager().registerEvents(new SpawnerPlaceHandler(this.API), this);
        Bukkit.getPluginManager().registerEvents(new IslandNodePlaceHandler(this.API), this);
        Bukkit.getPluginManager().registerEvents(new IslandNodeMineHandler(this.API), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceHandler(this.API), this);
        Bukkit.getPluginManager().registerEvents(new BlockGrowHandler(this.API), this);
        Bukkit.getPluginManager().registerEvents(new BlockSpreadHandler(this.API), this);
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
            this.getLogger().severe(Arrays.toString(ex.getStackTrace()));
        }
    }

    public void reloadIslands() {
        File dir = getDataFolder();
        File file = new File(dir, "islands.yml");

        if (!dir.exists()) dir.mkdir();

        try {
            if (!file.exists()) file.createNewFile();
        } catch (IOException ex) {
            this.getLogger().severe(Arrays.toString(ex.getStackTrace()));
        }

        this.ISLANDS_FILE = file;
        this.ISLANDS = YamlConfiguration.loadConfiguration(file);
    }

    public static Economy getEconomy() {
        return econ;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
}
