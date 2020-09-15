package ru.deelter.myrequests.utils.managers;

import org.bukkit.Bukkit;
import ru.deelter.myrequests.Main;
import ru.deelter.myrequests.utils.MyRequest;

public class TimerManager {

    public static void startTimer(MyRequest myRequest, int seconds) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), () -> myRequest.send(), 0, seconds * 20L);
    }

    public static void stopTimer(String timer) {}
}
