package tn.esprit.tahwissa.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ReservationVoyage Model - Maps to 'reservationvoyage' table in database
 */
public class ReservationVoyage {
    private int id;
    private int idUtilisateur;
    private int idVoyage;
    private LocalDateTime dateReservation;
    private StatutReservation statut;
    private int nbrPersonnes;
    private BigDecimal montantTotal;
    private LocalDateTime dateCreation;
    
    // For display purposes - joined data from other tables
    private String nomClient;
    private String destinationVoyage;
    private String titreVoyage;

    // Enum for reservation status
    public enum StatutReservation {
        EN_ATTENTE("EN_ATTENTE", "En Attente"),
        CONFIRMEE("CONFIRMEE", "Confirmée"),
        ANNULEE("ANNULEE", "Annulée"),
        TERMINEE("TERMINEE", "Terminée");

        private final String code;
        private final String label;

        StatutReservation(String code, String label) {
            this.code = code;
            this.label = label;
        }

        public String getCode() {
            return code;
        }

        public String getLabel() {
            return label;
        }

        public static StatutReservation fromCode(String code) {
            for (StatutReservation status : values()) {
                if (status.code.equals(code)) {
                    return status;
                }
            }
            return EN_ATTENTE;
        }
    }

    // Constructors
    public ReservationVoyage() {
    }

    public ReservationVoyage(int id, int idUtilisateur, int idVoyage, 
                            LocalDateTime dateReservation, StatutReservation statut,
                            int nbrPersonnes, BigDecimal montantTotal) {
        this.id = id;
        this.idUtilisateur = idUtilisateur;
        this.idVoyage = idVoyage;
        this.dateReservation = dateReservation;
        this.statut = statut;
        this.nbrPersonnes = nbrPersonnes;
        this.montantTotal = montantTotal;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public int getIdVoyage() {
        return idVoyage;
    }

    public void setIdVoyage(int idVoyage) {
        this.idVoyage = idVoyage;
    }

    public LocalDateTime getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(LocalDateTime dateReservation) {
        this.dateReservation = dateReservation;
    }

    public StatutReservation getStatut() {
        return statut;
    }

    public void setStatut(StatutReservation statut) {
        this.statut = statut;
    }

    public int getNbrPersonnes() {
        return nbrPersonnes;
    }

    public void setNbrPersonnes(int nbrPersonnes) {
        this.nbrPersonnes = nbrPersonnes;
    }

    public BigDecimal getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(BigDecimal montantTotal) {
        this.montantTotal = montantTotal;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getNomClient() {
        return nomClient;
    }

    public void setNomClient(String nomClient) {
        this.nomClient = nomClient;
    }

    public String getDestinationVoyage() {
        return destinationVoyage;
    }

    public void setDestinationVoyage(String destinationVoyage) {
        this.destinationVoyage = destinationVoyage;
    }

    public String getTitreVoyage() {
        return titreVoyage;
    }

    public void setTitreVoyage(String titreVoyage) {
        this.titreVoyage = titreVoyage;
    }

    @Override
    public String toString() {
        return "ReservationVoyage{" +
                "id=" + id +
                ", idUtilisateur=" + idUtilisateur +
                ", idVoyage=" + idVoyage +
                ", dateReservation=" + dateReservation +
                ", statut=" + statut +
                ", nbrPersonnes=" + nbrPersonnes +
                ", montantTotal=" + montantTotal +
                ", nomClient='" + nomClient + '\'' +
                ", destinationVoyage='" + destinationVoyage + '\'' +
                '}';
    }
}