package com.team1.roamio.view.ui.activity;

import android.content.Intent;
import android.content.res.ColorStateList; // [추가됨] 틴트 변경을 위해 필요
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.team1.roamio.R;
import com.team1.roamio.utility.planner.SavedUserData;

public class ActivityPlanning5 extends AppCompatActivity {
    ImageButton backButton;
    Button finalButton;
    Button imgBtn1;
    Button imgBtn2;
    Button imgBtn3;
    Button imgBtn4;
    Button imgBtn5;
    Button imgBtn6;
    private ImageView icon;

    // checkTog 배열의 인덱스는 버튼 imgBtn1 ~ imgBtn6과 맵핑됩니다.
    // 0: imgBtn1, 1: imgBtn2, ..., 5: imgBtn6
    private boolean[] checkTog = new boolean[6];

    // 선택된 버튼의 배경색
    private final int SELECTED_COLOR = Color.rgb(143, 255, 255);
    // 선택되지 않은 버튼의 배경색 (Color.WHITE)
    private final int UNSELECTED_COLOR = Color.rgb(255, 255, 255);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_planning_5);

        icon = findViewById(R.id.imageView7);
        Glide.with(this).asGif().load(R.drawable.romeo1).into(icon);

        // 1. 뒤로 가기 버튼 설정
        backButton = findViewById(R.id.imageButton6);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 2. 스타일 버튼 초기화 및 초기 배경색 설정
        imgBtn1 = findViewById(R.id.stylebutton1);
        imgBtn2 = findViewById(R.id.stylebutton2);
        imgBtn3 = findViewById(R.id.stylebutton3);
        imgBtn4 = findViewById(R.id.stylebutton4);
        imgBtn5 = findViewById(R.id.stylebutton5);
        imgBtn6 = findViewById(R.id.stylebutton6);

        // [수정됨] setBackgroundColor -> setBackgroundTintList 사용
        imgBtn1.setBackgroundTintList(ColorStateList.valueOf(UNSELECTED_COLOR));
        imgBtn2.setBackgroundTintList(ColorStateList.valueOf(UNSELECTED_COLOR));
        imgBtn3.setBackgroundTintList(ColorStateList.valueOf(UNSELECTED_COLOR));
        imgBtn4.setBackgroundTintList(ColorStateList.valueOf(UNSELECTED_COLOR));
        imgBtn5.setBackgroundTintList(ColorStateList.valueOf(UNSELECTED_COLOR));
        imgBtn6.setBackgroundTintList(ColorStateList.valueOf(UNSELECTED_COLOR));

        // SavedUserData.userStyle 맵 초기화
        SavedUserData.userStyle.clear();

        // 3. 버튼 리스너 설정 (토글 로직 적용 및 업데이트된 Key 사용)

        // imgBtn1: 카페·맛집
        imgBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTog[0]) {
                    // [수정됨]
                    imgBtn1.setBackgroundTintList(ColorStateList.valueOf(SELECTED_COLOR));
                    SavedUserData.userStyle.put("카페·맛집", true);
                    checkTog[0] = true;
                } else {
                    // [수정됨]
                    imgBtn1.setBackgroundTintList(ColorStateList.valueOf(UNSELECTED_COLOR));
                    SavedUserData.userStyle.put("카페·맛집", false);
                    checkTog[0] = false;
                }
            }
        });

        // imgBtn2: 액티비티
        imgBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTog[1]) {
                    // [수정됨]
                    imgBtn2.setBackgroundTintList(ColorStateList.valueOf(SELECTED_COLOR));
                    SavedUserData.userStyle.put("액티비티", true);
                    checkTog[1] = true;
                } else {
                    // [수정됨]
                    imgBtn2.setBackgroundTintList(ColorStateList.valueOf(UNSELECTED_COLOR));
                    SavedUserData.userStyle.put("액티비티", false);
                    checkTog[1] = false;
                }
            }
        });

        // imgBtn3: 문화·예술
        imgBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTog[2]) {
                    // [수정됨]
                    imgBtn3.setBackgroundTintList(ColorStateList.valueOf(SELECTED_COLOR));
                    SavedUserData.userStyle.put("문화·예술", true);
                    checkTog[2] = true;
                } else {
                    // [수정됨]
                    imgBtn3.setBackgroundTintList(ColorStateList.valueOf(UNSELECTED_COLOR));
                    SavedUserData.userStyle.put("문화·예술", false);
                    checkTog[2] = false;
                }
            }
        });

        // imgBtn4: 휴식·힐링
        imgBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTog[3]) {
                    // [수정됨]
                    imgBtn4.setBackgroundTintList(ColorStateList.valueOf(SELECTED_COLOR));
                    SavedUserData.userStyle.put("휴식·힐링", true);
                    checkTog[3] = true;
                } else {
                    // [수정됨]
                    imgBtn4.setBackgroundTintList(ColorStateList.valueOf(UNSELECTED_COLOR));
                    SavedUserData.userStyle.put("휴식·힐링", false);
                    checkTog[3] = false;
                }
            }
        });

        // imgBtn5: 자연·야외
        imgBtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTog[4]) {
                    // [수정됨]
                    imgBtn5.setBackgroundTintList(ColorStateList.valueOf(SELECTED_COLOR));
                    SavedUserData.userStyle.put("자연·야외", true);
                    checkTog[4] = true;
                } else {
                    // [수정됨]
                    imgBtn5.setBackgroundTintList(ColorStateList.valueOf(UNSELECTED_COLOR));
                    SavedUserData.userStyle.put("자연·야외", false);
                    checkTog[4] = false;
                }
            }
        });

        // imgBtn6: 쇼핑
        imgBtn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTog[5]) {
                    // [수정됨]
                    imgBtn6.setBackgroundTintList(ColorStateList.valueOf(SELECTED_COLOR));
                    SavedUserData.userStyle.put("쇼핑", true);
                    checkTog[5] = true;
                } else {
                    // [수정됨]
                    imgBtn6.setBackgroundTintList(ColorStateList.valueOf(UNSELECTED_COLOR));
                    SavedUserData.userStyle.put("쇼핑", false);
                    checkTog[5] = false;
                }
            }
        });

        // 4. 최종 버튼 설정
        finalButton = findViewById(R.id.finalbutton);
        finalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavedUserData.resultShowType = SavedUserData.SHOW_NEW;
                // 결과 화면으로 이동
                Intent intent = new Intent(ActivityPlanning5.this, ActivityPlanningResult.class);
                startActivity(intent);
            }
        });

    }
}