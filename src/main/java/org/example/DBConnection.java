package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public Connection getConnection() {
        try {
            String jdbcURl ="jdbc:postgresql://localhost:5432/ingredient" ;
            String user = "postgres";
            String password = "harentsoa";
            return DriverManager.getConnection(jdbcURl,user,password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}