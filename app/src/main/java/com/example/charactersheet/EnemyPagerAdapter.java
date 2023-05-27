package com.example.charactersheet;

import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class EnemyPagerAdapter extends FragmentStateAdapter {

    private final SparseArray<Fragment> mFragments = new SparseArray<>();

    public EnemyPagerAdapter(@NonNull EnemyFragment fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new EnemySubFragment1();
                break;
            case 1:
                fragment = new EnemySubFragment2();
                break;
            case 2:
                fragment = new EnemySubFragment3();
                break;
            default:
                throw new IllegalStateException("Unexpected position: " + position);
        }
        mFragments.put(position, fragment);
        return fragment;
    }


    @Override
    public int getItemCount() {
        return 3;
    }

    @Nullable
    public Fragment getFragment(int position) {
        return mFragments.get(position);
    }
}

