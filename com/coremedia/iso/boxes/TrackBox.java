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
    Iterator localIterator = getBoxes().iterator();
    Box localBox;
    do
    {
      if (!localIterator.hasNext()) {
        return null;
      }
      localBox = (Box)localIterator.next();
    } while (!(localBox instanceof MediaBox));
    return (MediaBox)localBox;
  }
  
  public SampleTableBox getSampleTableBox()
  {
    if (this.sampleTableBox != null) {
      return this.sampleTableBox;
    }
    Object localObject = getMediaBox();
    if (localObject != null)
    {
      localObject = ((MediaBox)localObject).getMediaInformationBox();
      if (localObject != null)
      {
        this.sampleTableBox = ((MediaInformationBox)localObject).getSampleTableBox();
        return this.sampleTableBox;
      }
    }
    return null;
  }
  
  public TrackHeaderBox getTrackHeaderBox()
  {
    Iterator localIterator = getBoxes().iterator();
    Box localBox;
    do
    {
      if (!localIterator.hasNext()) {
        return null;
      }
      localBox = (Box)localIterator.next();
    } while (!(localBox instanceof TrackHeaderBox));
    return (TrackHeaderBox)localBox;
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