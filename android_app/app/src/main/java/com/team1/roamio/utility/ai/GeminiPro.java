package com.team1.roamio.utility.ai;

// In: GeminiPro.java
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.team1.roamio.BuildConfig;

import java.util.concurrent.Executor;
public class GeminiPro {

    private final GenerativeModelFutures generativeModel;
    private final Executor mainExecutor;

    public GeminiPro(Context context) {
        // 1. 모델 초기화 (API 키는 BuildConfig에서 가져옴)
        GenerativeModel gm = new GenerativeModel(
                "gemini-2.5-flash",         // 사용하려는 모델 (예: gemini-2.5-flash)
                BuildConfig.GEMINI_API_KEY  // Gradle에서 설정한 API 키
        );
        this.generativeModel = GenerativeModelFutures.from(gm);

        // 2. 결과를 UI 스레드에서 처리하기 위한 Executor
        this.mainExecutor = ContextCompat.getMainExecutor(context);
    }

    /**
     * 요청하신 비동기 Gemini 호출 메서드
     * @param prompt 사용자의 입력 프롬프트
     * @param callback 비동기 결과를 처리할 콜백
     */
    public void callGemini(String prompt, GeminiCallback callback) {
        Content content = new Content.Builder()
                .addText(prompt)
                .build();

        // 3. 비동기 API 호출 (ListenableFuture 반환)
        ListenableFuture<GenerateContentResponse> responseFuture = generativeModel.generateContent(content);

        // 4. 콜백 등록: 메인 스레드에서 onSuccess 또는 onFailure 실행
        Futures.addCallback(responseFuture, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                // 성공 시, 응답 텍스트를 콜백으로 전달
                String responseText = result.getText();
                callback.onSuccess(responseText);
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                // 실패 시, 예외를 콜백으로 전달
                callback.onError(t);
            }
        }, mainExecutor); // mainExecutor를 지정하여 UI 스레드에서 콜백이 실행되도록 함
    }
}