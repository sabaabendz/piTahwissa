package com.tahwissa.entity;

import java.time.LocalDateTime;

public class Reclamation {
    private int idReclamation;
    private String titre;
    private String description;
    private String type;
    private String statut;
    private LocalDateTime dateCreation;
    private int idUser;
    private String nomUser; // Pour l'affichage

    public Reclamation() {}

    public Reclamation(int idReclamation, String titre, String description, String type, 
                      String statut, LocalDateTime dateCreation, int idUser) {
        this.idReclamation = idReclamation;
        this.titre = titre;
        this.description = description;
        this.type = type;
        this.statut = statut;
        this.dateCreation = dateCreation;
        this.idUser = idUser;
    }

    public Reclamation(String titre, String description, String type, String statut, 
                      LocalDateTime dateCreation, int idUser) {
        this.titre = titre;
        this.description = description;
        this.type = type;
        this.statut = statut;
        this.dateCreation = dateCreation;
        this.idUser = idUser;
    }

    // Getters and Setters
    public int getIdReclamation() {
        return idReclamation;
    }

    public void setIdReclamation(int idReclamation) {
        this.idReclamation = idReclamation;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getNomUser() {
        return nomUser;
    }

    public void setNomUser(String nomUser) {
        this.nomUser = nomUser;
    }

    @Override
    public String toString() {
        return "Reclamation{" +
                "idReclamation=" + idReclamation +
                ", titre='" + titre + '\'' +
                ", type='" + type + '\'' +
                ", statut='" + statut + '\'' +
                ", dateCreation=" + dateCreation +
                '}';
    }
}
