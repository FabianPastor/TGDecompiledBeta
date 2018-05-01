package com.coremedia.iso.boxes.threegpp26244;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class LocationInformationBox
  extends AbstractFullBox
{
  public static final String TYPE = "loci";
  private String additionalNotes = "";
  private double altitude;
  private String astronomicalBody = "";
  private String language;
  private double latitude;
  private double longitude;
  private String name = "";
  private int role;
  
  static {}
  
  public LocationInformationBox()
  {
    super("loci");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.language = IsoTypeReader.readIso639(paramByteBuffer);
    this.name = IsoTypeReader.readString(paramByteBuffer);
    this.role = IsoTypeReader.readUInt8(paramByteBuffer);
    this.longitude = IsoTypeReader.readFixedPoint1616(paramByteBuffer);
    this.latitude = IsoTypeReader.readFixedPoint1616(paramByteBuffer);
    this.altitude = IsoTypeReader.readFixedPoint1616(paramByteBuffer);
    this.astronomicalBody = IsoTypeReader.readString(paramByteBuffer);
    this.additionalNotes = IsoTypeReader.readString(paramByteBuffer);
  }
  
  public String getAdditionalNotes()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_14, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.additionalNotes;
  }
  
  public double getAltitude()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_10, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.altitude;
  }
  
  public String getAstronomicalBody()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_12, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.astronomicalBody;
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeIso639(paramByteBuffer, this.language);
    paramByteBuffer.put(Utf8.convert(this.name));
    paramByteBuffer.put((byte)0);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.role);
    IsoTypeWriter.writeFixedPoint1616(paramByteBuffer, this.longitude);
    IsoTypeWriter.writeFixedPoint1616(paramByteBuffer, this.latitude);
    IsoTypeWriter.writeFixedPoint1616(paramByteBuffer, this.altitude);
    paramByteBuffer.put(Utf8.convert(this.astronomicalBody));
    paramByteBuffer.put((byte)0);
    paramByteBuffer.put(Utf8.convert(this.additionalNotes));
    paramByteBuffer.put((byte)0);
  }
  
  protected long getContentSize()
  {
    return Utf8.convert(this.name).length + 22 + Utf8.convert(this.astronomicalBody).length + Utf8.convert(this.additionalNotes).length;
  }
  
  public String getLanguage()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.language;
  }
  
  public double getLatitude()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_8, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.latitude;
  }
  
  public double getLongitude()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.longitude;
  }
  
  public String getName()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.name;
  }
  
  public int getRole()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.role;
  }
  
  public void setAdditionalNotes(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_15, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.additionalNotes = paramString;
  }
  
  public void setAltitude(double paramDouble)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_11, this, this, Conversions.doubleObject(paramDouble));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.altitude = paramDouble;
  }
  
  public void setAstronomicalBody(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_13, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.astronomicalBody = paramString;
  }
  
  public void setLanguage(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.language = paramString;
  }
  
  public void setLatitude(double paramDouble)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_9, this, this, Conversions.doubleObject(paramDouble));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.latitude = paramDouble;
  }
  
  public void setLongitude(double paramDouble)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_7, this, this, Conversions.doubleObject(paramDouble));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.longitude = paramDouble;
  }
  
  public void setName(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.name = paramString;
  }
  
  public void setRole(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.role = paramInt;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/threegpp26244/LocationInformationBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */