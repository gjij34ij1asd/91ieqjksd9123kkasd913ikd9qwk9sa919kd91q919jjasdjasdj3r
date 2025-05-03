package me.melonik.oneblockcore.listeners;

import me.melonik.oneblockcore.Main;
import me.melonik.oneblockcore.models.Island;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {
    private final Main plugin;

    public BlockPlaceListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        // Only apply protection in oneblock world
        if (!block.getWorld().getName().equals("oneblock")) {
            return;
        }

        Island island = plugin.getIslandManager().getIslandAt(block.getLocation());
        if (island == null) {
            event.setCancelled(true);
            player.sendTitle("§4§lBłąd!", "§cNie możesz stawiać bloków poza wyspą!", 10, 40, 20);
            return;
        }

        boolean exceedsLimit = false;
        String limitMessage = "";
        int remaining = 0;

        switch (block.getType()) {
            case HOPPER:
                if (island.getUpgrades().getHopperCount() >= island.getUpgrades().getHopperLimit()) {
                    exceedsLimit = true;
                } else {
                    remaining = island.getUpgrades().getHopperLimit() - island.getUpgrades().getHopperCount();
                    player.sendMessage("§3§lUwaga! §7Jeszcze możesz postawić " + remaining + " lejków");
                }
                break;
            case PISTON:
            case STICKY_PISTON:
                if (island.getUpgrades().getPistonCount() >= island.getUpgrades().getPistonLimit()) {
                    exceedsLimit = true;
                } else {
                    remaining = island.getUpgrades().getPistonLimit() - island.getUpgrades().getPistonCount();
                    player.sendMessage("§3§lUwaga! §7Jeszcze możesz postawić " + remaining + " tłoków");
                }
                break;
        }

        if (exceedsLimit) {
            event.setCancelled(true);
            player.sendTitle("§4§lBłąd!", "§cOsiągnięto limit bloków tego typu!", 10, 40, 20);
            return;
        }

        if (!island.getOwnerId().equals(player.getUniqueId()) && !island.hasPermission(player.getUniqueId(), "BUILD")) {
            event.setCancelled(true);
            player.sendTitle("§4§lBłąd!", "§cNie masz permisji do budowania na tej wyspie!", 10, 40, 20);
            return;
        }

        // Update block counters
        switch (block.getType()) {
            case HOPPER:
                island.getUpgrades().setHopperCount(island.getUpgrades().getHopperCount() + 1);
                break;
            case PISTON:
            case STICKY_PISTON:
                island.getUpgrades().setPistonCount(island.getUpgrades().getPistonCount() + 1);
                break;
        }
    }
}