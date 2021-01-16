package ru.deelter.myrequests.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RequestReceiveEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final String id;
    private final String response;

    private int code;

    public RequestReceiveEvent(String id, String response, int code) {
        this.id = id;
        this.code = code;
        this.response = response;
    }

    public String getId() {
        return id;
    }

    public int getCode() {
        return code;
    }

    public String getResponse() {
        return response;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}