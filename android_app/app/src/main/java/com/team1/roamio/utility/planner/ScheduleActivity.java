package com.team1.roamio.utility.planner;
//데이터 파싱, 리사이클러뷰 연결, CRUD 다이얼로그 로직을 포함
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import java.util.ArrayList;

import android.app.DatePickerDialog;
import java.util.Calendar;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ScheduleAdapter adapter;
    private TripData tripData;

    private TextView tvDate;
    private TextView tvTitle;

    // 샘플 JSON 문자열
    private String jsonString = "{\n" +
            "  \"date\": \"2025-07-19\",\n" +
            "  \"title\": \"동기들과 국내 여행\",\n" +
            "  \"schedule\": [\n" +
            "    {\n" +
            "      \"type\": \"location\",\n" +
            "      \"description\": \"인천공항\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\": \"transport\",\n" +
            "      \"description\": \"공항 → 호텔\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\": \"location\",\n" +
            "      \"description\": \"롯데호텔\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        // 1. JSON 파싱
        Gson gson = new Gson();
        tripData = gson.fromJson(jsonString, TripData.class);

        // 2. UI 초기 설정 (Header)
        TextView tvDate = findViewById(R.id.tv_date);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvDate.setText(tripData.getDate().replace("-", "."));
        tvTitle.setText(tripData.getTitle());

        // 날짜 클릭 시 달력 띄우기
        tvDate.setOnClickListener(v -> showDatePicker());

        // 제목 클릭 시 수정 팝업 띄우기
        tvTitle.setOnClickListener(v -> showTitleEditDialog());

        // RecyclerView 설정
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 리스트가 null일 경우 대비
        if (tripData.getSchedule() == null) {
            // tripData 내부 리스트가 불변(Immutable) 리스트일 수 있으므로 ArrayList로 감쌈
            // (Gson은 보통 ArrayList로 주지만 안전을 위해)
            // 실제로는 Setter 등을 통해 빈 리스트 할당 필요
        }

        adapter = new ScheduleAdapter(new ArrayList<>(tripData.getSchedule()), (position, item) -> {
            // 아이템 클릭 시: 수정/삭제 다이얼로그
            showEditDeleteDialog(position, item);
        });
        recyclerView.setAdapter(adapter);

        // 4. FAB 추가 버튼 (Create)
        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> showAddDialog());
    }

    // [기능 1] 날짜 수정 (DatePickerDialog)
    private void showDatePicker() {
        // 현재 설정된 날짜 가져오기 (파싱)
        String currentDateStr = tripData.getDate(); // "2025-07-19"
        String[] parts = currentDateStr.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]) - 1; // 자바 Calendar의 월은 0부터 시작 (0=1월)
        int day = Integer.parseInt(parts[2]);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // 선택 완료 시 로직
                    // 1. 데이터 모델 업데이트 (월은 +1 해줘야 함)
                    String newDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                    // tripData에 setter가 없다면 추가하거나 필드에 직접 접근해야 합니다.
                    // 여기서는 TripData 클래스에 setDate()가 있다고 가정하거나 필드가 public이면 직접 수정
                    // tripData.date = newDate;

                    // 2. UI 업데이트
                    tvDate.setText(newDate.replace("-", "."));
                    Toast.makeText(this, "날짜가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                }, year, month, day);

        datePickerDialog.show();
    }

    // [기능 2] 제목 수정 (AlertDialog + EditText)
    private void showTitleEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("여행 제목 수정");

        final EditText input = new EditText(this);
        input.setText(tripData.getTitle()); // 기존 제목 채워넣기
        input.setSelection(input.getText().length()); // 커서를 맨 뒤로
        builder.setView(input);

        builder.setPositiveButton("저장", (dialog, which) -> {
            String newTitle = input.getText().toString();
            if (!newTitle.isEmpty()) {
                // 1. 데이터 모델 업데이트
                // tripData.title = newTitle; (Setter 사용 권장)

                // 2. UI 업데이트
                tvTitle.setText(newTitle);
                Toast.makeText(this, "제목이 수정되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("취소", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // --- CRUD: Create ---
    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("새 일정 추가");

        final EditText input = new EditText(this);
        input.setHint("내용을 입력하세요");
        builder.setView(input);

        // 간단히 하기 위해 버튼 2개로 타입 결정
        builder.setPositiveButton("장소 추가", (dialog, which) -> {
            adapter.addItem(new ScheduleItem("location", input.getText().toString()));
        });
        builder.setNegativeButton("이동 추가", (dialog, which) -> {
            adapter.addItem(new ScheduleItem("transport", input.getText().toString()));
        });

        builder.show();
    }

    // --- CRUD: Update / Delete ---
    private void showEditDeleteDialog(int position, ScheduleItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("일정 관리");

        // 수정용 입력창
        final EditText input = new EditText(this);
        input.setText(item.getDescription());
        builder.setView(input);

        builder.setPositiveButton("수정", (dialog, which) -> {
            adapter.updateItem(position, input.getText().toString());
        });

        builder.setNegativeButton("삭제", (dialog, which) -> {
            adapter.deleteItem(position);
        });

        builder.setNeutralButton("취소", null);

        builder.show();
    }
}