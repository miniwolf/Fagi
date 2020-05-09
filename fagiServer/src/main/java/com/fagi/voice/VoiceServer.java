package com.fagi.voice;
/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig.
 */

/*
public class VoiceServer extends VoiceServerThread {
    private static final int PORT = 4243;
    private byte[] recieveBuffer;
    private DatagramPacket serverSocket = new DatagramPacket(recieveBuffer, 0);

    ByteArrayOutputStream line_out;
    private boolean running;

    public VoiceServer() {
    }

    @Override
    public void run() {
        byte[] receiveData = new byte[16];

        while ( running ) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                //serverSocket.receive(receivePacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("RECEIVED: " + Arrays.toString(receivePacket.getConversationData()));

            byte[] audioData = receivePacket.getConversationData();
        }
    }
}
  */