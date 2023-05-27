package com.example.charactersheet;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SharedViewModel extends ViewModel {

    private final Map<Integer, Map<Integer, MutableLiveData<Integer>>> lastSelectedModifier = new HashMap<>();
    private final Map<Integer, Map<Integer, MutableLiveData<Integer>>> lastSelectedEnemy = new HashMap<>();

    private final MutableLiveData<Integer> selectedZone = new MutableLiveData<>(0);

    private final MutableLiveData<Boolean> armorCrafted = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> weaponCrafted = new MutableLiveData<>(false);

    private final MutableLiveData<String> currentCraftedArmorName = new MutableLiveData<>("");
    private final MutableLiveData<String> currentCraftedWeaponName = new MutableLiveData<>("");
    private final MutableLiveData<Integer> exp = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> essencePoints = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> buttonsEnabled = new MutableLiveData<>(false);

    private final MutableLiveData<Map<String, Integer>> previousCraftedArmorStats = new MutableLiveData<>(new HashMap<>());
    private final MutableLiveData<Map<String, Integer>> previousCraftedWeaponStats = new MutableLiveData<>(new HashMap<>());

    private final MutableLiveData<Map<String, Integer>> statUpdates = new MutableLiveData<>();

    public MutableLiveData<Boolean> isArmorCrafted() {
        return armorCrafted;
    }

    public MutableLiveData<Boolean> isWeaponCrafted() {
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

    public void setSelectedZone(int zone) {
        selectedZone.setValue(zone);
    }

    public void setLastSelectedModifier(int zone, int fragmentNumber, int modifierIndex) {
        if (!lastSelectedModifier.containsKey(fragmentNumber)) {
            lastSelectedModifier.put(fragmentNumber, new HashMap<>());
        }
        Map<Integer, MutableLiveData<Integer>> map = lastSelectedModifier.get(fragmentNumber);
        assert map != null;
        if (!map.containsKey(zone)) {
            map.put(zone, new MutableLiveData<>());
        }
        Objects.requireNonNull(map.get(zone)).setValue(modifierIndex);
    }

    public void setLastSelectedEnemy(int zone, int fragmentNumber, int enemyIndex) {
        if (!lastSelectedEnemy.containsKey(fragmentNumber)) {
            lastSelectedEnemy.put(fragmentNumber, new HashMap<>());
        }
        Map<Integer, MutableLiveData<Integer>> map = lastSelectedEnemy.get(fragmentNumber);
        assert map != null;
        if (!map.containsKey(zone)) {
            map.put(zone, new MutableLiveData<>());
        }
        Objects.requireNonNull(map.get(zone)).setValue(enemyIndex);
    }

    public LiveData<Integer> getLastSelectedModifier(Integer zone, int fragmentNumber) {
        if (!lastSelectedModifier.containsKey(fragmentNumber)) {
            return null;
        }
        Map<Integer, MutableLiveData<Integer>> map = lastSelectedModifier.get(fragmentNumber);
        assert map != null;
        return map.get(zone);
    }

    public LiveData<Integer> getLastSelectedEnemy(int zone, int fragmentNumber) {
        if (!lastSelectedEnemy.containsKey(fragmentNumber)) {
            return null;
        }
        Map<Integer, MutableLiveData<Integer>> map = lastSelectedEnemy.get(fragmentNumber);
        assert map != null;
        return map.get(zone);
    }
    public void updateEssencePoints(int change) {
        Integer currentValue = essencePoints.getValue();
        if (currentValue != null) {
            essencePoints.setValue(currentValue + change);
        } else {
            essencePoints.setValue(change);
        }
    }

    public void addExp(int value) {
        Integer currentValue = exp.getValue();
        if (currentValue != null) {
            exp.setValue(currentValue + value);
        } else {
            exp.setValue(value);
        }
    }

    public LiveData<Integer> getExp() {
        return exp;
    }

    public void setButtonsEnabled(boolean enabled) {
        buttonsEnabled.setValue(enabled);
    }

    public void updateStat(String stat, int value, boolean ignoredStore) {
        Map<String, Integer> updates = new HashMap<>();
        updates.put(stat, value);
        if (stat.equals("HP")) {
            updates.put("Max HP", value);
        }
        statUpdates.setValue(updates);
    }

    public void updateStat(String stat, int value) {
        updateStat(stat, value, true);
    }

    public LiveData<Map<String, Integer>> getStatUpdates() {
        return statUpdates;
    }

    private void removeStats(MutableLiveData<Map<String, Integer>> previousCraftedStats) {
        Map<String, Integer> entries = previousCraftedStats.getValue();
        if (entries == null) {
            return;
        }

        for (Map.Entry<String, Integer> entry : entries.entrySet()) {
            updateStat(entry.getKey(), -entry.getValue(), false);
        }
        previousCraftedStats.setValue(new HashMap<>());
    }

    public void removeCraftedArmorStats() {
        removeStats(previousCraftedArmorStats);
    }

    public void removeCraftedWeaponStats() {
        removeStats(previousCraftedWeaponStats);
    }

    private void updateItemNameAndStats(Enemy enemy, Modifier currentModifier, int stat, boolean isArmor, StringBuilder itemText) {
        int ratio = EnemyUtil.getRatioByStatIndex(stat);
        int rowIndex = selectedZone.getValue() != null ? selectedZone.getValue() : 0;
        int enemyStatValue = enemy.getStatByIndex(stat) + currentModifier.getStatByIndex(rowIndex, stat);
        int statValue = enemyStatValue / ratio;

        itemText.append("\n")
                .append(EnemyUtil.getStatNameByIndex(stat))
                .append(" +")
                .append(statValue);

        if (EnemyUtil.getStatNameByIndex(stat).equals("HP")) {
            updateStat("Max HP", statValue);
        } else {
            updateStat(EnemyUtil.getStatNameByIndex(stat), statValue);
        }

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

    private String craftItem(Enemy enemy, Modifier currentModifier, boolean isArmor) {
        String itemType = isArmor ? "Armor" : "Weapon";
        int[] craftStats = isArmor ? enemy.getCraftArmorStats() : enemy.getCraftWeaponStats();

        if (craftStats.length == 0) {
            return "Crafting " + itemType.toLowerCase() + " from this enemy is not possible.";
        }

        StringBuilder itemName = new StringBuilder();

        itemName.append(currentModifier.getName()).append(" ").append(enemy.getName()).append(" ").append(itemType);

        String currentCraftedItemName = isArmor ? getCurrentCraftedArmorName().getValue() : getCurrentCraftedWeaponName().getValue();

        if ((isArmor && Boolean.TRUE.equals(isArmorCrafted().getValue())) || (!isArmor && Boolean.TRUE.equals(isWeaponCrafted().getValue()))) {
            if (currentCraftedItemName != null && currentCraftedItemName.equals(itemName.toString())) {
                return "You already have this " + itemType.toLowerCase() + " crafted.";
            }
            if (isArmor) {
                removeCraftedArmorStats();
            } else {
                removeCraftedWeaponStats();
            }
        }

        if (isArmor) {
            currentCraftedArmorName.setValue(itemName.toString());
            armorCrafted.setValue(true);
        } else {
            currentCraftedWeaponName.setValue(itemName.toString());
            weaponCrafted.setValue(true);
        }

        StringBuilder itemText = new StringBuilder();

        itemText.append(currentModifier.getName()).append(" ").append(enemy.getName()).append(" ").append(itemType);

        for (int stat : craftStats) {
            updateItemNameAndStats(enemy, currentModifier, stat, isArmor, itemText);
        }

        return itemText.toString();
    }

    public String craftArmor(Enemy enemy, Modifier currentModifier) {
        return craftItem(enemy, currentModifier, true);
    }

    public String craftWeapon(Enemy enemy, Modifier currentModifier) {
        return craftItem(enemy, currentModifier, false);
    }
}

