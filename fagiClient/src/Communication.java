/*
 * COPYRIGHT © Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig 2011
 * Communication.java
 *
 * Handling in and output
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

/**
 * TODO: Add description
 */
class Communication {
    private ObjectOutputStream out;
    private static final String host = "localhost"; //jonne42.dyndns.org
    private static final int port = 4242;
    private InputHandler inputHandler;
    private Socket socket;

    public Communication() throws IOException {
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            inputHandler = new InputHandler(new ObjectInputStream(socket.getInputStream()));
            inputHandler.start();
        } catch (UnknownHostException Uhe) {
            System.err.println("c Uhe: " + Uhe);
        } catch (IOException ioe) {
            throw new IOException("c ioe: " + ioe.toString());
        }
    }

    public Message receiveMessage() {
        return inputHandler.containsMessage();
    }

    void sendObject(Object obj) {
        try {
            out.writeObject(obj);
            out.flush();
        } catch (IOException e) {
            System.err.println("c ioe: " + e.toString());
            System.exit(1);
        }
    }

    public Object handleObjects() {
        Object object = null;
        while ( object == null )
            object = inputHandler.getLast();
        return object;
    }

    /**
     * Getting the friend list from the server.
     *
     * @return Object[] which contains all of our friends as strings.
     */
    public List<String> getFriends() {
        List<String> list = null;
        sendObject(new GetFriends());
        try {
            list = inputHandler.containsList();
        } catch (IllegalArgumentException e) {
            System.err.println("c iae: " + e.toString());
        }
        return list;
    }

    // TODO: Need to close correctly?
    public void close() {
        inputHandler.close();

        try {
            Thread.sleep(10);
            socket.close();
            System.out.println("Closed socket.");
        } catch (IOException ioe) {
            System.err.println("c ioe: " + ioe.toString());
        } catch (InterruptedException ie) {
            System.err.println("c ie: " + ie.toString());
        }
        inputHandler.interrupt();
        while (!inputHandler.isInterrupted()) { }
    }
}