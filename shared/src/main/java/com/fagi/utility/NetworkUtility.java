package com.fagi.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

/**
 * Used for utility functions related to network
 */
public class NetworkUtility {
    /**
     * Calls external service to find the public IP of the machine the server is running on
     *
     * @return a string containing the public IP v4 of the current machine
     */
    public static String getExternalIP() {
        String ip = "";

        try {
            URL whatismyip = URI
                    .create("http://checkip.amazonaws.com")
                    .toURL();
            BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            ip = in.readLine();
        } catch (IOException e) {
            System.err.println("Could not get public it. Cause: " + e);
        }
        return ip;
    }
}
