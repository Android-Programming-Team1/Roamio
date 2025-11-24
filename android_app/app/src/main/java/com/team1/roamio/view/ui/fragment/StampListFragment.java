package com.team1.roamio.view.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.team1.roamio.R;
import com.team1.roamio.data.CountryDao;
import com.team1.roamio.data.Stamp;
import com.team1.roamio.data.StampDao;
import com.team1.roamio.view.ui.activity.AddStampActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StampListFragment extends Fragment {

    private LinearLayout stampListContainer;
    private StampDao stampDao;
    private CountryDao countryDao;

    // ★ 중요: 뷰와 변수들을 초기화하는 onCreateView가 반드시 필요합니다.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_stamp_list, container, false);

        stampListContainer = view.findViewById(R.id.stampListContainer);

        // DAO 초기화
        stampDao = new StampDao(requireContext());
        countryDao = new CountryDao(requireContext());

        // 최초 로딩 (onResume에서도 호출되지만, 뷰 생성 시점에도 호출 필요)
        loadStamps(inflater);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 액티비티가 닫히고 돌아올 때 목록 갱신 (DB 변경사항 반영)
        if (stampListContainer != null) {
            loadStamps(getLayoutInflater());
        }
    }

    /** 스탬프 목록을 불러오고, 맨 아래에 추가 버튼을 붙이는 함수 */
    private void loadStamps(LayoutInflater inflater) {
        // 1. 기존 뷰 초기화 (중복 방지)
        stampListContainer.removeAllViews();

        // 2. DB에서 저장된 모든 스탬프 가져오기
        List<Stamp> stampList = stampDao.getAllStamps();

        // 3. 저장된 스탬프 개수만큼 반복하며 뷰 추가
        for (Stamp stamp : stampList) {
            View item = inflater.inflate(R.layout.item_stamp, stampListContainer, false);
            applyStampData(item, stamp); // 데이터 바인딩
            stampListContainer.addView(item); // 컨테이너에 추가
        }

        // 4. 리스트의 맨 마지막에 '추가하기' 버튼 뷰 생성 및 추가
        View addItem = inflater.inflate(R.layout.item_stamp_add, stampListContainer, false);
        setupAddButton(addItem); // 클릭 이벤트 연결
        stampListContainer.addView(addItem); // 컨테이너의 가장 마지막에 추가됨
    }

    /** 추가하기 버튼 클릭 로직 (중복된 메서드 제거 후 하나만 남김) */
    private void setupAddButton(View item) {
        ImageButton img = item.findViewById(R.id.add_stamp_image); // item_stamp_add.xml의 ID

        img.setOnClickListener(v -> {
            // GPS 액티비티로 이동
            Intent intent = new Intent(requireContext(), AddStampActivity.class);
            startActivity(intent);
        });
    }

    /** 일반 스탬프 아이템 데이터 연결 */
    private void applyStampData(View item, Stamp stamp) {
        ImageButton imgButton = item.findViewById(R.id.stamp_image);
        TextView countryText = item.findViewById(R.id.text_country);
        TextView dateText = item.findViewById(R.id.text_date);
        TextView descText = item.findViewById(R.id.text_desc);

        // --- 국가 이름 설정 ---
        String countryName = countryDao.getCountryNameById(stamp.getCountryId());
        countryText.setText(countryName != null ? countryName : "Unknown");

        // --- 날짜 설정 ---
        try {
            Date date = new Date(stamp.getStampedAt());
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
            dateText.setText(sdf.format(date));
        } catch (Exception e) {
            dateText.setText("Invalid Date");
        }

        // --- 이미지 설정 ---
        String stampName = stamp.getImageName();
        Context context = getContext();
        if (context != null && stampName != null) {
            // DB에 저장된 이름(예: stamp_01)으로 drawable ID 찾기
            int resId = context.getResources().getIdentifier(
                    stampName, "drawable", context.getPackageName()
            );

            // 만약 못 찾으면 기본 이미지 stamp_00
            if (resId == 0) resId = R.drawable.stamp_00;

            imgButton.setImageResource(resId);
        }

        // --- 설명 숨김 (필요시 사용) ---
        descText.setVisibility(View.GONE);
    }
}