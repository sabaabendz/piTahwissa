package tn.esprit.tahwissa.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Voyage Model - Maps to 'voyage' table in database
 */
public class Voyage {
    private int id;
    private String titre;
    private String description;
    private String destination;
    private String categorie;
    private BigDecimal prixUnitaire;
    private LocalDate dateDepart;
    private LocalDate dateRetour;
    private int placesDisponibles;
    private String imageUrl;
    private String statut;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public Voyage() {
    }

    public Voyage(int id, String titre, String description, String destination,
                  String categorie, BigDecimal prixUnitaire, LocalDate dateDepart,
                  LocalDate dateRetour, int placesDisponibles, String imageUrl,
                  String statut) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.destination = destination;
        this.categorie = categorie;
        this.prixUnitaire = prixUnitaire;
        this.dateDepart = dateDepart;
        this.dateRetour = dateRetour;
        this.placesDisponibles = placesDisponibles;
        this.imageUrl = imageUrl;
        this.statut = statut;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public LocalDate getDateDepart() {
        return dateDepart;
    }

    public void setDateDepart(LocalDate dateDepart) {
        this.dateDepart = dateDepart;
    }

    public LocalDate getDateRetour() {
        return dateRetour;
    }

    public void setDateRetour(LocalDate dateRetour) {
        this.dateRetour = dateRetour;
    }

    public int getPlacesDisponibles() {
        return placesDisponibles;
    }

    public void setPlacesDisponibles(int placesDisponibles) {
        this.placesDisponibles = placesDisponibles;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Voyage{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", destination='" + destination + '\'' +
                ", categorie='" + categorie + '\'' +
                ", prixUnitaire=" + prixUnitaire +
                ", dateDepart=" + dateDepart +
                ", dateRetour=" + dateRetour +
                ", placesDisponibles=" + placesDisponibles +
                ", statut='" + statut + '\'' +
                '}';
    }
}