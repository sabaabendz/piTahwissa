package tn.esprit.tahwissa.dto;

public final class GlobalStats {
    private final int totalUsers;
    private final int totalEvents;
    private final int totalReservations;
    private final int totalReclamations;
    private final int totalLikes;
    private final int totalDislikes;

    public GlobalStats(int totalUsers, int totalEvents, int totalReservations,
                       int totalReclamations, int totalLikes, int totalDislikes) {
        this.totalUsers = totalUsers;
        this.totalEvents = totalEvents;
        this.totalReservations = totalReservations;
        this.totalReclamations = totalReclamations;
        this.totalLikes = totalLikes;
        this.totalDislikes = totalDislikes;
    }

    public int getTotalUsers() { return totalUsers; }
    public int getTotalEvents() { return totalEvents; }
    public int getTotalReservations() { return totalReservations; }
    public int getTotalReclamations() { return totalReclamations; }
    public int getTotalLikes() { return totalLikes; }
    public int getTotalDislikes() { return totalDislikes; }
}
