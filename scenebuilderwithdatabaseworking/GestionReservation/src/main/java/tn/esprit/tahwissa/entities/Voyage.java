package tn.esprit.tahwissa.entities;

import java.util.Date;

/**
 * Represents a travel package/voyage in the catalog
 * This is the PRODUCT that clients can book
 */
public class Voyage {
    // DATABASE FIELDS
    private int id;
    private String titre;              // "Paris 5 jours"
    private String description;        // Details about the voyage
    private String destination;        // "Paris"
    private String categorie;          // "City", "Beach", "Mountain"
    private double prixUnitaire;       // Price PER PERSON (500 DT)
    private Date dateDepart;           // Start date
    private Date dateRetour;           // End date
    private int placesDisponibles;     // How many seats left?
    private String imageUrl;           // Link to image
    private String statut;             // "ACTIF" or "INACTIF"
    private Date createdAt;
    private Date updatedAt;

    // ════════════════════ CONSTRUCTORS ════════════════════

    public Voyage() {
        this.statut = "ACTIF";
        this.createdAt = new Date();
    }

    public Voyage(String titre, String destination, double prixUnitaire,
                  Date dateDepart, Date dateRetour, int placesDisponibles) {
        this.titre = titre;
        this.destination = destination;
        this.prixUnitaire = prixUnitaire;
        this.dateDepart = dateDepart;
        this.dateRetour = dateRetour;
        this.placesDisponibles = placesDisponibles;
        this.statut = "ACTIF";
        this.createdAt = new Date();
    }

    // ════════════════════ GETTERS & SETTERS ════════════════════

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public double getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(double prixUnitaire) { this.prixUnitaire = prixUnitaire; }

    public Date getDateDepart() { return dateDepart; }
    public void setDateDepart(Date dateDepart) { this.dateDepart = dateDepart; }

    public Date getDateRetour() { return dateRetour; }
    public void setDateRetour(Date dateRetour) { this.dateRetour = dateRetour; }

    public int getPlacesDisponibles() { return placesDisponibles; }
    public void setPlacesDisponibles(int placesDisponibles) { this.placesDisponibles = placesDisponibles; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Voyage{" +
            "id=" + id +
            ", titre='" + titre + '\'' +
            ", destination='" + destination + '\'' +
            ", prixUnitaire=" + prixUnitaire +
            ", placesDisponibles=" + placesDisponibles +
            ", statut='" + statut + '\'' +
            '}';
    }
}
