package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractContainerBox;
import java.util.Iterator;
import java.util.List;

public class MediaBox
  extends AbstractContainerBox
{
  public static final String TYPE = "mdia";
  
  public MediaBox()
  {
    super("mdia");
  }
  
  public HandlerBox getHandlerBox()
  {
    Iterator localIterator = getBoxes().iterator();
    if (!localIterator.hasNext()) {}
    for (Object localObject = null;; localObject = (HandlerBox)localObject)
    {
      return (HandlerBox)localObject;
      localObject = (Box)localIterator.next();
      if (!(localObject instanceof HandlerBox)) {
        break;
      }
    }
  }
  
  public MediaHeaderBox getMediaHeaderBox()
  {
    Object localObject = getBoxes().iterator();
    if (!((Iterator)localObject).hasNext()) {}
    Box localBox;
    for (localObject = null;; localObject = (MediaHeaderBox)localBox)
    {
      return (MediaHeaderBox)localObject;
      localBox = (Box)((Iterator)localObject).next();
      if (!(localBox instanceof MediaHeaderBox)) {
        break;
      }
    }
  }
  
  public MediaInformationBox getMediaInformationBox()
  {
    Object localObject = getBoxes().iterator();
    if (!((Iterator)localObject).hasNext()) {}
    Box localBox;
    for (localObject = null;; localObject = (MediaInformationBox)localBox)
    {
      return (MediaInformationBox)localObject;
      localBox = (Box)((Iterator)localObject).next();
      if (!(localBox instanceof MediaInformationBox)) {
        break;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/MediaBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */