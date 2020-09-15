package ru.deelter.myrequests.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import ru.deelter.myrequests.Config;
import ru.deelter.myrequests.Main;

import java.util.UUID;

public class Other {

    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static void log(String s) {
        if (Config.CONSOLE_LOGS)
            Main.getInstance().getLogger().info(color(s));
    }

    public static String setPlaceholders(String text) {
        if (!Config.PLACEHOLDER_API)
            return text;

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString("9c21fd5b-ad42-42c5-94fb-4b4585ca630f"));
        return PlaceholderAPI.setPlaceholders(offlinePlayer, text);
    }
}
