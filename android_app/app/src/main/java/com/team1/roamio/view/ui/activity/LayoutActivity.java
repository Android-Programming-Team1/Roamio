package com.team1.roamio.view.ui.activity;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.team1.roamio.R;
import com.team1.roamio.view.ui.view_pager.PagerAdapter;
import com.team1.roamio.view.ui.fragment.AIRecommendFragment;
import com.team1.roamio.view.ui.fragment.HomeFragment;
import com.team1.roamio.view.ui.fragment.MyPageFragment;
import com.team1.roamio.view.ui.fragment.StampListFragment;

public class LayoutActivity extends AppCompatActivity {

    private ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_layout);

        resizeWithNavBar();

        initViewPager2();
    }

    // 네비게이션 바가 툴바를 가리는 문제 해결
    private void resizeWithNavBar() {
        View rootView = findViewById(R.id.layout_activity_root);

        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            int bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;

            v.setPadding(0, 0, 0, bottomInset);

            return insets;
        });
    }

    private void initViewPager2() {
        ViewPager2 viewPager2 = findViewById(R.id.view_page);
        TabLayout tabLayout = findViewById(R.id.menu_tab);

        PagerAdapter v2pAdapter = new PagerAdapter(this);

        HomeFragment homeFragment = new HomeFragment();
        MyPageFragment myPageFragment = new MyPageFragment();
        AIRecommendFragment aiRecommendFragment = new AIRecommendFragment();
        StampListFragment stampListFragment = new StampListFragment();

        v2pAdapter.addFragment(homeFragment);
        v2pAdapter.addFragment(aiRecommendFragment);
        v2pAdapter.addFragment(stampListFragment);
        v2pAdapter.addFragment(myPageFragment);

        viewPager2.setOffscreenPageLimit(4);

        viewPager2.setAdapter(v2pAdapter);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });

        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setIcon(R.drawable.ic_home);
                            break;
                        case 1:
                            tab.setIcon(R.drawable.ic_map);
                            break;
                        case 2:
                            tab.setIcon(R.drawable.ic_stamp);
                            break;
                        case 3:
                            tab.setIcon(R.drawable.ic_mypage);
                            break;
                    }
                }).attach();
    }
}