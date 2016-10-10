package com.coremedia.iso.boxes.fragment;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class TrackRunBox
  extends AbstractFullBox
{
  public static final String TYPE = "trun";
  private int dataOffset;
  private List<Entry> entries = new ArrayList();
  private SampleFlags firstSampleFlags;
  
  static {}
  
  public TrackRunBox()
  {
    super("trun");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    long l = IsoTypeReader.readUInt32(paramByteBuffer);
    int i;
    if ((getFlags() & 0x1) == 1)
    {
      this.dataOffset = CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer));
      if ((getFlags() & 0x4) == 4) {
        this.firstSampleFlags = new SampleFlags(paramByteBuffer);
      }
      i = 0;
    }
    for (;;)
    {
      if (i >= l)
      {
        return;
        this.dataOffset = -1;
        break;
      }
      Entry localEntry = new Entry();
      if ((getFlags() & 0x100) == 256) {
        localEntry.sampleDuration = IsoTypeReader.readUInt32(paramByteBuffer);
      }
      if ((getFlags() & 0x200) == 512) {
        localEntry.sampleSize = IsoTypeReader.readUInt32(paramByteBuffer);
      }
      if ((getFlags() & 0x400) == 1024) {
        localEntry.sampleFlags = new SampleFlags(paramByteBuffer);
      }
      if ((getFlags() & 0x800) == 2048) {
        localEntry.sampleCompositionTimeOffset = paramByteBuffer.getInt();
      }
      this.entries.add(localEntry);
      i += 1;
    }
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.entries.size());
    int i = getFlags();
    if ((i & 0x1) == 1) {
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.dataOffset);
    }
    if ((i & 0x4) == 4) {
      this.firstSampleFlags.getContent(paramByteBuffer);
    }
    Iterator localIterator = this.entries.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return;
      }
      Entry localEntry = (Entry)localIterator.next();
      if ((i & 0x100) == 256) {
        IsoTypeWriter.writeUInt32(paramByteBuffer, localEntry.sampleDuration);
      }
      if ((i & 0x200) == 512) {
        IsoTypeWriter.writeUInt32(paramByteBuffer, localEntry.sampleSize);
      }
      if ((i & 0x400) == 1024) {
        localEntry.sampleFlags.getContent(paramByteBuffer);
      }
      if ((i & 0x800) == 2048) {
        if (getVersion() == 0) {
          IsoTypeWriter.writeUInt32(paramByteBuffer, localEntry.sampleCompositionTimeOffset);
        } else {
          paramByteBuffer.putInt((int)localEntry.sampleCompositionTimeOffset);
        }
      }
    }
  }
  
  protected long getContentSize()
  {
    long l1 = 8L;
    int i = getFlags();
    if ((i & 0x1) == 1) {
      l1 = 8L + 4L;
    }
    long l3 = l1;
    if ((i & 0x4) == 4) {
      l3 = l1 + 4L;
    }
    long l2 = 0L;
    if ((i & 0x100) == 256) {
      l2 = 0L + 4L;
    }
    l1 = l2;
    if ((i & 0x200) == 512) {
      l1 = l2 + 4L;
    }
    l2 = l1;
    if ((i & 0x400) == 1024) {
      l2 = l1 + 4L;
    }
    l1 = l2;
    if ((i & 0x800) == 2048) {
      l1 = l2 + 4L;
    }
    return l3 + this.entries.size() * l1;
  }
  
  public int getDataOffset()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_15, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.dataOffset;
  }
  
  public List<Entry> getEntries()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.entries;
  }
  
  public SampleFlags getFirstSampleFlags()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_16, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.firstSampleFlags;
  }
  
  public long[] getSampleCompositionTimeOffsets()
  {
    Object localObject = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before((JoinPoint)localObject);
    if (isSampleCompositionTimeOffsetPresent())
    {
      localObject = new long[this.entries.size()];
      int i = 0;
      for (;;)
      {
        if (i >= localObject.length) {
          return (long[])localObject;
        }
        localObject[i] = ((Entry)this.entries.get(i)).getSampleCompositionTimeOffset();
        i += 1;
      }
    }
    return null;
  }
  
  public long getSampleCount()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.entries.size();
  }
  
  public boolean isDataOffsetPresent()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return (getFlags() & 0x1) == 1;
  }
  
  public boolean isFirstSampleFlagsPresent()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return (getFlags() & 0x4) == 4;
  }
  
  public boolean isSampleCompositionTimeOffsetPresent()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_9, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return (getFlags() & 0x800) == 2048;
  }
  
  public boolean isSampleDurationPresent()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_7, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return (getFlags() & 0x100) == 256;
  }
  
  public boolean isSampleFlagsPresent()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_8, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return (getFlags() & 0x400) == 1024;
  }
  
  public boolean isSampleSizePresent()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return (getFlags() & 0x200) == 512;
  }
  
  public void setDataOffset(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    if (paramInt == -1) {
      setFlags(getFlags() & 0xFFFFFE);
    }
    for (;;)
    {
      this.dataOffset = paramInt;
      return;
      setFlags(getFlags() | 0x1);
    }
  }
  
  public void setDataOffsetPresent(boolean paramBoolean)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_10, this, this, Conversions.booleanObject(paramBoolean));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    if (paramBoolean)
    {
      setFlags(getFlags() | 0x1);
      return;
    }
    setFlags(getFlags() & 0xFFFFFE);
  }
  
  public void setEntries(List<Entry> paramList)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_19, this, this, paramList);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.entries = paramList;
  }
  
  public void setFirstSampleFlags(SampleFlags paramSampleFlags)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_17, this, this, paramSampleFlags);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    if (paramSampleFlags == null) {
      setFlags(getFlags() & 0xFFFFFB);
    }
    for (;;)
    {
      this.firstSampleFlags = paramSampleFlags;
      return;
      setFlags(getFlags() | 0x4);
    }
  }
  
  public void setSampleCompositionTimeOffsetPresent(boolean paramBoolean)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_14, this, this, Conversions.booleanObject(paramBoolean));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    if (paramBoolean)
    {
      setFlags(getFlags() | 0x800);
      return;
    }
    setFlags(getFlags() & 0xFFF7FF);
  }
  
  public void setSampleDurationPresent(boolean paramBoolean)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_12, this, this, Conversions.booleanObject(paramBoolean));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    if (paramBoolean)
    {
      setFlags(getFlags() | 0x100);
      return;
    }
    setFlags(getFlags() & 0xFFFEFF);
  }
  
  public void setSampleFlagsPresent(boolean paramBoolean)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_13, this, this, Conversions.booleanObject(paramBoolean));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    if (paramBoolean)
    {
      setFlags(getFlags() | 0x400);
      return;
    }
    setFlags(getFlags() & 0xFFFBFF);
  }
  
  public void setSampleSizePresent(boolean paramBoolean)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_11, this, this, Conversions.booleanObject(paramBoolean));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    if (paramBoolean)
    {
      setFlags(getFlags() | 0x200);
      return;
    }
    setFlags(getFlags() & 0xFFFDFF);
  }
  
  public String toString()
  {
    Object localObject = Factory.makeJP(ajc$tjp_18, this, this);
    RequiresParseDetailAspect.aspectOf().before((JoinPoint)localObject);
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("TrackRunBox");
    ((StringBuilder)localObject).append("{sampleCount=").append(this.entries.size());
    ((StringBuilder)localObject).append(", dataOffset=").append(this.dataOffset);
    ((StringBuilder)localObject).append(", dataOffsetPresent=").append(isDataOffsetPresent());
    ((StringBuilder)localObject).append(", sampleSizePresent=").append(isSampleSizePresent());
    ((StringBuilder)localObject).append(", sampleDurationPresent=").append(isSampleDurationPresent());
    ((StringBuilder)localObject).append(", sampleFlagsPresentPresent=").append(isSampleFlagsPresent());
    ((StringBuilder)localObject).append(", sampleCompositionTimeOffsetPresent=").append(isSampleCompositionTimeOffsetPresent());
    ((StringBuilder)localObject).append(", firstSampleFlags=").append(this.firstSampleFlags);
    ((StringBuilder)localObject).append('}');
    return ((StringBuilder)localObject).toString();
  }
  
  public static class Entry
  {
    private long sampleCompositionTimeOffset;
    private long sampleDuration;
    private SampleFlags sampleFlags;
    private long sampleSize;
    
    public Entry() {}
    
    public Entry(long paramLong1, long paramLong2, SampleFlags paramSampleFlags, int paramInt)
    {
      this.sampleDuration = paramLong1;
      this.sampleSize = paramLong2;
      this.sampleFlags = paramSampleFlags;
      this.sampleCompositionTimeOffset = paramInt;
    }
    
    public long getSampleCompositionTimeOffset()
    {
      return this.sampleCompositionTimeOffset;
    }
    
    public long getSampleDuration()
    {
      return this.sampleDuration;
    }
    
    public SampleFlags getSampleFlags()
    {
      return this.sampleFlags;
    }
    
    public long getSampleSize()
    {
      return this.sampleSize;
    }
    
    public void setSampleCompositionTimeOffset(int paramInt)
    {
      this.sampleCompositionTimeOffset = paramInt;
    }
    
    public void setSampleDuration(long paramLong)
    {
      this.sampleDuration = paramLong;
    }
    
    public void setSampleFlags(SampleFlags paramSampleFlags)
    {
      this.sampleFlags = paramSampleFlags;
    }
    
    public void setSampleSize(long paramLong)
    {
      this.sampleSize = paramLong;
    }
    
    public String toString()
    {
      return "Entry{duration=" + this.sampleDuration + ", size=" + this.sampleSize + ", dlags=" + this.sampleFlags + ", compTimeOffset=" + this.sampleCompositionTimeOffset + '}';
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/fragment/TrackRunBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */