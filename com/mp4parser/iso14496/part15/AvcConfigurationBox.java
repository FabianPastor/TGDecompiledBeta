package com.mp4parser.iso14496.part15;

import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public final class AvcConfigurationBox
  extends AbstractBox
{
  public AvcDecoderConfigurationRecord avcDecoderConfigurationRecord = new AvcDecoderConfigurationRecord();
  
  static {}
  
  public AvcConfigurationBox()
  {
    super("avcC");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    this.avcDecoderConfigurationRecord = new AvcDecoderConfigurationRecord(paramByteBuffer);
  }
  
  public void getContent(ByteBuffer paramByteBuffer)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_25, this, this, paramByteBuffer);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.avcDecoderConfigurationRecord.getContent(paramByteBuffer);
  }
  
  public long getContentSize()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_24, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.avcDecoderConfigurationRecord.getContentSize();
  }
  
  public void setAvcLevelIndication(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_10, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.avcDecoderConfigurationRecord.avcLevelIndication = paramInt;
  }
  
  public void setAvcProfileIndication(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_8, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.avcDecoderConfigurationRecord.avcProfileIndication = paramInt;
  }
  
  public void setBitDepthChromaMinus8(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_19, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.avcDecoderConfigurationRecord.bitDepthChromaMinus8 = paramInt;
  }
  
  public void setBitDepthLumaMinus8(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_17, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.avcDecoderConfigurationRecord.bitDepthLumaMinus8 = paramInt;
  }
  
  public void setChromaFormat(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_15, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.avcDecoderConfigurationRecord.chromaFormat = paramInt;
  }
  
  public void setConfigurationVersion(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_7, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.avcDecoderConfigurationRecord.configurationVersion = paramInt;
  }
  
  public void setLengthSizeMinusOne(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_11, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.avcDecoderConfigurationRecord.lengthSizeMinusOne = paramInt;
  }
  
  public void setPictureParameterSets(List<byte[]> paramList)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_13, this, this, paramList);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.avcDecoderConfigurationRecord.pictureParameterSets = paramList;
  }
  
  public void setProfileCompatibility(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_9, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.avcDecoderConfigurationRecord.profileCompatibility = paramInt;
  }
  
  public void setSequenceParameterSets(List<byte[]> paramList)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_12, this, this, paramList);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.avcDecoderConfigurationRecord.sequenceParameterSets = paramList;
  }
  
  public String toString()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_29, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return "AvcConfigurationBox{avcDecoderConfigurationRecord=" + this.avcDecoderConfigurationRecord + '}';
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/mp4parser/iso14496/part15/AvcConfigurationBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */