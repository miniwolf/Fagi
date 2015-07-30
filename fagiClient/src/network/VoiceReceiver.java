/*
 * Copyright (c) 2015. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package network;

import model.VoiceMessage;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * @author miniwolf
 */
public class VoiceReceiver implements VoiceSystem {
    private static SourceDataLine line_out;
    DataLine.Info info_out = new DataLine.Info(SourceDataLine.class, format);

    public VoiceReceiver() {
        try {
            line_out = (SourceDataLine) AudioSystem.getLine(info_out);
            line_out.open(format);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        line_out.start();
    }

    public void playAudio(VoiceMessage message) {
        line_out.write(message.getData(), 0, blockSize);
    }
}
