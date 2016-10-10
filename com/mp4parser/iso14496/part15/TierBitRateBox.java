package com.mp4parser.iso14496.part15;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class TierBitRateBox
  extends AbstractBox
{
  public static final String TYPE = "tibr";
  long avgBitRate;
  long baseBitRate;
  long maxBitRate;
  long tierAvgBitRate;
  long tierBaseBitRate;
  long tierMaxBitRate;
  
  static {}
  
  public TierBitRateBox()
  {
    super("tibr");
  }
  
  protected void _parseDetails(ByteBuffer paramByteBuffer)
  {
    this.baseBitRate = IsoTypeReader.readUInt32(paramByteBuffer);
    this.maxBitRate = IsoTypeReader.readUInt32(paramByteBuffer);
    this.avgBitRate = IsoTypeReader.readUInt32(paramByteBuffer);
    this.tierBaseBitRate = IsoTypeReader.readUInt32(paramByteBuffer);
    this.tierMaxBitRate = IsoTypeReader.readUInt32(paramByteBuffer);
    this.tierAvgBitRate = IsoTypeReader.readUInt32(paramByteBuffer);
  }
  
  public long getAvgBitRate()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.avgBitRate;
  }
  
  public long getBaseBitRate()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.baseBitRate;
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.baseBitRate);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.maxBitRate);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.avgBitRate);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.tierBaseBitRate);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.tierMaxBitRate);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.tierAvgBitRate);
  }
  
  protected long getContentSize()
  {
    return 24L;
  }
  
  public long getMaxBitRate()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.maxBitRate;
  }
  
  public long getTierAvgBitRate()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_10, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.tierAvgBitRate;
  }
  
  public long getTierBaseBitRate()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.tierBaseBitRate;
  }
  
  public long getTierMaxBitRate()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_8, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.tierMaxBitRate;
  }
  
  public void setAvgBitRate(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.avgBitRate = paramLong;
  }
  
  public void setBaseBitRate(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.baseBitRate = paramLong;
  }
  
  public void setMaxBitRate(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.maxBitRate = paramLong;
  }
  
  public void setTierAvgBitRate(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_11, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.tierAvgBitRate = paramLong;
  }
  
  public void setTierBaseBitRate(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_7, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.tierBaseBitRate = paramLong;
  }
  
  public void setTierMaxBitRate(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_9, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.tierMaxBitRate = paramLong;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/mp4parser/iso14496/part15/TierBitRateBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */