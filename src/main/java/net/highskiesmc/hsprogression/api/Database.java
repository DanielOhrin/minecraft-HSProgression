package net.highskiesmc.hsprogression.api;

import net.highskiesmc.hscore.data.MySQLDatabase;
import net.highskiesmc.hscore.exceptions.Exception;
import net.highskiesmc.hscore.utils.TextUtils;
import net.highskiesmc.hscore.utils.item.ItemUtils;
import net.highskiesmc.hsprogression.HSProgression;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.*;
import java.sql.*;
import java.time.ZonedDateTime;
import java.util.*;

class Database extends MySQLDatabase {
    Database(HSProgression main, @NonNull ConfigurationSection DB_CONFIG) throws SQLException {
        super(main, DB_CONFIG);
        this.getHikari().getConnection();
    }

    @Override
    protected int getMaxPoolSize() {
        return 2;
    }

    @Override
    protected int getMinIdlePools() {
        return 2;
    }

    @Override
    protected int getMaxLifetime() {
        return 300000;
    }

    @Override
    protected void tryCreateTables() throws SQLException {
        try (Connection conn = this.getHikari().getConnection()) {
            Statement drops = conn.createStatement();
            Statement ddl = conn.createStatement();

            // Note: While dropping a table is DDL, it is good to have it separate from the batch that recreates them.
            drops.addBatch("DROP TABLE IF EXISTS island_level_block;");
            drops.addBatch("DROP TABLE IF EXISTS island_level;");
            drops.addBatch("DROP TABLE IF EXISTS island_slayer;");
            drops.addBatch("DROP PROCEDURE IF EXISTS upsert_contribution;");

            ddl.addBatch("CREATE TABLE IF NOT EXISTS island (" +
                    "Id INT AUTO_INCREMENT, " +
                    "Leader_UUID VARCHAR(36) NOT NULL, " +
                    "Island_UUID VARCHAR(36) NOT NULL UNIQUE, " +
                    "Level INT NOT NULL DEFAULT 1, " +
                    "Slayer_Level INT NOT NULL DEFAULT 1, " +
                    "Is_Deleted BIT(1) NOT NULL DEFAULT 0, " +
                    "PRIMARY KEY(Id)" +
                    ") ENGINE = INNODB;"
            );

            ddl.addBatch("CREATE TABLE island_level (" +
                    "Id INT AUTO_INCREMENT, " +
                    "Spawner_Limit INT NOT NULL, " +
                    "Member_Limit TINYINT(30) UNSIGNED, " +
                    "Island_Radius TINYINT UNSIGNED, " +
                    "Cost BIGINT UNSIGNED, " +
                    "Is_Announced BIT(1) NOT NULL DEFAULT 0, " +
                    "PRIMARY KEY(Id)" +
                    ") ENGINE = INNODB;"
            );

            ddl.addBatch("CREATE TABLE island_level_block (" +
                    "Id INT AUTO_INCREMENT, " +
                    "Label VARCHAR(50), " +
                    "Island_Level INT NOT NULL, " +
                    "Encoding VARCHAR(16000) UNIQUE, " +
                    "PRIMARY KEY(Id), " +
                    "FOREIGN KEY(Island_Level) REFERENCES island_level(Id)" +
                    ") ENGINE = INNODB;"
            );

            ddl.addBatch("CREATE TABLE island_slayer (" +
                    "Id INT AUTO_INCREMENT, " +
                    "Entity VARCHAR(50) UNIQUE, " +
                    "Previous_Required INT NOT NULL, " +
                    "Head_Id INT NOT NULL, " +
                    "PRIMARY KEY(Id)" +
                    ") ENGINE = INNODB;"
            );

            ddl.addBatch("CREATE TABLE IF NOT EXISTS island_contributor (" +
                    "Id INT AUTO_INCREMENT, " +
                    "Player_UUID VARCHAR(36) NOT NULL, " +
                    "Island_Id INT, " +
                    "PRIMARY KEY(Id), " +
                    "FOREIGN KEY(Island_Id) REFERENCES island(Id)" +
                    ") ENGINE = INNODB;"
            );

            ddl.addBatch("CREATE TABLE IF NOT EXISTS slayer_contribution (" +
                    "Id INT AUTO_INCREMENT, " +
                    "Contributor_Id INT, " +
                    "Entity VARCHAR(50), " +
                    "Amount INT, " +
                    "Date_Time DATETIME, " +
                    "PRIMARY KEY(Id), " +
                    "FOREIGN KEY(Contributor_Id) REFERENCES island_contributor(Id)" +
                    ") ENGINE = INNODB;"
            );

            ddl.addBatch(
                    " CREATE PROCEDURE IF NOT EXISTS upsert_contribution(   " +
                            "                                                            player_uid VARCHAR(36),   " +
                            "                                                            island_uid VARCHAR(36),   " +
                            "                                                            entity VARCHAR(50),   " +
                            "                                                            amount INT,   " +
                            "                                                            date_time DATETIME   " +
                            "                                                        )   " +
                            "                                                         BEGIN   " +
                            "                                                            DECLARE contributor_id INT;   " +
                            "                                                            DECLARE is_id INT;   " +
                            "                                                             " +
                            "                                                            SELECT Id INTO is_id FROM island WHERE Island_UUID = island_uid LIMIT 1;   " +
                            "                                                            SELECT Id INTO contributor_id FROM island_contributor WHERE Player_UUID = player_uid AND Island_Id = is_id LIMIT 1; " +
                            "                                                                                      " +
                            "                                                            IF contributor_id IS NULL THEN   " +
                            "                                                            INSERT INTO island_contributor (Player_UUID, Island_Id) VALUES (player_uid, is_id);   " +
                            "                                                            SET contributor_id = LAST_INSERT_ID();" +
                            "                                                            END IF;   " +
                            "                                                              " +
                            "                                                            INSERT INTO slayer_contribution(Contributor_Id, Entity, Amount, Date_Time)   " +
                            "                                                            VALUES (contributor_id, entity, amount, date_time);   " +
                            "                                                         END;"
            );
            // TODO: Pull amount of mobs slain on each island on startup
            // TODO: Handle updating island slayer level in the plugin itself when the island has reached enough mobs
            //  slain
            // Try Running this line if plugin startup time becomes high
            // You will want to add a guard clause of some sort.
            // ddl.addBatch("CREATE INDEX idx_islands_active ON island(Is_Deleted);");
            // END
            // TODO: Store only the current batch, then delete it and just have it as a number on the Island?
            // TODO: Then track the next 5 mins, etc.
            // TODO: Leaderboards can be locked behind
            drops.executeBatch();
            ddl.executeBatch();
        }
// TODO: handle null slayer on slayer kill mob event thing
    }

    @Override
    protected boolean useConfigXml() {
        return true;
    }

    //<editor-fold desc="Islands">
    //<editor-fold desc="Island">

    /**
     * @return HashMap of Islands by their UUID
     * @throws SQLException
     */
    public Map<UUID, Island> getIslands() throws SQLException {
        Map<UUID, Island> result = new HashMap<>();

        try (Connection conn = getHikari().getConnection()) {
            Statement statement = conn.createStatement();

            ResultSet islands = statement.executeQuery("SELECT Id, Leader_UUID, Island_UUID, Level, Slayer_Level, " +
                    "Is_Deleted FROM " +
                    "island WHERE Is_Deleted = 0;");

            while (islands.next()) {
                UUID islandUuid = UUID.fromString(islands.getString("Island_UUID"));
                result.put(islandUuid, new Island(
                                islands.getInt("Id"),
                                UUID.fromString(islands.getString("Leader_UUID")),
                                islandUuid,
                                islands.getInt("Level"),
                                islands.getInt("Slayer_Level"),
                                islands.getBoolean("Is_Deleted")
                        )
                );
            }

            ResultSet slayerNums = statement.executeQuery("SELECT i.Island_UUID, sc.Entity, SUM(sc.Amount) AS Amount " +
                    "FROM slayer_contribution sc " +
                    "INNER JOIN island_contributor ic ON ic.Id = sc.Contributor_Id " +
                    "INNER JOIN island i ON i.Id = ic.Island_Id " +
                    "WHERE i.Is_Deleted = 0 " +
                    "GROUP BY Island_UUID, Entity;"
            );

            while (slayerNums.next()) {
                Island island = result.getOrDefault(UUID.fromString(slayerNums.getString("Island_UUID")), null);

                if (island == null) {
                    throw new SQLException("Island mismatch found: " + slayerNums.getString("Island_UUID"));
                }

                island.setSlayerNum(EntityType.valueOf(slayerNums.getString("Entity")), slayerNums.getInt("Amount"));
            }
        }

        return result;
    }

    /**
     * @param plugin  Plugin calling this method
     * @param islands List of islands to upsert into the database
     */
    public void upsertIslands(@NonNull Plugin plugin, @NonNull List<Island> islands) {
        if (islands.isEmpty()) {
            plugin.getLogger().info("No island to update. Skipped batch upsert...");
            return;
        }

        plugin.getLogger().info("Updating islands...");
        // Upsert, Then Delete!
        try (Connection conn = getHikari().getConnection()) {
            PreparedStatement upsert = conn.prepareStatement(
                    "INSERT INTO island (Leader_UUID, Island_UUID, Level, Slayer_Level, Is_Deleted) VALUES (?, ?, ?, " +
                            "?, ?) ON DUPLICATE KEY UPDATE Leader_UUID = ?, Level = ?, Slayer_Level = ?, Is_Deleted =" +
                            " ?;"
            );

            for (Island island : islands) {
                upsert.setString(1, island.getLeaderUuid().toString());
                upsert.setString(2, island.getIslandUuid().toString());
                upsert.setInt(3, island.getLevel(IslandProgressionType.ISLAND));
                upsert.setInt(4, island.getLevel(IslandProgressionType.SLAYER));
                upsert.setBoolean(5, island.isDeleted());

                upsert.setString(6, island.getLeaderUuid().toString());
                upsert.setInt(7, island.getLevel(IslandProgressionType.ISLAND));
                upsert.setInt(8, island.getLevel(IslandProgressionType.SLAYER));
                upsert.setBoolean(9, island.isDeleted());

                upsert.addBatch();
            }

            int[] islandsUpdated = upsert.executeBatch();

            plugin.getLogger().info("Done! Islands updated: " + Arrays.stream(islandsUpdated).sum());
        } catch (SQLException ex) {
            Exception.useStackTrace(plugin.getLogger()::severe, ex);
        }
    }

    public void upsertContributions(@NonNull Plugin plugin, @NonNull Map<UUID, IslandContributor> contributions) {
        if (contributions.isEmpty()) {
            plugin.getLogger().info("No contributions to update. Skipped batch upsert...");
            return;
        }

        plugin.getLogger().info("Updating contributions...");

        ZonedDateTime dateTime = ZonedDateTime.now();
        // Upsert, Then Delete!
        try (Connection conn = getHikari().getConnection()) {
            PreparedStatement upsert = conn.prepareStatement("CALL upsert_contribution(?, ?, ?, ?, ?);");

            for (IslandContributor contributor : contributions.values()) {
                // SLAYER
                for (SlayerContribution slayerContribution : contributor.getSlayerContributions().values()) {
                    for (Map.Entry<EntityType, Integer> contribution :
                            slayerContribution.getContributions().entrySet()) {

                        upsert.setString(1, contributor.getPlayerUuid().toString());
                        upsert.setString(2, slayerContribution.getIslandUuid().toString());
                        upsert.setString(3, contribution.getKey().toString());
                        upsert.setInt(4, contribution.getValue());
                        System.out.printf("Player: %s\nIsland: %s\nEntity: %s\nAmount:%d%n",
                                contributor.getPlayerUuid().toString(), slayerContribution.getIslandUuid().toString()
                                , contribution.getKey().toString(), contribution.getValue());
                        upsert.setTimestamp(5, Timestamp.valueOf(dateTime.toLocalDateTime()));

                        upsert.addBatch();
                    }
                }
            }
            int[] contributionsUpdated = upsert.executeBatch();

            plugin.getLogger().info("Done! Contributions updated: " + Arrays.stream(contributionsUpdated).sum());
        } catch (SQLException ex) {
            Exception.useStackTrace(plugin.getLogger()::severe, ex);
        }
    }

    //</editor-fold>
    //<editor-fold desc="Island Levels">

    /**
     * Grabs Island Levels from the database
     */
    public List<IslandLevel> getIslandLevels() throws SQLException {
        List<IslandLevel> result = new ArrayList<>();

        try (Connection conn = getHikari().getConnection()) {
            Statement statement = conn.createStatement();

            ResultSet levels = statement.executeQuery("SELECT Id, Spawner_Limit, Member_Limit, Island_Radius, " +
                    "Cost, " +
                    "Is_Announced FROM island_level;");

            while (levels.next()) {
                result.add(new IslandLevel(
                        levels.getInt("Id"),
                        levels.getInt("Spawner_Limit"),
                        levels.getInt("Member_Limit"),
                        levels.getInt("Island_Radius"),
                        levels.getLong("Cost"),
                        levels.getBoolean("Is_Announced")
                ));
            }
        }

        return result;
    }

    public List<SlayerLevel> getSlayerLevels() throws SQLException {
        List<SlayerLevel> result = new ArrayList<>();

        try (Connection conn = getHikari().getConnection()) {
            Statement statement = conn.createStatement();

            ResultSet levels = statement.executeQuery("SELECT Id, Entity, Previous_Required, Head_Id FROM " +
                    "island_slayer;");

            EntityType previous = null;
            while (levels.next()) {
                result.add(new SlayerLevel(
                        levels.getInt("Id"),
                        EntityType.valueOf(levels.getString("Entity")),
                        previous,
                        levels.getLong("Previous_Required"),
                        levels.getInt("Head_Id")
                ));

                previous = EntityType.valueOf(levels.getString("Entity"));
            }
        }

        return result;
    }

    /**
     * Grabs config from the database
     */
    public Map<Integer, List<IslandBlock>> getIslandBlocks() throws SQLException, IOException {
        Map<Integer, List<IslandBlock>> result = new HashMap<>();

        try (Connection conn = getHikari().getConnection()) {
            Statement statement = conn.createStatement();

            ResultSet blocks = statement.executeQuery("SELECT Id, Label, Island_Level, Encoding FROM " +
                    "island_level_block;");

            while (blocks.next()) {
                int islandLevel = blocks.getInt("Island_Level");
                IslandBlock block = new IslandBlock(
                        islandLevel,
                        ItemUtils.fromBase64(blocks.getString("Encoding")),
                        TextUtils.translateColor(blocks.getString("Label"))
                );

                if (result.containsKey(islandLevel)) {
                    result.get(islandLevel).add(block);
                } else {
                    result.put(islandLevel, new ArrayList<>() {{
                        add(block);
                    }});
                }
            }
        }

        return result;
    }
    //</editor-fold>
    //</editor-fold>
}
