package com.googlecode.mp4parser.boxes.piff;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class TfxdBox
  extends AbstractFullBox
{
  public long fragmentAbsoluteDuration;
  public long fragmentAbsoluteTime;
  
  static {}
  
  public TfxdBox()
  {
    super("uuid");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    if (getVersion() == 1)
    {
      this.fragmentAbsoluteTime = IsoTypeReader.readUInt64(paramByteBuffer);
      this.fragmentAbsoluteDuration = IsoTypeReader.readUInt64(paramByteBuffer);
      return;
    }
    this.fragmentAbsoluteTime = IsoTypeReader.readUInt32(paramByteBuffer);
    this.fragmentAbsoluteDuration = IsoTypeReader.readUInt32(paramByteBuffer);
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    if (getVersion() == 1)
    {
      IsoTypeWriter.writeUInt64(paramByteBuffer, this.fragmentAbsoluteTime);
      IsoTypeWriter.writeUInt64(paramByteBuffer, this.fragmentAbsoluteDuration);
      return;
    }
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.fragmentAbsoluteTime);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.fragmentAbsoluteDuration);
  }
  
  protected long getContentSize()
  {
    if (getVersion() == 1) {}
    for (int i = 20;; i = 12) {
      return i;
    }
  }
  
  public long getFragmentAbsoluteDuration()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.fragmentAbsoluteDuration;
  }
  
  public long getFragmentAbsoluteTime()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.fragmentAbsoluteTime;
  }
  
  public byte[] getUserType()
  {
    return new byte[] { 109, 29, -101, 5, 66, -43, 68, -26, -128, -30, 20, 29, -81, -9, 87, -78 };
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/piff/TfxdBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */