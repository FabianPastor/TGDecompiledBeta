package com.mp4parser.iso14496.part15;

import com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry;
import java.nio.ByteBuffer;

public class TemporalSubLayerSampleGroup
  extends GroupEntry
{
  public static final String TYPE = "tsas";
  int i;
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    while ((paramObject != null) && (getClass() == paramObject.getClass())) {
      return true;
    }
    return false;
  }
  
  public ByteBuffer get()
  {
    return ByteBuffer.allocate(0);
  }
  
  public String getType()
  {
    return "tsas";
  }
  
  public int hashCode()
  {
    return 41;
  }
  
  public void parse(ByteBuffer paramByteBuffer) {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/mp4parser/iso14496/part15/TemporalSubLayerSampleGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */