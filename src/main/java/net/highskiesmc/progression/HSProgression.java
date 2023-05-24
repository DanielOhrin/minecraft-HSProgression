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
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public final class HSProgression extends JavaPlugin {
    private final HSNodes hsNodes = HSNodes.getInstance();
    private File ISLANDS_FILE;
    private FileConfiguration ISLANDS;
    private File SLAYER_FILE;
    private FileConfiguration SLAYER_CONFIG;
    private File FARMING_FILE;
    private FileConfiguration FARMING_CONFIG;
    private File MINING_FILE;
    private FileConfiguration MINING_CONFIG;
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

        reloadIslands();
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        reloadSlayerConfig();
        reloadFarmingConfig();
        reloadMiningConfig();

        getCommand("hsprogression").setExecutor(new HSProgressionCommand(this, this.API));
        getCommand("hsprogression").setTabCompleter(new HSProgressionTabComplete());

        SuperiorSkyblockAPI.registerCommand(new IsMiningCommand(this.API));
        SuperiorSkyblockAPI.registerCommand(new IsSlayerCommand(this.API));
        SuperiorSkyblockAPI.registerCommand(new IsFarmingCommand(this.API));

        Bukkit.getPluginManager().registerEvents(new GUIEventHandlers(this.API), this);
        Bukkit.getPluginManager().registerEvents(new IslandCreateHandler(this.API), this);
        Bukkit.getPluginManager().registerEvents(new EntityDeathHandler(this.API), this);
        Bukkit.getPluginManager().registerEvents(new SpawnerPlaceHandler(this.API), this);
        Bukkit.getPluginManager().registerEvents(new IslandNodePlaceHandler(this.API), this);
        Bukkit.getPluginManager().registerEvents(new IslandNodeMineHandler(this.API), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceHandler(this.API), this);
        Bukkit.getPluginManager().registerEvents(new BlockGrowHandler(this.API), this);
        Bukkit.getPluginManager().registerEvents(new BlockSpreadHandler(this.API), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractHandler(this.API), this);
        Bukkit.getPluginManager().registerEvents(new IslandProgressionHandlers(this.API), this);
    }

    @Override
    public void onDisable() {

    }

    public FileConfiguration getIslands() {
        return this.ISLANDS;
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

    public void reloadFarmingConfig() {
        File dir = getDataFolder();
        File file = new File(dir, "farming.yml");

        if (!dir.exists()) dir.mkdir();

        try {
            if (!file.exists()) {
                file.createNewFile();

                try (Reader stream = new InputStreamReader(this.getResource("farming.yml"), StandardCharsets.UTF_8)) {
                    if (stream != null) {
                        this.FARMING_FILE = file;
                        this.FARMING_CONFIG = YamlConfiguration.loadConfiguration(stream);
                        this.saveFarmingConfig();

                        return;
                    }
                }
            }
        } catch (IOException ex) {
            this.getLogger().severe(Arrays.toString(ex.getStackTrace()));
        }

        this.FARMING_FILE = file;
        this.FARMING_CONFIG = YamlConfiguration.loadConfiguration(file);
    }

    public void reloadSlayerConfig() {
        File dir = getDataFolder();
        File file = new File(dir, "slayer.yml");

        if (!dir.exists()) dir.mkdir();

        try {
            if (!file.exists()) {
                file.createNewFile();

                try (Reader stream = new InputStreamReader(this.getResource("slayer.yml"), StandardCharsets.UTF_8)) {
                    if (stream != null) {
                        this.SLAYER_FILE = file;
                        this.SLAYER_CONFIG = YamlConfiguration.loadConfiguration(stream);
                        this.saveSlayerConfig();

                        return;
                    }
                }
            }
        } catch (IOException ex) {
            this.getLogger().severe(Arrays.toString(ex.getStackTrace()));
        }

        this.SLAYER_FILE = file;
        this.SLAYER_CONFIG = YamlConfiguration.loadConfiguration(file);
    }

    public void reloadMiningConfig() {
        File dir = getDataFolder();
        File file = new File(dir, "mining.yml");

        if (!dir.exists()) dir.mkdir();

        try {
            if (!file.exists()) {
                file.createNewFile();

                try (Reader stream = new InputStreamReader(this.getResource("mining.yml"), StandardCharsets.UTF_8)) {
                    if (stream != null) {
                        this.MINING_FILE = file;
                        this.MINING_CONFIG = YamlConfiguration.loadConfiguration(stream);
                        this.saveMiningConfig();

                        return;
                    }
                }
            }
        } catch (IOException ex) {
            this.getLogger().severe(Arrays.toString(ex.getStackTrace()));
        }

        this.MINING_FILE = file;
        this.MINING_CONFIG = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getSlayerConfig() {
        return this.SLAYER_CONFIG;
    }

    public void saveSlayerConfig() {
        try {
            this.SLAYER_CONFIG.save(this.SLAYER_FILE);
        } catch (IOException ex) {
            this.getLogger().severe(Arrays.toString(ex.getStackTrace()));
        }
    }

    public FileConfiguration getFarmingConfig() {
        return this.FARMING_CONFIG;
    }

    public void saveFarmingConfig() {
        try {
            this.FARMING_CONFIG.save(this.FARMING_FILE);
        } catch (IOException ex) {
            this.getLogger().severe(Arrays.toString(ex.getStackTrace()));
        }
    }

    public FileConfiguration getMiningConfig() {
        return this.MINING_CONFIG;
    }

    public void saveMiningConfig() {
        try {
            this.MINING_CONFIG.save(this.MINING_FILE);
        } catch (IOException ex) {
            this.getLogger().severe(Arrays.toString(ex.getStackTrace()));
        }
    }
}
