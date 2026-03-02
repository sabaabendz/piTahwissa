package com.tahwissa.service;

import com.tahwissa.dao.StatisticsDAO;
import com.tahwissa.dto.EventReactionStat;
import com.tahwissa.dto.GlobalStats;
import com.tahwissa.dto.ReactionCountByDate;

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
