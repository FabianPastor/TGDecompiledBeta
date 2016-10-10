package com.mp4parser.iso14496.part15;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class HevcDecoderConfigurationRecord
{
  List<Array> arrays = new ArrayList();
  int avgFrameRate;
  int bitDepthChromaMinus8;
  int bitDepthLumaMinus8;
  int chromaFormat;
  int configurationVersion;
  int constantFrameRate;
  boolean frame_only_constraint_flag;
  long general_constraint_indicator_flags;
  int general_level_idc;
  long general_profile_compatibility_flags;
  int general_profile_idc;
  int general_profile_space;
  boolean general_tier_flag;
  boolean interlaced_source_flag;
  int lengthSizeMinusOne;
  int min_spatial_segmentation_idc;
  boolean non_packed_constraint_flag;
  int numTemporalLayers;
  int parallelismType;
  boolean progressive_source_flag;
  int reserved1 = 15;
  int reserved2 = 63;
  int reserved3 = 63;
  int reserved4 = 31;
  int reserved5 = 31;
  boolean temporalIdNested;
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if ((paramObject == null) || (getClass() != paramObject.getClass())) {
        return false;
      }
      paramObject = (HevcDecoderConfigurationRecord)paramObject;
      if (this.avgFrameRate != ((HevcDecoderConfigurationRecord)paramObject).avgFrameRate) {
        return false;
      }
      if (this.bitDepthChromaMinus8 != ((HevcDecoderConfigurationRecord)paramObject).bitDepthChromaMinus8) {
        return false;
      }
      if (this.bitDepthLumaMinus8 != ((HevcDecoderConfigurationRecord)paramObject).bitDepthLumaMinus8) {
        return false;
      }
      if (this.chromaFormat != ((HevcDecoderConfigurationRecord)paramObject).chromaFormat) {
        return false;
      }
      if (this.configurationVersion != ((HevcDecoderConfigurationRecord)paramObject).configurationVersion) {
        return false;
      }
      if (this.constantFrameRate != ((HevcDecoderConfigurationRecord)paramObject).constantFrameRate) {
        return false;
      }
      if (this.general_constraint_indicator_flags != ((HevcDecoderConfigurationRecord)paramObject).general_constraint_indicator_flags) {
        return false;
      }
      if (this.general_level_idc != ((HevcDecoderConfigurationRecord)paramObject).general_level_idc) {
        return false;
      }
      if (this.general_profile_compatibility_flags != ((HevcDecoderConfigurationRecord)paramObject).general_profile_compatibility_flags) {
        return false;
      }
      if (this.general_profile_idc != ((HevcDecoderConfigurationRecord)paramObject).general_profile_idc) {
        return false;
      }
      if (this.general_profile_space != ((HevcDecoderConfigurationRecord)paramObject).general_profile_space) {
        return false;
      }
      if (this.general_tier_flag != ((HevcDecoderConfigurationRecord)paramObject).general_tier_flag) {
        return false;
      }
      if (this.lengthSizeMinusOne != ((HevcDecoderConfigurationRecord)paramObject).lengthSizeMinusOne) {
        return false;
      }
      if (this.min_spatial_segmentation_idc != ((HevcDecoderConfigurationRecord)paramObject).min_spatial_segmentation_idc) {
        return false;
      }
      if (this.numTemporalLayers != ((HevcDecoderConfigurationRecord)paramObject).numTemporalLayers) {
        return false;
      }
      if (this.parallelismType != ((HevcDecoderConfigurationRecord)paramObject).parallelismType) {
        return false;
      }
      if (this.reserved1 != ((HevcDecoderConfigurationRecord)paramObject).reserved1) {
        return false;
      }
      if (this.reserved2 != ((HevcDecoderConfigurationRecord)paramObject).reserved2) {
        return false;
      }
      if (this.reserved3 != ((HevcDecoderConfigurationRecord)paramObject).reserved3) {
        return false;
      }
      if (this.reserved4 != ((HevcDecoderConfigurationRecord)paramObject).reserved4) {
        return false;
      }
      if (this.reserved5 != ((HevcDecoderConfigurationRecord)paramObject).reserved5) {
        return false;
      }
      if (this.temporalIdNested != ((HevcDecoderConfigurationRecord)paramObject).temporalIdNested) {
        return false;
      }
      if (this.arrays == null) {
        break;
      }
    } while (this.arrays.equals(((HevcDecoderConfigurationRecord)paramObject).arrays));
    for (;;)
    {
      return false;
      if (((HevcDecoderConfigurationRecord)paramObject).arrays == null) {
        break;
      }
    }
  }
  
  public List<Array> getArrays()
  {
    return this.arrays;
  }
  
  public int getAvgFrameRate()
  {
    return this.avgFrameRate;
  }
  
  public int getBitDepthChromaMinus8()
  {
    return this.bitDepthChromaMinus8;
  }
  
  public int getBitDepthLumaMinus8()
  {
    return this.bitDepthLumaMinus8;
  }
  
  public int getChromaFormat()
  {
    return this.chromaFormat;
  }
  
  public int getConfigurationVersion()
  {
    return this.configurationVersion;
  }
  
  public int getConstantFrameRate()
  {
    return this.constantFrameRate;
  }
  
  public long getGeneral_constraint_indicator_flags()
  {
    return this.general_constraint_indicator_flags;
  }
  
  public int getGeneral_level_idc()
  {
    return this.general_level_idc;
  }
  
  public long getGeneral_profile_compatibility_flags()
  {
    return this.general_profile_compatibility_flags;
  }
  
  public int getGeneral_profile_idc()
  {
    return this.general_profile_idc;
  }
  
  public int getGeneral_profile_space()
  {
    return this.general_profile_space;
  }
  
  public int getLengthSizeMinusOne()
  {
    return this.lengthSizeMinusOne;
  }
  
  public int getMin_spatial_segmentation_idc()
  {
    return this.min_spatial_segmentation_idc;
  }
  
  public int getNumTemporalLayers()
  {
    return this.numTemporalLayers;
  }
  
  public int getParallelismType()
  {
    return this.parallelismType;
  }
  
  public int getSize()
  {
    int j = 23;
    Iterator localIterator = this.arrays.iterator();
    if (!localIterator.hasNext()) {
      return j;
    }
    Object localObject = (Array)localIterator.next();
    int i = j + 3;
    localObject = ((Array)localObject).nalUnits.iterator();
    for (;;)
    {
      j = i;
      if (!((Iterator)localObject).hasNext()) {
        break;
      }
      i = i + 2 + ((byte[])((Iterator)localObject).next()).length;
    }
  }
  
  public int hashCode()
  {
    int j = 1;
    int k = 0;
    int m = this.configurationVersion;
    int n = this.general_profile_space;
    int i;
    int i1;
    int i2;
    int i3;
    int i4;
    int i5;
    int i6;
    int i7;
    int i8;
    int i9;
    int i10;
    int i11;
    int i12;
    int i13;
    int i14;
    int i15;
    int i16;
    int i17;
    if (this.general_tier_flag)
    {
      i = 1;
      i1 = this.general_profile_idc;
      i2 = (int)(this.general_profile_compatibility_flags ^ this.general_profile_compatibility_flags >>> 32);
      i3 = (int)(this.general_constraint_indicator_flags ^ this.general_constraint_indicator_flags >>> 32);
      i4 = this.general_level_idc;
      i5 = this.reserved1;
      i6 = this.min_spatial_segmentation_idc;
      i7 = this.reserved2;
      i8 = this.parallelismType;
      i9 = this.reserved3;
      i10 = this.chromaFormat;
      i11 = this.reserved4;
      i12 = this.bitDepthLumaMinus8;
      i13 = this.reserved5;
      i14 = this.bitDepthChromaMinus8;
      i15 = this.avgFrameRate;
      i16 = this.constantFrameRate;
      i17 = this.numTemporalLayers;
      if (!this.temporalIdNested) {
        break label312;
      }
    }
    for (;;)
    {
      int i18 = this.lengthSizeMinusOne;
      if (this.arrays != null) {
        k = this.arrays.hashCode();
      }
      return (((((((((((((((((((((m * 31 + n) * 31 + i) * 31 + i1) * 31 + i2) * 31 + i3) * 31 + i4) * 31 + i5) * 31 + i6) * 31 + i7) * 31 + i8) * 31 + i9) * 31 + i10) * 31 + i11) * 31 + i12) * 31 + i13) * 31 + i14) * 31 + i15) * 31 + i16) * 31 + i17) * 31 + j) * 31 + i18) * 31 + k;
      i = 0;
      break;
      label312:
      j = 0;
    }
  }
  
  public boolean isFrame_only_constraint_flag()
  {
    return this.frame_only_constraint_flag;
  }
  
  public boolean isGeneral_tier_flag()
  {
    return this.general_tier_flag;
  }
  
  public boolean isInterlaced_source_flag()
  {
    return this.interlaced_source_flag;
  }
  
  public boolean isNon_packed_constraint_flag()
  {
    return this.non_packed_constraint_flag;
  }
  
  public boolean isProgressive_source_flag()
  {
    return this.progressive_source_flag;
  }
  
  public boolean isTemporalIdNested()
  {
    return this.temporalIdNested;
  }
  
  public void parse(ByteBuffer paramByteBuffer)
  {
    this.configurationVersion = IsoTypeReader.readUInt8(paramByteBuffer);
    int i = IsoTypeReader.readUInt8(paramByteBuffer);
    this.general_profile_space = ((i & 0xC0) >> 6);
    if ((i & 0x20) > 0)
    {
      bool = true;
      this.general_tier_flag = bool;
      this.general_profile_idc = (i & 0x1F);
      this.general_profile_compatibility_flags = IsoTypeReader.readUInt32(paramByteBuffer);
      this.general_constraint_indicator_flags = IsoTypeReader.readUInt48(paramByteBuffer);
      if ((this.general_constraint_indicator_flags >> 44 & 0x8) <= 0L) {
        break label391;
      }
      bool = true;
      label84:
      this.frame_only_constraint_flag = bool;
      if ((this.general_constraint_indicator_flags >> 44 & 0x4) <= 0L) {
        break label397;
      }
      bool = true;
      label109:
      this.non_packed_constraint_flag = bool;
      if ((this.general_constraint_indicator_flags >> 44 & 0x2) <= 0L) {
        break label403;
      }
      bool = true;
      label134:
      this.interlaced_source_flag = bool;
      if ((this.general_constraint_indicator_flags >> 44 & 1L) <= 0L) {
        break label409;
      }
      bool = true;
      label157:
      this.progressive_source_flag = bool;
      this.general_constraint_indicator_flags &= 0x7FFFFFFFFFFF;
      this.general_level_idc = IsoTypeReader.readUInt8(paramByteBuffer);
      i = IsoTypeReader.readUInt16(paramByteBuffer);
      this.reserved1 = ((0xF000 & i) >> 12);
      this.min_spatial_segmentation_idc = (i & 0xFFF);
      i = IsoTypeReader.readUInt8(paramByteBuffer);
      this.reserved2 = ((i & 0xFC) >> 2);
      this.parallelismType = (i & 0x3);
      i = IsoTypeReader.readUInt8(paramByteBuffer);
      this.reserved3 = ((i & 0xFC) >> 2);
      this.chromaFormat = (i & 0x3);
      i = IsoTypeReader.readUInt8(paramByteBuffer);
      this.reserved4 = ((i & 0xF8) >> 3);
      this.bitDepthLumaMinus8 = (i & 0x7);
      i = IsoTypeReader.readUInt8(paramByteBuffer);
      this.reserved5 = ((i & 0xF8) >> 3);
      this.bitDepthChromaMinus8 = (i & 0x7);
      this.avgFrameRate = IsoTypeReader.readUInt16(paramByteBuffer);
      i = IsoTypeReader.readUInt8(paramByteBuffer);
      this.constantFrameRate = ((i & 0xC0) >> 6);
      this.numTemporalLayers = ((i & 0x38) >> 3);
      if ((i & 0x4) <= 0) {
        break label415;
      }
    }
    label391:
    label397:
    label403:
    label409:
    label415:
    for (boolean bool = true;; bool = false)
    {
      this.temporalIdNested = bool;
      this.lengthSizeMinusOne = (i & 0x3);
      int k = IsoTypeReader.readUInt8(paramByteBuffer);
      this.arrays = new ArrayList();
      i = 0;
      if (i < k) {
        break label421;
      }
      return;
      bool = false;
      break;
      bool = false;
      break label84;
      bool = false;
      break label109;
      bool = false;
      break label134;
      bool = false;
      break label157;
    }
    label421:
    Array localArray = new Array();
    int j = IsoTypeReader.readUInt8(paramByteBuffer);
    label446:
    label463:
    int m;
    if ((j & 0x80) > 0)
    {
      bool = true;
      localArray.array_completeness = bool;
      if ((j & 0x40) <= 0) {
        break label530;
      }
      bool = true;
      localArray.reserved = bool;
      localArray.nal_unit_type = (j & 0x3F);
      m = IsoTypeReader.readUInt16(paramByteBuffer);
      localArray.nalUnits = new ArrayList();
      j = 0;
    }
    for (;;)
    {
      if (j >= m)
      {
        this.arrays.add(localArray);
        i += 1;
        break;
        bool = false;
        break label446;
        label530:
        bool = false;
        break label463;
      }
      byte[] arrayOfByte = new byte[IsoTypeReader.readUInt16(paramByteBuffer)];
      paramByteBuffer.get(arrayOfByte);
      localArray.nalUnits.add(arrayOfByte);
      j += 1;
    }
  }
  
  public void setArrays(List<Array> paramList)
  {
    this.arrays = paramList;
  }
  
  public void setAvgFrameRate(int paramInt)
  {
    this.avgFrameRate = paramInt;
  }
  
  public void setBitDepthChromaMinus8(int paramInt)
  {
    this.bitDepthChromaMinus8 = paramInt;
  }
  
  public void setBitDepthLumaMinus8(int paramInt)
  {
    this.bitDepthLumaMinus8 = paramInt;
  }
  
  public void setChromaFormat(int paramInt)
  {
    this.chromaFormat = paramInt;
  }
  
  public void setConfigurationVersion(int paramInt)
  {
    this.configurationVersion = paramInt;
  }
  
  public void setConstantFrameRate(int paramInt)
  {
    this.constantFrameRate = paramInt;
  }
  
  public void setFrame_only_constraint_flag(boolean paramBoolean)
  {
    this.frame_only_constraint_flag = paramBoolean;
  }
  
  public void setGeneral_constraint_indicator_flags(long paramLong)
  {
    this.general_constraint_indicator_flags = paramLong;
  }
  
  public void setGeneral_level_idc(int paramInt)
  {
    this.general_level_idc = paramInt;
  }
  
  public void setGeneral_profile_compatibility_flags(long paramLong)
  {
    this.general_profile_compatibility_flags = paramLong;
  }
  
  public void setGeneral_profile_idc(int paramInt)
  {
    this.general_profile_idc = paramInt;
  }
  
  public void setGeneral_profile_space(int paramInt)
  {
    this.general_profile_space = paramInt;
  }
  
  public void setGeneral_tier_flag(boolean paramBoolean)
  {
    this.general_tier_flag = paramBoolean;
  }
  
  public void setInterlaced_source_flag(boolean paramBoolean)
  {
    this.interlaced_source_flag = paramBoolean;
  }
  
  public void setLengthSizeMinusOne(int paramInt)
  {
    this.lengthSizeMinusOne = paramInt;
  }
  
  public void setMin_spatial_segmentation_idc(int paramInt)
  {
    this.min_spatial_segmentation_idc = paramInt;
  }
  
  public void setNon_packed_constraint_flag(boolean paramBoolean)
  {
    this.non_packed_constraint_flag = paramBoolean;
  }
  
  public void setNumTemporalLayers(int paramInt)
  {
    this.numTemporalLayers = paramInt;
  }
  
  public void setParallelismType(int paramInt)
  {
    this.parallelismType = paramInt;
  }
  
  public void setProgressive_source_flag(boolean paramBoolean)
  {
    this.progressive_source_flag = paramBoolean;
  }
  
  public void setTemporalIdNested(boolean paramBoolean)
  {
    this.temporalIdNested = paramBoolean;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("HEVCDecoderConfigurationRecord{configurationVersion=").append(this.configurationVersion).append(", general_profile_space=").append(this.general_profile_space).append(", general_tier_flag=").append(this.general_tier_flag).append(", general_profile_idc=").append(this.general_profile_idc).append(", general_profile_compatibility_flags=").append(this.general_profile_compatibility_flags).append(", general_constraint_indicator_flags=").append(this.general_constraint_indicator_flags).append(", general_level_idc=").append(this.general_level_idc);
    if (this.reserved1 != 15)
    {
      str = ", reserved1=" + this.reserved1;
      localStringBuilder = localStringBuilder.append(str).append(", min_spatial_segmentation_idc=").append(this.min_spatial_segmentation_idc);
      if (this.reserved2 == 63) {
        break label431;
      }
      str = ", reserved2=" + this.reserved2;
      label172:
      localStringBuilder = localStringBuilder.append(str).append(", parallelismType=").append(this.parallelismType);
      if (this.reserved3 == 63) {
        break label438;
      }
      str = ", reserved3=" + this.reserved3;
      label221:
      localStringBuilder = localStringBuilder.append(str).append(", chromaFormat=").append(this.chromaFormat);
      if (this.reserved4 == 31) {
        break label445;
      }
      str = ", reserved4=" + this.reserved4;
      label270:
      localStringBuilder = localStringBuilder.append(str).append(", bitDepthLumaMinus8=").append(this.bitDepthLumaMinus8);
      if (this.reserved5 == 31) {
        break label452;
      }
    }
    label431:
    label438:
    label445:
    label452:
    for (String str = ", reserved5=" + this.reserved5;; str = "")
    {
      return str + ", bitDepthChromaMinus8=" + this.bitDepthChromaMinus8 + ", avgFrameRate=" + this.avgFrameRate + ", constantFrameRate=" + this.constantFrameRate + ", numTemporalLayers=" + this.numTemporalLayers + ", temporalIdNested=" + this.temporalIdNested + ", lengthSizeMinusOne=" + this.lengthSizeMinusOne + ", arrays=" + this.arrays + '}';
      str = "";
      break;
      str = "";
      break label172;
      str = "";
      break label221;
      str = "";
      break label270;
    }
  }
  
  public void write(ByteBuffer paramByteBuffer)
  {
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.configurationVersion);
    int j = this.general_profile_space;
    int k;
    if (this.general_tier_flag)
    {
      i = 32;
      IsoTypeWriter.writeUInt8(paramByteBuffer, i + (j << 6) + this.general_profile_idc);
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.general_profile_compatibility_flags);
      long l2 = this.general_constraint_indicator_flags;
      long l1 = l2;
      if (this.frame_only_constraint_flag) {
        l1 = l2 | 0x800000000000;
      }
      l2 = l1;
      if (this.non_packed_constraint_flag) {
        l2 = l1 | 0x400000000000;
      }
      l1 = l2;
      if (this.interlaced_source_flag) {
        l1 = l2 | 0x200000000000;
      }
      l2 = l1;
      if (this.progressive_source_flag) {
        l2 = l1 | 0x100000000000;
      }
      IsoTypeWriter.writeUInt48(paramByteBuffer, l2);
      IsoTypeWriter.writeUInt8(paramByteBuffer, this.general_level_idc);
      IsoTypeWriter.writeUInt16(paramByteBuffer, (this.reserved1 << 12) + this.min_spatial_segmentation_idc);
      IsoTypeWriter.writeUInt8(paramByteBuffer, (this.reserved2 << 2) + this.parallelismType);
      IsoTypeWriter.writeUInt8(paramByteBuffer, (this.reserved3 << 2) + this.chromaFormat);
      IsoTypeWriter.writeUInt8(paramByteBuffer, (this.reserved4 << 3) + this.bitDepthLumaMinus8);
      IsoTypeWriter.writeUInt8(paramByteBuffer, (this.reserved5 << 3) + this.bitDepthChromaMinus8);
      IsoTypeWriter.writeUInt16(paramByteBuffer, this.avgFrameRate);
      j = this.constantFrameRate;
      k = this.numTemporalLayers;
      if (!this.temporalIdNested) {
        break label306;
      }
    }
    Iterator localIterator;
    label306:
    for (int i = 4;; i = 0)
    {
      IsoTypeWriter.writeUInt8(paramByteBuffer, i + ((k << 3) + (j << 6)) + this.lengthSizeMinusOne);
      IsoTypeWriter.writeUInt8(paramByteBuffer, this.arrays.size());
      localIterator = this.arrays.iterator();
      if (localIterator.hasNext()) {
        break label311;
      }
      return;
      i = 0;
      break;
    }
    label311:
    Object localObject = (Array)localIterator.next();
    if (((Array)localObject).array_completeness)
    {
      i = 128;
      label335:
      if (!((Array)localObject).reserved) {
        break label429;
      }
    }
    label429:
    for (j = 64;; j = 0)
    {
      IsoTypeWriter.writeUInt8(paramByteBuffer, i + j + ((Array)localObject).nal_unit_type);
      IsoTypeWriter.writeUInt16(paramByteBuffer, ((Array)localObject).nalUnits.size());
      localObject = ((Array)localObject).nalUnits.iterator();
      while (((Iterator)localObject).hasNext())
      {
        byte[] arrayOfByte = (byte[])((Iterator)localObject).next();
        IsoTypeWriter.writeUInt16(paramByteBuffer, arrayOfByte.length);
        paramByteBuffer.put(arrayOfByte);
      }
      break;
      i = 0;
      break label335;
    }
  }
  
  public static class Array
  {
    public boolean array_completeness;
    public List<byte[]> nalUnits;
    public int nal_unit_type;
    public boolean reserved;
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = true;
      boolean bool3 = false;
      boolean bool1;
      if (this == paramObject) {
        bool1 = true;
      }
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                return bool1;
                bool1 = bool3;
              } while (paramObject == null);
              bool1 = bool3;
            } while (getClass() != paramObject.getClass());
            localObject = (Array)paramObject;
            bool1 = bool3;
          } while (this.array_completeness != ((Array)localObject).array_completeness);
          bool1 = bool3;
        } while (this.nal_unit_type != ((Array)localObject).nal_unit_type);
        bool1 = bool3;
      } while (this.reserved != ((Array)localObject).reserved);
      paramObject = this.nalUnits.listIterator();
      Object localObject = ((Array)localObject).nalUnits.listIterator();
      byte[] arrayOfByte1;
      byte[] arrayOfByte2;
      do
      {
        do
        {
          if ((!((ListIterator)paramObject).hasNext()) || (!((ListIterator)localObject).hasNext()))
          {
            if (!((ListIterator)paramObject).hasNext())
            {
              bool1 = bool2;
              if (!((ListIterator)localObject).hasNext()) {}
            }
            else
            {
              bool1 = false;
            }
            return bool1;
          }
          arrayOfByte1 = (byte[])((ListIterator)paramObject).next();
          arrayOfByte2 = (byte[])((ListIterator)localObject).next();
          if (arrayOfByte1 != null) {
            break;
          }
        } while (arrayOfByte2 == null);
        return false;
      } while (Arrays.equals(arrayOfByte1, arrayOfByte2));
      return false;
    }
    
    public int hashCode()
    {
      int j = 1;
      int k = 0;
      int i;
      if (this.array_completeness)
      {
        i = 1;
        if (!this.reserved) {
          break label66;
        }
      }
      for (;;)
      {
        int m = this.nal_unit_type;
        if (this.nalUnits != null) {
          k = this.nalUnits.hashCode();
        }
        return ((i * 31 + j) * 31 + m) * 31 + k;
        i = 0;
        break;
        label66:
        j = 0;
      }
    }
    
    public String toString()
    {
      return "Array{nal_unit_type=" + this.nal_unit_type + ", reserved=" + this.reserved + ", array_completeness=" + this.array_completeness + ", num_nals=" + this.nalUnits.size() + '}';
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/mp4parser/iso14496/part15/HevcDecoderConfigurationRecord.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */