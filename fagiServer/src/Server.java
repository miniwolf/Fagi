import java.net.ServerSocket;
import java.net.Socket;

class Server {
    private boolean running = true;
    private final int port;

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        System.out.println("Starting Server");
        ServerSocket ss = null;
        try {
            ss = new ServerSocket(port);
        } catch (Exception e) {
            System.out.println("Error while creating socket, are you sure you can use port " + port + " on you system?");
            running = false;
        }

        while ( running ) {
            try {
                workerCreation(ss);
            } catch (Exception e) {
                System.out.println("Error in server loop exception = " + e);
                running = false;
            }
        }
        System.out.println("Stopping Server");
    }

    private void workerCreation(ServerSocket ss) throws Exception {
        Socket s = ss.accept();
        Worker w = new Worker(s);
        w.start();
    }
}