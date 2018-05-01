package com.coremedia.iso;

import java.nio.ByteBuffer;

public final class IsoTypeReaderVariable
{
  public static long read(ByteBuffer paramByteBuffer, int paramInt)
  {
    long l;
    switch (paramInt)
    {
    case 5: 
    case 6: 
    case 7: 
    default: 
      throw new RuntimeException("I don't know how to read " + paramInt + " bytes");
    case 1: 
      l = IsoTypeReader.readUInt8(paramByteBuffer);
    }
    for (;;)
    {
      return l;
      l = IsoTypeReader.readUInt16(paramByteBuffer);
      continue;
      l = IsoTypeReader.readUInt24(paramByteBuffer);
      continue;
      l = IsoTypeReader.readUInt32(paramByteBuffer);
      continue;
      l = IsoTypeReader.readUInt64(paramByteBuffer);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/IsoTypeReaderVariable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */