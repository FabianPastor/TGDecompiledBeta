package com.googlecode.mp4parser.boxes.apple;

import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class AppleCoverBox
  extends AppleDataBox
{
  private static final int IMAGE_TYPE_JPG = 13;
  private static final int IMAGE_TYPE_PNG = 14;
  private byte[] data;
  
  static {}
  
  public AppleCoverBox()
  {
    super("covr", 1);
  }
  
  private void setImageData(byte[] paramArrayOfByte, int paramInt)
  {
    this.data = paramArrayOfByte;
    this.dataType = paramInt;
  }
  
  public byte[] getCoverData()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.data;
  }
  
  protected int getDataLength()
  {
    return this.data.length;
  }
  
  protected void parseData(ByteBuffer paramByteBuffer)
  {
    this.data = new byte[paramByteBuffer.limit()];
    paramByteBuffer.get(this.data);
  }
  
  public void setJpg(byte[] paramArrayOfByte)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, paramArrayOfByte);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    setImageData(paramArrayOfByte, 13);
  }
  
  public void setPng(byte[] paramArrayOfByte)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this, paramArrayOfByte);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    setImageData(paramArrayOfByte, 14);
  }
  
  protected byte[] writeData()
  {
    return this.data;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/apple/AppleCoverBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */