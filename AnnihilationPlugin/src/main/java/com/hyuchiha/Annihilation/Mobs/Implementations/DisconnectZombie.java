package com.hyuchiha.Annihilation.Mobs.Implementations;

import com.hyuchiha.Annihilation.Mobs.Abilities.ChargeAbility;
import com.hyuchiha.Annihilation.Mobs.CustomMob;
import com.hyuchiha.Annihilation.Mobs.MobAbility;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Decorator-style wrapper for the disconnect placeholder zombie spawned by
 * {@code ZombieManager}. {@link #configure()} is intentionally a no-op so the manager's
 * existing setup (HP 40, name = team-colored player name, copied hand item + armor) is
 * preserved bit-for-bit.
 *
 * <p>The single {@link ChargeAbility} simulates a player's sprint-chase — every 10s the
 * zombie gets a 3-second Speed II burst when a target is within 10 blocks. Without this
 * the placeholder is just a vanilla zombie wearing the player's gear; with it, the
 * placeholder feels like the absent player is still putting up a fight.
 */
public class DisconnectZombie extends CustomMob {

  public DisconnectZombie(LivingEntity entity) {
    super(entity);
  }

  @Override
  public String getTypeId() {
    return "annihilation_disconnect_zombie";
  }

  @Override
  protected void configure() {
    // No-op — ZombieManager.createZombiePlayer already set HP, name, equipment, etc.
  }

  @Override
  protected List<MobAbility> createAbilities() {
    return new ArrayList<>(Arrays.asList(
        new ChargeAbility(/*cooldown*/ 200L, /*duration*/ 60, /*amplifier*/ 1, /*range*/ 10.0)
    ));
  }
}
