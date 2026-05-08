package tn.esprit.tahwissa.models;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Destination {
    private int idDestination;
    private String nom;
    private String pays;
    private String ville;
    private String description;
    private String imageUrl;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Constructeur vide
    public Destination() {}

    // Constructeur pour l'ajout (sans ID)
    public Destination(String nom, String pays, String ville, String description,
                       String imageUrl, BigDecimal latitude, BigDecimal longitude) {
        this.nom = nom;
        this.pays = pays;
        this.ville = ville;
        this.description = description;
        this.imageUrl = imageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Constructeur complet
    public Destination(int idDestination, String nom, String pays, String ville,
                       String description, String imageUrl, BigDecimal latitude,
                       BigDecimal longitude, Timestamp createdAt, Timestamp updatedAt) {
        this.idDestination = idDestination;
        this.nom = nom;
        this.pays = pays;
        this.ville = ville;
        this.description = description;
        this.imageUrl = imageUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters et Setters
    public int getIdDestination() { return idDestination; }
    public void setIdDestination(int idDestination) { this.idDestination = idDestination; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Destination{" +
                "id=" + idDestination +
                ", nom='" + nom + '\'' +
                ", pays='" + pays + '\'' +
                ", ville='" + ville + '\'' +
                '}';
    }
}