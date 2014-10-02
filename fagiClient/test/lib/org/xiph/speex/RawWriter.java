/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig.
 */

/* $Id: RawWriter.java,v 1.2 2004/10/21 16:21:57 mgimpel Exp $ */

package org.xiph.speex;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;

/**
 * Raw Audio File Writer.
 *
 * @author Marc Gimpel, Wimba S.A. (mgimpel@horizonwimba.com)
 * @version $Revision: 1.2 $
 */
public class RawWriter
  extends AudioFileWriter
{
  private OutputStream out;

  /**
   * Closes the output file.
   * @exception java.io.IOException if there was an exception closing the Audio Writer.
   */
  public void close()
    throws IOException 
  {
    out.close(); 
  }
  
  /**
   * Open the output file. 
   * @param file - file to open.
   * @exception java.io.IOException if there was an exception opening the Audio Writer.
   */
  public void open(final File file)
    throws IOException
  {
    file.delete(); 
    out = new FileOutputStream(file);
  }

  /**
   * Open the output file. 
   * @param filename - file to open.
   * @exception java.io.IOException if there was an exception opening the Audio Writer.
   */
  public void open(final String filename)
    throws IOException 
  {
    open(new File(filename)); 
  }

  /**
   * Writes the header pages that start the Ogg Speex file. 
   * Prepares file for data to be written.
   * @param comment description to be included in the header.
   * @exception java.io.IOException
   */
  public void writeHeader(final String comment)
    throws IOException
  {
    // a raw audio file has no header
  }

  /**
   * Writes a packet of audio. 
   * @param data audio data
   * @param offset the offset from which to start reading the data.
   * @param len the length of data to read.
   * @exception java.io.IOException
   */
  public void writePacket(final byte[] data,
                          final int offset,
                          final int len)
    throws IOException 
  {
    out.write(data, offset, len);
  }
}
