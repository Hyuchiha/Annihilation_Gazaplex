package com.hyuchiha.Annihilation.Messages;

import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Output.Output;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Translator {
    private static final Main plugin = Main.getInstance();
    private static final HashMap<String, String> messages = new HashMap<>();
    private static final HashMap<String, List<String>> listMessages = new HashMap<>();

    public static void InitMessages() {
        Output.log("Registering messages");
        Configuration yml = plugin.getConfig("messages.yml");
        for (String s : yml.getKeys(false)) {
            if (yml.isList(s)) {
                Output.log("Message of type list");
                Output.log(s);
                listMessages.put(s, yml.getStringList(s));
                continue;
            }
            messages.put(s, yml.getString(s));
        }
    }


    public static String getString(String id) {
        return ChatColor.stripColor(findMessageWithId(id));
    }


    public static String getColoredString(String s) {
        String ss = findMessageWithId(s);
        return ChatColor.translateAlternateColorCodes('&', ss);
    }

    public static List<String> getMultiMessage(String id) {
        List<String> messages = new ArrayList<>();
        if (listMessages.containsKey(id)) {
            messages = listMessages.get(id);
        }

        return messages;
    }


    public static String getPrefix() {
        return getColoredString("PREFIX") + " ";
    }


    private static String findMessageWithId(String id) {
        if (messages.containsKey(id)) {
            return messages.get(id);
        }
        return id;
    }
}
