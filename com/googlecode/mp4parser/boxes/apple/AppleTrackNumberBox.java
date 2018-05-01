package com.googlecode.mp4parser.boxes.apple;

import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class AppleTrackNumberBox
  extends AppleDataBox
{
  int a;
  int b;
  
  static {}
  
  public AppleTrackNumberBox()
  {
    super("trkn", 0);
  }
  
  public int getA()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.a;
  }
  
  public int getB()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.b;
  }
  
  protected int getDataLength()
  {
    return 8;
  }
  
  protected void parseData(ByteBuffer paramByteBuffer)
  {
    this.a = paramByteBuffer.getInt();
    this.b = paramByteBuffer.getInt();
  }
  
  public void setA(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.a = paramInt;
  }
  
  public void setB(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.b = paramInt;
  }
  
  protected byte[] writeData()
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(8);
    localByteBuffer.putInt(this.a);
    localByteBuffer.putInt(this.b);
    return localByteBuffer.array();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/apple/AppleTrackNumberBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */