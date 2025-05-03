package me.melonik.oneblockcore.listeners;

import me.melonik.oneblockcore.Main;
import me.melonik.oneblockcore.gui.BankGUI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NPCListener implements Listener {
    private final BankGUI bankGUI;

    public NPCListener(Main plugin, BankGUI bankGUI) {
        this.bankGUI = bankGUI;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onNPCClick(NPCRightClickEvent event) {
        if (event.getNPC().getName().equalsIgnoreCase("bank_npc")) {
            Player player = event.getClicker();
            bankGUI.openGUI(player);
        }
    }
}
