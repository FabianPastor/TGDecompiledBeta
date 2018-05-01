package org.telegram.messenger.exoplayer2;

import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelectionArray;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.upstream.DefaultAllocator;
import org.telegram.messenger.exoplayer2.util.PriorityTaskManager;
import org.telegram.messenger.exoplayer2.util.Util;

public class DefaultLoadControl
  implements LoadControl
{
  public static final int DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS = 5000;
  public static final int DEFAULT_BUFFER_FOR_PLAYBACK_MS = 2500;
  public static final int DEFAULT_MAX_BUFFER_MS = 30000;
  public static final int DEFAULT_MIN_BUFFER_MS = 15000;
  public static final boolean DEFAULT_PRIORITIZE_TIME_OVER_SIZE_THRESHOLDS = true;
  public static final int DEFAULT_TARGET_BUFFER_BYTES = -1;
  private final DefaultAllocator allocator;
  private final long bufferForPlaybackAfterRebufferUs;
  private final long bufferForPlaybackUs;
  private boolean isBuffering;
  private final long maxBufferUs;
  private final long minBufferUs;
  private final boolean prioritizeTimeOverSizeThresholds;
  private final PriorityTaskManager priorityTaskManager;
  private final int targetBufferBytesOverwrite;
  private int targetBufferSize;
  
  public DefaultLoadControl()
  {
    this(new DefaultAllocator(true, 65536));
  }
  
  public DefaultLoadControl(DefaultAllocator paramDefaultAllocator)
  {
    this(paramDefaultAllocator, 15000, 30000, 2500, 5000, -1, true);
  }
  
  public DefaultLoadControl(DefaultAllocator paramDefaultAllocator, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean)
  {
    this(paramDefaultAllocator, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramBoolean, null);
  }
  
  public DefaultLoadControl(DefaultAllocator paramDefaultAllocator, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean, PriorityTaskManager paramPriorityTaskManager)
  {
    this.allocator = paramDefaultAllocator;
    this.minBufferUs = (paramInt1 * 1000L);
    this.maxBufferUs = (paramInt2 * 1000L);
    this.bufferForPlaybackUs = (paramInt3 * 1000L);
    this.bufferForPlaybackAfterRebufferUs = (paramInt4 * 1000L);
    this.targetBufferBytesOverwrite = paramInt5;
    this.prioritizeTimeOverSizeThresholds = paramBoolean;
    this.priorityTaskManager = paramPriorityTaskManager;
  }
  
  private void reset(boolean paramBoolean)
  {
    this.targetBufferSize = 0;
    if ((this.priorityTaskManager != null) && (this.isBuffering)) {
      this.priorityTaskManager.remove(0);
    }
    this.isBuffering = false;
    if (paramBoolean) {
      this.allocator.reset();
    }
  }
  
  protected int calculateTargetBufferSize(Renderer[] paramArrayOfRenderer, TrackSelectionArray paramTrackSelectionArray)
  {
    int i = 0;
    int j = 0;
    while (j < paramArrayOfRenderer.length)
    {
      int k = i;
      if (paramTrackSelectionArray.get(j) != null) {
        k = i + Util.getDefaultBufferSize(paramArrayOfRenderer[j].getTrackType());
      }
      j++;
      i = k;
    }
    return i;
  }
  
  public Allocator getAllocator()
  {
    return this.allocator;
  }
  
  public long getBackBufferDurationUs()
  {
    return 0L;
  }
  
  public void onPrepared()
  {
    reset(false);
  }
  
  public void onReleased()
  {
    reset(true);
  }
  
  public void onStopped()
  {
    reset(true);
  }
  
  public void onTracksSelected(Renderer[] paramArrayOfRenderer, TrackGroupArray paramTrackGroupArray, TrackSelectionArray paramTrackSelectionArray)
  {
    if (this.targetBufferBytesOverwrite == -1) {}
    for (int i = calculateTargetBufferSize(paramArrayOfRenderer, paramTrackSelectionArray);; i = this.targetBufferBytesOverwrite)
    {
      this.targetBufferSize = i;
      this.allocator.setTargetBufferSize(this.targetBufferSize);
      return;
    }
  }
  
  public boolean retainBackBufferFromKeyframe()
  {
    return false;
  }
  
  public boolean shouldContinueLoading(long paramLong, float paramFloat)
  {
    boolean bool1 = true;
    boolean bool2 = true;
    int i;
    boolean bool4;
    if (this.allocator.getTotalBytesAllocated() >= this.targetBufferSize)
    {
      i = 1;
      boolean bool3 = this.isBuffering;
      if (!this.prioritizeTimeOverSizeThresholds) {
        break label128;
      }
      bool4 = bool2;
      if (paramLong >= this.minBufferUs)
      {
        if ((paramLong > this.maxBufferUs) || (!this.isBuffering) || (i != 0)) {
          break label122;
        }
        bool4 = bool2;
      }
      label74:
      this.isBuffering = bool4;
      if ((this.priorityTaskManager != null) && (this.isBuffering != bool3))
      {
        if (!this.isBuffering) {
          break label181;
        }
        this.priorityTaskManager.add(0);
      }
    }
    for (;;)
    {
      return this.isBuffering;
      i = 0;
      break;
      label122:
      bool4 = false;
      break label74;
      label128:
      if (i == 0)
      {
        bool4 = bool1;
        if (paramLong >= this.minBufferUs) {
          if ((paramLong > this.maxBufferUs) || (!this.isBuffering)) {
            break label175;
          }
        }
      }
      label175:
      for (bool4 = bool1;; bool4 = false)
      {
        this.isBuffering = bool4;
        break;
      }
      label181:
      this.priorityTaskManager.remove(0);
    }
  }
  
  public boolean shouldStartPlayback(long paramLong, float paramFloat, boolean paramBoolean)
  {
    long l = Util.getPlayoutDurationForMediaDuration(paramLong, paramFloat);
    if (paramBoolean)
    {
      paramLong = this.bufferForPlaybackAfterRebufferUs;
      if ((paramLong > 0L) && (l < paramLong) && ((this.prioritizeTimeOverSizeThresholds) || (this.allocator.getTotalBytesAllocated() < this.targetBufferSize))) {
        break label65;
      }
    }
    label65:
    for (paramBoolean = true;; paramBoolean = false)
    {
      return paramBoolean;
      paramLong = this.bufferForPlaybackUs;
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/DefaultLoadControl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */