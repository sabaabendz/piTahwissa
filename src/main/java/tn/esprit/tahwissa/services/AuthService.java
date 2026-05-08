package tn.esprit.tahwissa.services;

import tn.esprit.tahwissa.dao.UtilisateurDAO;
import tn.esprit.tahwissa.models.User;
import tn.esprit.tahwissa.utils.PasswordUtils;
import tn.esprit.tahwissa.utils.SessionManager;

public class AuthService {
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    public User login(String email, String password) {
        User user = utilisateurDAO.findByEmail(email);
        
        if (user != null && PasswordUtils.verifyPassword(password, user.getPassword())) {
            SessionManager.getInstance().setCurrentUser(user);
            return user;
        }
        return null;
    }

    public boolean register(String nom, String email, String password) {
        // Vérifier si l'email existe déjà
        if (utilisateurDAO.findByEmail(email) != null) {
            return false;
        }

        // Hasher le mot de passe
        String hashedPassword = PasswordUtils.hashPassword(password);

        // Créer un nouvel utilisateur avec le rôle USER
       // User newUser = new User(nom, email, hashedPassword, "USER");
        User newUser = new User(nom, email , "USER");

        return utilisateurDAO.create(newUser);
    }

    public void logout() {
        SessionManager.getInstance().logout();
    }
}
