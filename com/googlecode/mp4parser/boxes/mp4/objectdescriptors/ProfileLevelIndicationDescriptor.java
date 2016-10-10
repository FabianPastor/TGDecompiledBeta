package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import com.coremedia.iso.IsoTypeReader;
import java.io.IOException;
import java.nio.ByteBuffer;

@Descriptor(tags={20})
public class ProfileLevelIndicationDescriptor
  extends BaseDescriptor
{
  int profileLevelIndicationIndex;
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if ((paramObject == null) || (getClass() != paramObject.getClass())) {
        return false;
      }
      paramObject = (ProfileLevelIndicationDescriptor)paramObject;
    } while (this.profileLevelIndicationIndex == ((ProfileLevelIndicationDescriptor)paramObject).profileLevelIndicationIndex);
    return false;
  }
  
  public int hashCode()
  {
    return this.profileLevelIndicationIndex;
  }
  
  public void parseDetail(ByteBuffer paramByteBuffer)
    throws IOException
  {
    this.profileLevelIndicationIndex = IsoTypeReader.readUInt8(paramByteBuffer);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("ProfileLevelIndicationDescriptor");
    localStringBuilder.append("{profileLevelIndicationIndex=").append(Integer.toHexString(this.profileLevelIndicationIndex));
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/mp4/objectdescriptors/ProfileLevelIndicationDescriptor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */