package com.tahwissa.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class Evenement {
    private int idEvenement;
    private String titre;
    private String description;
    private String lieu;
    private LocalDate dateEvent;
    private LocalTime heureEvent;
    private double prix;
    private int nbPlaces;
    private String categorie;
    private String statut;
    private LocalDateTime dateCreation;

    public Evenement() {}

    public Evenement(int idEvenement, String titre, String description, String lieu, 
                     LocalDate dateEvent, LocalTime heureEvent, double prix, int nbPlaces, 
                     String categorie, String statut, LocalDateTime dateCreation) {
        this.idEvenement = idEvenement;
        this.titre = titre;
        this.description = description;
        this.lieu = lieu;
        this.dateEvent = dateEvent;
        this.heureEvent = heureEvent;
        this.prix = prix;
        this.nbPlaces = nbPlaces;
        this.categorie = categorie;
        this.statut = statut;
        this.dateCreation = dateCreation;
    }

    public Evenement(String titre, String description, String lieu, LocalDate dateEvent, 
                     LocalTime heureEvent, double prix, int nbPlaces, String categorie, String statut) {
        this.titre = titre;
        this.description = description;
        this.lieu = lieu;
        this.dateEvent = dateEvent;
        this.heureEvent = heureEvent;
        this.prix = prix;
        this.nbPlaces = nbPlaces;
        this.categorie = categorie;
        this.statut = statut;
    }

    // Getters and Setters
    public int getIdEvenement() {
        return idEvenement;
    }

    public void setIdEvenement(int idEvenement) {
        this.idEvenement = idEvenement;
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

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public LocalDate getDateEvent() {
        return dateEvent;
    }

    public void setDateEvent(LocalDate dateEvent) {
        this.dateEvent = dateEvent;
    }

    public LocalTime getHeureEvent() {
        return heureEvent;
    }

    public void setHeureEvent(LocalTime heureEvent) {
        this.heureEvent = heureEvent;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public int getNbPlaces() {
        return nbPlaces;
    }

    public void setNbPlaces(int nbPlaces) {
        this.nbPlaces = nbPlaces;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
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

    @Override
    public String toString() {
        return "Evenement{" +
                "idEvenement=" + idEvenement +
                ", titre='" + titre + '\'' +
                ", lieu='" + lieu + '\'' +
                ", dateEvent=" + dateEvent +
                ", prix=" + prix +
                ", nbPlaces=" + nbPlaces +
                '}';
    }
}
