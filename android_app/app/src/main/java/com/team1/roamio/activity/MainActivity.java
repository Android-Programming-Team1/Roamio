package com.team1.roamio.activity;
// In: MainActivity.java
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.team1.roamio.R;
import com.team1.roamio.utility.ai.GeminiCallback;
import com.team1.roamio.utility.ai.GeminiPro;

public class MainActivity extends AppCompatActivity {

    private GeminiPro geminiPro;
    private EditText promptEditText;
    private TextView resultTextView;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // (레이아웃 XML이 필요합니다)

        // 뷰 초기화
        promptEditText = findViewById(R.id.prompt_edit_text);
        resultTextView = findViewById(R.id.result_text_view);
        sendButton = findViewById(R.id.send_button);

        // GeminiPro 헬퍼 초기화
        geminiPro = new GeminiPro(this);

        // 버튼 클릭 리스너 설정
        sendButton.setOnClickListener(v -> {
            String prompt = promptEditText.getText().toString();
            if (prompt.isEmpty()) {
                return;
            }

            resultTextView.setText("답변 생성 중...");
            sendButton.setEnabled(false);

            // 1. callGemini 메서드 호출
            geminiPro.callGemini(prompt, new GeminiCallback() {
                @Override
                public void onSuccess(String responseText) {
                    // 2. 성공 시 UI 업데이트
                    resultTextView.setText(responseText);
                    sendButton.setEnabled(true);
                }

                @Override
                public void onError(Throwable throwable) {
                    // 3. 실패 시 UI 업데이트
                    resultTextView.setText("오류가 발생했습니다: " + throwable.getMessage());
                    Log.e("GeminiApp", "API 호출 오류", throwable);
                    sendButton.setEnabled(true);
                }
            });
        });
    }
}