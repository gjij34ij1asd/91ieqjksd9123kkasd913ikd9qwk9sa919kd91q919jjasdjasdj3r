package me.melonik.oneblockcore.listeners;

import me.melonik.oneblockcore.Main;
import me.melonik.oneblockcore.gui.SpawnerGUI;
import me.melonik.oneblockcore.models.CustomSpawner;
import me.melonik.oneblockcore.models.Island;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SpawnerListener implements Listener {
    private final Main plugin;

    public SpawnerListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpawnerPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() != Material.SPAWNER) return;

        Player player = event.getPlayer();
        Block block = event.getBlock();
        Island island = plugin.getIslandManager().getIslandAt(block.getLocation());

        if (island == null) return;

        if (!island.getOwnerId().equals(player.getUniqueId()) &&
                !island.hasPermission(player.getUniqueId(), "SPAWNER_MANAGE")) {
            event.setCancelled(true);
            player.sendTitle("§4§lBłąd!", "§cNie masz permisji do stawiania spawnerów!", 10, 40, 20);
            return;
        }

        int currentSpawners = plugin.getSpawnerManager().getSpawners().size();
        if (currentSpawners >= island.getUpgrades().getSpawnerLimit()) {
            event.setCancelled(true);
            player.sendTitle("§4§lBłąd!","§cOsiągnięto maksymalny limit spawnerów na wyspie! §7(" + currentSpawners + "/" + island.getUpgrades().getSpawnerLimit() + ")",10,40,20);
            return;
        }

        ItemStack spawnerItem = event.getItemInHand();
        ItemMeta meta = spawnerItem.getItemMeta();
        if (meta != null && meta.hasLore()) {
            List<String> lore = meta.getLore();
            EntityType entityType = null;
            int speedLevel = 0;

            for (String line : lore) {
                if (line.startsWith("§7Typ: §f")) {
                    try {
                        entityType = EntityType.valueOf(line.substring(9));
                    } catch (IllegalArgumentException ignored) {}
                } else if (line.startsWith("§7Poziom szybkości: §f")) {
                    try {
                        speedLevel = Integer.parseInt(line.substring(20));
                    } catch (NumberFormatException ignored) {}
                }
            }

            CustomSpawner spawner = new CustomSpawner(block.getLocation());
            if (entityType != null) {
                spawner.setEntityType(entityType);
                spawner.setSpeedLevel(speedLevel);
                CreatureSpawner creatureSpawner = (CreatureSpawner) block.getState();
                creatureSpawner.setSpawnedType(entityType);
                creatureSpawner.update();
            }
            plugin.getSpawnerManager().addSpawner(block.getLocation());
        }
    }

    @EventHandler
    public void onSpawnerBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.SPAWNER) return;

        Player player = event.getPlayer();
        Block block = event.getBlock();
        Island island = plugin.getIslandManager().getIslandAt(block.getLocation());

        if (island == null) {
            if (!player.hasPermission("oneblock.admin")) {
                event.setCancelled(true);
                return;
            }
            return;
        }

        if (!island.getOwnerId().equals(player.getUniqueId()) &&
                !island.hasPermission(player.getUniqueId(), "SPAWNER_MANAGE")) {
            event.setCancelled(true);
            player.sendTitle("§4§lBłąd!", "§cNie masz permisji do niszczenia spawnerów!", 10, 40, 20);
            return;
        }

        CustomSpawner spawner = plugin.getSpawnerManager().getSpawner(block.getLocation());
        if (spawner != null) {
            event.setDropItems(false);
            ItemStack spawnerItem = new ItemStack(Material.SPAWNER);
            ItemMeta meta = spawnerItem.getItemMeta();
            meta.setDisplayName("§bSpawner");
            List<String> lore = new ArrayList<>();
            if (spawner.getEntityType() != null) {
                lore.add("§7Typ: §f" + spawner.getEntityType().name());
                lore.add("§7Poziom szybkości: §f" + spawner.getSpeedLevel());
                lore.add("§7Moby na minutę: §f" + spawner.getMobsPerMinute());
            } else {
                lore.add("§7Pusty spawner");
            }
            meta.setLore(lore);
            spawnerItem.setItemMeta(meta);
            block.getWorld().dropItemNaturally(block.getLocation(), spawnerItem);
            plugin.getSpawnerManager().removeSpawner(block.getLocation());
        }
    }

    @EventHandler
    public void onSpawnerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK ||
                event.getClickedBlock() == null ||
                event.getClickedBlock().getType() != Material.SPAWNER) return;

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        Island island = plugin.getIslandManager().getIslandAt(block.getLocation());

        if (island == null) {
            if (!player.hasPermission("oneblock.admin")) {
                event.setCancelled(true);
                return;
            }
            return;
        }

        if (!island.getOwnerId().equals(player.getUniqueId()) &&
                !island.hasPermission(player.getUniqueId(), "SPAWNER_SETTINGS")) {
            event.setCancelled(true);
            player.sendMessage("§cNie masz permisji do zarządzania spawnerami!");
            player.sendMessage("§7Poproś właściciela aby zmienił to pod /panel");
            return;
        }

        event.setCancelled(true);
        CustomSpawner spawner = plugin.getSpawnerManager().getSpawner(block.getLocation());
        if (spawner == null) {
            spawner = new CustomSpawner(block.getLocation());
            plugin.getSpawnerManager().addSpawner(block.getLocation());
        }
        new SpawnerGUI(plugin, player, spawner).open();
    }
}