package org.telegram.messenger.exoplayer.util;

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
    this.limit = this.data.length;
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
    if (this.data == null) {
      return 0;
    }
    return this.data.length;
  }
  
  public int getPosition()
  {
    return this.position;
  }
  
  public int limit()
  {
    return this.limit;
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
    k = arrayOfByte[k];
    arrayOfByte = this.data;
    int m = this.position;
    this.position = (m + 1);
    return (i & 0xFF) << 24 | (j & 0xFF) << 16 | (k & 0xFF) << 8 | arrayOfByte[m] & 0xFF;
  }
  
  public String readLine()
  {
    Object localObject;
    if (bytesLeft() == 0) {
      localObject = null;
    }
    String str;
    do
    {
      do
      {
        do
        {
          return (String)localObject;
          int i = this.position;
          while ((i < this.limit) && (this.data[i] != 10) && (this.data[i] != 13)) {
            i += 1;
          }
          if ((i - this.position >= 3) && (this.data[this.position] == -17) && (this.data[(this.position + 1)] == -69) && (this.data[(this.position + 2)] == -65)) {
            this.position += 3;
          }
          str = new String(this.data, this.position, i - this.position);
          this.position = i;
          localObject = str;
        } while (this.position == this.limit);
        if (this.data[this.position] != 13) {
          break;
        }
        this.position += 1;
        localObject = str;
      } while (this.position == this.limit);
      localObject = str;
    } while (this.data[this.position] != 10);
    this.position += 1;
    return str;
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
    k = arrayOfByte[k];
    arrayOfByte = this.data;
    int m = this.position;
    this.position = (m + 1);
    return i & 0xFF | (j & 0xFF) << 8 | (k & 0xFF) << 16 | (arrayOfByte[m] & 0xFF) << 24;
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
    j = arrayOfByte[j];
    arrayOfByte = this.data;
    int k = this.position;
    this.position = (k + 1);
    return i & 0xFF | (j & 0xFF) << 8 | (arrayOfByte[k] & 0xFF) << 16;
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
    String str;
    do
    {
      return (String)localObject;
      int i = this.position;
      while ((i < this.limit) && (this.data[i] != 0)) {
        i += 1;
      }
      int j = this.position;
      str = new String(this.data, this.position, i - j, Charset.defaultCharset());
      this.position = i;
      localObject = str;
    } while (this.position == this.limit);
    this.position += 1;
    return str;
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
    return readString(paramInt, Charset.defaultCharset());
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
  
  public long readUTF8EncodedLong()
  {
    int k = 0;
    long l2 = this.data[this.position];
    int j = 7;
    int i;
    long l1;
    for (;;)
    {
      i = k;
      l1 = l2;
      if (j >= 0)
      {
        if ((1 << j & l2) != 0L) {
          break label114;
        }
        if (j >= 6) {
          break label93;
        }
        l1 = l2 & (1 << j) - 1;
        i = 7 - j;
      }
      while (i == 0)
      {
        throw new NumberFormatException("Invalid UTF-8 sequence first byte: " + l1);
        label93:
        i = k;
        l1 = l2;
        if (j == 7)
        {
          i = 1;
          l1 = l2;
        }
      }
      label114:
      j -= 1;
    }
    j = 1;
    while (j < i)
    {
      k = this.data[(this.position + j)];
      if ((k & 0xC0) != 128) {
        throw new NumberFormatException("Invalid UTF-8 sequence continuation byte: " + l1);
      }
      l1 = l1 << 6 | k & 0x3F;
      j += 1;
    }
    this.position += i;
    return l1;
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
    i = arrayOfByte[i];
    arrayOfByte = this.data;
    int j = this.position;
    this.position = (j + 1);
    return (i & 0xFF) << 8 | arrayOfByte[j] & 0xFF;
  }
  
  public void reset()
  {
    this.position = 0;
    this.limit = 0;
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/util/ParsableByteArray.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */