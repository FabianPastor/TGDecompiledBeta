package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class SchemeTypeBox
  extends AbstractFullBox
{
  public static final String TYPE = "schm";
  String schemeType = "    ";
  String schemeUri = null;
  long schemeVersion;
  
  static
  {
    
    if (!SchemeTypeBox.class.desiredAssertionStatus()) {}
    for (boolean bool = true;; bool = false)
    {
      $assertionsDisabled = bool;
      return;
    }
  }
  
  public SchemeTypeBox()
  {
    super("schm");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.schemeType = IsoTypeReader.read4cc(paramByteBuffer);
    this.schemeVersion = IsoTypeReader.readUInt32(paramByteBuffer);
    if ((getFlags() & 0x1) == 1) {
      this.schemeUri = IsoTypeReader.readString(paramByteBuffer);
    }
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    paramByteBuffer.put(IsoFile.fourCCtoBytes(this.schemeType));
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.schemeVersion);
    if ((getFlags() & 0x1) == 1) {
      paramByteBuffer.put(Utf8.convert(this.schemeUri));
    }
  }
  
  protected long getContentSize()
  {
    if ((getFlags() & 0x1) == 1) {}
    for (int i = Utf8.utf8StringLengthInBytes(this.schemeUri) + 1;; i = 0) {
      return i + 12;
    }
  }
  
  public String getSchemeType()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.schemeType;
  }
  
  public String getSchemeUri()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.schemeUri;
  }
  
  public long getSchemeVersion()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.schemeVersion;
  }
  
  public void setSchemeType(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    assert ((paramString != null) && (paramString.length() == 4)) : "SchemeType may not be null or not 4 bytes long";
    this.schemeType = paramString;
  }
  
  public void setSchemeUri(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.schemeUri = paramString;
  }
  
  public void setSchemeVersion(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.schemeVersion = paramInt;
  }
  
  public String toString()
  {
    Object localObject = Factory.makeJP(ajc$tjp_6, this, this);
    RequiresParseDetailAspect.aspectOf().before((JoinPoint)localObject);
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Schema Type Box[");
    ((StringBuilder)localObject).append("schemeUri=").append(this.schemeUri).append("; ");
    ((StringBuilder)localObject).append("schemeType=").append(this.schemeType).append("; ");
    ((StringBuilder)localObject).append("schemeVersion=").append(this.schemeVersion).append("; ");
    ((StringBuilder)localObject).append("]");
    return ((StringBuilder)localObject).toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/SchemeTypeBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */