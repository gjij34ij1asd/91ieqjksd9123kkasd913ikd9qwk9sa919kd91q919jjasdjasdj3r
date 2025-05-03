package me.melonik.oneblockcore.commands;

import me.melonik.oneblockcore.Main;
import me.melonik.oneblockcore.models.Island;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHomeCommand implements CommandExecutor {
    private final Main plugin;

    public SetHomeCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cTa komenda jest dostępna tylko dla graczy!");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("§cUżyj: /sethome <numer>");
            return true;
        }

        try {
            int homeNumber = Integer.parseInt(args[0]);
            if (homeNumber < 1 || homeNumber > 7) {
                player.sendMessage("§cNumer domu musi być między 1 a 7!");
                return true;
            }

            if (!player.hasPermission("oneblockhome." + homeNumber)) {
                player.sendMessage("§cNie masz dostępu do tej ilości homeów!");
                player.sendMessage("§7Zakup rangę na stronie §ewww.zacraft.pl §7aby odblokować");
                return true;
            }

            // Check if player is on their island
            Island island = plugin.getIslandManager().getIslandAt(player.getLocation());
            if (island == null || (!island.getOwnerId().equals(player.getUniqueId()) && !island.getMembers().contains(player.getUniqueId()))) {
                player.sendMessage("§cMożesz ustawić home tylko na swojej wyspie!");
                return true;
            }

            plugin.getHomeManager().setHome(player.getUniqueId(), homeNumber, player.getLocation());
            player.sendMessage("§aUstawiono dom numer " + homeNumber + "!");
        } catch (NumberFormatException e) {
            player.sendMessage("§cNumer domu musi być liczbą!");
        }

        return true;
    }
}