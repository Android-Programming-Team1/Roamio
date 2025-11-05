// 파일명: TravelPlanParser.java
package com.team1.roamio.utility.planner;

import com.team1.roamio.data.TravelPlanData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * TravelPlanData 객체와 JSON 간의 직렬화(Serialization) 및
 * 역직렬화(Deserialization)를 담당하는 유틸리티 클래스입니다.
 */
public class TravelPlanParser {

    /**
     * JSON 문자열을 파싱하여 TravelPlanData 객체로 변환합니다. (역직렬화)
     *
     * @param jsonString Gemini API로부터 받은 순수 JSON 문자열
     * @return 파싱된 TravelPlanData 객체
     * @throws JSONException JSON 구조가 잘못되었을 경우 발생
     */
    public static TravelPlanData parseJsonToPlanData(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);

        return new TravelPlanData(jsonObject);
    }

    /**
     * TravelPlanData 객체를 다시 JSONObject로 변환합니다. (직렬화)
     * (예: 나중에 데이터를 저장하거나 서버로 전송할 때 사용)
     *
     * @param planData 변환할 TravelPlanData 객체
     * @return JSONObject
     * @throws JSONException
     */
    public static JSONObject parsePlanDataToJson(TravelPlanData planData) throws JSONException {
        if (planData.hasError()) {
            JSONObject errorJson = new JSONObject();
            errorJson.put("error", planData.getError());
            return errorJson;
        }

        JSONObject json = new JSONObject();
        json.put("plan_summary", planData.getPlanSummary());
        json.put("total_days", planData.getTotalDays());
        json.put("country", planData.getCountry());

        JSONArray dailyPlansArray = new JSONArray();
        for (TravelPlanData.DailyPlan dailyPlan : planData.getDailyPlans()) {
            dailyPlansArray.put(parseDailyPlanToJson(dailyPlan));
        }
        json.put("daily_plans", dailyPlansArray);

        return json;
    }

    private static JSONObject parseDailyPlanToJson(TravelPlanData.DailyPlan dailyPlan) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("day", dailyPlan.getDay());
        json.put("theme", dailyPlan.getTheme());

        JSONArray activitiesArray = new JSONArray();
        for (TravelPlanData.Activity activity : dailyPlan.getActivities()) {
            activitiesArray.put(parseActivityToJson(activity));
        }
        json.put("activities", activitiesArray);

        return json;
    }

    private static JSONObject parseActivityToJson(TravelPlanData.Activity activity) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("time", activity.getTime());
        json.put("title", activity.getTitle());
        json.put("description", activity.getDescription());
        json.put("location", activity.getLocation());
        json.put("transport", parseTransportToJson(activity.getTransport()));
        return json;
    }

    private static JSONObject parseTransportToJson(TravelPlanData.Transport transport) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("from", transport.getFrom());
        json.put("to", transport.getTo());
        json.put("estimated_time", transport.getEstimatedTime());
        json.put("google_map_link", transport.getGoogleMapLink());
        return json;
    }
}