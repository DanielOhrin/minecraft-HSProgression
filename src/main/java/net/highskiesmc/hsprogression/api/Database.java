package net.highskiesmc.hsprogression.api;

import net.highskiesmc.hscore.data.MySQLDatabase;
import net.highskiesmc.hscore.exceptions.Exception;
import net.highskiesmc.hscore.utils.TextUtils;
import net.highskiesmc.hscore.utils.item.ItemUtils;
import net.highskiesmc.hsprogression.HSProgression;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.sql.*;
import java.util.*;

class Database extends MySQLDatabase {
    Database(@NonNull ConfigurationSection DB_CONFIG) throws SQLException {
        super(DB_CONFIG);
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

            ddl.addBatch("CREATE TABLE IF NOT EXISTS island (" +
                    "Id INT AUTO_INCREMENT, " +
                    "Leader_UUID VARCHAR(36) NOT NULL, " +
                    "Island_UUID VARCHAR(36) NOT NULL UNIQUE, " +
                    "Level INT NOT NULL DEFAULT 1, " +
                    "Is_Deleted BIT(1) NOT NULL DEFAULT 0, " +
                    "PRIMARY KEY(Id)" +
                    ") ENGINE = INNODB;");

            ddl.addBatch("CREATE TABLE island_level (" +
                    "Id INT AUTO_INCREMENT, " +
                    "Spawner_Limit INT NOT NULL, " +
                    "Member_Limit TINYINT(30) UNSIGNED, " +
                    "Island_Radius TINYINT UNSIGNED, " +
                    "Cost BIGINT UNSIGNED, " +
                    "Is_Announced BIT(1) NOT NULL DEFAULT 0, " +
                    "PRIMARY KEY(Id)" +
                    ") ENGINE = INNODB;");

            ddl.addBatch("CREATE TABLE island_level_block (" +
                    "Id INT AUTO_INCREMENT, " +
                    "Label VARCHAR(50) UNIQUE, " +
                    "Island_Level INT NOT NULL, " +
                    "Encoding VARCHAR(16000), " +
                    "PRIMARY KEY(Id), " +
                    "FOREIGN KEY(Island_Level) REFERENCES island_level(Id)" +
                    ") ENGINE = INNODB;");

            // Try Running this line if plugin startup time becomes high
            // You will want to add a guard clause of some sort.
            // ddl.addBatch("CREATE INDEX idx_islands_active ON island(Is_Deleted);");
            // END

            drops.executeBatch();
            ddl.executeBatch();
        }

        try {
            insertConfigurationTables();
        } catch (IOException ex) {
            throw new SQLException(ex);
        }
    }

    /**
     * Inserts config values from resource `config.xml` into the database
     */
    protected void insertConfigurationTables() throws IOException, SQLException {
        try (Connection conn = getHikari().getConnection()) {
            Queue<PreparedStatement> statements = new LinkedList<>();
            // TODO: Extract to HSCore
            try (InputStream stream = HSProgression.class.getResourceAsStream("/config.xml")) {
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document doc = builder.parse(stream);
                doc.getDocumentElement().normalize();

                NodeList tables = doc.getElementsByTagName("table"); // We know these are all Element Nodes
                for (int i = 0; i < tables.getLength(); i++) {
                    Node table = tables.item(i);

                    // Process table
                    NamedNodeMap attributes = table.getAttributes();
                    String tableName = attributes.getNamedItem("name").getNodeValue();

                    // CREATE PREPARED STATEMENT
                    StringBuilder sb = new StringBuilder("INSERT INTO ");
                    sb.append(tableName);
                    sb.append(" (");

                    // Get First Row
                    NodeList rows = table.getChildNodes();
                    Node row = null;
                    for (int j = 0; j < rows.getLength(); j++) {
                        row = rows.item(j);

                        if (row.getNodeType() == Node.ELEMENT_NODE) {
                            break;
                        }
                    }

                    // Process Columns in row
                    NodeList columns = row.getChildNodes();
                    int values = 0;
                    for (int j = 0; j < columns.getLength(); j++) {
                        Node column = columns.item(j);

                        if (column.getNodeType() == Node.ELEMENT_NODE) {
                            sb.append(column.getNodeName());
                            sb.append(", ");
                            values++;
                        }
                    }
                    sb.setCharAt(sb.lastIndexOf(","), ')');
                    sb.append("VALUES (");
                    sb.append("?, ".repeat(values - 1));
                    sb.append("?);");

                    PreparedStatement batch = conn.prepareStatement(sb.toString());
                    // END OF PREPARED STATEMENT

                    // BEGIN BATCH INSERT
                    for (int j = 0; j < rows.getLength(); j++) {
                        int missingValues = values;
                        row = rows.item(j);
                        if (row.getNodeType() == Node.ELEMENT_NODE) {
                            columns = row.getChildNodes();
                            for (int k = 0; k < columns.getLength() && missingValues > 0; k++) {
                                Node column = columns.item(k);

                                if (column.getNodeType() == Node.ELEMENT_NODE) {
                                    String value = column.getTextContent();
                                    switch (value) {
                                        case "False","false" -> batch.setBoolean(values - missingValues + 1, false);
                                        case "True","true" -> batch.setBoolean(values - missingValues + 1, true);
                                        default -> batch.setObject(values - missingValues + 1, value);
                                    }

                                    missingValues--;
                                }
                            }
                        } else {
                            continue;
                        }

                        batch.addBatch();
                    }
                    // END BATCH INSERT

                    statements.add(batch);
                }
            } catch (ParserConfigurationException | SAXException ex) {
                throw new IOException();
            }

            while (statements.peek() != null) {
                PreparedStatement statement = statements.poll();
                statement.executeBatch();
            }
        }
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

            ResultSet island = statement.executeQuery("SELECT Id, Leader_UUID, Island_UUID, Level, Is_Deleted FROM " +
                    "island WHERE Is_Deleted = 0;");

            while (island.next()) {
                UUID islandUuid = UUID.fromString(island.getString("Island_UUID"));
                result.put(islandUuid, new Island(
                                island.getInt("Id"),
                                UUID.fromString(island.getString("Leader_UUID")),
                                islandUuid,
                                island.getInt("Level"),
                                island.getBoolean("Is_Deleted")
                        )
                );
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
        }

            plugin.getLogger().info("Updating islands...");
            // Upsert, Then Delete!
            try (Connection conn = getHikari().getConnection()) {
                PreparedStatement upsert = conn.prepareStatement(
                        "INSERT INTO island (Leader_UUID, Island_UUID, Level, Is_Deleted) VALUES (?, ?, ?, ?)" +
                                "ON DUPLICATE KEY UPDATE Leader_UUID = ?, Level = ?, Is_Deleted = ?"
                );

                for (Island island : islands) {
                    upsert.setString(1, island.getLeaderUuid().toString());
                    upsert.setString(2, island.getIslandUuid().toString());
                    upsert.setInt(3, island.getLevel());
                    upsert.setBoolean(4, island.isDeleted());

                    upsert.setString(5, island.getLeaderUuid().toString());
                    upsert.setInt(6, island.getLevel());
                    upsert.setBoolean(7, island.isDeleted());

                    upsert.addBatch();
                }

                int[] islandsUpdated = upsert.executeBatch();

                plugin.getLogger().info("Done! Islands updated: " + Arrays.stream(islandsUpdated).sum());
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

            ResultSet levels = statement.executeQuery("SELECT Id, Spawner_Limit, Member_Limit, Island_Radius, Cost, " +
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
