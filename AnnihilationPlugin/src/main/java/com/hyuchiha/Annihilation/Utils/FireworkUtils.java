package com.hyuchiha.Annihilation.Utils;

import com.hyuchiha.Annihilation.Output.Output;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Random;


public class FireworkUtils {
    public static void spawnFirework(Location loc) {
        Random colour = new Random();

        Firework fw = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta fwMeta = fw.getFireworkMeta();

        FireworkEffect.Type fwType = FireworkEffect.Type.BALL_LARGE;

        int c1i = colour.nextInt(17) + 1;
        int c2i = colour.nextInt(17) + 1;

        Color c1 = getFWColor(c1i);
        Color c2 = getFWColor(c2i);

        FireworkEffect effect = FireworkEffect.builder().withFade(c2).withColor(c1).with(fwType).build();

        fwMeta.addEffect(effect);

        fwMeta.setPower(1);

        fw.setFireworkMeta(fwMeta);
    }

    public static void spawnFirework(Location loc, Color c1, Color c2) {
        Firework fw = loc.getWorld().spawn(loc, Firework.class);

        FireworkMeta fwMeta = fw.getFireworkMeta();

        FireworkEffect.Type fwType = FireworkEffect.Type.BALL_LARGE;

        try {
            FireworkEffect effect = FireworkEffect.builder().withFade(c2).withColor(c1).with(fwType).build();

            fwMeta.addEffect(effect);
            fwMeta.setPower(1);
            fw.setFireworkMeta(fwMeta);
        } catch (Exception e) {
            Output.logError("Error spawning firework");
        }
    }

    private static Color getFWColor(int c) {
        switch (c) {
            case 1:
                return Color.TEAL;

            default:
                return Color.WHITE;
            case 3:
                return Color.YELLOW;
            case 4:
                return Color.AQUA;
            case 5:
                return Color.BLACK;
            case 6:
                return Color.BLUE;
            case 7:
                return Color.FUCHSIA;
            case 8:
                return Color.GRAY;
            case 9:
                return Color.GREEN;
            case 10:
                return Color.LIME;
            case 11:
                return Color.MAROON;
            case 12:
                return Color.NAVY;
            case 13:
                return Color.OLIVE;
            case 14:
                return Color.ORANGE;
            case 15:
                return Color.PURPLE;
            case 16:
                return Color.RED;
            case 17:
                break;
        }
        return Color.SILVER;
    }
}
