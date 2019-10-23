package com.hyuchiha.Annihilation.Hooks;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;


public class VaultHooks {
    public static boolean vault = false;

    public static Permission permission;
    public static Chat chat;
    public static Economy economy;

    private static VaultHooks inst;

    public static VaultHooks instance() {
        if (vault) {
            if (inst == null)
                inst = new VaultHooks();
            return inst;
        }
        return null;
    }


    public static Permission getPermissionManager() {
        return permission;
    }


    public static Chat getChatManager() {
        return chat;
    }


    public static Economy getEconomyManager() {
        return economy;
    }


    public static String getGroup(String name) {
        if (!vault) return "";

        String prefix = getChatManager().getPlayerPrefix(Bukkit.getPlayer(name));
        String group = getPermissionManager().getPrimaryGroup(Bukkit.getPlayer(name));

        if (prefix == null || prefix.equals("")) {
            prefix = getChatManager().getGroupPrefix(Bukkit.getPlayer(name).getWorld(), group);
        }
        return ChatColor.translateAlternateColorCodes('&', prefix);
    }

    public boolean setupPermissions() {
        if (!vault) {
            return false;
        }

        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServicesManager().getRegistration(Permission.class);

        if (permissionProvider != null)
            permission = permissionProvider.getProvider();
        return (permission != null);
    }

    public boolean setupChat() {
        if (!vault) {
            return false;
        }
        RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServicesManager().getRegistration(Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }

        return (chat != null);
    }

    public boolean setupEconomy() {
        if (!vault) {
            return false;
        }
        RegisteredServiceProvider<Economy> service = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (service != null) {
            economy = service.getProvider();
        }

        return (economy != null);
    }
}
