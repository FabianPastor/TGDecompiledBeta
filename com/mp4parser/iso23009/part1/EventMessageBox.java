package com.mp4parser.iso23009.part1;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class EventMessageBox
  extends AbstractFullBox
{
  public static final String TYPE = "emsg";
  long eventDuration;
  long id;
  byte[] messageData;
  long presentationTimeDelta;
  String schemeIdUri;
  long timescale;
  String value;
  
  static {}
  
  public EventMessageBox()
  {
    super("emsg");
  }
  
  protected void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.schemeIdUri = IsoTypeReader.readString(paramByteBuffer);
    this.value = IsoTypeReader.readString(paramByteBuffer);
    this.timescale = IsoTypeReader.readUInt32(paramByteBuffer);
    this.presentationTimeDelta = IsoTypeReader.readUInt32(paramByteBuffer);
    this.eventDuration = IsoTypeReader.readUInt32(paramByteBuffer);
    this.id = IsoTypeReader.readUInt32(paramByteBuffer);
    this.messageData = new byte[paramByteBuffer.remaining()];
    paramByteBuffer.get(this.messageData);
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUtf8String(paramByteBuffer, this.schemeIdUri);
    IsoTypeWriter.writeUtf8String(paramByteBuffer, this.value);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.timescale);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.presentationTimeDelta);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.eventDuration);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.id);
    paramByteBuffer.put(this.messageData);
  }
  
  protected long getContentSize()
  {
    return Utf8.utf8StringLengthInBytes(this.schemeIdUri) + 22 + Utf8.utf8StringLengthInBytes(this.value) + this.messageData.length;
  }
  
  public long getEventDuration()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_8, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.eventDuration;
  }
  
  public long getId()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_10, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.id;
  }
  
  public byte[] getMessageData()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_12, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.messageData;
  }
  
  public long getPresentationTimeDelta()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.presentationTimeDelta;
  }
  
  public String getSchemeIdUri()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.schemeIdUri;
  }
  
  public long getTimescale()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.timescale;
  }
  
  public String getValue()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.value;
  }
  
  public void setEventDuration(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_9, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.eventDuration = paramLong;
  }
  
  public void setId(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_11, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.id = paramLong;
  }
  
  public void setMessageData(byte[] paramArrayOfByte)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_13, this, this, paramArrayOfByte);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.messageData = paramArrayOfByte;
  }
  
  public void setPresentationTimeDelta(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_7, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.presentationTimeDelta = paramLong;
  }
  
  public void setSchemeIdUri(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.schemeIdUri = paramString;
  }
  
  public void setTimescale(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.timescale = paramLong;
  }
  
  public void setValue(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.value = paramString;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/mp4parser/iso23009/part1/EventMessageBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */