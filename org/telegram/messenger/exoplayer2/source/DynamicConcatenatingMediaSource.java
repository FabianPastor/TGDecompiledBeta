package org.telegram.messenger.exoplayer2.source;

import android.os.Handler;
import android.os.Looper;
import android.util.SparseIntArray;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.exoplayer2.ExoPlaybackException;
import org.telegram.messenger.exoplayer2.ExoPlayer;
import org.telegram.messenger.exoplayer2.PlayerMessage;
import org.telegram.messenger.exoplayer2.PlayerMessage.Target;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.Timeline.Period;
import org.telegram.messenger.exoplayer2.Timeline.Window;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

public final class DynamicConcatenatingMediaSource
  implements PlayerMessage.Target, MediaSource
{
  private static final int MSG_ADD = 0;
  private static final int MSG_ADD_MULTIPLE = 1;
  private static final int MSG_MOVE = 3;
  private static final int MSG_ON_COMPLETION = 4;
  private static final int MSG_REMOVE = 2;
  private final List<DeferredMediaPeriod> deferredMediaPeriods;
  private MediaSource.Listener listener;
  private final Map<MediaPeriod, MediaSource> mediaSourceByMediaPeriod;
  private final List<MediaSourceHolder> mediaSourceHolders;
  private final List<MediaSource> mediaSourcesPublic;
  private int periodCount;
  private ExoPlayer player;
  private boolean preventListenerNotification;
  private final MediaSourceHolder query;
  private ShuffleOrder shuffleOrder;
  private int windowCount;
  
  public DynamicConcatenatingMediaSource()
  {
    this(new ShuffleOrder.DefaultShuffleOrder(0));
  }
  
  public DynamicConcatenatingMediaSource(ShuffleOrder paramShuffleOrder)
  {
    this.shuffleOrder = paramShuffleOrder;
    this.mediaSourceByMediaPeriod = new IdentityHashMap();
    this.mediaSourcesPublic = new ArrayList();
    this.mediaSourceHolders = new ArrayList();
    this.deferredMediaPeriods = new ArrayList(1);
    this.query = new MediaSourceHolder(null, null, -1, -1, Integer.valueOf(-1));
  }
  
  private void addMediaSourceInternal(int paramInt, final MediaSource paramMediaSource)
  {
    Integer localInteger = Integer.valueOf(System.identityHashCode(paramMediaSource));
    DeferredTimeline localDeferredTimeline = new DeferredTimeline();
    MediaSourceHolder localMediaSourceHolder;
    int i;
    int j;
    int k;
    if (paramInt > 0)
    {
      localMediaSourceHolder = (MediaSourceHolder)this.mediaSourceHolders.get(paramInt - 1);
      i = localMediaSourceHolder.firstWindowIndexInChild;
      j = localMediaSourceHolder.timeline.getWindowCount();
      k = localMediaSourceHolder.firstPeriodIndexInChild;
    }
    for (paramMediaSource = new MediaSourceHolder(paramMediaSource, localDeferredTimeline, j + i, localMediaSourceHolder.timeline.getPeriodCount() + k, localInteger);; paramMediaSource = new MediaSourceHolder(paramMediaSource, localDeferredTimeline, 0, 0, localInteger))
    {
      correctOffsets(paramInt, localDeferredTimeline.getWindowCount(), localDeferredTimeline.getPeriodCount());
      this.mediaSourceHolders.add(paramInt, paramMediaSource);
      paramMediaSource.mediaSource.prepareSource(this.player, false, new MediaSource.Listener()
      {
        public void onSourceInfoRefreshed(MediaSource paramAnonymousMediaSource, Timeline paramAnonymousTimeline, Object paramAnonymousObject)
        {
          DynamicConcatenatingMediaSource.this.updateMediaSourceInternal(paramMediaSource, paramAnonymousTimeline);
        }
      });
      return;
    }
  }
  
  private void addMediaSourcesInternal(int paramInt, Collection<MediaSource> paramCollection)
  {
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext())
    {
      addMediaSourceInternal(paramInt, (MediaSource)paramCollection.next());
      paramInt++;
    }
  }
  
  private void correctOffsets(int paramInt1, int paramInt2, int paramInt3)
  {
    this.windowCount += paramInt2;
    this.periodCount += paramInt3;
    while (paramInt1 < this.mediaSourceHolders.size())
    {
      MediaSourceHolder localMediaSourceHolder = (MediaSourceHolder)this.mediaSourceHolders.get(paramInt1);
      localMediaSourceHolder.firstWindowIndexInChild += paramInt2;
      localMediaSourceHolder = (MediaSourceHolder)this.mediaSourceHolders.get(paramInt1);
      localMediaSourceHolder.firstPeriodIndexInChild += paramInt3;
      paramInt1++;
    }
  }
  
  private int findMediaSourceHolderByPeriodIndex(int paramInt)
  {
    this.query.firstPeriodIndexInChild = paramInt;
    int i = Collections.binarySearch(this.mediaSourceHolders, this.query);
    int j = i;
    if (i < 0) {}
    for (paramInt = -i - 2;; paramInt = j)
    {
      return paramInt;
      while ((j < this.mediaSourceHolders.size() - 1) && (((MediaSourceHolder)this.mediaSourceHolders.get(j + 1)).firstPeriodIndexInChild == paramInt)) {
        j++;
      }
    }
  }
  
  private void maybeNotifyListener(EventDispatcher paramEventDispatcher)
  {
    if (!this.preventListenerNotification)
    {
      this.listener.onSourceInfoRefreshed(this, new ConcatenatedTimeline(this.mediaSourceHolders, this.windowCount, this.periodCount, this.shuffleOrder), null);
      if (paramEventDispatcher != null) {
        this.player.createMessage(this).setType(4).setPayload(paramEventDispatcher).send();
      }
    }
  }
  
  private void moveMediaSourceInternal(int paramInt1, int paramInt2)
  {
    int i = Math.min(paramInt1, paramInt2);
    int j = Math.max(paramInt1, paramInt2);
    int k = ((MediaSourceHolder)this.mediaSourceHolders.get(i)).firstWindowIndexInChild;
    int m = ((MediaSourceHolder)this.mediaSourceHolders.get(i)).firstPeriodIndexInChild;
    this.mediaSourceHolders.add(paramInt2, this.mediaSourceHolders.remove(paramInt1));
    paramInt2 = k;
    paramInt1 = m;
    while (i <= j)
    {
      MediaSourceHolder localMediaSourceHolder = (MediaSourceHolder)this.mediaSourceHolders.get(i);
      localMediaSourceHolder.firstWindowIndexInChild = paramInt2;
      localMediaSourceHolder.firstPeriodIndexInChild = paramInt1;
      paramInt2 += localMediaSourceHolder.timeline.getWindowCount();
      paramInt1 += localMediaSourceHolder.timeline.getPeriodCount();
      i++;
    }
  }
  
  private void removeMediaSourceInternal(int paramInt)
  {
    MediaSourceHolder localMediaSourceHolder = (MediaSourceHolder)this.mediaSourceHolders.get(paramInt);
    this.mediaSourceHolders.remove(paramInt);
    DeferredTimeline localDeferredTimeline = localMediaSourceHolder.timeline;
    correctOffsets(paramInt, -localDeferredTimeline.getWindowCount(), -localDeferredTimeline.getPeriodCount());
    localMediaSourceHolder.mediaSource.releaseSource();
  }
  
  private void updateMediaSourceInternal(MediaSourceHolder paramMediaSourceHolder, Timeline paramTimeline)
  {
    if (paramMediaSourceHolder == null) {
      throw new IllegalArgumentException();
    }
    DeferredTimeline localDeferredTimeline = paramMediaSourceHolder.timeline;
    if (localDeferredTimeline.getTimeline() == paramTimeline) {}
    for (;;)
    {
      return;
      int i = paramTimeline.getWindowCount() - localDeferredTimeline.getWindowCount();
      int j = paramTimeline.getPeriodCount() - localDeferredTimeline.getPeriodCount();
      if ((i != 0) || (j != 0)) {
        correctOffsets(findMediaSourceHolderByPeriodIndex(paramMediaSourceHolder.firstPeriodIndexInChild) + 1, i, j);
      }
      paramMediaSourceHolder.timeline = localDeferredTimeline.cloneWithNewTimeline(paramTimeline);
      if (!paramMediaSourceHolder.isPrepared) {
        for (j = this.deferredMediaPeriods.size() - 1; j >= 0; j--) {
          if (((DeferredMediaPeriod)this.deferredMediaPeriods.get(j)).mediaSource == paramMediaSourceHolder.mediaSource)
          {
            ((DeferredMediaPeriod)this.deferredMediaPeriods.get(j)).createPeriod();
            this.deferredMediaPeriods.remove(j);
          }
        }
      }
      paramMediaSourceHolder.isPrepared = true;
      maybeNotifyListener(null);
    }
  }
  
  public void addMediaSource(int paramInt, MediaSource paramMediaSource)
  {
    try
    {
      addMediaSource(paramInt, paramMediaSource, null);
      return;
    }
    finally
    {
      paramMediaSource = finally;
      throw paramMediaSource;
    }
  }
  
  /* Error */
  public void addMediaSource(int paramInt, MediaSource paramMediaSource, Runnable paramRunnable)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore 4
    //   3: aload_0
    //   4: monitorenter
    //   5: aload_2
    //   6: invokestatic 286	org/telegram/messenger/exoplayer2/util/Assertions:checkNotNull	(Ljava/lang/Object;)Ljava/lang/Object;
    //   9: pop
    //   10: aload_0
    //   11: getfield 83	org/telegram/messenger/exoplayer2/source/DynamicConcatenatingMediaSource:mediaSourcesPublic	Ljava/util/List;
    //   14: aload_2
    //   15: invokeinterface 290 2 0
    //   20: ifne +6 -> 26
    //   23: iconst_1
    //   24: istore 4
    //   26: iload 4
    //   28: invokestatic 294	org/telegram/messenger/exoplayer2/util/Assertions:checkArgument	(Z)V
    //   31: aload_0
    //   32: getfield 83	org/telegram/messenger/exoplayer2/source/DynamicConcatenatingMediaSource:mediaSourcesPublic	Ljava/util/List;
    //   35: iload_1
    //   36: aload_2
    //   37: invokeinterface 145 3 0
    //   42: aload_0
    //   43: getfield 151	org/telegram/messenger/exoplayer2/source/DynamicConcatenatingMediaSource:player	Lorg/telegram/messenger/exoplayer2/ExoPlayer;
    //   46: ifnull +46 -> 92
    //   49: aload_0
    //   50: getfield 151	org/telegram/messenger/exoplayer2/source/DynamicConcatenatingMediaSource:player	Lorg/telegram/messenger/exoplayer2/ExoPlayer;
    //   53: aload_0
    //   54: invokeinterface 216 2 0
    //   59: iconst_0
    //   60: invokevirtual 222	org/telegram/messenger/exoplayer2/PlayerMessage:setType	(I)Lorg/telegram/messenger/exoplayer2/PlayerMessage;
    //   63: astore 5
    //   65: new 24	org/telegram/messenger/exoplayer2/source/DynamicConcatenatingMediaSource$MessageData
    //   68: astore 6
    //   70: aload 6
    //   72: iload_1
    //   73: aload_2
    //   74: aload_3
    //   75: invokespecial 297	org/telegram/messenger/exoplayer2/source/DynamicConcatenatingMediaSource$MessageData:<init>	(ILjava/lang/Object;Ljava/lang/Runnable;)V
    //   78: aload 5
    //   80: aload 6
    //   82: invokevirtual 226	org/telegram/messenger/exoplayer2/PlayerMessage:setPayload	(Ljava/lang/Object;)Lorg/telegram/messenger/exoplayer2/PlayerMessage;
    //   85: invokevirtual 230	org/telegram/messenger/exoplayer2/PlayerMessage:send	()Lorg/telegram/messenger/exoplayer2/PlayerMessage;
    //   88: pop
    //   89: aload_0
    //   90: monitorexit
    //   91: return
    //   92: aload_3
    //   93: ifnull -4 -> 89
    //   96: aload_3
    //   97: invokeinterface 302 1 0
    //   102: goto -13 -> 89
    //   105: astore_2
    //   106: aload_0
    //   107: monitorexit
    //   108: aload_2
    //   109: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	110	0	this	DynamicConcatenatingMediaSource
    //   0	110	1	paramInt	int
    //   0	110	2	paramMediaSource	MediaSource
    //   0	110	3	paramRunnable	Runnable
    //   1	26	4	bool	boolean
    //   63	16	5	localPlayerMessage	PlayerMessage
    //   68	13	6	localMessageData	MessageData
    // Exception table:
    //   from	to	target	type
    //   5	10	105	finally
    //   10	23	105	finally
    //   26	89	105	finally
    //   96	102	105	finally
  }
  
  public void addMediaSource(MediaSource paramMediaSource)
  {
    try
    {
      addMediaSource(this.mediaSourcesPublic.size(), paramMediaSource, null);
      return;
    }
    finally
    {
      paramMediaSource = finally;
      throw paramMediaSource;
    }
  }
  
  public void addMediaSource(MediaSource paramMediaSource, Runnable paramRunnable)
  {
    try
    {
      addMediaSource(this.mediaSourcesPublic.size(), paramMediaSource, paramRunnable);
      return;
    }
    finally
    {
      paramMediaSource = finally;
      throw paramMediaSource;
    }
  }
  
  public void addMediaSources(int paramInt, Collection<MediaSource> paramCollection)
  {
    try
    {
      addMediaSources(paramInt, paramCollection, null);
      return;
    }
    finally
    {
      paramCollection = finally;
      throw paramCollection;
    }
  }
  
  public void addMediaSources(int paramInt, Collection<MediaSource> paramCollection, Runnable paramRunnable)
  {
    Object localObject1;
    Object localObject2;
    for (;;)
    {
      try
      {
        localObject1 = paramCollection.iterator();
        if (!((Iterator)localObject1).hasNext()) {
          break;
        }
        localObject2 = (MediaSource)((Iterator)localObject1).next();
        Assertions.checkNotNull(localObject2);
        boolean bool;
        if (!this.mediaSourcesPublic.contains(localObject2))
        {
          bool = true;
          Assertions.checkArgument(bool);
        }
        else
        {
          bool = false;
        }
      }
      finally {}
    }
    this.mediaSourcesPublic.addAll(paramInt, paramCollection);
    if ((this.player != null) && (!paramCollection.isEmpty()))
    {
      localObject1 = this.player.createMessage(this).setType(1);
      localObject2 = new org/telegram/messenger/exoplayer2/source/DynamicConcatenatingMediaSource$MessageData;
      ((MessageData)localObject2).<init>(paramInt, paramCollection, paramRunnable);
      ((PlayerMessage)localObject1).setPayload(localObject2).send();
    }
    for (;;)
    {
      return;
      if (paramRunnable != null) {
        paramRunnable.run();
      }
    }
  }
  
  public void addMediaSources(Collection<MediaSource> paramCollection)
  {
    try
    {
      addMediaSources(this.mediaSourcesPublic.size(), paramCollection, null);
      return;
    }
    finally
    {
      paramCollection = finally;
      throw paramCollection;
    }
  }
  
  public void addMediaSources(Collection<MediaSource> paramCollection, Runnable paramRunnable)
  {
    try
    {
      addMediaSources(this.mediaSourcesPublic.size(), paramCollection, paramRunnable);
      return;
    }
    finally
    {
      paramCollection = finally;
      throw paramCollection;
    }
  }
  
  public MediaPeriod createPeriod(MediaSource.MediaPeriodId paramMediaPeriodId, Allocator paramAllocator)
  {
    int i = findMediaSourceHolderByPeriodIndex(paramMediaPeriodId.periodIndex);
    MediaSourceHolder localMediaSourceHolder = (MediaSourceHolder)this.mediaSourceHolders.get(i);
    paramMediaPeriodId = paramMediaPeriodId.copyWithPeriodIndex(paramMediaPeriodId.periodIndex - localMediaSourceHolder.firstPeriodIndexInChild);
    if (!localMediaSourceHolder.isPrepared)
    {
      paramMediaPeriodId = new DeferredMediaPeriod(localMediaSourceHolder.mediaSource, paramMediaPeriodId, paramAllocator);
      this.deferredMediaPeriods.add((DeferredMediaPeriod)paramMediaPeriodId);
    }
    for (;;)
    {
      this.mediaSourceByMediaPeriod.put(paramMediaPeriodId, localMediaSourceHolder.mediaSource);
      return paramMediaPeriodId;
      paramMediaPeriodId = localMediaSourceHolder.mediaSource.createPeriod(paramMediaPeriodId, paramAllocator);
    }
  }
  
  public MediaSource getMediaSource(int paramInt)
  {
    try
    {
      MediaSource localMediaSource = (MediaSource)this.mediaSourcesPublic.get(paramInt);
      return localMediaSource;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public int getSize()
  {
    try
    {
      int i = this.mediaSourcesPublic.size();
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void handleMessage(int paramInt, Object paramObject)
    throws ExoPlaybackException
  {
    if (paramInt == 4)
    {
      ((EventDispatcher)paramObject).dispatchEvent();
      return;
    }
    this.preventListenerNotification = true;
    switch (paramInt)
    {
    default: 
      throw new IllegalStateException();
    case 0: 
      paramObject = (MessageData)paramObject;
      this.shuffleOrder = this.shuffleOrder.cloneAndInsert(((MessageData)paramObject).index, 1);
      addMediaSourceInternal(((MessageData)paramObject).index, (MediaSource)((MessageData)paramObject).customData);
      paramObject = ((MessageData)paramObject).actionOnCompletion;
    }
    for (;;)
    {
      this.preventListenerNotification = false;
      maybeNotifyListener((EventDispatcher)paramObject);
      break;
      paramObject = (MessageData)paramObject;
      this.shuffleOrder = this.shuffleOrder.cloneAndInsert(((MessageData)paramObject).index, ((Collection)((MessageData)paramObject).customData).size());
      addMediaSourcesInternal(((MessageData)paramObject).index, (Collection)((MessageData)paramObject).customData);
      paramObject = ((MessageData)paramObject).actionOnCompletion;
      continue;
      paramObject = (MessageData)paramObject;
      this.shuffleOrder = this.shuffleOrder.cloneAndRemove(((MessageData)paramObject).index);
      removeMediaSourceInternal(((MessageData)paramObject).index);
      paramObject = ((MessageData)paramObject).actionOnCompletion;
      continue;
      paramObject = (MessageData)paramObject;
      this.shuffleOrder = this.shuffleOrder.cloneAndRemove(((MessageData)paramObject).index);
      this.shuffleOrder = this.shuffleOrder.cloneAndInsert(((Integer)((MessageData)paramObject).customData).intValue(), 1);
      moveMediaSourceInternal(((MessageData)paramObject).index, ((Integer)((MessageData)paramObject).customData).intValue());
      paramObject = ((MessageData)paramObject).actionOnCompletion;
    }
  }
  
  public void maybeThrowSourceInfoRefreshError()
    throws IOException
  {
    for (int i = 0; i < this.mediaSourceHolders.size(); i++) {
      ((MediaSourceHolder)this.mediaSourceHolders.get(i)).mediaSource.maybeThrowSourceInfoRefreshError();
    }
  }
  
  public void moveMediaSource(int paramInt1, int paramInt2)
  {
    try
    {
      moveMediaSource(paramInt1, paramInt2, null);
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public void moveMediaSource(int paramInt1, int paramInt2, Runnable paramRunnable)
  {
    if (paramInt1 == paramInt2) {}
    for (;;)
    {
      return;
      try
      {
        this.mediaSourcesPublic.add(paramInt2, this.mediaSourcesPublic.remove(paramInt1));
        if (this.player != null)
        {
          PlayerMessage localPlayerMessage = this.player.createMessage(this).setType(3);
          MessageData localMessageData = new org/telegram/messenger/exoplayer2/source/DynamicConcatenatingMediaSource$MessageData;
          localMessageData.<init>(paramInt1, Integer.valueOf(paramInt2), paramRunnable);
          localPlayerMessage.setPayload(localMessageData).send();
          continue;
        }
      }
      finally {}
      if (paramRunnable != null) {
        paramRunnable.run();
      }
    }
  }
  
  /* Error */
  public void prepareSource(ExoPlayer paramExoPlayer, boolean paramBoolean, MediaSource.Listener paramListener)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_2
    //   2: aload_0
    //   3: monitorenter
    //   4: aload_0
    //   5: getfield 201	org/telegram/messenger/exoplayer2/source/DynamicConcatenatingMediaSource:listener	Lorg/telegram/messenger/exoplayer2/source/MediaSource$Listener;
    //   8: ifnonnull +70 -> 78
    //   11: iload_2
    //   12: ldc_w 398
    //   15: invokestatic 402	org/telegram/messenger/exoplayer2/util/Assertions:checkState	(ZLjava/lang/Object;)V
    //   18: aload_0
    //   19: aload_1
    //   20: putfield 151	org/telegram/messenger/exoplayer2/source/DynamicConcatenatingMediaSource:player	Lorg/telegram/messenger/exoplayer2/ExoPlayer;
    //   23: aload_0
    //   24: aload_3
    //   25: putfield 201	org/telegram/messenger/exoplayer2/source/DynamicConcatenatingMediaSource:listener	Lorg/telegram/messenger/exoplayer2/source/MediaSource$Listener;
    //   28: aload_0
    //   29: iconst_1
    //   30: putfield 199	org/telegram/messenger/exoplayer2/source/DynamicConcatenatingMediaSource:preventListenerNotification	Z
    //   33: aload_0
    //   34: aload_0
    //   35: getfield 73	org/telegram/messenger/exoplayer2/source/DynamicConcatenatingMediaSource:shuffleOrder	Lorg/telegram/messenger/exoplayer2/source/ShuffleOrder;
    //   38: iconst_0
    //   39: aload_0
    //   40: getfield 83	org/telegram/messenger/exoplayer2/source/DynamicConcatenatingMediaSource:mediaSourcesPublic	Ljava/util/List;
    //   43: invokeinterface 187 1 0
    //   48: invokeinterface 364 3 0
    //   53: putfield 73	org/telegram/messenger/exoplayer2/source/DynamicConcatenatingMediaSource:shuffleOrder	Lorg/telegram/messenger/exoplayer2/source/ShuffleOrder;
    //   56: aload_0
    //   57: iconst_0
    //   58: aload_0
    //   59: getfield 83	org/telegram/messenger/exoplayer2/source/DynamicConcatenatingMediaSource:mediaSourcesPublic	Ljava/util/List;
    //   62: invokespecial 375	org/telegram/messenger/exoplayer2/source/DynamicConcatenatingMediaSource:addMediaSourcesInternal	(ILjava/util/Collection;)V
    //   65: aload_0
    //   66: iconst_0
    //   67: putfield 199	org/telegram/messenger/exoplayer2/source/DynamicConcatenatingMediaSource:preventListenerNotification	Z
    //   70: aload_0
    //   71: aconst_null
    //   72: invokespecial 276	org/telegram/messenger/exoplayer2/source/DynamicConcatenatingMediaSource:maybeNotifyListener	(Lorg/telegram/messenger/exoplayer2/source/DynamicConcatenatingMediaSource$EventDispatcher;)V
    //   75: aload_0
    //   76: monitorexit
    //   77: return
    //   78: iconst_0
    //   79: istore_2
    //   80: goto -69 -> 11
    //   83: astore_1
    //   84: aload_0
    //   85: monitorexit
    //   86: aload_1
    //   87: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	88	0	this	DynamicConcatenatingMediaSource
    //   0	88	1	paramExoPlayer	ExoPlayer
    //   0	88	2	paramBoolean	boolean
    //   0	88	3	paramListener	MediaSource.Listener
    // Exception table:
    //   from	to	target	type
    //   4	11	83	finally
    //   11	75	83	finally
  }
  
  public void releasePeriod(MediaPeriod paramMediaPeriod)
  {
    MediaSource localMediaSource = (MediaSource)this.mediaSourceByMediaPeriod.get(paramMediaPeriod);
    this.mediaSourceByMediaPeriod.remove(paramMediaPeriod);
    if ((paramMediaPeriod instanceof DeferredMediaPeriod))
    {
      this.deferredMediaPeriods.remove(paramMediaPeriod);
      ((DeferredMediaPeriod)paramMediaPeriod).releasePeriod();
    }
    for (;;)
    {
      return;
      localMediaSource.releasePeriod(paramMediaPeriod);
    }
  }
  
  public void releaseSource()
  {
    for (int i = 0; i < this.mediaSourceHolders.size(); i++) {
      ((MediaSourceHolder)this.mediaSourceHolders.get(i)).mediaSource.releaseSource();
    }
  }
  
  public void removeMediaSource(int paramInt)
  {
    try
    {
      removeMediaSource(paramInt, null);
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /* Error */
  public void removeMediaSource(int paramInt, Runnable paramRunnable)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 83	org/telegram/messenger/exoplayer2/source/DynamicConcatenatingMediaSource:mediaSourcesPublic	Ljava/util/List;
    //   6: iload_1
    //   7: invokeinterface 244 2 0
    //   12: pop
    //   13: aload_0
    //   14: getfield 151	org/telegram/messenger/exoplayer2/source/DynamicConcatenatingMediaSource:player	Lorg/telegram/messenger/exoplayer2/ExoPlayer;
    //   17: ifnull +44 -> 61
    //   20: aload_0
    //   21: getfield 151	org/telegram/messenger/exoplayer2/source/DynamicConcatenatingMediaSource:player	Lorg/telegram/messenger/exoplayer2/ExoPlayer;
    //   24: aload_0
    //   25: invokeinterface 216 2 0
    //   30: iconst_2
    //   31: invokevirtual 222	org/telegram/messenger/exoplayer2/PlayerMessage:setType	(I)Lorg/telegram/messenger/exoplayer2/PlayerMessage;
    //   34: astore_3
    //   35: new 24	org/telegram/messenger/exoplayer2/source/DynamicConcatenatingMediaSource$MessageData
    //   38: astore 4
    //   40: aload 4
    //   42: iload_1
    //   43: aconst_null
    //   44: aload_2
    //   45: invokespecial 297	org/telegram/messenger/exoplayer2/source/DynamicConcatenatingMediaSource$MessageData:<init>	(ILjava/lang/Object;Ljava/lang/Runnable;)V
    //   48: aload_3
    //   49: aload 4
    //   51: invokevirtual 226	org/telegram/messenger/exoplayer2/PlayerMessage:setPayload	(Ljava/lang/Object;)Lorg/telegram/messenger/exoplayer2/PlayerMessage;
    //   54: invokevirtual 230	org/telegram/messenger/exoplayer2/PlayerMessage:send	()Lorg/telegram/messenger/exoplayer2/PlayerMessage;
    //   57: pop
    //   58: aload_0
    //   59: monitorexit
    //   60: return
    //   61: aload_2
    //   62: ifnull -4 -> 58
    //   65: aload_2
    //   66: invokeinterface 302 1 0
    //   71: goto -13 -> 58
    //   74: astore_2
    //   75: aload_0
    //   76: monitorexit
    //   77: aload_2
    //   78: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	79	0	this	DynamicConcatenatingMediaSource
    //   0	79	1	paramInt	int
    //   0	79	2	paramRunnable	Runnable
    //   34	15	3	localPlayerMessage	PlayerMessage
    //   38	12	4	localMessageData	MessageData
    // Exception table:
    //   from	to	target	type
    //   2	58	74	finally
    //   65	71	74	finally
  }
  
  private static final class ConcatenatedTimeline
    extends AbstractConcatenatedTimeline
  {
    private final SparseIntArray childIndexByUid;
    private final int[] firstPeriodInChildIndices;
    private final int[] firstWindowInChildIndices;
    private final int periodCount;
    private final Timeline[] timelines;
    private final int[] uids;
    private final int windowCount;
    
    public ConcatenatedTimeline(Collection<DynamicConcatenatingMediaSource.MediaSourceHolder> paramCollection, int paramInt1, int paramInt2, ShuffleOrder paramShuffleOrder)
    {
      super();
      this.windowCount = paramInt1;
      this.periodCount = paramInt2;
      paramInt1 = paramCollection.size();
      this.firstPeriodInChildIndices = new int[paramInt1];
      this.firstWindowInChildIndices = new int[paramInt1];
      this.timelines = new Timeline[paramInt1];
      this.uids = new int[paramInt1];
      this.childIndexByUid = new SparseIntArray();
      paramInt1 = 0;
      paramCollection = paramCollection.iterator();
      while (paramCollection.hasNext())
      {
        paramShuffleOrder = (DynamicConcatenatingMediaSource.MediaSourceHolder)paramCollection.next();
        this.timelines[paramInt1] = paramShuffleOrder.timeline;
        this.firstPeriodInChildIndices[paramInt1] = paramShuffleOrder.firstPeriodIndexInChild;
        this.firstWindowInChildIndices[paramInt1] = paramShuffleOrder.firstWindowIndexInChild;
        this.uids[paramInt1] = ((Integer)paramShuffleOrder.uid).intValue();
        this.childIndexByUid.put(this.uids[paramInt1], paramInt1);
        paramInt1++;
      }
    }
    
    protected int getChildIndexByChildUid(Object paramObject)
    {
      int i = -1;
      if (!(paramObject instanceof Integer)) {}
      for (;;)
      {
        return i;
        int j = this.childIndexByUid.get(((Integer)paramObject).intValue(), -1);
        i = j;
        if (j == -1) {
          i = -1;
        }
      }
    }
    
    protected int getChildIndexByPeriodIndex(int paramInt)
    {
      return Util.binarySearchFloor(this.firstPeriodInChildIndices, paramInt + 1, false, false);
    }
    
    protected int getChildIndexByWindowIndex(int paramInt)
    {
      return Util.binarySearchFloor(this.firstWindowInChildIndices, paramInt + 1, false, false);
    }
    
    protected Object getChildUidByChildIndex(int paramInt)
    {
      return Integer.valueOf(this.uids[paramInt]);
    }
    
    protected int getFirstPeriodIndexByChildIndex(int paramInt)
    {
      return this.firstPeriodInChildIndices[paramInt];
    }
    
    protected int getFirstWindowIndexByChildIndex(int paramInt)
    {
      return this.firstWindowInChildIndices[paramInt];
    }
    
    public int getPeriodCount()
    {
      return this.periodCount;
    }
    
    protected Timeline getTimelineByChildIndex(int paramInt)
    {
      return this.timelines[paramInt];
    }
    
    public int getWindowCount()
    {
      return this.windowCount;
    }
  }
  
  private static final class DeferredTimeline
    extends Timeline
  {
    private static final Object DUMMY_ID = new Object();
    private static final Timeline.Period period = new Timeline.Period();
    private final Object replacedID;
    private final Timeline timeline;
    
    public DeferredTimeline()
    {
      this.timeline = null;
      this.replacedID = null;
    }
    
    private DeferredTimeline(Timeline paramTimeline, Object paramObject)
    {
      this.timeline = paramTimeline;
      this.replacedID = paramObject;
    }
    
    public DeferredTimeline cloneWithNewTimeline(Timeline paramTimeline)
    {
      if ((this.replacedID == null) && (paramTimeline.getPeriodCount() > 0)) {}
      for (Object localObject = paramTimeline.getPeriod(0, period, true).uid;; localObject = this.replacedID) {
        return new DeferredTimeline(paramTimeline, localObject);
      }
    }
    
    public int getIndexOfPeriod(Object paramObject)
    {
      int i;
      if (this.timeline == null) {
        if (paramObject == DUMMY_ID) {
          i = 0;
        }
      }
      for (;;)
      {
        return i;
        i = -1;
        continue;
        Timeline localTimeline = this.timeline;
        Object localObject = paramObject;
        if (paramObject == DUMMY_ID) {
          localObject = this.replacedID;
        }
        i = localTimeline.getIndexOfPeriod(localObject);
      }
    }
    
    public Timeline.Period getPeriod(int paramInt, Timeline.Period paramPeriod, boolean paramBoolean)
    {
      Object localObject1 = null;
      Object localObject2;
      if (this.timeline == null) {
        if (paramBoolean)
        {
          localObject2 = DUMMY_ID;
          if (paramBoolean) {
            localObject1 = DUMMY_ID;
          }
          localObject2 = paramPeriod.set(localObject2, localObject1, 0, -9223372036854775807L, -9223372036854775807L);
        }
      }
      for (;;)
      {
        return (Timeline.Period)localObject2;
        localObject2 = null;
        break;
        this.timeline.getPeriod(paramInt, paramPeriod, paramBoolean);
        localObject2 = paramPeriod;
        if (paramPeriod.uid == this.replacedID)
        {
          paramPeriod.uid = DUMMY_ID;
          localObject2 = paramPeriod;
        }
      }
    }
    
    public int getPeriodCount()
    {
      if (this.timeline == null) {}
      for (int i = 1;; i = this.timeline.getPeriodCount()) {
        return i;
      }
    }
    
    public Timeline getTimeline()
    {
      return this.timeline;
    }
    
    public Timeline.Window getWindow(int paramInt, Timeline.Window paramWindow, boolean paramBoolean, long paramLong)
    {
      Object localObject;
      if (this.timeline == null) {
        if (paramBoolean) {
          localObject = DUMMY_ID;
        }
      }
      for (paramWindow = paramWindow.set(localObject, -9223372036854775807L, -9223372036854775807L, false, true, 0L, -9223372036854775807L, 0, 0, 0L);; paramWindow = this.timeline.getWindow(paramInt, paramWindow, paramBoolean, paramLong))
      {
        return paramWindow;
        localObject = null;
        break;
      }
    }
    
    public int getWindowCount()
    {
      if (this.timeline == null) {}
      for (int i = 1;; i = this.timeline.getWindowCount()) {
        return i;
      }
    }
  }
  
  private static final class EventDispatcher
  {
    public final Handler eventHandler;
    public final Runnable runnable;
    
    public EventDispatcher(Runnable paramRunnable)
    {
      this.runnable = paramRunnable;
      if (Looper.myLooper() != null) {}
      for (paramRunnable = Looper.myLooper();; paramRunnable = Looper.getMainLooper())
      {
        this.eventHandler = new Handler(paramRunnable);
        return;
      }
    }
    
    public void dispatchEvent()
    {
      this.eventHandler.post(this.runnable);
    }
  }
  
  private static final class MediaSourceHolder
    implements Comparable<MediaSourceHolder>
  {
    public int firstPeriodIndexInChild;
    public int firstWindowIndexInChild;
    public boolean isPrepared;
    public final MediaSource mediaSource;
    public DynamicConcatenatingMediaSource.DeferredTimeline timeline;
    public final Object uid;
    
    public MediaSourceHolder(MediaSource paramMediaSource, DynamicConcatenatingMediaSource.DeferredTimeline paramDeferredTimeline, int paramInt1, int paramInt2, Object paramObject)
    {
      this.mediaSource = paramMediaSource;
      this.timeline = paramDeferredTimeline;
      this.firstWindowIndexInChild = paramInt1;
      this.firstPeriodIndexInChild = paramInt2;
      this.uid = paramObject;
    }
    
    public int compareTo(MediaSourceHolder paramMediaSourceHolder)
    {
      return this.firstPeriodIndexInChild - paramMediaSourceHolder.firstPeriodIndexInChild;
    }
  }
  
  private static final class MessageData<CustomType>
  {
    public final DynamicConcatenatingMediaSource.EventDispatcher actionOnCompletion;
    public final CustomType customData;
    public final int index;
    
    public MessageData(int paramInt, CustomType paramCustomType, Runnable paramRunnable)
    {
      this.index = paramInt;
      if (paramRunnable != null) {}
      for (paramRunnable = new DynamicConcatenatingMediaSource.EventDispatcher(paramRunnable);; paramRunnable = null)
      {
        this.actionOnCompletion = paramRunnable;
        this.customData = paramCustomType;
        return;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/DynamicConcatenatingMediaSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */