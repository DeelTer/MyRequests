package ru.deelter.myrequests;

import org.bukkit.plugin.java.JavaPlugin;
import ru.deelter.myrequests.commands.Request;
import ru.deelter.myrequests.utils.MyRequest;
import ru.deelter.myrequests.utils.Other;

import java.io.File;

public final class MyRequests extends JavaPlugin {

    private static JavaPlugin instance;

    public static JavaPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        File config = new File(instance.getDataFolder().getPath() + "/config.yml");
        if (!config.exists()) {
            Other.log("&cThe config doesn't exist. Create a new one");
            saveDefaultConfig();
        }
        Config.reload();
        MyRequest.load();
        getCommand("myrequest").setExecutor(new Request());

        /* Final console icon */
        Other.log("\n" +
                "&8######################################### \n" +
                "&6█▄&f░&6▄█ ▀▄&f░&6▄▀ █▀▀▄ █▀▀ ▄▀█ █&f░&6█ █▀▀ ▄▀▀ ▀█▀ \n" +
                "&6█&f░&6█&f░&6█ &f░░&6█&f░░&6 █▐█▀ █▀▀ █&f░&6█ █&f░&6█ █▀▀ &f░&6▀▄ &f░&6█&f░ \n" +
                "&6▀&f░░░&6▀ &f░░&6▀&f░░ &6▀&f░&6▀▀ ▀▀▀ &f░&6▀█ &f░&6▀&f░ &6▀▀▀ ▀▀&f░ ░&6▀&f░ \n" +
                "&8            &fBY DEELTER\n" +
                "&8#########################################");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
