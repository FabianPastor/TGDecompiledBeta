package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class ProgressiveDownloadInformationBox
  extends AbstractFullBox
{
  public static final String TYPE = "pdin";
  List<Entry> entries = Collections.emptyList();
  
  static {}
  
  public ProgressiveDownloadInformationBox()
  {
    super("pdin");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.entries = new LinkedList();
    for (;;)
    {
      if (paramByteBuffer.remaining() < 8) {
        return;
      }
      Entry localEntry = new Entry(IsoTypeReader.readUInt32(paramByteBuffer), IsoTypeReader.readUInt32(paramByteBuffer));
      this.entries.add(localEntry);
    }
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    Iterator localIterator = this.entries.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return;
      }
      Entry localEntry = (Entry)localIterator.next();
      IsoTypeWriter.writeUInt32(paramByteBuffer, localEntry.getRate());
      IsoTypeWriter.writeUInt32(paramByteBuffer, localEntry.getInitialDelay());
    }
  }
  
  protected long getContentSize()
  {
    return this.entries.size() * 8 + 4;
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
    return "ProgressiveDownloadInfoBox{entries=" + this.entries + '}';
  }
  
  public static class Entry
  {
    long initialDelay;
    long rate;
    
    public Entry(long paramLong1, long paramLong2)
    {
      this.rate = paramLong1;
      this.initialDelay = paramLong2;
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
          paramObject = (Entry)paramObject;
          if (this.initialDelay != ((Entry)paramObject).initialDelay) {
            bool = false;
          } else if (this.rate != ((Entry)paramObject).rate) {
            bool = false;
          }
        }
      }
    }
    
    public long getInitialDelay()
    {
      return this.initialDelay;
    }
    
    public long getRate()
    {
      return this.rate;
    }
    
    public int hashCode()
    {
      return (int)(this.rate ^ this.rate >>> 32) * 31 + (int)(this.initialDelay ^ this.initialDelay >>> 32);
    }
    
    public void setInitialDelay(long paramLong)
    {
      this.initialDelay = paramLong;
    }
    
    public void setRate(long paramLong)
    {
      this.rate = paramLong;
    }
    
    public String toString()
    {
      return "Entry{rate=" + this.rate + ", initialDelay=" + this.initialDelay + '}';
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/ProgressiveDownloadInformationBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */