package tn.esprit.tahwissa.services;

import tn.esprit.tahwissa.entities.ReservationVoyage;
import tn.esprit.tahwissa.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing ReservationVoyage database operations
 * This handles: CREATE, READ, UPDATE, DELETE operations
 */
public class ReservationVoyageService implements IService<ReservationVoyage> {

    private Connection connection;

    public ReservationVoyageService() {
        // Get database connection (singleton pattern)
        this.connection = MyDatabase.getInstance().getConnection();
        if (this.connection != null) {
            System.out.println("✅ ReservationVoyageService connected to database");
        } else {
            System.out.println("❌ Failed to connect to database");
        }
    }

    // ════════════════════ CREATE (INSERT) ════════════════════
    /**
     * Adds a new reservation to the database
     *
     * What happens:
     * 1. Take the ReservationVoyage object with all booking info
     * 2. Create SQL INSERT command
     * 3. Replace ? with actual values
     * 4. Send to database
     *
     * Example:
     * Input: ReservationVoyage(idUtil=5, idVoyage=1, nbPeople=2, amount=1000)
     * Output: New row in database
     */
    @Override
    public void ajouter(ReservationVoyage r) throws SQLException {
        // SQL template
        String sql = "INSERT INTO reservationvoyage " +
            "(idUtilisateur, id_voyage, nbrPersonnes, montantTotal, statut, dateCreation) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        // Prepare statement (like a template)
        PreparedStatement ps = connection.prepareStatement(sql);

        // Replace each ? with actual value
        ps.setInt(1, r.getIdUtilisateur());           // ? #1 = client ID
        ps.setInt(2, r.getIdVoyage());                // ? #2 = voyage ID
        ps.setInt(3, r.getNbrPersonnes());            // ? #3 = number of people
        ps.setDouble(4, r.getMontantTotal());         // ? #4 = total price
        ps.setString(5, r.getStatut());               // ? #5 = status (always EN_ATTENTE for new)
        ps.setTimestamp(6, new java.sql.Timestamp(r.getDateCreation().getTime())); // ? #6 = now

        // Execute the INSERT
        ps.executeUpdate();
        ps.close();
        System.out.println("✅ Reservation added to database");
    }

    // ════════════════════ READ (SELECT) ════════════════════
    /**
     * Gets ALL reservations from database
     * JOINS with voyage table to get voyage title
     *
     * Why JOIN?
     * Database has: idVoyage = 1
     * We want to show: "Paris 5 jours"
     * So we JOIN with voyage table to get the title
     *
     * Returns: List of all reservations with voyage info
     */
    @Override
    public List<ReservationVoyage> recupererTous() throws SQLException {
        List<ReservationVoyage> liste = new ArrayList<>();

        // SQL with JOIN to get voyage title
        String sql = "SELECT r.*, v.titre as voyage_titre " +
            "FROM reservationvoyage r " +
            "LEFT JOIN voyage v ON r.id_voyage = v.id " +
            "ORDER BY r.dateCreation DESC";

        // LEFT JOIN means: show all reservations, even if voyage doesn't exist

        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);

        // Loop through each row
        while (rs.next()) {
            ReservationVoyage r = new ReservationVoyage();

            // Extract values from database row
            r.setId(rs.getInt("id"));
            r.setIdUtilisateur(rs.getInt("idUtilisateur"));
            r.setIdVoyage(rs.getInt("id_voyage"));
            r.setNbrPersonnes(rs.getInt("nbrPersonnes"));
            r.setMontantTotal(rs.getDouble("montantTotal"));
            r.setStatut(rs.getString("statut"));
            r.setDateCreation(rs.getTimestamp("dateCreation"));
            r.setVoyageTitre(rs.getString("voyage_titre")); // ← From JOIN

            // Add to list
            liste.add(r);
        }

        rs.close();
        st.close();
        return liste;
    }

    // ════════════════════ UPDATE (MODIFY) ════════════════════
    /**
     * Updates an existing reservation
     *
     * Used when:
     * - Agent wants to CONFIRM a booking (status EN_ATTENTE → CONFIRMEE)
     * - Agent wants to REJECT a booking (status EN_ATTENTE → ANNULEA)
     * - Need to change number of people or amount
     */
    @Override
    public void modifier(ReservationVoyage r) throws SQLException {
        String sql = "UPDATE reservationvoyage SET " +
            "idUtilisateur=?, id_voyage=?, nbrPersonnes=?, montantTotal=?, statut=? " +
            "WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, r.getIdUtilisateur());
        ps.setInt(2, r.getIdVoyage());
        ps.setInt(3, r.getNbrPersonnes());
        ps.setDouble(4, r.getMontantTotal());
        ps.setString(5, r.getStatut());              // ← This is usually what changes
        ps.setInt(6, r.getId());                     // WHERE clause - which row to update?

        ps.executeUpdate();
        ps.close();
        System.out.println("✅ Reservation updated in database");
    }

    // ════════════════════ DELETE (REMOVE) ════════════════════
    /**
     * Deletes a reservation from database
     */
    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM reservationvoyage WHERE id=?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);

        int affected = ps.executeUpdate();
        ps.close();

        if (affected > 0) {
            System.out.println("✅ Reservation deleted from database");
        }
    }

    // ════════════════════ ADDITIONAL METHODS ════════════════════

    /**
     * Get a single reservation by ID
     */
    public ReservationVoyage recupererParId(int id) throws SQLException {
        String sql = "SELECT r.*, v.titre as voyage_titre FROM reservationvoyage r " +
            "LEFT JOIN voyage v ON r.id_voyage = v.id WHERE r.id=?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        ReservationVoyage r = null;
        if (rs.next()) {
            r = new ReservationVoyage();
            r.setId(rs.getInt("id"));
            r.setIdUtilisateur(rs.getInt("idUtilisateur"));
            r.setIdVoyage(rs.getInt("id_voyage"));
            r.setNbrPersonnes(rs.getInt("nbrPersonnes"));
            r.setMontantTotal(rs.getDouble("montantTotal"));
            r.setStatut(rs.getString("statut"));
            r.setDateCreation(rs.getTimestamp("dateCreation"));
            r.setVoyageTitre(rs.getString("voyage_titre"));
        }
        rs.close();
        ps.close();
        return r;
    }

    /**
     * Get all reservations for a specific client
     */
    public List<ReservationVoyage> recupererParUtilisateur(int idUtilisateur) throws SQLException {
        List<ReservationVoyage> liste = new ArrayList<>();
        String sql = "SELECT r.*, v.titre as voyage_titre FROM reservationvoyage r " +
            "LEFT JOIN voyage v ON r.id_voyage = v.id " +
            "WHERE r.idUtilisateur=? ORDER BY r.dateCreation DESC";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, idUtilisateur);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            ReservationVoyage r = new ReservationVoyage();
            r.setId(rs.getInt("id"));
            r.setIdUtilisateur(rs.getInt("idUtilisateur"));
            r.setIdVoyage(rs.getInt("id_voyage"));
            r.setNbrPersonnes(rs.getInt("nbrPersonnes"));
            r.setMontantTotal(rs.getDouble("montantTotal"));
            r.setStatut(rs.getString("statut"));
            r.setDateCreation(rs.getTimestamp("dateCreation"));
            r.setVoyageTitre(rs.getString("voyage_titre"));
            liste.add(r);
        }
        rs.close();
        ps.close();
        return liste;
    }

    /**
     * Get all pending reservations (for agent to review)
     */
    public List<ReservationVoyage> recupererEnAttente() throws SQLException {
        List<ReservationVoyage> liste = new ArrayList<>();
        String sql = "SELECT r.*, v.titre as voyage_titre FROM reservationvoyage r " +
            "LEFT JOIN voyage v ON r.id_voyage = v.id " +
            "WHERE r.statut='EN_ATTENTE' ORDER BY r.dateCreation ASC";

        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            ReservationVoyage r = new ReservationVoyage();
            r.setId(rs.getInt("id"));
            r.setIdUtilisateur(rs.getInt("idUtilisateur"));
            r.setIdVoyage(rs.getInt("id_voyage"));
            r.setNbrPersonnes(rs.getInt("nbrPersonnes"));
            r.setMontantTotal(rs.getDouble("montantTotal"));
            r.setStatut(rs.getString("statut"));
            r.setDateCreation(rs.getTimestamp("dateCreation"));
            r.setVoyageTitre(rs.getString("voyage_titre"));
            liste.add(r);
        }
        rs.close();
        st.close();
        return liste;
    }
}
