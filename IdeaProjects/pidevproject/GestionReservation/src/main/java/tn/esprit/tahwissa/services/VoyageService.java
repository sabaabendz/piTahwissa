package tn.esprit.tahwissa.services;

import tn.esprit.tahwissa.entities.Voyage;
import tn.esprit.tahwissa.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VoyageService implements IService<Voyage> {

    private Connection connection;

    public VoyageService() {
        this.connection = MyDatabase.getInstance().getConnection();
        System.out.println("✅ Service Voyage initialisé");
    }

    @Override
    public void ajouter(Voyage v) throws SQLException {
        String sql = "INSERT INTO voyage (titre, description, destination, categorie, prix_unitaire, " +
            "date_depart, date_retour, places_disponibles, image_url, statut) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, v.getTitre());
        ps.setString(2, v.getDescription());
        ps.setString(3, v.getDestination());
        ps.setString(4, v.getCategorie());
        ps.setDouble(5, v.getPrixUnitaire());
        ps.setDate(6, v.getDateDepart() != null ? new java.sql.Date(v.getDateDepart().getTime()) : null);
        ps.setDate(7, v.getDateRetour() != null ? new java.sql.Date(v.getDateRetour().getTime()) : null);
        ps.setInt(8, v.getPlacesDisponibles());
        ps.setString(9, v.getImageUrl());
        ps.setString(10, v.getStatut());
        ps.executeUpdate();
        System.out.println("📦 Voyage ajouté");
    }

    @Override
    public List<Voyage> recupererTous() throws SQLException {
        List<Voyage> liste = new ArrayList<>();
        String sql = "SELECT * FROM voyage ORDER BY date_depart";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            Voyage v = new Voyage();
            v.setId(rs.getInt("id"));
            v.setTitre(rs.getString("titre"));
            v.setDescription(rs.getString("description"));
            v.setDestination(rs.getString("destination"));
            v.setCategorie(rs.getString("categorie"));
            v.setPrixUnitaire(rs.getDouble("prix_unitaire"));
            v.setDateDepart(rs.getDate("date_depart"));
            v.setDateRetour(rs.getDate("date_retour"));
            v.setPlacesDisponibles(rs.getInt("places_disponibles"));
            v.setImageUrl(rs.getString("image_url"));
            v.setStatut(rs.getString("statut"));
            v.setCreatedAt(rs.getTimestamp("created_at"));
            v.setUpdatedAt(rs.getTimestamp("updated_at"));
            liste.add(v);
        }
        return liste;
    }

    @Override
    public void modifier(Voyage v) throws SQLException {
        String sql = "UPDATE voyage SET titre=?, description=?, destination=?, categorie=?, prix_unitaire=?, " +
            "date_depart=?, date_retour=?, places_disponibles=?, image_url=?, statut=? WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, v.getTitre());
        ps.setString(2, v.getDescription());
        ps.setString(3, v.getDestination());
        ps.setString(4, v.getCategorie());
        ps.setDouble(5, v.getPrixUnitaire());
        ps.setDate(6, v.getDateDepart() != null ? new java.sql.Date(v.getDateDepart().getTime()) : null);
        ps.setDate(7, v.getDateRetour() != null ? new java.sql.Date(v.getDateRetour().getTime()) : null);
        ps.setInt(8, v.getPlacesDisponibles());
        ps.setString(9, v.getImageUrl());
        ps.setString(10, v.getStatut());
        ps.setInt(11, v.getId());
        ps.executeUpdate();
        System.out.println("✏️ Voyage modifié");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM voyage WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("🗑️ Voyage supprimé");
    }
}
