package org.telegram.messenger.exoplayer2.source;

import java.io.IOException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import org.telegram.messenger.exoplayer2.SeekParameters;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.util.Assertions;

final class MergingMediaPeriod
  implements MediaPeriod, MediaPeriod.Callback
{
  private MediaPeriod.Callback callback;
  private SequenceableLoader compositeSequenceableLoader;
  private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
  private MediaPeriod[] enabledPeriods;
  private int pendingChildPrepareCount;
  public final MediaPeriod[] periods;
  private final IdentityHashMap<SampleStream, Integer> streamPeriodIndices;
  private TrackGroupArray trackGroups;
  
  public MergingMediaPeriod(CompositeSequenceableLoaderFactory paramCompositeSequenceableLoaderFactory, MediaPeriod... paramVarArgs)
  {
    this.compositeSequenceableLoaderFactory = paramCompositeSequenceableLoaderFactory;
    this.periods = paramVarArgs;
    this.streamPeriodIndices = new IdentityHashMap();
  }
  
  public boolean continueLoading(long paramLong)
  {
    return this.compositeSequenceableLoader.continueLoading(paramLong);
  }
  
  public void discardBuffer(long paramLong, boolean paramBoolean)
  {
    MediaPeriod[] arrayOfMediaPeriod = this.enabledPeriods;
    int i = arrayOfMediaPeriod.length;
    for (int j = 0; j < i; j++) {
      arrayOfMediaPeriod[j].discardBuffer(paramLong, paramBoolean);
    }
  }
  
  public long getAdjustedSeekPositionUs(long paramLong, SeekParameters paramSeekParameters)
  {
    return this.enabledPeriods[0].getAdjustedSeekPositionUs(paramLong, paramSeekParameters);
  }
  
  public long getBufferedPositionUs()
  {
    return this.compositeSequenceableLoader.getBufferedPositionUs();
  }
  
  public long getNextLoadPositionUs()
  {
    return this.compositeSequenceableLoader.getNextLoadPositionUs();
  }
  
  public TrackGroupArray getTrackGroups()
  {
    return this.trackGroups;
  }
  
  public void maybeThrowPrepareError()
    throws IOException
  {
    MediaPeriod[] arrayOfMediaPeriod = this.periods;
    int i = arrayOfMediaPeriod.length;
    for (int j = 0; j < i; j++) {
      arrayOfMediaPeriod[j].maybeThrowPrepareError();
    }
  }
  
  public void onContinueLoadingRequested(MediaPeriod paramMediaPeriod)
  {
    if (this.trackGroups == null) {}
    for (;;)
    {
      return;
      this.callback.onContinueLoadingRequested(this);
    }
  }
  
  public void onPrepared(MediaPeriod paramMediaPeriod)
  {
    int i = 0;
    int j = this.pendingChildPrepareCount - 1;
    this.pendingChildPrepareCount = j;
    if (j > 0) {}
    for (;;)
    {
      return;
      int k = 0;
      paramMediaPeriod = this.periods;
      int m = paramMediaPeriod.length;
      for (j = 0; j < m; j++) {
        k += paramMediaPeriod[j].getTrackGroups().length;
      }
      paramMediaPeriod = new TrackGroup[k];
      j = 0;
      MediaPeriod[] arrayOfMediaPeriod = this.periods;
      m = arrayOfMediaPeriod.length;
      for (k = i; k < m; k++)
      {
        TrackGroupArray localTrackGroupArray = arrayOfMediaPeriod[k].getTrackGroups();
        int n = localTrackGroupArray.length;
        i = 0;
        while (i < n)
        {
          paramMediaPeriod[j] = localTrackGroupArray.get(i);
          i++;
          j++;
        }
      }
      this.trackGroups = new TrackGroupArray(paramMediaPeriod);
      this.callback.onPrepared(this);
    }
  }
  
  public void prepare(MediaPeriod.Callback paramCallback, long paramLong)
  {
    this.callback = paramCallback;
    this.pendingChildPrepareCount = this.periods.length;
    paramCallback = this.periods;
    int i = paramCallback.length;
    for (int j = 0; j < i; j++) {
      paramCallback[j].prepare(this, paramLong);
    }
  }
  
  public long readDiscontinuity()
  {
    long l = this.periods[0].readDiscontinuity();
    for (int i = 1; i < this.periods.length; i++) {
      if (this.periods[i].readDiscontinuity() != -9223372036854775807L) {
        throw new IllegalStateException("Child reported discontinuity");
      }
    }
    if (l != -9223372036854775807L) {
      for (MediaPeriod localMediaPeriod : this.enabledPeriods) {
        if ((localMediaPeriod != this.periods[0]) && (localMediaPeriod.seekToUs(l) != l)) {
          throw new IllegalStateException("Children seeked to different positions");
        }
      }
    }
    return l;
  }
  
  public void reevaluateBuffer(long paramLong)
  {
    this.compositeSequenceableLoader.reevaluateBuffer(paramLong);
  }
  
  public long seekToUs(long paramLong)
  {
    paramLong = this.enabledPeriods[0].seekToUs(paramLong);
    for (int i = 1; i < this.enabledPeriods.length; i++) {
      if (this.enabledPeriods[i].seekToUs(paramLong) != paramLong) {
        throw new IllegalStateException("Children seeked to different positions");
      }
    }
    return paramLong;
  }
  
  public long selectTracks(TrackSelection[] paramArrayOfTrackSelection, boolean[] paramArrayOfBoolean1, SampleStream[] paramArrayOfSampleStream, boolean[] paramArrayOfBoolean2, long paramLong)
  {
    int[] arrayOfInt1 = new int[paramArrayOfTrackSelection.length];
    int[] arrayOfInt2 = new int[paramArrayOfTrackSelection.length];
    int i = 0;
    int j;
    label32:
    Object localObject;
    if (i < paramArrayOfTrackSelection.length)
    {
      if (paramArrayOfSampleStream[i] == null)
      {
        j = -1;
        arrayOfInt1[i] = j;
        arrayOfInt2[i] = -1;
        if (paramArrayOfTrackSelection[i] != null) {
          localObject = paramArrayOfTrackSelection[i].getTrackGroup();
        }
      }
      for (j = 0;; j++) {
        if (j < this.periods.length)
        {
          if (this.periods[j].getTrackGroups().indexOf((TrackGroup)localObject) != -1) {
            arrayOfInt2[i] = j;
          }
        }
        else
        {
          i++;
          break;
          j = ((Integer)this.streamPeriodIndices.get(paramArrayOfSampleStream[i])).intValue();
          break label32;
        }
      }
    }
    this.streamPeriodIndices.clear();
    SampleStream[] arrayOfSampleStream1 = new SampleStream[paramArrayOfTrackSelection.length];
    SampleStream[] arrayOfSampleStream2 = new SampleStream[paramArrayOfTrackSelection.length];
    TrackSelection[] arrayOfTrackSelection = new TrackSelection[paramArrayOfTrackSelection.length];
    ArrayList localArrayList = new ArrayList(this.periods.length);
    i = 0;
    while (i < this.periods.length)
    {
      j = 0;
      if (j < paramArrayOfTrackSelection.length)
      {
        if (arrayOfInt1[j] == i)
        {
          localObject = paramArrayOfSampleStream[j];
          label219:
          arrayOfSampleStream2[j] = localObject;
          if (arrayOfInt2[j] != i) {
            break label261;
          }
        }
        label261:
        for (localObject = paramArrayOfTrackSelection[j];; localObject = null)
        {
          arrayOfTrackSelection[j] = localObject;
          j++;
          break;
          localObject = null;
          break label219;
        }
      }
      long l1 = this.periods[i].selectTracks(arrayOfTrackSelection, paramArrayOfBoolean1, arrayOfSampleStream2, paramArrayOfBoolean2, paramLong);
      long l2;
      int k;
      label305:
      label333:
      int m;
      if (i == 0)
      {
        l2 = l1;
        k = 0;
        j = 0;
        if (j >= paramArrayOfTrackSelection.length) {
          break label450;
        }
        if (arrayOfInt2[j] != i) {
          break label407;
        }
        if (arrayOfSampleStream2[j] == null) {
          break label401;
        }
        bool = true;
        Assertions.checkState(bool);
        arrayOfSampleStream1[j] = arrayOfSampleStream2[j];
        m = 1;
        this.streamPeriodIndices.put(arrayOfSampleStream2[j], Integer.valueOf(i));
      }
      label401:
      label407:
      do
      {
        j++;
        k = m;
        break label305;
        l2 = paramLong;
        if (l1 == paramLong) {
          break;
        }
        throw new IllegalStateException("Children enabled at different positions");
        bool = false;
        break label333;
        m = k;
      } while (arrayOfInt1[j] != i);
      if (arrayOfSampleStream2[j] == null) {}
      for (boolean bool = true;; bool = false)
      {
        Assertions.checkState(bool);
        m = k;
        break;
      }
      label450:
      if (k != 0) {
        localArrayList.add(this.periods[i]);
      }
      i++;
      paramLong = l2;
    }
    System.arraycopy(arrayOfSampleStream1, 0, paramArrayOfSampleStream, 0, arrayOfSampleStream1.length);
    this.enabledPeriods = new MediaPeriod[localArrayList.size()];
    localArrayList.toArray(this.enabledPeriods);
    this.compositeSequenceableLoader = this.compositeSequenceableLoaderFactory.createCompositeSequenceableLoader(this.enabledPeriods);
    return paramLong;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/MergingMediaPeriod.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */