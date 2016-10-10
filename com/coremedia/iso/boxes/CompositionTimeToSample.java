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
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class CompositionTimeToSample
  extends AbstractFullBox
{
  public static final String TYPE = "ctts";
  List<Entry> entries = Collections.emptyList();
  
  static
  {
    
    if (!CompositionTimeToSample.class.desiredAssertionStatus()) {}
    for (boolean bool = true;; bool = false)
    {
      $assertionsDisabled = bool;
      return;
    }
  }
  
  public CompositionTimeToSample()
  {
    super("ctts");
  }
  
  public static int[] blowupCompositionTimes(List<Entry> paramList)
  {
    long l = 0L;
    Object localObject = paramList.iterator();
    for (;;)
    {
      if (!((Iterator)localObject).hasNext())
      {
        if (($assertionsDisabled) || (l <= 2147483647L)) {
          break;
        }
        throw new AssertionError();
      }
      l += ((Entry)((Iterator)localObject).next()).getCount();
    }
    localObject = new int[(int)l];
    int i = 0;
    paramList = paramList.iterator();
    if (!paramList.hasNext()) {
      return (int[])localObject;
    }
    Entry localEntry = (Entry)paramList.next();
    int k = 0;
    int j = i;
    for (;;)
    {
      i = j;
      if (k >= localEntry.getCount()) {
        break;
      }
      localObject[j] = localEntry.getOffset();
      k += 1;
      j += 1;
    }
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
      Entry localEntry = new Entry(CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer)), paramByteBuffer.getInt());
      this.entries.add(localEntry);
      i += 1;
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
      paramByteBuffer.putInt(localEntry.getOffset());
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
  
  public static class Entry
  {
    int count;
    int offset;
    
    public Entry(int paramInt1, int paramInt2)
    {
      this.count = paramInt1;
      this.offset = paramInt2;
    }
    
    public int getCount()
    {
      return this.count;
    }
    
    public int getOffset()
    {
      return this.offset;
    }
    
    public void setCount(int paramInt)
    {
      this.count = paramInt;
    }
    
    public void setOffset(int paramInt)
    {
      this.offset = paramInt;
    }
    
    public String toString()
    {
      return "Entry{count=" + this.count + ", offset=" + this.offset + '}';
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/CompositionTimeToSample.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */