# Annihilation

Inspired by the classic **Annihilation** minigame from ShowBow. Originally built for **Gazaplex Network**, now open-source.

Four teams (Red, Blue, Green, Yellow) compete to destroy each other's Nexus. The last team with a standing Nexus wins.

**Supported versions:** 1.9 – 1.21 | **Author:** Hyuchiha

---

## Features

- 4-team PvP with Nexus destruction mechanics
- 15 selectable kits with unique abilities
- Map voting system with multi-arena support
- World restore on game finish
- Phase-based progression (5 phases, Phase 5 = double nexus damage)
- Zombie player replacement when players disconnect mid-game
- Virtual ender furnaces, brewing stands, and ender chests per team
- Custom Wither boss and Witches per arena
- In-game shop via sign interaction
- Statistics system (kills, deaths, wins, losses, nexus damage)
- Leaderboard commands
- BungeeCord support (return to hub on game end)
- Vault economy integration (money rewards per action)
- Custom MOTD with game state placeholders
- Anti-nuker protection near nexuses
- VIP perks (extra ender chest slots, late-join pass)
- Database: MySQL, SQLite, MongoDB
- Hot reload of YAML configs via `/anni reload` (no server restart)
- PlaceholderAPI integration — exposes player stats and live game state to scoreboards/chat plugins

---

## Requirements

No external plugin is **strictly** required — the plugin runs on a vanilla Spigot/Paper server with just the JVM. Every external integration is a soft-depend; if absent, the related feature degrades gracefully (money rewards become no-ops, packet helmets are skipped, PAPI placeholders aren't registered, etc.).

| Dependency | Type | Notes |
|---|---|---|
| Spigot / Paper / Purpur | Required | 1.9 – 1.21 |
| Java 8+ | Required | Plugin compiled for Java 8 |
| MySQL server / SQLite driver / MongoDB server | Required | At least one database backend. SQLite is zero-config (uses the driver bundled with Spigot/CraftBukkit). |
| Vault | Soft | Money rewards; disabled if absent |
| Economy plugin (EssentialsX, CMI…) | Soft | Required alongside Vault for money to actually move |
| ProtocolLib | Soft | Team-colored helmets via packets |
| Multiverse-Core | Soft | Recommended for multi-world arena management |
| PlaceholderAPI | Soft | Enables `%annihilation_*%` placeholders for external plugins |

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

### MySQL (recommended for production)
Create the schema first: `CREATE DATABASE anni;`  
Then configure `Database.type: "MySQL"` in `config.yml`.

### SQLite (zero-config, single server)
No setup needed. Set `Database.type: "SQLite"`. File is saved to the plugin data folder.

### MongoDB
Set `Database.type: "MongoDB"` and fill in host/port/name/user/pass.

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

### Installing ReflectionHelper (required before first build)

This project uses an updated version of ReflectionHelper (1.21.4-SNAPSHOT) compiled locally:

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
# Full build (all 26 modules + final shaded JAR)
mvn clean package -DskipTests

# Output JAR location
AnnihilationPlugin/target/Annihilation_v1.6.0.jar
```

### Quick rebuild (after making code changes)

```bash
# Build only the main plugin module (faster, assumes NMS modules already installed)
mvn package -pl AnnihilationPlugin -DskipTests
```
