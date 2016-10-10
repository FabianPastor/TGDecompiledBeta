package com.mp4parser.iso14496.part15;

import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class HevcConfigurationBox
  extends AbstractBox
{
  public static final String TYPE = "hvcC";
  private HevcDecoderConfigurationRecord hevcDecoderConfigurationRecord = new HevcDecoderConfigurationRecord();
  
  static {}
  
  public HevcConfigurationBox()
  {
    super("hvcC");
  }
  
  protected void _parseDetails(ByteBuffer paramByteBuffer)
  {
    this.hevcDecoderConfigurationRecord.parse(paramByteBuffer);
  }
  
  public boolean equals(Object paramObject)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this, paramObject);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    if (this == paramObject) {}
    do
    {
      return true;
      if ((paramObject == null) || (getClass() != paramObject.getClass())) {
        return false;
      }
      paramObject = (HevcConfigurationBox)paramObject;
      if (this.hevcDecoderConfigurationRecord == null) {
        break;
      }
    } while (this.hevcDecoderConfigurationRecord.equals(((HevcConfigurationBox)paramObject).hevcDecoderConfigurationRecord));
    for (;;)
    {
      return false;
      if (((HevcConfigurationBox)paramObject).hevcDecoderConfigurationRecord == null) {
        break;
      }
    }
  }
  
  public List<HevcDecoderConfigurationRecord.Array> getArrays()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_21, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.hevcDecoderConfigurationRecord.arrays;
  }
  
  public int getAvgFrameRate()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_16, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.hevcDecoderConfigurationRecord.avgFrameRate;
  }
  
  public int getBitDepthChromaMinus8()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_15, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.hevcDecoderConfigurationRecord.bitDepthChromaMinus8;
  }
  
  public int getBitDepthLumaMinus8()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_14, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.hevcDecoderConfigurationRecord.bitDepthLumaMinus8;
  }
  
  public int getChromaFormat()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_13, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.hevcDecoderConfigurationRecord.chromaFormat;
  }
  
  public int getConfigurationVersion()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.hevcDecoderConfigurationRecord.configurationVersion;
  }
  
  public int getConstantFrameRate()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_20, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.hevcDecoderConfigurationRecord.constantFrameRate;
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    this.hevcDecoderConfigurationRecord.write(paramByteBuffer);
  }
  
  protected long getContentSize()
  {
    return this.hevcDecoderConfigurationRecord.getSize();
  }
  
  public long getGeneral_constraint_indicator_flags()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_9, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.hevcDecoderConfigurationRecord.general_constraint_indicator_flags;
  }
  
  public int getGeneral_level_idc()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_10, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.hevcDecoderConfigurationRecord.general_level_idc;
  }
  
  public long getGeneral_profile_compatibility_flags()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_8, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.hevcDecoderConfigurationRecord.general_profile_compatibility_flags;
  }
  
  public int getGeneral_profile_idc()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_7, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.hevcDecoderConfigurationRecord.general_profile_idc;
  }
  
  public int getGeneral_profile_space()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.hevcDecoderConfigurationRecord.general_profile_space;
  }
  
  public HevcDecoderConfigurationRecord getHevcDecoderConfigurationRecord()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.hevcDecoderConfigurationRecord;
  }
  
  public int getLengthSizeMinusOne()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_18, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.hevcDecoderConfigurationRecord.lengthSizeMinusOne;
  }
  
  public int getMin_spatial_segmentation_idc()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_11, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.hevcDecoderConfigurationRecord.min_spatial_segmentation_idc;
  }
  
  public int getNumTemporalLayers()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_17, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.hevcDecoderConfigurationRecord.numTemporalLayers;
  }
  
  public int getParallelismType()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_12, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.hevcDecoderConfigurationRecord.parallelismType;
  }
  
  public int hashCode()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    if (this.hevcDecoderConfigurationRecord != null) {
      return this.hevcDecoderConfigurationRecord.hashCode();
    }
    return 0;
  }
  
  public boolean isGeneral_tier_flag()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.hevcDecoderConfigurationRecord.general_tier_flag;
  }
  
  public boolean isTemporalIdNested()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_19, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.hevcDecoderConfigurationRecord.temporalIdNested;
  }
  
  public void setHevcDecoderConfigurationRecord(HevcDecoderConfigurationRecord paramHevcDecoderConfigurationRecord)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, paramHevcDecoderConfigurationRecord);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.hevcDecoderConfigurationRecord = paramHevcDecoderConfigurationRecord;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/mp4parser/iso14496/part15/HevcConfigurationBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */