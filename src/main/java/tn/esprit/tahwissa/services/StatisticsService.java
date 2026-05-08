package tn.esprit.tahwissa.services;

import tn.esprit.tahwissa.dao.StatisticsDAO;
import tn.esprit.tahwissa.dto.EventReactionStat;
import tn.esprit.tahwissa.dto.GlobalStats;
import tn.esprit.tahwissa.dto.ReactionCountByDate;

import java.util.List;

public class StatisticsService {
    private final StatisticsDAO statisticsDAO = new StatisticsDAO();

    public GlobalStats getGlobalStats() {
        return statisticsDAO.loadGlobalStats();
    }

    public List<EventReactionStat> getReactionsByEvent() {
        return statisticsDAO.loadReactionsByEvent();
    }

    public List<ReactionCountByDate> getReactionsByDate(int lastDays) {
        return statisticsDAO.loadReactionsByDate(lastDays);
    }
}
