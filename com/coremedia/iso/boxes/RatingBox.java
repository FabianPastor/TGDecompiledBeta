package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class RatingBox
  extends AbstractFullBox
{
  public static final String TYPE = "rtng";
  private String language;
  private String ratingCriteria;
  private String ratingEntity;
  private String ratingInfo;
  
  static {}
  
  public RatingBox()
  {
    super("rtng");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.ratingEntity = IsoTypeReader.read4cc(paramByteBuffer);
    this.ratingCriteria = IsoTypeReader.read4cc(paramByteBuffer);
    this.language = IsoTypeReader.readIso639(paramByteBuffer);
    this.ratingInfo = IsoTypeReader.readString(paramByteBuffer);
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    paramByteBuffer.put(IsoFile.fourCCtoBytes(this.ratingEntity));
    paramByteBuffer.put(IsoFile.fourCCtoBytes(this.ratingCriteria));
    IsoTypeWriter.writeIso639(paramByteBuffer, this.language);
    paramByteBuffer.put(Utf8.convert(this.ratingInfo));
    paramByteBuffer.put((byte)0);
  }
  
  protected long getContentSize()
  {
    return Utf8.utf8StringLengthInBytes(this.ratingInfo) + 15;
  }
  
  public String getLanguage()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.language;
  }
  
  public String getRatingCriteria()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.ratingCriteria;
  }
  
  public String getRatingEntity()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.ratingEntity;
  }
  
  public String getRatingInfo()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_7, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.ratingInfo;
  }
  
  public void setLanguage(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.language = paramString;
  }
  
  public void setRatingCriteria(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.ratingCriteria = paramString;
  }
  
  public void setRatingEntity(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.ratingEntity = paramString;
  }
  
  public void setRatingInfo(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.ratingInfo = paramString;
  }
  
  public String toString()
  {
    Object localObject = Factory.makeJP(ajc$tjp_8, this, this);
    RequiresParseDetailAspect.aspectOf().before((JoinPoint)localObject);
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("RatingBox[language=").append(getLanguage());
    ((StringBuilder)localObject).append("ratingEntity=").append(getRatingEntity());
    ((StringBuilder)localObject).append(";ratingCriteria=").append(getRatingCriteria());
    ((StringBuilder)localObject).append(";language=").append(getLanguage());
    ((StringBuilder)localObject).append(";ratingInfo=").append(getRatingInfo());
    ((StringBuilder)localObject).append("]");
    return ((StringBuilder)localObject).toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/RatingBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */