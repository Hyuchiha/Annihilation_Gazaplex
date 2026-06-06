# Annihilation

Inspired by the classic **Annihilation** minigame from ShowBow. Originally built for **Gazaplex Network**, now open-source.

Four teams (Red, Blue, Green, Yellow) compete to destroy each other's Nexus. The last team with a standing Nexus wins.

**Supported versions:** 1.9 – 1.21.11 (Spigot) | **Author:** Hyuchiha

> **Server software:** built for **Spigot**. Runs on **Paper/Purpur ≤ 1.20.4** identically. On **Paper/Purpur 1.20.5+** the core game works but NMS-backed features degrade — see [Server Compatibility](#server-compatibility).

---

## Features

- 4-team PvP with Nexus destruction mechanics
- 15 selectable kits with unique abilities
- Map voting system with multi-arena support
- World restore on game finish
- Phase-based progression (5 phases, Phase 5 = double nexus damage)
- Zombie player replacement when players disconnect mid-game
- Virtual ender furnaces, brewing stands, and ender chests per team
- **Custom mob framework** (MC 1.18+, opt-in on 1.14–1.17): bosses, witches, guardians and zombies with scripted abilities — charge, potion barrage, fireball, sonic beam, wither-skull volleys, AoE stomp (see [Custom Mobs](#custom-mobs))
- Two-stage arena boss: **Wither** on first spawn, **Warden** (2× HP) on respawn for MC ≥ 1.19
- Visual kit-ability cooldowns via Minecraft's native item-cooldown overlay (MC 1.11+)
- In-game shop via sign interaction
- Statistics system (kills, deaths, wins, losses, nexus damage)
- Leaderboard commands
- BungeeCord support (return to hub on game end)
- Vault economy integration (money rewards per action)
- Custom MOTD with game state placeholders
- Anti-nuker protection near nexuses
- VIP perks (extra ender chest slots, late-join pass)
- Database: MySQL, SQLite, MongoDB — **HikariCP connection pooling** for SQL backends (see [Database Setup](#database-setup))
- Hot reload of YAML configs via `/anni reload` (no server restart)
- PlaceholderAPI integration — exposes player stats and live game state to scoreboards/chat plugins

---

## Requirements

No external plugin is **strictly** required — the plugin runs on a vanilla Spigot/Paper server with just the JVM. Every external integration is a soft-depend; if absent, the related feature degrades gracefully (money rewards become no-ops, packet helmets are skipped, PAPI placeholders aren't registered, etc.).

| Dependency | Type | Notes |
|---|---|---|
| Spigot (recommended) | Required | 1.9 – 1.21.11. Paper/Purpur work too — read [Server Compatibility](#server-compatibility) |
| Java 8+ | Required | Plugin compiled for Java 8 |
| MySQL server / SQLite driver / MongoDB server | Required | At least one database backend. SQLite is zero-config (uses the driver bundled with Spigot/CraftBukkit). |
| Vault | Soft | Money rewards; disabled if absent |
| Economy plugin (EssentialsX, CMI…) | Soft | Required alongside Vault for money to actually move |
| ProtocolLib | Soft | Team-colored helmets via packets |
| Multiverse-Core | Soft | Recommended for multi-world arena management |
| PlaceholderAPI | Soft | Enables `%annihilation_*%` placeholders for external plugins |

---

## Server Compatibility

The plugin is built for **Spigot's versioned NMS scheme** — every `net.minecraft` / `CraftBukkit` call lives in a version module (`v1_9_R1` … `v1_21_R7`) and the right one is picked at runtime by reading the server's CraftBukkit package name (`org.bukkit.craftbukkit.v1_21_R3` → `v1_21_R3`).

| Server | Result |
|---|---|
| **Spigot** (any 1.9 – 1.21.11) | ✅ Full support — every feature works. This is the target platform. |
| **Paper / Purpur ≤ 1.20.4** | ✅ Full support — package is still versioned, so detection succeeds (Paper's plugin-remapper deobfuscates the NMS bytecode at load). |
| **Paper / Purpur ≥ 1.20.5** | ⚠️ **Loads and the core game runs, but NMS-backed features silently disable.** |

**Why 1.20.5+ degrades on Paper/Purpur:** since 1.20.5 Paper [removed the versioned CraftBukkit package relocation](https://papermc.io/news/important-dev-psa-future-removal-of-cb-package-relocation/) — the package is now plain `org.bukkit.craftbukkit` with no `vX_Y_RZ` suffix. Runtime version detection (`Minecraft.Version.getVersion()`) parses that package name, so on Paper 1.20.5+ it resolves to `UNKNOWN`. Paper's plugin-remapper fixes obfuscated *symbols* but not this *string-based version parse*, so it can't rescue it.

**What still works on Paper/Purpur 1.20.5+:** team/arena/nexus logic, phases, scoreboards, kits, shop, voting, stats, database, PlaceholderAPI, BungeeCord, MOTD, ProtocolLib team helmets, and player respawn (falls back to `player.spigot().respawn()`).

**What disables (no-ops) on Paper/Purpur 1.20.5+:** the custom mob framework + legacy NMS mobs (so no charge/potion/skull abilities), the two-stage Wither/Warden boss spawn, the virtual Ender furnace and brewing stand, and the disconnect zombie-player.

> **Recommendation:** run on **Spigot** for full feature coverage on modern versions. If you require Paper/Purpur on 1.20.5+, expect the degraded set above until version detection is migrated off CraftBukkit package-name parsing (e.g. to `Bukkit.getMinecraftVersion()`).

---

## Installation

1. Download `Annihilation_v*.jar` and drop it in `plugins/`.
2. Start the server once — config files generate automatically.
3. Edit `plugins/Annihilation/config.yml`: configure your database and game settings.
4. Edit `plugins/Annihilation/maps.yml`: configure the lobby and at least one arena.
5. Restart the server.
6. Test with `/anni start` (requires op or `annihilation.command.start`).

---

## Configuration — `config.yml`

```yaml
# Seconds before Phase 1 begins after game start
start-delay: 120

# Duration of each phase in seconds (phases 1–4; phase 5 is endless)
phase-period: 600

# Seconds to wait after a team wins before resetting
restart-delay: 120

# Minimum online players to trigger auto-start
requiredToStart: 20

# Max block distance from a nexus where players can place/break blocks
build: 10

# Boss respawn delay (minutes) after being killed
bossRespawnDelay: 10

# Witch respawn delay (minutes) after being killed
witchRespawnDelay: 5

# Custom mob behaviors are always ON for MC 1.18+. On 1.14–1.17 the legacy NMS mobs
# are used by default; set this true to enable the custom mob framework there too
# (requires MC 1.14+; below 1.14 it stays off regardless). See "Custom Mobs".
enable-custom-mobs-legacy: false

# Players can join a team up to this phase number (0 = lobby only)
lastJoinPhase: 3

# Auto-end the game after a fixed wall-clock duration
ForceGameEnding: true
Force-end:
  hours: 2
  minutes: 00

# Show team name as chat prefix
useTeamPrefix: true

# Run console commands when a game ends
userCommandsOnFinish: false
commands:
  - "give %player% diamond 1"

# Send players to another BungeeCord server on game end
enableBungeeCommunication: false
Bungee:
  server: "lobby"
  item: "RED_BED"         # Item in inventory players click to return

# Database — uncomment ONE block
Database:
  type: "MySQL"
  host: "localhost"
  port: 3306
  name: "anni"
  user: "root"
  pass: "root"
  pool-size: 10          # Max HikariCP connections (MySQL only). See Database Setup.
#Database:
#  type: "SQLite"
#Database:
#  type: "MongoDB"
#  host: "localhost"
#  port: 27017
#  name: "anni"
#  user: "root"
#  pass: "root"

# Custom server list MOTD
enableMotd: true
motd: '&cPhase: &5%PHASE%'
motd-lobby: '&5Lobby'
motd-start: '&6Phase: &2%PHASE%'
# Placeholders: %PHASE% %TIME% %PLAYERCOUNT% %MAXPLAYERS%
#               %GREENNEXUS% %REDNEXUS% %BLUENEXUS% %YELLOWNEXUS%
#               %GREENCOUNT% %REDCOUNT% %BLUECOUNT% %YELLOWCOUNT%

# Notify players when they earn money (requires Vault)
showMoneyEarn: false

# Economy rewards (requires Vault + economy plugin)
Exp-player-kill: 20
Exp-boss-kill: 300
Money-nexus-hit: 5
Money-nexus-kill: 50
Money-player-kill: 30
Money-boss-kill: 80
Money-win: 150
```

---

## Configuration — `maps.yml`

Defines the lobby and all arena maps. Coordinates are written as `"x,y,z"` or `"x,y,z,yaw,pitch"` (all values as decimals, surrounded by quotes).

### Lobby (required)

```yaml
lobby:
  spawn: "0,64,0"          # Where players teleport while waiting
  signs:                   # Physical sign locations players click to join a team
    red:
      - "x,y,z"
    yellow:
      - "x,y,z"
    green:
      - "x,y,z"
    blue:
      - "x,y,z"
```

### Arena (add one block per arena, named freely)

```yaml
mymap:
  env: "NORMAL"            # NORMAL | NETHER | THE_END

  spawns:                  # Team spawn points (add as many as you want)
    red:
      - "x,y,z"
    yellow:
      - "x,y,z"
    green:
      - "x,y,z"
    blue:
      - "x,y,z"

  nexuses:                 # One nexus block per team (required)
    red: "x,y,z"
    yellow: "x,y,z"
    green: "x,y,z"
    blue: "x,y,z"

  # Optional features:
  furnaces:                # Virtual ender furnace per team
    red: "x,y,z"    # (repeat for yellow/green/blue)
  brewingstands:           # Virtual ender brewing stand per team
    red: "x,y,z"
  enderchests:             # Team ender chest per team
    red: "x,y,z"
  diamonds:                # Diamond resource spawn locations
    - "x,y,z"

  boss:
    world_spawn: "nether_world"   # Name of the world the boss lives in
    world_env: "NETHER"           # NORMAL | NETHER | THE_END
    hearts: 300
    boss_spawn: "x,y,z"
    boss_name: "&bBoss Name"
    teleports:                    # Blocks players step on to enter the boss world
      - "x,y,z"
    spawns:                       # Where each team spawns inside the boss world
      red: "x,y,z"
      yellow: "x,y,z"
      green: "x,y,z"
      blue: "x,y,z"
    chest: "x,y,z"               # Loot chest that appears when the boss dies

  witch:
    witch1:
      spawn: "x,y,z"
      name: "&eWitch"
      hearts: 100
    witch2:
      spawn: "x,y,z"
      name: ""
      hearts: 100
```

---

## Commands

| Command | Alias | Permission | Description |
|---|---|---|---|
| `/anni start` | `/w start` | `annihilation.command.start` | Start the pre-game countdown |
| `/anni stop` | `/w stop` | `annihilation.command.stop` | Force-stop the current game |
| `/anni reload` | `/w reload` | `annihilation.command.reload` | Reload all YAML configs without restarting (see [Hot Reload](#hot-reload)) |
| `/team <name>` | `/t` | — | Join a team by name (uses translated team names from `messages.yml`) |
| `/class <kit>` | `/c` | `annihilation.class.<kit>` | Select your kit |
| `/vote <map>` | `/v` | — | Vote for a map |
| `/stats [player]` | `/s` | — | View a player's statistics |
| `/top <kills\|deaths\|wins\|losses\|nexus>` | `/p` | — | View leaderboard |
| `/star` | `/st` | op | Give a wither star (testing only) |

> **Team command note:** The team name used in `/team` is the translated name from `messages.yml` (e.g. if Red is configured as "Rojo", use `/team rojo`).

---

## Permissions

```
annihilation.*                       Full access to everything
  annihilation.command.*             All admin commands
    annihilation.command.start       Start a game
    annihilation.command.stop        Stop a game
    annihilation.command.switch_kit  Switch kit via command
    annihilation.command.reload      Reload YAML configs at runtime
  annihilation.bypass.*              Bypass game restrictions
    annihilation.bypass.switch_team  Switch teams freely during game
    annihilation.bypass.team_limit   Join a full team
    annihilation.bypass.construction Build/break inside nexus protection zone
  annihilation.vip.*                 VIP perks
    annihilation.vip.diamond         Diamond-tier ender chest slots
    annihilation.vip.gold            Gold-tier ender chest slots
    annihilation.vip.iron            Iron-tier ender chest slots
    annihilation.vip.pass            Join past lastJoinPhase cutoff
  annihilation.class.*               All kits
    annihilation.class.acrobat
    annihilation.class.alchemist
    annihilation.class.archer
    annihilation.class.berserker
    annihilation.class.bloodmage
    annihilation.class.builder
    annihilation.class.defender
    annihilation.class.enchanter
    annihilation.class.miner
    annihilation.class.pyro
    annihilation.class.scorpio
    annihilation.class.scout
    annihilation.class.transporter
    annihilation.class.vampire
    annihilation.class.warrior
```

All permissions default to `op`.

---

## Kits

| Kit | Ability summary |
|---|---|
| Civilian | No abilities — default fallback |
| Archer | Bonus arrow damage; crafts extra arrows |
| Warrior | Enhanced melee |
| Acrobat | Reduced fall damage, mobility perks |
| Alchemist | Starts with potions; brewing bonuses |
| Berserker | Damage scales up as health drops |
| Bloodmage | Life steal on hit |
| Builder | Extended build distance near own nexus |
| Defender | Defensive bonuses near own nexus |
| Enchanter | Bonus enchanting access |
| Miner | Extra drops and XP from mining |
| Pyro | Fire-based attacks |
| Scorpio | Poison-based abilities |
| Scout | Speed and stealth perks |
| Transporter | Teleportation abilities |
| Vampire | Life steal on hit |

---

## Custom Mobs

Arena bosses, witches, the nexus guardian and disconnect-zombies run on a **pure-Bukkit custom mob framework** (no per-version NMS for the behavior itself — abilities use the stable PersistentDataContainer + Attribute APIs).

### Version gate

| MC version | Behavior |
|---|---|
| **≥ 1.18** | Always enabled. |
| **1.14 – 1.17** | Opt-in via `enable-custom-mobs-legacy: true` in `config.yml`. Default = legacy NMS mobs. |
| **< 1.14** | Always disabled (the framework identifies mobs via PersistentDataContainer, added in 1.14). |

When the framework is **active it fully replaces** the legacy NMS mob path (no double-spawn). When disabled, the plugin falls back to the legacy NMS creators / manual setup, so mobs still appear with basic behavior.

> Note: mob types that need an entity from a newer version still require that version — e.g. the **Warden** boss only spawns on MC ≥ 1.19. Where the entity doesn't exist, that specific mob is skipped (the Wither boss is used instead).

### Abilities

| Ability | Effect |
|---|---|
| Charge | Sprint-chase toward the target (disconnect zombie) |
| Potion Barrage | Throws a staggered volley of splash potions |
| Fireball | Launches small fireballs at range |
| Sonic Beam | Warden-style ranged beam damage |
| Wither Skull | Single homing wither skull |
| Wither Skull Barrage | Multi-skull volley |
| AoE Stomp | Area knockback + damage around the mob |

### Mob types

`WitherBoss`, `WardenBoss` (≥1.19, 2× HP second-stage boss), `CustomWitch`, `NexusGuardian`, `ArmoredZombie`, `DisconnectZombie`. HP and display names come from the per-arena `maps.yml` (`boss`/`witch` blocks). All custom mobs are wiped on game end and plugin disable.

---

## Game Flow

```
Players join → Lobby (waiting for requiredToStart players online)
    ↓  auto-triggered or /anni start
Countdown (start-delay seconds — map vote happens here)
    ↓
Phase 1 → Phase 2 → Phase 3 → Phase 4 → Phase 5 (no time limit)
  Each phase = phase-period seconds
  Phase 5 = double nexus damage
    ↓  last nexus standing (or ForceGameEnding timer)
Winner announced → restart-delay countdown → game resets
```

**Nexus:** A block each team must protect. Enemies can attack it in-game. Destroying an enemy nexus eliminates that team. The team with the last standing nexus wins.

**Zombie replacement:** When a player disconnects mid-game, a zombie spawns in their place. If they reconnect, the zombie is removed and they resume.

---

## Hot Reload

Run `/anni reload` (or `/w reload`) — works from console too — to re-read every YAML file (`config.yml`, `maps.yml`, `messages.yml`, `shops.yml`, `games.yml`, `kits.yml`) and refresh the in-memory caches.

**What reloads immediately:**

- All YAML caches (`Main.getConfig(...)` returns fresh values)
- Translated messages (`messages.yml`)
- Shop contents (`shops.yml`)

**What does NOT reload (and why):**

- **Game timer values** (`start-delay`, `phase-period`, `restart-delay`, `Force-end.*`) — cached at `Game` constructor time. Apply on the **next** game.
- **Map definitions** (`maps.yml`) — re-cached, but the running arena is not re-loaded. New maps appear in the next vote round.
- **Kit definitions** (`kits.yml`) — bound to the `Kit` enum class-init. A full kit reload requires a server restart.

If you reload while a game is running, you'll see a yellow warning telling you that timing/map values won't apply until the next round. Reload is otherwise safe to run at any time.

---

## PlaceholderAPI Integration

If [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) is installed, Annihilation auto-registers an expansion called `annihilation` exposing live game state and player stats. Detection is automatic — no extra config needed inside Annihilation. If PAPI is missing, the hook is silently skipped.

### How to install / configure for testing

1. Install PlaceholderAPI on your server (drop the jar in `plugins/`, restart).
2. Start the server with Annihilation. You'll see in the log:
   ```
   [Annihilation] PlaceholderAPI hook registered.
   ```
3. Verify the expansion is loaded:
   ```
   /papi list
   ```
   You should see `annihilation` in the list.
4. Test a placeholder in chat as a player:
   ```
   /papi parse me %annihilation_kills%
   /papi parse me %annihilation_team%
   /papi parse me %annihilation_phase%
   ```
   `parse me` resolves the placeholder against you, the sender. Use `/papi parse <other_player> <text>` to test against another player.
5. Use the placeholders in any plugin that supports PAPI (Featherboard, TAB, DeluxeChat, custom scoreboards, etc.).

### Available placeholders

**Player-scoped** (require an online player; return empty string otherwise):

| Placeholder | Returns |
|---|---|
| `%annihilation_team%` | Localized team name from `messages.yml` (e.g. `Red`, `Rojo`) |
| `%annihilation_team_raw%` | Enum constant: `RED` / `BLUE` / `GREEN` / `YELLOW` / `NONE` |
| `%annihilation_team_color%` | Chat color code of the team (e.g. `§c`) |
| `%annihilation_kit%` | Current kit name (e.g. `WARRIOR`) |
| `%annihilation_alive%` | `true` / `false` |
| `%annihilation_kills%` | Total kills (lifetime) |
| `%annihilation_deaths%` | Total deaths (lifetime) |
| `%annihilation_wins%` | Total wins |
| `%annihilation_losses%` | Total losses |
| `%annihilation_nexus_damage%` | Total nexus damage dealt |
| `%annihilation_kdr%` | kills / max(1, deaths), one decimal |

**Game-scoped** (no player needed):

| Placeholder | Returns |
|---|---|
| `%annihilation_phase%` | Current phase number (`0` if no game) |
| `%annihilation_state%` | `STARTING` / `PHASE_1` … `PHASE_5` / `RESTARTING` |
| `%annihilation_in_game%` | `true` / `false` |
| `%annihilation_map%` | Current arena name (empty if none selected) |
| `%annihilation_time_left%` | `mm:ss` until next phase / game end |
| `%annihilation_team_red_count%` | Red team member count (also `_blue`, `_green`, `_yellow`) |
| `%annihilation_team_red_alive_count%` | Red alive players (same suffix pattern) |
| `%annihilation_team_red_alive%` | `true` if Red's nexus is alive |
| `%annihilation_team_red_nexus_hp%` | Red nexus HP (`0` if not loaded) |

### Performance & safety

- Stat placeholders are **cache-only**: the expansion calls `Database.getCachedAccount(uuid)`, never triggers a DB query. Accounts are cached on join, so by the time a player is seen on a scoreboard the stats are local.
- If an account isn't yet cached (rare race window during join), stat placeholders return `"0"`.
- The expansion class is loaded lazily — if PAPI is absent, the class is never referenced and incurs no startup cost.

### Version compatibility

The PlaceholderAPI integration is **decoupled from Minecraft version**. It only depends on PlaceholderAPI's stable expansion API (`PlaceholderExpansion`), which has been stable across PAPI 2.10+. If the user's PAPI build is incompatible with the expansion class (e.g. a very old fork), `Main.hookPlaceholderAPI()` catches the failure and logs a warning — the rest of the plugin keeps working. So PAPI does **not** affect Annihilation's MC 1.9–1.21 compatibility matrix; it only affects whether the placeholders are available.

---

## Database Setup

The plugin caches every account in memory and only touches the DB on join (load), disconnect (save), and game end (bulk win/loss saves). SQL backends (MySQL, SQLite) use a **HikariCP connection pool** — no shared single connection, no manual locking — so the ~80–100 saves at game end run concurrently instead of serializing.

### MySQL (recommended for production / 80–100 players)
1. Create the schema first: `CREATE DATABASE anni;`
2. Set `Database.type: "MySQL"` and fill host/port/name/user/pass in `config.yml`.

Tables are created automatically with the right indexes (`PRIMARY KEY (uuid)` on accounts, FK-joined unlocked-kits table).

### SQLite (zero-config, single server)
No setup needed. Set `Database.type: "SQLite"`. File is saved to the plugin data folder. The pool is **fixed at size 1** — SQLite serializes writes at the file level, and a larger pool would just produce `database is locked` errors. Fine for a single server; use MySQL if you run multiple instances or expect heavy concurrency.

### MongoDB
Set `Database.type: "MongoDB"` and fill in host/port/name/user/pass.

### Connection pool tuning & recommendations

For SQL backends, HikariCP is configured with sensible defaults; the only knob exposed in `config.yml` is `pool-size` (MySQL only):

```yaml
Database:
  type: "MySQL"
  ...
  pool-size: 10     # max connections in the pool (default 10)
```

| Setting | Value | Notes |
|---|---|---|
| `pool-size` (max pool) | 10 (default) | Raise toward 15–20 for 100+ players **only if** your MySQL `max_connections` allows it and you see saves queueing at game end. More is not better — past the DB's CPU/core count it adds contention. |
| min idle | 2 | Fixed |
| connection timeout | 10 s | Fixed |
| idle timeout | 10 min | Fixed |
| max lifetime | 30 min | Fixed — keep below your MySQL `wait_timeout` |

**Recommendations for an optimized setup:**

- **Co-locate** the MySQL server with the Minecraft server (same host / LAN). A save is a single round-trip; latency dominates, so a remote DB across the internet hurts most.
- Keep MySQL's `max_connections` comfortably above `pool-size × (number of Annihilation servers)`.
- Use **InnoDB** (the auto-created tables already do) for row-level locking under concurrent saves.
- **Runtime requirement — SLF4J:** HikariCP needs `slf4j-api` on the classpath. Spigot 1.19.4+ and modern Paper/Purpur bundle it. On older servers, drop `slf4j-api.jar` into `plugins/lib/` (or the server's `lib/`) or you'll see a `NoClassDefFoundError` from Hikari at startup.
- HikariCP is shaded and relocated to `com.hyuchiha.hikari` so it won't clash with other plugins that bundle their own copy.

---

## Arena Setup Checklist

- [ ] Create or import the arena world (Multiverse-Core recommended)
- [ ] Fill `maps.yml` with all coordinates (spawns, nexuses, signs, etc.)
- [ ] Place physical sign blocks at the exact locations listed in `lobby.signs`
- [ ] Confirm the world name in `maps.yml` matches the actual loaded world
- [ ] Restart the server
- [ ] Run `/anni start` to test

---

## Building from Source

### Prerequisites

1. **Maven 3.6+** and **Java 8+**
2. **Spigot NMS dependencies** installed in your local Maven repository via BuildTools (for NMS modules)
3. **ReflectionHelper** — compiled locally from updated source

### Spigot NMS jars (BuildTools)

Each version module compiles against the `remapped-mojang` Spigot artifact for its Minecraft version, produced by BuildTools with `--remapped`. Install every version you want to support into your local `~/.m2`:

```bash
# one per supported revision — example for the newest four added in 1.6.0
java -jar BuildTools.jar --rev 1.21.5  --remapped   # → v1_21_R4
java -jar BuildTools.jar --rev 1.21.8  --remapped   # → v1_21_R5
java -jar BuildTools.jar --rev 1.21.10 --remapped   # → v1_21_R6
java -jar BuildTools.jar --rev 1.21.11 --remapped   # → v1_21_R7
```

Building 1.20.5+ requires **JDK 21**. A revision whose jar is missing from `~/.m2` will fail the reactor at that module.

### Installing ReflectionHelper (required before first build)

This project uses an updated build of ReflectionHelper (**1.21.11-SNAPSHOT**, pinned in the parent `pom.xml`) compiled locally — its `Minecraft.Version` enum must include the `v1_21_R4`…`v1_21_R7` constants:

```bash
# Clone and build ReflectionHelper
git clone https://github.com/InventivetalentDev/ReflectionHelper.git
cd ReflectionHelper
mvn clean install -DskipTests
```

This installs ReflectionHelper to your local Maven repository (`~/.m2/repository/`). The Annihilation build will find it automatically.

> **Note for teams:** Each developer needs to run this once on their machine before building Annihilation.

### Building Annihilation

```bash
# Full build (all 31 modules + final shaded JAR)
mvn clean package -DskipTests
# If a forked compiler/remap JVM crashes on low heap, bump it:
#   MAVEN_OPTS="-Xmx2g" mvn clean package -DskipTests

# Output JAR location
AnnihilationPlugin/target/Annihilation_v1.6.0.jar
```

### Quick rebuild (after making code changes)

```bash
# Build only the main plugin module (faster, assumes NMS modules already installed)
mvn package -pl AnnihilationPlugin -DskipTests
```
