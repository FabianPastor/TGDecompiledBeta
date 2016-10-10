package com.googlecode.mp4parser.contentprotection;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.boxes.piff.ProtectionSpecificHeader;
import java.io.UnsupportedEncodingException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayReadyHeader
  extends ProtectionSpecificHeader
{
  public static UUID PROTECTION_SYSTEM_ID = UUID.fromString("9A04F079-9840-4286-AB92-E65BE0885F95");
  private long length;
  private List<PlayReadyRecord> records;
  
  static
  {
    ProtectionSpecificHeader.uuidRegistry.put(PROTECTION_SYSTEM_ID, PlayReadyHeader.class);
  }
  
  public ByteBuffer getData()
  {
    int i = 6;
    Object localObject = this.records.iterator();
    Iterator localIterator;
    if (!((Iterator)localObject).hasNext())
    {
      localObject = ByteBuffer.allocate(i);
      IsoTypeWriter.writeUInt32BE((ByteBuffer)localObject, i);
      IsoTypeWriter.writeUInt16BE((ByteBuffer)localObject, this.records.size());
      localIterator = this.records.iterator();
    }
    for (;;)
    {
      if (!localIterator.hasNext())
      {
        return (ByteBuffer)localObject;
        i = i + 4 + ((PlayReadyRecord)((Iterator)localObject).next()).getValue().rewind().limit();
        break;
      }
      PlayReadyRecord localPlayReadyRecord = (PlayReadyRecord)localIterator.next();
      IsoTypeWriter.writeUInt16BE((ByteBuffer)localObject, localPlayReadyRecord.type);
      IsoTypeWriter.writeUInt16BE((ByteBuffer)localObject, localPlayReadyRecord.getValue().limit());
      ((ByteBuffer)localObject).put(localPlayReadyRecord.getValue());
    }
  }
  
  public List<PlayReadyRecord> getRecords()
  {
    return Collections.unmodifiableList(this.records);
  }
  
  public UUID getSystemId()
  {
    return PROTECTION_SYSTEM_ID;
  }
  
  public void parse(ByteBuffer paramByteBuffer)
  {
    this.length = IsoTypeReader.readUInt32BE(paramByteBuffer);
    this.records = PlayReadyRecord.createFor(paramByteBuffer, IsoTypeReader.readUInt16BE(paramByteBuffer));
  }
  
  public void setRecords(List<PlayReadyRecord> paramList)
  {
    this.records = paramList;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("PlayReadyHeader");
    localStringBuilder.append("{length=").append(this.length);
    localStringBuilder.append(", recordCount=").append(this.records.size());
    localStringBuilder.append(", records=").append(this.records);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
  
  public static abstract class PlayReadyRecord
  {
    int type;
    
    public PlayReadyRecord(int paramInt)
    {
      this.type = paramInt;
    }
    
    public static List<PlayReadyRecord> createFor(ByteBuffer paramByteBuffer, int paramInt)
    {
      ArrayList localArrayList = new ArrayList(paramInt);
      int i = 0;
      if (i >= paramInt) {
        return localArrayList;
      }
      int j = IsoTypeReader.readUInt16BE(paramByteBuffer);
      int k = IsoTypeReader.readUInt16BE(paramByteBuffer);
      Object localObject;
      switch (j)
      {
      default: 
        localObject = new DefaulPlayReadyRecord(j);
      }
      for (;;)
      {
        ((PlayReadyRecord)localObject).parse((ByteBuffer)paramByteBuffer.slice().limit(k));
        paramByteBuffer.position(paramByteBuffer.position() + k);
        localArrayList.add(localObject);
        i += 1;
        break;
        localObject = new RMHeader();
        continue;
        localObject = new DefaulPlayReadyRecord(2);
        continue;
        localObject = new EmeddedLicenseStore();
      }
    }
    
    public abstract ByteBuffer getValue();
    
    public abstract void parse(ByteBuffer paramByteBuffer);
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("PlayReadyRecord");
      localStringBuilder.append("{type=").append(this.type);
      localStringBuilder.append(", length=").append(getValue().limit());
      localStringBuilder.append('}');
      return localStringBuilder.toString();
    }
    
    public static class DefaulPlayReadyRecord
      extends PlayReadyHeader.PlayReadyRecord
    {
      ByteBuffer value;
      
      public DefaulPlayReadyRecord(int paramInt)
      {
        super();
      }
      
      public ByteBuffer getValue()
      {
        return this.value;
      }
      
      public void parse(ByteBuffer paramByteBuffer)
      {
        this.value = paramByteBuffer.duplicate();
      }
    }
    
    public static class EmeddedLicenseStore
      extends PlayReadyHeader.PlayReadyRecord
    {
      ByteBuffer value;
      
      public EmeddedLicenseStore()
      {
        super();
      }
      
      public ByteBuffer getValue()
      {
        return this.value;
      }
      
      public void parse(ByteBuffer paramByteBuffer)
      {
        this.value = paramByteBuffer.duplicate();
      }
      
      public String toString()
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("EmeddedLicenseStore");
        localStringBuilder.append("{length=").append(getValue().limit());
        localStringBuilder.append('}');
        return localStringBuilder.toString();
      }
    }
    
    public static class RMHeader
      extends PlayReadyHeader.PlayReadyRecord
    {
      String header;
      
      public RMHeader()
      {
        super();
      }
      
      public String getHeader()
      {
        return this.header;
      }
      
      public ByteBuffer getValue()
      {
        try
        {
          byte[] arrayOfByte = this.header.getBytes("UTF-16LE");
          return ByteBuffer.wrap(arrayOfByte);
        }
        catch (UnsupportedEncodingException localUnsupportedEncodingException)
        {
          throw new RuntimeException(localUnsupportedEncodingException);
        }
      }
      
      public void parse(ByteBuffer paramByteBuffer)
      {
        try
        {
          byte[] arrayOfByte = new byte[paramByteBuffer.slice().limit()];
          paramByteBuffer.get(arrayOfByte);
          this.header = new String(arrayOfByte, "UTF-16LE");
          return;
        }
        catch (UnsupportedEncodingException paramByteBuffer)
        {
          throw new RuntimeException(paramByteBuffer);
        }
      }
      
      public void setHeader(String paramString)
      {
        this.header = paramString;
      }
      
      public String toString()
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("RMHeader");
        localStringBuilder.append("{length=").append(getValue().limit());
        localStringBuilder.append(", header='").append(this.header).append('\'');
        localStringBuilder.append('}');
        return localStringBuilder.toString();
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/contentprotection/PlayReadyHeader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */