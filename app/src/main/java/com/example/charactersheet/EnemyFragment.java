package com.example.charactersheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class EnemyFragment extends Fragment {
    // Make EnemyPagerAdapter a member variable
    private static EnemyPagerAdapter enemyPagerAdapter;

    public EnemyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_enemy, container, false);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        ViewPager2 viewPager = view.findViewById(R.id.view_pager);

        // Initialize the adapter
        enemyPagerAdapter = new EnemyPagerAdapter(this);
        viewPager.setAdapter(enemyPagerAdapter);

        // Connect the TabLayout with ViewPager
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Slot 1");
                            break;
                        case 1:
                            tab.setText("Slot 2/Boss");
                            break;
                        case 2:
                            tab.setText("Slot 3");
                            break;
                    }
                }
        ).attach();

        return view;
    }

    // Getter method for enemyPagerAdapter
    public static EnemyPagerAdapter getEnemyPagerAdapter() {
        return enemyPagerAdapter;
    }
}
