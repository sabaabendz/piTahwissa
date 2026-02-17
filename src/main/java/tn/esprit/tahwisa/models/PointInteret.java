package tn.esprit.tahwisa.models;

import java.sql.Timestamp;

public class PointInteret {

    private int idPointInteret;
    private String nom;
    private String type;
    private String description;
    private String imageUrl;
    private int destinationId;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // ==================== CONSTRUCTEURS ====================

    // Constructeur vide
    public PointInteret() {}

    // Constructeur pour CRÉATION (sans ID, sans timestamps)
    public PointInteret(String nom, String type, String description,
                        String imageUrl, int destinationId) {
        this.nom = nom;
        this.type = type;
        this.description = description;
        this.imageUrl = imageUrl;
        this.destinationId = destinationId;
    }

    // Constructeur complet (pour lecture depuis BDD)
    public PointInteret(int idPointInteret, String nom, String type,
                        String description, String imageUrl, int destinationId,
                        Timestamp createdAt, Timestamp updatedAt) {
        this.idPointInteret = idPointInteret;
        this.nom = nom;
        this.type = type;
        this.description = description;
        this.imageUrl = imageUrl;
        this.destinationId = destinationId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ==================== GETTERS / SETTERS ====================

    public int getIdPointInteret() { return idPointInteret; }
    public void setIdPointInteret(int idPointInteret) { this.idPointInteret = idPointInteret; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getDestinationId() { return destinationId; }
    public void setDestinationId(int destinationId) { this.destinationId = destinationId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "PointInteret{id=" + idPointInteret + ", nom='" + nom + "', type='" + type + "'}";
    }
}