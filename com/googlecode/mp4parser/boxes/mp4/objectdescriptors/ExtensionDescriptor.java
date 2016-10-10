package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import com.coremedia.iso.Hex;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

@Descriptor(tags={19, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253})
public class ExtensionDescriptor
  extends BaseDescriptor
{
  private static Logger log = Logger.getLogger(ExtensionDescriptor.class.getName());
  byte[] bytes;
  
  static int[] allTags()
  {
    int[] arrayOfInt = new int[''];
    int i = 106;
    for (;;)
    {
      if (i >= 254) {
        return arrayOfInt;
      }
      int j = i - 106;
      log.finest("pos:" + j);
      arrayOfInt[j] = i;
      i += 1;
    }
  }
  
  public void parseDetail(ByteBuffer paramByteBuffer)
    throws IOException
  {
    if (getSize() > 0)
    {
      this.bytes = new byte[this.sizeOfInstance];
      paramByteBuffer.get(this.bytes);
    }
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder1 = new StringBuilder();
    localStringBuilder1.append("ExtensionDescriptor");
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/mp4/objectdescriptors/ExtensionDescriptor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */