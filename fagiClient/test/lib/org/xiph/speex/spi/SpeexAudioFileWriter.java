/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig.
 */

/* $Id: SpeexAudioFileWriter.java,v 1.3 2005/02/11 12:54:05 mgimpel Exp $ */

package org.xiph.speex.spi;

import  java.io.File;
import  java.io.IOException;
import  java.io.OutputStream;
import  java.io.FileOutputStream;

import  javax.sound.sampled.AudioFileFormat;
import  javax.sound.sampled.AudioInputStream;
import  javax.sound.sampled.spi.AudioFileWriter;

/**
 * Provider for Speex audio file writing services.
 * This implementation can write Speex audio files from an audio stream.
 * 
 * @author Marc Gimpel, Wimba S.A. (mgimpel@horizonwimba.com)
 * @version $Revision: 1.3 $
 */
public class SpeexAudioFileWriter
  extends AudioFileWriter
{
  /** */
  public static final AudioFileFormat.Type[] NO_FORMAT = {};
  /** */
  public static final AudioFileFormat.Type[] SPEEX_FORMAT = {SpeexFileFormatType.SPEEX};

  /**
   * Obtains the file types for which file writing support is provided by this audio file writer.
   * @return array of file types. If no file types are supported, an array of length 0 is returned.
   */
  public AudioFileFormat.Type[] getAudioFileTypes()
  {
    return SPEEX_FORMAT;
  }

  /**
   * Obtains the file types that this audio file writer can write from the
   * audio input stream specified.
   * @param stream - the audio input stream for which audio file type support
   * is queried.
   * @return array of file types. If no file types are supported, an array of
   * length 0 is returned.
   */
  public AudioFileFormat.Type[] getAudioFileTypes(final AudioInputStream stream)
  {
    if (stream.getFormat().getEncoding() instanceof SpeexEncoding) {
      return SPEEX_FORMAT;
    }
    else {
      return NO_FORMAT;
    }
  }

  /**
   * Writes a stream of bytes representing an audio file of the file type
   * indicated to the output stream provided. Some file types require that the
   * length be written into the file header, and cannot be written from start
   * to finish unless the length is known in advance.
   * An attempt to write such a file type will fail with an IOException if the
   * length in the audio file format is AudioSystem.NOT_SPECIFIED.
   * @param stream - the audio input stream containing audio data to be written
   * to the output stream.
   * @param fileType - file type to be written to the output stream.
   * @param out - stream to which the file data should be written.
   * @return the number of bytes written to the output stream.
   * @exception java.io.IOException - if an I/O exception occurs.
   * @exception IllegalArgumentException - if the file type is not supported by the system.
   * @see #isFileTypeSupported(javax.sound.sampled.AudioFileFormat.Type, javax.sound.sampled.AudioInputStream)
   * @see #getAudioFileTypes()
   */
  public int write(final AudioInputStream stream,
                   final AudioFileFormat.Type fileType,
                   final OutputStream out)
    throws IOException
  {
    AudioFileFormat.Type[] formats = getAudioFileTypes(stream);
    if (formats != null && formats.length > 0) {
      return write(stream, out);
    }
    else {
      throw new IllegalArgumentException("cannot write given file type");
    }
  }

  /**
   * Writes a stream of bytes representing an audio file of the file format
   * indicated to the external file provided.
   * @param stream - the audio input stream containing audio data to be written
   * to the file.
   * @param fileType - file type to be written to the file.
   * @param out - external file to which the file data should be written.
   * @return the number of bytes written to the file.
   * @exception java.io.IOException - if an I/O exception occurs.
   * @exception IllegalArgumentException - if the file format is not supported by the system
   * @see #isFileTypeSupported(javax.sound.sampled.AudioFileFormat.Type)
   * @see #getAudioFileTypes()
   */
  public int write(final AudioInputStream stream,
                   final AudioFileFormat.Type fileType,
                   final File out)
    throws IOException
  {
    AudioFileFormat.Type[] formats = getAudioFileTypes(stream);
    if (formats != null && formats.length > 0) {
      FileOutputStream fos = new FileOutputStream(out);
      return write(stream, fos);
    }
    else {
      throw new IllegalArgumentException("cannot write given file type");
    }
  }

  /**
   * Writes a stream of bytes representing an audio file of the file type
   * indicated to the output stream provided.
   * @param stream - the audio input stream containing audio data to be written
   * to the output stream.
   * @param out - stream to which the file data should be written.
   * @return the number of bytes written to the output stream.
   * @exception java.io.IOException - if an I/O exception occurs.
   */
  private int write(final AudioInputStream stream,
                    final OutputStream out)
    throws IOException
  {
    byte[] data = new byte[2048];
    int read = 0;
    int temp;
    while ((temp = stream.read(data, 0, 2048)) > 0) {
      out.write(data, 0, temp);
      read += temp;
    }
    /*
    According to jsresources.org, the write() method is supposed to close the
    File or FileOutputStream on completion.
    Without it, people passing in a File parameter are not able to close the
    file on their own, and hence can't delete the audio file (especially on
    FAT32 systems), since it's still technically open.
    */
    out.flush();
    out.close();
    return read;
  }
}
