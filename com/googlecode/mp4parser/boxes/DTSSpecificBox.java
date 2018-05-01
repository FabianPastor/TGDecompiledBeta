package com.googlecode.mp4parser.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.annotations.DoNotParseDetail;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitWriterBuffer;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class DTSSpecificBox
  extends AbstractBox
{
  public static final String TYPE = "ddts";
  long DTSSamplingFrequency;
  int LBRDurationMod;
  long avgBitRate;
  int channelLayout;
  int coreLFEPresent;
  int coreLayout;
  int coreSize;
  int frameDuration;
  long maxBitRate;
  int multiAssetFlag;
  int pcmSampleDepth;
  int representationType;
  int reserved;
  int reservedBoxPresent;
  int stereoDownmix;
  int streamConstruction;
  
  static {}
  
  public DTSSpecificBox()
  {
    super("ddts");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    this.DTSSamplingFrequency = IsoTypeReader.readUInt32(paramByteBuffer);
    this.maxBitRate = IsoTypeReader.readUInt32(paramByteBuffer);
    this.avgBitRate = IsoTypeReader.readUInt32(paramByteBuffer);
    this.pcmSampleDepth = IsoTypeReader.readUInt8(paramByteBuffer);
    paramByteBuffer = new BitReaderBuffer(paramByteBuffer);
    this.frameDuration = paramByteBuffer.readBits(2);
    this.streamConstruction = paramByteBuffer.readBits(5);
    this.coreLFEPresent = paramByteBuffer.readBits(1);
    this.coreLayout = paramByteBuffer.readBits(6);
    this.coreSize = paramByteBuffer.readBits(14);
    this.stereoDownmix = paramByteBuffer.readBits(1);
    this.representationType = paramByteBuffer.readBits(3);
    this.channelLayout = paramByteBuffer.readBits(16);
    this.multiAssetFlag = paramByteBuffer.readBits(1);
    this.LBRDurationMod = paramByteBuffer.readBits(1);
    this.reservedBoxPresent = paramByteBuffer.readBits(1);
    this.reserved = paramByteBuffer.readBits(5);
  }
  
  public long getAvgBitRate()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.avgBitRate;
  }
  
  public int getChannelLayout()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_22, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.channelLayout;
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.DTSSamplingFrequency);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.maxBitRate);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.avgBitRate);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.pcmSampleDepth);
    paramByteBuffer = new BitWriterBuffer(paramByteBuffer);
    paramByteBuffer.writeBits(this.frameDuration, 2);
    paramByteBuffer.writeBits(this.streamConstruction, 5);
    paramByteBuffer.writeBits(this.coreLFEPresent, 1);
    paramByteBuffer.writeBits(this.coreLayout, 6);
    paramByteBuffer.writeBits(this.coreSize, 14);
    paramByteBuffer.writeBits(this.stereoDownmix, 1);
    paramByteBuffer.writeBits(this.representationType, 3);
    paramByteBuffer.writeBits(this.channelLayout, 16);
    paramByteBuffer.writeBits(this.multiAssetFlag, 1);
    paramByteBuffer.writeBits(this.LBRDurationMod, 1);
    paramByteBuffer.writeBits(this.reservedBoxPresent, 1);
    paramByteBuffer.writeBits(this.reserved, 5);
  }
  
  protected long getContentSize()
  {
    return 20L;
  }
  
  public int getCoreLFEPresent()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_12, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.coreLFEPresent;
  }
  
  public int getCoreLayout()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_14, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.coreLayout;
  }
  
  public int getCoreSize()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_16, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.coreSize;
  }
  
  public long getDTSSamplingFrequency()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.DTSSamplingFrequency;
  }
  
  @DoNotParseDetail
  public int[] getDashAudioChannelConfiguration()
  {
    int i1 = getChannelLayout();
    int m = 0;
    int k = 0;
    if ((i1 & 0x1) == 1)
    {
      m = 0 + 1;
      k = 0x0 | 0x4;
    }
    int i = k;
    int j = m;
    if ((i1 & 0x2) == 2)
    {
      j = m + 2;
      i = k | 0x1 | 0x2;
    }
    k = i;
    m = j;
    if ((i1 & 0x4) == 4)
    {
      m = j + 2;
      k = i | 0x10 | 0x20;
    }
    i = k;
    j = m;
    if ((i1 & 0x8) == 8)
    {
      j = m + 1;
      i = k | 0x8;
    }
    k = i;
    m = j;
    if ((i1 & 0x10) == 16)
    {
      m = j + 1;
      k = i | 0x100;
    }
    i = k;
    j = m;
    if ((i1 & 0x20) == 32)
    {
      j = m + 2;
      i = k | 0x1000 | 0x4000;
    }
    k = i;
    m = j;
    if ((i1 & 0x40) == 64)
    {
      m = j + 2;
      k = i | 0x10 | 0x20;
    }
    i = k;
    j = m;
    if ((i1 & 0x80) == 128)
    {
      j = m + 1;
      i = k | 0x2000;
    }
    k = i;
    m = j;
    if ((i1 & 0x100) == 256)
    {
      m = j + 1;
      k = i | 0x800;
    }
    i = k;
    j = m;
    if ((i1 & 0x200) == 512)
    {
      j = m + 2;
      i = k | 0x40 | 0x80;
    }
    k = i;
    m = j;
    if ((i1 & 0x400) == 1024)
    {
      m = j + 2;
      k = i | 0x200 | 0x400;
    }
    i = k;
    j = m;
    if ((i1 & 0x800) == 2048)
    {
      j = m + 2;
      i = k | 0x10 | 0x20;
    }
    k = i;
    m = j;
    if ((i1 & 0x1000) == 4096)
    {
      m = j + 1;
      k = i | 0x8;
    }
    i = k;
    j = m;
    if ((i1 & 0x2000) == 8192)
    {
      j = m + 2;
      i = k | 0x10 | 0x20;
    }
    k = i;
    m = j;
    if ((i1 & 0x4000) == 16384)
    {
      m = j + 1;
      k = i | 0x10000;
    }
    int n = k;
    i = m;
    if ((i1 & 0x8000) == 32768)
    {
      i = m + 2;
      n = k | 0x8000 | 0x20000;
    }
    j = i;
    if ((i1 & 0x10000) == 65536) {
      j = i + 1;
    }
    i = j;
    if ((i1 & 0x20000) == 131072) {
      i = j + 2;
    }
    return new int[] { i, n };
  }
  
  public int getFrameDuration()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_8, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.frameDuration;
  }
  
  public int getLBRDurationMod()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_26, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.LBRDurationMod;
  }
  
  public long getMaxBitRate()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.maxBitRate;
  }
  
  public int getMultiAssetFlag()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_24, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.multiAssetFlag;
  }
  
  public int getPcmSampleDepth()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.pcmSampleDepth;
  }
  
  public int getRepresentationType()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_20, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.representationType;
  }
  
  public int getReserved()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_28, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.reserved;
  }
  
  public int getReservedBoxPresent()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_30, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.reservedBoxPresent;
  }
  
  public int getStereoDownmix()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_18, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.stereoDownmix;
  }
  
  public int getStreamConstruction()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_10, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.streamConstruction;
  }
  
  public void setAvgBitRate(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.avgBitRate = paramLong;
  }
  
  public void setChannelLayout(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_23, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.channelLayout = paramInt;
  }
  
  public void setCoreLFEPresent(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_13, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.coreLFEPresent = paramInt;
  }
  
  public void setCoreLayout(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_15, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.coreLayout = paramInt;
  }
  
  public void setCoreSize(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_17, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.coreSize = paramInt;
  }
  
  public void setDTSSamplingFrequency(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.DTSSamplingFrequency = paramLong;
  }
  
  public void setFrameDuration(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_9, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.frameDuration = paramInt;
  }
  
  public void setLBRDurationMod(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_27, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.LBRDurationMod = paramInt;
  }
  
  public void setMaxBitRate(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.maxBitRate = paramLong;
  }
  
  public void setMultiAssetFlag(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_25, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.multiAssetFlag = paramInt;
  }
  
  public void setPcmSampleDepth(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_7, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.pcmSampleDepth = paramInt;
  }
  
  public void setRepresentationType(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_21, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.representationType = paramInt;
  }
  
  public void setReserved(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_29, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.reserved = paramInt;
  }
  
  public void setReservedBoxPresent(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_31, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.reservedBoxPresent = paramInt;
  }
  
  public void setStereoDownmix(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_19, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.stereoDownmix = paramInt;
  }
  
  public void setStreamConstruction(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_11, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.streamConstruction = paramInt;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/DTSSpecificBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */