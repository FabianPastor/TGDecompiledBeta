package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractContainerBox;
import java.util.Iterator;
import java.util.List;

public class TrackBox
  extends AbstractContainerBox
{
  public static final String TYPE = "trak";
  private SampleTableBox sampleTableBox;
  
  public TrackBox()
  {
    super("trak");
  }
  
  public MediaBox getMediaBox()
  {
    Object localObject = getBoxes().iterator();
    if (!((Iterator)localObject).hasNext()) {}
    Box localBox;
    for (localObject = null;; localObject = (MediaBox)localBox)
    {
      return (MediaBox)localObject;
      localBox = (Box)((Iterator)localObject).next();
      if (!(localBox instanceof MediaBox)) {
        break;
      }
    }
  }
  
  public SampleTableBox getSampleTableBox()
  {
    Object localObject;
    if (this.sampleTableBox != null) {
      localObject = this.sampleTableBox;
    }
    for (;;)
    {
      return (SampleTableBox)localObject;
      localObject = getMediaBox();
      if (localObject != null)
      {
        localObject = ((MediaBox)localObject).getMediaInformationBox();
        if (localObject != null)
        {
          this.sampleTableBox = ((MediaInformationBox)localObject).getSampleTableBox();
          localObject = this.sampleTableBox;
          continue;
        }
      }
      localObject = null;
    }
  }
  
  public TrackHeaderBox getTrackHeaderBox()
  {
    Iterator localIterator = getBoxes().iterator();
    if (!localIterator.hasNext()) {}
    for (Object localObject = null;; localObject = (TrackHeaderBox)localObject)
    {
      return (TrackHeaderBox)localObject;
      localObject = (Box)localIterator.next();
      if (!(localObject instanceof TrackHeaderBox)) {
        break;
      }
    }
  }
  
  public void setBoxes(List<Box> paramList)
  {
    super.setBoxes(paramList);
    this.sampleTableBox = null;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/TrackBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */