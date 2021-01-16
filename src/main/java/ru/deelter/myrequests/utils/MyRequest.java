package ru.deelter.myrequests.utils;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import org.jetbrains.annotations.NotNull;
import ru.deelter.myrequests.Config;
import ru.deelter.myrequests.MyRequests;
import ru.deelter.myrequests.api.RequestReceiveEvent;
import ru.deelter.myrequests.utils.managers.LoggerManager;
import ru.deelter.myrequests.utils.managers.TimerManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class MyRequest implements Cloneable {

    private static final Map<String, MyRequest> requests = new HashMap<>();

    private final Map<String, String> headers = new HashMap<>();
    private Map<String, String> body = new HashMap<>();

    private String id, response;
    private final String url;

    private int code;
    private boolean isGET = true;

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

    public static void load() {

        FileConfiguration config = MyRequests.getInstance().getConfig();
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

    public void setBody(Map<String, String> body) {
        this.body = body;
    }

    public Map getBody() {
        return body;
    }

    /* Get methods */
    public String getResponse() {
        return response;
    }

    public static MyRequest getRequest(String id) {
        if (!requests.containsKey(id))
            return null;

        return requests.get(id);
    }

    /* Sending */
    public void send() {
        HttpGet get = null;
        HttpPost post = null;
        if (isGET) {
            get = new HttpGet(url);
            headers.forEach(get::addHeader);

            List<NameValuePair> params = new ArrayList<>();
            body.forEach((key, value) -> params.add(new BasicNameValuePair(key, value)));
        } else {
            post = new HttpPost(url);
            headers.forEach(post::addHeader);

            List<NameValuePair> params = new ArrayList<>();
            body.forEach((key, value) -> params.add(new BasicNameValuePair(key, value)));
            try {
                post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(isGET ? get : post)) {

             this.response = EntityUtils.toString(response.getEntity());
             this.code = response.getStatusLine().getStatusCode();
        } catch (IOException e) {
            e.printStackTrace();
        }

        LoggerManager.log(id, response, code);

        /* Call event for api */
        if (Config.PLUGIN_API) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(MyRequests.getInstance(), () -> {
                RequestReceiveEvent receiveEvent = new RequestReceiveEvent(id, response, code);
                Bukkit.getPluginManager().callEvent(receiveEvent);
            });
        }
    }

    /* other */
    public void register() {
        requests.put(id, this);
    }

    public int getResponseCode() {
        return code;
    }
}
