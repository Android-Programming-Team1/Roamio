package com.team1.roamio.view.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class FixPlanActivity extends AppCompatActivity {

    private ImageView icon;
    private ImageButton backBtn;
    private Button nextBtn;
    private EditText newLocatTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fix_plan);

        icon = findViewById(R.id.imageView7);
        backBtn = findViewById(R.id.imageButton6);
        nextBtn = findViewById(R.id.nextButton);
        newLocatTxt = findViewById(R.id.countryInput);

        Glide.with(this).asGif().load(R.drawable.romeo1).into(icon);

        SavedUserData.isBackFromFix = true;

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SavedUserData.fixResult = newLocatTxt.getText().toString();
                SavedUserData.isBackFromFix = false;
                SavedUserData.resultShowType = SavedUserData.SHOW_FIX;

                finish();
            }
        });
    }
}