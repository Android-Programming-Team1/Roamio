package com.team1.roamio.data;

import org.json.JSONArray;
import org.json.JSONObject;

public class PlanData {
    private JSONArray planDataJson;
    // TODO: 2025-11-03 유저의 커스텀 정보 ex) 채류 기간, 여행 취향, 숙소 위치 등등

    public PlanData() {
        planDataJson = new JSONArray();
    }

    public static PlanData PlanDataBuilder() {
        return new PlanData();
    }

    // TODO: 2025-11-03 유저 커스텀 정보 setter(빌더 패턴)

    public PlanData build() {
        // TODO: 2025-11-03 프롬프트 실행
        return this;
    }
}
