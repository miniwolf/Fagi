package com.fagi.config;

import com.fagi.encryption.Conversion;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PublicKey;

/**
 * Created by Marcus on 04-06-2016.
 */
public class ServerConfig implements Serializable {
    private String ip, name;
    private PublicKey serverKey;
    private int port;

    public ServerConfig(String name, String ip, int port, PublicKey serverKey) {
        this.ip = ip;
        this.name = name;
        this.serverKey = serverKey;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }

    public String getName() {
        return name;
    }

    public PublicKey getServerKey() {
        return serverKey;
    }

    public void saveToPath(String path) throws IOException {
        byte[] config = Conversion.convertToBytes(this);
        FileOutputStream fos = new FileOutputStream(path);
        fos.write(config);
        fos.close();
    }

    public static ServerConfig pathToServerConfig(String path) throws IOException, ClassNotFoundException {
        Path p = Paths.get(path);
        byte[] data = Files.readAllBytes(p);

        Object o = Conversion.convertFromBytes(data);

        if(!(o instanceof ServerConfig)) {
            throw new InvalidClassException(path + " does not contain a ServerConfig object.");
        }

        return (ServerConfig)o;
    }
}