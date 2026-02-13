package tn.esprit.tahwissa.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {
    // ⚠️ MODIFIE CES 3 LIGNES SELON TON MYSQL
    private final String URL = "jdbc:mysql://localhost:3306/gestionreservation";
    private final String USER = "root";
    private final String PASSWORD = ""; // Met ton mot de passe si tu en as un

    private static MyDatabase instance;
    private Connection connection;

    // Constructeur PRIVÉ (pattern Singleton)
    private MyDatabase() {
        try {
            // 1. Charge le driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            // 2. Établit la connexion
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connexion à MySQL réussie !");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver MySQL introuvable !");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion MySQL : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Méthode STATIQUE pour obtenir l'instance unique
    public static MyDatabase getInstance() {
        if (instance == null) {
            instance = new MyDatabase();
        }
        return instance;
    }

    // Getter pour la connexion
    public Connection getConnection() {
        return connection;
    }
}
