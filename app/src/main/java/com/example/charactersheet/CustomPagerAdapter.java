package com.example.charactersheet;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class CustomPagerAdapter extends FragmentStateAdapter {
    private final StatsFragment statsFragment;

    public CustomPagerAdapter(FragmentActivity fa) {
        super(fa);
        statsFragment = new StatsFragment();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return statsFragment;
            case 1:
                return new CharacterLevelFragment();
            case 2:
                return new EnemyFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public StatsFragment getStatsFragment() {
        return statsFragment;
    }

}
