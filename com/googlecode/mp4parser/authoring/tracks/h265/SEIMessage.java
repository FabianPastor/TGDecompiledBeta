package com.googlecode.mp4parser.authoring.tracks.h265;

import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import java.io.IOException;
import java.io.PrintStream;

public class SEIMessage
{
  public SEIMessage(BitReaderBuffer paramBitReaderBuffer)
    throws IOException
  {
    int i = 0;
    for (;;)
    {
      if (paramBitReaderBuffer.readBits(8) != 255L)
      {
        int j = paramBitReaderBuffer.readBits(8);
        while (paramBitReaderBuffer.readBits(8) == 255L) {}
        paramBitReaderBuffer.readBits(8);
        System.err.println("payloadType " + (i + j));
        return;
      }
      i += 255;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/tracks/h265/SEIMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */