package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import com.coremedia.iso.Hex;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

@Descriptor(tags={5})
public class DecoderSpecificInfo
  extends BaseDescriptor
{
  byte[] bytes;
  
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
        paramObject = (DecoderSpecificInfo)paramObject;
        if (!Arrays.equals(this.bytes, ((DecoderSpecificInfo)paramObject).bytes)) {
          bool = false;
        }
      }
    }
  }
  
  public int hashCode()
  {
    if (this.bytes != null) {}
    for (int i = Arrays.hashCode(this.bytes);; i = 0) {
      return i;
    }
  }
  
  public void parseDetail(ByteBuffer paramByteBuffer)
    throws IOException
  {
    if (this.sizeOfInstance > 0)
    {
      this.bytes = new byte[this.sizeOfInstance];
      paramByteBuffer.get(this.bytes);
    }
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder1 = new StringBuilder();
    localStringBuilder1.append("DecoderSpecificInfo");
    StringBuilder localStringBuilder2 = localStringBuilder1.append("{bytes=");
    if (this.bytes == null) {}
    for (String str = "null";; str = Hex.encodeHex(this.bytes))
    {
      localStringBuilder2.append(str);
      localStringBuilder1.append('}');
      return localStringBuilder1.toString();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/mp4/objectdescriptors/DecoderSpecificInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */