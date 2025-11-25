package com.team1.roamio.utility.stamp;

import com.team1.roamio.data.Stamp;

import org.json.JSONException;
import org.json.JSONObject;

public class StampJsonParser {

    // 1. 객체 -> JSON String 변환
    public static String toJson(Stamp stamp) {
        if (stamp == null) return null;

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", stamp.getId());
            jsonObject.put("countryId", stamp.getCountryId());
            // Long 타입이 null일 경우를 대비하거나, 0으로 처리
            jsonObject.put("stampedAt", stamp.getStampedAt());

            jsonObject.put("imageName", stamp.getImageName());
            jsonObject.put("imageUri", stamp.getImageUri());
            jsonObject.put("imageUrl", stamp.getImageUrl());

            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 2. JSON String -> 객체 변환
    public static Stamp fromJson(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) return null;

        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            Stamp stamp = new Stamp();
            // opt... 메서드는 키가 없을 경우 기본값을 반환하여 에러를 방지합니다.
            stamp.setId(jsonObject.optLong("id", 0));
            stamp.setCountryId(jsonObject.optLong("countryId", 0));
            stamp.setStampedAt(jsonObject.optLong("stampedAt", 0));

            stamp.setImageName(jsonObject.optString("imageName", null));
            stamp.setImageUri(jsonObject.optString("imageUri", null));
            stamp.setImageUrl(jsonObject.optString("imageUrl", null));

            return stamp;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}