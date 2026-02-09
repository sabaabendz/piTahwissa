package entites;

import java.sql.Date;
import java.sql.Time;

public class Transport {

    private int idTransport;
    private String typeTransport;
    private String villeDepart;
    private String villeArrivee;
    private Date dateDepart;
    private Time heureDepart;
    private int duree;
    private double prix;
    private int nbPlaces;

    // constructeur vide
    public Transport() {}

    // constructeur sans id
    public Transport(String typeTransport, String villeDepart, String villeArrivee,
                     Date dateDepart, Time heureDepart,
                     int duree, double prix, int nbPlaces) {

        this.typeTransport = typeTransport;
        this.villeDepart = villeDepart;
        this.villeArrivee = villeArrivee;
        this.dateDepart = dateDepart;
        this.heureDepart = heureDepart;
        this.duree = duree;
        this.prix = prix;
        this.nbPlaces = nbPlaces;
    }

    // constructeur complet
    public Transport(int idTransport, String typeTransport, String villeDepart, String villeArrivee,
                     Date dateDepart, Time heureDepart,
                     int duree, double prix, int nbPlaces) {

        this.idTransport = idTransport;
        this.typeTransport = typeTransport;
        this.villeDepart = villeDepart;
        this.villeArrivee = villeArrivee;
        this.dateDepart = dateDepart;
        this.heureDepart = heureDepart;
        this.duree = duree;
        this.prix = prix;
        this.nbPlaces = nbPlaces;
    }

    // getters & setters
    public int getIdTransport() { return idTransport; }
    public void setIdTransport(int idTransport) { this.idTransport = idTransport; }

    public String getTypeTransport() { return typeTransport; }
    public void setTypeTransport(String typeTransport) { this.typeTransport = typeTransport; }

    public String getVilleDepart() { return villeDepart; }
    public void setVilleDepart(String villeDepart) { this.villeDepart = villeDepart; }

    public String getVilleArrivee() { return villeArrivee; }
    public void setVilleArrivee(String villeArrivee) { this.villeArrivee = villeArrivee; }

    public Date getDateDepart() { return dateDepart; }
    public void setDateDepart(Date dateDepart) { this.dateDepart = dateDepart; }

    public Time getHeureDepart() { return heureDepart; }
    public void setHeureDepart(Time heureDepart) { this.heureDepart = heureDepart; }

    public int getDuree() { return duree; }
    public void setDuree(int duree) { this.duree = duree; }

    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    public int getNbPlaces() { return nbPlaces; }
    public void setNbPlaces(int nbPlaces) { this.nbPlaces = nbPlaces; }

    @Override
    public String toString() {
        return "Transport{" +
                "id=" + idTransport +
                ", type='" + typeTransport + '\'' +
                ", depart='" + villeDepart + '\'' +
                ", arrivee='" + villeArrivee + '\'' +
                ", date=" + dateDepart +
                ", heure=" + heureDepart +
                ", prix=" + prix +
                '}';
    }
}
