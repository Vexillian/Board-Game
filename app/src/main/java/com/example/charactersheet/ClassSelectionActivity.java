package com.example.charactersheet;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class ClassSelectionActivity extends BaseActivity {
    private Button firstSelectedButton;
    private Button secondSelectedButton;
    private Drawable firstButtonOriginalBackground;
    private Drawable secondButtonOriginalBackground;
    private final ArrayList<String> selectedClasses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_selection);

        Button confirmClassesButton = findViewById(R.id.confirmClassesButton);
        confirmClassesButton.setOnClickListener(view -> proceedToStats());
    }

    private void proceedToStats() {
        if (firstSelectedButton != null && secondSelectedButton != null) {
            Intent intent = new Intent(ClassSelectionActivity.this, StatsActivity.class);
            intent.putStringArrayListExtra("selectedClasses", selectedClasses);
            startActivity(intent);
        } else {
            Toast.makeText(ClassSelectionActivity.this, "Please select two classes", Toast.LENGTH_SHORT).show();
        }
    }

    public void onButtonClicked(View view) {
        Button clickedButton = (Button) view;
        int primaryColor = ThemeUtils.obtainColorPrimary(this);
        int orangeColor = ContextCompat.getColor(this, R.color.orange);

        if (firstSelectedButton == null) {
            firstSelectedButton = clickedButton;
            firstButtonOriginalBackground = clickedButton.getBackground();
            clickedButton.setBackgroundColor(orangeColor);
        } else if (secondSelectedButton == null && !firstSelectedButton.equals(clickedButton)) {
            secondSelectedButton = clickedButton;
            secondButtonOriginalBackground = clickedButton.getBackground();
            clickedButton.setBackgroundColor(orangeColor);
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
    }
}
