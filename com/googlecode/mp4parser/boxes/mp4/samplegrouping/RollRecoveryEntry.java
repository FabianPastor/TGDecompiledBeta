package com.googlecode.mp4parser.boxes.mp4.samplegrouping;

import java.nio.ByteBuffer;

public class RollRecoveryEntry
  extends GroupEntry
{
  public static final String TYPE = "roll";
  private short rollDistance;
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if ((paramObject == null) || (getClass() != paramObject.getClass())) {
        return false;
      }
      paramObject = (RollRecoveryEntry)paramObject;
    } while (this.rollDistance == ((RollRecoveryEntry)paramObject).rollDistance);
    return false;
  }
  
  public ByteBuffer get()
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(2);
    localByteBuffer.putShort(this.rollDistance);
    localByteBuffer.rewind();
    return localByteBuffer;
  }
  
  public short getRollDistance()
  {
    return this.rollDistance;
  }
  
  public String getType()
  {
    return "roll";
  }
  
  public int hashCode()
  {
    return this.rollDistance;
  }
  
  public void parse(ByteBuffer paramByteBuffer)
  {
    this.rollDistance = paramByteBuffer.getShort();
  }
  
  public void setRollDistance(short paramShort)
  {
    this.rollDistance = paramShort;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/mp4/samplegrouping/RollRecoveryEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */