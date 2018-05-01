package org.telegram.messenger.exoplayer2.source;

import java.io.IOException;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import org.telegram.messenger.exoplayer2.ExoPlayer;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

public final class ConcatenatingMediaSource
  implements MediaSource
{
  private final boolean[] duplicateFlags;
  private final boolean isAtomic;
  private MediaSource.Listener listener;
  private final Object[] manifests;
  private final MediaSource[] mediaSources;
  private final ShuffleOrder shuffleOrder;
  private final Map<MediaPeriod, Integer> sourceIndexByMediaPeriod;
  private ConcatenatedTimeline timeline;
  private final Timeline[] timelines;
  
  public ConcatenatingMediaSource(boolean paramBoolean, ShuffleOrder paramShuffleOrder, MediaSource... paramVarArgs)
  {
    int i = paramVarArgs.length;
    for (int j = 0; j < i; j++) {
      Assertions.checkNotNull(paramVarArgs[j]);
    }
    if (paramShuffleOrder.getLength() == paramVarArgs.length) {
      bool = true;
    }
    Assertions.checkArgument(bool);
    this.mediaSources = paramVarArgs;
    this.isAtomic = paramBoolean;
    this.shuffleOrder = paramShuffleOrder;
    this.timelines = new Timeline[paramVarArgs.length];
    this.manifests = new Object[paramVarArgs.length];
    this.sourceIndexByMediaPeriod = new HashMap();
    this.duplicateFlags = buildDuplicateFlags(paramVarArgs);
  }
  
  public ConcatenatingMediaSource(boolean paramBoolean, MediaSource... paramVarArgs)
  {
    this(paramBoolean, new ShuffleOrder.DefaultShuffleOrder(paramVarArgs.length), paramVarArgs);
  }
  
  public ConcatenatingMediaSource(MediaSource... paramVarArgs)
  {
    this(false, paramVarArgs);
  }
  
  private static boolean[] buildDuplicateFlags(MediaSource[] paramArrayOfMediaSource)
  {
    boolean[] arrayOfBoolean = new boolean[paramArrayOfMediaSource.length];
    IdentityHashMap localIdentityHashMap = new IdentityHashMap(paramArrayOfMediaSource.length);
    int i = 0;
    if (i < paramArrayOfMediaSource.length)
    {
      MediaSource localMediaSource = paramArrayOfMediaSource[i];
      if (!localIdentityHashMap.containsKey(localMediaSource)) {
        localIdentityHashMap.put(localMediaSource, null);
      }
      for (;;)
      {
        i++;
        break;
        arrayOfBoolean[i] = true;
      }
    }
    return arrayOfBoolean;
  }
  
  private void handleSourceInfoRefreshed(int paramInt, Timeline paramTimeline, Object paramObject)
  {
    this.timelines[paramInt] = paramTimeline;
    this.manifests[paramInt] = paramObject;
    for (int i = paramInt + 1; i < this.mediaSources.length; i++) {
      if (this.mediaSources[i] == this.mediaSources[paramInt])
      {
        this.timelines[i] = paramTimeline;
        this.manifests[i] = paramObject;
      }
    }
    paramTimeline = this.timelines;
    i = paramTimeline.length;
    paramInt = 0;
    if (paramInt < i) {
      if (paramTimeline[paramInt] != null) {}
    }
    for (;;)
    {
      return;
      paramInt++;
      break;
      this.timeline = new ConcatenatedTimeline((Timeline[])this.timelines.clone(), this.isAtomic, this.shuffleOrder);
      this.listener.onSourceInfoRefreshed(this, this.timeline, this.manifests.clone());
    }
  }
  
  public MediaPeriod createPeriod(MediaSource.MediaPeriodId paramMediaPeriodId, Allocator paramAllocator)
  {
    int i = this.timeline.getChildIndexByPeriodIndex(paramMediaPeriodId.periodIndex);
    paramMediaPeriodId = paramMediaPeriodId.copyWithPeriodIndex(paramMediaPeriodId.periodIndex - this.timeline.getFirstPeriodIndexByChildIndex(i));
    paramMediaPeriodId = this.mediaSources[i].createPeriod(paramMediaPeriodId, paramAllocator);
    this.sourceIndexByMediaPeriod.put(paramMediaPeriodId, Integer.valueOf(i));
    return paramMediaPeriodId;
  }
  
  public void maybeThrowSourceInfoRefreshError()
    throws IOException
  {
    for (int i = 0; i < this.mediaSources.length; i++) {
      if (this.duplicateFlags[i] == 0) {
        this.mediaSources[i].maybeThrowSourceInfoRefreshError();
      }
    }
  }
  
  public void prepareSource(ExoPlayer paramExoPlayer, boolean paramBoolean, MediaSource.Listener paramListener)
  {
    if (this.listener == null)
    {
      paramBoolean = true;
      Assertions.checkState(paramBoolean, "MediaSource instances are not allowed to be reused.");
      this.listener = paramListener;
      if (this.mediaSources.length != 0) {
        break label45;
      }
      paramListener.onSourceInfoRefreshed(this, Timeline.EMPTY, null);
    }
    for (;;)
    {
      return;
      paramBoolean = false;
      break;
      label45:
      for (final int i = 0; i < this.mediaSources.length; i++) {
        if (this.duplicateFlags[i] == 0) {
          this.mediaSources[i].prepareSource(paramExoPlayer, false, new MediaSource.Listener()
          {
            public void onSourceInfoRefreshed(MediaSource paramAnonymousMediaSource, Timeline paramAnonymousTimeline, Object paramAnonymousObject)
            {
              ConcatenatingMediaSource.this.handleSourceInfoRefreshed(i, paramAnonymousTimeline, paramAnonymousObject);
            }
          });
        }
      }
    }
  }
  
  public void releasePeriod(MediaPeriod paramMediaPeriod)
  {
    int i = ((Integer)this.sourceIndexByMediaPeriod.get(paramMediaPeriod)).intValue();
    this.sourceIndexByMediaPeriod.remove(paramMediaPeriod);
    this.mediaSources[i].releasePeriod(paramMediaPeriod);
  }
  
  public void releaseSource()
  {
    for (int i = 0; i < this.mediaSources.length; i++) {
      if (this.duplicateFlags[i] == 0) {
        this.mediaSources[i].releaseSource();
      }
    }
  }
  
  private static final class ConcatenatedTimeline
    extends AbstractConcatenatedTimeline
  {
    private final boolean isAtomic;
    private final int[] sourcePeriodOffsets;
    private final int[] sourceWindowOffsets;
    private final Timeline[] timelines;
    
    public ConcatenatedTimeline(Timeline[] paramArrayOfTimeline, boolean paramBoolean, ShuffleOrder paramShuffleOrder)
    {
      super();
      int[] arrayOfInt = new int[paramArrayOfTimeline.length];
      paramShuffleOrder = new int[paramArrayOfTimeline.length];
      long l = 0L;
      int i = 0;
      int j = 0;
      if (j < paramArrayOfTimeline.length)
      {
        Timeline localTimeline = paramArrayOfTimeline[j];
        l += localTimeline.getPeriodCount();
        if (l <= 2147483647L) {}
        for (boolean bool = true;; bool = false)
        {
          Assertions.checkState(bool, "ConcatenatingMediaSource children contain too many periods");
          arrayOfInt[j] = ((int)l);
          i += localTimeline.getWindowCount();
          paramShuffleOrder[j] = i;
          j++;
          break;
        }
      }
      this.timelines = paramArrayOfTimeline;
      this.sourcePeriodOffsets = arrayOfInt;
      this.sourceWindowOffsets = paramShuffleOrder;
      this.isAtomic = paramBoolean;
    }
    
    protected int getChildIndexByChildUid(Object paramObject)
    {
      if (!(paramObject instanceof Integer)) {}
      for (int i = -1;; i = ((Integer)paramObject).intValue()) {
        return i;
      }
    }
    
    protected int getChildIndexByPeriodIndex(int paramInt)
    {
      return Util.binarySearchFloor(this.sourcePeriodOffsets, paramInt + 1, false, false) + 1;
    }
    
    protected int getChildIndexByWindowIndex(int paramInt)
    {
      return Util.binarySearchFloor(this.sourceWindowOffsets, paramInt + 1, false, false) + 1;
    }
    
    protected Object getChildUidByChildIndex(int paramInt)
    {
      return Integer.valueOf(paramInt);
    }
    
    protected int getFirstPeriodIndexByChildIndex(int paramInt)
    {
      if (paramInt == 0) {}
      for (paramInt = 0;; paramInt = this.sourcePeriodOffsets[(paramInt - 1)]) {
        return paramInt;
      }
    }
    
    public int getFirstWindowIndex(boolean paramBoolean)
    {
      if ((!this.isAtomic) && (paramBoolean)) {}
      for (paramBoolean = true;; paramBoolean = false) {
        return super.getFirstWindowIndex(paramBoolean);
      }
    }
    
    protected int getFirstWindowIndexByChildIndex(int paramInt)
    {
      if (paramInt == 0) {}
      for (paramInt = 0;; paramInt = this.sourceWindowOffsets[(paramInt - 1)]) {
        return paramInt;
      }
    }
    
    public int getLastWindowIndex(boolean paramBoolean)
    {
      if ((!this.isAtomic) && (paramBoolean)) {}
      for (paramBoolean = true;; paramBoolean = false) {
        return super.getLastWindowIndex(paramBoolean);
      }
    }
    
    public int getNextWindowIndex(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      boolean bool = true;
      int i = paramInt2;
      if (this.isAtomic)
      {
        i = paramInt2;
        if (paramInt2 == 1) {
          i = 2;
        }
      }
      if ((!this.isAtomic) && (paramBoolean)) {}
      for (paramBoolean = bool;; paramBoolean = false) {
        return super.getNextWindowIndex(paramInt1, i, paramBoolean);
      }
    }
    
    public int getPeriodCount()
    {
      return this.sourcePeriodOffsets[(this.sourcePeriodOffsets.length - 1)];
    }
    
    public int getPreviousWindowIndex(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      boolean bool = true;
      int i = paramInt2;
      if (this.isAtomic)
      {
        i = paramInt2;
        if (paramInt2 == 1) {
          i = 2;
        }
      }
      if ((!this.isAtomic) && (paramBoolean)) {}
      for (paramBoolean = bool;; paramBoolean = false) {
        return super.getPreviousWindowIndex(paramInt1, i, paramBoolean);
      }
    }
    
    protected Timeline getTimelineByChildIndex(int paramInt)
    {
      return this.timelines[paramInt];
    }
    
    public int getWindowCount()
    {
      return this.sourceWindowOffsets[(this.sourceWindowOffsets.length - 1)];
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/ConcatenatingMediaSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */