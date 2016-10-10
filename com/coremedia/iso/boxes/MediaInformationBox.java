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
    Iterator localIterator = getBoxes().iterator();
    Box localBox;
    do
    {
      if (!localIterator.hasNext()) {
        return null;
      }
      localBox = (Box)localIterator.next();
    } while (!(localBox instanceof AbstractMediaHeaderBox));
    return (AbstractMediaHeaderBox)localBox;
  }
  
  public SampleTableBox getSampleTableBox()
  {
    Iterator localIterator = getBoxes().iterator();
    Box localBox;
    do
    {
      if (!localIterator.hasNext()) {
        return null;
      }
      localBox = (Box)localIterator.next();
    } while (!(localBox instanceof SampleTableBox));
    return (SampleTableBox)localBox;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/MediaInformationBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */