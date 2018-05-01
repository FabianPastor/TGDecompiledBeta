package com.coremedia.iso.boxes.fragment;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class TrackFragmentBaseMediaDecodeTimeBox
  extends AbstractFullBox
{
  public static final String TYPE = "tfdt";
  private long baseMediaDecodeTime;
  
  static {}
  
  public TrackFragmentBaseMediaDecodeTimeBox()
  {
    super("tfdt");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    if (getVersion() == 1) {}
    for (this.baseMediaDecodeTime = IsoTypeReader.readUInt64(paramByteBuffer);; this.baseMediaDecodeTime = IsoTypeReader.readUInt32(paramByteBuffer)) {
      return;
    }
  }
  
  public long getBaseMediaDecodeTime()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.baseMediaDecodeTime;
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    if (getVersion() == 1) {
      IsoTypeWriter.writeUInt64(paramByteBuffer, this.baseMediaDecodeTime);
    }
    for (;;)
    {
      return;
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.baseMediaDecodeTime);
    }
  }
  
  protected long getContentSize()
  {
    if (getVersion() == 0) {}
    for (int i = 8;; i = 12) {
      return i;
    }
  }
  
  public void setBaseMediaDecodeTime(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.baseMediaDecodeTime = paramLong;
  }
  
  public String toString()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return "TrackFragmentBaseMediaDecodeTimeBox{baseMediaDecodeTime=" + this.baseMediaDecodeTime + '}';
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/fragment/TrackFragmentBaseMediaDecodeTimeBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */