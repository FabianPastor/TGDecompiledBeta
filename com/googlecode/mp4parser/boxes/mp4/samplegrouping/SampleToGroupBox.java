package com.googlecode.mp4parser.boxes.mp4.samplegrouping;

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

public class SampleToGroupBox
  extends AbstractFullBox
{
  public static final String TYPE = "sbgp";
  List<Entry> entries = new LinkedList();
  private String groupingType;
  private String groupingTypeParameter;
  
  static {}
  
  public SampleToGroupBox()
  {
    super("sbgp");
  }
  
  protected void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.groupingType = IsoTypeReader.read4cc(paramByteBuffer);
    if (getVersion() == 1) {
      this.groupingTypeParameter = IsoTypeReader.read4cc(paramByteBuffer);
    }
    for (long l = IsoTypeReader.readUInt32(paramByteBuffer);; l -= 1L)
    {
      if (l <= 0L) {
        return;
      }
      this.entries.add(new Entry(CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer)), CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer))));
    }
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    paramByteBuffer.put(this.groupingType.getBytes());
    if (getVersion() == 1) {
      paramByteBuffer.put(this.groupingTypeParameter.getBytes());
    }
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.entries.size());
    Iterator localIterator = this.entries.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return;
      }
      Entry localEntry = (Entry)localIterator.next();
      IsoTypeWriter.writeUInt32(paramByteBuffer, localEntry.getSampleCount());
      IsoTypeWriter.writeUInt32(paramByteBuffer, localEntry.getGroupDescriptionIndex());
    }
  }
  
  protected long getContentSize()
  {
    if (getVersion() == 1) {}
    for (int i = this.entries.size() * 8 + 16;; i = this.entries.size() * 8 + 12) {
      return i;
    }
  }
  
  public List<Entry> getEntries()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.entries;
  }
  
  public String getGroupingType()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.groupingType;
  }
  
  public String getGroupingTypeParameter()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.groupingTypeParameter;
  }
  
  public void setEntries(List<Entry> paramList)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this, paramList);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.entries = paramList;
  }
  
  public void setGroupingType(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.groupingType = paramString;
  }
  
  public void setGroupingTypeParameter(String paramString)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, paramString);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.groupingTypeParameter = paramString;
  }
  
  public static class Entry
  {
    private int groupDescriptionIndex;
    private long sampleCount;
    
    public Entry(long paramLong, int paramInt)
    {
      this.sampleCount = paramLong;
      this.groupDescriptionIndex = paramInt;
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
        if (this.groupDescriptionIndex != ((Entry)paramObject).groupDescriptionIndex) {
          return false;
        }
      } while (this.sampleCount == ((Entry)paramObject).sampleCount);
      return false;
    }
    
    public int getGroupDescriptionIndex()
    {
      return this.groupDescriptionIndex;
    }
    
    public long getSampleCount()
    {
      return this.sampleCount;
    }
    
    public int hashCode()
    {
      return (int)(this.sampleCount ^ this.sampleCount >>> 32) * 31 + this.groupDescriptionIndex;
    }
    
    public void setGroupDescriptionIndex(int paramInt)
    {
      this.groupDescriptionIndex = paramInt;
    }
    
    public void setSampleCount(long paramLong)
    {
      this.sampleCount = paramLong;
    }
    
    public String toString()
    {
      return "Entry{sampleCount=" + this.sampleCount + ", groupDescriptionIndex=" + this.groupDescriptionIndex + '}';
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/mp4/samplegrouping/SampleToGroupBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */