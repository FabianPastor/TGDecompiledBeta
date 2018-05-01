package com.coremedia.iso.boxes;

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

public class SampleDependencyTypeBox
  extends AbstractFullBox
{
  public static final String TYPE = "sdtp";
  private List<Entry> entries = new ArrayList();
  
  static {}
  
  public SampleDependencyTypeBox()
  {
    super("sdtp");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    for (;;)
    {
      if (paramByteBuffer.remaining() <= 0) {
        return;
      }
      this.entries.add(new Entry(IsoTypeReader.readUInt8(paramByteBuffer)));
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
      IsoTypeWriter.writeUInt8(paramByteBuffer, ((Entry)localIterator.next()).value);
    }
  }
  
  protected long getContentSize()
  {
    return this.entries.size() + 4;
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
    Object localObject = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before((JoinPoint)localObject);
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("SampleDependencyTypeBox");
    ((StringBuilder)localObject).append("{entries=").append(this.entries);
    ((StringBuilder)localObject).append('}');
    return ((StringBuilder)localObject).toString();
  }
  
  public static class Entry
  {
    private int value;
    
    public Entry(int paramInt)
    {
      this.value = paramInt;
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
          if (this.value != ((Entry)paramObject).value) {
            bool = false;
          }
        }
      }
    }
    
    public int getReserved()
    {
      return this.value >> 6 & 0x3;
    }
    
    public int getSampleDependsOn()
    {
      return this.value >> 4 & 0x3;
    }
    
    public int getSampleHasRedundancy()
    {
      return this.value & 0x3;
    }
    
    public int getSampleIsDependentOn()
    {
      return this.value >> 2 & 0x3;
    }
    
    public int hashCode()
    {
      return this.value;
    }
    
    public void setReserved(int paramInt)
    {
      this.value = ((paramInt & 0x3) << 6 | this.value & 0x3F);
    }
    
    public void setSampleDependsOn(int paramInt)
    {
      this.value = ((paramInt & 0x3) << 4 | this.value & 0xCF);
    }
    
    public void setSampleHasRedundancy(int paramInt)
    {
      this.value = (paramInt & 0x3 | this.value & 0xFC);
    }
    
    public void setSampleIsDependentOn(int paramInt)
    {
      this.value = ((paramInt & 0x3) << 2 | this.value & 0xF3);
    }
    
    public String toString()
    {
      return "Entry{reserved=" + getReserved() + ", sampleDependsOn=" + getSampleDependsOn() + ", sampleIsDependentOn=" + getSampleIsDependentOn() + ", sampleHasRedundancy=" + getSampleHasRedundancy() + '}';
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/SampleDependencyTypeBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */