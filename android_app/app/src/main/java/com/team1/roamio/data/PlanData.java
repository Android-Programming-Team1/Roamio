package com.team1.roamio.data;

import org.json.JSONArray;
import org.json.JSONObject;

public class PlanData {
    private JSONArray planDataJson; // TODO: 2025-11-03 유저의 커스텀 정보 ex) 체류 기간, 여행 취향, 숙소 위치 등등
    private JSONObject userCustomData;
    private PlanData() {
        planDataJson = new JSONArray();
        userCustomData = new JSONObject();
    }

    public static PlanData planDataBuilder() {
        return new PlanData();
    }

    // TODO: 2025-11-03 유저 커스텀 정보 setter(빌더 패턴)
    // 체류 기간
    public PlanData setStayDuration(int days){
        userCustomData.put("stay_duration", days);
        return this;
    }
    // 여행 취향
    public PlanData setPreference(String preference){
        userCustomData.put("travel_preference", preference);
        return this;
    }
    // 숙소 위치(좌표)
    /*
    public PlanData setHotelLocation(JSONObject location){
        userCustomData.put("hotel_location",location);
        return this;
    }
    */
    // 숙소 위치(String)
    public PlanData setHotelLocation(String location){
        userCustomData.put("hotel_location",location);
        return this;
    }
    // 일정
    public PlanData addPlan(JSONObject plan){
        planDataJson.put(plan);
        return this;
    }
    // 빌드
    public JSONObject build(){
        //TODO : 20251105 AI한테 prompting
    }
}