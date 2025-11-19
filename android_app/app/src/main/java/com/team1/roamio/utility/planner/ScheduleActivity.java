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

public class ScheduleActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ScheduleAdapter adapter;
    private TripData tripData;

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

        // 3. RecyclerView 설정
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