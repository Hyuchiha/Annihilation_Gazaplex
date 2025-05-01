package com.hyuchiha.Annihilation.Utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.inventivetalent.reflection.minecraft.Minecraft;
import org.inventivetalent.reflection.minecraft.MinecraftVersion;

@SuppressWarnings("deprecation")
public class PotionUtils {
    public static ItemStack getBasePotionType(PotionType type, int amount) {
        ItemStack potion = new ItemStack(Material.POTION, amount);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();

        if (MinecraftVersion.getVersion().olderThan(Minecraft.Version.v1_20_R1)) {
            meta.setBasePotionData(new PotionData(type, false, false));
            potion.setItemMeta(meta);
        } else {
            meta.setBasePotionType(type);
            potion.setItemMeta(meta);
        }

        return potion;
    }
}
