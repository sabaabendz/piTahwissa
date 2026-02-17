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
    public void ajouterDestination(Destination dest) throws SQLException {
        // Réinitialiser l'auto-increment AVANT insertion
        resetAutoIncrement();

        String query = "INSERT INTO destination (nom, pays, ville, description, image_url, latitude, longitude) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, dest.getNom());
            ps.setString(2, dest.getPays());
            ps.setString(3, dest.getVille());
            ps.setString(4, dest.getDescription());
            ps.setString(5, dest.getImageUrl());

            if (dest.getLatitude() != null)
                ps.setBigDecimal(6, dest.getLatitude());
            else
                ps.setNull(6, Types.DECIMAL);

            if (dest.getLongitude() != null)
                ps.setBigDecimal(7, dest.getLongitude());
            else
                ps.setNull(7, Types.DECIMAL);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    dest.setIdDestination(keys.getInt(1));
                    System.out.println("✅ Destination ajoutée avec ID: " + dest.getIdDestination());
                }
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
                destinations.add(mapResultSet(rs));
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
                if (rs.next()) return mapResultSet(rs);
            }
        }
        return null;
    }

    // ==================== UPDATE ====================
    public void modifierDestination(Destination dest) throws SQLException {
        String query = "UPDATE destination SET nom=?, pays=?, ville=?, description=?, " +
                "image_url=?, latitude=?, longitude=? WHERE id_destination=?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, dest.getNom());
            ps.setString(2, dest.getPays());
            ps.setString(3, dest.getVille());
            ps.setString(4, dest.getDescription());
            ps.setString(5, dest.getImageUrl());

            if (dest.getLatitude() != null)
                ps.setBigDecimal(6, dest.getLatitude());
            else
                ps.setNull(6, Types.DECIMAL);

            if (dest.getLongitude() != null)
                ps.setBigDecimal(7, dest.getLongitude());
            else
                ps.setNull(7, Types.DECIMAL);

            ps.setInt(8, dest.getIdDestination());

            int rows = ps.executeUpdate();
            if (rows > 0) System.out.println("✅ Destination modifiée: " + dest.getNom());
        }
    }

    // ==================== DELETE ====================
    public void supprimerDestination(int id) throws SQLException {
        String query = "DELETE FROM destination WHERE id_destination = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("✅ Destination supprimée (ID: " + id + ")");

            // Réinitialiser l'auto-increment après suppression
            resetAutoIncrement();
        }
    }

    // ==================== RECHERCHE ====================
    public List<Destination> rechercherDestinations(String motCle) throws SQLException {
        List<Destination> destinations = new ArrayList<>();
        String query = "SELECT * FROM destination WHERE " +
                "LOWER(nom) LIKE ? OR LOWER(pays) LIKE ? OR LOWER(ville) LIKE ? " +
                "ORDER BY nom";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            String pattern = "%" + motCle.toLowerCase() + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    destinations.add(mapResultSet(rs));
                }
            }
        }
        return destinations;
    }

    // ==================== RESET AUTO INCREMENT ====================
    /**
     * Réinitialise l'AUTO_INCREMENT pour éviter les gaps
     * Ex: si on a ids 1,2,3 et on supprime 3,
     * le prochain sera 4 et non 7
     */
    private void resetAutoIncrement() {
        try {
            String getMax = "SELECT COALESCE(MAX(id_destination), 0) + 1 FROM destination";
            try (Statement st = connection.createStatement();
                 ResultSet rs = st.executeQuery(getMax)) {
                if (rs.next()) {
                    int nextId = rs.getInt(1);
                    String reset = "ALTER TABLE destination AUTO_INCREMENT = " + nextId;
                    try (Statement st2 = connection.createStatement()) {
                        st2.execute(reset);
                        System.out.println("🔄 AUTO_INCREMENT destination réinitialisé à: " + nextId);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Impossible de réinitialiser AUTO_INCREMENT: " + e.getMessage());
        }
    }

    // ==================== HELPER ====================
    private Destination mapResultSet(ResultSet rs) throws SQLException {
        Destination dest = new Destination();
        dest.setIdDestination(rs.getInt("id_destination"));
        dest.setNom(rs.getString("nom"));
        dest.setPays(rs.getString("pays"));
        dest.setVille(rs.getString("ville"));
        dest.setDescription(rs.getString("description"));
        dest.setImageUrl(rs.getString("image_url"));
        dest.setLatitude(rs.getBigDecimal("latitude"));
        dest.setLongitude(rs.getBigDecimal("longitude"));
        dest.setCreatedAt(rs.getTimestamp("created_at"));
        dest.setUpdatedAt(rs.getTimestamp("updated_at"));
        return dest;
    }
}