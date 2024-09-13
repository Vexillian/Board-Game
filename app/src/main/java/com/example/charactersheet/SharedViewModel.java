package com.example.charactersheet;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Map;

public class SharedViewModel extends ViewModel {

    private final Map<Integer, Map<Integer, MutableLiveData<String>>> lastSelectedModifier = new HashMap<>();
    private final Map<Integer, Map<Integer, MutableLiveData<String>>> lastSelectedEnemy = new HashMap<>();
    private final Map<Integer, Map<Integer, Map<String, MutableLiveData<Integer>>>> enemyCurrentHp = new HashMap<>();

    private final MutableLiveData<Integer> selectedZone = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> armorCrafted = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> weaponCrafted = new MutableLiveData<>(false);
    private final MutableLiveData<String> currentCraftedArmorName = new MutableLiveData<>("");
    private final MutableLiveData<String> currentCraftedWeaponName = new MutableLiveData<>("");
    private final MutableLiveData<Integer> exp = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> essencePoints = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> buttonsEnabled = new MutableLiveData<>(false);
    private final MutableLiveData<Map<String, Integer>> statUpdates = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Integer>> previousCraftedWeaponStats = new MutableLiveData<>(new HashMap<>());
    private final MutableLiveData<Map<String, Integer>> previousCraftedArmorStats = new MutableLiveData<>(new HashMap<>());

    private static final Map<String, Integer> maxStatValues = createMaxStatValues();

    private final Map<String, Integer> classStats = new HashMap<>();
    private final Map<String, Integer> currentStats = new HashMap<>();

    // Method to process incoming socket messages
    public void processSocketMessage(String message) {
        // Parse the message and update relevant LiveData
        // Example: If the message is an update for stats, update the stats LiveData
        if (message.startsWith("UPDATE_STAT:")) {
            String[] parts = message.split(":");
            String statName = parts[1];
            int value = Integer.parseInt(parts[2]);
            updateStat(statName, value);
        }
        // Add more cases as needed
    }

    // Initialize default stats
    public SharedViewModel() {
        initDefaultStats();
        initializeLastSelectedMaps();
    }

    private void initializeLastSelectedMaps() {
        for (int zone = 0; zone < EnemyUtil.enemies.length; zone++) {
            lastSelectedEnemy.put(zone, new HashMap<>());
            lastSelectedModifier.put(zone, new HashMap<>());
        }
    }

    private static Map<String, Integer> createMaxStatValues() {
        Map<String, Integer> maxStatValues = new HashMap<>();
        maxStatValues.put("Tactics", 6);
        maxStatValues.put("Introspection", 6);
        maxStatValues.put("Agility", 15);
        maxStatValues.put("Precision", 15);
        maxStatValues.put("Max HP", 100);
        maxStatValues.put("Current HP", 100);
        // Add other stats and their max values as necessary
        return maxStatValues;
    }

    private void initDefaultStats() {
        currentStats.put("Max HP", 5);
        currentStats.put("Current HP", 5);
        currentStats.put("Might", 5);
        currentStats.put("Intuition", 5);
        currentStats.put("Agility", 1);
        currentStats.put("Precision", 1);
        currentStats.put("Tactics", 3);
        currentStats.put("Introspection", 0);
        currentStats.put("Physical Resistance", 0);
        currentStats.put("Magical Resistance", 0);
    }

    // Getter methods
    public LiveData<Boolean> isArmorCrafted() {
        return armorCrafted;
    }

    public LiveData<Boolean> isWeaponCrafted() {
        return weaponCrafted;
    }

    public LiveData<String> getCurrentCraftedArmorName() {
        return currentCraftedArmorName;
    }

    public LiveData<String> getCurrentCraftedWeaponName() {
        return currentCraftedWeaponName;
    }

    public LiveData<Boolean> areButtonsEnabled() {
        return buttonsEnabled;
    }

    public LiveData<Integer> getEssencePoints() {
        return essencePoints;
    }

    public LiveData<Integer> getSelectedZone() {
        return selectedZone;
    }

    // Setter methods
    public void setSelectedZone(final int zone) {
        selectedZone.setValue(zone);
    }

    public void setLastSelectedModifier(final int zone, final int fragmentNumber, final String modifierName) {
        lastSelectedModifier.computeIfAbsent(fragmentNumber, k -> new HashMap<>())
                .computeIfAbsent(zone, k -> new MutableLiveData<>())
                .setValue(modifierName);
    }

    public void setLastSelectedEnemy(final int zone, final int fragmentNumber, final String enemyName) {
        lastSelectedEnemy.computeIfAbsent(fragmentNumber, k -> new HashMap<>())
                .computeIfAbsent(zone, k -> new MutableLiveData<>())
                .setValue(enemyName);
    }

    public LiveData<String> getLastSelectedModifier(final Integer zone, final int fragmentNumber) {
        Map<Integer, MutableLiveData<String>> fragmentModifiers = lastSelectedModifier.get(fragmentNumber);
        return fragmentModifiers != null ? fragmentModifiers.get(zone) : null;
    }

    public LiveData<String> getLastSelectedEnemy(final Integer zone, final int fragmentNumber) {
        Map<Integer, MutableLiveData<String>> fragmentEnemies = lastSelectedEnemy.get(fragmentNumber);
        return fragmentEnemies != null ? fragmentEnemies.get(zone) : null;
    }

    public void updateEssencePoints(final int change) {
        int currentPoints = essencePoints.getValue() != null ? essencePoints.getValue() : 0;
        essencePoints.setValue(currentPoints + change);
    }

