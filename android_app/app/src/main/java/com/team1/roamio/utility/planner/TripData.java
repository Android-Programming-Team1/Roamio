package com.team1.roamio.utility.planner;
//전체 데이터 래퍼
import java.util.List;

public class TripData {
    private String date;
    private String title;
    private List<ScheduleItem> schedule;

    // Setter
    public void setDate(String date) { this.date = date; }
    public void setTitle(String title) { this.title = title; }

    // Getters
    public String getDate() { return date; }
    public String getTitle() { return title; }
    public List<ScheduleItem> getSchedule() { return schedule; }
}