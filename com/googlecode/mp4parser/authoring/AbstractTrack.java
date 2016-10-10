package com.googlecode.mp4parser.authoring;

import com.coremedia.iso.boxes.CompositionTimeToSample.Entry;
import com.coremedia.iso.boxes.SampleDependencyTypeBox.Entry;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractTrack
  implements Track
{
  List<Edit> edits = new ArrayList();
  String name;
  Map<GroupEntry, long[]> sampleGroups = new HashMap();
  
  public AbstractTrack(String paramString)
  {
    this.name = paramString;
  }
  
  public List<CompositionTimeToSample.Entry> getCompositionTimeEntries()
  {
    return null;
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
    return this.edits;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public List<SampleDependencyTypeBox.Entry> getSampleDependencies()
  {
    return null;
  }
  
  public Map<GroupEntry, long[]> getSampleGroups()
  {
    return this.sampleGroups;
  }
  
  public SubSampleInformationBox getSubsampleInformationBox()
  {
    return null;
  }
  
  public long[] getSyncSamples()
  {
    return null;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/AbstractTrack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */