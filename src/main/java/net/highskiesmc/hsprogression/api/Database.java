package net.highskiesmc.hsprogression.api;

import net.highskiesmc.hscore.data.MySQLDatabase;
import net.highskiesmc.hscore.exceptions.Exception;
import net.highskiesmc.hscore.utils.TextUtils;
import net.highskiesmc.hscore.utils.item.ItemUtils;
import net.highskiesmc.hsprogression.HSProgression;
import org.bukkit.Material;
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
            drops.addBatch("DROP TABLE IF EXISTS island_farming;");
            drops.addBatch("DROP TABLE IF EXISTS island_mining;");
            drops.addBatch("DROP TABLE IF EXISTS island_fishing;");
            drops.addBatch("DROP PROCEDURE IF EXISTS upsert_contribution;");

            ddl.addBatch("CREATE TABLE IF NOT EXISTS island (" +
                    "Id INT AUTO_INCREMENT, " +
                    "Leader_UUID VARCHAR(36) NOT NULL, " +
                    "Island_UUID VARCHAR(36) NOT NULL UNIQUE, " +
                    "Level INT NOT NULL DEFAULT 1, " +
                    "Slayer_Level INT NOT NULL DEFAULT 1, " +
                    "Farming_Level INT NOT NULL DEFAULT 1, " +
                    "Mining_Level INT NOT NULL DEFAULT 1, " +
                    "Fishing_Level INT NOT NULL DEFAULT 1, " +
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

            ddl.addBatch("CREATE TABLE island_farming (" +
                    "Id INT AUTO_INCREMENT, " +
                    "Crop VARCHAR(50) NOT NULL UNIQUE, " +
                    "Seed VARCHAR(50) NOT NULL UNIQUE, " +
                    "Previous_Required INT NOT NULL, " +
                    "PRIMARY KEY(Id)" +
                    ") ENGINE = INNODB;"
            );

            ddl.addBatch("CREATE TABLE island_mining (" +
                    "Id INT AUTO_INCREMENT, " +
                    "Node_Id VARCHAR(50) NOT NULL UNIQUE, " +
                    "Material VARCHAR(50) NOT NULL UNIQUE, " +
                    "Previous_Required INT NOT NULL, " +
                    "PRIMARY KEY(Id)" +
                    ") ENGINE = INNODB;");

            ddl.addBatch("CREATE TABLE island_fishing (" +
                    "Id INT AUTO_INCREMENT, " +
                    "Label VARCHAR(50) NOT NULL UNIQUE, " +
                    "Item_Id VARCHAR(50) NOT NULL UNIQUE, " +
                    "Experience INT NOT NULL, " +
                    "Previous_Required INT NOT NULL, " +
                    "PRIMARY KEY(Id)" +
                    ") ENGINE = INNODB;"
            );

            // TODO: Fix Id increment bug
            ddl.addBatch("CREATE TABLE IF NOT EXISTS island_contributor (" +
                    "Id INT AUTO_INCREMENT, " +
                    "Player_UUID VARCHAR(36) NOT NULL, " +
                    "Island_Id INT, " +
                    "PRIMARY KEY(Id), " +
                    "FOREIGN KEY(Island_Id) REFERENCES island(Id)" +
                    ") ENGINE = INNODB;"
            );

            ddl.addBatch("CREATE TABLE IF NOT EXISTS island_contribution (" +
                    "Id INT AUTO_INCREMENT, " +
                    "Contributor_Id INT, " +
                    "Label VARCHAR(50) NOT NULL, " +
                    "Amount INT NOT NULL, " +
                    "DataType VARCHAR(50) NOT NULL, " +
                    "Date_Time DATETIME NOT NULL, " +
                    "PRIMARY KEY(Id), " +
                    "FOREIGN KEY(Contributor_Id) REFERENCES island_contributor(Id)" +
                    ") ENGINE = INNODB;"
            );

            ddl.addBatch(
                    " CREATE PROCEDURE IF NOT EXISTS upsert_contribution(   " +
                            "                                                            player_uid VARCHAR(36),   " +
                            "                                                            island_uid VARCHAR(36),   " +
                            "                                                            label VARCHAR(50),   " +
                            "                                                            amount INT,   " +
                            "                                                            datatype VARCHAR(50), " +
                            "                                                            date_time DATETIME   " +
                            "                                                        )   " +
                            "                                                         BEGIN   " +
                            "                                                            DECLARE contributor_id INT; " +
                            "  " +
                            "                                                            DECLARE is_id INT;   " +
                            "                                                             " + // TODO: Check Datatype
                            // TODO: for valid string
                            "                                                            SELECT Id INTO is_id FROM " +
                            "island WHERE Island_UUID = island_uid LIMIT 1;   " +
                            "                                                            SELECT Id INTO " +
                            "contributor_id FROM island_contributor WHERE Player_UUID = player_uid AND Island_Id = " +
                            "is_id LIMIT 1; " +
                            "                                                                                      " +
                            "                                                            IF contributor_id IS NULL " +
                            "THEN   " +
                            "                                                            INSERT INTO " +
                            "island_contributor (Player_UUID, Island_Id) VALUES (player_uid, is_id);   " +
                            "                                                            SET contributor_id = " +
                            "LAST_INSERT_ID();" +
                            "                                                            END IF;   " +
                            "                                                              " +
                            "                                                            INSERT INTO " +
                            "island_contribution(Contributor_Id, Label, Amount, DataType, Date_Time)   " +
                            "                                                            VALUES (contributor_id, " +
                            "label, amount, datatype, date_time);   " +
                            "                                                         END;"
            );
            // Try Running this line if plugin startup time becomes high
            // You will want to add a guard clause of some sort.
            // ddl.addBatch("CREATE INDEX idx_islands_active ON island(Is_Deleted);");
            // END
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
                    "Farming_Level, Mining_Level, Fishing_Level, Is_Deleted FROM island WHERE Is_Deleted = 0;");

            while (islands.next()) {
                UUID islandUuid = UUID.fromString(islands.getString("Island_UUID"));
                result.put(islandUuid, new Island(
                                islands.getInt("Id"),
                                UUID.fromString(islands.getString("Leader_UUID")),
                                islandUuid,
                                islands.getInt("Level"),
                                islands.getInt("Slayer_Level"),
                                islands.getInt("Farming_Level"),
                                islands.getInt("Mining_Level"),
                                islands.getInt("Fishing_Level"),
                                islands.getBoolean("Is_Deleted")
                        )
                );
            }

            ResultSet slayerNums = statement.executeQuery("SELECT i.Island_UUID, icn.Label, SUM(icn.Amount) AS Amount" +
                    " " +
                    "FROM island_contribution icn " +
                    "INNER JOIN island_contributor icr ON icr.Id = icn.Contributor_Id " +
                    "INNER JOIN island i ON i.Id = icr.Island_Id " +
                    "WHERE i.Is_Deleted = 0 AND icn.DataType = 'SLAYER' " +
                    "GROUP BY Island_UUID, Label;"
            );

            while (slayerNums.next()) {
                Island island = result.getOrDefault(UUID.fromString(slayerNums.getString("Island_UUID")), null);

                if (island == null) {
                    throw new SQLException("Island mismatch found: " + slayerNums.getString("Island_UUID"));
                }

                island.setSlayerNum(EntityType.valueOf(slayerNums.getString("Label")), slayerNums.getInt("Amount"));
            }

            ResultSet farmingNums = statement.executeQuery("SELECT i.Island_UUID, icn.Label, SUM(icn.Amount) AS " +
                    "Amount " +
                    "FROM island_contribution icn " +
                    "INNER JOIN island_contributor icr ON icr.Id = icn.Contributor_Id " +
                    "INNER JOIN island i ON i.Id = icr.Island_Id " +
                    "WHERE i.Is_Deleted = 0 AND icn.DataType = 'FARMING' AND icr.Player_UUID = 'Unknown'" +
                    "GROUP BY Island_UUID, Label;"
            );

            while (farmingNums.next()) {
                Island island = result.getOrDefault(UUID.fromString(farmingNums.getString("Island_UUID")), null);

                if (island == null) {
                    throw new SQLException("Island mismatch found: " + farmingNums.getString("Island_UUID"));
                }

                island.setFarmingNum(Material.valueOf(farmingNums.getString("Label")), farmingNums.getInt("Amount"));
            }

            ResultSet miningNums = statement.executeQuery("SELECT i.Island_UUID, icn.Label, SUM(icn.Amount) AS " +
                    "Amount " +
                    "FROM island_contribution icn " +
                    "INNER JOIN island_contributor icr ON icr.Id = icn.Contributor_Id " +
                    "INNER JOIN island i ON i.Id = icr.Island_Id " +
                    "WHERE i.Is_Deleted = 0 AND icn.DataType = 'MINING' " +
                    "GROUP BY Island_UUID, Label;"
            );

            while (miningNums.next()) {
                Island island = result.getOrDefault(UUID.fromString(miningNums.getString("Island_UUID")), null);

                if (island == null) {
                    throw new SQLException("Island mismatch found: " + miningNums.getString("Island_UUID"));
                }

                island.setMiningNum(miningNums.getString("Label"), miningNums.getInt("Amount"));
            }

            ResultSet fishingNums = statement.executeQuery("SELECT i.Island_UUID, icn.Label, SUM(icn.Amount) AS " +
                    "Amount " +
                    "FROM island_contribution icn " +
                    "INNER JOIN island_contributor icr ON icr.Id = icn.Contributor_Id " +
                    "INNER JOIN island i ON i.Id = icr.Island_Id " +
                    "WHERE i.Is_Deleted = 0 AND icn.DataType = 'FISHING' " +
                    "GROUP BY Island_UUID, Label;"
            );

            while (fishingNums.next()) {
                Island island = result.getOrDefault(UUID.fromString(fishingNums.getString("Island_UUID")), null);

                if (island == null) {
                    throw new SQLException("Island mismatch found: " + fishingNums.getString("Island_UUID"));
                }

                island.setFishingNum(fishingNums.getString("Label"), fishingNums.getInt("Amount"));
            }
        }
        // TODO: If contributor is NULL for FARMING contribution, it is just a crop grown
        // TODO: Otherwise its a player breaking for xp/contribution tracking
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
                    "INSERT INTO island (Leader_UUID, Island_UUID, Level, Slayer_Level, Farming_Level, Mining_Level, " +
                            "Is_Deleted) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE Leader_UUID = ?, Level = ?, " +
                            "Slayer_Level = ?, Farming_Level = ?, Mining_Level = ?, Is_Deleted = ?;"
            );

            for (Island island : islands) {
                upsert.setString(1, island.getLeaderUuid().toString());
                upsert.setString(2, island.getIslandUuid().toString());
                upsert.setInt(3, island.getLevel(IslandProgressionType.ISLAND));
                upsert.setInt(4, island.getLevel(IslandProgressionType.SLAYER));
                upsert.setInt(5, island.getLevel(IslandProgressionType.FARMING));
                upsert.setInt(6, island.getLevel(IslandProgressionType.MINING));
                upsert.setBoolean(7, island.isDeleted());

                upsert.setString(8, island.getLeaderUuid().toString());
                upsert.setInt(9, island.getLevel(IslandProgressionType.ISLAND));
                upsert.setInt(10, island.getLevel(IslandProgressionType.SLAYER));
                upsert.setInt(11, island.getLevel(IslandProgressionType.FARMING));
                upsert.setInt(12, island.getLevel(IslandProgressionType.MINING));
                upsert.setBoolean(13, island.isDeleted());

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
            PreparedStatement upsert = conn.prepareStatement("CALL upsert_contribution(?, ?, ?, ?, ?, ?);");

            for (IslandContributor contributor : contributions.values()) {
                // SLAYER
                for (SlayerContribution slayerContribution : contributor.getSlayerContributions().values()) {
                    for (Map.Entry<EntityType, Integer> contribution :
                            slayerContribution.getContributions().entrySet()) {

                        String contributorUuid = contributor.getPlayerUuid() == null ? "Unknown" :
                                contributor.getPlayerUuid().toString();
                        upsert.setString(1, contributorUuid);
                        upsert.setString(2, slayerContribution.getIslandUuid().toString());
                        upsert.setString(3, contribution.getKey().toString());
                        upsert.setInt(4, contribution.getValue());
                        upsert.setString(5, IslandProgressionType.SLAYER.name());
                        upsert.setTimestamp(6, Timestamp.valueOf(dateTime.toLocalDateTime()));

                        upsert.addBatch();
                    }
                }

                // FARMING
                for (FarmingContribution farmingContribution : contributor.getFarmingContributions().values()) {
                    for (Map.Entry<Material, Integer> contribution :
                            farmingContribution.getContributions().entrySet()) {

                        String contributorUuid = contributor.getPlayerUuid() == null ? "Unknown" :
                                contributor.getPlayerUuid().toString();
                        upsert.setString(1, contributorUuid);
                        upsert.setString(2, farmingContribution.getIslandUuid().toString());
                        upsert.setString(3, contribution.getKey().toString());
                        upsert.setInt(4, contribution.getValue());
                        upsert.setString(5, IslandProgressionType.FARMING.name());
                        upsert.setTimestamp(6, Timestamp.valueOf(dateTime.toLocalDateTime()));

                        upsert.addBatch();
                    }
                }

                // MINING
                for (MiningContribution miningContribution : contributor.getMiningContributions().values()) {
                    for (Map.Entry<String, Integer> contribution :
                            miningContribution.getContributions().entrySet()) {

                        String contributorUuid = contributor.getPlayerUuid() == null ? "Unknown" :
                                contributor.getPlayerUuid().toString();
                        upsert.setString(1, contributorUuid);
                        upsert.setString(2, miningContribution.getIslandUuid().toString());
                        upsert.setString(3, contribution.getKey());
                        upsert.setInt(4, contribution.getValue());
                        upsert.setString(5, IslandProgressionType.MINING.name());
                        upsert.setTimestamp(6, Timestamp.valueOf(dateTime.toLocalDateTime()));

                        upsert.addBatch();
                    }
                }

                // FISHING
                for (FishingContribution fishingContribution : contributor.getFishingContributions().values()) {
                    for (Map.Entry<String, Integer> contribution :
                            fishingContribution.getContributions().entrySet()) {

                        String contributorUuid = contributor.getPlayerUuid() == null ? "Unknown" :
                                contributor.getPlayerUuid().toString();
                        upsert.setString(1, contributorUuid);
                        upsert.setString(2, fishingContribution.getIslandUuid().toString());
                        upsert.setString(3, contribution.getKey());
                        upsert.setInt(4, contribution.getValue());
                        upsert.setString(5, IslandProgressionType.FISHING.name());
                        upsert.setTimestamp(6, Timestamp.valueOf(dateTime.toLocalDateTime()));

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

    public List<FarmingLevel> getFarmingLevels() throws SQLException {
        List<FarmingLevel> result = new ArrayList<>();

        try (Connection conn = getHikari().getConnection()) {
            Statement statement = conn.createStatement();

            ResultSet levels = statement.executeQuery("SELECT Id, Crop, Seed, Previous_Required FROM island_farming;");

            Material previous = null;
            while (levels.next()) {
                result.add(new FarmingLevel(
                        levels.getInt("Id"),
                        Material.valueOf(levels.getString("Crop")),
                        Material.valueOf(levels.getString("Seed")),
                        previous,
                        levels.getLong("Previous_Required")
                ));

                previous = Material.valueOf(levels.getString("Crop"));
            }
        }

        return result;
    }

    public List<MiningLevel> getMiningLevels() throws SQLException {
        List<MiningLevel> result = new ArrayList<>();

        try (Connection conn = getHikari().getConnection()) {
            Statement statement = conn.createStatement();

            ResultSet levels = statement.executeQuery("SELECT Id, Node_Id, Material, Previous_Required FROM " +
                    "island_mining;");

            String previous = null;
            while (levels.next()) {
                result.add(new MiningLevel(
                        levels.getInt("Id"),
                        Material.valueOf(levels.getString("Material")),
                        levels.getString("Node_Id"),
                        previous,
                        levels.getLong("Previous_Required")
                ));

                previous = levels.getString("Node_Id");
            }
        }

        return result;
    }

    public List<FishingLevel> getFishingLevels() throws SQLException {
        List<FishingLevel> result = new ArrayList<>();

        try (Connection conn = getHikari().getConnection()) {
            Statement statement = conn.createStatement();

            ResultSet levels = statement.executeQuery("SELECT Id, Label, Item_Id, Experience, Previous_Required FROM " +
                    "island_fishing;");
            // IMPORTANT: Fishing requires you to pass the Item_Id AND the Label, because the Item_Id is so much
            // different
            String previousId = null;
            String previousLabel = null;
            while (levels.next()) {
                result.add(new FishingLevel(
                        levels.getInt("Id"),
                        previousId,
                        previousLabel,
                        levels.getString("Label"),
                        levels.getString("Item_Id"),
                        levels.getDouble("Experience"),
                        levels.getLong("Previous_Required")
                ));

                previousId = levels.getString("Item_Id");
                previousLabel = levels.getString("Label");
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
