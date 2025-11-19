package com.team1.roamio.utility.planner;
//개별 일정 항목
public class ScheduleItem {
    private String type; // "location" or "transport"
    private String description;

    public ScheduleItem(String type, String description) {
        this.type = type;
        this.description = description;
    }

    // Getters and Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}