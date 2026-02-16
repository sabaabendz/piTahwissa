package tn.esprit.tahwisa.services;

import tn.esprit.tahwisa.config.MyConnection;
import tn.esprit.tahwisa.models.Destination;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DestinationService {
    private Connection connection;

    public DestinationService() {
        this.connection = MyConnection.getInstance().getConnection();
    }

    // ==================== CREATE ====================
    public void ajouterDestination(Destination destination) throws SQLException {
        String query = "INSERT INTO destination (nom, pays, ville, description, image_url, latitude, longitude) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, destination.getNom());
            ps.setString(2, destination.getPays());
            ps.setString(3, destination.getVille());
            ps.setString(4, destination.getDescription());
            ps.setString(5, destination.getImageUrl());
            ps.setBigDecimal(6, destination.getLatitude());
            ps.setBigDecimal(7, destination.getLongitude());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                // Récupérer l'ID généré
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        destination.setIdDestination(generatedKeys.getInt(1));
                    }
                }
                System.out.println("✅ Destination ajoutée avec succès : " + destination.getNom());
            }
        }
    }

    // ==================== READ ALL ====================
    public List<Destination> afficherDestinations() throws SQLException {
        List<Destination> destinations = new ArrayList<>();
        String query = "SELECT * FROM destination ORDER BY created_at DESC";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                Destination dest = new Destination(
                        rs.getInt("id_destination"),
                        rs.getString("nom"),
                        rs.getString("pays"),
                        rs.getString("ville"),
                        rs.getString("description"),
                        rs.getString("image_url"),
                        rs.getBigDecimal("latitude"),
                        rs.getBigDecimal("longitude"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                );
                destinations.add(dest);
            }
        }

        System.out.println("📋 " + destinations.size() + " destinations récupérées");
        return destinations;
    }

    // ==================== READ BY ID ====================
    public Destination getDestinationById(int id) throws SQLException {
        String query = "SELECT * FROM destination WHERE id_destination = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Destination(
                            rs.getInt("id_destination"),
                            rs.getString("nom"),
                            rs.getString("pays"),
                            rs.getString("ville"),
                            rs.getString("description"),
                            rs.getString("image_url"),
                            rs.getBigDecimal("latitude"),
                            rs.getBigDecimal("longitude"),
                            rs.getTimestamp("created_at"),
                            rs.getTimestamp("updated_at")
                    );
                }
            }
        }
        return null;
    }

    // ==================== UPDATE ====================
    public void modifierDestination(Destination destination) throws SQLException {
        String query = "UPDATE destination SET nom = ?, pays = ?, ville = ?, " +
                "description = ?, image_url = ?, latitude = ?, longitude = ? " +
                "WHERE id_destination = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, destination.getNom());
            ps.setString(2, destination.getPays());
            ps.setString(3, destination.getVille());
            ps.setString(4, destination.getDescription());
            ps.setString(5, destination.getImageUrl());
            ps.setBigDecimal(6, destination.getLatitude());
            ps.setBigDecimal(7, destination.getLongitude());
            ps.setInt(8, destination.getIdDestination());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Destination modifiée : " + destination.getNom());
            } else {
                System.out.println("⚠️ Aucune destination trouvée avec l'ID : " + destination.getIdDestination());
            }
        }
    }

    // ==================== DELETE ====================
    public void supprimerDestination(int id) throws SQLException {
        String query = "DELETE FROM destination WHERE id_destination = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Destination supprimée (ID: " + id + ")");
            } else {
                System.out.println("⚠️ Aucune destination trouvée avec l'ID : " + id);
            }
        }
    }

    // ==================== SEARCH ====================
    public List<Destination> rechercherDestinations(String motCle) throws SQLException {
        List<Destination> destinations = new ArrayList<>();
        String query = "SELECT * FROM destination WHERE nom LIKE ? OR pays LIKE ? OR ville LIKE ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            String searchPattern = "%" + motCle + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Destination dest = new Destination(
                            rs.getInt("id_destination"),
                            rs.getString("nom"),
                            rs.getString("pays"),
                            rs.getString("ville"),
                            rs.getString("description"),
                            rs.getString("image_url"),
                            rs.getBigDecimal("latitude"),
                            rs.getBigDecimal("longitude"),
                            rs.getTimestamp("created_at"),
                            rs.getTimestamp("updated_at")
                    );
                    destinations.add(dest);
                }
            }
        }

        System.out.println("🔍 Recherche '" + motCle + "' : " + destinations.size() + " résultat(s)");
        return destinations;
    }
}