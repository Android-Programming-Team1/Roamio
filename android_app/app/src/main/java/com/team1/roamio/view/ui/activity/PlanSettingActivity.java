package com.team1.roamio.view.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.team1.roamio.R;

public class PlanSettingActivity extends AppCompatActivity {

    ImageButton backButton;
    MaterialButton nextButton;

    private ImageView icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_plan_setting);

        nextButton = findViewById(R.id.btnIntroduce);
        backButton = findViewById(R.id.imageButton3);
        icon = findViewById(R.id.imageView9);

        Glide.with(this).asGif().load(R.drawable.romeo1).into(icon);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlanSettingActivity.this, ActivityPlanning4.class);
                startActivity(intent);
            }
        });

    }
}