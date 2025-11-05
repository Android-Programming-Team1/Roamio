package com.team1.roamio.utility.planner;

import com.team1.roamio.data.TravelPlanData;

/**
 * TravelPlanBuilder의 비동기 작업 결과를 처리하기 위한 콜백 인터페이스
 */
public interface PlanBuildCallback {
    /**
     * 작업이 성공적으로 완료되었을 때 (메인 스레드에서) 호출됩니다.
     * @param planData 생성된 여행 계획 데이터
     */
    void onSuccess(TravelPlanData planData);

    /**
     * 작업 중 에러가 발생했을 때 (메인 스레드에서) 호출됩니다.
     * @param e 발생한 예외
     */
    void onError(Exception e);
}