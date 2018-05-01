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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ChangeTimeScaleTrack
  implements Track
{
  private static final Logger LOG = Logger.getLogger(ChangeTimeScaleTrack.class.getName());
  List<CompositionTimeToSample.Entry> ctts;
  long[] decodingTimes;
  Track source;
  long timeScale;
  
  public ChangeTimeScaleTrack(Track paramTrack, long paramLong, long[] paramArrayOfLong)
  {
    this.source = paramTrack;
    this.timeScale = paramLong;
    double d = paramLong / paramTrack.getTrackMetaData().getTimescale();
    this.ctts = adjustCtts(paramTrack.getCompositionTimeEntries(), d);
    this.decodingTimes = adjustTts(paramTrack.getSampleDurations(), d, paramArrayOfLong, getTimes(paramTrack, paramArrayOfLong, paramLong));
  }
  
  static List<CompositionTimeToSample.Entry> adjustCtts(List<CompositionTimeToSample.Entry> paramList, double paramDouble)
  {
    if (paramList != null)
    {
      ArrayList localArrayList = new ArrayList(paramList.size());
      paramList = paramList.iterator();
      for (;;)
      {
        if (!paramList.hasNext()) {
          return localArrayList;
        }
        CompositionTimeToSample.Entry localEntry = (CompositionTimeToSample.Entry)paramList.next();
        localArrayList.add(new CompositionTimeToSample.Entry(localEntry.getCount(), (int)Math.round(localEntry.getOffset() * paramDouble)));
      }
    }
    return null;
  }
  
  static long[] adjustTts(long[] paramArrayOfLong1, double paramDouble, long[] paramArrayOfLong2, long[] paramArrayOfLong3)
  {
    long l1 = 0L;
    long[] arrayOfLong = new long[paramArrayOfLong1.length];
    int i = 1;
    for (;;)
    {
      if (i > paramArrayOfLong1.length) {
        return arrayOfLong;
      }
      long l3 = Math.round(paramArrayOfLong1[(i - 1)] * paramDouble);
      int j = Arrays.binarySearch(paramArrayOfLong2, i + 1);
      long l2 = l3;
      if (j >= 0)
      {
        l2 = l3;
        if (paramArrayOfLong3[j] != l1)
        {
          l2 = paramArrayOfLong3[j] - (l1 + l3);
          LOG.finest(String.format("Sample %d %d / %d - correct by %d", new Object[] { Integer.valueOf(i), Long.valueOf(l1), Long.valueOf(paramArrayOfLong3[j]), Long.valueOf(l2) }));
          l2 = l3 + l2;
        }
      }
      l1 += l2;
      arrayOfLong[(i - 1)] = l2;
      i += 1;
    }
  }
  
  private static long[] getTimes(Track paramTrack, long[] paramArrayOfLong, long paramLong)
  {
    long[] arrayOfLong = new long[paramArrayOfLong.length];
    int i = 1;
    long l = 0L;
    int k;
    for (int j = 0;; j = k)
    {
      if (i > paramArrayOfLong[(paramArrayOfLong.length - 1)]) {
        return arrayOfLong;
      }
      k = j;
      if (i == paramArrayOfLong[j])
      {
        arrayOfLong[j] = (l * paramLong / paramTrack.getTrackMetaData().getTimescale());
        k = j + 1;
      }
      l += paramTrack.getSampleDurations()[(i - 1)];
      i += 1;
    }
  }
  
  public void close()
    throws IOException
  {
    this.source.close();
  }
  
  public List<CompositionTimeToSample.Entry> getCompositionTimeEntries()
  {
    return this.ctts;
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
    return this.source.getEdits();
  }
  
  public String getHandler()
  {
    return this.source.getHandler();
  }
  
  public String getName()
  {
    return "timeScale(" + this.source.getName() + ")";
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
    return this.decodingTimes;
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
    localTrackMetaData.setTimescale(this.timeScale);
    return localTrackMetaData;
  }
  
  public String toString()
  {
    return "ChangeTimeScaleTrack{source=" + this.source + '}';
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/tracks/ChangeTimeScaleTrack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */