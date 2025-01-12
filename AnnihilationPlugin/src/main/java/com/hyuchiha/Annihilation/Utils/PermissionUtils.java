package com.hyuchiha.Annihilation.Utils;

import org.bukkit.entity.Player;

public class PermissionUtils {
    public static boolean hasPermission(Player player, String permission) {
        // Check if the player has the exact permission
        if (player.hasPermission(permission)) {
            return true;
        }

        // Split the permission into parts and check for parent permissions
        String[] parts = permission.split("\\.");
        StringBuilder parentPermission = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            parentPermission.append(parts[i]).append(".*");
            if (player.hasPermission(parentPermission.toString())) {
                return true;
            }
            parentPermission.setLength(parentPermission.length() - 2); // Remove .* for the next iteration
        }

        // Finally, check if the player has the global wildcard permission
        return player.hasPermission("*");
    }
}
