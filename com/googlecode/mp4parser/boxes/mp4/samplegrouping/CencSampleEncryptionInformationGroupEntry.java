package com.googlecode.mp4parser.boxes.mp4.samplegrouping;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.util.UUIDConverter;
import java.nio.ByteBuffer;
import java.util.UUID;

public class CencSampleEncryptionInformationGroupEntry
  extends GroupEntry
{
  public static final String TYPE = "seig";
  private boolean isEncrypted;
  private byte ivSize;
  private UUID kid;
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if ((paramObject == null) || (getClass() != paramObject.getClass())) {
        return false;
      }
      paramObject = (CencSampleEncryptionInformationGroupEntry)paramObject;
      if (this.isEncrypted != ((CencSampleEncryptionInformationGroupEntry)paramObject).isEncrypted) {
        return false;
      }
      if (this.ivSize != ((CencSampleEncryptionInformationGroupEntry)paramObject).ivSize) {
        return false;
      }
      if (this.kid == null) {
        break;
      }
    } while (this.kid.equals(((CencSampleEncryptionInformationGroupEntry)paramObject).kid));
    for (;;)
    {
      return false;
      if (((CencSampleEncryptionInformationGroupEntry)paramObject).kid == null) {
        break;
      }
    }
  }
  
  public ByteBuffer get()
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(20);
    int i;
    if (this.isEncrypted)
    {
      i = 1;
      IsoTypeWriter.writeUInt24(localByteBuffer, i);
      if (!this.isEncrypted) {
        break label59;
      }
      IsoTypeWriter.writeUInt8(localByteBuffer, this.ivSize);
      localByteBuffer.put(UUIDConverter.convert(this.kid));
    }
    for (;;)
    {
      localByteBuffer.rewind();
      return localByteBuffer;
      i = 0;
      break;
      label59:
      localByteBuffer.put(new byte[17]);
    }
  }
  
  public byte getIvSize()
  {
    return this.ivSize;
  }
  
  public UUID getKid()
  {
    return this.kid;
  }
  
  public String getType()
  {
    return "seig";
  }
  
  public int hashCode()
  {
    int i;
    int k;
    if (this.isEncrypted)
    {
      i = 7;
      k = this.ivSize;
      if (this.kid == null) {
        break label48;
      }
    }
    label48:
    for (int j = this.kid.hashCode();; j = 0)
    {
      return (i * 31 + k) * 31 + j;
      i = 19;
      break;
    }
  }
  
  public boolean isEncrypted()
  {
    return this.isEncrypted;
  }
  
  public void parse(ByteBuffer paramByteBuffer)
  {
    boolean bool = true;
    if (IsoTypeReader.readUInt24(paramByteBuffer) == 1) {}
    for (;;)
    {
      this.isEncrypted = bool;
      this.ivSize = ((byte)IsoTypeReader.readUInt8(paramByteBuffer));
      byte[] arrayOfByte = new byte[16];
      paramByteBuffer.get(arrayOfByte);
      this.kid = UUIDConverter.convert(arrayOfByte);
      return;
      bool = false;
    }
  }
  
  public void setEncrypted(boolean paramBoolean)
  {
    this.isEncrypted = paramBoolean;
  }
  
  public void setIvSize(int paramInt)
  {
    this.ivSize = ((byte)paramInt);
  }
  
  public void setKid(UUID paramUUID)
  {
    this.kid = paramUUID;
  }
  
  public String toString()
  {
    return "CencSampleEncryptionInformationGroupEntry{isEncrypted=" + this.isEncrypted + ", ivSize=" + this.ivSize + ", kid=" + this.kid + '}';
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/mp4/samplegrouping/CencSampleEncryptionInformationGroupEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */