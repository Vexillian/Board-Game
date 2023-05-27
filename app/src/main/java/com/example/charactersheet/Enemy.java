package com.example.charactersheet;

import androidx.annotation.NonNull;

public class Enemy {
    private final String name;
    private int hp;
    private final int might;
    private final int intuition;
    private final int agility;
    private final int precision;
    private final int physicalResistance;
    private final int magicalResistance;
    private final int experience;
    private final int initiative;
    private final int[] craftArmorStats;
    private final int[] craftWeaponStats;
    private double experienceMultiplier = 1.0;


    public Enemy(String name, int hp, int might, int intuition, int agility, int precision,
                 int physicalResistance, int magicalResistance, int experience, int initiative,
                 int[] craftArmorStats, int[] craftWeaponStats) {
        this.name = name;
        this.hp = hp;
        this.might = might;
        this.intuition = intuition;
        this.agility = agility;
        this.precision = precision;
        this.physicalResistance = physicalResistance;
        this.magicalResistance = magicalResistance;
        this.experience = experience;
        this.initiative = initiative;
        this.craftArmorStats = craftArmorStats;
        this.craftWeaponStats = craftWeaponStats;
    }
    @NonNull
    @Override
    public String toString() {
        return this.name;
    }

    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }

    public int getMight() {
        return might;
    }

    public int getIntuition() {
        return intuition;
    }

    public int getAgility() {
        return agility;
    }

    public int getPrecision() {
        return precision;
    }

    public int getPhysicalResistance() {
        return physicalResistance;
    }

    public int getMagicalResistance() {
        return magicalResistance;
    }

    public int getExperience() {
        return (int) (experience * experienceMultiplier);
    }

    // Method to set the experience multiplier.
    public void setExperienceMultiplier(double multiplier) {
        this.experienceMultiplier = multiplier;
    }

    public int getInitiative() {
        return initiative;
    }

    public int[] getCraftArmorStats() {
        return craftArmorStats;
    }

    public int[] getCraftWeaponStats() {
        return craftWeaponStats;
    }

    public int getMaxHp() {
        return hp;
    }

    public void setCurrentHP() {
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getStatByIndex(int index) {
        switch(index) {
            case 0: return hp;
            case 1: return might;
            case 2: return intuition;
            case 3: return agility;
            case 4: return precision;
            case 5: return physicalResistance;
            case 6: return magicalResistance;
            case 7: return experience;
            case 8: return initiative;
            default: return 0; // return a default value in case of an invalid index
        }
    }

}
