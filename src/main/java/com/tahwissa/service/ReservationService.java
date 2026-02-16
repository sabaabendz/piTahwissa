package com.tahwissa.service;

import com.tahwissa.dao.ReservationEvenementDAO;
import com.tahwissa.entity.ReservationEvenement;
import com.tahwissa.utils.SessionManager;

import java.util.List;

public class ReservationService {
    private final ReservationEvenementDAO reservationDAO = new ReservationEvenementDAO();

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
}
