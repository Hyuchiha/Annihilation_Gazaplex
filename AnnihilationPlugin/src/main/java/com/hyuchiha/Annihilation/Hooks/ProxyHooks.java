package com.hyuchiha.Annihilation.Hooks;

import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Messages.Translator;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Sends players to the proxy's lobby server. Every proxy decision in the plugin lives here.
 *
 * <ul>
 *   <li>{@link #sendToLobby} = manual path (the return-to-lobby item): needs {@code proxy.enabled}.</li>
 *   <li>{@link #autoSendToLobby} = automatic path (game end, ForceGameEnding time limit): needs
 *       {@code proxy.enabled} and {@code proxy.send-on-end}.</li>
 * </ul>
 *
 * <p>One code path for BungeeCord (and Waterfall) and Velocity: Velocity answers the same
 * {@code BungeeCord} plugin channel as long as {@code bungee-plugin-message-channel = true}
 * in {@code velocity.toml}, which is its default. On 1.13+ Bukkit maps the legacy channel
 * name to {@code bungeecord:main} by itself.
 *
 * <p>Config is read through ConfigManager on every send, not cached, so {@code /anni reload}
 * needs nothing extra.
 *
 * <p>Callers put the player in a safe state on this server first (lobby spawn, lobby
 * inventory): a lobby server that is down or misnamed just leaves them here.
 */
public final class ProxyHooks {

  public static final String CHANNEL = "BungeeCord";

  private ProxyHooks() {
  }

  /** Resolved proxy settings: new {@code proxy.*} keys, or the legacy keys on an upgraded server. */
  static final class Settings {
    final boolean enabled;
    final boolean sendOnEnd;
    final String server;
    final String item;

    Settings(boolean enabled, boolean sendOnEnd, String server, String item) {
      this.enabled = enabled;
      this.sendOnEnd = sendOnEnd;
      this.server = server;
      this.item = item;
    }
  }

  /**
   * {@code isSet}, not {@code contains}: contains() also sees the jar defaults, so it would
   * always say the admin adopted {@code proxy:}. New keys are read without an explicit default
   * so the jar value applies; an explicit default would bypass it.
   */
  static Settings resolve(Configuration config) {
    String item = config.isSet("proxy.item") ? config.getString("proxy.item")
        : config.isSet("Bungee.item") ? config.getString("Bungee.item")
        : config.getString("proxy.item", "RED_BED");
    if (config.isSet("proxy.enabled")) {
      return new Settings(config.getBoolean("proxy.enabled"), config.getBoolean("proxy.send-on-end"),
          config.getString("proxy.lobby-server"), item);
    }
    // Legacy (before proxy:): the old toggle only gave the item, so it never auto-sends.
    return new Settings(config.getBoolean("enableBungeeCommunication", false), false,
        config.getString("Bungee.server", "lobby"), item);
  }

  private static Settings settings(Main plugin) {
    Configuration config = plugin.getConfig("config.yml");
    return config == null ? new Settings(false, false, "lobby", "RED_BED") : resolve(config);
  }

  /** Registered unconditionally: harmless when unused, and survives a reload that enables it. */
  public static void register(Main plugin) {
    plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL);
  }

  /** Master switch: also decides whether the return-to-lobby item is handed out. */
  public static boolean isEnabled(Main plugin) {
    return settings(plugin).enabled;
  }

  public static boolean sendOnEnd(Main plugin) {
    Settings s = settings(plugin);
    return s.enabled && s.sendOnEnd;
  }

  /** Material name of the return-to-lobby item. */
  public static String lobbyItem(Main plugin) {
    return settings(plugin).item;
  }

  /** Manual path. */
  public static void sendToLobby(Main plugin, Player player) {
    Settings s = settings(plugin);
    if (s.enabled) {
      connect(plugin, player, s.server);
    }
  }

  /** Automatic path. @return true if at least one Connect was sent. */
  public static boolean autoSendToLobby(Main plugin, Iterable<? extends Player> players) {
    Settings s = settings(plugin);
    if (!s.enabled || !s.sendOnEnd) {
      return false;
    }
    boolean sent = false;
    for (Player player : players) {
      sent |= connect(plugin, player, s.server);
    }
    return sent;
  }

  private static boolean connect(Main plugin, Player player, String server) {
    if (!player.isOnline()) {
      return false;
    }
    player.sendMessage(Translator.getPrefix()
        + Translator.getColoredString("GAME.SENDING_TO_SERVER").replace("%SERVER%", server));
    player.sendPluginMessage(plugin, CHANNEL, connect(server));
    return true;
  }

  /** The {@code Connect} sub-channel message: two UTF strings. */
  static byte[] connect(String server) {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    try (DataOutputStream out = new DataOutputStream(bytes)) {
      out.writeUTF("Connect");
      out.writeUTF(server);
    } catch (IOException e) {
      // A ByteArrayOutputStream does not throw.
      throw new IllegalStateException(e);
    }
    return bytes.toByteArray();
  }

  private static org.bukkit.configuration.file.YamlConfiguration file(String yaml, Configuration jar)
      throws org.bukkit.configuration.InvalidConfigurationException {
    org.bukkit.configuration.file.YamlConfiguration c = new org.bukkit.configuration.file.YamlConfiguration();
    c.loadFromString(yaml);
    c.setDefaults(jar);
    return c;
  }

  public static void main(String[] args) throws Exception {
    java.io.DataInputStream in = new java.io.DataInputStream(
        new java.io.ByteArrayInputStream(connect("lobby-1")));
    assert "Connect".equals(in.readUTF()) : "sub-channel first";
    assert "lobby-1".equals(in.readUTF()) : "then the target server";
    assert in.read() == -1 : "nothing after the server name";

    // Same values as the shipped config.yml.
    org.bukkit.configuration.file.YamlConfiguration jar = new org.bukkit.configuration.file.YamlConfiguration();
    jar.loadFromString("proxy:\n  enabled: false\n  lobby-server: lobby\n  send-on-end: true\n  item: RED_BED\n");

    Settings s = resolve(file("proxy:\n  enabled: true\n  lobby-server: hub\n", jar));
    assert s.enabled && s.sendOnEnd && "hub".equals(s.server) : "new keys win, send-on-end from jar";
    assert "RED_BED".equals(s.item) : "item from jar";

    s = resolve(file("proxy:\n  enabled: true\n  send-on-end: false\n", jar));
    assert s.enabled && !s.sendOnEnd && "lobby".equals(s.server) : "send-on-end respected";

    s = resolve(file("proxy:\n  enabled: true\nenableBungeeCommunication: false\nBungee:\n  server: old\n", jar));
    assert s.enabled && "lobby".equals(s.server) : "legacy ignored once proxy.enabled is set";

    s = resolve(file("enableBungeeCommunication: true\nBungee:\n  server: old\n  item: COMPASS\n", jar));
    assert s.enabled && !s.sendOnEnd && "old".equals(s.server) && "COMPASS".equals(s.item)
        : "legacy fallback, never auto-sends";

    s = resolve(file("proxy:\n  lobby-server: hub\nenableBungeeCommunication: true\n", jar));
    assert s.enabled && !s.sendOnEnd : "proxy.enabled absent -> legacy, even with other proxy keys";

    s = resolve(file("start-delay: 120\n", jar));
    assert !s.enabled && !s.sendOnEnd : "neither set -> off";
    System.out.println("ProxyHooks self-check OK");
  }
}
