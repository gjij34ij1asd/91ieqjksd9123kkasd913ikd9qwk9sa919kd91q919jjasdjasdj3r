package me.melonik.oneblockcore.gui;

import me.melonik.oneblockcore.Main;
import me.melonik.oneblockcore.models.Home;
import me.melonik.oneblockcore.models.Island;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HomesGUI implements Listener {
    private final Main plugin;
    private final Player player;
    private final Inventory inventory;
    private final Map<Integer, Integer> homeSlots = new HashMap<>();

    public HomesGUI(Main plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(null, 45, "§8Twoje domy");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        initializeItems();
    }

    private void initializeItems() {
        int[] slotPositions = {19, 20, 21, 22, 23, 24, 25};
        Map<Integer, Home> homes = plugin.getHomeManager().getPlayerHomes(player.getUniqueId());

        for (int i = 0; i < 7; i++) {
            int slot = slotPositions[i];
            int homeNumber = i + 2;
            homeSlots.put(slot, homeNumber);

            if (homeNumber > 2 && !player.hasPermission("oneblockhome." + homeNumber)) {
                ItemStack lockedItem = createItem(Material.PAPER, "§cDom " + homeNumber,
                        "§7Nie posiadasz dostępu do tej ilości homeów",
                        "§7Zakup rangę na stronie §ewww.zacraft.pl §7aby odblokować");
                ItemMeta meta = lockedItem.getItemMeta();
                meta.setCustomModelData(9997);
                lockedItem.setItemMeta(meta);
                inventory.setItem(slot, lockedItem);
                continue;
            }

            Home home = homes.get(homeNumber);
            if (home != null) {
                ItemStack homeItem = createItem(Material.LIME_BED, "§aDom " + homeNumber,
                        "§7Kliknij §eLPM §7aby się teleportować",
                        "§7Kliknij §ePPM §7aby usunąć dom",
                        "",
                        "§7Lokalizacja:",
                        "§7X: §f" + home.getLocation().getBlockX(),
                        "§7Y: §f" + home.getLocation().getBlockY(),
                        "§7Z: §f" + home.getLocation().getBlockZ());
                inventory.setItem(slot, homeItem);
            } else {
                ItemStack emptyItem = createItem(Material.WHITE_BED, "§eDom " + homeNumber,
                        "§7Kliknij aby ustawić dom",
                        "§cMożesz ustawić home tylko na swojej wyspie!");
                inventory.setItem(slot, emptyItem);
            }
        }
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

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        int slot = event.getSlot();
        if (!homeSlots.containsKey(slot)) return;

        int homeNumber = homeSlots.get(slot);
        if (homeNumber > 2 && !player.hasPermission("oneblockhome." + homeNumber)) {
            player.sendMessage("§cNie masz dostępu do tej ilości homeów!");
            player.sendMessage("§7Zakup rangę na stronie §ewww.zacraft.pl §7aby odblokować");
            return;
        }

        Home home = plugin.getHomeManager().getHome(player.getUniqueId(), homeNumber);

        if (clicked.getType() == Material.WHITE_BED) {
            // Check if player is on their island
            Island island = plugin.getIslandManager().getIslandAt(player.getLocation());
            if (island == null || (!island.getOwnerId().equals(player.getUniqueId()) && !island.getMembers().contains(player.getUniqueId()))) {
                player.sendMessage("§cMożesz ustawić home tylko na swojej wyspie!");
                return;
            }

            plugin.getHomeManager().setHome(player.getUniqueId(), homeNumber, player.getLocation());
            player.sendMessage("§aUstawiono dom numer " + homeNumber + "!");
            initializeItems();
        } else if (clicked.getType() == Material.LIME_BED) {
            if (event.isLeftClick()) {
                player.teleport(home.getLocation());
                player.sendMessage("§aTeleportowano do domu!");
                player.closeInventory();
            } else if (event.isRightClick()) {
                plugin.getHomeManager().removeHome(player.getUniqueId(), homeNumber);
                player.sendMessage("§cUsunięto dom!");
                initializeItems();
            }
        }
    }

    public void open() {
        player.openInventory(inventory);
    }
}