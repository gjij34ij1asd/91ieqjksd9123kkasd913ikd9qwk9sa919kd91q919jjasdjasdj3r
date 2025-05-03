package me.melonik.oneblockcore.models;

import org.bukkit.Location;

public class Home {
    private final int number;
    private Location location;

    public Home(int number, Location location) {
        this.number = number;
        this.location = location;
    }

    public int getNumber() {
        return number;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}