package org.example.calenjax;

import java.time.LocalDateTime;

public class Event {

    private String summary;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private int startRow;
    private int startCol;
    private int rowSpan;

    public Event() {
        // Constructeur par défaut
    }

    public Event(String summary, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.summary = summary;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public int getStartCol() {
        return startCol;
    }

    public void setStartCol(int startCol) {
        this.startCol = startCol;
    }

    public int getRowSpan() {
        return rowSpan;
    }

    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }
}