package com.googlecode.mp4parser.boxes.threegpp26245;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.reflect.Factory;

public class FontTableBox
  extends AbstractBox
{
  public static final String TYPE = "ftab";
  List<FontRecord> entries = new LinkedList();
  
  static {}
  
  public FontTableBox()
  {
    super("ftab");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    int j = IsoTypeReader.readUInt16(paramByteBuffer);
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return;
      }
      FontRecord localFontRecord = new FontRecord();
      localFontRecord.parse(paramByteBuffer);
      this.entries.add(localFontRecord);
      i += 1;
    }
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.entries.size());
    Iterator localIterator = this.entries.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return;
      }
      ((FontRecord)localIterator.next()).getContent(paramByteBuffer);
    }
  }
  
  protected long getContentSize()
  {
    int i = 2;
    Iterator localIterator = this.entries.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return i;
      }
      i += ((FontRecord)localIterator.next()).getSize();
    }
  }
  
  public List<FontRecord> getEntries()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.entries;
  }
  
  public void setEntries(List<FontRecord> paramList)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, paramList);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.entries = paramList;
  }
  
  public static class FontRecord
  {
    int fontId;
    String fontname;
    
    public FontRecord() {}
    
    public FontRecord(int paramInt, String paramString)
    {
      this.fontId = paramInt;
      this.fontname = paramString;
    }
    
    public void getContent(ByteBuffer paramByteBuffer)
    {
      IsoTypeWriter.writeUInt16(paramByteBuffer, this.fontId);
      IsoTypeWriter.writeUInt8(paramByteBuffer, this.fontname.length());
      paramByteBuffer.put(Utf8.convert(this.fontname));
    }
    
    public int getSize()
    {
      return Utf8.utf8StringLengthInBytes(this.fontname) + 3;
    }
    
    public void parse(ByteBuffer paramByteBuffer)
    {
      this.fontId = IsoTypeReader.readUInt16(paramByteBuffer);
      this.fontname = IsoTypeReader.readString(paramByteBuffer, IsoTypeReader.readUInt8(paramByteBuffer));
    }
    
    public String toString()
    {
      return "FontRecord{fontId=" + this.fontId + ", fontname='" + this.fontname + '\'' + '}';
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/threegpp26245/FontTableBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */