package com.example.charactersheet;

import android.app.AlertDialog;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

public class Modifier {
    String name;
    int[][] stats;

    int hp;
    protected DefeatStrategy defeatStrategy;

    public Modifier(String name, int[][] statsArray, DefeatStrategy defeatStrategy) {
        this.name = name;
        this.stats = statsArray;
        this.defeatStrategy = defeatStrategy;

    }

    public String getName() {
        return name;
    }

    public int getHp(int rowIndex) {
        return stats[rowIndex][0];
    }

    public int getMight(int rowIndex) {
        return stats[rowIndex][1];
    }

    public int getIntuition(int rowIndex) {
        return stats[rowIndex][2];
    }

    public int getAgility(int rowIndex) {
        return stats[rowIndex][3];
    }

    public int getPrecision(int rowIndex) {
        return stats[rowIndex][4];
    }

    public int getPhysicalResistance(int rowIndex) {
        return stats[rowIndex][5];
    }

    public int getMagicalResistance(int rowIndex) {
        return stats[rowIndex][6];
    }

    public int getExperience(int rowIndex) {
        return stats[rowIndex][7];
    }

    public int getInitiative(int rowIndex) {
        return stats[rowIndex][8];
    }


    public static final Modifier[] modifiers = {
            new Modifier("None", new int[][]{
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> {
            }),

            new Modifier("Acidic", new int[][]{
                    {0, 0, 0, 0, 1, 0, 0, 1, 0},
                    {0, 0, 0, 0, 2, 0, 0, 1, 0},
                    {0, 0, 0, 0, 3, 0, 0, 2, 0},
                    {0, 0, 0, 0, 4, 0, 0, 2, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> new AlertDialog.Builder(context)
            .setTitle("Vanquisher rolls a D6")
            .setMessage("Was a 6 rolled?")
            .setPositiveButton("Yes!", (dialogInterface, i) -> new AlertDialog.Builder(context)
                    .setMessage("You may discard a card from your hand, and draw a new one.")
                    .setPositiveButton(android.R.string.ok, null)
                    .show())
            .setNegativeButton("No.", null)
            .show()),

            new Modifier("Agile", new int[][]{
                    {0, 0, 0, 3, 0, 0, 0, 1, 3},
                    {0, 0, 0, 4, 0, 0, 0, 1, 4},
                    {0, 0, 0, 5, 0, 0, 0, 2, 5},
                    {0, 0, 0, 7, 0, 0, 0, 2, 7}
            }, (sharedViewModel, context, selectedZone, enemy) -> new AlertDialog.Builder(context)
                    .setTitle("Vanquisher rolls a D6")
                    .setMessage("Was a 6 rolled?")
                    .setPositiveButton("Yes!", (dialogInterface, i) -> new AlertDialog.Builder(context)
                            .setMessage("You may increase your Agility by 1 for free!")
                            .setPositiveButton(android.R.string.ok, null)
                            .show())
                    .setNegativeButton("No.", null)
                    .show()),

            new Modifier("Aggressive", new int[][]{
                    {4, 3, 3, 0, 0, 0, 0, 2, 0},
                    {5, 4, 4, 0, 0, 0, 0, 2, 0},
                    {6, 5, 5, 0, 0, 0, 0, 3, 0},
                    {7, 6, 6, 0, 0, 0, 0, 4, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> new AlertDialog.Builder(context)
                    .setTitle("Vanquisher rolls a D6")
                    .setMessage("Was a 6 rolled?")
                    .setPositiveButton("Yes!", (dialogInterface, i) -> new AlertDialog.Builder(context)
                            .setMessage("You may increase your Tactics by 1 for free!")
                            .setPositiveButton(android.R.string.ok, null)
                            .show())
                    .setNegativeButton("No.", null)
                    .show()),

            new Modifier("Arctic", new int[][]{
                    {0, 0, 0, 0, 0, 0, 0, 1, 0},
                    {0, 0, 0, 0, 0, 0, 0, 1, 0},
                    {0, 0, 0, 0, 0, 0, 0, 2, 0},
                    {0, 0, 0, 0, 0, 0, 0, 2, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> {

                int tokenRoll6 = selectedZone + 1;

                new AlertDialog.Builder(context)
                        .setTitle("Vanquisher a D6")
                        .setMessage("Was a 6 rolled?")
                        .setPositiveButton("Yes!", (dialogInterface, i) -> {
                            // Increase the player's experience points
                            SharedViewModel sharedViewModel1 = new ViewModelProvider((FragmentActivity) context).get(SharedViewModel.class);
                            sharedViewModel1.addExp(tokenRoll6);
                            new AlertDialog.Builder(context)
                                    .setMessage("+ " + tokenRoll6 + " Exp!")
                                    .setPositiveButton(android.R.string.ok, null)
                                    .show();
                        })
                        .setNegativeButton("No", null)
                        .show();

            }),

            new Modifier("Bladed", new int[][]{
                    {0, 0, 0, 0, 0, 0, 0, 1, 0},
                    {0, 0, 0, 0, 0, 0, 0, 2, 0},
                    {0, 0, 0, 0, 0, 0, 0, 3, 0},
                    {0, 0, 0, 0, 0, 0, 0, 3, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> {

                int tokenRoll6 = selectedZone + 1;

                new AlertDialog.Builder(context)
                        .setTitle("Vanquisher a D6")
                        .setMessage("Was a 6 rolled?")
                        .setPositiveButton("Yes!", (dialogInterface, i) -> {
                            // Increase the player's experience points
                            SharedViewModel sharedViewModel1 = new ViewModelProvider((FragmentActivity) context).get(SharedViewModel.class);
                            sharedViewModel1.addExp(tokenRoll6);
                            new AlertDialog.Builder(context)
                                    .setMessage("+ " + tokenRoll6 + " Exp!")
                                    .setPositiveButton(android.R.string.ok, null)
                                    .show();
                        })
                        .setNegativeButton("No", null)
                        .show();
            }),

            new Modifier("Cunning", new int[][]{
                    {0, 0, 0, 2, 2, 0, 0, 2, 6},
                    {0, 0, 0, 3, 3, 0, 0, 2, 9},
                    {0, 0, 0, 4, 4, 0, 0, 3, 13},
                    {0, 0, 0, 5, 5, 0, 0, 4, 17}
            }, (sharedViewModel, context, selectedZone, enemy) -> new AlertDialog.Builder(context)
                    .setTitle("Vanquisher rolls a D6")
                    .setMessage("Was a 6 rolled?")
                    .setPositiveButton("Yes!", (dialogInterface, i) -> new AlertDialog.Builder(context)
                            .setMessage("You may increase your Introspection by 1 for free!")
                            .setPositiveButton(android.R.string.ok, null)
                            .show())
                    .setNegativeButton("No.", null)
                    .show()),

            new Modifier("Cowering", new int[][]{
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> {
                    enemy.setExperienceMultiplier(0.5);

                new AlertDialog.Builder(context)
                        .setTitle("This enemy offers no Tokens and half exp.")
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }),

            new Modifier("Earthen", new int[][]{
                    {2, 0, 0, 0, 0, 1, 0, 1, 0},
                    {3, 0, 0, 0, 0, 2, 0, 2, 0},
                    {4, 0, 0, 0, 0, 2, 0, 3, 0},
                    {6, 0, 0, 0, 0, 3, 0, 3, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> new AlertDialog.Builder(context)
                    .setTitle("Vanquisher rolls a D6")
                    .setMessage("Was a 6 rolled?")
                    .setPositiveButton("Yes!", (dialogInterface, i) -> new AlertDialog.Builder(context)
                            .setMessage("Party Leader may draw 1 Equipment card.")
                            .setPositiveButton(android.R.string.ok, null)
                            .show())
                    .setNegativeButton("No.", null)
                    .show()),

            new Modifier("Electrified", new int[][]{
                    {0, 0, 2, 0, 0, 0, 0, 1, 0},
                    {0, 0, 3, 0, 0, 0, 0, 1, 0},
                    {0, 0, 4, 0, 0, 0, 0, 2, 0},
                    {0, 0, 6, 0, 0, 0, 0, 2, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> {

                int tokenRoll6 = selectedZone + 1;

                new AlertDialog.Builder(context)
                        .setTitle("Vanquisher a D6")
                        .setMessage("Was a 6 rolled?")
                        .setPositiveButton("Yes!", (dialogInterface, i) -> {
                            // Increase the player's experience points
                            SharedViewModel sharedViewModel1 = new ViewModelProvider((FragmentActivity) context).get(SharedViewModel.class);
                            sharedViewModel1.addExp(tokenRoll6);
                            new AlertDialog.Builder(context)
                                    .setMessage("+ " + tokenRoll6 + " Exp!")
                                    .setPositiveButton(android.R.string.ok, null)
                                    .show();
                        })
                        .setNegativeButton("No", null)
                        .show();

            }),

            new Modifier("Fiery", new int[][]{
                    {0, 0, 0, 0, 0, 0, 0, 1, 0},
                    {0, 0, 0, 0, 0, 0, 0, 1, 0},
                    {0, 0, 0, 0, 0, 0, 0, 2, 0},
                    {0, 0, 0, 0, 0, 0, 0, 2, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> {

                int tokenRoll6 = selectedZone + 1;

                new AlertDialog.Builder(context)
                        .setTitle("Vanquisher a D6")
                        .setMessage("Was a 6 rolled?")
                        .setPositiveButton("Yes!", (dialogInterface, i) -> {
                            // Increase the player's experience points
                            SharedViewModel sharedViewModel1 = new ViewModelProvider((FragmentActivity) context).get(SharedViewModel.class);
                            sharedViewModel1.addExp(tokenRoll6);
                            new AlertDialog.Builder(context)
                                    .setMessage("+ " + tokenRoll6 + " Exp!")
                                    .setPositiveButton(android.R.string.ok, null)
                                    .show();
                        })
                        .setNegativeButton("No", null)
                        .show();

            }),

            new Modifier("Flaying", new int[][]{
                    {0, 0, 0, 0, 0, 0, 0, 1, 0},
                    {0, 0, 0, 0, 0, 0, 0, 1, 0},
                    {0, 0, 0, 0, 0, 0, 0, 2, 0},
                    {0, 0, 0, 0, 0, 0, 0, 2, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> new AlertDialog.Builder(context)
                    .setTitle("Vanquisher rolls a D6")
                    .setMessage("Was a 6 rolled?")
                    .setPositiveButton("Yes!", (dialogInterface, i) -> new AlertDialog.Builder(context)
                            .setMessage("You may discard a card from your hand, and draw a new one.")
                            .setPositiveButton(android.R.string.ok, null)
                            .show())
                    .setNegativeButton("No.", null)
                    .show()),

            new Modifier("Ghostly", new int[][]{
                    {0, 0, 0, 2, 0, 0, 0, 1, 2},
                    {0, 0, 0, 3, 0, 0, 0, 1, 3},
                    {0, 0, 0, 4, 0, 0, 0, 2, 4},
                    {0, 0, 0, 5, 0, 0, 0, 2, 5}
            }, (sharedViewModel, context, selectedZone, enemy) -> new AlertDialog.Builder(context)
                    .setTitle("Vanquisher rolls a D6")
                    .setMessage("Was a 6 rolled?")
                    .setPositiveButton("Yes!", (dialogInterface, i) -> {

                        new AlertDialog.Builder(context)
                                .setMessage("+1 Essence Point!")
                                .setPositiveButton(android.R.string.ok, null)
                                .show();
                        sharedViewModel.updateEssencePoints(1);

                    })
                    .setNegativeButton("No.", null)
                    .show()),

            new Modifier("Greedy", new int[][]{
                    {0, 0, 0, 0, 0, 0, 0, 1, 4},
                    {0, 0, 0, 0, 0, 0, 0, 1, 6},
                    {0, 0, 0, 0, 0, 0, 0, 2, 8},
                    {0, 0, 0, 0, 0, 0, 0, 2, 10}
            }, (sharedViewModel, context, selectedZone, enemy) -> new AlertDialog.Builder(context)
                    .setTitle("Vanquisher rolls a D6")
                    .setMessage("Was a 6 rolled?")
                    .setPositiveButton("Yes!", (dialogInterface, i) -> new AlertDialog.Builder(context)
                            .setMessage("Party Leader may draw 1 Equipment card.")
                            .setPositiveButton(android.R.string.ok, null)
                            .show())
                    .setNegativeButton("No.", null)
                    .show()),

            new Modifier("Hearty", new int[][]{
                    {6, 0, 0, 0, 0, 0, 0, 1, 0},
                    {12, 0, 0, 0, 0, 0, 0, 1, 0},
                    {18, 0, 0, 0, 0, 0, 0, 2, 0},
                    {24, 0, 0, 0, 0, 0, 0, 2, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> new AlertDialog.Builder(context)
                    .setTitle("Vanquisher rolls a D6")
                    .setMessage("Was a 6 rolled?")
                    .setPositiveButton("Yes!", (dialogInterface, i) -> {

                        new AlertDialog.Builder(context)
                                .setMessage("+1 Max HP!")
                                .setPositiveButton(android.R.string.ok, null)
                                .show();
                        sharedViewModel.updateStat("Max HP", 1);
                    })
                    .setNegativeButton("No.", null)
                    .show()),

            new Modifier("Impervious", new int[][]{
                    {0, 0, 0, 0, 0, 0, 0, 2, 0},
                    {0, 0, 0, 0, 0, 0, 0, 2, 0},
                    {0, 0, 0, 0, 0, 1, 1, 3, 0},
                    {0, 0, 0, 0, 0, 1, 1, 4, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> new AlertDialog.Builder(context)
                    .setTitle("Vanquisher rolls a D6")
                    .setMessage("Was a 6 rolled?")
                    .setPositiveButton("Yes!", (dialogInterface, i) -> {

                        new AlertDialog.Builder(context)
                                .setMessage("+1 Essence Point!")
                                .setPositiveButton(android.R.string.ok, null)
                                .show();
                        sharedViewModel.updateEssencePoints(1);

                    })
                    .setNegativeButton("No.", null)
                    .show()),

            new Modifier("Intelligent", new int[][]{
                    {0, 0, 4, 0, 0, 0, 0, 1, 0},
                    {0, 0, 8, 0, 0, 0, 0, 1, 0},
                    {0, 0, 12, 0, 0, 0, 0, 2, 0},
                    {0, 0, 16, 0, 0, 0, 0, 2, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> new AlertDialog.Builder(context)
                    .setTitle("Vanquisher rolls a D6")
                    .setMessage("Was a 6 rolled?")
                    .setPositiveButton("Yes!", (dialogInterface, i) -> {
                        // Increase the player's experience points
                        new AlertDialog.Builder(context)
                                .setMessage("+1 Intuition!")
                                .setPositiveButton(android.R.string.ok, null)
                                .show();
                        sharedViewModel.updateStat("Intuition", 1);
                    })
                    .setNegativeButton("No.", null)
                    .show()),

            new Modifier("Lively", new int[][]{
                    {0, 0, 0, 0, 0, 0, 0, 1, 0},
                    {0, 0, 0, 0, 0, 0, 0, 2, 0},
                    {0, 0, 0, 0, 0, 0, 0, 3, 0},
                    {0, 0, 0, 0, 0, 0, 0, 3, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> {

                int heal = selectedZone +2;

                new AlertDialog.Builder(context)
                        .setTitle("Vanquisher rolls a D6")
                        .setMessage("Was a 6 rolled?")
                        .setPositiveButton("Yes!", (dialogInterface, i) -> new AlertDialog.Builder(context)
                                .setMessage("You may heal yourself by " + heal + " Hp!")
                                .setPositiveButton(android.R.string.ok, null)
                                .show())
                        .setNegativeButton("No.", null)
                        .show();

            }),

            new Modifier("Noxious", new int[][]{
                    {2, 0, 0, 0, 0, 0, 0, 1, 0},
                    {3, 0, 0, 0, 0, 0, 0, 2, 0},
                    {4, 0, 0, 0, 0, 0, 0, 3, 0},
                    {6, 0, 0, 0, 0, 0, 0, 3, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> new AlertDialog.Builder(context)
                    .setTitle("Vanquisher rolls a D6")
                    .setMessage("Was a 6 rolled?")
                    .setPositiveButton("Yes!", (dialogInterface, i) -> new AlertDialog.Builder(context)
                            .setMessage("You may discard a card from your hand, and draw a new one.")
                            .setPositiveButton(android.R.string.ok, null)
                            .show())
                    .setNegativeButton("No.", null)
                    .show()),

            new Modifier("Percipient", new int[][]{
                    {0, 0, 0, 0, 0, 0, 0, 1, 7},
                    {0, 0, 0, 0, 0, 0, 0, 1, 9},
                    {0, 0, 0, 0, 0, 0, 0, 2, 12},
                    {0, 0, 0, 0, 0, 0, 0, 2, 16}
            }, (sharedViewModel, context, selectedZone, enemy) -> new AlertDialog.Builder(context)
                    .setTitle("Vanquisher rolls a D6")
                    .setMessage("Was a 6 rolled?")
                    .setPositiveButton("Yes!", (dialogInterface, i) -> new AlertDialog.Builder(context)
                            .setMessage("You may discard a card from your hand, and draw a new one.")
                            .setPositiveButton(android.R.string.ok, null)
                            .show())
                    .setNegativeButton("No.", null)
                    .show()),

            new Modifier("Perceptive", new int[][]{
                    {0, 0, 0, 0, 3, 0, 0, 1, 0},
                    {0, 0, 0, 0, 4, 0, 0, 1, 0},
                    {0, 0, 0, 0, 5, 0, 0, 2, 0},
                    {0, 0, 0, 0, 7, 0, 0, 2, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> new AlertDialog.Builder(context)
                    .setTitle("Vanquisher rolls a D6")
                    .setMessage("Was a 6 rolled?")
                    .setPositiveButton("Yes!", (dialogInterface, i) -> {
                        // Increase the player's experience points
                        new AlertDialog.Builder(context)

                                .setMessage("You may increase your Precision by 1 for free!")
                                .setPositiveButton(android.R.string.ok, null)
                                .show();
                    })
                    .setNegativeButton("No.", null)
                    .show()),

            new Modifier("Piercing", new int[][]{
                    {0, 0, 0, 0, 0, 0, 0, 1, 0},
                    {0, 0, 0, 0, 0, 0, 0, 1, 0},
                    {0, 0, 0, 0, 0, 0, 0, 2, 0},
                    {0, 0, 0, 0, 0, 0, 0, 2, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> new AlertDialog.Builder(context)
                    .setTitle("Vanquisher rolls a D6")
                    .setMessage("Was a 6 rolled?")
                    .setPositiveButton("Yes!", (dialogInterface, i) -> new AlertDialog.Builder(context)
                            .setMessage("You may discard a card from your hand, and draw a new one.")
                            .setPositiveButton(android.R.string.ok, null)
                            .show())
                    .setNegativeButton("No.", null)
                    .show()),

            new Modifier("Plump", new int[][]{
                    {4, 0, 0, 0, 0, 0, 0, 1, 0},
                    {8, 0, 0, 0, 0, 0, 0, 1, 0},
                    {12, 0, 0, 0, 0, 0, 0, 2, 0},
                    {16, 0, 0, 0, 0, 0, 0, 2, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> {

                int extraTokens;
                if(selectedZone < 3) {
                    extraTokens = 1;
                } else {
                    extraTokens = 2;
                }

                new AlertDialog.Builder(context)
                        .setTitle("Extra Tokens!")
                        .setMessage("You gain " + extraTokens + " extra token(s)")
                                .setPositiveButton(android.R.string.ok, null)
                        .show();

            }),

            new Modifier("Pustulating", new int[][]{
                    {-3, 0, 0, 0, 0, 0, 0, 1, 0},
                    {-6, 0, 0, 0, 0, 0, 0, 1, 0},
                    {-9, 0, 0, 0, 0, 0, 0, 2, 0},
                    {-12, 0, 0, 0, 0, 0, 0, 2, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> {

                int damage = selectedZone +1;

                new AlertDialog.Builder(context)
                        .setMessage("You take " + damage + " points of Physical Damage.")
                        .setPositiveButton(android.R.string.ok, null)
                        .show();

                new AlertDialog.Builder(context)
                        .setTitle("Vanquisher rolls a D6")
                        .setMessage("Was a 6 rolled?")
                        .setPositiveButton("Yes!", (dialogInterface, i) -> new AlertDialog.Builder(context)
                                .setMessage("Party Leader may draw 1 Equipment card.")
                                .setPositiveButton(android.R.string.ok, null)
                                .show())
                        .setNegativeButton("No.", null)
                        .show();
            }),


            new Modifier("Reinforced", new int[][]{
                    {0, 0, 0, 0, 0, 1, 0, 2, 0},
                    {0, 0, 0, 0, 0, 3, 0, 3, 0},
                    {0, 0, 0, 0, 0, 5, 0, 4, 0},
                    {0, 0, 0, 0, 0, 8, 0, 5, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> new AlertDialog.Builder(context)
                    .setTitle("Vanquisher rolls a D6")
                    .setMessage("Was a 6 rolled?")
                    .setPositiveButton("Yes!", (dialogInterface, i) -> {

                        new AlertDialog.Builder(context)
                                .setMessage("+1 Essence Point!")
                                .setPositiveButton(android.R.string.ok, null)
                                .show();
                        sharedViewModel.updateEssencePoints(1);

                    })
                    .setNegativeButton("No.", null)
                    .show()),

            new Modifier("Robust", new int[][]{
                    {0, 4, 0, 0, 0, 0, 0, 1, 0},
                    {0, 8, 0, 0, 0, 0, 0, 1, 0},
                    {0, 12, 0, 0, 0, 0, 0, 2, 0},
                    {0, 16, 0, 0, 0, 0, 0, 2, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> new AlertDialog.Builder(context)
                    .setTitle("Vanquisher rolls a D6")
                    .setMessage("Was a 6 rolled?")
                    .setPositiveButton("Yes!", (dialogInterface, i) -> {
                        // Increase the player's experience points
                        new AlertDialog.Builder(context)
                                .setMessage("+1 Might!")
                                .setPositiveButton(android.R.string.ok, null)
                                .show();
                        sharedViewModel.updateStat("Might", 1);
                    })
                    .setNegativeButton("No.", null)
                    .show()),

            new Modifier("Shielded", new int[][]{
                    {2, 0, 0, 0, 0, 0, 0, 1, 0},
                    {3, 0, 0, 0, 0, 0, 0, 2, 0},
                    {4, 0, 0, 0, 0, 0, 0, 3, 0},
                    {6, 0, 0, 0, 0, 0, 0, 3, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> new AlertDialog.Builder(context)
                    .setTitle("Vanquisher rolls a D6")
                    .setMessage("Was a 6 rolled?")
                    .setPositiveButton("Yes!", (dialogInterface, i) -> new AlertDialog.Builder(context)
                            .setMessage("Party Leader may draw 1 Equipment card.")
                            .setPositiveButton(android.R.string.ok, null)
                            .show())
                    .setNegativeButton("No.", null)
                    .show()),

            new Modifier("Shelled", new int[][]{
                    {2, 0, 0, 0, 0, 0, 0, 1, 0},
                    {3, 0, 0, 0, 0, 0, 0, 2, 0},
                    {4, 0, 0, 0, 0, 0, 0, 3, 0},
                    {6, 0, 0, 0, 0, 0, 0, 3, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> new AlertDialog.Builder(context)
                    .setTitle("Vanquisher rolls a D6")
                    .setMessage("Was a 6 rolled?")
                    .setPositiveButton("Yes!", (dialogInterface, i) -> new AlertDialog.Builder(context)
                            .setMessage("Party Leader may draw 1 Equipment card.")
                            .setPositiveButton(android.R.string.ok, null)
                            .show())
                    .setNegativeButton("No.", null)
                    .show()),

            new Modifier("Translucent", new int[][]{
                    {0, 0, 0, 0, 2, 0, 0, 1, 0},
                    {0, 0, 0, 0, 3, 0, 0, 1, 0},
                    {0, 0, 0, 0, 4, 0, 0, 2, 0},
                    {0, 0, 0, 0, 5, 0, 0, 2, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> new AlertDialog.Builder(context)
                    .setTitle("Vanquisher rolls a D6")
                    .setMessage("Was a 6 rolled?")
                    .setPositiveButton("Yes!", (dialogInterface, i) -> {

                        new AlertDialog.Builder(context)
                                .setMessage("+1 Essence Point!")
                                .setPositiveButton(android.R.string.ok, null)
                                .show();
                        sharedViewModel.updateEssencePoints(1);

                    })
                    .setNegativeButton("No.", null)
                    .show()),

            new Modifier("Tranquil", new int[][]{
                    {0, 0, 0, 0, 0, 0, 1, 2, 0},
                    {0, 0, 0, 0, 0, 0, 3, 3, 0},
                    {0, 0, 0, 0, 0, 0, 5, 4, 0},
                    {0, 0, 0, 0, 0, 0, 8, 5, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> new AlertDialog.Builder(context)
                    .setTitle("Vanquisher rolls a D6")
                    .setMessage("Was a 6 rolled?")
                    .setPositiveButton("Yes!", (dialogInterface, i) -> {

                        new AlertDialog.Builder(context)
                                .setMessage("+1 Essence Point!")
                                .setPositiveButton(android.R.string.ok, null)
                                .show();
                        sharedViewModel.updateEssencePoints(1);

                    })
                    .setNegativeButton("No.", null)
                    .show()),

            new Modifier("Vampiric", new int[][]{
                    {0, 0, 0, 0, 0, 0, 0, 1, 0},
                    {0, 0, 0, 0, 0, 0, 0, 2, 0},
                    {0, 0, 0, 0, 0, 0, 0, 3, 0},
                    {0, 0, 0, 0, 0, 0, 0, 3, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> {

                int heal = (selectedZone < 2) ? 1 : 2;

                new AlertDialog.Builder(context)
                        .setTitle("Vanquisher rolls a D6")
                        .setMessage("Was a 6 rolled?")
                        .setPositiveButton("Yes!", (dialogInterface, i) -> new AlertDialog.Builder(context)
                                .setMessage("You may heal yourself by " + heal + " Hp!")
                                .setPositiveButton(android.R.string.ok, null)
                                .show())
                        .setNegativeButton("No.", null)
                        .show();
            }),

            new Modifier("Venomous", new int[][]{
                    {0, 0, 3, 0, 0, 0, 0, 1, 0},
                    {0, 0, 4, 0, 0, 0, 0, 1, 0},
                    {0, 0, 5, 0, 0, 0, 0, 2, 0},
                    {0, 0, 6, 0, 0, 0, 0, 2, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> {

                int tokenRoll6 = selectedZone + 1;

                new AlertDialog.Builder(context)
                        .setTitle("Vanquisher a D6")
                        .setMessage("Was a 6 rolled?")
                        .setPositiveButton("Yes!", (dialogInterface, i) -> new AlertDialog.Builder(context)
                                .setMessage("+ " + tokenRoll6 + " Token(s)!")
                                .setPositiveButton(android.R.string.ok, null)
                                .show())
                        .setNegativeButton("No", null)
                        .show();

            }),

            new Modifier("Vigorous", new int[][]{
                    {3, 4, 0, 0, 0, 0, 0, 1, 0},
                    {6, 6, 0, 0, 0, 0, 0, 1, 0},
                    {8, 8, 0, 0, 0, 0, 0, 2, 0},
                    {10, 10, 0, 0, 0, 0, 0, 2, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> {

                int tokenRoll6 = selectedZone + 1;

                new AlertDialog.Builder(context)
                        .setTitle("Vanquisher a D6")
                        .setMessage("Was a 6 rolled?")
                        .setPositiveButton("Yes!", (dialogInterface, i) -> new AlertDialog.Builder(context)
                                .setMessage("+ " + tokenRoll6 + " Token(s)!")
                                .setPositiveButton(android.R.string.ok, null)
                                .show())
                        .setNegativeButton("No", null)
                        .show();

            }),

            new Modifier("Wind Walking", new int[][]{
                    {0, 0, 0, 1, 0, 0, 0, 1, 1},
                    {0, 0, 0, 2, 0, 0, 1, 2, 2},
                    {0, 0, 0, 3, 0, 0, 1, 3, 3},
                    {0, 0, 0, 5, 0, 0, 2, 3, 5}
            }, (sharedViewModel, context, selectedZone, enemy) -> new AlertDialog.Builder(context)
                    .setTitle("Vanquisher rolls a D6")
                    .setMessage("Was a 6 rolled?")
                    .setPositiveButton("Yes!", (dialogInterface, i) -> new AlertDialog.Builder(context)
                            .setMessage("You may discard a card from your hand, and draw a new one.")
                            .setPositiveButton(android.R.string.ok, null)
                            .show())
                    .setNegativeButton("No.", null)
                    .show())

    };

    public static Modifier getModifierByIndex(int index) {
        if (index >= 0 && index < modifiers.length) {
            return modifiers[index];
        }
        return null;
    }
    public int getStatByIndex(int rowIndex, int index) {
        switch (index) {
            case 0: return getHp(rowIndex);
            case 1: return getMight(rowIndex);
            case 2: return getIntuition(rowIndex);
            case 3: return getAgility(rowIndex);
            case 4: return getPrecision(rowIndex);
            case 5: return getPhysicalResistance(rowIndex);
            case 6: return getMagicalResistance(rowIndex);
            case 7: return getExperience(rowIndex);
            case 8: return getInitiative(rowIndex);
            default: return 0;
        }
    }

    public static List<String> getModifierNames() {
        List<String> names = new ArrayList<>();
        for (Modifier modifier : modifiers) {
            names.add(modifier.name);
        }
        return names;
    }

    public DefeatStrategy getDefeatStrategy() {
        return defeatStrategy;
    }

}
