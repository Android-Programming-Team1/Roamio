package com.team1.roamio.view.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.team1.roamio.R;
import com.team1.roamio.utility.planner.SavedUserData;

public class ActivityPlanning4 extends AppCompatActivity {
    ImageButton backButton;
    Button nextButton;
    EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_planning_4);
        backButton = findViewById(R.id.imageButton6);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        text = findViewById(R.id.countryInput);
        nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavedUserData.country = text.getText().toString();
                Intent intent = new Intent(ActivityPlanning4.this, ActivityPlanning1.class);
                startActivity(intent);
            }
        });

    }
}