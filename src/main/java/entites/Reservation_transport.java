package entites;

import java.sql.Date;

public class Reservation_transport {

    private int idReservation;
    private Date dateReservation;
    private int nbPlacesReservees;
    private String statut;
    private int idTransport;
    private int idUser;

    // constructeur vide
    public Reservation_transport() {}

    // constructeur sans id
    public Reservation_transport(Date dateReservation, int nbPlacesReservees,
                                 String statut, int idTransport, int idUser) {
        this.dateReservation = dateReservation;
        this.nbPlacesReservees = nbPlacesReservees;
        this.statut = statut;
        this.idTransport = idTransport;
        this.idUser = idUser;
    }

    // constructeur complet
    public Reservation_transport(int idReservation, Date dateReservation,
                                 int nbPlacesReservees, String statut,
                                 int idTransport, int idUser) {
        this.idReservation = idReservation;
        this.dateReservation = dateReservation;
        this.nbPlacesReservees = nbPlacesReservees;
        this.statut = statut;
        this.idTransport = idTransport;
        this.idUser = idUser;
    }

    // getters & setters

    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    public Date getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(Date dateReservation) {
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

    public int getIdTransport() {
        return idTransport;
    }

    public void setIdTransport(int idTransport) {
        this.idTransport = idTransport;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    @Override
    public String toString() {
        return "Reservation_transport{" +
                "id=" + idReservation +
                ", date=" + dateReservation +
                ", places=" + nbPlacesReservees +
                ", statut='" + statut + '\'' +
                '}';
    }
}
