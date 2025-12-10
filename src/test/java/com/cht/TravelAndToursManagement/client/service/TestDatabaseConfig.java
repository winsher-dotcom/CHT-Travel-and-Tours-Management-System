package com.cht.TravelAndToursManagement.client.service;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class TestDatabaseConfig {

    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        config.setUsername("sa");
        config.setPassword("");
        config.setMaximumPoolSize(2);

        dataSource = new HikariDataSource(config);

        runSchema();
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private static void runSchema() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(Files.readString(Paths.get(
                    TestDatabaseConfig.class.getClassLoader()
                            .getResource("schema.sql").toURI()
            )));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
