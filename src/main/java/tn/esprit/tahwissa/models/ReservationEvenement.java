package tn.esprit.tahwissa.models;

import java.time.LocalDateTime;

public class ReservationEvenement {
    private int idReservation;
    private LocalDateTime dateReservation;
    private int nbPlacesReservees;
    private String statut;
    private int idEvenement;
    private int idUser;
    
    // Pour l'affichage
    private String titreEvenement;
    private String nomUser;

    public ReservationEvenement() {}

    public ReservationEvenement(int idReservation, LocalDateTime dateReservation, 
                               int nbPlacesReservees, String statut, int idEvenement, int idUser) {
        this.idReservation = idReservation;
        this.dateReservation = dateReservation;
        this.nbPlacesReservees = nbPlacesReservees;
        this.statut = statut;
        this.idEvenement = idEvenement;
        this.idUser = idUser;
    }

    public ReservationEvenement(LocalDateTime dateReservation, int nbPlacesReservees, 
                               String statut, int idEvenement, int idUser) {
        this.dateReservation = dateReservation;
        this.nbPlacesReservees = nbPlacesReservees;
        this.statut = statut;
        this.idEvenement = idEvenement;
        this.idUser = idUser;
    }

    // Getters and Setters
    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    public LocalDateTime getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(LocalDateTime dateReservation) {
        this.dateReservation = dateReservation;
    }

    public int getNbPlacesReservees() {
        return nbPlacesReservees;
    }

    public void setNbPlacesReservees(int nbPlacesReservees) {
        this.nbPlacesReservees = nbPlacesReservees;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getIdEvenement() {
        return idEvenement;
    }

    public void setIdEvenement(int idEvenement) {
        this.idEvenement = idEvenement;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getTitreEvenement() {
        return titreEvenement;
    }

    public void setTitreEvenement(String titreEvenement) {
        this.titreEvenement = titreEvenement;
    }

    public String getNomUser() {
        return nomUser;
    }

    public void setNomUser(String nomUser) {
        this.nomUser = nomUser;
    }

    @Override
    public String toString() {
        return "ReservationEvenement{" +
                "idReservation=" + idReservation +
                ", dateReservation=" + dateReservation +
                ", nbPlacesReservees=" + nbPlacesReservees +
                ", statut='" + statut + '\'' +
                '}';
    }
}
