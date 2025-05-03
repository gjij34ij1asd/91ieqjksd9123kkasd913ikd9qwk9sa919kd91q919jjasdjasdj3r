package me.melonik.oneblockcore.listeners;

import me.melonik.oneblockcore.Main;
import me.melonik.oneblockcore.models.Island;
import org.bukkit.block.Hopper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class HopperListener implements Listener {
    private final Main plugin;

    public HopperListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onHopperMove(InventoryMoveItemEvent event) {
        if (!(event.getInitiator().getHolder() instanceof Hopper)) return;

        Hopper hopper = (Hopper) event.getInitiator().getHolder();
        Island island = plugin.getIslandManager().getIslandAt(hopper.getLocation());

        if (island != null) {
            int speedLevel = island.getUpgrades().getHopperSpeedLevel();
            if (speedLevel > 1) {
                event.setCancelled(true);

                Inventory source = event.getSource();
                Inventory destination = event.getDestination();
                ItemStack originalItem = event.getItem().clone();

                int maxTransfer = speedLevel;
                int availableSpace = 0;

                // Sprawdź dostępne miejsce w docelowym inventory
                for (ItemStack item : destination.getStorageContents()) {
                    if (item == null) {
                        availableSpace += originalItem.getMaxStackSize();
                    } else if (item.isSimilar(originalItem)) {
                        availableSpace += originalItem.getMaxStackSize() - item.getAmount();
                    }
                }

                // Znajdź dostępne przedmioty w źródłowym inventory
                int availableItems = 0;
                for (ItemStack item : source.getContents()) {
                    if (item != null && item.isSimilar(originalItem)) {
                        availableItems += item.getAmount();
                    }
                }

                // Oblicz ile przedmiotów faktycznie przenieść
                int itemsToMove = Math.min(Math.min(maxTransfer, availableItems), availableSpace);

                if (itemsToMove > 0) {
                    ItemStack itemsToTransfer = originalItem.clone();
                    itemsToTransfer.setAmount(itemsToMove);

                    // Usuń przedmioty ze źródła
                    int remainingToRemove = itemsToMove;
                    for (ItemStack item : source.getContents()) {
                        if (item != null && item.isSimilar(originalItem)) {
                            int toRemove = Math.min(remainingToRemove, item.getAmount());
                            item.setAmount(item.getAmount() - toRemove);
                            remainingToRemove -= toRemove;
                            if (remainingToRemove <= 0) break;
                        }
                    }

                    // Dodaj przedmioty do celu
                    destination.addItem(itemsToTransfer);
                }
            }
        }
    }

    @EventHandler
    public void onHopperPickup(InventoryPickupItemEvent event) {
        if (!(event.getInventory().getHolder() instanceof Hopper)) return;

        Hopper hopper = (Hopper) event.getInventory().getHolder();
        Island island = plugin.getIslandManager().getIslandAt(hopper.getLocation());

        if (island != null) {
            int speedLevel = island.getUpgrades().getHopperSpeedLevel();
            if (speedLevel > 1) {
                ItemStack originalItem = event.getItem().getItemStack();
                int amount = Math.min(speedLevel, originalItem.getAmount());

                if (amount > 1) {
                    ItemStack newItem = originalItem.clone();
                    newItem.setAmount(amount);
                    event.getItem().setItemStack(newItem);
                }
            }
        }
    }
}