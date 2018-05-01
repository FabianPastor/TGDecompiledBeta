package com.googlecode.mp4parser.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.UUID;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public abstract class AbstractTrackEncryptionBox
  extends AbstractFullBox
{
  int defaultAlgorithmId;
  int defaultIvSize;
  byte[] default_KID;
  
  static {}
  
  protected AbstractTrackEncryptionBox(String paramString)
  {
    super(paramString);
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.defaultAlgorithmId = IsoTypeReader.readUInt24(paramByteBuffer);
    this.defaultIvSize = IsoTypeReader.readUInt8(paramByteBuffer);
    this.default_KID = new byte[16];
    paramByteBuffer.get(this.default_KID);
  }
  
  public boolean equals(Object paramObject)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this, paramObject);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    if (this == paramObject) {}
    do
    {
      return true;
      if ((paramObject == null) || (getClass() != paramObject.getClass())) {
        return false;
      }
      paramObject = (AbstractTrackEncryptionBox)paramObject;
      if (this.defaultAlgorithmId != ((AbstractTrackEncryptionBox)paramObject).defaultAlgorithmId) {
        return false;
      }
      if (this.defaultIvSize != ((AbstractTrackEncryptionBox)paramObject).defaultIvSize) {
        return false;
      }
    } while (Arrays.equals(this.default_KID, ((AbstractTrackEncryptionBox)paramObject).default_KID));
    return false;
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt24(paramByteBuffer, this.defaultAlgorithmId);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.defaultIvSize);
    paramByteBuffer.put(this.default_KID);
  }
  
  protected long getContentSize()
  {
    return 24L;
  }
  
  public int getDefaultAlgorithmId()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.defaultAlgorithmId;
  }
  
  public int getDefaultIvSize()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.defaultIvSize;
  }
  
  public UUID getDefault_KID()
  {
    Object localObject = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before((JoinPoint)localObject);
    localObject = ByteBuffer.wrap(this.default_KID);
    ((ByteBuffer)localObject).order(ByteOrder.BIG_ENDIAN);
    return new UUID(((ByteBuffer)localObject).getLong(), ((ByteBuffer)localObject).getLong());
  }
  
  public int hashCode()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_7, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    int j = this.defaultAlgorithmId;
    int k = this.defaultIvSize;
    if (this.default_KID != null) {}
    for (int i = Arrays.hashCode(this.default_KID);; i = 0) {
      return (j * 31 + k) * 31 + i;
    }
  }
  
  public void setDefaultAlgorithmId(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.defaultAlgorithmId = paramInt;
  }
  
  public void setDefaultIvSize(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.defaultIvSize = paramInt;
  }
  
  public void setDefault_KID(UUID paramUUID)
  {
    Object localObject = Factory.makeJP(ajc$tjp_5, this, this, paramUUID);
    RequiresParseDetailAspect.aspectOf().before((JoinPoint)localObject);
    localObject = ByteBuffer.wrap(new byte[16]);
    ((ByteBuffer)localObject).putLong(paramUUID.getMostSignificantBits());
    ((ByteBuffer)localObject).putLong(paramUUID.getLeastSignificantBits());
    this.default_KID = ((ByteBuffer)localObject).array();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/AbstractTrackEncryptionBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */