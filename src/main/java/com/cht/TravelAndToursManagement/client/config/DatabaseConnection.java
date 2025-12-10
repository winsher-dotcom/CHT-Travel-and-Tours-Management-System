package com.cht.TravelAndToursManagement.client.config;

import java.sql.Connection;
import java.sql.DriverManager;


public class DatabaseConnection {
    public Connection databaseLink;

    public Connection getConnection() {
        String databaseName = "cht_updated";
        String databaseUser = "jerico";
        String databasePassword = "password@12345";
//        String url = "jdbc:mysql://localhost:3306/" + databaseName;
        String url = "jdbc:mysql://192.168.254.149:3306/" + databaseName;


        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink = DriverManager.getConnection(url, databaseUser, databasePassword);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return databaseLink;
    }
}
