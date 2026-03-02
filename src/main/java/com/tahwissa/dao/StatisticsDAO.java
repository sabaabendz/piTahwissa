package com.tahwissa.dao;

import com.tahwissa.dto.EventReactionStat;
import com.tahwissa.dto.GlobalStats;
import com.tahwissa.utils.DBConnection;

import com.tahwissa.dto.ReactionCountByDate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StatisticsDAO {

    public GlobalStats loadGlobalStats() {
        int users = countTable("utilisateur");
        int events = countTable("evenement");
        int reservations = countTable("reservation_evenement");
        int reclamations = countTable("reclamation");
        int likes = countReactionsByType("LIKE");
        int dislikes = countReactionsByType("DISLIKE");
        return new GlobalStats(users, events, reservations, reclamations, likes, dislikes);
    }

    public List<EventReactionStat> loadReactionsByEvent() {
        List<EventReactionStat> list = new ArrayList<>();
        String sql = "SELECT e.id_evenement, e.titre, " +
                     "COALESCE(SUM(CASE WHEN r.type = 'LIKE' THEN 1 ELSE 0 END), 0) AS likes, " +
                     "COALESCE(SUM(CASE WHEN r.type = 'DISLIKE' THEN 1 ELSE 0 END), 0) AS dislikes " +
                     "FROM evenement e " +
                     "LEFT JOIN evenement_reaction r ON e.id_evenement = r.id_evenement " +
                     "GROUP BY e.id_evenement, e.titre " +
                     "ORDER BY e.titre";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new EventReactionStat(
                        rs.getInt("id_evenement"),
                        rs.getString("titre"),
                        rs.getLong("likes"),
                        rs.getLong("dislikes")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ReactionCountByDate> loadReactionsByDate(int lastDays) {
        List<ReactionCountByDate> result = new ArrayList<>();
        String sql = "SELECT DATE(date_creation) AS d, COUNT(*) AS c FROM evenement_reaction " +
                     "WHERE date_creation >= DATE_SUB(CURDATE(), INTERVAL ? DAY) " +
                     "GROUP BY DATE(date_creation) ORDER BY d ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, lastDays);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(new ReactionCountByDate(
                        rs.getDate("d").toLocalDate(),
                        rs.getLong("c")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private int countTable(String tableName) {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int countReactionsByType(String type) {
        String sql = "SELECT COUNT(*) FROM evenement_reaction WHERE type = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type);
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
