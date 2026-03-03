package tn.esprit.tahwissa.dao;

import tn.esprit.tahwissa.models.Reclamation;
import tn.esprit.tahwissa.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReclamationDAO {
    
    public boolean create(Reclamation reclamation) {
        String query = "INSERT INTO reclamation (titre, description, type, statut, date_creation, id_user) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, reclamation.getTitre());
            stmt.setString(2, reclamation.getDescription());
            stmt.setString(3, reclamation.getType());
            stmt.setString(4, reclamation.getStatut());
            stmt.setTimestamp(5, Timestamp.valueOf(reclamation.getDateCreation()));
            stmt.setInt(6, reclamation.getIdUser());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    reclamation.setIdReclamation(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Reclamation> findAll() {
        List<Reclamation> reclamations = new ArrayList<>();
        String query = "SELECT r.*, u.nom as nom_user FROM reclamation r " +
                      "LEFT JOIN utilisateur u ON r.id_user = u.id_user " +
                      "ORDER BY r.date_creation DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                reclamations.add(extractReclamationFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reclamations;
    }

    public List<Reclamation> findByUserId(int userId) {
        List<Reclamation> reclamations = new ArrayList<>();
        String query = "SELECT r.*, u.nom as nom_user FROM reclamation r " +
                      "LEFT JOIN utilisateur u ON r.id_user = u.id_user " +
                      "WHERE r.id_user = ? ORDER BY r.date_creation DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                reclamations.add(extractReclamationFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reclamations;
    }

    public Reclamation findById(int id) {
        String query = "SELECT r.*, u.nom as nom_user FROM reclamation r " +
                      "LEFT JOIN utilisateur u ON r.id_user = u.id_user " +
                      "WHERE r.id_reclamation = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractReclamationFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean update(Reclamation reclamation) {
        String query = "UPDATE reclamation SET titre = ?, description = ?, type = ?, statut = ? WHERE id_reclamation = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, reclamation.getTitre());
            stmt.setString(2, reclamation.getDescription());
            stmt.setString(3, reclamation.getType());
            stmt.setString(4, reclamation.getStatut());
            stmt.setInt(5, reclamation.getIdReclamation());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String query = "DELETE FROM reclamation WHERE id_reclamation = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Reclamation> searchByKeyword(String keyword) {
        List<Reclamation> reclamations = new ArrayList<>();
        String query = "SELECT r.*, u.nom as nom_user FROM reclamation r " +
                      "LEFT JOIN utilisateur u ON r.id_user = u.id_user " +
                      "WHERE r.titre LIKE ? OR r.description LIKE ? OR r.type LIKE ? " +
                      "ORDER BY r.date_creation DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reclamations.add(extractReclamationFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reclamations;
    }

    public List<Reclamation> findByStatut(String statut) {
        List<Reclamation> reclamations = new ArrayList<>();
        String query = "SELECT r.*, u.nom as nom_user FROM reclamation r " +
                      "LEFT JOIN utilisateur u ON r.id_user = u.id_user " +
                      "WHERE r.statut = ? ORDER BY r.date_creation DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, statut);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                reclamations.add(extractReclamationFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reclamations;
    }

    private Reclamation extractReclamationFromResultSet(ResultSet rs) throws SQLException {
        Reclamation reclamation = new Reclamation();
        reclamation.setIdReclamation(rs.getInt("id_reclamation"));
        reclamation.setTitre(rs.getString("titre"));
        reclamation.setDescription(rs.getString("description"));
        reclamation.setType(rs.getString("type"));
        reclamation.setStatut(rs.getString("statut"));
        reclamation.setIdUser(rs.getInt("id_user"));
        
        Timestamp dateCreation = rs.getTimestamp("date_creation");
        if (dateCreation != null) {
            reclamation.setDateCreation(dateCreation.toLocalDateTime());
        }
        
        String nomUser = rs.getString("nom_user");
        if (nomUser != null) {
            reclamation.setNomUser(nomUser);
        }
        
        return reclamation;
    }
}
