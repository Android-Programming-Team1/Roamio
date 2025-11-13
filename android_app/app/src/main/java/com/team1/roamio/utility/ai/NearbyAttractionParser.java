package com.team1.roamio.utility.ai;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NearbyAttractionParser {

    /**
     * AI가 생성한 JSON 문자열을 AttractionData 리스트로 변환합니다.
     *
     * @param json AI 응답으로 받은 JSON 배열 문자열
     * @return AttractionData 객체 리스트
     * @throws JSONException JSON 파싱 중 오류 발생 시
     */
    public static List<AttractionData> parseJsonToAttractionList(String json) throws JSONException {
        List<AttractionData> recommendations = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(json);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);

            // 필드 추출 (opt* 메소드를 사용하여 키가 없어도 null 또는 기본값 반환)
            String name = obj.optString("name", "N/A");
            String address = obj.optString("address", "주소 정보 없음");
            String category = obj.optString("category", "기타");
            String uri = obj.optString("uri", "");

            // 별점 처리: JSON에서 double로 올 수도 있으므로 유연하게 처리
            double starDouble = obj.optDouble("starPoint", 0.0);
            int starPoint = (int) Math.round(starDouble); // 반올림하여 정수형으로 변환

            recommendations.add(new AttractionData(name, address, category, starPoint, uri));
        }

        return recommendations;
    }
}