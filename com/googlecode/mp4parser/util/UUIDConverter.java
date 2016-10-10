package com.googlecode.mp4parser.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

public class UUIDConverter
{
  public static UUID convert(byte[] paramArrayOfByte)
  {
    paramArrayOfByte = ByteBuffer.wrap(paramArrayOfByte);
    paramArrayOfByte.order(ByteOrder.BIG_ENDIAN);
    return new UUID(paramArrayOfByte.getLong(), paramArrayOfByte.getLong());
  }
  
  public static byte[] convert(UUID paramUUID)
  {
    long l1 = paramUUID.getMostSignificantBits();
    long l2 = paramUUID.getLeastSignificantBits();
    paramUUID = new byte[16];
    int i = 0;
    if (i >= 8) {
      i = 8;
    }
    for (;;)
    {
      if (i >= 16)
      {
        return paramUUID;
        paramUUID[i] = ((byte)(int)(l1 >>> (7 - i) * 8));
        i += 1;
        break;
      }
      paramUUID[i] = ((byte)(int)(l2 >>> (7 - i) * 8));
      i += 1;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/util/UUIDConverter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */