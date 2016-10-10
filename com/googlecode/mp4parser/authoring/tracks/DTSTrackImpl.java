package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.boxes.CompositionTimeToSample.Entry;
import com.coremedia.iso.boxes.SampleDependencyTypeBox.Entry;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.boxes.DTSSpecificBox;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DTSTrackImpl
  extends AbstractTrack
{
  private static final int BUFFER = 67108864;
  int bcCoreBitRate = 0;
  int bcCoreChannelMask = 0;
  int bcCoreMaxSampleRate = 0;
  int bitrate;
  int channelCount;
  int channelMask = 0;
  int codecDelayAtMaxFs = 0;
  int coreBitRate = 0;
  int coreChannelMask = 0;
  int coreFramePayloadInBytes = 0;
  int coreMaxSampleRate = 0;
  boolean coreSubStreamPresent = false;
  private int dataOffset = 0;
  private DataSource dataSource;
  DTSSpecificBox ddts = new DTSSpecificBox();
  int extAvgBitrate = 0;
  int extFramePayloadInBytes = 0;
  int extPeakBitrate = 0;
  int extSmoothBuffSize = 0;
  boolean extensionSubStreamPresent = false;
  int frameSize = 0;
  boolean isVBR = false;
  private String lang = "eng";
  int lbrCodingPresent = 0;
  int lsbTrimPercent = 0;
  int maxSampleRate = 0;
  int numExtSubStreams = 0;
  int numFramesTotal = 0;
  int numSamplesOrigAudioAtMaxFs = 0;
  SampleDescriptionBox sampleDescriptionBox;
  private long[] sampleDurations;
  int sampleSize;
  int samplerate;
  private List<Sample> samples;
  int samplesPerFrame;
  int samplesPerFrameAtMaxFs = 0;
  TrackMetaData trackMetaData = new TrackMetaData();
  String type = "none";
  
  public DTSTrackImpl(DataSource paramDataSource)
    throws IOException
  {
    super(paramDataSource.toString());
    this.dataSource = paramDataSource;
    parse();
  }
  
  public DTSTrackImpl(DataSource paramDataSource, String paramString)
    throws IOException
  {
    super(paramDataSource.toString());
    this.lang = paramString;
    this.dataSource = paramDataSource;
    parse();
  }
  
  private List<Sample> generateSamples(DataSource paramDataSource, int paramInt1, long paramLong, int paramInt2)
    throws IOException
  {
    paramDataSource = new LookAhead(paramDataSource, paramInt1, paramLong, paramInt2);
    ArrayList localArrayList = new ArrayList();
    for (;;)
    {
      final ByteBuffer localByteBuffer = paramDataSource.findNextStart();
      if (localByteBuffer == null)
      {
        System.err.println("all samples found");
        return localArrayList;
      }
      localArrayList.add(new Sample()
      {
        public ByteBuffer asByteBuffer()
        {
          return localByteBuffer;
        }
        
        public long getSize()
        {
          return localByteBuffer.rewind().remaining();
        }
        
        public void writeTo(WritableByteChannel paramAnonymousWritableByteChannel)
          throws IOException
        {
          paramAnonymousWritableByteChannel.write((ByteBuffer)localByteBuffer.rewind());
        }
      });
    }
  }
  
  private int getBitRate(int paramInt)
    throws IOException
  {
    switch (paramInt)
    {
    default: 
      throw new IOException("Unknown bitrate value");
    case 0: 
      return 32;
    case 1: 
      return 56;
    case 2: 
      return 64;
    case 3: 
      return 96;
    case 4: 
      return 112;
    case 5: 
      return 128;
    case 6: 
      return 192;
    case 7: 
      return 224;
    case 8: 
      return 256;
    case 9: 
      return 320;
    case 10: 
      return 384;
    case 11: 
      return 448;
    case 12: 
      return 512;
    case 13: 
      return 576;
    case 14: 
      return 640;
    case 15: 
      return 768;
    case 16: 
      return 960;
    case 17: 
      return 1024;
    case 18: 
      return 1152;
    case 19: 
      return 1280;
    case 20: 
      return 1344;
    case 21: 
      return 1408;
    case 22: 
      return 1411;
    case 23: 
      return 1472;
    case 24: 
      return 1536;
    }
    return -1;
  }
  
  private int getSampleRate(int paramInt)
    throws IOException
  {
    switch (paramInt)
    {
    case 4: 
    case 5: 
    case 9: 
    case 10: 
    default: 
      throw new IOException("Unknown Sample Rate");
    case 1: 
      return 8000;
    case 2: 
      return 16000;
    case 3: 
      return 32000;
    case 6: 
      return 11025;
    case 7: 
      return 22050;
    case 8: 
      return 44100;
    case 11: 
      return 12000;
    case 12: 
      return 24000;
    }
    return 48000;
  }
  
  private void parse()
    throws IOException
  {
    if (!readVariables()) {
      throw new IOException();
    }
    this.sampleDescriptionBox = new SampleDescriptionBox();
    AudioSampleEntry localAudioSampleEntry = new AudioSampleEntry(this.type);
    localAudioSampleEntry.setChannelCount(this.channelCount);
    localAudioSampleEntry.setSampleRate(this.samplerate);
    localAudioSampleEntry.setDataReferenceIndex(1);
    localAudioSampleEntry.setSampleSize(16);
    localAudioSampleEntry.addBox(this.ddts);
    this.sampleDescriptionBox.addBox(localAudioSampleEntry);
    this.trackMetaData.setCreationTime(new Date());
    this.trackMetaData.setModificationTime(new Date());
    this.trackMetaData.setLanguage(this.lang);
    this.trackMetaData.setTimescale(this.samplerate);
  }
  
  private boolean parseAuprhdr(int paramInt, ByteBuffer paramByteBuffer)
  {
    paramByteBuffer.get();
    int k = paramByteBuffer.getShort();
    this.maxSampleRate = (paramByteBuffer.get() << 16 | paramByteBuffer.getShort() & 0xFFFF);
    this.numFramesTotal = paramByteBuffer.getInt();
    this.samplesPerFrameAtMaxFs = paramByteBuffer.getShort();
    this.numSamplesOrigAudioAtMaxFs = (paramByteBuffer.get() << 32 | paramByteBuffer.getInt() & 0xFFFF);
    this.channelMask = paramByteBuffer.getShort();
    this.codecDelayAtMaxFs = paramByteBuffer.getShort();
    int j = 21;
    if ((k & 0x3) == 3)
    {
      this.bcCoreMaxSampleRate = (paramByteBuffer.get() << 16 | paramByteBuffer.getShort() & 0xFFFF);
      this.bcCoreBitRate = paramByteBuffer.getShort();
      this.bcCoreChannelMask = paramByteBuffer.getShort();
      j = 21 + 7;
    }
    int i = j;
    if ((k & 0x4) > 0)
    {
      this.lsbTrimPercent = paramByteBuffer.get();
      i = j + 1;
    }
    j = i;
    if ((k & 0x8) > 0)
    {
      this.lbrCodingPresent = 1;
      j = i;
    }
    for (;;)
    {
      if (j >= paramInt) {
        return true;
      }
      paramByteBuffer.get();
      j += 1;
    }
  }
  
  private boolean parseCoressmd(int paramInt, ByteBuffer paramByteBuffer)
  {
    this.coreMaxSampleRate = (paramByteBuffer.get() << 16 | 0xFFFF & paramByteBuffer.getShort());
    this.coreBitRate = paramByteBuffer.getShort();
    this.coreChannelMask = paramByteBuffer.getShort();
    this.coreFramePayloadInBytes = paramByteBuffer.getInt();
    int i = 11;
    for (;;)
    {
      if (i >= paramInt) {
        return true;
      }
      paramByteBuffer.get();
      i += 1;
    }
  }
  
  private void parseDtshdhdr(int paramInt, ByteBuffer paramByteBuffer)
  {
    paramByteBuffer.getInt();
    paramByteBuffer.get();
    paramByteBuffer.getInt();
    paramByteBuffer.get();
    int i = paramByteBuffer.getShort();
    paramByteBuffer.get();
    this.numExtSubStreams = paramByteBuffer.get();
    if ((i & 0x1) == 1) {
      this.isVBR = true;
    }
    if ((i & 0x8) == 8) {
      this.coreSubStreamPresent = true;
    }
    if ((i & 0x10) == 16)
    {
      this.extensionSubStreamPresent = true;
      this.numExtSubStreams += 1;
      i = 14;
    }
    for (;;)
    {
      if (i >= paramInt)
      {
        return;
        this.numExtSubStreams = 0;
        break;
      }
      paramByteBuffer.get();
      i += 1;
    }
  }
  
  private boolean parseExtssmd(int paramInt, ByteBuffer paramByteBuffer)
  {
    this.extAvgBitrate = (paramByteBuffer.get() << 16 | paramByteBuffer.getShort() & 0xFFFF);
    int i;
    if (this.isVBR)
    {
      this.extPeakBitrate = (paramByteBuffer.get() << 16 | paramByteBuffer.getShort() & 0xFFFF);
      this.extSmoothBuffSize = paramByteBuffer.getShort();
      i = 3 + 5;
    }
    for (;;)
    {
      if (i >= paramInt)
      {
        return true;
        this.extFramePayloadInBytes = paramByteBuffer.getInt();
        i = 3 + 4;
      }
      else
      {
        paramByteBuffer.get();
        i += 1;
      }
    }
  }
  
  private boolean readVariables()
    throws IOException
  {
    ByteBuffer localByteBuffer = this.dataSource.map(0L, 25000L);
    int j = localByteBuffer.getInt();
    int k = localByteBuffer.getInt();
    if (j == 1146377032)
    {
      i = k;
      if (k == 1145586770) {}
    }
    else
    {
      throw new IOException("data does not start with 'DTSHDHDR' as required for a DTS-HD file");
    }
    while (((j != 1398035021) || (i != 1145132097)) && (localByteBuffer.remaining() > 100))
    {
      k = (int)localByteBuffer.getLong();
      if ((j != 1146377032) || (i != 1145586770)) {
        break;
      }
      parseDtshdhdr(k, localByteBuffer);
      j = localByteBuffer.getInt();
      i = localByteBuffer.getInt();
    }
    long l = localByteBuffer.getLong();
    this.dataOffset = localByteBuffer.position();
    k = -1;
    int i7 = 0;
    int i8 = 0;
    int m = -1;
    int i9 = -1;
    int i6 = 0;
    int i1 = 0;
    int i3 = 0;
    int i2 = 0;
    int i5 = 0;
    int n = 0;
    int i4 = 0;
    int i = 0;
    label171:
    if (i != 0)
    {
      j = -1;
      switch (this.samplesPerFrame)
      {
      }
    }
    int i10;
    for (;;)
    {
      if (j != -1) {
        break label1365;
      }
      return false;
      if ((j == 1129271877) && (i == 1397968196))
      {
        if (parseCoressmd(k, localByteBuffer)) {
          break;
        }
        return false;
      }
      if ((j == 1096110162) && (i == 759710802))
      {
        if (parseAuprhdr(k, localByteBuffer)) {
          break;
        }
        return false;
      }
      if ((j == 1163416659) && (i == 1398754628))
      {
        if (parseExtssmd(k, localByteBuffer)) {
          break;
        }
        return false;
      }
      i = 0;
      while (i < k)
      {
        localByteBuffer.get();
        i += 1;
      }
      int i18 = localByteBuffer.position();
      j = localByteBuffer.getInt();
      BitReaderBuffer localBitReaderBuffer;
      if (j == 2147385345)
      {
        if (m == 1)
        {
          i = 1;
          break label171;
        }
        m = 1;
        localBitReaderBuffer = new BitReaderBuffer(localByteBuffer);
        j = localBitReaderBuffer.readBits(1);
        k = localBitReaderBuffer.readBits(5);
        i10 = localBitReaderBuffer.readBits(1);
        if ((j != 1) || (k != 31) || (i10 != 0)) {
          return false;
        }
        this.samplesPerFrame = ((localBitReaderBuffer.readBits(7) + 1) * 32);
        j = localBitReaderBuffer.readBits(14);
        this.frameSize += j + 1;
        k = localBitReaderBuffer.readBits(6);
        this.samplerate = getSampleRate(localBitReaderBuffer.readBits(4));
        this.bitrate = getBitRate(localBitReaderBuffer.readBits(5));
        if (localBitReaderBuffer.readBits(1) != 0) {
          return false;
        }
        localBitReaderBuffer.readBits(1);
        localBitReaderBuffer.readBits(1);
        localBitReaderBuffer.readBits(1);
        localBitReaderBuffer.readBits(1);
        i7 = localBitReaderBuffer.readBits(3);
        i8 = localBitReaderBuffer.readBits(1);
        localBitReaderBuffer.readBits(1);
        localBitReaderBuffer.readBits(2);
        localBitReaderBuffer.readBits(1);
        if (i10 == 1) {
          localBitReaderBuffer.readBits(16);
        }
        localBitReaderBuffer.readBits(1);
        i10 = localBitReaderBuffer.readBits(4);
        localBitReaderBuffer.readBits(2);
        switch (localBitReaderBuffer.readBits(3))
        {
        case 4: 
        default: 
          return false;
        case 0: 
        case 1: 
          this.sampleSize = 16;
          label656:
          localBitReaderBuffer.readBits(1);
          localBitReaderBuffer.readBits(1);
          switch (i10)
          {
          default: 
            localBitReaderBuffer.readBits(4);
          }
          break;
        }
        for (;;)
        {
          localByteBuffer.position(i18 + j + 1);
          break;
          this.sampleSize = 20;
          break label656;
          this.sampleSize = 24;
          break label656;
          i10 = -(localBitReaderBuffer.readBits(4) + 16);
          continue;
          i10 = -localBitReaderBuffer.readBits(4);
        }
      }
      if (j == 1683496997)
      {
        i9 = m;
        if (m == -1)
        {
          i9 = 0;
          this.samplesPerFrame = this.samplesPerFrameAtMaxFs;
        }
        int i17 = 1;
        localBitReaderBuffer = new BitReaderBuffer(localByteBuffer);
        localBitReaderBuffer.readBits(8);
        localBitReaderBuffer.readBits(2);
        i10 = localBitReaderBuffer.readBits(1);
        m = 12;
        j = 20;
        if (i10 == 0)
        {
          m = 8;
          j = 16;
        }
        i10 = localBitReaderBuffer.readBits(m);
        m = localBitReaderBuffer.readBits(j) + 1;
        localByteBuffer.position(i18 + (i10 + 1));
        int i19 = localByteBuffer.getInt();
        int i15;
        int i16;
        int i14;
        int i13;
        int i12;
        int i11;
        if (i19 == 1515870810)
        {
          if (i6 == 1) {
            i = 1;
          }
          i15 = 1;
          i16 = n;
          i14 = i2;
          i13 = i1;
          i12 = i3;
          i11 = i5;
          i10 = i4;
          j = i;
        }
        for (;;)
        {
          if (j == 0) {
            this.frameSize += m;
          }
          localByteBuffer.position(i18 + m);
          m = i9;
          i = j;
          i4 = i10;
          i5 = i11;
          i9 = i17;
          i3 = i12;
          i1 = i13;
          i2 = i14;
          i6 = i15;
          n = i16;
          break;
          if (i19 == 1191201283)
          {
            if (i1 == 1) {
              i = 1;
            }
            i13 = 1;
            j = i;
            i10 = i4;
            i11 = i5;
            i12 = i3;
            i14 = i2;
            i15 = i6;
            i16 = n;
          }
          else if (i19 == 496366178)
          {
            if (i3 == 1) {
              i = 1;
            }
            i12 = 1;
            j = i;
            i10 = i4;
            i11 = i5;
            i13 = i1;
            i14 = i2;
            i15 = i6;
            i16 = n;
          }
          else if (i19 == 1700671838)
          {
            if (i2 == 1) {
              i = 1;
            }
            i14 = 1;
            j = i;
            i10 = i4;
            i11 = i5;
            i12 = i3;
            i13 = i1;
            i15 = i6;
            i16 = n;
          }
          else if (i19 == 176167201)
          {
            if (i5 == 1) {
              i = 1;
            }
            i11 = 1;
            j = i;
            i10 = i4;
            i12 = i3;
            i13 = i1;
            i14 = i2;
            i15 = i6;
            i16 = n;
          }
          else if (i19 == 1101174087)
          {
            if (n == 1) {
              i = 1;
            }
            i16 = 1;
            j = i;
            i10 = i4;
            i11 = i5;
            i12 = i3;
            i13 = i1;
            i14 = i2;
            i15 = i6;
          }
          else
          {
            j = i;
            i10 = i4;
            i11 = i5;
            i12 = i3;
            i13 = i1;
            i14 = i2;
            i15 = i6;
            i16 = n;
            if (i19 == 45126241)
            {
              if (i4 == 1) {
                i = 1;
              }
              i10 = 1;
              j = i;
              i11 = i5;
              i12 = i3;
              i13 = i1;
              i14 = i2;
              i15 = i6;
              i16 = n;
            }
          }
        }
      }
      throw new IOException("No DTS_SYNCWORD_* found at " + localByteBuffer.position());
      j = 0;
      continue;
      j = 1;
      continue;
      j = 2;
      continue;
      j = 3;
    }
    label1365:
    i = 31;
    i6 = i;
    switch (k)
    {
    default: 
      i6 = i;
    case 1: 
    case 3: 
      i10 = 0;
      k = 0;
      if (m == 0) {
        if (n == 1) {
          if (i4 == 0)
          {
            i = 17;
            this.type = "dtsl";
            label1462:
            this.samplerate = this.maxSampleRate;
            this.sampleSize = 24;
            label1476:
            this.ddts.setDTSSamplingFrequency(this.maxSampleRate);
            if (!this.isVBR) {
              break label2487;
            }
            this.ddts.setMaxBitRate((this.coreBitRate + this.extPeakBitrate) * 1000);
            label1516:
            this.ddts.setAvgBitRate((this.coreBitRate + this.extAvgBitrate) * 1000);
            this.ddts.setPcmSampleDepth(this.sampleSize);
            this.ddts.setFrameDuration(j);
            this.ddts.setStreamConstruction(i);
            if (((this.coreChannelMask & 0x8) <= 0) && ((this.coreChannelMask & 0x1000) <= 0)) {
              break label2511;
            }
            this.ddts.setCoreLFEPresent(1);
            label1593:
            this.ddts.setCoreLayout(i6);
            this.ddts.setCoreSize(this.coreFramePayloadInBytes);
            this.ddts.setStereoDownmix(0);
            this.ddts.setRepresentationType(4);
            this.ddts.setChannelLayout(this.channelMask);
            if ((this.coreMaxSampleRate <= 0) || (this.extAvgBitrate <= 0)) {
              break label2522;
            }
            this.ddts.setMultiAssetFlag(1);
          }
        }
      }
      break;
    }
    for (;;)
    {
      this.ddts.setLBRDurationMod(this.lbrCodingPresent);
      this.ddts.setReservedBoxPresent(0);
      this.channelCount = 0;
      i = 0;
      if (i < 16) {
        break label2533;
      }
      this.samples = generateSamples(this.dataSource, this.dataOffset, l, m);
      this.sampleDurations = new long[this.samples.size()];
      Arrays.fill(this.sampleDurations, this.samplesPerFrame);
      return true;
      i6 = k;
      break;
      i = 21;
      this.type = "dtsh";
      break label1462;
      if (i5 == 1)
      {
        i = 18;
        this.type = "dtse";
        break label1462;
      }
      i = k;
      if (i4 != 1) {
        break label1462;
      }
      this.type = "dtsh";
      if ((i1 == 0) && (n == 0))
      {
        i = 19;
        break label1462;
      }
      if ((i1 == 1) && (n == 0))
      {
        i = 20;
        break label1462;
      }
      i = k;
      if (i1 != 0) {
        break label1462;
      }
      i = k;
      if (n != 1) {
        break label1462;
      }
      i = 21;
      break label1462;
      if (i9 < 1)
      {
        if (i8 > 0) {
          switch (i7)
          {
          default: 
            i = 0;
            this.type = "dtsh";
            break;
          case 0: 
            i = 2;
            this.type = "dtsc";
            break;
          case 2: 
            i = 4;
            this.type = "dtsc";
            break;
          case 6: 
            i = 3;
            this.type = "dtsh";
            break;
          }
        }
        i = 1;
        this.type = "dtsc";
        break label1476;
      }
      this.type = "dtsh";
      if (i8 == 0)
      {
        if ((i4 == 0) && (i1 == 1) && (i3 == 0) && (i2 == 0) && (n == 0) && (i5 == 0))
        {
          i = 5;
          break label1476;
        }
        if ((i4 == 0) && (i1 == 0) && (i3 == 0) && (i2 == 1) && (n == 0) && (i5 == 0))
        {
          i = 6;
          break label1476;
        }
        if ((i4 == 0) && (i1 == 1) && (i3 == 0) && (i2 == 1) && (n == 0) && (i5 == 0))
        {
          i = 9;
          break label1476;
        }
        if ((i4 == 0) && (i1 == 0) && (i3 == 1) && (i2 == 0) && (n == 0) && (i5 == 0))
        {
          i = 10;
          break label1476;
        }
        if ((i4 == 0) && (i1 == 1) && (i3 == 1) && (i2 == 0) && (n == 0) && (i5 == 0))
        {
          i = 13;
          break label1476;
        }
        i = i10;
        if (i4 != 0) {
          break label1476;
        }
        i = i10;
        if (i1 != 0) {
          break label1476;
        }
        i = i10;
        if (i3 != 0) {
          break label1476;
        }
        i = i10;
        if (i2 != 0) {
          break label1476;
        }
        i = i10;
        if (n != 1) {
          break label1476;
        }
        i = i10;
        if (i5 != 0) {
          break label1476;
        }
        i = 14;
        break label1476;
      }
      if ((i7 == 0) && (i4 == 0) && (i1 == 0) && (i3 == 0) && (i2 == 1) && (n == 0) && (i5 == 0))
      {
        i = 7;
        break label1476;
      }
      if ((i7 == 6) && (i4 == 0) && (i1 == 0) && (i3 == 0) && (i2 == 1) && (n == 0) && (i5 == 0))
      {
        i = 8;
        break label1476;
      }
      if ((i7 == 0) && (i4 == 0) && (i1 == 0) && (i3 == 1) && (i2 == 0) && (n == 0) && (i5 == 0))
      {
        i = 11;
        break label1476;
      }
      if ((i7 == 6) && (i4 == 0) && (i1 == 0) && (i3 == 1) && (i2 == 0) && (n == 0) && (i5 == 0))
      {
        i = 12;
        break label1476;
      }
      if ((i7 == 0) && (i4 == 0) && (i1 == 0) && (i3 == 0) && (i2 == 0) && (n == 1) && (i5 == 0))
      {
        i = 15;
        break label1476;
      }
      i = i10;
      if (i7 != 2) {
        break label1476;
      }
      i = i10;
      if (i4 != 0) {
        break label1476;
      }
      i = i10;
      if (i1 != 0) {
        break label1476;
      }
      i = i10;
      if (i3 != 0) {
        break label1476;
      }
      i = i10;
      if (i2 != 0) {
        break label1476;
      }
      i = i10;
      if (n != 1) {
        break label1476;
      }
      i = i10;
      if (i5 != 0) {
        break label1476;
      }
      i = 16;
      break label1476;
      label2487:
      this.ddts.setMaxBitRate((this.coreBitRate + this.extAvgBitrate) * 1000);
      break label1516;
      label2511:
      this.ddts.setCoreLFEPresent(0);
      break label1593;
      label2522:
      this.ddts.setMultiAssetFlag(0);
    }
    label2533:
    if ((this.channelMask >> i & 0x1) == 1) {
      switch (i)
      {
      }
    }
    for (this.channelCount += 2;; this.channelCount += 1)
    {
      i += 1;
      break;
    }
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
    return this.sampleDurations;
  }
  
  public List<Sample> getSamples()
  {
    return this.samples;
  }
  
  public long[] getSyncSamples()
  {
    return null;
  }
  
  public TrackMetaData getTrackMetaData()
  {
    return this.trackMetaData;
  }
  
  class LookAhead
  {
    ByteBuffer buffer;
    long bufferStartPos;
    private final int corePresent;
    long dataEnd;
    DataSource dataSource;
    int inBufferPos = 0;
    long start;
    
    LookAhead(DataSource paramDataSource, long paramLong1, long paramLong2, int paramInt)
      throws IOException
    {
      this.dataSource = paramDataSource;
      this.bufferStartPos = paramLong1;
      this.dataEnd = (paramLong2 + paramLong1);
      this.corePresent = paramInt;
      fillBuffer();
    }
    
    private void discardByte()
    {
      this.inBufferPos += 1;
    }
    
    private void discardNext4AndMarkStart()
    {
      this.start = (this.bufferStartPos + this.inBufferPos);
      this.inBufferPos += 4;
    }
    
    private void discardQWord()
    {
      this.inBufferPos += 4;
    }
    
    private void fillBuffer()
      throws IOException
    {
      System.err.println("Fill Buffer");
      this.buffer = this.dataSource.map(this.bufferStartPos, Math.min(this.dataEnd - this.bufferStartPos, 67108864L));
    }
    
    private ByteBuffer getSample()
    {
      if (this.start >= this.bufferStartPos)
      {
        this.buffer.position((int)(this.start - this.bufferStartPos));
        ByteBuffer localByteBuffer = this.buffer.slice();
        localByteBuffer.limit((int)(this.inBufferPos - (this.start - this.bufferStartPos)));
        return (ByteBuffer)localByteBuffer;
      }
      throw new RuntimeException("damn! NAL exceeds buffer");
    }
    
    private boolean nextFourEquals(byte paramByte1, byte paramByte2, byte paramByte3, byte paramByte4)
      throws IOException
    {
      boolean bool2 = false;
      boolean bool1;
      if (this.buffer.limit() - this.inBufferPos >= 4)
      {
        bool1 = bool2;
        if (this.buffer.get(this.inBufferPos) == paramByte1)
        {
          bool1 = bool2;
          if (this.buffer.get(this.inBufferPos + 1) == paramByte2)
          {
            bool1 = bool2;
            if (this.buffer.get(this.inBufferPos + 2) == paramByte3)
            {
              bool1 = bool2;
              if (this.buffer.get(this.inBufferPos + 3) == paramByte4) {
                bool1 = true;
              }
            }
          }
        }
      }
      do
      {
        return bool1;
        bool1 = bool2;
      } while (this.bufferStartPos + this.inBufferPos + 4L < this.dataSource.size());
      throw new EOFException();
    }
    
    private boolean nextFourEquals0x64582025()
      throws IOException
    {
      return nextFourEquals((byte)100, (byte)88, (byte)32, (byte)37);
    }
    
    private boolean nextFourEquals0x64582025orEof()
      throws IOException
    {
      return nextFourEqualsOrEof((byte)100, (byte)88, (byte)32, (byte)37);
    }
    
    private boolean nextFourEquals0x7FFE8001()
      throws IOException
    {
      return nextFourEquals((byte)Byte.MAX_VALUE, (byte)-2, (byte)Byte.MIN_VALUE, (byte)1);
    }
    
    private boolean nextFourEquals0x7FFE8001orEof()
      throws IOException
    {
      return nextFourEqualsOrEof((byte)Byte.MAX_VALUE, (byte)-2, (byte)Byte.MIN_VALUE, (byte)1);
    }
    
    private boolean nextFourEqualsOrEof(byte paramByte1, byte paramByte2, byte paramByte3, byte paramByte4)
      throws IOException
    {
      if (this.buffer.limit() - this.inBufferPos >= 4)
      {
        if ((this.bufferStartPos + this.inBufferPos) % 1048576L == 0L) {
          System.err.println((this.bufferStartPos + this.inBufferPos) / 1024L / 1024L);
        }
        if ((this.buffer.get(this.inBufferPos) != paramByte1) || (this.buffer.get(this.inBufferPos + 1) != paramByte2) || (this.buffer.get(this.inBufferPos + 2) != paramByte3) || (this.buffer.get(this.inBufferPos + 3) != paramByte4)) {}
      }
      do
      {
        return true;
        return false;
        if (this.bufferStartPos + this.inBufferPos + 4L <= this.dataEnd) {
          break;
        }
      } while (this.bufferStartPos + this.inBufferPos == this.dataEnd);
      return false;
      this.bufferStartPos = this.start;
      this.inBufferPos = 0;
      fillBuffer();
      return nextFourEquals0x7FFE8001();
    }
    
    public ByteBuffer findNextStart()
      throws IOException
    {
      try
      {
        if (this.corePresent == 1) {
          if (nextFourEquals0x7FFE8001())
          {
            label15:
            discardNext4AndMarkStart();
            if (this.corePresent != 1) {
              break label63;
            }
            if (!nextFourEquals0x7FFE8001orEof()) {
              break label56;
            }
          }
        }
        for (;;)
        {
          return getSample();
          do
          {
            discardByte();
            break;
          } while (!nextFourEquals0x64582025());
          break label15;
          label56:
          label63:
          boolean bool;
          do
          {
            discardQWord();
            break;
            bool = nextFourEquals0x64582025orEof();
          } while (!bool);
        }
        return null;
      }
      catch (EOFException localEOFException) {}
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/tracks/DTSTrackImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */