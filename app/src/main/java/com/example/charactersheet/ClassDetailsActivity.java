package com.example.charactersheet;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ClassDetailsActivity extends AppCompatActivity {

    private int selectedZone = 0;
    private SharedViewModel sharedViewModel;


    public int getSelectedZone() {
        return selectedZone;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Do you want to exit the app?")
                .setPositiveButton("Yes", (dialog, which) -> finishAffinity())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
    public void onZoneButtonClick(int zoneNumber) {

        selectedZone = zoneNumber;

        sharedViewModel.setSelectedZone(zoneNumber);  // Update the selected zone in the SharedViewModel
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);


        Button zone1Button = findViewById(R.id.zone1Button);
        Button zone2Button = findViewById(R.id.zone2Button);
        Button zone3Button = findViewById(R.id.zone3Button);
        Button zone4Button = findViewById(R.id.zone4Button);

        zone1Button.setOnClickListener(v -> onZoneButtonClick(0));
        zone2Button.setOnClickListener(v -> onZoneButtonClick(1));
        zone3Button.setOnClickListener(v -> onZoneButtonClick(2));
        zone4Button.setOnClickListener(v -> onZoneButtonClick(3));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager2 viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new CustomPagerAdapter(this));

        // Get the selected class names from the intent
        String className1 = getIntent().getStringExtra("selectedClass1");
        String className2 = getIntent().getStringExtra("selectedClass2");

        TextView activityTitle = findViewById(R.id.activity_title);
        activityTitle.setText(className1 + " / " + className2);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Stats");
                    break;
                case 1:
                    tab.setText("Character Level");
                    break;
                case 2:
                    tab.setText("Enemy");
                    break;
            }
        }).attach();
    }

}