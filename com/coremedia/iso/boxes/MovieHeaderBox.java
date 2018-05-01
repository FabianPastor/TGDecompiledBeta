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

public class MovieHeaderBox
  extends AbstractFullBox
{
  public static final String TYPE = "mvhd";
  private Date creationTime;
  private int currentTime;
  private long duration;
  private Matrix matrix = Matrix.ROTATE_0;
  private Date modificationTime;
  private long nextTrackId;
  private int posterTime;
  private int previewDuration;
  private int previewTime;
  private double rate = 1.0D;
  private int selectionDuration;
  private int selectionTime;
  private long timescale;
  private float volume = 1.0F;
  
  static {}
  
  public MovieHeaderBox()
  {
    super("mvhd");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    if (getVersion() == 1)
    {
      this.creationTime = DateHelper.convert(IsoTypeReader.readUInt64(paramByteBuffer));
      this.modificationTime = DateHelper.convert(IsoTypeReader.readUInt64(paramByteBuffer));
      this.timescale = IsoTypeReader.readUInt32(paramByteBuffer);
    }
    for (this.duration = IsoTypeReader.readUInt64(paramByteBuffer);; this.duration = IsoTypeReader.readUInt32(paramByteBuffer))
    {
      this.rate = IsoTypeReader.readFixedPoint1616(paramByteBuffer);
      this.volume = IsoTypeReader.readFixedPoint88(paramByteBuffer);
      IsoTypeReader.readUInt16(paramByteBuffer);
      IsoTypeReader.readUInt32(paramByteBuffer);
      IsoTypeReader.readUInt32(paramByteBuffer);
      this.matrix = Matrix.fromByteBuffer(paramByteBuffer);
      this.previewTime = paramByteBuffer.getInt();
      this.previewDuration = paramByteBuffer.getInt();
      this.posterTime = paramByteBuffer.getInt();
      this.selectionTime = paramByteBuffer.getInt();
      this.selectionDuration = paramByteBuffer.getInt();
      this.currentTime = paramByteBuffer.getInt();
      this.nextTrackId = IsoTypeReader.readUInt32(paramByteBuffer);
      return;
      this.creationTime = DateHelper.convert(IsoTypeReader.readUInt32(paramByteBuffer));
      this.modificationTime = DateHelper.convert(IsoTypeReader.readUInt32(paramByteBuffer));
      this.timescale = IsoTypeReader.readUInt32(paramByteBuffer);
    }
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    if (getVersion() == 1)
    {
      IsoTypeWriter.writeUInt64(paramByteBuffer, DateHelper.convert(this.creationTime));
      IsoTypeWriter.writeUInt64(paramByteBuffer, DateHelper.convert(this.modificationTime));
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.timescale);
      IsoTypeWriter.writeUInt64(paramByteBuffer, this.duration);
    }
    for (;;)
    {
      IsoTypeWriter.writeFixedPoint1616(paramByteBuffer, this.rate);
      IsoTypeWriter.writeFixedPoint88(paramByteBuffer, this.volume);
      IsoTypeWriter.writeUInt16(paramByteBuffer, 0);
      IsoTypeWriter.writeUInt32(paramByteBuffer, 0L);
      IsoTypeWriter.writeUInt32(paramByteBuffer, 0L);
      this.matrix.getContent(paramByteBuffer);
      paramByteBuffer.putInt(this.previewTime);
      paramByteBuffer.putInt(this.previewDuration);
      paramByteBuffer.putInt(this.posterTime);
      paramByteBuffer.putInt(this.selectionTime);
      paramByteBuffer.putInt(this.selectionDuration);
      paramByteBuffer.putInt(this.currentTime);
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.nextTrackId);
      return;
      IsoTypeWriter.writeUInt32(paramByteBuffer, DateHelper.convert(this.creationTime));
      IsoTypeWriter.writeUInt32(paramByteBuffer, DateHelper.convert(this.modificationTime));
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.timescale);
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.duration);
    }
  }
  
  protected long getContentSize()
  {
    if (getVersion() == 1) {}
    for (long l = 4L + 28L;; l = 4L + 16L) {
      return l + 80L;
    }
  }
  
  public Date getCreationTime()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.creationTime;
  }
  
  public int getCurrentTime()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_27, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.currentTime;
  }
  
  public long getDuration()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.duration;
  }
  
  public Matrix getMatrix()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.matrix;
  }
  
  public Date getModificationTime()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.modificationTime;
  }
  
  public long getNextTrackId()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_7, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.nextTrackId;
  }
  
  public int getPosterTime()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_21, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.posterTime;
  }
  
  public int getPreviewDuration()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_19, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.previewDuration;
  }
  
  public int getPreviewTime()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_17, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.previewTime;
  }
  
  public double getRate()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.rate;
  }
  
  public int getSelectionDuration()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_25, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.selectionDuration;
  }
  
  public int getSelectionTime()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_23, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.selectionTime;
  }
  
  public long getTimescale()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.timescale;
  }
  
  public float getVolume()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.volume;
  }
  
  public void setCreationTime(Date paramDate)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_9, this, this, paramDate);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.creationTime = paramDate;
    if (DateHelper.convert(paramDate) >= 4294967296L) {
      setVersion(1);
    }
  }
  
  public void setCurrentTime(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_28, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.currentTime = paramInt;
  }
  
  public void setDuration(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_12, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.duration = paramLong;
    if (paramLong >= 4294967296L) {
      setVersion(1);
    }
  }
  
  public void setMatrix(Matrix paramMatrix)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_15, this, this, paramMatrix);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.matrix = paramMatrix;
  }
  
  public void setModificationTime(Date paramDate)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_10, this, this, paramDate);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.modificationTime = paramDate;
    if (DateHelper.convert(paramDate) >= 4294967296L) {
      setVersion(1);
    }
  }
  
  public void setNextTrackId(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_16, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.nextTrackId = paramLong;
  }
  
  public void setPosterTime(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_22, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.posterTime = paramInt;
  }
  
  public void setPreviewDuration(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_20, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.previewDuration = paramInt;
  }
  
  public void setPreviewTime(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_18, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.previewTime = paramInt;
  }
  
  public void setRate(double paramDouble)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_13, this, this, Conversions.doubleObject(paramDouble));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.rate = paramDouble;
  }
  
  public void setSelectionDuration(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_26, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.selectionDuration = paramInt;
  }
  
  public void setSelectionTime(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_24, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.selectionTime = paramInt;
  }
  
  public void setTimescale(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_11, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.timescale = paramLong;
  }
  
  public void setVolume(float paramFloat)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_14, this, this, Conversions.floatObject(paramFloat));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.volume = paramFloat;
  }
  
  public String toString()
  {
    Object localObject = Factory.makeJP(ajc$tjp_8, this, this);
    RequiresParseDetailAspect.aspectOf().before((JoinPoint)localObject);
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("MovieHeaderBox[");
    ((StringBuilder)localObject).append("creationTime=").append(getCreationTime());
    ((StringBuilder)localObject).append(";");
    ((StringBuilder)localObject).append("modificationTime=").append(getModificationTime());
    ((StringBuilder)localObject).append(";");
    ((StringBuilder)localObject).append("timescale=").append(getTimescale());
    ((StringBuilder)localObject).append(";");
    ((StringBuilder)localObject).append("duration=").append(getDuration());
    ((StringBuilder)localObject).append(";");
    ((StringBuilder)localObject).append("rate=").append(getRate());
    ((StringBuilder)localObject).append(";");
    ((StringBuilder)localObject).append("volume=").append(getVolume());
    ((StringBuilder)localObject).append(";");
    ((StringBuilder)localObject).append("matrix=").append(this.matrix);
    ((StringBuilder)localObject).append(";");
    ((StringBuilder)localObject).append("nextTrackId=").append(getNextTrackId());
    ((StringBuilder)localObject).append("]");
    return ((StringBuilder)localObject).toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/MovieHeaderBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */