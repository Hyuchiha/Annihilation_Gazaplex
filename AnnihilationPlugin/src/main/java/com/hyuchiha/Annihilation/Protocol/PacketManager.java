package com.hyuchiha.Annihilation.Protocol;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Protocol.Packets.WrapperPlayServerEntityEquipment;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PacketManager {
  private static FakeEquipment equipmentListener;

  public static void initHelmetListener() {
    if (checkProtocolEnabled()) {
      equipmentListener = new FakeEquipment(Main.getInstance()) {
        @Override
        protected boolean onEquipmentSending(EquipmentSendingEvent equipmentEvent) {
          if (equipmentEvent.getSlot() == EquipmentSlot.HEAD && equipmentEvent.getVisibleEntity() instanceof Player && equipmentEvent.getVisibleEntity().getEquipment() != null) {
            Player player = (Player) equipmentEvent.getVisibleEntity();

            if (player.getEquipment().getHelmet() != null && player.getEquipment().getHelmet().getType() != Material.AIR) {
              ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);

              boolean isInvisible = false;
              for (PotionEffect effect : player.getActivePotionEffects()) {
                if (effect.getType() == PotionEffectType.INVISIBILITY) {
                  isInvisible = true;
                }
              }

              GamePlayer gamePlayer = PlayerManager.getGamePlayer(player);
              if (gamePlayer.getTeam() != GameTeam.NONE && !isInvisible) {
                Color color = gamePlayer.getTeam().getColor();
                LeatherArmorMeta leather = (LeatherArmorMeta) helmet.getItemMeta();
                leather.setColor(color);
                helmet.setItemMeta(leather);

                equipmentEvent.setEquipment(helmet);
              } else {
                if (isInvisible) {
                  equipmentEvent.setEquipment(player.getEquipment().getHelmet());
                }
              }
            }

            return true;
          }
          return false;
        }

        @Override
        protected void onEntitySpawn(Player client, LivingEntity visibleEntity) {
          // Remember to change this if you're intercepting a different slot!
          if (EquipmentSlot.HEAD.isEmpty(visibleEntity)) {
            updateSlot(client, visibleEntity, EquipmentSlot.HEAD);
          }
        }
      };
    }
  }

  private static boolean checkProtocolEnabled() {
    return Bukkit.getServer().getPluginManager().isPluginEnabled("ProtocolLib");
  }

  public static void sendPacketHelmet(Player client, ItemStack helmet) {
    WrapperPlayServerEntityEquipment wrapper = new WrapperPlayServerEntityEquipment();
    wrapper.setEntityID(client.getEntityId());
    wrapper.setSlot(EnumWrappers.ItemSlot.HEAD);
    wrapper.setItem(helmet);

    for (Entity toSend: client.getNearbyEntities(30, 30, 30)) {
      if (toSend instanceof Player) {
        Player toSendPlayer = (Player) toSend;
        wrapper.sendPacket(toSendPlayer);
      }
    }
  }

  public static void clear() {
    if (equipmentListener != null) {
      equipmentListener.close();
      equipmentListener = null;
    }
  }
}
