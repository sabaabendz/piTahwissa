package tn.esprit.tahwissa.services;

import tn.esprit.tahwissa.dao.ReclamationDAO;
import tn.esprit.tahwissa.models.Reclamation;
import tn.esprit.tahwissa.utils.SessionManager;

import java.util.List;

public class ReclamationService {
    private final ReclamationDAO reclamationDAO = new ReclamationDAO();

    public boolean createReclamation(Reclamation reclamation) {
        return reclamationDAO.create(reclamation);
    }

    public boolean updateReclamation(Reclamation reclamation) {
        return reclamationDAO.update(reclamation);
    }

    public boolean deleteReclamation(int id) {
        return reclamationDAO.delete(id);
    }

    public List<Reclamation> getAllReclamations() {
        if (SessionManager.getInstance().isAdmin()) {
            return reclamationDAO.findAll();
        } else {
            return reclamationDAO.findByUserId(SessionManager.getInstance().getCurrentUserId());
        }
    }

    public List<Reclamation> getReclamationsByUserId(int userId) {
        return reclamationDAO.findByUserId(userId);
    }

    public Reclamation getReclamationById(int id) {
        return reclamationDAO.findById(id);
    }

    public List<Reclamation> searchReclamations(String keyword) {
        return reclamationDAO.searchByKeyword(keyword);
    }

    public List<Reclamation> getReclamationsByStatut(String statut) {
        return reclamationDAO.findByStatut(statut);
    }
}
