package tn.esprit.tahwissa.utils;

import tn.esprit.tahwissa.models.User;

/**
 * Gestionnaire de session utilisateur
 * Conserve les informations de l'utilisateur connectÃ© dans toute l'application
 */
public class SessionManager {
    
    private static SessionManager instance;
    private User currentUser;
    
    private SessionManager() {
        // Constructeur privÃ© pour le pattern Singleton
    }
    
    /**
     * Obtenir l'instance unique du SessionManager
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    /**
     * DÃ©finir l'utilisateur connectÃ©
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        System.out.println("ðŸ”‘ Session: Utilisateur connectÃ© - " + 
                          (user != null ? user.getEmail() + " (" + user.getRole() + ")" : "null"));
    }
    
    /**
     * Obtenir l'utilisateur connectÃ©
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * VÃ©rifier si un utilisateur est connectÃ©
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Obtenir le rÃ´le de l'utilisateur connectÃ©
     */
    public String getCurrentRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }
    
    /**
     * VÃ©rifier si l'utilisateur a un rÃ´le spÃ©cifique
     */
    public boolean hasRole(String role) {
        return currentUser != null && 
               currentUser.getRole() != null && 
               currentUser.getRole().equalsIgnoreCase(role);
    }
    
    /**
     * VÃ©rifier si l'utilisateur est ADMIN
     */
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }
    
    /**
     * VÃ©rifier si l'utilisateur est AGENT
     */
    public boolean isAgent() {
        return hasRole("AGENT");
    }
    
    /**
     * VÃ©rifier si l'utilisateur est ADMIN ou AGENT
     */
    public boolean isAdminOrAgent() {
        return isAdmin() || isAgent();
    }
    
    /**
     * DÃ©connecter l'utilisateur
     */
    public void logout() {
        System.out.println("ðŸšª Session: DÃ©connexion de " + 
                          (currentUser != null ? currentUser.getEmail() : "inconnu"));
        currentUser = null;
    }
    
    /**
     * Obtenir l'email de l'utilisateur connectÃ©
     */
    public String getCurrentUserEmail() {
        return currentUser != null ? currentUser.getEmail() : null;
    }
    
    /**
     * Obtenir le nom complet de l'utilisateur connectÃ©
     */
    public String getCurrentUserFullName() {
        if (currentUser != null) {
            return currentUser.getFirstName() + " " + currentUser.getLastName();
        }
        return null;
    }

    public int getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }

}

