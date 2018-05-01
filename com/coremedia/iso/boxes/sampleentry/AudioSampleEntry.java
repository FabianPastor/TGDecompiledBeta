package com.coremedia.iso.boxes.sampleentry;

import com.coremedia.iso.BoxParser;
import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.util.CastUtils;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public final class AudioSampleEntry
  extends AbstractSampleEntry
{
  public static final String TYPE1 = "samr";
  public static final String TYPE10 = "mlpa";
  public static final String TYPE11 = "dtsl";
  public static final String TYPE12 = "dtsh";
  public static final String TYPE13 = "dtse";
  public static final String TYPE2 = "sawb";
  public static final String TYPE3 = "mp4a";
  public static final String TYPE4 = "drms";
  public static final String TYPE5 = "alac";
  public static final String TYPE7 = "owma";
  public static final String TYPE8 = "ac-3";
  public static final String TYPE9 = "ec-3";
  public static final String TYPE_ENCRYPTED = "enca";
  private long bytesPerFrame;
  private long bytesPerPacket;
  private long bytesPerSample;
  private int channelCount;
  private int compressionId;
  private int packetSize;
  private int reserved1;
  private long reserved2;
  private long sampleRate;
  private int sampleSize;
  private long samplesPerPacket;
  private int soundVersion;
  private byte[] soundVersion2Data;
  
  static
  {
    if (!AudioSampleEntry.class.desiredAssertionStatus()) {}
    for (boolean bool = true;; bool = false)
    {
      $assertionsDisabled = bool;
      return;
    }
  }
  
  public AudioSampleEntry(String paramString)
  {
    super(paramString);
  }
  
  public void getBox(WritableByteChannel paramWritableByteChannel)
    throws IOException
  {
    int i = 0;
    paramWritableByteChannel.write(getHeader());
    int j;
    ByteBuffer localByteBuffer;
    if (this.soundVersion == 1)
    {
      j = 16;
      if (this.soundVersion == 2) {
        i = 36;
      }
      localByteBuffer = ByteBuffer.allocate(j + 28 + i);
      localByteBuffer.position(6);
      IsoTypeWriter.writeUInt16(localByteBuffer, this.dataReferenceIndex);
      IsoTypeWriter.writeUInt16(localByteBuffer, this.soundVersion);
      IsoTypeWriter.writeUInt16(localByteBuffer, this.reserved1);
      IsoTypeWriter.writeUInt32(localByteBuffer, this.reserved2);
      IsoTypeWriter.writeUInt16(localByteBuffer, this.channelCount);
      IsoTypeWriter.writeUInt16(localByteBuffer, this.sampleSize);
      IsoTypeWriter.writeUInt16(localByteBuffer, this.compressionId);
      IsoTypeWriter.writeUInt16(localByteBuffer, this.packetSize);
      if (!this.type.equals("mlpa")) {
        break label271;
      }
      IsoTypeWriter.writeUInt32(localByteBuffer, getSampleRate());
    }
    for (;;)
    {
      if (this.soundVersion == 1)
      {
        IsoTypeWriter.writeUInt32(localByteBuffer, this.samplesPerPacket);
        IsoTypeWriter.writeUInt32(localByteBuffer, this.bytesPerPacket);
        IsoTypeWriter.writeUInt32(localByteBuffer, this.bytesPerFrame);
        IsoTypeWriter.writeUInt32(localByteBuffer, this.bytesPerSample);
      }
      if (this.soundVersion == 2)
      {
        IsoTypeWriter.writeUInt32(localByteBuffer, this.samplesPerPacket);
        IsoTypeWriter.writeUInt32(localByteBuffer, this.bytesPerPacket);
        IsoTypeWriter.writeUInt32(localByteBuffer, this.bytesPerFrame);
        IsoTypeWriter.writeUInt32(localByteBuffer, this.bytesPerSample);
        localByteBuffer.put(this.soundVersion2Data);
      }
      paramWritableByteChannel.write((ByteBuffer)localByteBuffer.rewind());
      writeContainer(paramWritableByteChannel);
      return;
      j = 0;
      break;
      label271:
      IsoTypeWriter.writeUInt32(localByteBuffer, getSampleRate() << 16);
    }
  }
  
  public long getBytesPerFrame()
  {
    return this.bytesPerFrame;
  }
  
  public long getBytesPerPacket()
  {
    return this.bytesPerPacket;
  }
  
  public long getBytesPerSample()
  {
    return this.bytesPerSample;
  }
  
  public int getChannelCount()
  {
    return this.channelCount;
  }
  
  public int getCompressionId()
  {
    return this.compressionId;
  }
  
  public int getPacketSize()
  {
    return this.packetSize;
  }
  
  public int getReserved1()
  {
    return this.reserved1;
  }
  
  public long getReserved2()
  {
    return this.reserved2;
  }
  
  public long getSampleRate()
  {
    return this.sampleRate;
  }
  
  public int getSampleSize()
  {
    return this.sampleSize;
  }
  
  public long getSamplesPerPacket()
  {
    return this.samplesPerPacket;
  }
  
  public long getSize()
  {
    int i = 16;
    int j = 0;
    long l;
    if (this.soundVersion == 1)
    {
      k = 16;
      if (this.soundVersion == 2) {
        j = 36;
      }
      l = k + 28 + j + getContainerSize();
      k = i;
      if (!this.largeBox) {
        if (8L + l < 4294967296L) {
          break label76;
        }
      }
    }
    label76:
    for (int k = i;; k = 8)
    {
      return l + k;
      k = 0;
      break;
    }
  }
  
  public int getSoundVersion()
  {
    return this.soundVersion;
  }
  
  public byte[] getSoundVersion2Data()
  {
    return this.soundVersion2Data;
  }
  
  public void parse(DataSource paramDataSource, ByteBuffer paramByteBuffer, final long paramLong, BoxParser paramBoxParser)
    throws IOException
  {
    paramByteBuffer = ByteBuffer.allocate(28);
    paramDataSource.read(paramByteBuffer);
    paramByteBuffer.position(6);
    this.dataReferenceIndex = IsoTypeReader.readUInt16(paramByteBuffer);
    this.soundVersion = IsoTypeReader.readUInt16(paramByteBuffer);
    this.reserved1 = IsoTypeReader.readUInt16(paramByteBuffer);
    this.reserved2 = IsoTypeReader.readUInt32(paramByteBuffer);
    this.channelCount = IsoTypeReader.readUInt16(paramByteBuffer);
    this.sampleSize = IsoTypeReader.readUInt16(paramByteBuffer);
    this.compressionId = IsoTypeReader.readUInt16(paramByteBuffer);
    this.packetSize = IsoTypeReader.readUInt16(paramByteBuffer);
    this.sampleRate = IsoTypeReader.readUInt32(paramByteBuffer);
    if (!this.type.equals("mlpa")) {
      this.sampleRate >>>= 16;
    }
    if (this.soundVersion == 1)
    {
      paramByteBuffer = ByteBuffer.allocate(16);
      paramDataSource.read(paramByteBuffer);
      paramByteBuffer.rewind();
      this.samplesPerPacket = IsoTypeReader.readUInt32(paramByteBuffer);
      this.bytesPerPacket = IsoTypeReader.readUInt32(paramByteBuffer);
      this.bytesPerFrame = IsoTypeReader.readUInt32(paramByteBuffer);
      this.bytesPerSample = IsoTypeReader.readUInt32(paramByteBuffer);
    }
    if (this.soundVersion == 2)
    {
      paramByteBuffer = ByteBuffer.allocate(36);
      paramDataSource.read(paramByteBuffer);
      paramByteBuffer.rewind();
      this.samplesPerPacket = IsoTypeReader.readUInt32(paramByteBuffer);
      this.bytesPerPacket = IsoTypeReader.readUInt32(paramByteBuffer);
      this.bytesPerFrame = IsoTypeReader.readUInt32(paramByteBuffer);
      this.bytesPerSample = IsoTypeReader.readUInt32(paramByteBuffer);
      this.soundVersion2Data = new byte[20];
      paramByteBuffer.get(this.soundVersion2Data);
    }
    long l;
    if ("owma".equals(this.type))
    {
      System.err.println("owma");
      if (this.soundVersion == 1)
      {
        i = 16;
        l = i;
        if (this.soundVersion != 2) {
          break label350;
        }
      }
      label350:
      for (i = 36;; i = 0)
      {
        paramLong = paramLong - 28L - l - i;
        paramByteBuffer = ByteBuffer.allocate(CastUtils.l2i(paramLong));
        paramDataSource.read(paramByteBuffer);
        addBox(new Box()
        {
          public void getBox(WritableByteChannel paramAnonymousWritableByteChannel)
            throws IOException
          {
            this.val$owmaSpecifics.rewind();
            paramAnonymousWritableByteChannel.write(this.val$owmaSpecifics);
          }
          
          public long getOffset()
          {
            return 0L;
          }
          
          public Container getParent()
          {
            return AudioSampleEntry.this;
          }
          
          public long getSize()
          {
            return paramLong;
          }
          
          public String getType()
          {
            return "----";
          }
          
          public void parse(DataSource paramAnonymousDataSource, ByteBuffer paramAnonymousByteBuffer, long paramAnonymousLong, BoxParser paramAnonymousBoxParser)
            throws IOException
          {
            throw new RuntimeException("NotImplemented");
          }
          
          public void setParent(Container paramAnonymousContainer)
          {
            if ((!AudioSampleEntry.$assertionsDisabled) && (paramAnonymousContainer != AudioSampleEntry.this)) {
              throw new AssertionError("you cannot diswown this special box");
            }
          }
        });
        return;
        i = 0;
        break;
      }
    }
    if (this.soundVersion == 1)
    {
      i = 16;
      label368:
      l = i;
      if (this.soundVersion != 2) {
        break label413;
      }
    }
    label413:
    for (int i = 36;; i = 0)
    {
      initContainer(paramDataSource, paramLong - 28L - l - i, paramBoxParser);
      break;
      i = 0;
      break label368;
    }
  }
  
  public void setBytesPerFrame(long paramLong)
  {
    this.bytesPerFrame = paramLong;
  }
  
  public void setBytesPerPacket(long paramLong)
  {
    this.bytesPerPacket = paramLong;
  }
  
  public void setBytesPerSample(long paramLong)
  {
    this.bytesPerSample = paramLong;
  }
  
  public void setChannelCount(int paramInt)
  {
    this.channelCount = paramInt;
  }
  
  public void setCompressionId(int paramInt)
  {
    this.compressionId = paramInt;
  }
  
  public void setPacketSize(int paramInt)
  {
    this.packetSize = paramInt;
  }
  
  public void setReserved1(int paramInt)
  {
    this.reserved1 = paramInt;
  }
  
  public void setReserved2(long paramLong)
  {
    this.reserved2 = paramLong;
  }
  
  public void setSampleRate(long paramLong)
  {
    this.sampleRate = paramLong;
  }
  
  public void setSampleSize(int paramInt)
  {
    this.sampleSize = paramInt;
  }
  
  public void setSamplesPerPacket(long paramLong)
  {
    this.samplesPerPacket = paramLong;
  }
  
  public void setSoundVersion(int paramInt)
  {
    this.soundVersion = paramInt;
  }
  
  public void setSoundVersion2Data(byte[] paramArrayOfByte)
  {
    this.soundVersion2Data = paramArrayOfByte;
  }
  
  public void setType(String paramString)
  {
    this.type = paramString;
  }
  
  public String toString()
  {
    return "AudioSampleEntry{bytesPerSample=" + this.bytesPerSample + ", bytesPerFrame=" + this.bytesPerFrame + ", bytesPerPacket=" + this.bytesPerPacket + ", samplesPerPacket=" + this.samplesPerPacket + ", packetSize=" + this.packetSize + ", compressionId=" + this.compressionId + ", soundVersion=" + this.soundVersion + ", sampleRate=" + this.sampleRate + ", sampleSize=" + this.sampleSize + ", channelCount=" + this.channelCount + ", boxes=" + getBoxes() + '}';
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/sampleentry/AudioSampleEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */