package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.util.CastUtils;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class TimeToSampleBox
  extends AbstractFullBox
{
  public static final String TYPE = "stts";
  static Map<List<Entry>, SoftReference<long[]>> cache;
  List<Entry> entries = Collections.emptyList();
  
  static
  {
    
    if (!TimeToSampleBox.class.desiredAssertionStatus()) {}
    for (boolean bool = true;; bool = false)
    {
      $assertionsDisabled = bool;
      cache = new WeakHashMap();
      return;
    }
  }
  
  public TimeToSampleBox()
  {
    super("stts");
  }
  
  public static long[] blowupTimeToSamples(List<Entry> paramList)
  {
    long l;
    for (;;)
    {
      try
      {
        localObject1 = (SoftReference)cache.get(paramList);
        if (localObject1 != null)
        {
          localObject1 = (long[])((SoftReference)localObject1).get();
          if (localObject1 != null)
          {
            paramList = (List<Entry>)localObject1;
            return paramList;
          }
        }
        l = 0L;
        localObject1 = paramList.iterator();
        if (!((Iterator)localObject1).hasNext())
        {
          if (($assertionsDisabled) || (l <= 2147483647L)) {
            break;
          }
          paramList = new java/lang/AssertionError;
          paramList.<init>();
          throw paramList;
        }
      }
      finally {}
      l += ((Entry)((Iterator)localObject1).next()).getCount();
    }
    Object localObject1 = new long[(int)l];
    int i = 0;
    Object localObject2 = paramList.iterator();
    for (;;)
    {
      if (!((Iterator)localObject2).hasNext())
      {
        localObject2 = cache;
        localObject3 = new java/lang/ref/SoftReference;
        ((SoftReference)localObject3).<init>(localObject1);
        ((Map)localObject2).put(paramList, localObject3);
        paramList = (List<Entry>)localObject1;
        break;
      }
      Object localObject3 = (Entry)((Iterator)localObject2).next();
      int j = 0;
      while (j < ((Entry)localObject3).getCount())
      {
        localObject1[i] = ((Entry)localObject3).getDelta();
        j++;
        i++;
      }
    }
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    int i = CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer));
    this.entries = new ArrayList(i);
    for (int j = 0;; j++)
    {
      if (j >= i) {
        return;
      }
      this.entries.add(new Entry(IsoTypeReader.readUInt32(paramByteBuffer), IsoTypeReader.readUInt32(paramByteBuffer)));
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
      IsoTypeWriter.writeUInt32(paramByteBuffer, localEntry.getCount());
      IsoTypeWriter.writeUInt32(paramByteBuffer, localEntry.getDelta());
    }
  }
  
  protected long getContentSize()
  {
    return this.entries.size() * 8 + 8;
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
    return "TimeToSampleBox[entryCount=" + this.entries.size() + "]";
  }
  
  public static class Entry
  {
    long count;
    long delta;
    
    public Entry(long paramLong1, long paramLong2)
    {
      this.count = paramLong1;
      this.delta = paramLong2;
    }
    
    public long getCount()
    {
      return this.count;
    }
    
    public long getDelta()
    {
      return this.delta;
    }
    
    public void setCount(long paramLong)
    {
      this.count = paramLong;
    }
    
    public void setDelta(long paramLong)
    {
      this.delta = paramLong;
    }
    
    public String toString()
    {
      return "Entry{count=" + this.count + ", delta=" + this.delta + '}';
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/TimeToSampleBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */