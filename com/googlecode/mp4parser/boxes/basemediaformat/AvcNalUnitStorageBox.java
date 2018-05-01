package com.googlecode.mp4parser.boxes.basemediaformat;

import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.mp4parser.iso14496.part15.AvcConfigurationBox;
import com.mp4parser.iso14496.part15.AvcDecoderConfigurationRecord;
import java.nio.ByteBuffer;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class AvcNalUnitStorageBox
  extends AbstractBox
{
  public static final String TYPE = "avcn";
  AvcDecoderConfigurationRecord avcDecoderConfigurationRecord;
  
  static {}
  
  public AvcNalUnitStorageBox()
  {
    super("avcn");
  }
  
  public AvcNalUnitStorageBox(AvcConfigurationBox paramAvcConfigurationBox)
  {
    super("avcn");
    this.avcDecoderConfigurationRecord = paramAvcConfigurationBox.getavcDecoderConfigurationRecord();
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    this.avcDecoderConfigurationRecord = new AvcDecoderConfigurationRecord(paramByteBuffer);
  }
  
  public AvcDecoderConfigurationRecord getAvcDecoderConfigurationRecord()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.avcDecoderConfigurationRecord;
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    this.avcDecoderConfigurationRecord.getContent(paramByteBuffer);
  }
  
  protected long getContentSize()
  {
    return this.avcDecoderConfigurationRecord.getContentSize();
  }
  
  public int getLengthSizeMinusOne()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.avcDecoderConfigurationRecord.lengthSizeMinusOne;
  }
  
  public String[] getPPS()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.avcDecoderConfigurationRecord.getPPS();
  }
  
  public List<String> getPictureParameterSetsAsStrings()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.avcDecoderConfigurationRecord.getPictureParameterSetsAsStrings();
  }
  
  public String[] getSPS()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.avcDecoderConfigurationRecord.getSPS();
  }
  
  public List<String> getSequenceParameterSetExtsAsStrings()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.avcDecoderConfigurationRecord.getSequenceParameterSetExtsAsStrings();
  }
  
  public List<String> getSequenceParameterSetsAsStrings()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.avcDecoderConfigurationRecord.getSequenceParameterSetsAsStrings();
  }
  
  public String toString()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_7, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return "AvcNalUnitStorageBox{SPS=" + this.avcDecoderConfigurationRecord.getSequenceParameterSetsAsStrings() + ",PPS=" + this.avcDecoderConfigurationRecord.getPictureParameterSetsAsStrings() + ",lengthSize=" + (this.avcDecoderConfigurationRecord.lengthSizeMinusOne + 1) + '}';
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/basemediaformat/AvcNalUnitStorageBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */