package com.googlecode.mp4parser.boxes.apple;

import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class AppleGPSCoordinatesBox
  extends AbstractBox
{
  private static final int DEFAULT_LANG = 5575;
  public static final String TYPE = "©xyz";
  String coords;
  int lang = 5575;
  
  static {}
  
  public AppleGPSCoordinatesBox()
  {
    super("©xyz");
  }
  
  protected void _parseDetails(ByteBuffer paramByteBuffer)
  {
    int i = paramByteBuffer.getShort();
    this.lang = paramByteBuffer.getShort();
    byte[] arrayOfByte = new byte[i];
    paramByteBuffer.get(arrayOfByte);
    this.coords = Utf8.convert(arrayOfByte);
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer.putShort((short)this.coords.length());
    paramByteBuffer.putShort((short)this.lang);
    paramByteBuffer.put(Utf8.convert(this.coords));
  }
  
  protected long getContentSize()
  {
    return Utf8.utf8StringLengthInBytes(this.coords) + 4;
  }
  
  public String getValue()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.coords;
  }
  
  public void setValue(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.lang = 5575;
    this.coords = paramString;
  }
  
  public String toString()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return "AppleGPSCoordinatesBox[" + this.coords + "]";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/apple/AppleGPSCoordinatesBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */