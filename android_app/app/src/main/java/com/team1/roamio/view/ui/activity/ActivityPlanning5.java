package com.team1.roamio.view.ui.activity;

import android.content.Intent;
import android.graphics.Color;
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

public class ActivityPlanning5 extends AppCompatActivity {
    ImageButton backButton;
    Button finalButton;
    Button imgBtn1;
    Button imgBtn2;
    Button imgBtn3;
    Button imgBtn4;
    Button imgBtn5;
    Button imgBtn6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_planning_5);
        backButton = findViewById(R.id.imageButton6);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgBtn1 = findViewById(R.id.stylebutton1);
        imgBtn2 = findViewById(R.id.stylebutton2);
        imgBtn3 = findViewById(R.id.stylebutton3);
        imgBtn4 = findViewById(R.id.stylebutton4);
        imgBtn5 = findViewById(R.id.stylebutton5);
        imgBtn6 = findViewById(R.id.stylebutton6);

        imgBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgBtn1.setBackgroundColor(Color.BLUE);
            }
        });
        imgBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgBtn2.setBackgroundColor(Color.BLUE);
            }
        });
        imgBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgBtn3.setBackgroundColor(Color.BLUE);
            }
        });
        imgBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgBtn4.setBackgroundColor(Color.BLUE);
            }
        });
        imgBtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgBtn5.setBackgroundColor(Color.BLUE);
            }
        });
        imgBtn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgBtn6.setBackgroundColor(Color.BLUE);
            }
        });

        finalButton = findViewById(R.id.finalbutton);
        finalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityPlanning5.this, ActivityPlanningResult.class);
                startActivity(intent);
            }
        });

    }

}