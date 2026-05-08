package tn.esprit.tahwissa.services;

import tn.esprit.tahwissa.dao.EvenementDAO;
import tn.esprit.tahwissa.models.Evenement;

import java.util.List;

public class EvenementService {
    private final EvenementDAO evenementDAO = new EvenementDAO();

    public boolean createEvenement(Evenement evenement) {
        return evenementDAO.create(evenement);
    }

    public boolean updateEvenement(Evenement evenement) {
        return evenementDAO.update(evenement);
    }

    public boolean deleteEvenement(int id) {
        return evenementDAO.delete(id);
    }

    public List<Evenement> getAllEvenements() {
        return evenementDAO.findAll();
    }

    public List<Evenement> getAvailableEvenements() {
        return evenementDAO.findAvailable();
    }

    public Evenement getEvenementById(int id) {
        return evenementDAO.findById(id);
    }

    public List<Evenement> searchEvenements(String keyword) {
        return evenementDAO.searchByKeyword(keyword);
    }
}
