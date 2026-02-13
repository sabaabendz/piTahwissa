package tn.esprit.tahwissa.entities;

import java.util.Date;

/**
 * Represents a client's reservation/booking for a voyage
 *
 * Why no dateDebut/dateFin?
 * → Those dates are in the Voyage table, not here!
 * → We only need voyage ID, and can fetch dates from voyage if needed
 */
public class ReservationVoyage {
    // DATABASE FIELDS
    private int id;                    // Auto-increment from DB
    private int idUtilisateur;         // Which client (user ID)
    private int idVoyage;              // Which voyage (links to voyage.id)
    private int nbrPersonnes;          // How many people booking
    private double montantTotal;       // Total cost
    private String statut;             // EN_ATTENTE, CONFIRMEE, ANNULEA, TERMINEE
    private Date dateCreation;         // When they booked

    // DISPLAY FIELD (not in database, fetched from voyage table)
    private String voyageTitre;        // Shows voyage name instead of ID

    // ════════════════════ CONSTRUCTORS ════════════════════

    /**
     * Empty constructor (required by database)
     */
    public ReservationVoyage() {
        this.dateCreation = new Date();
        this.statut = "EN_ATTENTE";  // Default status when booking
    }

    /**
     * Constructor with parameters (for creating new reservation)
     * @param idUtilisateur - Client who is booking
     * @param idVoyage - Which voyage they want
     * @param nbrPersonnes - Number of people
     * @param montantTotal - Total price (voyage.prix × nbrPersonnes)
     */
    public ReservationVoyage(int idUtilisateur, int idVoyage,
                             int nbrPersonnes, double montantTotal) {
        this.idUtilisateur = idUtilisateur;
        this.idVoyage = idVoyage;
        this.nbrPersonnes = nbrPersonnes;
        this.montantTotal = montantTotal;
        this.statut = "EN_ATTENTE";  // New bookings always start here
        this.dateCreation = new Date();
    }

    // ════════════════════ GETTERS & SETTERS ════════════════════

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(int idUtilisateur) { this.idUtilisateur = idUtilisateur; }

    public int getIdVoyage() { return idVoyage; }
    public void setIdVoyage(int idVoyage) { this.idVoyage = idVoyage; }

    public int getNbrPersonnes() { return nbrPersonnes; }
    public void setNbrPersonnes(int nbrPersonnes) { this.nbrPersonnes = nbrPersonnes; }

    public double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(double montantTotal) { this.montantTotal = montantTotal; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }

    public String getVoyageTitre() { return voyageTitre; }
    public void setVoyageTitre(String voyageTitre) { this.voyageTitre = voyageTitre; }

    @Override
    public String toString() {
        return "ReservationVoyage{" +
            "id=" + id +
            ", idUtilisateur=" + idUtilisateur +
            ", idVoyage=" + idVoyage +
            ", nbrPersonnes=" + nbrPersonnes +
            ", montantTotal=" + montantTotal +
            ", statut='" + statut + '\'' +
            ", dateCreation=" + dateCreation +
            ", voyageTitre='" + voyageTitre + '\'' +
            '}';
    }
}
