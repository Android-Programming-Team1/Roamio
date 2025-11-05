package com.team1.roamio.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

// Material 컴포넌트 import
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

// 직접 만든 유틸리티 클래스 import
import com.team1.roamio.R;
import com.team1.roamio.data.TravelPlanData;
import com.team1.roamio.utility.planner.PlanBuildCallback;
import com.team1.roamio.utility.planner.TravelPlanBuilder;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    // View 변수
    private TextInputEditText editCountry, editDuration, editHotel, editPreferences;
    private SwitchMaterial switchIsHardPlan;
    private Button buttonGenerate;
    private ProgressBar progressBar;
    private TextView textError;
    private LinearLayout layoutResultsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // XML 레이아웃 연결

        initializeViews(); // 뷰 바인딩
        buttonGenerate.setOnClickListener(v -> {
            try {
                generateTravelPlan();
            }
            catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 레이아웃의 뷰들을 ID로 찾아 변수에 할당합니다.
     */
    private void initializeViews() {
        editCountry = findViewById(R.id.edit_country);
        editDuration = findViewById(R.id.edit_duration);
        editHotel = findViewById(R.id.edit_hotel);
        editPreferences = findViewById(R.id.edit_preferences);
        switchIsHardPlan = findViewById(R.id.switch_is_hard_plan);
        buttonGenerate = findViewById(R.id.button_generate);
        progressBar = findViewById(R.id.progress_bar);
        textError = findViewById(R.id.text_error);
        layoutResultsContainer = findViewById(R.id.layout_results_container);
    }

    /**
     * '계획 생성' 버튼을 눌렀을 때 실행되는 메인 로직입니다.
     */
    private void generateTravelPlan() throws JSONException {
        // 1. UI에서 입력값 가져오기
        String country = editCountry.getText().toString().trim();
        String durationStr = editDuration.getText().toString().trim();
        String hotel = editHotel.getText().toString().trim();
        String preferences = editPreferences.getText().toString().trim();
        boolean isHardPlan = switchIsHardPlan.isChecked();

        // 2. 간단한 유효성 검사
        int duration;
        try {
            if (country.isEmpty() || durationStr.isEmpty()) {
                Toast.makeText(this, "국가와 기간은 필수 입력 항목입니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            duration = Integer.parseInt(durationStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "체류 기간은 숫자만 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. 로딩 UI 시작
        setLoadingState(true);

        // 4. TravelPlanBuilder 비동기 호출 (콜백 방식)
        TravelPlanBuilder.planDataBuilder()
                .setVisitCountry(country)
                .setStayDuration(duration)
                .setHotelLocation(hotel.isEmpty() ? null : hotel)
                .setPreference(preferences.isEmpty() ? null : preferences)
                .setIsHardPlan(isHardPlan)
                .build(new PlanBuildCallback() {

                    @Override
                    public void onSuccess(TravelPlanData planData) {
                        // 성공! (메인 스레드에서 실행 보장됨)
                        setLoadingState(false);
                        displayPlan(planData); // 파싱된 객체로 뷰 그리기
                    }

                    @Override
                    public void onError(Exception e) {
                        // 실패! (메인 스레드에서 실행 보장됨)
                        setLoadingState(false);
                        textError.setText("오류 발생:\n" + e.getMessage());
                        textError.setVisibility(View.VISIBLE);
                        Log.e("Planner", "여행 계획 생성 실패", e);
                    }
                });
    }

    /**
     * 로딩 상태(true) 또는 결과 상태(false)에 따라 UI를 변경합니다.
     */
    private void setLoadingState(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            buttonGenerate.setEnabled(false); // 버튼 비활성화

            // 이전 결과 숨기기 및 초기화
            textError.setVisibility(View.GONE);
            layoutResultsContainer.setVisibility(View.GONE);
            layoutResultsContainer.removeAllViews(); // 동적 뷰 모두 제거
        } else {
            progressBar.setVisibility(View.GONE);
            buttonGenerate.setEnabled(true); // 버튼 다시 활성화
        }
    }

    /**
     * 파싱된 TravelPlanData 객체를 사용하여 동적으로 뷰를 생성하고
     * 'layout_results_container'에 추가합니다.
     */
    private void displayPlan(TravelPlanData planData) {
        layoutResultsContainer.setVisibility(View.VISIBLE);

        // 1. 전체 요약 (H1)
        addTextView(planData.getPlanSummary(), 20, Typeface.BOLD, 0, 16);

        // 2. 국가 / 일수 (H2)
        String metaInfo = "📍 " + planData.getCountry() + " (" + planData.getTotalDays() + "일)";
        addTextView(metaInfo, 16, Typeface.ITALIC, 0, 24);

        // 3. 일자별 계획 루프
        for (TravelPlanData.DailyPlan dailyPlan : planData.getDailyPlans()) {

            // Day X: 테마 (H3 - 날짜 구분선)
            String dayHeader = "🗓️ Day " + dailyPlan.getDay() + ": " + dailyPlan.getTheme();
            addTextView(dayHeader, 18, Typeface.BOLD, 8, 16);

            // 4. 활동별 루프
            for (TravelPlanData.Activity activity : dailyPlan.getActivities()) {

                // 활동 카드(LinearLayout) 생성 - 시각적 구분을 위함
                LinearLayout activityCard = new LinearLayout(this);
                activityCard.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(8));
                activityCard.setLayoutParams(params);
                // 카드 배경 및 패딩 설정
                activityCard.setBackground(getDrawable(R.drawable.bg_card_rounded)); // (아래 bg_card_rounded.xml 필요)
                activityCard.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));

                // 활동 시간 + 제목
                String title = "▪ " + activity.getTime() + ": " + activity.getTitle();
                addTextViewToLayout(activityCard, title, 16, Typeface.BOLD, 0, 4);

                // 활동 설명
                addTextViewToLayout(activityCard, activity.getDescription(), 14, Typeface.NORMAL, 0, 4);

                // 위치
                String location = "장소: " + activity.getLocation();
                addTextViewToLayout(activityCard, location, 14, Typeface.NORMAL, 0, 4);

                // 이동 정보
                TravelPlanData.Transport transport = activity.getTransport();
                String transportInfo = "이동: " + transport.getFrom() + " ➔ " +
                        transport.getTo() + " (약 " + transport.getEstimatedTime() + ")";
                addTextViewToLayout(activityCard, transportInfo, 14, Typeface.NORMAL, 0, 8);

                // 구글 지도 링크 (클릭 가능하게)
                addClickableLink(activityCard, "🔗 Google Maps에서 경로 보기", transport.getGoogleMapLink());

                // 완성된 활동 카드를 메인 컨테이너에 추가
                layoutResultsContainer.addView(activityCard);
            }
        }
    }

    // --- 뷰 생성을 위한 헬퍼 메서드 ---

    /** 메인 컨테이너(layoutResultsContainer)에 TextView를 바로 추가 */
    private void addTextView(String text, float sizeSp, int style, int marginTop, int marginBottom) {
        addTextViewToLayout(layoutResultsContainer, text, sizeSp, style, marginTop, marginBottom);
    }

    /** 지정된 레이아웃에 TextView를 추가 */
    private void addTextViewToLayout(ViewGroup layout, String text, float sizeSp, int style, int marginTop, int marginBottom) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp);
        tv.setTypeface(null, style);
        tv.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4.0f, getResources().getDisplayMetrics()), 1.0f); // 줄 간격

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, dpToPx(marginTop), 0, dpToPx(marginBottom));
        tv.setLayoutParams(params);

        layout.addView(tv);
    }

    /** 클릭 가능한 링크 TextView를 레이아웃에 추가 */
    private void addClickableLink(ViewGroup layout, String text, String url) {
        TextView linkTv = new TextView(this);
        SpannableString content = new SpannableString(text);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "링크를 열 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        };
        content.setSpan(clickableSpan, 0, content.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);

        linkTv.setText(content);
        linkTv.setMovementMethod(android.text.method.LinkMovementMethod.getInstance()); // 클릭 활성화
        linkTv.setTextColor(0xFF0000FF); // (colors.xml에 정의된 색상)

        layout.addView(linkTv);
    }

    /** DP 단위를 PX (픽셀) 단위로 변환 */
    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}