package com.mp4parser.iso14496.part12;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public final class BitRateBox
  extends AbstractBox
{
  public static final String TYPE = "btrt";
  private long avgBitrate;
  private long bufferSizeDb;
  private long maxBitrate;
  
  static {}
  
  public BitRateBox()
  {
    super("btrt");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    this.bufferSizeDb = IsoTypeReader.readUInt32(paramByteBuffer);
    this.maxBitrate = IsoTypeReader.readUInt32(paramByteBuffer);
    this.avgBitrate = IsoTypeReader.readUInt32(paramByteBuffer);
  }
  
  public long getAvgBitrate()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.avgBitrate;
  }
  
  public long getBufferSizeDb()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.bufferSizeDb;
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.bufferSizeDb);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.maxBitrate);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.avgBitrate);
  }
  
  protected long getContentSize()
  {
    return 12L;
  }
  
  public long getMaxBitrate()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.maxBitrate;
  }
  
  public void setAvgBitrate(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.avgBitrate = paramLong;
  }
  
  public void setBufferSizeDb(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.bufferSizeDb = paramLong;
  }
  
  public void setMaxBitrate(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.maxBitrate = paramLong;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/mp4parser/iso14496/part12/BitRateBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */