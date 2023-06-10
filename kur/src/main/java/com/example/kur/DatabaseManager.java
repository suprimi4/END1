package com.example.kur;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class  DatabaseManager {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/Tasks";
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "13v16v27v";

    private Connection connection;

    public DatabaseManager() {
        // Создание экземпляра класса DatabaseManager не открывает соединение с базой данных
        // Соединение будет открыто при вызове метода getConnection()
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
