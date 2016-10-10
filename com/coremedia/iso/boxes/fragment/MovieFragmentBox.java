package com.coremedia.iso.boxes.fragment;

import com.coremedia.iso.boxes.SampleDependencyTypeBox;
import com.coremedia.iso.boxes.SampleDependencyTypeBox.Entry;
import com.googlecode.mp4parser.AbstractContainerBox;
import com.googlecode.mp4parser.DataSource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MovieFragmentBox
  extends AbstractContainerBox
{
  public static final String TYPE = "moof";
  
  public MovieFragmentBox()
  {
    super("moof");
  }
  
  public DataSource getFileChannel()
  {
    return this.dataSource;
  }
  
  public List<Long> getSyncSamples(SampleDependencyTypeBox paramSampleDependencyTypeBox)
  {
    ArrayList localArrayList = new ArrayList();
    paramSampleDependencyTypeBox = paramSampleDependencyTypeBox.getEntries();
    long l = 1L;
    paramSampleDependencyTypeBox = paramSampleDependencyTypeBox.iterator();
    for (;;)
    {
      if (!paramSampleDependencyTypeBox.hasNext()) {
        return localArrayList;
      }
      if (((SampleDependencyTypeBox.Entry)paramSampleDependencyTypeBox.next()).getSampleDependsOn() == 2) {
        localArrayList.add(Long.valueOf(l));
      }
      l += 1L;
    }
  }
  
  public int getTrackCount()
  {
    return getBoxes(TrackFragmentBox.class, false).size();
  }
  
  public List<TrackFragmentHeaderBox> getTrackFragmentHeaderBoxes()
  {
    return getBoxes(TrackFragmentHeaderBox.class, true);
  }
  
  public long[] getTrackNumbers()
  {
    List localList = getBoxes(TrackFragmentBox.class, false);
    long[] arrayOfLong = new long[localList.size()];
    int i = 0;
    for (;;)
    {
      if (i >= localList.size()) {
        return arrayOfLong;
      }
      arrayOfLong[i] = ((TrackFragmentBox)localList.get(i)).getTrackFragmentHeaderBox().getTrackId();
      i += 1;
    }
  }
  
  public List<TrackRunBox> getTrackRunBoxes()
  {
    return getBoxes(TrackRunBox.class, true);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/fragment/MovieFragmentBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */