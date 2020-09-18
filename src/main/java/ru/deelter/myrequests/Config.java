package ru.deelter.myrequests;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import ru.deelter.myrequests.utils.MyRequest;
import ru.deelter.myrequests.utils.Other;
import ru.deelter.myrequests.utils.managers.TimerManager;

import java.util.Objects;

public class Config {

    /* Messages */
    public static String MSG_INVALID_ID;
    public static String MSG_SENDING_REQUEST;
    public static String MSG_NO_PERMISSION;
    public static String MSG_PLAYER_REQUEST;

    /* Settings */
    public static String SPACE_SYMBOL = "%20";

    public static boolean PLACEHOLDER_API = false;
    public static boolean CONSOLE_LOGS = false;
    public static boolean FILE_LOGS = false;
    public static boolean PLUGIN_API = false;

    public static void reload() {
        Main.getInstance().reloadConfig();

        FileConfiguration config = Main.getInstance().getConfig();
        ConfigurationSection settings = config.getConfigurationSection("settings");
        if (settings != null) {
            /* PlaceholderAPI hook */
            if (settings.getBoolean("placeholder-api")) {
                PLACEHOLDER_API = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
                Other.log(PLACEHOLDER_API ? "&fModule &ePlaceholder-API&f successfully enabled" : "&cError: &6Placeholder-API&c not found");
            }
            SPACE_SYMBOL = settings.getString("space-symbol");
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
        loadRequests();
    }

    private static void loadRequests() {

        FileConfiguration config = Main.getInstance().getConfig();
        ConfigurationSection requests = config.getConfigurationSection("requests");
        if (requests == null) {
            Other.log("&cВ конфиге нет раздела 'requests'");
            return;
        }

        requests.getKeys(false).forEach(request -> {
            ConfigurationSection settings = config.getConfigurationSection("requests." + request);
            if (settings == null) {
                Other.log("&cВ конфиге нет раздела '" + request + "'");
                return;
            }

            MyRequest myRequest = new MyRequest(settings.getString("url"));
            myRequest.setType(Objects.requireNonNull(settings.getString("type")));
            myRequest.setId(request);

            /* Add header */
            settings.getStringList("headers").forEach(header -> {
                String[] param = header.split("=");
                myRequest.addHeader(param[0], Other.setPlaceholders(param[1]));
            });

            /* Add body */
            settings.getStringList("body").forEach(header -> {
                String[] param = header.split("=");
                myRequest.addBody(param[0], Other.setPlaceholders(param[1]));
            });

            myRequest.register();

            /* Check timers */
            if (settings.getBoolean("timer.enable")) {
                TimerManager.startTimer(myRequest, settings.getInt("timer.seconds"));
            }

            Other.log("&fЗарегистрирован запрос '&e" + request + "&f'");
        });
    }
}
