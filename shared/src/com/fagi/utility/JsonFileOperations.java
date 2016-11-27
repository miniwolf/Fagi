package com.fagi.utility;

import com.fagi.conversation.Conversation;
import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by costa on 09-11-2016.
 */
public class JsonFileOperations {
    public static final String CONVERSATION_FOLDER_PATH = "conversations/";
    public static final String FAGI_EXTENSION = ".fagi";

    public static <T extends Serializable> void storeObjectToFile(T object, String folderPath, String fileName) {
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
        }
    }

    public static <T extends Serializable> T loadObjectFromFile(String fileName, Class<T> clazz) {
        T res = null;
        try {
            File file = new File(fileName);
            if (!file.exists()) return null;

            String json = "";
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while ( reader.ready() ) {
                json += reader.readLine();
            }

            Gson gson = new Gson();

            res = gson.fromJson(json, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static <T extends Serializable> List<T> loadAllObjectsInFolder(String folderName, Class<T> clazz) {
        List<T> res = new ArrayList<>();

        File folder = new File(folderName);
        if (!folder.exists()) return res;

        File[] files = folder.listFiles();
        if (files == null) return res;

        for (File file : files) {
            res.add(loadObjectFromFile(file.getAbsolutePath(), clazz));
        }

        return res;
    }

    public static void storeConversation(Conversation c) {
        storeObjectToFile(c, CONVERSATION_FOLDER_PATH, c.getId() + "");
    }

    public static void storeClientConversation(Conversation c, String username) {
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
