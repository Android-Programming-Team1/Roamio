package com.team1.roamio.view.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.team1.roamio.R;
import com.team1.roamio.utility.planner.SavedUserData;

public class ActivityPlanning2 extends AppCompatActivity {

    ImageButton backButton;
    Button lazyButton;
    Button busyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_planning_2);

        backButton = findViewById(R.id.imageButton6);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        lazyButton = findViewById(R.id.imageButton4);
        lazyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavedUserData.style = false;
                Intent intent = new Intent(ActivityPlanning2.this, ActivityPlanning3.class);
                startActivity(intent);
            }
        });
        lazyButton = findViewById(R.id.imageButton5);
        lazyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavedUserData.style = true;
                Intent intent = new Intent(ActivityPlanning2.this, ActivityPlanning3.class);
                startActivity(intent);
            }
        });

    }
}