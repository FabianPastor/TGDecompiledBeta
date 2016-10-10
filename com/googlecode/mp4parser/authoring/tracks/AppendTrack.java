package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.CompositionTimeToSample;
import com.coremedia.iso.boxes.CompositionTimeToSample.Entry;
import com.coremedia.iso.boxes.SampleDependencyTypeBox.Entry;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.coremedia.iso.boxes.sampleentry.SampleEntry;
import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.boxes.mp4.AbstractDescriptorBox;
import com.googlecode.mp4parser.boxes.mp4.ESDescriptorBox;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.AudioSpecificConfig;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BaseDescriptor;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.DecoderConfigDescriptor;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.DecoderSpecificInfo;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.ESDescriptor;
import com.googlecode.mp4parser.boxes.mp4.objectdescriptors.SLConfigDescriptor;
import com.googlecode.mp4parser.util.Logger;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class AppendTrack
  extends AbstractTrack
{
  private static Logger LOG = Logger.getLogger(AppendTrack.class);
  SampleDescriptionBox stsd;
  Track[] tracks;
  
  public AppendTrack(Track... paramVarArgs)
    throws IOException
  {
    super(appendTracknames(paramVarArgs));
    this.tracks = paramVarArgs;
    int j = paramVarArgs.length;
    int i = 0;
    if (i >= j) {
      return;
    }
    Track localTrack = paramVarArgs[i];
    if (this.stsd == null)
    {
      this.stsd = new SampleDescriptionBox();
      this.stsd.addBox((Box)localTrack.getSampleDescriptionBox().getBoxes(SampleEntry.class).get(0));
    }
    for (;;)
    {
      i += 1;
      break;
      this.stsd = mergeStsds(this.stsd, localTrack.getSampleDescriptionBox());
    }
  }
  
  public static String appendTracknames(Track... paramVarArgs)
  {
    String str = "";
    int j = paramVarArgs.length;
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return str.substring(0, str.length() - 3);
      }
      Track localTrack = paramVarArgs[i];
      str = str + localTrack.getName() + " + ";
      i += 1;
    }
  }
  
  private AudioSampleEntry mergeAudioSampleEntries(AudioSampleEntry paramAudioSampleEntry1, AudioSampleEntry paramAudioSampleEntry2)
  {
    AudioSampleEntry localAudioSampleEntry = new AudioSampleEntry(paramAudioSampleEntry2.getType());
    if (paramAudioSampleEntry1.getBytesPerFrame() == paramAudioSampleEntry2.getBytesPerFrame())
    {
      localAudioSampleEntry.setBytesPerFrame(paramAudioSampleEntry1.getBytesPerFrame());
      if (paramAudioSampleEntry1.getBytesPerPacket() != paramAudioSampleEntry2.getBytesPerPacket()) {
        break label291;
      }
      localAudioSampleEntry.setBytesPerPacket(paramAudioSampleEntry1.getBytesPerPacket());
      if (paramAudioSampleEntry1.getBytesPerSample() != paramAudioSampleEntry2.getBytesPerSample()) {
        break label293;
      }
      localAudioSampleEntry.setBytesPerSample(paramAudioSampleEntry1.getBytesPerSample());
      if (paramAudioSampleEntry1.getChannelCount() != paramAudioSampleEntry2.getChannelCount()) {
        break label303;
      }
      localAudioSampleEntry.setChannelCount(paramAudioSampleEntry1.getChannelCount());
      if (paramAudioSampleEntry1.getPacketSize() != paramAudioSampleEntry2.getPacketSize()) {
        break label305;
      }
      localAudioSampleEntry.setPacketSize(paramAudioSampleEntry1.getPacketSize());
      if (paramAudioSampleEntry1.getCompressionId() != paramAudioSampleEntry2.getCompressionId()) {
        break label315;
      }
      localAudioSampleEntry.setCompressionId(paramAudioSampleEntry1.getCompressionId());
      if (paramAudioSampleEntry1.getSampleRate() != paramAudioSampleEntry2.getSampleRate()) {
        break label317;
      }
      localAudioSampleEntry.setSampleRate(paramAudioSampleEntry1.getSampleRate());
      if (paramAudioSampleEntry1.getSampleSize() != paramAudioSampleEntry2.getSampleSize()) {
        break label319;
      }
      localAudioSampleEntry.setSampleSize(paramAudioSampleEntry1.getSampleSize());
      if (paramAudioSampleEntry1.getSamplesPerPacket() != paramAudioSampleEntry2.getSamplesPerPacket()) {
        break label321;
      }
      localAudioSampleEntry.setSamplesPerPacket(paramAudioSampleEntry1.getSamplesPerPacket());
      if (paramAudioSampleEntry1.getSoundVersion() != paramAudioSampleEntry2.getSoundVersion()) {
        break label323;
      }
      localAudioSampleEntry.setSoundVersion(paramAudioSampleEntry1.getSoundVersion());
      if (!Arrays.equals(paramAudioSampleEntry1.getSoundVersion2Data(), paramAudioSampleEntry2.getSoundVersion2Data())) {
        break label325;
      }
      localAudioSampleEntry.setSoundVersion2Data(paramAudioSampleEntry1.getSoundVersion2Data());
      if (paramAudioSampleEntry1.getBoxes().size() == paramAudioSampleEntry2.getBoxes().size())
      {
        paramAudioSampleEntry1 = paramAudioSampleEntry1.getBoxes().iterator();
        paramAudioSampleEntry2 = paramAudioSampleEntry2.getBoxes().iterator();
      }
    }
    for (;;)
    {
      if (!paramAudioSampleEntry1.hasNext())
      {
        return localAudioSampleEntry;
        LOG.logError("BytesPerFrame differ");
        return null;
        label291:
        return null;
        label293:
        LOG.logError("BytesPerSample differ");
        return null;
        label303:
        return null;
        label305:
        LOG.logError("ChannelCount differ");
        return null;
        label315:
        return null;
        label317:
        return null;
        label319:
        return null;
        label321:
        return null;
        label323:
        return null;
        label325:
        return null;
      }
      Box localBox = (Box)paramAudioSampleEntry1.next();
      Object localObject1 = (Box)paramAudioSampleEntry2.next();
      Object localObject2 = new ByteArrayOutputStream();
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      try
      {
        localBox.getBox(Channels.newChannel((OutputStream)localObject2));
        ((Box)localObject1).getBox(Channels.newChannel(localByteArrayOutputStream));
        if (Arrays.equals(((ByteArrayOutputStream)localObject2).toByteArray(), localByteArrayOutputStream.toByteArray())) {
          localAudioSampleEntry.addBox(localBox);
        }
      }
      catch (IOException paramAudioSampleEntry1)
      {
        LOG.logWarn(paramAudioSampleEntry1.getMessage());
        return null;
      }
      if (("esds".equals(localBox.getType())) && ("esds".equals(((Box)localObject1).getType())))
      {
        localObject2 = (ESDescriptorBox)localBox;
        localObject1 = (ESDescriptorBox)localObject1;
        ((ESDescriptorBox)localObject2).setDescriptor(mergeDescriptors(((ESDescriptorBox)localObject2).getEsDescriptor(), ((ESDescriptorBox)localObject1).getEsDescriptor()));
        localAudioSampleEntry.addBox(localBox);
      }
    }
  }
  
  private ESDescriptor mergeDescriptors(BaseDescriptor paramBaseDescriptor1, BaseDescriptor paramBaseDescriptor2)
  {
    if (((paramBaseDescriptor1 instanceof ESDescriptor)) && ((paramBaseDescriptor2 instanceof ESDescriptor)))
    {
      ESDescriptor localESDescriptor = (ESDescriptor)paramBaseDescriptor1;
      paramBaseDescriptor2 = (ESDescriptor)paramBaseDescriptor2;
      if (localESDescriptor.getURLFlag() != paramBaseDescriptor2.getURLFlag()) {
        paramBaseDescriptor1 = null;
      }
      label228:
      label235:
      label260:
      do
      {
        return paramBaseDescriptor1;
        localESDescriptor.getURLLength();
        paramBaseDescriptor2.getURLLength();
        if (localESDescriptor.getDependsOnEsId() != paramBaseDescriptor2.getDependsOnEsId()) {
          return null;
        }
        if (localESDescriptor.getEsId() != paramBaseDescriptor2.getEsId()) {
          return null;
        }
        if (localESDescriptor.getoCREsId() != paramBaseDescriptor2.getoCREsId()) {
          return null;
        }
        if (localESDescriptor.getoCRstreamFlag() != paramBaseDescriptor2.getoCRstreamFlag()) {
          return null;
        }
        if (localESDescriptor.getRemoteODFlag() != paramBaseDescriptor2.getRemoteODFlag()) {
          return null;
        }
        if (localESDescriptor.getStreamDependenceFlag() != paramBaseDescriptor2.getStreamDependenceFlag()) {
          return null;
        }
        localESDescriptor.getStreamPriority();
        paramBaseDescriptor2.getStreamPriority();
        if (localESDescriptor.getURLString() != null)
        {
          localESDescriptor.getURLString().equals(paramBaseDescriptor2.getURLString());
          if (localESDescriptor.getDecoderConfigDescriptor() == null) {
            break label228;
          }
          if (localESDescriptor.getDecoderConfigDescriptor().equals(paramBaseDescriptor2.getDecoderConfigDescriptor())) {
            break label235;
          }
        }
        DecoderConfigDescriptor localDecoderConfigDescriptor;
        while (paramBaseDescriptor2.getDecoderConfigDescriptor() != null)
        {
          paramBaseDescriptor1 = localESDescriptor.getDecoderConfigDescriptor();
          localDecoderConfigDescriptor = paramBaseDescriptor2.getDecoderConfigDescriptor();
          if ((paramBaseDescriptor1.getAudioSpecificInfo() == null) || (localDecoderConfigDescriptor.getAudioSpecificInfo() == null) || (paramBaseDescriptor1.getAudioSpecificInfo().equals(localDecoderConfigDescriptor.getAudioSpecificInfo()))) {
            break label260;
          }
          return null;
          paramBaseDescriptor2.getURLString();
          break;
        }
        if (localESDescriptor.getOtherDescriptors() != null)
        {
          if (localESDescriptor.getOtherDescriptors().equals(paramBaseDescriptor2.getOtherDescriptors())) {}
        }
        else {
          while (paramBaseDescriptor2.getOtherDescriptors() != null)
          {
            return null;
            if (paramBaseDescriptor1.getAvgBitRate() != localDecoderConfigDescriptor.getAvgBitRate()) {
              paramBaseDescriptor1.setAvgBitRate((paramBaseDescriptor1.getAvgBitRate() + localDecoderConfigDescriptor.getAvgBitRate()) / 2L);
            }
            paramBaseDescriptor1.getBufferSizeDB();
            localDecoderConfigDescriptor.getBufferSizeDB();
            if (paramBaseDescriptor1.getDecoderSpecificInfo() != null)
            {
              if (paramBaseDescriptor1.getDecoderSpecificInfo().equals(localDecoderConfigDescriptor.getDecoderSpecificInfo())) {}
            }
            else {
              while (localDecoderConfigDescriptor.getDecoderSpecificInfo() != null) {
                return null;
              }
            }
            if (paramBaseDescriptor1.getMaxBitRate() != localDecoderConfigDescriptor.getMaxBitRate()) {
              paramBaseDescriptor1.setMaxBitRate(Math.max(paramBaseDescriptor1.getMaxBitRate(), localDecoderConfigDescriptor.getMaxBitRate()));
            }
            if (!paramBaseDescriptor1.getProfileLevelIndicationDescriptors().equals(localDecoderConfigDescriptor.getProfileLevelIndicationDescriptors())) {
              return null;
            }
            if (paramBaseDescriptor1.getObjectTypeIndication() != localDecoderConfigDescriptor.getObjectTypeIndication()) {
              return null;
            }
            if (paramBaseDescriptor1.getStreamType() != localDecoderConfigDescriptor.getStreamType()) {
              return null;
            }
            if (paramBaseDescriptor1.getUpStream() == localDecoderConfigDescriptor.getUpStream()) {
              break;
            }
            return null;
          }
        }
        if (localESDescriptor.getSlConfigDescriptor() == null) {
          break;
        }
        paramBaseDescriptor1 = localESDescriptor;
      } while (localESDescriptor.getSlConfigDescriptor().equals(paramBaseDescriptor2.getSlConfigDescriptor()));
      for (;;)
      {
        return null;
        paramBaseDescriptor1 = localESDescriptor;
        if (paramBaseDescriptor2.getSlConfigDescriptor() == null) {
          break;
        }
      }
    }
    LOG.logError("I can only merge ESDescriptors");
    return null;
  }
  
  private SampleEntry mergeSampleEntry(SampleEntry paramSampleEntry1, SampleEntry paramSampleEntry2)
  {
    if (!paramSampleEntry1.getType().equals(paramSampleEntry2.getType())) {}
    do
    {
      return null;
      if (((paramSampleEntry1 instanceof VisualSampleEntry)) && ((paramSampleEntry2 instanceof VisualSampleEntry))) {
        return mergeVisualSampleEntry((VisualSampleEntry)paramSampleEntry1, (VisualSampleEntry)paramSampleEntry2);
      }
    } while ((!(paramSampleEntry1 instanceof AudioSampleEntry)) || (!(paramSampleEntry2 instanceof AudioSampleEntry)));
    return mergeAudioSampleEntries((AudioSampleEntry)paramSampleEntry1, (AudioSampleEntry)paramSampleEntry2);
  }
  
  private SampleDescriptionBox mergeStsds(SampleDescriptionBox paramSampleDescriptionBox1, SampleDescriptionBox paramSampleDescriptionBox2)
    throws IOException
  {
    Object localObject2 = new ByteArrayOutputStream();
    Object localObject1 = new ByteArrayOutputStream();
    try
    {
      paramSampleDescriptionBox1.getBox(Channels.newChannel((OutputStream)localObject2));
      paramSampleDescriptionBox2.getBox(Channels.newChannel((OutputStream)localObject1));
      localObject2 = ((ByteArrayOutputStream)localObject2).toByteArray();
      if (!Arrays.equals(((ByteArrayOutputStream)localObject1).toByteArray(), (byte[])localObject2))
      {
        localObject1 = mergeSampleEntry((SampleEntry)paramSampleDescriptionBox1.getBoxes(SampleEntry.class).get(0), (SampleEntry)paramSampleDescriptionBox2.getBoxes(SampleEntry.class).get(0));
        if (localObject1 != null) {
          paramSampleDescriptionBox1.setBoxes(Collections.singletonList(localObject1));
        }
      }
      else
      {
        return paramSampleDescriptionBox1;
      }
    }
    catch (IOException paramSampleDescriptionBox1)
    {
      LOG.logError(paramSampleDescriptionBox1.getMessage());
      return null;
    }
    throw new IOException("Cannot merge " + paramSampleDescriptionBox1.getBoxes(SampleEntry.class).get(0) + " and " + paramSampleDescriptionBox2.getBoxes(SampleEntry.class).get(0));
  }
  
  private VisualSampleEntry mergeVisualSampleEntry(VisualSampleEntry paramVisualSampleEntry1, VisualSampleEntry paramVisualSampleEntry2)
  {
    VisualSampleEntry localVisualSampleEntry = new VisualSampleEntry();
    if (paramVisualSampleEntry1.getHorizresolution() == paramVisualSampleEntry2.getHorizresolution())
    {
      localVisualSampleEntry.setHorizresolution(paramVisualSampleEntry1.getHorizresolution());
      localVisualSampleEntry.setCompressorname(paramVisualSampleEntry1.getCompressorname());
      if (paramVisualSampleEntry1.getDepth() != paramVisualSampleEntry2.getDepth()) {
        break label215;
      }
      localVisualSampleEntry.setDepth(paramVisualSampleEntry1.getDepth());
      if (paramVisualSampleEntry1.getFrameCount() != paramVisualSampleEntry2.getFrameCount()) {
        break label226;
      }
      localVisualSampleEntry.setFrameCount(paramVisualSampleEntry1.getFrameCount());
      if (paramVisualSampleEntry1.getHeight() != paramVisualSampleEntry2.getHeight()) {
        break label237;
      }
      localVisualSampleEntry.setHeight(paramVisualSampleEntry1.getHeight());
      if (paramVisualSampleEntry1.getWidth() != paramVisualSampleEntry2.getWidth()) {
        break label248;
      }
      localVisualSampleEntry.setWidth(paramVisualSampleEntry1.getWidth());
      if (paramVisualSampleEntry1.getVertresolution() != paramVisualSampleEntry2.getVertresolution()) {
        break label259;
      }
      localVisualSampleEntry.setVertresolution(paramVisualSampleEntry1.getVertresolution());
      if (paramVisualSampleEntry1.getHorizresolution() != paramVisualSampleEntry2.getHorizresolution()) {
        break label270;
      }
      localVisualSampleEntry.setHorizresolution(paramVisualSampleEntry1.getHorizresolution());
      if (paramVisualSampleEntry1.getBoxes().size() == paramVisualSampleEntry2.getBoxes().size())
      {
        paramVisualSampleEntry1 = paramVisualSampleEntry1.getBoxes().iterator();
        paramVisualSampleEntry2 = paramVisualSampleEntry2.getBoxes().iterator();
      }
    }
    for (;;)
    {
      if (!paramVisualSampleEntry1.hasNext())
      {
        return localVisualSampleEntry;
        LOG.logError("Horizontal Resolution differs");
        return null;
        label215:
        LOG.logError("Depth differs");
        return null;
        label226:
        LOG.logError("frame count differs");
        return null;
        label237:
        LOG.logError("height differs");
        return null;
        label248:
        LOG.logError("width differs");
        return null;
        label259:
        LOG.logError("vert resolution differs");
        return null;
        label270:
        LOG.logError("horizontal resolution differs");
        return null;
      }
      Box localBox = (Box)paramVisualSampleEntry1.next();
      Object localObject = (Box)paramVisualSampleEntry2.next();
      ByteArrayOutputStream localByteArrayOutputStream1 = new ByteArrayOutputStream();
      ByteArrayOutputStream localByteArrayOutputStream2 = new ByteArrayOutputStream();
      try
      {
        localBox.getBox(Channels.newChannel(localByteArrayOutputStream1));
        ((Box)localObject).getBox(Channels.newChannel(localByteArrayOutputStream2));
        if (Arrays.equals(localByteArrayOutputStream1.toByteArray(), localByteArrayOutputStream2.toByteArray())) {
          localVisualSampleEntry.addBox(localBox);
        }
      }
      catch (IOException paramVisualSampleEntry1)
      {
        LOG.logWarn(paramVisualSampleEntry1.getMessage());
        return null;
      }
      if (((localBox instanceof AbstractDescriptorBox)) && ((localObject instanceof AbstractDescriptorBox)))
      {
        localObject = mergeDescriptors(((AbstractDescriptorBox)localBox).getDescriptor(), ((AbstractDescriptorBox)localObject).getDescriptor());
        ((AbstractDescriptorBox)localBox).setDescriptor((BaseDescriptor)localObject);
        localVisualSampleEntry.addBox(localBox);
      }
    }
  }
  
  public void close()
    throws IOException
  {
    Track[] arrayOfTrack = this.tracks;
    int j = arrayOfTrack.length;
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return;
      }
      arrayOfTrack[i].close();
      i += 1;
    }
  }
  
  public List<CompositionTimeToSample.Entry> getCompositionTimeEntries()
  {
    if ((this.tracks[0].getCompositionTimeEntries() != null) && (!this.tracks[0].getCompositionTimeEntries().isEmpty()))
    {
      Object localObject2 = new LinkedList();
      Object localObject1 = this.tracks;
      int j = localObject1.length;
      int i = 0;
      for (;;)
      {
        if (i >= j)
        {
          localObject1 = new LinkedList();
          localObject2 = ((List)localObject2).iterator();
          if (((Iterator)localObject2).hasNext()) {
            break;
          }
          return (List<CompositionTimeToSample.Entry>)localObject1;
        }
        ((List)localObject2).add(CompositionTimeToSample.blowupCompositionTimes(localObject1[i].getCompositionTimeEntries()));
        i += 1;
      }
      int[] arrayOfInt = (int[])((Iterator)localObject2).next();
      j = arrayOfInt.length;
      i = 0;
      label135:
      if (i < j)
      {
        int k = arrayOfInt[i];
        if ((!((LinkedList)localObject1).isEmpty()) && (((CompositionTimeToSample.Entry)((LinkedList)localObject1).getLast()).getOffset() == k)) {
          break label190;
        }
        ((LinkedList)localObject1).add(new CompositionTimeToSample.Entry(1, k));
      }
      for (;;)
      {
        i += 1;
        break label135;
        break;
        label190:
        CompositionTimeToSample.Entry localEntry = (CompositionTimeToSample.Entry)((LinkedList)localObject1).getLast();
        localEntry.setCount(localEntry.getCount() + 1);
      }
    }
    return null;
  }
  
  public String getHandler()
  {
    return this.tracks[0].getHandler();
  }
  
  public List<SampleDependencyTypeBox.Entry> getSampleDependencies()
  {
    int i = 0;
    if ((this.tracks[0].getSampleDependencies() != null) && (!this.tracks[0].getSampleDependencies().isEmpty()))
    {
      LinkedList localLinkedList = new LinkedList();
      Track[] arrayOfTrack = this.tracks;
      int j = arrayOfTrack.length;
      for (;;)
      {
        if (i >= j) {
          return localLinkedList;
        }
        localLinkedList.addAll(arrayOfTrack[i].getSampleDependencies());
        i += 1;
      }
    }
    return null;
  }
  
  public SampleDescriptionBox getSampleDescriptionBox()
  {
    return this.stsd;
  }
  
  public long[] getSampleDurations()
  {
    int j = 0;
    for (;;)
    {
      Object localObject1;
      int k;
      int i;
      long[] arrayOfLong;
      int n;
      try
      {
        localObject1 = this.tracks;
        k = localObject1.length;
        i = 0;
        Track[] arrayOfTrack;
        if (i >= k)
        {
          localObject1 = new long[j];
          i = 0;
          arrayOfTrack = this.tracks;
          int m = arrayOfTrack.length;
          j = 0;
          if (j >= m) {
            return (long[])localObject1;
          }
        }
        else
        {
          j += localObject1[i].getSampleDurations().length;
          i += 1;
          continue;
        }
        arrayOfLong = arrayOfTrack[j].getSampleDurations();
        n = arrayOfLong.length;
        k = 0;
      }
      finally {}
      localObject1[i] = arrayOfLong[k];
      k += 1;
      i += 1;
      if (k >= n) {
        j += 1;
      }
    }
  }
  
  public List<Sample> getSamples()
  {
    ArrayList localArrayList = new ArrayList();
    Track[] arrayOfTrack = this.tracks;
    int j = arrayOfTrack.length;
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return localArrayList;
      }
      localArrayList.addAll(arrayOfTrack[i].getSamples());
      i += 1;
    }
  }
  
  public SubSampleInformationBox getSubsampleInformationBox()
  {
    return this.tracks[0].getSubsampleInformationBox();
  }
  
  public long[] getSyncSamples()
  {
    if ((this.tracks[0].getSyncSamples() != null) && (this.tracks[0].getSyncSamples().length > 0))
    {
      int j = 0;
      Object localObject = this.tracks;
      int k = localObject.length;
      int i = 0;
      long l;
      Track[] arrayOfTrack;
      for (;;)
      {
        if (i >= k)
        {
          localObject = new long[j];
          i = 0;
          l = 0L;
          arrayOfTrack = this.tracks;
          int m = arrayOfTrack.length;
          j = 0;
          if (j < m) {
            break;
          }
          return (long[])localObject;
        }
        j += localObject[i].getSyncSamples().length;
        i += 1;
      }
      Track localTrack = arrayOfTrack[j];
      long[] arrayOfLong = localTrack.getSyncSamples();
      int n = arrayOfLong.length;
      k = 0;
      for (;;)
      {
        if (k >= n)
        {
          l += localTrack.getSamples().size();
          j += 1;
          break;
        }
        localObject[i] = (l + arrayOfLong[k]);
        k += 1;
        i += 1;
      }
    }
    return null;
  }
  
  public TrackMetaData getTrackMetaData()
  {
    return this.tracks[0].getTrackMetaData();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/tracks/AppendTrack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */