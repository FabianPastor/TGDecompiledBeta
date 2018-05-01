package com.coremedia.iso.boxes.apple;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public final class AppleLosslessSpecificBox
  extends AbstractFullBox
{
  public static final String TYPE = "alac";
  private long bitRate;
  private int channels;
  private int historyMult;
  private int initialHistory;
  private int kModifier;
  private long maxCodedFrameSize;
  private long maxSamplePerFrame;
  private long sampleRate;
  private int sampleSize;
  private int unknown1;
  private int unknown2;
  
  static {}
  
  public AppleLosslessSpecificBox()
  {
    super("alac");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.maxSamplePerFrame = IsoTypeReader.readUInt32(paramByteBuffer);
    this.unknown1 = IsoTypeReader.readUInt8(paramByteBuffer);
    this.sampleSize = IsoTypeReader.readUInt8(paramByteBuffer);
    this.historyMult = IsoTypeReader.readUInt8(paramByteBuffer);
    this.initialHistory = IsoTypeReader.readUInt8(paramByteBuffer);
    this.kModifier = IsoTypeReader.readUInt8(paramByteBuffer);
    this.channels = IsoTypeReader.readUInt8(paramByteBuffer);
    this.unknown2 = IsoTypeReader.readUInt16(paramByteBuffer);
    this.maxCodedFrameSize = IsoTypeReader.readUInt32(paramByteBuffer);
    this.bitRate = IsoTypeReader.readUInt32(paramByteBuffer);
    this.sampleRate = IsoTypeReader.readUInt32(paramByteBuffer);
  }
  
  public long getBitRate()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_18, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.bitRate;
  }
  
  public int getChannels()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_12, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.channels;
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.maxSamplePerFrame);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.unknown1);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.sampleSize);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.historyMult);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.initialHistory);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.kModifier);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.channels);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.unknown2);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.maxCodedFrameSize);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.bitRate);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.sampleRate);
  }
  
  protected long getContentSize()
  {
    return 28L;
  }
  
  public int getHistoryMult()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.historyMult;
  }
  
  public int getInitialHistory()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_8, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.initialHistory;
  }
  
  public int getKModifier()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_10, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.kModifier;
  }
  
  public long getMaxCodedFrameSize()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_16, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.maxCodedFrameSize;
  }
  
  public long getMaxSamplePerFrame()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.maxSamplePerFrame;
  }
  
  public long getSampleRate()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_20, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.sampleRate;
  }
  
  public int getSampleSize()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.sampleSize;
  }
  
  public int getUnknown1()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.unknown1;
  }
  
  public int getUnknown2()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_14, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.unknown2;
  }
  
  public void setBitRate(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_19, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.bitRate = paramInt;
  }
  
  public void setChannels(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_13, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.channels = paramInt;
  }
  
  public void setHistoryMult(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_7, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.historyMult = paramInt;
  }
  
  public void setInitialHistory(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_9, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.initialHistory = paramInt;
  }
  
  public void setKModifier(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_11, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.kModifier = paramInt;
  }
  
  public void setMaxCodedFrameSize(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_17, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.maxCodedFrameSize = paramInt;
  }
  
  public void setMaxSamplePerFrame(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.maxSamplePerFrame = paramInt;
  }
  
  public void setSampleRate(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_21, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.sampleRate = paramInt;
  }
  
  public void setSampleSize(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.sampleSize = paramInt;
  }
  
  public void setUnknown1(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.unknown1 = paramInt;
  }
  
  public void setUnknown2(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_15, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.unknown2 = paramInt;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/apple/AppleLosslessSpecificBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */