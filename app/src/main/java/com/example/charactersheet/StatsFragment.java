package com.example.charactersheet;

import android.app.Activity;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class StatsFragment extends Fragment implements LifecycleOwner {

    private SharedViewModel sharedViewModel;

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

    private final HashMap<String, AtomicInteger> stats = new HashMap<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
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
    private void updateMaxHPButtonState(int essencePoints) {
        Button maxHPPlusButton = requireView().findViewById(R.id.EssenceMaxHPPlus);
        boolean isEnabled = essencePoints >= 2;
        maxHPPlusButton.setEnabled(isEnabled);
        int color = isEnabled ? getResources().getColor(android.R.color.holo_blue_light) : getResources().getColor(android.R.color.darker_gray);
        maxHPPlusButton.setBackgroundTintList(ColorStateList.valueOf(color));
    }
    private void updateMightButtonState(int essencePoints) {
        Button mightPlusButton = requireView().findViewById(R.id.EssenceMightPlus);
        boolean isEnabled = essencePoints >= 2;
        mightPlusButton.setEnabled(isEnabled);
        int color = isEnabled ? getResources().getColor(android.R.color.holo_blue_light) : getResources().getColor(android.R.color.darker_gray);
        mightPlusButton.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    private void updateIntuitionButtonState(int essencePoints) {
        Button intuitionPlusButton = requireView().findViewById(R.id.EssenceIntuitionPlus);
        boolean isEnabled = essencePoints >= 2;
        intuitionPlusButton.setEnabled(isEnabled);
        int color = isEnabled ? getResources().getColor(android.R.color.holo_blue_light) : getResources().getColor(android.R.color.darker_gray);
        intuitionPlusButton.setBackgroundTintList(ColorStateList.valueOf(color));
    }
    private void updateAgilityButtonState(int essencePoints) {
        Button agilityPlusButton = requireView().findViewById(R.id.EssenceAgilityPlus);

        // Check if the Agility value is less than 15 and the essence points are sufficient.
        boolean isEnabled = getAgilityValue() < 15 && essencePoints >= 3;

        agilityPlusButton.setEnabled(isEnabled);
        int color = isEnabled ? getResources().getColor(android.R.color.holo_blue_light) : getResources().getColor(android.R.color.darker_gray);
        agilityPlusButton.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    private void updatePrecisionButtonState(int essencePoints) {
        Button precisionPlusButton = requireView().findViewById(R.id.EssencePrecisionPlus);

        // Check if the Precision value is less than 15 and the essence points are sufficient.
        boolean isEnabled = getPrecisionValue() < 15 && essencePoints >= 3;

        precisionPlusButton.setEnabled(isEnabled);
        int color = isEnabled ? getResources().getColor(android.R.color.holo_blue_light) : getResources().getColor(android.R.color.darker_gray);
        precisionPlusButton.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    private void updateTacticsButtonState(int essencePoints) {
        Button tacticsPlusButton = requireView().findViewById(R.id.EssenceTacticsPlus);

        // Check if the Tactics value is less than 6 and the essence points are sufficient.
        boolean isEnabled = getTacticsValue() < 6 && essencePoints >= 4;

        tacticsPlusButton.setEnabled(isEnabled);
        int color = isEnabled ? getResources().getColor(android.R.color.holo_blue_light) : getResources().getColor(android.R.color.darker_gray);
        tacticsPlusButton.setBackgroundTintList(ColorStateList.valueOf(color));
    }


    private void updateIntrospectionButtonState(int essencePoints) {
        Button introspectionPlusButton = requireView().findViewById(R.id.EssenceIntrospectionPlus);

        // Check if the Introspection value is less than 6 and the essence points are sufficient.
        boolean isEnabled = getIntrospectionValue() < 6 && essencePoints >= 4;

        introspectionPlusButton.setEnabled(isEnabled);
        int color = isEnabled ? getResources().getColor(android.R.color.holo_blue_light) : getResources().getColor(android.R.color.darker_gray);
        introspectionPlusButton.setBackgroundTintList(ColorStateList.valueOf(color));
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            String selectedClassName1 = args.getString("selectedClassName1");
            String selectedClassName2 = args.getString("selectedClassName2");
            if (selectedClassName1 != null && selectedClassName2 != null) {
                requireActivity().setTitle(selectedClassName1 + " / " + selectedClassName2);
            }
        }

        // Initialize sharedViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        SwitchMaterial enableStatsButtonsSwitch = requireActivity().findViewById(R.id.enable_stats_buttons_switch);
        List<Button> buttons = getButtons(view);
        enableStatsButtonsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> sharedViewModel.setButtonsEnabled(isChecked));

        sharedViewModel.areButtonsEnabled().observe(getViewLifecycleOwner(), enabled -> {
            for (Button button : buttons) {
                button.setEnabled(enabled);
            }
        });

        sharedViewModel.getStatUpdates().observe(getViewLifecycleOwner(), this::handleStatUpdate);



        initDefaultStats();

        Activity activity = getActivity();
        if (activity != null) {
            Intent intent = activity.getIntent();
            if (intent != null) {
                HashMap<String, Integer> selectedClassStats1 = (HashMap<String, Integer>) intent.getSerializableExtra("selectedClassStats1");
                HashMap<String, Integer> selectedClassStats2 = (HashMap<String, Integer>) intent.getSerializableExtra("selectedClassStats2");
                if (selectedClassStats1 != null && selectedClassStats2 != null) {
                    updateStatsWithSelectedClasses(selectedClassStats1, selectedClassStats2);
                }
            }
        }

        setupUI(view);

        sharedViewModel.getEssencePoints().observe(getViewLifecycleOwner(), this::updateMaxHPButtonState);
        Button essenceMaxHPPlusButton = view.findViewById(R.id.EssenceMaxHPPlus);
        essenceMaxHPPlusButton.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
                .setTitle("Level Up Max HP")
                .setMessage("Are you sure you want to level up your Max HP? It costs 2 Essence Points.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    sharedViewModel.updateStat("Max HP", 1);
                    sharedViewModel.updateStat("Current HP", 1);
                    sharedViewModel.updateEssencePoints(-2);
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show());

        sharedViewModel.getEssencePoints().observe(getViewLifecycleOwner(), this::updateMightButtonState);
        Button essenceMightPlusButton = view.findViewById(R.id.EssenceMightPlus);
        essenceMightPlusButton.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
                .setTitle("Level Up Might")
                .setMessage("Are you sure you want to level up your Might stat? It costs 2 Essence Points.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    sharedViewModel.updateStat("Might", 1);
                    sharedViewModel.updateEssencePoints(-2);
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show());

        sharedViewModel.getEssencePoints().observe(getViewLifecycleOwner(), this::updateIntuitionButtonState);
        Button essenceIntuitionPlusButton = view.findViewById(R.id.EssenceIntuitionPlus);
        essenceIntuitionPlusButton.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
                .setTitle("Level Up Intuition")
                .setMessage("Are you sure you want to level up your Intuition stat? It costs 2 Essence Points.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    sharedViewModel.updateStat("Intuition", 1);
                    sharedViewModel.updateEssencePoints(-2);
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show());
        // Observers and click listeners for Agility
        sharedViewModel.getEssencePoints().observe(getViewLifecycleOwner(), this::updateAgilityButtonState);
        Button essenceAgilityPlusButton = view.findViewById(R.id.EssenceAgilityPlus);
        essenceAgilityPlusButton.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
                .setTitle("Level Up Agility")
                .setMessage("Are you sure you want to level up your Agility stat? It costs 3 Essence Points.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    sharedViewModel.updateStat("Agility", 1);
                    sharedViewModel.updateEssencePoints(-3);
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show());

// Observers and click listeners for Precision
        sharedViewModel.getEssencePoints().observe(getViewLifecycleOwner(), this::updatePrecisionButtonState);
        Button essencePrecisionPlusButton = view.findViewById(R.id.EssencePrecisionPlus);
        essencePrecisionPlusButton.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
                .setTitle("Level Up Precision")
                .setMessage("Are you sure you want to level up your Precision stat? It costs 3 Essence Points.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    sharedViewModel.updateStat("Precision", 1);
                    sharedViewModel.updateEssencePoints(-3);
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show());

// Observers and click listeners for Tactics
        sharedViewModel.getEssencePoints().observe(getViewLifecycleOwner(), this::updateTacticsButtonState);
        Button essenceTacticsPlusButton = view.findViewById(R.id.EssenceTacticsPlus);
        essenceTacticsPlusButton.setOnClickListener(v -> {
            if (getTacticsValue() >= 6) {
                Toast.makeText(requireContext(), "Tactics stat is already maxed out.", Toast.LENGTH_SHORT).show();
            } else {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Level Up Tactics")
                        .setMessage("Are you sure you want to level up your Tactics stat? It costs 4 Essence Points.")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            sharedViewModel.updateStat("Tactics", 1);
                            sharedViewModel.updateEssencePoints(-4);
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });


// Observers and click listeners for Introspection
        sharedViewModel.getEssencePoints().observe(getViewLifecycleOwner(), this::updateIntrospectionButtonState);
        Button essenceIntrospectionPlusButton = view.findViewById(R.id.EssenceIntrospectionPlus);
        essenceIntrospectionPlusButton.setOnClickListener(v -> new AlertDialog.Builder(requireContext())
                .setTitle("Level Up Introspection")
                .setMessage("Are you sure you want to level up your Introspection stat? It costs 4 Essence Points.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    sharedViewModel.updateStat("Introspection", 1);
                    sharedViewModel.updateEssencePoints(-4);
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show());
    }

    private void handleStatUpdate(Map<String, Integer> statUpdates) {

        HashMap<String, Integer[]> resourceIds = getResourceIds();

        for (Map.Entry<String, Integer> statUpdate : statUpdates.entrySet()) {
            String stat = statUpdate.getKey();
            int valueToAdd = statUpdate.getValue();
            updateStat(stat, valueToAdd);

            // Update the UI accordingly
            Integer[] ids = resourceIds.get(stat);
            if (ids != null && ids.length >= 1) {
                TextView statValueTextView = requireView().findViewById(ids[0]);
                if (statValueTextView != null) {
                    statValueTextView.setText(String.valueOf(stats.get(stat)));
                }
            }
        }
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
        // Merge the two selected class stats into one
        HashMap<String, Integer> mergedClassStats = new HashMap<>(classStats1);
        for (Map.Entry<String, Integer> entry : classStats2.entrySet()) {
            String statKey = entry.getKey();
            int statValue = entry.getValue();
            mergedClassStats.merge(statKey, statValue, Integer::sum);
        }

        // Apply the merged class stats to the default stats
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
        AtomicInteger agilityValue = stats.get("Agility");
        return agilityValue != null ? agilityValue.get() : 0;
    }
    public int getPrecisionValue() {
        AtomicInteger precisionValue = stats.get("Precision");
        return precisionValue != null ? precisionValue.get() : 0;
    }
    public int getTacticsValue() {
        AtomicInteger tacticsValue = stats.get("Tactics");
        return tacticsValue != null ? tacticsValue.get() : 0;
    }

    public int getIntrospectionValue() {
        AtomicInteger introspectionValue = stats.get("Introspection");
        return introspectionValue != null ? introspectionValue.get() : 0;
    }

    private void setupUI(View view) {
        HashMap<String, Integer[]> resourceIds = getResourceIds();

        for (String stat : stats.keySet()) {
            // Get UI elements by ID
            Integer[] ids = resourceIds.get(stat);
            if (ids == null || ids.length != 3) {
                continue;
            }

            TextView statValueTextView = view.findViewById(ids[0]);
            Button plusButton = view.findViewById(ids[1]);
            Button minusButton = view.findViewById(ids[2]);


            // Set initial value for stat
            statValueTextView.setText(String.valueOf(stats.get(stat)));
            final int originalTextColor = statValueTextView.getTextColors().getDefaultColor();
            // Set button listeners
            plusButton.setOnClickListener(buttonView -> {
                int currentValue = Integer.parseInt(statValueTextView.getText().toString());
                int maxValue;

                if (stat.equals("Tactics") || stat.equals("Introspection")) {
                    maxValue = 6;
                } else if (stat.equals("Agility") || stat.equals("Precision")) {
                    maxValue = 15;
                } else {
                    maxValue = 100;
                }

                if (currentValue < maxValue) {
                    int newValue = Objects.requireNonNull(stats.get(stat)).incrementAndGet();
                    statValueTextView.setText(String.valueOf(newValue));
                }
            });


            minusButton.setOnClickListener(buttonView -> {
                int currentValue = Integer.parseInt(statValueTextView.getText().toString());
                if (currentValue > 0) {
                    int newValue = Objects.requireNonNull(stats.get(stat)).decrementAndGet();
                    statValueTextView.setText(String.valueOf(newValue));
                }
            });


            // Change text color if Agility or Precision goes past 15
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