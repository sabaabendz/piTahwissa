package tn.esprit.tahwissa.services;

import tn.esprit.tahwissa.config.Database;
import tn.esprit.tahwissa.models.Paiement;
import tn.esprit.tahwissa.models.Paiement.StatutPaiement;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for managing Paiement operations
 */
public class PaiementService {

    private final Connection connection;

    public PaiementService() throws SQLException {
        this.connection = Database.getInstance();
    }

    /**
     * Ensure the paiement table exists
     */
    public void ensureTableExists() {
        String createTable = """
            CREATE TABLE IF NOT EXISTS paiement (
                id INT AUTO_INCREMENT PRIMARY KEY,
                id_reservation INT NOT NULL,
                id_utilisateur INT NOT NULL,
                montant DECIMAL(10,2) NOT NULL,
                date_paiement DATETIME DEFAULT CURRENT_TIMESTAMP,
                methode_paiement VARCHAR(50) NOT NULL DEFAULT 'CARTE',
                statut VARCHAR(20) NOT NULL DEFAULT 'EN_ATTENTE',
                reference VARCHAR(100),
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (id_reservation) REFERENCES reservationvoyage(id) ON DELETE CASCADE
            )
        """;
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createTable);
            System.out.println("✅ Table 'paiement' verified/created");
        } catch (SQLException e) {
            System.err.println("❌ Error creating paiement table: " + e.getMessage());
        }
    }

    /**
     * Get all payments for a specific user
     */
    public List<Paiement> getPaiementsByUserId(int userId) {
        List<Paiement> paiements = new ArrayList<>();
        String query = """
            SELECT p.*, v.destination, v.titre
            FROM paiement p
            LEFT JOIN reservationvoyage r ON p.id_reservation = r.id
            LEFT JOIN voyage v ON r.id_voyage = v.id
            WHERE p.id_utilisateur = ?
            ORDER BY p.date_paiement DESC
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    paiements.add(extractPaiementFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching payments: " + e.getMessage());
        }
        return paiements;
    }

    /**
     * Get all payments
     */
    public List<Paiement> getAllPaiements() {
        List<Paiement> paiements = new ArrayList<>();
        String query = """
            SELECT p.*, v.destination, v.titre
            FROM paiement p
            LEFT JOIN reservationvoyage r ON p.id_reservation = r.id
            LEFT JOIN voyage v ON r.id_voyage = v.id
            ORDER BY p.date_paiement DESC
        """;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                paiements.add(extractPaiementFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching all payments: " + e.getMessage());
        }
        return paiements;
    }

    /**
     * Add a new payment
     */
    public boolean addPaiement(Paiement paiement) {
        String query = """
            INSERT INTO paiement (id_reservation, id_utilisateur, montant, date_paiement, methode_paiement, statut, reference)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, paiement.getIdReservation());
            pstmt.setInt(2, paiement.getIdUtilisateur());
            pstmt.setBigDecimal(3, paiement.getMontant());
            pstmt.setTimestamp(4, Timestamp.valueOf(
                    paiement.getDatePaiement() != null ? paiement.getDatePaiement() : LocalDateTime.now()));
            pstmt.setString(5, paiement.getMethodePaiement());
            pstmt.setString(6, paiement.getStatut().getCode());
            pstmt.setString(7, paiement.getReference());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) paiement.setId(keys.getInt(1));
                }
                System.out.println("✅ Payment added with ID: " + paiement.getId());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error adding payment: " + e.getMessage());
        }
        return false;
    }

    /**
     * Update payment status
     */
    public boolean updateStatut(int id, StatutPaiement newStatut) {
        String query = "UPDATE paiement SET statut = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, newStatut.getCode());
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error updating payment status: " + e.getMessage());
        }
        return false;
    }

    private Paiement extractPaiementFromResultSet(ResultSet rs) throws SQLException {
        Paiement p = new Paiement();
        p.setId(rs.getInt("id"));
        p.setIdReservation(rs.getInt("id_reservation"));
        p.setIdUtilisateur(rs.getInt("id_utilisateur"));
        p.setMontant(rs.getBigDecimal("montant"));

        Timestamp dp = rs.getTimestamp("date_paiement");
        if (dp != null) p.setDatePaiement(dp.toLocalDateTime());

        p.setMethodePaiement(rs.getString("methode_paiement"));
        p.setStatut(StatutPaiement.fromCode(rs.getString("statut")));
        p.setReference(rs.getString("reference"));

        Timestamp ca = rs.getTimestamp("created_at");
        if (ca != null) p.setCreatedAt(ca.toLocalDateTime());

        try {
            p.setDestinationVoyage(rs.getString("destination"));
            p.setTitreVoyage(rs.getString("titre"));
        } catch (SQLException ignored) {}

        return p;
    }
}
