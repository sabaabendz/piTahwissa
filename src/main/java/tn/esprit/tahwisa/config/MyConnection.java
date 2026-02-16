package tn.esprit.tahwisa.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection {
    private static MyConnection instance;
    private Connection connection;

    // ⚠️ MODIFIE CES VALEURS SELON TA CONFIGURATION
    private final String URL = "jdbc:mysql://localhost:3306/tahwisa_db";
    private final String USER = "root";
    private final String PASSWORD = ""; // Mets ton mot de passe si nécessaire

    private MyConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connexion à la base de données réussie !");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver MySQL introuvable : " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion à la BD : " + e.getMessage());
        }
    }

    public static MyConnection getInstance() {
        if (instance == null) {
            instance = new MyConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}