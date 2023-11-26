package net.highskiesmc.hsprogression.api;

import net.highskiesmc.hscore.data.MySQLDatabase;
import net.highskiesmc.hscore.utils.TextUtils;
import net.highskiesmc.hscore.utils.item.ItemUtils;
import net.highskiesmc.hsprogression.HSProgression;
import org.bukkit.configuration.ConfigurationSection;
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

            // ISLAND + ISLAND LEVELS
            // TODO: Create procedure for creating new island
            // TODO: Insert config values from the external file
            drops.addBatch("DROP TABLE IF EXISTS island_level_block;");
            drops.addBatch("DROP TABLE IF EXISTS island_level;");

            ddl.addBatch("CREATE TABLE IF NOT EXISTS island (" +
                    "Id INT AUTO_INCREMENT, " +
                    "Island_UUID VARCHAR(36) NOT NULL, " +
                    "Level INT NOT NULL DEFAULT(1), " +
                    "IsDeleted BIT(1) NOT NULL DEFAULT(0), " +
                    "PRIMARY KEY(Id)" +
                    ") ENGINE = INNODB;");

            ddl.addBatch("CREATE TABLE island_level (" +
                    "Id INT AUTO_INCREMENT, " +
                    "Spawner_Limit INT NOT NULL, " +
                    "Member_Limit TINYINT(30) UNSIGNED, " +
                    "Island_Radius TINYINT UNSIGNED, " +
                    "Cost BIGINT UNSIGNED, " +
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
     * Inserts config values from resource `config.xml`
     */
    protected void insertConfigurationTables() throws IOException, SQLException {
        // TODO: Try to get the file and read just one line for now to test!
        try (Connection conn = getHikari().getConnection()) {
            Queue<PreparedStatement> statements = new LinkedList<>();

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
                                    batch.setObject(values - missingValues + 1, value);
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

    /**
     * Grabs Island Levels from the database
     */
    public List<IslandLevel> getIslandLevels() throws SQLException {
        List<IslandLevel> result = new ArrayList<>();

        try (Connection conn = getHikari().getConnection()) {
            Statement statement = conn.createStatement();

            ResultSet levels = statement.executeQuery("SELECT Id, Spawner_Limit, Member_Limit, Island_Radius, Cost " +
                    "FROM island_level;");

            while (levels.next()) {
                result.add(new IslandLevel(
                        levels.getInt("Id"),
                        levels.getInt("Spawner_Limit"),
                        levels.getInt("Member_Limit"),
                        levels.getInt("Island_Radius"),
                        levels.getLong("Cost")
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
}
