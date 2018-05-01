package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import java.io.IOException;
import java.nio.ByteBuffer;

@Descriptor(tags={6})
public class SLConfigDescriptor
  extends BaseDescriptor
{
  int predefined;
  
  public boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (this == paramObject) {}
    for (;;)
    {
      return bool;
      if ((paramObject == null) || (getClass() != paramObject.getClass()))
      {
        bool = false;
      }
      else
      {
        paramObject = (SLConfigDescriptor)paramObject;
        if (this.predefined != ((SLConfigDescriptor)paramObject).predefined) {
          bool = false;
        }
      }
    }
  }
  
  public int hashCode()
  {
    return this.predefined;
  }
  
  public void parseDetail(ByteBuffer paramByteBuffer)
    throws IOException
  {
    this.predefined = IsoTypeReader.readUInt8(paramByteBuffer);
  }
  
  public ByteBuffer serialize()
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(3);
    IsoTypeWriter.writeUInt8(localByteBuffer, 6);
    IsoTypeWriter.writeUInt8(localByteBuffer, 1);
    IsoTypeWriter.writeUInt8(localByteBuffer, this.predefined);
    return localByteBuffer;
  }
  
  public int serializedSize()
  {
    return 3;
  }
  
  public void setPredefined(int paramInt)
  {
    this.predefined = paramInt;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("SLConfigDescriptor");
    localStringBuilder.append("{predefined=").append(this.predefined);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/mp4/objectdescriptors/SLConfigDescriptor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */