package com.team1.roamio.utility.ai;

// In: GeminiCallback.java
public interface GeminiCallback {
    void onSuccess(String responseText);
    void onError(Throwable throwable);
}