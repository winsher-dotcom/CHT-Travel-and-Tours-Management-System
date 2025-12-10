package com.cht.TravelAndToursManagement.client.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConfig {
    private static final Logger logger =
            LoggerFactory.getLogger(DatabaseConfig.class);

    public static HikariDataSource dataSource;

    static {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(ConfigLoader.get("db.url"));
            config.setUsername(ConfigLoader.get("db.username"));
            config.setPassword(ConfigLoader.get("db.password"));
            config.setMaximumPoolSize(ConfigLoader.getInt("db.pool.max"));
            config.setMinimumIdle(ConfigLoader.getInt("db.pool.min"));
            config.setMaxLifetime(ConfigLoader.getInt("db.pool.timeout"));

            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            logger.error("Failed to initialize HikariCP", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    private DatabaseConfig() {
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

}
