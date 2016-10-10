package com.googlecode.mp4parser.boxes;

import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitWriterBuffer;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class AC3SpecificBox
  extends AbstractBox
{
  public static final String TYPE = "dac3";
  int acmod;
  int bitRateCode;
  int bsid;
  int bsmod;
  int fscod;
  int lfeon;
  int reserved;
  
  static {}
  
  public AC3SpecificBox()
  {
    super("dac3");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer = new BitReaderBuffer(paramByteBuffer);
    this.fscod = paramByteBuffer.readBits(2);
    this.bsid = paramByteBuffer.readBits(5);
    this.bsmod = paramByteBuffer.readBits(3);
    this.acmod = paramByteBuffer.readBits(3);
    this.lfeon = paramByteBuffer.readBits(1);
    this.bitRateCode = paramByteBuffer.readBits(5);
    this.reserved = paramByteBuffer.readBits(5);
  }
  
  public int getAcmod()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.acmod;
  }
  
  public int getBitRateCode()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_10, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.bitRateCode;
  }
  
  public int getBsid()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.bsid;
  }
  
  public int getBsmod()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.bsmod;
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer = new BitWriterBuffer(paramByteBuffer);
    paramByteBuffer.writeBits(this.fscod, 2);
    paramByteBuffer.writeBits(this.bsid, 5);
    paramByteBuffer.writeBits(this.bsmod, 3);
    paramByteBuffer.writeBits(this.acmod, 3);
    paramByteBuffer.writeBits(this.lfeon, 1);
    paramByteBuffer.writeBits(this.bitRateCode, 5);
    paramByteBuffer.writeBits(this.reserved, 5);
  }
  
  protected long getContentSize()
  {
    return 3L;
  }
  
  public int getFscod()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.fscod;
  }
  
  public int getLfeon()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_8, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.lfeon;
  }
  
  public int getReserved()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_12, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.reserved;
  }
  
  public void setAcmod(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_7, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.acmod = paramInt;
  }
  
  public void setBitRateCode(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_11, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.bitRateCode = paramInt;
  }
  
  public void setBsid(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.bsid = paramInt;
  }
  
  public void setBsmod(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.bsmod = paramInt;
  }
  
  public void setFscod(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.fscod = paramInt;
  }
  
  public void setLfeon(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_9, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.lfeon = paramInt;
  }
  
  public void setReserved(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_13, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.reserved = paramInt;
  }
  
  public String toString()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_14, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return "AC3SpecificBox{fscod=" + this.fscod + ", bsid=" + this.bsid + ", bsmod=" + this.bsmod + ", acmod=" + this.acmod + ", lfeon=" + this.lfeon + ", bitRateCode=" + this.bitRateCode + ", reserved=" + this.reserved + '}';
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/AC3SpecificBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */