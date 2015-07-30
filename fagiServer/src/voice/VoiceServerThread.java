/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig.
 */

package voice;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by miniwolf on 05-10-2014.
 */
/*
public class VoiceServerThread implements Runnable {
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private boolean alreadyClosed = false;

    private boolean TALKING = false;
    private boolean ADMIN = false;
    private boolean MUTE = false;

    ServerSendThread s = null;

    boolean keepGoing = false;

    CommonSoundClass cs;

    boolean lastpacketreceived = true;

    public VoiceServerThread(Socket socket) {
        this.socket = socket;
    }

    public class PingClass implements Runnable {
        VoiceServerThread ptrtoThis = null;

        public PingClass(VoiceServerThread ptrtoThis) {
            this.ptrtoThis = ptrtoThis;
        }

        @Override
        public void run() {
            while ( keepGoing ) {
                ptrtoThis.cs.writebyte()
            }
        }
    }

    @Override
    public void run() {

    }

    public class ServerSendThread implements Runnable {
        VoiceServerThread fr;

        public ServerSendThread(VoiceServerThread fr) {
            this.fr = fr;
        }

        @Override
        public void run() {
            try {
                while ( keepGoing ) {

                }
            }
        }
    }
}     */
