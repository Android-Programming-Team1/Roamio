package com.team1.roamio.view.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.api.services.drive.model.File;
import com.team1.roamio.R;
import com.team1.roamio.data.CountryDao;
import com.team1.roamio.data.Stamp;
import com.team1.roamio.data.StampDao;
import com.team1.roamio.utility.database.DriveManager;
import com.team1.roamio.utility.stamp.StampJsonParser;
import com.team1.roamio.view.ui.activity.AddStampActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StampListFragment extends Fragment {

    private LinearLayout stampListContainer;
    private CountryDao countryDao;
    private ImageView icon;

    // ★ 중요: 뷰와 변수들을 초기화하는 onCreateView가 반드시 필요합니다.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_stamp_list, container, false);

        countryDao = new CountryDao(getActivity());

        stampListContainer = view.findViewById(R.id.stampListContainer);
        icon = view.findViewById(R.id.imageView21);

        Glide.with(getActivity()).asGif().load(R.drawable.romeo3).into(icon);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 액티비티가 닫히고 돌아올 때 목록 갱신 (DB 변경사항 반영)
        if (stampListContainer != null) {
            icon.setVisibility(View.VISIBLE);
            loadStamps(getLayoutInflater());
        }
    }

    /** 스탬프 목록을 불러오고, 맨 아래에 추가 버튼을 붙이는 함수 */
    private void loadStamps(LayoutInflater inflater) {
        // 1. 기존 뷰 초기화 (중복 방지)
        stampListContainer.removeAllViews();

        DriveManager driveManager = new DriveManager(getActivity());
        driveManager.initialize(GoogleSignIn.getLastSignedInAccount(getActivity()));

        // 2. DB에서 저장된 모든 스탬프 가져오기
        driveManager.readStampFileContents(new DriveManager.DriveFileContentListCallback() {
            @Override
            public void onSuccess(List<Pair<String, String>> contentList) {
                List<Stamp> stampList = new ArrayList<>();

                for(var content : contentList) {
                    stampList.add(StampJsonParser.fromJson(content.second));
                }

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
                icon.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

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
    /** 일반 스탬프 아이템 데이터 연결 */
    private void applyStampData(View item, Stamp stamp) {
        ImageButton imgButton = item.findViewById(R.id.stamp_image);
        TextView countryText = item.findViewById(R.id.text_country);
        TextView dateText = item.findViewById(R.id.text_date);
        TextView descText = item.findViewById(R.id.text_desc);

        // 국가 이름 설정
        String countryName = countryDao.getCountryNameById(stamp.getCountryId());
        countryText.setText(countryName != null ? countryName : "Unknown");

        // 날짜 설정 (밀리초 -> 날짜 변환)
        try {
            long timeValue = stamp.getStampedAt();
            // 기존 8자리 데이터(20240308)와 새 데이터(밀리초) 호환 처리
            if (String.valueOf(timeValue).length() <= 8) {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd", Locale.US);
                Date date = inputFormat.parse(String.valueOf(timeValue));
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
                dateText.setText(outputFormat.format(date));
            } else {
                Date date = new Date(timeValue);
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
                dateText.setText(sdf.format(date));
            }
        } catch (Exception e) {
            dateText.setText("Invalid Date");
        }

        // 이미지 설정
        String stampName = stamp.getImageName();

        Context context = getContext();
        if (context != null && stampName != null && !stampName.isEmpty()) {

            if (stampName.contains(".")) {
                stampName = stampName.substring(0, stampName.lastIndexOf("."));
            }

            // "seoul" 문자열 -> R.drawable.seoul ID 변환
            int resId = context.getResources().getIdentifier(
                    stampName, "drawable", context.getPackageName()
            );

            // 이미지가 있으면 출력, 없으면 기본 이미지(stamp_00)
            if (resId != 0) {
                imgButton.setImageResource(resId);
            } else {
                imgButton.setImageResource(R.drawable.stamp_00);
            }
        } else {
            imgButton.setImageResource(R.drawable.stamp_00);
        }

        descText.setVisibility(View.GONE);
    }
}