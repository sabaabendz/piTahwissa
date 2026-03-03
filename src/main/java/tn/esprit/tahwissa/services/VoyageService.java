package tn.esprit.tahwissa.services;

import tn.esprit.tahwissa.config.Database;
import tn.esprit.tahwissa.models.Voyage;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for managing Voyage operations
 */
public class VoyageService {

    private final Connection connection;

    public VoyageService() throws SQLException {
        this.connection = Database.getInstance();
    }

    /**
     * Get all voyages
     */
    public List<Voyage> getAllVoyages() {
        List<Voyage> voyages = new ArrayList<>();
        String query = "SELECT * FROM voyage ORDER BY date_depart DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Voyage voyage = extractVoyageFromResultSet(rs);
                voyages.add(voyage);
            }

            System.out.println("✅ Retrieved " + voyages.size() + " voyages");

        } catch (SQLException e) {
            System.err.println("❌ Error fetching voyages: " + e.getMessage());
            e.printStackTrace();
        }

        return voyages;
    }

    /**
     * Get voyage by ID
     */
    public Voyage getVoyageById(int id) {
        String query = "SELECT * FROM voyage WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractVoyageFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching voyage: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Add new voyage
     */
    public boolean addVoyage(Voyage voyage) {
        String query = """
            INSERT INTO voyage 
            (titre, description, destination, categorie, prix_unitaire, date_depart, date_retour, places_disponibles, image_url, statut)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, voyage.getTitre());
            pstmt.setString(2, voyage.getDescription());
            pstmt.setString(3, voyage.getDestination());
            pstmt.setString(4, voyage.getCategorie());
            pstmt.setBigDecimal(5, voyage.getPrixUnitaire());
            pstmt.setDate(6, java.sql.Date.valueOf(voyage.getDateDepart()));
            pstmt.setDate(7, java.sql.Date.valueOf(voyage.getDateRetour()));
            pstmt.setInt(8, voyage.getPlacesDisponibles());
            pstmt.setString(9, voyage.getImageUrl());
            pstmt.setString(10, voyage.getStatut());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        voyage.setId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("✅ Voyage added successfully with ID: " + voyage.getId());
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error adding voyage: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Update voyage
     */
    public boolean updateVoyage(Voyage voyage) {
        String query = """
            UPDATE voyage 
            SET titre = ?, description = ?, destination = ?, categorie = ?, 
                prix_unitaire = ?, date_depart = ?, date_retour = ?, 
                places_disponibles = ?, image_url = ?, statut = ?
            WHERE id = ?
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setString(1, voyage.getTitre());
            pstmt.setString(2, voyage.getDescription());
            pstmt.setString(3, voyage.getDestination());
            pstmt.setString(4, voyage.getCategorie());
            pstmt.setBigDecimal(5, voyage.getPrixUnitaire());
            pstmt.setDate(6, java.sql.Date.valueOf(voyage.getDateDepart()));
            pstmt.setDate(7, java.sql.Date.valueOf(voyage.getDateRetour()));
            pstmt.setInt(8, voyage.getPlacesDisponibles());
            pstmt.setString(9, voyage.getImageUrl());
            pstmt.setString(10, voyage.getStatut());
            pstmt.setInt(11, voyage.getId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("✅ Voyage updated successfully");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error updating voyage: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Delete voyage
     */
    public boolean deleteVoyage(int id) {
        String query = "DELETE FROM voyage WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("✅ Voyage deleted successfully");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error deleting voyage: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Get voyages by category
     */
    public List<Voyage> getVoyagesByCategory(String categorie) {
        List<Voyage> voyages = new ArrayList<>();
        String query = "SELECT * FROM voyage WHERE categorie = ? ORDER BY date_depart DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, categorie);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Voyage voyage = extractVoyageFromResultSet(rs);
                    voyages.add(voyage);
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching voyages by category: " + e.getMessage());
            e.printStackTrace();
        }

        return voyages;
    }

    /**
     * Search voyages
     */
    public List<Voyage> searchVoyages(String searchTerm) {
        List<Voyage> voyages = new ArrayList<>();
        String query = """
            SELECT * FROM voyage 
            WHERE titre LIKE ? OR destination LIKE ? OR description LIKE ?
            ORDER BY date_depart DESC
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Voyage voyage = extractVoyageFromResultSet(rs);
                    voyages.add(voyage);
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error searching voyages: " + e.getMessage());
            e.printStackTrace();
        }

        return voyages;
    }

    /**
     * Extract Voyage object from ResultSet
     */
    private Voyage extractVoyageFromResultSet(ResultSet rs) throws SQLException {
        Voyage voyage = new Voyage();

        voyage.setId(rs.getInt("id"));
        voyage.setTitre(rs.getString("titre"));
        voyage.setDescription(rs.getString("description"));
        voyage.setDestination(rs.getString("destination"));
        voyage.setCategorie(rs.getString("categorie"));
        voyage.setPrixUnitaire(rs.getBigDecimal("prix_unitaire"));
        voyage.setDateDepart(rs.getDate("date_depart").toLocalDate());
        voyage.setDateRetour(rs.getDate("date_retour").toLocalDate());
        voyage.setPlacesDisponibles(rs.getInt("places_disponibles"));
        voyage.setImageUrl(rs.getString("image_url"));
        voyage.setStatut(rs.getString("statut"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            voyage.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            voyage.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return voyage;
    }
}