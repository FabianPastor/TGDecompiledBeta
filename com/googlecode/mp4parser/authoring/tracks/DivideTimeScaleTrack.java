package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.boxes.CompositionTimeToSample.Entry;
import com.coremedia.iso.boxes.SampleDependencyTypeBox.Entry;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.googlecode.mp4parser.authoring.Edit;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DivideTimeScaleTrack
  implements Track
{
  Track source;
  private int timeScaleDivisor;
  
  public DivideTimeScaleTrack(Track paramTrack, int paramInt)
  {
    this.source = paramTrack;
    this.timeScaleDivisor = paramInt;
  }
  
  List<CompositionTimeToSample.Entry> adjustCtts()
  {
    Object localObject = this.source.getCompositionTimeEntries();
    if (localObject != null)
    {
      ArrayList localArrayList = new ArrayList(((List)localObject).size());
      localObject = ((List)localObject).iterator();
      for (;;)
      {
        if (!((Iterator)localObject).hasNext()) {
          return localArrayList;
        }
        CompositionTimeToSample.Entry localEntry = (CompositionTimeToSample.Entry)((Iterator)localObject).next();
        localArrayList.add(new CompositionTimeToSample.Entry(localEntry.getCount(), localEntry.getOffset() / this.timeScaleDivisor));
      }
    }
    return null;
  }
  
  public void close()
    throws IOException
  {
    this.source.close();
  }
  
  public List<CompositionTimeToSample.Entry> getCompositionTimeEntries()
  {
    return adjustCtts();
  }
  
  public long getDuration()
  {
    long l = 0L;
    long[] arrayOfLong = getSampleDurations();
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
    return this.source.getEdits();
  }
  
  public String getHandler()
  {
    return this.source.getHandler();
  }
  
  public String getName()
  {
    return "timscale(" + this.source.getName() + ")";
  }
  
  public List<SampleDependencyTypeBox.Entry> getSampleDependencies()
  {
    return this.source.getSampleDependencies();
  }
  
  public SampleDescriptionBox getSampleDescriptionBox()
  {
    return this.source.getSampleDescriptionBox();
  }
  
  public long[] getSampleDurations()
  {
    long[] arrayOfLong = new long[this.source.getSampleDurations().length];
    int i = 0;
    for (;;)
    {
      if (i >= this.source.getSampleDurations().length) {
        return arrayOfLong;
      }
      arrayOfLong[i] = (this.source.getSampleDurations()[i] / this.timeScaleDivisor);
      i += 1;
    }
  }
  
  public Map<GroupEntry, long[]> getSampleGroups()
  {
    return this.source.getSampleGroups();
  }
  
  public List<Sample> getSamples()
  {
    return this.source.getSamples();
  }
  
  public SubSampleInformationBox getSubsampleInformationBox()
  {
    return this.source.getSubsampleInformationBox();
  }
  
  public long[] getSyncSamples()
  {
    return this.source.getSyncSamples();
  }
  
  public TrackMetaData getTrackMetaData()
  {
    TrackMetaData localTrackMetaData = (TrackMetaData)this.source.getTrackMetaData().clone();
    localTrackMetaData.setTimescale(this.source.getTrackMetaData().getTimescale() / this.timeScaleDivisor);
    return localTrackMetaData;
  }
  
  public String toString()
  {
    return "MultiplyTimeScaleTrack{source=" + this.source + '}';
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/tracks/DivideTimeScaleTrack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */