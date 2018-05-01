package org.telegram.messenger.video;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
  private int[] sampleCompositions;
  private SampleDescriptionBox sampleDescriptionBox = null;
  private long[] sampleDurations;
  private ArrayList<SamplePresentationTime> samplePresentationTimes = new ArrayList();
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
  {
    this.trackId = paramInt;
    this.isAudio = paramBoolean;
    if (!this.isAudio)
    {
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
          ByteBuffer localByteBuffer = paramMediaFormat.getByteBuffer("csd-0");
          localByteBuffer.position(4);
          localObject4 = new byte[localByteBuffer.remaining()];
          localByteBuffer.get((byte[])localObject4);
          ((ArrayList)localObject3).add(localObject4);
          localObject4 = new ArrayList();
          localByteBuffer = paramMediaFormat.getByteBuffer("csd-1");
          localByteBuffer.position(4);
          byte[] arrayOfByte = new byte[localByteBuffer.remaining()];
          localByteBuffer.get(arrayOfByte);
          ((ArrayList)localObject4).add(arrayOfByte);
          ((AvcConfigurationBox)localObject2).setSequenceParameterSets((List)localObject3);
          ((AvcConfigurationBox)localObject2).setPictureParameterSets((List)localObject4);
        }
        if (paramMediaFormat.containsKey("level"))
        {
          paramInt = paramMediaFormat.getInteger("level");
          if (paramInt == 1)
          {
            ((AvcConfigurationBox)localObject2).setAvcLevelIndication(1);
            if (!paramMediaFormat.containsKey("profile")) {
              break label843;
            }
            paramInt = paramMediaFormat.getInteger("profile");
            if (paramInt != 1) {
              break label748;
            }
            ((AvcConfigurationBox)localObject2).setAvcProfileIndication(66);
            label427:
            ((AvcConfigurationBox)localObject2).setBitDepthLumaMinus8(-1);
            ((AvcConfigurationBox)localObject2).setBitDepthChromaMinus8(-1);
            ((AvcConfigurationBox)localObject2).setChromaFormat(-1);
            ((AvcConfigurationBox)localObject2).setConfigurationVersion(1);
            ((AvcConfigurationBox)localObject2).setLengthSizeMinusOne(3);
            ((AvcConfigurationBox)localObject2).setProfileCompatibility(0);
            ((VisualSampleEntry)localObject1).addBox((Box)localObject2);
            this.sampleDescriptionBox.addBox((Box)localObject1);
          }
        }
      }
      for (;;)
      {
        return;
        if (paramInt == 32)
        {
          ((AvcConfigurationBox)localObject2).setAvcLevelIndication(2);
          break;
        }
        if (paramInt == 4)
        {
          ((AvcConfigurationBox)localObject2).setAvcLevelIndication(11);
          break;
        }
        if (paramInt == 8)
        {
          ((AvcConfigurationBox)localObject2).setAvcLevelIndication(12);
          break;
        }
        if (paramInt == 16)
        {
          ((AvcConfigurationBox)localObject2).setAvcLevelIndication(13);
          break;
        }
        if (paramInt == 64)
        {
          ((AvcConfigurationBox)localObject2).setAvcLevelIndication(21);
          break;
        }
        if (paramInt == 128)
        {
          ((AvcConfigurationBox)localObject2).setAvcLevelIndication(22);
          break;
        }
        if (paramInt == 256)
        {
          ((AvcConfigurationBox)localObject2).setAvcLevelIndication(3);
          break;
        }
        if (paramInt == 512)
        {
          ((AvcConfigurationBox)localObject2).setAvcLevelIndication(31);
          break;
        }
        if (paramInt == 1024)
        {
          ((AvcConfigurationBox)localObject2).setAvcLevelIndication(32);
          break;
        }
        if (paramInt == 2048)
        {
          ((AvcConfigurationBox)localObject2).setAvcLevelIndication(4);
          break;
        }
        if (paramInt == 4096)
        {
          ((AvcConfigurationBox)localObject2).setAvcLevelIndication(41);
          break;
        }
        if (paramInt == 8192)
        {
          ((AvcConfigurationBox)localObject2).setAvcLevelIndication(42);
          break;
        }
        if (paramInt == 16384)
        {
          ((AvcConfigurationBox)localObject2).setAvcLevelIndication(5);
          break;
        }
        if (paramInt == 32768)
        {
          ((AvcConfigurationBox)localObject2).setAvcLevelIndication(51);
          break;
        }
        if (paramInt == 65536)
        {
          ((AvcConfigurationBox)localObject2).setAvcLevelIndication(52);
          break;
        }
        if (paramInt != 2) {
          break;
        }
        ((AvcConfigurationBox)localObject2).setAvcLevelIndication(27);
        break;
        ((AvcConfigurationBox)localObject2).setAvcLevelIndication(13);
        break;
        label748:
        if (paramInt == 2)
        {
          ((AvcConfigurationBox)localObject2).setAvcProfileIndication(77);
          break label427;
        }
        if (paramInt == 4)
        {
          ((AvcConfigurationBox)localObject2).setAvcProfileIndication(88);
          break label427;
        }
        if (paramInt == 8)
        {
          ((AvcConfigurationBox)localObject2).setAvcProfileIndication(100);
          break label427;
        }
        if (paramInt == 16)
        {
          ((AvcConfigurationBox)localObject2).setAvcProfileIndication(110);
          break label427;
        }
        if (paramInt == 32)
        {
          ((AvcConfigurationBox)localObject2).setAvcProfileIndication(122);
          break label427;
        }
        if (paramInt != 64) {
          break label427;
        }
        ((AvcConfigurationBox)localObject2).setAvcProfileIndication(244);
        break label427;
        label843:
        ((AvcConfigurationBox)localObject2).setAvcProfileIndication(100);
        break label427;
        if (((String)localObject1).equals("video/mp4v"))
        {
          paramMediaFormat = new VisualSampleEntry("mp4v");
          paramMediaFormat.setDataReferenceIndex(1);
          paramMediaFormat.setDepth(24);
          paramMediaFormat.setFrameCount(1);
          paramMediaFormat.setHorizresolution(72.0D);
          paramMediaFormat.setVertresolution(72.0D);
          paramMediaFormat.setWidth(this.width);
          paramMediaFormat.setHeight(this.height);
          this.sampleDescriptionBox.addBox(paramMediaFormat);
        }
      }
    }
    this.volume = 1.0F;
    this.timeScale = paramMediaFormat.getInteger("sample-rate");
    this.handler = "soun";
    this.headerBox = new SoundMediaHeaderBox();
    this.sampleDescriptionBox = new SampleDescriptionBox();
    Object localObject3 = new AudioSampleEntry("mp4a");
    ((AudioSampleEntry)localObject3).setChannelCount(paramMediaFormat.getInteger("channel-count"));
    ((AudioSampleEntry)localObject3).setSampleRate(paramMediaFormat.getInteger("sample-rate"));
    ((AudioSampleEntry)localObject3).setDataReferenceIndex(1);
    ((AudioSampleEntry)localObject3).setSampleSize(16);
    Object localObject1 = new ESDescriptorBox();
    Object localObject2 = new ESDescriptor();
    ((ESDescriptor)localObject2).setEsId(0);
    Object localObject4 = new SLConfigDescriptor();
    ((SLConfigDescriptor)localObject4).setPredefined(2);
    ((ESDescriptor)localObject2).setSlConfigDescriptor((SLConfigDescriptor)localObject4);
    localObject4 = new DecoderConfigDescriptor();
    ((DecoderConfigDescriptor)localObject4).setObjectTypeIndication(64);
    ((DecoderConfigDescriptor)localObject4).setStreamType(5);
    ((DecoderConfigDescriptor)localObject4).setBufferSizeDB(1536);
    if (paramMediaFormat.containsKey("max-bitrate")) {
      ((DecoderConfigDescriptor)localObject4).setMaxBitRate(paramMediaFormat.getInteger("max-bitrate"));
    }
    for (;;)
    {
      ((DecoderConfigDescriptor)localObject4).setAvgBitRate(this.timeScale);
      paramMediaFormat = new AudioSpecificConfig();
      paramMediaFormat.setAudioObjectType(2);
      paramMediaFormat.setSamplingFrequencyIndex(((Integer)samplingFrequencyIndexMap.get(Integer.valueOf((int)((AudioSampleEntry)localObject3).getSampleRate()))).intValue());
      paramMediaFormat.setChannelConfiguration(((AudioSampleEntry)localObject3).getChannelCount());
      ((DecoderConfigDescriptor)localObject4).setAudioSpecificInfo(paramMediaFormat);
      ((ESDescriptor)localObject2).setDecoderConfigDescriptor((DecoderConfigDescriptor)localObject4);
      paramMediaFormat = ((ESDescriptor)localObject2).serialize();
      ((ESDescriptorBox)localObject1).setEsDescriptor((ESDescriptor)localObject2);
      ((ESDescriptorBox)localObject1).setData(paramMediaFormat);
      ((AudioSampleEntry)localObject3).addBox((Box)localObject1);
      this.sampleDescriptionBox.addBox((Box)localObject3);
      break;
      ((DecoderConfigDescriptor)localObject4).setMaxBitRate(96000L);
    }
  }
  
  public void addSample(long paramLong, MediaCodec.BufferInfo paramBufferInfo)
  {
    if ((!this.isAudio) && ((paramBufferInfo.flags & 0x1) != 0)) {}
    for (int i = 1;; i = 0)
    {
      this.samples.add(new Sample(paramLong, paramBufferInfo.size));
      if ((this.syncSamples != null) && (i != 0)) {
        this.syncSamples.add(Integer.valueOf(this.samples.size()));
      }
      this.samplePresentationTimes.add(new SamplePresentationTime(this.samplePresentationTimes.size(), (paramBufferInfo.presentationTimeUs * this.timeScale + 500000L) / 1000000L));
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
  
  public int[] getSampleCompositions()
  {
    return this.sampleCompositions;
  }
  
  public SampleDescriptionBox getSampleDescriptionBox()
  {
    return this.sampleDescriptionBox;
  }
  
  public long[] getSampleDurations()
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
    for (int i = 0;; i++)
    {
      localObject = arrayOfLong;
      if (i >= this.syncSamples.size()) {
        break;
      }
      arrayOfLong[i] = ((Integer)this.syncSamples.get(i)).intValue();
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
  
  public void prepare()
  {
    Object localObject = new ArrayList(this.samplePresentationTimes);
    Collections.sort(this.samplePresentationTimes, new Comparator()
    {
      public int compare(Track.SamplePresentationTime paramAnonymousSamplePresentationTime1, Track.SamplePresentationTime paramAnonymousSamplePresentationTime2)
      {
        int i;
        if (Track.SamplePresentationTime.access$000(paramAnonymousSamplePresentationTime1) > Track.SamplePresentationTime.access$000(paramAnonymousSamplePresentationTime2)) {
          i = 1;
        }
        for (;;)
        {
          return i;
          if (Track.SamplePresentationTime.access$000(paramAnonymousSamplePresentationTime1) < Track.SamplePresentationTime.access$000(paramAnonymousSamplePresentationTime2)) {
            i = -1;
          } else {
            i = 0;
          }
        }
      }
    });
    long l1 = 0L;
    this.sampleDurations = new long[this.samplePresentationTimes.size()];
    long l2 = Long.MAX_VALUE;
    int i = 0;
    int j = 0;
    while (j < this.samplePresentationTimes.size())
    {
      SamplePresentationTime localSamplePresentationTime = (SamplePresentationTime)this.samplePresentationTimes.get(j);
      long l3 = localSamplePresentationTime.presentationTime - l1;
      l1 = localSamplePresentationTime.presentationTime;
      this.sampleDurations[localSamplePresentationTime.index] = l3;
      if (localSamplePresentationTime.index != 0) {
        this.duration += l3;
      }
      long l4 = l2;
      if (l3 != 0L) {
        l4 = Math.min(l2, l3);
      }
      if (localSamplePresentationTime.index != j) {
        i = 1;
      }
      j++;
      l2 = l4;
    }
    if (this.sampleDurations.length > 0)
    {
      this.sampleDurations[0] = l2;
      this.duration += l2;
    }
    for (j = 1; j < ((ArrayList)localObject).size(); j++) {
      SamplePresentationTime.access$202((SamplePresentationTime)((ArrayList)localObject).get(j), this.sampleDurations[j] + ((SamplePresentationTime)((ArrayList)localObject).get(j - 1)).dt);
    }
    if (i != 0)
    {
      this.sampleCompositions = new int[this.samplePresentationTimes.size()];
      for (i = 0; i < this.samplePresentationTimes.size(); i++)
      {
        localObject = (SamplePresentationTime)this.samplePresentationTimes.get(i);
        this.sampleCompositions[localObject.index] = ((int)(((SamplePresentationTime)localObject).presentationTime - ((SamplePresentationTime)localObject).dt));
      }
    }
  }
  
  private class SamplePresentationTime
  {
    private long dt;
    private int index;
    private long presentationTime;
    
    public SamplePresentationTime(int paramInt, long paramLong)
    {
      this.index = paramInt;
      this.presentationTime = paramLong;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/video/Track.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */