package me.melonik.oneblockcore.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.melonik.oneblockcore.Main;
import me.melonik.oneblockcore.models.Island;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Map;
import java.util.UUID;

public class OneBlockPlaceholders extends PlaceholderExpansion {
    private final Main plugin;

    public OneBlockPlaceholders(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "oneblock";
    }

    @Override
    public String getAuthor() {
        return "Melonik";
    }

    @Override
    public String getVersion() {
        return "ZacolecOneBlock";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (player == null) return "";

        if (params.equals("has_island")) {
            return plugin.getIslandManager().hasIsland(player.getUniqueId()) ? "1" : "0";
        }

        if (params.equals("generator_level")) {
            Island island = plugin.getIslandManager().getPlayerIsland(player.getUniqueId());
            return island != null ? String.valueOf(island.getMaxLevel()) : "0";
        }

        if (params.equals("generator_progress")) {
            Island island = plugin.getIslandManager().getPlayerIsland(player.getUniqueId());
            if (island != null) {
                int maxLevel = island.getMaxLevel();
                int highestProgress = 0;

                for (int i = 1; i <= maxLevel; i++) {
                    int progress = island.getGenerator().getProgressForLevel(i);
                    if (progress > highestProgress) {
                        highestProgress = progress;
                    }
                }

                return String.format("§8[§7%d§8/§7%d%%§8]", maxLevel, highestProgress);
            }
            return "§8[1/0%]";
        }

        if (params.equals("generator_star")) {
            Island island = plugin.getIslandManager().getPlayerIsland(player.getUniqueId());
            if (island != null && island.getMaxLevel() == 7) {
                return " §b★";
            }
            return "";
        }

        if (params.equals("generator_progress_bar")) {
            Island island = plugin.getIslandManager().getPlayerIsland(player.getUniqueId());
            if (island != null) {
                int maxLevel = island.getMaxLevel();
                int highestProgress = 0;

                for (int i = 1; i <= maxLevel; i++) {
                    int progress = island.getGenerator().getProgressForLevel(i);
                    if (progress > highestProgress) {
                        highestProgress = progress;
                    }
                }

                String progressBar = plugin.getGeneratorManager().getProgressBar(highestProgress);
                return String.format("§8[%s§8] §b%d%%", progressBar, highestProgress);
            }
            return "0%";
        }

        if (params.startsWith("money_top_")) {
            try {
                int position = Integer.parseInt(params.substring(10)) - 1;
                Map.Entry<UUID, Double> entry = plugin.getEconomyManager().getMoneyTopList(position + 1).get(position);
                OfflinePlayer topPlayer = Bukkit.getOfflinePlayer(entry.getKey());
                return String.format("§3%s §8(§a%s$§8)",
                        topPlayer.getName(),
                        formatMoney(entry.getValue()));
            } catch (Exception e) {
                return "§3Brak §8(§a0$§8)";
            }
        }

        if (params.startsWith("bank_top_")) {
            try {
                int position = Integer.parseInt(params.substring(9)) - 1;
                Map.Entry<UUID, Double> entry = plugin.getEconomyManager().getBankTopList(position + 1).get(position);
                OfflinePlayer topPlayer = Bukkit.getOfflinePlayer(entry.getKey());
                return String.format("§3%s §8(§a%s$§8)",
                        topPlayer.getName(),
                        formatMoney(entry.getValue()));
            } catch (Exception e) {
                return "§3Brak §8(§a0$§8)";
            }
        }

        if (params.equals("money")) {
            return formatMoney(plugin.getEconomyManager().getPlayerMoney(player.getUniqueId()));
        }

        if (params.equals("bank")) {
            return formatMoney(plugin.getEconomyManager().getBankMoney(player.getUniqueId()));
        }

        return null;
    }

    private String formatMoney(double amount) {
        if (amount >= 1_000_000_000) {
            return String.format("%.1fmld", amount / 1_000_000_000);
        } else if (amount >= 1_000_000) {
            return String.format("%.1fmln", amount / 1_000_000);
        } else if (amount >= 1_000) {
            return String.format("%.1fk", amount / 1_000);
        } else {
            return String.format("%.1f", amount);
        }
    }
}
