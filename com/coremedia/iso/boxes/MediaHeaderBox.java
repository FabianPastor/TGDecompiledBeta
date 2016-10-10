package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.util.DateHelper;
import java.nio.ByteBuffer;
import java.util.Date;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class MediaHeaderBox
  extends AbstractFullBox
{
  public static final String TYPE = "mdhd";
  private Date creationTime = new Date();
  private long duration;
  private String language = "eng";
  private Date modificationTime = new Date();
  private long timescale;
  
  static {}
  
  public MediaHeaderBox()
  {
    super("mdhd");
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
      this.language = IsoTypeReader.readIso639(paramByteBuffer);
      IsoTypeReader.readUInt16(paramByteBuffer);
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
      IsoTypeWriter.writeIso639(paramByteBuffer, this.language);
      IsoTypeWriter.writeUInt16(paramByteBuffer, 0);
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
      return l + 2L + 2L;
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
  
  public String getLanguage()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.language;
  }
  
  public Date getModificationTime()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.modificationTime;
  }
  
  public long getTimescale()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.timescale;
  }
  
  public void setCreationTime(Date paramDate)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this, paramDate);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.creationTime = paramDate;
  }
  
  public void setDuration(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_8, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.duration = paramLong;
  }
  
  public void setLanguage(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_9, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.language = paramString;
  }
  
  public void setModificationTime(Date paramDate)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this, paramDate);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.modificationTime = paramDate;
  }
  
  public void setTimescale(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_7, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.timescale = paramLong;
  }
  
  public String toString()
  {
    Object localObject = Factory.makeJP(ajc$tjp_10, this, this);
    RequiresParseDetailAspect.aspectOf().before((JoinPoint)localObject);
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("MediaHeaderBox[");
    ((StringBuilder)localObject).append("creationTime=").append(getCreationTime());
    ((StringBuilder)localObject).append(";");
    ((StringBuilder)localObject).append("modificationTime=").append(getModificationTime());
    ((StringBuilder)localObject).append(";");
    ((StringBuilder)localObject).append("timescale=").append(getTimescale());
    ((StringBuilder)localObject).append(";");
    ((StringBuilder)localObject).append("duration=").append(getDuration());
    ((StringBuilder)localObject).append(";");
    ((StringBuilder)localObject).append("language=").append(getLanguage());
    ((StringBuilder)localObject).append("]");
    return ((StringBuilder)localObject).toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/MediaHeaderBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */