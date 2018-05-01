package com.googlecode.mp4parser.boxes.mp4.samplegrouping;

import com.coremedia.iso.Hex;
import java.nio.ByteBuffer;

public class UnknownEntry
  extends GroupEntry
{
  private ByteBuffer content;
  private String type;
  
  public UnknownEntry(String paramString)
  {
    this.type = paramString;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if ((paramObject == null) || (getClass() != paramObject.getClass())) {
        return false;
      }
      paramObject = (UnknownEntry)paramObject;
      if (this.content == null) {
        break;
      }
    } while (this.content.equals(((UnknownEntry)paramObject).content));
    for (;;)
    {
      return false;
      if (((UnknownEntry)paramObject).content == null) {
        break;
      }
    }
  }
  
  public ByteBuffer get()
  {
    return this.content.duplicate();
  }
  
  public ByteBuffer getContent()
  {
    return this.content;
  }
  
  public String getType()
  {
    return this.type;
  }
  
  public int hashCode()
  {
    if (this.content != null) {
      return this.content.hashCode();
    }
    return 0;
  }
  
  public void parse(ByteBuffer paramByteBuffer)
  {
    this.content = ((ByteBuffer)paramByteBuffer.duplicate().rewind());
  }
  
  public void setContent(ByteBuffer paramByteBuffer)
  {
    this.content = ((ByteBuffer)paramByteBuffer.duplicate().rewind());
  }
  
  public String toString()
  {
    ByteBuffer localByteBuffer = this.content.duplicate();
    localByteBuffer.rewind();
    byte[] arrayOfByte = new byte[localByteBuffer.limit()];
    localByteBuffer.get(arrayOfByte);
    return "UnknownEntry{content=" + Hex.encodeHex(arrayOfByte) + '}';
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/mp4/samplegrouping/UnknownEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */