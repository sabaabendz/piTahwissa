package tn.esprit.tahwisa.services;

import tn.esprit.tahwisa.config.MyConnection;
import tn.esprit.tahwisa.models.PointInteret;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PointInteretService {
    private Connection connection;

    public PointInteretService() {
        this.connection = MyConnection.getInstance().getConnection();
    }

    // ==================== CREATE ====================
    public void ajouterPointInteret(PointInteret pointInteret) throws SQLException {
        String query = "INSERT INTO point_interet (nom, type, description, image_url, destination_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, pointInteret.getNom());
            ps.setString(2, pointInteret.getType());
            ps.setString(3, pointInteret.getDescription());
            ps.setString(4, pointInteret.getImageUrl());
            ps.setInt(5, pointInteret.getDestinationId());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        pointInteret.setIdPointInteret(generatedKeys.getInt(1));
                    }
                }
                System.out.println("✅ Point d'intérêt ajouté : " + pointInteret.getNom());
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
        String query = "SELECT * FROM point_interet WHERE destination_id = ? ORDER BY created_at DESC";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, destinationId);

            try (ResultSet rs = ps.executeQuery()) {
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
        }

        return points;
    }

    // ==================== UPDATE ====================
    public void modifierPointInteret(PointInteret pointInteret) throws SQLException {
        String query = "UPDATE point_interet SET nom = ?, type = ?, description = ?, " +
                "image_url = ?, destination_id = ? WHERE id_point_interet = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, pointInteret.getNom());
            ps.setString(2, pointInteret.getType());
            ps.setString(3, pointInteret.getDescription());
            ps.setString(4, pointInteret.getImageUrl());
            ps.setInt(5, pointInteret.getDestinationId());
            ps.setInt(6, pointInteret.getIdPointInteret());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Point d'intérêt modifié : " + pointInteret.getNom());
            }
        }
    }

    // ==================== DELETE ====================
    public void supprimerPointInteret(int id) throws SQLException {
        String query = "DELETE FROM point_interet WHERE id_point_interet = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Point d'intérêt supprimé (ID: " + id + ")");
            }
        }
    }

    // ==================== SEARCH BY TYPE ====================
    public List<PointInteret> getPointsByType(String type) throws SQLException {
        List<PointInteret> points = new ArrayList<>();
        String query = "SELECT * FROM point_interet WHERE type = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, type);

            try (ResultSet rs = ps.executeQuery()) {
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
        }

        return points;
    }
}