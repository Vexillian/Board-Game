package com.example.charactersheet;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class EnemySubFragment1 extends Fragment {

    private SharedViewModel sharedViewModel;
    private int currentHP;
    private Modifier currentModifier;
    private TextView hpTextView;
    private Spinner enemySpinner;
    private Spinner modifierSpinner;
    private Button defeatedButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enemy_sub1, container, false);

        initializeViews(view);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        setupSpinners();
        setupButtons(view);
        observeSelectedZone();

        return view;
    }

    private void initializeViews(View view) {
        hpTextView = view.findViewById(R.id.hpTextView);
        enemySpinner = view.findViewById(R.id.enemySpinner);
        modifierSpinner = view.findViewById(R.id.modifierSpinner);
        defeatedButton = view.findViewById(R.id.enemyDefeatedButton);
    }

    private void setupSpinners() {
        setupSpinner(modifierSpinner, Modifier.getModifierNames(), new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentModifier = Modifier.getModifierByIndex(position);
                filterEnemiesBasedOnModifier();
                updateSelectedEnemyData();
                Integer selectedZone = sharedViewModel.getSelectedZone().getValue();
                if (selectedZone != null) {
                    sharedViewModel.setLastSelectedModifier(selectedZone, 1, currentModifier.getName());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        setupSpinner(enemySpinner, new ArrayList<>(), new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateSelectedEnemyData();
                Integer selectedZone = sharedViewModel.getSelectedZone().getValue();
                if (selectedZone != null) {
                    sharedViewModel.setLastSelectedEnemy(selectedZone, 1, (String) enemySpinner.getSelectedItem());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    private void setupSpinner(Spinner spinner, List<String> items, AdapterView.OnItemSelectedListener listener) {
        if (getActivity() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), R.layout.spinner_item_black, items);
            adapter.setDropDownViewResource(R.layout.spinner_item_black);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(listener);
        }
    }

    private void filterEnemiesBasedOnModifier() {
        List<String> filteredEnemies = new ArrayList<>();
        Integer selectedZone = sharedViewModel.getSelectedZone().getValue();
        if (selectedZone == null) {
            selectedZone = 0; // Default zone if selectedZone is null
        }
        for (Enemy enemy : EnemyUtil.enemies[selectedZone]) {
            if (currentModifier.getName().equals("None") || (!enemy.getName().equals("Bandit") && !enemy.getName().equals("Mystic Bedlamite"))) {
                filteredEnemies.add(enemy.getName());
            }
        }
        updateSpinnerData(enemySpinner, filteredEnemies);
        // Restore the previously selected enemy for the current zone
        restoreSpinnerSelectionFromViewModel(enemySpinner, sharedViewModel.getLastSelectedEnemy(selectedZone, 1));
    }


    private void setupButtons(View view) {
        Button decrementHPButton = view.findViewById(R.id.decrementHPButton);
        Button incrementHPButton = view.findViewById(R.id.incrementHPButton);
        Button craftWeaponButton = view.findViewById(R.id.craftWeaponButton);
        Button craftArmorButton = view.findViewById(R.id.craftArmorButton);

        if (getActivity() != null) {
            int armorButtonColor = ContextCompat.getColor(requireActivity(), R.color.lightBlue);
            int weaponButtonColor = ContextCompat.getColor(requireActivity(), R.color.red);
            craftWeaponButton.setBackgroundTintList(ColorStateList.valueOf(weaponButtonColor));
            craftArmorButton.setBackgroundTintList(ColorStateList.valueOf(armorButtonColor));
        }

        decrementHPButton.setOnClickListener(v -> adjustHP(-1));
        incrementHPButton.setOnClickListener(v -> adjustHP(1));
        setupCraftButton(craftArmorButton, true);
        setupCraftButton(craftWeaponButton, false);

        defeatedButton.setEnabled(false);
        defeatedButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Was the enemy defeated?")
                    .setPositiveButton("Yes", (dialog, id) -> {
                        ClassDetailsActivity activity = (ClassDetailsActivity) getActivity();
                        int selectedZone = activity.getSelectedZone();
                        String selectedEnemyName = (String) enemySpinner.getSelectedItem();
                        Enemy enemy = EnemyUtil.getEnemyByName(selectedZone, selectedEnemyName);
                        currentModifier.getDefeatStrategy().applyEffectAfterDefeat(sharedViewModel, getContext(), selectedZone, enemy);

                        int maxHP = enemy.getMaxHp();
                        currentHP = maxHP;
                        hpTextView.setText(String.valueOf(maxHP + currentModifier.getHp(selectedZone)));

                        int finalExp = enemy.getExperience() + currentModifier.getExperience(selectedZone);
                        sharedViewModel.addExp(finalExp);
                        enemy.setCurrentHP();
                    })
                    .setNegativeButton("No", (dialog, id) -> dialog.dismiss());
            builder.create().show();
        });
    }

    private void observeSelectedZone() {
        sharedViewModel.getSelectedZone().observe(getViewLifecycleOwner(), selectedZone -> {
            updateSpinners(selectedZone);

            // Restore the previously selected modifier and enemy
            restoreSpinnerSelectionFromViewModel(modifierSpinner, sharedViewModel.getLastSelectedModifier(selectedZone, 1));
            restoreSpinnerSelectionFromViewModel(enemySpinner, sharedViewModel.getLastSelectedEnemy(selectedZone, 1));
        });
    }

    private void restoreSpinnerSelectionFromViewModel(Spinner spinner, LiveData<String> selectionLiveData) {
        if (selectionLiveData != null && selectionLiveData.getValue() != null) {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
            int position = adapter.getPosition(selectionLiveData.getValue());
            if (position >= 0) {
                spinner.setSelection(position);
            }
        }
    }



    private void updateSpinners(int selectedZone) {
        updateSpinnerData(enemySpinner, getEnemyNames(selectedZone));
        updateSpinnerData(modifierSpinner, Modifier.getModifierNames());

        // Restore the spinner selection from ViewModel after updating the data
        restoreSpinnerSelectionFromViewModel(modifierSpinner, sharedViewModel.getLastSelectedModifier(selectedZone, 1));
        restoreSpinnerSelectionFromViewModel(enemySpinner, sharedViewModel.getLastSelectedEnemy(selectedZone, 1));
    }


    private List<String> getEnemyNames(int selectedZone) {
        List<String> names = new ArrayList<>();
        for (Enemy enemy : EnemyUtil.enemies[selectedZone]) {
            names.add(enemy.getName());
        }
        return names;
    }



    private void updateSpinnerData(Spinner spinner, List<String> data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), R.layout.spinner_item_black, data);
        adapter.setDropDownViewResource(R.layout.spinner_item_black);
        spinner.setAdapter(adapter);
    }


    private void resetCurrentHp(String enemyName) {
        ClassDetailsActivity activity = (ClassDetailsActivity) getActivity();
        if (activity != null) {
            int selectedZone = activity.getSelectedZone();
            Enemy enemy = EnemyUtil.getEnemyByName(selectedZone, enemyName);
            currentHP = enemy.getMaxHp() + currentModifier.getHp(selectedZone);
            hpTextView.setText(String.valueOf(currentHP));
        }
    }

    private void updateSelectedEnemyData() {
        String selectedEnemyName = (String) enemySpinner.getSelectedItem();
        if (selectedEnemyName != null) {
            ClassDetailsActivity activity = (ClassDetailsActivity) getActivity();
            if (activity != null) {
                int selectedZone = activity.getSelectedZone();
                Enemy enemy = EnemyUtil.getEnemyByName(selectedZone, selectedEnemyName);
                if (defeatedButton != null) {
                    defeatedButton.setEnabled(!selectedEnemyName.equals("None"));
                }
                resetCurrentHp(enemy.getName());
                updateEnemyStatsTable(enemy);
            }
        }
    }

    private void adjustHP(int amount) {
        currentHP += amount;
        hpTextView.setText(String.valueOf(currentHP));
    }

    private void setupCraftButton(Button button, boolean isArmor) {
        button.setOnClickListener(v -> craftItem(isArmor));
    }

    private void craftItem(boolean isArmor) {
        ClassDetailsActivity activity = (ClassDetailsActivity) getActivity();
        if (activity == null) return;

        int selectedZone = activity.getSelectedZone();
        String selectedEnemyName = (String) enemySpinner.getSelectedItem();
        Enemy enemy = EnemyUtil.getEnemyByName(selectedZone, selectedEnemyName);
        String itemName = currentModifier.getName() + " " + enemy.getName() + (isArmor ? " Armor" : " Weapon");

        if ((isArmor && Objects.equals(sharedViewModel.getCurrentCraftedArmorName().getValue(), itemName)) ||
                (!isArmor && Objects.equals(sharedViewModel.getCurrentCraftedWeaponName().getValue(), itemName))) {
            Toast.makeText(getActivity(), "You already have this item crafted.", Toast.LENGTH_SHORT).show();
            return;
        }

        if ((isArmor && enemy.getCraftArmorStats().length == 0) ||
                (!isArmor && enemy.getCraftWeaponStats().length == 0)) {
            Toast.makeText(getActivity(), "Crafting this item from this enemy is not possible.", Toast.LENGTH_SHORT).show();
            return;
        }

        int tokenCost = selectedZone + 2;
        new AlertDialog.Builder(getContext())
                .setTitle("Craft " + (isArmor ? "Armor" : "Weapon"))
                .setMessage("Do you want to craft and equip " + itemName + "? It will cost you " + tokenCost + " Monster Tokens. If you already have an item, you will lose it.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    String result = isArmor ? sharedViewModel.craftArmor(enemy, currentModifier) : sharedViewModel.craftWeapon(enemy, currentModifier);
                    clearOtherFragmentsItemText(isArmor);
                    updateItemStatsTextView(isArmor, result);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void clearOtherFragmentsItemText(boolean isArmor) {
        for (int i = 0; i < 3; i++) {
            Fragment fragment = EnemyFragment.getEnemyPagerAdapter().getFragment(i);
            if (fragment instanceof EnemySubFragment1 && fragment != this) {
                ((EnemySubFragment1) fragment).clearItemStatsTextView(isArmor);
            } else if (fragment instanceof EnemySubFragment2 && fragment != this) {
                ((EnemySubFragment2) fragment).clearItemStatsTextView(isArmor);
            } else if (fragment instanceof EnemySubFragment3 && fragment != this) {
                ((EnemySubFragment3) fragment).clearItemStatsTextView(isArmor);
            }
        }
    }

    public void clearItemStatsTextView(boolean isArmor) {
        TextView itemStatsTextView = requireView().findViewById(isArmor ? R.id.armor_stats_text_view : R.id.weapon_stats_text_view);
        if (itemStatsTextView != null) itemStatsTextView.setText("");
    }

    private void updateItemStatsTextView(boolean isArmor, String result) {
        TextView itemStatsTextView = requireView().findViewById(isArmor ? R.id.armor_stats_text_view : R.id.weapon_stats_text_view);
        if (itemStatsTextView != null) itemStatsTextView.setText(result);
    }


    private void updateEnemyStatsTable(Enemy enemy) {
        ClassDetailsActivity activity = (ClassDetailsActivity) getActivity();
        if (activity == null) return;

        int selectedZone = activity.getSelectedZone();
        currentHP = enemy.getHp() + currentModifier.getHp(selectedZone);

        TableLayout enemyStatsTable = requireView().findViewById(R.id.enemyStatsTable);
        enemyStatsTable.removeAllViews();

        int borderColorId = getBorderColorId(selectedZone);
        enemyStatsTable.setBackgroundResource(borderColorId);

        String[] statNames = {
                "Max HP", "Might", "Intuition", "Agility", "Precision",
                "Physical Resistance", "Magical Resistance", "Experience", "Initiative"
        };

        int[] enemyStats = {
                enemy.getMaxHp() + currentModifier.getHp(selectedZone),
                enemy.getMight() + currentModifier.getMight(selectedZone),
                enemy.getIntuition() + currentModifier.getIntuition(selectedZone),
                enemy.getAgility() + currentModifier.getAgility(selectedZone),
                enemy.getPrecision() + currentModifier.getPrecision(selectedZone),
                enemy.getPhysicalResistance() + currentModifier.getPhysicalResistance(selectedZone),
                enemy.getMagicalResistance() + currentModifier.getMagicalResistance(selectedZone),
                enemy.getExperience() + currentModifier.getExperience(selectedZone),
                enemy.getInitiative() + currentModifier.getInitiative(selectedZone)
        };

        for (int i = 0; i < statNames.length; i++) {
            addStatRow(enemyStatsTable, statNames[i], enemyStats[i], enemy, i);
        }
    }

    private int getBorderColorId(int selectedZone) {
        switch (selectedZone) {
            case 1:
                return R.drawable.table_border_zone2;
            case 2:
                return R.drawable.table_border_zone3;
            case 3:
                return R.drawable.table_border_zone4;
            default:
                return R.drawable.table_border_zone1;
        }
    }

    private void addStatRow(TableLayout table, String statName, int statValue, Enemy enemy, int statIndex) {
        TableRow row = new TableRow(getContext());
        row.setBackgroundColor(statIndex % 2 == 0 ? ContextCompat.getColor(requireContext(), R.color.tableRowColor1) : ContextCompat.getColor(requireContext(), R.color.tableRowColor2));

        TextView nameTextView = createTextView(statName);
        TextView valueTextView = createTextView(String.valueOf(statValue));

        applyStatColoring(nameTextView, valueTextView, enemy, statIndex);

        row.addView(nameTextView);
        row.addView(valueTextView);

        table.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        textView.setTextColor(Color.WHITE);
        return textView;
    }

    private void applyStatColoring(TextView nameTextView, TextView valueTextView, Enemy enemy, int statIndex) {
        boolean isArmorStat = Arrays.stream(enemy.getCraftArmorStats()).anyMatch(stat -> stat == statIndex);
        boolean isWeaponStat = Arrays.stream(enemy.getCraftWeaponStats()).anyMatch(stat -> stat == statIndex);

        int weaponAndArmorColor = ContextCompat.getColor(requireActivity(), R.color.purple_200);
        int weaponButtonColor = ContextCompat.getColor(requireActivity(), R.color.red);
        int armorButtonColor = ContextCompat.getColor(requireActivity(), R.color.lightBlue);

        if (isArmorStat && isWeaponStat) {
            setTextViewColor(nameTextView, valueTextView, weaponAndArmorColor);
        } else if (isArmorStat) {
            setTextViewColor(nameTextView, valueTextView, armorButtonColor);
        } else if (isWeaponStat) {
            setTextViewColor(nameTextView, valueTextView, weaponButtonColor);
        }
    }

    private void setTextViewColor(TextView nameTextView, TextView valueTextView, int color) {
        nameTextView.setTextColor(color);
        nameTextView.setTypeface(null, Typeface.BOLD);
        valueTextView.setTextColor(color);
        valueTextView.setTypeface(null, Typeface.BOLD);
    }
}
