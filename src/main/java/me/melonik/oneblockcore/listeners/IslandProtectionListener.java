package me.melonik.oneblockcore.listeners;

import me.melonik.oneblockcore.Main;
import me.melonik.oneblockcore.models.Island;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.block.Container;

public class IslandProtectionListener implements Listener {
    private final Main plugin;

    public IslandProtectionListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        // Check if we're in the oneblock world
        if (!block.getWorld().getName().equals("oneblock")) {
            return;
        }

        Island island = plugin.getIslandManager().getIslandAt(block.getLocation());
        if (island == null) {
            if (!player.hasPermission("oneblock.admin")) {
                event.setCancelled(true);
                player.sendTitle("§4§lBłąd!", "§cNie możesz niszczyć bloków poza wyspą!", 10, 40, 20);
            }
            return;
        }

        if (island.getOwnerId().equals(player.getUniqueId())) {
            if (block.getType() == Material.HOPPER) {
                island.getUpgrades().decrementHopperCount();
            }
            return;
        }

        if (!island.getMembers().contains(player.getUniqueId()) && !island.getCoopPlayers().contains(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendTitle("§4§lBłąd!", "§cNie możesz niszczyć bloków na tej wyspie!", 10, 40, 20);
            return;
        }

        String permission = "BREAK";
        if (block.getType() == Material.BEACON) {
            permission = "BEACON_BREAK";
        } else if (block.getType() == Material.SPAWNER) {
            permission = "SPAWNER_MANAGE";
        }

        if (!island.hasPermission(player.getUniqueId(), permission) &&
                !island.getCoopPermission(permission)) {
            event.setCancelled(true);
            player.sendTitle("§4§lBłąd!", "§cNie masz permisji do niszczenia bloków na tej wyspie!", 10, 40, 20);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        // Check if we're in the oneblock world
        if (!block.getWorld().getName().equals("oneblock")) {
            return;
        }

        Island island = plugin.getIslandManager().getIslandAt(block.getLocation());
        if (island == null) {
            if (!player.hasPermission("oneblock.admin")) {
                event.setCancelled(true);
                player.sendTitle("§4§lBłąd!", "§cNie możesz stawiać bloków poza wyspą!", 10, 40, 20);
            }
            return;
        }

        boolean exceedsLimit = false;
        String limitMessage = "";
        int remaining = 0;

        switch (block.getType()) {
            case SPAWNER:
                if (island.getUpgrades().getSpawnerCount() >= island.getUpgrades().getSpawnerLimit()) {
                    exceedsLimit = true;
                    limitMessage = "spawnerów";
                } else {
                    remaining = island.getUpgrades().getSpawnerLimit() - island.getUpgrades().getSpawnerCount();
                    player.sendMessage("§a§lLimit! §7Możesz jeszcze postawić " + remaining + " spawnerów");
                }
                break;
            case HOPPER:
                if (island.getUpgrades().getHopperCount() >= island.getUpgrades().getHopperLimit()) {
                    exceedsLimit = true;
                    limitMessage = "lejków";
                } else {
                    remaining = island.getUpgrades().getHopperLimit() - island.getUpgrades().getHopperCount();
                    player.sendMessage("§a§lLimit! §7Możesz jeszcze postawić " + remaining + " lejków");
                }
                break;
            case PISTON:
            case STICKY_PISTON:
                if (island.getUpgrades().getPistonCount() >= island.getUpgrades().getPistonLimit()) {
                    exceedsLimit = true;
                    limitMessage = "pistonów";
                } else {
                    remaining = island.getUpgrades().getPistonLimit() - island.getUpgrades().getPistonCount();
                    player.sendMessage("§a§lLimit! §7Możesz jeszcze postawić " + remaining + " pistonów");
                }
                break;
        }

        if (exceedsLimit) {
            event.setCancelled(true);
            player.sendTitle("§4§lBłąd!", "§cOsiągnięto limit " + limitMessage + " na tej wyspie!", 10, 40, 20);
            return;
        }

        if (!island.getOwnerId().equals(player.getUniqueId()) && !island.hasPermission(player.getUniqueId(), "BUILD")) {
            event.setCancelled(true);
            player.sendTitle("§4§lBłąd!", "§cNie masz permisji do budowania na tej wyspie!", 10, 40, 20);
            return;
        }

        switch (block.getType()) {
            case SPAWNER:
                island.getUpgrades().setSpawnerCount(island.getUpgrades().getSpawnerCount() + 1);
                break;
            case HOPPER:
                island.getUpgrades().setHopperCount(island.getUpgrades().getHopperCount() + 1);
                break;
            case PISTON:
            case STICKY_PISTON:
                island.getUpgrades().setPistonCount(island.getUpgrades().getPistonCount() + 1);
                break;
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.PHYSICAL) return;

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block == null) return;

        // Check if we're in the oneblock world
        if (!block.getWorld().getName().equals("oneblock")) {
            return;
        }

        Island island = plugin.getIslandManager().getIslandAt(block.getLocation());
        if (island == null) {
            if (!player.hasPermission("oneblock.admin")) {
                event.setCancelled(true);
                player.sendTitle("§4§lBłąd!", "§cNie możesz wchodzić w interakcje poza wyspą!", 10, 40, 20);
            }
            return;
        }

        if (island.getOwnerId().equals(player.getUniqueId())) {
            return;
        }

        if (!island.getMembers().contains(player.getUniqueId()) && !island.getCoopPlayers().contains(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendTitle("§4§lBłąd!", "§cNie możesz wchodzić w interakcje na tej wyspie!", 10, 40, 20);
            return;
        }

        String permission = null;
        if (block.getState() instanceof Container) {
            permission = "CHEST";
        } else if (block.getType() == Material.FURNACE) {
            permission = "FURNACE";
        } else if (block.getType().name().contains("DOOR") ||
                block.getType().name().contains("GATE") ||
                block.getType().name().contains("TRAPDOOR")) {
            permission = "DOORS";
        } else if (block.getType().name().contains("BUTTON") ||
                block.getType().name().contains("PLATE") ||
                block.getType() == Material.LEVER) {
            permission = "REDSTONE";
        } else if (block.getType() == Material.SPAWNER) {
            permission = "SPAWNER_SETTINGS";
        }

        if (permission != null && !island.hasPermission(player.getUniqueId(), permission) &&
                !island.getCoopPermission(permission)) {
            event.setCancelled(true);
            player.sendTitle("§4§lBłąd!", "§cNie możesz tego używać na tej wyspie!", 10, 40, 20);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;

        Player player = (Player) event.getDamager();

        // Check if we're in the oneblock world
        if (!event.getEntity().getWorld().getName().equals("oneblock")) {
            return;
        }

        Island island = plugin.getIslandManager().getIslandAt(event.getEntity().getLocation());
        if (island == null) {
            if (!player.hasPermission("oneblock.admin")) {
                event.setCancelled(true);
                player.sendTitle("§4§lBłąd!", "§cNie możesz atakować mobów poza wyspą!", 10, 40, 20);
            }
            return;
        }

        if (island.getOwnerId().equals(player.getUniqueId())) {
            return;
        }

        if (event.getEntity() instanceof Monster && !island.isMobDamage()) {
            event.setCancelled(true);
            return;
        }

        if (event.getEntity() instanceof Animals && !island.isAnimalDamage()) {
            event.setCancelled(true);
            return;
        }

        String permission = null;
        if (event.getEntity() instanceof Monster) {
            permission = "ATTACK_MOBS";
        } else if (event.getEntity() instanceof Animals) {
            permission = "ATTACK_ANIMALS";
        }

        if (permission != null && !island.hasPermission(player.getUniqueId(), permission) &&
                !island.getCoopPermission(permission)) {
            event.setCancelled(true);
            player.sendTitle("§4§lBłąd!", "§cNie masz permisji do atakowania na tej wyspie!", 10, 40, 20);
        }
    }
}