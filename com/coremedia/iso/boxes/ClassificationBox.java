package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class ClassificationBox
  extends AbstractFullBox
{
  public static final String TYPE = "clsf";
  private String classificationEntity;
  private String classificationInfo;
  private int classificationTableIndex;
  private String language;
  
  static {}
  
  public ClassificationBox()
  {
    super("clsf");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    byte[] arrayOfByte = new byte[4];
    paramByteBuffer.get(arrayOfByte);
    this.classificationEntity = IsoFile.bytesToFourCC(arrayOfByte);
    this.classificationTableIndex = IsoTypeReader.readUInt16(paramByteBuffer);
    this.language = IsoTypeReader.readIso639(paramByteBuffer);
    this.classificationInfo = IsoTypeReader.readString(paramByteBuffer);
  }
  
  public String getClassificationEntity()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.classificationEntity;
  }
  
  public String getClassificationInfo()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.classificationInfo;
  }
  
  public int getClassificationTableIndex()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.classificationTableIndex;
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer.put(IsoFile.fourCCtoBytes(this.classificationEntity));
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.classificationTableIndex);
    IsoTypeWriter.writeIso639(paramByteBuffer, this.language);
    paramByteBuffer.put(Utf8.convert(this.classificationInfo));
    paramByteBuffer.put((byte)0);
  }
  
  protected long getContentSize()
  {
    return Utf8.utf8StringLengthInBytes(this.classificationInfo) + 8 + 1;
  }
  
  public String getLanguage()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.language;
  }
  
  public void setClassificationEntity(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.classificationEntity = paramString;
  }
  
  public void setClassificationInfo(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_7, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.classificationInfo = paramString;
  }
  
  public void setClassificationTableIndex(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.classificationTableIndex = paramInt;
  }
  
  public void setLanguage(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.language = paramString;
  }
  
  public String toString()
  {
    Object localObject = Factory.makeJP(ajc$tjp_8, this, this);
    RequiresParseDetailAspect.aspectOf().before((JoinPoint)localObject);
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("ClassificationBox[language=").append(getLanguage());
    ((StringBuilder)localObject).append("classificationEntity=").append(getClassificationEntity());
    ((StringBuilder)localObject).append(";classificationTableIndex=").append(getClassificationTableIndex());
    ((StringBuilder)localObject).append(";language=").append(getLanguage());
    ((StringBuilder)localObject).append(";classificationInfo=").append(getClassificationInfo());
    ((StringBuilder)localObject).append("]");
    return ((StringBuilder)localObject).toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/ClassificationBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */