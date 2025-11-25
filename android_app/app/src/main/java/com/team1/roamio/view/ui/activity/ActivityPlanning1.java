package com.team1.roamio.view.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.team1.roamio.R;
import com.team1.roamio.utility.planner.SavedUserData;

public class ActivityPlanning1 extends AppCompatActivity {
    ImageButton backButton;
    Button onetwoDay;
    Button twothreeDay;
    Button threefourDay;
    Button fourfiveDay;
    Button fivesixDay;
    Button sixsevenDay;

    private ImageView icon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_planning_1);

        icon = findViewById(R.id.imageView7);
        Glide.with(this).asGif().load(R.drawable.romeo1).into(icon);

        backButton = findViewById(R.id.imageButton6);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        onetwoDay = findViewById(R.id.btn1);
        onetwoDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavedUserData.day = 2;
                Intent intent = new Intent(ActivityPlanning1.this, ActivityPlanning2.class);
                startActivity(intent);
            }
        });
        twothreeDay = findViewById(R.id.btn2);
        twothreeDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavedUserData.day = 3;
                Intent intent = new Intent(ActivityPlanning1.this, ActivityPlanning2.class);
                startActivity(intent);
            }
        });
        threefourDay = findViewById(R.id.btn3);
        threefourDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavedUserData.day = 4;
                Intent intent = new Intent(ActivityPlanning1.this, ActivityPlanning2.class);
                startActivity(intent);
            }
        });
        fourfiveDay = findViewById(R.id.btn4);
        fourfiveDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavedUserData.day = 5;
                Intent intent = new Intent(ActivityPlanning1.this, ActivityPlanning2.class);
                startActivity(intent);
            }
        });
        fivesixDay = findViewById(R.id.btn5);
        fivesixDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavedUserData.day = 6;
                Intent intent = new Intent(ActivityPlanning1.this, ActivityPlanning2.class);
                startActivity(intent);
            }
        });
        sixsevenDay = findViewById(R.id.btn6);
        sixsevenDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavedUserData.day = 7;
                Intent intent = new Intent(ActivityPlanning1.this, ActivityPlanning2.class);
                startActivity(intent);
            }
        });

    }

    public static class PlanSettingActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_plan_setting);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }
}