package com.googlecode.mp4parser.authoring.builder;

import com.coremedia.iso.boxes.OriginalFormatBox;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.sampleentry.AbstractSampleEntry;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import com.coremedia.iso.boxes.sampleentry.SampleEntry;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.util.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class SyncSampleIntersectFinderImpl
  implements FragmentIntersectionFinder
{
  private static Logger LOG = Logger.getLogger(SyncSampleIntersectFinderImpl.class.getName());
  private final int minFragmentDurationSeconds;
  private Movie movie;
  private Track referenceTrack;
  
  public SyncSampleIntersectFinderImpl(Movie paramMovie, Track paramTrack, int paramInt)
  {
    this.movie = paramMovie;
    this.referenceTrack = paramTrack;
    this.minFragmentDurationSeconds = paramInt;
  }
  
  private static long calculateTracktimesScalingFactor(Movie paramMovie, Track paramTrack)
  {
    long l = 1L;
    paramMovie = paramMovie.getTracks().iterator();
    for (;;)
    {
      if (!paramMovie.hasNext()) {
        return l;
      }
      Track localTrack = (Track)paramMovie.next();
      if ((localTrack.getHandler().equals(paramTrack.getHandler())) && (localTrack.getTrackMetaData().getTimescale() != paramTrack.getTrackMetaData().getTimescale())) {
        l = com.googlecode.mp4parser.util.Math.lcm(l, localTrack.getTrackMetaData().getTimescale());
      }
    }
  }
  
  static String getFormat(Track paramTrack)
  {
    AbstractSampleEntry localAbstractSampleEntry = paramTrack.getSampleDescriptionBox().getSampleEntry();
    String str = localAbstractSampleEntry.getType();
    if ((!str.equals("encv")) && (!str.equals("enca")))
    {
      paramTrack = str;
      if (!str.equals("encv")) {}
    }
    else
    {
      paramTrack = ((OriginalFormatBox)Path.getPath(localAbstractSampleEntry, "sinf/frma")).getDataFormat();
    }
    return paramTrack;
  }
  
  public static List<long[]> getSyncSamplesTimestamps(Movie paramMovie, Track paramTrack)
  {
    LinkedList localLinkedList = new LinkedList();
    Iterator localIterator = paramMovie.getTracks().iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return localLinkedList;
      }
      Track localTrack = (Track)localIterator.next();
      if (localTrack.getHandler().equals(paramTrack.getHandler()))
      {
        long[] arrayOfLong = localTrack.getSyncSamples();
        if ((arrayOfLong != null) && (arrayOfLong.length > 0)) {
          localLinkedList.add(getTimes(localTrack, paramMovie));
        }
      }
    }
  }
  
  private static long[] getTimes(Track paramTrack, Movie paramMovie)
  {
    long[] arrayOfLong1 = paramTrack.getSyncSamples();
    long[] arrayOfLong2 = new long[arrayOfLong1.length];
    int i = 1;
    long l1 = 0L;
    int j = 0;
    long l2 = calculateTracktimesScalingFactor(paramMovie, paramTrack);
    for (;;)
    {
      if (i > arrayOfLong1[(arrayOfLong1.length - 1)]) {
        return arrayOfLong2;
      }
      int k = j;
      if (i == arrayOfLong1[j])
      {
        arrayOfLong2[j] = (l1 * l2);
        k = j + 1;
      }
      l1 += paramTrack.getSampleDurations()[(i - 1)];
      i += 1;
      j = k;
    }
  }
  
  public long[] getCommonIndices(long[] paramArrayOfLong1, long[] paramArrayOfLong2, long paramLong, long[]... paramVarArgs)
  {
    Object localObject = new LinkedList();
    LinkedList localLinkedList = new LinkedList();
    int i = 0;
    label97:
    int j;
    if (i >= paramArrayOfLong2.length)
    {
      if (((List)localObject).size() >= paramArrayOfLong1.length * 0.25D) {
        break label430;
      }
      paramArrayOfLong2 = "" + String.format("%5d - Common:  [", new Object[] { Integer.valueOf(((List)localObject).size()) });
      paramVarArgs = ((List)localObject).iterator();
      if (paramVarArgs.hasNext()) {
        break label331;
      }
      paramArrayOfLong2 = paramArrayOfLong2 + "]";
      LOG.warning(paramArrayOfLong2);
      paramArrayOfLong2 = "" + String.format("%5d - In    :  [", new Object[] { Integer.valueOf(paramArrayOfLong1.length) });
      j = paramArrayOfLong1.length;
      i = 0;
    }
    for (;;)
    {
      if (i >= j)
      {
        paramArrayOfLong1 = paramArrayOfLong2 + "]";
        LOG.warning(paramArrayOfLong1);
        LOG.warning("There are less than 25% of common sync samples in the given track.");
        throw new RuntimeException("There are less than 25% of common sync samples in the given track.");
        int k = 1;
        int n = paramVarArgs.length;
        j = 0;
        if (j >= n)
        {
          if (k != 0)
          {
            ((List)localObject).add(Long.valueOf(paramArrayOfLong1[i]));
            localLinkedList.add(Long.valueOf(paramArrayOfLong2[i]));
          }
          i += 1;
          break;
        }
        if (Arrays.binarySearch(paramVarArgs[j], paramArrayOfLong2[i]) >= 0) {}
        for (int m = 1;; m = 0)
        {
          k &= m;
          j += 1;
          break;
        }
        label331:
        paramLong = ((Long)paramVarArgs.next()).longValue();
        paramArrayOfLong2 = paramArrayOfLong2 + String.format("%10d,", new Object[] { Long.valueOf(paramLong) });
        break label97;
      }
      paramLong = paramArrayOfLong1[i];
      paramArrayOfLong2 = paramArrayOfLong2 + String.format("%10d,", new Object[] { Long.valueOf(paramLong) });
      i += 1;
    }
    label430:
    long l1;
    if (((List)localObject).size() < paramArrayOfLong1.length * 0.5D)
    {
      LOG.fine("There are less than 50% of common sync samples in the given track. This is implausible but I'm ok to continue.");
      paramArrayOfLong2 = new LinkedList();
      if (this.minFragmentDurationSeconds <= 0) {
        break label667;
      }
      l1 = -1L;
      paramVarArgs = ((List)localObject).iterator();
      localObject = localLinkedList.iterator();
      label495:
      paramArrayOfLong1 = paramArrayOfLong2;
      if (paramVarArgs.hasNext())
      {
        if (((Iterator)localObject).hasNext()) {
          break label593;
        }
        paramArrayOfLong1 = paramArrayOfLong2;
      }
      label519:
      paramArrayOfLong2 = new long[paramArrayOfLong1.size()];
      i = 0;
    }
    for (;;)
    {
      if (i >= paramArrayOfLong2.length)
      {
        return paramArrayOfLong2;
        if (((List)localObject).size() >= paramArrayOfLong1.length) {
          break;
        }
        LOG.finest("Common SyncSample positions vs. this tracks SyncSample positions: " + ((List)localObject).size() + " vs. " + paramArrayOfLong1.length);
        break;
        label593:
        long l3 = ((Long)paramVarArgs.next()).longValue();
        long l2 = ((Long)((Iterator)localObject).next()).longValue();
        if ((l1 != -1L) && ((l2 - l1) / paramLong < this.minFragmentDurationSeconds)) {
          break label495;
        }
        paramArrayOfLong2.add(Long.valueOf(l3));
        l1 = l2;
        break label495;
        label667:
        paramArrayOfLong1 = (long[])localObject;
        break label519;
      }
      paramArrayOfLong2[i] = ((Long)paramArrayOfLong1.get(i)).longValue();
      i += 1;
    }
  }
  
  public long[] sampleNumbers(Track paramTrack)
  {
    if ("vide".equals(paramTrack.getHandler()))
    {
      if ((paramTrack.getSyncSamples() != null) && (paramTrack.getSyncSamples().length > 0))
      {
        localObject1 = getSyncSamplesTimestamps(this.movie, paramTrack);
        paramTrack = getCommonIndices(paramTrack.getSyncSamples(), getTimes(paramTrack, this.movie), paramTrack.getTrackMetaData().getTimescale(), (long[][])((List)localObject1).toArray(new long[((List)localObject1).size()][]));
        return paramTrack;
      }
      throw new RuntimeException("Video Tracks need sync samples. Only tracks other than video may have no sync samples.");
    }
    if ("soun".equals(paramTrack.getHandler()))
    {
      long l1;
      Iterator localIterator;
      if (this.referenceTrack == null)
      {
        localObject1 = this.movie.getTracks().iterator();
        if (((Iterator)localObject1).hasNext()) {}
      }
      else
      {
        if (this.referenceTrack == null) {
          break label511;
        }
        localObject2 = sampleNumbers(this.referenceTrack);
        i = this.referenceTrack.getSamples().size();
        localObject1 = new long[localObject2.length];
        l1 = 192000L;
        localIterator = this.movie.getTracks().iterator();
      }
      Track localTrack;
      AudioSampleEntry localAudioSampleEntry;
      do
      {
        do
        {
          if (!localIterator.hasNext())
          {
            localObject2 = (AudioSampleEntry)paramTrack.getSampleDescriptionBox().getSampleEntry();
            l2 = paramTrack.getSampleDurations()[0];
            d = ((AudioSampleEntry)localObject2).getSampleRate() / l1;
            if (d == Math.rint(d)) {
              break label468;
            }
            throw new RuntimeException("Sample rates must be a multiple of the lowest sample rate to create a correct file!");
            localObject2 = (Track)((Iterator)localObject1).next();
            if ((((Track)localObject2).getSyncSamples() == null) || (!"vide".equals(((Track)localObject2).getHandler())) || (((Track)localObject2).getSyncSamples().length <= 0)) {
              break;
            }
            this.referenceTrack = ((Track)localObject2);
            break;
          }
          localTrack = (Track)localIterator.next();
        } while (!getFormat(paramTrack).equals(getFormat(localTrack)));
        localAudioSampleEntry = (AudioSampleEntry)localTrack.getSampleDescriptionBox().getSampleEntry();
      } while (localAudioSampleEntry.getSampleRate() >= 192000L);
      long l2 = localAudioSampleEntry.getSampleRate();
      d = localTrack.getSamples().size() / i;
      long l3 = localTrack.getSampleDurations()[0];
      i = 0;
      for (;;)
      {
        l1 = l2;
        if (i >= localObject1.length) {
          break;
        }
        localObject1[i] = (Math.ceil((localObject2[i] - 1L) * d * l3));
        i += 1;
      }
      label468:
      i = 0;
      for (;;)
      {
        paramTrack = (Track)localObject1;
        if (i >= localObject1.length) {
          break;
        }
        localObject1[i] = ((1.0D + localObject1[i] * d / l2));
        i += 1;
      }
      label511:
      throw new RuntimeException("There was absolutely no Track with sync samples. I can't work with that!");
    }
    Object localObject2 = this.movie.getTracks().iterator();
    do
    {
      if (!((Iterator)localObject2).hasNext()) {
        throw new RuntimeException("There was absolutely no Track with sync samples. I can't work with that!");
      }
      localObject1 = (Track)((Iterator)localObject2).next();
    } while ((((Track)localObject1).getSyncSamples() == null) || (((Track)localObject1).getSyncSamples().length <= 0));
    localObject2 = sampleNumbers((Track)localObject1);
    int i = ((Track)localObject1).getSamples().size();
    Object localObject1 = new long[localObject2.length];
    double d = paramTrack.getSamples().size() / i;
    i = 0;
    for (;;)
    {
      paramTrack = (Track)localObject1;
      if (i >= localObject1.length) {
        break;
      }
      localObject1[i] = (Math.ceil((localObject2[i] - 1L) * d) + 1L);
      i += 1;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/builder/SyncSampleIntersectFinderImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */