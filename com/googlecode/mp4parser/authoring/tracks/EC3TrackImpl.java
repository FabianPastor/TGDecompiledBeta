package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.CompositionTimeToSample.Entry;
import com.coremedia.iso.boxes.SampleDependencyTypeBox.Entry;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.boxes.EC3SpecificBox;
import com.googlecode.mp4parser.boxes.EC3SpecificBox.Entry;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.googlecode.mp4parser.util.CastUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class EC3TrackImpl
  extends AbstractTrack
{
  private static final long MAX_FRAMES_PER_MMAP = 20L;
  private List<BitStreamInfo> bitStreamInfos = new LinkedList();
  private int bitrate;
  private final DataSource dataSource;
  private long[] decodingTimes;
  private int frameSize;
  SampleDescriptionBox sampleDescriptionBox;
  private List<Sample> samples;
  TrackMetaData trackMetaData = new TrackMetaData();
  
  public EC3TrackImpl(DataSource paramDataSource)
    throws IOException
  {
    super(paramDataSource.toString());
    this.dataSource = paramDataSource;
    int i = 0;
    int j;
    if (i != 0)
    {
      if (this.bitStreamInfos.size() == 0) {
        throw new IOException();
      }
    }
    else
    {
      localObject1 = readVariables();
      if (localObject1 == null) {
        throw new IOException();
      }
      localObject2 = this.bitStreamInfos.iterator();
      for (j = i;; j = 1) {
        do
        {
          if (!((Iterator)localObject2).hasNext())
          {
            i = j;
            if (j != 0) {
              break;
            }
            this.bitStreamInfos.add(localObject1);
            i = j;
            break;
          }
          localObject3 = (BitStreamInfo)((Iterator)localObject2).next();
        } while ((((BitStreamInfo)localObject1).strmtyp == 1) || (((BitStreamInfo)localObject3).substreamid != ((BitStreamInfo)localObject1).substreamid));
      }
    }
    i = ((BitStreamInfo)this.bitStreamInfos.get(0)).samplerate;
    this.sampleDescriptionBox = new SampleDescriptionBox();
    Object localObject1 = new AudioSampleEntry("ec-3");
    ((AudioSampleEntry)localObject1).setChannelCount(2);
    ((AudioSampleEntry)localObject1).setSampleRate(i);
    ((AudioSampleEntry)localObject1).setDataReferenceIndex(1);
    ((AudioSampleEntry)localObject1).setSampleSize(16);
    Object localObject2 = new EC3SpecificBox();
    Object localObject3 = new int[this.bitStreamInfos.size()];
    int[] arrayOfInt = new int[this.bitStreamInfos.size()];
    Iterator localIterator = this.bitStreamInfos.iterator();
    if (!localIterator.hasNext()) {
      localIterator = this.bitStreamInfos.iterator();
    }
    for (;;)
    {
      if (!localIterator.hasNext())
      {
        ((EC3SpecificBox)localObject2).setDataRate(this.bitrate / 1000);
        ((AudioSampleEntry)localObject1).addBox((Box)localObject2);
        this.sampleDescriptionBox.addBox((Box)localObject1);
        this.trackMetaData.setCreationTime(new Date());
        this.trackMetaData.setModificationTime(new Date());
        this.trackMetaData.setTimescale(i);
        this.trackMetaData.setVolume(1.0F);
        paramDataSource.position(0L);
        this.samples = readSamples();
        this.decodingTimes = new long[this.samples.size()];
        Arrays.fill(this.decodingTimes, 1536L);
        return;
        localBitStreamInfo = (BitStreamInfo)localIterator.next();
        if (localBitStreamInfo.strmtyp != 1) {
          break;
        }
        j = localBitStreamInfo.substreamid;
        localObject3[j] += 1;
        arrayOfInt[localBitStreamInfo.substreamid] = (localBitStreamInfo.chanmap >> 6 & 0x100 | localBitStreamInfo.chanmap >> 5 & 0xFF);
        break;
      }
      BitStreamInfo localBitStreamInfo = (BitStreamInfo)localIterator.next();
      if (localBitStreamInfo.strmtyp != 1)
      {
        EC3SpecificBox.Entry localEntry = new EC3SpecificBox.Entry();
        localEntry.fscod = localBitStreamInfo.fscod;
        localEntry.bsid = localBitStreamInfo.bsid;
        localEntry.bsmod = localBitStreamInfo.bsmod;
        localEntry.acmod = localBitStreamInfo.acmod;
        localEntry.lfeon = localBitStreamInfo.lfeon;
        localEntry.reserved = 0;
        localEntry.num_dep_sub = localObject3[localBitStreamInfo.substreamid];
        localEntry.chan_loc = arrayOfInt[localBitStreamInfo.substreamid];
        localEntry.reserved2 = 0;
        ((EC3SpecificBox)localObject2).addEntry(localEntry);
      }
      this.bitrate += localBitStreamInfo.bitrate;
      this.frameSize += localBitStreamInfo.frameSize;
    }
  }
  
  private List<Sample> readSamples()
    throws IOException
  {
    int j = CastUtils.l2i((this.dataSource.size() - this.dataSource.position()) / this.frameSize);
    ArrayList localArrayList = new ArrayList(j);
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return localArrayList;
      }
      localArrayList.add(new Sample()
      {
        public ByteBuffer asByteBuffer()
        {
          try
          {
            ByteBuffer localByteBuffer = EC3TrackImpl.this.dataSource.map(this.val$start, EC3TrackImpl.this.frameSize);
            return localByteBuffer;
          }
          catch (IOException localIOException)
          {
            throw new RuntimeException(localIOException);
          }
        }
        
        public long getSize()
        {
          return EC3TrackImpl.this.frameSize;
        }
        
        public void writeTo(WritableByteChannel paramAnonymousWritableByteChannel)
          throws IOException
        {
          EC3TrackImpl.this.dataSource.transferTo(this.val$start, EC3TrackImpl.this.frameSize, paramAnonymousWritableByteChannel);
        }
      });
      i += 1;
    }
  }
  
  private BitStreamInfo readVariables()
    throws IOException
  {
    long l = this.dataSource.position();
    Object localObject = ByteBuffer.allocate(200);
    this.dataSource.read((ByteBuffer)localObject);
    ((ByteBuffer)localObject).rewind();
    localObject = new BitReaderBuffer((ByteBuffer)localObject);
    if (((BitReaderBuffer)localObject).readBits(16) != 2935) {
      return null;
    }
    BitStreamInfo localBitStreamInfo = new BitStreamInfo();
    localBitStreamInfo.strmtyp = ((BitReaderBuffer)localObject).readBits(2);
    localBitStreamInfo.substreamid = ((BitReaderBuffer)localObject).readBits(3);
    localBitStreamInfo.frameSize = ((((BitReaderBuffer)localObject).readBits(11) + 1) * 2);
    localBitStreamInfo.fscod = ((BitReaderBuffer)localObject).readBits(2);
    int j = -1;
    int k;
    int i;
    label176:
    int m;
    if (localBitStreamInfo.fscod == 3)
    {
      j = ((BitReaderBuffer)localObject).readBits(2);
      k = 3;
      i = 0;
      switch (k)
      {
      default: 
        localBitStreamInfo.frameSize *= 6 / i;
        localBitStreamInfo.acmod = ((BitReaderBuffer)localObject).readBits(3);
        localBitStreamInfo.lfeon = ((BitReaderBuffer)localObject).readBits(1);
        localBitStreamInfo.bsid = ((BitReaderBuffer)localObject).readBits(5);
        ((BitReaderBuffer)localObject).readBits(5);
        if (1 == ((BitReaderBuffer)localObject).readBits(1)) {
          ((BitReaderBuffer)localObject).readBits(8);
        }
        if (localBitStreamInfo.acmod == 0)
        {
          ((BitReaderBuffer)localObject).readBits(5);
          if (1 == ((BitReaderBuffer)localObject).readBits(1)) {
            ((BitReaderBuffer)localObject).readBits(8);
          }
        }
        if ((1 == localBitStreamInfo.strmtyp) && (1 == ((BitReaderBuffer)localObject).readBits(1))) {
          localBitStreamInfo.chanmap = ((BitReaderBuffer)localObject).readBits(16);
        }
        if (1 == ((BitReaderBuffer)localObject).readBits(1))
        {
          if (localBitStreamInfo.acmod > 2) {
            ((BitReaderBuffer)localObject).readBits(2);
          }
          if ((1 == (localBitStreamInfo.acmod & 0x1)) && (localBitStreamInfo.acmod > 2))
          {
            ((BitReaderBuffer)localObject).readBits(3);
            ((BitReaderBuffer)localObject).readBits(3);
          }
          if ((localBitStreamInfo.acmod & 0x4) > 0)
          {
            ((BitReaderBuffer)localObject).readBits(3);
            ((BitReaderBuffer)localObject).readBits(3);
          }
          if ((1 == localBitStreamInfo.lfeon) && (1 == ((BitReaderBuffer)localObject).readBits(1))) {
            ((BitReaderBuffer)localObject).readBits(5);
          }
          if (localBitStreamInfo.strmtyp == 0)
          {
            if (1 == ((BitReaderBuffer)localObject).readBits(1)) {
              ((BitReaderBuffer)localObject).readBits(6);
            }
            if ((localBitStreamInfo.acmod == 0) && (1 == ((BitReaderBuffer)localObject).readBits(1))) {
              ((BitReaderBuffer)localObject).readBits(6);
            }
            if (1 == ((BitReaderBuffer)localObject).readBits(1)) {
              ((BitReaderBuffer)localObject).readBits(6);
            }
            m = ((BitReaderBuffer)localObject).readBits(2);
            if (1 != m) {
              break;
            }
            ((BitReaderBuffer)localObject).readBits(5);
            label514:
            if (localBitStreamInfo.acmod < 2)
            {
              if (1 == ((BitReaderBuffer)localObject).readBits(1)) {
                ((BitReaderBuffer)localObject).readBits(14);
              }
              if ((localBitStreamInfo.acmod == 0) && (1 == ((BitReaderBuffer)localObject).readBits(1))) {
                ((BitReaderBuffer)localObject).readBits(14);
              }
              if (1 == ((BitReaderBuffer)localObject).readBits(1))
              {
                if (k != 0) {
                  break label987;
                }
                ((BitReaderBuffer)localObject).readBits(5);
              }
            }
          }
        }
        if (1 == ((BitReaderBuffer)localObject).readBits(1)) {
          localBitStreamInfo.bsmod = ((BitReaderBuffer)localObject).readBits(3);
        }
        switch (localBitStreamInfo.fscod)
        {
        }
        break;
      }
    }
    for (;;)
    {
      if (localBitStreamInfo.samplerate != 0) {
        break label1129;
      }
      return null;
      k = ((BitReaderBuffer)localObject).readBits(2);
      break;
      i = 1;
      break label176;
      i = 2;
      break label176;
      i = 3;
      break label176;
      i = 6;
      break label176;
      if (2 == m)
      {
        ((BitReaderBuffer)localObject).readBits(12);
        break label514;
      }
      if (3 != m) {
        break label514;
      }
      int n = ((BitReaderBuffer)localObject).readBits(5);
      if (1 == ((BitReaderBuffer)localObject).readBits(1))
      {
        ((BitReaderBuffer)localObject).readBits(5);
        if (1 == ((BitReaderBuffer)localObject).readBits(1)) {
          ((BitReaderBuffer)localObject).readBits(4);
        }
        if (1 == ((BitReaderBuffer)localObject).readBits(1)) {
          ((BitReaderBuffer)localObject).readBits(4);
        }
        if (1 == ((BitReaderBuffer)localObject).readBits(1)) {
          ((BitReaderBuffer)localObject).readBits(4);
        }
        if (1 == ((BitReaderBuffer)localObject).readBits(1)) {
          ((BitReaderBuffer)localObject).readBits(4);
        }
        if (1 == ((BitReaderBuffer)localObject).readBits(1)) {
          ((BitReaderBuffer)localObject).readBits(4);
        }
        if (1 == ((BitReaderBuffer)localObject).readBits(1)) {
          ((BitReaderBuffer)localObject).readBits(4);
        }
        if (1 == ((BitReaderBuffer)localObject).readBits(1)) {
          ((BitReaderBuffer)localObject).readBits(4);
        }
        if (1 == ((BitReaderBuffer)localObject).readBits(1))
        {
          if (1 == ((BitReaderBuffer)localObject).readBits(1)) {
            ((BitReaderBuffer)localObject).readBits(4);
          }
          if (1 == ((BitReaderBuffer)localObject).readBits(1)) {
            ((BitReaderBuffer)localObject).readBits(4);
          }
        }
      }
      if (1 == ((BitReaderBuffer)localObject).readBits(1))
      {
        ((BitReaderBuffer)localObject).readBits(5);
        if (1 == ((BitReaderBuffer)localObject).readBits(1))
        {
          ((BitReaderBuffer)localObject).readBits(7);
          if (1 == ((BitReaderBuffer)localObject).readBits(1)) {
            ((BitReaderBuffer)localObject).readBits(8);
          }
        }
      }
      m = 0;
      for (;;)
      {
        if (m >= n + 2)
        {
          ((BitReaderBuffer)localObject).byteSync();
          break;
        }
        ((BitReaderBuffer)localObject).readBits(8);
        m += 1;
      }
      label987:
      k = 0;
      while (k < i)
      {
        if (1 == ((BitReaderBuffer)localObject).readBits(1)) {
          ((BitReaderBuffer)localObject).readBits(5);
        }
        k += 1;
      }
      localBitStreamInfo.samplerate = 48000;
      continue;
      localBitStreamInfo.samplerate = 44100;
      continue;
      localBitStreamInfo.samplerate = 32000;
      continue;
      switch (j)
      {
      default: 
        break;
      case 0: 
        localBitStreamInfo.samplerate = 24000;
        break;
      case 1: 
        localBitStreamInfo.samplerate = 22050;
        break;
      case 2: 
        localBitStreamInfo.samplerate = 16000;
        break;
      case 3: 
        localBitStreamInfo.samplerate = 0;
      }
    }
    label1129:
    localBitStreamInfo.bitrate = ((int)(localBitStreamInfo.samplerate / 1536.0D * localBitStreamInfo.frameSize * 8.0D));
    this.dataSource.position(localBitStreamInfo.frameSize + l);
    return localBitStreamInfo;
  }
  
  public void close()
    throws IOException
  {
    this.dataSource.close();
  }
  
  public List<CompositionTimeToSample.Entry> getCompositionTimeEntries()
  {
    return null;
  }
  
  public String getHandler()
  {
    return "soun";
  }
  
  public List<SampleDependencyTypeBox.Entry> getSampleDependencies()
  {
    return null;
  }
  
  public SampleDescriptionBox getSampleDescriptionBox()
  {
    return this.sampleDescriptionBox;
  }
  
  public long[] getSampleDurations()
  {
    return this.decodingTimes;
  }
  
  public List<Sample> getSamples()
  {
    return this.samples;
  }
  
  public SubSampleInformationBox getSubsampleInformationBox()
  {
    return null;
  }
  
  public long[] getSyncSamples()
  {
    return null;
  }
  
  public TrackMetaData getTrackMetaData()
  {
    return this.trackMetaData;
  }
  
  public String toString()
  {
    return "EC3TrackImpl{bitrate=" + this.bitrate + ", bitStreamInfos=" + this.bitStreamInfos + '}';
  }
  
  public static class BitStreamInfo
    extends EC3SpecificBox.Entry
  {
    public int bitrate;
    public int chanmap;
    public int frameSize;
    public int samplerate;
    public int strmtyp;
    public int substreamid;
    
    public String toString()
    {
      return "BitStreamInfo{frameSize=" + this.frameSize + ", substreamid=" + this.substreamid + ", bitrate=" + this.bitrate + ", samplerate=" + this.samplerate + ", strmtyp=" + this.strmtyp + ", chanmap=" + this.chanmap + '}';
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/tracks/EC3TrackImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */