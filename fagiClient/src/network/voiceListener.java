/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig.
 */

package network;

import model.Voice;
import org.xiph.speex.SpeexDecoder;
import org.xiph.speex.SpeexEncoder;

import javax.sound.sampled.*;
import java.io.StreamCorruptedException;

public class voiceListener implements Runnable {
    private boolean running = true;
    private Communication communication;
    private final int sampleRate = 44100;
    private final int sampelSize = 16;
    private final int channels = 2;
    private SpeexDecoder decoder = new SpeexDecoder();
    private SpeexEncoder encoder = new SpeexEncoder();
    private SourceDataLine line_out;
    private TargetDataLine line_in;
    private boolean muted;

    public voiceListener(Communication communication) {
        this.communication = communication;
        AudioFormat format = new AudioFormat(sampleRate, sampelSize, channels, true, false);
        DataLine.Info info_out = new DataLine.Info(SourceDataLine.class, format);
        DataLine.Info info_in = new DataLine.Info(TargetDataLine.class, format);
        try {
            line_out = (SourceDataLine) AudioSystem.getLine(info_out);
            line_out.open(format);
            line_in = (TargetDataLine) AudioSystem.getLine(info_in);
            line_in.open(format);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

        decoder.init(1, sampleRate, channels, false);
        encoder.init(1, 10, sampleRate, channels);
        try {
            line_out.open(format);
            line_in.open(format);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        int blockSize = channels * (sampelSize / 8) * encoder.getFrameSize();
        byte[] buffer = new byte[blockSize * 2];
        line_in.start();
        line_out.start();
        int read;
        while ( running ) {
            //send
            if ( !muted ) {
                line_in.read(buffer, 0, blockSize);
                if (!encoder.processData(buffer, 0, blockSize)) {
                    System.err.println("Could not encode data!");
                    break;
                }
                int encoded = encoder.getProcessedData(buffer, 0);
                byte[] encodedData = new byte[encoded];
                communication.sendObject(new Voice(encodedData));
            }

            //receive
            byte[] data = communication.getVoice();
            try {
                decoder.processData(data, 0, data.length);
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            }
            byte[] decoded_data = new byte[decoder.getProcessedDataByteSize()];
            int decoded = decoder.getProcessedData(decoded_data, 0);
            line_out.write(decoded_data, 0, decoded);
        }
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public void close() {
        running = false;
    }
}
