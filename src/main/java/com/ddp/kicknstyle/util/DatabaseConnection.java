package com.ddp.kicknstyle.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:404/kicknstyle"; // Make sure port is 404
    private static final String USER = "root";  // Default MySQL username
    private static final String PASSWORD = "Earljoshdelgado_0404"; // Default MySQL password

    public static Connection getConnection() throws SQLException {
        try {
            // Load MySQL driver (Not always required but good practice)
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Return connection object
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("MySQL Driver not found.");
        }
    }
}