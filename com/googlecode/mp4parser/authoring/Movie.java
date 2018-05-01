package com.googlecode.mp4parser.authoring;

import com.googlecode.mp4parser.util.Matrix;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Movie
{
  Matrix matrix = Matrix.ROTATE_0;
  List<Track> tracks = new LinkedList();
  
  public Movie() {}
  
  public Movie(List<Track> paramList)
  {
    this.tracks = paramList;
  }
  
  public static long gcd(long paramLong1, long paramLong2)
  {
    if (paramLong2 == 0L) {
      return paramLong1;
    }
    return gcd(paramLong2, paramLong1 % paramLong2);
  }
  
  public void addTrack(Track paramTrack)
  {
    if (getTrackByTrackId(paramTrack.getTrackMetaData().getTrackId()) != null) {
      paramTrack.getTrackMetaData().setTrackId(getNextTrackId());
    }
    this.tracks.add(paramTrack);
  }
  
  public Matrix getMatrix()
  {
    return this.matrix;
  }
  
  public long getNextTrackId()
  {
    long l1 = 0L;
    Iterator localIterator = this.tracks.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return l1 + 1L;
      }
      Track localTrack = (Track)localIterator.next();
      long l2 = l1;
      if (l1 < localTrack.getTrackMetaData().getTrackId()) {
        l2 = localTrack.getTrackMetaData().getTrackId();
      }
      l1 = l2;
    }
  }
  
  public long getTimescale()
  {
    long l = ((Track)getTracks().iterator().next()).getTrackMetaData().getTimescale();
    Iterator localIterator = getTracks().iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return l;
      }
      l = gcd(((Track)localIterator.next()).getTrackMetaData().getTimescale(), l);
    }
  }
  
  public Track getTrackByTrackId(long paramLong)
  {
    Iterator localIterator = this.tracks.iterator();
    Track localTrack;
    do
    {
      if (!localIterator.hasNext()) {
        return null;
      }
      localTrack = (Track)localIterator.next();
    } while (localTrack.getTrackMetaData().getTrackId() != paramLong);
    return localTrack;
  }
  
  public List<Track> getTracks()
  {
    return this.tracks;
  }
  
  public void setMatrix(Matrix paramMatrix)
  {
    this.matrix = paramMatrix;
  }
  
  public void setTracks(List<Track> paramList)
  {
    this.tracks = paramList;
  }
  
  public String toString()
  {
    String str = "Movie{ ";
    Iterator localIterator = this.tracks.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return str + '}';
      }
      Track localTrack = (Track)localIterator.next();
      str = str + "track_" + localTrack.getTrackMetaData().getTrackId() + " (" + localTrack.getHandler() + ") ";
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/Movie.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */