package com.example.charactersheet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class StatsActivity extends AppCompatActivity {
    private HashMap<String, Integer[]> getResourceIds() {
        HashMap<String, Integer[]> resourceIds = new HashMap<>();
        resourceIds.put("MaxHP", new Integer[]{R.id.MaxHPValue, R.id.MaxHPPlus, R.id.MaxHPMinus});
        resourceIds.put("CurrentHP", new Integer[]{R.id.CurrentHPValue, R.id.CurrentHPPlus, R.id.CurrentHPMinus});
        resourceIds.put("Might", new Integer[]{R.id.MightValue, R.id.MightPlus, R.id.MightMinus});
        resourceIds.put("Intuition", new Integer[]{R.id.IntuitionValue, R.id.IntuitionPlus, R.id.IntuitionMinus});
        resourceIds.put("Agility", new Integer[]{R.id.AgilityValue, R.id.AgilityPlus, R.id.AgilityMinus});
        resourceIds.put("Precision", new Integer[]{R.id.PrecisionValue, R.id.PrecisionPlus, R.id.PrecisionMinus});
        resourceIds.put("Tactics", new Integer[]{R.id.TacticsValue, R.id.TacticsPlus, R.id.TacticsMinus});
        resourceIds.put("Introspection", new Integer[]{R.id.IntrospectionValue, R.id.IntrospectionPlus, R.id.IntrospectionMinus});
        resourceIds.put("PhysicalResistance", new Integer[]{R.id.PhysicalResistanceValue, R.id.PhysicalResistancePlus, R.id.PhysicalResistanceMinus});
        resourceIds.put("MagicalResistance", new Integer[]{R.id.MagicalResistanceValue, R.id.MagicalResistancePlus, R.id.MagicalResistanceMinus});
        return resourceIds;
    }


    private final HashMap<String, Integer> stats = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("selectedClasses")) {
            ArrayList<String> selectedClasses = intent.getStringArrayListExtra("selectedClasses");
            if (selectedClasses.size() >= 2) {
                String title = selectedClasses.get(0) + " / " + selectedClasses.get(1);
                setTitle(title);
            } else {
                // Handle the case when there are fewer than two elements in the list.
                // For example, set the title to a default value.
                setTitle("Character Sheet");
            }
        }


        initDefaultStats();
        // Update stats based on selected classes

        setupUI();
    }

    private void initDefaultStats() {
        stats.put("MaxHP", 5);
        stats.put("CurrentHP", 5);
        stats.put("Might", 5);
        stats.put("Intuition", 5);
        stats.put("Agility", 1);
        stats.put("Precision", 1);
        stats.put("Tactics", 3);
        stats.put("Introspection", 0);
        stats.put("PhysicalResistance", 0);
        stats.put("MagicalResistance", 0);
    }

    private void setupUI() {
        HashMap<String, Integer[]> resourceIds = getResourceIds();

        for (String stat : stats.keySet()) {
            // Get UI elements by ID
            Integer[] ids = resourceIds.get(stat);
            if (ids == null || ids.length != 3) {
                continue;
            }

            TextView statValueTextView = findViewById(ids[0]);
            Button plusButton = findViewById(ids[1]);
            Button minusButton = findViewById(ids[2]);

            // Set initial value for stat
            statValueTextView.setText(String.valueOf(stats.get(stat)));

            // Set button listeners
            plusButton.setOnClickListener(view -> {
                int currentValue = Integer.parseInt(statValueTextView.getText().toString());
                int maxValue = (stat.equals("Tactics") || stat.equals("Introspection")) ? 6 : 20;
                if (currentValue < maxValue) {
                    statValueTextView.setText(String.valueOf(currentValue + 1));
                    stats.put(stat, currentValue + 1);
                }
            });

            minusButton.setOnClickListener(view -> {
                int currentValue = Integer.parseInt(statValueTextView.getText().toString());
                if (currentValue > 0) {
                    statValueTextView.setText(String.valueOf(currentValue - 1));
                    stats.put(stat, currentValue - 1);
                }
            });

            // Change text color if Agility or Precision goes past 15
            if (stat.equals("Agility") || stat.equals("Precision")) {
                statValueTextView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        int value = Integer.parseInt(editable.toString());
                        if (value > 15) {
                            statValueTextView.setTextColor(getResources().getColor(android.R.color.holo_orange_light, getTheme()));
                        } else {
                            statValueTextView.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
                        }
                    }
                });
            }
        }
    }
}