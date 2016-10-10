package com.googlecode.mp4parser.boxes.apple;

import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class GenericMediaHeaderTextAtom
  extends AbstractBox
{
  public static final String TYPE = "text";
  int unknown_1 = 65536;
  int unknown_2;
  int unknown_3;
  int unknown_4;
  int unknown_5 = 65536;
  int unknown_6;
  int unknown_7;
  int unknown_8;
  int unknown_9 = 1073741824;
  
  static {}
  
  public GenericMediaHeaderTextAtom()
  {
    super("text");
  }
  
  protected void _parseDetails(ByteBuffer paramByteBuffer)
  {
    this.unknown_1 = paramByteBuffer.getInt();
    this.unknown_2 = paramByteBuffer.getInt();
    this.unknown_3 = paramByteBuffer.getInt();
    this.unknown_4 = paramByteBuffer.getInt();
    this.unknown_5 = paramByteBuffer.getInt();
    this.unknown_6 = paramByteBuffer.getInt();
    this.unknown_7 = paramByteBuffer.getInt();
    this.unknown_8 = paramByteBuffer.getInt();
    this.unknown_9 = paramByteBuffer.getInt();
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer.putInt(this.unknown_1);
    paramByteBuffer.putInt(this.unknown_2);
    paramByteBuffer.putInt(this.unknown_3);
    paramByteBuffer.putInt(this.unknown_4);
    paramByteBuffer.putInt(this.unknown_5);
    paramByteBuffer.putInt(this.unknown_6);
    paramByteBuffer.putInt(this.unknown_7);
    paramByteBuffer.putInt(this.unknown_8);
    paramByteBuffer.putInt(this.unknown_9);
  }
  
  protected long getContentSize()
  {
    return 36L;
  }
  
  public int getUnknown_1()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.unknown_1;
  }
  
  public int getUnknown_2()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.unknown_2;
  }
  
  public int getUnknown_3()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.unknown_3;
  }
  
  public int getUnknown_4()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.unknown_4;
  }
  
  public int getUnknown_5()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_8, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.unknown_5;
  }
  
  public int getUnknown_6()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_10, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.unknown_6;
  }
  
  public int getUnknown_7()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_12, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.unknown_7;
  }
  
  public int getUnknown_8()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_14, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.unknown_8;
  }
  
  public int getUnknown_9()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_16, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.unknown_9;
  }
  
  public void setUnknown_1(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.unknown_1 = paramInt;
  }
  
  public void setUnknown_2(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.unknown_2 = paramInt;
  }
  
  public void setUnknown_3(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.unknown_3 = paramInt;
  }
  
  public void setUnknown_4(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_7, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.unknown_4 = paramInt;
  }
  
  public void setUnknown_5(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_9, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.unknown_5 = paramInt;
  }
  
  public void setUnknown_6(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_11, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.unknown_6 = paramInt;
  }
  
  public void setUnknown_7(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_13, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.unknown_7 = paramInt;
  }
  
  public void setUnknown_8(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_15, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.unknown_8 = paramInt;
  }
  
  public void setUnknown_9(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_17, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.unknown_9 = paramInt;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/apple/GenericMediaHeaderTextAtom.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */