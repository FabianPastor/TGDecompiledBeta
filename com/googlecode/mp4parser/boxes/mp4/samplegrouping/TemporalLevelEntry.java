package com.googlecode.mp4parser.boxes.mp4.samplegrouping;

import java.nio.ByteBuffer;

public class TemporalLevelEntry
  extends GroupEntry
{
  public static final String TYPE = "tele";
  private boolean levelIndependentlyDecodable;
  private short reserved;
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if ((paramObject == null) || (getClass() != paramObject.getClass())) {
        return false;
      }
      paramObject = (TemporalLevelEntry)paramObject;
      if (this.levelIndependentlyDecodable != ((TemporalLevelEntry)paramObject).levelIndependentlyDecodable) {
        return false;
      }
    } while (this.reserved == ((TemporalLevelEntry)paramObject).reserved);
    return false;
  }
  
  public ByteBuffer get()
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(1);
    if (this.levelIndependentlyDecodable) {}
    for (int i = 128;; i = 0)
    {
      localByteBuffer.put((byte)i);
      localByteBuffer.rewind();
      return localByteBuffer;
    }
  }
  
  public String getType()
  {
    return "tele";
  }
  
  public int hashCode()
  {
    if (this.levelIndependentlyDecodable) {}
    for (int i = 1;; i = 0) {
      return i * 31 + this.reserved;
    }
  }
  
  public boolean isLevelIndependentlyDecodable()
  {
    return this.levelIndependentlyDecodable;
  }
  
  public void parse(ByteBuffer paramByteBuffer)
  {
    if ((paramByteBuffer.get() & 0x80) == 128) {}
    for (boolean bool = true;; bool = false)
    {
      this.levelIndependentlyDecodable = bool;
      return;
    }
  }
  
  public void setLevelIndependentlyDecodable(boolean paramBoolean)
  {
    this.levelIndependentlyDecodable = paramBoolean;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("TemporalLevelEntry");
    localStringBuilder.append("{levelIndependentlyDecodable=").append(this.levelIndependentlyDecodable);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/mp4/samplegrouping/TemporalLevelEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */