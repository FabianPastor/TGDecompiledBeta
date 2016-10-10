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
import com.googlecode.mp4parser.boxes.mp4.ESDescriptorBox;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.AudioSpecificConfig;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BitReaderBuffer;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.DecoderConfigDescriptor;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ESDescriptor;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.SLConfigDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AACTrackImpl
  extends AbstractTrack
{
  static Map<Integer, String> audioObjectTypes = new HashMap();
  public static Map<Integer, Integer> samplingFrequencyIndexMap;
  long avgBitRate;
  int bufferSizeDB;
  private DataSource dataSource;
  long[] decTimes;
  AdtsHeader firstHeader;
  private String lang = "eng";
  long maxBitRate;
  SampleDescriptionBox sampleDescriptionBox;
  private List<Sample> samples;
  TrackMetaData trackMetaData = new TrackMetaData();
  
  static
  {
    audioObjectTypes.put(Integer.valueOf(1), "AAC Main");
    audioObjectTypes.put(Integer.valueOf(2), "AAC LC (Low Complexity)");
    audioObjectTypes.put(Integer.valueOf(3), "AAC SSR (Scalable Sample Rate)");
    audioObjectTypes.put(Integer.valueOf(4), "AAC LTP (Long Term Prediction)");
    audioObjectTypes.put(Integer.valueOf(5), "SBR (Spectral Band Replication)");
    audioObjectTypes.put(Integer.valueOf(6), "AAC Scalable");
    audioObjectTypes.put(Integer.valueOf(7), "TwinVQ");
    audioObjectTypes.put(Integer.valueOf(8), "CELP (Code Excited Linear Prediction)");
    audioObjectTypes.put(Integer.valueOf(9), "HXVC (Harmonic Vector eXcitation Coding)");
    audioObjectTypes.put(Integer.valueOf(10), "Reserved");
    audioObjectTypes.put(Integer.valueOf(11), "Reserved");
    audioObjectTypes.put(Integer.valueOf(12), "TTSI (Text-To-Speech Interface)");
    audioObjectTypes.put(Integer.valueOf(13), "Main Synthesis");
    audioObjectTypes.put(Integer.valueOf(14), "Wavetable Synthesis");
    audioObjectTypes.put(Integer.valueOf(15), "General MIDI");
    audioObjectTypes.put(Integer.valueOf(16), "Algorithmic Synthesis and Audio Effects");
    audioObjectTypes.put(Integer.valueOf(17), "ER (Error Resilient) AAC LC");
    audioObjectTypes.put(Integer.valueOf(18), "Reserved");
    audioObjectTypes.put(Integer.valueOf(19), "ER AAC LTP");
    audioObjectTypes.put(Integer.valueOf(20), "ER AAC Scalable");
    audioObjectTypes.put(Integer.valueOf(21), "ER TwinVQ");
    audioObjectTypes.put(Integer.valueOf(22), "ER BSAC (Bit-Sliced Arithmetic Coding)");
    audioObjectTypes.put(Integer.valueOf(23), "ER AAC LD (Low Delay)");
    audioObjectTypes.put(Integer.valueOf(24), "ER CELP");
    audioObjectTypes.put(Integer.valueOf(25), "ER HVXC");
    audioObjectTypes.put(Integer.valueOf(26), "ER HILN (Harmonic and Individual Lines plus Noise)");
    audioObjectTypes.put(Integer.valueOf(27), "ER Parametric");
    audioObjectTypes.put(Integer.valueOf(28), "SSC (SinuSoidal Coding)");
    audioObjectTypes.put(Integer.valueOf(29), "PS (Parametric Stereo)");
    audioObjectTypes.put(Integer.valueOf(30), "MPEG Surround");
    audioObjectTypes.put(Integer.valueOf(31), "(Escape value)");
    audioObjectTypes.put(Integer.valueOf(32), "Layer-1");
    audioObjectTypes.put(Integer.valueOf(33), "Layer-2");
    audioObjectTypes.put(Integer.valueOf(34), "Layer-3");
    audioObjectTypes.put(Integer.valueOf(35), "DST (Direct Stream Transfer)");
    audioObjectTypes.put(Integer.valueOf(36), "ALS (Audio Lossless)");
    audioObjectTypes.put(Integer.valueOf(37), "SLS (Scalable LosslesS)");
    audioObjectTypes.put(Integer.valueOf(38), "SLS non-core");
    audioObjectTypes.put(Integer.valueOf(39), "ER AAC ELD (Enhanced Low Delay)");
    audioObjectTypes.put(Integer.valueOf(40), "SMR (Symbolic Music Representation) Simple");
    audioObjectTypes.put(Integer.valueOf(41), "SMR Main");
    audioObjectTypes.put(Integer.valueOf(42), "USAC (Unified Speech and Audio Coding) (no SBR)");
    audioObjectTypes.put(Integer.valueOf(43), "SAOC (Spatial Audio Object Coding)");
    audioObjectTypes.put(Integer.valueOf(44), "LD MPEG Surround");
    audioObjectTypes.put(Integer.valueOf(45), "USAC");
    samplingFrequencyIndexMap = new HashMap();
    samplingFrequencyIndexMap.put(Integer.valueOf(96000), Integer.valueOf(0));
    samplingFrequencyIndexMap.put(Integer.valueOf(88200), Integer.valueOf(1));
    samplingFrequencyIndexMap.put(Integer.valueOf(64000), Integer.valueOf(2));
    samplingFrequencyIndexMap.put(Integer.valueOf(48000), Integer.valueOf(3));
    samplingFrequencyIndexMap.put(Integer.valueOf(44100), Integer.valueOf(4));
    samplingFrequencyIndexMap.put(Integer.valueOf(32000), Integer.valueOf(5));
    samplingFrequencyIndexMap.put(Integer.valueOf(24000), Integer.valueOf(6));
    samplingFrequencyIndexMap.put(Integer.valueOf(22050), Integer.valueOf(7));
    samplingFrequencyIndexMap.put(Integer.valueOf(16000), Integer.valueOf(8));
    samplingFrequencyIndexMap.put(Integer.valueOf(12000), Integer.valueOf(9));
    samplingFrequencyIndexMap.put(Integer.valueOf(11025), Integer.valueOf(10));
    samplingFrequencyIndexMap.put(Integer.valueOf(8000), Integer.valueOf(11));
    samplingFrequencyIndexMap.put(Integer.valueOf(0), Integer.valueOf(96000));
    samplingFrequencyIndexMap.put(Integer.valueOf(1), Integer.valueOf(88200));
    samplingFrequencyIndexMap.put(Integer.valueOf(2), Integer.valueOf(64000));
    samplingFrequencyIndexMap.put(Integer.valueOf(3), Integer.valueOf(48000));
    samplingFrequencyIndexMap.put(Integer.valueOf(4), Integer.valueOf(44100));
    samplingFrequencyIndexMap.put(Integer.valueOf(5), Integer.valueOf(32000));
    samplingFrequencyIndexMap.put(Integer.valueOf(6), Integer.valueOf(24000));
    samplingFrequencyIndexMap.put(Integer.valueOf(7), Integer.valueOf(22050));
    samplingFrequencyIndexMap.put(Integer.valueOf(8), Integer.valueOf(16000));
    samplingFrequencyIndexMap.put(Integer.valueOf(9), Integer.valueOf(12000));
    samplingFrequencyIndexMap.put(Integer.valueOf(10), Integer.valueOf(11025));
    samplingFrequencyIndexMap.put(Integer.valueOf(11), Integer.valueOf(8000));
  }
  
  public AACTrackImpl(DataSource paramDataSource)
    throws IOException
  {
    this(paramDataSource, "eng");
  }
  
  public AACTrackImpl(DataSource paramDataSource, String paramString)
    throws IOException
  {
    super(paramDataSource.toString());
    this.lang = paramString;
    this.dataSource = paramDataSource;
    this.samples = new ArrayList();
    this.firstHeader = readSamples(paramDataSource);
    double d1 = this.firstHeader.sampleRate / 1024.0D;
    double d2 = this.samples.size() / d1;
    long l1 = 0L;
    paramDataSource = new LinkedList();
    Object localObject1 = this.samples.iterator();
    if (!((Iterator)localObject1).hasNext())
    {
      this.avgBitRate = ((int)(8L * l1 / d2));
      this.bufferSizeDB = 1536;
      this.sampleDescriptionBox = new SampleDescriptionBox();
      paramDataSource = new AudioSampleEntry("mp4a");
      if (this.firstHeader.channelconfig != 7) {
        break label621;
      }
      paramDataSource.setChannelCount(8);
    }
    for (;;)
    {
      paramDataSource.setSampleRate(this.firstHeader.sampleRate);
      paramDataSource.setDataReferenceIndex(1);
      paramDataSource.setSampleSize(16);
      localObject1 = new ESDescriptorBox();
      Object localObject2 = new ESDescriptor();
      ((ESDescriptor)localObject2).setEsId(0);
      Object localObject3 = new SLConfigDescriptor();
      ((SLConfigDescriptor)localObject3).setPredefined(2);
      ((ESDescriptor)localObject2).setSlConfigDescriptor((SLConfigDescriptor)localObject3);
      localObject3 = new DecoderConfigDescriptor();
      ((DecoderConfigDescriptor)localObject3).setObjectTypeIndication(64);
      ((DecoderConfigDescriptor)localObject3).setStreamType(5);
      ((DecoderConfigDescriptor)localObject3).setBufferSizeDB(this.bufferSizeDB);
      ((DecoderConfigDescriptor)localObject3).setMaxBitRate(this.maxBitRate);
      ((DecoderConfigDescriptor)localObject3).setAvgBitRate(this.avgBitRate);
      AudioSpecificConfig localAudioSpecificConfig = new AudioSpecificConfig();
      localAudioSpecificConfig.setAudioObjectType(2);
      localAudioSpecificConfig.setSamplingFrequencyIndex(this.firstHeader.sampleFrequencyIndex);
      localAudioSpecificConfig.setChannelConfiguration(this.firstHeader.channelconfig);
      ((DecoderConfigDescriptor)localObject3).setAudioSpecificInfo(localAudioSpecificConfig);
      ((ESDescriptor)localObject2).setDecoderConfigDescriptor((DecoderConfigDescriptor)localObject3);
      localObject3 = ((ESDescriptor)localObject2).serialize();
      ((ESDescriptorBox)localObject1).setEsDescriptor((ESDescriptor)localObject2);
      ((ESDescriptorBox)localObject1).setData((ByteBuffer)localObject3);
      paramDataSource.addBox((Box)localObject1);
      this.sampleDescriptionBox.addBox(paramDataSource);
      this.trackMetaData.setCreationTime(new Date());
      this.trackMetaData.setModificationTime(new Date());
      this.trackMetaData.setLanguage(paramString);
      this.trackMetaData.setVolume(1.0F);
      this.trackMetaData.setTimescale(this.firstHeader.sampleRate);
      this.decTimes = new long[this.samples.size()];
      Arrays.fill(this.decTimes, 1024L);
      return;
      int i = (int)((Sample)((Iterator)localObject1).next()).getSize();
      long l2 = l1 + i;
      paramDataSource.add(Integer.valueOf(i));
      label503:
      if (paramDataSource.size() <= d1)
      {
        l1 = l2;
        if (paramDataSource.size() != (int)d1) {
          break;
        }
        i = 0;
        localObject2 = paramDataSource.iterator();
      }
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
          break label503;
        }
        i += ((Integer)((Iterator)localObject2).next()).intValue();
      }
      label621:
      paramDataSource.setChannelCount(this.firstHeader.channelconfig);
    }
  }
  
  private AdtsHeader readADTSHeader(DataSource paramDataSource)
    throws IOException
  {
    AdtsHeader localAdtsHeader = new AdtsHeader();
    Object localObject = ByteBuffer.allocate(7);
    do
    {
      if (((ByteBuffer)localObject).position() >= 7)
      {
        localObject = new BitReaderBuffer((ByteBuffer)((ByteBuffer)localObject).rewind());
        if (((BitReaderBuffer)localObject).readBits(12) == 4095) {
          break;
        }
        throw new IOException("Expected Start Word 0xfff");
      }
    } while (paramDataSource.read((ByteBuffer)localObject) != -1);
    localObject = null;
    do
    {
      return (AdtsHeader)localObject;
      localAdtsHeader.mpegVersion = ((BitReaderBuffer)localObject).readBits(1);
      localAdtsHeader.layer = ((BitReaderBuffer)localObject).readBits(2);
      localAdtsHeader.protectionAbsent = ((BitReaderBuffer)localObject).readBits(1);
      localAdtsHeader.profile = (((BitReaderBuffer)localObject).readBits(2) + 1);
      localAdtsHeader.sampleFrequencyIndex = ((BitReaderBuffer)localObject).readBits(4);
      localAdtsHeader.sampleRate = ((Integer)samplingFrequencyIndexMap.get(Integer.valueOf(localAdtsHeader.sampleFrequencyIndex))).intValue();
      ((BitReaderBuffer)localObject).readBits(1);
      localAdtsHeader.channelconfig = ((BitReaderBuffer)localObject).readBits(3);
      localAdtsHeader.original = ((BitReaderBuffer)localObject).readBits(1);
      localAdtsHeader.home = ((BitReaderBuffer)localObject).readBits(1);
      localAdtsHeader.copyrightedStream = ((BitReaderBuffer)localObject).readBits(1);
      localAdtsHeader.copyrightStart = ((BitReaderBuffer)localObject).readBits(1);
      localAdtsHeader.frameLength = ((BitReaderBuffer)localObject).readBits(13);
      localAdtsHeader.bufferFullness = ((BitReaderBuffer)localObject).readBits(11);
      localAdtsHeader.numAacFramesPerAdtsFrame = (((BitReaderBuffer)localObject).readBits(2) + 1);
      if (localAdtsHeader.numAacFramesPerAdtsFrame != 1) {
        throw new IOException("This muxer can only work with 1 AAC frame per ADTS frame");
      }
      localObject = localAdtsHeader;
    } while (localAdtsHeader.protectionAbsent != 0);
    paramDataSource.read(ByteBuffer.allocate(2));
    return localAdtsHeader;
  }
  
  private AdtsHeader readSamples(DataSource paramDataSource)
    throws IOException
  {
    Object localObject2;
    for (Object localObject1 = null;; localObject1 = localObject2)
    {
      AdtsHeader localAdtsHeader = readADTSHeader(paramDataSource);
      if (localAdtsHeader == null) {
        return (AdtsHeader)localObject1;
      }
      localObject2 = localObject1;
      if (localObject1 == null) {
        localObject2 = localAdtsHeader;
      }
      final long l1 = paramDataSource.position();
      long l2 = localAdtsHeader.frameLength - localAdtsHeader.getSize();
      this.samples.add(new Sample()
      {
        public ByteBuffer asByteBuffer()
        {
          try
          {
            ByteBuffer localByteBuffer = AACTrackImpl.this.dataSource.map(l1, this.val$frameSize);
            return localByteBuffer;
          }
          catch (IOException localIOException)
          {
            throw new RuntimeException(localIOException);
          }
        }
        
        public long getSize()
        {
          return this.val$frameSize;
        }
        
        public void writeTo(WritableByteChannel paramAnonymousWritableByteChannel)
          throws IOException
        {
          AACTrackImpl.this.dataSource.transferTo(l1, this.val$frameSize, paramAnonymousWritableByteChannel);
        }
      });
      paramDataSource.position(paramDataSource.position() + localAdtsHeader.frameLength - localAdtsHeader.getSize());
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
    return this.decTimes;
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
    return "AACTrackImpl{sampleRate=" + this.firstHeader.sampleRate + ", channelconfig=" + this.firstHeader.channelconfig + '}';
  }
  
  class AdtsHeader
  {
    int bufferFullness;
    int channelconfig;
    int copyrightStart;
    int copyrightedStream;
    int frameLength;
    int home;
    int layer;
    int mpegVersion;
    int numAacFramesPerAdtsFrame;
    int original;
    int profile;
    int protectionAbsent;
    int sampleFrequencyIndex;
    int sampleRate;
    
    AdtsHeader() {}
    
    int getSize()
    {
      if (this.protectionAbsent == 0) {}
      for (int i = 2;; i = 0) {
        return i + 7;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/tracks/AACTrackImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */