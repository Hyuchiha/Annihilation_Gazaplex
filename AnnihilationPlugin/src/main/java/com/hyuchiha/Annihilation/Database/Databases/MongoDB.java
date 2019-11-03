package com.hyuchiha.Annihilation.Database.Databases;

import com.hyuchiha.Annihilation.Database.Base.Account;
import com.hyuchiha.Annihilation.Database.Base.Database;
import com.hyuchiha.Annihilation.Database.StatType;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;


public class MongoDB extends Database {
  private static final String ACCOUNTS_COLLECTION = "accounts";

  private final Plugin plugin;
  private MongoClient mongoClient;

  public MongoDB(Plugin plugin) {
    super(plugin);

    this.plugin = plugin;
  }


  public boolean init() {
    super.init();

    ConfigurationSection section = getConfigSection();

    MongoCredential credential = MongoCredential.createScramSha1Credential(
        section.getString("user"),
        section.getString("name"),
        section.getString("pass").toCharArray()
    );

    MongoClientOptions.Builder options = new MongoClientOptions.Builder();
    //options.sslEnabled(true);
    //options.sslInvalidHostNameAllowed(true);
    options.connectTimeout(10000);

    this.mongoClient = new MongoClient(
        new ServerAddress(
            getConfigSection().getString("host"),
            getConfigSection().getInt("port")
        ), credential, options.build());

    return getDatabase() != null;
  }


  public MongoDatabase getDatabase() {
    return this.mongoClient.getDatabase(getConfigSection().getString("name"));
  }


  protected List<Account> loadTopAccountsByStatType(StatType type, int size) {
    MongoDatabase database = getDatabase();

    MongoCollection<Document> collection = database.getCollection(ACCOUNTS_COLLECTION);

    MongoIterable<Document> findIterable = collection.find().sort(Sorts.descending(type.name().toLowerCase())).limit(size);

    List<Account> accounts = new ArrayList<>();

    MongoCursor<Document> cursor = findIterable.iterator();
    while (cursor.hasNext()) {
      Document document = cursor.next();
      accounts.add(getAccountFromDocument(document));
    }

    return accounts;
  }


  protected void createAccountAndAddToDatabase(Account account) {
    MongoDatabase database = getDatabase();

    MongoCollection<Document> collection = database.getCollection(ACCOUNTS_COLLECTION);

    collection.insertOne(getDocument(account));

    this.cachedAccounts.put(account.getUUID(), account);
  }


  protected Account loadAccount(String uuid) {
    MongoDatabase database = getDatabase();

    Document document = database.getCollection(ACCOUNTS_COLLECTION).find(Filters.eq("uuid", uuid)).first();

    if (document != null) {
      Account account = getAccountFromDocument(document);

      this.cachedAccounts.put(uuid, account);

      return account;
    }
    return null;
  }


  public void saveAccount(Account account) {
    MongoDatabase database = getDatabase();

    MongoCollection<Document> collection = database.getCollection(ACCOUNTS_COLLECTION);

    Document dbAccount = collection.find(Filters.eq("uuid", account.getUUID())).first();

    if (dbAccount != null) {
      collection.replaceOne(
          Filters.eq("_id", dbAccount.get("_id")),
          getDocument(account));
    } else {

      collection.insertOne(getDocument(account));
    }
  }


  private Document getDocument(Account account) {
    return new Document("uuid", account.getUUID())
               .append("username", account.getName())
               .append("kills", account.getKills())
               .append("deaths", account.getDeaths())
               .append("wins", account.getWins())
               .append("losses", account.getLosses())
               .append("nexus_damage", account.getNexus_damage());
  }


  private Account getAccountFromDocument(Document document) {
    return new Account(
        document.getString("uuid"),
        document.getString("username"),
        document.getInteger("kills"),
        document.getInteger("deaths"),
        document.getInteger("wins"),
        document.getInteger("losses"),
        document.getInteger("nexus_damage")
    );
  }


  private ConfigurationSection getConfigSection() {
    return this.plugin.getConfig().getConfigurationSection("Database");
  }
}
