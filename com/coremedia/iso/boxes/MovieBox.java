package com.coremedia.iso.boxes;

import com.googlecode.mp4parser.AbstractContainerBox;
import java.util.Iterator;
import java.util.List;

public class MovieBox
  extends AbstractContainerBox
{
  public static final String TYPE = "moov";
  
  public MovieBox()
  {
    super("moov");
  }
  
  public MovieHeaderBox getMovieHeaderBox()
  {
    Object localObject = getBoxes().iterator();
    if (!((Iterator)localObject).hasNext()) {}
    Box localBox;
    for (localObject = null;; localObject = (MovieHeaderBox)localBox)
    {
      return (MovieHeaderBox)localObject;
      localBox = (Box)((Iterator)localObject).next();
      if (!(localBox instanceof MovieHeaderBox)) {
        break;
      }
    }
  }
  
  public int getTrackCount()
  {
    return getBoxes(TrackBox.class).size();
  }
  
  public long[] getTrackNumbers()
  {
    List localList = getBoxes(TrackBox.class);
    long[] arrayOfLong = new long[localList.size()];
    for (int i = 0;; i++)
    {
      if (i >= localList.size()) {
        return arrayOfLong;
      }
      arrayOfLong[i] = ((TrackBox)localList.get(i)).getTrackHeaderBox().getTrackId();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/MovieBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */