package org.example.calenjax;

import java.time.LocalDateTime;

public class Event {

    private String uid;
    private String summary;
    private String description;
    private String location;
    private int startRow;
    private int startCol;
    private int rowSpan;

    public Event() {
        // Constructeur par défaut
    }

    public Event(String uid, String summary, String description, String location, int startRow, int startCol, int rowSpan) {
        this.uid = uid;
        this.summary = summary;
        this.description = description;
        this.location = location;
        this.startRow = startRow;
        this.startCol = startCol;
        this.rowSpan = rowSpan;
    }

    public String getSummary() {
        return summary;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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