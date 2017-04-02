package com.fagi.utility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by costa on 02-04-2017.
 */
public class Logger {
    public static final String LOGS_FOLDER_PATH = "logs/";

    public static void logStackTrace(Exception e) {
        long time = System.nanoTime();
        String filePath = LOGS_FOLDER_PATH + "log-error-" + time + JsonFileOperations.FAGI_EXTENSION;
        File folder = new File(LOGS_FOLDER_PATH);
        File logFile = new File(filePath);
        if (!folder.exists()) {
            folder.mkdir();
        }
        try {
            logFile.createNewFile();

            PrintWriter out = new PrintWriter(new FileWriter(logFile), false);
            e.printStackTrace(out);
            out.flush();
            out.close();
        } catch (IOException ioe) {
            e.printStackTrace();
        }
    }
}
