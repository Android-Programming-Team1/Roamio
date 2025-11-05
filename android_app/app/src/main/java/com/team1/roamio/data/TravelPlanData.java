// 파일명: TravelPlanData.java
package com.team1.roamio.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Gemini API로부터 받은 여행 계획 JSON 응답을 파싱하여 저장하는 메인 데이터 클래스입니다.
 */
public class TravelPlanData {

    private String planSummary;
    private int totalDays;
    private String country;
    private List<DailyPlan> dailyPlans;

    // 파싱 중 에러가 발생했을 때 에러 메시지를 담기 위한 필드
    private String error;

    /**
     * JSON 객체로부터 TravelPlanData 인스턴스를 생성하는 생성자입니다.
     * @param json 파싱할 메인 JSONObject
     * @throws JSONException JSON 구조가 예상과 다를 경우 발생
     */
    public TravelPlanData(JSONObject json) throws JSONException {
        this.planSummary = json.getString("plan_summary");
        this.totalDays = json.getInt("total_days");
        this.country = json.getString("country");

        this.dailyPlans = new ArrayList<>();
        JSONArray plansArray = json.getJSONArray("daily_plans");
        for (int i = 0; i < plansArray.length(); i++) {
            this.dailyPlans.add(new DailyPlan(plansArray.getJSONObject(i)));
        }
    }

    /**
     * 에러가 발생했을 때 사용하는 생성자입니다.
     * @param error 발생한 에러 메시지
     */
    public TravelPlanData(String error) {
        this.error = error;
        // 기본값 초기화
        this.dailyPlans = new ArrayList<>();
        this.planSummary = "";
        this.country = "";
        this.totalDays = 0;
    }

    /**
     * 이 객체가 에러 상태인지 확인합니다.
     * @return 에러가 있으면 true, 정상이면 false
     */
    public boolean hasError() {
        return this.error != null && !this.error.isEmpty();
    }


    public String getPlanSummary() { return planSummary; }

    public int getTotalDays() { return totalDays; }

    public String getCountry() { return country; }

    public List<DailyPlan> getDailyPlans() { return dailyPlans; }

    public String getError() { return error; }


    /**
     * 일자별 계획을 담는 내부 데이터 클래스
     */
    public static class DailyPlan {
        private int day;
        private String theme;
        private List<Activity> activities;

        public DailyPlan(JSONObject json) throws JSONException {
            this.day = json.getInt("day");
            this.theme = json.getString("theme");

            this.activities = new ArrayList<>();
            JSONArray activitiesArray = json.getJSONArray("activities");
            for (int i = 0; i < activitiesArray.length(); i++) {
                this.activities.add(new Activity(activitiesArray.getJSONObject(i)));
            }
        }

        // Getters
        public int getDay() { return day; }
        public String getTheme() { return theme; }
        public List<Activity> getActivities() { return activities; }
    }

    /**
     * 개별 활동을 담는 내부 데이터 클래스
     */
    public static class Activity {
        private String time;
        private String title;
        private String description;
        private String location;
        private Transport transport;

        public Activity(JSONObject json) throws JSONException {
            this.time = json.getString("time");
            this.title = json.getString("title");
            this.description = json.getString("description");
            this.location = json.getString("location");
            this.transport = new Transport(json.getJSONObject("transport"));
        }

        // Getters
        public String getTime() { return time; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getLocation() { return location; }
        public Transport getTransport() { return transport; }
    }

    /**
     * 이동 정보를 담는 내부 데이터 클래스
     */
    public static class Transport {
        private String from;
        private String to;
        private String estimatedTime;
        private String googleMapLink;

        public Transport(JSONObject json) throws JSONException {
            this.from = json.getString("from");
            this.to = json.getString("to");
            this.estimatedTime = json.getString("estimated_time");
            this.googleMapLink = json.getString("google_map_link");
        }

        // Getters
        public String getFrom() { return from; }
        public String getTo() { return to; }
        public String getEstimatedTime() { return estimatedTime; }
        public String getGoogleMapLink() { return googleMapLink; }
    }
}