package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.SampleImpl;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.boxes.mp4.ESDescriptorBox;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.DecoderConfigDescriptor;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ESDescriptor;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.SLConfigDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MP3TrackImpl
  extends AbstractTrack
{
  private static final int[] BIT_RATE;
  private static final int ES_OBJECT_TYPE_INDICATION = 107;
  private static final int ES_STREAM_TYPE = 5;
  private static final int MPEG_L3 = 1;
  private static final int MPEG_V1 = 3;
  private static final int SAMPLES_PER_FRAME = 1152;
  private static final int[] SAMPLE_RATE;
  long avgBitRate;
  private final DataSource dataSource;
  private long[] durations;
  MP3Header firstHeader;
  long maxBitRate;
  SampleDescriptionBox sampleDescriptionBox;
  private List<Sample> samples;
  TrackMetaData trackMetaData = new TrackMetaData();
  
  static
  {
    int[] arrayOfInt = new int[4];
    arrayOfInt[0] = 44100;
    arrayOfInt[1] = 48000;
    arrayOfInt[2] = 32000;
    SAMPLE_RATE = arrayOfInt;
    arrayOfInt = new int[16];
    arrayOfInt[1] = 32000;
    arrayOfInt[2] = 40000;
    arrayOfInt[3] = 48000;
    arrayOfInt[4] = 56000;
    arrayOfInt[5] = 64000;
    arrayOfInt[6] = 80000;
    arrayOfInt[7] = 96000;
    arrayOfInt[8] = 112000;
    arrayOfInt[9] = 128000;
    arrayOfInt[10] = 160000;
    arrayOfInt[11] = 192000;
    arrayOfInt[12] = 224000;
    arrayOfInt[13] = 256000;
    arrayOfInt[14] = 320000;
    BIT_RATE = arrayOfInt;
  }
  
  public MP3TrackImpl(DataSource paramDataSource)
    throws IOException
  {
    this(paramDataSource, "eng");
  }
  
  public MP3TrackImpl(DataSource paramDataSource, String paramString)
    throws IOException
  {
    super(paramDataSource.toString());
    this.dataSource = paramDataSource;
    this.samples = new LinkedList();
    this.firstHeader = readSamples(paramDataSource);
    double d1 = this.firstHeader.sampleRate / 1152.0D;
    double d2 = this.samples.size() / d1;
    long l1 = 0L;
    paramDataSource = new LinkedList();
    Object localObject1 = this.samples.iterator();
    long l2;
    label412:
    do
    {
      if (!((Iterator)localObject1).hasNext())
      {
        this.avgBitRate = ((int)(8L * l1 / d2));
        this.sampleDescriptionBox = new SampleDescriptionBox();
        paramDataSource = new AudioSampleEntry("mp4a");
        paramDataSource.setChannelCount(this.firstHeader.channelCount);
        paramDataSource.setSampleRate(this.firstHeader.sampleRate);
        paramDataSource.setDataReferenceIndex(1);
        paramDataSource.setSampleSize(16);
        localObject1 = new ESDescriptorBox();
        localObject2 = new ESDescriptor();
        ((ESDescriptor)localObject2).setEsId(0);
        Object localObject3 = new SLConfigDescriptor();
        ((SLConfigDescriptor)localObject3).setPredefined(2);
        ((ESDescriptor)localObject2).setSlConfigDescriptor((SLConfigDescriptor)localObject3);
        localObject3 = new DecoderConfigDescriptor();
        ((DecoderConfigDescriptor)localObject3).setObjectTypeIndication(107);
        ((DecoderConfigDescriptor)localObject3).setStreamType(5);
        ((DecoderConfigDescriptor)localObject3).setMaxBitRate(this.maxBitRate);
        ((DecoderConfigDescriptor)localObject3).setAvgBitRate(this.avgBitRate);
        ((ESDescriptor)localObject2).setDecoderConfigDescriptor((DecoderConfigDescriptor)localObject3);
        ((ESDescriptorBox)localObject1).setData(((ESDescriptor)localObject2).serialize());
        paramDataSource.addBox((Box)localObject1);
        this.sampleDescriptionBox.addBox(paramDataSource);
        this.trackMetaData.setCreationTime(new Date());
        this.trackMetaData.setModificationTime(new Date());
        this.trackMetaData.setLanguage(paramString);
        this.trackMetaData.setVolume(1.0F);
        this.trackMetaData.setTimescale(this.firstHeader.sampleRate);
        this.durations = new long[this.samples.size()];
        Arrays.fill(this.durations, 1152L);
        return;
      }
      i = (int)((Sample)((Iterator)localObject1).next()).getSize();
      l2 = l1 + i;
      paramDataSource.add(Integer.valueOf(i));
      if (paramDataSource.size() > d1) {
        break;
      }
      l1 = l2;
    } while (paramDataSource.size() != (int)d1);
    int i = 0;
    Object localObject2 = paramDataSource.iterator();
    for (;;)
    {
      if (!((Iterator)localObject2).hasNext())
      {
        double d3 = 8.0D * i / paramDataSource.size() * d1;
        l1 = l2;
        if (d3 <= this.maxBitRate) {
          break;
        }
        this.maxBitRate = ((int)d3);
        l1 = l2;
        break;
        paramDataSource.pop();
        break label412;
      }
      i += ((Integer)((Iterator)localObject2).next()).intValue();
    }
  }
  
  private MP3Header readMP3Header(DataSource paramDataSource)
    throws IOException
  {
    MP3Header localMP3Header = new MP3Header();
    ByteBuffer localByteBuffer = ByteBuffer.allocate(4);
    do
    {
      if (localByteBuffer.position() >= 4)
      {
        paramDataSource = new BitReaderBuffer((ByteBuffer)localByteBuffer.rewind());
        if (paramDataSource.readBits(11) == 2047) {
          break;
        }
        throw new IOException("Expected Start Word 0x7ff");
      }
    } while (paramDataSource.read(localByteBuffer) != -1);
    return null;
    localMP3Header.mpegVersion = paramDataSource.readBits(2);
    if (localMP3Header.mpegVersion != 3) {
      throw new IOException("Expected MPEG Version 1 (ISO/IEC 11172-3)");
    }
    localMP3Header.layer = paramDataSource.readBits(2);
    if (localMP3Header.layer != 1) {
      throw new IOException("Expected Layer III");
    }
    localMP3Header.protectionAbsent = paramDataSource.readBits(1);
    localMP3Header.bitRateIndex = paramDataSource.readBits(4);
    localMP3Header.bitRate = BIT_RATE[localMP3Header.bitRateIndex];
    if (localMP3Header.bitRate == 0) {
      throw new IOException("Unexpected (free/bad) bit rate");
    }
    localMP3Header.sampleFrequencyIndex = paramDataSource.readBits(2);
    localMP3Header.sampleRate = SAMPLE_RATE[localMP3Header.sampleFrequencyIndex];
    if (localMP3Header.sampleRate == 0) {
      throw new IOException("Unexpected (reserved) sample rate frequency");
    }
    localMP3Header.padding = paramDataSource.readBits(1);
    paramDataSource.readBits(1);
    localMP3Header.channelMode = paramDataSource.readBits(2);
    if (localMP3Header.channelMode == 3) {}
    for (int i = 1;; i = 2)
    {
      localMP3Header.channelCount = i;
      return localMP3Header;
    }
  }
  
  private MP3Header readSamples(DataSource paramDataSource)
    throws IOException
  {
    Object localObject1;
    for (Object localObject2 = null;; localObject2 = localObject1)
    {
      long l = paramDataSource.position();
      MP3Header localMP3Header = readMP3Header(paramDataSource);
      if (localMP3Header == null) {
        return (MP3Header)localObject2;
      }
      localObject1 = localObject2;
      if (localObject2 == null) {
        localObject1 = localMP3Header;
      }
      paramDataSource.position(l);
      localObject2 = ByteBuffer.allocate(localMP3Header.getFrameLength());
      paramDataSource.read((ByteBuffer)localObject2);
      ((ByteBuffer)localObject2).rewind();
      this.samples.add(new SampleImpl((ByteBuffer)localObject2));
    }
  }
  
  public void close()
    throws IOException
  {
    this.dataSource.close();
  }
  
  public String getHandler()
  {
    return "soun";
  }
  
  public SampleDescriptionBox getSampleDescriptionBox()
  {
    return this.sampleDescriptionBox;
  }
  
  public long[] getSampleDurations()
  {
    return this.durations;
  }
  
  public List<Sample> getSamples()
  {
    return this.samples;
  }
  
  public TrackMetaData getTrackMetaData()
  {
    return this.trackMetaData;
  }
  
  public String toString()
  {
    return "MP3TrackImpl";
  }
  
  class MP3Header
  {
    int bitRate;
    int bitRateIndex;
    int channelCount;
    int channelMode;
    int layer;
    int mpegVersion;
    int padding;
    int protectionAbsent;
    int sampleFrequencyIndex;
    int sampleRate;
    
    MP3Header() {}
    
    int getFrameLength()
    {
      return this.bitRate * 144 / this.sampleRate + this.padding;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/tracks/MP3TrackImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */