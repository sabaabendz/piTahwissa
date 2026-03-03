package tn.esprit.tahwissa.models;

import java.time.LocalDateTime;

public class EventReaction {
    public static final String TYPE_LIKE = "LIKE";
    public static final String TYPE_DISLIKE = "DISLIKE";

    private int idReaction;
    private int idEvenement;
    private int idUser;
    private String type;
    private LocalDateTime dateCreation;

    public EventReaction() {}

    public EventReaction(int idEvenement, int idUser, String type) {
        this.idEvenement = idEvenement;
        this.idUser = idUser;
        this.type = type;
    }

    public int getIdReaction() {
        return idReaction;
    }

    public void setIdReaction(int idReaction) {
        this.idReaction = idReaction;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
}
