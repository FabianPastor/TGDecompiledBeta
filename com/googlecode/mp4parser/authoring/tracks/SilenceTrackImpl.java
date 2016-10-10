package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.boxes.CompositionTimeToSample.Entry;
import com.coremedia.iso.boxes.SampleDependencyTypeBox.Entry;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.coremedia.iso.boxes.sampleentry.AbstractSampleEntry;
import com.googlecode.mp4parser.authoring.Edit;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.SampleImpl;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry;
import com.googlecode.mp4parser.util.CastUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SilenceTrackImpl
  implements Track
{
  long[] decodingTimes;
  String name;
  List<Sample> samples = new LinkedList();
  Track source;
  
  public SilenceTrackImpl(Track paramTrack, long paramLong)
  {
    this.source = paramTrack;
    this.name = (paramLong + "ms silence");
    if ("mp4a".equals(paramTrack.getSampleDescriptionBox().getSampleEntry().getType()))
    {
      int i = CastUtils.l2i(getTrackMetaData().getTimescale() * paramLong / 1000L / 1024L);
      this.decodingTimes = new long[i];
      Arrays.fill(this.decodingTimes, getTrackMetaData().getTimescale() * paramLong / i / 1000L);
      for (;;)
      {
        if (i <= 0) {
          return;
        }
        this.samples.add(new SampleImpl((ByteBuffer)ByteBuffer.wrap(new byte[] { 33, 16, 4, 96, -116, 28 }).rewind()));
        i -= 1;
      }
    }
    throw new RuntimeException("Tracks of type " + paramTrack.getClass().getSimpleName() + " are not supported");
  }
  
  public void close()
    throws IOException
  {}
  
  public List<CompositionTimeToSample.Entry> getCompositionTimeEntries()
  {
    return null;
  }
  
  public long getDuration()
  {
    long l = 0L;
    long[] arrayOfLong = this.decodingTimes;
    int j = arrayOfLong.length;
    int i = 0;
    for (;;)
    {
      if (i >= j) {
        return l;
      }
      l += arrayOfLong[i];
      i += 1;
    }
  }
  
  public List<Edit> getEdits()
  {
    return null;
  }
  
  public String getHandler()
  {
    return this.source.getHandler();
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public List<SampleDependencyTypeBox.Entry> getSampleDependencies()
  {
    return null;
  }
  
  public SampleDescriptionBox getSampleDescriptionBox()
  {
    return this.source.getSampleDescriptionBox();
  }
  
  public long[] getSampleDurations()
  {
    return this.decodingTimes;
  }
  
  public Map<GroupEntry, long[]> getSampleGroups()
  {
    return this.source.getSampleGroups();
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
    return this.source.getTrackMetaData();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/tracks/SilenceTrackImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */