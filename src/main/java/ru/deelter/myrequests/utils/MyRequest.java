package ru.deelter.myrequests.utils;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import okhttp3.Response;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import ru.deelter.myrequests.Config;
import ru.deelter.myrequests.Main;
import ru.deelter.myrequests.api.RequestReceiveEvent;
import ru.deelter.myrequests.utils.managers.LoggerManager;
import ru.deelter.myrequests.utils.managers.TimerManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MyRequest {

    private final OkHttpClient client = new OkHttpClient();

    private static final Map<String, MyRequest> requests = new HashMap<>();
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> body = new HashMap<>();

    private String id;
    private final String url;
    private String response;

    private int responseCode;
    private boolean isGET = true;

    public MyRequest(String url) {
        this.url = url;
    }

    public static void load() {

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

    /* Set methods */
    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.isGET = type.equalsIgnoreCase("GET");
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void addBody(String key, String value) {
        body.put(key, value);
    }

    /* Get methods */
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
        if (isGET) {
            Request.Builder builder = new Request.Builder();
            builder.url(url);

            headers.forEach(builder::addHeader);
            request = builder.build();
        }
        /* Send POST */
        else {
            FormBody.Builder formBody = new FormBody.Builder();
            body.forEach(formBody::add);

            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.url(url);

            headers.forEach(requestBuilder::addHeader);
            final FormBody body = formBody.build();
            requestBuilder.post(body);

            request = requestBuilder.build();
        }
        /* Sending */
        try (Response response = client.newCall(request).execute()) {
            this.responseCode = response.code();
            if(!response.isSuccessful())
                return;

            this.response = Objects.requireNonNull(response.body()).string();
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
