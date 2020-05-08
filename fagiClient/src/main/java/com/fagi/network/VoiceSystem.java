/*
 * Copyright (c) 2015. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.network;


import javax.sound.sampled.AudioFormat;

/**
 * @author miniwolf
 */
public interface VoiceSystem {
    int sampleRate = 44100;
    int sampelSize = 16;
    int channels = 2;
    int blockSize = channels * (sampelSize / 8);
    AudioFormat format = new AudioFormat(sampleRate, sampelSize, channels, true, false);
}
