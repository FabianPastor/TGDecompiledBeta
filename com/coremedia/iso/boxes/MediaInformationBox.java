package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractContainerBox;
import java.util.Iterator;
import java.util.List;

public class MediaInformationBox
  extends AbstractContainerBox
{
  public static final String TYPE = "minf";
  
  public MediaInformationBox()
  {
    super("minf");
  }
  
  public AbstractMediaHeaderBox getMediaHeaderBox()
  {
    Object localObject = getBoxes().iterator();
    if (!((Iterator)localObject).hasNext()) {}
    Box localBox;
    for (localObject = null;; localObject = (AbstractMediaHeaderBox)localBox)
    {
      return (AbstractMediaHeaderBox)localObject;
      localBox = (Box)((Iterator)localObject).next();
      if (!(localBox instanceof AbstractMediaHeaderBox)) {
        break;
      }
    }
  }
  
  public SampleTableBox getSampleTableBox()
  {
    Object localObject = getBoxes().iterator();
    if (!((Iterator)localObject).hasNext()) {}
    Box localBox;
    for (localObject = null;; localObject = (SampleTableBox)localBox)
    {
      return (SampleTableBox)localObject;
      localBox = (Box)((Iterator)localObject).next();
      if (!(localBox instanceof SampleTableBox)) {
        break;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/MediaInformationBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */