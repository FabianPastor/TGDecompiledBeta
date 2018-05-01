package com.coremedia.iso.boxes.dece;

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

public class TrickPlayBox
  extends AbstractFullBox
{
  public static final String TYPE = "trik";
  private List<Entry> entries = new ArrayList();
  
  static {}
  
  public TrickPlayBox()
  {
    super("trik");
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
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.entries;
  }
  
  public void setEntries(List<Entry> paramList)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this, paramList);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.entries = paramList;
  }
  
  public String toString()
  {
    Object localObject = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before((JoinPoint)localObject);
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("TrickPlayBox");
    ((StringBuilder)localObject).append("{entries=").append(this.entries);
    ((StringBuilder)localObject).append('}');
    return ((StringBuilder)localObject).toString();
  }
  
  public static class Entry
  {
    private int value;
    
    public Entry() {}
    
    public Entry(int paramInt)
    {
      this.value = paramInt;
    }
    
    public int getDependencyLevel()
    {
      return this.value & 0x3F;
    }
    
    public int getPicType()
    {
      return this.value >> 6 & 0x3;
    }
    
    public void setDependencyLevel(int paramInt)
    {
      this.value = (paramInt & 0x3F | this.value);
    }
    
    public void setPicType(int paramInt)
    {
      this.value &= 0x1F;
      this.value = ((paramInt & 0x3) << 6 | this.value);
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Entry");
      localStringBuilder.append("{picType=").append(getPicType());
      localStringBuilder.append(",dependencyLevel=").append(getDependencyLevel());
      localStringBuilder.append('}');
      return localStringBuilder.toString();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/dece/TrickPlayBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */