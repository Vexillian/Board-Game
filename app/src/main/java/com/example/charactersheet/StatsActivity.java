package com.example.charactersheet;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StatsActivity extends AppCompatActivity {

    // ...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        initDefaultStats();
        // Update stats based on selected classes
        // ...

        setupUI();
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
