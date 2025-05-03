package me.melonik.oneblockcore.gui;

import me.melonik.oneblockcore.Main;
import me.melonik.oneblockcore.models.Island;
import me.melonik.oneblockcore.models.IslandUpgrades;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class IslandUpgradesGUI implements Listener {
    private final Main plugin;
    private final Player player;
    private final Inventory inventory;
    private static final int[] BORDER_SLOTS = {0,1,2,3,4,5,6,7,8,9,17,18,26};
    private int currentNoMoneySlot = -1;
    private boolean isClosing = false;

    public IslandUpgradesGUI(Main plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(null, 27, "§8Ulepszenia Wyspy");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        initializeItems();
    }

    private void initializeItems() {
        Island island = plugin.getIslandManager().getPlayerIsland(player.getUniqueId());
        if (island == null) return;

        for (int slot : BORDER_SLOTS) {
        }

        // Border
        String borderLevel = String.format("%d/%s",
                island.getBorderSize() / 50,
                IslandUpgrades.getUpgradeMaxLevel("BORDER"));
        ItemStack borderItem = createItem(Material.IRON_BARS, "§3Border wyspy §7(" + borderLevel + ")",
                "§7Obecny rozmiar: §f" + island.getBorderSize() + "x" + island.getBorderSize(),
                "§7Następny rozmiar: §f" + IslandUpgrades.getNextBorderSize(island.getBorderSize()) + "x" + IslandUpgrades.getNextBorderSize(island.getBorderSize()),
                IslandUpgrades.getBorderUpgradeCost(island.getBorderSize()) > 0 ? "§7Koszt ulepszenia: §f$" + IslandUpgrades.getBorderUpgradeCost(island.getBorderSize()) : "",
                "",
                IslandUpgrades.getBorderUpgradeCost(island.getBorderSize()) > 0 ? "§bKliknij, aby ulepszyć" : "§cOsiągnięto maksymalny poziom"
        );
        inventory.setItem(10, borderItem);

        // Spawner Limit
        String spawnerLevel = String.format("%d/%s",
                island.getUpgrades().getSpawnerLimit(),
                IslandUpgrades.getUpgradeMaxLevel("SPAWNER"));
        ItemStack spawnerItem = createItem(Material.SPAWNER, "§3Limit spawnerów §7(" + spawnerLevel + ")",
                "§7Obecny limit: §f" + island.getUpgrades().getSpawnerLimit(),
                "§7Następny limit: §f" + IslandUpgrades.getNextSpawnerLimit(island.getUpgrades().getSpawnerLimit()),
                IslandUpgrades.getSpawnerUpgradeCost(island.getUpgrades().getSpawnerLimit()) > 0 ? "§7Koszt ulepszenia: §f$" + IslandUpgrades.getSpawnerUpgradeCost(island.getUpgrades().getSpawnerLimit()) : "",
                "",
                IslandUpgrades.getSpawnerUpgradeCost(island.getUpgrades().getSpawnerLimit()) > 0 ? "§bKliknij, aby ulepszyć" : "§cOsiągnięto maksymalny poziom"
        );
        inventory.setItem(11, spawnerItem);

        // Hopper Limit
        String hopperLevel = String.format("%d/%s",
                island.getUpgrades().getHopperLimit() / 150,
                IslandUpgrades.getUpgradeMaxLevel("HOPPER"));
        ItemStack hopperItem = createItem(Material.HOPPER, "§3Limit lejków §7(" + hopperLevel + ")",
                "§7Obecny limit: §f" + island.getUpgrades().getHopperLimit(),
                "§7Następny limit: §f" + IslandUpgrades.getNextHopperLimit(island.getUpgrades().getHopperLimit()),
                IslandUpgrades.getHopperUpgradeCost(island.getUpgrades().getHopperLimit()) > 0 ? "§7Koszt ulepszenia: §f$" + IslandUpgrades.getHopperUpgradeCost(island.getUpgrades().getHopperLimit()) : "",
                "",
                IslandUpgrades.getHopperUpgradeCost(island.getUpgrades().getHopperLimit()) > 0 ? "§bKliknij, aby ulepszyć" : "§cOsiągnięto maksymalny poziom"
        );
        inventory.setItem(12, hopperItem);

        // Hopper Speed
        String hopperSpeedLevel = String.format("%d/%s",
                island.getUpgrades().getHopperSpeedLevel(),
                IslandUpgrades.getUpgradeMaxLevel("HOPPER_SPEED"));
        ItemStack hopperSpeedItem = createItem(Material.CLOCK, "§3Szybkość lejków §7(" + hopperSpeedLevel + ")",
                "§7Obecna szybkość: §fx" + island.getUpgrades().getHopperSpeedLevel(),
                "§7Następna szybkość: §fx" + IslandUpgrades.getNextHopperSpeedLevel(island.getUpgrades().getHopperSpeedLevel()),
                IslandUpgrades.getHopperSpeedUpgradeCost(island.getUpgrades().getHopperSpeedLevel()) > 0 ? "§7Koszt ulepszenia: §f$" + IslandUpgrades.getHopperSpeedUpgradeCost(island.getUpgrades().getHopperSpeedLevel()) : "",
                "",
                IslandUpgrades.getHopperSpeedUpgradeCost(island.getUpgrades().getHopperSpeedLevel()) > 0 ? "§bKliknij, aby ulepszyć" : "§cOsiągnięto maksymalny poziom"
        );
        inventory.setItem(13, hopperSpeedItem);

        // Member Limit
        String memberLevel = String.format("%d/%s",
                island.getUpgrades().getMemberLimit(),
                IslandUpgrades.getUpgradeMaxLevel("MEMBER"));
        ItemStack memberItem = createItem(Material.PLAYER_HEAD, "§3Limit członków §7(" + memberLevel + ")",
                "§7Obecny limit: §f" + island.getUpgrades().getMemberLimit(),
                "§7Następny limit: §f" + IslandUpgrades.getNextMemberLimit(island.getUpgrades().getMemberLimit()),
                IslandUpgrades.getMemberUpgradeCost(island.getUpgrades().getMemberLimit()) > 0 ? "§7Koszt ulepszenia: §f$" + IslandUpgrades.getMemberUpgradeCost(island.getUpgrades().getMemberLimit()) : "",
                "",
                IslandUpgrades.getMemberUpgradeCost(island.getUpgrades().getMemberLimit()) > 0 ? "§bKliknij, aby ulepszyć" : "§cOsiągnięto maksymalny poziom"
        );
        inventory.setItem(14, memberItem);

        // Piston Limit
        String pistonLevel = String.format("%d/%s",
                island.getUpgrades().getPistonLimit() / 10,
                IslandUpgrades.getUpgradeMaxLevel("PISTON"));
        ItemStack pistonItem = createItem(Material.PISTON, "§3Limit tłoków §7(" + pistonLevel + ")",
                "§7Obecny limit: §f" + island.getUpgrades().getPistonLimit(),
                "§7Następny limit: §f" + IslandUpgrades.getNextPistonLimit(island.getUpgrades().getPistonLimit()),
                IslandUpgrades.getPistonUpgradeCost(island.getUpgrades().getPistonLimit()) > 0 ? "§7Koszt ulepszenia: §f$" + IslandUpgrades.getPistonUpgradeCost(island.getUpgrades().getPistonLimit()) : "",
                "",
                IslandUpgrades.getPistonUpgradeCost(island.getUpgrades().getPistonLimit()) > 0 ? "§bKliknij, aby ulepszyć" : "§cOsiągnięto maksymalny poziom"
        );
        inventory.setItem(15, pistonItem);

        ItemStack backButton = createItem(Material.PAPER, "§cPowrót!",
                "§7Kliknij, aby wrócić do panelu wyspy"
        );

        ItemMeta meta = backButton.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(9996);
            backButton.setItemMeta(meta);
        }

        inventory.setItem(22, backButton);
    }

    private ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(inventory)) return;
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        Island island = plugin.getIslandManager().getPlayerIsland(player.getUniqueId());
        if (island == null) return;

        if (!island.getOwnerId().equals(player.getUniqueId()) &&
                !island.hasPermission(player.getUniqueId(), "UPGRADES")) {
            player.sendMessage("§cNie masz uprawnień do ulepszania wyspy!");
            player.sendMessage("§7Poproś właściciela aby zmienił to pod /panel");
            player.closeInventory();
            return;
        }

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR ||
                clickedItem.getType() == Material.BLACK_STAINED_GLASS_PANE) return;

        int slot = event.getSlot();

        if (slot == 22) {
            new IslandPanelGUI(plugin, player).open();
            return;
        }

        if (slot < 10 || slot > 15) return;

        if (currentNoMoneySlot != -1) {
            initializeItems();
        }

        double cost = getUpgradeCost(island, slot);
        if (cost <= 0) {
            player.sendTitle("§4§lBłąd!", "§cOsiągnieto maksymalny poziom ulepszenia!", 10, 40, 20);
            player.closeInventory();
            return;
        }

        if (plugin.getEconomyManager().getPlayerMoney(player.getUniqueId()) < cost) {
            currentNoMoneySlot = slot;

            ItemStack noMoneyItem = new ItemStack(Material.PAPER);
            ItemMeta meta = noMoneyItem.getItemMeta();
            meta.setDisplayName("§cNie stać Cię na to!");
            meta.setLore(Arrays.asList("§7Potrzebujesz jeszcze: §c$" + (cost - plugin.getEconomyManager().getPlayerMoney(player.getUniqueId()))));
            meta.setCustomModelData(9997);
            noMoneyItem.setItemMeta(meta);

            inventory.setItem(slot, noMoneyItem);
            return;
        }

        handleUpgrade(island, slot);
    }

    private double getUpgradeCost(Island island, int slot) {
        switch (slot) {
            case 10: return IslandUpgrades.getBorderUpgradeCost(island.getBorderSize());
            case 11: return IslandUpgrades.getSpawnerUpgradeCost(island.getUpgrades().getSpawnerLimit());
            case 12: return IslandUpgrades.getHopperUpgradeCost(island.getUpgrades().getHopperLimit());
            case 13: return IslandUpgrades.getHopperSpeedUpgradeCost(island.getUpgrades().getHopperSpeedLevel());
            case 14: return IslandUpgrades.getMemberUpgradeCost(island.getUpgrades().getMemberLimit());
            case 15: return IslandUpgrades.getPistonUpgradeCost(island.getUpgrades().getPistonLimit());
            default: return -1;
        }
    }

    private void handleUpgrade(Island island, int slot) {
        double cost = getUpgradeCost(island, slot);
        if (cost <= 0) return;

        if (!plugin.getEconomyManager().removePlayerMoney(player.getUniqueId(), cost)) {
            return;
        }

        switch (slot) {
            case 10:
                island.setBorderSize(IslandUpgrades.getNextBorderSize(island.getBorderSize()));
                player.sendTitle("§2§lSuckes!", "§aPowiększyłeś border wyspy!", 10, 40, 20);
                player.closeInventory();
                break;
            case 11:
                island.getUpgrades().setSpawnerLimit(IslandUpgrades.getNextSpawnerLimit(island.getUpgrades().getSpawnerLimit()));
                player.sendTitle("§2§lSuckes!", "§aUlepszyłeś limit spawnerów!", 10, 40, 20);
                break;
            case 12:
                island.getUpgrades().setHopperLimit(IslandUpgrades.getNextHopperLimit(island.getUpgrades().getHopperLimit()));
                player.sendTitle("§2§lSuckes!", "§aUlepszyłeś limit lejków!", 10, 40, 20);
                player.closeInventory();
                break;
            case 13:
                island.getUpgrades().setHopperSpeedLevel(IslandUpgrades.getNextHopperSpeedLevel(island.getUpgrades().getHopperSpeedLevel()));
                player.sendTitle("§2§lSuckes!", "§aUlepszyłeś szybkość lejków!", 10, 40, 20);
                player.closeInventory();
                break;
            case 14:
                island.getUpgrades().setMemberLimit(IslandUpgrades.getNextMemberLimit(island.getUpgrades().getMemberLimit()));
                player.sendTitle("§2§lSuckes!", "§aUlepszyłeś limit członków!", 10, 40, 20);
                player.closeInventory();
                break;
            case 15:
                island.getUpgrades().setPistonLimit(IslandUpgrades.getNextPistonLimit(island.getUpgrades().getPistonLimit()));
                player.sendTitle("§2§lSuckes!", "§aUlepszyłeś limit tłoków!", 10, 40, 20);
                player.closeInventory();
                break;
        }

        plugin.getIslandManager().saveIslands();
        initializeItems();
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().equals(inventory)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getInventory().equals(inventory)) return;
        isClosing = true;
        currentNoMoneySlot = -1;
    }

    public void open() {
        player.openInventory(inventory);
    }
}