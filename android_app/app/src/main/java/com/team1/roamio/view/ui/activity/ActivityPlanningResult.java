package com.team1.roamio.view.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.team1.roamio.R;
import com.team1.roamio.data.TravelPlanData;
import com.team1.roamio.utility.database.DriveManager;
import com.team1.roamio.utility.planner.PlanBuildCallback;
import com.team1.roamio.utility.planner.SavedUserData;
import com.team1.roamio.utility.planner.TravelPlanBuilder;
import com.team1.roamio.utility.planner.TravelPlanParser;
import com.team1.roamio.view.ui.list_view_adapter.PlanDataResultListViewAdapter;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ActivityPlanningResult extends AppCompatActivity {

    RecyclerView resultList;
    ImageButton backButton;
    ImageButton saveButton;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private ImageView loadingIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_planning_result);

        loadingIcon = findViewById(R.id.imageView16);
        Glide.with(this).asGif().load(R.drawable.romeo3).into(loadingIcon);

        sharedPreferences = getSharedPreferences("plan", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        resizeWithNavBar();

        resultList = findViewById(R.id.result_list);
        backButton = findViewById(R.id.imageButton6);
        saveButton = findViewById(R.id.imageButton2);

        backButton.setOnClickListener(v -> finish());

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    DriveManager driveManager = new DriveManager(ActivityPlanningResult.this);
                    driveManager.initialize(GoogleSignIn.getLastSignedInAccount(ActivityPlanningResult.this));

                    driveManager.savePlanJson(SavedUserData.planData.getPlanSummary(), TravelPlanParser.parsePlanDataToJson(SavedUserData.planData).toString());

                    Toast.makeText(ActivityPlanningResult.this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
                }
                catch (JSONException e) {
                    Log.e("error", e.getMessage());
                    Toast.makeText(ActivityPlanningResult.this, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        if (SavedUserData.isShowSavedData) {
            saveButton.setVisibility(View.GONE);
            showSavedData();
        }
        else {
            saveButton.setVisibility(View.VISIBLE);
            getResult();
        }
    }

    private void resizeWithNavBar() {
        View rootView = findViewById(R.id.main);

        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            int bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;

            v.setPadding(0, 0, 0, bottomInset);

            return insets;
        });
    }

    public void showSavedData() {
        TravelPlanData planData = SavedUserData.planData;

        resultList.setLayoutManager(new LinearLayoutManager(ActivityPlanningResult.this));

        List<Pair<String, String>> data = new ArrayList<>();
        boolean isFirst = true;

        for (var dailyPlan : planData.getDailyPlans()) {
            for (var activity : dailyPlan.getActivities()) {
                if(isFirst) isFirst = false;
                else data.add(new Pair<>(activity.getTransport().getEstimatedTime(), activity.getTransport().getGoogleMapLink()));

                data.add(new Pair<>(activity.getTitle(), null));
            }

        }

        PlanDataResultListViewAdapter adapter = new PlanDataResultListViewAdapter(data);

        adapter.setOnItemClickListener((position, item) -> {
            if (item.second == null) return;

            String url = item.second;

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

        resultList.setAdapter(adapter);
        loadingIcon.setVisibility(View.GONE);
    }

    public void getResult() {
        try {
            String styleStr = "";

            for (var entry : SavedUserData.userStyle.entrySet()) {
                if (entry.getValue()) styleStr += entry.getKey() + ", ";
            }

            TravelPlanBuilder.planDataBuilder()
                    .setStayDuration(SavedUserData.day)
                    .setIsHardPlan(SavedUserData.style)
                    .setHotelLocation(SavedUserData.hotelLocation)
                    .setVisitCountry(SavedUserData.country)
                    .setPreference(styleStr)
                    .build(new PlanBuildCallback() {
                        @Override
                        public void onSuccess(TravelPlanData planData) {
                            SavedUserData.planData = planData;

                            resultList.setLayoutManager(new LinearLayoutManager(ActivityPlanningResult.this));

                            List<Pair<String, String>> data = new ArrayList<>();
                            boolean isFirst = true;

                            for (var dailyPlan : planData.getDailyPlans()) {
                                for (var activity : dailyPlan.getActivities()) {
                                    if(isFirst) isFirst = false;
                                    else data.add(new Pair<>(activity.getTransport().getEstimatedTime(), activity.getTransport().getGoogleMapLink()));

                                    data.add(new Pair<>(activity.getTitle(), null));
                                }

                            }

                            PlanDataResultListViewAdapter adapter = new PlanDataResultListViewAdapter(data);

                            adapter.setOnItemClickListener((position, item) -> {
                                if (item.second == null) return;

                                String url = item.second;

                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(intent);
                            });



                            resultList.setAdapter(adapter);

                            loadingIcon.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            resultList.setLayoutManager(new LinearLayoutManager(ActivityPlanningResult.this));

                            List<Pair<String, String>> data = new ArrayList<>();

                            data.add(new Pair<>("api 오류로 현재 기능을 사용할 수 없습니다.", null));

                            PlanDataResultListViewAdapter adapter = new PlanDataResultListViewAdapter(data);
                            resultList.setAdapter(adapter);

                            loadingIcon.setVisibility(View.GONE);
                        }
                    });
        }
        catch (JSONException e) {
            Log.e("error", e.getMessage());
            Toast.makeText(ActivityPlanningResult.this, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}