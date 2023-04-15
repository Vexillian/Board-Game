package com.example.charactersheet;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

public class StatsActivity extends AppCompatActivity {

    private final HashMap<String, Integer> stats = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        initDefaultStats();
        // Update stats based on selected classes
        // ...

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
        for (String stat : stats.keySet()) {
            // Find UI elements by ID
            TextView statValueTextView = findViewById(getResources().getIdentifier(stat + "Value", "id", getPackageName()));
            Button plusButton = findViewById(getResources().getIdentifier(stat + "Plus", "id", getPackageName()));
            Button minusButton = findViewById(getResources().getIdentifier(stat + "Minus", "id", getPackageName()));

            // Set initial value for stat
            statValueTextView.setText(String.valueOf(stats.get(stat)));

            // Set button listeners
            plusButton.setOnClickListener(view -> {
                int currentValue = Integer.parseInt(statValueTextView.getText().toString());
                statValueTextView.setText(String.valueOf(currentValue + 1));
                stats.put(stat, currentValue + 1);
            });

            minusButton.setOnClickListener(view -> {
                int currentValue = Integer.parseInt(statValueTextView.getText().toString());
                if (currentValue > 0) {
                    statValueTextView.setText(String.valueOf(currentValue - 1));
                    stats.put(stat, currentValue - 1);
                }
            });
        }
    }
}
