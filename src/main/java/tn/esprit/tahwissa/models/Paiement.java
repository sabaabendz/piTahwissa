package tn.esprit.tahwissa.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Paiement Model - Maps to 'paiement' table in database
 */
public class Paiement {
    private int id;
    private int idReservation;
    private int idUtilisateur;
    private BigDecimal montant;
    private LocalDateTime datePaiement;
    private String methodePaiement; // CARTE, VIREMENT, ESPECES, PAYPAL
    private StatutPaiement statut;
    private String reference;
    private LocalDateTime createdAt;

    // For display purposes - joined data
    private String destinationVoyage;
    private String titreVoyage;

    public enum StatutPaiement {
        PAYE("PAYE", "Payé"),
        EN_ATTENTE("EN_ATTENTE", "En attente"),
        REMBOURSE("REMBOURSE", "Remboursé"),
        ECHOUE("ECHOUE", "Échoué");

        private final String code;
        private final String label;

        StatutPaiement(String code, String label) {
            this.code = code;
            this.label = label;
        }

        public String getCode() { return code; }
        public String getLabel() { return label; }

        public static StatutPaiement fromCode(String code) {
            for (StatutPaiement s : values()) {
                if (s.code.equals(code)) return s;
            }
            return EN_ATTENTE;
        }
    }

    // Constructors
    public Paiement() {}

    public Paiement(int id, int idReservation, int idUtilisateur, BigDecimal montant,
                    LocalDateTime datePaiement, String methodePaiement, StatutPaiement statut, String reference) {
        this.id = id;
        this.idReservation = idReservation;
        this.idUtilisateur = idUtilisateur;
        this.montant = montant;
        this.datePaiement = datePaiement;
        this.methodePaiement = methodePaiement;
        this.statut = statut;
        this.reference = reference;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdReservation() { return idReservation; }
    public void setIdReservation(int idReservation) { this.idReservation = idReservation; }

    public int getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(int idUtilisateur) { this.idUtilisateur = idUtilisateur; }

    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }

    public LocalDateTime getDatePaiement() { return datePaiement; }
    public void setDatePaiement(LocalDateTime datePaiement) { this.datePaiement = datePaiement; }

    public String getMethodePaiement() { return methodePaiement; }
    public void setMethodePaiement(String methodePaiement) { this.methodePaiement = methodePaiement; }

    public StatutPaiement getStatut() { return statut; }
    public void setStatut(StatutPaiement statut) { this.statut = statut; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getDestinationVoyage() { return destinationVoyage; }
    public void setDestinationVoyage(String destinationVoyage) { this.destinationVoyage = destinationVoyage; }

    public String getTitreVoyage() { return titreVoyage; }
    public void setTitreVoyage(String titreVoyage) { this.titreVoyage = titreVoyage; }

    @Override
    public String toString() {
        return "Paiement{" +
                "id=" + id +
                ", idReservation=" + idReservation +
                ", montant=" + montant +
                ", datePaiement=" + datePaiement +
                ", methodePaiement='" + methodePaiement + '\'' +
                ", statut=" + statut +
                ", reference='" + reference + '\'' +
                '}';
    }
}
