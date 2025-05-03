package me.melonik.oneblockcore.commands;

import me.melonik.oneblockcore.Main;
import me.melonik.oneblockcore.gui.HomesGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCommand implements CommandExecutor {
    private final Main plugin;

    public HomeCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cTa komenda jest dostępna tylko dla graczy!");
            return true;
        }

        new HomesGUI(plugin, player).open();
        return true;
    }
}