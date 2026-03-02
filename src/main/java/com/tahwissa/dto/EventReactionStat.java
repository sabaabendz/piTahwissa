package com.tahwissa.dto;

public final class EventReactionStat {
    private final int idEvenement;
    private final String titreEvenement;
    private final long likeCount;
    private final long dislikeCount;

    public EventReactionStat(int idEvenement, String titreEvenement, long likeCount, long dislikeCount) {
        this.idEvenement = idEvenement;
        this.titreEvenement = titreEvenement;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
    }

    public int getIdEvenement() { return idEvenement; }
    public String getTitreEvenement() { return titreEvenement; }
    public long getLikeCount() { return likeCount; }
    public long getDislikeCount() { return dislikeCount; }
}
