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
    if (this == paramObject) {}
    do
    {
      return true;
      if ((paramObject == null) || (getClass() != paramObject.getClass())) {
        return false;
      }
      paramObject = (DecoderSpecificInfo)paramObject;
    } while (Arrays.equals(this.bytes, ((DecoderSpecificInfo)paramObject).bytes));
    return false;
  }
  
  public int hashCode()
  {
    if (this.bytes != null) {
      return Arrays.hashCode(this.bytes);
    }
    return 0;
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
  
  public ByteBuffer serialize()
  {
    return ByteBuffer.wrap(this.bytes);
  }
  
  public int serializedSize()
  {
    return this.bytes.length;
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