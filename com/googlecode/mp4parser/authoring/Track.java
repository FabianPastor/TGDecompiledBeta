package com.googlecode.mp4parser.authoring;

import com.coremedia.iso.boxes.CompositionTimeToSample.Entry;
import com.coremedia.iso.boxes.SampleDependencyTypeBox.Entry;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry;
import java.io.Closeable;
import java.util.List;
import java.util.Map;

public abstract interface Track
  extends Closeable
{
  public abstract List<CompositionTimeToSample.Entry> getCompositionTimeEntries();
  
  public abstract long getDuration();
  
  public abstract List<Edit> getEdits();
  
  public abstract String getHandler();
  
  public abstract String getName();
  
  public abstract List<SampleDependencyTypeBox.Entry> getSampleDependencies();
  
  public abstract SampleDescriptionBox getSampleDescriptionBox();
  
  public abstract long[] getSampleDurations();
  
  public abstract Map<GroupEntry, long[]> getSampleGroups();
  
  public abstract List<Sample> getSamples();
  
  public abstract SubSampleInformationBox getSubsampleInformationBox();
  
  public abstract long[] getSyncSamples();
  
  public abstract TrackMetaData getTrackMetaData();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/Track.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */