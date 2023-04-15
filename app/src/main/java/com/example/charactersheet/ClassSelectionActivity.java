package com.example.charactersheet;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.core.content.ContextCompat;

public class ClassSelectionActivity extends BaseActivity {
    private Button firstSelectedButton;
    private Button secondSelectedButton;
    private Drawable firstButtonOriginalBackground;
    private Drawable secondButtonOriginalBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_selection);
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