/*
 * COPYRIGHT © Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig 2011
 * Main.java
 *
 * Handling server start.
 */

/**
 * Main class for starting up server.
 */
class Main {
    public static void main(String[] args) throws Exception {
        Data.readInData();

        int port = args.length > 0 ? Integer.parseInt(args[0]) : 4242;

        Server server = new Server(port);
        server.start();
    }
}