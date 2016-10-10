package com.googlecode.mp4parser.boxes.apple;

import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class PixelAspectRationAtom
  extends AbstractBox
{
  public static final String TYPE = "pasp";
  private int hSpacing;
  private int vSpacing;
  
  static {}
  
  public PixelAspectRationAtom()
  {
    super("pasp");
  }
  
  protected void _parseDetails(ByteBuffer paramByteBuffer)
  {
    this.hSpacing = paramByteBuffer.getInt();
    this.vSpacing = paramByteBuffer.getInt();
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer.putInt(this.hSpacing);
    paramByteBuffer.putInt(this.vSpacing);
  }
  
  protected long getContentSize()
  {
    return 8L;
  }
  
  public int gethSpacing()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.hSpacing;
  }
  
  public int getvSpacing()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.vSpacing;
  }
  
  public void sethSpacing(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.hSpacing = paramInt;
  }
  
  public void setvSpacing(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.vSpacing = paramInt;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/apple/PixelAspectRationAtom.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */