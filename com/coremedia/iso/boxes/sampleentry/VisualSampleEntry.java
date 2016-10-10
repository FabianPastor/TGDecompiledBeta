package com.coremedia.iso.boxes.sampleentry;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.Utf8;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.util.CastUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public final class VisualSampleEntry
  extends AbstractSampleEntry
  implements Container
{
  public static final String TYPE1 = "mp4v";
  public static final String TYPE2 = "s263";
  public static final String TYPE3 = "avc1";
  public static final String TYPE4 = "avc3";
  public static final String TYPE5 = "drmi";
  public static final String TYPE6 = "hvc1";
  public static final String TYPE7 = "hev1";
  public static final String TYPE_ENCRYPTED = "encv";
  private String compressorname = "";
  private int depth = 24;
  private int frameCount = 1;
  private int height;
  private double horizresolution = 72.0D;
  private long[] predefined = new long[3];
  private double vertresolution = 72.0D;
  private int width;
  
  static
  {
    if (!VisualSampleEntry.class.desiredAssertionStatus()) {}
    for (boolean bool = true;; bool = false)
    {
      $assertionsDisabled = bool;
      return;
    }
  }
  
  public VisualSampleEntry()
  {
    super("avc1");
  }
  
  public VisualSampleEntry(String paramString)
  {
    super(paramString);
  }
  
  public void getBox(WritableByteChannel paramWritableByteChannel)
    throws IOException
  {
    paramWritableByteChannel.write(getHeader());
    ByteBuffer localByteBuffer = ByteBuffer.allocate(78);
    localByteBuffer.position(6);
    IsoTypeWriter.writeUInt16(localByteBuffer, this.dataReferenceIndex);
    IsoTypeWriter.writeUInt16(localByteBuffer, 0);
    IsoTypeWriter.writeUInt16(localByteBuffer, 0);
    IsoTypeWriter.writeUInt32(localByteBuffer, this.predefined[0]);
    IsoTypeWriter.writeUInt32(localByteBuffer, this.predefined[1]);
    IsoTypeWriter.writeUInt32(localByteBuffer, this.predefined[2]);
    IsoTypeWriter.writeUInt16(localByteBuffer, getWidth());
    IsoTypeWriter.writeUInt16(localByteBuffer, getHeight());
    IsoTypeWriter.writeFixedPoint1616(localByteBuffer, getHorizresolution());
    IsoTypeWriter.writeFixedPoint1616(localByteBuffer, getVertresolution());
    IsoTypeWriter.writeUInt32(localByteBuffer, 0L);
    IsoTypeWriter.writeUInt16(localByteBuffer, getFrameCount());
    IsoTypeWriter.writeUInt8(localByteBuffer, Utf8.utf8StringLengthInBytes(getCompressorname()));
    localByteBuffer.put(Utf8.convert(getCompressorname()));
    int i = Utf8.utf8StringLengthInBytes(getCompressorname());
    for (;;)
    {
      if (i >= 31)
      {
        IsoTypeWriter.writeUInt16(localByteBuffer, getDepth());
        IsoTypeWriter.writeUInt16(localByteBuffer, 65535);
        paramWritableByteChannel.write((ByteBuffer)localByteBuffer.rewind());
        writeContainer(paramWritableByteChannel);
        return;
      }
      i += 1;
      localByteBuffer.put((byte)0);
    }
  }
  
  public String getCompressorname()
  {
    return this.compressorname;
  }
  
  public int getDepth()
  {
    return this.depth;
  }
  
  public int getFrameCount()
  {
    return this.frameCount;
  }
  
  public int getHeight()
  {
    return this.height;
  }
  
  public double getHorizresolution()
  {
    return this.horizresolution;
  }
  
  public long getSize()
  {
    long l = getContainerSize();
    if ((this.largeBox) || (l + 78L + 8L >= 4294967296L)) {}
    for (int i = 16;; i = 8) {
      return i + (l + 78L);
    }
  }
  
  public double getVertresolution()
  {
    return this.vertresolution;
  }
  
  public int getWidth()
  {
    return this.width;
  }
  
  public void parse(DataSource paramDataSource, ByteBuffer paramByteBuffer, long paramLong, BoxParser paramBoxParser)
    throws IOException
  {
    long l1 = paramDataSource.position();
    paramByteBuffer = ByteBuffer.allocate(78);
    paramDataSource.read(paramByteBuffer);
    paramByteBuffer.position(6);
    this.dataReferenceIndex = IsoTypeReader.readUInt16(paramByteBuffer);
    long l2 = IsoTypeReader.readUInt16(paramByteBuffer);
    assert (0L == l2) : "reserved byte not 0";
    l2 = IsoTypeReader.readUInt16(paramByteBuffer);
    assert (0L == l2) : "reserved byte not 0";
    this.predefined[0] = IsoTypeReader.readUInt32(paramByteBuffer);
    this.predefined[1] = IsoTypeReader.readUInt32(paramByteBuffer);
    this.predefined[2] = IsoTypeReader.readUInt32(paramByteBuffer);
    this.width = IsoTypeReader.readUInt16(paramByteBuffer);
    this.height = IsoTypeReader.readUInt16(paramByteBuffer);
    this.horizresolution = IsoTypeReader.readFixedPoint1616(paramByteBuffer);
    this.vertresolution = IsoTypeReader.readFixedPoint1616(paramByteBuffer);
    l2 = IsoTypeReader.readUInt32(paramByteBuffer);
    assert (0L == l2) : "reserved byte not 0";
    this.frameCount = IsoTypeReader.readUInt16(paramByteBuffer);
    int j = IsoTypeReader.readUInt8(paramByteBuffer);
    int i = j;
    if (j > 31) {
      i = 31;
    }
    byte[] arrayOfByte = new byte[i];
    paramByteBuffer.get(arrayOfByte);
    this.compressorname = Utf8.convert(arrayOfByte);
    if (i < 31) {
      paramByteBuffer.get(new byte[31 - i]);
    }
    this.depth = IsoTypeReader.readUInt16(paramByteBuffer);
    l2 = IsoTypeReader.readUInt16(paramByteBuffer);
    assert (65535L == l2);
    initContainer(new DataSource()
    {
      public void close()
        throws IOException
      {
        this.val$dataSource.close();
      }
      
      public ByteBuffer map(long paramAnonymousLong1, long paramAnonymousLong2)
        throws IOException
      {
        return this.val$dataSource.map(paramAnonymousLong1, paramAnonymousLong2);
      }
      
      public long position()
        throws IOException
      {
        return this.val$dataSource.position();
      }
      
      public void position(long paramAnonymousLong)
        throws IOException
      {
        this.val$dataSource.position(paramAnonymousLong);
      }
      
      public int read(ByteBuffer paramAnonymousByteBuffer)
        throws IOException
      {
        if (this.val$endPosition == this.val$dataSource.position()) {
          return -1;
        }
        if (paramAnonymousByteBuffer.remaining() > this.val$endPosition - this.val$dataSource.position())
        {
          ByteBuffer localByteBuffer = ByteBuffer.allocate(CastUtils.l2i(this.val$endPosition - this.val$dataSource.position()));
          this.val$dataSource.read(localByteBuffer);
          paramAnonymousByteBuffer.put((ByteBuffer)localByteBuffer.rewind());
          return localByteBuffer.capacity();
        }
        return this.val$dataSource.read(paramAnonymousByteBuffer);
      }
      
      public long size()
        throws IOException
      {
        return this.val$endPosition;
      }
      
      public long transferTo(long paramAnonymousLong1, long paramAnonymousLong2, WritableByteChannel paramAnonymousWritableByteChannel)
        throws IOException
      {
        return this.val$dataSource.transferTo(paramAnonymousLong1, paramAnonymousLong2, paramAnonymousWritableByteChannel);
      }
    }, paramLong - 78L, paramBoxParser);
  }
  
  public void setCompressorname(String paramString)
  {
    this.compressorname = paramString;
  }
  
  public void setDepth(int paramInt)
  {
    this.depth = paramInt;
  }
  
  public void setFrameCount(int paramInt)
  {
    this.frameCount = paramInt;
  }
  
  public void setHeight(int paramInt)
  {
    this.height = paramInt;
  }
  
  public void setHorizresolution(double paramDouble)
  {
    this.horizresolution = paramDouble;
  }
  
  public void setType(String paramString)
  {
    this.type = paramString;
  }
  
  public void setVertresolution(double paramDouble)
  {
    this.vertresolution = paramDouble;
  }
  
  public void setWidth(int paramInt)
  {
    this.width = paramInt;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/sampleentry/VisualSampleEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */