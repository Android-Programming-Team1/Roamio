package com.team1.roamio.view.ui.fragment;

import android.os.Bundle;
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

import com.team1.roamio.R;
import com.team1.roamio.data.Stamp;
import com.team1.roamio.data.StampDao;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class StampListFragment extends Fragment {

    private LinearLayout stampListContainer;
    private StampDao stampDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_stamp_list, container, false);

        stampListContainer = view.findViewById(R.id.stampListContainer);
        stampDao = new StampDao(requireContext());

        loadStamps(inflater);

        return view;
    }

    /** DB에서 스탬프 로드 후 UI 표시 */
    private void loadStamps(LayoutInflater inflater) {
        stampListContainer.removeAllViews();

        List<Stamp> stampList = stampDao.getAllStamps();

        int count = Math.min(stampList.size(), 3);

        for (int i = 0; i < count; i++) {
            View item = inflater.inflate(R.layout.item_stamp, stampListContainer, false);
            applyStampData(item, stampList.get(i));
            stampListContainer.addView(item);
        }

        // 마지막 “추가하기”
        View addItem = inflater.inflate(R.layout.item_stamp_add, stampListContainer, false);
        setupAddButton(addItem);
        stampListContainer.addView(addItem);
    }

    /** 스탬프 목록 UI 적용 */
    private void applyStampData(View item, Stamp stamp) {
        ImageButton img = item.findViewById(R.id.stamp_image);
        TextView country = item.findViewById(R.id.text_country);
        TextView date = item.findViewById(R.id.text_date);
        TextView desc = item.findViewById(R.id.text_desc);

        // 이미지가 없으면 기본 이미지 출력
        if (stamp.getImageResId() != 0) {
            img.setImageResource(stamp.getImageResId());
        } else {
            img.setImageResource(R.drawable.stamp_00);
        }

        country.setText("ID: " + stamp.getCountryId());

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        date.setText(sdf.format(stamp.getStampedAt()));

        desc.setText(stamp.getImageUrl() != null ? stamp.getImageUrl() : "No description");
    }

    /** 추가하기 버튼 */
    private void setupAddButton(View item) {
        ImageButton img = item.findViewById(R.id.add_stamp_image);

        img.setOnClickListener(v -> {
            // 기본값으로 Stamp 추가
            Stamp newStamp = new Stamp(
                    0,
                    1,                          // 기본 countryId
                    System.currentTimeMillis(), // 시간
                    R.drawable.stamp_00,        // 기본 이미지
                    null,
                    "New Stamp"
            );

            stampDao.insertStamp(newStamp);
            loadStamps(getLayoutInflater());
        });
    }
}
