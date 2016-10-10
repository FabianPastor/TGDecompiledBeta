package com.googlecode.mp4parser.boxes.apple;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.annotations.DoNotParseDetail;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public abstract class Utf8AppleDataBox
  extends AppleDataBox
{
  String value;
  
  static {}
  
  protected Utf8AppleDataBox(String paramString)
  {
    super(paramString, 1);
  }
  
  protected int getDataLength()
  {
    return this.value.getBytes(Charset.forName("UTF-8")).length;
  }
  
  public String getValue()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    if (!isParsed()) {
      parseDetails();
    }
    return this.value;
  }
  
  protected void parseData(ByteBuffer paramByteBuffer)
  {
    this.value = IsoTypeReader.readString(paramByteBuffer, paramByteBuffer.remaining());
  }
  
  public void setValue(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.value = paramString;
  }
  
  @DoNotParseDetail
  public byte[] writeData()
  {
    return Utf8.convert(this.value);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/apple/Utf8AppleDataBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */