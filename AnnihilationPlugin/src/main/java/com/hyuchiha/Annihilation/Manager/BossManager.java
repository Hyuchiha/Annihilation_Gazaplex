package com.hyuchiha.Annihilation.Manager;

import com.hyuchiha.Annihilation.Game.BossStarItem;
import com.hyuchiha.Annihilation.Game.GameBoss;
import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Mobs.MobCreator;
import com.hyuchiha.Annihilation.Mobs.v1_10_R1.MobCreator_v1_10_R1;
import com.hyuchiha.Annihilation.Mobs.v1_11_R1.MobCreator_v1_11_R1;
import com.hyuchiha.Annihilation.Mobs.v1_12_R1.MobCreator_v1_12_R1;
import com.hyuchiha.Annihilation.Mobs.v1_9_R1.MobCreator_v1_9_R1;
import com.hyuchiha.Annihilation.Mobs.v1_9_R2.MobCreator_v1_9_R2;
import com.hyuchiha.Annihilation.Output.Output;
import com.hyuchiha.Annihilation.Serializers.PlayerSerializer;
import com.hyuchiha.Annihilation.Tasks.BossRespawnTask;
import com.hyuchiha.Annihilation.Utils.ChestUtils;
import com.hyuchiha.Annihilation.Utils.FireworkUtils;
import com.hyuchiha.Annihilation.Utils.LocationUtils;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wither;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.inventivetalent.reflection.minecraft.Minecraft;

import java.util.*;

public class BossManager {
  private static MobCreator creator;

  private static GameBoss boss;
  private static BossRespawnTask task;
  private static HashMap<GameTeam, Location> bossTeamSpawnLocations = new HashMap<>();
  private static List<Location> teleportLocations = new ArrayList<>();
  private static List<BossStarItem> bossStarItems = new ArrayList<>();

  public static void init() {
    Output.log("Initializing boss instance generator");

    switch (Minecraft.Version.getVersion()){
      case v1_9_R1:
        creator = new MobCreator_v1_9_R1();
        break;
      case v1_9_R2:
        creator = new MobCreator_v1_9_R2();
        break;
      case v1_10_R1:
        creator = new MobCreator_v1_10_R1();
        break;
      case v1_11_R1:
        creator = new MobCreator_v1_11_R1();
        break;
      case v1_12_R1:
        creator = new MobCreator_v1_12_R1();
        break;
      default:
        Output.log("Version not supported");
        break;
    }

    loadBossStarItems();

  }

  private static void loadBossStarItems() {
    Output.log("Loading Boss star items");

    Configuration config = Main.getInstance().getConfig("games.yml");

    ConfigurationSection section = config.getConfigurationSection("Boss-Star");
    for (String entry : section.getKeys(false)) {
      BossStarItem item = loadItem(section, entry);
      bossStarItems.add(item);
    }
  }

  private static BossStarItem loadItem(ConfigurationSection config, String itemName) {
    try {
      Material type = Material.getMaterial(config.getString(itemName + ".type"));
      int position = config.getInt(itemName + ".position");

      BossStarItem item = null;
      if (type == Material.POTION) {

        String potionType   = config.getString(itemName + ".potionType");
        int potionEffectNum = config.getInt(itemName + ".potionEffectNum");
        boolean splash      = config.getBoolean( itemName + ".splash");
        boolean extended    = config.getBoolean(itemName + ".extended");

        Potion potion = new Potion(PotionType.valueOf(potionType), potionEffectNum);
        potion.setSplash(splash);

        if(extended){
          potion.setHasExtendedDuration(extended);
        }

        item = new BossStarItem(potion.toItemStack(1), position);

      } else {
        int qty = config.getInt(itemName + ".amount");

        ItemStack stack = new ItemStack(type, qty);

        boolean hasEnchant = config.getBoolean(itemName + ".hasEnchant");
        if (hasEnchant) {
          ConfigurationSection section = config.getConfigurationSection(itemName + ".enchants");
          for (String keys : section.getKeys(false)) {
            Enchantment newEnchant = Enchantment.getByName(keys);
            int level = section.getInt(keys);
            stack.addEnchantment(newEnchant, level);
          }
        }

        item = new BossStarItem(stack, position);
      }

      return item;
    } catch (Exception e) {
      e.printStackTrace();
      Output.logError(e.getLocalizedMessage());
    }
    return null;
  }

  public static void loadBossConfiguration(ConfigurationSection config, World originalWorld) {
    Output.log("Loading boss configuration");

    World bossWorld = Bukkit.getWorld(config.getString("world_spawn"));

    for (String teleport: config.getStringList("teleports")) {
      teleportLocations.add(LocationUtils.parseLocation(originalWorld, teleport));
    }

    for (GameTeam team : GameTeam.teams()) {
      String name = team.name().toLowerCase();
      if (config.contains("spawns." + name)) {
        String cords = config.getString("spawns." + name);
        Location location = LocationUtils.parseLocation(bossWorld, cords);
        bossTeamSpawnLocations.put(team, location);
      }
    }

    int health = config.getInt("hearts") * 2;
    String name = config.getString("boss_name");
    Location spawnPoint = LocationUtils.parseLocation(bossWorld, config.getString("boss_spawn"));
    Location chestPoint = LocationUtils.parseLocation(bossWorld, config.getString("chest"));

    boss = new GameBoss(health, name, spawnPoint, chestPoint);

    Output.log("Boss loaded");
  }

  public static void spawnBoss() {
    Location spawn = boss.getBossSpawn();

    if (spawn != null && spawn.getWorld() != null) {
      Bukkit.getWorld(spawn.getWorld().getName()).loadChunk(spawn.getChunk());

      Wither witherBoss;

      if (creator != null) {
        witherBoss = (Wither) creator.getMob("Wither").spawnEntity(spawn);
      } else {
        witherBoss = (Wither) spawn.getWorld().spawnEntity(spawn, EntityType.WITHER);
      }

      witherBoss.setMaxHealth(boss.getHealth());
      witherBoss.setHealth(boss.getHealth());
      witherBoss.setCanPickupItems(false);
      witherBoss.setRemoveWhenFarAway(false);
      witherBoss.setCustomNameVisible(true);
      witherBoss.setCustomName(
          ChatColor.translateAlternateColorCodes('&', boss
                                                          .getBossName() + " &8» &a" + boss.getHealth() + " HP"));

      FireworkUtils.spawnFirework(boss.getBossSpawn());
      FireworkUtils.spawnFirework(boss.getBossSpawn());
      FireworkUtils.spawnFirework(boss.getBossSpawn());
    } else {
      Output.logError("Boss spawn location is null, not spawning the Boss");
    }
  }

  public static void update(Wither g) {
    g.setCustomName(ChatColor.translateAlternateColorCodes('&', boss.getBossName() + " &8» &a" + g.getHealth() + " HP"));
  }

  public static boolean hasBossConfig() {
    return boss != null;
  }

  public static World getBossSpawnWorld() {
    if (hasBossConfig()) {
      return boss.getBossSpawn().getWorld();
    }
    return null;
  }

  public static List<Location> getTeleportLocations() {
    return teleportLocations;
  }

  public static Location getTeamSpawn(GameTeam team) {
    return bossTeamSpawnLocations.get(team);
  }

  public static GameBoss getBoss() {
    return boss;
  }

  public static void beginRespawnTime(int respawnTime) {
    if (task != null) {
      task.cancel();
      task = null;
    }

    task = new BossRespawnTask(respawnTime);
  }

  public static void cancelRespawnTask() {
    if (task != null) {
      task.cancel();
      task = null;
    }
  }

  public static void clearBossData() {
    World bossWorld = getBossSpawnWorld();

    if (bossWorld != null) {
      for (Entity entity: bossWorld.getEntities()) {
        if(entity.getType() == EntityType.WITHER){
          Output.log("Removing wither");
          entity.remove();
        }
      }
    }

    cancelRespawnTask();

    boss = null;
    bossTeamSpawnLocations.clear();
    teleportLocations.clear();
  }

  public static void spawnLootChest() {
    Location chest = boss.getChest();
    chest.getBlock().setType(Material.CHEST);

    double y = chest.getY() - 1;
    Location lf = new Location(chest.getWorld(), chest.getBlockX(), y, chest.getBlockZ());
    FireworkUtils.spawnFirework(lf);

    // TODO add effect to chest spawn
//    for (int i = 0; i < 5; i++) {
//      EffectsManager.playEffectLoc(chest.getWorld(), Effect.ENDER_SIGNAL, chest.getBlock().getLocation());
//    }

    Chest c = (Chest) chest.getBlock().getState();
    Inventory inv = c.getBlockInventory();

    Random r = new Random();
    for (int i = 0; i < 5; i++) {
      ItemStack randomItem = getRandomItem();
      inv.setItem(r.nextInt(inv.getSize()), randomItem);
    }

    int ingots = 4;
    for (int i = 0; i < ingots; i++) {
      int slot = r.nextInt(inv.getSize());
      ItemStack stack = inv.getItem(slot);
      if (ChestUtils.isEmpty(inv, slot)) {
        inv.setItem(slot, new ItemStack(Material.IRON_INGOT));
      } else if (stack.getType() == Material.IRON_INGOT) {
        inv.getItem(slot).setAmount(stack.getAmount() + 1);
      } else {
        i--;
      }
    }
  }

  public static ItemStack getRandomItem() {
    int randomIndex = new Random().nextInt(rItems().size());
    return (ItemStack) rItems().toArray()[randomIndex];
  }

  public static Collection<ItemStack> rItems() {
    Collection<ItemStack> items = new ArrayList<>();
    ItemStack[] ritems = PlayerSerializer.BossLoot();
    Collections.addAll(items, ritems);
    return items;
  }

  public static List<BossStarItem> getBossStarItems() {
    return bossStarItems;
  }
}
