package com.googlecode.mp4parser.boxes.mp4.samplegrouping;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.util.CastUtils;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class RateShareEntry
  extends GroupEntry
{
  public static final String TYPE = "rash";
  private short discardPriority;
  private List<Entry> entries = new LinkedList();
  private int maximumBitrate;
  private int minimumBitrate;
  private short operationPointCut;
  private short targetRateShare;
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if ((paramObject == null) || (getClass() != paramObject.getClass())) {
        return false;
      }
      paramObject = (RateShareEntry)paramObject;
      if (this.discardPriority != ((RateShareEntry)paramObject).discardPriority) {
        return false;
      }
      if (this.maximumBitrate != ((RateShareEntry)paramObject).maximumBitrate) {
        return false;
      }
      if (this.minimumBitrate != ((RateShareEntry)paramObject).minimumBitrate) {
        return false;
      }
      if (this.operationPointCut != ((RateShareEntry)paramObject).operationPointCut) {
        return false;
      }
      if (this.targetRateShare != ((RateShareEntry)paramObject).targetRateShare) {
        return false;
      }
      if (this.entries == null) {
        break;
      }
    } while (this.entries.equals(((RateShareEntry)paramObject).entries));
    for (;;)
    {
      return false;
      if (((RateShareEntry)paramObject).entries == null) {
        break;
      }
    }
  }
  
  public ByteBuffer get()
  {
    int i;
    ByteBuffer localByteBuffer;
    if (this.operationPointCut == 1)
    {
      i = 13;
      localByteBuffer = ByteBuffer.allocate(i);
      localByteBuffer.putShort(this.operationPointCut);
      if (this.operationPointCut != 1) {
        break label89;
      }
      localByteBuffer.putShort(this.targetRateShare);
    }
    for (;;)
    {
      localByteBuffer.putInt(this.maximumBitrate);
      localByteBuffer.putInt(this.minimumBitrate);
      IsoTypeWriter.writeUInt8(localByteBuffer, this.discardPriority);
      localByteBuffer.rewind();
      return localByteBuffer;
      i = this.operationPointCut * 6 + 11;
      break;
      label89:
      Iterator localIterator = this.entries.iterator();
      while (localIterator.hasNext())
      {
        Entry localEntry = (Entry)localIterator.next();
        localByteBuffer.putInt(localEntry.getAvailableBitrate());
        localByteBuffer.putShort(localEntry.getTargetRateShare());
      }
    }
  }
  
  public short getDiscardPriority()
  {
    return this.discardPriority;
  }
  
  public List<Entry> getEntries()
  {
    return this.entries;
  }
  
  public int getMaximumBitrate()
  {
    return this.maximumBitrate;
  }
  
  public int getMinimumBitrate()
  {
    return this.minimumBitrate;
  }
  
  public short getOperationPointCut()
  {
    return this.operationPointCut;
  }
  
  public short getTargetRateShare()
  {
    return this.targetRateShare;
  }
  
  public String getType()
  {
    return "rash";
  }
  
  public int hashCode()
  {
    int j = this.operationPointCut;
    int k = this.targetRateShare;
    if (this.entries != null) {}
    for (int i = this.entries.hashCode();; i = 0) {
      return ((((j * 31 + k) * 31 + i) * 31 + this.maximumBitrate) * 31 + this.minimumBitrate) * 31 + this.discardPriority;
    }
  }
  
  public void parse(ByteBuffer paramByteBuffer)
  {
    this.operationPointCut = paramByteBuffer.getShort();
    if (this.operationPointCut == 1) {
      this.targetRateShare = paramByteBuffer.getShort();
    }
    for (;;)
    {
      this.maximumBitrate = CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer));
      this.minimumBitrate = CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer));
      this.discardPriority = ((short)IsoTypeReader.readUInt8(paramByteBuffer));
      return;
      int i = this.operationPointCut;
      while (i > 0)
      {
        this.entries.add(new Entry(CastUtils.l2i(IsoTypeReader.readUInt32(paramByteBuffer)), paramByteBuffer.getShort()));
        i -= 1;
      }
    }
  }
  
  public void setDiscardPriority(short paramShort)
  {
    this.discardPriority = paramShort;
  }
  
  public void setEntries(List<Entry> paramList)
  {
    this.entries = paramList;
  }
  
  public void setMaximumBitrate(int paramInt)
  {
    this.maximumBitrate = paramInt;
  }
  
  public void setMinimumBitrate(int paramInt)
  {
    this.minimumBitrate = paramInt;
  }
  
  public void setOperationPointCut(short paramShort)
  {
    this.operationPointCut = paramShort;
  }
  
  public void setTargetRateShare(short paramShort)
  {
    this.targetRateShare = paramShort;
  }
  
  public static class Entry
  {
    int availableBitrate;
    short targetRateShare;
    
    public Entry(int paramInt, short paramShort)
    {
      this.availableBitrate = paramInt;
      this.targetRateShare = paramShort;
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
        if (this.availableBitrate != ((Entry)paramObject).availableBitrate) {
          return false;
        }
      } while (this.targetRateShare == ((Entry)paramObject).targetRateShare);
      return false;
    }
    
    public int getAvailableBitrate()
    {
      return this.availableBitrate;
    }
    
    public short getTargetRateShare()
    {
      return this.targetRateShare;
    }
    
    public int hashCode()
    {
      return this.availableBitrate * 31 + this.targetRateShare;
    }
    
    public void setAvailableBitrate(int paramInt)
    {
      this.availableBitrate = paramInt;
    }
    
    public void setTargetRateShare(short paramShort)
    {
      this.targetRateShare = paramShort;
    }
    
    public String toString()
    {
      return "{availableBitrate=" + this.availableBitrate + ", targetRateShare=" + this.targetRateShare + '}';
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/mp4/samplegrouping/RateShareEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */