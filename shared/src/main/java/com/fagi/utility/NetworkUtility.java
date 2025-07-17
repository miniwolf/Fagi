package com.fagi.utility;

import com.fagi.logging.FagiLogger;
import com.fagi.logging.FagiLoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

/**
 * Used for utility functions related to network
 */
public class NetworkUtility {
    private static final FagiLogger LOGGER = FagiLoggerFactory.createLogger(NetworkUtility.class);

    /**
     * Calls external service to find the public IP of the machine the server is running on
     *
     * @return a string containing the public IP v4 of the current machine
     */
    public static String getExternalIP() {
        String ip = "";
        String checkIPServiceUrl = "http://checkip.amazonaws.com";

        try {
            URL whatismyip = URI
                    .create(checkIPServiceUrl)
                    .toURL();
            BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            ip = in.readLine();
        } catch (IOException e) {
            LOGGER.error(
                    e,
                    () -> "Could not get public ip from " + checkIPServiceUrl
            );
        }

        return ip;
    }
}
