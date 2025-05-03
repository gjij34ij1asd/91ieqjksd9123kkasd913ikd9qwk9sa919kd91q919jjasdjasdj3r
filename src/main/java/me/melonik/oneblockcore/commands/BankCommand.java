package me.melonik.oneblockcore.commands;

import me.melonik.oneblockcore.Main;
import me.melonik.oneblockcore.gui.BankGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BankCommand implements CommandExecutor {
    private final Main plugin;

    public BankCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Ta komenda jest dostępna tylko dla graczy.");
            return true;
        }

        new BankGUI(plugin, player); // otwieranie GUI wewnątrz tej klasy
        return true;
    }
}
