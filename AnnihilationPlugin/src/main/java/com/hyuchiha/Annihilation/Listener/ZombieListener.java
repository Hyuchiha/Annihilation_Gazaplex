package com.hyuchiha.Annihilation.Listener;

import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Manager.ZombieManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Serializers.PlayerSerializer;
import com.hyuchiha.Annihilation.Utils.TeamUtils;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;

public class ZombieListener implements Listener {

  @EventHandler
  public void onTakeDamage(EntityDamageByEntityEvent event) {
    Entity ent = event.getEntity();
    EntityType type = ent.getType();

    if (type != EntityType.ZOMBIE) {
      return;
    }

    Zombie z = (Zombie) event.getEntity();
    if (event.getDamager() instanceof Snowball) {
      if (event.getDamager() == null) {
        return;
      }
      Snowball s = (Snowball) event.getDamager();
      if (s == null) {
        return;
      }
      if (s.getShooter() == null) {
        event.setCancelled(true);
        return;
      }
      if (s.getShooter() instanceof Player) {
        Player p = (Player) s.getShooter();
        GamePlayer meta = PlayerManager.getGamePlayer(p);

        if (z.getCustomName() == null) {
          z.remove();
          return;
        }

        if (GameTeam.getTeamChar(z.getCustomName()).equals(meta.getTeam().getNexus().getTeam())) {
          p.sendMessage(Translator.getPrefix() + Translator.getColoredString("ERRORS.DAMAGE_TEAM"));
          event.setCancelled(true);
        }

      }
    }

    if (event.getDamager() instanceof Arrow) {
      if (event.getDamager() == null) {
        return;
      }
      Arrow a = (Arrow) event.getDamager();
      if (a == null) {
        return;
      }
      if (a.getShooter() == null) {
        event.setCancelled(true);
        return;
      }
      if (a.getShooter() instanceof Player) {
        Player p = (Player) a.getShooter();
        GamePlayer meta = PlayerManager.getGamePlayer(p);
        if (meta == null) {
          return;
        }
        if (z.getCustomName() == null) {
          z.remove();
          return;
        }

        if (GameTeam.getTeamChar(z.getCustomName()).equals(meta.getTeam().getNexus().getTeam())) {
          p.sendMessage(Translator.getPrefix() + Translator.getColoredString("ERRORS.DAMAGE_TEAM"));
          event.setCancelled(true);
        }
      }
    }

    if (event.getDamager() instanceof Player) {
      if (event.getDamager() == null) {
        return;
      }
      Player p = (Player) event.getDamager();
      if (p == null) {
        return;
      }
      GamePlayer meta = PlayerManager.getGamePlayer(p);
      if (meta == null) {
        return;
      }
      if (z.getCustomName() == null) {
        z.remove();
        return;
      }

      if (GameTeam.getTeamChar(z.getCustomName()).equals(meta.getTeam().getNexus().getTeam())) {
        p.sendMessage(Translator.getPrefix() + Translator.getColoredString("ERRORS.DAMAGE_TEAM"));
        event.setCancelled(true);
      }
    }
  }

  @EventHandler
  public void onDeathEntity(EntityDeathEvent e) {
    if (e.getEntity() instanceof Zombie) {

      Zombie z = (Zombie) e.getEntity();
      if (z.getCustomName() == null) {
        return;
      }
      String zName = TeamUtils.replaceTeamColor(z.getCustomName());
      //e.getDrops().addAll(PlayerSerializer.dropItem(zName));
      if (ZombieManager.getZombies().containsKey(z.getCustomName())) {
        e.getDrops().clear();
        for (ItemStack stack : PlayerSerializer.dropItem(zName)) {
          z.getWorld().dropItemNaturally(e.getEntity().getLocation(), stack);
        }

        ZombieManager.getZombies().remove(zName);
        z.remove();
        PlayerSerializer.removeItems(zName);
      }
    }
  }

  @EventHandler
  public void onDamageEntity(EntityDamageEvent e) {
    if (e.getEntity() instanceof Zombie) {
      Zombie z = (Zombie) e.getEntity();
      if (z.getCustomName() == null) {
        return;
      }
      if (e.getDamage() >= z.getHealth() || e.getEntity().getLocation().getY() <= 0) {
        String zName = TeamUtils.replaceTeamColor(z.getCustomName());
        if (ZombieManager.getZombies().containsKey(z.getCustomName())) {
          ZombieManager.getZombies().remove(zName);
          z.remove();
          PlayerSerializer.removeItems(zName);
        }
      }
    }
  }

  @EventHandler
  public void onTarget(EntityTargetEvent e) {
    if (e.getEntity() instanceof Zombie) {
        Zombie z = (Zombie) e.getEntity();
        if (e.getTarget() instanceof Player) {
          Player p = (Player) e.getTarget();
          GamePlayer meta = PlayerManager.getGamePlayer(p);
          if (z.getCustomName() == null) {
            z.remove();
            return;
          }
          if (GameTeam.getTeamChar(z.getCustomName()).equals(meta.getTeam().getNexus().getTeam())) {
            e.setCancelled(true);
          }
        }
      }
  }
}
