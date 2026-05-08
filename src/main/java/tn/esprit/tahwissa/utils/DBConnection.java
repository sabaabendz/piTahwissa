package tn.esprit.tahwissa.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String DB_NAME = System.getenv().getOrDefault("TAHWISSA_DB_NAME", "basededonneeintegre_3");
    private static final String URL = "jdbc:mysql://localhost:3306/" + DB_NAME + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = System.getenv().getOrDefault("TAHWISSA_DB_USER", "root");
    private static final String PASSWORD = System.getenv().getOrDefault("TAHWISSA_DB_PASSWORD", "");
    
    private static Connection connection;

    private DBConnection() {}

    public static Connection getConnection() {
        try {
            // Log what we are trying to connect with
            if (connection == null || connection.isClosed()) {
                System.out.println("Connecting to: " + URL + " as " + USER);
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✓ Connexion à la base de données réussie");
            }
        } catch (SQLException e) {
            System.err.println("✗ SQL State: " + e.getSQLState());
            System.err.println("✗ Error Code: " + e.getErrorCode());
            System.err.println("✗ Message: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Connexion à la base de données fermée");
            }
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de la fermeture de la connexion: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
