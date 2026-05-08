package tn.esprit.tahwissa.services;

import tn.esprit.tahwissa.dao.EvenementDAO;
import tn.esprit.tahwissa.dao.ReservationEvenementDAO;
import tn.esprit.tahwissa.models.ReservationEvenement;
import tn.esprit.tahwissa.utils.SessionManager;

import java.util.List;

public class ReservationService {
    private final ReservationEvenementDAO reservationDAO = new ReservationEvenementDAO();
    private final EvenementDAO evenementDAO = new EvenementDAO();

    public boolean createReservation(ReservationEvenement reservation) {
        return reservationDAO.create(reservation);
    }

    public boolean updateReservation(ReservationEvenement reservation) {
        return reservationDAO.update(reservation);
    }

    public boolean deleteReservation(int id) {
        return reservationDAO.delete(id);
    }

    public List<ReservationEvenement> getAllReservations() {
        if (SessionManager.getInstance().isAdmin()) {
            return reservationDAO.findAll();
        } else {
            return reservationDAO.findByUserId(SessionManager.getInstance().getCurrentUserId());
        }
    }

    public List<ReservationEvenement> getReservationsByUserId(int userId) {
        return reservationDAO.findByUserId(userId);
    }

    public List<ReservationEvenement> getReservationsByEvenementId(int evenementId) {
        return reservationDAO.findByEvenementId(evenementId);
    }

    public ReservationEvenement getReservationById(int id) {
        return reservationDAO.findById(id);
    }

    /**
     * Available places for an event (total capacity minus reserved CONFIRMEE + EN_ATTENTE).
     */
    public int getAvailablePlacesForEvent(int idEvenement) {
        var evenement = evenementDAO.findById(idEvenement);
        if (evenement == null) return 0;
        int reserved = reservationDAO.getReservedPlacesForEvent(idEvenement);
        return Math.max(0, evenement.getNbPlaces() - reserved);
    }
}
