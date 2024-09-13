package com.example.charactersheet;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ClassSelectionActivity extends AppCompatActivity implements SocketClient.MessageListener, SocketServer.MessageListener {
    private boolean isSelectionEnabled = false;
    private boolean isFirstSelectionConfirmed = false;
    private String currentSelectedClass = null;
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
    private ArrayList<HashMap<String, Integer>> selectedClassesStats = new ArrayList<>();
    private final List<Integer> disabledButtonIds = new ArrayList<>();
    private final Handler handler = new Handler();
    private Runnable randomSelectionRunnable;
    private int randomSelectionStep = 0;
    private List<Integer> availableButtonIds = new ArrayList<>();
    private final long[] delays = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 25, 30, 35, 40, 45, 50, 60, 70, 80, 90, 100, 200, 200, 200, 300, 300, 300, 400, 400, 500}; // Delays for each step
    private boolean isPendingRandomSelection = false;
    private boolean isRandomSelectionActive = false;
    private String firstSelectedClassName;
    private String secondSelectedClassName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_selection);

        boolean isGroupOwner = getIntent().getBooleanExtra("IS_GROUP_OWNER", false);
        String groupOwnerAddress = getIntent().getStringExtra("GROUP_OWNER_ADDRESS");
        ArrayList<String> birthdates = getIntent().getStringArrayListExtra("BIRTHDATES");
        ArrayList<Integer> daysLived = getIntent().getIntegerArrayListExtra("DAYS_LIVED");
        ArrayList<String> deviceIds = getIntent().getStringArrayListExtra("DEVICE_IDS");

        Log.d("ClassSelectionActivity", "isGroupOwner: " + isGroupOwner + ", groupOwnerAddress: " + groupOwnerAddress);
        Log.d("ClassSelectionActivity", "Birthdates: " + birthdates + ", Days Lived: " + daysLived + ", Device IDs: " + deviceIds);

        if (isGroupOwner) {
            startSocketServerWithDetails(birthdates, daysLived, deviceIds);
        } else {
            if (groupOwnerAddress == null) {
                Log.e("ClassSelectionActivity", "Group owner address is null for non-group owner.");
            } else {
                connectToServer(groupOwnerAddress, deviceIds, daysLived);
            }
        }

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
        confirmClassesButton.setOnClickListener(view -> confirmClassSelection());
        firstClassSummary = findViewById(R.id.classSummary1);
        secondClassSummary = findViewById(R.id.classSummary2);
        firstClassStats = findViewById(R.id.classStats1);
        secondClassStats = findViewById(R.id.classStats2);
        totalClassStats = findViewById(R.id.totalClassStats);
        className1 = findViewById(R.id.className1);
        className2 = findViewById(R.id.className2);
        totalLabel = findViewById(R.id.totalLabel);

        enableClassButtons(false); // Disable class buttons initially

        // Set the ClassSelectionActivity as the listener
        SocketServer server = SocketManager.getSocketServer();
        if (server != null) {
            server.setMessageListener(this);
        }
        // Ensure this is called in onCreate() method of ClassSelectionActivity
        SocketClient client = SocketManager.getSocketClient();
        if (client != null) {
            client.setMessageListener(this);
        }
    }

    private void startSocketServerWithDetails(List<String> birthdates, List<Integer> daysLived, List<String> deviceIds) {
        SocketServer server = SocketManager.getSocketServer();
        if (server == null || !server.isRunning()) {
            server = new SocketServer(this, this, birthdates, daysLived, deviceIds);
            server.start();
            SocketManager.setSocketServer(server);
            Log.d("ClassSelectionActivity", "Socket server started and listening for connections.");
            onMessageReceived("CLASS_SELECTION_READY:" + getDeviceId());
        } else {
            Log.d("ClassSelectionActivity", "Socket server is already running.");
            notifyServerClassSelectionReady(deviceIds, (ArrayList<Integer>) daysLived);
        }
    }

    private void connectToServer(String groupOwnerAddress, List<String> deviceIds, ArrayList<Integer> daysLived) {
        SocketClient client = SocketManager.getSocketClient();
        if (client != null && client.isConnected()) {
            Log.d(TAG, "Client already connected to server.");
            client.setMessageListener(this); // Ensure the listener is set even if already connected
            notifyServerClassSelectionReady(deviceIds, daysLived);
            return;
        }

        final SocketClient newClient = new SocketClient(groupOwnerAddress, 8888);
        newClient.setConnectionListener(new SocketClient.ConnectionListener() {
            @Override
            public void onConnectionSuccess() {
                Log.i(TAG, "Client connected successfully.");
                newClient.setMessageListener(ClassSelectionActivity.this); // Ensure the listener is set upon successful connection
                notifyServerClassSelectionReady(deviceIds, daysLived);
                Log.d("ClassSelectionActivity", "notifyServerClassSelectionReady called after successful connection.");
            }

            @Override
            public void onConnectionFailure(IOException e) {
                Log.e(TAG, "Client connection failed: " + e.getMessage());
            }
        });
        newClient.start();
        SocketManager.setSocketClient(newClient);
    }

    private void notifyServerClassSelectionReady(List<String> deviceIds, ArrayList<Integer> daysLived) {
        SocketServer server = SocketManager.getSocketServer();
        if (server != null && server.isRunning()) {
            server.handleClassSelectionReady(deviceIds, daysLived);
        } else {
            SocketClient client = SocketManager.getSocketClient();
            if (client != null && client.isConnected()) {
                client.sendMessage("CLASS_SELECTION_READY:" + getDeviceId());
            }
        }
    }
    @Override
    public void onMessageReceived(String message) {
        Log.d("ClassSelectionActivity", "Received message: " + message);
        if (message.startsWith("TURN:")) {
            String deviceId = message.substring(5);
            Log.d("ClassSelectionActivity", "TURN message for deviceId: " + deviceId + ", my deviceId: " + getDeviceId());
            if (deviceId.equals(getDeviceId())) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "It's your turn", Toast.LENGTH_SHORT).show();
                    Log.d("ClassSelectionActivity", "Enabled class buttons for device: " + getDeviceId());

                    // Enable class selection
                    isSelectionEnabled = true;

                    // Enable all class buttons
                    enableClassButtons(true);
                });
            }
        } else if (message.startsWith("DISABLE_BUTTON:")) {
            String buttonIdStr = message.substring(15); // Extract the button ID
            try {
                int buttonId = Integer.parseInt(buttonIdStr);
                runOnUiThread(() -> disableButton(buttonId));
            } catch (NumberFormatException e) {
                Log.e("ClassSelectionActivity", "Invalid button ID format: " + buttonIdStr, e);
            }
        } else if (message.startsWith("RANDOM_SELECTION:")) {
            String[] parts = message.split(":");
            if (parts.length == 2) {
                String deviceId = parts[1];
                if (deviceId.equals(getDeviceId())) {
                    runOnUiThread(this::selectRandomClass);
                }
            }
        } else if (message.equals("ALL_CLASSES_SELECTED")) {
            runOnUiThread(this::proceedToStats);
        }
    }


    private void enableClassButtons(boolean enable) {
        int[] buttonIds = {
                R.id.alchemistButton,
                R.id.barbarianButton,
                R.id.battleMageButton,
                R.id.clericButton,
                R.id.conjurerButton,
                R.id.controllerButton,
                R.id.druidButton,
                R.id.dualistButton,
                R.id.gamblerButton,
                R.id.illusionistButton,
                R.id.necromancerButton,
                R.id.ninjaButton,
                R.id.paladinButton,
                R.id.rangerButton,
                R.id.seerButton,
                R.id.trapSpecialistButton
        };

        for (int id : buttonIds) {
            MaterialButton button = findViewById(id);
            button.setEnabled(enable && !disabledButtonIds.contains(id)); // Ensure the button stays disabled if already selected
            Log.d("ClassSelectionActivity", "Button " + id + " enabled: " + enable);
        }
    }

    private void disableButton(int buttonId) {
        MaterialButton button = findViewById(buttonId);
        if (button != null) {
            button.setEnabled(false);
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.tableRowColor1)); // Set the background color
            disabledButtonIds.add(buttonId); // Add to the list of disabled button IDs
        }
        // Ensure random selection only after all buttons are disabled
        if (isPendingRandomSelection && !isRandomSelectionActive) {
            isPendingRandomSelection = false;
            selectRandomClass();
        }
    }

    private void notifyServerOfSelection(int buttonId) {
        SocketServer server = SocketManager.getSocketServer();
        if (server != null) {
            server.sendMessage("SELECTION_DONE:" + getDeviceId(), null);
            server.sendMessage("CLASS_SELECTED:" + buttonId, null);
        } else {
            SocketClient client = SocketManager.getSocketClient();
            if (client != null && client.isConnected()) {
                client.sendMessage("SELECTION_DONE:" + getDeviceId());
                client.sendMessage("CLASS_SELECTED:" + buttonId);
            }
        }

        // Disable class selection after notifying the server
        isSelectionEnabled = false;
        enableClassButtons(false); // Disable class buttons
    }
    private String getDeviceId() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private void selectRandomClass() {
        availableButtonIds = new ArrayList<>(Arrays.asList(
                R.id.alchemistButton,
                R.id.barbarianButton,
                R.id.battleMageButton,
                R.id.clericButton,
                R.id.conjurerButton,
                R.id.controllerButton,
                R.id.druidButton,
                R.id.dualistButton,
                R.id.gamblerButton,
                R.id.illusionistButton,
                R.id.necromancerButton,
                R.id.ninjaButton,
                R.id.paladinButton,
                R.id.rangerButton,
                R.id.seerButton,
                R.id.trapSpecialistButton
        ));

        // Remove already disabled buttons
        availableButtonIds.removeAll(disabledButtonIds);

        if (!availableButtonIds.isEmpty()) {
            isRandomSelectionActive = true;
            enableClassButtons(false); // Disable all buttons
            Button confirmClassesButton = findViewById(R.id.confirmClassesButton);
            confirmClassesButton.setEnabled(false);
            randomSelectionStep = 0;
            randomSelectionRunnable = new Runnable() {
                @Override
                public void run() {
                    if (randomSelectionStep < delays.length) {
                        int randomIndex = new Random().nextInt(availableButtonIds.size());
                        int randomButtonId = availableButtonIds.get(randomIndex);
                        MaterialButton randomButton = findViewById(randomButtonId);
                        randomButton.performClick(); // Simulate button click

                        randomSelectionStep++;
                        handler.postDelayed(this, delays[randomSelectionStep % delays.length]);
                    } else {
                        confirmClassSelectionWithoutPrompt();
                        isRandomSelectionActive = false;
                    }
                }
            };
            handler.postDelayed(randomSelectionRunnable, delays[0]);
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(randomSelectionRunnable);
        super.onDestroy();
    }

    private void confirmClassSelectionWithoutPrompt() {
        if (currentSelectedClass == null) {
            return;
        }

        completeClassSelection();
    }

    public void onButtonClicked(View view) {
        if (!isSelectionEnabled) {
            return; // If selection is not enabled, exit the method early
        }

        MaterialButton clickedButton = (MaterialButton) view;
        int orangeColor = ContextCompat.getColor(this, R.color.orange);
        int originalColor = ContextCompat.getColor(this, R.color.purple_200); // Assuming purple_200 is defined in colors.xml

        if (!isFirstSelectionConfirmed) {
            if (firstSelectedButton == null) {
                firstSelectedButton = clickedButton;
                firstButtonOriginalBackground = clickedButton.getBackground();
                clickedButton.setBackgroundColor(orangeColor);
                currentSelectedClass = clickedButton.getText().toString();
                updateSummariesAndStats(false); // Update summaries and stats for the selected class
                findViewById(R.id.confirmClassesButton).setVisibility(View.VISIBLE); // Show the confirm button
            } else if (!firstSelectedButton.equals(clickedButton)) {
                firstSelectedButton.setBackgroundColor(originalColor); // Reset the original background color
                firstSelectedButton = clickedButton;
                firstButtonOriginalBackground = clickedButton.getBackground();
                clickedButton.setBackgroundColor(orangeColor);
                currentSelectedClass = clickedButton.getText().toString();
                updateSummariesAndStats(false); // Update summaries and stats for the new selection
            }
        } else {
            if (secondSelectedButton == null) {
                secondSelectedButton = clickedButton;
                secondButtonOriginalBackground = clickedButton.getBackground();
                clickedButton.setBackgroundColor(orangeColor);
                currentSelectedClass = clickedButton.getText().toString();
                updateSummariesAndStats(false); // Update summaries and stats for the selected class
                findViewById(R.id.confirmClassesButton).setVisibility(View.VISIBLE); // Show the confirm button
            } else if (!secondSelectedButton.equals(clickedButton)) {
                secondSelectedButton.setBackgroundColor(originalColor); // Reset the original background color
                secondSelectedButton = clickedButton;
                secondButtonOriginalBackground = clickedButton.getBackground();
                clickedButton.setBackgroundColor(orangeColor);
                currentSelectedClass = clickedButton.getText().toString();
                updateSummariesAndStats(false); // Update summaries and stats for the new selection
            }
        }
    }

    private void confirmClassSelection() {
        if (currentSelectedClass == null) {
            Toast.makeText(this, "Select a class before confirming", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirm Class")
                .setMessage("Do you want to select this class?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    // User confirmed the selection
                    completeClassSelection();
                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                    // User cancelled the selection
                    cancelClassSelection();
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void completeClassSelection() {
        int tableRowColor1 = ContextCompat.getColor(this, R.color.tableRowColor1);

        if (!isFirstSelectionConfirmed) {
            selectedClassesStats.add(getClassStats(currentSelectedClass));
            firstSelectedClassName = currentSelectedClass; // Store the first selected class name
            isFirstSelectionConfirmed = true;
            notifyServerOfSelection(firstSelectedButton.getId()); // Notify the server of the first selection and disable button
            firstSelectedButton.setBackground(firstButtonOriginalBackground);
            firstSelectedButton.setBackgroundColor(tableRowColor1);
            firstSelectedButton = null;
            currentSelectedClass = null; // Reset current selection
            updateSummariesAndStats(false); // Keep the first class summaries and stats visible
        } else {
            selectedClassesStats.add(getClassStats(currentSelectedClass));
            secondSelectedClassName = currentSelectedClass; // Store the second selected class name
            notifyServerOfSelection(secondSelectedButton.getId()); // Notify the server of the second selection and disable button
            secondSelectedButton.setBackground(secondButtonOriginalBackground);
            secondSelectedButton.setBackgroundColor(tableRowColor1);
            secondSelectedButton = null;
            currentSelectedClass = null; // Reset current selection
            isFirstSelectionConfirmed = false; // Reset for next user
            updateSummariesAndStats(true); // Show summaries and stats for both classes and the total
        }
    }


    private void cancelClassSelection() {
        int originalColor = ContextCompat.getColor(this, R.color.purple_200); // Assuming purple_200 is defined in colors.xml

        if (!isFirstSelectionConfirmed) {
            if (firstSelectedButton != null) {
                firstSelectedButton.setBackgroundColor(originalColor); // Reset the original background color
                firstSelectedButton = null;
            }
            // Clear the first class summary and stats
            firstClassSummary.setVisibility(View.GONE);
            firstClassStats.setVisibility(View.GONE);
            className1.setVisibility(View.GONE);
        } else {
            if (secondSelectedButton != null) {
                secondSelectedButton.setBackgroundColor(originalColor);
                secondSelectedButton = null;
            }
            // Clear the second class summary and stats
            secondClassSummary.setVisibility(View.GONE);
            secondClassStats.setVisibility(View.GONE);
            className2.setVisibility(View.GONE);
        }
        currentSelectedClass = null; // Reset current selected class

        // Clear the total stats if no classes are selected
        if (firstSelectedButton == null && secondSelectedButton == null) {
            totalClassStats.setVisibility(View.GONE);
            totalLabel.setVisibility(View.GONE);
        }
    }


    private void updateSummariesAndStats(boolean showTotal) {
        // Update summaries and stats logic
        if (firstSelectedButton != null) {
            firstClassSummary.setText(getClassSummary(firstSelectedButton.getText().toString()));
            firstClassSummary.setVisibility(View.VISIBLE);
            firstClassStats.setText(getStatsText(getClassStats(firstSelectedButton.getText().toString())));
            firstClassStats.setVisibility(View.VISIBLE);
            className1.setText(firstSelectedButton.getText().toString());
            className1.setVisibility(View.VISIBLE);
        }

        if (secondSelectedButton != null) {
            secondClassSummary.setText(getClassSummary(secondSelectedButton.getText().toString()));
            secondClassSummary.setVisibility(View.VISIBLE);
            secondClassStats.setText(getStatsText(getClassStats(secondSelectedButton.getText().toString())));
            secondClassStats.setVisibility(View.VISIBLE);
            className2.setText(secondSelectedButton.getText().toString());
            className2.setVisibility(View.VISIBLE);
        }

        // Calculate total stats only if the second class has been confirmed
        if (showTotal && !selectedClassesStats.isEmpty()) {
            StringBuilder totalStatsText = new StringBuilder();
            HashMap<String, Integer> totalStats = new HashMap<>();
            for (HashMap<String, Integer> stats : selectedClassesStats) {
                for (Map.Entry<String, Integer> entry : stats.entrySet()) {
                    totalStats.put(entry.getKey(), totalStats.getOrDefault(entry.getKey(), 0) + entry.getValue());
                }
            }

            for (String key : statsOrder) {
                if (key.equals("Current HP")) {
                    continue;
                }
                int value = totalStats.getOrDefault(key, 0);
                totalStatsText.append(key).append(": ").append(value).append("\n");
            }

            totalClassStats.setText(totalStatsText.toString());
            totalClassStats.setVisibility(View.VISIBLE);
            totalLabel.setVisibility(View.VISIBLE); // Show the total label
        } else {
            totalClassStats.setVisibility(View.GONE);
            totalLabel.setVisibility(View.GONE); // Hide the total label
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
    private void proceedToStats() {
        if (selectedClassesStats.size() == 2) {
            Intent intent = new Intent(this, ClassDetailsActivity.class);
            intent.putExtra("selectedClassStats1", selectedClassesStats.get(0));
            intent.putExtra("selectedClassStats2", selectedClassesStats.get(1));
            intent.putExtra("selectedClass1", firstSelectedClassName);
            intent.putExtra("selectedClass2", secondSelectedClassName);
            startActivity(intent);
        }
    }

}
