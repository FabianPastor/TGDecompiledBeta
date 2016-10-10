package com.googlecode.mp4parser.boxes.apple;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class AppleRecordingYearBox
  extends AppleDataBox
{
  Date date = new Date();
  DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ssZ");
  
  static {}
  
  public AppleRecordingYearBox()
  {
    super("©day", 1);
    this.df.setTimeZone(TimeZone.getTimeZone("UTC"));
  }
  
  protected static String iso8601toRfc822Date(String paramString)
  {
    return paramString.replaceAll("Z$", "+0000").replaceAll("([0-9][0-9]):([0-9][0-9])$", "$1$2");
  }
  
  protected static String rfc822toIso8601Date(String paramString)
  {
    return paramString.replaceAll("\\+0000$", "Z");
  }
  
  protected int getDataLength()
  {
    return Utf8.convert(rfc822toIso8601Date(this.df.format(this.date))).length;
  }
  
  public Date getDate()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.date;
  }
  
  protected void parseData(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer = IsoTypeReader.readString(paramByteBuffer, paramByteBuffer.remaining());
    try
    {
      this.date = this.df.parse(iso8601toRfc822Date(paramByteBuffer));
      return;
    }
    catch (ParseException paramByteBuffer)
    {
      throw new RuntimeException(paramByteBuffer);
    }
  }
  
  public void setDate(Date paramDate)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, paramDate);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.date = paramDate;
  }
  
  protected byte[] writeData()
  {
    return Utf8.convert(rfc822toIso8601Date(this.df.format(this.date)));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/apple/AppleRecordingYearBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */