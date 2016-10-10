package com.googlecode.mp4parser.authoring.builder;

import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class TwoSecondIntersectionFinder
  implements FragmentIntersectionFinder
{
  private int fragmentLength = 2;
  private Movie movie;
  
  public TwoSecondIntersectionFinder(Movie paramMovie, int paramInt)
  {
    this.movie = paramMovie;
    this.fragmentLength = paramInt;
  }
  
  public long[] sampleNumbers(Track paramTrack)
  {
    double d1 = 0.0D;
    Object localObject1 = this.movie.getTracks().iterator();
    int j;
    int i;
    Object localObject2;
    long l1;
    if (!((Iterator)localObject1).hasNext())
    {
      j = Math.min((int)Math.ceil(d1 / this.fragmentLength) - 1, paramTrack.getSamples().size());
      i = j;
      if (j < 1) {
        i = 1;
      }
      localObject2 = new long[i];
      Arrays.fill((long[])localObject2, -1L);
      localObject2[0] = 1L;
      l1 = 0L;
      localObject1 = paramTrack.getSampleDurations();
      int k = localObject1.length;
      j = 0;
      i = 0;
      label109:
      if (j < k) {
        break label202;
      }
      label116:
      l1 = i + 1;
      i = localObject2.length - 1;
      label130:
      if (i >= 0) {
        break label272;
      }
      paramTrack = new long[0];
      j = localObject2.length;
      i = 0;
    }
    for (;;)
    {
      if (i >= j)
      {
        return paramTrack;
        localObject2 = (Track)((Iterator)localObject1).next();
        double d2 = ((Track)localObject2).getDuration() / ((Track)localObject2).getTrackMetaData().getTimescale();
        if (d1 >= d2) {
          break;
        }
        d1 = d2;
        break;
        label202:
        long l2 = localObject1[j];
        int m = (int)(l1 / paramTrack.getTrackMetaData().getTimescale() / this.fragmentLength) + 1;
        if (m >= localObject2.length) {
          break label116;
        }
        localObject2[m] = (i + 1);
        l1 += l2;
        j += 1;
        i += 1;
        break label109;
        label272:
        if (localObject2[i] == -1L) {
          localObject2[i] = l1;
        }
        l1 = localObject2[i];
        i -= 1;
        break label130;
      }
      l1 = localObject2[i];
      if (paramTrack.length != 0)
      {
        localObject1 = paramTrack;
        if (paramTrack[(paramTrack.length - 1)] == l1) {}
      }
      else
      {
        localObject1 = Arrays.copyOf(paramTrack, paramTrack.length + 1);
        localObject1[(localObject1.length - 1)] = l1;
      }
      i += 1;
      paramTrack = (Track)localObject1;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/builder/TwoSecondIntersectionFinder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */