/*
 * COPYRIGHT © Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig 2011
 * Router.java
 *
 * Handling and storing clients including message tranfers
 */

/**
 * @author miniwolf
 */

/*
 * TODO: Implement specific From - To model
 */

import java.util.Vector;
import java.lang.StringBuilder;
import java.net.Socket;

public class Router extends Thread {
 
    private Vector<Client> clients;
    private StringBuilder msgSend;
 
    public Router() { 
	clients = new Vector();
	msgSend = new StringBuilder();
    }

    private void updateClients(Vector<Client> s) {
        clients = s;
    }
    
    /** Adds a new client to the list */
    private void addClient(Socket s) {
        clients.add(new Client(s));
    }

    /** Method for sending a message to a specific user, including the sender information */
    private void sendMessage(String from, String to, String msgSend) {
	
    }
 
    public void run() {
	
    }
}
