package com.example.charactersheet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button newCharacterButton = findViewById(R.id.newCharacterButtonId);
        newCharacterButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ClassSelectionActivity.class);
            startActivity(intent);
        });
    }
}
