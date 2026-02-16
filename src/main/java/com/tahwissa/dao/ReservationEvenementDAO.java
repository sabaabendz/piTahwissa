package com.tahwissa.dao;

import com.tahwissa.entity.ReservationEvenement;
import com.tahwissa.utils.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationEvenementDAO {
    
    public boolean create(ReservationEvenement reservation) {
        String query = "INSERT INTO reservation_evenement (date_reservation, nb_places_reservees, statut, id_evenement, id_user) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(reservation.getDateReservation()));
            stmt.setInt(2, reservation.getNbPlacesReservees());
            stmt.setString(3, reservation.getStatut());
            stmt.setInt(4, reservation.getIdEvenement());
            stmt.setInt(5, reservation.getIdUser());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    reservation.setIdReservation(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<ReservationEvenement> findAll() {
        List<ReservationEvenement> reservations = new ArrayList<>();
        String query = "SELECT r.*, e.titre as titre_evenement, u.nom as nom_user " +
                      "FROM reservation_evenement r " +
                      "LEFT JOIN evenement e ON r.id_evenement = e.id_evenement " +
                      "LEFT JOIN utilisateur u ON r.id_user = u.id_user " +
                      "ORDER BY r.date_reservation DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                reservations.add(extractReservationFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    public List<ReservationEvenement> findByUserId(int userId) {
        List<ReservationEvenement> reservations = new ArrayList<>();
        String query = "SELECT r.*, e.titre as titre_evenement, u.nom as nom_user " +
                      "FROM reservation_evenement r " +
                      "LEFT JOIN evenement e ON r.id_evenement = e.id_evenement " +
                      "LEFT JOIN utilisateur u ON r.id_user = u.id_user " +
                      "WHERE r.id_user = ? ORDER BY r.date_reservation DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                reservations.add(extractReservationFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    public List<ReservationEvenement> findByEvenementId(int evenementId) {
        List<ReservationEvenement> reservations = new ArrayList<>();
        String query = "SELECT r.*, e.titre as titre_evenement, u.nom as nom_user " +
                      "FROM reservation_evenement r " +
                      "LEFT JOIN evenement e ON r.id_evenement = e.id_evenement " +
                      "LEFT JOIN utilisateur u ON r.id_user = u.id_user " +
                      "WHERE r.id_evenement = ? ORDER BY r.date_reservation DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, evenementId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                reservations.add(extractReservationFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    public ReservationEvenement findById(int id) {
        String query = "SELECT r.*, e.titre as titre_evenement, u.nom as nom_user " +
                      "FROM reservation_evenement r " +
                      "LEFT JOIN evenement e ON r.id_evenement = e.id_evenement " +
                      "LEFT JOIN utilisateur u ON r.id_user = u.id_user " +
                      "WHERE r.id_reservation = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractReservationFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean update(ReservationEvenement reservation) {
        String query = "UPDATE reservation_evenement SET nb_places_reservees = ?, statut = ? WHERE id_reservation = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, reservation.getNbPlacesReservees());
            stmt.setString(2, reservation.getStatut());
            stmt.setInt(3, reservation.getIdReservation());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String query = "DELETE FROM reservation_evenement WHERE id_reservation = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private ReservationEvenement extractReservationFromResultSet(ResultSet rs) throws SQLException {
        ReservationEvenement reservation = new ReservationEvenement();
        reservation.setIdReservation(rs.getInt("id_reservation"));
        reservation.setNbPlacesReservees(rs.getInt("nb_places_reservees"));
        reservation.setStatut(rs.getString("statut"));
        reservation.setIdEvenement(rs.getInt("id_evenement"));
        reservation.setIdUser(rs.getInt("id_user"));
        
        Timestamp dateReservation = rs.getTimestamp("date_reservation");
        if (dateReservation != null) {
            reservation.setDateReservation(dateReservation.toLocalDateTime());
        }
        
        String titreEvenement = rs.getString("titre_evenement");
        if (titreEvenement != null) {
            reservation.setTitreEvenement(titreEvenement);
        }
        
        String nomUser = rs.getString("nom_user");
        if (nomUser != null) {
            reservation.setNomUser(nomUser);
        }
        
        return reservation;
    }
}
