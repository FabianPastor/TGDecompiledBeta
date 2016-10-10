package com.mp4parser.iso23001.part7;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.util.CastUtils;
import com.googlecode.mp4parser.util.UUIDConverter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class ProtectionSystemSpecificHeaderBox
  extends AbstractFullBox
{
  public static byte[] OMA2_SYSTEM_ID;
  public static byte[] PLAYREADY_SYSTEM_ID;
  public static final String TYPE = "pssh";
  byte[] content;
  List<UUID> keyIds = new ArrayList();
  byte[] systemId;
  
  static
  {
    
    if (!ProtectionSystemSpecificHeaderBox.class.desiredAssertionStatus()) {}
    for (boolean bool = true;; bool = false)
    {
      $assertionsDisabled = bool;
      OMA2_SYSTEM_ID = UUIDConverter.convert(UUID.fromString("A2B55680-6F43-11E0-9A3F-0002A5D5C51B"));
      PLAYREADY_SYSTEM_ID = UUIDConverter.convert(UUID.fromString("9A04F079-9840-4286-AB92-E65BE0885F95"));
      return;
    }
  }
  
  public ProtectionSystemSpecificHeaderBox()
  {
    super("pssh");
  }
  
  protected void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.systemId = new byte[16];
    paramByteBuffer.get(this.systemId);
    int i;
    if (getVersion() > 0) {
      i = CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer));
    }
    for (;;)
    {
      if (i <= 0)
      {
        long l = IsoTypeReader.readUInt32(paramByteBuffer);
        this.content = new byte[paramByteBuffer.remaining()];
        paramByteBuffer.get(this.content);
        if (($assertionsDisabled) || (l == this.content.length)) {
          break;
        }
        throw new AssertionError();
      }
      byte[] arrayOfByte = new byte[16];
      paramByteBuffer.get(arrayOfByte);
      this.keyIds.add(UUIDConverter.convert(arrayOfByte));
      i -= 1;
    }
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    assert (this.systemId.length == 16);
    paramByteBuffer.put(this.systemId, 0, 16);
    Iterator localIterator;
    if (getVersion() > 0)
    {
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.keyIds.size());
      localIterator = this.keyIds.iterator();
    }
    for (;;)
    {
      if (!localIterator.hasNext())
      {
        IsoTypeWriter.writeUInt32(paramByteBuffer, this.content.length);
        paramByteBuffer.put(this.content);
        return;
      }
      paramByteBuffer.put(UUIDConverter.convert((UUID)localIterator.next()));
    }
  }
  
  public byte[] getContent()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.content;
  }
  
  protected long getContentSize()
  {
    long l2 = this.content.length + 24;
    long l1 = l2;
    if (getVersion() > 0) {
      l1 = l2 + 4L + this.keyIds.size() * 16;
    }
    return l1;
  }
  
  public List<UUID> getKeyIds()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.keyIds;
  }
  
  public byte[] getSystemId()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.systemId;
  }
  
  public void setContent(byte[] paramArrayOfByte)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this, paramArrayOfByte);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.content = paramArrayOfByte;
  }
  
  public void setKeyIds(List<UUID> paramList)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, paramList);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.keyIds = paramList;
  }
  
  public void setSystemId(byte[] paramArrayOfByte)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, paramArrayOfByte);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    assert (paramArrayOfByte.length == 16);
    this.systemId = paramArrayOfByte;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/mp4parser/iso23001/part7/ProtectionSystemSpecificHeaderBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */