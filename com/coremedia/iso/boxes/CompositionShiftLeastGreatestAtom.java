package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class CompositionShiftLeastGreatestAtom
  extends AbstractFullBox
{
  public static final String TYPE = "cslg";
  int compositionOffsetToDisplayOffsetShift;
  int displayEndTime;
  int displayStartTime;
  int greatestDisplayOffset;
  int leastDisplayOffset;
  
  static {}
  
  public CompositionShiftLeastGreatestAtom()
  {
    super("cslg");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.compositionOffsetToDisplayOffsetShift = paramByteBuffer.getInt();
    this.leastDisplayOffset = paramByteBuffer.getInt();
    this.greatestDisplayOffset = paramByteBuffer.getInt();
    this.displayStartTime = paramByteBuffer.getInt();
    this.displayEndTime = paramByteBuffer.getInt();
  }
  
  public int getCompositionOffsetToDisplayOffsetShift()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.compositionOffsetToDisplayOffsetShift;
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    paramByteBuffer.putInt(this.compositionOffsetToDisplayOffsetShift);
    paramByteBuffer.putInt(this.leastDisplayOffset);
    paramByteBuffer.putInt(this.greatestDisplayOffset);
    paramByteBuffer.putInt(this.displayStartTime);
    paramByteBuffer.putInt(this.displayEndTime);
  }
  
  protected long getContentSize()
  {
    return 24L;
  }
  
  public int getDisplayEndTime()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_8, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.displayEndTime;
  }
  
  public int getDisplayStartTime()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.displayStartTime;
  }
  
  public int getGreatestDisplayOffset()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.greatestDisplayOffset;
  }
  
  public int getLeastDisplayOffset()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.leastDisplayOffset;
  }
  
  public void setCompositionOffsetToDisplayOffsetShift(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.compositionOffsetToDisplayOffsetShift = paramInt;
  }
  
  public void setDisplayEndTime(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_9, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.displayEndTime = paramInt;
  }
  
  public void setDisplayStartTime(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_7, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.displayStartTime = paramInt;
  }
  
  public void setGreatestDisplayOffset(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.greatestDisplayOffset = paramInt;
  }
  
  public void setLeastDisplayOffset(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.leastDisplayOffset = paramInt;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/CompositionShiftLeastGreatestAtom.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */