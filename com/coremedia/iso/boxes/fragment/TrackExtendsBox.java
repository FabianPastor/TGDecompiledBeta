package com.coremedia.iso.boxes.fragment;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class TrackExtendsBox
  extends AbstractFullBox
{
  public static final String TYPE = "trex";
  private long defaultSampleDescriptionIndex;
  private long defaultSampleDuration;
  private SampleFlags defaultSampleFlags;
  private long defaultSampleSize;
  private long trackId;
  
  static {}
  
  public TrackExtendsBox()
  {
    super("trex");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.trackId = IsoTypeReader.readUInt32(paramByteBuffer);
    this.defaultSampleDescriptionIndex = IsoTypeReader.readUInt32(paramByteBuffer);
    this.defaultSampleDuration = IsoTypeReader.readUInt32(paramByteBuffer);
    this.defaultSampleSize = IsoTypeReader.readUInt32(paramByteBuffer);
    this.defaultSampleFlags = new SampleFlags(paramByteBuffer);
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.trackId);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.defaultSampleDescriptionIndex);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.defaultSampleDuration);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.defaultSampleSize);
    this.defaultSampleFlags.getContent(paramByteBuffer);
  }
  
  protected long getContentSize()
  {
    return 24L;
  }
  
  public long getDefaultSampleDescriptionIndex()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.defaultSampleDescriptionIndex;
  }
  
  public long getDefaultSampleDuration()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.defaultSampleDuration;
  }
  
  public SampleFlags getDefaultSampleFlags()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.defaultSampleFlags;
  }
  
  public String getDefaultSampleFlagsStr()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.defaultSampleFlags.toString();
  }
  
  public long getDefaultSampleSize()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.defaultSampleSize;
  }
  
  public long getTrackId()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.trackId;
  }
  
  public void setDefaultSampleDescriptionIndex(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_7, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.defaultSampleDescriptionIndex = paramLong;
  }
  
  public void setDefaultSampleDuration(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_8, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.defaultSampleDuration = paramLong;
  }
  
  public void setDefaultSampleFlags(SampleFlags paramSampleFlags)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_10, this, this, paramSampleFlags);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.defaultSampleFlags = paramSampleFlags;
  }
  
  public void setDefaultSampleSize(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_9, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.defaultSampleSize = paramLong;
  }
  
  public void setTrackId(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.trackId = paramLong;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/fragment/TrackExtendsBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */