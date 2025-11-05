package com.team1.roamio.utility.ai;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.team1.roamio.BuildConfig;

public class GeminiPro {

    private Client client;

    public GeminiPro() {
        client = Client.builder().apiKey(BuildConfig.GEMINI_API_KEY).build();
    }

    /**
     * [동기식]
     * 이 메서드는 네트워크 통신이 완료될 때까지 호출한 스레드를 멈춥니다.
     * 절대로 메인 스레드에서 호출하면 안 됩니다.
     */
    public String callGemini(String prompt) throws Exception {
        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        prompt,
                        null
                );
        return response.text();
    }
}