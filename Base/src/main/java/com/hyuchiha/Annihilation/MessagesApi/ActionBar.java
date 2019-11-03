package com.hyuchiha.Annihilation.MessagesApi;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.inventivetalent.reflection.minecraft.Minecraft;
import org.inventivetalent.reflection.resolver.*;
import org.inventivetalent.reflection.resolver.minecraft.NMSClassResolver;

/**
 * Utility class for sending action bar messages to players.
 *
 * @author Luca
 */
public class ActionBar {

  static ClassResolver classResolver = new ClassResolver();
  static NMSClassResolver nmsClassResolver = new NMSClassResolver();

  static Class<?> IChatBaseComponent = nmsClassResolver.resolveSilent("IChatBaseComponent");
  static Class<?> ChatSerializer = nmsClassResolver.resolveSilent("ChatSerializer", "IChatBaseComponent$ChatSerializer");
  static Class<?> PlayerConnection = nmsClassResolver.resolveSilent("PlayerConnection");
  static Class<?> EntityPlayer = nmsClassResolver.resolveSilent("EntityPlayer");
  static Class<?> PacketPlayOutChat = classResolver.resolveSilent("net.minecraft.server." + Minecraft.getVersion() + "PacketPlayOutChat");

  static ConstructorResolver PacketChatConstructorResolver = new ConstructorResolver(PacketPlayOutChat);

  static FieldResolver EntityPlayerFieldResolver = new FieldResolver(EntityPlayer);

  static MethodResolver ChatSerailizerMethodResolver = new MethodResolver(ChatSerializer);
  static MethodResolver PlayerConnectionMethodResolver = new MethodResolver(PlayerConnection);

  public static void send(Player player, String message) {
    sendMessage(player, "{\"text\": \"" + message + "\"}");
  }

  /**
   * Compatible with 1.8
   *
   * @param player
   * @param json
   */
  public static void sendMessage(Player player, String json) {
    if (player == null || json == null) {
      throw new IllegalArgumentException("null argument");
    }
    if (Minecraft.VERSION.olderThan(Minecraft.Version.v1_8_R1)) {
      return;
    }
    if (!json.startsWith("{") || !json.endsWith("}")) {
      throw new IllegalArgumentException("invalid json: " + json);
    }

    try {
      Object serialized = serialize(json);
      Object packetChat = PacketChatConstructorResolver.resolve(new Class[]{
          IChatBaseComponent,
          byte.class
      }).newInstance(serialized, (byte) 2);
      sendPacket(player, packetChat);
    } catch (Exception e) {
      throw new RuntimeException("Failed to send ActionBar " + json + " to " + player, e);
    }
  }

  /**
   * Sends an action bar message to all online players.
   *
   * @param message The message to send.
   */
  public static void sendToAll(String message) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      send(player, message);
    }
  }

  //Helper methods

  static Object serialize(String json) throws ReflectiveOperationException {
    return ChatSerailizerMethodResolver.resolve(new ResolverQuery("a", String.class)).invoke(null, json);
  }

  static void sendPacket(Player receiver, Object packet) throws ReflectiveOperationException {
    Object handle = Minecraft.getHandle(receiver);
    Object connection = EntityPlayerFieldResolver.resolve("playerConnection").get(handle);
    PlayerConnectionMethodResolver.resolve("sendPacket").invoke(connection, packet);
  }

}