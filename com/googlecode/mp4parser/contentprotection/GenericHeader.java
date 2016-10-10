package com.googlecode.mp4parser.contentprotection;

import com.googlecode.mp4parser.boxes.piff.ProtectionSpecificHeader;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;

public class GenericHeader
  extends ProtectionSpecificHeader
{
  public static UUID PROTECTION_SYSTEM_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
  ByteBuffer data;
  
  static
  {
    ProtectionSpecificHeader.uuidRegistry.put(PROTECTION_SYSTEM_ID, GenericHeader.class);
  }
  
  public ByteBuffer getData()
  {
    return this.data;
  }
  
  public UUID getSystemId()
  {
    return PROTECTION_SYSTEM_ID;
  }
  
  public void parse(ByteBuffer paramByteBuffer)
  {
    this.data = paramByteBuffer;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/contentprotection/GenericHeader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */