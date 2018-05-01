package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeReader;
import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class OriginalFormatBox
  extends AbstractBox
{
  public static final String TYPE = "frma";
  private String dataFormat = "    ";
  
  static
  {
    
    if (!OriginalFormatBox.class.desiredAssertionStatus()) {}
    for (boolean bool = true;; bool = false)
    {
      $assertionsDisabled = bool;
      return;
    }
  }
  
  public OriginalFormatBox()
  {
    super("frma");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    this.dataFormat = IsoTypeReader.read4cc(paramByteBuffer);
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer.put(IsoFile.fourCCtoBytes(this.dataFormat));
  }
  
  protected long getContentSize()
  {
    return 4L;
  }
  
  public String getDataFormat()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.dataFormat;
  }
  
  public void setDataFormat(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    assert (paramString.length() == 4);
    this.dataFormat = paramString;
  }
  
  public String toString()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return "OriginalFormatBox[dataFormat=" + getDataFormat() + "]";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/OriginalFormatBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */