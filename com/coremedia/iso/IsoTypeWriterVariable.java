package com.coremedia.iso;

import java.nio.ByteBuffer;

public final class IsoTypeWriterVariable
{
  public static void write(long paramLong, ByteBuffer paramByteBuffer, int paramInt)
  {
    switch (paramInt)
    {
    case 5: 
    case 6: 
    case 7: 
    default: 
      throw new RuntimeException("I don't know how to read " + paramInt + " bytes");
    case 1: 
      IsoTypeWriter.writeUInt8(paramByteBuffer, (int)(0xFF & paramLong));
      return;
    case 2: 
      IsoTypeWriter.writeUInt16(paramByteBuffer, (int)(0xFFFF & paramLong));
      return;
    case 3: 
      IsoTypeWriter.writeUInt24(paramByteBuffer, (int)(0xFFFFFF & paramLong));
      return;
    case 4: 
      IsoTypeWriter.writeUInt32(paramByteBuffer, paramLong);
      return;
    }
    IsoTypeWriter.writeUInt64(paramByteBuffer, paramLong);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/IsoTypeWriterVariable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */