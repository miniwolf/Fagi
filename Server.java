/*
 * COPYRIGHT © Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig 2011
 * Server.java
 *
 * Server, recieving userlogins and handling connections
 */

/**
 * @author miniwolf
 */

/*
 * TODO: implement the ability to store username, password and validate
 */
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class Server {

    /** Create a new instance of the class Router */
    public Server() { }

    /**
     * @param args the command line arguments
     */ 
    public static void main(String args[]) {
        ServerSocket socket = null;
 
        System.out.println("\t\t**********Welcome to Fagi Server**********");
        System.out.println("\n\nBinding Socket...");
	// Creating a socket using the port given in the parameters
        try {
	    socket = new ServerSocket(Integer.parseInt(args[0]));
        } catch (Exception e) {
            System.out.println("Unable to Bind Socket:");
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Socket Bound Waiting for Clients....");
	//Starting the Router and start accepting clients
	boolean started = false;
        while (true) {
            try {
		Router router = new Router();
                Socket s = socket.accept();
                router.addClient(s);
                if (!started) {
		    
                    router.start();
                    started = true;
                }
                System.out.println("Client Connected From: " + s.getRemoteSocketAddress().toString());
            } catch (IOException ioe) {
                System.exit(0);
            }
        }
    }
}
