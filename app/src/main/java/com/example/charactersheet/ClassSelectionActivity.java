package com.example.charactersheet;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClassSelectionActivity extends BaseActivity {
    private MaterialButton firstSelectedButton;
    private MaterialButton secondSelectedButton;
    private Drawable firstButtonOriginalBackground;
    private Drawable secondButtonOriginalBackground;
    private TextView firstClassSummary;
    private TextView secondClassSummary;
    private TextView firstClassStats;
    private TextView secondClassStats;
    private TextView totalClassStats;
    private TextView className1;
    private TextView className2;
    private TextView totalLabel;

    private List<String> statsOrder;

    private final ArrayList<HashMap<String, Integer>> selectedClassesStats = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_selection);

        statsOrder = Arrays.asList(
                getString(R.string.max_hp),
                getString(R.string.current_hp),
                getString(R.string.might),
                getString(R.string.agility),
                getString(R.string.precision),
                getString(R.string.intuition),
                getString(R.string.tactics),
                getString(R.string.introspection)
        );

        Button confirmClassesButton = findViewById(R.id.confirmClassesButton);
        confirmClassesButton.setOnClickListener(view -> proceedToStats());
        firstClassSummary = findViewById(R.id.classSummary1);
        secondClassSummary = findViewById(R.id.classSummary2);
        firstClassStats = findViewById(R.id.classStats1);
        secondClassStats = findViewById(R.id.classStats2);
        totalClassStats = findViewById(R.id.totalClassStats);
        className1 = findViewById(R.id.className1);
        className2 = findViewById(R.id.className2);
        totalLabel = findViewById(R.id.totalLabel);
    }

    private void proceedToStats() {
        if (selectedClassesStats.size() == 2) {
            Intent intent = new Intent(this, ClassDetailsActivity.class);
            intent.putExtra("selectedClassStats1", selectedClassesStats.get(0));
            intent.putExtra("selectedClassStats2", selectedClassesStats.get(1));
            intent.putExtra("selectedClass1", firstSelectedButton.getText().toString()); // Add this line
            intent.putExtra("selectedClass2", secondSelectedButton.getText().toString()); // Add this line
            startActivity(intent);
        } else {
            Toast.makeText(this, "Select two classes before proceeding", Toast.LENGTH_SHORT).show();
        }
    }


    public void onButtonClicked(View view) {
        MaterialButton clickedButton = (MaterialButton) view;
        int primaryColor = ThemeUtils.obtainColorPrimary(this);
        int orangeColor = ContextCompat.getColor(this, R.color.orange);

        if (firstSelectedButton == null) {
            firstSelectedButton = clickedButton;
            firstButtonOriginalBackground = clickedButton.getBackground();
            clickedButton.setBackgroundColor(orangeColor);
            selectedClassesStats.add(getClassStats(clickedButton.getText().toString()));
        } else if (secondSelectedButton == null && !firstSelectedButton.equals(clickedButton)) {
            secondSelectedButton = clickedButton;
            secondButtonOriginalBackground = clickedButton.getBackground();
            clickedButton.setBackgroundColor(orangeColor);
            selectedClassesStats.add(getClassStats(clickedButton.getText().toString()));
    } else if (firstSelectedButton.equals(clickedButton)) {
            firstSelectedButton.setBackground(firstButtonOriginalBackground);
            firstSelectedButton.setBackgroundColor(primaryColor);
            firstSelectedButton = secondSelectedButton;
            firstButtonOriginalBackground = secondButtonOriginalBackground;
            secondSelectedButton = null;
            secondButtonOriginalBackground = null;
        } else if (secondSelectedButton != null && secondSelectedButton.equals(clickedButton)) {
            secondSelectedButton.setBackground(secondButtonOriginalBackground);
            secondSelectedButton.setBackgroundColor(primaryColor);
            secondSelectedButton = null;
            secondButtonOriginalBackground = null;
        }

        // Update summaries
        if (firstSelectedButton != null) {
            firstClassSummary.setText(getClassSummary(firstSelectedButton.getText().toString()));
            firstClassSummary.setVisibility(View.VISIBLE);
            firstClassStats.setText(getStatsText(selectedClassesStats.get(0))); // Add this line
            firstClassStats.setVisibility(View.VISIBLE);
        } else {
            firstClassSummary.setVisibility(View.GONE);
            firstClassStats.setVisibility(View.GONE);
        }

        if (secondSelectedButton != null) {
            secondClassSummary.setText(getClassSummary(secondSelectedButton.getText().toString()));
            secondClassSummary.setVisibility(View.VISIBLE);
            secondClassStats.setText(getStatsText(selectedClassesStats.get(1))); // Add this line
            secondClassStats.setVisibility(View.VISIBLE);
        } else {
            secondClassSummary.setVisibility(View.GONE);
            secondClassStats.setVisibility(View.GONE);
        }

        // Remove the selected class stats when deselecting a class
        if (firstSelectedButton == null || secondSelectedButton == null) {
            selectedClassesStats.clear();
            if (firstSelectedButton != null) {
                selectedClassesStats.add(getClassStats(firstSelectedButton.getText().toString()));
            }
            if (secondSelectedButton != null) {
                selectedClassesStats.add(getClassStats(secondSelectedButton.getText().toString()));
            }
        }
// Add together the names and values of the first and second selected classes
        if (firstSelectedButton != null && secondSelectedButton != null) {
            String totalStatsText = "";
            HashMap<String, Integer> firstClassStats = selectedClassesStats.get(0);
            HashMap<String, Integer> secondClassStats = selectedClassesStats.get(1);

            for (String key : statsOrder) {
                if (key.equals("Current HP")){
                    continue;
                }
                int value = 0;
                if (firstClassStats.containsKey(key)) {
                    value += firstClassStats.get(key);
                }
                if (secondClassStats.containsKey(key)) {
                    value += secondClassStats.get(key);
                }
                if (value > 0) {
                    totalStatsText += key + ": " + value + "\n";
                }
            }
            totalClassStats.setText(totalStatsText);
            totalClassStats.setVisibility(View.VISIBLE);
        } else {
            totalClassStats.setVisibility(View.GONE);
        }

        // Update class names
        if (firstSelectedButton != null) {
            className1.setText("(" + firstSelectedButton.getText().toString() + ")");
            className1.setVisibility(View.VISIBLE);
        } else {
            className1.setVisibility(View.GONE);
        }

        if (secondSelectedButton != null) {
            className2.setText("(" + secondSelectedButton.getText().toString() + ")");
            className2.setVisibility(View.VISIBLE);
        } else {
            className2.setVisibility(View.GONE);
        }

// Show or hide the "Total" label
        if (firstSelectedButton != null && secondSelectedButton != null) {
            totalLabel.setVisibility(View.VISIBLE);
        } else {
            totalLabel.setVisibility(View.GONE);
        }

    }
    private String getStatsText(HashMap<String, Integer> classStats) {
        if (classStats == null) {
            return "";
        }

        StringBuilder statsText = new StringBuilder();

        for (Map.Entry<String, Integer> entry : classStats.entrySet()) {
            if (!entry.getKey().equals("Current HP")) {
                statsText.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }

        return statsText.toString();
    }

    private LinkedHashMap<String, Integer> getClassStats(String className) {
        LinkedHashMap<String, Integer> classStats = new LinkedHashMap<>();

        switch (className) {
            case "Alchemist":
                classStats.put(getString(R.string.max_hp), 1);
                classStats.put(getString(R.string.current_hp), 1);
                classStats.put(getString(R.string.tactics), 1);
                classStats.put(getString(R.string.introspection), 1);
                break;
            case "Barbarian":
                classStats.put(getString(R.string.max_hp), 1);
                classStats.put(getString(R.string.current_hp), 1);
                classStats.put(getString(R.string.might), 4);
                break;
            case "Battle Mage":
                classStats.put(getString(R.string.max_hp), 1);
                classStats.put(getString(R.string.current_hp), 1);
                classStats.put(getString(R.string.might), 2);
                classStats.put(getString(R.string.intuition), 2);
                break;
            case "Cleric":
                classStats.put(getString(R.string.intuition), 1);
                classStats.put(getString(R.string.introspection), 2);
                break;
            case "Conjurer":
                classStats.put(getString(R.string.max_hp), 4);
                classStats.put(getString(R.string.current_hp), 4);
                classStats.put(getString(R.string.intuition), 1);
                break;
            case "Controller":
                classStats.put(getString(R.string.intuition), 5);
                break;
            case "Druid":
                classStats.put(getString(R.string.max_hp), 2);
                classStats.put(getString(R.string.current_hp), 2);
                classStats.put(getString(R.string.intuition), 1);
                classStats.put(getString(R.string.introspection), 1);
                break;
            case "Dualist":
                classStats.put(getString(R.string.agility), 1);
                classStats.put(getString(R.string.precision), 1);
                classStats.put(getString(R.string.tactics), 1);
                break;
            case "Gambler":
                classStats.put(getString(R.string.max_hp), 1);
                classStats.put(getString(R.string.current_hp), 1);
                classStats.put(getString(R.string.tactics), 2);
                break;
            case "Illusionist":
                classStats.put(getString(R.string.agility), 2);
                classStats.put(getString(R.string.tactics), 1);
                break;
            case "Necro":
                classStats.put(getString(R.string.max_hp), 1);
                classStats.put(getString(R.string.current_hp), 1);
                classStats.put(getString(R.string.intuition), 4);
                break;
            case "Ninja":
                classStats.put(getString(R.string.might), 2);
                classStats.put(getString(R.string.agility), 2);
                break;
            case "Paladin":
                classStats.put(getString(R.string.max_hp), 3);
                classStats.put(getString(R.string.current_hp), 3);
                classStats.put(getString(R.string.introspection), 1);
                break;
            case "Ranger":
                classStats.put(getString(R.string.might), 2);
                classStats.put(getString(R.string.precision), 2);
                break;
            case "Seer":
                classStats.put(getString(R.string.intuition), 3);
                classStats.put(getString(R.string.tactics), 1);
                break;
            case "Trap Specialist":
                classStats.put(getString(R.string.precision), 2);
                classStats.put(getString(R.string.tactics), 1);
                break;

        }
        return classStats;
    }

    private String getClassSummary(String className) {
        switch (className) {
            case "Alchemist":
                return "Gathers fresh components to create short-lived potions and brews to both heal and harm. (Alchemist)";
            case "Barbarian":
                return "Uses raw might to crush every foe on the battlefield.(Barbarian)";
            case "Battle Mage":
                return "Uses a versatile mix of both magical and physical attacks to adapt to any enemy. (Battle Mage)";
            case "Cleric":
                return "Focuses on healing the party and keeping everyone in the best fighting shape. (Cleric)";
            case "Conjurer":
                return "Uses the gift of life to inflict pain, while conjuring manifestations of blood. (Conjurer)";
            case "Controller":
                return "Controls the elements to inflict massive magical destruction. (Controller)";
            case "Druid":
                return "Summons wildlife to support the party and has a few healing skills to keep the party alive. (Druid)";
            case "Dualist":
                return "Uses a set of swords to make a flurry of melee attacks. (Dualist)";
            case "Gambler":
                return "Changes the tide of battle not with skill but with luck. Can use some skills outside of battle. (Gambler)";
            case "Illusionist":
                return "Jack of all trades, master of none. The Illusionist uses a versatile moveset to trick and distract. (Illusionist)";
            case "Necro":
                return "Raises undead to support the party as well as plaguing the enemy with strong magical attacks. (Necromancer)";
            case "Ninja":
                return "Uses the shadows to make themselves a tricky target to hit while punishing those who target anyone else. (Ninja)";
            case "Paladin":
                return "Does it all with taking hits, dealing damage, and healing allies. (Paladin)";
            case "Ranger":
                return "Focuses on landing precise ranged attacks with their bow to land critical hits. (Ranger)";
            case "Seer":
                return "Uses the gift of premonition to lay magical traps before the enemy even attacks. (Seer)";
            case "Trap Specialist":
                return "Master of traps, the Trap Specialist covers the field with various devices to find the enemy's critical weaknesses. (Trap Specialist)";

            default:
                return "";
        }
    }
}
