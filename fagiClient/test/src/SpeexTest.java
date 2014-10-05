/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig.
 */

import org.xiph.speex.SpeexDecoder;
import org.xiph.speex.SpeexEncoder;

import javax.sound.sampled.*;
import java.util.Vector;

public class SpeexTest {
    public static void main(String[] args) throws Exception {
        /*for ( AudioFormat audioFormat : new SpeexTest().getSupportedFormats(TargetDataLine.class) ) {
            System.out.println(audioFormat.toString());
        }*/

        int sample_rate = 44100;
        int sample_size = 16;
        int channels = 2;
        AudioFormat format = new AudioFormat(sample_rate, sample_size, channels, true, false);
        TargetDataLine line_in;
        DataLine.Info info_in = new DataLine.Info(TargetDataLine.class, format);
        try {
            line_in = (TargetDataLine) AudioSystem.getLine(info_in);
            line_in.open(format);
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
            return;
        }

        DataLine.Info info_out = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine line_out;
        try {
            line_out = (SourceDataLine) AudioSystem.getLine(info_out);
            line_out.open(format);
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
            return;
        }
        SpeexEncoder encoder = new SpeexEncoder();
        encoder.init(1, 10, sample_rate, channels);
        SpeexDecoder decoder = new SpeexDecoder();
        decoder.init(1, sample_rate, channels, false);
        int raw_block_size = channels * (sample_size / 8) * encoder.getFrameSize();
        byte[] buffer = new byte[raw_block_size * 2];
        line_in.start();
        line_out.start();
        int read;
        while ( true ) {
            read = line_in.read(buffer, 0, raw_block_size);
            if (!encoder.processData(buffer, 0, raw_block_size)) {
                System.err.println("Could not encode data!");
                break;
            }
            int encoded = encoder.getProcessedData(buffer, 0);
            byte[] encoded_data = new byte[encoded];
            System.arraycopy(buffer, 0, encoded_data, 0, encoded);
            decoder.processData(encoded_data, 0, encoded);
            byte[] decoded_data = new byte[decoder.getProcessedDataByteSize()];
            int decoded = decoder.getProcessedData(decoded_data, 0);
            System.out.println(decoded + " bytes resulted as a result of decoding " + encoded + " encoded bytes.");
            line_out.write(decoded_data, 0, decoded);
        }
    }

    public Vector<AudioFormat> getSupportedFormats(Class<?> dataLineClass) {
    /*
     * These define our criteria when searching for formats supported
     * by Mixers on the system.
     */
        float sampleRates[] = { (float) 8000.0, (float) 16000.0, (float) 44100.0, (float) 48000.0, (float) 96000.0 };
        int channels[] = { 2, 5 };
        int bytesPerSample[] = { 1, 2, 3, 4, 5, 6 };

        AudioFormat format;
        DataLine.Info lineInfo;

        Vector<AudioFormat> formats = new Vector<>();

        for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo())
            for (float sampleRate : sampleRates)
                for (int channel : channels)
                    for (int aBytesPerSample : bytesPerSample) {
                        format = new AudioFormat(sampleRate, 8 * aBytesPerSample, channel, true, false);
                        lineInfo = new DataLine.Info(dataLineClass, format);
                        if (AudioSystem.isLineSupported(lineInfo))
                            if (AudioSystem.getMixer(mixerInfo).isLineSupported(lineInfo)) formats.add(format);
                    }
        return formats;
    }
}