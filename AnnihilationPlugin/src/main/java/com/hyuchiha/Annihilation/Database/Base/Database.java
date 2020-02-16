package com.hyuchiha.Annihilation.Database.Base;

import com.hyuchiha.Annihilation.Database.StatType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class Database {
  protected final HashMap<String, Account> cachedAccounts;
  private final Plugin plugin;

  public Database(Plugin plugin) {
    this.plugin = plugin;

    this.cachedAccounts = new HashMap<>();
  }

  public boolean init() {
    return true;
  }


  public List<Account> getTopKillsAccounts(int size) {
    List<Account> topAccounts = loadTopAccountsByStatType(StatType.KILLS, size * 2);

    topAccounts.sort((account1, account2) -> account2.getKills() - account1.getKills());

    if (topAccounts.size() > size) {
      topAccounts = topAccounts.subList(0, size);
    }

    return topAccounts;
  }

  public List<Account> getTopDeathsAccounts(int size) {
    List<Account> topAccounts = loadTopAccountsByStatType(StatType.DEATHS, size * 2);

    topAccounts.sort((account1, account2) -> account2.getDeaths() - account1.getDeaths());

    if (topAccounts.size() > size) {
      topAccounts = topAccounts.subList(0, size);
    }

    return topAccounts;
  }

  public List<Account> getTopWinsAccounts(int size) {
    List<Account> topAccounts = loadTopAccountsByStatType(StatType.WINS, size * 2);

    topAccounts.sort((account1, account2) -> account2.getWins() - account1.getWins());

    if (topAccounts.size() > size) {
      topAccounts = topAccounts.subList(0, size);
    }

    return topAccounts;
  }

  public List<Account> getTopLossesAccounts(int size) {
    List<Account> topAccounts = loadTopAccountsByStatType(StatType.DEATHS, size * 2);

    topAccounts.sort((account1, account2) -> account2.getLosses() - account1.getLosses());

    if (topAccounts.size() > size) {
      topAccounts = topAccounts.subList(0, size);
    }

    return topAccounts;
  }

  public List<Account> getTopNexusDamageAccounts(int size) {
    List<Account> topAccounts = loadTopAccountsByStatType(StatType.NEXUS_DAMAGE, size * 2);

    topAccounts.sort((account1, account2) -> account2.getNexus_damage() - account1.getNexus_damage());

    if (topAccounts.size() > size) {
      topAccounts = topAccounts.subList(0, size);
    }

    return topAccounts;
  }


  protected abstract List<Account> loadTopAccountsByStatType(StatType paramStatType, int paramInt);


  protected abstract void createAccountAndAddToDatabase(Account paramAccount);


  protected abstract Account loadAccount(String paramString);


  public abstract void saveAccount(Account paramAccount);

  public abstract void addUnlockedKit(String uuid, String kit);

  public Account getAccount(String uuid, String name) {
    Account account = getCachedAccount(uuid, name);

    if (account != null) {
      return account;
    }

    Account loadedAccount = loadAccount(uuid);

    if (loadedAccount != null) {
      loadedAccount.setName(name);
      return loadedAccount;
    }

    return null;
  }


  public Account createAccount(String uuid, String name) {
    Account account = getAccount(uuid, name);

    if (account == null) {
      account = createAndAddAccount(uuid, name);
    }

    return account;
  }

  public boolean removeCachedAccount(Account account) {
    Account removed = this.cachedAccounts.remove(account.getUUID());
    return (removed != null);
  }

  private Account createAndAddAccount(String uuid, String name) {
    Account account = new Account(uuid, name);

    Player player = this.plugin.getServer().getPlayer(UUID.fromString(uuid));

    if (player != null) {
      createAccountAndAddToDatabase(account);
      this.cachedAccounts.put(account.getUUID(), account);
    }

    return account;
  }

  private Account getCachedAccount(String uuid, String name) {
    Account account = this.cachedAccounts.get(uuid);

    if (account != null) {
      account.setName(name);
    }

    return account;
  }

  public void close() {
    Collection<Account> collection = this.cachedAccounts.values();

    for (Account account : collection)
      saveAccount(account);
  }
}
