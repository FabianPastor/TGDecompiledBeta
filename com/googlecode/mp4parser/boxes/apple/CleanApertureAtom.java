package com.googlecode.mp4parser.boxes.apple;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class CleanApertureAtom
  extends AbstractFullBox
{
  public static final String TYPE = "clef";
  double height;
  double width;
  
  static {}
  
  public CleanApertureAtom()
  {
    super("clef");
  }
  
  protected void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.width = IsoTypeReader.readFixedPoint1616(paramByteBuffer);
    this.height = IsoTypeReader.readFixedPoint1616(paramByteBuffer);
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeFixedPoint1616(paramByteBuffer, this.width);
    IsoTypeWriter.writeFixedPoint1616(paramByteBuffer, this.height);
  }
  
  protected long getContentSize()
  {
    return 12L;
  }
  
  public double getHeight()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.height;
  }
  
  public double getWidth()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.width;
  }
  
  public void setHeight(double paramDouble)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, Conversions.doubleObject(paramDouble));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.height = paramDouble;
  }
  
  public void setWidth(double paramDouble)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, Conversions.doubleObject(paramDouble));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.width = paramDouble;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/apple/CleanApertureAtom.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */