package com.hyuchiha.Annihilation.Config;

import com.hyuchiha.Annihilation.Main;
import com.hyuchiha.Annihilation.Output.Output;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.TreeMap;

public class ConfigManager {
  private static final TreeMap<String, Configuration> configs = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

  private final Main plugin;

  private final File configFolder;
  private final File playerDataFolder;

  public ConfigManager(Main plugin) {
    this.plugin = plugin;
    this.configFolder = plugin.getDataFolder();
    this.playerDataFolder = new File(plugin.getDataFolder() + "/users/");

    if (!this.configFolder.exists()) {
      this.configFolder.mkdirs();
    }

    if (!this.playerDataFolder.exists()) {
      this.playerDataFolder.mkdirs();
    }
  }


  public void loadConfigFile(String filename) {
    loadConfigFiles(filename);
  }


  public void loadConfigFiles(String... filenames) {
    for (String filename : filenames) {
      File configFile = new File(this.configFolder, filename);

      try {
        if (!configFile.exists()) {
          configFile.createNewFile();
          InputStream in = this.plugin.getResource(filename);
          if (in != null) {
            try {
              OutputStream out = new FileOutputStream(configFile);
              byte[] buf = new byte[1024];
              int len;
              while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
              }
              out.close();
              in.close();
            } catch (IOException e) {
              Output.logError("Error reading configuration");
            }
          } else {
            this.plugin.getLogger().warning("Default configuration for " + filename + " missing");
          }
        }


        Configuration config = new Configuration(configFile);
        config.load();
        applyJarDefaults(config.getConfig(), filename);
        configs.put(filename, config);
      } catch (IOException | InvalidConfigurationException e) {
        Output.logError("Error in the configuration");
        e.printStackTrace();
      }
    }
  }

  public void save(String filename) {
    if (configs.containsKey(filename)) {
      try {
        configs.get(filename).save();
      } catch (IOException | InvalidConfigurationException e) {
        printException(e, filename);
      }
    }
  }

  public void reload(String filename) {
    if (configs.containsKey(filename)) {
      try {
        configs.get(filename).load();
      } catch (IOException | InvalidConfigurationException e) {
        printException(e, filename);
      }
    }
  }

  public void reloadAll() {
    for (String filename : configs.keySet()) {
      reload(filename);
    }
  }


  public YamlConfiguration getConfig(String filename) {
    if (configs.containsKey(filename)) {
      return configs.get(filename).getConfig();
    }
    return null;
  }

  /**
   * Backs the admin's file with the copy shipped in the jar, so a key missing from the file
   * reads the shipped value. Read-only: copyDefaults stays off, nothing is written to the file.
   * Applied once: reload() re-loads the same YamlConfiguration, which keeps its defaults.
   * Not for maps.yml: the shipped one is a sample, and its lobby signs/spawns would leak into
   * the admin's maps.
   */
  private void applyJarDefaults(YamlConfiguration config, String filename) {
    if ("maps.yml".equalsIgnoreCase(filename)) {
      return;
    }
    InputStream in = this.plugin.getResource(filename);
    if (in == null) {
      return;
    }
    try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
      config.setDefaults(YamlConfiguration.loadConfiguration(reader));
    } catch (IOException e) {
      printException(e, filename);
    }
  }

  private void printException(Exception e, String filename) {
    if (e instanceof IOException) {
      this.plugin.getLogger().severe("I/O exception while handling " + filename);
    } else if (e instanceof InvalidConfigurationException) {
      this.plugin.getLogger().severe("Invalid configuration in " + filename);
    }
  }


  private static class Configuration {
    private final File configFile;
    private YamlConfiguration config;

    public Configuration(File configFile) {
      this.configFile = configFile;
      this.config = new YamlConfiguration();
    }


    public YamlConfiguration getConfig() {
      return this.config;
    }


    public void load() throws IOException, InvalidConfigurationException {
      this.config.load(this.configFile);
    }


    public void save() throws IOException, InvalidConfigurationException {
      this.config.save(this.configFile);
    }
  }
}
