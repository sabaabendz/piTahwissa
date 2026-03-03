package tn.esprit.tahwissa.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/gestionreservation?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    private static Connection connection;

    private DBConnection() {}

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✓ Connexion à la base de données réussie");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("✗ Driver MySQL non trouvé: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("✗ Erreur de connexion à la base de données: " + e.getMessage());
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
