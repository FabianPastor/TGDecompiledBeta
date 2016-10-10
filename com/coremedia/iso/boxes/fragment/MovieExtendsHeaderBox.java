package com.coremedia.iso.boxes.fragment;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class MovieExtendsHeaderBox
  extends AbstractFullBox
{
  public static final String TYPE = "mehd";
  private long fragmentDuration;
  
  static {}
  
  public MovieExtendsHeaderBox()
  {
    super("mehd");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    if (getVersion() == 1) {}
    for (long l = IsoTypeReader.readUInt64(paramByteBuffer);; l = IsoTypeReader.readUInt32(paramByteBuffer))
    {
      this.fragmentDuration = l;
      return;
    }
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    if (getVersion() == 1)
    {
      IsoTypeWriter.writeUInt64(paramByteBuffer, this.fragmentDuration);
      return;
    }
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.fragmentDuration);
  }
  
  protected long getContentSize()
  {
    if (getVersion() == 1) {}
    for (int i = 12;; i = 8) {
      return i;
    }
  }
  
  public long getFragmentDuration()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.fragmentDuration;
  }
  
  public void setFragmentDuration(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.fragmentDuration = paramLong;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/fragment/MovieExtendsHeaderBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */