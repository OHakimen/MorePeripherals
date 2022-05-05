package com.hakimen.peripherals.utils;

public class PlayerStatUtil {
    public static int getExpToLevelUp(int level) {
        if (level <= 15) {
            return 2 * level + 7;
        }
        if (level <= 30) {
            return 5 * level - 38;
        }
        return 9 * level - 158;
    }

    public static int getExpAtLevel(int level) {
        if (level <= 16) {
            return (int) (Math.pow(level, 2.0) + (double) (6 * level));
        }
        if (level <= 31) {
            return (int) (2.5 * Math.pow(level, 2.0) - 40.5 * (double) level + 360.0);
        }
        return (int) (4.5 * Math.pow(level, 2.0) - 162.5 * (double) level + 2220.0);
    }
}