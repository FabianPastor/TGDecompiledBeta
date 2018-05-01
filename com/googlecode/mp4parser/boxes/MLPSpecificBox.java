package com.googlecode.mp4parser.boxes;

import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitWriterBuffer;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class MLPSpecificBox
  extends AbstractBox
{
  public static final String TYPE = "dmlp";
  int format_info;
  int peak_data_rate;
  int reserved;
  int reserved2;
  
  static {}
  
  public MLPSpecificBox()
  {
    super("dmlp");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer = new BitReaderBuffer(paramByteBuffer);
    this.format_info = paramByteBuffer.readBits(32);
    this.peak_data_rate = paramByteBuffer.readBits(15);
    this.reserved = paramByteBuffer.readBits(1);
    this.reserved2 = paramByteBuffer.readBits(32);
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer = new BitWriterBuffer(paramByteBuffer);
    paramByteBuffer.writeBits(this.format_info, 32);
    paramByteBuffer.writeBits(this.peak_data_rate, 15);
    paramByteBuffer.writeBits(this.reserved, 1);
    paramByteBuffer.writeBits(this.reserved2, 32);
  }
  
  protected long getContentSize()
  {
    return 10L;
  }
  
  public int getFormat_info()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.format_info;
  }
  
  public int getPeak_data_rate()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.peak_data_rate;
  }
  
  public int getReserved()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.reserved;
  }
  
  public int getReserved2()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.reserved2;
  }
  
  public void setFormat_info(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.format_info = paramInt;
  }
  
  public void setPeak_data_rate(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.peak_data_rate = paramInt;
  }
  
  public void setReserved(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.reserved = paramInt;
  }
  
  public void setReserved2(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_7, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.reserved2 = paramInt;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/MLPSpecificBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */