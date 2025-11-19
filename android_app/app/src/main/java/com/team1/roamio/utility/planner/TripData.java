package com.team1.roamio.utility.planner;
//전체 데이터 래퍼
import java.util.List;

public class TripData {
    private String date;
    private String title;
    private List<ScheduleItem> schedule;

    // Getters
    public String getDate() { return date; }
    public String getTitle() { return title; }
    public List<ScheduleItem> getSchedule() { return schedule; }
}