package com.googlecode.mp4parser.boxes.piff;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.util.CastUtils;
import com.googlecode.mp4parser.util.UUIDConverter;
import java.nio.ByteBuffer;
import java.util.UUID;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class UuidBasedProtectionSystemSpecificHeaderBox
  extends AbstractFullBox
{
  public static byte[] USER_TYPE = { -48, -118, 79, 24, 16, -13, 74, -126, -74, -56, 50, -40, -85, -95, -125, -45 };
  ProtectionSpecificHeader protectionSpecificHeader;
  UUID systemId;
  
  static {}
  
  public UuidBasedProtectionSystemSpecificHeaderBox()
  {
    super("uuid", USER_TYPE);
  }
  
  protected void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    byte[] arrayOfByte = new byte[16];
    paramByteBuffer.get(arrayOfByte);
    this.systemId = UUIDConverter.convert(arrayOfByte);
    CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer));
    this.protectionSpecificHeader = ProtectionSpecificHeader.createFor(this.systemId, paramByteBuffer);
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt64(paramByteBuffer, this.systemId.getMostSignificantBits());
    IsoTypeWriter.writeUInt64(paramByteBuffer, this.systemId.getLeastSignificantBits());
    ByteBuffer localByteBuffer = this.protectionSpecificHeader.getData();
    localByteBuffer.rewind();
    IsoTypeWriter.writeUInt32(paramByteBuffer, localByteBuffer.limit());
    paramByteBuffer.put(localByteBuffer);
  }
  
  protected long getContentSize()
  {
    return this.protectionSpecificHeader.getData().limit() + 24;
  }
  
  public ProtectionSpecificHeader getProtectionSpecificHeader()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.protectionSpecificHeader;
  }
  
  public String getProtectionSpecificHeaderString()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.protectionSpecificHeader.toString();
  }
  
  public UUID getSystemId()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.systemId;
  }
  
  public String getSystemIdString()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.systemId.toString();
  }
  
  public byte[] getUserType()
  {
    return USER_TYPE;
  }
  
  public void setProtectionSpecificHeader(ProtectionSpecificHeader paramProtectionSpecificHeader)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this, paramProtectionSpecificHeader);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.protectionSpecificHeader = paramProtectionSpecificHeader;
  }
  
  public void setSystemId(UUID paramUUID)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, paramUUID);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.systemId = paramUUID;
  }
  
  public String toString()
  {
    Object localObject = Factory.makeJP(ajc$tjp_6, this, this);
    RequiresParseDetailAspect.aspectOf().before((JoinPoint)localObject);
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("UuidBasedProtectionSystemSpecificHeaderBox");
    ((StringBuilder)localObject).append("{systemId=").append(this.systemId.toString());
    ((StringBuilder)localObject).append(", dataSize=").append(this.protectionSpecificHeader.getData().limit());
    ((StringBuilder)localObject).append('}');
    return ((StringBuilder)localObject).toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/piff/UuidBasedProtectionSystemSpecificHeaderBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */