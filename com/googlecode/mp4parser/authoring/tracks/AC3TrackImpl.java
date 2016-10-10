package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.boxes.CompositionTimeToSample.Entry;
import com.coremedia.iso.boxes.SampleDependencyTypeBox.Entry;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.boxes.AC3SpecificBox;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AC3TrackImpl
  extends AbstractTrack
{
  static int[][][][] bitRateAndFrameSizeTable = (int[][][][])Array.newInstance(Integer.TYPE, new int[] { 19, 2, 3, 2 });
  private final DataSource dataSource;
  private long[] duration;
  private SampleDescriptionBox sampleDescriptionBox;
  private List<Sample> samples;
  private TrackMetaData trackMetaData = new TrackMetaData();
  
  static
  {
    bitRateAndFrameSizeTable[0][0][0][0] = 32;
    bitRateAndFrameSizeTable[0][1][0][0] = 32;
    bitRateAndFrameSizeTable[0][0][0][1] = 64;
    bitRateAndFrameSizeTable[0][1][0][1] = 64;
    bitRateAndFrameSizeTable[1][0][0][0] = 40;
    bitRateAndFrameSizeTable[1][1][0][0] = 40;
    bitRateAndFrameSizeTable[1][0][0][1] = 80;
    bitRateAndFrameSizeTable[1][1][0][1] = 80;
    bitRateAndFrameSizeTable[2][0][0][0] = 48;
    bitRateAndFrameSizeTable[2][1][0][0] = 48;
    bitRateAndFrameSizeTable[2][0][0][1] = 96;
    bitRateAndFrameSizeTable[2][1][0][1] = 96;
    bitRateAndFrameSizeTable[3][0][0][0] = 56;
    bitRateAndFrameSizeTable[3][1][0][0] = 56;
    bitRateAndFrameSizeTable[3][0][0][1] = 112;
    bitRateAndFrameSizeTable[3][1][0][1] = 112;
    bitRateAndFrameSizeTable[4][0][0][0] = 64;
    bitRateAndFrameSizeTable[4][1][0][0] = 64;
    bitRateAndFrameSizeTable[4][0][0][1] = '';
    bitRateAndFrameSizeTable[4][1][0][1] = '';
    bitRateAndFrameSizeTable[5][0][0][0] = 80;
    bitRateAndFrameSizeTable[5][1][0][0] = 80;
    bitRateAndFrameSizeTable[5][0][0][1] = ' ';
    bitRateAndFrameSizeTable[5][1][0][1] = ' ';
    bitRateAndFrameSizeTable[6][0][0][0] = 96;
    bitRateAndFrameSizeTable[6][1][0][0] = 96;
    bitRateAndFrameSizeTable[6][0][0][1] = 'À';
    bitRateAndFrameSizeTable[6][1][0][1] = 'À';
    bitRateAndFrameSizeTable[7][0][0][0] = 112;
    bitRateAndFrameSizeTable[7][1][0][0] = 112;
    bitRateAndFrameSizeTable[7][0][0][1] = 'à';
    bitRateAndFrameSizeTable[7][1][0][1] = 'à';
    bitRateAndFrameSizeTable[8][0][0][0] = '';
    bitRateAndFrameSizeTable[8][1][0][0] = '';
    bitRateAndFrameSizeTable[8][0][0][1] = 'Ā';
    bitRateAndFrameSizeTable[8][1][0][1] = 'Ā';
    bitRateAndFrameSizeTable[9][0][0][0] = ' ';
    bitRateAndFrameSizeTable[9][1][0][0] = ' ';
    bitRateAndFrameSizeTable[9][0][0][1] = 'ŀ';
    bitRateAndFrameSizeTable[9][1][0][1] = 'ŀ';
    bitRateAndFrameSizeTable[10][0][0][0] = 'À';
    bitRateAndFrameSizeTable[10][1][0][0] = 'À';
    bitRateAndFrameSizeTable[10][0][0][1] = 'ƀ';
    bitRateAndFrameSizeTable[10][1][0][1] = 'ƀ';
    bitRateAndFrameSizeTable[11][0][0][0] = 'à';
    bitRateAndFrameSizeTable[11][1][0][0] = 'à';
    bitRateAndFrameSizeTable[11][0][0][1] = 'ǀ';
    bitRateAndFrameSizeTable[11][1][0][1] = 'ǀ';
    bitRateAndFrameSizeTable[12][0][0][0] = 'Ā';
    bitRateAndFrameSizeTable[12][1][0][0] = 'Ā';
    bitRateAndFrameSizeTable[12][0][0][1] = 'Ȁ';
    bitRateAndFrameSizeTable[12][1][0][1] = 'Ȁ';
    bitRateAndFrameSizeTable[13][0][0][0] = 'ŀ';
    bitRateAndFrameSizeTable[13][1][0][0] = 'ŀ';
    bitRateAndFrameSizeTable[13][0][0][1] = 'ʀ';
    bitRateAndFrameSizeTable[13][1][0][1] = 'ʀ';
    bitRateAndFrameSizeTable[14][0][0][0] = 'ƀ';
    bitRateAndFrameSizeTable[14][1][0][0] = 'ƀ';
    bitRateAndFrameSizeTable[14][0][0][1] = '̀';
    bitRateAndFrameSizeTable[14][1][0][1] = '̀';
    bitRateAndFrameSizeTable[15][0][0][0] = 'ǀ';
    bitRateAndFrameSizeTable[15][1][0][0] = 'ǀ';
    bitRateAndFrameSizeTable[15][0][0][1] = '΀';
    bitRateAndFrameSizeTable[15][1][0][1] = '΀';
    bitRateAndFrameSizeTable[16][0][0][0] = 'Ȁ';
    bitRateAndFrameSizeTable[16][1][0][0] = 'Ȁ';
    bitRateAndFrameSizeTable[16][0][0][1] = 'Ѐ';
    bitRateAndFrameSizeTable[16][1][0][1] = 'Ѐ';
    bitRateAndFrameSizeTable[17][0][0][0] = 'ɀ';
    bitRateAndFrameSizeTable[17][1][0][0] = 'ɀ';
    bitRateAndFrameSizeTable[17][0][0][1] = 'Ҁ';
    bitRateAndFrameSizeTable[17][1][0][1] = 'Ҁ';
    bitRateAndFrameSizeTable[18][0][0][0] = 'ʀ';
    bitRateAndFrameSizeTable[18][1][0][0] = 'ʀ';
    bitRateAndFrameSizeTable[18][0][0][1] = 'Ԁ';
    bitRateAndFrameSizeTable[18][1][0][1] = 'Ԁ';
    bitRateAndFrameSizeTable[0][0][1][0] = 32;
    bitRateAndFrameSizeTable[0][1][1][0] = 32;
    bitRateAndFrameSizeTable[0][0][1][1] = 69;
    bitRateAndFrameSizeTable[0][1][1][1] = 70;
    bitRateAndFrameSizeTable[1][0][1][0] = 40;
    bitRateAndFrameSizeTable[1][1][1][0] = 40;
    bitRateAndFrameSizeTable[1][0][1][1] = 87;
    bitRateAndFrameSizeTable[1][1][1][1] = 88;
    bitRateAndFrameSizeTable[2][0][1][0] = 48;
    bitRateAndFrameSizeTable[2][1][1][0] = 48;
    bitRateAndFrameSizeTable[2][0][1][1] = 104;
    bitRateAndFrameSizeTable[2][1][1][1] = 105;
    bitRateAndFrameSizeTable[3][0][1][0] = 56;
    bitRateAndFrameSizeTable[3][1][1][0] = 56;
    bitRateAndFrameSizeTable[3][0][1][1] = 121;
    bitRateAndFrameSizeTable[3][1][1][1] = 122;
    bitRateAndFrameSizeTable[4][0][1][0] = 64;
    bitRateAndFrameSizeTable[4][1][1][0] = 64;
    bitRateAndFrameSizeTable[4][0][1][1] = '';
    bitRateAndFrameSizeTable[4][1][1][1] = '';
    bitRateAndFrameSizeTable[5][0][1][0] = 80;
    bitRateAndFrameSizeTable[5][1][1][0] = 80;
    bitRateAndFrameSizeTable[5][0][1][1] = '®';
    bitRateAndFrameSizeTable[5][1][1][1] = '¯';
    bitRateAndFrameSizeTable[6][0][1][0] = 96;
    bitRateAndFrameSizeTable[6][1][1][0] = 96;
    bitRateAndFrameSizeTable[6][0][1][1] = 'Ð';
    bitRateAndFrameSizeTable[6][1][1][1] = 'Ñ';
    bitRateAndFrameSizeTable[7][0][1][0] = 112;
    bitRateAndFrameSizeTable[7][1][1][0] = 112;
    bitRateAndFrameSizeTable[7][0][1][1] = 'ó';
    bitRateAndFrameSizeTable[7][1][1][1] = 'ô';
    bitRateAndFrameSizeTable[8][0][1][0] = '';
    bitRateAndFrameSizeTable[8][1][1][0] = '';
    bitRateAndFrameSizeTable[8][0][1][1] = 'Ė';
    bitRateAndFrameSizeTable[8][1][1][1] = 'ė';
    bitRateAndFrameSizeTable[9][0][1][0] = ' ';
    bitRateAndFrameSizeTable[9][1][1][0] = ' ';
    bitRateAndFrameSizeTable[9][0][1][1] = 'Ŝ';
    bitRateAndFrameSizeTable[9][1][1][1] = 'ŝ';
    bitRateAndFrameSizeTable[10][0][1][0] = 'À';
    bitRateAndFrameSizeTable[10][1][1][0] = 'À';
    bitRateAndFrameSizeTable[10][0][1][1] = 'ơ';
    bitRateAndFrameSizeTable[10][1][1][1] = 'Ƣ';
    bitRateAndFrameSizeTable[11][0][1][0] = 'à';
    bitRateAndFrameSizeTable[11][1][1][0] = 'à';
    bitRateAndFrameSizeTable[11][0][1][1] = 'ǧ';
    bitRateAndFrameSizeTable[11][1][1][1] = 'Ǩ';
    bitRateAndFrameSizeTable[12][0][1][0] = 'Ā';
    bitRateAndFrameSizeTable[12][1][1][0] = 'Ā';
    bitRateAndFrameSizeTable[12][0][1][1] = 'ȭ';
    bitRateAndFrameSizeTable[12][1][1][1] = 'Ȯ';
    bitRateAndFrameSizeTable[13][0][1][0] = 'ŀ';
    bitRateAndFrameSizeTable[13][1][1][0] = 'ŀ';
    bitRateAndFrameSizeTable[13][0][1][1] = 'ʸ';
    bitRateAndFrameSizeTable[13][1][1][1] = 'ʹ';
    bitRateAndFrameSizeTable[14][0][1][0] = 'ƀ';
    bitRateAndFrameSizeTable[14][1][1][0] = 'ƀ';
    bitRateAndFrameSizeTable[14][0][1][1] = '̓';
    bitRateAndFrameSizeTable[14][1][1][1] = '̈́';
    bitRateAndFrameSizeTable[15][0][1][0] = 'ǀ';
    bitRateAndFrameSizeTable[15][1][1][0] = 'ǀ';
    bitRateAndFrameSizeTable[15][0][1][1] = 'Ϗ';
    bitRateAndFrameSizeTable[15][1][1][1] = 'Ϗ';
    bitRateAndFrameSizeTable[16][0][1][0] = 'Ȁ';
    bitRateAndFrameSizeTable[16][1][1][0] = 'Ȁ';
    bitRateAndFrameSizeTable[16][0][1][1] = 'њ';
    bitRateAndFrameSizeTable[16][1][1][1] = 'ћ';
    bitRateAndFrameSizeTable[17][0][1][0] = 'ɀ';
    bitRateAndFrameSizeTable[17][1][1][0] = 'ɀ';
    bitRateAndFrameSizeTable[17][0][1][1] = 'ӥ';
    bitRateAndFrameSizeTable[17][1][1][1] = 'Ӧ';
    bitRateAndFrameSizeTable[18][0][1][0] = 'ʀ';
    bitRateAndFrameSizeTable[18][1][1][0] = 'ʀ';
    bitRateAndFrameSizeTable[18][0][1][1] = 'ձ';
    bitRateAndFrameSizeTable[18][1][1][1] = 'ղ';
    bitRateAndFrameSizeTable[0][0][2][0] = 32;
    bitRateAndFrameSizeTable[0][1][2][0] = 32;
    bitRateAndFrameSizeTable[0][0][2][1] = 96;
    bitRateAndFrameSizeTable[0][1][2][1] = 96;
    bitRateAndFrameSizeTable[1][0][2][0] = 40;
    bitRateAndFrameSizeTable[1][1][2][0] = 40;
    bitRateAndFrameSizeTable[1][0][2][1] = 120;
    bitRateAndFrameSizeTable[1][1][2][1] = 120;
    bitRateAndFrameSizeTable[2][0][2][0] = 48;
    bitRateAndFrameSizeTable[2][1][2][0] = 48;
    bitRateAndFrameSizeTable[2][0][2][1] = '';
    bitRateAndFrameSizeTable[2][1][2][1] = '';
    bitRateAndFrameSizeTable[3][0][2][0] = 56;
    bitRateAndFrameSizeTable[3][1][2][0] = 56;
    bitRateAndFrameSizeTable[3][0][2][1] = '¨';
    bitRateAndFrameSizeTable[3][1][2][1] = '¨';
    bitRateAndFrameSizeTable[4][0][2][0] = 64;
    bitRateAndFrameSizeTable[4][1][2][0] = 64;
    bitRateAndFrameSizeTable[4][0][2][1] = 'À';
    bitRateAndFrameSizeTable[4][1][2][1] = 'À';
    bitRateAndFrameSizeTable[5][0][2][0] = 80;
    bitRateAndFrameSizeTable[5][1][2][0] = 80;
    bitRateAndFrameSizeTable[5][0][2][1] = 'ð';
    bitRateAndFrameSizeTable[5][1][2][1] = 'ð';
    bitRateAndFrameSizeTable[6][0][2][0] = 96;
    bitRateAndFrameSizeTable[6][1][2][0] = 96;
    bitRateAndFrameSizeTable[6][0][2][1] = 'Ġ';
    bitRateAndFrameSizeTable[6][1][2][1] = 'Ġ';
    bitRateAndFrameSizeTable[7][0][2][0] = 112;
    bitRateAndFrameSizeTable[7][1][2][0] = 112;
    bitRateAndFrameSizeTable[7][0][2][1] = 'Ő';
    bitRateAndFrameSizeTable[7][1][2][1] = 'Ő';
    bitRateAndFrameSizeTable[8][0][2][0] = '';
    bitRateAndFrameSizeTable[8][1][2][0] = '';
    bitRateAndFrameSizeTable[8][0][2][1] = 'ƀ';
    bitRateAndFrameSizeTable[8][1][2][1] = 'ƀ';
    bitRateAndFrameSizeTable[9][0][2][0] = ' ';
    bitRateAndFrameSizeTable[9][1][2][0] = ' ';
    bitRateAndFrameSizeTable[9][0][2][1] = 'Ǡ';
    bitRateAndFrameSizeTable[9][1][2][1] = 'Ǡ';
    bitRateAndFrameSizeTable[10][0][2][0] = 'À';
    bitRateAndFrameSizeTable[10][1][2][0] = 'À';
    bitRateAndFrameSizeTable[10][0][2][1] = 'ɀ';
    bitRateAndFrameSizeTable[10][1][2][1] = 'ɀ';
    bitRateAndFrameSizeTable[11][0][2][0] = 'à';
    bitRateAndFrameSizeTable[11][1][2][0] = 'à';
    bitRateAndFrameSizeTable[11][0][2][1] = 'ʠ';
    bitRateAndFrameSizeTable[11][1][2][1] = 'ʠ';
    bitRateAndFrameSizeTable[12][0][2][0] = 'Ā';
    bitRateAndFrameSizeTable[12][1][2][0] = 'Ā';
    bitRateAndFrameSizeTable[12][0][2][1] = '̀';
    bitRateAndFrameSizeTable[12][1][2][1] = '̀';
    bitRateAndFrameSizeTable[13][0][2][0] = 'ŀ';
    bitRateAndFrameSizeTable[13][1][2][0] = 'ŀ';
    bitRateAndFrameSizeTable[13][0][2][1] = 'π';
    bitRateAndFrameSizeTable[13][1][2][1] = 'π';
    bitRateAndFrameSizeTable[14][0][2][0] = 'ƀ';
    bitRateAndFrameSizeTable[14][1][2][0] = 'ƀ';
    bitRateAndFrameSizeTable[14][0][2][1] = 'Ҁ';
    bitRateAndFrameSizeTable[14][1][2][1] = 'Ҁ';
    bitRateAndFrameSizeTable[15][0][2][0] = 'ǀ';
    bitRateAndFrameSizeTable[15][1][2][0] = 'ǀ';
    bitRateAndFrameSizeTable[15][0][2][1] = 'Հ';
    bitRateAndFrameSizeTable[15][1][2][1] = 'Հ';
    bitRateAndFrameSizeTable[16][0][2][0] = 'Ȁ';
    bitRateAndFrameSizeTable[16][1][2][0] = 'Ȁ';
    bitRateAndFrameSizeTable[16][0][2][1] = '؀';
    bitRateAndFrameSizeTable[16][1][2][1] = '؀';
    bitRateAndFrameSizeTable[17][0][2][0] = 'ɀ';
    bitRateAndFrameSizeTable[17][1][2][0] = 'ɀ';
    bitRateAndFrameSizeTable[17][0][2][1] = 'ۀ';
    bitRateAndFrameSizeTable[17][1][2][1] = 'ۀ';
    bitRateAndFrameSizeTable[18][0][2][0] = 'ʀ';
    bitRateAndFrameSizeTable[18][1][2][0] = 'ʀ';
    bitRateAndFrameSizeTable[18][0][2][1] = 'ހ';
    bitRateAndFrameSizeTable[18][1][2][1] = 'ހ';
  }
  
  public AC3TrackImpl(DataSource paramDataSource)
    throws IOException
  {
    this(paramDataSource, "eng");
  }
  
  public AC3TrackImpl(DataSource paramDataSource, String paramString)
    throws IOException
  {
    super(paramDataSource.toString());
    this.dataSource = paramDataSource;
    this.trackMetaData.setLanguage(paramString);
    this.samples = readSamples();
    this.sampleDescriptionBox = new SampleDescriptionBox();
    paramDataSource = createAudioSampleEntry();
    this.sampleDescriptionBox.addBox(paramDataSource);
    this.trackMetaData.setCreationTime(new Date());
    this.trackMetaData.setModificationTime(new Date());
    this.trackMetaData.setLanguage(paramString);
    this.trackMetaData.setTimescale(paramDataSource.getSampleRate());
    this.trackMetaData.setVolume(1.0F);
  }
  
  private AudioSampleEntry createAudioSampleEntry()
    throws IOException
  {
    Object localObject = new BitReaderBuffer(((Sample)this.samples.get(0)).asByteBuffer());
    if (((BitReaderBuffer)localObject).readBits(16) != 2935) {
      throw new RuntimeException("Stream doesn't seem to be AC3");
    }
    ((BitReaderBuffer)localObject).readBits(16);
    int k = ((BitReaderBuffer)localObject).readBits(2);
    switch (k)
    {
    default: 
      throw new RuntimeException("Unsupported Sample Rate");
    case 0: 
      i = 48000;
    }
    int m;
    int n;
    int i1;
    int i2;
    for (;;)
    {
      m = ((BitReaderBuffer)localObject).readBits(6);
      n = ((BitReaderBuffer)localObject).readBits(5);
      i1 = ((BitReaderBuffer)localObject).readBits(3);
      i2 = ((BitReaderBuffer)localObject).readBits(3);
      if (n != 16) {
        break;
      }
      throw new RuntimeException("You cannot read E-AC-3 track with AC3TrackImpl.class - user EC3TrackImpl.class");
      i = 44100;
      continue;
      i = 32000;
    }
    int j;
    if (n == 9) {
      j = i / 2;
    }
    do
    {
      do
      {
        if ((i2 != 1) && ((i2 & 0x1) == 1)) {
          ((BitReaderBuffer)localObject).readBits(2);
        }
        if ((i2 & 0x4) != 0) {
          ((BitReaderBuffer)localObject).readBits(2);
        }
        if (i2 == 2) {
          ((BitReaderBuffer)localObject).readBits(2);
        }
        switch (i2)
        {
        default: 
          throw new RuntimeException("Unsupported acmod");
          j = i;
        }
      } while (n == 8);
      j = i;
    } while (n == 6);
    throw new RuntimeException("Unsupported bsid");
    int i = ((BitReaderBuffer)localObject).readBits(1);
    if (i == 1) {}
    localObject = new AudioSampleEntry("ac-3");
    ((AudioSampleEntry)localObject).setChannelCount(2);
    ((AudioSampleEntry)localObject).setSampleRate(j);
    ((AudioSampleEntry)localObject).setDataReferenceIndex(1);
    ((AudioSampleEntry)localObject).setSampleSize(16);
    AC3SpecificBox localAC3SpecificBox = new AC3SpecificBox();
    localAC3SpecificBox.setAcmod(i2);
    localAC3SpecificBox.setBitRateCode(m >> 1);
    localAC3SpecificBox.setBsid(n);
    localAC3SpecificBox.setBsmod(i1);
    localAC3SpecificBox.setFscod(k);
    localAC3SpecificBox.setLfeon(i);
    localAC3SpecificBox.setReserved(0);
    ((AudioSampleEntry)localObject).addBox(localAC3SpecificBox);
    return (AudioSampleEntry)localObject;
  }
  
  private int getFrameSize(int paramInt1, int paramInt2)
  {
    int i = paramInt1 >>> 1;
    paramInt1 &= 0x1;
    if ((i > 18) || (paramInt1 > 1) || (paramInt2 > 2)) {
      throw new RuntimeException("Cannot determine framesize of current sample");
    }
    return bitRateAndFrameSizeTable[i][paramInt1][paramInt2][1] * 2;
  }
  
  private List<Sample> readSamples()
    throws IOException
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(5);
    ArrayList localArrayList = new ArrayList();
    for (;;)
    {
      if (-1 == this.dataSource.read(localByteBuffer))
      {
        this.duration = new long[localArrayList.size()];
        Arrays.fill(this.duration, 1536L);
        return localArrayList;
      }
      int i = getFrameSize(localByteBuffer.get(4) & 0x3F, localByteBuffer.get(4) >> 6);
      localArrayList.add(new Sample()
      {
        private final DataSource dataSource;
        private final long size;
        private final long start;
        
        public ByteBuffer asByteBuffer()
        {
          try
          {
            ByteBuffer localByteBuffer = this.dataSource.map(this.start, this.size);
            return localByteBuffer;
          }
          catch (IOException localIOException)
          {
            throw new RuntimeException(localIOException);
          }
        }
        
        public long getSize()
        {
          return this.size;
        }
        
        public void writeTo(WritableByteChannel paramAnonymousWritableByteChannel)
          throws IOException
        {
          this.dataSource.transferTo(this.start, this.size, paramAnonymousWritableByteChannel);
        }
      });
      this.dataSource.position(this.dataSource.position() - 5L + i);
      localByteBuffer.rewind();
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
    try
    {
      long[] arrayOfLong = this.duration;
      return arrayOfLong;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
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
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/tracks/AC3TrackImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */