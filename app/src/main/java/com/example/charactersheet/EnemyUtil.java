package com.example.charactersheet;

public class EnemyUtil {

    public static final Enemy[][] enemies = {
            {
                    new Enemy("None", 0, 0, 0, 0, 0, 0, 0, 0, 0, new int[]{}, new int[]{}),
                    new Enemy("Arachnid", 8, 7, 7, 3, 2, 0, 0, 2, 3, new int[]{0}, new int[]{2}),
                    new Enemy("Bandit", 8, 6, 2, 1, 3, 0, 0, 2, 1, new int[]{}, new int[]{}),
                    new Enemy("Corrupted Imp", 9, 4, 7, 3, 3, 0, 0, 1, 3, new int[]{0}, new int[]{2}),
                    new Enemy("Flock of Ravens", 12, 1, 1, 4, 2, 0, 0, 2, 4, new int[]{3}, new int[]{4}),
                    new Enemy("Man Eating Plant", 9, 10, 3, 1, 1, 0, 0, 2, 1, new int[]{0}, new int[]{}),
                    new Enemy("Mud Golem", 16, 9, 4, 1, 1, 0, 0, 2, 1, new int[]{}, new int[]{1}),
                    new Enemy("Mystic Bedlamite", 9, 2, 5, 1, 2, 0, 0, 1, 1, new int[]{}, new int[]{}),
                    new Enemy("Scorpion", 9, 7, 5, 1, 2, 1, 0, 2, 1, new int[]{0}, new int[]{1}),
                    new Enemy("Undine", 10, 5, 12, 1, 2, 0, 1, 2, 1, new int[]{0}, new int[]{2}),
                    new Enemy("Vampire Bat", 8, 6, 7, 3, 2, 0, 0, 2, 3, new int[]{0}, new int[]{3}),
                    new Enemy("Wolf", 10, 8, 3, 3, 2, 0, 0, 2, 3, new int[]{0}, new int[]{4}),
                    new Enemy("Worm", 4, 5, 1, 1, 1, 0, 0, 1, 1, new int[]{0}, new int[]{}),
            },

            {
                    new Enemy("None", 0, 0, 0, 0, 0, 0, 0, 0, 0, new int[]{}, new int[]{}),
                    new Enemy("Arachnid", 14, 13, 11, 4, 3, 1, 1, 3, 4, new int[]{0}, new int[]{4}),
                    new Enemy("Bandit", 14, 8, 4, 2, 4, 1, 0, 4, 2, new int[]{}, new int[]{}),
                    new Enemy("Corrupted Imp", 15, 7, 13, 6, 6, 0, 2, 3, 6, new int[]{0}, new int[]{4}),
                    new Enemy("Flock of Ravens", 18, 2, 1, 8, 3, 0, 0, 4, 8, new int[]{3}, new int[]{4}),
                    new Enemy("Man Eating Plant", 14, 16, 4, 1, 1, 1, 0, 2, 1, new int[]{0}, new int[]{}),
                    new Enemy("Mud Golem", 22, 16, 6, 1, 1, 0, 0, 4, 1, new int[]{}, new int[]{1}),
                    new Enemy("Mystic Bedlamite", 14, 3, 8, 1, 3, 0, 0, 1, 1, new int[]{}, new int[]{}),
                    new Enemy("Scorpion", 14, 14, 7, 1, 3, 1, 0, 3, 1, new int[]{0}, new int[]{1}),
                    new Enemy("Undine", 15, 9, 19, 2, 3, 0, 3, 4, 2, new int[]{0}, new int[]{2}),
                    new Enemy("Vampire Bat", 13, 11, 15, 5, 3, 0, 1, 4, 5, new int[]{0}, new int[]{4}),
                    new Enemy("Wolf", 16, 12, 4, 4, 2, 1, 0, 4, 4, new int[]{0}, new int[]{1}),
                    new Enemy("Worm", 7, 7, 1, 1, 1, 0, 0, 1, 1, new int[]{0}, new int[]{}),

            },
            {
                    new Enemy("None", 0, 0, 0, 0, 0, 0, 0, 0, 0, new int[]{}, new int[]{}),
                    new Enemy("Arachnid", 17, 18, 16, 5, 4, 1, 2, 6, 5, new int[]{0,6}, new int[]{2,4}),
                    new Enemy("Bandit", 20, 11, 5, 4, 5, 1, 0, 5, 4, new int[]{}, new int[]{}),
                    new Enemy("Corrupted Imp", 19, 10, 19, 7, 9, 0, 3, 6, 7, new int[]{0,3}, new int[]{2}),
                    new Enemy("Flock of Ravens", 24, 3, 1, 10, 5, 0, 0, 5, 10, new int[]{3}, new int[]{4}),
                    new Enemy("Man Eating Plant", 17, 22, 5, 1, 1, 2, 0, 6, 1, new int[]{0,5}, new int[]{5}),
                    new Enemy("Mud Golem", 29, 21, 9, 1, 3, 0, 0, 6, 1, new int[]{}, new int[]{1,2}),
                    new Enemy("Mystic Bedlamite", 20, 4, 13, 2, 4, 0, 0, 6, 2, new int[]{}, new int[]{}),
                    new Enemy("Scorpion", 19, 21, 17, 2, 5, 2, 1, 6, 2, new int[]{0,1}, new int[]{2,3}),
                    new Enemy("Undine", 18, 13, 26, 4, 5, 0, 5, 5, 4, new int[]{0,6}, new int[]{2,3}),
                    new Enemy("Vampire Bat", 17, 14, 19, 7, 4, 0, 2, 5, 7, new int[]{0,2}, new int[]{2}),
                    new Enemy("Wolf", 20, 16, 5, 5, 3, 1, 1, 6, 5, new int[]{0,4}, new int[]{1,4}),
                    new Enemy("Worm", 10, 11, 1, 2, 1, 0, 0, 2, 2, new int[]{0}, new int[]{}),


            },
            {
                    new Enemy("None", 0, 0, 0, 0, 0, 0, 0, 0, 0, new int[]{}, new int[]{}),
                    new Enemy("Arachnid", 23, 25, 21, 7, 8, 2, 3, 8, 7, new int[]{0,3}, new int[]{1,3}),
                    new Enemy("Bandit", 27, 13, 7, 9, 6, 2, 0, 8, 9, new int[]{}, new int[]{}),
                    new Enemy("Corrupted Imp", 25, 13, 24, 9, 12, 0, 4, 7, 9, new int[]{0,6}, new int[]{2}),
                    new Enemy("Flock of Ravens", 32, 4, 1, 12, 6, 0, 0, 8, 12, new int[]{3}, new int[]{4}),
                    new Enemy("Man Eating Plant", 24, 28, 6, 1, 1, 2, 0, 8, 1, new int[]{0,5}, new int[]{5}),
                    new Enemy("Mud Golem", 37, 28, 12, 2, 4, 0, 0, 8, 2, new int[]{}, new int[]{1,4}),
                    new Enemy("Mystic Bedlamite", 26, 4, 14, 3, 7, 0, 1, 7, 3, new int[]{}, new int[]{}),
                    new Enemy("Scorpion", 24, 24, 20, 2, 6, 3, 2, 7, 2, new int[]{0,5}, new int[]{1,2}),
                    new Enemy("Undine", 24, 16, 31, 5, 6, 1, 6, 7, 5, new int[]{0,4}, new int[]{1,3}),
                    new Enemy("Vampire Bat", 23, 19, 23, 9, 4, 0, 3, 7, 9, new int[]{0,4}, new int[]{1,3}),
                    new Enemy("Wolf", 27, 21, 7, 6, 4, 2, 1, 7, 6, new int[]{0,5}, new int[]{1,4}),
                    new Enemy("Worm", 13, 14, 1, 2, 1, 1, 1, 3, 2, new int[]{0}, new int[]{}),


            },

    };

    public static final Boss[][] bosses = {
            {
                    new Boss("Sun Beetle", 20, 11, 13, 2, 4, 2, 1, 5, 2, new int[]{}, new int[]{}),
                    new Boss("Berunda", 24, 13, 14, 5, 2, 0, 0, 5, 5, new int[]{}, new int[]{}),
                    new Boss("Giant Lavellan", 21, 9, 15, 5, 1, 0, 0, 5, 5, new int[]{}, new int[]{}),
                    new Boss("Leadened Grizzly", 22, 14, 6, 2, 2, 4, 0, 5, -8, new int[]{}, new int[]{})
            },

            {
                    new Boss("Sun Beetle", 27, 15, 17, 2, 5, 3, 2, 10, 2, new int[]{}, new int[]{}),
                    new Boss("Berunda", 32, 21, 18, 7, 3, 0, 0, 10, 7, new int[]{}, new int[]{}),
                    new Boss("Giant Lavellan", 29, 14, 24, 5, 1, 0, 0, 10, 5, new int[]{}, new int[]{}),
                    new Boss("Leadened Grizzly", 30, 20, 11, 3, 7, 7, 0, 10, -7, new int[]{}, new int[]{})
            },
            {
                    new Boss("Sun Beetle", 35, 19, 23, 3, 7, 4, 3, 15, 3, new int[]{}, new int[]{}),
                    new Boss("Berunda", 41, 29, 24, 9, 3, 0, 1, 15, 9, new int[]{}, new int[]{}),
                    new Boss("Giant Lavellan", 37, 18, 33, 6, 1, 0, 0, 15, 6, new int[]{}, new int[]{}),
                    new Boss("Leadened Grizzly", 38, 28, 15, 3, 3, 11, 1, 15, -7, new int[]{}, new int[]{})
            },
            {
                    new Boss("Sun Beetle", 70, 23, 27, 4, 9, 5, 4, 0, 4, new int[]{}, new int[]{}),
                    new Boss("Berunda", 90, 37, 28, 11, 4, 0, 2, 0, 11, new int[]{}, new int[]{}),
                    new Boss("Giant Lavellan", 61, 22, 42, 7, 1, 0, 0, 0, 7, new int[]{}, new int[]{}),
                    new Boss("Leadened Grizzly", 83, 35, 19, 4, 4, 15, 1, 0, -6, new int[]{}, new int[]{})
            },
    };

    public static Enemy getEnemyByZoneAndIndex(int zone, int index) {
        if (zone >= 0 && zone < enemies.length) {
            if (index >= 0 && index < enemies[zone].length) {
                return enemies[zone][index];
            } else if (index >= enemies[zone].length && index < enemies[zone].length + bosses[zone].length) {
                return bosses[zone][index - enemies[zone].length];
            } else {
                throw new IllegalArgumentException("Index out of range for enemies/bosses array");
            }
        } else {
            throw new IllegalArgumentException("Zone out of range for enemies/bosses array");
        }
    }


    public static String getStatNameByIndex(int index) {
        switch(index) {
            case 0: return "Max HP";
            case 1: return "Might";
            case 2: return "Intuition";
            case 3: return "Agility";
            case 4: return "Precision";
            case 5: return "Physical Resistance";
            case 6: return "Magical Resistance";
            case 7: return "Experience";
            case 8: return "Initiative";
            default: return ""; // return an empty string in case of an invalid index
        }
    }

    public static Enemy getEnemyByName(int zone, String name) {
        for (Enemy enemy : enemies[zone]) {
            if (enemy.getName().equals(name)) {
                return enemy;
            }
        }
        for (Boss boss : bosses[zone]) {
            if (boss.getName().equals(name)) {
                return boss;
            }
        }
        throw new IllegalArgumentException("Enemy or Boss with the name " + name + " not found in zone " + zone);
    }

    public static int getRatioByStatIndex(int index) {
        switch(index) {
            case 0: // HP
            case 1: // Might
            case 2: // Intuition
                return 5;
            case 3: // Agility
            case 4: // Precision
                return 2;
            case 5: // Physical Resistance
            case 6: // Magical Resistance
                return 1;
            default:
                throw new IllegalArgumentException("Invalid index for stat ratio");
        }
    }

}
