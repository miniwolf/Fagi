/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig.
 */

/* $Id: SpeexDecoder.java,v 1.4 2005/05/27 13:15:54 mgimpel Exp $ */

package org.xiph.speex;
 
import java.io.StreamCorruptedException;

/**
 * Main Speex Decoder class.
 * This class decodes the given Speex packets into PCM 16bit samples.
 * 
 * <p>Here's an example that decodes and recovers one Speex packet.
 * <pre>
 * SpeexDecoder speexDecoder = new SpeexDecoder();
 * speexDecoder.processData(data, packetOffset, packetSize);
 * byte[] decoded = new byte[speexDecoder.getProcessedBataByteSize()];
 * speexDecoder.getProcessedData(decoded, 0);
 * </pre>
 * 
 * @author Jim Lawrence, helloNetwork.com
 * @author Marc Gimpel, Wimba S.A. (mgimpel@horizonwimba.com)
 * @version $Revision: 1.4 $
 */
public class SpeexDecoder
{
  /**
   * Version of the Speex Decoder
   */
  public static final String VERSION = "Java Speex Decoder v0.9.7 ($Revision: 1.4 $)";

  private int     sampleRate;
  private int     channels;
  private float[] decodedData;
  private short[] outputData;
  private int     outputSize;
  private Bits    bits;
  private Decoder decoder;
  private int     frameSize;

  /**
   * Constructor
   */
  public SpeexDecoder()
  {
    bits = new Bits();
    sampleRate = 0;
    channels   = 0;
  }
  
  /**
   * Initialise the Speex Decoder.
   * @param mode       the mode of the decoder (0=NB, 1=WB, 2=UWB).
   * @param sampleRate the number of samples per second.
   * @param channels   the number of audio channels (1=mono, 2=stereo, ...).
   * @param enhanced   whether to enable perceptual enhancement or not.
   * @return true if initialisation successful.
   */
  public boolean init(final int mode,
                      final int sampleRate,
                      final int channels,
                      final boolean enhanced)
  {
    switch (mode) {
    case 0:
      decoder = new NbDecoder();
      ((NbDecoder)decoder).nbinit();
      break;
//Wideband
    case 1:
      decoder = new SbDecoder();
      ((SbDecoder)decoder).wbinit();
      break;
    case 2:
      decoder = new SbDecoder();
      ((SbDecoder)decoder).uwbinit();
      break;
//*/
    default:
      return false;
    }
    
    /* initialize the speex decoder */
    decoder.setPerceptualEnhancement(enhanced);
    /* set decoder format and properties */
    this.frameSize  = decoder.getFrameSize();
    this.sampleRate = sampleRate;
    this.channels   = channels;
    int secondSize  = sampleRate*channels;
    decodedData     = new float[secondSize*2];
    outputData      = new short[secondSize*2];
    outputSize      = 0;
    bits.init();
    return true;
  }
  
  /**
   * Returns the sample rate.
   * @return the sample rate.
   */
  public int getSampleRate() 
  {
    return sampleRate;
  }
  
  /**
   * Returns the number of channels.
   * @return the number of channels.
   */
  public int getChannels() 
  {
    return channels;
  }
  
  /**
   * Pull the decoded data out into a byte array at the given offset
   * and returns the number of bytes processed and just read.
   * @param data
   * @param offset
   * @return the number of bytes processed and just read.
   */
  public int getProcessedData(final byte[] data, final int offset) 
  {    
    if (outputSize<=0) {
      return outputSize;
    }
    for (int i=0; i<outputSize; i++) {
      int dx     =  offset + (i<<1);
      data[dx]   = (byte) (outputData[i] & 0xff);
      data[dx+1] = (byte) ((outputData[i] >> 8) &  0xff );
    }
    int size = outputSize*2;
    outputSize = 0;
    return size;
  }

  /**
   * Pull the decoded data out into a short array at the given offset
   * and returns tne number of shorts processed and just read
   * @param data
   * @param offset
   * @return the number of samples processed and just read.
   */
  public int getProcessedData(final short[] data, final int offset)
  {
    if (outputSize<=0) {
      return outputSize;
    }
    System.arraycopy(outputData, 0, data, offset, outputSize);
    int size = outputSize;
    outputSize = 0;
    return size;
  }

  /**
   * Returns the number of bytes processed and ready to be read.
   * @return the number of bytes processed and ready to be read.
   */
  public int getProcessedDataByteSize() 
  {
    return (outputSize*2);
  }
  
  /**
   * This is where the actual decoding takes place
   * @param data - the Speex data (frame) to decode.
   * If it is null, the packet is supposed lost.
   * @param offset - the offset from which to start reading the data.
   * @param len - the length of data to read (Speex frame size).
   * @throws java.io.StreamCorruptedException If the input stream is invalid.
   */
  public void processData(final byte[] data,
                          final int offset,
                          final int len)
    throws StreamCorruptedException
  {
    if (data == null) {
      processData(true);
    }
    else {
      /* read packet bytes into bitstream */
      bits.read_from(data, offset, len);
      processData(false);
    }
  }

  /**
   * This is where the actual decoding takes place.
   * @param lost - true if the Speex packet has been lost.
   * @throws java.io.StreamCorruptedException If the input stream is invalid.
   */
  public void processData(final boolean lost)
    throws StreamCorruptedException
  {
    int i;
    /* decode the bitstream */
    if (lost)
      decoder.decode(null, decodedData);
    else
      decoder.decode(bits, decodedData);
    if (channels == 2)
      decoder.decodeStereo(decodedData, frameSize);

    /* PCM saturation */
    for (i=0; i<frameSize*channels; i++) {
      if (decodedData[i]>32767.0f)
        decodedData[i]=32767.0f;
      else if (decodedData[i]<-32768.0f)
        decodedData[i]=-32768.0f;
    }

    /* convert to short and save to buffer */
    for (i=0; i<frameSize*channels; i++, outputSize++) {
      outputData[outputSize] = (decodedData[i]>0) ?
                               (short) (decodedData[i]+.5) :
                               (short) (decodedData[i]-.5);
    } 
  }
}
