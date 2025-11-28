package com.team1.roamio.view.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.team1.roamio.R;

import java.util.Random;

public class PlanSettingActivity extends AppCompatActivity {

    ImageButton backButton;
    Button nextButton;

    private ImageView icon;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_plan_setting);

        nextButton = findViewById(R.id.btnIntroduce);
        backButton = findViewById(R.id.imageButton3);
        icon = findViewById(R.id.imageView9);
        textView = findViewById(R.id.textView6);

        setTextView();

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

    private void setTextView() {
        int rand = (new Random()).nextInt(8);

        switch (rand) {
            case 0:
                textView.setText("안녕!\n나는 당신만의 여행비서 로미오예요!\n나에게 당신을 조금만 알려줄래요?");
                break;

            case 1:
                textView.setText("안녕!\n나는 당신의 여행 동반자 로미오예요!\n당신을 조금 알려주면 더 좋은 여행을 준비해줄게요.");
                break;

            case 2:
                textView.setText("반가워요!\n여행 길잡이 로미오가 왔어요 :)\n당신에 대해 조금 알려주실래요?");
                break;

            case 3:
                textView.setText("안녕하세요!\n당신의 여행을 특별하게 만들어줄 로미오예요.\n먼저 당신에 대해 조금 알려주세요!");
                break;

            case 4:
                textView.setText("어서오세요!\n로미오는 항상 당신의 여행을 응원하고 있어요.\n더 잘 도와드리려면 당신을 알고 싶어요 :)");
                break;

            case 5:
                textView.setText("안녕!\n당신의 여행을 책임질 로미오예요!\n취향을 조금만 알려주면 멋진 여행을 만들어드릴게요.");
                break;

            case 6:
                textView.setText("만나서 반가워요!\n여행비서 로미오가 대기 중이에요.\n당신을 더 잘 이해할 수 있게 몇 가지 알려주실래요?");
                break;

            default:
                textView.setText("환영해요!\n당신만의 여행 파트너 로미오입니다 :)\n당신이 어떤 분인지 살짝 알려주면 여행을 딱 맞게 준비해드릴게요.");
                break;
        }

    }
}