package org.telegram.messenger.exoplayer;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.PriorityHandlerThread;
import org.telegram.messenger.exoplayer.util.TraceUtil;
import org.telegram.messenger.exoplayer.util.Util;

final class ExoPlayerImplInternal
  implements Handler.Callback
{
  private static final int IDLE_INTERVAL_MS = 1000;
  private static final int MSG_CUSTOM = 9;
  private static final int MSG_DO_SOME_WORK = 7;
  public static final int MSG_ERROR = 4;
  private static final int MSG_INCREMENTAL_PREPARE = 2;
  private static final int MSG_PREPARE = 1;
  public static final int MSG_PREPARED = 1;
  private static final int MSG_RELEASE = 5;
  private static final int MSG_SEEK_TO = 6;
  private static final int MSG_SET_PLAY_WHEN_READY = 3;
  public static final int MSG_SET_PLAY_WHEN_READY_ACK = 3;
  private static final int MSG_SET_RENDERER_SELECTED_TRACK = 8;
  public static final int MSG_STATE_CHANGED = 2;
  private static final int MSG_STOP = 4;
  private static final int PREPARE_INTERVAL_MS = 10;
  private static final int RENDERING_INTERVAL_MS = 10;
  private static final String TAG = "ExoPlayerImplInternal";
  private volatile long bufferedPositionUs;
  private int customMessagesProcessed = 0;
  private int customMessagesSent = 0;
  private volatile long durationUs;
  private long elapsedRealtimeUs;
  private final List<TrackRenderer> enabledRenderers;
  private final Handler eventHandler;
  private final Handler handler;
  private final HandlerThread internalPlaybackThread;
  private long lastSeekPositionMs;
  private final long minBufferUs;
  private final long minRebufferUs;
  private final AtomicInteger pendingSeekCount;
  private boolean playWhenReady;
  private volatile long positionUs;
  private boolean rebuffering;
  private boolean released;
  private MediaClock rendererMediaClock;
  private TrackRenderer rendererMediaClockSource;
  private TrackRenderer[] renderers;
  private final int[] selectedTrackIndices;
  private final StandaloneMediaClock standaloneMediaClock;
  private int state;
  private final MediaFormat[][] trackFormats;
  
  public ExoPlayerImplInternal(Handler paramHandler, boolean paramBoolean, int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    this.eventHandler = paramHandler;
    this.playWhenReady = paramBoolean;
    this.minBufferUs = (paramInt1 * 1000L);
    this.minRebufferUs = (paramInt2 * 1000L);
    this.selectedTrackIndices = Arrays.copyOf(paramArrayOfInt, paramArrayOfInt.length);
    this.state = 1;
    this.durationUs = -1L;
    this.bufferedPositionUs = -1L;
    this.standaloneMediaClock = new StandaloneMediaClock();
    this.pendingSeekCount = new AtomicInteger();
    this.enabledRenderers = new ArrayList(paramArrayOfInt.length);
    this.trackFormats = new MediaFormat[paramArrayOfInt.length][];
    this.internalPlaybackThread = new PriorityHandlerThread("ExoPlayerImplInternal:Handler", -16);
    this.internalPlaybackThread.start();
    this.handler = new Handler(this.internalPlaybackThread.getLooper(), this);
  }
  
  private void doSomeWork()
    throws ExoPlaybackException
  {
    TraceUtil.beginSection("doSomeWork");
    long l3 = SystemClock.elapsedRealtime();
    long l1;
    int i;
    int j;
    int k;
    long l2;
    label41:
    TrackRenderer localTrackRenderer;
    if (this.durationUs != -1L)
    {
      l1 = this.durationUs;
      i = 1;
      j = 1;
      updatePositionUs();
      k = 0;
      l2 = l1;
      if (k >= this.enabledRenderers.size()) {
        break label253;
      }
      localTrackRenderer = (TrackRenderer)this.enabledRenderers.get(k);
      localTrackRenderer.doSomeWork(this.positionUs, this.elapsedRealtimeUs);
      if ((i == 0) || (!localTrackRenderer.isEnded())) {
        break label157;
      }
      i = 1;
      label96:
      boolean bool = rendererReadyOrEnded(localTrackRenderer);
      if (!bool) {
        localTrackRenderer.maybeThrowError();
      }
      if ((j == 0) || (!bool)) {
        break label162;
      }
      j = 1;
      label125:
      if (l2 != -1L) {
        break label167;
      }
      l1 = l2;
    }
    for (;;)
    {
      k += 1;
      l2 = l1;
      break label41;
      l1 = Long.MAX_VALUE;
      break;
      label157:
      i = 0;
      break label96;
      label162:
      j = 0;
      break label125;
      label167:
      long l4 = localTrackRenderer.getDurationUs();
      long l5 = localTrackRenderer.getBufferedPositionUs();
      if (l5 == -1L)
      {
        l1 = -1L;
      }
      else
      {
        l1 = l2;
        if (l5 != -3L) {
          if ((l4 != -1L) && (l4 != -2L))
          {
            l1 = l2;
            if (l5 >= l4) {}
          }
          else
          {
            l1 = Math.min(l2, l5);
          }
        }
      }
    }
    label253:
    this.bufferedPositionUs = l2;
    if ((i != 0) && ((this.durationUs == -1L) || (this.durationUs <= this.positionUs)))
    {
      setState(5);
      stopRenderers();
      this.handler.removeMessages(7);
      if (((!this.playWhenReady) || (this.state != 4)) && (this.state != 3)) {
        break label405;
      }
      scheduleNextOperation(7, l3, 10L);
    }
    for (;;)
    {
      TraceUtil.endSection();
      return;
      if ((this.state == 3) && (j != 0))
      {
        setState(4);
        if (!this.playWhenReady) {
          break;
        }
        startRenderers();
        break;
      }
      if ((this.state != 4) || (j != 0)) {
        break;
      }
      this.rebuffering = this.playWhenReady;
      setState(3);
      stopRenderers();
      break;
      label405:
      if (!this.enabledRenderers.isEmpty()) {
        scheduleNextOperation(7, l3, 1000L);
      }
    }
  }
  
  private void enableRenderer(TrackRenderer paramTrackRenderer, int paramInt, boolean paramBoolean)
    throws ExoPlaybackException
  {
    paramTrackRenderer.enable(paramInt, this.positionUs, paramBoolean);
    this.enabledRenderers.add(paramTrackRenderer);
    MediaClock localMediaClock = paramTrackRenderer.getMediaClock();
    if (localMediaClock != null) {
      if (this.rendererMediaClock != null) {
        break label57;
      }
    }
    label57:
    for (paramBoolean = true;; paramBoolean = false)
    {
      Assertions.checkState(paramBoolean);
      this.rendererMediaClock = localMediaClock;
      this.rendererMediaClockSource = paramTrackRenderer;
      return;
    }
  }
  
  private void ensureDisabled(TrackRenderer paramTrackRenderer)
    throws ExoPlaybackException
  {
    ensureStopped(paramTrackRenderer);
    if (paramTrackRenderer.getState() == 2)
    {
      paramTrackRenderer.disable();
      if (paramTrackRenderer == this.rendererMediaClockSource)
      {
        this.rendererMediaClock = null;
        this.rendererMediaClockSource = null;
      }
    }
  }
  
  private void ensureStopped(TrackRenderer paramTrackRenderer)
    throws ExoPlaybackException
  {
    if (paramTrackRenderer.getState() == 3) {
      paramTrackRenderer.stop();
    }
  }
  
  private void incrementalPrepareInternal()
    throws ExoPlaybackException
  {
    long l1 = SystemClock.elapsedRealtime();
    int j = 1;
    int i = 0;
    TrackRenderer localTrackRenderer;
    while (i < this.renderers.length)
    {
      localTrackRenderer = this.renderers[i];
      k = j;
      if (localTrackRenderer.getState() == 0)
      {
        k = j;
        if (localTrackRenderer.prepare(this.positionUs) == 0)
        {
          localTrackRenderer.maybeThrowError();
          k = 0;
        }
      }
      i += 1;
      j = k;
    }
    if (j == 0)
    {
      scheduleNextOperation(2, l1, 10L);
      return;
    }
    long l2 = 0L;
    j = 1;
    int m = 1;
    int k = 0;
    if (k < this.renderers.length)
    {
      localTrackRenderer = this.renderers[k];
      int i1 = localTrackRenderer.getTrackCount();
      MediaFormat[] arrayOfMediaFormat = new MediaFormat[i1];
      i = 0;
      while (i < i1)
      {
        arrayOfMediaFormat[i] = localTrackRenderer.getFormat(i);
        i += 1;
      }
      this.trackFormats[k] = arrayOfMediaFormat;
      int n = j;
      i = m;
      long l3 = l2;
      if (i1 > 0)
      {
        if (l2 != -1L) {
          break label287;
        }
        l1 = l2;
        label183:
        i1 = this.selectedTrackIndices[k];
        n = j;
        i = m;
        l3 = l1;
        if (i1 >= 0)
        {
          n = j;
          i = m;
          l3 = l1;
          if (i1 < arrayOfMediaFormat.length)
          {
            enableRenderer(localTrackRenderer, i1, false);
            if ((j == 0) || (!localTrackRenderer.isEnded())) {
              break label336;
            }
            j = 1;
            label247:
            if ((m == 0) || (!rendererReadyOrEnded(localTrackRenderer))) {
              break label341;
            }
            i = 1;
            l3 = l1;
            n = j;
          }
        }
      }
      for (;;)
      {
        k += 1;
        j = n;
        m = i;
        l2 = l3;
        break;
        label287:
        l3 = localTrackRenderer.getDurationUs();
        if (l3 == -1L)
        {
          l1 = -1L;
          break label183;
        }
        l1 = l2;
        if (l3 == -2L) {
          break label183;
        }
        l1 = Math.max(l2, l3);
        break label183;
        label336:
        j = 0;
        break label247;
        label341:
        i = 0;
        n = j;
        l3 = l1;
      }
    }
    this.durationUs = l2;
    if ((j != 0) && ((l2 == -1L) || (l2 <= this.positionUs)))
    {
      this.state = 5;
      this.eventHandler.obtainMessage(1, this.state, 0, this.trackFormats).sendToTarget();
      if ((this.playWhenReady) && (this.state == 4)) {
        startRenderers();
      }
      this.handler.sendEmptyMessage(7);
      return;
    }
    if (m != 0) {}
    for (i = 4;; i = 3)
    {
      this.state = i;
      break;
    }
  }
  
  private void prepareInternal(TrackRenderer[] paramArrayOfTrackRenderer)
    throws ExoPlaybackException
  {
    resetInternal();
    this.renderers = paramArrayOfTrackRenderer;
    Arrays.fill(this.trackFormats, null);
    setState(2);
    incrementalPrepareInternal();
  }
  
  private void release(TrackRenderer paramTrackRenderer)
  {
    try
    {
      paramTrackRenderer.release();
      return;
    }
    catch (ExoPlaybackException paramTrackRenderer)
    {
      Log.e("ExoPlayerImplInternal", "Release failed.", paramTrackRenderer);
      return;
    }
    catch (RuntimeException paramTrackRenderer)
    {
      Log.e("ExoPlayerImplInternal", "Release failed.", paramTrackRenderer);
    }
  }
  
  private void releaseInternal()
  {
    resetInternal();
    setState(1);
    try
    {
      this.released = true;
      notifyAll();
      return;
    }
    finally {}
  }
  
  private boolean rendererReadyOrEnded(TrackRenderer paramTrackRenderer)
  {
    boolean bool2 = false;
    if (paramTrackRenderer.isEnded()) {}
    do
    {
      return true;
      if (!paramTrackRenderer.isReady()) {
        return false;
      }
    } while (this.state == 4);
    long l2 = paramTrackRenderer.getDurationUs();
    long l3 = paramTrackRenderer.getBufferedPositionUs();
    if (this.rebuffering) {}
    for (long l1 = this.minRebufferUs;; l1 = this.minBufferUs)
    {
      boolean bool1;
      if ((l1 > 0L) && (l3 != -1L) && (l3 != -3L) && (l3 < this.positionUs + l1))
      {
        bool1 = bool2;
        if (l2 != -1L)
        {
          bool1 = bool2;
          if (l2 != -2L)
          {
            bool1 = bool2;
            if (l3 < l2) {}
          }
        }
      }
      else
      {
        bool1 = true;
      }
      return bool1;
    }
  }
  
  private void resetInternal()
  {
    this.handler.removeMessages(7);
    this.handler.removeMessages(2);
    this.rebuffering = false;
    this.standaloneMediaClock.stop();
    if (this.renderers == null) {
      return;
    }
    int i = 0;
    while (i < this.renderers.length)
    {
      TrackRenderer localTrackRenderer = this.renderers[i];
      stopAndDisable(localTrackRenderer);
      release(localTrackRenderer);
      i += 1;
    }
    this.renderers = null;
    this.rendererMediaClock = null;
    this.rendererMediaClockSource = null;
    this.enabledRenderers.clear();
  }
  
  private void scheduleNextOperation(int paramInt, long paramLong1, long paramLong2)
  {
    paramLong1 = paramLong1 + paramLong2 - SystemClock.elapsedRealtime();
    if (paramLong1 <= 0L)
    {
      this.handler.sendEmptyMessage(paramInt);
      return;
    }
    this.handler.sendEmptyMessageDelayed(paramInt, paramLong1);
  }
  
  private void seekToInternal(long paramLong)
    throws ExoPlaybackException
  {
    try
    {
      long l = this.positionUs / 1000L;
      if (paramLong == l) {
        return;
      }
      this.rebuffering = false;
      this.positionUs = (paramLong * 1000L);
      this.standaloneMediaClock.stop();
      this.standaloneMediaClock.setPositionUs(this.positionUs);
      if (this.state != 1)
      {
        i = this.state;
        if (i != 2) {}
      }
      else
      {
        return;
      }
      int i = 0;
      while (i < this.enabledRenderers.size())
      {
        TrackRenderer localTrackRenderer = (TrackRenderer)this.enabledRenderers.get(i);
        ensureStopped(localTrackRenderer);
        localTrackRenderer.seekTo(this.positionUs);
        i += 1;
      }
      setState(3);
      this.handler.sendEmptyMessage(7);
      return;
    }
    finally
    {
      this.pendingSeekCount.decrementAndGet();
    }
  }
  
  /* Error */
  private <T> void sendMessageInternal(int paramInt, Object paramObject)
    throws ExoPlaybackException
  {
    // Byte code:
    //   0: aload_2
    //   1: checkcast 384	android/util/Pair
    //   4: astore_2
    //   5: aload_2
    //   6: getfield 388	android/util/Pair:first	Ljava/lang/Object;
    //   9: checkcast 390	org/telegram/messenger/exoplayer/ExoPlayer$ExoPlayerComponent
    //   12: iload_1
    //   13: aload_2
    //   14: getfield 393	android/util/Pair:second	Ljava/lang/Object;
    //   17: invokeinterface 396 3 0
    //   22: aload_0
    //   23: getfield 104	org/telegram/messenger/exoplayer/ExoPlayerImplInternal:state	I
    //   26: iconst_1
    //   27: if_icmpeq +21 -> 48
    //   30: aload_0
    //   31: getfield 104	org/telegram/messenger/exoplayer/ExoPlayerImplInternal:state	I
    //   34: iconst_2
    //   35: if_icmpeq +13 -> 48
    //   38: aload_0
    //   39: getfield 156	org/telegram/messenger/exoplayer/ExoPlayerImplInternal:handler	Landroid/os/Handler;
    //   42: bipush 7
    //   44: invokevirtual 323	android/os/Handler:sendEmptyMessage	(I)Z
    //   47: pop
    //   48: aload_0
    //   49: monitorenter
    //   50: aload_0
    //   51: aload_0
    //   52: getfield 84	org/telegram/messenger/exoplayer/ExoPlayerImplInternal:customMessagesProcessed	I
    //   55: iconst_1
    //   56: iadd
    //   57: putfield 84	org/telegram/messenger/exoplayer/ExoPlayerImplInternal:customMessagesProcessed	I
    //   60: aload_0
    //   61: invokevirtual 353	java/lang/Object:notifyAll	()V
    //   64: aload_0
    //   65: monitorexit
    //   66: return
    //   67: astore_2
    //   68: aload_0
    //   69: monitorexit
    //   70: aload_2
    //   71: athrow
    //   72: astore_2
    //   73: aload_0
    //   74: monitorenter
    //   75: aload_0
    //   76: aload_0
    //   77: getfield 84	org/telegram/messenger/exoplayer/ExoPlayerImplInternal:customMessagesProcessed	I
    //   80: iconst_1
    //   81: iadd
    //   82: putfield 84	org/telegram/messenger/exoplayer/ExoPlayerImplInternal:customMessagesProcessed	I
    //   85: aload_0
    //   86: invokevirtual 353	java/lang/Object:notifyAll	()V
    //   89: aload_0
    //   90: monitorexit
    //   91: aload_2
    //   92: athrow
    //   93: astore_2
    //   94: aload_0
    //   95: monitorexit
    //   96: aload_2
    //   97: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	98	0	this	ExoPlayerImplInternal
    //   0	98	1	paramInt	int
    //   0	98	2	paramObject	Object
    // Exception table:
    //   from	to	target	type
    //   50	66	67	finally
    //   68	70	67	finally
    //   0	48	72	finally
    //   75	91	93	finally
    //   94	96	93	finally
  }
  
  private void setPlayWhenReadyInternal(boolean paramBoolean)
    throws ExoPlaybackException
  {
    for (;;)
    {
      try
      {
        this.rebuffering = false;
        this.playWhenReady = paramBoolean;
        if (!paramBoolean)
        {
          stopRenderers();
          updatePositionUs();
          return;
        }
        if (this.state == 4)
        {
          startRenderers();
          this.handler.sendEmptyMessage(7);
          continue;
        }
        if (this.state != 3) {
          continue;
        }
      }
      finally
      {
        this.eventHandler.obtainMessage(3).sendToTarget();
      }
      this.handler.sendEmptyMessage(7);
    }
  }
  
  private void setRendererSelectedTrackInternal(int paramInt1, int paramInt2)
    throws ExoPlaybackException
  {
    boolean bool = true;
    if (this.selectedTrackIndices[paramInt1] == paramInt2) {}
    TrackRenderer localTrackRenderer;
    int i;
    label97:
    do
    {
      do
      {
        do
        {
          return;
          this.selectedTrackIndices[paramInt1] = paramInt2;
        } while ((this.state == 1) || (this.state == 2));
        localTrackRenderer = this.renderers[paramInt1];
        i = localTrackRenderer.getState();
      } while ((i == 0) || (i == -1) || (localTrackRenderer.getTrackCount() == 0));
      if ((i != 2) && (i != 3)) {
        break;
      }
      i = 1;
      if ((paramInt2 < 0) || (paramInt2 >= this.trackFormats[paramInt1].length)) {
        break label211;
      }
      paramInt1 = 1;
      if (i != 0)
      {
        if ((paramInt1 == 0) && (localTrackRenderer == this.rendererMediaClockSource)) {
          this.standaloneMediaClock.setPositionUs(this.rendererMediaClock.getPositionUs());
        }
        ensureDisabled(localTrackRenderer);
        this.enabledRenderers.remove(localTrackRenderer);
      }
    } while (paramInt1 == 0);
    if ((this.playWhenReady) && (this.state == 4))
    {
      paramInt1 = 1;
      label169:
      if ((i != 0) || (paramInt1 == 0)) {
        break label221;
      }
    }
    for (;;)
    {
      enableRenderer(localTrackRenderer, paramInt2, bool);
      if (paramInt1 != 0) {
        localTrackRenderer.start();
      }
      this.handler.sendEmptyMessage(7);
      return;
      i = 0;
      break;
      label211:
      paramInt1 = 0;
      break label97;
      paramInt1 = 0;
      break label169;
      label221:
      bool = false;
    }
  }
  
  private void setState(int paramInt)
  {
    if (this.state != paramInt)
    {
      this.state = paramInt;
      this.eventHandler.obtainMessage(2, paramInt, 0).sendToTarget();
    }
  }
  
  private void startRenderers()
    throws ExoPlaybackException
  {
    this.rebuffering = false;
    this.standaloneMediaClock.start();
    int i = 0;
    while (i < this.enabledRenderers.size())
    {
      ((TrackRenderer)this.enabledRenderers.get(i)).start();
      i += 1;
    }
  }
  
  private void stopAndDisable(TrackRenderer paramTrackRenderer)
  {
    try
    {
      ensureDisabled(paramTrackRenderer);
      return;
    }
    catch (ExoPlaybackException paramTrackRenderer)
    {
      Log.e("ExoPlayerImplInternal", "Stop failed.", paramTrackRenderer);
      return;
    }
    catch (RuntimeException paramTrackRenderer)
    {
      Log.e("ExoPlayerImplInternal", "Stop failed.", paramTrackRenderer);
    }
  }
  
  private void stopInternal()
  {
    resetInternal();
    setState(1);
  }
  
  private void stopRenderers()
    throws ExoPlaybackException
  {
    this.standaloneMediaClock.stop();
    int i = 0;
    while (i < this.enabledRenderers.size())
    {
      ensureStopped((TrackRenderer)this.enabledRenderers.get(i));
      i += 1;
    }
  }
  
  private void updatePositionUs()
  {
    if ((this.rendererMediaClock != null) && (this.enabledRenderers.contains(this.rendererMediaClockSource)) && (!this.rendererMediaClockSource.isEnded()))
    {
      this.positionUs = this.rendererMediaClock.getPositionUs();
      this.standaloneMediaClock.setPositionUs(this.positionUs);
    }
    for (;;)
    {
      this.elapsedRealtimeUs = (SystemClock.elapsedRealtime() * 1000L);
      return;
      this.positionUs = this.standaloneMediaClock.getPositionUs();
    }
  }
  
  /* Error */
  public void blockingSendMessage(ExoPlayer.ExoPlayerComponent paramExoPlayerComponent, int paramInt, Object paramObject)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 350	org/telegram/messenger/exoplayer/ExoPlayerImplInternal:released	Z
    //   6: ifeq +38 -> 44
    //   9: ldc 38
    //   11: new 432	java/lang/StringBuilder
    //   14: dup
    //   15: invokespecial 433	java/lang/StringBuilder:<init>	()V
    //   18: ldc_w 435
    //   21: invokevirtual 439	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   24: iload_2
    //   25: invokevirtual 442	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   28: ldc_w 444
    //   31: invokevirtual 439	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   34: invokevirtual 448	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   37: invokestatic 452	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   40: pop
    //   41: aload_0
    //   42: monitorexit
    //   43: return
    //   44: aload_0
    //   45: getfield 82	org/telegram/messenger/exoplayer/ExoPlayerImplInternal:customMessagesSent	I
    //   48: istore 4
    //   50: aload_0
    //   51: iload 4
    //   53: iconst_1
    //   54: iadd
    //   55: putfield 82	org/telegram/messenger/exoplayer/ExoPlayerImplInternal:customMessagesSent	I
    //   58: aload_0
    //   59: getfield 156	org/telegram/messenger/exoplayer/ExoPlayerImplInternal:handler	Landroid/os/Handler;
    //   62: bipush 9
    //   64: iload_2
    //   65: iconst_0
    //   66: aload_1
    //   67: aload_3
    //   68: invokestatic 456	android/util/Pair:create	(Ljava/lang/Object;Ljava/lang/Object;)Landroid/util/Pair;
    //   71: invokevirtual 314	android/os/Handler:obtainMessage	(IIILjava/lang/Object;)Landroid/os/Message;
    //   74: invokevirtual 319	android/os/Message:sendToTarget	()V
    //   77: aload_0
    //   78: getfield 84	org/telegram/messenger/exoplayer/ExoPlayerImplInternal:customMessagesProcessed	I
    //   81: istore_2
    //   82: iload_2
    //   83: iload 4
    //   85: if_icmpgt -44 -> 41
    //   88: aload_0
    //   89: invokevirtual 459	java/lang/Object:wait	()V
    //   92: goto -15 -> 77
    //   95: astore_1
    //   96: invokestatic 465	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   99: invokevirtual 468	java/lang/Thread:interrupt	()V
    //   102: goto -25 -> 77
    //   105: astore_1
    //   106: aload_0
    //   107: monitorexit
    //   108: aload_1
    //   109: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	110	0	this	ExoPlayerImplInternal
    //   0	110	1	paramExoPlayerComponent	ExoPlayer.ExoPlayerComponent
    //   0	110	2	paramInt	int
    //   0	110	3	paramObject	Object
    //   48	38	4	i	int
    // Exception table:
    //   from	to	target	type
    //   88	92	95	java/lang/InterruptedException
    //   2	41	105	finally
    //   44	77	105	finally
    //   77	82	105	finally
    //   88	92	105	finally
    //   96	102	105	finally
  }
  
  public long getBufferedPosition()
  {
    if (this.bufferedPositionUs == -1L) {
      return -1L;
    }
    return this.bufferedPositionUs / 1000L;
  }
  
  public long getCurrentPosition()
  {
    if (this.pendingSeekCount.get() > 0) {
      return this.lastSeekPositionMs;
    }
    return this.positionUs / 1000L;
  }
  
  public long getDuration()
  {
    if (this.durationUs == -1L) {
      return -1L;
    }
    return this.durationUs / 1000L;
  }
  
  public Looper getPlaybackLooper()
  {
    return this.internalPlaybackThread.getLooper();
  }
  
  public boolean handleMessage(Message paramMessage)
  {
    boolean bool = false;
    try
    {
      switch (paramMessage.what)
      {
      case 1: 
        prepareInternal((TrackRenderer[])paramMessage.obj);
        return true;
      }
    }
    catch (ExoPlaybackException paramMessage)
    {
      Log.e("ExoPlayerImplInternal", "Internal track renderer error.", paramMessage);
      this.eventHandler.obtainMessage(4, paramMessage).sendToTarget();
      stopInternal();
      return true;
    }
    catch (RuntimeException paramMessage)
    {
      Log.e("ExoPlayerImplInternal", "Internal runtime error.", paramMessage);
      this.eventHandler.obtainMessage(4, new ExoPlaybackException(paramMessage, true)).sendToTarget();
      stopInternal();
      return true;
    }
    incrementalPrepareInternal();
    return true;
    if (paramMessage.arg1 != 0) {
      bool = true;
    }
    setPlayWhenReadyInternal(bool);
    return true;
    doSomeWork();
    return true;
    seekToInternal(Util.getLong(paramMessage.arg1, paramMessage.arg2));
    return true;
    stopInternal();
    return true;
    releaseInternal();
    return true;
    sendMessageInternal(paramMessage.arg1, paramMessage.obj);
    return true;
    setRendererSelectedTrackInternal(paramMessage.arg1, paramMessage.arg2);
    return true;
    return false;
  }
  
  public void prepare(TrackRenderer... paramVarArgs)
  {
    this.handler.obtainMessage(1, paramVarArgs).sendToTarget();
  }
  
  public void release()
  {
    for (;;)
    {
      try
      {
        boolean bool = this.released;
        if (bool) {
          return;
        }
        this.handler.sendEmptyMessage(5);
        bool = this.released;
        if (!bool) {
          try
          {
            wait();
          }
          catch (InterruptedException localInterruptedException)
          {
            Thread.currentThread().interrupt();
          }
        } else {
          this.internalPlaybackThread.quit();
        }
      }
      finally {}
    }
  }
  
  public void seekTo(long paramLong)
  {
    this.lastSeekPositionMs = paramLong;
    this.pendingSeekCount.incrementAndGet();
    this.handler.obtainMessage(6, Util.getTopInt(paramLong), Util.getBottomInt(paramLong)).sendToTarget();
  }
  
  public void sendMessage(ExoPlayer.ExoPlayerComponent paramExoPlayerComponent, int paramInt, Object paramObject)
  {
    this.customMessagesSent += 1;
    this.handler.obtainMessage(9, paramInt, 0, Pair.create(paramExoPlayerComponent, paramObject)).sendToTarget();
  }
  
  public void setPlayWhenReady(boolean paramBoolean)
  {
    Handler localHandler = this.handler;
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localHandler.obtainMessage(3, i, 0).sendToTarget();
      return;
    }
  }
  
  public void setRendererSelectedTrack(int paramInt1, int paramInt2)
  {
    this.handler.obtainMessage(8, paramInt1, paramInt2).sendToTarget();
  }
  
  public void stop()
  {
    this.handler.sendEmptyMessage(4);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/ExoPlayerImplInternal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */