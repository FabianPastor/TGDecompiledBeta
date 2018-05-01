package org.telegram.messenger.exoplayer2.util;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public final class ParsableByteArray
{
  public byte[] data;
  private int limit;
  private int position;
  
  public ParsableByteArray() {}
  
  public ParsableByteArray(int paramInt)
  {
    this.data = new byte[paramInt];
    this.limit = paramInt;
  }
  
  public ParsableByteArray(byte[] paramArrayOfByte)
  {
    this.data = paramArrayOfByte;
    this.limit = paramArrayOfByte.length;
  }
  
  public ParsableByteArray(byte[] paramArrayOfByte, int paramInt)
  {
    this.data = paramArrayOfByte;
    this.limit = paramInt;
  }
  
  public int bytesLeft()
  {
    return this.limit - this.position;
  }
  
  public int capacity()
  {
    if (this.data == null) {}
    for (int i = 0;; i = this.data.length) {
      return i;
    }
  }
  
  public int getPosition()
  {
    return this.position;
  }
  
  public int limit()
  {
    return this.limit;
  }
  
  public char peekChar()
  {
    return (char)((this.data[this.position] & 0xFF) << 8 | this.data[(this.position + 1)] & 0xFF);
  }
  
  public int peekUnsignedByte()
  {
    return this.data[this.position] & 0xFF;
  }
  
  public void readBytes(ByteBuffer paramByteBuffer, int paramInt)
  {
    paramByteBuffer.put(this.data, this.position, paramInt);
    this.position += paramInt;
  }
  
  public void readBytes(ParsableBitArray paramParsableBitArray, int paramInt)
  {
    readBytes(paramParsableBitArray.data, 0, paramInt);
    paramParsableBitArray.setPosition(0);
  }
  
  public void readBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    System.arraycopy(this.data, this.position, paramArrayOfByte, paramInt1, paramInt2);
    this.position += paramInt2;
  }
  
  public double readDouble()
  {
    return Double.longBitsToDouble(readLong());
  }
  
  public float readFloat()
  {
    return Float.intBitsToFloat(readInt());
  }
  
  public int readInt()
  {
    byte[] arrayOfByte = this.data;
    int i = this.position;
    this.position = (i + 1);
    i = arrayOfByte[i];
    arrayOfByte = this.data;
    int j = this.position;
    this.position = (j + 1);
    j = arrayOfByte[j];
    arrayOfByte = this.data;
    int k = this.position;
    this.position = (k + 1);
    int m = arrayOfByte[k];
    arrayOfByte = this.data;
    k = this.position;
    this.position = (k + 1);
    return (i & 0xFF) << 24 | (j & 0xFF) << 16 | (m & 0xFF) << 8 | arrayOfByte[k] & 0xFF;
  }
  
  public int readInt24()
  {
    byte[] arrayOfByte = this.data;
    int i = this.position;
    this.position = (i + 1);
    i = arrayOfByte[i];
    arrayOfByte = this.data;
    int j = this.position;
    this.position = (j + 1);
    int k = arrayOfByte[j];
    arrayOfByte = this.data;
    j = this.position;
    this.position = (j + 1);
    return (i & 0xFF) << 24 >> 8 | (k & 0xFF) << 8 | arrayOfByte[j] & 0xFF;
  }
  
  public String readLine()
  {
    Object localObject;
    if (bytesLeft() == 0) {
      localObject = null;
    }
    for (;;)
    {
      return (String)localObject;
      for (int i = this.position; (i < this.limit) && (!Util.isLinebreak(this.data[i])); i++) {}
      if ((i - this.position >= 3) && (this.data[this.position] == -17) && (this.data[(this.position + 1)] == -69) && (this.data[(this.position + 2)] == -65)) {
        this.position += 3;
      }
      String str = new String(this.data, this.position, i - this.position);
      this.position = i;
      localObject = str;
      if (this.position != this.limit) {
        if (this.data[this.position] == 13)
        {
          this.position += 1;
          localObject = str;
          if (this.position == this.limit) {}
        }
        else
        {
          localObject = str;
          if (this.data[this.position] == 10)
          {
            this.position += 1;
            localObject = str;
          }
        }
      }
    }
  }
  
  public int readLittleEndianInt()
  {
    byte[] arrayOfByte = this.data;
    int i = this.position;
    this.position = (i + 1);
    i = arrayOfByte[i];
    arrayOfByte = this.data;
    int j = this.position;
    this.position = (j + 1);
    j = arrayOfByte[j];
    arrayOfByte = this.data;
    int k = this.position;
    this.position = (k + 1);
    int m = arrayOfByte[k];
    arrayOfByte = this.data;
    k = this.position;
    this.position = (k + 1);
    return i & 0xFF | (j & 0xFF) << 8 | (m & 0xFF) << 16 | (arrayOfByte[k] & 0xFF) << 24;
  }
  
  public int readLittleEndianInt24()
  {
    byte[] arrayOfByte = this.data;
    int i = this.position;
    this.position = (i + 1);
    i = arrayOfByte[i];
    arrayOfByte = this.data;
    int j = this.position;
    this.position = (j + 1);
    j = arrayOfByte[j];
    arrayOfByte = this.data;
    int k = this.position;
    this.position = (k + 1);
    return i & 0xFF | (j & 0xFF) << 8 | (arrayOfByte[k] & 0xFF) << 16;
  }
  
  public long readLittleEndianLong()
  {
    byte[] arrayOfByte = this.data;
    int i = this.position;
    this.position = (i + 1);
    long l1 = arrayOfByte[i];
    arrayOfByte = this.data;
    i = this.position;
    this.position = (i + 1);
    long l2 = arrayOfByte[i];
    arrayOfByte = this.data;
    i = this.position;
    this.position = (i + 1);
    long l3 = arrayOfByte[i];
    arrayOfByte = this.data;
    i = this.position;
    this.position = (i + 1);
    long l4 = arrayOfByte[i];
    arrayOfByte = this.data;
    i = this.position;
    this.position = (i + 1);
    long l5 = arrayOfByte[i];
    arrayOfByte = this.data;
    i = this.position;
    this.position = (i + 1);
    long l6 = arrayOfByte[i];
    arrayOfByte = this.data;
    i = this.position;
    this.position = (i + 1);
    long l7 = arrayOfByte[i];
    arrayOfByte = this.data;
    i = this.position;
    this.position = (i + 1);
    return l1 & 0xFF | (l2 & 0xFF) << 8 | (l3 & 0xFF) << 16 | (l4 & 0xFF) << 24 | (l5 & 0xFF) << 32 | (l6 & 0xFF) << 40 | (l7 & 0xFF) << 48 | (arrayOfByte[i] & 0xFF) << 56;
  }
  
  public short readLittleEndianShort()
  {
    byte[] arrayOfByte = this.data;
    int i = this.position;
    this.position = (i + 1);
    i = arrayOfByte[i];
    arrayOfByte = this.data;
    int j = this.position;
    this.position = (j + 1);
    return (short)(i & 0xFF | (arrayOfByte[j] & 0xFF) << 8);
  }
  
  public long readLittleEndianUnsignedInt()
  {
    byte[] arrayOfByte = this.data;
    int i = this.position;
    this.position = (i + 1);
    long l1 = arrayOfByte[i];
    arrayOfByte = this.data;
    i = this.position;
    this.position = (i + 1);
    long l2 = arrayOfByte[i];
    arrayOfByte = this.data;
    i = this.position;
    this.position = (i + 1);
    long l3 = arrayOfByte[i];
    arrayOfByte = this.data;
    i = this.position;
    this.position = (i + 1);
    return l1 & 0xFF | (l2 & 0xFF) << 8 | (l3 & 0xFF) << 16 | (arrayOfByte[i] & 0xFF) << 24;
  }
  
  public int readLittleEndianUnsignedInt24()
  {
    byte[] arrayOfByte = this.data;
    int i = this.position;
    this.position = (i + 1);
    i = arrayOfByte[i];
    arrayOfByte = this.data;
    int j = this.position;
    this.position = (j + 1);
    int k = arrayOfByte[j];
    arrayOfByte = this.data;
    j = this.position;
    this.position = (j + 1);
    return i & 0xFF | (k & 0xFF) << 8 | (arrayOfByte[j] & 0xFF) << 16;
  }
  
  public int readLittleEndianUnsignedIntToInt()
  {
    int i = readLittleEndianInt();
    if (i < 0) {
      throw new IllegalStateException("Top bit not zero: " + i);
    }
    return i;
  }
  
  public int readLittleEndianUnsignedShort()
  {
    byte[] arrayOfByte = this.data;
    int i = this.position;
    this.position = (i + 1);
    i = arrayOfByte[i];
    arrayOfByte = this.data;
    int j = this.position;
    this.position = (j + 1);
    return i & 0xFF | (arrayOfByte[j] & 0xFF) << 8;
  }
  
  public long readLong()
  {
    byte[] arrayOfByte = this.data;
    int i = this.position;
    this.position = (i + 1);
    long l1 = arrayOfByte[i];
    arrayOfByte = this.data;
    i = this.position;
    this.position = (i + 1);
    long l2 = arrayOfByte[i];
    arrayOfByte = this.data;
    i = this.position;
    this.position = (i + 1);
    long l3 = arrayOfByte[i];
    arrayOfByte = this.data;
    i = this.position;
    this.position = (i + 1);
    long l4 = arrayOfByte[i];
    arrayOfByte = this.data;
    i = this.position;
    this.position = (i + 1);
    long l5 = arrayOfByte[i];
    arrayOfByte = this.data;
    i = this.position;
    this.position = (i + 1);
    long l6 = arrayOfByte[i];
    arrayOfByte = this.data;
    i = this.position;
    this.position = (i + 1);
    long l7 = arrayOfByte[i];
    arrayOfByte = this.data;
    i = this.position;
    this.position = (i + 1);
    return (l1 & 0xFF) << 56 | (l2 & 0xFF) << 48 | (l3 & 0xFF) << 40 | (l4 & 0xFF) << 32 | (l5 & 0xFF) << 24 | (l6 & 0xFF) << 16 | (l7 & 0xFF) << 8 | arrayOfByte[i] & 0xFF;
  }
  
  public String readNullTerminatedString()
  {
    Object localObject;
    if (bytesLeft() == 0) {
      localObject = null;
    }
    for (;;)
    {
      return (String)localObject;
      for (int i = this.position; (i < this.limit) && (this.data[i] != 0); i++) {}
      String str = new String(this.data, this.position, i - this.position);
      this.position = i;
      localObject = str;
      if (this.position < this.limit)
      {
        this.position += 1;
        localObject = str;
      }
    }
  }
  
  public String readNullTerminatedString(int paramInt)
  {
    String str;
    if (paramInt == 0) {
      str = "";
    }
    for (;;)
    {
      return str;
      int i = paramInt;
      int j = this.position + paramInt - 1;
      int k = i;
      if (j < this.limit)
      {
        k = i;
        if (this.data[j] == 0) {
          k = i - 1;
        }
      }
      str = new String(this.data, this.position, k);
      this.position += paramInt;
    }
  }
  
  public short readShort()
  {
    byte[] arrayOfByte = this.data;
    int i = this.position;
    this.position = (i + 1);
    i = arrayOfByte[i];
    arrayOfByte = this.data;
    int j = this.position;
    this.position = (j + 1);
    return (short)((i & 0xFF) << 8 | arrayOfByte[j] & 0xFF);
  }
  
  public String readString(int paramInt)
  {
    return readString(paramInt, Charset.forName("UTF-8"));
  }
  
  public String readString(int paramInt, Charset paramCharset)
  {
    paramCharset = new String(this.data, this.position, paramInt, paramCharset);
    this.position += paramInt;
    return paramCharset;
  }
  
  public int readSynchSafeInt()
  {
    return readUnsignedByte() << 21 | readUnsignedByte() << 14 | readUnsignedByte() << 7 | readUnsignedByte();
  }
  
  public int readUnsignedByte()
  {
    byte[] arrayOfByte = this.data;
    int i = this.position;
    this.position = (i + 1);
    return arrayOfByte[i] & 0xFF;
  }
  
  public int readUnsignedFixedPoint1616()
  {
    byte[] arrayOfByte = this.data;
    int i = this.position;
    this.position = (i + 1);
    i = arrayOfByte[i];
    arrayOfByte = this.data;
    int j = this.position;
    this.position = (j + 1);
    j = arrayOfByte[j];
    this.position += 2;
    return (i & 0xFF) << 8 | j & 0xFF;
  }
  
  public long readUnsignedInt()
  {
    byte[] arrayOfByte = this.data;
    int i = this.position;
    this.position = (i + 1);
    long l1 = arrayOfByte[i];
    arrayOfByte = this.data;
    i = this.position;
    this.position = (i + 1);
    long l2 = arrayOfByte[i];
    arrayOfByte = this.data;
    i = this.position;
    this.position = (i + 1);
    long l3 = arrayOfByte[i];
    arrayOfByte = this.data;
    i = this.position;
    this.position = (i + 1);
    return (l1 & 0xFF) << 24 | (l2 & 0xFF) << 16 | (l3 & 0xFF) << 8 | arrayOfByte[i] & 0xFF;
  }
  
  public int readUnsignedInt24()
  {
    byte[] arrayOfByte = this.data;
    int i = this.position;
    this.position = (i + 1);
    i = arrayOfByte[i];
    arrayOfByte = this.data;
    int j = this.position;
    this.position = (j + 1);
    j = arrayOfByte[j];
    arrayOfByte = this.data;
    int k = this.position;
    this.position = (k + 1);
    return (i & 0xFF) << 16 | (j & 0xFF) << 8 | arrayOfByte[k] & 0xFF;
  }
  
  public int readUnsignedIntToInt()
  {
    int i = readInt();
    if (i < 0) {
      throw new IllegalStateException("Top bit not zero: " + i);
    }
    return i;
  }
  
  public long readUnsignedLongToLong()
  {
    long l = readLong();
    if (l < 0L) {
      throw new IllegalStateException("Top bit not zero: " + l);
    }
    return l;
  }
  
  public int readUnsignedShort()
  {
    byte[] arrayOfByte = this.data;
    int i = this.position;
    this.position = (i + 1);
    int j = arrayOfByte[i];
    arrayOfByte = this.data;
    i = this.position;
    this.position = (i + 1);
    return (j & 0xFF) << 8 | arrayOfByte[i] & 0xFF;
  }
  
  public long readUtf8EncodedLong()
  {
    int i = 0;
    long l1 = this.data[this.position];
    int k;
    long l2;
    for (int j = 7;; j--)
    {
      k = i;
      l2 = l1;
      if (j >= 0)
      {
        if ((1 << j & l1) != 0L) {
          continue;
        }
        if (j >= 6) {
          break label98;
        }
        l2 = l1 & (1 << j) - 1;
        k = 7 - j;
      }
      while (k == 0)
      {
        throw new NumberFormatException("Invalid UTF-8 sequence first byte: " + l2);
        label98:
        k = i;
        l2 = l1;
        if (j == 7)
        {
          k = 1;
          l2 = l1;
        }
      }
    }
    for (j = 1; j < k; j++)
    {
      i = this.data[(this.position + j)];
      if ((i & 0xC0) != 128) {
        throw new NumberFormatException("Invalid UTF-8 sequence continuation byte: " + l2);
      }
      l2 = l2 << 6 | i & 0x3F;
    }
    this.position += k;
    return l2;
  }
  
  public void reset()
  {
    this.position = 0;
    this.limit = 0;
  }
  
  public void reset(int paramInt)
  {
    if (capacity() < paramInt) {}
    for (byte[] arrayOfByte = new byte[paramInt];; arrayOfByte = this.data)
    {
      reset(arrayOfByte, paramInt);
      return;
    }
  }
  
  public void reset(byte[] paramArrayOfByte, int paramInt)
  {
    this.data = paramArrayOfByte;
    this.limit = paramInt;
    this.position = 0;
  }
  
  public void setLimit(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt <= this.data.length)) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkArgument(bool);
      this.limit = paramInt;
      return;
    }
  }
  
  public void setPosition(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt <= this.limit)) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkArgument(bool);
      this.position = paramInt;
      return;
    }
  }
  
  public void skipBytes(int paramInt)
  {
    setPosition(this.position + paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/util/ParsableByteArray.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */