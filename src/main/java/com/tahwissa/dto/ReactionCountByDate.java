package com.tahwissa.dto;

import java.time.LocalDate;

public final class ReactionCountByDate {
    private final LocalDate date;
    private final long count;

    public ReactionCountByDate(LocalDate date, long count) {
        this.date = date;
        this.count = count;
    }

    public LocalDate getDate() { return date; }
    public long getCount() { return count; }
}
