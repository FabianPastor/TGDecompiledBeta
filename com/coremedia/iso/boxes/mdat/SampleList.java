package com.coremedia.iso.boxes.mdat;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.coremedia.iso.boxes.fragment.MovieExtendsBox;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.samples.DefaultMp4SampleList;
import com.googlecode.mp4parser.authoring.samples.FragmentedMp4SampleList;
import java.util.AbstractList;
import java.util.List;

public class SampleList
  extends AbstractList<Sample>
{
  List<Sample> samples;
  
  public SampleList(TrackBox paramTrackBox, IsoFile... paramVarArgs)
  {
    Container localContainer = ((Box)paramTrackBox.getParent()).getParent();
    if (paramTrackBox.getParent().getBoxes(MovieExtendsBox.class).isEmpty()) {
      if (paramVarArgs.length > 0) {
        throw new RuntimeException("The TrackBox comes from a standard MP4 file. Only use the additionalFragments param if you are dealing with ( fragmented MP4 files AND additional fragments in standalone files )");
      }
    }
    for (this.samples = new DefaultMp4SampleList(paramTrackBox.getTrackHeaderBox().getTrackId(), localContainer);; this.samples = new FragmentedMp4SampleList(paramTrackBox.getTrackHeaderBox().getTrackId(), localContainer, paramVarArgs)) {
      return;
    }
  }
  
  public Sample get(int paramInt)
  {
    return (Sample)this.samples.get(paramInt);
  }
  
  public int size()
  {
    return this.samples.size();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/mdat/SampleList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */