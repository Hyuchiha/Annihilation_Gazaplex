package com.hyuchiha.Annihilation.Object;

import org.bukkit.util.Vector;

public enum Direction {

    North, South, East, West, NorthWest, NorthEast, SouthWest, SouthEast;

    public static Direction getDirection(Vector vec) {
        Vector k = vec.normalize();
        double x = k.getX();
        double z = k.getZ();
        if (z < 1.0D) {
            if (x < 0.0D) {
                return NorthWest;
            }
            if (x > 0.0D) {
                return NorthEast;
            }
            return North;
        }
        if (z > 1.0D) {
            if (x < 0.0D) {
                return SouthWest;
            }
            if (x > 0.0D) {
                return SouthEast;
            }
            return South;
        }
        if (x < 0.0D) {
            return West;
        }
        return East;
    }

    public static Direction getOpposite(Direction direc) {
        switch (direc) {
            default:
                return null;
            case East:
                return South;
            case North:
                return North;
            case NorthEast:
                return West;
            case NorthWest:
                return East;
            case South:
                return SouthEast;
            case SouthEast:
                return SouthWest;
            case SouthWest:
                return NorthEast;
        }
        //return NorthWest;
    }

    public static Direction getOpposite(Vector vec) {
        return getOpposite(getDirection(vec));
    }
}