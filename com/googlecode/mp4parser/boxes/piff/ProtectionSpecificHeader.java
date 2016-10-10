package com.googlecode.mp4parser.boxes.piff;

import com.coremedia.iso.Hex;
import com.googlecode.mp4parser.contentprotection.GenericHeader;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class ProtectionSpecificHeader
{
  protected static Map<UUID, Class<? extends ProtectionSpecificHeader>> uuidRegistry = new HashMap();
  
  public static ProtectionSpecificHeader createFor(UUID paramUUID, ByteBuffer paramByteBuffer)
  {
    Object localObject = (Class)uuidRegistry.get(paramUUID);
    paramUUID = null;
    if (localObject != null) {}
    try
    {
      paramUUID = (ProtectionSpecificHeader)((Class)localObject).newInstance();
      localObject = paramUUID;
      if (paramUUID == null) {
        localObject = new GenericHeader();
      }
      ((ProtectionSpecificHeader)localObject).parse(paramByteBuffer);
      return (ProtectionSpecificHeader)localObject;
    }
    catch (InstantiationException paramUUID)
    {
      throw new RuntimeException(paramUUID);
    }
    catch (IllegalAccessException paramUUID)
    {
      throw new RuntimeException(paramUUID);
    }
  }
  
  public boolean equals(Object paramObject)
  {
    throw new RuntimeException("somebody called equals on me but that's not supposed to happen.");
  }
  
  public abstract ByteBuffer getData();
  
  public abstract UUID getSystemId();
  
  public abstract void parse(ByteBuffer paramByteBuffer);
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("ProtectionSpecificHeader");
    localStringBuilder.append("{data=");
    ByteBuffer localByteBuffer = getData().duplicate();
    localByteBuffer.rewind();
    byte[] arrayOfByte = new byte[localByteBuffer.limit()];
    localByteBuffer.get(arrayOfByte);
    localStringBuilder.append(Hex.encodeHex(arrayOfByte));
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/piff/ProtectionSpecificHeader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */