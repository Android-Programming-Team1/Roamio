package com.team1.roamio.utility.ai;

import java.util.List;

/**
 * AI 추천 결과(성공/실패)를 비동기적으로 전달받기 위한 콜백 인터페이스
 */
public interface RecommendationCallback {
    /**
     * 추천 성공 시 호출됩니다. (메인 스레드에서 실행 보장)
     * @param recommendations 파싱된 추천 장소 리스트
     */
    void onSuccess(List<AttractionData> recommendations);

    /**
     * 오류 발생 시 호출됩니다. (메인 스레드에서 실행 보장)
     * @param e 발생한 예외
     */
    void onError(Exception e);
}