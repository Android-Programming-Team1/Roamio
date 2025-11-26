package com.team1.roamio.view.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.team1.roamio.R;
import com.team1.roamio.data.TravelPlanData;
import com.team1.roamio.utility.database.DriveManager;
import com.team1.roamio.utility.planner.SavedUserData;
import com.team1.roamio.utility.planner.TravelPlanParser;
import com.team1.roamio.view.ui.list_view_adapter.PlanDataListViewAdapter;
import com.team1.roamio.view.ui.list_view_item.PlanDataListViewItem;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * 저장된 여행 계획 목록을 Google Drive에서 불러와 표시하는 액티비티입니다.
 * 리사이클러 뷰를 사용하여 각 계획을 표시하고, 클릭 시 해당 계획의 상세 결과 화면으로 이동합니다.
 */
public class SavedPlanDataActivity extends AppCompatActivity {

    private static final String TAG = "SavedPlanDataActivity";
    private RecyclerView recyclerView;
    private ImageButton backButton;
    private PlanDataListViewAdapter adapter;
    // itemList: <PlanDataListViewItem (summary, date), TravelPlanData (full data)>
    private List<Pair<PlanDataListViewItem, TravelPlanData>> itemList;
    private ImageView loadingIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_plan_data);

        loadingIcon = findViewById(R.id.imageView17);
        Glide.with(this).asGif().load(R.drawable.romeo3).into(loadingIcon);

        initPlanList();

        backButton = findViewById(R.id.imageButton7);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * Google Drive에서 저장된 여행 계획 파일 내용을 불러와 목록을 초기화합니다.
     */
    private void initPlanList() {
        recyclerView = findViewById(R.id.planDataSaveList);

        // 액티비티 Context를 사용하여 레이아웃 관리자를 설정합니다.
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        itemList = new ArrayList<>();

        // DriveManager 초기화
        DriveManager driveManager = new DriveManager(this);

        // 로그인 확인 및 DriveManager 초기화
        if (GoogleSignIn.getLastSignedInAccount(this) == null) {
            Log.e(TAG, "GoogleSignInAccount is null. Cannot initialize DriveManager.");
            showErrorState("구글 로그인이 필요합니다.");
            return;
        }

        driveManager.initialize(GoogleSignIn.getLastSignedInAccount(this));

        // Drive에서 'Roamio/meta/plan' 폴더의 모든 파일 ID와 내용을 비동기적으로 읽어옵니다.
        driveManager.readPlanFileContents(new DriveManager.DriveFileContentListCallback() {
            @Override
            public void onSuccess(List<Pair<String, String>> contentList) {
                itemList.clear(); // 목록을 초기화합니다.
                boolean parsingErrorOccurred = false;

                for (Pair<String, String> driveContentPair : contentList) {
                    // Pair.second (String)가 파일 내용(JSON)입니다.
                    String json = driveContentPair.second;

                    try {
                        TravelPlanData data = TravelPlanParser.parseJsonToPlanData(json);
                        Log.d(TAG, "Parsed Data Summary: " + data.getPlanSummary());

                        // 목록 아이템 생성: 제목은 요약, 부제목은 날짜로 사용합니다.
                        itemList.add(new Pair<>(
                                new PlanDataListViewItem(data.getPlanSummary(), ""),
                                data // 전체 데이터 객체를 저장하여 클릭 시 사용합니다.
                        ));

                        loadingIcon.setVisibility(View.GONE);
                    }
                    catch (JSONException e) {
                        Log.e(TAG, "JSON Parsing Error for content: " + e.getMessage(), e);
                        parsingErrorOccurred = true;
                    }
                }

                // 불러온 계획이 없는 경우 처리
                if (itemList.isEmpty()) {
                    showEmptyState("저장된 계획이 없습니다.");

                    loadingIcon.setVisibility(View.GONE);
                }
                else {
                    if (parsingErrorOccurred) {
                        Log.w(TAG, "하나 이상의 파일에서 파싱 오류가 발생했습니다.");
                    }
                    setupRecyclerView();

                    loadingIcon.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Drive Read Failure: " + e.getMessage(), e);
                showErrorState("데이터를 불러오는 중 오류가 발생했습니다.");
                loadingIcon.setVisibility(View.GONE);
            }
        });
    }

    /**
     * RecyclerView에 어댑터와 클릭 리스너를 설정합니다.
     */
    private void setupRecyclerView() {
        // Activity Context를 사용하여 어댑터 생성
        adapter = new PlanDataListViewAdapter(this, itemList);

        // 저장된 계획이 있을 때만 클릭 리스너를 설정합니다.
        if (itemList.size() > 0 && !itemList.get(0).first.getTitle().equals("저장된 계획이 없습니다.")
                && !itemList.get(0).first.getTitle().equals("데이터를 불러오는 중 오류가 발생했습니다.")) {

            adapter.setOnItemClickListener((position, item) -> {
                // 선택된 계획 데이터를 전역 변수에 설정합니다.
                SavedUserData.resultShowType = SavedUserData.SHOW_SAVED;
                SavedUserData.planData = item.second; // Pair의 second는 TravelPlanData 객체입니다.

                // 계획 결과 화면으로 이동합니다.
                Intent intent = new Intent(SavedPlanDataActivity.this, ActivityPlanningResult.class);
                startActivity(intent);
            });
        }

        recyclerView.setAdapter(adapter);

        // 구분선을 추가합니다.
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        );
    }

    /**
     * 목록이 비어있을 때 메시지를 표시합니다.
     */
    private void showEmptyState(String message) {
        itemList.clear();
        itemList.add(new Pair<>(
                new PlanDataListViewItem(message, ""),
                null
        ));
        setupRecyclerView();
    }

    /**
     * 오류 발생 시 메시지를 표시합니다.
     */
    private void showErrorState(String message) {
        itemList.clear();
        itemList.add(new Pair<>(
                new PlanDataListViewItem(message, ""),
                null
        ));
        setupRecyclerView();
    }
}