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
    if (this == paramObject) {}
    do
    {
      return true;
      if ((paramObject == null) || (getClass() != paramObject.getClass())) {
        return false;
      }
      paramObject = (SLConfigDescriptor)paramObject;
    } while (this.predefined == ((SLConfigDescriptor)paramObject).predefined);
    return false;
  }
  
  public int getPredefined()
  {
    return this.predefined;
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