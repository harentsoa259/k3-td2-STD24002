package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public String URL =  "jdbc:postgresql://localhost:5432/mini_football_db";
    public String USER = "mini_football_db_manager";
    public String PASSWORD = "123456";

    public Connection getDBConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Erreur de connexion : " + e.getMessage());
            throw e;
        }
    }
}

