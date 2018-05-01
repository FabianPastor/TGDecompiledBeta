package com.googlecode.mp4parser.boxes.apple;

import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class TrackLoadSettingsAtom
  extends AbstractBox
{
  public static final String TYPE = "load";
  int defaultHints;
  int preloadDuration;
  int preloadFlags;
  int preloadStartTime;
  
  static {}
  
  public TrackLoadSettingsAtom()
  {
    super("load");
  }
  
  protected void _parseDetails(ByteBuffer paramByteBuffer)
  {
    this.preloadStartTime = paramByteBuffer.getInt();
    this.preloadDuration = paramByteBuffer.getInt();
    this.preloadFlags = paramByteBuffer.getInt();
    this.defaultHints = paramByteBuffer.getInt();
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer.putInt(this.preloadStartTime);
    paramByteBuffer.putInt(this.preloadDuration);
    paramByteBuffer.putInt(this.preloadFlags);
    paramByteBuffer.putInt(this.defaultHints);
  }
  
  protected long getContentSize()
  {
    return 16L;
  }
  
  public int getDefaultHints()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.defaultHints;
  }
  
  public int getPreloadDuration()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.preloadDuration;
  }
  
  public int getPreloadFlags()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.preloadFlags;
  }
  
  public int getPreloadStartTime()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.preloadStartTime;
  }
  
  public void setDefaultHints(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_7, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.defaultHints = paramInt;
  }
  
  public void setPreloadDuration(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.preloadDuration = paramInt;
  }
  
  public void setPreloadFlags(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.preloadFlags = paramInt;
  }
  
  public void setPreloadStartTime(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.preloadStartTime = paramInt;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/apple/TrackLoadSettingsAtom.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */