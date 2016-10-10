package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class SampleToChunkBox
  extends AbstractFullBox
{
  public static final String TYPE = "stsc";
  List<Entry> entries = Collections.emptyList();
  
  static {}
  
  public SampleToChunkBox()
  {
    super("stsc");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    int j = CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer));
    this.entries = new ArrayList(j);
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return;
      }
      this.entries.add(new Entry(IsoTypeReader.readUInt32(paramByteBuffer), IsoTypeReader.readUInt32(paramByteBuffer), IsoTypeReader.readUInt32(paramByteBuffer)));
      i += 1;
    }
  }
  
  public long[] blowup(int paramInt)
  {
    Object localObject1 = Factory.makeJP(ajc$tjp_3, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before((JoinPoint)localObject1);
    long[] arrayOfLong = new long[paramInt];
    localObject1 = new LinkedList(this.entries);
    Collections.reverse((List)localObject1);
    Iterator localIterator = ((List)localObject1).iterator();
    localObject1 = (Entry)localIterator.next();
    paramInt = arrayOfLong.length;
    for (;;)
    {
      if (paramInt <= 1)
      {
        arrayOfLong[0] = ((Entry)localObject1).getSamplesPerChunk();
        return arrayOfLong;
      }
      arrayOfLong[(paramInt - 1)] = ((Entry)localObject1).getSamplesPerChunk();
      Object localObject2 = localObject1;
      if (paramInt == ((Entry)localObject1).getFirstChunk()) {
        localObject2 = (Entry)localIterator.next();
      }
      paramInt -= 1;
      localObject1 = localObject2;
    }
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.entries.size());
    Iterator localIterator = this.entries.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return;
      }
      Entry localEntry = (Entry)localIterator.next();
      IsoTypeWriter.writeUInt32(paramByteBuffer, localEntry.getFirstChunk());
      IsoTypeWriter.writeUInt32(paramByteBuffer, localEntry.getSamplesPerChunk());
      IsoTypeWriter.writeUInt32(paramByteBuffer, localEntry.getSampleDescriptionIndex());
    }
  }
  
  protected long getContentSize()
  {
    return this.entries.size() * 12 + 8;
  }
  
  public List<Entry> getEntries()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.entries;
  }
  
  public void setEntries(List<Entry> paramList)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, paramList);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.entries = paramList;
  }
  
  public String toString()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return "SampleToChunkBox[entryCount=" + this.entries.size() + "]";
  }
  
  public static class Entry
  {
    long firstChunk;
    long sampleDescriptionIndex;
    long samplesPerChunk;
    
    public Entry(long paramLong1, long paramLong2, long paramLong3)
    {
      this.firstChunk = paramLong1;
      this.samplesPerChunk = paramLong2;
      this.sampleDescriptionIndex = paramLong3;
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
        paramObject = (Entry)paramObject;
        if (this.firstChunk != ((Entry)paramObject).firstChunk) {
          return false;
        }
        if (this.sampleDescriptionIndex != ((Entry)paramObject).sampleDescriptionIndex) {
          return false;
        }
      } while (this.samplesPerChunk == ((Entry)paramObject).samplesPerChunk);
      return false;
    }
    
    public long getFirstChunk()
    {
      return this.firstChunk;
    }
    
    public long getSampleDescriptionIndex()
    {
      return this.sampleDescriptionIndex;
    }
    
    public long getSamplesPerChunk()
    {
      return this.samplesPerChunk;
    }
    
    public int hashCode()
    {
      return ((int)(this.firstChunk ^ this.firstChunk >>> 32) * 31 + (int)(this.samplesPerChunk ^ this.samplesPerChunk >>> 32)) * 31 + (int)(this.sampleDescriptionIndex ^ this.sampleDescriptionIndex >>> 32);
    }
    
    public void setFirstChunk(long paramLong)
    {
      this.firstChunk = paramLong;
    }
    
    public void setSampleDescriptionIndex(long paramLong)
    {
      this.sampleDescriptionIndex = paramLong;
    }
    
    public void setSamplesPerChunk(long paramLong)
    {
      this.samplesPerChunk = paramLong;
    }
    
    public String toString()
    {
      return "Entry{firstChunk=" + this.firstChunk + ", samplesPerChunk=" + this.samplesPerChunk + ", sampleDescriptionIndex=" + this.sampleDescriptionIndex + '}';
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/SampleToChunkBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */