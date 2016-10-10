package com.coremedia.iso;

import java.nio.ByteBuffer;

public final class IsoTypeReaderVariable
{
  public static long read(ByteBuffer paramByteBuffer, int paramInt)
  {
    switch (paramInt)
    {
    case 5: 
    case 6: 
    case 7: 
    default: 
      throw new RuntimeException("I don't know how to read " + paramInt + " bytes");
    case 1: 
      return IsoTypeReader.readUInt8(paramByteBuffer);
    case 2: 
      return IsoTypeReader.readUInt16(paramByteBuffer);
    case 3: 
      return IsoTypeReader.readUInt24(paramByteBuffer);
    case 4: 
      return IsoTypeReader.readUInt32(paramByteBuffer);
    }
    return IsoTypeReader.readUInt64(paramByteBuffer);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/IsoTypeReaderVariable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */