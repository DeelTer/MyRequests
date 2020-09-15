package ru.deelter.myrequests;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import ru.deelter.myrequests.utils.Other;

public class Config {

    public static String MSG_INVALID_ID;
    public static String MSG_SENDING_REQUEST;
    public static String MSG_NO_PERMISSION;
    public static String MSG_PLAYER_REQUEST;

    public static boolean PLACEHOLDER_API = false;
    public static boolean CONSOLE_LOGS = false;
    public static boolean FILE_LOGS = false;
    public static boolean PLUGIN_API = false;

    public static void reload() {
        FileConfiguration config = Main.getInstance().getConfig();
        ConfigurationSection settings = config.getConfigurationSection("settings");
        if (settings != null) {
            /* PlaceholderAPI hook */
            if (settings.getBoolean("placeholder-api")) {
                PLACEHOLDER_API = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
                Other.log(PLACEHOLDER_API ? "&fModule &ePlaceholder-API&f successfully enabled" : "&cError: &6Placeholder-API&c not found");
            }
            PLUGIN_API = settings.getBoolean("plugin-api");

            /* Logs settings */
            CONSOLE_LOGS = settings.getBoolean("console-logs");
            FILE_LOGS = settings.getBoolean("file-logs");
        }

        /* Messages settings */
        ConfigurationSection messages = config.getConfigurationSection("messages");
        if (messages != null) {
            MSG_INVALID_ID = Other.color(messages.getString("invalid-id"));
            MSG_SENDING_REQUEST = Other.color(messages.getString("sending-request"));
            MSG_NO_PERMISSION = Other.color(messages.getString("no-permission"));
            MSG_PLAYER_REQUEST = Other.color(messages.getString("player-request"));
        }
        Main.getInstance().reloadConfig();
    }
}
