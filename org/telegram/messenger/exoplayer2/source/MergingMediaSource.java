package org.telegram.messenger.exoplayer2.source;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.ExoPlayer;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class MergingMediaSource
  implements MediaSource
{
  private static final int PERIOD_COUNT_UNSET = -1;
  private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
  private MediaSource.Listener listener;
  private final MediaSource[] mediaSources;
  private IllegalMergeException mergeError;
  private final ArrayList<MediaSource> pendingTimelineSources;
  private int periodCount;
  private Object primaryManifest;
  private Timeline primaryTimeline;
  
  public MergingMediaSource(CompositeSequenceableLoaderFactory paramCompositeSequenceableLoaderFactory, MediaSource... paramVarArgs)
  {
    this.mediaSources = paramVarArgs;
    this.compositeSequenceableLoaderFactory = paramCompositeSequenceableLoaderFactory;
    this.pendingTimelineSources = new ArrayList(Arrays.asList(paramVarArgs));
    this.periodCount = -1;
  }
  
  public MergingMediaSource(MediaSource... paramVarArgs)
  {
    this(new DefaultCompositeSequenceableLoaderFactory(), paramVarArgs);
  }
  
  private IllegalMergeException checkTimelineMerges(Timeline paramTimeline)
  {
    if (this.periodCount == -1) {
      this.periodCount = paramTimeline.getPeriodCount();
    }
    for (paramTimeline = null;; paramTimeline = new IllegalMergeException(0))
    {
      return paramTimeline;
      if (paramTimeline.getPeriodCount() == this.periodCount) {
        break;
      }
    }
  }
  
  private void handleSourceInfoRefreshed(int paramInt, Timeline paramTimeline, Object paramObject)
  {
    if (this.mergeError == null) {
      this.mergeError = checkTimelineMerges(paramTimeline);
    }
    if (this.mergeError != null) {}
    for (;;)
    {
      return;
      this.pendingTimelineSources.remove(this.mediaSources[paramInt]);
      if (paramInt == 0)
      {
        this.primaryTimeline = paramTimeline;
        this.primaryManifest = paramObject;
      }
      if (this.pendingTimelineSources.isEmpty()) {
        this.listener.onSourceInfoRefreshed(this, this.primaryTimeline, this.primaryManifest);
      }
    }
  }
  
  public MediaPeriod createPeriod(MediaSource.MediaPeriodId paramMediaPeriodId, Allocator paramAllocator)
  {
    MediaPeriod[] arrayOfMediaPeriod = new MediaPeriod[this.mediaSources.length];
    for (int i = 0; i < arrayOfMediaPeriod.length; i++) {
      arrayOfMediaPeriod[i] = this.mediaSources[i].createPeriod(paramMediaPeriodId, paramAllocator);
    }
    return new MergingMediaPeriod(this.compositeSequenceableLoaderFactory, arrayOfMediaPeriod);
  }
  
  public void maybeThrowSourceInfoRefreshError()
    throws IOException
  {
    if (this.mergeError != null) {
      throw this.mergeError;
    }
    MediaSource[] arrayOfMediaSource = this.mediaSources;
    int i = arrayOfMediaSource.length;
    for (int j = 0; j < i; j++) {
      arrayOfMediaSource[j].maybeThrowSourceInfoRefreshError();
    }
  }
  
  public void prepareSource(ExoPlayer paramExoPlayer, boolean paramBoolean, MediaSource.Listener paramListener)
  {
    if (this.listener == null) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      Assertions.checkState(paramBoolean, "MediaSource instances are not allowed to be reused.");
      this.listener = paramListener;
      for (final int i = 0; i < this.mediaSources.length; i++) {
        this.mediaSources[i].prepareSource(paramExoPlayer, false, new MediaSource.Listener()
        {
          public void onSourceInfoRefreshed(MediaSource paramAnonymousMediaSource, Timeline paramAnonymousTimeline, Object paramAnonymousObject)
          {
            MergingMediaSource.this.handleSourceInfoRefreshed(i, paramAnonymousTimeline, paramAnonymousObject);
          }
        });
      }
    }
  }
  
  public void releasePeriod(MediaPeriod paramMediaPeriod)
  {
    paramMediaPeriod = (MergingMediaPeriod)paramMediaPeriod;
    for (int i = 0; i < this.mediaSources.length; i++) {
      this.mediaSources[i].releasePeriod(paramMediaPeriod.periods[i]);
    }
  }
  
  public void releaseSource()
  {
    MediaSource[] arrayOfMediaSource = this.mediaSources;
    int i = arrayOfMediaSource.length;
    for (int j = 0; j < i; j++) {
      arrayOfMediaSource[j].releaseSource();
    }
  }
  
  public static final class IllegalMergeException
    extends IOException
  {
    public static final int REASON_PERIOD_COUNT_MISMATCH = 0;
    public final int reason;
    
    public IllegalMergeException(int paramInt)
    {
      this.reason = paramInt;
    }
    
    @Retention(RetentionPolicy.SOURCE)
    public static @interface Reason {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/MergingMediaSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */