package com.googlecode.mp4parser.boxes.threegpp26244;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitWriterBuffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class SegmentIndexBox
  extends AbstractFullBox
{
  public static final String TYPE = "sidx";
  long earliestPresentationTime;
  List<Entry> entries = new ArrayList();
  long firstOffset;
  long referenceId;
  int reserved;
  long timeScale;
  
  static {}
  
  public SegmentIndexBox()
  {
    super("sidx");
  }
  
  protected void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    this.referenceId = IsoTypeReader.readUInt32(paramByteBuffer);
    this.timeScale = IsoTypeReader.readUInt32(paramByteBuffer);
    int j;
    int i;
    if (getVersion() == 0)
    {
      this.earliestPresentationTime = IsoTypeReader.readUInt32(paramByteBuffer);
      this.firstOffset = IsoTypeReader.readUInt32(paramByteBuffer);
      this.reserved = IsoTypeReader.readUInt16(paramByteBuffer);
      j = IsoTypeReader.readUInt16(paramByteBuffer);
      i = 0;
    }
    for (;;)
    {
      if (i >= j)
      {
        return;
        this.earliestPresentationTime = IsoTypeReader.readUInt64(paramByteBuffer);
        this.firstOffset = IsoTypeReader.readUInt64(paramByteBuffer);
        break;
      }
      BitReaderBuffer localBitReaderBuffer = new BitReaderBuffer(paramByteBuffer);
      Entry localEntry = new Entry();
      localEntry.setReferenceType((byte)localBitReaderBuffer.readBits(1));
      localEntry.setReferencedSize(localBitReaderBuffer.readBits(31));
      localEntry.setSubsegmentDuration(IsoTypeReader.readUInt32(paramByteBuffer));
      localBitReaderBuffer = new BitReaderBuffer(paramByteBuffer);
      localEntry.setStartsWithSap((byte)localBitReaderBuffer.readBits(1));
      localEntry.setSapType((byte)localBitReaderBuffer.readBits(3));
      localEntry.setSapDeltaTime(localBitReaderBuffer.readBits(28));
      this.entries.add(localEntry);
      i += 1;
    }
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.referenceId);
    IsoTypeWriter.writeUInt32(paramByteBuffer, this.timeScale);
    Iterator localIterator;
    if (getVersion() == 0)
    {
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.earliestPresentationTime);
      IsoTypeWriter.writeUInt32(paramByteBuffer, this.firstOffset);
      IsoTypeWriter.writeUInt16(paramByteBuffer, this.reserved);
      IsoTypeWriter.writeUInt16(paramByteBuffer, this.entries.size());
      localIterator = this.entries.iterator();
    }
    for (;;)
    {
      if (!localIterator.hasNext())
      {
        return;
        IsoTypeWriter.writeUInt64(paramByteBuffer, this.earliestPresentationTime);
        IsoTypeWriter.writeUInt64(paramByteBuffer, this.firstOffset);
        break;
      }
      Entry localEntry = (Entry)localIterator.next();
      BitWriterBuffer localBitWriterBuffer = new BitWriterBuffer(paramByteBuffer);
      localBitWriterBuffer.writeBits(localEntry.getReferenceType(), 1);
      localBitWriterBuffer.writeBits(localEntry.getReferencedSize(), 31);
      IsoTypeWriter.writeUInt32(paramByteBuffer, localEntry.getSubsegmentDuration());
      localBitWriterBuffer = new BitWriterBuffer(paramByteBuffer);
      localBitWriterBuffer.writeBits(localEntry.getStartsWithSap(), 1);
      localBitWriterBuffer.writeBits(localEntry.getSapType(), 3);
      localBitWriterBuffer.writeBits(localEntry.getSapDeltaTime(), 28);
    }
  }
  
  protected long getContentSize()
  {
    if (getVersion() == 0) {}
    for (int i = 8;; i = 16) {
      return 4L + 4L + 4L + i + 2L + 2L + this.entries.size() * 12;
    }
  }
  
  public long getEarliestPresentationTime()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.earliestPresentationTime;
  }
  
  public List<Entry> getEntries()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.entries;
  }
  
  public long getFirstOffset()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_8, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.firstOffset;
  }
  
  public long getReferenceId()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.referenceId;
  }
  
  public int getReserved()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_10, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.reserved;
  }
  
  public long getTimeScale()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.timeScale;
  }
  
  public void setEarliestPresentationTime(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_7, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.earliestPresentationTime = paramLong;
  }
  
  public void setEntries(List<Entry> paramList)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, paramList);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.entries = paramList;
  }
  
  public void setFirstOffset(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_9, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.firstOffset = paramLong;
  }
  
  public void setReferenceId(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.referenceId = paramLong;
  }
  
  public void setReserved(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_11, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.reserved = paramInt;
  }
  
  public void setTimeScale(long paramLong)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this, Conversions.longObject(paramLong));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.timeScale = paramLong;
  }
  
  public String toString()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_12, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return "SegmentIndexBox{entries=" + this.entries + ", referenceId=" + this.referenceId + ", timeScale=" + this.timeScale + ", earliestPresentationTime=" + this.earliestPresentationTime + ", firstOffset=" + this.firstOffset + ", reserved=" + this.reserved + '}';
  }
  
  public static class Entry
  {
    byte referenceType;
    int referencedSize;
    int sapDeltaTime;
    byte sapType;
    byte startsWithSap;
    long subsegmentDuration;
    
    public Entry() {}
    
    public Entry(int paramInt1, int paramInt2, long paramLong, boolean paramBoolean, int paramInt3, int paramInt4)
    {
      this.referenceType = ((byte)paramInt1);
      this.referencedSize = paramInt2;
      this.subsegmentDuration = paramLong;
      if (paramBoolean) {}
      for (paramInt1 = 1;; paramInt1 = 0)
      {
        this.startsWithSap = ((byte)paramInt1);
        this.sapType = ((byte)paramInt3);
        this.sapDeltaTime = paramInt4;
        return;
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
        if (this.referenceType != ((Entry)paramObject).referenceType) {
          return false;
        }
        if (this.referencedSize != ((Entry)paramObject).referencedSize) {
          return false;
        }
        if (this.sapDeltaTime != ((Entry)paramObject).sapDeltaTime) {
          return false;
        }
        if (this.sapType != ((Entry)paramObject).sapType) {
          return false;
        }
        if (this.startsWithSap != ((Entry)paramObject).startsWithSap) {
          return false;
        }
      } while (this.subsegmentDuration == ((Entry)paramObject).subsegmentDuration);
      return false;
    }
    
    public byte getReferenceType()
    {
      return this.referenceType;
    }
    
    public int getReferencedSize()
    {
      return this.referencedSize;
    }
    
    public int getSapDeltaTime()
    {
      return this.sapDeltaTime;
    }
    
    public byte getSapType()
    {
      return this.sapType;
    }
    
    public byte getStartsWithSap()
    {
      return this.startsWithSap;
    }
    
    public long getSubsegmentDuration()
    {
      return this.subsegmentDuration;
    }
    
    public int hashCode()
    {
      return ((((this.referenceType * 31 + this.referencedSize) * 31 + (int)(this.subsegmentDuration ^ this.subsegmentDuration >>> 32)) * 31 + this.startsWithSap) * 31 + this.sapType) * 31 + this.sapDeltaTime;
    }
    
    public void setReferenceType(byte paramByte)
    {
      this.referenceType = paramByte;
    }
    
    public void setReferencedSize(int paramInt)
    {
      this.referencedSize = paramInt;
    }
    
    public void setSapDeltaTime(int paramInt)
    {
      this.sapDeltaTime = paramInt;
    }
    
    public void setSapType(byte paramByte)
    {
      this.sapType = paramByte;
    }
    
    public void setStartsWithSap(byte paramByte)
    {
      this.startsWithSap = paramByte;
    }
    
    public void setSubsegmentDuration(long paramLong)
    {
      this.subsegmentDuration = paramLong;
    }
    
    public String toString()
    {
      return "Entry{referenceType=" + this.referenceType + ", referencedSize=" + this.referencedSize + ", subsegmentDuration=" + this.subsegmentDuration + ", startsWithSap=" + this.startsWithSap + ", sapType=" + this.sapType + ", sapDeltaTime=" + this.sapDeltaTime + '}';
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/threegpp26244/SegmentIndexBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */