package com.example.charactersheet;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.IOException;

public class ClassDetailsActivity extends AppCompatActivity implements SocketClient.MessageListener, SocketServer.MessageListener {

    private int selectedZone = 0;
    private SharedViewModel sharedViewModel;
    private SocketServer socketServer;
    private SocketClient socketClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        // Setup socket communication
        setupSocketCommunication();

        // Initialize the switch and set its listener
        SwitchMaterial enableZoneButtonsSwitch = findViewById(R.id.enable_zone_buttons_switch);
        enableZoneButtonsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> setZoneButtonsEnabled(isChecked));

        // Set up zone buttons
        setupZoneButtons();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager2 viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new CustomPagerAdapter(this));

        // Get the selected class names from the intent
        String className1 = getIntent().getStringExtra("selectedClass1");
        String className2 = getIntent().getStringExtra("selectedClass2");

        TextView activityTitle = findViewById(R.id.activity_title);
        String activityTitleFormat = getResources().getString(R.string.activity_title_format);
        activityTitle.setText(String.format(activityTitleFormat, className1, className2));

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

    private void setupSocketCommunication() {
        boolean isGroupOwner = getIntent().getBooleanExtra("IS_GROUP_OWNER", false);
        String groupOwnerAddress = getIntent().getStringExtra("GROUP_OWNER_ADDRESS");

        if (isGroupOwner) {
            startSocketServer();
        } else {
            connectToServer(groupOwnerAddress);
        }
    }

    private void startSocketServer() {
        socketServer = new SocketServer(this, this);
        socketServer.start();
    }

    private void connectToServer(String groupOwnerAddress) {
        socketClient = new SocketClient(groupOwnerAddress, 8888);
        socketClient.setMessageListener(this);
        socketClient.start();
    }

    @Override
    public void onMessageReceived(String message) {
        // Handle socket messages and update SharedViewModel accordingly
        runOnUiThread(() -> {
            sharedViewModel.processSocketMessage(message);
        });
    }

    // Clean up socket resources
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (socketServer != null) {
            socketServer.stop();
        }
        if (socketClient != null) {
            try {
                socketClient.clearMessageListener();
                socketClient.closeClient();
            } catch (IOException e) {
                Log.e(TAG, "Error closing socket client: " + e.getMessage());
            }
        }
    }

    public void sendSocketMessage(String message) {
        if (socketServer != null) {
            socketServer.sendMessage(message, null);
        } else if (socketClient != null) {
            socketClient.sendMessage(message);
        }
    }


    private void setupZoneButtons() {
        findViewById(R.id.zone1Button).setOnClickListener(v -> onZoneButtonClick(0));
        findViewById(R.id.zone2Button).setOnClickListener(v -> onZoneButtonClick(1));
        findViewById(R.id.zone3Button).setOnClickListener(v -> onZoneButtonClick(2));
        findViewById(R.id.zone4Button).setOnClickListener(v -> onZoneButtonClick(3));
    }

    private void setZoneButtonsEnabled(boolean enabled) {
        findViewById(R.id.zone1Button).setEnabled(enabled);
        findViewById(R.id.zone2Button).setEnabled(enabled);
        findViewById(R.id.zone3Button).setEnabled(enabled);
        findViewById(R.id.zone4Button).setEnabled(enabled);
    }

    public void onZoneButtonClick(int zoneNumber) {
        updateSelectedZone(zoneNumber);
    }

    public void updateSelectedZone(int zoneNumber) {
        selectedZone = zoneNumber;
        sharedViewModel.setSelectedZone(zoneNumber);  // Update the selected zone in the SharedViewModel

        // Programmatically click the zone button
        Button zoneButton = getZoneButtonByIndex(zoneNumber);
        if (zoneButton != null) {
            zoneButton.setSelected(true); // Visually indicate the selected button
        }
    }

    public Button getZoneButtonByIndex(int index) {
        switch (index) {
            case 0:
                return findViewById(R.id.zone1Button);
            case 1:
                return findViewById(R.id.zone2Button);
            case 2:
                return findViewById(R.id.zone3Button);
            case 3:
                return findViewById(R.id.zone4Button);
            default:
                return null;
        }
    }

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
}
