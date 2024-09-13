package com.example.charactersheet;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity implements WifiP2pManager.ConnectionInfoListener {

    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1001;
    private static final int PERMISSIONS_REQUEST_CODE_NEARBY_DEVICES = 1002;
    private static final String TAG = "MainActivity";

    private final List<WifiP2pDevice> peers = new ArrayList<>();
    private final List<String> connectedDevices = new ArrayList<>();
    private final Map<String, String> deviceIdToNameMap = new HashMap<>(); // Map to store device IDs and names
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver;
    private IntentFilter intentFilter;
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> connectedDevicesAdapter;
    private TextView receivedMessagesTextView;

    private boolean isGroupOwner = false;
    private Handler peerDiscoveryHandler;
    private Handler discoverabilityHandler;
    private Runnable peerDiscoveryRunnable;
    private Runnable discoverabilityRunnable;
    private Handler groupInfoHandler;
    private Runnable groupInfoRunnable;
    private static final int MAX_RETRY_COUNT = 3;
    private int retryCount = 0;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the WiFi P2P manager and channel
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        if (manager != null) {
            channel = manager.initialize(this, getMainLooper(), null);
        } else {
            Log.e(TAG, "WifiP2pManager is null. Cannot proceed.");
            return;
        }

        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        ListView nearbyDevicesList = findViewById(R.id.nearbyDevicesList);
        ListView connectedDevicesList = findViewById(R.id.listViewConnectedDevices);

        if (nearbyDevicesList != null && connectedDevicesList != null) {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
            connectedDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, connectedDevices);
            nearbyDevicesList.setAdapter(adapter);
            connectedDevicesList.setAdapter(connectedDevicesAdapter);

            nearbyDevicesList.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
                WifiP2pDevice device = peers.get(position);
                showConnectDialog(device);
            });
        } else {
            Log.e(TAG, "ListView initialization failed.");
        }

        receivedMessagesTextView = findViewById(R.id.textViewReceivedMessages);

        Button buttonSendTestMessage = findViewById(R.id.buttonSendTestMessage);
        buttonSendTestMessage.setOnClickListener(this::onSendMessageClick);
        Button buttonGoToBirthdateScreen = findViewById(R.id.buttonGoToBirthdateScreen);
        buttonGoToBirthdateScreen.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BirthdateActivity.class);
            intent.putStringArrayListExtra("CONNECTED_DEVICES", new ArrayList<>(connectedDevices));
            startActivity(intent);
        });

        peerDiscoveryHandler = new Handler();
        discoverabilityHandler = new Handler();
        peerDiscoveryRunnable = this::startPeerDiscovery;
        discoverabilityRunnable = this::enableDiscoverability;

        groupInfoHandler = new Handler();
        groupInfoRunnable = new Runnable() {
            @Override
            public void run() {
                groupInfoHandler.postDelayed(this, 10000); // 10 seconds interval
            }
        };

        // Request permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.NEARBY_WIFI_DEVICES) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.NEARBY_WIFI_DEVICES}, PERMISSIONS_REQUEST_CODE_NEARBY_DEVICES);
        } else {
            removeExistingGroup();
        }
    }

    private String getDeviceId() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
        groupInfoHandler.post(groupInfoRunnable); // Start requesting group info periodically
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        groupInfoHandler.removeCallbacks(groupInfoRunnable); // Stop requesting group info
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION || requestCode == PERMISSIONS_REQUEST_CODE_NEARBY_DEVICES) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                removeExistingGroup();
            } else {
                Log.e(TAG, "Permission denied. Cannot proceed.");
            }
        }
    }

    public void updateDeviceList(Collection<WifiP2pDevice> deviceList) {
        if (adapter == null) {
            Log.e(TAG, "Adapter is null, initialization failed.");
            return;
        }

        peers.clear();
        for (WifiP2pDevice device : deviceList) {
            if ((device.status == WifiP2pDevice.AVAILABLE || device.status == WifiP2pDevice.CONNECTED) &&
                    !connectedDevices.contains(device.deviceName)) {
                peers.add(device);
            }
        }

        List<String> deviceNames = new ArrayList<>();
        for (WifiP2pDevice device : peers) {
            deviceNames.add(device.deviceName);
        }
        adapter.clear();
        adapter.addAll(deviceNames);
        adapter.notifyDataSetChanged();

        Log.d(TAG, "Device list updated: " + deviceNames);
    }

    public void removeNearbyDevice(String deviceName) {
        runOnUiThread(() -> {
            WifiP2pDevice toRemove = null;
            for (WifiP2pDevice device : peers) {
                if (device.deviceName.equals(deviceName)) {
                    toRemove = device;
                    break;
                }
            }
            if (toRemove != null) {
                peers.remove(toRemove);
            }
            updateDeviceList(peers); // Update the adapter
        });
    }

    public void clearDeviceList() {
        if (adapter != null) {
            peers.clear();
            adapter.clear();
            adapter.notifyDataSetChanged();
        }
    }

    public void updateConnectedDevices(String deviceName, String deviceId) {
        runOnUiThread(() -> {
            if (deviceName != null && !connectedDevices.contains(deviceName)) {
                connectedDevices.add(deviceName);
                connectedDevicesAdapter.notifyDataSetChanged();
                Log.d(TAG, "Device added to connected list: " + deviceName);

                // Store the device ID in the map with the device name
                deviceIdToNameMap.put(deviceId, deviceName);

                removeNearbyDevice(deviceName); // Remove from nearby devices list
            }
        });
    }

    public void startPeerDiscovery() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "Peer discovery started.");
                    retryCount = 0; // Reset the retry count on success
                }

                @Override
                public void onFailure(int reason) {
                    Log.d(TAG, "Peer discovery failed: " + reason);
                    if (reason == WifiP2pManager.BUSY && retryCount < MAX_RETRY_COUNT) {
                        retryCount++;
                        peerDiscoveryHandler.postDelayed(() -> startPeerDiscovery(), (long) Math.pow(2, retryCount) * 1000); // Exponential backoff
                    } else {
                        Log.e(TAG, "Peer discovery failed with reason code: " + reason);
                    }
                }
            });
            peerDiscoveryHandler.postDelayed(peerDiscoveryRunnable, 5000);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION);
        }
    }

    private void enableDiscoverability() {
        discoverabilityHandler.postDelayed(discoverabilityRunnable, 3000);
    }

    private void removeExistingGroup() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION);
            return;
        }

        if (manager == null || channel == null) {
            Log.e(TAG, "WifiP2pManager or Channel is null. Cannot proceed with removing group.");
            return;
        }

        manager.requestGroupInfo(channel, group -> {
            if (group != null) {
                manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Removed existing WiFi Direct group.");
                        startPeerDiscovery();
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.d(TAG, "Failed to remove existing WiFi Direct group: " + reason);
                        startPeerDiscovery();
                    }
                });
            } else {
                startPeerDiscovery();
            }
        });
    }

    private void showConnectDialog(WifiP2pDevice device) {
        new AlertDialog.Builder(this)
                .setTitle("Connect to Device")
                .setMessage("Do you want to connect to " + device.deviceName + "?")
                .setPositiveButton("Yes", (dialog, which) -> connectToPeer(device.deviceAddress))
                .setNegativeButton("No", null)
                .show();
    }

    public void connectToPeer(String deviceAddress) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION);
            return;
        }

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Connection initiated successfully.");
            }

            @Override
            public void onFailure(int reason) {
                Log.d(TAG, "Connection initiation failed: " + reason);
                new Handler().postDelayed(() -> connectToPeer(deviceAddress), 5000);
            }
        });
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        Log.d(TAG, "onConnectionInfoAvailable: " + info);
        if (info.groupFormed) {
            InetAddress groupOwnerAddress = info.groupOwnerAddress;
            isGroupOwner = info.isGroupOwner;
            Log.d(TAG, "Group formed. Group Owner IP: " + groupOwnerAddress.getHostAddress());

            if (isGroupOwner) {
                Log.d(TAG, "This device is the group owner. (Main)");
                startSocketServer();
                // Clear birthdates list at startup to ensure a fresh state
                SocketManager.getSocketServer().clearBirthdates();
                // Store the group owner's address
                updateConnectedDevices("Group Owner", getDeviceId()); // Assuming the group owner name as "Group Owner"
            } else {
                Log.d(TAG, "This device is not the group owner. Connecting to group owner.");
                retrySocketClient(groupOwnerAddress.getHostAddress(), 5);

                // Fetch the group owner's device name
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.NEARBY_WIFI_DEVICES) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                new Thread(() -> manager.requestGroupInfo(channel, group -> {
                    if (group != null) {
                        runOnUiThread(() -> updateConnectedDevices(group.getOwner().deviceName, getDeviceId()));
                    }
                })).start();
            }

            // Fetch device names of connected clients
            if (isGroupOwner) {
                new Thread(() -> manager.requestGroupInfo(channel, group -> {
                    if (group != null) {
                        for (WifiP2pDevice device : group.getClientList()) {
                            runOnUiThread(() -> updateConnectedDevices(device.deviceName, device.deviceAddress));
                        }
                    }
                })).start();
            }

            // Pass group info to BirthdateActivity
            Button buttonGoToBirthdateScreen = findViewById(R.id.buttonGoToBirthdateScreen);
            buttonGoToBirthdateScreen.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, BirthdateActivity.class);
                intent.putStringArrayListExtra("CONNECTED_DEVICES", new ArrayList<>(connectedDevices));
                intent.putExtra("GROUP_OWNER_ADDRESS", groupOwnerAddress.getHostAddress());
                intent.putExtra("IS_GROUP_OWNER", isGroupOwner);
                startActivity(intent);
            });
        } else {
            Log.d(TAG, "Group not formed.");
            isGroupOwner = false;
            // Attempt to re-establish the connection
            startPeerDiscovery();
        }
    }

    private void retrySocketClient(String hostAddress, int retries) {
        if (retries <= 0) {
            Log.d(TAG, "Client failed to connect after maximum retries.");
            return;
        }
        SocketClient client = new SocketClient(hostAddress, 8888);
        client.setConnectionListener(new SocketClient.ConnectionListener() {
            @Override
            public void onConnectionSuccess() {
                Log.d(TAG, "Client connected successfully.");
            }

            @Override
            public void onConnectionFailure(IOException e) {
                Log.e(TAG, "Client connection failed: " + e.getMessage());
                runOnUiThread(() -> new Handler().postDelayed(() -> retrySocketClient(hostAddress, retries - 1), 5000));
            }
        });
        client.setMessageListener(message -> runOnUiThread(() -> receivedMessagesTextView.append("\n" + message)));
        client.start();
        SocketManager.setSocketClient(client);
    }

    public void onSendMessageClick(View view) {
        if (isGroupOwner) {
            Log.d(TAG, "This device is the group owner.");
            SocketServer server = SocketManager.getSocketServer();
            if (server != null && server.isRunning()) {
                Log.d(TAG, "Socket server is running. Sending message from server.");
                server.sendMessage("Hello from server!", null);
            }
        } else {
            Log.d(TAG, "This device is not the group owner.");
            SocketClient client = SocketManager.getSocketClient();
            if (client != null && client.isConnected()) {
                Log.d(TAG, "Socket client is connected. Sending message from client.");
                client.sendMessage("Hello from client!");
            }
        }
    }

    private void startSocketServer() {
        SocketServer server = SocketManager.getSocketServer();
        if (server == null || !server.isRunning()) {
            server = new SocketServer(message -> runOnUiThread(() -> receivedMessagesTextView.append("\n" + message)), this); // Pass the context
            server.start();
            SocketManager.setSocketServer(server);
            Log.d(TAG, "Socket server started and listening for connections.");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            if (SocketManager.getSocketServer() != null) {
                SocketManager.getSocketServer().stop();
            }
            if (SocketManager.getSocketClient() != null) {
                try {
                    SocketManager.getSocketClient().closeClient();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing client socket", e);
                }
            }
        }
    }
}
