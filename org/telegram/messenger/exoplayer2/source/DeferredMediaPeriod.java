package org.telegram.messenger.exoplayer2.source;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.SeekParameters;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.upstream.Allocator;

public final class DeferredMediaPeriod
  implements MediaPeriod, MediaPeriod.Callback
{
  private final Allocator allocator;
  private MediaPeriod.Callback callback;
  private final MediaSource.MediaPeriodId id;
  private MediaPeriod mediaPeriod;
  public final MediaSource mediaSource;
  private long preparePositionUs;
  
  public DeferredMediaPeriod(MediaSource paramMediaSource, MediaSource.MediaPeriodId paramMediaPeriodId, Allocator paramAllocator)
  {
    this.id = paramMediaPeriodId;
    this.allocator = paramAllocator;
    this.mediaSource = paramMediaSource;
  }
  
  public boolean continueLoading(long paramLong)
  {
    if ((this.mediaPeriod != null) && (this.mediaPeriod.continueLoading(paramLong))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void createPeriod()
  {
    this.mediaPeriod = this.mediaSource.createPeriod(this.id, this.allocator);
    if (this.callback != null) {
      this.mediaPeriod.prepare(this, this.preparePositionUs);
    }
  }
  
  public void discardBuffer(long paramLong, boolean paramBoolean)
  {
    this.mediaPeriod.discardBuffer(paramLong, paramBoolean);
  }
  
  public long getAdjustedSeekPositionUs(long paramLong, SeekParameters paramSeekParameters)
  {
    return this.mediaPeriod.getAdjustedSeekPositionUs(paramLong, paramSeekParameters);
  }
  
  public long getBufferedPositionUs()
  {
    return this.mediaPeriod.getBufferedPositionUs();
  }
  
  public long getNextLoadPositionUs()
  {
    return this.mediaPeriod.getNextLoadPositionUs();
  }
  
  public TrackGroupArray getTrackGroups()
  {
    return this.mediaPeriod.getTrackGroups();
  }
  
  public void maybeThrowPrepareError()
    throws IOException
  {
    if (this.mediaPeriod != null) {
      this.mediaPeriod.maybeThrowPrepareError();
    }
    for (;;)
    {
      return;
      this.mediaSource.maybeThrowSourceInfoRefreshError();
    }
  }
  
  public void onContinueLoadingRequested(MediaPeriod paramMediaPeriod)
  {
    this.callback.onContinueLoadingRequested(this);
  }
  
  public void onPrepared(MediaPeriod paramMediaPeriod)
  {
    this.callback.onPrepared(this);
  }
  
  public void prepare(MediaPeriod.Callback paramCallback, long paramLong)
  {
    this.callback = paramCallback;
    this.preparePositionUs = paramLong;
    if (this.mediaPeriod != null) {
      this.mediaPeriod.prepare(this, paramLong);
    }
  }
  
  public long readDiscontinuity()
  {
    return this.mediaPeriod.readDiscontinuity();
  }
  
  public void reevaluateBuffer(long paramLong)
  {
    this.mediaPeriod.reevaluateBuffer(paramLong);
  }
  
  public void releasePeriod()
  {
    if (this.mediaPeriod != null) {
      this.mediaSource.releasePeriod(this.mediaPeriod);
    }
  }
  
  public long seekToUs(long paramLong)
  {
    return this.mediaPeriod.seekToUs(paramLong);
  }
  
  public long selectTracks(TrackSelection[] paramArrayOfTrackSelection, boolean[] paramArrayOfBoolean1, SampleStream[] paramArrayOfSampleStream, boolean[] paramArrayOfBoolean2, long paramLong)
  {
    return this.mediaPeriod.selectTracks(paramArrayOfTrackSelection, paramArrayOfBoolean1, paramArrayOfSampleStream, paramArrayOfBoolean2, paramLong);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/DeferredMediaPeriod.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */