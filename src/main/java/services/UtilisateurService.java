package services;

import entites.Utilisateur;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurService implements IUtilisateurService {

    private Connection connection;

    public UtilisateurService() {
        connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void ajouterUtilisateur(Utilisateur u) throws SQLException {
        String sql = "INSERT INTO utilisateur(nom, email, mot_de_passe, role) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, u.getNom());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getMotDePasse());
            ps.setString(4, u.getRole());
            ps.executeUpdate();
            System.out.println("✅ Utilisateur ajouté avec succès !");
        }
    }

    @Override
    public void modifierUtilisateur(Utilisateur u) throws SQLException {
        String sql = "UPDATE utilisateur SET nom=?, email=?, mot_de_passe=?, role=? WHERE id_user=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, u.getNom());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getMotDePasse());
            ps.setString(4, u.getRole());
            ps.setInt(5, u.getIdUser());
            ps.executeUpdate();
            System.out.println("✅ Utilisateur modifié avec succès !");
        }
    }

    @Override
    public void supprimerUtilisateur(int idUser) throws SQLException {
        String sql = "DELETE FROM utilisateur WHERE id_user=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            ps.executeUpdate();
            System.out.println("✅ Utilisateur supprimé avec succès !");
        }
    }

    @Override
    public List<Utilisateur> recupererUtilisateurs() throws SQLException {
        List<Utilisateur> users = new ArrayList<>();
        String sql = "SELECT * FROM utilisateur";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Utilisateur u = new Utilisateur();
                u.setIdUser(rs.getInt("id_user"));
                u.setNom(rs.getString("nom"));
                u.setEmail(rs.getString("email"));
                u.setMotDePasse(rs.getString("mot_de_passe"));
                u.setRole(rs.getString("role"));

                users.add(u);
            }
        }
        return users;
    }


}

