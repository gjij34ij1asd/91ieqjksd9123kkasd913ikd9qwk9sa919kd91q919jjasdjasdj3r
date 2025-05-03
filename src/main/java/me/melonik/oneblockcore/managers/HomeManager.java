package me.melonik.oneblockcore.managers;

import me.melonik.oneblockcore.Main;
import me.melonik.oneblockcore.models.Home;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HomeManager {
    private final Main plugin;
    private final Map<UUID, Map<Integer, Home>> homes;

    public HomeManager(Main plugin) {
        this.plugin = plugin;
        this.homes = new ConcurrentHashMap<>();
    }

    public void setHome(UUID playerId, int number, Location location) {
        homes.computeIfAbsent(playerId, k -> new HashMap<>())
                .put(number, new Home(number, location));
    }

    public void removeHome(UUID playerId, int number) {
        if (homes.containsKey(playerId)) {
            homes.get(playerId).remove(number);
        }
    }

    public Home getHome(UUID playerId, int number) {
        return homes.getOrDefault(playerId, new HashMap<>()).get(number);
    }

    public Map<Integer, Home> getPlayerHomes(UUID playerId) {
        return homes.getOrDefault(playerId, new HashMap<>());
    }

    public void loadHomes(Map<UUID, Map<Integer, Home>> homes) {
        this.homes.clear();
        this.homes.putAll(homes);
    }

    public Map<UUID, Map<Integer, Home>> getAllHomes() {
        return new HashMap<>(homes);
    }
}