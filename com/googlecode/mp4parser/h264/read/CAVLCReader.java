package com.googlecode.mp4parser.h264.read;

import com.googlecode.mp4parser.h264.BTree;
import com.googlecode.mp4parser.h264.CharCache;
import com.googlecode.mp4parser.h264.Debug;
import java.io.IOException;
import java.io.InputStream;

public class CAVLCReader
  extends BitstreamReader
{
  public CAVLCReader(InputStream paramInputStream)
    throws IOException
  {
    super(paramInputStream);
  }
  
  private int readUE()
    throws IOException
  {
    int i = 0;
    for (;;)
    {
      if (read1Bit() != 0)
      {
        int j = 0;
        if (i > 0)
        {
          long l = readNBit(i);
          j = (int)((1 << i) - 1 + l);
        }
        return j;
      }
      i += 1;
    }
  }
  
  private void trace(String paramString1, String paramString2)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    String str = String.valueOf(BitstreamReader.bitsRead - this.debugBits.length());
    int j = str.length();
    localStringBuilder.append("@" + str);
    int i = 0;
    int k;
    if (i >= 8 - j)
    {
      localStringBuilder.append(paramString1);
      j = localStringBuilder.length();
      k = this.debugBits.length();
      i = 0;
    }
    for (;;)
    {
      if (i >= 100 - j - k)
      {
        localStringBuilder.append(this.debugBits);
        localStringBuilder.append(" (" + paramString2 + ")");
        this.debugBits.clear();
        Debug.println(localStringBuilder.toString());
        return;
        localStringBuilder.append(' ');
        i += 1;
        break;
      }
      localStringBuilder.append(' ');
      i += 1;
    }
  }
  
  public byte[] read(int paramInt)
    throws IOException
  {
    byte[] arrayOfByte = new byte[paramInt];
    int i = 0;
    for (;;)
    {
      if (i >= paramInt) {
        return arrayOfByte;
      }
      arrayOfByte[i] = ((byte)readByte());
      i += 1;
    }
  }
  
  public boolean readAE()
  {
    throw new UnsupportedOperationException("Stan");
  }
  
  public int readAEI()
  {
    throw new UnsupportedOperationException("Stan");
  }
  
  public boolean readBool(String paramString)
    throws IOException
  {
    boolean bool;
    if (read1Bit() == 0)
    {
      bool = false;
      if (!bool) {
        break label29;
      }
    }
    label29:
    for (String str = "1";; str = "0")
    {
      trace(paramString, str);
      return bool;
      bool = true;
      break;
    }
  }
  
  public Object readCE(BTree paramBTree, String paramString)
    throws IOException
  {
    Object localObject;
    do
    {
      paramBTree = paramBTree.down(read1Bit());
      if (paramBTree == null) {
        throw new RuntimeException("Illegal code");
      }
      localObject = paramBTree.getValue();
    } while (localObject == null);
    trace(paramString, localObject.toString());
    return localObject;
  }
  
  public int readME(String paramString)
    throws IOException
  {
    return readUE(paramString);
  }
  
  public long readNBit(int paramInt, String paramString)
    throws IOException
  {
    long l = readNBit(paramInt);
    trace(paramString, String.valueOf(l));
    return l;
  }
  
  public int readSE(String paramString)
    throws IOException
  {
    int i = readUE();
    i = ((i >> 1) + (i & 0x1)) * (((i & 0x1) << 1) - 1);
    trace(paramString, String.valueOf(i));
    return i;
  }
  
  public int readTE(int paramInt)
    throws IOException
  {
    if (paramInt > 1) {
      return readUE();
    }
    return (read1Bit() ^ 0xFFFFFFFF) & 0x1;
  }
  
  public void readTrailingBits()
    throws IOException
  {
    read1Bit();
    readRemainingByte();
  }
  
  public int readU(int paramInt, String paramString)
    throws IOException
  {
    return (int)readNBit(paramInt, paramString);
  }
  
  public int readUE(String paramString)
    throws IOException
  {
    int i = readUE();
    trace(paramString, String.valueOf(i));
    return i;
  }
  
  public int readZeroBitCount(String paramString)
    throws IOException
  {
    int i = 0;
    for (;;)
    {
      if (read1Bit() != 0)
      {
        trace(paramString, String.valueOf(i));
        return i;
      }
      i += 1;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/h264/read/CAVLCReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */