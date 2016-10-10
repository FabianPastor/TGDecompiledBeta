package org.telegram.messenger.video;

import android.annotation.TargetApi;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaFormat;
import com.coremedia.iso.boxes.AbstractMediaHeaderBox;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SoundMediaHeaderBox;
import com.coremedia.iso.boxes.VideoMediaHeaderBox;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.googlecode.mp4parser.boxes.mp4.ESDescriptorBox;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.AudioSpecificConfig;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.DecoderConfigDescriptor;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ESDescriptor;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.SLConfigDescriptor;
import com.mp4parser.iso14496.part15.AvcConfigurationBox;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@TargetApi(16)
public class Track
{
  private static Map<Integer, Integer> samplingFrequencyIndexMap = new HashMap();
  private Date creationTime = new Date();
  private long duration = 0L;
  private boolean first = true;
  private String handler;
  private AbstractMediaHeaderBox headerBox = null;
  private int height;
  private boolean isAudio = false;
  private long lastPresentationTimeUs = 0L;
  private SampleDescriptionBox sampleDescriptionBox = null;
  private ArrayList<Long> sampleDurations = new ArrayList();
  private ArrayList<Sample> samples = new ArrayList();
  private LinkedList<Integer> syncSamples = null;
  private int timeScale;
  private long trackId = 0L;
  private float volume = 0.0F;
  private int width;
  
  static
  {
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
  }
  
  public Track(int paramInt, MediaFormat paramMediaFormat, boolean paramBoolean)
    throws Exception
  {
    this.trackId = paramInt;
    this.isAudio = paramBoolean;
    if (!this.isAudio)
    {
      this.sampleDurations.add(Long.valueOf(3015L));
      this.duration = 3015L;
      this.width = paramMediaFormat.getInteger("width");
      this.height = paramMediaFormat.getInteger("height");
      this.timeScale = 90000;
      this.syncSamples = new LinkedList();
      this.handler = "vide";
      this.headerBox = new VideoMediaHeaderBox();
      this.sampleDescriptionBox = new SampleDescriptionBox();
      localObject1 = paramMediaFormat.getString("mime");
      if (((String)localObject1).equals("video/avc"))
      {
        localObject1 = new VisualSampleEntry("avc1");
        ((VisualSampleEntry)localObject1).setDataReferenceIndex(1);
        ((VisualSampleEntry)localObject1).setDepth(24);
        ((VisualSampleEntry)localObject1).setFrameCount(1);
        ((VisualSampleEntry)localObject1).setHorizresolution(72.0D);
        ((VisualSampleEntry)localObject1).setVertresolution(72.0D);
        ((VisualSampleEntry)localObject1).setWidth(this.width);
        ((VisualSampleEntry)localObject1).setHeight(this.height);
        localObject2 = new AvcConfigurationBox();
        if (paramMediaFormat.getByteBuffer("csd-0") != null)
        {
          localObject3 = new ArrayList();
          localObject4 = paramMediaFormat.getByteBuffer("csd-0");
          ((ByteBuffer)localObject4).position(4);
          arrayOfByte = new byte[((ByteBuffer)localObject4).remaining()];
          ((ByteBuffer)localObject4).get(arrayOfByte);
          ((ArrayList)localObject3).add(arrayOfByte);
          localObject4 = new ArrayList();
          paramMediaFormat = paramMediaFormat.getByteBuffer("csd-1");
          paramMediaFormat.position(4);
          arrayOfByte = new byte[paramMediaFormat.remaining()];
          paramMediaFormat.get(arrayOfByte);
          ((ArrayList)localObject4).add(arrayOfByte);
          ((AvcConfigurationBox)localObject2).setSequenceParameterSets((List)localObject3);
          ((AvcConfigurationBox)localObject2).setPictureParameterSets((List)localObject4);
        }
        ((AvcConfigurationBox)localObject2).setAvcLevelIndication(13);
        ((AvcConfigurationBox)localObject2).setAvcProfileIndication(100);
        ((AvcConfigurationBox)localObject2).setBitDepthLumaMinus8(-1);
        ((AvcConfigurationBox)localObject2).setBitDepthChromaMinus8(-1);
        ((AvcConfigurationBox)localObject2).setChromaFormat(-1);
        ((AvcConfigurationBox)localObject2).setConfigurationVersion(1);
        ((AvcConfigurationBox)localObject2).setLengthSizeMinusOne(3);
        ((AvcConfigurationBox)localObject2).setProfileCompatibility(0);
        ((VisualSampleEntry)localObject1).addBox((Box)localObject2);
        this.sampleDescriptionBox.addBox((Box)localObject1);
      }
      while (!((String)localObject1).equals("video/mp4v"))
      {
        byte[] arrayOfByte;
        return;
      }
      paramMediaFormat = new VisualSampleEntry("mp4v");
      paramMediaFormat.setDataReferenceIndex(1);
      paramMediaFormat.setDepth(24);
      paramMediaFormat.setFrameCount(1);
      paramMediaFormat.setHorizresolution(72.0D);
      paramMediaFormat.setVertresolution(72.0D);
      paramMediaFormat.setWidth(this.width);
      paramMediaFormat.setHeight(this.height);
      this.sampleDescriptionBox.addBox(paramMediaFormat);
      return;
    }
    this.sampleDurations.add(Long.valueOf(1024L));
    this.duration = 1024L;
    this.volume = 1.0F;
    this.timeScale = paramMediaFormat.getInteger("sample-rate");
    this.handler = "soun";
    this.headerBox = new SoundMediaHeaderBox();
    this.sampleDescriptionBox = new SampleDescriptionBox();
    Object localObject1 = new AudioSampleEntry("mp4a");
    ((AudioSampleEntry)localObject1).setChannelCount(paramMediaFormat.getInteger("channel-count"));
    ((AudioSampleEntry)localObject1).setSampleRate(paramMediaFormat.getInteger("sample-rate"));
    ((AudioSampleEntry)localObject1).setDataReferenceIndex(1);
    ((AudioSampleEntry)localObject1).setSampleSize(16);
    paramMediaFormat = new ESDescriptorBox();
    Object localObject2 = new ESDescriptor();
    ((ESDescriptor)localObject2).setEsId(0);
    Object localObject3 = new SLConfigDescriptor();
    ((SLConfigDescriptor)localObject3).setPredefined(2);
    ((ESDescriptor)localObject2).setSlConfigDescriptor((SLConfigDescriptor)localObject3);
    localObject3 = new DecoderConfigDescriptor();
    ((DecoderConfigDescriptor)localObject3).setObjectTypeIndication(64);
    ((DecoderConfigDescriptor)localObject3).setStreamType(5);
    ((DecoderConfigDescriptor)localObject3).setBufferSizeDB(1536);
    ((DecoderConfigDescriptor)localObject3).setMaxBitRate(96000L);
    ((DecoderConfigDescriptor)localObject3).setAvgBitRate(96000L);
    Object localObject4 = new AudioSpecificConfig();
    ((AudioSpecificConfig)localObject4).setAudioObjectType(2);
    ((AudioSpecificConfig)localObject4).setSamplingFrequencyIndex(((Integer)samplingFrequencyIndexMap.get(Integer.valueOf((int)((AudioSampleEntry)localObject1).getSampleRate()))).intValue());
    ((AudioSpecificConfig)localObject4).setChannelConfiguration(((AudioSampleEntry)localObject1).getChannelCount());
    ((DecoderConfigDescriptor)localObject3).setAudioSpecificInfo((AudioSpecificConfig)localObject4);
    ((ESDescriptor)localObject2).setDecoderConfigDescriptor((DecoderConfigDescriptor)localObject3);
    localObject3 = ((ESDescriptor)localObject2).serialize();
    paramMediaFormat.setEsDescriptor((ESDescriptor)localObject2);
    paramMediaFormat.setData((ByteBuffer)localObject3);
    ((AudioSampleEntry)localObject1).addBox(paramMediaFormat);
    this.sampleDescriptionBox.addBox((Box)localObject1);
  }
  
  public void addSample(long paramLong, MediaCodec.BufferInfo paramBufferInfo)
  {
    long l = paramBufferInfo.presentationTimeUs - this.lastPresentationTimeUs;
    if (l < 0L) {
      return;
    }
    if ((!this.isAudio) && ((paramBufferInfo.flags & 0x1) != 0)) {}
    for (int i = 1;; i = 0)
    {
      this.samples.add(new Sample(paramLong, paramBufferInfo.size));
      if ((this.syncSamples != null) && (i != 0)) {
        this.syncSamples.add(Integer.valueOf(this.samples.size()));
      }
      paramLong = (this.timeScale * l + 500000L) / 1000000L;
      this.lastPresentationTimeUs = paramBufferInfo.presentationTimeUs;
      if (!this.first)
      {
        this.sampleDurations.add(this.sampleDurations.size() - 1, Long.valueOf(paramLong));
        this.duration += paramLong;
      }
      this.first = false;
      return;
    }
  }
  
  public Date getCreationTime()
  {
    return this.creationTime;
  }
  
  public long getDuration()
  {
    return this.duration;
  }
  
  public String getHandler()
  {
    return this.handler;
  }
  
  public int getHeight()
  {
    return this.height;
  }
  
  public AbstractMediaHeaderBox getMediaHeaderBox()
  {
    return this.headerBox;
  }
  
  public SampleDescriptionBox getSampleDescriptionBox()
  {
    return this.sampleDescriptionBox;
  }
  
  public ArrayList<Long> getSampleDurations()
  {
    return this.sampleDurations;
  }
  
  public ArrayList<Sample> getSamples()
  {
    return this.samples;
  }
  
  public long[] getSyncSamples()
  {
    Object localObject;
    if ((this.syncSamples == null) || (this.syncSamples.isEmpty()))
    {
      localObject = null;
      return (long[])localObject;
    }
    long[] arrayOfLong = new long[this.syncSamples.size()];
    int i = 0;
    for (;;)
    {
      localObject = arrayOfLong;
      if (i >= this.syncSamples.size()) {
        break;
      }
      arrayOfLong[i] = ((Integer)this.syncSamples.get(i)).intValue();
      i += 1;
    }
  }
  
  public int getTimeScale()
  {
    return this.timeScale;
  }
  
  public long getTrackId()
  {
    return this.trackId;
  }
  
  public float getVolume()
  {
    return this.volume;
  }
  
  public int getWidth()
  {
    return this.width;
  }
  
  public boolean isAudio()
  {
    return this.isAudio;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/video/Track.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */