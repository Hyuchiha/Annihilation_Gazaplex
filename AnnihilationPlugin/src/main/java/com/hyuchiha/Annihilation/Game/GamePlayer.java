package com.hyuchiha.Annihilation.Game;

import com.cryptomorin.xseries.XSound;
import com.hyuchiha.Annihilation.BossBar.BossBarAPI;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.MapManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Output.Output;
import com.hyuchiha.Annihilation.Scoreboard.ScoreboardManager;
import com.hyuchiha.Annihilation.Utils.ItemSelectorUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import java.util.Random;
import java.util.UUID;


public class GamePlayer {
  private UUID playerUUID;
  private PlayerState state;
  private GameTeam team;
  private boolean alive;
  private Kit kit;

  public GamePlayer(UUID uuid) {
    this.playerUUID = uuid;
    this.state = PlayerState.LOBBY;
    this.team = GameTeam.NONE;
    this.alive = false;

    this.kit = Kit.CIVILIAN;
  }

  public UUID getPlayerUUID() {
    return this.playerUUID;
  }

  public Player getPlayer() {
    return Bukkit.getPlayer(this.playerUUID);
  }

  public GameTeam getTeam() {
    return this.team;
  }

  public void setTeam(GameTeam t) {
    if (this.team != null) {
      this.team = t;
    } else {
      this.team = GameTeam.NONE;
    }
  }

  public boolean isAlive() {
    return this.alive;
  }

  public void setAlive(boolean b) {
    this.alive = b;
  }

  private void setupPlayerData() {
    Player player = getPlayer();

    AttributeInstance attribute = player.getAttribute(Attribute.MAX_HEALTH);
    attribute.setBaseValue(20.0D);

    player.setHealth(20.0D);
    player.setFoodLevel(20);
    player.setExp(0.0F);
    player.setLevel(0);
    player.setSaturation(20.0F);
    player.setAllowFlight(false);
  }

  public void addXp(int exp) {
    getPlayer().giveExp(exp);
    XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(getPlayer(), 1.0F, 1.0F);
  }

  public void prepareLobbyPlayer() {
    final Player player = getPlayer();
    player.teleport(MapManager.getLobbySpawn());

    Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
      public void run() {
        BossBarAPI.setMessage(player, Translator.getColoredString("BOSSBAR.WELCOME_TO_ANNIHILATION"), 1.0F);
      }
    }, 20L);


    this.state = PlayerState.LOBBY_GAME;

    ScoreboardManager.updatePlayerScoreboard();

    player.setGameMode(GameMode.SURVIVAL);

    setupPlayerData();

    PlayerInventory inv = player.getInventory();
    inv.setHelmet(null);
    inv.setChestplate(null);
    inv.setLeggings(null);
    inv.setBoots(null);

    inv.clear();

    for (PotionEffect effect : player.getActivePotionEffects()) {
      player.removePotionEffect(effect.getType());
    }

    ItemSelectorUtils.giveTeamSelector(player);
    ItemSelectorUtils.giveMapSelector(player);
    ItemSelectorUtils.giveLobbyReturnItem(player);
    ItemSelectorUtils.giveKitSelectorItem(player);
    ItemSelectorUtils.giveKitUnlockerItem(player);

    setTeam(GameTeam.NONE);
    setKit(Kit.CIVILIAN);
  }


  public void preparePlayerForGame() {
    getPlayer().getInventory().clear();
    getPlayer().setGameMode(GameMode.SURVIVAL);
    setAlive(true);
    setupPlayerData();
    getPlayer().teleport(getTeam().getRandomSpawn());
    getKit().getKit().giveKitItems(getPlayer());

    this.state = PlayerState.IN_GAME;
  }

  public void regamePlayer() {
    getPlayer().setGameMode(GameMode.SURVIVAL);
    setupPlayerData();
    getPlayer().teleport(getTeam().getRandomSpawn());
    getKit().getKit().giveKitItems(getPlayer());
  }

  public void giveOreXP(int xp) {
    if (xp > 0) {
      int xpMultiplier = getKit().getKit().getXpMultiplier();

      getPlayer().giveExp(xp * xpMultiplier);
      float pitch = new Random().nextFloat() * 0.2F + 0.9F;
      XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(getPlayer(), 1.0F, pitch);
    }
  }

  public void giveOreDrops(ItemStack[] drops) {
    for (ItemStack stack : drops) {
      if (stack.getAmount() > 0) {
        getPlayer().getInventory().addItem(stack);
      }
    }
  }

  public PlayerState getState() {
    return this.state;
  }

  public void setState(PlayerState state) {
    this.state = state;
  }

  public Kit getKit() {
    return kit;
  }

  public void setKit(Kit kit) {
    this.kit = kit;
  }
}
