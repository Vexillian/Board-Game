package com.example.charactersheet;

import android.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

public class BossModifier extends Modifier {

    public BossModifier(String name, int[][] values, DefeatStrategy defeatStrategy) {
        super(name, values, defeatStrategy);
    }

    public DefeatStrategy getDefeatStrategy() {
        return defeatStrategy;
    }


    static final BossModifier[] bossModifiers = {
            new BossModifier("Chaotic", new int[][]{
                    {30, 0, 0, 0, 0, 0, 0, 10, 0},
                    {60, 0, 0, 0, 0, 0, 0, 20, 0},
                    {90, 0, 0, 0, 0, 0, 0, 30, 0},
                    {120, 0, 0, 0, 0, 0, 0, 0, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> {

                sharedViewModel.updateStat("Current HP", 1, true);
                sharedViewModel.updateStat("Max HP", 1, true);
                sharedViewModel.updateStat("Might", 1, true);
                sharedViewModel.updateStat("Intuition", 1, true);
                sharedViewModel.updateStat("Agility", 1, true);
                sharedViewModel.updateStat("Precision", 1, true);

                new AlertDialog.Builder(context)
                        .setTitle("Chaotic Boss Defeated!")
                        .setMessage("+1 Max HP, Might, Intuition, Agility, and Precision! (This may break your limits.)")
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }),


            new BossModifier("Elusive", new int[][]{
                    {0, 0, 0, 2, 0, 0, 0, 4, 2},
                    {0, 0, 0, 4, 0, 0, 0, 8, 4},
                    {0, 0, 0, 6, 0, 0, 0, 12, 6},
                    {0, 0, 0, 8, 0, 0, 0, 0, 8}
            }, (sharedViewModel, context, selectedZone, enemy) -> new AlertDialog.Builder(context)
                    .setTitle("Elusive Boss Defeated!")
                    .setMessage("You may exchange a learned skill for an abandoned skill!")
                    .setPositiveButton(android.R.string.ok, null)
                    .show()),

            new BossModifier("Elite", new int[][]{
                    {10, 2, 2, 1, 1, 1, 1, 6, 1},
                    {20, 4, 4, 1, 1, 1, 1, 12, 1},
                    {30, 6, 6, 1, 1, 2, 2, 18, 1},
                    {40, 8, 8, 2, 2, 2, 2, 0, 2}
            }, (sharedViewModel, context, selectedZone, enemy) -> {

                int equipmentCards = selectedZone +2;

                sharedViewModel.updateEssencePoints(3);

                new AlertDialog.Builder(context)
                        .setTitle("Elite Boss Defeated!")
                        .setMessage("Party leader may draw " + equipmentCards + " Equipment Cards. +3 Essence Points!")
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }),

            new BossModifier("Essence Infused", new int[][]{
                    {0, 2, 2, 0, 0, 1, 1, 8, 0},
                    {0, 4, 4, 0, 0, 1, 1, 16, 0},
                    {0, 6, 6, 0, 0, 1, 1, 24, 0},
                    {0, 8, 8, 0, 0, 2, 2, 0, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> new AlertDialog.Builder(context)
                    .setTitle("Essence Infused Boss Defeated!")
                    .setMessage("You may exchange a Basic Attack for an abandoned skill!")
                    .setPositiveButton(android.R.string.ok, null)
                    .show()),

            new BossModifier("Explosive", new int[][]{
                    {0, 0, 0, 0, 0, 0, 0, 6, 0},
                    {0, 0, 0, 0, 0, 0, 0, 12, 0},
                    {0, 0, 0, 0, 0, 0, 0, 18, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> {

                sharedViewModel.updateEssencePoints(3);

                new AlertDialog.Builder(context)
                        .setTitle("Explosive Boss Defeated!")
                        .setMessage("+3 Essence Points!")
                        .setPositiveButton(android.R.string.ok, null)
                        .show();

            }),

            new BossModifier("Gravatic", new int[][]{
                    {0, 0, 0, 0, 0, 0, 0, 4, 0},
                    {0, 0, 0, 0, 0, 0, 0, 8, 0},
                    {0, 0, 0, 0, 0, 0, 0, 16, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> {
                sharedViewModel.updateStat("Tactics", 1, true);
                sharedViewModel.updateStat("Introspection", 1, true);

                new AlertDialog.Builder(context)
                        .setTitle("Gravatic Boss Defeated!")
                        .setMessage("+1 to Tactics and Introspection! (This may break your limits.)")
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }),

            new BossModifier("Hyper Focused", new int[][]{
                    {0, 0, 0, 0, 0, 0, 0, 4, 0},
                    {0, 0, 0, 0, 0, 0, 0, 8, 0},
                    {0, 0, 0, 0, 0, 0, 0, 12, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> {

                sharedViewModel.updateEssencePoints(3);

                new AlertDialog.Builder(context)
                        .setTitle("Hyper Focused Boss Defeated!")
                        .setMessage("+3 Essence Points!")
                        .setPositiveButton(android.R.string.ok, null)
                        .show();

            }),

            new BossModifier("Insane", new int[][]{
                    {0, 0, 0, 0, 0, 0, 0, 5, 0},
                    {0, 0, 0, 0, 0, 0, 0, 10, 0},
                    {0, 0, 0, 0, 0, 0, 0, 15, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> new AlertDialog.Builder(context)
                    .setTitle("Insane Boss Defeated!")
                    .setMessage("Before leveling, look at the top two cards of your Skill Deck and add 1 to your hand. Then, put an abandoned skill in its place.")
                    .setPositiveButton(android.R.string.ok, null)
                    .show()),

            new BossModifier("Intimidating", new int[][]{
                    {0, 0, 0, 0, 0, 0, 0, 6, 0},
                    {0, 0, 0, 0, 0, 0, 0, 12, 0},
                    {0, 0, 0, 0, 0, 0, 0, 18, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> {

                sharedViewModel.updateStat("Physical Resistance",1);
                sharedViewModel.updateStat("Magical Resistance",1);


                new AlertDialog.Builder(context)
                        .setTitle("Intimidating Boss Defeated!")
                        .setMessage("+1 to Physical and Magical Resistance!")
                        .setPositiveButton(android.R.string.ok, null)
                        .show();

            }),

            new BossModifier("Lucky", new int[][]{
                    {0, 0, 0, 0, 0, 0, 0, 4, 0},
                    {0, 0, 0, 0, 0, 0, 0, 8, 0},
                    {0, 0, 0, 0, 0, 0, 0, 12, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> {

                sharedViewModel.updateEssencePoints(3);

                new AlertDialog.Builder(context)
                        .setTitle("Lucky Boss Defeated!")
                        .setMessage("You may freely assign Essence points on your next Level Up! +3 Essence Points!")
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }),

            new BossModifier("Mesmerizing", new int[][]{
                    {0, 0, 0, 0, 0, 0, 0, 6, 0},
                    {0, 0, 0, 0, 0, 0, 0, 12, 0},
                    {0, 0, 0, 0, 0, 0, 0, 18, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> {

                sharedViewModel.updateEssencePoints(3);

                new AlertDialog.Builder(context)
                        .setTitle("Mesmerizing Boss Defeated!")
                        .setMessage("+3 Essence Points!")
                        .setPositiveButton(android.R.string.ok, null)
                        .show();

            }),

            new BossModifier("Necrotic", new int[][]{
                    {0, 0, 0, 0, 0, 0, 0, 4, 0},
                    {0, 0, 0, 0, 0, 0, 0, 8, 0},
                    {0, 0, 0, 0, 0, 0, 0, 12, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> {

                sharedViewModel.updateStat("Max HP",3);

                new AlertDialog.Builder(context)
                        .setTitle("Necrotic Boss Defeated!")
                        .setMessage("+3 Max HP!")
                        .setPositiveButton(android.R.string.ok, null)
                        .show();

            }),

            new BossModifier("Reflective", new int[][]{
                    {0, 0, 0, 1, 0, 0, 0, 4, 1},
                    {0, 0, 0, 3, 0, 0, 0, 8, 3},
                    {0, 0, 0, 5, 0, 0, 0, 12, 5},
                    {0, 0, 0, 7, 0, 0, 0, 0, 7}
            }, (sharedViewModel, context, selectedZone, enemy) -> {

                sharedViewModel.updateEssencePoints(3);

                new AlertDialog.Builder(context)
                        .setTitle("Reflective Boss Defeated!")
                        .setMessage("+3 Essence Points!")
                        .setPositiveButton(android.R.string.ok, null)
                        .show();

            }),

            new BossModifier("Resilient", new int[][]{
                    {0, 0, 0, 0, 0, 0, 0, 6, 0},
                    {0, 0, 0, 0, 0, 0, 0, 12, 0},
                    {0, 0, 0, 0, 0, 0, 0, 18, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> {

                sharedViewModel.updateEssencePoints(3);

                new AlertDialog.Builder(context)
                        .setTitle("Resilient Boss Defeated!")
                        .setMessage("+3 Essence Points!")
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }),

            new BossModifier("Royal", new int[][]{
                    {0, 0, 0, 0, 0, 0, 0, 5, 0},
                    {0, 0, 0, 0, 0, 0, 0, 10, 0},
                    {0, 0, 0, 0, 0, 0, 0, 15, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0, 0}
            }, (sharedViewModel, context, selectedZone, enemy) -> new AlertDialog.Builder(context)
                    .setTitle("Royal Boss Defeated!")
                    .setMessage("Party Leader may draw 4 Equipment and 4 Item cards!")
                    .setPositiveButton(android.R.string.ok, null)
                    .show()),
    };

    public static BossModifier getBossModifierByIndex(int index) {
        if (index >= 0 && index < bossModifiers.length) {
            return bossModifiers[index];
        }
        return null;
    }

    public static List<String> getBossModifierNames() {
        List<String> names = new ArrayList<>();
        for (BossModifier bossModifier : bossModifiers) {
            names.add(bossModifier.getName());
        }
        return names;
    }
}
