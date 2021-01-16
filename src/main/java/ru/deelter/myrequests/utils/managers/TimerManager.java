package ru.deelter.myrequests.utils.managers;

import org.bukkit.Bukkit;
import ru.deelter.myrequests.MyRequests;
import ru.deelter.myrequests.utils.MyRequest;

public class TimerManager {

    public static void startTimer(MyRequest myRequest, int seconds) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(MyRequests.getInstance(), myRequest::send, 0, seconds * 20L);
    }
}
