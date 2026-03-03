package tn.esprit.tahwissa.services;

import tn.esprit.tahwissa.config.Database;
import tn.esprit.tahwissa.models.PointInteret;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PointInteretService {

    private Connection connection;

    public PointInteretService() {
        try {
            this.connection = Database.getInstance();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ==================== CREATE ====================
    public void ajouterPointInteret(PointInteret pi) throws SQLException {
        // Réinitialiser l'auto-increment avant l'insertion
        resetAutoIncrement("point_interet", "id_point_interet");

        String query = "INSERT INTO point_interet (nom, type, description, image_url, destination_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, pi.getNom());
            ps.setString(2, pi.getType());
            ps.setString(3, pi.getDescription());
            ps.setString(4, pi.getImageUrl());
            ps.setInt(5, pi.getDestinationId());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    pi.setIdPointInteret(keys.getInt(1));
                    System.out.println("✅ Point d'intérêt ajouté avec ID: " + pi.getIdPointInteret());
                }
            }
        }
    }

    // ==================== READ ALL ====================
    public List<PointInteret> afficherPointsInteret() throws SQLException {
        List<PointInteret> points = new ArrayList<>();
        String query = "SELECT * FROM point_interet ORDER BY created_at DESC";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                PointInteret pi = new PointInteret(
                        rs.getInt("id_point_interet"),
                        rs.getString("nom"),
                        rs.getString("type"),
                        rs.getString("description"),
                        rs.getString("image_url"),
                        rs.getInt("destination_id"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                );
                points.add(pi);
            }
        }

        System.out.println("📋 " + points.size() + " points d'intérêt récupérés");
        return points;
    }

    // ==================== READ BY DESTINATION ====================
    public List<PointInteret> getPointsByDestination(int destinationId) throws SQLException {
        List<PointInteret> points = new ArrayList<>();
        String query = "SELECT * FROM point_interet WHERE destination_id = ? ORDER BY nom";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, destinationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    points.add(mapResultSet(rs));
                }
            }
        }
        return points;
    }

    // ==================== READ BY TYPE ====================
    public List<PointInteret> getPointsByType(String type) throws SQLException {
        List<PointInteret> points = new ArrayList<>();
        String query = "SELECT * FROM point_interet WHERE type = ? ORDER BY nom";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    points.add(mapResultSet(rs));
                }
            }
        }
        return points;
    }

    // ==================== UPDATE ====================
    public void modifierPointInteret(PointInteret pi) throws SQLException {
        String query = "UPDATE point_interet SET nom=?, type=?, description=?, " +
                "image_url=?, destination_id=? WHERE id_point_interet=?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, pi.getNom());
            ps.setString(2, pi.getType());
            ps.setString(3, pi.getDescription());
            ps.setString(4, pi.getImageUrl());
            ps.setInt(5, pi.getDestinationId());
            ps.setInt(6, pi.getIdPointInteret());

            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("✅ Point d'intérêt modifié: " + pi.getNom());
        }
    }

    // ==================== DELETE ====================
    public void supprimerPointInteret(int id) throws SQLException {
        String query = "DELETE FROM point_interet WHERE id_point_interet=?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("✅ Point d'intérêt supprimé (ID: " + id + ")");

            // Réinitialiser l'auto-increment après suppression
            resetAutoIncrement("point_interet", "id_point_interet");
        }
    }

    // ==================== RESET AUTO INCREMENT ====================
    /**
     * Réinitialise l'AUTO_INCREMENT pour que le prochain ID
     * soit max(id) + 1 (sans gaps dus aux suppressions)
     */
    private void resetAutoIncrement(String tableName, String idColumn) {
        try {
            String getMax = "SELECT COALESCE(MAX(" + idColumn + "), 0) + 1 FROM " + tableName;
            try (Statement st = connection.createStatement();
                 ResultSet rs = st.executeQuery(getMax)) {
                if (rs.next()) {
                    int nextId = rs.getInt(1);
                    String reset = "ALTER TABLE " + tableName + " AUTO_INCREMENT = " + nextId;
                    try (Statement st2 = connection.createStatement()) {
                        st2.execute(reset);
                        System.out.println("🔄 AUTO_INCREMENT réinitialisé à: " + nextId);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Impossible de réinitialiser AUTO_INCREMENT: " + e.getMessage());
        }
    }

    // ==================== HELPER ====================
    private PointInteret mapResultSet(ResultSet rs) throws SQLException {
        return new PointInteret(
                rs.getInt("id_point_interet"),
                rs.getString("nom"),
                rs.getString("type"),
                rs.getString("description"),
                rs.getString("image_url"),
                rs.getInt("destination_id"),
                rs.getTimestamp("created_at"),
                rs.getTimestamp("updated_at")
        );
    }
}