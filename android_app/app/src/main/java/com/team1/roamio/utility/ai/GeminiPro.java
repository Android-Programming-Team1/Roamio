package com.team1.roamio.utility.ai;

import android.os.Build;
import androidx.annotation.RequiresApi; // CompletableFuture는 API 24+가 필요합니다

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@RequiresApi(api = Build.VERSION_CODES.N) // CompletableFuture 사용을 위해 API 24+ 필요
public class GeminiPro {

    // 1. API 엔드포인트 및 키 (BuildConfig에서 가져오기)
    // 모델 이름을 'gemini-pro' 또는 cURL 예시의 'gemini-1.5-flash' 등으로 변경 가능
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    // 중요: API 키는 코드에 하드코딩하지 말고,
    // 'local.properties' -> 'build.gradle.kts' -> 'BuildConfig'를 통해 주입해야 합니다.
    private static final String API_KEY = BuildConfig.GEMINI_API_KEY;

    private final OkHttpClient client;
    private final Gson gson;
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public GeminiPro() {
        this.client = new OkHttpClient();
        this.gson = new Gson();
    }

    /**
     * Gemini API를 비동기적으로 호출하고 Google Search Grounding을 사용합니다.
     *
     * @param prompt 사용자 입력 프롬프트
     * @return API 응답 텍스트를 포함하는 CompletableFuture<String>
     */
    public CompletableFuture<String> callGemini(String prompt) {

        // supplyAsync: 백그라운드 스레드에서 작업을 실행합니다.
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 1. 요청 본문(JSON) 생성
                // cURL 예시에 있던 "tools"와 "google_search" 포함
                GeminiRequest requestBody = buildRequestBody(prompt);
                String requestBodyJson = gson.toJson(requestBody);

                // 2. OkHttp Request 생성
                RequestBody body = RequestBody.create(requestBodyJson, JSON);
                Request request = new Request.Builder()
                        .url(API_URL)
                        .addHeader("x-goog-api-key", API_KEY) // cURL 예시와 동일하게 헤더 사용
                        .addHeader("Content-Type", "application/json")
                        .post(body)
                        .build();

                // 3. 동기식 네트워크 호출 실행 (supplyAsync가 백그라운드 스레드이므로 .execute() 사용 가능)
                try (Response response = client.newCall(request).execute()) {

                    if (!response.isSuccessful()) {
                        throw new IOException("API 호출 실패: " + response.code() + " | " + response.body().string());
                    }

                    String responseBodyString = response.body().string();

                    // 4. 응답(JSON) 파싱
                    GeminiResponse geminiResponse = gson.fromJson(responseBodyString, GeminiResponse.class);

                    // 5. 응답 텍스트 추출
                    return extractTextFromResponse(geminiResponse);
                }

            } catch (Exception e) {
                // 예외 발생 시 CompletableFuture를 예외적으로 완료시킵니다.
                // 호출한 쪽의 .exceptionally()에서 이 예외를 처리할 수 있습니다.
                throw new RuntimeException("Gemini API 호출 중 오류 발생", e);
            }
        });
    }

    /**
     * API 요청 본문(POJO)을 생성합니다.
     */
    private GeminiRequest buildRequestBody(String prompt) {
        Part part = new Part(prompt);
        Content content = new Content(Collections.singletonList(part));
        GoogleSearch googleSearch = new GoogleSearch(); // Grounding을 위한 빈 객체
        Tool tool = new Tool(googleSearch);

        return new GeminiRequest(
                Collections.singletonList(content),
                Collections.singletonList(tool)
        );
    }

    /**
     * API 응답(POJO)에서 텍스트를 추출합니다.
     */
    private String extractTextFromResponse(GeminiResponse response) {
        if (response == null || response.candidates == null || response.candidates.isEmpty() ||
                response.candidates.get(0).content == null ||
                response.candidates.get(0).content.parts == null ||
                response.candidates.get(0).content.parts.isEmpty()) {

            // 안전 설정 등에 의해 차단되었는지 확인
            if (response != null && response.promptFeedback != null &&
                    response.promptFeedback.blockReason != null) {
                return "응답이 차단되었습니다. 이유: " + response.promptFeedback.blockReason;
            }

            return "오류: API로부터 유효한 응답을 받지 못했습니다.";
        }

        // 첫 번째 후보의 첫 번째 파트 텍스트 반환
        return response.candidates.get(0).content.parts.get(0).text;
    }

    // --- JSON 직렬화/역직렬화를 위한 POJO 클래스 ---
    // (이 클래스 내부에 private static으로 선언하거나 별도 파일로 분리)

    // --- 요청 POJOs ---
    private static class GeminiRequest {
        final List<Content> contents;
        final List<Tool> tools;

        GeminiRequest(List<Content> contents, List<Tool> tools) {
            this.contents = contents;
            this.tools = tools;
        }
    }

    private static class Tool {
        final GoogleSearch google_search;
        Tool(GoogleSearch google_search) { this.google_search = google_search; }
    }

    private static class GoogleSearch {
        // cURL의 {"google_search": {}} 를 표현하기 위한 빈 클래스
    }

    // --- 공통 POJOs ---
    private static class Content {
        final List<Part> parts;
        Content(List<Part> parts) { this.parts = parts; }
    }

    private static class Part {
        final String text;
        Part(String text) { this.text = text; }
    }

    // --- 응답 POJOs ---
    private static class GeminiResponse {
        List<Candidate> candidates;
        PromptFeedback promptFeedback;
    }

    private static class Candidate {
        Content content;
        // finishReason, safetyRatings 등 필요에 따라 추가
    }

    private static class PromptFeedback {
        String blockReason;
        // safetyRatings 등 필요에 따라 추가
    }
}