package com.mp4parser.iso14496.part12;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class SampleAuxiliaryInformationSizesBox
  extends AbstractFullBox
{
  public static final String TYPE = "saiz";
  private String auxInfoType;
  private String auxInfoTypeParameter;
  private short defaultSampleInfoSize;
  private int sampleCount;
  private short[] sampleInfoSizes = new short[0];
  
  static
  {
    
    if (!SampleAuxiliaryInformationSizesBox.class.desiredAssertionStatus()) {}
    for (boolean bool = true;; bool = false)
    {
      $assertionsDisabled = bool;
      return;
    }
  }
  
  public SampleAuxiliaryInformationSizesBox()
  {
    super("saiz");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    if ((getFlags() & 0x1) == 1)
    {
      this.auxInfoType = IsoTypeReader.read4cc(paramByteBuffer);
      this.auxInfoTypeParameter = IsoTypeReader.read4cc(paramByteBuffer);
    }
    this.defaultSampleInfoSize = ((short)IsoTypeReader.readUInt8(paramByteBuffer));
    this.sampleCount = CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer));
    int i;
    if (this.defaultSampleInfoSize == 0)
    {
      this.sampleInfoSizes = new short[this.sampleCount];
      i = 0;
    }
    for (;;)
    {
      if (i >= this.sampleCount) {
        return;
      }
      this.sampleInfoSizes[i] = ((short)IsoTypeReader.readUInt8(paramByteBuffer));
      i += 1;
    }
  }
  
  public String getAuxInfoType()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.auxInfoType;
  }
  
  public String getAuxInfoTypeParameter()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this);
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
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.defaultSampleInfoSize);
    if (this.defaultSampleInfoSize == 0)
    {
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.sampleInfoSizes.length);
      short[] arrayOfShort = this.sampleInfoSizes;
      int j = arrayOfShort.length;
      int i = 0;
      for (;;)
      {
        if (i >= j) {
          return;
        }
        IsoTypeWriter.writeUInt8(paramByteBuffer, arrayOfShort[i]);
        i += 1;
      }
    }
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.sampleCount);
  }
  
  protected long getContentSize()
  {
    int i = 4;
    if ((getFlags() & 0x1) == 1) {
      i = 4 + 8;
    }
    if (this.defaultSampleInfoSize == 0) {}
    for (int j = this.sampleInfoSizes.length;; j = 0) {
      return i + 5 + j;
    }
  }
  
  public int getDefaultSampleInfoSize()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.defaultSampleInfoSize;
  }
  
  public int getSampleCount()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_9, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.sampleCount;
  }
  
  public short[] getSampleInfoSizes()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_7, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return Arrays.copyOf(this.sampleInfoSizes, this.sampleInfoSizes.length);
  }
  
  public short getSize(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    if (getDefaultSampleInfoSize() == 0) {
      return this.sampleInfoSizes[paramInt];
    }
    return this.defaultSampleInfoSize;
  }
  
  public void setAuxInfoType(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.auxInfoType = paramString;
  }
  
  public void setAuxInfoTypeParameter(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.auxInfoTypeParameter = paramString;
  }
  
  public void setDefaultSampleInfoSize(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    assert (paramInt <= 255);
    this.defaultSampleInfoSize = ((short)paramInt);
  }
  
  public void setSampleCount(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_10, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.sampleCount = paramInt;
  }
  
  public void setSampleInfoSizes(short[] paramArrayOfShort)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_8, this, this, paramArrayOfShort);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.sampleInfoSizes = Arrays.copyOf(paramArrayOfShort, paramArrayOfShort.length);
  }
  
  public String toString()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_11, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return "SampleAuxiliaryInformationSizesBox{defaultSampleInfoSize=" + this.defaultSampleInfoSize + ", sampleCount=" + this.sampleCount + ", auxInfoType='" + this.auxInfoType + '\'' + ", auxInfoTypeParameter='" + this.auxInfoTypeParameter + '\'' + '}';
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/mp4parser/iso14496/part12/SampleAuxiliaryInformationSizesBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */