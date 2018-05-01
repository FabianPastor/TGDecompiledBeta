package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class AlbumBox
  extends AbstractFullBox
{
  public static final String TYPE = "albm";
  private String albumTitle;
  private String language;
  private int trackNumber;
  
  static {}
  
  public AlbumBox()
  {
    super("albm");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.language = IsoTypeReader.readIso639(paramByteBuffer);
    this.albumTitle = IsoTypeReader.readString(paramByteBuffer);
    if (paramByteBuffer.remaining() > 0) {}
    for (this.trackNumber = IsoTypeReader.readUInt8(paramByteBuffer);; this.trackNumber = -1) {
      return;
    }
  }
  
  public String getAlbumTitle()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.albumTitle;
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeIso639(paramByteBuffer, this.language);
    paramByteBuffer.put(Utf8.convert(this.albumTitle));
    paramByteBuffer.put((byte)0);
    if (this.trackNumber != -1) {
      IsoTypeWriter.writeUInt8(paramByteBuffer, this.trackNumber);
    }
  }
  
  protected long getContentSize()
  {
    int i = Utf8.utf8StringLengthInBytes(this.albumTitle);
    if (this.trackNumber == -1) {}
    for (int j = 0;; j = 1) {
      return j + (i + 6 + 1);
    }
  }
  
  public String getLanguage()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.language;
  }
  
  public int getTrackNumber()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.trackNumber;
  }
  
  public void setAlbumTitle(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.albumTitle = paramString;
  }
  
  public void setLanguage(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.language = paramString;
  }
  
  public void setTrackNumber(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.trackNumber = paramInt;
  }
  
  public String toString()
  {
    Object localObject = Factory.makeJP(ajc$tjp_6, this, this);
    RequiresParseDetailAspect.aspectOf().before((JoinPoint)localObject);
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("AlbumBox[language=").append(getLanguage()).append(";");
    ((StringBuilder)localObject).append("albumTitle=").append(getAlbumTitle());
    if (this.trackNumber >= 0) {
      ((StringBuilder)localObject).append(";trackNumber=").append(getTrackNumber());
    }
    ((StringBuilder)localObject).append("]");
    return ((StringBuilder)localObject).toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/AlbumBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */