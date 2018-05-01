package com.googlecode.mp4parser.boxes.piff;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class TfrfBox
  extends AbstractFullBox
{
  public List<Entry> entries = new ArrayList();
  
  static {}
  
  public TfrfBox()
  {
    super("uuid");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    int j = IsoTypeReader.readUInt8(paramByteBuffer);
    int i = 0;
    if (i >= j) {
      return;
    }
    Entry localEntry = new Entry();
    if (getVersion() == 1) {
      localEntry.fragmentAbsoluteTime = IsoTypeReader.readUInt64(paramByteBuffer);
    }
    for (localEntry.fragmentAbsoluteDuration = IsoTypeReader.readUInt64(paramByteBuffer);; localEntry.fragmentAbsoluteDuration = IsoTypeReader.readUInt32(paramByteBuffer))
    {
      this.entries.add(localEntry);
      i += 1;
      break;
      localEntry.fragmentAbsoluteTime = IsoTypeReader.readUInt32(paramByteBuffer);
    }
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.entries.size());
    Iterator localIterator = this.entries.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return;
      }
      Entry localEntry = (Entry)localIterator.next();
      if (getVersion() == 1)
      {
        IsoTypeWriter.writeUInt64(paramByteBuffer, localEntry.fragmentAbsoluteTime);
        IsoTypeWriter.writeUInt64(paramByteBuffer, localEntry.fragmentAbsoluteDuration);
      }
      else
      {
        IsoTypeWriter.writeUInt32(paramByteBuffer, localEntry.fragmentAbsoluteTime);
        IsoTypeWriter.writeUInt32(paramByteBuffer, localEntry.fragmentAbsoluteDuration);
      }
    }
  }
  
  protected long getContentSize()
  {
    int j = this.entries.size();
    if (getVersion() == 1) {}
    for (int i = 16;; i = 8) {
      return i * j + 5;
    }
  }
  
  public List<Entry> getEntries()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.entries;
  }
  
  public long getFragmentCount()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.entries.size();
  }
  
  public byte[] getUserType()
  {
    return new byte[] { -44, -128, 126, -14, -54, 57, 70, -107, -114, 84, 38, -53, -98, 70, -89, -97 };
  }
  
  public String toString()
  {
    Object localObject = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before((JoinPoint)localObject);
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("TfrfBox");
    ((StringBuilder)localObject).append("{entries=").append(this.entries);
    ((StringBuilder)localObject).append('}');
    return ((StringBuilder)localObject).toString();
  }
  
  public class Entry
  {
    long fragmentAbsoluteDuration;
    long fragmentAbsoluteTime;
    
    public Entry() {}
    
    public long getFragmentAbsoluteDuration()
    {
      return this.fragmentAbsoluteDuration;
    }
    
    public long getFragmentAbsoluteTime()
    {
      return this.fragmentAbsoluteTime;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Entry");
      localStringBuilder.append("{fragmentAbsoluteTime=").append(this.fragmentAbsoluteTime);
      localStringBuilder.append(", fragmentAbsoluteDuration=").append(this.fragmentAbsoluteDuration);
      localStringBuilder.append('}');
      return localStringBuilder.toString();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/piff/TfrfBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */