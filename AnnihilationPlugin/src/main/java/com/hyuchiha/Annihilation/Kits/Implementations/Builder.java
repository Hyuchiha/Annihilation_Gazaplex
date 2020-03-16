package com.hyuchiha.Annihilation.Kits.Implementations;

import com.hyuchiha.Annihilation.Game.GamePlayer;
import com.hyuchiha.Annihilation.Game.Kit;
import com.hyuchiha.Annihilation.Kits.Base.BaseKit;
import com.hyuchiha.Annihilation.Manager.PlayerManager;
import com.hyuchiha.Annihilation.Messages.Translator;
import com.hyuchiha.Annihilation.Utils.GameUtils;
import com.hyuchiha.Annihilation.Utils.KitUtils;
import com.hyuchiha.Annihilation.Utils.LocationUtils;
import com.hyuchiha.Annihilation.Utils.TimersUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Builder extends BaseKit {
    private final List<ItemStack> drops;
    private HashMap<UUID, List<Block>> delayBlocks;
    private final Random random = new Random();

    // https://wiki.shotbow.net/Builder
    public Builder(String name, ItemStack icon, ConfigurationSection section) {
        super(name, icon, section);

        drops = new ArrayList<>();
        delayBlocks = new HashMap<>();

        fillDrops();
    }

    private void fillDrops() {
        drops.add(new ItemStack(Material.GLASS, 20));
        drops.add(new ItemStack(Material.SPRUCE_WOOD_STAIRS, 20));
        drops.add(new ItemStack(Material.WOOD, 70));
        drops.add(new ItemStack(Material.SPRUCE_FENCE, 10));
        drops.add(new ItemStack(Material.TORCH, 5));
        drops.add(new ItemStack(Material.BRICK, 40));
        drops.add(new ItemStack(Material.STONE, 50));
        drops.add(new ItemStack(Material.WOOL, 30));
        drops.add(new ItemStack(Material.DIRT, 60));
        drops.add(new ItemStack(Material.IRON_FENCE, 10));
    }

    @Override
    protected void setupSpawnItems() {
        spawnItems.add(new ItemStack(Material.WOOD_SWORD));
        spawnItems.add(new ItemStack(Material.WOOD_PICKAXE));
        spawnItems.add(new ItemStack(Material.WOOD_AXE));
        spawnItems.add(new ItemStack(Material.WOOD_SPADE));

        ItemStack book = new ItemStack(Material.BOOK, 1);
        ItemMeta bookMeta = book.getItemMeta();
        bookMeta.setDisplayName(Translator.getColoredString("KITS.BUILDER_BOOK"));
        book.setItemMeta(bookMeta);
        spawnItems.add(book);

        spawnItems.add(getDelayingBlocks(2));
    }

    @Override
    protected void giveSpecialPotions(Player recipient) {
        // Nope, not needed
    }

    @Override
    protected void giveExtraHearts(Player recipient) {
        // Still not needed
    }

    @Override
    protected void extraConfiguration(Player recipient) {
        // I think no
    }

    @Override
    public void removePlayer(Player recipient) {
        // We remove delaying blocks
    }

    @Override
    public void resetData() {
        // Reset delaying blocks
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBuilderBookInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        GamePlayer gPlayer = PlayerManager.getGamePlayer(player);
        Action action = e.getAction();

        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            PlayerInventory inventory = player.getInventory();
            ItemStack handItem = inventory.getItemInMainHand();

            if (handItem != null && KitUtils.isKitItem(handItem, "KITS.BUILDER_BOOK")
                    && gPlayer.getKit() == Kit.BUILDER) {

                if (TimersUtils.hasExpired(player, Kit.BUILDER)) {
                    List<ItemStack> drops = getRandomDrops();

                    if (random.nextInt(100) < 30) {
                        drops.add(getDelayingBlocks(1));
                    }

                    createInventoryMenu(player, drops);

                    TimersUtils.addDelay(player, Kit.BUILDER, 90, TimeUnit.SECONDS);
                } else {
                    KitUtils.showKitItemDelay(player, gPlayer.getKit());
                }
            }
        }
    }

    @EventHandler()
    public void onPlaceBlock(BlockPlaceEvent event) {
        Player p = event.getPlayer();
        GamePlayer gPlayer = PlayerManager.getGamePlayer(p);

        if (gPlayer.getKit() == Kit.BUILDER) {
            p.giveExp(1);

            if (event.getBlock().getType() == Material.SEA_LANTERN && event.getItemInHand().hasItemMeta()) {
                ItemMeta blockMeta = event.getItemInHand().getItemMeta();

                if (blockMeta.getDisplayName().equals(Translator.getColoredString("KITS.BUILDER_DELAYING_BLOCK"))) {
                    List<Block> blocks = delayBlocks.get(p.getUniqueId());

                    if (blocks == null) {
                        blocks = new ArrayList<>();
                    }

                    blocks.add(event.getBlockPlaced());

                    delayBlocks.put(p.getUniqueId(), blocks);
                }
            }

        }

    }

    @EventHandler()
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        GamePlayer gPlayer = PlayerManager.getGamePlayer(player);

        Block block = event.getBlock();

        for (List<Block> blocks: delayBlocks.values()) {

            for (Block delayBlock: blocks) {
                if (LocationUtils.isSameBlock(delayBlock, block)) {

                } else if (GameUtils.blockNearBlock(block, delayBlock, 5)) {

                }
            }

        }

    }

    private List<ItemStack> getRandomDrops() {
        LinkedList<ItemStack> shuffledMaterials = new LinkedList<>(drops);
        Collections.shuffle(shuffledMaterials);
        return shuffledMaterials.subList(0, Math.min(5, 8));
    }

    public void createInventoryMenu(Player player, List<ItemStack> drops) {
        int size = 9 * (int) Math.ceil(drops.size() / 9.0D);
        Inventory inv = Bukkit.createInventory(player, size, Translator.getColoredString("KITS.BUILDER_ITEM_MENU"));

        for (ItemStack drop : drops) {
            inv.addItem(drop);
        }

        player.openInventory(inv);
    }

    private ItemStack getDelayingBlocks(int quantity) {
        ItemStack delayingBlock = new ItemStack(Material.SEA_LANTERN, quantity);
        ItemMeta delayingBlockMeta = delayingBlock.getItemMeta();
        delayingBlockMeta.setDisplayName(Translator.getColoredString("KITS.BUILDER_DELAYING_BLOCK"));
        delayingBlock.setItemMeta(delayingBlockMeta);
        return delayingBlock;
    }
}
