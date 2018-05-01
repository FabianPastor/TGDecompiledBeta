package com.googlecode.mp4parser.boxes.apple;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.sampleentry.SampleEntry;
import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class TimeCodeBox
  extends AbstractBox
  implements SampleEntry
{
  public static final String TYPE = "tmcd";
  int dataReferenceIndex;
  long flags;
  int frameDuration;
  int numberOfFrames;
  int reserved1;
  int reserved2;
  byte[] rest = new byte[0];
  int timeScale;
  
  static {}
  
  public TimeCodeBox()
  {
    super("tmcd");
  }
  
  protected void _parseDetails(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer.position(6);
    this.dataReferenceIndex = IsoTypeReader.readUInt16(paramByteBuffer);
    this.reserved1 = paramByteBuffer.getInt();
    this.flags = IsoTypeReader.readUInt32(paramByteBuffer);
    this.timeScale = paramByteBuffer.getInt();
    this.frameDuration = paramByteBuffer.getInt();
    this.numberOfFrames = IsoTypeReader.readUInt8(paramByteBuffer);
    this.reserved2 = IsoTypeReader.readUInt24(paramByteBuffer);
    this.rest = new byte[paramByteBuffer.remaining()];
    paramByteBuffer.get(this.rest);
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer.put(new byte[6]);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.dataReferenceIndex);
    paramByteBuffer.putInt(this.reserved1);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.flags);
    paramByteBuffer.putInt(this.timeScale);
    paramByteBuffer.putInt(this.frameDuration);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.numberOfFrames);
    IsoTypeWriter.writeUInt24(paramByteBuffer, this.reserved2);
    paramByteBuffer.put(this.rest);
  }
  
  protected long getContentSize()
  {
    return this.rest.length + 28;
  }
  
  public int getDataReferenceIndex()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.dataReferenceIndex;
  }
  
  public long getFlags()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_13, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.flags;
  }
  
  public int getFrameDuration()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.frameDuration;
  }
  
  public int getNumberOfFrames()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_7, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.numberOfFrames;
  }
  
  public int getReserved1()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_9, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.reserved1;
  }
  
  public int getReserved2()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_11, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.reserved2;
  }
  
  public byte[] getRest()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_15, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.rest;
  }
  
  public int getTimeScale()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.timeScale;
  }
  
  public void setDataReferenceIndex(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.dataReferenceIndex = paramInt;
  }
  
  public void setFlags(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_14, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.flags = paramLong;
  }
  
  public void setFrameDuration(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.frameDuration = paramInt;
  }
  
  public void setNumberOfFrames(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_8, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.numberOfFrames = paramInt;
  }
  
  public void setReserved1(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_10, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.reserved1 = paramInt;
  }
  
  public void setReserved2(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_12, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.reserved2 = paramInt;
  }
  
  public void setRest(byte[] paramArrayOfByte)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_16, this, this, paramArrayOfByte);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.rest = paramArrayOfByte;
  }
  
  public void setTimeScale(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.timeScale = paramInt;
  }
  
  public String toString()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return "TimeCodeBox{timeScale=" + this.timeScale + ", frameDuration=" + this.frameDuration + ", numberOfFrames=" + this.numberOfFrames + ", reserved1=" + this.reserved1 + ", reserved2=" + this.reserved2 + ", flags=" + this.flags + '}';
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/apple/TimeCodeBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */