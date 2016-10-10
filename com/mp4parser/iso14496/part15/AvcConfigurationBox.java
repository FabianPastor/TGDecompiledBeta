package com.mp4parser.iso14496.part15;

import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public final class AvcConfigurationBox
  extends AbstractBox
{
  public static final String TYPE = "avcC";
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
  
  public int getAvcLevelIndication()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.avcDecoderConfigurationRecord.avcLevelIndication;
  }
  
  public int getAvcProfileIndication()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.avcDecoderConfigurationRecord.avcProfileIndication;
  }
  
  public int getBitDepthChromaMinus8()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_18, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.avcDecoderConfigurationRecord.bitDepthChromaMinus8;
  }
  
  public int getBitDepthLumaMinus8()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_16, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.avcDecoderConfigurationRecord.bitDepthLumaMinus8;
  }
  
  public int getChromaFormat()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_14, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.avcDecoderConfigurationRecord.chromaFormat;
  }
  
  public int getConfigurationVersion()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.avcDecoderConfigurationRecord.configurationVersion;
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
  
  public int getLengthSizeMinusOne()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.avcDecoderConfigurationRecord.lengthSizeMinusOne;
  }
  
  public String[] getPPS()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_27, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.avcDecoderConfigurationRecord.getPPS();
  }
  
  public List<byte[]> getPictureParameterSets()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return Collections.unmodifiableList(this.avcDecoderConfigurationRecord.pictureParameterSets);
  }
  
  public int getProfileCompatibility()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.avcDecoderConfigurationRecord.profileCompatibility;
  }
  
  public String[] getSPS()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_26, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.avcDecoderConfigurationRecord.getSPS();
  }
  
  public List<byte[]> getSequenceParameterSetExts()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_20, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.avcDecoderConfigurationRecord.sequenceParameterSetExts;
  }
  
  public List<byte[]> getSequenceParameterSets()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return Collections.unmodifiableList(this.avcDecoderConfigurationRecord.sequenceParameterSets);
  }
  
  public AvcDecoderConfigurationRecord getavcDecoderConfigurationRecord()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_28, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.avcDecoderConfigurationRecord;
  }
  
  public boolean hasExts()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_22, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.avcDecoderConfigurationRecord.hasExts;
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
  
  public void setHasExts(boolean paramBoolean)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_23, this, this, Conversions.booleanObject(paramBoolean));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.avcDecoderConfigurationRecord.hasExts = paramBoolean;
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
  
  public void setSequenceParameterSetExts(List<byte[]> paramList)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_21, this, this, paramList);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.avcDecoderConfigurationRecord.sequenceParameterSetExts = paramList;
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