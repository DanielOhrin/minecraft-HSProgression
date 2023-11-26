package net.highskiesmc.hsprogression.api;

import net.highskiesmc.hsprogression.HSProgression;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class HSProgressionApi {
    private final HSProgression main;
    private List<IslandLevel> islandLevels;
    private Map<Integer, List<IslandBlock>> islandBlocks;
    private final Database db;

    public HSProgressionApi(@NonNull HSProgression main, @NonNull ConfigurationSection dbConfig) throws SQLException, IOException {
        this.main = main;
        db = new Database(dbConfig);

        // Populate with data
        this.islandLevels = db.getIslandLevels();
        this.islandBlocks = db.getIslandBlocks();
    }

    public void dispose() {
        db.disconnect();
    }
}
