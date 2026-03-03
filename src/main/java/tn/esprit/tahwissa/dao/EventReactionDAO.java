package tn.esprit.tahwissa.dao;

import tn.esprit.tahwissa.models.EventReaction;
import tn.esprit.tahwissa.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class EventReactionDAO {

    public boolean upsert(EventReaction reaction) {
        String sql = "INSERT INTO evenement_reaction (id_evenement, id_user, type, date_creation) VALUES (?, ?, ?, CURRENT_TIMESTAMP) " +
                     "ON DUPLICATE KEY UPDATE type = VALUES(type), date_creation = CURRENT_TIMESTAMP";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reaction.getIdEvenement());
            stmt.setInt(2, reaction.getIdUser());
            stmt.setString(3, reaction.getType());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int idEvenement, int idUser) {
        String sql = "DELETE FROM evenement_reaction WHERE id_evenement = ? AND id_user = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEvenement);
            stmt.setInt(2, idUser);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Optional<String> findTypeByEventAndUser(int idEvenement, int idUser) {
        String sql = "SELECT type FROM evenement_reaction WHERE id_evenement = ? AND id_user = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEvenement);
            stmt.setInt(2, idUser);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(rs.getString("type"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public int countByEventAndType(int idEvenement, String type) {
        String sql = "SELECT COUNT(*) FROM evenement_reaction WHERE id_evenement = ? AND type = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEvenement);
            stmt.setString(2, type);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
