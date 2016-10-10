package com.googlecode.mp4parser.util;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public class ChannelHelper
{
  private static ByteBuffer empty = ByteBuffer.allocate(0).asReadOnlyBuffer();
  
  public static int readFully(ReadableByteChannel paramReadableByteChannel, ByteBuffer paramByteBuffer, int paramInt)
    throws IOException
  {
    int i = 0;
    int k = paramReadableByteChannel.read(paramByteBuffer);
    if (-1 == k) {}
    for (;;)
    {
      if (k != -1) {
        return i;
      }
      throw new EOFException("End of file. No more boxes.");
      int j = i + k;
      i = j;
      if (j != paramInt) {
        break;
      }
      i = j;
    }
    return i;
  }
  
  public static void readFully(ReadableByteChannel paramReadableByteChannel, ByteBuffer paramByteBuffer)
    throws IOException
  {
    readFully(paramReadableByteChannel, paramByteBuffer, paramByteBuffer.remaining());
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/util/ChannelHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */