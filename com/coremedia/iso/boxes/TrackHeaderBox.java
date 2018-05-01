package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.util.DateHelper;
import com.googlecode.mp4parser.util.Matrix;
import java.nio.ByteBuffer;
import java.util.Date;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class TrackHeaderBox
  extends AbstractFullBox
{
  public static final String TYPE = "tkhd";
  private int alternateGroup;
  private Date creationTime;
  private long duration;
  private double height;
  private int layer;
  private Matrix matrix = Matrix.ROTATE_0;
  private Date modificationTime;
  private long trackId;
  private float volume;
  private double width;
  
  static {}
  
  public TrackHeaderBox()
  {
    super("tkhd");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    if (getVersion() == 1)
    {
      this.creationTime = DateHelper.convert(IsoTypeReader.readUInt64(paramByteBuffer));
      this.modificationTime = DateHelper.convert(IsoTypeReader.readUInt64(paramByteBuffer));
      this.trackId = IsoTypeReader.readUInt32(paramByteBuffer);
      IsoTypeReader.readUInt32(paramByteBuffer);
      this.duration = paramByteBuffer.getLong();
      if (this.duration < -1L) {
        throw new RuntimeException("The tracks duration is bigger than Long.MAX_VALUE");
      }
    }
    else
    {
      this.creationTime = DateHelper.convert(IsoTypeReader.readUInt32(paramByteBuffer));
      this.modificationTime = DateHelper.convert(IsoTypeReader.readUInt32(paramByteBuffer));
      this.trackId = IsoTypeReader.readUInt32(paramByteBuffer);
      IsoTypeReader.readUInt32(paramByteBuffer);
      this.duration = IsoTypeReader.readUInt32(paramByteBuffer);
    }
    IsoTypeReader.readUInt32(paramByteBuffer);
    IsoTypeReader.readUInt32(paramByteBuffer);
    this.layer = IsoTypeReader.readUInt16(paramByteBuffer);
    this.alternateGroup = IsoTypeReader.readUInt16(paramByteBuffer);
    this.volume = IsoTypeReader.readFixedPoint88(paramByteBuffer);
    IsoTypeReader.readUInt16(paramByteBuffer);
    this.matrix = Matrix.fromByteBuffer(paramByteBuffer);
    this.width = IsoTypeReader.readFixedPoint1616(paramByteBuffer);
    this.height = IsoTypeReader.readFixedPoint1616(paramByteBuffer);
  }
  
  public int getAlternateGroup()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.alternateGroup;
  }
  
  public void getContent(ByteBuffer paramByteBuffer)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_10, this, this, paramByteBuffer);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    writeVersionAndFlags(paramByteBuffer);
    if (getVersion() == 1)
    {
      IsoTypeWriter.writeUInt64(paramByteBuffer, DateHelper.convert(this.creationTime));
      IsoTypeWriter.writeUInt64(paramByteBuffer, DateHelper.convert(this.modificationTime));
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.trackId);
      IsoTypeWriter.writeUInt32(paramByteBuffer, 0L);
      IsoTypeWriter.writeUInt64(paramByteBuffer, this.duration);
    }
    for (;;)
    {
      IsoTypeWriter.writeUInt32(paramByteBuffer, 0L);
      IsoTypeWriter.writeUInt32(paramByteBuffer, 0L);
      IsoTypeWriter.writeUInt16(paramByteBuffer, this.layer);
      IsoTypeWriter.writeUInt16(paramByteBuffer, this.alternateGroup);
      IsoTypeWriter.writeFixedPoint88(paramByteBuffer, this.volume);
      IsoTypeWriter.writeUInt16(paramByteBuffer, 0);
      this.matrix.getContent(paramByteBuffer);
      IsoTypeWriter.writeFixedPoint1616(paramByteBuffer, this.width);
      IsoTypeWriter.writeFixedPoint1616(paramByteBuffer, this.height);
      return;
      IsoTypeWriter.writeUInt32(paramByteBuffer, DateHelper.convert(this.creationTime));
      IsoTypeWriter.writeUInt32(paramByteBuffer, DateHelper.convert(this.modificationTime));
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.trackId);
      IsoTypeWriter.writeUInt32(paramByteBuffer, 0L);
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.duration);
    }
  }
  
  protected long getContentSize()
  {
    if (getVersion() == 1) {}
    for (long l = 4L + 32L;; l = 4L + 20L) {
      return l + 60L;
    }
  }
  
  public Date getCreationTime()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.creationTime;
  }
  
  public long getDuration()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.duration;
  }
  
  public double getHeight()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_9, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.height;
  }
  
  public int getLayer()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.layer;
  }
  
  public Matrix getMatrix()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_7, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.matrix;
  }
  
  public Date getModificationTime()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.modificationTime;
  }
  
  public long getTrackId()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.trackId;
  }
  
  public float getVolume()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.volume;
  }
  
  public double getWidth()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_8, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.width;
  }
  
  public boolean isEnabled()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_22, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    if ((getFlags() & 0x1) > 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isInMovie()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_23, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    if ((getFlags() & 0x2) > 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isInPoster()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_25, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    if ((getFlags() & 0x8) > 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isInPreview()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_24, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    if ((getFlags() & 0x4) > 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void setAlternateGroup(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_17, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.alternateGroup = paramInt;
  }
  
  public void setCreationTime(Date paramDate)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_12, this, this, paramDate);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.creationTime = paramDate;
    if (DateHelper.convert(paramDate) >= 4294967296L) {
      setVersion(1);
    }
  }
  
  public void setDuration(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_15, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.duration = paramLong;
    if (paramLong >= 4294967296L) {
      setFlags(1);
    }
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_26, this, this, Conversions.booleanObject(paramBoolean));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    if (paramBoolean) {
      setFlags(getFlags() | 0x1);
    }
    for (;;)
    {
      return;
      setFlags(getFlags() & 0xFFFFFFFE);
    }
  }
  
  public void setHeight(double paramDouble)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_21, this, this, Conversions.doubleObject(paramDouble));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.height = paramDouble;
  }
  
  public void setInMovie(boolean paramBoolean)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_27, this, this, Conversions.booleanObject(paramBoolean));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    if (paramBoolean) {
      setFlags(getFlags() | 0x2);
    }
    for (;;)
    {
      return;
      setFlags(getFlags() & 0xFFFFFFFD);
    }
  }
  
  public void setInPoster(boolean paramBoolean)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_29, this, this, Conversions.booleanObject(paramBoolean));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    if (paramBoolean) {
      setFlags(getFlags() | 0x8);
    }
    for (;;)
    {
      return;
      setFlags(getFlags() & 0xFFFFFFF7);
    }
  }
  
  public void setInPreview(boolean paramBoolean)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_28, this, this, Conversions.booleanObject(paramBoolean));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    if (paramBoolean) {
      setFlags(getFlags() | 0x4);
    }
    for (;;)
    {
      return;
      setFlags(getFlags() & 0xFFFFFFFB);
    }
  }
  
  public void setLayer(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_16, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.layer = paramInt;
  }
  
  public void setMatrix(Matrix paramMatrix)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_19, this, this, paramMatrix);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.matrix = paramMatrix;
  }
  
  public void setModificationTime(Date paramDate)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_13, this, this, paramDate);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.modificationTime = paramDate;
    if (DateHelper.convert(paramDate) >= 4294967296L) {
      setVersion(1);
    }
  }
  
  public void setTrackId(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_14, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.trackId = paramLong;
  }
  
  public void setVolume(float paramFloat)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_18, this, this, Conversions.floatObject(paramFloat));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.volume = paramFloat;
  }
  
  public void setWidth(double paramDouble)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_20, this, this, Conversions.doubleObject(paramDouble));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.width = paramDouble;
  }
  
  public String toString()
  {
    Object localObject = Factory.makeJP(ajc$tjp_11, this, this);
    RequiresParseDetailAspect.aspectOf().before((JoinPoint)localObject);
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("TrackHeaderBox[");
    ((StringBuilder)localObject).append("creationTime=").append(getCreationTime());
    ((StringBuilder)localObject).append(";");
    ((StringBuilder)localObject).append("modificationTime=").append(getModificationTime());
    ((StringBuilder)localObject).append(";");
    ((StringBuilder)localObject).append("trackId=").append(getTrackId());
    ((StringBuilder)localObject).append(";");
    ((StringBuilder)localObject).append("duration=").append(getDuration());
    ((StringBuilder)localObject).append(";");
    ((StringBuilder)localObject).append("layer=").append(getLayer());
    ((StringBuilder)localObject).append(";");
    ((StringBuilder)localObject).append("alternateGroup=").append(getAlternateGroup());
    ((StringBuilder)localObject).append(";");
    ((StringBuilder)localObject).append("volume=").append(getVolume());
    ((StringBuilder)localObject).append(";");
    ((StringBuilder)localObject).append("matrix=").append(this.matrix);
    ((StringBuilder)localObject).append(";");
    ((StringBuilder)localObject).append("width=").append(getWidth());
    ((StringBuilder)localObject).append(";");
    ((StringBuilder)localObject).append("height=").append(getHeight());
    ((StringBuilder)localObject).append("]");
    return ((StringBuilder)localObject).toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/TrackHeaderBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */