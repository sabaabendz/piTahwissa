package com.tahwissa.service;

import com.tahwissa.dao.UtilisateurDAO;
import com.tahwissa.entity.Utilisateur;
import com.tahwissa.utils.PasswordUtils;
import com.tahwissa.utils.SessionManager;

public class AuthService {
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    public Utilisateur login(String email, String password) {
        Utilisateur user = utilisateurDAO.findByEmail(email);
        
        if (user != null && PasswordUtils.verifyPassword(password, user.getMotDePasse())) {
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
        Utilisateur newUser = new Utilisateur(nom, email, hashedPassword, "USER");
        
        return utilisateurDAO.create(newUser);
    }

    public void logout() {
        SessionManager.getInstance().logout();
    }
}
