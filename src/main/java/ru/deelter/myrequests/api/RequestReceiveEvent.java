package ru.deelter.myrequests.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RequestReceiveEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final String id;
    private final String response;
    private final int code;

    public RequestReceiveEvent(String id, String response, int responseCode, int code) {
        this.id = id;
        this.response = response;
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public String getResponse() {
        return response;
    }

    public int getCode() {
        return code;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}