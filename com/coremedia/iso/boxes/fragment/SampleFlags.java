package com.coremedia.iso.boxes.fragment;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import java.nio.ByteBuffer;

public class SampleFlags
{
  private byte is_leading;
  private byte reserved;
  private int sampleDegradationPriority;
  private byte sampleDependsOn;
  private byte sampleHasRedundancy;
  private byte sampleIsDependedOn;
  private boolean sampleIsDifferenceSample;
  private byte samplePaddingValue;
  
  public SampleFlags() {}
  
  public SampleFlags(ByteBuffer paramByteBuffer)
  {
    long l = IsoTypeReader.readUInt32(paramByteBuffer);
    this.reserved = ((byte)(byte)(int)((0xFFFFFFFFF0000000 & l) >> 28));
    this.is_leading = ((byte)(byte)(int)((0xC000000 & l) >> 26));
    this.sampleDependsOn = ((byte)(byte)(int)((0x3000000 & l) >> 24));
    this.sampleIsDependedOn = ((byte)(byte)(int)((0xC00000 & l) >> 22));
    this.sampleHasRedundancy = ((byte)(byte)(int)((0x300000 & l) >> 20));
    this.samplePaddingValue = ((byte)(byte)(int)((0xE0000 & l) >> 17));
    if ((0x10000 & l) >> 16 > 0L) {}
    for (boolean bool = true;; bool = false)
    {
      this.sampleIsDifferenceSample = bool;
      this.sampleDegradationPriority = ((int)(0xFFFF & l));
      return;
    }
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (this == paramObject) {}
    for (;;)
    {
      return bool;
      if ((paramObject == null) || (getClass() != paramObject.getClass()))
      {
        bool = false;
      }
      else
      {
        paramObject = (SampleFlags)paramObject;
        if (this.is_leading != ((SampleFlags)paramObject).is_leading) {
          bool = false;
        } else if (this.reserved != ((SampleFlags)paramObject).reserved) {
          bool = false;
        } else if (this.sampleDegradationPriority != ((SampleFlags)paramObject).sampleDegradationPriority) {
          bool = false;
        } else if (this.sampleDependsOn != ((SampleFlags)paramObject).sampleDependsOn) {
          bool = false;
        } else if (this.sampleHasRedundancy != ((SampleFlags)paramObject).sampleHasRedundancy) {
          bool = false;
        } else if (this.sampleIsDependedOn != ((SampleFlags)paramObject).sampleIsDependedOn) {
          bool = false;
        } else if (this.sampleIsDifferenceSample != ((SampleFlags)paramObject).sampleIsDifferenceSample) {
          bool = false;
        } else if (this.samplePaddingValue != ((SampleFlags)paramObject).samplePaddingValue) {
          bool = false;
        }
      }
    }
  }
  
  public void getContent(ByteBuffer paramByteBuffer)
  {
    long l1 = this.reserved << 28;
    long l2 = this.is_leading << 26;
    long l3 = this.sampleDependsOn << 24;
    long l4 = this.sampleIsDependedOn << 22;
    long l5 = this.sampleHasRedundancy << 20;
    long l6 = this.samplePaddingValue << 17;
    if (this.sampleIsDifferenceSample) {}
    for (int i = 1;; i = 0)
    {
      IsoTypeWriter.writeUInt32(paramByteBuffer, 0L | l1 | l2 | l3 | l4 | l5 | l6 | i << 16 | this.sampleDegradationPriority);
      return;
    }
  }
  
  public int getReserved()
  {
    return this.reserved;
  }
  
  public int getSampleDegradationPriority()
  {
    return this.sampleDegradationPriority;
  }
  
  public int getSampleDependsOn()
  {
    return this.sampleDependsOn;
  }
  
  public int getSampleHasRedundancy()
  {
    return this.sampleHasRedundancy;
  }
  
  public int getSampleIsDependedOn()
  {
    return this.sampleIsDependedOn;
  }
  
  public int getSamplePaddingValue()
  {
    return this.samplePaddingValue;
  }
  
  public int hashCode()
  {
    int i = this.reserved;
    int j = this.is_leading;
    int k = this.sampleDependsOn;
    int m = this.sampleIsDependedOn;
    int n = this.sampleHasRedundancy;
    int i1 = this.samplePaddingValue;
    if (this.sampleIsDifferenceSample) {}
    for (int i2 = 1;; i2 = 0) {
      return ((((((i * 31 + j) * 31 + k) * 31 + m) * 31 + n) * 31 + i1) * 31 + i2) * 31 + this.sampleDegradationPriority;
    }
  }
  
  public boolean isSampleIsDifferenceSample()
  {
    return this.sampleIsDifferenceSample;
  }
  
  public void setReserved(int paramInt)
  {
    this.reserved = ((byte)(byte)paramInt);
  }
  
  public void setSampleDegradationPriority(int paramInt)
  {
    this.sampleDegradationPriority = paramInt;
  }
  
  public void setSampleDependsOn(int paramInt)
  {
    this.sampleDependsOn = ((byte)(byte)paramInt);
  }
  
  public void setSampleHasRedundancy(int paramInt)
  {
    this.sampleHasRedundancy = ((byte)(byte)paramInt);
  }
  
  public void setSampleIsDependedOn(int paramInt)
  {
    this.sampleIsDependedOn = ((byte)(byte)paramInt);
  }
  
  public void setSampleIsDifferenceSample(boolean paramBoolean)
  {
    this.sampleIsDifferenceSample = paramBoolean;
  }
  
  public void setSamplePaddingValue(int paramInt)
  {
    this.samplePaddingValue = ((byte)(byte)paramInt);
  }
  
  public String toString()
  {
    return "SampleFlags{reserved=" + this.reserved + ", isLeading=" + this.is_leading + ", depOn=" + this.sampleDependsOn + ", isDepOn=" + this.sampleIsDependedOn + ", hasRedundancy=" + this.sampleHasRedundancy + ", padValue=" + this.samplePaddingValue + ", isDiffSample=" + this.sampleIsDifferenceSample + ", degradPrio=" + this.sampleDegradationPriority + '}';
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/fragment/SampleFlags.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */