package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {
    // Configuration pour ta base pidev
    private final String URL = "jdbc:mysql://localhost:3306/pidev";
    private final String USER = "root";
    private final String PASSWORD = "";

    private static MyDatabase instance;
    private Connection connection;

    // Constructeur privé (Singleton)
    private MyDatabase() {
        try {
            // Charger le driver MySQL (optionnel pour les versions récentes)
            // Class.forName("com.mysql.cj.jdbc.Driver");

            // Établir la connexion
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connexion établie à la base: " + URL);

        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion à la base de données:");
            System.err.println("   URL: " + URL);
            System.err.println("   User: " + USER);
            System.err.println("   Erreur: " + e.getMessage());
            throw new RuntimeException("Impossible de se connecter à la base de données", e);
        }
    }

    // Méthode Singleton pour obtenir l'instance
    public static MyDatabase getInstance() {
        if (instance == null) {
            synchronized (MyDatabase.class) {
                if (instance == null) {
                    instance = new MyDatabase();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    // Méthode pour tester la connexion
    public boolean testConnection() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    // Méthode pour fermer la connexion (optionnel)
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔌 Connexion fermée");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture: " + e.getMessage());
        }
    }
}