    public void addExp(final int value) {
        int currentExp = exp.getValue() != null ? exp.getValue() : 0;
        exp.setValue(currentExp + value);
    }

    public LiveData<Integer> getExp() {
        return exp;
    }

    public void setButtonsEnabled(final boolean enabled) {
        buttonsEnabled.setValue(enabled);
    }

    public void updateStat(final String stat, final int value, final boolean bypassLimits) {
        int currentStat = currentStats.getOrDefault(stat, 0);
        int maxStat = maxStatValues.getOrDefault(stat, Integer.MAX_VALUE);
        int newValue = bypassLimits ? currentStat + value : Math.min(currentStat + value, maxStat);

        if (newValue != currentStat) {
            currentStats.put(stat, newValue);

            Map<String, Integer> updates = new HashMap<>();
            updates.put(stat, newValue - currentStat);
            statUpdates.setValue(updates);
        } else if (!bypassLimits) {
            Map<String, Integer> updates = new HashMap<>();
            updates.put(stat, 0); // No change in value but triggers update
            statUpdates.setValue(updates);
        }
    }

    // Overloaded method for backward compatibility
    public void updateStat(final String stat, final int value) {
        updateStat(stat, value, false);
    }

    public int getCurrentStatValue(final String stat) {
        return currentStats.getOrDefault(stat, 0);
    }

    public int getMaxStatValue(final String stat) {
        return maxStatValues.getOrDefault(stat, Integer.MAX_VALUE);
    }

    public LiveData<Map<String, Integer>> getStatUpdates() {
        return statUpdates;
    }

    private void removeStats(final MutableLiveData<Map<String, Integer>> previousCraftedStats) {
        Map<String, Integer> entries = previousCraftedStats.getValue();
        if (entries == null) return;

        for (Map.Entry<String, Integer> entry : entries.entrySet()) {
            updateStat(entry.getKey(), -entry.getValue());
        }
        previousCraftedStats.setValue(new HashMap<>());
    }

    public void removeCraftedArmorStats() {
        removeStats(previousCraftedArmorStats);
    }

    public void removeCraftedWeaponStats() {
        removeStats(previousCraftedWeaponStats);
    }

    private void updateItemNameAndStats(final Enemy enemy, final Modifier currentModifier, final int stat, final boolean isArmor, final StringBuilder itemText) {
        int ratio = EnemyUtil.getRatioByStatIndex(stat);
        int rowIndex = selectedZone.getValue() != null ? selectedZone.getValue() : 0;
        int enemyStatValue = enemy.getStatByIndex(stat) + currentModifier.getStatByIndex(rowIndex, stat);
        int statValue = enemyStatValue / ratio;

        itemText.append("\n")
                .append(EnemyUtil.getStatNameByIndex(stat))
                .append(" +")
                .append(statValue);

        updateStat(EnemyUtil.getStatNameByIndex(stat), statValue);

        Map<String, Integer> itemStats = isArmor ? previousCraftedArmorStats.getValue() : previousCraftedWeaponStats.getValue();
        if (itemStats != null) {
            itemStats.put(EnemyUtil.getStatNameByIndex(stat), statValue);
            if (isArmor) {
                previousCraftedArmorStats.setValue(itemStats);
            } else {
                previousCraftedWeaponStats.setValue(itemStats);
            }
        }
    }

    private String craftItem(final Enemy enemy, final Modifier currentModifier, final boolean isArmor) {
        String itemType = isArmor ? "Armor" : "Weapon";
        int[] craftStats = isArmor ? enemy.getCraftArmorStats() : enemy.getCraftWeaponStats();

        if (craftStats.length == 0) {
            return "Crafting " + itemType.toLowerCase() + " from this enemy is not possible.";
        }

        String itemName = currentModifier.getName() + " " + enemy.getName() + " " + itemType;

        String currentCraftedItemName = isArmor ? getCurrentCraftedArmorName().getValue() : getCurrentCraftedWeaponName().getValue();

        if ((isArmor && Boolean.TRUE.equals(isArmorCrafted().getValue())) || (!isArmor && Boolean.TRUE.equals(isWeaponCrafted().getValue()))) {
            if (itemName.equals(currentCraftedItemName)) {
                return "You already have this " + itemType.toLowerCase() + " crafted.";
            }
            if (isArmor) {
                removeCraftedArmorStats();
            } else {
                removeCraftedWeaponStats();
            }
        }

        if (isArmor) {
            currentCraftedArmorName.setValue(itemName);
            armorCrafted.setValue(true);
        } else {
            currentCraftedWeaponName.setValue(itemName);
            weaponCrafted.setValue(true);
        }

        StringBuilder itemText = new StringBuilder(currentModifier.getName() + " " + enemy.getName() + " " + itemType);

        for (int stat : craftStats) {
            updateItemNameAndStats(enemy, currentModifier, stat, isArmor, itemText);
        }

        return itemText.toString();
    }

    public String craftArmor(final Enemy enemy, final Modifier currentModifier) {
        return craftItem(enemy, currentModifier, true);
    }

    public String craftWeapon(final Enemy enemy, final Modifier currentModifier) {
        return craftItem(enemy, currentModifier, false);
    }

    public void setClassStats(final Map<String, Integer> classStats1, final Map<String, Integer> classStats2) {
        classStats.clear();
        for
        (Map.Entry<String, Integer> entry : classStats1.entrySet()) {
            classStats.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
        for (Map.Entry<String, Integer> entry : classStats2.entrySet()) {
            classStats.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
        // Initialize currentStats to reflect initial values
        for (Map.Entry<String, Integer> entry : classStats.entrySet()) {
            currentStats.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
    }
}
