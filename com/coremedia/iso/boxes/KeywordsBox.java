package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class KeywordsBox
  extends AbstractFullBox
{
  public static final String TYPE = "kywd";
  private String[] keywords;
  private String language;
  
  static {}
  
  public KeywordsBox()
  {
    super("kywd");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.language = IsoTypeReader.readIso639(paramByteBuffer);
    int j = IsoTypeReader.readUInt8(paramByteBuffer);
    this.keywords = new String[j];
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return;
      }
      IsoTypeReader.readUInt8(paramByteBuffer);
      this.keywords[i] = IsoTypeReader.readString(paramByteBuffer);
      i += 1;
    }
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeIso639(paramByteBuffer, this.language);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.keywords.length);
    String[] arrayOfString = this.keywords;
    int j = arrayOfString.length;
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return;
      }
      String str = arrayOfString[i];
      IsoTypeWriter.writeUInt8(paramByteBuffer, Utf8.utf8StringLengthInBytes(str) + 1);
      paramByteBuffer.put(Utf8.convert(str));
      i += 1;
    }
  }
  
  protected long getContentSize()
  {
    long l = 7L;
    String[] arrayOfString = this.keywords;
    int j = arrayOfString.length;
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return l;
      }
      l += Utf8.utf8StringLengthInBytes(arrayOfString[i]) + 1 + 1;
      i += 1;
    }
  }
  
  public String[] getKeywords()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.keywords;
  }
  
  public String getLanguage()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.language;
  }
  
  public void setKeywords(String[] paramArrayOfString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, paramArrayOfString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.keywords = paramArrayOfString;
  }
  
  public void setLanguage(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.language = paramString;
  }
  
  public String toString()
  {
    Object localObject = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before((JoinPoint)localObject);
    localObject = new StringBuffer();
    ((StringBuffer)localObject).append("KeywordsBox[language=").append(getLanguage());
    int i = 0;
    for (;;)
    {
      if (i >= this.keywords.length)
      {
        ((StringBuffer)localObject).append("]");
        return ((StringBuffer)localObject).toString();
      }
      ((StringBuffer)localObject).append(";keyword").append(i).append("=").append(this.keywords[i]);
      i += 1;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/KeywordsBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */