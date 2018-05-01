package com.googlecode.mp4parser.authoring;

import com.coremedia.iso.boxes.CompositionTimeToSample.Entry;
import com.coremedia.iso.boxes.SampleDependencyTypeBox.Entry;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class WrappingTrack
  implements Track
{
  Track parent;
  
  public WrappingTrack(Track paramTrack)
  {
    this.parent = paramTrack;
  }
  
  public void close()
    throws IOException
  {
    this.parent.close();
  }
  
  public List<CompositionTimeToSample.Entry> getCompositionTimeEntries()
  {
    return this.parent.getCompositionTimeEntries();
  }
  
  public long getDuration()
  {
    return this.parent.getDuration();
  }
  
  public List<Edit> getEdits()
  {
    return this.parent.getEdits();
  }
  
  public String getHandler()
  {
    return this.parent.getHandler();
  }
  
  public String getName()
  {
    return this.parent.getName() + "'";
  }
  
  public List<SampleDependencyTypeBox.Entry> getSampleDependencies()
  {
    return this.parent.getSampleDependencies();
  }
  
  public SampleDescriptionBox getSampleDescriptionBox()
  {
    return this.parent.getSampleDescriptionBox();
  }
  
  public long[] getSampleDurations()
  {
    return this.parent.getSampleDurations();
  }
  
  public Map<GroupEntry, long[]> getSampleGroups()
  {
    return this.parent.getSampleGroups();
  }
  
  public List<Sample> getSamples()
  {
    return this.parent.getSamples();
  }
  
  public SubSampleInformationBox getSubsampleInformationBox()
  {
    return this.parent.getSubsampleInformationBox();
  }
  
  public long[] getSyncSamples()
  {
    return this.parent.getSyncSamples();
  }
  
  public TrackMetaData getTrackMetaData()
  {
    return this.parent.getTrackMetaData();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/WrappingTrack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */