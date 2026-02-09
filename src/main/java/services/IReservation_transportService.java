package services;

import entites.Reservation_transport;

import java.sql.SQLException;
import java.util.List;

public interface IReservation_transportService {

    // Ajouter une réservation
    void ajouterReservation(Reservation_transport r) throws SQLException;

    // Modifier une réservation
    void modifierReservation(Reservation_transport r) throws SQLException;

    // Supprimer une réservation
    void supprimerReservation(Reservation_transport r) throws SQLException;

    // Récupérer toutes les réservations
    List<Reservation_transport> recupererReservations() throws SQLException;
}

