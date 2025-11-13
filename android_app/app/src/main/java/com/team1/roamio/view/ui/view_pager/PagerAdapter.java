package com.team1.roamio.view.ui.view_pager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class PagerAdapter extends FragmentStateAdapter {

    private final ArrayList<Fragment> fragments = new ArrayList<>();

    public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    public void addFragment(Fragment fragment) {
        fragments.add(fragment);
        notifyItemInserted(fragments.size() - 1);
    }

    public void removeFragment() {
        if (!fragments.isEmpty()) {
            int lastIndex = fragments.size() - 1;
            fragments.remove(lastIndex);
            notifyItemRemoved(lastIndex);
        }
    }
}
