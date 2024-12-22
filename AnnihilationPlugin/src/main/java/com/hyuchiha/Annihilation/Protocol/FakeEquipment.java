/* The MIT License (MIT)
 *
 * Copyright (c) 2014 Kristian S. Stangeland
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.hyuchiha.Annihilation.Protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Map;

import static com.comphenix.protocol.PacketType.Play.Server.ENTITY_EQUIPMENT;
import static com.comphenix.protocol.PacketType.Play.Server.NAMED_ENTITY_SPAWN;

/**
 * Modify player equipment.
 *
 * @author Kristian
 */
public abstract class FakeEquipment {
  /**
   * Represents an equipment slot.
   *
   * @author Kristian
   */
  public enum EquipmentSlot {
    // http://wiki.vg/Protocol#Entity_Equipment_.280x05.29
    MAINHAND(0),
    OFFHAND(1),
    FEET(2),
    LEGS(3),
    CHEST(4),
    HEAD(5);

    private int id;

    private EquipmentSlot(int id) {
      this.id = id;
    }

    /**
     * Retrieve the entity's equipment in the current slot.
     *
     * @param entity - the entity.
     * @return The equipment.
     */
    public ItemStack getEquipment(LivingEntity entity) {
      try {
        switch (this) {
          case MAINHAND:
            return entity.getEquipment().getItemInMainHand();
          case OFFHAND:
            return entity.getEquipment().getItemInOffHand();
          case FEET:
            return entity.getEquipment().getBoots();
          case LEGS:
            return entity.getEquipment().getLeggings();
          case CHEST:
            return entity.getEquipment().getChestplate();
          case HEAD:
            return entity.getEquipment().getHelmet();
          default:
            throw new IllegalArgumentException("Unknown slot: " + this);
        }
      } catch (NullPointerException ex) {
        return null;
      }
    }

    /**
     * Determine if the entity has an equipment in the current slot.
     *
     * @param entity - the entity.
     * @return TRUE if it is empty, FALSE otherwise.
     */
    public boolean isEmpty(LivingEntity entity) {
      ItemStack stack = getEquipment(entity);
      return stack != null && stack.getType() == Material.AIR;
    }

    /**
     * Retrieve the underlying equipment slot ID.
     *
     * @return The ID.
     */
    public int getId() {
      return id;
    }

    /**
     * Find the corresponding equipment slot.
     *
     * @param id - the slot ID.
     * @return The equipment slot.
     */
    public static EquipmentSlot fromId(int id) {
      for (EquipmentSlot slot : values()) {
        if (slot.getId() == id) {
          return slot;
        }
      }
      throw new IllegalArgumentException("Cannot find slot id: " + id);
    }
  }

  /**
   * Represents an equipment event.
   *
   * @author Kristian
   */
  public static class EquipmentSendingEvent {
    private Player client;
    private LivingEntity visibleEntity;
    private EquipmentSlot slot;
    private ItemStack equipment;

    private EquipmentSendingEvent(Player client, LivingEntity visibleEntity, EquipmentSlot slot, ItemStack equipment) {
      this.client = client;
      this.visibleEntity = visibleEntity;
      this.slot = slot;
      this.equipment = equipment;
    }

    /**
     * Retrieve the client that is observing the entity.
     *
     * @return The observing client.
     */
    public Player getClient() {
      return client;
    }

    /**
     * Retrieve the entity whose armor or held item we are updating.
     *
     * @return The visible entity.
     */
    public LivingEntity getVisibleEntity() {
      return visibleEntity;
    }

    /**
     * Retrieve the equipment that we are
     *
     * @return
     */
    public ItemStack getEquipment() {
      return equipment;
    }

    /**
     * Set the equipment we will send to the player.
     *
     * @param equipment - the equipment, or NULL to sent air.
     */
    public void setEquipment(ItemStack equipment) {
      this.equipment = equipment;
    }

    /**
     * Retrieve the slot of this equipment.
     *
     * @return The slot.
     */
    public EquipmentSlot getSlot() {
      return slot;
    }

    /**
     * Set the slot of this equipment.
     *
     * @param slot - the slot.
     */
    public void setSlot(EquipmentSlot slot) {
      this.slot = Preconditions.checkNotNull(slot, "slot cannot be NULL");
    }
  }

  // Necessary to detect duplicate
  private Map<Object, EquipmentSlot> processedPackets = new MapMaker().weakKeys().makeMap();

  private Plugin plugin;
  private ProtocolManager manager;

  // Current listener
  private PacketListener listener;

  public FakeEquipment(Plugin plugin) {
    this.plugin = plugin;
    this.manager = ProtocolLibrary.getProtocolManager();

    manager.addPacketListener(
        listener = new PacketAdapter(plugin, ENTITY_EQUIPMENT, NAMED_ENTITY_SPAWN) {
          @Override
          public void onPacketSending(PacketEvent event) {
            PacketContainer packet = event.getPacket();
            PacketType type = event.getPacketType();

            if (packet.getEntityModifier(event).read(0) instanceof LivingEntity) {
              // The entity that is being displayed on the player's screen
              LivingEntity visibleEntity = (LivingEntity) packet.getEntityModifier(event).read(0);
              Player observingPlayer = event.getPlayer();

              if (ENTITY_EQUIPMENT.equals(type)) {
                EquipmentSlot slot = EquipmentSlot.fromId(packet.getItemSlots().read(0).ordinal());
                ItemStack equipment = packet.getItemModifier().read(0);
                EquipmentSendingEvent sendingEvent = new EquipmentSendingEvent(
                    observingPlayer, visibleEntity, slot, equipment);

                // Assume we process all packets - the overhead isn't that bad
                EquipmentSlot previous = processedPackets.get(packet.getHandle());

                // See if this packet instance has already been processed
                if (previous != null) {
                  // Clone it - otherwise, we'll loose the old modification
                  packet = event.getPacket().deepClone();
                  sendingEvent.setSlot(previous);
                  sendingEvent.setEquipment(previous.getEquipment(visibleEntity).clone());
                }

                if (onEquipmentSending(sendingEvent)) {
                  processedPackets.put(packet.getHandle(), previous != null ? previous : slot);
                }

                // Save changes
                if (slot != sendingEvent.getSlot()) {
                  packet.getIntegers().write(0, slot.getId());
                }
                if (equipment != sendingEvent.getEquipment()) {
                  packet.getItemModifier().write(0, sendingEvent.getEquipment());
                }

              } else if (NAMED_ENTITY_SPAWN.equals(type)) {
                // Trigger updates?
                onEntitySpawn(observingPlayer, visibleEntity);
              } else {
                throw new IllegalArgumentException("Unknown packet type:" + type);
              }
            }
          }
        });
  }

  /**
   * Invoked when a living entity has been spawned on the given client.
   *
   * @param client        - the client.
   * @param visibleEntity - the visibleEntity.
   */
  protected void onEntitySpawn(Player client, LivingEntity visibleEntity) {
    // Update all the slots?
  }

  /**
   * Invoked when the equipment or held item of an living entity is sent to a client.
   * <p>
   * This can be fully modified. Please return TRUE if you do, though.
   *
   * @param equipmentEvent - the equipment event.
   * @return TRUE if the equipment was modified, FALSE otherwise.
   */
  protected abstract boolean onEquipmentSending(EquipmentSendingEvent equipmentEvent);

  /**
   * Update the given slot.
   *
   * @param client        - the observing client.
   * @param visibleEntity - the visible entity that will be updated.
   * @param slot          - the equipment slot to update.
   */
  public void updateSlot(final Player client, LivingEntity visibleEntity, EquipmentSlot slot) {
    if (listener == null)
      throw new IllegalStateException("FakeEquipment has closed.");

    final PacketContainer equipmentPacket = new PacketContainer(ENTITY_EQUIPMENT);
    equipmentPacket.getIntegers().
        write(0, visibleEntity.getEntityId()).
        write(1, slot.getId());
    equipmentPacket.getItemModifier().
        write(0, slot.getEquipment(visibleEntity));

    // We have to send the packet AFTER named entity spawn has been sent
    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
      @Override
      public void run() {
        try {
          ProtocolLibrary.getProtocolManager().sendServerPacket(client, equipmentPacket);
        } catch (Exception e) {
          throw new RuntimeException("Unable to update slot.", e);
        }
      }
    });
  }

  /**
   * Close the current equipment modifier.
   */
  public void close() {
    if (listener != null) {
      manager.removePacketListener(listener);
      listener = null;
    }
  }
}
