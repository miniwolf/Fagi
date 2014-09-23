package network;/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * InputHandler.java
 *
 * Handling every incoming object from the server
 */

import model.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * TODO: Write description
 */
class InputHandler implements Runnable {
    private final ConcurrentLinkedDeque<Object> inputs = new ConcurrentLinkedDeque<>();
    private final ObjectInputStream in;
    private boolean running = true;

    public InputHandler(ObjectInputStream in) {
        this.in = in;
    }

    @Override
    public void run() {
        Object object = null;
        while ( running ) {
            while ( object == null && running ) {
                try {
                    object = in.readObject();
                    inputs.add(object);
                } catch (IOException ioe) {
                    if ( running ) {
                        System.err.println("i ioe: " + ioe.toString());
                        ioe.printStackTrace(); // DEBUG need to terminate before closing socket.
                    }
                    return;
                } catch (ClassNotFoundException cnfe) {
                    // Shared files are not the same on both side of the server
                    System.err.println(cnfe.getMessage());
                    // TODO: This will be a bitch when having to update the server
                }
            }
            object = null;
        }
    }

    public Object getLast() {
        if ( inputs.isEmpty() ) return null;
        return inputs.pollLast();
    }

    /**
     * Checking our queue for a list from the server,
     * if the list contains something not a string,
     * we will throw an IllegalArgumentException.
     *
     * @return List<String> containing all the friends.
     * @throws IllegalArgumentException meaning the server
     *         has given some illegal list
     */
    public List<String> containsList() throws IllegalArgumentException {
        List<String> list = new ArrayList<>();
        for ( Object object : inputs ) {
            if ( !(object instanceof List) ) continue;
            try {
                list = checkList(object);
                inputs.remove(object);
                return list;
            } catch (IllegalArgumentException iae) {
                inputs.remove(object);
                throw iae;
            }
        }
        return list;
    }

    List<String> checkList(Object object) throws IllegalArgumentException {
        List<String> list = new ArrayList<>();
        List<?> tmp = (List<?>) object;
        for ( Object o : tmp ) {
            if ( !(o instanceof String) )
                throw new IllegalArgumentException("Server returned invalid list " + o.toString());
            list.add((String) o);
        }
        return list;
    }

    public Message containsMessage() {
        Message message;
        for ( Object object : inputs ) {
            if ( !(object instanceof Message) ) continue;

            message = (Message) object;
            if ( !message.isSystemMessage() ) {
                inputs.remove(object);
                return message;
            }
        }
        return null;
    }

    public void close() {
        running = false;
    }
}