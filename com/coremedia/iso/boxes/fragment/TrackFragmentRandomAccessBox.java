package com.coremedia.iso.boxes.fragment;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeReaderVariable;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.IsoTypeWriterVariable;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class TrackFragmentRandomAccessBox
  extends AbstractFullBox
{
  public static final String TYPE = "tfra";
  private List<Entry> entries = Collections.emptyList();
  private int lengthSizeOfSampleNum = 2;
  private int lengthSizeOfTrafNum = 2;
  private int lengthSizeOfTrunNum = 2;
  private int reserved;
  private long trackId;
  
  static {}
  
  public TrackFragmentRandomAccessBox()
  {
    super("tfra");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.trackId = IsoTypeReader.readUInt32(paramByteBuffer);
    long l = IsoTypeReader.readUInt32(paramByteBuffer);
    this.reserved = ((int)(l >> 6));
    this.lengthSizeOfTrafNum = (((int)(0x3F & l) >> 4) + 1);
    this.lengthSizeOfTrunNum = (((int)(0xC & l) >> 2) + 1);
    this.lengthSizeOfSampleNum = ((int)(0x3 & l) + 1);
    l = IsoTypeReader.readUInt32(paramByteBuffer);
    this.entries = new ArrayList();
    int i = 0;
    if (i >= l) {
      return;
    }
    Entry localEntry = new Entry();
    if (getVersion() == 1) {
      localEntry.time = IsoTypeReader.readUInt64(paramByteBuffer);
    }
    for (localEntry.moofOffset = IsoTypeReader.readUInt64(paramByteBuffer);; localEntry.moofOffset = IsoTypeReader.readUInt32(paramByteBuffer))
    {
      localEntry.trafNumber = IsoTypeReaderVariable.read(paramByteBuffer, this.lengthSizeOfTrafNum);
      localEntry.trunNumber = IsoTypeReaderVariable.read(paramByteBuffer, this.lengthSizeOfTrunNum);
      localEntry.sampleNumber = IsoTypeReaderVariable.read(paramByteBuffer, this.lengthSizeOfSampleNum);
      this.entries.add(localEntry);
      i += 1;
      break;
      localEntry.time = IsoTypeReader.readUInt32(paramByteBuffer);
    }
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.trackId);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.reserved << 6 | (this.lengthSizeOfTrafNum - 1 & 0x3) << 4 | (this.lengthSizeOfTrunNum - 1 & 0x3) << 2 | this.lengthSizeOfSampleNum - 1 & 0x3);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.entries.size());
    Iterator localIterator = this.entries.iterator();
    if (!localIterator.hasNext()) {
      return;
    }
    Entry localEntry = (Entry)localIterator.next();
    if (getVersion() == 1)
    {
      IsoTypeWriter.writeUInt64(paramByteBuffer, localEntry.time);
      IsoTypeWriter.writeUInt64(paramByteBuffer, localEntry.moofOffset);
    }
    for (;;)
    {
      IsoTypeWriterVariable.write(localEntry.trafNumber, paramByteBuffer, this.lengthSizeOfTrafNum);
      IsoTypeWriterVariable.write(localEntry.trunNumber, paramByteBuffer, this.lengthSizeOfTrunNum);
      IsoTypeWriterVariable.write(localEntry.sampleNumber, paramByteBuffer, this.lengthSizeOfSampleNum);
      break;
      IsoTypeWriter.writeUInt32(paramByteBuffer, localEntry.time);
      IsoTypeWriter.writeUInt32(paramByteBuffer, localEntry.moofOffset);
    }
  }
  
  protected long getContentSize()
  {
    long l = 4L + 12L;
    if (getVersion() == 1) {}
    for (l += this.entries.size() * 16;; l += this.entries.size() * 8) {
      return l + this.lengthSizeOfTrafNum * this.entries.size() + this.lengthSizeOfTrunNum * this.entries.size() + this.lengthSizeOfSampleNum * this.entries.size();
    }
  }
  
  public List<Entry> getEntries()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_10, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return Collections.unmodifiableList(this.entries);
  }
  
  public int getLengthSizeOfSampleNum()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_8, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.lengthSizeOfSampleNum;
  }
  
  public int getLengthSizeOfTrafNum()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.lengthSizeOfTrafNum;
  }
  
  public int getLengthSizeOfTrunNum()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_7, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.lengthSizeOfTrunNum;
  }
  
  public long getNumberOfEntries()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_9, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.entries.size();
  }
  
  public int getReserved()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.reserved;
  }
  
  public long getTrackId()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.trackId;
  }
  
  public void setEntries(List<Entry> paramList)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_11, this, this, paramList);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.entries = paramList;
  }
  
  public void setLengthSizeOfSampleNum(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.lengthSizeOfSampleNum = paramInt;
  }
  
  public void setLengthSizeOfTrafNum(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.lengthSizeOfTrafNum = paramInt;
  }
  
  public void setLengthSizeOfTrunNum(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.lengthSizeOfTrunNum = paramInt;
  }
  
  public void setTrackId(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.trackId = paramLong;
  }
  
  public String toString()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_12, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return "TrackFragmentRandomAccessBox{trackId=" + this.trackId + ", entries=" + this.entries + '}';
  }
  
  public static class Entry
  {
    private long moofOffset;
    private long sampleNumber;
    private long time;
    private long trafNumber;
    private long trunNumber;
    
    public Entry() {}
    
    public Entry(long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5)
    {
      this.moofOffset = paramLong2;
      this.sampleNumber = paramLong5;
      this.time = paramLong1;
      this.trafNumber = paramLong3;
      this.trunNumber = paramLong4;
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
        if (this.moofOffset != ((Entry)paramObject).moofOffset) {
          return false;
        }
        if (this.sampleNumber != ((Entry)paramObject).sampleNumber) {
          return false;
        }
        if (this.time != ((Entry)paramObject).time) {
          return false;
        }
        if (this.trafNumber != ((Entry)paramObject).trafNumber) {
          return false;
        }
      } while (this.trunNumber == ((Entry)paramObject).trunNumber);
      return false;
    }
    
    public long getMoofOffset()
    {
      return this.moofOffset;
    }
    
    public long getSampleNumber()
    {
      return this.sampleNumber;
    }
    
    public long getTime()
    {
      return this.time;
    }
    
    public long getTrafNumber()
    {
      return this.trafNumber;
    }
    
    public long getTrunNumber()
    {
      return this.trunNumber;
    }
    
    public int hashCode()
    {
      return ((((int)(this.time ^ this.time >>> 32) * 31 + (int)(this.moofOffset ^ this.moofOffset >>> 32)) * 31 + (int)(this.trafNumber ^ this.trafNumber >>> 32)) * 31 + (int)(this.trunNumber ^ this.trunNumber >>> 32)) * 31 + (int)(this.sampleNumber ^ this.sampleNumber >>> 32);
    }
    
    public void setMoofOffset(long paramLong)
    {
      this.moofOffset = paramLong;
    }
    
    public void setSampleNumber(long paramLong)
    {
      this.sampleNumber = paramLong;
    }
    
    public void setTime(long paramLong)
    {
      this.time = paramLong;
    }
    
    public void setTrafNumber(long paramLong)
    {
      this.trafNumber = paramLong;
    }
    
    public void setTrunNumber(long paramLong)
    {
      this.trunNumber = paramLong;
    }
    
    public String toString()
    {
      return "Entry{time=" + this.time + ", moofOffset=" + this.moofOffset + ", trafNumber=" + this.trafNumber + ", trunNumber=" + this.trunNumber + ", sampleNumber=" + this.sampleNumber + '}';
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/fragment/TrackFragmentRandomAccessBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */