package com.googlecode.mp4parser.authoring.container.mp4;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.MovieHeaderBox;
import com.coremedia.iso.boxes.SchemeTypeBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.CencMp4TrackImplImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Mp4TrackImpl;
import com.googlecode.mp4parser.util.Path;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class MovieCreator
{
  public static Movie build(DataSource paramDataSource)
    throws IOException
  {
    IsoFile localIsoFile = new IsoFile(paramDataSource);
    Movie localMovie = new Movie();
    Iterator localIterator = localIsoFile.getMovieBox().getBoxes(TrackBox.class).iterator();
    for (;;)
    {
      if (!localIterator.hasNext())
      {
        localMovie.setMatrix(localIsoFile.getMovieBox().getMovieHeaderBox().getMatrix());
        return localMovie;
      }
      TrackBox localTrackBox = (TrackBox)localIterator.next();
      SchemeTypeBox localSchemeTypeBox = (SchemeTypeBox)Path.getPath(localTrackBox, "mdia[0]/minf[0]/stbl[0]/stsd[0]/enc.[0]/sinf[0]/schm[0]");
      if ((localSchemeTypeBox != null) && ((localSchemeTypeBox.getSchemeType().equals("cenc")) || (localSchemeTypeBox.getSchemeType().equals("cbc1")))) {
        localMovie.addTrack(new CencMp4TrackImplImpl(paramDataSource.toString() + "[" + localTrackBox.getTrackHeaderBox().getTrackId() + "]", localTrackBox, new IsoFile[0]));
      } else {
        localMovie.addTrack(new Mp4TrackImpl(paramDataSource.toString() + "[" + localTrackBox.getTrackHeaderBox().getTrackId() + "]", localTrackBox, new IsoFile[0]));
      }
    }
  }
  
  public static Movie build(String paramString)
    throws IOException
  {
    return build(new FileDataSourceImpl(new File(paramString)));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/container/mp4/MovieCreator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */