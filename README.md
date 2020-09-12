# Annihilation!

This plugins is inspired by **Annihilation Plugin** from **ShowBow**.

This repo contains the code base for **Annihilation Plugin** and it was designed for **Gazaplex Network**, now becomes an open source hoping it can be used in another servers and be maintained by the community.

# Features
- Map Voting System
- Map restore on finish
- Support for Vault
- Support for SQLite, MySQL, MongoDB
- BungeeCord Support
- Balanced teams validator
- Multi-Configurable Arenas
- Customizable Messages
- Support for Minecraft 1.9 - 1.14
- Multiple Kits for players (15)
- Antinuker protection
- Stats commands
- Custom Wither Boss and Witches
- Scoreboards

## Permissions

All the permissions required for running this plugin are listed here:
 
 - **annihilation.command.start** command to force a game to start
 - **annihilation.command.stop** command to force a game to stop
 - **annihilation.command.switch_kit** command to  switch kit
 - **annihilation.bypass.switch_team** needed if one player can switch team
 - **annihilation.bypass.team_limit** needed if one player can join to a full team
 - **annihilation.bypass.construction** needed if one player can build and destroy inside the nexus
 - **annihilation.vip.diamond** for unlock enderchest diamond slots
 - **annihilation.vip.gold** for unlock enderchest gold slots
 - **annihilation.vip.iron** for unlock enderchest iron slots
 - **annihilation.vip.pass** to let a user join a game when is above phase 3
 - **annihilation.class.[kit]** add permission to users for use a kit

## Commands

This are the commands a player can execute in game

- **/anni** for view all the commands
- **/anni start** start the current game
- **/anni stop** stop the current game
- **/team** list all the available teams
- **/team [red, blue, green,yellow]** join player to a team
- **/vote [map]** vote for a map to play
- **/stats [kills, deaths, wins, losses, nexus_damage]** list the stats of a category for a player
- **/top [kills, deaths, wins, losses, nexus_damage]** list top stats for a giver category
- **/class [kit]** change a class for a player
- **/star** gives a wither star (only for testing)


> **Note:** The **Teams** command requires a team with the configured name in messages.yml if you put **Team Red** is called **Rojo** The command would be **/team rojo**
> 

## Configure a map
For now the only way to configure a map is adding all the map data to **maps.yml** using the example format and placing the map in the directory called **maps**

