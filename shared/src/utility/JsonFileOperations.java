package utility;

import com.google.gson.Gson;

import java.io.*;

/**
 * Created by costa on 09-11-2016.
 */
public class JsonFileOperations {
    public static <T extends Serializable> void StoreObjectToFile(T object, String folderPath, String fileName) {
        try {
            File folder = new File(folderPath);
            File file = new File(folderPath + fileName + ".fagi");
            if (!folder.exists()) {
                folder.mkdir();
            }
            if (!file.exists()) {
                file.createNewFile();
            }

            Gson gson = new Gson();

            PrintWriter out = new PrintWriter(new FileWriter(folderPath + fileName + ".fagi", false));
            out.println(gson.toJson(object));
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T extends Serializable> T LoadObjectFromFile(String fileName, Class<T> clazz) {
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
}
