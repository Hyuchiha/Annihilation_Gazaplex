package com.hyuchiha.Annihilation.Protocol;

import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
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
              for (PotionEffect effect: player.getActivePotionEffects()) {
                if (effect.getType() == PotionEffectType.INVISIBILITY) {
                  isInvisible = true;
                }
              }

              GamePlayer gamePlayer = PlayerManager.getGamePlayer(player);
              if (gamePlayer.getTeam() != GameTeam.NONE && !isInvisible) {
                Color color = gamePlayer.getTeam().getColor();
                LeatherArmorMeta leather = (LeatherArmorMeta) helmet.getItemMeta();
                leather.setColor(color);

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

  private static boolean checkProtocolEnabled(){
    return Bukkit.getServer().getPluginManager().isPluginEnabled("ProtocolLib");
  }
}
