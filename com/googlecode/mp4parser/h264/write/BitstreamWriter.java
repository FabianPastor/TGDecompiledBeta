package com.googlecode.mp4parser.h264.write;

import com.googlecode.mp4parser.h264.Debug;
import java.io.IOException;
import java.io.OutputStream;

public class BitstreamWriter
{
  private int curBit;
  private int[] curByte = new int[8];
  private final OutputStream os;
  
  public BitstreamWriter(OutputStream paramOutputStream)
  {
    this.os = paramOutputStream;
  }
  
  private void writeCurByte()
    throws IOException
  {
    int i = this.curByte[0];
    int j = this.curByte[1];
    int k = this.curByte[2];
    int m = this.curByte[3];
    int n = this.curByte[4];
    int i1 = this.curByte[5];
    int i2 = this.curByte[6];
    int i3 = this.curByte[7];
    this.os.write(i << 7 | j << 6 | k << 5 | m << 4 | n << 3 | i1 << 2 | i2 << 1 | i3);
  }
  
  public void flush()
    throws IOException
  {
    int i = this.curBit;
    for (;;)
    {
      if (i >= 8)
      {
        this.curBit = 0;
        writeCurByte();
        return;
      }
      this.curByte[i] = 0;
      i += 1;
    }
  }
  
  public void write1Bit(int paramInt)
    throws IOException
  {
    Debug.print(paramInt);
    if (this.curBit == 8)
    {
      this.curBit = 0;
      writeCurByte();
    }
    int[] arrayOfInt = this.curByte;
    int i = this.curBit;
    this.curBit = (i + 1);
    arrayOfInt[i] = paramInt;
  }
  
  public void writeByte(int paramInt)
    throws IOException
  {
    this.os.write(paramInt);
  }
  
  public void writeNBit(long paramLong, int paramInt)
    throws IOException
  {
    int i = 0;
    for (;;)
    {
      if (i >= paramInt) {
        return;
      }
      write1Bit((int)(paramLong >> paramInt - i - 1) & 0x1);
      i += 1;
    }
  }
  
  public void writeRemainingZero()
    throws IOException
  {
    writeNBit(0L, 8 - this.curBit);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/h264/write/BitstreamWriter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */