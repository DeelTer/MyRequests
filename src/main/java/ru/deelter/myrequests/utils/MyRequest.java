package ru.deelter.myrequests.utils;

import okhttp3.*;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import ru.deelter.myrequests.Config;
import ru.deelter.myrequests.Main;
import ru.deelter.myrequests.api.RequestReceiveEvent;
import ru.deelter.myrequests.utils.managers.LoggerManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MyRequest implements Cloneable {

    private final OkHttpClient client = new OkHttpClient();
    private static final Map<String, MyRequest> requests = new HashMap<>();

    private final Map<String, String> header = new HashMap<>();
    private Map<String, String> body = new HashMap<>();

    private final String url;
    private String id;
    private String type;

    private String response = "none";

    private int responseCode = 0;

    public MyRequest(String url) {
        this.url = url;
    }

    @NotNull
    public MyRequest clone() {
        try {
            return (MyRequest) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

    /* Set methods */
    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setBody(Map<String, String> body) {
        this.body = body;
    }

    public void addHeader(String key, String value) {
        header.put(key, Other.setPlaceholders(value));
    }

    public void addBody(String key, String value) {
        body.put(key, Other.setPlaceholders(value));
    }

    /* Get methods */
    public Map<String, String> getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return header;
    }

    public boolean isGet() {
        return type.equalsIgnoreCase("GET");
    }

    public String getResponse() {
        return response;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public static MyRequest getRequest(String id) {
        if (!requests.containsKey(id))
            return null;

        return requests.get(id);
    }

    /* other */
    public void register() {
        requests.put(id, this);
    }

    /* Sending */
    public void send() {
        Request request;
        /* SEND GET */
        if (isGet()) {
            Request.Builder builder = new Request.Builder();
            builder.url(url);

            header.forEach(builder::addHeader);
            request = builder.build();
        }
        /* Send POST */
        else {
            FormBody.Builder formBody = new FormBody.Builder();
            body.forEach(formBody::add);

            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.url(url);

            header.forEach(requestBuilder::addHeader);
            FormBody body = formBody.build();
            requestBuilder.post(body);

            request = requestBuilder.build();
        }
        /* Sending */
        try (Response response = client.newCall(request).execute()) {
            this.responseCode = response.code();
            if(!response.isSuccessful())
                return;

            this.response = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        LoggerManager.log(id, response, responseCode);

        /* Call event for api */
        if (Config.PLUGIN_API) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                RequestReceiveEvent receiveEvent = new RequestReceiveEvent(id, response, responseCode, responseCode);
                Bukkit.getPluginManager().callEvent(receiveEvent);
            });
        }
    }
}
