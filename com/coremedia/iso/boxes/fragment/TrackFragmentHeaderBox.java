package com.coremedia.iso.boxes.fragment;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class TrackFragmentHeaderBox
  extends AbstractFullBox
{
  public static final String TYPE = "tfhd";
  private long baseDataOffset = -1L;
  private boolean defaultBaseIsMoof;
  private long defaultSampleDuration = -1L;
  private SampleFlags defaultSampleFlags;
  private long defaultSampleSize = -1L;
  private boolean durationIsEmpty;
  private long sampleDescriptionIndex;
  private long trackId;
  
  static {}
  
  public TrackFragmentHeaderBox()
  {
    super("tfhd");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.trackId = IsoTypeReader.readUInt32(paramByteBuffer);
    if ((getFlags() & 0x1) == 1) {
      this.baseDataOffset = IsoTypeReader.readUInt64(paramByteBuffer);
    }
    if ((getFlags() & 0x2) == 2) {
      this.sampleDescriptionIndex = IsoTypeReader.readUInt32(paramByteBuffer);
    }
    if ((getFlags() & 0x8) == 8) {
      this.defaultSampleDuration = IsoTypeReader.readUInt32(paramByteBuffer);
    }
    if ((getFlags() & 0x10) == 16) {
      this.defaultSampleSize = IsoTypeReader.readUInt32(paramByteBuffer);
    }
    if ((getFlags() & 0x20) == 32) {
      this.defaultSampleFlags = new SampleFlags(paramByteBuffer);
    }
    if ((getFlags() & 0x10000) == 65536) {
      this.durationIsEmpty = true;
    }
    if ((getFlags() & 0x20000) == 131072) {
      this.defaultBaseIsMoof = true;
    }
  }
  
  public long getBaseDataOffset()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.baseDataOffset;
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.trackId);
    if ((getFlags() & 0x1) == 1) {
      IsoTypeWriter.writeUInt64(paramByteBuffer, getBaseDataOffset());
    }
    if ((getFlags() & 0x2) == 2) {
      IsoTypeWriter.writeUInt32(paramByteBuffer, getSampleDescriptionIndex());
    }
    if ((getFlags() & 0x8) == 8) {
      IsoTypeWriter.writeUInt32(paramByteBuffer, getDefaultSampleDuration());
    }
    if ((getFlags() & 0x10) == 16) {
      IsoTypeWriter.writeUInt32(paramByteBuffer, getDefaultSampleSize());
    }
    if ((getFlags() & 0x20) == 32) {
      this.defaultSampleFlags.getContent(paramByteBuffer);
    }
  }
  
  protected long getContentSize()
  {
    long l2 = 8L;
    int i = getFlags();
    if ((i & 0x1) == 1) {
      l2 = 8L + 8L;
    }
    long l1 = l2;
    if ((i & 0x2) == 2) {
      l1 = l2 + 4L;
    }
    l2 = l1;
    if ((i & 0x8) == 8) {
      l2 = l1 + 4L;
    }
    l1 = l2;
    if ((i & 0x10) == 16) {
      l1 = l2 + 4L;
    }
    l2 = l1;
    if ((i & 0x20) == 32) {
      l2 = l1 + 4L;
    }
    return l2;
  }
  
  public long getDefaultSampleDuration()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_8, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.defaultSampleDuration;
  }
  
  public SampleFlags getDefaultSampleFlags()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_10, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.defaultSampleFlags;
  }
  
  public long getDefaultSampleSize()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_9, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.defaultSampleSize;
  }
  
  public long getSampleDescriptionIndex()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_7, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.sampleDescriptionIndex;
  }
  
  public long getTrackId()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.trackId;
  }
  
  public boolean hasBaseDataOffset()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return (getFlags() & 0x1) != 0;
  }
  
  public boolean hasDefaultSampleDuration()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return (getFlags() & 0x8) != 0;
  }
  
  public boolean hasDefaultSampleFlags()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return (getFlags() & 0x20) != 0;
  }
  
  public boolean hasDefaultSampleSize()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return (getFlags() & 0x10) != 0;
  }
  
  public boolean hasSampleDescriptionIndex()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return (getFlags() & 0x2) != 0;
  }
  
  public boolean isDefaultBaseIsMoof()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_12, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.defaultBaseIsMoof;
  }
  
  public boolean isDurationIsEmpty()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_11, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.durationIsEmpty;
  }
  
  public void setBaseDataOffset(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_14, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    if (paramLong == -1L) {
      setFlags(getFlags() & 0x7FFFFFFE);
    }
    for (;;)
    {
      this.baseDataOffset = paramLong;
      return;
      setFlags(getFlags() | 0x1);
    }
  }
  
  public void setDefaultBaseIsMoof(boolean paramBoolean)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_20, this, this, Conversions.booleanObject(paramBoolean));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    setFlags(getFlags() | 0x20000);
    this.defaultBaseIsMoof = paramBoolean;
  }
  
  public void setDefaultSampleDuration(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_16, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    setFlags(getFlags() | 0x8);
    this.defaultSampleDuration = paramLong;
  }
  
  public void setDefaultSampleFlags(SampleFlags paramSampleFlags)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_18, this, this, paramSampleFlags);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    setFlags(getFlags() | 0x20);
    this.defaultSampleFlags = paramSampleFlags;
  }
  
  public void setDefaultSampleSize(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_17, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    setFlags(getFlags() | 0x10);
    this.defaultSampleSize = paramLong;
  }
  
  public void setDurationIsEmpty(boolean paramBoolean)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_19, this, this, Conversions.booleanObject(paramBoolean));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    setFlags(getFlags() | 0x10000);
    this.durationIsEmpty = paramBoolean;
  }
  
  public void setSampleDescriptionIndex(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_15, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    if (paramLong == -1L) {
      setFlags(getFlags() & 0x7FFFFFFD);
    }
    for (;;)
    {
      this.sampleDescriptionIndex = paramLong;
      return;
      setFlags(getFlags() | 0x2);
    }
  }
  
  public void setTrackId(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_13, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.trackId = paramLong;
  }
  
  public String toString()
  {
    Object localObject = Factory.makeJP(ajc$tjp_21, this, this);
    RequiresParseDetailAspect.aspectOf().before((JoinPoint)localObject);
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("TrackFragmentHeaderBox");
    ((StringBuilder)localObject).append("{trackId=").append(this.trackId);
    ((StringBuilder)localObject).append(", baseDataOffset=").append(this.baseDataOffset);
    ((StringBuilder)localObject).append(", sampleDescriptionIndex=").append(this.sampleDescriptionIndex);
    ((StringBuilder)localObject).append(", defaultSampleDuration=").append(this.defaultSampleDuration);
    ((StringBuilder)localObject).append(", defaultSampleSize=").append(this.defaultSampleSize);
    ((StringBuilder)localObject).append(", defaultSampleFlags=").append(this.defaultSampleFlags);
    ((StringBuilder)localObject).append(", durationIsEmpty=").append(this.durationIsEmpty);
    ((StringBuilder)localObject).append(", defaultBaseIsMoof=").append(this.defaultBaseIsMoof);
    ((StringBuilder)localObject).append('}');
    return ((StringBuilder)localObject).toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/fragment/TrackFragmentHeaderBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */