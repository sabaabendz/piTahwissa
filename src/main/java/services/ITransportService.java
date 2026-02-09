package services;

import entites.Transport;

import java.sql.SQLException;
import java.util.List;

public interface ITransportService {

    // Ajouter un transport
    void ajouterTransport(Transport t) throws SQLException;

    // Modifier un transport
    void modifierTransport(Transport t) throws SQLException;

    // Supprimer un transport
    void supprimerTransport(int idTransport) throws SQLException;

    // Récupérer tous les transports
    List<Transport> recupererTransport() throws SQLException;
}

