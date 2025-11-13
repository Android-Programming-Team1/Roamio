package com.team1.roamio.utility.ai; // (GeminiPro의 패키지 경로)

import android.util.Log;
// BuildConfig 클래스를 임포트합니다.
// (자신의 앱 패키지 경로에 맞게 수정하세요)
import com.team1.roamio.BuildConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class GeminiPro {

    private static final String TAG = "GeminiPro";
    private static final String API_KEY = BuildConfig.GEMINI_API_KEY;
    private static final String MODEL_NAME = "gemini-1.5-flash-latest";
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/" + MODEL_NAME + ":generateContent";

    /**
     * [신규] Google Search Grounding만 사용하여 Gemini API를 호출합니다.
     * (위치 정보가 필요 없는 일반적인 질문용)
     *
     * @param prompt 사용자 질문
     * @return 모델의 응답 텍스트를 포함하는 CompletableFuture<String>
     */
    public CompletableFuture<String> callGemini(String prompt) {
        try {
            // Google Search만 포함하는 JSON 본문 생성
            String jsonRequestBody = createSimpleJsonBody(prompt);
            return executeRequest(jsonRequestBody);
        } catch (Exception e) {
            // JSON 생성 중 예외 발생 시
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * [기존] Google Maps 및 Search Grounding을 사용하여 Gemini API를 호출합니다.
     * (현재 위치 기반 질문용)
     *
     * @param prompt    사용자 질문
     * @param latitude  현재 위치의 위도
     * @param longitude 현재 위치의 경도
     * @return 모델의 응답 텍스트를 포함하는 CompletableFuture<String>
     */
    public CompletableFuture<String> callGemini(String prompt, double latitude, double longitude) {
        try {
            // Google Maps + Search가 포함된 JSON 본문 생성
            String jsonRequestBody = createGroundedJsonBody(prompt, latitude, longitude);
            return executeRequest(jsonRequestBody);
        } catch (Exception e) {
            // JSON 생성 중 예외 발생 시
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * [리팩토링] 실제 네트워크 요청을 수행하는 비공개 헬퍼 메서드
     *
     * @param jsonRequestBody API에 전송할 JSON 문자열
     * @return 비동기 응답 결과
     */
    private CompletableFuture<String> executeRequest(String jsonRequestBody) {

        return CompletableFuture.supplyAsync(() -> {
            HttpURLConnection conn = null;
            try {
                // 1. URL 연결 설정
                URL url = new URL(API_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("x-goog-api-key", API_KEY);
                conn.setDoOutput(true);

                // 2. 요청 본문 전송
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonRequestBody.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                // 3. 응답 코드 확인 및 응답 읽기
                int responseCode = conn.getResponseCode();
                InputStream inputStream;
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    inputStream = conn.getInputStream();
                } else {
                    inputStream = conn.getErrorStream();
                }

                String rawResponse = readResponse(inputStream);

                // 4. 응답 파싱
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return parseSuccessResponse(rawResponse);
                } else {
                    String errorMessage = parseErrorResponse(rawResponse, responseCode);
                    throw new RuntimeException("Gemini API Error: " + errorMessage);
                }

            } catch (Exception e) {
                Log.e(TAG, "Error calling Gemini API", e);
                throw new RuntimeException("Gemini API call failed", e);
            } finally {
                if (conn != null) {
                    conn.disconnect(); // 연결 종료
                }
            }
        });
    }

    /**
     * [신규] Google Search Grounding만 포함하는 JSON 본문을 생성합니다.
     */
    private String createSimpleJsonBody(String prompt) throws Exception {
        JSONObject root = new JSONObject();

        // 1. contents (사용자 프롬프트)
        JSONObject textPart = new JSONObject();
        textPart.put("text", prompt);
        JSONArray partsArray = new JSONArray();
        partsArray.put(textPart);
        JSONObject content = new JSONObject();
        content.put("role", "user");
        content.put("parts", partsArray);
        JSONArray contentsArray = new JSONArray();
        contentsArray.put(content);
        root.put("contents", contentsArray);

        // 2. tools (Google Search ONLY)
        JSONArray toolsArray = new JSONArray();
        toolsArray.put(new JSONObject().put("google_search", new JSONObject()));
        root.put("tools", toolsArray);

        // 3. toolConfig 없음 (위치 정보가 없으므로)

        return root.toString();
    }


    /**
     * [이름 변경] Google Maps + Search Grounding JSON 본문을 생성합니다.
     * (기존 createJsonBody -> createGroundedJsonBody)
     */
    private String createGroundedJsonBody(String prompt, double latitude, double longitude) throws Exception {
        JSONObject root = new JSONObject();

        // 1. contents
        JSONObject textPart = new JSONObject();
        textPart.put("text", prompt);
        JSONArray partsArray = new JSONArray();
        partsArray.put(textPart);
        JSONObject content = new JSONObject();
        content.put("role", "user");
        content.put("parts", partsArray);
        JSONArray contentsArray = new JSONArray();
        contentsArray.put(content);
        root.put("contents", contentsArray);

        // 2. tools (Google Search 및 Google Maps)
        JSONArray toolsArray = new JSONArray();
        toolsArray.put(new JSONObject().put("google_search", new JSONObject()));
        toolsArray.put(new JSONObject().put("googleMaps", new JSONObject()));
        root.put("tools", toolsArray);

        // 3. toolConfig (Google Maps Grounding을 위한 위치 정보)
        JSONObject latLng = new JSONObject();
        latLng.put("latitude", latitude);
        latLng.put("longitude", longitude);
        JSONObject retrievalConfig = new JSONObject();
        retrievalConfig.put("latLng", latLng);
        JSONObject toolConfig = new JSONObject();
        toolConfig.put("retrievalConfig", retrievalConfig);
        root.put("toolConfig", toolConfig);

        return root.toString();
    }

    /**
     * InputStream에서 문자열을 읽어옵니다. (변경 없음)
     */
    private String readResponse(InputStream inputStream) throws Exception {
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }
        return response.toString();
    }

    /**
     * 성공적인 API 응답(JSON)을 파싱하여 텍스트를 추출합니다. (변경 없음)
     */
    private String parseSuccessResponse(String rawResponse) throws Exception {
        JSONObject jsonResponse = new JSONObject(rawResponse);

        if (!jsonResponse.has("candidates") || jsonResponse.getJSONArray("candidates").length() == 0) {
            if (jsonResponse.has("promptFeedback") && jsonResponse.getJSONObject("promptFeedback").has("blockReason")) {
                String reason = jsonResponse.getJSONObject("promptFeedback").getString("blockReason");
                throw new RuntimeException("Prompt blocked by safety settings: " + reason);
            }
            throw new RuntimeException("No valid candidates found in response.");
        }

        JSONArray candidates = jsonResponse.getJSONArray("candidates");
        JSONObject firstCandidate = candidates.getJSONObject(0);
        JSONObject content = firstCandidate.getJSONObject("content");
        JSONArray parts = content.getJSONArray("parts");

        StringBuilder resultText = new StringBuilder();
        for (int i = 0; i < parts.length(); i++) {
            JSONObject part = parts.getJSONObject(i);
            if (part.has("text")) {
                resultText.append(part.getString("text"));
            }
        }

        return resultText.toString();
    }

    /**
     * 오류 API 응답(JSON)을 파싱하여 오류 메시지를 추출합니다. (변경 없음)
     */
    private String parseErrorResponse(String rawResponse, int responseCode) {
        try {
            JSONObject jsonResponse = new JSONObject(rawResponse);
            if (jsonResponse.has("error") && jsonResponse.getJSONObject("error").has("message")) {
                return jsonResponse.getJSONObject("error").getString("message");
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed to parse error JSON", e);
        }
        return "HTTP Error " + responseCode + ": " + rawResponse;
    }
}