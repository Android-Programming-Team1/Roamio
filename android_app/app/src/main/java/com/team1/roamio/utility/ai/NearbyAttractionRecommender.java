package com.team1.roamio.utility.ai;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CompletionException;

// GeminiPro 클래스가 동일한 패키지 또는 상위 패키지에 있다고 가정합니다.
// 만약 'com.example.yourapp'에 있다면 import com.example.yourapp.GeminiPro; 로 변경
// 여기서는 동일 패키지에 있다고 가정합니다.
// import com.team1.roamio.utility.ai.GeminiPro;

public class NearbyAttractionRecommender {

    private static final String TAG = "AttractionRecommender";
    private final GeminiPro geminiPro;
    private final Handler mainThreadHandler; // 결과를 메인 스레드로 전달하기 위함

    public NearbyAttractionRecommender() {
        this.geminiPro = new GeminiPro();
        // 메인 스레드의 Looper를 사용하여 Handler 생성
        this.mainThreadHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * [비동기]
     * 사용자의 위치를 기반으로 주변 관광지를 추천합니다.
     *
     * @param latitude  위도
     * @param longitude 경도
     * @param callback  추천 결과를 전달받을 콜백 (메인 스레드에서 호출됨)
     */
    public void recommend(double latitude, double longitude, RecommendationCallback callback) {

        String prompt = createPrompt(latitude, longitude);

        // 1. GeminiPro 비동기 호출 (이미 백그라운드 스레드에서 실행됨)
        geminiPro.callGemini(prompt, latitude, longitude)
                .thenApply(response -> {
                    // 2. 백그라운드에서 응답 전처리 및 파싱
                    // (thenApply는 이전 작업과 동일한 스레드(백그라운드)에서 실행됨)
                    try {
                        String cleanedResponse = cleanJsonResponse(response);
                        return NearbyAttractionParser.parseJsonToAttractionList(cleanedResponse);
                    } catch (Exception e) {
                        // 파싱 실패 시 예외를 발생시켜 exceptionally 블록으로 넘김
                        Log.e(TAG, "JSON Parsing failed", e);
                        throw new CompletionException("JSON 파싱에 실패했습니다.", e);
                    }
                })
                .thenAccept(recommendations -> {
                    // 3. 성공 시: 결과를 메인 스레드로 전달
                    mainThreadHandler.post(() -> callback.onSuccess(recommendations));
                })
                .exceptionally(e -> {
                    // 4. 실패 시: 예외를 메인 스레드로 전달
                    // CompletionException으로 래핑된 경우 원인을 추출
                    Throwable cause = (e instanceof CompletionException) ? e.getCause() : e;
                    Exception finalException = (cause instanceof Exception) ? (Exception) cause : new Exception(cause);

                    Log.e(TAG, "Gemini recommendation failed", finalException);
                    mainThreadHandler.post(() -> callback.onError(finalException));
                    return null; // exceptionally 블록은 null을 반환해야 함
                });
    }

    /**
     * AI에게 전달할 프롬프트 생성
     * (Grounding을 사용하므로, 프롬프트에서 위치 정보를 명시적으로 사용하도록 지시)
     */
    private String createPrompt(double latitude, double longitude) {
        // Grounding으로 위치가 전달되지만, 프롬프트에도 명시하여 AI가 컨텍스트를 확실히 인지하도록 함
        // JSON 형식을 매우 구체적으로 지시하여 파싱 오류를 줄임
        return "현재 내 위치(약 " + latitude + ", " + longitude + ") 주변의 인기 있는 관광지나 맛집 5곳을 추천해줘.\n" +
                "응답은 반드시 유효한 JSON 배열 형식이어야 해. JSON 배열 앞뒤로 설명이나 ```json 같은 마크다운을 절대 붙이지 마.\n" +
                "배열의 각 객체는 다음 5개의 키를 가져야 해:\n" +
                "1. name: (String) 장소 이름\n" +
                "2. address: (String) 주소\n" +
                "3. category: (String) 카테고리 (예: '음식점', '명소', '박물관', '공원')\n" +
                "4. starPoint: (Number) 1점에서 5점 사이의 숫자 별점\n" +
                "5. uri: (String) 해당 장소의 Google 지도 URL 또는 공식 웹사이트 URL";
    }

    /**
     * AI 응답에서 불필요한 문자열 제거 및 JSON 파싱용 전처리
     */
    private String cleanJsonResponse(String response) {
        if (response == null || response.isEmpty()) {
            return "[]";
        }

        // AI가 프롬프트 지시를 어기고 ```json ... ``` 같은 마크다운을 포함한 경우
        String cleaned = response.trim();
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7); // "```json\n" 제거
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }

        // 응답이 배열 '[' 로 시작하거나 객체 '{' 로 시작하는 부분을 찾음
        int arrayStartIndex = cleaned.indexOf('[');
        int objectStartIndex = cleaned.indexOf('{');

        // 배열이나 객체 시작 지점을 찾지 못한 경우
        if (arrayStartIndex == -1 && objectStartIndex == -1) {
            Log.w(TAG, "Response is not valid JSON format: " + response);
            return "[]"; // 빈 배열 반환
        }

        // 배열이 우선
        int startIndex = (arrayStartIndex != -1) ? arrayStartIndex : objectStartIndex;

        // 마지막 닫는 괄호 찾기
        int arrayEndIndex = cleaned.lastIndexOf(']');
        int objectEndIndex = cleaned.lastIndexOf('}');

        int endIndex = (arrayEndIndex > objectEndIndex) ? arrayEndIndex : objectEndIndex;

        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            return cleaned.substring(startIndex, endIndex + 1).trim();
        }

        return cleaned; // 최후의 시도
    }
}