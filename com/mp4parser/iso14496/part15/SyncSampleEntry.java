package com.mp4parser.iso14496.part15;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry;
import java.nio.ByteBuffer;

public class SyncSampleEntry
  extends GroupEntry
{
  public static final String TYPE = "sync";
  int nalUnitType;
  int reserved;
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if ((paramObject == null) || (getClass() != paramObject.getClass())) {
        return false;
      }
      paramObject = (SyncSampleEntry)paramObject;
      if (this.nalUnitType != ((SyncSampleEntry)paramObject).nalUnitType) {
        return false;
      }
    } while (this.reserved == ((SyncSampleEntry)paramObject).reserved);
    return false;
  }
  
  public ByteBuffer get()
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(1);
    IsoTypeWriter.writeUInt8(localByteBuffer, this.nalUnitType + (this.reserved << 6));
    return (ByteBuffer)localByteBuffer.rewind();
  }
  
  public int getNalUnitType()
  {
    return this.nalUnitType;
  }
  
  public int getReserved()
  {
    return this.reserved;
  }
  
  public String getType()
  {
    return "sync";
  }
  
  public int hashCode()
  {
    return this.reserved * 31 + this.nalUnitType;
  }
  
  public void parse(ByteBuffer paramByteBuffer)
  {
    int i = IsoTypeReader.readUInt8(paramByteBuffer);
    this.reserved = ((i & 0xC0) >> 6);
    this.nalUnitType = (i & 0x3F);
  }
  
  public void setNalUnitType(int paramInt)
  {
    this.nalUnitType = paramInt;
  }
  
  public void setReserved(int paramInt)
  {
    this.reserved = paramInt;
  }
  
  public String toString()
  {
    return "SyncSampleEntry{reserved=" + this.reserved + ", nalUnitType=" + this.nalUnitType + '}';
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/mp4parser/iso14496/part15/SyncSampleEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */