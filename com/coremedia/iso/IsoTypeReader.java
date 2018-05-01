package com.coremedia.iso;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public final class IsoTypeReader
{
  public static int byte2int(byte paramByte)
  {
    int i = paramByte;
    if (paramByte < 0) {
      i = paramByte + 256;
    }
    return i;
  }
  
  public static String read4cc(ByteBuffer paramByteBuffer)
  {
    byte[] arrayOfByte = new byte[4];
    paramByteBuffer.get(arrayOfByte);
    try
    {
      paramByteBuffer = new String(arrayOfByte, "ISO-8859-1");
      return paramByteBuffer;
    }
    catch (UnsupportedEncodingException paramByteBuffer)
    {
      throw new RuntimeException(paramByteBuffer);
    }
  }
  
  public static double readFixedPoint0230(ByteBuffer paramByteBuffer)
  {
    byte[] arrayOfByte = new byte[4];
    paramByteBuffer.get(arrayOfByte);
    return (0x0 | arrayOfByte[0] << 24 & 0xFF000000 | arrayOfByte[1] << 16 & 0xFF0000 | arrayOfByte[2] << 8 & 0xFF00 | arrayOfByte[3] & 0xFF) / 1.073741824E9D;
  }
  
  public static double readFixedPoint1616(ByteBuffer paramByteBuffer)
  {
    byte[] arrayOfByte = new byte[4];
    paramByteBuffer.get(arrayOfByte);
    return (0x0 | arrayOfByte[0] << 24 & 0xFF000000 | arrayOfByte[1] << 16 & 0xFF0000 | arrayOfByte[2] << 8 & 0xFF00 | arrayOfByte[3] & 0xFF) / 65536.0D;
  }
  
  public static float readFixedPoint88(ByteBuffer paramByteBuffer)
  {
    byte[] arrayOfByte = new byte[2];
    paramByteBuffer.get(arrayOfByte);
    int i = (short)(arrayOfByte[0] << 8 & 0xFF00 | 0x0);
    return (short)(arrayOfByte[1] & 0xFF | i) / 256.0F;
  }
  
  public static String readIso639(ByteBuffer paramByteBuffer)
  {
    int i = readUInt16(paramByteBuffer);
    paramByteBuffer = new StringBuilder();
    for (int j = 0;; j++)
    {
      if (j >= 3) {
        return paramByteBuffer.toString();
      }
      paramByteBuffer.append((char)((i >> (2 - j) * 5 & 0x1F) + 96));
    }
  }
  
  public static String readString(ByteBuffer paramByteBuffer)
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    for (;;)
    {
      int i = paramByteBuffer.get();
      if (i == 0) {
        return Utf8.convert(localByteArrayOutputStream.toByteArray());
      }
      localByteArrayOutputStream.write(i);
    }
  }
  
  public static String readString(ByteBuffer paramByteBuffer, int paramInt)
  {
    byte[] arrayOfByte = new byte[paramInt];
    paramByteBuffer.get(arrayOfByte);
    return Utf8.convert(arrayOfByte);
  }
  
  public static int readUInt16(ByteBuffer paramByteBuffer)
  {
    return 0 + (byte2int(paramByteBuffer.get()) << 8) + byte2int(paramByteBuffer.get());
  }
  
  public static int readUInt16BE(ByteBuffer paramByteBuffer)
  {
    return 0 + byte2int(paramByteBuffer.get()) + (byte2int(paramByteBuffer.get()) << 8);
  }
  
  public static int readUInt24(ByteBuffer paramByteBuffer)
  {
    return 0 + (readUInt16(paramByteBuffer) << 8) + byte2int(paramByteBuffer.get());
  }
  
  public static long readUInt32(ByteBuffer paramByteBuffer)
  {
    long l1 = paramByteBuffer.getInt();
    long l2 = l1;
    if (l1 < 0L) {
      l2 = l1 + 4294967296L;
    }
    return l2;
  }
  
  public static long readUInt32BE(ByteBuffer paramByteBuffer)
  {
    long l1 = readUInt8(paramByteBuffer);
    long l2 = readUInt8(paramByteBuffer);
    long l3 = readUInt8(paramByteBuffer);
    return (readUInt8(paramByteBuffer) << 24) + (l3 << 16) + (l2 << 8) + (l1 << 0);
  }
  
  public static long readUInt48(ByteBuffer paramByteBuffer)
  {
    long l = readUInt16(paramByteBuffer) << 32;
    if (l < 0L) {
      throw new RuntimeException("I don't know how to deal with UInt64! long is not sufficient and I don't want to use BigInt");
    }
    return l + readUInt32(paramByteBuffer);
  }
  
  public static long readUInt64(ByteBuffer paramByteBuffer)
  {
    long l = 0L + (readUInt32(paramByteBuffer) << 32);
    if (l < 0L) {
      throw new RuntimeException("I don't know how to deal with UInt64! long is not sufficient and I don't want to use BigInt");
    }
    return l + readUInt32(paramByteBuffer);
  }
  
  public static int readUInt8(ByteBuffer paramByteBuffer)
  {
    return byte2int(paramByteBuffer.get());
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/IsoTypeReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */