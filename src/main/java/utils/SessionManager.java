package utils;

import entities.User;

/**
 * Gestionnaire de session utilisateur
 * Conserve les informations de l'utilisateur connecté dans toute l'application
 */
public class SessionManager {
    
    private static SessionManager instance;
    private User currentUser;
    
    private SessionManager() {
        // Constructeur privé pour le pattern Singleton
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
     * Définir l'utilisateur connecté
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        System.out.println("🔑 Session: Utilisateur connecté - " + 
                          (user != null ? user.getEmail() + " (" + user.getRole() + ")" : "null"));
    }
    
    /**
     * Obtenir l'utilisateur connecté
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Vérifier si un utilisateur est connecté
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Obtenir le rôle de l'utilisateur connecté
     */
    public String getCurrentRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }
    
    /**
     * Vérifier si l'utilisateur a un rôle spécifique
     */
    public boolean hasRole(String role) {
        return currentUser != null && 
               currentUser.getRole() != null && 
               currentUser.getRole().equalsIgnoreCase(role);
    }
    
    /**
     * Vérifier si l'utilisateur est ADMIN
     */
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }
    
    /**
     * Vérifier si l'utilisateur est AGENT
     */
    public boolean isAgent() {
        return hasRole("AGENT");
    }
    
    /**
     * Vérifier si l'utilisateur est ADMIN ou AGENT
     */
    public boolean isAdminOrAgent() {
        return isAdmin() || isAgent();
    }
    
    /**
     * Déconnecter l'utilisateur
     */
    public void logout() {
        System.out.println("🚪 Session: Déconnexion de " + 
                          (currentUser != null ? currentUser.getEmail() : "inconnu"));
        currentUser = null;
    }
    
    /**
     * Obtenir l'email de l'utilisateur connecté
     */
    public String getCurrentUserEmail() {
        return currentUser != null ? currentUser.getEmail() : null;
    }
    
    /**
     * Obtenir le nom complet de l'utilisateur connecté
     */
    public String getCurrentUserFullName() {
        if (currentUser != null) {
            return currentUser.getFirstName() + " " + currentUser.getLastName();
        }
        return null;
    }
}
