# Pending — Technical Debt & Improvement Backlog

Análisis orientado a un servidor con 80–100 jugadores simultáneos. Los items están organizados por nivel de prioridad: de lo que puede crashear o causar degradación seria a lo que es mejora de calidad de vida.

> **Última verificación:** 2026-05-09 — re-análisis completo del código contra la lista previa.
> **Última iteración de fixes:** 2026-05-09 — BUG-7, BUG-8, BUG-9, BUG-11, BUG-12, BUG-1b, BUG-2 (resto), SCALE-5 (resto).
> **Iteración 2 de fixes:** 2026-05-09 — BUG-10, SCALE-1, SCALE-2, SCALE-3, SCALE-4, SCALE-6.
> **Iteración 3 (double-check):** 2026-05-09 — corregidas 3 regresiones detectadas en review:
>   - SCALE-3: jugadores que se unen mid-game no recibían BossBar por el cache → expuesto `invalidateBossBarCache()` invocado desde `JoinListener`.
>   - SCALE-1: `memberCount()` podía contar UUIDs huérfanos por crash sin handleDisconnect → agregado `onlineMemberCount()` para signs.
>   - SCALE-6: async save en `handleDisconnect` arriesgaba pérdida de datos en shutdown del plugin → revertido a sync; solo `canEndGame` (80 saves al cerrar partida) permanece async donde el ahorro real está.
> **Iteración 4 de fixes:** 2026-05-09 — QUALITY-1, QUALITY-2b (resto), QUALITY-5, QUALITY-6, QUALITY-8, QUALITY-9, QUALITY-10, QUALITY-12, QUALITY-13, SCALE-7.
> **Iteración 5 de fixes:** 2026-05-10 — FEATURE-1 (`/anni reload`) y FEATURE-2 (PlaceholderAPI hook).
> **Iteración 4 (double-check):** 2026-05-09 — review encontró 1 fix menor (comentario obsoleto en `QuitListener.handleDisconnect` que mencionaba el lock de SQLDB ya removido; actualizado para describir la nueva realidad con Hikari). Confirmado:
>   - `QUALITY-1`: defaults son sensatos; el único cambio de comportamiento para servers mal-configurados es `build` (radio anti-construcción) que pasa de 0→30. Sensato como default.
>   - `QUALITY-8`: reordenamiento de `endGame` correcto. La ventana breve con scoreboard vacía existe igual que antes (entre resetScoreboard y `VotingManager.start`).
>   - `QUALITY-2b/9`: null guards son defensivos, no alteran comportamiento en runs normales.
>   - `ZombieListener`: simplificación `meta.getTeam().getNexus().getTeam()` → `meta.getTeam()` semánticamente equivalente (Nexus.getTeam siempre devuelve la misma GameTeam que la dueña), pero NPE-safe.
>   - `SCALE-7` HikariCP: lifecycle de init/close revisado; partial-init failures y shutdown manejan correctamente. **Requisito**: SLF4J-api en classpath en runtime — Spigot 1.19.4+ y Paper modernos lo proveen. Para servers más antiguos puede requerir agregar `slf4j-api.jar` a `lib/`.
>   - `BossManager.loadItem`: detectado bug pre-existente — al setear `meta` faltaba `item.setItemMeta(meta)` así que displayName/lore nunca se aplicaban. Mi fix de QUALITY-9 también resuelve esto como side-effect.

---

## Nivel 1 — Crítico: Bugs que crashean o corrompen datos

### ~~[BUG-1] `PlayerManager.players` nunca se limpia al desconectarse~~ ✅ FIXED
**Archivo:** `Manager/PlayerManager.java:128`, `Listener/QuitListener.java`
**Estado:** Cleanup completo extraído a `handleDisconnect(Player)` — ahora se ejecuta tanto en `onQuit()` como en `onKick()`. Cubre `PlayerManager.removePlayer`, `TimersUtils.clearPlayer`, `FastBreakProtect.clearPlayer`, `SignManager.updateSigns`, `PlayerSerializer` y `ZombieManager`.

---

### ~~[BUG-2] `GamePlayer.getPlayer()` puede retornar null — NPE en muchos lugares~~ ✅ FIXED
**Archivo:** `Game/GamePlayer.java`
Todos los métodos públicos hacen `Player player = getPlayer(); if (player == null) return;` al inicio: `addXp`, `giveOreXP`, `giveOreDrops`, `setupPlayerData`, `prepareLobbyPlayer`, `preparePlayerForGame`, `regamePlayer`. El runnable diferido del bossbar también valida `player.isOnline()` antes de usar la referencia capturada.

---

### ~~[BUG-3] `Database.getAccount()` carga de DB pero no cachea el resultado~~ ✅ FIXED
**Archivo:** `Database/Base/Database.java:108-114` — confirmado: `cachedAccounts.put(loadedAccount.getUUID(), loadedAccount)` está presente.

---

### ~~[BUG-4] `AsyncPlayerPreLoginEvent` accede a estado compartido sin sincronización~~ ✅ FIXED
**Archivo:** `Listener/JoinListener.java:121-144`
Snapshot de `Game game = GameManager.getCurrentGame()` al inicio + `volatile Game currentGame` en `GameManager.java:14`.

---

### ~~[BUG-5] `GamePlayer.setTeam()` tiene lógica de null invertida~~ ✅ FIXED
**Archivo:** `Game/GamePlayer.java:53-55` — `this.team = (t != null) ? t : GameTeam.NONE;`

---


---

### ~~[BUG-7] `PlayerListener.onPlayerDeath` da XP/dinero al jugador muerto~~ ✅ FIXED
**Archivo:** `Listener/PlayerListener.java:89-101`
`gpKiller`, `calculateVipMoneyGive` y `addMoney` ahora reciben `killer` en lugar de `player`.

---

### ~~[BUG-8] `GameListener.onNexusDestroy` carga la cuenta del breaker en vez de la víctima~~ ✅ FIXED
**Archivo:** `Listener/GameListener.java:138-142`, `Database/Base/Account.java:127`
La cuenta cargada es la del jugador iterado (víctima del equipo perdedor). El método se renombró a `increaseLosses()` en `Account` y se actualizó la única llamada.

---

### ~~[BUG-9] Loop infinito en `InteractListener.onItemInteract` con la brújula (COMPASS)~~ ✅ FIXED
**Archivo:** `Listener/InteractListener.java:48-77`
Condición ahora es `while (!setCompass && count < 6)`. Adicionalmente, si la pasada no encontró ningún equipo coincidente con el display name actual, fuerza `setToNext = true` para que la próxima iteración rote a `RED`. También agregué guard de `team.getNexus() != null` antes de `setCompassTarget`.

---

### ~~[BUG-10] `BlockListener.onBuild` cancela la construcción al jugador con permiso de bypass~~ ✅ FIXED
**Archivo:** `Listener/BlockListener.java:41-45`
La condición ahora es `if (tooClose && !hasPermission(bypass.construction))`.

---

### ~~[BUG-11] `ResourceListener.breakResource` NPE si se rompe el recurso con la mano vacía~~ ✅ FIXED
**Archivo:** `Listener/ResourceListener.java:138,153-167`
Se agregó `itemInHand != null` al check de looting. El bloque de restauración de durabilidad también se envuelve completo en `if (itemInHand != null)`.

---

### ~~[BUG-12] SQL injection latente en `addUnlockedKit`~~ ✅ FIXED
**Archivo:** `Database/Databases/SQLDB.java`
Se migraron a `PreparedStatement` con bind parameters: `addUnlockedKit` (uuid + idKit), `getIdOfElement` (kit name), y `getKitsFromAccount` (uuid). De paso se corrigió el JOIN sin `ON` en `getKitsFromAccount`.

---

## Nivel 2 — Memory Leaks confirmados

### ~~[LEAK-1] `TimersUtils.kitDelays` nunca se limpia al quit~~ ✅ FIXED
`TimersUtils.clearPlayer()` ahora se llama desde `handleDisconnect()` (cubre quit y kick).

---

### ~~[LEAK-2] `ZombieManager` usa nombre de jugador como clave~~ ✅ FIXED
Ahora usa `player.getUniqueId().toString()` (`ZombieManager.java:112-114`) consistente con el resto.

---

### ~~[LEAK-3] `FastBreakProtect` usa nombre de jugador como clave y no se limpia al quit~~ ✅ FIXED
Cambiado a UUID. `clearPlayer()` se llama desde `handleDisconnect()` (cubre quit y kick).

---

### ~~[BUG-1b / LEAK-4] `QuitListener.onKick` no hace cleanup~~ ✅ FIXED
**Archivo:** `Listener/QuitListener.java`
La lógica completa de cleanup (`SignManager.updateSigns`, save/uncache de Account, `TimersUtils.clearPlayer`, `FastBreakProtect.clearPlayer`, `PlayerSerializer`, `ZombieManager.createZombiePlayer`, `PlayerManager.removePlayer`) se extrajo al método privado `handleDisconnect(Player)`, invocado tanto desde `onQuit()` como desde `onKick()`.

---

## Nivel 3 — Escalabilidad para 80–100 jugadores

### ~~[SCALE-1] `GameTeam.getPlayers()` es O(n) sobre todos los jugadores online~~ ✅ FIXED
**Archivo:** `Game/GameTeam.java`
Cada `GameTeam` mantiene un `Set<UUID> members` (ConcurrentHashMap.newKeySet) actualizado transaccionalmente desde `GamePlayer.setTeam`. `getPlayers()` ahora es O(team_size) y `getPlayersAlive()` itera una sola vez (también resuelve QUALITY-3). Limpieza en `handleDisconnect` (per-jugador) y `Game.endGame` (`team.restartMembers()` defensivo). Para conteo en signs se usa `onlineMemberCount()` que filtra UUIDs huérfanos (post-crash).

---

### ~~[SCALE-2] `SignManager.updateSigns()` llama `getPlayers()` cada segundo~~ ✅ FIXED
**Archivos:** `Game/GameTimer.java`, `Manager/SignManager.java`, `Game/Game.java`, `Listener/GameListener.java`, `Listener/QuitListener.java`
- Eliminado el `SignManager.updateSigns()` per-tick en `GameTimer.onSecond()`.
- `updateIndividualSign` ahora cachea las 4 líneas renderizadas por equipo y skip-block-state si nada cambió.
- Reemplazado `getPlayers().size()` por `memberCount()` (O(1)) y eliminada la doble iteración.
- `Game.joinTeam`, `GameListener.onNexusDamage`, `QuitListener.handleDisconnect` ahora invocan `updateIndividualSign(team)` en lugar de `updateSigns()` (4 equipos).

---

### ~~[SCALE-3] BossBar actualiza todos los jugadores cada segundo con string recalculado~~ ✅ FIXED
**Archivos:** `Game/GameTimer.java`, `Listener/JoinListener.java`
`sendRemainingTime()` cachea el último `text` y `percent` enviados; si ambos son idénticos a la iteración previa, no broadcast. La cache se invalida en `stop()`. **Importante:** `invalidateBossBarCache()` se invoca desde `JoinListener.onPlayerJoin` (cuando phase > 0) para que un jugador que se une durante una partida sí reciba el BossBar al siguiente tick. (Nota: como mm:ss avanza cada segundo durante el juego, el ahorro real está en transiciones donde el render produce el mismo texto.)

---

### ~~[SCALE-4] `ScoreboardManager.updatePlayerScoreboard()` actualiza a todos~~ ✅ FIXED
**Archivos:** `Scoreboard/ScoreboardManager.java`, `Game/GamePlayer.java`, `Listener/JoinListener.java`
Sobrecarga `updatePlayerScoreboard(Player p)` que actualiza solo a un jugador. Call sites por-jugador (`prepareLobbyPlayer`, `JoinListener.rejoinPlayer`) usan la nueva sobrecarga. La versión sin parámetros queda para transiciones de game start/end donde sí se necesita broadcast.

---

### ~~[SCALE-5] `new Random()` creado por llamada~~ ✅ FIXED (hot paths)
- `GamePlayer.giveOreXP` → `ThreadLocalRandom`
- `GameTeam.getRandomSpawn` → `ThreadLocalRandom`
- `BossManager.getRandomItem` y `spawnLootChest` → `ThreadLocalRandom`
- `ResourceListener.quantityDroppedWithBonus` → `ThreadLocalRandom`

Quedan instancias `new Random()` que son **fields reutilizados** (no se crean por llamada): `GameListener.random`, `ResourceListener.rand`, kits (Vampire/Alchemist/Pyro/etc.), `FireworkUtils`, `ChestUtils`, `WitchListener`. Esos son aceptables; no requieren cambio.

---

### ~~[SCALE-6] Operaciones de base de datos en el hilo principal~~ ✅ FIXED (parcial)
**Archivos:** `Listener/QuitListener.java`, `Manager/GameManager.java`, `Database/Databases/SQLDB.java`, `Database/Base/Database.java`
- `GameManager.canEndGame`: el loop de win/loss saves pasa a async — 80 saves se serializan en el lock de SQLDB pero ya no bloquean ticks. Se ejecutan durante la fase RESTARTING que dura ~30s, suficiente para que terminen.
- Las operaciones JDBC en `SQLDB` ya no requieren `synchronized` — el pool de HikariCP gestiona la concurrencia (ver SCALE-7). Cada operación toma una connection del pool, la usa con try-with-resources, y la devuelve.
- `cachedAccounts` migrado a `ConcurrentHashMap` para puts/removes seguros desde hilo async.

**Decisión deliberada — `handleDisconnect` mantiene save SYNC:** un save async aquí arriesga pérdida de datos si el plugin se desactiva entre el schedule y la ejecución (Bukkit cancela tareas async pendientes en `onDisable`). Con un solo save por desconexión, el costo sync es aceptable (~50ms MySQL roundtrip).

**Aún sync (por correctness):** `createAccount` en join, `addUnlockedKit` desde el menú de UI (el jugador necesita ver el kit unlocked inmediatamente), `getAccount` en lugares que leen stats.

---

### ~~[SCALE-7] `SQLDB` — conexión SQL compartida no es thread-safe~~ ✅ FIXED
**Archivos:** `Database/Databases/SQLDB.java`, `Database/Databases/MySQLDB.java`, `Database/Databases/SQLiteDB.java`, `pom.xml`

Reemplazado el modelo de `Connection` única + `synchronized` por un pool de **HikariCP 4.0.3** (último 4.x con soporte Java 8):
- Agregada dependencia `com.zaxxer:HikariCP:4.0.3` en `pom.xml` con relocation `com.zaxxer.hikari` → `com.hyuchiha.hikari` para evitar choques con otros plugins que también shadeen Hikari.
- `SQLDB` ahora tiene un `HikariDataSource dataSource`. Cada método DB hace `try (Connection conn = dataSource.getConnection())` y libera la conexión al pool al salir del bloque. Try-with-resources garantiza el cierre incluso en path de excepción.
- Eliminados todos los `synchronized` y el manual ping task — Hikari se encarga de keep-alive, validation, y concurrencia. Esto elimina la serialización del lock que era el bottleneck principal: ahora 80 saves concurrentes pueden usar hasta `pool-size` connections en paralelo (hasta el límite del DB server).
- `MySQLDB.buildHikariConfig` — pool size configurable vía `pool-size` (default 10), idle/max-lifetime razonables. Driver explícito `com.mysql.jdbc.Driver`.
- `SQLiteDB.buildHikariConfig` — pool size 1 (SQLite serializa writes a nivel de archivo; pool más grande causaría "database is locked"). Driver `org.sqlite.JDBC`.
- Statements/ResultSets ahora SIEMPRE se cierran via try-with-resources. El código original tenía leaks en INSERT/UPDATE statements (cerraba solo cuando `execute()` retornaba `true`, lo cual nunca pasa para INSERT/UPDATE).

**Requisito de runtime:** SLF4J-api en classpath. Spigot 1.19.4+ y Paper modernos lo proveen. Servers más antiguos pueden necesitar agregar `slf4j-api.jar` a `plugins/lib/` o `lib/`.

**Pendiente (BUG-12 extendido):** `getCreateAccountQuery` y `getUpdateAccountQuery` en `MySQLDB`/`SQLiteDB` siguen construyendo SQL con string-concat (vulnerable si en el futuro un username contiene un quote). Migrar a PreparedStatement con bind requiere cambiar el contrato del método abstracto.

---

## Nivel 4 — Mejoras de calidad y mantenimiento

### ~~[QUALITY-1] Valores de config sin defaults~~ ✅ FIXED
Defaults aplicados en `Main.initDatabase` (`Database.type` → "SQLite" + fallback en default del switch + null guard en `onDisable`), `Game.java` (timing), `GameManager.java` (`requiredToStart` → 4), `PlayerListener.java` (Exp/Money de kill), `GameListener.java` (Money nexus hit/kill), `JoinListener/MotdListener/PlayerSerializer.java` (`lastJoinPhase` → 3), `ScoreboardManager.java` (`useTeamPrefix` → false), `PlayerManager.java` (`showMoneyEarn` → false), `GameUtils.java` (`build` → 30).

---

### ~~[QUALITY-2] `GameTeam.isTeamAlive()` puede lanzar NPE si el nexus no está cargado~~ ✅ FIXED
`GameTeam.java:155-157` — `nexus != null && nexus.isAlive()`.

---

### ~~[QUALITY-2b] Otros accesos a `nexus` sin null-check~~ ✅ FIXED
Null guards agregados en:
- `Game.getForcedWinner()` — calcula HP=0 si el nexus no está cargado.
- `PlayerListener.onPlayerDeath` — usa `team.isTeamAlive()` (que ya hace null-check) en lugar de `team.getNexus().isAlive()`.
- `BlockListener.onBreakBlockNexus` — `continue` si nexus es null.
- `ScoreboardManager.createInGameScoreboard/updateInGameScoreboard` — fallback a 0 HP.
- `Kits/Implementations/Defender.java` — early-return en compass-jump y applyHearts.
- `Kits/Base/BaseKit.java` — guard antes de setCompassTarget.
- `Utils/GameUtils.tooClose` — `continue` si nexus es null.
- `Listener/InteractListener.java` (compass) — ya tenía guard de la iteración previa.
- `Listener/ZombieListener.java` — eliminada la cadena `getNexus().getTeam()` (era equivalente a `getTeam()` y vulnerable a NPE).

---

### ~~[QUALITY-3] `GameTeam.getPlayersAlive()` hace doble iteración~~ ✅ FIXED
Resuelto en SCALE-1: ahora itera el `Set<UUID>` una sola vez incrementando un contador.

---

### ~~[QUALITY-4] `GamePlayer.giveOreXP()` crea `new Random()` por llamada~~ ✅ FIXED
Migrado a `ThreadLocalRandom` (`GamePlayer.java:152`).

---

### ~~[QUALITY-5] Mensajes de log mezclando inglés y español~~ ✅ FIXED
- `BossManager.loadItem` — comentarios en español traducidos a inglés (potion duration block).
- Logs de debug ruidosos eliminados: `BossManager.loadItem` ("Material:...-ToFound"), `ResourceManager.getDropMaterial`, `ResourceListener` ("Item in hand: ..."), `JoinListener.rejoinPlayer` ("Checking for zombie:" + "Zombie successfully removed!"), `SQLDB.loadAccount` ("Loading account with uuid:").
- Imports `Output` ahora innecesarios eliminados de `JoinListener` y `ResourceListener`.

---

### ~~[QUALITY-6] `geDelayRemaining` — typo en nombre de método~~ ✅ FIXED
Renombrado a `getDelayRemaining` en `TimersUtils.java` y actualizado el único caller en `KitUtils.java`.

---

### ~~[QUALITY-7] `Database.close()` usa `Collection` directa durante iteración~~ ✅ FIXED
`Database.java:158-160` — `new ArrayList<>(this.cachedAccounts.values()).forEach(...)`.

---

### ~~[QUALITY-8] Trabajo duplicado de scoreboard en startGame/endGame~~ ✅ FIXED
**Archivo:** `Game/Game.java`
Reordenado `endGame()` para llamar `resetScoreboard()` ANTES del loop de `prepareLobbyPlayer`. De esta forma cada `prepareLobbyPlayer` bindea al scoreboard nuevo via la sobrecarga `updatePlayerScoreboard(Player)` (introducida en SCALE-4) y se elimina el broadcast redundante posterior. `startGame()` ya estaba bien (un solo broadcast antes del loop, los `preparePlayerForGame` no hacen rebind).

---

### ~~[QUALITY-9] Métodos que se asumen no-null pero no validan~~ ✅ FIXED
- `GameListener.onNexusDamage` — `breakerPlayer = breaker.getPlayer()` se captura una vez con early-return si null. `getAccount` también null-checked.
- `GameListener.onNexusDestroy` — idem; `victimData` null-check antes de `increaseLosses()`.
- `Kit.java` (constructor) — guard de `meta != null` antes de `setDisplayName` + `setItemMeta`.
- `BossManager.loadItem` — guard de `meta != null` y `setItemMeta(meta)` agregado para que el cambio se persista.

---

### ~~[QUALITY-10] `GameManager.canEndGame` y `forceStopGame` mezclan lectura/escritura DB en un loop~~ ✅ FIXED
**Archivo:** `Manager/GameManager.java`
Ambos loops ahora dispatchan `saveAccount` con `runTaskAsynchronously`. Los `getAccount` permanecen sync porque son cache hits en steady state. Null guards agregados en ambos métodos.

---

### ~~[QUALITY-11] `Game.joinTeam` recibe nombre localizado~~ ✅ FIXED
**Archivo:** `Game/Game.java:78`
Corregido a `team.name()` — ahora usa el enum name (RED, YELLOW, GREEN, BLUE) en lugar del nombre localizado. Los nombres traducidos en `messages.yml` son solo para display (UI), no para lógica interna.

---

### ~~[QUALITY-12] `Game.canEndGame` permite condición igual a 0 (todos muertos)~~ ✅ FIXED
**Archivo:** `Manager/GameManager.canEndGame`
Guard agregado: si `winner == null || winner == NONE`, log de warning y skip de win/loss bookkeeping. Account-null guards también para evitar NPE.

---

### ~~[QUALITY-13] `InventoryListener.onInvClose` no valida que el juego esté activo~~ ✅ FIXED
**Archivo:** `Listener/InventoryListener.java`
Guards agregados: cast `instanceof Player` + null check de `GameManager.getCurrentGame()`.

---

## Nivel 5 — Features pendientes / ideas documentadas

### ~~[FEATURE-1] Recarga de configuración en caliente~~ ✅ FIXED
**Archivos:** `Commands/AnnihilationCommand.java`, `Commands/AnnihilationTabCompletion.java`, `Config/ConfigManager.java`, `Messages/Translator.java`, `Main.java`, `plugin.yml`, `messages.yml`
- `/anni reload` recarga todos los YAML del cache de `ConfigManager` (`reloadAll()`).
- `Translator.reload()` limpia y re-lee `messages.yml`.
- `ShopManager.clearShops() + initShops()` re-leen `shops.yml`.
- Permiso `annihilation.command.reload` (default: op).
- El comando es runnable desde la consola (helper `hasCommandPermission` evita CCE en sender no-Player).
- **Limitaciones documentadas (visibles vía `INFO.RELOAD_GAME_RUNNING`):** los timings de `Game` se cachean en el constructor — los cambios en `config.yml` (`start-delay`, `phase-period`, `restart-delay`, `Force-end.*`) sólo aplican al siguiente `Game` (próxima ronda). `maps.yml` se re-cachea en memoria pero la `MapManager` no re-carga el arena en juego — los cambios aplican al siguiente sorteo. `kits.yml` está atado al class-init del enum `Kit`; un reload completo requeriría re-walk del enum, fuera de scope.

### ~~[FEATURE-2] Placeholder API (PAPI) para stats en scoreboards externos~~ ✅ FIXED
**Archivos:** `Hooks/AnnihilationExpansion.java` (nuevo), `Main.java`, `Database/Base/Database.java`, `Game/GameTimer.java`, `pom.xml`, `plugin.yml`
- Soft-depend `PlaceholderAPI` añadido en `plugin.yml` y dependencia `me.clip:placeholderapi:2.11.6` (provided) en `pom.xml`.
- `AnnihilationExpansion` extiende `PlaceholderExpansion`. Se registra en `hookPlaceholderAPI()` solo si PAPI está cargado; falla silenciosa si la API no es compatible.
- **Player placeholders:** `team`, `team_raw`, `team_color`, `kit`, `alive`, `kills`, `deaths`, `wins`, `losses`, `nexus_damage`, `kdr`.
- **Game placeholders:** `phase`, `state`, `in_game`, `map`, `time_left`, `team_<color>_count`, `team_<color>_alive_count`, `team_<color>_alive`, `team_<color>_nexus_hp`.
- Las stats usan `Database.getCachedAccount(uuid)` (nuevo método cache-only) — **no dispara IO** desde el render de PAPI; si el account no está cacheado retorna "0".
- `GameTimer.getRemainingTime()` se hizo público para soportar `time_left`.

### [FEATURE-3] Soporte a múltiples mundos de boss por arena
Un solo boss/boss-world por arena.

### [FEATURE-4] Registro de kits desbloqueables persistido en DB
El sistema de "kit unlocking" existe (`addUnlockedKit()` en Database) pero el método `Kit.resetKit()` está vacío (`Kit.java:121-123`) — verificar persistencia entre sesiones.

### ~~[FEATURE-5] Cooldown visible para habilidades de kit~~ ✅ FIXED
**Archivos:** `Utils/KitUtils.java`, 8 kits en `Kits/Implementations/`
Implementado vía el item-cooldown nativo de Minecraft (`Player.setCooldown(Material, ticks)`):
- Helper `KitUtils.applyKitCooldown(player, material, seconds)` con guard de versión (`Minecraft.Version.olderThan(v1_11_R1)` → no-op silencioso para 1.9/1.10).
- Cableado en `Defender` (60s), `Archer` (45s), `Scout` (1s), `Pyro` (40s), `Scorpio` (30s), `Alchemist` (90s), `Bloodmage` (60s), `Builder` (90s). Cada uno muestra el overlay gris animado en el slot del item del kit usando `handItem.getType()`.
- `Acrobat` se omitió: su ability (double-jump) es pasiva al volar, no hay item clickeable donde mostrar el cooldown. El sonido `ENTITY_ZOMBIE_INFECT` ya funciona como feedback de activación.
- El feedback complementario (`KitUtils.showKitItemDelay` → ActionBar + sound al click-while-cooling-down) ya existía y se conserva.

**Bonus — bugs pre-existentes encontrados durante el audit y corregidos:**

1. **`Bloodmage.java:132` lógica invertida en check de item.** `if (handItem == null || isKitItem(...))` retornaba si era el item del kit. La habilidad solo disparaba con cualquier OTRO item en mano. Corregido a `... || !isKitItem(...)`.

2. **`Bloodmage.java:136-138` cooldown completamente roto.** El bloque `if (!hasExpired) { showKitItemDelay; }` no tenía `return`, así que el wither + heart-drain se ejecutaban en cada click independiente del cooldown. El `addDelay` final solo refrescaba el contador. Agregado el `return;` faltante.

3. **`Acrobat.java:140` re-habilitar flight durante cooldown.** `&& !hasExpired` (cooldown ACTIVO) re-habilitaba flight + sonido de wither en cada PlayerMoveEvent durante los 10s del cooldown. Resultado: spam de sonido + UX rota (tap-tap brevemente flota y se cae). Corregido a `&& hasExpired` — flight solo se re-habilita una vez el cooldown haya terminado, así el cooldown sí bloquea el doble-salto.

### ~~[FEATURE-6] Versiones NMS soportadas inconsistentes~~ ✅ RESUELTO con CustomMob API
**Archivos:** `Mobs/CustomMobManager.java`, `Manager/ZombieManager.java:117-120`, `Manager/WitchManager.java:81-89`, `Manager/BossManager.java:427`

**Estado actual (post-CustomMob):**
- **1.9–1.17:** Usa NMS `MobCreator` para spawning custom (equipment, AI, stats). Soporte completo en los 3 managers.
- **1.18+:** `CustomMobManager` (API moderna con PDC/Attributes) reemplaza `MobCreator`. Todos los mobs pasan por `CustomMobManager.spawn()` que aplica abilities, HP custom, identificación PDC, y death rewards de forma unificada.

**Cobertura por manager:**
- `PlayerManager.fetchRespawner()` → 1.9–1.21.3 (23 versiones) ✅
- `ZombieManager` → 1.9–1.18.1 con NMS; 1.18+ usa `CustomMobManager.spawn()` + fallback a API Bukkit estándar si CustomMobManager está deshabilitado ✅
- `WitchManager` → 1.18+ usa `CustomMobManager.spawn()` + fallback manual pre-1.18 ✅
- `BossManager` → 1.9–1.21.3 helpers (ChunkHelper); 1.9–1.17 con `MobCreator`; 1.18+ usa `CustomMobManager.spawn()` para wither/warden ✅

**Resultado:** La asimetría original se resolvió con la nueva arquitectura. CustomMob unifica la lógica de mobs complejos en 1.18+ (ver `.claude/docs/custom_mobs.md`). Versiones antiguas (1.9–1.17) mantienen NMS directo. No requiere acción adicional.

---

## Resumen de prioridades

| ID | Estado | Severidad | Esfuerzo | Descripción corta |
|---|---|---|---|---|
| BUG-1  | ✅ | 🔴 Crítico | Bajo | Cleanup compartido en handleDisconnect (quit + kick) |
| BUG-2  | ✅ | 🔴 Crítico | Medio | Null guards en todos los métodos de GamePlayer |
| BUG-3  | ✅ | 🔴 Crítico | Bajo | getAccount() ahora cachea |
| BUG-4  | ✅ | 🟠 Alto | Medio | snapshot + volatile aplicado |
| BUG-5  | ✅ | 🟠 Alto | Bajo | setTeam corregido |
| BUG-7  | ✅ | 🔴 Crítico | Bajo | XP/dinero ahora al killer |
| BUG-8  | ✅ | 🟠 Alto | Bajo | losses ahora a la víctima + typo renombrado |
| BUG-9  | ✅ | 🔴 Crítico | Bajo | Loop con `&& count < 6` + safety rotate |
| BUG-10 | ✅ | 🟠 Alto | Bajo | onBuild ahora niega permiso correctamente |
| BUG-11 | ✅ | 🟡 Medio | Bajo | itemInHand null-checked en breakResource |
| BUG-12 | ✅ | 🟡 Medio | Medio | PreparedStatement con bind en SQLDB (3 queries) |
| BUG-1b | ✅ | 🔴 Crítico | Bajo | handleDisconnect compartido |
| LEAK-1 | ✅ | 🟠 Alto | Bajo | Limpia en quit y kick |
| LEAK-2 | ✅ | 🟡 Medio | Bajo | UUID-keyed |
| LEAK-3 | ✅ | 🟡 Medio | Bajo | Limpia en quit y kick |
| QUALITY-1  | ✅ | 🟡 Medio | Bajo | Config defaults aplicados |
| QUALITY-2  | ✅ | 🟠 Alto | Bajo | isTeamAlive con null guard |
| QUALITY-2b | ✅ | 🟠 Alto | Bajo | Null guards en accesos restantes a getNexus() |
| QUALITY-3  | ✅ | 🟢 Bajo | Bajo | Resuelto al implementar SCALE-1 |
| QUALITY-5  | ✅ | 🟢 Bajo | Bajo | Logs traducidos + debug spam eliminado |
| QUALITY-6  | ✅ | 🟢 Bajo | Bajo | typo `getDelayRemaining` corregido |
| QUALITY-7  | ✅ | 🟡 Medio | Bajo | close() copia colección antes de iterar |
| QUALITY-8  | ✅ | 🟡 Medio | Bajo | Reset scoreboard antes del loop en endGame |
| QUALITY-9  | ✅ | 🟡 Medio | Bajo | Null guards en breaker.getPlayer() y meta |
| QUALITY-10 | ✅ | 🟠 Alto | Medio | Saves async + null guards en canEndGame/forceStopGame |
| QUALITY-11 | ✅ | 🟠 Alto | Bajo | joinTeam ahora usa enum name en lugar de localizado |
| QUALITY-12 | ✅ | 🟡 Medio | Bajo | canEndGame guard si winner es NONE |
| QUALITY-13 | ✅ | 🟢 Bajo | Bajo | onInvClose con guards |
| BOSS    | ✅ | 🟠 Alto | Medio | Boss world fallback, gamerules, chunk keep-alive |
| SCALE-1 | ✅ | 🟠 Alto | Medio | Set<UUID> per team |
| SCALE-2 | ✅ | 🟠 Alto | Medio | Per-tick eliminado + cache de líneas + updateIndividualSign |
| SCALE-3 | ✅ | 🟡 Medio | Bajo | BossBar text/percent cached |
| SCALE-4 | ✅ | 🟡 Medio | Bajo | updatePlayerScoreboard(Player) sobrecarga |
| SCALE-5 | ✅ | 🟡 Medio | Bajo | ThreadLocalRandom en hot paths |
| SCALE-6 | ✅ | 🟠 Alto | Medio | Async saves + Hikari pool sin sync |
| SCALE-7 | ✅ | 🟡 Medio | Alto | HikariCP pool reemplaza connection única; synchronized eliminados |
| FEATURE-6 | ✅ | 🟢 Bajo | — | Resuelto con CustomMob API (1.18+) + NMS legacy (1.9-1.17) |

### Próximos a atacar (impacto/esfuerzo)
1. **BUG-12 (extendido)** — el resto de queries en SQLDB que aún hacen string-concat (createAccountAndAddToDatabase, saveAccount Update queries) — están dentro de los strings devueltos por `getCreateAccountQuery`/`getUpdateAccountQuery` en MySQLDB/SQLiteDB. Migrar a PreparedStatement con bind.
2. **FEATURE-3** — soporte a múltiples boss-worlds por arena (actualmente uno solo).
3. **FEATURE-4** — verificar persistencia de kits desbloqueables entre sesiones (`Kit.resetKit()` está vacío).
