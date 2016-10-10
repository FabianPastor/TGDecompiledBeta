package com.coremedia.iso.boxes.fragment;

import com.coremedia.iso.boxes.Box;
import com.googlecode.mp4parser.AbstractContainerBox;
import com.googlecode.mp4parser.annotations.DoNotParseDetail;
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
  
  @DoNotParseDetail
  public TrackFragmentHeaderBox getTrackFragmentHeaderBox()
  {
    Iterator localIterator = getBoxes().iterator();
    Box localBox;
    do
    {
      if (!localIterator.hasNext()) {
        return null;
      }
      localBox = (Box)localIterator.next();
    } while (!(localBox instanceof TrackFragmentHeaderBox));
    return (TrackFragmentHeaderBox)localBox;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/fragment/TrackFragmentBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */