package com.hyuchiha.Annihilation.Manager;

import com.cryptomorin.xseries.XAttribute;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import com.cryptomorin.xseries.XSound;
import com.hyuchiha.Annihilation.Base.*;
import com.hyuchiha.Annihilation.Game.BossStarItem;
import com.hyuchiha.Annihilation.Game.GameBoss;
import com.hyuchiha.Annihilation.Game.GameTeam;
import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Maps.MapLoader;
import com.hyuchiha.Annihilation.Mobs.MobCreator;
import com.hyuchiha.Annihilation.Mobs.v1_10_R1.MobCreator_v1_10_R1;
import com.hyuchiha.Annihilation.Mobs.v1_11_R1.MobCreator_v1_11_R1;
import com.hyuchiha.Annihilation.Mobs.v1_12_R1.MobCreator_v1_12_R1;
import com.hyuchiha.Annihilation.Mobs.v1_13_R1.MobCreator_v1_13_R1;
import com.hyuchiha.Annihilation.Mobs.v1_13_R2.MobCreator_v1_13_R2;
import com.hyuchiha.Annihilation.Mobs.v1_14_R1.MobCreator_v1_14_R1;
import com.hyuchiha.Annihilation.Mobs.v1_15_R1.MobCreator_v1_15_R1;
import com.hyuchiha.Annihilation.Mobs.v1_16_R1.MobCreator_v1_16_R1;
import com.hyuchiha.Annihilation.Mobs.v1_16_R2.MobCreator_v1_16_R2;
import com.hyuchiha.Annihilation.Mobs.v1_16_R3.MobCreator_v1_16_R3;
import com.hyuchiha.Annihilation.Mobs.v1_17_R1.MobCreator_v1_17_R1;
import com.hyuchiha.Annihilation.Mobs.v1_9_R1.MobCreator_v1_9_R1;
import com.hyuchiha.Annihilation.Mobs.v1_9_R2.MobCreator_v1_9_R2;
import com.hyuchiha.Annihilation.Output.Output;
import com.hyuchiha.Annihilation.Tasks.BossRespawnTask;
import com.hyuchiha.Annihilation.Utils.ChestUtils;
import com.hyuchiha.Annihilation.Utils.FireworkUtils;
import com.hyuchiha.Annihilation.Utils.LocationUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Chest;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wither;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.inventivetalent.reflection.minecraft.Minecraft;

import java.util.*;

public class BossManager {
  private static MobCreator creator;
  private static ChunkHelper helper;

  private static GameBoss boss;
  private static BossRespawnTask task;
  private static HashMap<GameTeam, Location> bossTeamSpawnLocations = new HashMap<>();
  private static List<Location> teleportLocations = new ArrayList<>();
  private static List<BossStarItem> bossStarItems = new ArrayList<>();
  private static List<ItemStack> bossChestItems = new ArrayList<>();

  public static void init() {
    Output.log("Initializing boss instance generator");

    switch (Minecraft.Version.getVersion()) {
      case v1_9_R1:
        creator = new MobCreator_v1_9_R1();
        helper = new ChunkHelper_v1_9_R1();
        break;
      case v1_9_R2:
        creator = new MobCreator_v1_9_R2();
        helper = new ChunkHelper_v1_9_R2();
        break;
      case v1_10_R1:
        creator = new MobCreator_v1_10_R1();
        helper = new ChunkHelper_v1_10_R1();
        break;
      case v1_11_R1:
        creator = new MobCreator_v1_11_R1();
        helper = new ChunkHelper_v1_11_R1();
        break;
      case v1_12_R1:
        creator = new MobCreator_v1_12_R1();
        helper = new ChunkHelper_v1_12_R1();
        break;
      case v1_13_R1:
        creator = new MobCreator_v1_13_R1();
        helper = new ChunkHelper_v1_13_R1();
        break;
      case v1_13_R2:
        creator = new MobCreator_v1_13_R2();
        helper = new ChunkHelper_v1_13_R2();
        break;
      case v1_14_R1:
        creator = new MobCreator_v1_14_R1();
        helper = new ChunkHelper_v1_14_R1();
        break;
      case v1_15_R1:
        creator = new MobCreator_v1_15_R1();
        helper = new ChunkHelper_v1_15_R1();
        break;
      case v1_16_R1:
        creator = new MobCreator_v1_16_R1();
        helper = new ChunkHelper_v1_16_R1();
        break;
      case v1_16_R2:
        creator = new MobCreator_v1_16_R2();
        helper = new ChunkHelper_v1_16_R2();
        break;
      case v1_16_R3:
        creator = new MobCreator_v1_16_R3();
        helper = new ChunkHelper_v1_16_R3();
        break;
      case v1_17_R1:
        creator = new MobCreator_v1_17_R1();
        helper = new ChunkHelper_v1_17_R1();
        break;
      case v1_18_R1:
        // No creator
        helper = new ChunkHelper_v1_18_R1();
        break;
      case v1_18_R2:
        // No creator
        helper = new ChunkHelper_v1_18_R2();
        break;
      case v1_19_R1:
        // No creator
        helper = new ChunkHelper_v1_19_R1();
        break;
      case v1_19_R2:
        // No creator
        helper = new ChunkHelper_v1_19_R2();
        break;
      case v1_19_R3:
        // No creator
        helper = new ChunkHelper_v1_19_R3();
        break;
      case v1_20_R1:
        // No creator
        helper = new ChunkHelper_v1_20_R1();
        break;
      case v1_20_R2:
        // No creator
        helper = new ChunkHelper_v1_20_R2();
        break;
      case v1_20_R3:
        // No creator
        helper = new ChunkHelper_v1_20_R3();
        break;
      case v1_21_R1:
        // No Creator
        helper = new ChunkHelper_v1_21_R1();
        break;
      case v1_21_R2:
        // No creator
        helper = new ChunkHelper_v1_21_R2();
        break;
      case v1_21_R3:
        // No creator
        helper = new ChunkHelper_v1_21_R3();
        break;
      default:
        Output.log("Version not supported");
        break;
    }

    loadBossChestItems();
    loadBossStarItems();

  }

  private static void loadBossChestItems() {
    Output.log("Loading Boss chest items");

    Configuration config = Main.getInstance().getConfig("games.yml");

    ConfigurationSection section = config.getConfigurationSection("Boss-loot");
    for (String entry : section.getKeys(false)) {
      ItemStack item = loadItem(section, entry);
      bossChestItems.add(item);
    }
  }

  private static void loadBossStarItems() {
    Output.log("Loading Boss star items");

    Configuration config = Main.getInstance().getConfig("games.yml");

    ConfigurationSection section = config.getConfigurationSection("Boss-Star");
    for (String entry : section.getKeys(false)) {
      ItemStack item = loadItem(section, entry);
      int position = section.getInt(entry + ".position");

      bossStarItems.add(new BossStarItem(item, position));
    }
  }

  private static ItemStack loadItem(ConfigurationSection config, String itemName) {
    try {
      String material = config.getString(itemName + ".type");

      Output.log("Material:" + material + "-ToFound");

      Material type = XMaterial.matchXMaterial(material).get().get();

      ItemStack item = null;
      if (type == Material.POTION) {

        String potionType = config.getString(itemName + ".potionType");
        int potionEffectNum = config.getInt(itemName + ".potionEffectNum");
        boolean splash = config.getBoolean(itemName + ".splash");
        boolean extended = config.getBoolean(itemName + ".extended");

        // Obtén el XPotion del nombre base.
        XPotion xPotion = XPotion.of(potionType).orElse(null);

        if (xPotion != null) {
          // Obtén el PotionEffectType.
          PotionEffectType effectType = xPotion.getPotionEffectType();

          // Ajusta la duración y el amplificador según si es extendida o no.
          int duration = 0;
          int amplifier = potionEffectNum;

          if (extended) {
            // Asigna la duración de una poción extendida
            // XPotion.parseEffect() usa 2400 ticks (2 minutos). Debes definir la tuya.
            // Una poción extendida suele durar el doble o más. Usamos una base de ejemplo.
            duration = 9600; // Por ejemplo, 8 minutos (9600 ticks) para una poción normal. Ajusta según tus necesidades.

            // Las pociones extendidas no pueden tener un amplificador alto, generalmente 0.
            amplifier = 0;
          } else {
            // Asigna la duración normal de una poción.
            // La duración estándar es de 180 segundos (3600 ticks). Ajusta según tu lógica.
            duration = 3600;
          }

          // Crea el ItemStack y el PotionMeta.
          ItemStack newPotionItemStack = splash ? XMaterial.SPLASH_POTION.parseItem() : XMaterial.POTION.parseItem();
          PotionMeta newPotionMeta = (PotionMeta) newPotionItemStack.getItemMeta();

          // Crea el PotionEffect con la duración y el amplificador correctos.
          PotionEffect newEffect = new PotionEffect(effectType, duration, amplifier - 1);

          // Agrega el efecto al PotionMeta.
          newPotionMeta.addCustomEffect(newEffect, true);

          // Aplica el meta y asigna el item.
          newPotionItemStack.setItemMeta(newPotionMeta);
          item = newPotionItemStack;
        }

      } else {
        int qty = config.getInt(itemName + ".amount");

        item = new ItemStack(type, qty);

        boolean hasEnchant = config.getBoolean(itemName + ".hasEnchants");
        if (hasEnchant) {
          ConfigurationSection section = config.getConfigurationSection(itemName + ".enchants");
          for (String key : section.getKeys(false)) {
            Enchantment newEnchant = Enchantment.getByName(key);
            int level = section.getInt(key);

            if (newEnchant != null) {
              item.addUnsafeEnchantment(newEnchant, level);
            } else {
              Output.log("Enchantment not found: " + key);
            }
          }
        }
      }

      if (config.getBoolean(itemName + ".hasMeta")) {
        ItemMeta meta = item.getItemMeta();
        String displayName = config.getString(itemName + ".displayName");
        List<String> lore = config.getStringList(itemName + ".lore");

        meta.setDisplayName(displayName);
        meta.setLore(lore);
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

    String bossMap = config.getString("world_spawn");
    String envValue = config.getString("world_env", "THE_END");

    World.Environment environment = World.Environment.valueOf(envValue);
    MapLoader.loadMap(bossMap, environment);

    World bossWorld = Bukkit.getWorld(bossMap);

    for (String teleport : config.getStringList("teleports")) {
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
      Chunk chunk = spawn.getChunk();
      World world = spawn.getWorld();

      Bukkit.getWorld(world.getName()).loadChunk(chunk);

      helper.forceChunkLoad(world, chunk);

      Wither witherBoss;

      if (creator != null) {
        witherBoss = (Wither) creator.getMob("CUSTOM_WITHER").spawnEntity(spawn);
      } else {
        witherBoss = (Wither) spawn.getWorld().spawnEntity(spawn, EntityType.WITHER);
      }

      Output.log("Location: " + spawn.toString());

      AttributeInstance attribute = witherBoss.getAttribute(XAttribute.MAX_HEALTH.get());
      attribute.setBaseValue(boss.getHealth());
      witherBoss.setHealth(boss.getHealth());
      witherBoss.setCanPickupItems(false);
      witherBoss.setRemoveWhenFarAway(false);
      witherBoss.setCustomNameVisible(true);
      witherBoss.setCustomName(
          ChatColor.translateAlternateColorCodes('&', boss
              .getBossName() + " &8» &a" + boss.getHealth() + " HP"));

      Output.log("Boss: " + witherBoss.toString());

      FireworkUtils.spawnFirework(boss.getBossSpawn());
      FireworkUtils.spawnFirework(boss.getBossSpawn());
      FireworkUtils.spawnFirework(boss.getBossSpawn());

      XSound.ENTITY_WITHER_SPAWN.play(boss.getBossSpawn(), 1.0F, 0.1F);
    } else {
      Output.logError("Boss spawn location is null, not spawning the Boss");
    }
  }

  public static void update(Wither g) {
    int health = (int) g.getHealth();
    g.setCustomName(ChatColor.translateAlternateColorCodes('&', boss.getBossName() + " &8» &a" + health + " HP"));
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
      Bukkit.getScheduler().cancelTask(task.getPID());
      task = null;
    }

    task = new BossRespawnTask(respawnTime);
  }

  public static void cancelRespawnTask() {
    if (task != null) {
      Bukkit.getScheduler().cancelTask(task.getPID());
      task = null;
    }
  }

  public static void clearBossData() {
    World bossWorld = getBossSpawnWorld();

    if (bossWorld != null) {
      for (Entity entity : bossWorld.getEntities()) {
        if (entity.getType() == EntityType.WITHER) {
          Output.log("Removing wither");
          entity.remove();
        }
      }

      MapLoader.loadMap(bossWorld.getName(), bossWorld.getEnvironment());
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
    ItemStack[] ritems = bossChestItems.toArray(new ItemStack[0]);
    return new ArrayList<ItemStack>(Arrays.asList(ritems));
  }

  public static List<BossStarItem> getBossStarItems() {
    return bossStarItems;
  }

  public static ChunkHelper getChunkHelper() {
    return helper;
  }
}
