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
    Box localBox;
    do
    {
      if (!localIterator.hasNext()) {
        return null;
      }
      localBox = (Box)localIterator.next();
    } while (!(localBox instanceof HandlerBox));
    return (HandlerBox)localBox;
  }
  
  public MediaHeaderBox getMediaHeaderBox()
  {
    Iterator localIterator = getBoxes().iterator();
    Box localBox;
    do
    {
      if (!localIterator.hasNext()) {
        return null;
      }
      localBox = (Box)localIterator.next();
    } while (!(localBox instanceof MediaHeaderBox));
    return (MediaHeaderBox)localBox;
  }
  
  public MediaInformationBox getMediaInformationBox()
  {
    Iterator localIterator = getBoxes().iterator();
    Box localBox;
    do
    {
      if (!localIterator.hasNext()) {
        return null;
      }
      localBox = (Box)localIterator.next();
    } while (!(localBox instanceof MediaInformationBox));
    return (MediaInformationBox)localBox;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/MediaBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */