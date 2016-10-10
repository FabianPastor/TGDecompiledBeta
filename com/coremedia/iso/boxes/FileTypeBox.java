package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.annotations.DoNotParseDetail;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class FileTypeBox
  extends AbstractBox
{
  public static final String TYPE = "ftyp";
  private List<String> compatibleBrands = Collections.emptyList();
  private String majorBrand;
  private long minorVersion;
  
  static {}
  
  public FileTypeBox()
  {
    super("ftyp");
  }
  
  public FileTypeBox(String paramString, long paramLong, List<String> paramList)
  {
    super("ftyp");
    this.majorBrand = paramString;
    this.minorVersion = paramLong;
    this.compatibleBrands = paramList;
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    this.majorBrand = IsoTypeReader.read4cc(paramByteBuffer);
    this.minorVersion = IsoTypeReader.readUInt32(paramByteBuffer);
    int j = paramByteBuffer.remaining() / 4;
    this.compatibleBrands = new LinkedList();
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return;
      }
      this.compatibleBrands.add(IsoTypeReader.read4cc(paramByteBuffer));
      i += 1;
    }
  }
  
  public List<String> getCompatibleBrands()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.compatibleBrands;
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer.put(IsoFile.fourCCtoBytes(this.majorBrand));
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.minorVersion);
    Iterator localIterator = this.compatibleBrands.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return;
      }
      paramByteBuffer.put(IsoFile.fourCCtoBytes((String)localIterator.next()));
    }
  }
  
  protected long getContentSize()
  {
    return this.compatibleBrands.size() * 4 + 8;
  }
  
  public String getMajorBrand()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.majorBrand;
  }
  
  public long getMinorVersion()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.minorVersion;
  }
  
  public void setCompatibleBrands(List<String> paramList)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this, paramList);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.compatibleBrands = paramList;
  }
  
  public void setMajorBrand(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.majorBrand = paramString;
  }
  
  public void setMinorVersion(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.minorVersion = paramLong;
  }
  
  @DoNotParseDetail
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("FileTypeBox[");
    localStringBuilder.append("majorBrand=").append(getMajorBrand());
    localStringBuilder.append(";");
    localStringBuilder.append("minorVersion=").append(getMinorVersion());
    Iterator localIterator = this.compatibleBrands.iterator();
    for (;;)
    {
      if (!localIterator.hasNext())
      {
        localStringBuilder.append("]");
        return localStringBuilder.toString();
      }
      String str = (String)localIterator.next();
      localStringBuilder.append(";");
      localStringBuilder.append("compatibleBrand=").append(str);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/FileTypeBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */