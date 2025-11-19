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

public class ActivityPlanning1 extends AppCompatActivity {
    ImageButton backButton;
    Button onetwoDay;
    Button twothreeDay;
    Button threefourDay;
    Button fourfiveDay;
    Button fivesixDay;
    Button sixsevenDay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_planning_1);

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
        threefourDay = findViewById(R.id.btn1);
        threefourDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavedUserData.day = 4;
                Intent intent = new Intent(ActivityPlanning1.this, ActivityPlanning2.class);
                startActivity(intent);
            }
        });
        fourfiveDay = findViewById(R.id.btn1);
        fourfiveDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavedUserData.day = 5;
                Intent intent = new Intent(ActivityPlanning1.this, ActivityPlanning2.class);
                startActivity(intent);
            }
        });
        fivesixDay = findViewById(R.id.btn1);
        fivesixDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavedUserData.day = 6;
                Intent intent = new Intent(ActivityPlanning1.this, ActivityPlanning2.class);
                startActivity(intent);
            }
        });
        sixsevenDay = findViewById(R.id.btn1);
        sixsevenDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavedUserData.day = 7;
                Intent intent = new Intent(ActivityPlanning1.this, ActivityPlanning2.class);
                startActivity(intent);
            }
        });

    }

}