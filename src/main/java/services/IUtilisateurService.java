package services;

import entites.Utilisateur;

import java.sql.SQLException;
import java.util.List;

public interface IUtilisateurService {

    // Ajouter un utilisateur
    void ajouterUtilisateur(Utilisateur u) throws SQLException;

    // Modifier un utilisateur
    void modifierUtilisateur(Utilisateur u) throws SQLException;

    // Supprimer un utilisateur
    void supprimerUtilisateur(int idUser) throws SQLException;

    // Récupérer tous les utilisateurs
    List<Utilisateur> recupererUtilisateurs() throws SQLException;
}

