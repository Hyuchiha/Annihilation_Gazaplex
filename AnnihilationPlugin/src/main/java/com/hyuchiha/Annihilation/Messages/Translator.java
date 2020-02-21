package com.hyuchiha.Annihilation.Messages;

import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Output.Output;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Translator {
  private static final Main plugin = Main.getInstance();
  private static final HashMap<String, String> messages = new HashMap<>();
  private static final HashMap<String, List<String>> listMessages = new HashMap<>();

  public static void InitMessages() {
    Output.log("Registering messages");
    ConfigurationSection section = plugin.getConfig("messages.yml");
    Map<String, Object> map = section.getValues(false);

    for (String key: map.keySet()) {
      if (section.isConfigurationSection(key)) {
        for (String subKey: section.getConfigurationSection(key).getKeys(false)) {
          String concatKey = key + '.' + subKey;
          messages.put(concatKey, section.getString(concatKey));
        }
      }

      if (section.isString(key)) {
        messages.put(key, section.getString(key));
      }

      if (section.isList(key)) {
        listMessages.put(key, section.getStringList(key));
      }
    }
  }


  public static String getString(String id) {
    String ss = findMessageWithId(id);
    return ChatColor.stripColor(ss);
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
