package tn.esprit.tahwissa.services;

import tn.esprit.tahwissa.config.Database;
import tn.esprit.tahwissa.models.ReservationVoyage;
import tn.esprit.tahwissa.models.ReservationVoyage.StatutReservation;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Service class for managing ReservationVoyage operations
 */
public class ReservationVoyageService {

    private final Connection connection;


    public ReservationVoyageService() throws SQLException {
        this.connection = Database.getInstance();
    }
    /**
     * Get all reservations with joined data (client name, destination, etc.)
     */
    public List<ReservationVoyage> getAllReservations() {
        List<ReservationVoyage> reservations = new ArrayList<>();
        
        String query = """
            SELECT 
                r.id,
                r.idUtilisateur,
                r.id_voyage,
                r.date_reservation,
                r.statut,
                r.nbrPersonnes,
                r.montantTotal,
                r.dateCreation,
                v.destination,
                v.titre,
                v.date_depart,
                v.date_retour
            FROM reservationvoyage r
            INNER JOIN voyage v ON r.id_voyage = v.id
            ORDER BY r.dateCreation DESC
        """;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                ReservationVoyage reservation = extractReservationFromResultSet(rs);
                reservations.add(reservation);
            }
            
            System.out.println("✅ Retrieved " + reservations.size() + " reservations");
            
        } catch (SQLException e) {
            System.err.println("❌ Error fetching reservations: " + e.getMessage());
            e.printStackTrace();
        }
        
        return reservations;
    }

    /**
     * Get reservation by ID
     */
    public ReservationVoyage getReservationById(int id) {
        String query = """
            SELECT 
                r.id,
                r.idUtilisateur,
                r.id_voyage,
                r.date_reservation,
                r.statut,
                r.nbrPersonnes,
                r.montantTotal,
                r.dateCreation,
                v.destination,
                v.titre,
                v.date_depart,
                v.date_retour
            FROM reservationvoyage r
            INNER JOIN voyage v ON r.id_voyage = v.id
            WHERE r.id = ?
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractReservationFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error fetching reservation: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * Add new reservation
     */
    public boolean addReservation(ReservationVoyage reservation) {
        String query = """
            INSERT INTO reservationvoyage 
            (idUtilisateur, id_voyage, date_reservation, statut, nbrPersonnes, montantTotal, dateCreation)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, reservation.getIdUtilisateur());
            pstmt.setInt(2, reservation.getIdVoyage());
            pstmt.setTimestamp(3, Timestamp.valueOf(reservation.getDateReservation()));
            pstmt.setString(4, reservation.getStatut().getCode());
            pstmt.setInt(5, reservation.getNbrPersonnes());
            pstmt.setBigDecimal(6, reservation.getMontantTotal());
            pstmt.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Get generated ID
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        reservation.setId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("✅ Reservation added successfully with ID: " + reservation.getId());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error adding reservation: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * Update existing reservation
     */
    public boolean updateReservation(ReservationVoyage reservation) {
        String query = """
            UPDATE reservationvoyage 
            SET idUtilisateur = ?,
                id_voyage = ?,
                date_reservation = ?,
                statut = ?,
                nbrPersonnes = ?,
                montantTotal = ?
            WHERE id = ?
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, reservation.getIdUtilisateur());
            pstmt.setInt(2, reservation.getIdVoyage());
            pstmt.setTimestamp(3, Timestamp.valueOf(reservation.getDateReservation()));
            pstmt.setString(4, reservation.getStatut().getCode());
            pstmt.setInt(5, reservation.getNbrPersonnes());
            pstmt.setBigDecimal(6, reservation.getMontantTotal());
            pstmt.setInt(7, reservation.getId());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("✅ Reservation updated successfully");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating reservation: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * Delete reservation
     */
    public boolean deleteReservation(int id) {
        String query = "DELETE FROM reservationvoyage WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("✅ Reservation deleted successfully");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error deleting reservation: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * Get reservations by user ID
     */
    public List<ReservationVoyage> getReservationsByUserId(int userId) {
        List<ReservationVoyage> reservations = new ArrayList<>();
        
        String query = """
            SELECT 
                r.id,
                r.idUtilisateur,
                r.id_voyage,
                r.date_reservation,
                r.statut,
                r.nbrPersonnes,
                r.montantTotal,
                r.dateCreation,
                v.destination,
                v.titre,
                v.date_depart,
                v.date_retour
            FROM reservationvoyage r
            INNER JOIN voyage v ON r.id_voyage = v.id
            WHERE r.idUtilisateur = ?
            ORDER BY r.dateCreation DESC
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ReservationVoyage reservation = extractReservationFromResultSet(rs);
                    reservations.add(reservation);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error fetching user reservations: " + e.getMessage());
            e.printStackTrace();
        }
        
        return reservations;
    }

    /**
     * Update reservation status
     */
    public boolean updateReservationStatus(int id, StatutReservation newStatus) {
        String query = "UPDATE reservationvoyage SET statut = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setString(1, newStatus.getCode());
            pstmt.setInt(2, id);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("✅ Reservation status updated to: " + newStatus.getLabel());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating reservation status: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * Search reservations by destination or client name
     */
    public List<ReservationVoyage> searchReservations(String searchTerm) {
        List<ReservationVoyage> reservations = new ArrayList<>();
        
        String query = """
            SELECT 
                r.id,
                r.idUtilisateur,
                r.id_voyage,
                r.date_reservation,
                r.statut,
                r.nbrPersonnes,
                r.montantTotal,
                r.dateCreation,
                v.destination,
                v.titre,
                v.date_depart,
                v.date_retour
            FROM reservationvoyage r
            INNER JOIN voyage v ON r.id_voyage = v.id
            WHERE v.destination LIKE ? OR v.titre LIKE ?
            ORDER BY r.dateCreation DESC
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ReservationVoyage reservation = extractReservationFromResultSet(rs);
                    reservations.add(reservation);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error searching reservations: " + e.getMessage());
            e.printStackTrace();
        }
        
        return reservations;
    }

    /**
     * Extract ReservationVoyage object from ResultSet
     */
    private ReservationVoyage extractReservationFromResultSet(ResultSet rs) throws SQLException {
        ReservationVoyage reservation = new ReservationVoyage();
        
        reservation.setId(rs.getInt("id"));
        reservation.setIdUtilisateur(rs.getInt("idUtilisateur"));
        reservation.setIdVoyage(rs.getInt("id_voyage"));
        
        Timestamp dateReservation = rs.getTimestamp("date_reservation");
        if (dateReservation != null) {
            reservation.setDateReservation(dateReservation.toLocalDateTime());
        }
        
        String statutStr = rs.getString("statut");
        reservation.setStatut(StatutReservation.fromCode(statutStr));
        
        reservation.setNbrPersonnes(rs.getInt("nbrPersonnes"));
        reservation.setMontantTotal(rs.getBigDecimal("montantTotal"));
        
        Timestamp dateCreation = rs.getTimestamp("dateCreation");
        if (dateCreation != null) {
            reservation.setDateCreation(dateCreation.toLocalDateTime());
        }
        
        // Set joined data
        reservation.setDestinationVoyage(rs.getString("destination"));
        reservation.setTitreVoyage(rs.getString("titre"));
        
        // For display: use "Client #ID" since we don't have user names yet
        reservation.setNomClient("Client #" + rs.getInt("idUtilisateur"));
        
        return reservation;
    }
    // Get total number of reservations
    public int getTotalReservations() throws SQLException {
        String query = "SELECT COUNT(*) FROM reservationvoyage";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    // Get total revenue (sum of montantTotal)
    public BigDecimal getTotalRevenue() throws SQLException {
        String query = "SELECT SUM(montantTotal) FROM reservationvoyage";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getBigDecimal(1);
        }
        return BigDecimal.ZERO;
    }

    // Get count of reservations per status
    public Map<String, Integer> getReservationsCountByStatus() throws SQLException {
        Map<String, Integer> map = new HashMap<>();
        String query = "SELECT statut, COUNT(*) FROM reservationvoyage GROUP BY statut";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                map.put(rs.getString(1), rs.getInt(2));
            }
        }
        return map;
    }

    // Get top 5 destinations by number of reservations
    public List<Object[]> getTopDestinations(int limit) throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String query = """
        SELECT v.destination, COUNT(*) as cnt
        FROM reservationvoyage r
        JOIN voyage v ON r.id_voyage = v.id
        GROUP BY v.destination
        ORDER BY cnt DESC
        LIMIT ?
    """;
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{rs.getString(1), rs.getInt(2)});
                }
            }
        }
        return list;
    }
}