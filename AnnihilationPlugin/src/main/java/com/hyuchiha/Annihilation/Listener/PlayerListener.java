package com.hyuchiha.Annihilation.Listener;

import com.cryptomorin.xseries.XMaterial;
import com.hyuchiha.Annihilation.Chat.ChatUtil;
import com.hyuchiha.Annihilation.Database.Base.Account;
import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.GameManager;
import com.hyuchiha.Annihilation.Manager.MapManager;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Manager.VotingManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Protocol.PacketManager;
import com.hyuchiha.Annihilation.Utils.GameUtils;
import com.hyuchiha.Annihilation.Utils.MenuUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class PlayerListener implements Listener {
  private Main plugin;
  private Configuration config;

  public PlayerListener(Main plugin) {
    this.plugin = plugin;
    this.config = plugin.getConfig("config.yml");
  }

  @EventHandler
  public void onPlayerDamage(EntityDamageEvent event) {
    Entity entity = event.getEntity();

    if (entity instanceof Player &&
        GameManager.getCurrentGame() != null) {
      switch (GameManager.getCurrentGame().getTimer().getGameState()) {
        case WAITING:
        case STARTING:
        case RESTARTING:
          event.setCancelled(true);
          break;
      }
    }
  }


  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent e) {
    final Player player = e.getEntity();

    if (GameManager.getCurrentGame().isInGame()) {
      GamePlayer gm = PlayerManager.getGamePlayer(player);

      if (gm.getTeam() != GameTeam.NONE && !gm.getTeam().getNexus().isAlive()) {
        gm.setAlive(false);
      }

      if (gm.getTeam() != GameTeam.NONE && !player.getWorld().getName().equals("lobby")) {
        Account data = this.plugin.getMainDatabase().getAccount(player.getUniqueId().toString(), player.getName());
        data.increaseDeaths();
      }

      if (player.getKiller() != null && !player.getKiller().equals(player)) {
        Player killer = player.getKiller();

        GamePlayer gpKiller = PlayerManager.getGamePlayer(player);
        gpKiller.addXp(this.config.getInt("Exp-player-kill"));

        Account data = this.plugin.getMainDatabase().getAccount(killer.getUniqueId().toString(), killer.getName());
        data.increaseKills();

        double money = this.config.getDouble("Money-player-kill");
        double vipMoney = PlayerManager.calculateVipMoneyGive(player, money);
        PlayerManager.addMoney(player, vipMoney);


        String message = ChatUtil.formatDeathMessage(player, killer, e.getDeathMessage());
        e.setDeathMessage(message);
      } else {
        String message = ChatUtil.formatDeathMessage(player, e.getDeathMessage());
        e.setDeathMessage(message);
      }

      e.setDroppedExp(player.getTotalExperience());
    }

    Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> PlayerManager.respawnPlayer(player), 5L);

    GameUtils.giveEffect(PlayerManager.getGamePlayer(player));
  }

  @EventHandler
  public void onPlayerRespawn(PlayerRespawnEvent e) {
    Player player = e.getPlayer();


    GamePlayer gm = PlayerManager.getGamePlayer(player);

    if (gm.isAlive()) {
      e.setRespawnLocation(gm.getTeam().getRandomSpawn());
      gm.regamePlayer();
    } else {
      e.setRespawnLocation(MapManager.getLobbySpawn());
      gm.prepareLobbyPlayer();
    }
  }

  @EventHandler
  public void LobbyBlockBreak(BlockBreakEvent e) {
    if (e.getPlayer().getGameMode() != GameMode.CREATIVE && e.getPlayer().getWorld().getName().equalsIgnoreCase("lobby")) {
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void LobbyBlockPlace(BlockPlaceEvent e) {
    if (e.getPlayer().getGameMode() != GameMode.CREATIVE && e.getPlayer().getWorld().getName().equalsIgnoreCase("lobby")) {
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void onPlayerLobbyDropItem(PlayerDropItemEvent e) {
    if (e.getPlayer().getWorld().getName().equalsIgnoreCase("lobby")) {
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void onLobbyItemInteract(PlayerInteractEvent e) {
    Player player = e.getPlayer();
    GamePlayer pmeta = PlayerManager.getGamePlayer(player);

    Action action = e.getAction();
    if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
      ItemStack handItem = player.getInventory().getItemInMainHand();
      if (handItem != null) {
        XMaterial type = XMaterial.matchXMaterial(handItem);

        switch (type) {
          case PAPER:
            if (handItem.getItemMeta().hasDisplayName() &&
                handItem.getItemMeta().getDisplayName().contains(Translator.getColoredString("GAME.CLICK_TO_VOTE_MAP"))) {
              e.setCancelled(true);

              if (VotingManager.isRunning()) {
                MenuUtils.showMapSelector(player);
                break;
              }
              player.sendMessage(Translator.getPrefix() + ChatColor.RED + Translator.getString("ERRORS.NOT_VOTE_PHASE"));
            }
            break;


          case WHITE_WOOL:
            if (handItem.getItemMeta().hasDisplayName() &&
                handItem.getItemMeta().getDisplayName().contains(Translator.getColoredString("GAME.CLICK_TO_CHOOSE_TEAM"))) {
              e.setCancelled(true);

              MenuUtils.showTeamSelector(player);
            }
            break;

          case RED_BED:
            if (handItem.getItemMeta().hasDisplayName() &&
                handItem.getItemMeta().getDisplayName().contains(Translator.getColoredString("GAME.CLICK_TO_RETURN_LOBBY"))) {
              e.setCancelled(true);

              final String ServerExit = plugin.getConfig().getString("Bungee.server");
              try {
                final ByteArrayOutputStream b = new ByteArrayOutputStream();
                final DataOutputStream out = new DataOutputStream(b);
                out.writeUTF("Connect");
                out.writeUTF(ServerExit);
                player.sendPluginMessage(this.plugin, "BungeeCord", b.toByteArray());
                b.close();
                out.close();
              } catch (Exception error) {
                String message = Translator.getColoredString("ERRORS.COULD_NOT_CONNECT_SERVER").replace("%SERVER%", ServerExit);
                player.sendMessage(Translator.getPrefix() + ChatColor.RED + message);
              }

            }
            break;
        }
      }
    }
  }


  @EventHandler
  public void onPlayerPortal(PlayerPortalEvent event) {
    event.setCancelled(true);

    Player player = event.getPlayer();
    if (player.getWorld() != Bukkit.getWorld("lobby")) {

      GamePlayer gamePlayer = PlayerManager.getGamePlayer(player);

      player.teleport(gamePlayer.getTeam().getRandomSpawn());

      Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
        public void run() {
          MenuUtils.showKitSelector(player);
        }
      }, 20L);

    } else {
      player.teleport(MapManager.getLobbySpawn());
    }
  }

  @EventHandler
  public void onFoodLevelChange(FoodLevelChangeEvent event) {
    if (event.getEntity().getWorld().getName().equals("lobby")) {
      event.setCancelled(true);
      event.setFoodLevel(20);
    }
  }

  @EventHandler
  public void onPotionDrink(PlayerItemConsumeEvent event) {
    ItemStack item = event.getItem();
    if (item.getType() == Material.POTION) {
      PotionMeta potion = (PotionMeta) item.getItemMeta();
      for (PotionEffect effect : potion.getCustomEffects()) {
        if (effect.getType() == PotionEffectType.INVISIBILITY) {
          Player player = event.getPlayer();
          PacketManager.sendPacketHelmet(player, player.getInventory().getHelmet());
        }
      }
    }
  }


  @EventHandler
  public void onPotionSplash(PotionSplashEvent event) {
    ThrownPotion thrownPotion = event.getPotion();
    for (PotionEffect effect : thrownPotion.getEffects()) {
      if (effect.getType() == PotionEffectType.INVISIBILITY) {
        for (LivingEntity entity : event.getAffectedEntities()) {
          if (entity instanceof Player) {
            Player player = (Player) entity;
            PacketManager.sendPacketHelmet(player, player.getInventory().getHelmet());
          }
        }
      }
    }
  }


  @EventHandler
  public void onWorkbechInteract(PlayerInteractEvent e) {
    Player player1 = e.getPlayer();
    if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    Block b = e.getClickedBlock();
    if (e.isCancelled()) {
      return;
    }
    if ((b.getType() == XMaterial.CRAFTING_TABLE.get() || b.getType() == Material.ANVIL) && b.getType().isBlock() &&
        GameManager.getCurrentGame().isInGame() && !GameManager.getCurrentGame().getCrafting().containsKey(((Player) player1).getName())) {
      GameManager.getCurrentGame().getCrafting().put(((Player) player1).getName(), b);
    }
  }


  @EventHandler(ignoreCancelled = true)
  public void onPlayerDamageByVoid(EntityDamageEvent e) {
    if (e.getEntity() instanceof Player && e.getEntity().getWorld().getName().equals("lobby")) {
      e.setCancelled(true);
      if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
        e.getEntity().teleport(MapManager.getLobbySpawn());
      }
    }
  }
}
