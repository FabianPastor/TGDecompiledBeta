package com.googlecode.mp4parser.boxes.apple;

import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class AppleDiskNumberBox
  extends AppleDataBox
{
  int a;
  short b;
  
  static {}
  
  public AppleDiskNumberBox()
  {
    super("disk", 0);
  }
  
  public int getA()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.a;
  }
  
  public short getB()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.b;
  }
  
  protected int getDataLength()
  {
    return 6;
  }
  
  protected void parseData(ByteBuffer paramByteBuffer)
  {
    this.a = paramByteBuffer.getInt();
    this.b = paramByteBuffer.getShort();
  }
  
  public void setA(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.a = paramInt;
  }
  
  public void setB(short paramShort)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, Conversions.shortObject(paramShort));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.b = paramShort;
  }
  
  protected byte[] writeData()
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(6);
    localByteBuffer.putInt(this.a);
    localByteBuffer.putShort(this.b);
    return localByteBuffer.array();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/apple/AppleDiskNumberBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */