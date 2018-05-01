package com.mp4parser.iso14496.part12;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class SampleAuxiliaryInformationOffsetsBox
  extends AbstractFullBox
{
  public static final String TYPE = "saio";
  private String auxInfoType;
  private String auxInfoTypeParameter;
  private long[] offsets = new long[0];
  
  static {}
  
  public SampleAuxiliaryInformationOffsetsBox()
  {
    super("saio");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    if ((getFlags() & 0x1) == 1)
    {
      this.auxInfoType = IsoTypeReader.read4cc(paramByteBuffer);
      this.auxInfoTypeParameter = IsoTypeReader.read4cc(paramByteBuffer);
    }
    int j = CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer));
    this.offsets = new long[j];
    int i = 0;
    if (i >= j) {
      return;
    }
    if (getVersion() == 0) {
      this.offsets[i] = IsoTypeReader.readUInt32(paramByteBuffer);
    }
    for (;;)
    {
      i += 1;
      break;
      this.offsets[i] = IsoTypeReader.readUInt64(paramByteBuffer);
    }
  }
  
  public String getAuxInfoType()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.auxInfoType;
  }
  
  public String getAuxInfoTypeParameter()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.auxInfoTypeParameter;
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    if ((getFlags() & 0x1) == 1)
    {
      paramByteBuffer.put(IsoFile.fourCCtoBytes(this.auxInfoType));
      paramByteBuffer.put(IsoFile.fourCCtoBytes(this.auxInfoTypeParameter));
    }
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.offsets.length);
    long[] arrayOfLong = this.offsets;
    int j = arrayOfLong.length;
    int i = 0;
    if (i >= j) {
      return;
    }
    Long localLong = Long.valueOf(arrayOfLong[i]);
    if (getVersion() == 0) {
      IsoTypeWriter.writeUInt32(paramByteBuffer, localLong.longValue());
    }
    for (;;)
    {
      i += 1;
      break;
      IsoTypeWriter.writeUInt64(paramByteBuffer, localLong.longValue());
    }
  }
  
  protected long getContentSize()
  {
    int i;
    if (getVersion() == 0)
    {
      i = this.offsets.length * 4;
      if ((getFlags() & 0x1) != 1) {
        break label48;
      }
    }
    label48:
    for (int j = 8;; j = 0)
    {
      return j + (i + 8);
      i = this.offsets.length * 8;
      break;
    }
  }
  
  public long[] getOffsets()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.offsets;
  }
  
  public void setAuxInfoType(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.auxInfoType = paramString;
  }
  
  public void setAuxInfoTypeParameter(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.auxInfoTypeParameter = paramString;
  }
  
  public void setOffsets(long[] paramArrayOfLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this, paramArrayOfLong);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.offsets = paramArrayOfLong;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/mp4parser/iso14496/part12/SampleAuxiliaryInformationOffsetsBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */