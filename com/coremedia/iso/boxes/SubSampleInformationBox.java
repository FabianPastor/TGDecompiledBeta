package com.coremedia.iso.boxes;

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
import org.aspectj.runtime.reflect.Factory;

public class SubSampleInformationBox
  extends AbstractFullBox
{
  public static final String TYPE = "subs";
  private List<SubSampleEntry> entries = new ArrayList();
  
  static {}
  
  public SubSampleInformationBox()
  {
    super("subs");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    long l2 = IsoTypeReader.readUInt32(paramByteBuffer);
    int i = 0;
    SubSampleEntry localSubSampleEntry;
    int j;
    for (;;)
    {
      if (i >= l2) {
        return;
      }
      localSubSampleEntry = new SubSampleEntry();
      localSubSampleEntry.setSampleDelta(IsoTypeReader.readUInt32(paramByteBuffer));
      int k = IsoTypeReader.readUInt16(paramByteBuffer);
      j = 0;
      if (j < k) {
        break;
      }
      this.entries.add(localSubSampleEntry);
      i += 1;
    }
    SubSampleInformationBox.SubSampleEntry.SubsampleEntry localSubsampleEntry = new SubSampleInformationBox.SubSampleEntry.SubsampleEntry();
    if (getVersion() == 1) {}
    for (long l1 = IsoTypeReader.readUInt32(paramByteBuffer);; l1 = IsoTypeReader.readUInt16(paramByteBuffer))
    {
      localSubsampleEntry.setSubsampleSize(l1);
      localSubsampleEntry.setSubsamplePriority(IsoTypeReader.readUInt8(paramByteBuffer));
      localSubsampleEntry.setDiscardable(IsoTypeReader.readUInt8(paramByteBuffer));
      localSubsampleEntry.setReserved(IsoTypeReader.readUInt32(paramByteBuffer));
      localSubSampleEntry.getSubsampleEntries().add(localSubsampleEntry);
      j += 1;
      break;
    }
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.entries.size());
    Iterator localIterator = this.entries.iterator();
    if (!localIterator.hasNext()) {
      return;
    }
    Object localObject = (SubSampleEntry)localIterator.next();
    IsoTypeWriter.writeUInt32(paramByteBuffer, ((SubSampleEntry)localObject).getSampleDelta());
    IsoTypeWriter.writeUInt16(paramByteBuffer, ((SubSampleEntry)localObject).getSubsampleCount());
    localObject = ((SubSampleEntry)localObject).getSubsampleEntries().iterator();
    label75:
    SubSampleInformationBox.SubSampleEntry.SubsampleEntry localSubsampleEntry;
    if (((Iterator)localObject).hasNext())
    {
      localSubsampleEntry = (SubSampleInformationBox.SubSampleEntry.SubsampleEntry)((Iterator)localObject).next();
      if (getVersion() != 1) {
        break label142;
      }
      IsoTypeWriter.writeUInt32(paramByteBuffer, localSubsampleEntry.getSubsampleSize());
    }
    for (;;)
    {
      IsoTypeWriter.writeUInt8(paramByteBuffer, localSubsampleEntry.getSubsamplePriority());
      IsoTypeWriter.writeUInt8(paramByteBuffer, localSubsampleEntry.getDiscardable());
      IsoTypeWriter.writeUInt32(paramByteBuffer, localSubsampleEntry.getReserved());
      break label75;
      break;
      label142:
      IsoTypeWriter.writeUInt16(paramByteBuffer, CastUtils.l2i(localSubsampleEntry.getSubsampleSize()));
    }
  }
  
  protected long getContentSize()
  {
    long l2 = 8L;
    Iterator localIterator = this.entries.iterator();
    SubSampleEntry localSubSampleEntry;
    long l1;
    int i;
    do
    {
      if (!localIterator.hasNext()) {
        return l2;
      }
      localSubSampleEntry = (SubSampleEntry)localIterator.next();
      l1 = l2 + 4L + 2L;
      i = 0;
      l2 = l1;
    } while (i >= localSubSampleEntry.getSubsampleEntries().size());
    if (getVersion() == 1) {}
    for (l1 += 4L;; l1 += 2L)
    {
      l1 = l1 + 2L + 4L;
      i += 1;
      break;
    }
  }
  
  public List<SubSampleEntry> getEntries()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.entries;
  }
  
  public void setEntries(List<SubSampleEntry> paramList)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, paramList);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.entries = paramList;
  }
  
  public String toString()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return "SubSampleInformationBox{entryCount=" + this.entries.size() + ", entries=" + this.entries + '}';
  }
  
  public static class SubSampleEntry
  {
    private long sampleDelta;
    private List<SubsampleEntry> subsampleEntries = new ArrayList();
    
    public long getSampleDelta()
    {
      return this.sampleDelta;
    }
    
    public int getSubsampleCount()
    {
      return this.subsampleEntries.size();
    }
    
    public List<SubsampleEntry> getSubsampleEntries()
    {
      return this.subsampleEntries;
    }
    
    public void setSampleDelta(long paramLong)
    {
      this.sampleDelta = paramLong;
    }
    
    public String toString()
    {
      return "SampleEntry{sampleDelta=" + this.sampleDelta + ", subsampleCount=" + this.subsampleEntries.size() + ", subsampleEntries=" + this.subsampleEntries + '}';
    }
    
    public static class SubsampleEntry
    {
      private int discardable;
      private long reserved;
      private int subsamplePriority;
      private long subsampleSize;
      
      public int getDiscardable()
      {
        return this.discardable;
      }
      
      public long getReserved()
      {
        return this.reserved;
      }
      
      public int getSubsamplePriority()
      {
        return this.subsamplePriority;
      }
      
      public long getSubsampleSize()
      {
        return this.subsampleSize;
      }
      
      public void setDiscardable(int paramInt)
      {
        this.discardable = paramInt;
      }
      
      public void setReserved(long paramLong)
      {
        this.reserved = paramLong;
      }
      
      public void setSubsamplePriority(int paramInt)
      {
        this.subsamplePriority = paramInt;
      }
      
      public void setSubsampleSize(long paramLong)
      {
        this.subsampleSize = paramLong;
      }
      
      public String toString()
      {
        return "SubsampleEntry{subsampleSize=" + this.subsampleSize + ", subsamplePriority=" + this.subsamplePriority + ", discardable=" + this.discardable + ", reserved=" + this.reserved + '}';
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/SubSampleInformationBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */