package com.example.charactersheet;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class StatsFragment extends Fragment implements LifecycleOwner {

    private SharedViewModel sharedViewModel;
    private final HashMap<String, AtomicInteger> stats = new HashMap<>();
    private HashMap<String, Integer[]> resourceIds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        SwitchMaterial enableStatsButtonsSwitch = view.findViewById(R.id.enable_stats_buttons_switch);
        if (enableStatsButtonsSwitch == null) {
            enableStatsButtonsSwitch = requireActivity().findViewById(R.id.enable_stats_buttons_switch);
        }
        if (enableStatsButtonsSwitch != null) {
            enableStatsButtonsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> sharedViewModel.setButtonsEnabled(isChecked));
        }

        List<Button> buttons = getButtons(view);
        sharedViewModel.areButtonsEnabled().observe(getViewLifecycleOwner(), enabled -> {
            for (Button button : buttons) {
                button.setEnabled(enabled);
            }
        });

        sharedViewModel.getStatUpdates().observe(getViewLifecycleOwner(), this::handleStatUpdate);

        initDefaultStats();

        Intent intent = requireActivity().getIntent();
        if (intent != null) {
            HashMap<String, Integer> selectedClassStats1 = (HashMap<String, Integer>) intent.getSerializableExtra("selectedClassStats1");
            HashMap<String, Integer> selectedClassStats2 = (HashMap<String, Integer>) intent.getSerializableExtra("selectedClassStats2");
            if (selectedClassStats1 != null && selectedClassStats2 != null) {
                sharedViewModel.setClassStats(selectedClassStats1, selectedClassStats2); // Pass class stats to ViewModel
                updateStatsWithSelectedClasses(selectedClassStats1, selectedClassStats2);
            }
        }

        setupUI(view);

        sharedViewModel.getEssencePoints().observe(getViewLifecycleOwner(), this::updateEssenceButtonsState);
    }

    private HashMap<String, Integer[]> getResourceIds() {
        HashMap<String, Integer[]> resourceIds = new HashMap<>();
        resourceIds.put("Max HP", new Integer[]{R.id.MaxHPValue, R.id.MaxHPPlus, R.id.MaxHPMinus});
        resourceIds.put("Current HP", new Integer[]{R.id.CurrentHPValue, R.id.CurrentHPPlus, R.id.CurrentHPMinus});
        resourceIds.put("Might", new Integer[]{R.id.MightValue, R.id.MightPlus, R.id.MightMinus});
        resourceIds.put("Intuition", new Integer[]{R.id.IntuitionValue, R.id.IntuitionPlus, R.id.IntuitionMinus});
        resourceIds.put("Agility", new Integer[]{R.id.AgilityValue, R.id.AgilityPlus, R.id.AgilityMinus});
        resourceIds.put("Precision", new Integer[]{R.id.PrecisionValue, R.id.PrecisionPlus, R.id.PrecisionMinus});
        resourceIds.put("Tactics", new Integer[]{R.id.TacticsValue, R.id.TacticsPlus, R.id.TacticsMinus});
        resourceIds.put("Introspection", new Integer[]{R.id.IntrospectionValue, R.id.IntrospectionPlus, R.id.IntrospectionMinus});
        resourceIds.put("Physical Resistance", new Integer[]{R.id.PhysicalResistanceValue, R.id.PhysicalResistancePlus, R.id.PhysicalResistanceMinus});
        resourceIds.put("Magical Resistance", new Integer[]{R.id.MagicalResistanceValue, R.id.MagicalResistancePlus, R.id.MagicalResistanceMinus});
        return resourceIds;
    }

    private List<Button> getButtons(View view) {
        return Arrays.asList(
                view.findViewById(R.id.MaxHPMinus),
                view.findViewById(R.id.MaxHPPlus),
                view.findViewById(R.id.MightMinus),
                view.findViewById(R.id.MightPlus),
                view.findViewById(R.id.IntuitionMinus),
                view.findViewById(R.id.IntuitionPlus),
                view.findViewById(R.id.AgilityMinus),
                view.findViewById(R.id.AgilityPlus),
                view.findViewById(R.id.PrecisionMinus),
                view.findViewById(R.id.PrecisionPlus),
                view.findViewById(R.id.TacticsMinus),
                view.findViewById(R.id.TacticsPlus),
                view.findViewById(R.id.IntrospectionMinus),
                view.findViewById(R.id.IntrospectionPlus),
                view.findViewById(R.id.PhysicalResistanceMinus),
                view.findViewById(R.id.PhysicalResistancePlus),
                view.findViewById(R.id.MagicalResistanceMinus),
                view.findViewById(R.id.MagicalResistancePlus)
        );
    }

    public void updateEssenceButtonsState(int essencePoints) {
        updateButtonState(R.id.EssenceMaxHPPlus, essencePoints >= 2, "Max HP", 2, "Current HP", 1, 100);
        updateButtonState(R.id.EssenceMightPlus, essencePoints >= 2, "Might", 2, null, 0, 100);
        updateButtonState(R.id.EssenceIntuitionPlus, essencePoints >= 2, "Intuition", 2, null, 0, 100);
        updateButtonState(R.id.EssenceAgilityPlus, essencePoints >= 3 && getAgilityValue() < 15, "Agility", 3, null, 0, 15);
        updateButtonState(R.id.EssencePrecisionPlus, essencePoints >= 3 && getPrecisionValue() < 15, "Precision", 3, null, 0, 15);
        updateButtonState(R.id.EssenceTacticsPlus, essencePoints >= 4 && getTacticsValue() < 6, "Tactics", 4, null, 0, 6);
        updateButtonState(R.id.EssenceIntrospectionPlus, essencePoints >= 4 && getIntrospectionValue() < 6, "Introspection", 4, null, 0, 6);
    }

    private void updateButtonState(int buttonId, boolean isEnabled, String stat, int cost, String secondaryStat, int secondaryValue, int maxStatValue) {
        Button button = requireView().findViewById(buttonId);
        isEnabled = isEnabled && (getStatValue(stat) < maxStatValue); // Ensure stat is not maxed out
        button.setEnabled(isEnabled);
        int color = isEnabled ? ContextCompat.getColor(requireContext(), android.R.color.holo_blue_light) : ContextCompat.getColor(requireContext(), android.R.color.darker_gray);
        button.setBackgroundTintList(ColorStateList.valueOf(color));

        if (isEnabled) {
            button.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
                    .setTitle("Level Up " + stat)
                    .setMessage("Are you sure you want to level up your " + stat + " stat? It costs " + cost + " Essence Points.")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        sharedViewModel.updateStat(stat, 1);
                        if (secondaryStat != null) {
                            sharedViewModel.updateStat(secondaryStat, secondaryValue);
                        }
                        sharedViewModel.updateEssencePoints(-cost);
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show());
        } else {
            button.setOnClickListener(null);
        }
    }

    private int getStatValue(String stat) {
        return sharedViewModel.getCurrentStatValue(stat);
    }

    private void handleStatUpdate(Map<String, Integer> statUpdates) {
        if (resourceIds == null) {
            resourceIds = getResourceIds();
        }

        for (Map.Entry<String, Integer> statUpdate : statUpdates.entrySet()) {
            String stat = statUpdate.getKey();
            int valueToAdd = statUpdate.getValue();

            Integer[] ids = resourceIds.get(stat);
            if (ids != null && ids.length >= 1) {
                TextView statValueTextView = requireView().findViewById(ids[0]);
                if (statValueTextView != null) {
                    int currentValue = Integer.parseInt(statValueTextView.getText().toString());
                    int newValue = currentValue + valueToAdd;
                    statValueTextView.setText(String.valueOf(newValue));
                }
            }
        }
        updateEssenceButtonsState(sharedViewModel.getEssencePoints().getValue());
    }

    private void initDefaultStats() {
        stats.put("Max HP", new AtomicInteger(5));
        stats.put("Current HP", new AtomicInteger(5));
        stats.put("Might", new AtomicInteger(5));
        stats.put("Intuition", new AtomicInteger(5));
        stats.put("Agility", new AtomicInteger(1));
        stats.put("Precision", new AtomicInteger(1));
        stats.put("Tactics", new AtomicInteger(3));
        stats.put("Introspection", new AtomicInteger(0));
        stats.put("Physical Resistance", new AtomicInteger(0));
        stats.put("Magical Resistance", new AtomicInteger(0));
    }

    private void updateStatsWithSelectedClasses(HashMap<String, Integer> classStats1, HashMap<String, Integer> classStats2) {
        HashMap<String, Integer> mergedClassStats = new HashMap<>(classStats1);
        for (Map.Entry<String, Integer> entry : classStats2.entrySet()) {
            String statKey = entry.getKey();
            int statValue = entry.getValue();
            mergedClassStats.merge(statKey, statValue, Integer::sum);
        }

        for (Map.Entry<String, Integer> entry : mergedClassStats.entrySet()) {
            String statKey = entry.getKey();
            int statValue = entry.getValue();
            updateStat(statKey, statValue);
        }
    }

    private void updateStat(String statKey, int valueToAdd) {
        AtomicInteger currentStatValue = stats.get(statKey);
        if (currentStatValue != null) {
            currentStatValue.addAndGet(valueToAdd);
        }
    }

    public int getAgilityValue() {
        return getStatValue("Agility");
    }

    public int getPrecisionValue() {
        return getStatValue("Precision");
    }

    public int getTacticsValue() {
        return getStatValue("Tactics");
    }

    public int getIntrospectionValue() {
        return getStatValue("Introspection");
    }

    private void setupUI(View view) {
        if (resourceIds == null) {
            resourceIds = getResourceIds();
        }

        for (String stat : stats.keySet()) {
            Integer[] ids = resourceIds.get(stat);
            if (ids == null || ids.length != 3) {
                continue;
            }

            TextView statValueTextView = view.findViewById(ids[0]);
            Button plusButton = view.findViewById(ids[1]);
            Button minusButton = view.findViewById(ids[2]);

            statValueTextView.setText(String.valueOf(getStatValue(stat)));
            final int originalTextColor = statValueTextView.getTextColors().getDefaultColor();

            plusButton.setOnClickListener(buttonView -> {
                int currentValue = Integer.parseInt(statValueTextView.getText().toString());
                int maxValue = sharedViewModel.getMaxStatValue(stat);

                if (currentValue < maxValue) {
                    sharedViewModel.updateStat(stat, 1); // Update SharedViewModel
                }
            });

            minusButton.setOnClickListener(buttonView -> {
                int currentValue = Integer.parseInt(statValueTextView.getText().toString());
                if (currentValue > 0) {
                    sharedViewModel.updateStat(stat, -1); // Update SharedViewModel
                }
            });

            if (stat.equals("Agility") || stat.equals("Precision")) {
                statValueTextView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        int value = Integer.parseInt(editable.toString());
                        if (value > 15) {
                            statValueTextView.setTextColor(getResources().getColor(android.R.color.holo_orange_light, requireContext().getTheme()));
                        } else {
                            statValueTextView.setTextColor(originalTextColor);
                        }
                    }
                });
            }
        }
    }
}
