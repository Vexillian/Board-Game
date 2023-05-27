package com.example.charactersheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;


public class CharacterLevelFragment extends Fragment implements LifecycleOwner {

    private SharedViewModel sharedViewModel;

    private TextView essencePointsValueTextView;
    private int characterLevel = 1; // initial level value
    private int highestLevel = 1; // initial highest level value
    private int essencePoints = 0; // initial essence points value
    private int exp = 0; // initial exp value


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_character_level, container, false);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        TextView levelValueTextView = view.findViewById(R.id.character_level_value_text_view);
        levelValueTextView.setText(String.valueOf(characterLevel));

        essencePointsValueTextView = view.findViewById(R.id.essence_points_value_text_view);
        essencePointsValueTextView.setText(String.valueOf(essencePoints));

        TextView expValueTextView = view.findViewById(R.id.exp_value_text_view);
        expValueTextView.setText(String.valueOf(exp));

        sharedViewModel.getEssencePoints().observe(getViewLifecycleOwner(), essencePointsValue -> {
            essencePointsValueTextView.setText(String.valueOf(essencePointsValue));
            essencePoints = essencePointsValue;
        });

        // Essence Points buttons
        Button essencePointsMinusButton = view.findViewById(R.id.essence_points_minus_button);
        essencePointsMinusButton.setOnClickListener(v -> {
            if (essencePoints > 0) {
                sharedViewModel.updateEssencePoints(-1);
            }
        });

        Button essencePointsPlusButton = view.findViewById(R.id.essence_points_plus_button);
        essencePointsPlusButton.setOnClickListener(v -> {
            sharedViewModel.updateEssencePoints(1); // Notify the sharedViewModel of the change
        });


        // Exp minus button
        Button expMinusButton = view.findViewById(R.id.exp_minus_button);
        expMinusButton.setOnClickListener(v -> {
            if (exp > 0) {
                exp--;
                expValueTextView.setText(String.valueOf(exp));

                int newLevel = calculateLevel(exp);
                if (newLevel != characterLevel) {
                    characterLevel = newLevel;
                    levelValueTextView.setText(String.valueOf(characterLevel));
                }
            }
        });

        // Exp plus button
        Button expPlusButton = view.findViewById(R.id.exp_plus_button);
        expPlusButton.setOnClickListener(v -> {
            exp++;
            expValueTextView.setText(String.valueOf(exp));

            int newLevel = calculateLevel(exp);
            if (newLevel != characterLevel) {
                characterLevel = newLevel;
                levelValueTextView.setText(String.valueOf(characterLevel));
                if (characterLevel > highestLevel) {
                    highestLevel = characterLevel;
                    sharedViewModel.updateEssencePoints(5); // Notify the sharedViewModel of the change
                    showMilestoneDialog(); // Show the milestone dialog here
                }
            }
        });

        sharedViewModel.getExp().observe(getViewLifecycleOwner(), expValue -> {
            exp = expValue;
            expValueTextView.setText(String.valueOf(exp));

            int newLevel = calculateLevel(exp);
            if (newLevel != characterLevel) {
                characterLevel = newLevel;
                levelValueTextView.setText(String.valueOf(characterLevel));
                if (characterLevel > highestLevel) {
                    highestLevel = characterLevel;
                    sharedViewModel.updateEssencePoints(5); // Notify the sharedViewModel of the change
                    showMilestoneDialog(); // Show the milestone dialog here
                }
            }
        });


        return view;
    }

    private int calculateLevel(int exp) {
        int level = 0;
        int[] milestones = {0, 3, 8, 15, 24, 35, 48, 63, 80, 99, 120, 143, 168, 195, 224, 255, 288, 323, 360, 400};

        for (int i = 0; i < milestones.length; i++) {
            if (exp >= milestones[i]) {
                level = i + 1;
            } else {
                break;
            }
        }

        return level;
    }

    private void showMilestoneDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_milestone, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        Button button1 = dialogView.findViewById(R.id.button1);
        button1.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Your Max HP (and Current HP) has increased by 1!", Toast.LENGTH_SHORT).show();
            essencePoints -= 2;
            essencePointsValueTextView.setText(String.valueOf(essencePoints));
            sharedViewModel.updateEssencePoints(-2); // Update the Essence Points in SharedViewModel
            dialog.dismiss();

            // Update Max HP and Current HP stats in the shared ViewModel
            sharedViewModel.updateStat("Max HP", 1);
            sharedViewModel.updateStat("Current HP", 1);
        });

        Button button2 = dialogView.findViewById(R.id.button2);
        button2.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Your Might has increased by 1!", Toast.LENGTH_SHORT).show();
            essencePoints -= 2;
            essencePointsValueTextView.setText(String.valueOf(essencePoints));
            sharedViewModel.updateEssencePoints(-2); // Update the Essence Points in SharedViewModel
            dialog.dismiss();

            // Update Might in the shared ViewModel
            sharedViewModel.updateStat("Might", 1);
        });

        Button button3 = dialogView.findViewById(R.id.button3);
        button3.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Your Intuition has increased by 1!", Toast.LENGTH_SHORT).show();
            essencePoints -= 2;
            essencePointsValueTextView.setText(String.valueOf(essencePoints));
            sharedViewModel.updateEssencePoints(-2); // Update the Essence Points in SharedViewModel
            dialog.dismiss();
            // Update Intuition in the shared ViewModel
            sharedViewModel.updateStat("Intuition", 1);
        });
        Button button4 = dialogView.findViewById(R.id.button4);
        button4.setOnClickListener(v -> {
            int agilityValue = getStatsFragment().getAgilityValue();
            if (agilityValue < 15) {
                Toast.makeText(requireContext(), "Your Agility has increased by 1!", Toast.LENGTH_SHORT).show();
                essencePoints -= 3;
                essencePointsValueTextView.setText(String.valueOf(essencePoints));
                sharedViewModel.updateEssencePoints(-3);
                dialog.dismiss();

                // Update Agility in the shared ViewModel
                sharedViewModel.updateStat("Agility", 1);
            } else {
                Toast.makeText(requireContext(), "You may spend your Essence Points freely!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        Button button5 = dialogView.findViewById(R.id.button5);
        button5.setOnClickListener(v -> {
            int precisionValue = getStatsFragment().getPrecisionValue();
            if (precisionValue < 15) {
                Toast.makeText(requireContext(), "Your Precision has increased by 1!", Toast.LENGTH_SHORT).show();
                essencePoints -= 3;
                essencePointsValueTextView.setText(String.valueOf(essencePoints));
                sharedViewModel.updateEssencePoints(-3);
                dialog.dismiss();

                // Update Precision in the shared ViewModel
                sharedViewModel.updateStat("Precision", 1);
            } else {
                Toast.makeText(requireContext(), "You may spend your Essence Points freely!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });


        // Add onClickListener for button6
        Button button6 = dialogView.findViewById(R.id.button6);
        button6.setOnClickListener(v -> {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(requireContext());
            builder1.setTitle("Select an option:");

            StatsFragment statsFragment = getStatsFragment();
            int tacticsValue = statsFragment.getTacticsValue();
            int introspectionValue = statsFragment.getIntrospectionValue();

            List<CharSequence> options = new ArrayList<>();
            if (tacticsValue < 6) {
                options.add("Tactics");
            }
            if (introspectionValue < 6) {
                options.add("Introspection");
            }

            if (options.isEmpty()) {
                Toast.makeText(requireContext(), " You may spend your Essence Points freely!", Toast.LENGTH_LONG).show();
                dialog.dismiss();
                return;
            }

            CharSequence[] items = new CharSequence[options.size()];
            options.toArray(items);

            builder1.setItems(items, (dialogInterface, i) -> {
                String selectedOption = options.get(i).toString();
                Toast.makeText(requireContext(), "Your " + selectedOption + " has increased by 1!", Toast.LENGTH_SHORT).show();
                essencePoints -= 4;
                essencePointsValueTextView.setText(String.valueOf(essencePoints));
                sharedViewModel.updateEssencePoints(-4);
                dialog.dismiss();

                // Update the selected stat in the shared ViewModel
                sharedViewModel.updateStat(selectedOption, 1);
            });

            AlertDialog dialog1 = builder1.create();
            dialog1.show();
        });


        dialog.setCancelable(false); // Disables closing the dialog by touching outside or pressing the back button
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
    private StatsFragment getStatsFragment() {
        ViewPager2 viewPager = requireActivity().findViewById(R.id.view_pager);
        CustomPagerAdapter adapter = (CustomPagerAdapter) viewPager.getAdapter();
        assert adapter != null;
        return adapter.getStatsFragment();
    }



}

