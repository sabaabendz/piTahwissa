package tn.esprit.tahwissa.dao;

import tn.esprit.tahwissa.models.Evenement;
import tn.esprit.tahwissa.utils.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EvenementDAO {
    
    public boolean create(Evenement evenement) {
        String query = "INSERT INTO evenement (titre, description, lieu, date_event, heure_event, prix, nb_places, categorie, statut, image_filename, date_creation) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, evenement.getTitre());
            stmt.setString(2, evenement.getDescription());
            stmt.setString(3, evenement.getLieu());
            stmt.setDate(4, Date.valueOf(evenement.getDateEvent()));
            stmt.setTime(5, Time.valueOf(evenement.getHeureEvent()));
            stmt.setDouble(6, evenement.getPrix());
            stmt.setInt(7, evenement.getNbPlaces());
            stmt.setString(8, evenement.getCategorie());
            stmt.setString(9, evenement.getStatut());
            stmt.setString(10, evenement.getImageFilename());
            stmt.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    evenement.setIdEvenement(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Evenement> findAll() {
        List<Evenement> evenements = new ArrayList<>();
        String query = "SELECT * FROM evenement ORDER BY date_event DESC, heure_event DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                evenements.add(extractEvenementFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return evenements;
    }

    public List<Evenement> findAvailable() {
        List<Evenement> evenements = new ArrayList<>();
        String query = "SELECT * FROM evenement WHERE statut = 'DISPONIBLE' AND date_event >= CURDATE() ORDER BY date_event ASC, heure_event ASC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                evenements.add(extractEvenementFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return evenements;
    }

    public Evenement findById(int id) {
        String query = "SELECT * FROM evenement WHERE id_evenement = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractEvenementFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean update(Evenement evenement) {
        String query = "UPDATE evenement SET titre = ?, description = ?, lieu = ?, date_event = ?, heure_event = ?, prix = ?, nb_places = ?, categorie = ?, statut = ?, image_filename = ? WHERE id_evenement = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, evenement.getTitre());
            stmt.setString(2, evenement.getDescription());
            stmt.setString(3, evenement.getLieu());
            stmt.setDate(4, Date.valueOf(evenement.getDateEvent()));
            stmt.setTime(5, Time.valueOf(evenement.getHeureEvent()));
            stmt.setDouble(6, evenement.getPrix());
            stmt.setInt(7, evenement.getNbPlaces());
            stmt.setString(8, evenement.getCategorie());
            stmt.setString(9, evenement.getStatut());
            stmt.setString(10, evenement.getImageFilename());
            stmt.setInt(11, evenement.getIdEvenement());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String query = "DELETE FROM evenement WHERE id_evenement = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Evenement> searchByKeyword(String keyword) {
        List<Evenement> evenements = new ArrayList<>();
        String query = "SELECT * FROM evenement WHERE titre LIKE ? OR description LIKE ? OR lieu LIKE ? OR categorie LIKE ? ORDER BY date_event DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                evenements.add(extractEvenementFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return evenements;
    }

    private Evenement extractEvenementFromResultSet(ResultSet rs) throws SQLException {
        Evenement evenement = new Evenement();
        evenement.setIdEvenement(rs.getInt("id_evenement"));
        evenement.setTitre(rs.getString("titre"));
        evenement.setDescription(rs.getString("description"));
        evenement.setLieu(rs.getString("lieu"));
        evenement.setDateEvent(rs.getDate("date_event").toLocalDate());
        evenement.setHeureEvent(rs.getTime("heure_event").toLocalTime());
        evenement.setPrix(rs.getDouble("prix"));
        evenement.setNbPlaces(rs.getInt("nb_places"));
        evenement.setCategorie(rs.getString("categorie"));
        evenement.setStatut(rs.getString("statut"));
        try {
            evenement.setImageFilename(rs.getString("image_filename"));
        } catch (SQLException ex) {
            evenement.setImageFilename(null);
        }
        
        try {
            Timestamp dateCreation = rs.getTimestamp("date_creation");
            if (dateCreation != null) {
                evenement.setDateCreation(dateCreation.toLocalDateTime());
            }
        } catch (SQLException e) {
            // date_creation column doesn't exist, set default value
            evenement.setDateCreation(LocalDateTime.now());
        }
        
        return evenement;
    }
}
