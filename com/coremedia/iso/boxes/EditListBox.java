package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class EditListBox
  extends AbstractFullBox
{
  public static final String TYPE = "elst";
  private List<Entry> entries = new LinkedList();
  
  static {}
  
  public EditListBox()
  {
    super("elst");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    int j = CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer));
    this.entries = new LinkedList();
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return;
      }
      this.entries.add(new Entry(this, paramByteBuffer));
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
      ((Entry)localIterator.next()).getContent(paramByteBuffer);
    }
  }
  
  protected long getContentSize()
  {
    if (getVersion() == 1) {
      return 8L + this.entries.size() * 20;
    }
    return 8L + this.entries.size() * 12;
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
    return "EditListBox{entries=" + this.entries + '}';
  }
  
  public static class Entry
  {
    EditListBox editListBox;
    private double mediaRate;
    private long mediaTime;
    private long segmentDuration;
    
    public Entry(EditListBox paramEditListBox, long paramLong1, long paramLong2, double paramDouble)
    {
      this.segmentDuration = paramLong1;
      this.mediaTime = paramLong2;
      this.mediaRate = paramDouble;
      this.editListBox = paramEditListBox;
    }
    
    public Entry(EditListBox paramEditListBox, ByteBuffer paramByteBuffer)
    {
      if (paramEditListBox.getVersion() == 1)
      {
        this.segmentDuration = IsoTypeReader.readUInt64(paramByteBuffer);
        this.mediaTime = paramByteBuffer.getLong();
      }
      for (this.mediaRate = IsoTypeReader.readFixedPoint1616(paramByteBuffer);; this.mediaRate = IsoTypeReader.readFixedPoint1616(paramByteBuffer))
      {
        this.editListBox = paramEditListBox;
        return;
        this.segmentDuration = IsoTypeReader.readUInt32(paramByteBuffer);
        this.mediaTime = paramByteBuffer.getInt();
      }
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
        if (this.mediaTime != ((Entry)paramObject).mediaTime) {
          return false;
        }
      } while (this.segmentDuration == ((Entry)paramObject).segmentDuration);
      return false;
    }
    
    public void getContent(ByteBuffer paramByteBuffer)
    {
      if (this.editListBox.getVersion() == 1)
      {
        IsoTypeWriter.writeUInt64(paramByteBuffer, this.segmentDuration);
        paramByteBuffer.putLong(this.mediaTime);
      }
      for (;;)
      {
        IsoTypeWriter.writeFixedPoint1616(paramByteBuffer, this.mediaRate);
        return;
        IsoTypeWriter.writeUInt32(paramByteBuffer, CastUtils.l2i(this.segmentDuration));
        paramByteBuffer.putInt(CastUtils.l2i(this.mediaTime));
      }
    }
    
    public double getMediaRate()
    {
      return this.mediaRate;
    }
    
    public long getMediaTime()
    {
      return this.mediaTime;
    }
    
    public long getSegmentDuration()
    {
      return this.segmentDuration;
    }
    
    public int hashCode()
    {
      return (int)(this.segmentDuration ^ this.segmentDuration >>> 32) * 31 + (int)(this.mediaTime ^ this.mediaTime >>> 32);
    }
    
    public void setMediaRate(double paramDouble)
    {
      this.mediaRate = paramDouble;
    }
    
    public void setMediaTime(long paramLong)
    {
      this.mediaTime = paramLong;
    }
    
    public void setSegmentDuration(long paramLong)
    {
      this.segmentDuration = paramLong;
    }
    
    public String toString()
    {
      return "Entry{segmentDuration=" + this.segmentDuration + ", mediaTime=" + this.mediaTime + ", mediaRate=" + this.mediaRate + '}';
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/EditListBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */