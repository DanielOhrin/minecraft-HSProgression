package net.highskiesmc.hsprogression.api;

import net.highskiesmc.hscore.data.MySQLDatabase;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Database extends MySQLDatabase {
    protected Database(@NonNull ConfigurationSection DB_CONFIG) throws SQLException {
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
            Statement statement = conn.createStatement();

            // ISLAND + ISLAND LEVELS
            // TODO: Create procedure for creating new island
            // TODO: Insert config values from the external file
            statement.addBatch("CREATE TABLE IF NOT EXISTS island (" +
                    "Id INT AUTO INCREMENT, " +
                    "Island_UUID VARCHAR(36) NOT NULL" +
                    "Level INT NOT NULL DEFAULT(1), " +
                    "IsDeleted BIT(1) NOT NULL DEFAULT(0), " +
                    "PRIMARY KEY(Id)" +
                    ") ENGINE = INNODB;");

            statement.addBatch("DROP TABLE IF EXISTS island_level;");
            statement.addBatch("CREATE TABLE island_level (" +
                    "Id INT AUTO INCREMENT, " +
                    "Spawner_Limit INT NOT NULL, " +
                    "Member_Limit UNSIGNED TINYINT(30), " +
                    "Island_Radius UNSIGNED TINYINT, " +
                    "Cost UNSIGNED BIGINT" +
                    "PRIMARY KEY(Id)" +
                    ") ENGINE = INNODB;");

            statement.addBatch("DROP TABLE IF EXISTS island_level_block;");
            statement.addBatch("CREATE TABLE island_level_block (" +
                    "Id INT AUTO INCREMENT, " +
                    "Island_Level INT NOT NULL, " +
                    "Encoding VARCHAR(MAX), " +
                    "PRIMARY KEY(Id)" +
                    ") ENGINE = INNODB;");
            // END

            statement.executeBatch();
        }
    }

    /**
     * Yeah this is documentation...
     */
    protected void insertConfigurationTables () throws IOException {
        // TODO: Try to get the file and read just one line for now to test!
        try (InputStream stream = Database.class.getResourceAsStream("/config.xml")) {
            StringBuilder sb = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }

            Bukkit.getLogger().info(sb.toString());
        }
    }
}
