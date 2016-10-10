package com.googlecode.mp4parser.h264.read;

import com.googlecode.mp4parser.h264.CharCache;
import java.io.IOException;
import java.io.InputStream;

public class BitstreamReader
{
  protected static int bitsRead;
  private int curByte;
  protected CharCache debugBits = new CharCache(50);
  private InputStream is;
  int nBit;
  private int nextByte;
  
  public BitstreamReader(InputStream paramInputStream)
    throws IOException
  {
    this.is = paramInputStream;
    this.curByte = paramInputStream.read();
    this.nextByte = paramInputStream.read();
  }
  
  private void advance()
    throws IOException
  {
    this.curByte = this.nextByte;
    this.nextByte = this.is.read();
    this.nBit = 0;
  }
  
  public void close()
    throws IOException
  {}
  
  public long getBitPosition()
  {
    return bitsRead * 8 + this.nBit % 8;
  }
  
  public int getCurBit()
  {
    return this.nBit;
  }
  
  public boolean isByteAligned()
  {
    return this.nBit % 8 == 0;
  }
  
  public boolean moreRBSPData()
    throws IOException
  {
    boolean bool2 = true;
    if (this.nBit == 8) {
      advance();
    }
    int i = 1 << 8 - this.nBit - 1;
    if ((this.curByte & (i << 1) - 1) == i) {}
    for (i = 1;; i = 0)
    {
      boolean bool1;
      if (this.curByte != -1)
      {
        bool1 = bool2;
        if (this.nextByte == -1)
        {
          bool1 = bool2;
          if (i == 0) {}
        }
      }
      else
      {
        bool1 = false;
      }
      return bool1;
    }
  }
  
  public int peakNextBits(int paramInt)
    throws IOException
  {
    int k = -1;
    if (paramInt > 8) {
      throw new IllegalArgumentException("N should be less then 8");
    }
    if (this.nBit == 8)
    {
      advance();
      if (this.curByte == -1) {
        return k;
      }
    }
    int[] arrayOfInt = new int[16 - this.nBit];
    int j = this.nBit;
    int i = 0;
    label61:
    if (j >= 8) {
      j = 0;
    }
    for (;;)
    {
      if (j >= 8)
      {
        i = 0;
        j = 0;
        for (;;)
        {
          k = i;
          if (j >= paramInt) {
            break;
          }
          i = i << 1 | arrayOfInt[j];
          j += 1;
        }
        arrayOfInt[i] = (this.curByte >> 7 - j & 0x1);
        j += 1;
        i += 1;
        break label61;
      }
      arrayOfInt[i] = (this.nextByte >> 7 - j & 0x1);
      j += 1;
      i += 1;
    }
  }
  
  public int read1Bit()
    throws IOException
  {
    if (this.nBit == 8)
    {
      advance();
      if (this.curByte == -1) {
        return -1;
      }
    }
    int i = this.curByte >> 7 - this.nBit & 0x1;
    this.nBit += 1;
    CharCache localCharCache = this.debugBits;
    if (i == 0) {}
    for (char c = '0';; c = '1')
    {
      localCharCache.append(c);
      bitsRead += 1;
      return i;
    }
  }
  
  public boolean readBool()
    throws IOException
  {
    return read1Bit() == 1;
  }
  
  public int readByte()
    throws IOException
  {
    if (this.nBit > 0) {
      advance();
    }
    int i = this.curByte;
    advance();
    return i;
  }
  
  public long readNBit(int paramInt)
    throws IOException
  {
    if (paramInt > 64) {
      throw new IllegalArgumentException("Can not readByte more then 64 bit");
    }
    long l = 0L;
    int i = 0;
    for (;;)
    {
      if (i >= paramInt) {
        return l;
      }
      l = l << 1 | read1Bit();
      i += 1;
    }
  }
  
  public long readRemainingByte()
    throws IOException
  {
    return readNBit(8 - this.nBit);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/h264/read/BitstreamReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */