package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import com.coremedia.iso.IsoTypeReader;
import java.io.IOException;
import java.nio.ByteBuffer;

@Descriptor(tags={0})
public abstract class BaseDescriptor
{
  int sizeBytes;
  int sizeOfInstance;
  int tag;
  
  static
  {
    if (!BaseDescriptor.class.desiredAssertionStatus()) {}
    for (boolean bool = true;; bool = false)
    {
      $assertionsDisabled = bool;
      return;
    }
  }
  
  public int getSize()
  {
    return this.sizeOfInstance + 1 + this.sizeBytes;
  }
  
  public int getSizeBytes()
  {
    return this.sizeBytes;
  }
  
  public int getSizeOfInstance()
  {
    return this.sizeOfInstance;
  }
  
  public final void parse(int paramInt, ByteBuffer paramByteBuffer)
    throws IOException
  {
    this.tag = paramInt;
    int i = IsoTypeReader.readUInt8(paramByteBuffer);
    paramInt = 0 + 1;
    for (this.sizeOfInstance = (i & 0x7F);; this.sizeOfInstance = (this.sizeOfInstance << 7 | i & 0x7F))
    {
      if (i >>> 7 != 1)
      {
        this.sizeBytes = paramInt;
        ByteBuffer localByteBuffer = paramByteBuffer.slice();
        localByteBuffer.limit(this.sizeOfInstance);
        parseDetail(localByteBuffer);
        if (($assertionsDisabled) || (localByteBuffer.remaining() == 0)) {
          break;
        }
        throw new AssertionError(getClass().getSimpleName() + " has not been fully parsed");
      }
      i = IsoTypeReader.readUInt8(paramByteBuffer);
      paramInt++;
    }
    paramByteBuffer.position(paramByteBuffer.position() + this.sizeOfInstance);
  }
  
  public abstract void parseDetail(ByteBuffer paramByteBuffer)
    throws IOException;
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("BaseDescriptor");
    localStringBuilder.append("{tag=").append(this.tag);
    localStringBuilder.append(", sizeOfInstance=").append(this.sizeOfInstance);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/mp4/objectdescriptors/BaseDescriptor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */