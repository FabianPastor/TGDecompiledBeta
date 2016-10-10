package com.googlecode.mp4parser.h264.write;

import com.googlecode.mp4parser.h264.Debug;
import java.io.IOException;
import java.io.OutputStream;

public class CAVLCWriter
  extends BitstreamWriter
{
  public CAVLCWriter(OutputStream paramOutputStream)
  {
    super(paramOutputStream);
  }
  
  public void writeBool(boolean paramBoolean, String paramString)
    throws IOException
  {
    Debug.print(paramString + "\t");
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      write1Bit(i);
      Debug.println("\t" + paramBoolean);
      return;
    }
  }
  
  public void writeNBit(long paramLong, int paramInt, String paramString)
    throws IOException
  {
    Debug.print(paramString + "\t");
    int i = 0;
    for (;;)
    {
      if (i >= paramInt)
      {
        Debug.println("\t" + paramLong);
        return;
      }
      write1Bit((int)(paramLong >> paramInt - i - 1) & 0x1);
      i += 1;
    }
  }
  
  public void writeSE(int paramInt, String paramString)
    throws IOException
  {
    int j = 1;
    Debug.print(paramString + "\t");
    int i;
    if (paramInt < 0)
    {
      i = -1;
      if (paramInt <= 0) {
        break label72;
      }
    }
    for (;;)
    {
      writeUE(j + i * (paramInt << 1));
      Debug.println("\t" + paramInt);
      return;
      i = 1;
      break;
      label72:
      j = 0;
    }
  }
  
  public void writeSliceTrailingBits()
  {
    throw new IllegalStateException("todo");
  }
  
  public void writeTrailingBits()
    throws IOException
  {
    write1Bit(1);
    writeRemainingZero();
    flush();
  }
  
  public void writeU(int paramInt1, int paramInt2)
    throws IOException
  {
    writeNBit(paramInt1, paramInt2);
  }
  
  public void writeU(int paramInt1, int paramInt2, String paramString)
    throws IOException
  {
    Debug.print(paramString + "\t");
    writeNBit(paramInt1, paramInt2);
    Debug.println("\t" + paramInt1);
  }
  
  public void writeUE(int paramInt)
    throws IOException
  {
    int k = 0;
    int j = 0;
    int i = 0;
    for (;;)
    {
      if (i >= 15) {
        i = k;
      }
      while (paramInt < (1 << i) + j)
      {
        writeNBit(0L, i);
        write1Bit(1);
        writeNBit(paramInt - j, i);
        return;
      }
      j += (1 << i);
      i += 1;
    }
  }
  
  public void writeUE(int paramInt, String paramString)
    throws IOException
  {
    Debug.print(paramString + "\t");
    writeUE(paramInt);
    Debug.println("\t" + paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/h264/write/CAVLCWriter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */