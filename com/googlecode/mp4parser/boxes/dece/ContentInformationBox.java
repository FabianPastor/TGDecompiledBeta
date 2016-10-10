package com.googlecode.mp4parser.boxes.dece;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class ContentInformationBox
  extends AbstractFullBox
{
  public static final String TYPE = "cinf";
  Map<String, String> brandEntries = new LinkedHashMap();
  String codecs;
  Map<String, String> idEntries = new LinkedHashMap();
  String languages;
  String mimeSubtypeName;
  String profileLevelIdc;
  String protection;
  
  static {}
  
  public ContentInformationBox()
  {
    super("cinf");
  }
  
  protected void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.mimeSubtypeName = IsoTypeReader.readString(paramByteBuffer);
    this.profileLevelIdc = IsoTypeReader.readString(paramByteBuffer);
    this.codecs = IsoTypeReader.readString(paramByteBuffer);
    this.protection = IsoTypeReader.readString(paramByteBuffer);
    this.languages = IsoTypeReader.readString(paramByteBuffer);
    int i = IsoTypeReader.readUInt8(paramByteBuffer);
    if (i <= 0) {
      i = IsoTypeReader.readUInt8(paramByteBuffer);
    }
    for (;;)
    {
      if (i <= 0)
      {
        return;
        this.brandEntries.put(IsoTypeReader.readString(paramByteBuffer), IsoTypeReader.readString(paramByteBuffer));
        i -= 1;
        break;
      }
      this.idEntries.put(IsoTypeReader.readString(paramByteBuffer), IsoTypeReader.readString(paramByteBuffer));
      i -= 1;
    }
  }
  
  public Map<String, String> getBrandEntries()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_10, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.brandEntries;
  }
  
  public String getCodecs()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.codecs;
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeZeroTermUtf8String(paramByteBuffer, this.mimeSubtypeName);
    IsoTypeWriter.writeZeroTermUtf8String(paramByteBuffer, this.profileLevelIdc);
    IsoTypeWriter.writeZeroTermUtf8String(paramByteBuffer, this.codecs);
    IsoTypeWriter.writeZeroTermUtf8String(paramByteBuffer, this.protection);
    IsoTypeWriter.writeZeroTermUtf8String(paramByteBuffer, this.languages);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.brandEntries.size());
    Iterator localIterator = this.brandEntries.entrySet().iterator();
    if (!localIterator.hasNext())
    {
      IsoTypeWriter.writeUInt8(paramByteBuffer, this.idEntries.size());
      localIterator = this.idEntries.entrySet().iterator();
    }
    for (;;)
    {
      if (!localIterator.hasNext())
      {
        return;
        localEntry = (Map.Entry)localIterator.next();
        IsoTypeWriter.writeZeroTermUtf8String(paramByteBuffer, (String)localEntry.getKey());
        IsoTypeWriter.writeZeroTermUtf8String(paramByteBuffer, (String)localEntry.getValue());
        break;
      }
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      IsoTypeWriter.writeZeroTermUtf8String(paramByteBuffer, (String)localEntry.getKey());
      IsoTypeWriter.writeZeroTermUtf8String(paramByteBuffer, (String)localEntry.getValue());
    }
  }
  
  protected long getContentSize()
  {
    long l = 4L + (Utf8.utf8StringLengthInBytes(this.mimeSubtypeName) + 1) + (Utf8.utf8StringLengthInBytes(this.profileLevelIdc) + 1) + (Utf8.utf8StringLengthInBytes(this.codecs) + 1) + (Utf8.utf8StringLengthInBytes(this.protection) + 1) + (Utf8.utf8StringLengthInBytes(this.languages) + 1) + 1L;
    Iterator localIterator = this.brandEntries.entrySet().iterator();
    if (!localIterator.hasNext())
    {
      l += 1L;
      localIterator = this.idEntries.entrySet().iterator();
    }
    for (;;)
    {
      if (!localIterator.hasNext())
      {
        return l;
        localEntry = (Map.Entry)localIterator.next();
        l = l + (Utf8.utf8StringLengthInBytes((String)localEntry.getKey()) + 1) + (Utf8.utf8StringLengthInBytes((String)localEntry.getValue()) + 1);
        break;
      }
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      l = l + (Utf8.utf8StringLengthInBytes((String)localEntry.getKey()) + 1) + (Utf8.utf8StringLengthInBytes((String)localEntry.getValue()) + 1);
    }
  }
  
  public Map<String, String> getIdEntries()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_12, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.idEntries;
  }
  
  public String getLanguages()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_8, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.languages;
  }
  
  public String getMimeSubtypeName()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.mimeSubtypeName;
  }
  
  public String getProfileLevelIdc()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.profileLevelIdc;
  }
  
  public String getProtection()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.protection;
  }
  
  public void setBrandEntries(Map<String, String> paramMap)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_11, this, this, paramMap);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.brandEntries = paramMap;
  }
  
  public void setCodecs(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.codecs = paramString;
  }
  
  public void setIdEntries(Map<String, String> paramMap)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_13, this, this, paramMap);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.idEntries = paramMap;
  }
  
  public void setLanguages(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_9, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.languages = paramString;
  }
  
  public void setMimeSubtypeName(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.mimeSubtypeName = paramString;
  }
  
  public void setProfileLevelIdc(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.profileLevelIdc = paramString;
  }
  
  public void setProtection(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_7, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.protection = paramString;
  }
  
  public static class BrandEntry
  {
    String iso_brand;
    String version;
    
    public BrandEntry(String paramString1, String paramString2)
    {
      this.iso_brand = paramString1;
      this.version = paramString2;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {}
      do
      {
        return true;
        if ((paramObject == null) || (getClass() != paramObject.getClass())) {
          return false;
        }
        paramObject = (BrandEntry)paramObject;
        if (this.iso_brand != null)
        {
          if (this.iso_brand.equals(((BrandEntry)paramObject).iso_brand)) {}
        }
        else {
          while (((BrandEntry)paramObject).iso_brand != null) {
            return false;
          }
        }
        if (this.version == null) {
          break;
        }
      } while (this.version.equals(((BrandEntry)paramObject).version));
      for (;;)
      {
        return false;
        if (((BrandEntry)paramObject).version == null) {
          break;
        }
      }
    }
    
    public int hashCode()
    {
      int j = 0;
      if (this.iso_brand != null) {}
      for (int i = this.iso_brand.hashCode();; i = 0)
      {
        if (this.version != null) {
          j = this.version.hashCode();
        }
        return i * 31 + j;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/dece/ContentInformationBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */