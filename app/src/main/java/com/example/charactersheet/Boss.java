package com.example.charactersheet;


public class Boss extends Enemy {
    public Boss(String name, int hp, int might, int intuition, int agility, int precision,
                int physicalResistance, int magicalResistance, int experience, int initiative,
                int[] array1, int[] array2) {
        super(name, hp, might, intuition, agility, precision, physicalResistance, magicalResistance,
                experience, initiative, array1, array2);
    }
}

