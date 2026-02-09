package services;

import entites.Transport;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransportService implements ITransportService {

    private Connection connection;

    public TransportService() {
        connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void ajouterTransport(Transport t) throws SQLException {
        String sql = "INSERT INTO transport(type_transport, ville_depart, ville_arrivee, date_depart, heure_depart, duree, prix, nb_places) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, t.getTypeTransport());
            ps.setString(2, t.getVilleDepart());
            ps.setString(3, t.getVilleArrivee());
            ps.setDate(4, t.getDateDepart());
            ps.setTime(5, t.getHeureDepart());
            ps.setInt(6, t.getDuree());
            ps.setDouble(7, t.getPrix());
            ps.setInt(8, t.getNbPlaces());
            ps.executeUpdate();
            System.out.println("✅ Transport ajouté avec succès !");
        }
    }

    @Override
    public void modifierTransport(Transport t) throws SQLException {
        String sql = "UPDATE transport SET type_transport=?, ville_depart=?, ville_arrivee=?, date_depart=?, heure_depart=?, duree=?, prix=?, nb_places=? " +
                "WHERE id_transport=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, t.getTypeTransport());
            ps.setString(2, t.getVilleDepart());
            ps.setString(3, t.getVilleArrivee());
            ps.setDate(4, t.getDateDepart());
            ps.setTime(5, t.getHeureDepart());
            ps.setInt(6, t.getDuree());
            ps.setDouble(7, t.getPrix());
            ps.setInt(8, t.getNbPlaces());
            ps.setInt(9, t.getIdTransport());
            ps.executeUpdate();
            System.out.println("✅ Transport modifié avec succès !");
        }
    }

    @Override
    public void supprimerTransport(int idTransport) throws SQLException {
        String sql = "DELETE FROM transport WHERE id_transport=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idTransport);
            ps.executeUpdate();
            System.out.println("✅ Transport supprimé avec succès !");
        }
    }

    @Override
    public List<Transport> recupererTransport() throws SQLException {
        List<Transport> transports = new ArrayList<>();
        String sql = "SELECT * FROM transport";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Transport t = new Transport();
                t.setIdTransport(rs.getInt("id_transport"));
                t.setTypeTransport(rs.getString("type_transport"));
                t.setVilleDepart(rs.getString("ville_depart"));
                t.setVilleArrivee(rs.getString("ville_arrivee"));
                t.setDateDepart(rs.getDate("date_depart"));
                t.setHeureDepart(rs.getTime("heure_depart"));
                t.setDuree(rs.getInt("duree"));
                t.setPrix(rs.getDouble("prix"));
                t.setNbPlaces(rs.getInt("nb_places"));

                transports.add(t);
            }
        }

        return transports;
    }
}

