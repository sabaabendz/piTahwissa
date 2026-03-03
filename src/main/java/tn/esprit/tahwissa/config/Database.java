package tn.esprit.tahwissa.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database Connection Manager
 * Singleton pattern for database connections
 */
public class Database {

    private static Connection connection;

    // Database configuration
    private static final String URL = "jdbc:mysql://localhost:3306/gestionreservation";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    /**
     * Private constructor to prevent instantiation
     */
    private Database() {
    }

    /**
     * Get database connection (Singleton)
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getInstance() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load MySQL JDBC Driver
                Class.forName(DRIVER);

                // Establish connection
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("✅ Database connection established successfully!");

            } catch (ClassNotFoundException e) {
                System.err.println("❌ MySQL JDBC Driver not found!");
                throw new SQLException("MySQL JDBC Driver not found", e);
            } catch (SQLException e) {
                System.err.println("❌ Failed to connect to database!");
                throw e;
            }
        }
        return connection;
    }

    /**
     * Close database connection
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("🔌 Database connection closed!");
                connection = null;
            } catch (SQLException e) {
                System.err.println("❌ Error closing database connection!");
                e.printStackTrace();
            }
        }
    }

    /**
     * Test database connection
     * @return true if connected, false otherwise
     */
    public static boolean testConnection() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}