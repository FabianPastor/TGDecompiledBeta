package com.mp4parser.iso14496.part30;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class WebVTTConfigurationBox
  extends AbstractBox
{
  public static final String TYPE = "vttC";
  String config;
  
  static {}
  
  public WebVTTConfigurationBox()
  {
    super("vttC");
  }
  
  protected void _parseDetails(ByteBuffer paramByteBuffer)
  {
    this.config = IsoTypeReader.readString(paramByteBuffer, paramByteBuffer.remaining());
  }
  
  public String getConfig()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.config;
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer.put(Utf8.convert(this.config));
  }
  
  protected long getContentSize()
  {
    return Utf8.utf8StringLengthInBytes(this.config);
  }
  
  public void setConfig(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.config = paramString;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/mp4parser/iso14496/part30/WebVTTConfigurationBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */