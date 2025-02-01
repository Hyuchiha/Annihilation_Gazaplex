package com.hyuchiha.Annihilation.Messages;

import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Output.Output;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.stream.Collectors;

public class Translator {
  private static final Main plugin = Main.getInstance();
  private static final HashMap<String, String> messages = new HashMap<>();
  private static final HashMap<String, List<String>> listMessages = new HashMap<>();

  public static void InitMessages() {
    Output.log("Registering messages");
    ConfigurationSection section = plugin.getConfig("messages.yml");
    Map<String, Object> map = section.getValues(false);

    loadMessages("", section);
  }

  private static void loadMessages(String prefix, ConfigurationSection section) {
    for (String key: section.getKeys(false)) {
      String fullKey = prefix.isEmpty() ? key : prefix + "." + key;

      if (section.isConfigurationSection(key)) {
        loadMessages(fullKey, section.getConfigurationSection(key));
      } else if (section.isString(key)) {
        messages.put(fullKey, section.getString(key));
      } else if (section.isList(key)) {
        listMessages.put(fullKey, section.getStringList(key));
      }
    }
  }

  public static String getString(String id) {
    return ChatColor.stripColor(findMessageWithId(id));
  }

  public static String getColoredString(String id) {
    return ChatColor.translateAlternateColorCodes('&', findMessageWithId(id));
  }

  public static List<String> getMultiMessage(String id) {
    return listMessages.getOrDefault(id, Collections.emptyList())
            .stream()
            .map(line -> ChatColor.translateAlternateColorCodes('&', line))
            .collect(Collectors.toList());
  }

  public static String getPrefix() {
    return getColoredString("PREFIX") + " ";
  }


  private static String findMessageWithId(String id) {
    return messages.getOrDefault(id, id);
  }
}
