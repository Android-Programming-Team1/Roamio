package com.team1.roamio.utility.ai;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// ... (기존 import 문들)
import okhttp3.Response;
import java.util.concurrent.TimeUnit; // <-- 이 줄을 추가하세요

public class GeminiPro {

    private static final String TAG = "GeminiPro";

    // OkHttpClient는 재사용하는 것이 효율적입니다.
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // 연결 타임아웃 30초
            .readTimeout(30, TimeUnit.SECONDS)    // 읽기 타임아웃 30초
            .writeTimeout(30, TimeUnit.SECONDS)   // 쓰기 타임아웃 30초
            .build();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    // 사용할 Gemini 모델 (1.5 Flash가 빠르고 효율적입니다)
    private static final String MODEL_NAME = "gemini-2.5-flash";

    // 1. Context를 저장할 멤버 변수(필드) 선언
    private final Context context;

    /**
     * 2. 생성자 추가:
     * 이 클래스가 생성될 때 Context를 한 번만 받습니다.
     * @param context (Activity, Service, Application 등)
     */
    public GeminiPro(Context context) {
        // Activity Context 대신 Application Context를 저장하는 것이
        // 메모리 누수(Memory Leak) 방지에 더 안전합니다.
        this.context = context.getApplicationContext();
    }

    /**
     * 3. callGemini 메서드 시그니처 변경 (Context 파라미터 제거)
     * 이제 이 메서드는 저장된 'this.context'를 사용합니다.
     *
     * @param prompt  사용자 질문
     * @return Gemini의 응답 텍스트를 담은 CompletableFuture
     */
    public CompletableFuture<String> callGemini(String prompt) {

        // 1. 현재 위치 비동기적으로 가져오기
        CompletableFuture<Location> locationFuture = getCurrentLocation();

        // 2. 위치 정보를 성공적으로 가져오면, 이어서 Gemini API 호출
        return locationFuture.thenApplyAsync(location -> {
            try {

                // (1) 위치 정보가 유효한지 로그로 확인
                if (location == null) {
                    throw new RuntimeException("위치 정보를 가져오지 못했습니다 (null).");
                }
                Log.d(TAG, "사용할 위치 정보: " + location.getLatitude() + ", " + location.getLongitude());

                // 3. API 호출 URL 생성
                String url = String.format("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
                        MODEL_NAME, BuildConfig.GEMINI_API_KEY);

                // 4. JSON 요청 본문 생성
                String jsonBody = buildJsonRequest(prompt, location);
                Log.d(TAG, "API 요청 JSON: " + jsonBody); // <-- ★★★ 매우 중요 ★★★

                // 5. OkHttp Request 생성
                RequestBody body = RequestBody.create(jsonBody, JSON);
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                // 6. OkHttp 동기 방식 호출 (thenApplyAsync 블록 안이므로 괜찮음)
                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("API 호출 실패: " + response.code() + " " + response.message());
                    }

                    String responseBody = response.body().string();

                    // 7. 응답 JSON 파싱
                    return parseGeminiResponse(responseBody);
                }
            } catch (Exception e) {
                Log.e(TAG, "Gemini API 호출 중 오류 발생", e);
                // 예외를 RuntimeException으로 래핑하여 CompletableFuture를 실패시킵니다.
                throw new RuntimeException(e);
            }
        }).exceptionally(ex -> {
            // 위치 가져오기 또는 API 호출 중 발생한 모든 예외 처리
            Log.e(TAG, "CompletableFuture 체인 중 예외 발생", ex);
            return "오류가 발생했습니다: " + ex.getMessage();
        });
    }

    /**
     * FusedLocationProviderClient를 사용해 현재 위치를 비동기적으로 가져옵니다.
     * Task API를 CompletableFuture로 변환합니다.
     */
    private CompletableFuture<Location> getCurrentLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.context);
        CompletableFuture<Location> future = new CompletableFuture<>();

        try {
            // 위치 권한이 확인되었다고 가정합니다. (보안 예외 발생 가능)
            CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
            Task<Location> locationTask = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY, // 높은 정확도
                    cancellationTokenSource.getToken()
            );

            locationTask.addOnSuccessListener(location -> {
                if (location != null) {
                    Log.d(TAG, "위치 획득 성공: " + location.getLatitude() + ", " + location.getLongitude());
                    future.complete(location);
                } else {
                    Log.w(TAG, "위치 정보가 null입니다.");
                    future.completeExceptionally(new RuntimeException("위치 정보를 가져올 수 없습니다 (null)."));
                }
            });

            locationTask.addOnFailureListener(e -> {
                Log.e(TAG, "위치 획득 실패", e);
                future.completeExceptionally(e);
            });

        } catch (SecurityException e) {
            Log.e(TAG, "위치 권한이 없습니다. AndroidManifest.xml을 확인하고 동적 권한을 요청하세요.", e);
            future.completeExceptionally(e);
        }

        return future;
    }

    /**
     * Gemini API 요청을 위한 JSON 본문을 생성합니다.
     */
    private String buildJsonRequest(String prompt, Location location) throws Exception {
        JSONObject root = new JSONObject();

        // 1. "contents": (이전과 동일, 정상)
        JSONObject part = new JSONObject();
        part.put("text", prompt);
        JSONArray partsArray = new JSONArray();
        partsArray.put(part);
        JSONObject content = new JSONObject();
        content.put("role", "user");
        content.put("parts", partsArray);
        JSONArray contentsArray = new JSONArray();
        contentsArray.put(content);
        root.put("contents", contentsArray);

        // 2. "tools": (필드 이름 수정)
        JSONObject googleSearch = new JSONObject();
        googleSearch.put("google_search", new JSONObject()); // "googleSearchRetrieval" -> "google_search"

        JSONObject googleMaps = new JSONObject();
        googleMaps.put("googleMaps", new JSONObject()); // "googleMapsApiGrounding" -> "googleMaps"

        JSONArray toolsArray = new JSONArray();
        toolsArray.put(googleSearch);
        toolsArray.put(googleMaps);
        root.put("tools", toolsArray);

        // 3. "toolConfig": (위치 정보 구조 수정)
        // "context" 블록 대신 "toolConfig" 블록을 사용합니다.
        JSONObject locationJson = new JSONObject();
        locationJson.put("latitude", location.getLatitude());
        locationJson.put("longitude", location.getLongitude());

        JSONObject retrievalConfig = new JSONObject();
        retrievalConfig.put("latLng", locationJson); // "latLng" 필드 사용

        JSONObject toolConfig = new JSONObject();
        toolConfig.put("retrievalConfig", retrievalConfig); // "retrievalConfig" 필드 사용

        root.put("toolConfig", toolConfig);

        return root.toString();
    }


    /**
     * Gemini API 응답 JSON을 파싱하여 텍스트만 추출합니다.
     */
    /**
     * Gemini API 응답 JSON을 파싱합니다.
     * (수정됨: groundingMetadata를 파싱하여 인용 정보를 텍스트에 삽입)
     */
    /**
     * (수정됨) Gemini API 응답 JSON을 파싱합니다.
     * .get...() 대신 .opt...()를 사용하여 더 안전하게(Robust) 파싱합니다.
     */
    /**
     * (수정됨) Gemini API 응답 JSON을 파싱합니다.
     * 본문 텍스트와 인용 목록을 분리하여, 모든 인용을 텍스트 하단에 나열합니다.
     */
    private String parseGeminiResponse(String responseBody) throws Exception {
        try {
            JSONObject responseJson = new JSONObject(responseBody);

            JSONObject candidate = responseJson.optJSONArray("candidates").optJSONObject(0);
            if (candidate == null) return "응답 형식이 올바르지 않습니다. (No candidates)";

            JSONObject content = candidate.optJSONObject("content");
            if (content == null) return "응답 형식이 올바르지 않습니다. (No content)";

            JSONArray parts = content.optJSONArray("parts");
            if (parts == null || parts.length() == 0) return "응답 형식이 올바르지 않습니다. (No parts)";

            // 1. 기본 텍스트 추출 (인용이 삽입되지 않은 원본)
            String baseText = parts.optJSONObject(0).optString("text", "(텍스트 응답 없음)");

            // 2. 인용 메타데이터 추출
            JSONObject metadata = candidate.optJSONObject("groundingMetadata");
            if (metadata == null) {
                return baseText; // 인용이 없으면 기본 텍스트 반환
            }

            JSONArray chunksJson = metadata.optJSONArray("groundingChunks");
            JSONArray supportsJson = metadata.optJSONArray("groundingSupports");

            if (chunksJson == null || supportsJson == null || chunksJson.length() == 0 || supportsJson.length() == 0) {
                return baseText; // 인용 소스나 세그먼트가 없으면 반환
            }

            // --- ★★★ 로직 변경 시작 ★★★ ---

            // 3. Chunks 파싱: 모든 출처의 "완성된 인용 문자열"을 미리 만듭니다.
            // (예: "[1] (Yangtanjib)", "[2] (Bao Seoul)")
            List<String> chunkCitationStrings = new ArrayList<>();
            for (int i = 0; i < chunksJson.length(); i++) {
                JSONObject chunk = chunksJson.optJSONObject(i);
                if (chunk == null) continue;

                int displayIndex = i + 1; // 1 기반 인덱스
                String citationString = String.format("[%d] (알 수 없는 출처)", displayIndex); // 기본값

                JSONObject web = chunk.optJSONObject("web");
                JSONObject maps = chunk.optJSONObject("maps");

                if (web != null) {
                    String webUri = web.optString("uri", "URI 없음");
                    String webTitle = web.optString("title", ""); // 제목이 있으면 가져옴

                    if (webTitle.isEmpty() || webTitle.equals(webUri)) {
                        citationString = String.format("[%d] %s", displayIndex, webUri);
                    } else {
                        citationString = String.format("[%d] %s (%s)", displayIndex, webTitle, webUri);
                    }
                } else if (maps != null) {
                    String mapUri = maps.optString("uri", "장소 이름 없음");
                    String mapTitle = maps.optString("title", ""); // 제목이 있으면 가져옴

                    if (mapTitle.isEmpty() || mapTitle.equals(mapUri)) {
                        citationString = String.format("[%d] %s", displayIndex, mapUri);
                    } else {
                        citationString = String.format("[%d] %s (%s)", displayIndex, mapTitle, mapUri);
                    }
                }
                chunkCitationStrings.add(citationString);
            }

            // 4. Supports 파싱: 모델이 "실제로 사용했다"고 주장하는 인용의 인덱스만 수집
            // (중복을 제거하기 위해 Set 사용)
            java.util.Set<Integer> usedChunkIndices = new java.util.HashSet<>();
            for (int i = 0; i < supportsJson.length(); i++) {
                JSONObject support = supportsJson.optJSONObject(i);
                if (support == null) continue;

                JSONArray indices = support.optJSONArray("groundingChunkIndices");
                if (indices != null) {
                    for (int j = 0; j < indices.length(); j++) {
                        usedChunkIndices.add(indices.optInt(j));
                    }
                }
            }

            if (usedChunkIndices.isEmpty()) {
                return baseText; // 실제 사용된 인용이 없으면 원본 텍스트만 반환
            }

            // 5. 최종 출력 문자열 생성
            StringBuilder finalOutput = new StringBuilder(baseText);
            finalOutput.append("\n\n---\n"); // 구분선
            finalOutput.append("## 출처\n");

            // 6. 사용된 인용 목록을 정렬하여 추가
            // (Set을 List로 변환 후 정렬)
            List<Integer> sortedIndices = new ArrayList<>(usedChunkIndices);
            java.util.Collections.sort(sortedIndices);

            for (int index : sortedIndices) {
                if (index >= 0 && index < chunkCitationStrings.size()) {
                    // 3번 단계에서 만들어 둔 완성형 문자열을 가져와서 추가
                    finalOutput.append(chunkCitationStrings.get(index)).append("\n");
                }
            }

            return finalOutput.toString().trim(); // 뒤쪽 공백 제거

        } catch (Exception e) {
            Log.e(TAG, "JSON 파싱 실패 (하단 인용 목록 생성 중): " + responseBody, e);
            throw new RuntimeException("응답 파싱에 실패했습니다 (v-footer).", e);
        }
    }
}