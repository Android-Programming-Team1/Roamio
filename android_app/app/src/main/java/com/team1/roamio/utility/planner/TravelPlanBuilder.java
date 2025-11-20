package com.team1.roamio.utility.planner;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.team1.roamio.data.TravelPlanData;
import com.team1.roamio.utility.ai.GeminiPro;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TravelPlanBuilder {

    private JSONObject userCustomData;

    // 백그라운드 작업을 실행할 스레드 풀
    private final ExecutorService executor;

    // 결과를 메인 스레드로 전달할 핸들러
    private final Handler handler;

    private TravelPlanBuilder() {
        userCustomData = new JSONObject();

        // Executor와 Handler 초기화
        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
    }

    public static TravelPlanBuilder planDataBuilder() {
        return new TravelPlanBuilder();
    }

    public TravelPlanBuilder setStayDuration(int days) throws JSONException {
        userCustomData.put("stay_duration", days); return this;
    }
    public TravelPlanBuilder setIsHardPlan(boolean isHardPlan) throws JSONException {
        userCustomData.put("travel_isHardPlan", isHardPlan); return this;
    }
    public TravelPlanBuilder setHotelLocation(String location) throws JSONException {
        if (location == null) userCustomData.put("hotel_location", "숙소 미정.");
        else userCustomData.put("hotel_location", location);
        return this;
    }
    public TravelPlanBuilder setVisitCountry(String country) throws JSONException {
        userCustomData.put("visit_country", country); return this;
    }
    public TravelPlanBuilder setPreference(String preference) throws JSONException {
        if (preference == null) userCustomData.put("travel_preference", "호불호 없음.");
        else userCustomData.put("travel_preference", preference);
        return this;
    }


    /**
     * [비동기]
     * 여행 계획 생성을 백그라운드 스레드에서 시작합니다.
     * 완료되면 콜백을 통해 메인 스레드로 결과를 전달합니다.
     * @param callback 결과를 전달받을 콜백
     */
    public void build(PlanBuildCallback callback) {

        // 백그라운드 스레드에서 작업을 실행
        executor.execute(() -> {
            try {
                String prompt = createPrompt();

                GeminiPro geminiPro = new GeminiPro();
                String response = geminiPro.callGemini(prompt).get();

                String cleanedResponse = cleanJsonResponse(response);

                TravelPlanData planData = TravelPlanParser.parseJsonToPlanData(cleanedResponse);

                // 성공
                handler.post(() -> callback.onSuccess(planData));
            }
            catch (Exception e) {
                Log.e("TravelPlanBuilder", "빌드 중 에러 발생", e);
                // 실패
                handler.post(() -> callback.onError(e));
            }
        });
    }

    private String createPrompt() throws JSONException {
        // ... (프롬프트 문자열 생성 로직) ...
        return "You are an expert travel planner that outputs ONLY JSON but do not use code block.\n" +
                "Do not include any explanations, comments, or natural language text outside the JSON.\n\n" +
                "---\n" +
                "### Input (User JSON):\n" +
                userCustomData.toString() + "\n\n" +
                "---\n" +
                "### Rules for Output:\n" +
                "1. Output format: Return ONLY a JSON object with these keys:\n" +
                "   - \"plan_summary\": 여행 전체를 요약한 한 문장 (한국어)\n" +
                "   - \"total_days\": 여행 총 일수 (3일경우 3일차에 귀국)\n" +
                "   - \"country\": 입력된 국가명\n" +
                "   - \"daily_plans\": 일자별 일정 배열\n" +
                "     각 일자는 다음을 포함:\n" +
                "     - \"day\": 숫자 (1일부터 시작)\n" +
                "     - \"theme\": 그날 일정의 주제 (한국어)\n" +
                "     - \"activities\": 하루 일정 배열 (보통 3~4개)\n" +
                "       각 활동은 다음을 포함:\n" +
                "         - \"time\": 오전 / 오후 / 저녁 / 밤\n" +
                "         - \"title\": 짧은 제목 (최대 30자)\n" +
                "         - \"description\": 활동 설명 (한국어)\n" +
                "         - \"location\": 장소명 또는 지역명\n" +
                "         - \"transport\": {\n" +
                "             \"from\": 출발지,\n" +
                "             \"to\": 도착지,\n" +
                "             \"estimated_time\": 예상 소요시간 (분 또는 시간 단위),\n" +
                "             \"google_map_link\": 구글 지도 경로 링크\n" +
                "           }\n" +
                "   - 각 날짜의 마지막 활동은 반드시 숙소 복귀를 포함해야 합니다.\n" +
                "   - 여행 일 수가 3일일 경우 적어도 3일차 오전에 무조건 귀국하는 일정을 담아야 합니다.\n\n" +
                "2. 숙소 위치:\n" +
                "   - \"hotel_location\"이 미정일 경우 임의로 중심지(예: 시부야역 인근 호텔)를 설정하세요.\n\n" +
                "3. 이동:\n" +
                "   - 모든 이동은 대중교통 기준으로 하며, 이동 시간과 Google Maps 링크를 포함합니다.\n\n" +
                "4. 일정 스타일:\n" +
                "   - \"travel_isHardPlan\"이 true이면 하루 3~4개 활동의 빽빽한 일정으로 구성하세요.\n" +
                "   - \"travel_preference\"에 맞게 장소를 선택하세요 (예: 관광지 중심).\n\n" +
                "5. 출력 제한:\n" +
                "   - JSON 외에 어떠한 부가 텍스트도 포함하지 마세요.\n" +
                "   - \"recommendations\" 섹션은 포함하지 마세요.\n\n" +
                "---\n" +
                "### Output Example:\n" +
                "{\n" +
                "  \"plan_summary\": \"일본 도쿄에서 4일 동안 주요 관광지를 중심으로 빽빽하게 즐기는 여행 일정입니다.\",\n" +
                "  \"total_days\": 4,\n" +
                "  \"country\": \"일본\",\n" +
                "  \"daily_plans\": [ ... ]\n" +
                "}\n" +
                "\n" +
                "응답은 반드시 코드블럭 없이 순수 JSON 형식으로만 출력하세요.\n" +
                "(예: {\"key\":\"value\"})  \n" +
                "```json 등 코드블럭을 절대 포함하지 마세요.";
    }

    private String cleanJsonResponse(String responseText) {
        return responseText
                .replaceAll("(?i)```json\\s*", "")
                .replaceAll("```", "")
                .trim();
    }
}