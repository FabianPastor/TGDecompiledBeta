package com.coremedia.iso.boxes.fragment;

import com.coremedia.iso.boxes.Box;
import com.googlecode.mp4parser.AbstractContainerBox;
import java.util.Iterator;
import java.util.List;

public class TrackFragmentBox
  extends AbstractContainerBox
{
  public static final String TYPE = "traf";
  
  public TrackFragmentBox()
  {
    super("traf");
  }
  
  public TrackFragmentHeaderBox getTrackFragmentHeaderBox()
  {
    Iterator localIterator = getBoxes().iterator();
    if (!localIterator.hasNext()) {}
    for (Object localObject = null;; localObject = (TrackFragmentHeaderBox)localObject)
    {
      return (TrackFragmentHeaderBox)localObject;
      localObject = (Box)localIterator.next();
      if (!(localObject instanceof TrackFragmentHeaderBox)) {
        break;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/fragment/TrackFragmentBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */