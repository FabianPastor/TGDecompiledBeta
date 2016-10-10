package com.googlecode.mp4parser.boxes;

import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitWriterBuffer;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class EC3SpecificBox
  extends AbstractBox
{
  public static final String TYPE = "dec3";
  int dataRate;
  List<Entry> entries = new LinkedList();
  int numIndSub;
  
  static {}
  
  public EC3SpecificBox()
  {
    super("dec3");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer = new BitReaderBuffer(paramByteBuffer);
    this.dataRate = paramByteBuffer.readBits(13);
    this.numIndSub = (paramByteBuffer.readBits(3) + 1);
    int i = 0;
    if (i >= this.numIndSub) {
      return;
    }
    Entry localEntry = new Entry();
    localEntry.fscod = paramByteBuffer.readBits(2);
    localEntry.bsid = paramByteBuffer.readBits(5);
    localEntry.bsmod = paramByteBuffer.readBits(5);
    localEntry.acmod = paramByteBuffer.readBits(3);
    localEntry.lfeon = paramByteBuffer.readBits(1);
    localEntry.reserved = paramByteBuffer.readBits(3);
    localEntry.num_dep_sub = paramByteBuffer.readBits(4);
    if (localEntry.num_dep_sub > 0) {
      localEntry.chan_loc = paramByteBuffer.readBits(9);
    }
    for (;;)
    {
      this.entries.add(localEntry);
      i += 1;
      break;
      localEntry.reserved2 = paramByteBuffer.readBits(1);
    }
  }
  
  public void addEntry(Entry paramEntry)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this, paramEntry);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.entries.add(paramEntry);
  }
  
  public void getContent(ByteBuffer paramByteBuffer)
  {
    Object localObject = Factory.makeJP(ajc$tjp_1, this, this, paramByteBuffer);
    RequiresParseDetailAspect.aspectOf().before((JoinPoint)localObject);
    paramByteBuffer = new BitWriterBuffer(paramByteBuffer);
    paramByteBuffer.writeBits(this.dataRate, 13);
    paramByteBuffer.writeBits(this.entries.size() - 1, 3);
    localObject = this.entries.iterator();
    for (;;)
    {
      if (!((Iterator)localObject).hasNext()) {
        return;
      }
      Entry localEntry = (Entry)((Iterator)localObject).next();
      paramByteBuffer.writeBits(localEntry.fscod, 2);
      paramByteBuffer.writeBits(localEntry.bsid, 5);
      paramByteBuffer.writeBits(localEntry.bsmod, 5);
      paramByteBuffer.writeBits(localEntry.acmod, 3);
      paramByteBuffer.writeBits(localEntry.lfeon, 1);
      paramByteBuffer.writeBits(localEntry.reserved, 3);
      paramByteBuffer.writeBits(localEntry.num_dep_sub, 4);
      if (localEntry.num_dep_sub > 0) {
        paramByteBuffer.writeBits(localEntry.chan_loc, 9);
      } else {
        paramByteBuffer.writeBits(localEntry.reserved2, 1);
      }
    }
  }
  
  public long getContentSize()
  {
    Object localObject = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before((JoinPoint)localObject);
    long l = 2L;
    localObject = this.entries.iterator();
    for (;;)
    {
      if (!((Iterator)localObject).hasNext()) {
        return l;
      }
      if (((Entry)((Iterator)localObject).next()).num_dep_sub > 0) {
        l += 4L;
      } else {
        l += 3L;
      }
    }
  }
  
  public int getDataRate()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.dataRate;
  }
  
  public List<Entry> getEntries()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.entries;
  }
  
  public int getNumIndSub()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_7, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.numIndSub;
  }
  
  public void setDataRate(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.dataRate = paramInt;
  }
  
  public void setEntries(List<Entry> paramList)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, paramList);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.entries = paramList;
  }
  
  public void setNumIndSub(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_8, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.numIndSub = paramInt;
  }
  
  public static class Entry
  {
    public int acmod;
    public int bsid;
    public int bsmod;
    public int chan_loc;
    public int fscod;
    public int lfeon;
    public int num_dep_sub;
    public int reserved;
    public int reserved2;
    
    public String toString()
    {
      return "Entry{fscod=" + this.fscod + ", bsid=" + this.bsid + ", bsmod=" + this.bsmod + ", acmod=" + this.acmod + ", lfeon=" + this.lfeon + ", reserved=" + this.reserved + ", num_dep_sub=" + this.num_dep_sub + ", chan_loc=" + this.chan_loc + ", reserved2=" + this.reserved2 + '}';
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/EC3SpecificBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */