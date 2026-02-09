package services;

import entites.Reservation_transport;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Reservation_transportService implements IReservation_transportService {

    private Connection connection;

    public Reservation_transportService() {
        connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void ajouterReservation(Reservation_transport r) throws SQLException {
        String sql = "INSERT INTO reservation_transport(date_reservation, nb_places_reservees, statut, id_transport, id_user) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, r.getDateReservation());
            ps.setInt(2, r.getNbPlacesReservees());
            ps.setString(3, r.getStatut());
            ps.setInt(4, r.getIdTransport());
            ps.setInt(5, r.getIdUser());
            ps.executeUpdate();
            System.out.println("✅ Réservation ajoutée avec succès !");
        }
    }

    @Override
    public void modifierReservation(Reservation_transport r) throws SQLException {
        String sql = "UPDATE reservation_transport SET date_reservation=?, nb_places_reservees=?, statut=?, id_transport=?, id_user=? " +
                "WHERE id_reservation=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, r.getDateReservation());
            ps.setInt(2, r.getNbPlacesReservees());
            ps.setString(3, r.getStatut());
            ps.setInt(4, r.getIdTransport());
            ps.setInt(5, r.getIdUser());
            ps.setInt(6, r.getIdReservation());
            ps.executeUpdate();
            System.out.println("✅ Réservation modifiée avec succès !");
        }
    }

    @Override
    public void supprimerReservation(Reservation_transport r) throws SQLException {
        String sql = "DELETE FROM reservation_transport WHERE id_reservation=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, r.getIdReservation());
            ps.executeUpdate();
            System.out.println("✅ Réservation supprimée avec succès !");
        }
    }

    @Override
    public List<Reservation_transport> recupererReservations() throws SQLException {
        List<Reservation_transport> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservation_transport";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Reservation_transport r = new Reservation_transport();
                r.setIdReservation(rs.getInt("id_reservation"));
                r.setDateReservation(rs.getDate("date_reservation"));
                r.setNbPlacesReservees(rs.getInt("nb_places_reservees"));
                r.setStatut(rs.getString("statut"));
                r.setIdTransport(rs.getInt("id_transport"));
                r.setIdUser(rs.getInt("id_user"));

                reservations.add(r);
            }
        }

        return reservations;
    }
}

