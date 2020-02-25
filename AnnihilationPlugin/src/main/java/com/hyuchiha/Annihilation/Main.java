package com.hyuchiha.Annihilation;

import com.hyuchiha.Annihilation.BossBar.BossBarAPI;
import com.hyuchiha.Annihilation.Chat.ChatListener;
import com.hyuchiha.Annihilation.Commands.*;
import com.hyuchiha.Annihilation.Config.ConfigManager;
import com.hyuchiha.Annihilation.Database.Base.Database;
import com.hyuchiha.Annihilation.Database.Databases.MongoDB;
import com.hyuchiha.Annihilation.Database.Databases.MySQLDB;
import com.hyuchiha.Annihilation.Database.Databases.SQLiteDB;
import com.hyuchiha.Annihilation.Hooks.VaultHooks;
import com.hyuchiha.Annihilation.Listener.*;
import com.hyuchiha.Annihilation.Manager.*;
import com.hyuchiha.Annihilation.Maps.MapLoader;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Output.Output;
import com.hyuchiha.Annihilation.Protocol.PacketManager;
import com.hyuchiha.Annihilation.Scoreboard.ScoreboardManager;
import com.hyuchiha.Annihilation.Serializers.PlayerSerializer;
import com.hyuchiha.Annihilation.Utils.GameUtils;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
  private static Main instance;
  private static ConfigManager configManager;
  private Database database;

  public static Main getInstance() {
    return instance;
  }

  public void onEnable() {
    instance = this;

    Output.log("Welcome to Annihilation");
    Output.log("Developed by Hyuchiha");

    configManager = new ConfigManager(this);
    configManager.loadConfigFiles("config.yml", "maps.yml", "messages.yml", "shops.yml", "games.yml", "kits.yml");

    BossManager.init();
    ZombieManager.init();
    MapLoader.initMapLoader(this);
    Translator.InitMessages();
    MapManager.initMaps();
    SignManager.initSigns();
    ScoreboardManager.initScoreboard();
    ShopManager.initShops();
    ResourceManager.initializeResources();

    GameManager.initGameManager();
    EnderBrewingManager.initBrewingManager();
    EnderFurnaceManager.initFurnaceManager();
    VotingManager.start();

    PlayerManager.fetchRespawner();
    BossBarAPI.init(this);

    PlayerSerializer.restartDataOfPlayers();

    registerListeners();
    registerCommands();

    hookVault();
    hookBungeeCord();

    initDatabase();

    // Init packet listener
    PacketManager.initHelmetListener();

    // Just for the lol
    this.easterEgg();
  }


  public void onDisable() {
    EnderBrewingManager.disableBrewingManager();
    EnderFurnaceManager.disableFurnaceManager();

    this.database.close();
  }

  private void easterEgg() {
    // byHyuchiha Head
    ItemStack byHyuchihaHead = GameUtils.getPlayerHead("byHyuchiha");

    ShapedRecipe byHyuchihaRecipe = new ShapedRecipe(byHyuchihaHead);
    byHyuchihaRecipe.shape("PIB", "DRE", " G ");
    byHyuchihaRecipe.setIngredient('P', GameUtils.getDyeColor(DyeColor.PINK).getData());
    byHyuchihaRecipe.setIngredient('I', Material.IRON_INGOT);
    byHyuchihaRecipe.setIngredient('B', GameUtils.getDyeColor(DyeColor.BLUE).getData());
    byHyuchihaRecipe.setIngredient('D', Material.DIAMOND);
    byHyuchihaRecipe.setIngredient('R', Material.REDSTONE);
    byHyuchihaRecipe.setIngredient('E', Material.EMERALD);
    byHyuchihaRecipe.setIngredient('G', Material.GOLD_INGOT);

    Bukkit.addRecipe(byHyuchihaRecipe);
  }

  public Configuration getConfig(String config) {
    return configManager.getConfig(config);
  }


  private void registerListeners() {
    PluginManager pm = getServer().getPluginManager();

    pm.registerEvents(new JoinListener(this), this);
    pm.registerEvents(new SignListener(this), this);
    pm.registerEvents(new GameListener(this), this);
    pm.registerEvents(new PlayerListener(this), this);
    pm.registerEvents(new ChatListener(), this);
    pm.registerEvents(new InventoryListener(), this);
    pm.registerEvents(new WorldListener(), this);
    pm.registerEvents(new BlockListener(), this);
    pm.registerEvents(new QuitListener(this), this);
    pm.registerEvents(new SoulboundListener(), this);
    pm.registerEvents(new MotdListener(this), this);
    pm.registerEvents(new ResourceListener(this), this);
    pm.registerEvents(new EnderChestListener(), this);
    pm.registerEvents(new EnderBrewingStandListener(), this);
    pm.registerEvents(new EnderFurnaceListener(), this);

    pm.registerEvents(new BossListener(this), this);
    pm.registerEvents(new WitchListener(this), this);
    pm.registerEvents(new ZombieListener(), this);
    pm.registerEvents(new InteractListener(), this);
  }

  private void registerCommands() {
    getCommand("anni").setExecutor(new AnnihilationCommand(this));
    getCommand("team").setExecutor(new TeamCommand());
    getCommand("vote").setExecutor(new VoteCommand());
    getCommand("stats").setExecutor(new StatsCommand(this));
    getCommand("top").setExecutor(new TopCommand(this));
    getCommand("class").setExecutor(new KitCommand());
    getCommand("star").setExecutor(new StarCommand());
  }


  private void hookBungeeCord() {
    Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
  }


  private void hookVault() {
    if (getServer().getPluginManager().isPluginEnabled("Vault")) {
      VaultHooks.vault = true;


      if (!VaultHooks.instance().setupPermissions()) {
        VaultHooks.vault = false;
        getLogger().warning("Unable to load Vault: No permission plugin found.");
      }


      if (!VaultHooks.instance().setupChat()) {
        VaultHooks.vault = false;
        getLogger().warning("Unable to load Vault: No chat plugin found.");
      }


      if (!VaultHooks.instance().setupEconomy()) {
        VaultHooks.vault = false;
        getLogger().warning("Unable to load Vault: No economy plugin found.");
      }

      if (VaultHooks.vault) {
        getLogger().info("Vault hook initalized!");
      }
    } else {
      getLogger().warning("Vault not found! Permissions features disabled.");
    }
  }

  public void initDatabase() {
    Configuration configValues = getConfig("config.yml");

    switch (configValues.getString("Database.type")) {
      case "MySQL":
        this.database = new MySQLDB(this);
        break;
      case "SQLite":
        this.database = new SQLiteDB(this);
        break;
      case "MongoDB":
        this.database = new MongoDB(this);
        break;
    }

    if (this.database != null && !this.database.init()) {
      Output.logError("Database init error");

      setEnabled(false);
    }
  }


  public Database getMainDatabase() {
    return this.database;
  }
}
