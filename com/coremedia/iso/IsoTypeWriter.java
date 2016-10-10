package com.coremedia.iso;

import java.nio.ByteBuffer;

public final class IsoTypeWriter
{
  static
  {
    if (!IsoTypeWriter.class.desiredAssertionStatus()) {}
    for (boolean bool = true;; bool = false)
    {
      $assertionsDisabled = bool;
      return;
    }
  }
  
  public static void writeFixedPoint0230(ByteBuffer paramByteBuffer, double paramDouble)
  {
    int i = (int)(1.073741824E9D * paramDouble);
    paramByteBuffer.put((byte)((0xFF000000 & i) >> 24));
    paramByteBuffer.put((byte)((0xFF0000 & i) >> 16));
    paramByteBuffer.put((byte)((0xFF00 & i) >> 8));
    paramByteBuffer.put((byte)(i & 0xFF));
  }
  
  public static void writeFixedPoint1616(ByteBuffer paramByteBuffer, double paramDouble)
  {
    int i = (int)(65536.0D * paramDouble);
    paramByteBuffer.put((byte)((0xFF000000 & i) >> 24));
    paramByteBuffer.put((byte)((0xFF0000 & i) >> 16));
    paramByteBuffer.put((byte)((0xFF00 & i) >> 8));
    paramByteBuffer.put((byte)(i & 0xFF));
  }
  
  public static void writeFixedPoint88(ByteBuffer paramByteBuffer, double paramDouble)
  {
    int i = (short)(int)(256.0D * paramDouble);
    paramByteBuffer.put((byte)((0xFF00 & i) >> 8));
    paramByteBuffer.put((byte)(i & 0xFF));
  }
  
  public static void writeIso639(ByteBuffer paramByteBuffer, String paramString)
  {
    if (paramString.getBytes().length != 3) {
      throw new IllegalArgumentException("\"" + paramString + "\" language string isn't exactly 3 characters long!");
    }
    int j = 0;
    int i = 0;
    for (;;)
    {
      if (i >= 3)
      {
        writeUInt16(paramByteBuffer, j);
        return;
      }
      j += (paramString.getBytes()[i] - 96 << (2 - i) * 5);
      i += 1;
    }
  }
  
  public static void writePascalUtfString(ByteBuffer paramByteBuffer, String paramString)
  {
    paramString = Utf8.convert(paramString);
    assert (paramString.length < 255);
    writeUInt8(paramByteBuffer, paramString.length);
    paramByteBuffer.put(paramString);
  }
  
  public static void writeUInt16(ByteBuffer paramByteBuffer, int paramInt)
  {
    paramInt &= 0xFFFF;
    writeUInt8(paramByteBuffer, paramInt >> 8);
    writeUInt8(paramByteBuffer, paramInt & 0xFF);
  }
  
  public static void writeUInt16BE(ByteBuffer paramByteBuffer, int paramInt)
  {
    paramInt &= 0xFFFF;
    writeUInt8(paramByteBuffer, paramInt & 0xFF);
    writeUInt8(paramByteBuffer, paramInt >> 8);
  }
  
  public static void writeUInt24(ByteBuffer paramByteBuffer, int paramInt)
  {
    paramInt &= 0xFFFFFF;
    writeUInt16(paramByteBuffer, paramInt >> 8);
    writeUInt8(paramByteBuffer, paramInt);
  }
  
  public static void writeUInt32(ByteBuffer paramByteBuffer, long paramLong)
  {
    assert ((paramLong >= 0L) && (paramLong <= 4294967296L)) : ("The given long is not in the range of uint32 (" + paramLong + ")");
    paramByteBuffer.putInt((int)paramLong);
  }
  
  public static void writeUInt32BE(ByteBuffer paramByteBuffer, long paramLong)
  {
    assert ((paramLong >= 0L) && (paramLong <= 4294967296L)) : ("The given long is not in the range of uint32 (" + paramLong + ")");
    writeUInt16BE(paramByteBuffer, (int)paramLong & 0xFFFF);
    writeUInt16BE(paramByteBuffer, (int)(paramLong >> 16 & 0xFFFF));
  }
  
  public static void writeUInt48(ByteBuffer paramByteBuffer, long paramLong)
  {
    paramLong &= 0xFFFFFFFFFFFF;
    writeUInt16(paramByteBuffer, (int)(paramLong >> 32));
    writeUInt32(paramByteBuffer, 0xFFFFFFFF & paramLong);
  }
  
  public static void writeUInt64(ByteBuffer paramByteBuffer, long paramLong)
  {
    assert (paramLong >= 0L) : "The given long is negative";
    paramByteBuffer.putLong(paramLong);
  }
  
  public static void writeUInt8(ByteBuffer paramByteBuffer, int paramInt)
  {
    paramByteBuffer.put((byte)(paramInt & 0xFF));
  }
  
  public static void writeUtf8String(ByteBuffer paramByteBuffer, String paramString)
  {
    paramByteBuffer.put(Utf8.convert(paramString));
    writeUInt8(paramByteBuffer, 0);
  }
  
  public static void writeZeroTermUtf8String(ByteBuffer paramByteBuffer, String paramString)
  {
    paramByteBuffer.put(Utf8.convert(paramString));
    writeUInt8(paramByteBuffer, 0);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/IsoTypeWriter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */