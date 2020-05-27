package com.fagi.utility;

import com.fagi.conversation.Conversation;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by costa on 09-11-2016.
 */
public class JsonFileOperations {
    public static final String FAGI_EXTENSION = ".fagi";
    public static final String CONFIG_FOLDER_PATH = "config/";
    public static final String CONVERSATION_FOLDER_PATH = "conversations/";
    public static final String INVITE_CODES_FILE = "codes";
    public static final String INVITE_CODES_FILE_PATH = CONFIG_FOLDER_PATH + INVITE_CODES_FILE + FAGI_EXTENSION;
    public static final String USERS_FOLDER = "users/";

    public static <T extends Serializable> void storeObjectToFile(
            T object,
            String folderPath,
            String fileName) {
        try {
            File folder = new File(folderPath);
            File file = new File(folderPath + fileName + FAGI_EXTENSION);
            if (!folder.exists()) {
                folder.mkdir();
            }
            if (!file.exists()) {
                file.createNewFile();
            }

            Gson gson = new Gson();

            PrintWriter out = new PrintWriter(new FileWriter(folderPath + fileName + FAGI_EXTENSION, false));
            out.println(gson.toJson(object));
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            Logger.logStackTrace(e);
        }
    }

    public static <T extends Serializable> T loadObjectFromFile(
            String fileName,
            Class<T> clazz) {
        T res = null;
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                return null;
            }

            StringBuilder json = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while (reader.ready()) {
                json.append(reader.readLine());
            }

            Gson gson = new Gson();

            res = gson.fromJson(json.toString(), clazz);
        } catch (IOException e) {
            e.printStackTrace();
            Logger.logStackTrace(e);
        }
        return res;
    }

    public static <T extends Serializable> List<T> loadAllObjectsInFolder(
            String folderName,
            Class<T> clazz) {
        List<T> res = new CopyOnWriteArrayList<>();

        File folder = new File(folderName);
        if (!folder.exists()) {
            return res;
        }

        File[] files = folder.listFiles();
        if (files == null) {
            return res;
        }

        for (File file : files) {
            var loadedObj = loadObjectFromFile(file.getAbsolutePath(), clazz);
            if (loadedObj == null) {
                System.err.println("Warning: We were about to add null due to this file path: " + file.getAbsolutePath());
                continue;
            }
            res.add(loadedObj);
        }

        return res;
    }

    public static void storeConversation(Conversation c) {
        storeObjectToFile(c, CONVERSATION_FOLDER_PATH, c.getId() + "");
    }

    public static void storeClientConversation(
            Conversation c,
            String username) {
        File clientFolder = new File(username + "/");
        if (!clientFolder.exists()) {
            clientFolder.mkdir();
        }
        storeObjectToFile(c, username + "/" + CONVERSATION_FOLDER_PATH, c.getId() + "");
    }

    public static List<Conversation> loadAllConversations() {
        return loadAllObjectsInFolder(CONVERSATION_FOLDER_PATH, Conversation.class);
    }

    public static List<Conversation> loadAllClientConversations(String username) {
        return loadAllObjectsInFolder(username + "/" + CONVERSATION_FOLDER_PATH, Conversation.class);
    }
}
