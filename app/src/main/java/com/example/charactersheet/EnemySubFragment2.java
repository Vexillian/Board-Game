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

public class EnemySubFragment2 extends Fragment {

    private SharedViewModel sharedViewModel;
    private int currentHP;
    private Modifier currentModifier;
    private TextView hpTextView;
    private Spinner enemySpinner;
    private Spinner modifierSpinner;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enemy_sub2, container, false);


        enemySpinner = view.findViewById(R.id.enemySpinner);
        modifierSpinner = view.findViewById(R.id.modifierSpinner);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        sharedViewModel.getSelectedZone().observe(getViewLifecycleOwner(), selectedZone -> {
            // Update enemy list whenever zone changes
            updateEnemyList(selectedZone);
            updateModifierList(selectedZone);

            LiveData<Integer> lastSelectedEnemyLiveData = sharedViewModel.getLastSelectedEnemy(selectedZone, 2);
            if (lastSelectedEnemyLiveData != null) {
                Integer enemyIndex = lastSelectedEnemyLiveData.getValue();
                if (enemyIndex != null) {
                    enemySpinner.setSelection(enemyIndex);
                    updateEnemyStatsTable(enemyIndex);
                }
            }

            LiveData<Integer> lastSelectedModifierLiveData = sharedViewModel.getLastSelectedModifier(selectedZone, 2);
            if (lastSelectedModifierLiveData != null) {
                Integer modifierIndex = lastSelectedModifierLiveData.getValue();
                if (modifierIndex != null) {
                    modifierSpinner.setSelection(modifierIndex);
                }
            }

        });

        hpTextView = view.findViewById(R.id.hpTextView);

        List<String> modifierNames = Modifier.getModifierNames();
        ArrayAdapter<String> modifierAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, modifierNames);

        List<Enemy> enemyList = Arrays.asList(EnemyUtil.enemies[0]); // change the index based on selected zone
        ArrayAdapter<Enemy> enemyAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item,
                enemyList
        );
        modifierSpinner.setAdapter(modifierAdapter);
        enemySpinner.setAdapter(enemyAdapter);
        modifierAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        enemyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Button decrementHPButton = view.findViewById(R.id.decrementHPButton);
        Button incrementHPButton = view.findViewById(R.id.incrementHPButton);
        Button craftWeaponButton = view.findViewById(R.id.craftWeaponButton);
        Button craftArmorButton = view.findViewById(R.id.craftArmorButton);
        int armorButtonColor = ContextCompat.getColor(requireActivity(), R.color.lightBlue);
        int weaponButtonColor = ContextCompat.getColor(getActivity(), R.color.red);
        craftWeaponButton.setBackgroundTintList(ColorStateList.valueOf(weaponButtonColor));
        craftArmorButton.setBackgroundTintList(ColorStateList.valueOf(armorButtonColor));

        decrementHPButton.setOnClickListener(v -> {
            ClassDetailsActivity activity = (ClassDetailsActivity) getActivity();
            assert activity != null;
            currentHP = Integer.parseInt(hpTextView.getText().toString());
            if (currentHP > 0) {
                currentHP--;
                hpTextView.setText(String.valueOf(currentHP));
            }
        });

        incrementHPButton.setOnClickListener(v -> {
            ClassDetailsActivity activity = (ClassDetailsActivity) getActivity();
            assert activity != null;
            currentHP = Integer.parseInt(hpTextView.getText().toString());
            currentHP++;
            hpTextView.setText(String.valueOf(currentHP));
        });

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getSelectedZone().observe(getViewLifecycleOwner(), this::updateEnemyList);

        Button defeatedButton = view.findViewById(R.id.enemyDefeatedButton);
        defeatedButton.setEnabled(false);
        defeatedButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Was the enemy defeated?")
                    .setPositiveButton("Yes", (dialog, id) -> {
                        // User confirmed that the enemy was defeated
                        ClassDetailsActivity activity = (ClassDetailsActivity) getActivity();
                        int selectedZone = activity.getSelectedZone();
                        Enemy enemy = EnemyUtil.getEnemyByZoneAndIndex(selectedZone, enemySpinner.getSelectedItemPosition());
                        currentModifier.getDefeatStrategy().applyEffectAfterDefeat(sharedViewModel, getContext(), selectedZone, enemy);

                        // Reset enemy's HP to its max HP
                        int maxHP = enemy.getMaxHp();

                        // Update currentHP as well
                        currentHP = maxHP;

                        hpTextView.setText(String.valueOf(maxHP + currentModifier.getHp(selectedZone)));

                        // Reward experience points
                        int finalExp = enemy.getExperience() + currentModifier.getExperience(selectedZone);
                        sharedViewModel.addExp(finalExp);
                        enemy.setCurrentHP();
                    })
                    .setNegativeButton("No", (dialog, id) -> {
                        // User cancelled the dialog
                        dialog.dismiss();
                    });
            builder.create().show();
        });

        craftArmorButton.setOnClickListener(v -> {
            // Get the current enemy
            ClassDetailsActivity activity = (ClassDetailsActivity) getActivity();
            int selectedZone = activity.getSelectedZone();
            Enemy enemy = EnemyUtil.getEnemyByZoneAndIndex(selectedZone, enemySpinner.getSelectedItemPosition());
            String itemName = currentModifier.getName() + " " + enemy.getName() + " Armor";

            if (Objects.equals(sharedViewModel.getCurrentCraftedArmorName().getValue(), itemName)) {
                Toast.makeText(getActivity(), "You already have this armor crafted.", Toast.LENGTH_SHORT).show();
                return;
            } else if (enemy.getCraftArmorStats().length == 0) {
                Toast.makeText(getActivity(), "Crafting armor from this enemy is not possible.", Toast.LENGTH_SHORT).show();
                return;
            }

            int tokenCost = selectedZone + 2;


            new AlertDialog.Builder(getContext())
                    .setTitle("Craft Armor")
                    .setMessage("Do you want to craft and equip " + currentModifier.getName() + " " + enemy.getName() + " Armor? It will cost you " + tokenCost + " Monster Tokens. If you already have armor, you will lose it.")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        String result = sharedViewModel.craftArmor(enemy, currentModifier);
                        // Clear armor text in other SubFragments
                        EnemySubFragment1 subFragmentOne = (EnemySubFragment1) EnemyFragment.getEnemyPagerAdapter().getFragment(0);
                        EnemySubFragment3 subFragmentThree = (EnemySubFragment3) EnemyFragment.getEnemyPagerAdapter().getFragment(2);

                        if (subFragmentOne != null) {
                            TextView armorText1 = subFragmentOne.requireView().findViewById(R.id.armor_stats_text_view);
                            if (armorText1 != null) {
                                armorText1.setText("");
                            }
                        }
                        if (subFragmentThree != null) {
                            TextView armorText3 = subFragmentThree.requireView().findViewById(R.id.armor_stats_text_view);
                            if (armorText3 != null) {
                                armorText3.setText("");
                            }
                        }

                        // Update the armor_stats_text_view TextView with the armorText
                        TextView armorStatsTextView = getActivity().findViewById(R.id.armor_stats_text_view);
                        armorStatsTextView.setText(result);
                    })
                    .setNegativeButton("No", null)
                    .show();
        });


        craftWeaponButton.setOnClickListener(v -> {
            // Get the current enemy
            ClassDetailsActivity activity = (ClassDetailsActivity) getActivity();
            int selectedZone = activity.getSelectedZone();
            Enemy enemy = EnemyUtil.getEnemyByZoneAndIndex(selectedZone, enemySpinner.getSelectedItemPosition());
            String itemName = currentModifier.getName() + " " + enemy.getName() + " Weapon";

            if (Objects.equals(sharedViewModel.getCurrentCraftedWeaponName().getValue(), itemName)) {
                Toast.makeText(getActivity(), "You already have this weapon crafted.", Toast.LENGTH_SHORT).show();
                return;
            } else if (enemy.getCraftWeaponStats().length == 0) {
                Toast.makeText(getActivity(), "Crafting a weapon from this enemy is not possible.", Toast.LENGTH_SHORT).show();
                return;
            }

            int tokenCost = selectedZone + 2;


            new AlertDialog.Builder(getContext())
                    .setTitle("Craft Weapon")
                    .setMessage("Do you want to craft and equip " + currentModifier.getName() + " " + enemy.getName() + " Weapon? It will cost you " + tokenCost + " Monster Tokens. If you already have a weapon, you will lose it.")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        String result = sharedViewModel.craftWeapon(enemy, currentModifier);
                        // Clear weapon text in other SubFragments
                        EnemySubFragment1 subFragmentOne = (EnemySubFragment1) EnemyFragment.getEnemyPagerAdapter().getFragment(0);
                        EnemySubFragment3 subFragmentThree = (EnemySubFragment3) EnemyFragment.getEnemyPagerAdapter().getFragment(2);

                        if (subFragmentOne != null) {
                            TextView weaponText1 = subFragmentOne.requireView().findViewById(R.id.weapon_stats_text_view);
                            if (weaponText1 != null) {
                                weaponText1.setText("");
                            }
                        }
                        if (subFragmentThree != null) {
                            TextView weaponText3 = subFragmentThree.requireView().findViewById(R.id.weapon_stats_text_view);
                            if (weaponText3 != null) {
                                weaponText3.setText("");
                            }
                        }

                        // Update the weapon_stats_text_view TextView with the weaponText
                        TextView weaponStatsTextView = getActivity().findViewById(R.id.weapon_stats_text_view);
                        weaponStatsTextView.setText(result);
                    })
                    .setNegativeButton("No", null)
                    .show();
        });


        modifierSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int modifierPosition = modifierSpinner.getSelectedItemPosition();
                if (modifierPosition < Modifier.modifiers.length) {
                    currentModifier = Modifier.getModifierByIndex(modifierPosition);
                } else {
                    currentModifier = BossModifier.getBossModifierByIndex(modifierPosition - Modifier.modifiers.length);
                }

                resetCurrentHp(enemySpinner.getSelectedItemPosition());
                updateEnemyStatsTable(enemySpinner.getSelectedItemPosition());
                Integer selectedZone = sharedViewModel.getSelectedZone().getValue();
                if (selectedZone != null) {
                    sharedViewModel.setLastSelectedModifier(selectedZone, 2, position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        enemySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedEnemy = parent.getItemAtPosition(position).toString();
                defeatedButton.setEnabled(!selectedEnemy.equals("None"));
                resetCurrentHp(enemySpinner.getSelectedItemPosition());
                updateEnemyStatsTable(enemySpinner.getSelectedItemPosition());
                Integer selectedZone = sharedViewModel.getSelectedZone().getValue();
                if (selectedZone != null) {
                    sharedViewModel.setLastSelectedEnemy(selectedZone, 2, position);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return view;
    }

    private void resetCurrentHp(int enemyIndex) {
        ClassDetailsActivity activity = (ClassDetailsActivity) getActivity();
        assert activity != null;
        int selectedZone = activity.getSelectedZone();
        Enemy enemy = EnemyUtil.getEnemyByZoneAndIndex(selectedZone, enemyIndex);
        currentHP = enemy.getMaxHp() + currentModifier.getHp(selectedZone); // only set the baseHP
        hpTextView.setText(String.valueOf(currentHP));
    }

    private void updateEnemyList(int selectedZone) {
        List<Enemy> enemyList = new ArrayList<>();
        enemyList.addAll(Arrays.asList(EnemyUtil.enemies[selectedZone]));
        enemyList.addAll(Arrays.asList(EnemyUtil.bosses[selectedZone]));
        ArrayAdapter<Enemy> enemyAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                enemyList
        );
        if (enemySpinner != null) {
            enemySpinner.setAdapter(enemyAdapter);
            LiveData<Integer> lastSelectedEnemyLiveData = sharedViewModel.getLastSelectedEnemy(selectedZone,2);
            if (lastSelectedEnemyLiveData != null) {
                Integer lastSelectedEnemy = lastSelectedEnemyLiveData.getValue();
                if (lastSelectedEnemy != null) {
                    enemySpinner.setSelection(lastSelectedEnemy);
                }
            }
        }
    }


    private void updateModifierList(int selectedZone) {
        // Fetch the names of both regular and boss modifiers
        List<String> modifierNames = new ArrayList<>();
        modifierNames.addAll(Modifier.getModifierNames());
        modifierNames.addAll(BossModifier.getBossModifierNames());

        ArrayAdapter<String> modifierAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, modifierNames);

        // Check if the spinner is not null before setting the adapter
        if (modifierSpinner != null) {
            modifierSpinner.setAdapter(modifierAdapter);

            LiveData<Integer> lastSelectedModifierLiveData = sharedViewModel.getLastSelectedEnemy(selectedZone, 2);
            if (lastSelectedModifierLiveData != null) {
                Integer lastSelectedModifier = lastSelectedModifierLiveData.getValue();
                if (lastSelectedModifier != null) {
                    modifierSpinner.setSelection(lastSelectedModifier);
                }
            }
        }
    }


    private void updateEnemyStatsTable(int enemyIndex) {
        int weaponAndArmorColor = ContextCompat.getColor(getActivity(), R.color.purple_200);

        Integer selectedZone = sharedViewModel.getSelectedZone().getValue();
        if (selectedZone != null) {
            Enemy enemy = EnemyUtil.getEnemyByZoneAndIndex(selectedZone, enemyIndex);

            currentHP = enemy.getHp() + currentModifier.hp;

            TableLayout enemyStatsTable = requireView().findViewById(R.id.enemyStatsTable);
            enemyStatsTable.removeAllViews();

            // Set the border color based on selectedZone
            int borderColorId;
            switch (selectedZone) {
                case 1:
                    borderColorId = R.drawable.table_border_zone2;
                    break;
                case 2:
                    borderColorId = R.drawable.table_border_zone3;
                    break;
                case 3:
                    borderColorId = R.drawable.table_border_zone4;
                    break;
                default:
                    borderColorId = R.drawable.table_border_zone1;
                    break;
            }

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

                boolean isArmorStat = false;
                boolean isWeaponStat = false;

                TableRow row = new TableRow(getContext());
                row.setBackgroundColor(i % 2 == 0 ? getResources().getColor(R.color.tableRowColor1) : getResources().getColor(R.color.tableRowColor2));

                TextView statName = new TextView(getContext());
                statName.setText(statNames[i]);
                statName.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                statName.setTextColor(Color.WHITE);

                TextView statValue = new TextView(getContext());
                statValue.setText(String.valueOf(enemyStats[i]));
                statValue.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                statValue.setTextColor(Color.WHITE);

                // Check if this stat is used for crafting armor or weapon and change the color accordingly
                int weaponButtonColor = ContextCompat.getColor(getActivity(), R.color.red);
                int armorButtonColor = ContextCompat.getColor(getActivity(), R.color.lightBlue);

                for (int armorStat : enemy.getCraftArmorStats()) {
                    if (armorStat == i) {
                        isArmorStat = true;
                        break;
                    }
                }
                for (int weaponStat : enemy.getCraftWeaponStats()) {
                    if (weaponStat == i) {
                        isWeaponStat = true;
                        break;
                    }
                }

                // If stat is used in both weapon and armor crafting
                if (isArmorStat && isWeaponStat) {
                    statName.setTextColor(weaponAndArmorColor); // Purple for both weapon and armor
                    statName.setTypeface(null, Typeface.BOLD);
                    statValue.setTextColor(weaponAndArmorColor); // Purple for both weapon and armor
                    statValue.setTypeface(null, Typeface.BOLD);
                }
                // If stat is used only in armor crafting
                else if (isArmorStat) {
                    statName.setTextColor(armorButtonColor); // Light blue for armor
                    statName.setTypeface(null, Typeface.BOLD);
                    statValue.setTextColor(armorButtonColor); // Light blue for armor
                    statValue.setTypeface(null, Typeface.BOLD);
                }
                // If stat is used only in weapon crafting
                else if (isWeaponStat) {
                    statName.setTextColor(weaponButtonColor); // Red for weapon
                    statName.setTypeface(null, Typeface.BOLD);
                    statValue.setTextColor(weaponButtonColor); // Red for weapon
                    statValue.setTypeface(null, Typeface.BOLD);
                }

                row.addView(statName);
                row.addView(statValue);

                enemyStatsTable.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            }
        }
    }
}