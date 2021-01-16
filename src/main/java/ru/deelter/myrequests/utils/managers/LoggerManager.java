package ru.deelter.myrequests.utils.managers;

import ru.deelter.myrequests.Config;
import ru.deelter.myrequests.MyRequests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggerManager {

    /* Just log */
    public static void log(String id, String response, int code) {
        if (!Config.FILE_LOGS)
            return;

        String date = new SimpleDateFormat("[dd-MM-yyyy]").format(new Date());
        File folder = new File(MyRequests.getInstance().getDataFolder() + File.separator + "logs");
        if (!folder.exists())
            folder.mkdir();

        File dateFolder = new File(folder + "/" + date);
        if (!dateFolder.exists())
            dateFolder.mkdir();

        File file = new File(dateFolder, id + ".txt");
        int i = 1;
        while (file.exists()) {
            file = new File(dateFolder, id + i++ + ".txt");
        }

        try {
            if(!file.exists())
                file.createNewFile();

            FileWriter writer = new FileWriter(file.getPath(), true);
            BufferedWriter bufferWriter = new BufferedWriter(writer);
            bufferWriter.write("\nid: '" + id + "\ncode: '" + code + "'" + "\nresponse: '" + response + "'");
            bufferWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
