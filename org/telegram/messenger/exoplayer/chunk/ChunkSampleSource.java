package org.telegram.messenger.exoplayer.chunk;

import android.os.Handler;
import android.os.SystemClock;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.telegram.messenger.exoplayer.LoadControl;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.MediaFormatHolder;
import org.telegram.messenger.exoplayer.SampleHolder;
import org.telegram.messenger.exoplayer.SampleSource;
import org.telegram.messenger.exoplayer.SampleSource.SampleSourceReader;
import org.telegram.messenger.exoplayer.extractor.DefaultTrackOutput;
import org.telegram.messenger.exoplayer.upstream.DataSpec;
import org.telegram.messenger.exoplayer.upstream.Loader;
import org.telegram.messenger.exoplayer.upstream.Loader.Callback;
import org.telegram.messenger.exoplayer.upstream.Loader.Loadable;
import org.telegram.messenger.exoplayer.util.Assertions;

public class ChunkSampleSource
  implements SampleSource, SampleSource.SampleSourceReader, Loader.Callback
{
  public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT = 3;
  private static final long NO_RESET_PENDING = Long.MIN_VALUE;
  private static final int STATE_ENABLED = 3;
  private static final int STATE_IDLE = 0;
  private static final int STATE_INITIALIZED = 1;
  private static final int STATE_PREPARED = 2;
  private final int bufferSizeContribution;
  private final ChunkSource chunkSource;
  private long currentLoadStartTimeMs;
  private IOException currentLoadableException;
  private int currentLoadableExceptionCount;
  private long currentLoadableExceptionTimestamp;
  private final ChunkOperationHolder currentLoadableHolder;
  private Format downstreamFormat;
  private MediaFormat downstreamMediaFormat;
  private long downstreamPositionUs;
  private int enabledTrackCount;
  private final Handler eventHandler;
  private final EventListener eventListener;
  private final int eventSourceId;
  private long lastPerformedBufferOperation;
  private long lastSeekPositionUs;
  private final LoadControl loadControl;
  private Loader loader;
  private boolean loadingFinished;
  private final LinkedList<BaseMediaChunk> mediaChunks;
  private final int minLoadableRetryCount;
  private boolean pendingDiscontinuity;
  private long pendingResetPositionUs;
  private final List<BaseMediaChunk> readOnlyMediaChunks;
  protected final DefaultTrackOutput sampleQueue;
  private int state;
  
  public ChunkSampleSource(ChunkSource paramChunkSource, LoadControl paramLoadControl, int paramInt)
  {
    this(paramChunkSource, paramLoadControl, paramInt, null, null, 0);
  }
  
  public ChunkSampleSource(ChunkSource paramChunkSource, LoadControl paramLoadControl, int paramInt1, Handler paramHandler, EventListener paramEventListener, int paramInt2)
  {
    this(paramChunkSource, paramLoadControl, paramInt1, paramHandler, paramEventListener, paramInt2, 3);
  }
  
  public ChunkSampleSource(ChunkSource paramChunkSource, LoadControl paramLoadControl, int paramInt1, Handler paramHandler, EventListener paramEventListener, int paramInt2, int paramInt3)
  {
    this.chunkSource = paramChunkSource;
    this.loadControl = paramLoadControl;
    this.bufferSizeContribution = paramInt1;
    this.eventHandler = paramHandler;
    this.eventListener = paramEventListener;
    this.eventSourceId = paramInt2;
    this.minLoadableRetryCount = paramInt3;
    this.currentLoadableHolder = new ChunkOperationHolder();
    this.mediaChunks = new LinkedList();
    this.readOnlyMediaChunks = Collections.unmodifiableList(this.mediaChunks);
    this.sampleQueue = new DefaultTrackOutput(paramLoadControl.getAllocator());
    this.state = 0;
    this.pendingResetPositionUs = Long.MIN_VALUE;
  }
  
  private void clearCurrentLoadable()
  {
    this.currentLoadableHolder.chunk = null;
    clearCurrentLoadableException();
  }
  
  private void clearCurrentLoadableException()
  {
    this.currentLoadableException = null;
    this.currentLoadableExceptionCount = 0;
  }
  
  private boolean discardUpstreamMediaChunks(int paramInt)
  {
    if (this.mediaChunks.size() <= paramInt) {
      return false;
    }
    long l1 = 0L;
    long l2 = ((BaseMediaChunk)this.mediaChunks.getLast()).endTimeUs;
    BaseMediaChunk localBaseMediaChunk = null;
    while (this.mediaChunks.size() > paramInt)
    {
      localBaseMediaChunk = (BaseMediaChunk)this.mediaChunks.removeLast();
      l1 = localBaseMediaChunk.startTimeUs;
      this.loadingFinished = false;
    }
    this.sampleQueue.discardUpstreamSamples(localBaseMediaChunk.getFirstSampleIndex());
    notifyUpstreamDiscarded(l1, l2);
    return true;
  }
  
  private void doChunkOperation()
  {
    this.currentLoadableHolder.endOfStream = false;
    this.currentLoadableHolder.queueSize = this.readOnlyMediaChunks.size();
    ChunkSource localChunkSource = this.chunkSource;
    List localList = this.readOnlyMediaChunks;
    if (this.pendingResetPositionUs != Long.MIN_VALUE) {}
    for (long l = this.pendingResetPositionUs;; l = this.downstreamPositionUs)
    {
      localChunkSource.getChunkOperation(localList, l, this.currentLoadableHolder);
      this.loadingFinished = this.currentLoadableHolder.endOfStream;
      return;
    }
  }
  
  private long getNextLoadPositionUs()
  {
    if (isPendingReset()) {
      return this.pendingResetPositionUs;
    }
    if (this.loadingFinished) {
      return -1L;
    }
    return ((BaseMediaChunk)this.mediaChunks.getLast()).endTimeUs;
  }
  
  private long getRetryDelayMillis(long paramLong)
  {
    return Math.min((paramLong - 1L) * 1000L, 5000L);
  }
  
  private boolean isMediaChunk(Chunk paramChunk)
  {
    return paramChunk instanceof BaseMediaChunk;
  }
  
  private boolean isPendingReset()
  {
    return this.pendingResetPositionUs != Long.MIN_VALUE;
  }
  
  private void maybeStartLoading()
  {
    Chunk localChunk = this.currentLoadableHolder.chunk;
    if (localChunk == null) {
      return;
    }
    this.currentLoadStartTimeMs = SystemClock.elapsedRealtime();
    if (isMediaChunk(localChunk))
    {
      BaseMediaChunk localBaseMediaChunk = (BaseMediaChunk)localChunk;
      localBaseMediaChunk.init(this.sampleQueue);
      this.mediaChunks.add(localBaseMediaChunk);
      if (isPendingReset()) {
        this.pendingResetPositionUs = Long.MIN_VALUE;
      }
      notifyLoadStarted(localBaseMediaChunk.dataSpec.length, localBaseMediaChunk.type, localBaseMediaChunk.trigger, localBaseMediaChunk.format, localBaseMediaChunk.startTimeUs, localBaseMediaChunk.endTimeUs);
    }
    for (;;)
    {
      this.loader.startLoading(localChunk, this);
      return;
      notifyLoadStarted(localChunk.dataSpec.length, localChunk.type, localChunk.trigger, localChunk.format, -1L, -1L);
    }
  }
  
  private void notifyDownstreamFormatChanged(final Format paramFormat, final int paramInt, final long paramLong)
  {
    if ((this.eventHandler != null) && (this.eventListener != null)) {
      this.eventHandler.post(new Runnable()
      {
        public void run()
        {
          ChunkSampleSource.this.eventListener.onDownstreamFormatChanged(ChunkSampleSource.this.eventSourceId, paramFormat, paramInt, ChunkSampleSource.this.usToMs(paramLong));
        }
      });
    }
  }
  
  private void notifyLoadCanceled(final long paramLong)
  {
    if ((this.eventHandler != null) && (this.eventListener != null)) {
      this.eventHandler.post(new Runnable()
      {
        public void run()
        {
          ChunkSampleSource.this.eventListener.onLoadCanceled(ChunkSampleSource.this.eventSourceId, paramLong);
        }
      });
    }
  }
  
  private void notifyLoadCompleted(final long paramLong1, int paramInt1, final int paramInt2, final Format paramFormat, final long paramLong2, final long paramLong3, long paramLong4, final long paramLong5)
  {
    if ((this.eventHandler != null) && (this.eventListener != null)) {
      this.eventHandler.post(new Runnable()
      {
        public void run()
        {
          ChunkSampleSource.this.eventListener.onLoadCompleted(ChunkSampleSource.this.eventSourceId, paramLong1, paramInt2, paramFormat, paramLong2, ChunkSampleSource.this.usToMs(paramLong3), ChunkSampleSource.this.usToMs(paramLong5), this.val$elapsedRealtimeMs, this.val$loadDurationMs);
        }
      });
    }
  }
  
  private void notifyLoadError(final IOException paramIOException)
  {
    if ((this.eventHandler != null) && (this.eventListener != null)) {
      this.eventHandler.post(new Runnable()
      {
        public void run()
        {
          ChunkSampleSource.this.eventListener.onLoadError(ChunkSampleSource.this.eventSourceId, paramIOException);
        }
      });
    }
  }
  
  private void notifyLoadStarted(final long paramLong1, int paramInt1, final int paramInt2, final Format paramFormat, final long paramLong2, final long paramLong3)
  {
    if ((this.eventHandler != null) && (this.eventListener != null)) {
      this.eventHandler.post(new Runnable()
      {
        public void run()
        {
          ChunkSampleSource.this.eventListener.onLoadStarted(ChunkSampleSource.this.eventSourceId, paramLong1, paramInt2, paramFormat, paramLong2, ChunkSampleSource.this.usToMs(paramLong3), ChunkSampleSource.this.usToMs(this.val$mediaEndTimeUs));
        }
      });
    }
  }
  
  private void notifyUpstreamDiscarded(final long paramLong1, long paramLong2)
  {
    if ((this.eventHandler != null) && (this.eventListener != null)) {
      this.eventHandler.post(new Runnable()
      {
        public void run()
        {
          ChunkSampleSource.this.eventListener.onUpstreamDiscarded(ChunkSampleSource.this.eventSourceId, ChunkSampleSource.this.usToMs(paramLong1), ChunkSampleSource.this.usToMs(this.val$mediaEndTimeUs));
        }
      });
    }
  }
  
  private void restartFrom(long paramLong)
  {
    this.pendingResetPositionUs = paramLong;
    this.loadingFinished = false;
    if (this.loader.isLoading())
    {
      this.loader.cancelLoading();
      return;
    }
    this.sampleQueue.clear();
    this.mediaChunks.clear();
    clearCurrentLoadable();
    updateLoadControl();
  }
  
  private void resumeFromBackOff()
  {
    this.currentLoadableException = null;
    Chunk localChunk = this.currentLoadableHolder.chunk;
    if (!isMediaChunk(localChunk))
    {
      doChunkOperation();
      discardUpstreamMediaChunks(this.currentLoadableHolder.queueSize);
      if (this.currentLoadableHolder.chunk == localChunk)
      {
        this.loader.startLoading(localChunk, this);
        return;
      }
      notifyLoadCanceled(localChunk.bytesLoaded());
      maybeStartLoading();
      return;
    }
    if (localChunk == this.mediaChunks.getFirst())
    {
      this.loader.startLoading(localChunk, this);
      return;
    }
    BaseMediaChunk localBaseMediaChunk = (BaseMediaChunk)this.mediaChunks.removeLast();
    if (localChunk == localBaseMediaChunk) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      doChunkOperation();
      this.mediaChunks.add(localBaseMediaChunk);
      if (this.currentLoadableHolder.chunk != localChunk) {
        break;
      }
      this.loader.startLoading(localChunk, this);
      return;
    }
    notifyLoadCanceled(localChunk.bytesLoaded());
    discardUpstreamMediaChunks(this.currentLoadableHolder.queueSize);
    clearCurrentLoadableException();
    maybeStartLoading();
  }
  
  private void updateLoadControl()
  {
    long l3 = SystemClock.elapsedRealtime();
    long l2 = getNextLoadPositionUs();
    int i;
    boolean bool1;
    label37:
    long l1;
    boolean bool2;
    if (this.currentLoadableException != null)
    {
      i = 1;
      if ((!this.loader.isLoading()) && (i == 0)) {
        break label171;
      }
      bool1 = true;
      l1 = l2;
      if (!bool1) {
        if ((this.currentLoadableHolder.chunk != null) || (l2 == -1L))
        {
          l1 = l2;
          if (l3 - this.lastPerformedBufferOperation <= 2000L) {}
        }
        else
        {
          this.lastPerformedBufferOperation = l3;
          doChunkOperation();
          bool2 = discardUpstreamMediaChunks(this.currentLoadableHolder.queueSize);
          if (this.currentLoadableHolder.chunk != null) {
            break label177;
          }
          l1 = -1L;
        }
      }
      label118:
      bool1 = this.loadControl.update(this, this.downstreamPositionUs, l1, bool1);
      if (i == 0) {
        break label193;
      }
      if (l3 - this.currentLoadableExceptionTimestamp >= getRetryDelayMillis(this.currentLoadableExceptionCount)) {
        resumeFromBackOff();
      }
    }
    label171:
    label177:
    label193:
    while ((this.loader.isLoading()) || (!bool1))
    {
      return;
      i = 0;
      break;
      bool1 = false;
      break label37;
      l1 = l2;
      if (!bool2) {
        break label118;
      }
      l1 = getNextLoadPositionUs();
      break label118;
    }
    maybeStartLoading();
  }
  
  public boolean continueBuffering(int paramInt, long paramLong)
  {
    boolean bool2 = false;
    if (this.state == 3) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      Assertions.checkState(bool1);
      this.downstreamPositionUs = paramLong;
      this.chunkSource.continueBuffering(paramLong);
      updateLoadControl();
      if (!this.loadingFinished)
      {
        bool1 = bool2;
        if (this.sampleQueue.isEmpty()) {}
      }
      else
      {
        bool1 = true;
      }
      return bool1;
    }
  }
  
  public void disable(int paramInt)
  {
    boolean bool2 = true;
    boolean bool1;
    if (this.state == 3) {
      bool1 = true;
    }
    for (;;)
    {
      Assertions.checkState(bool1);
      paramInt = this.enabledTrackCount - 1;
      this.enabledTrackCount = paramInt;
      if (paramInt == 0)
      {
        bool1 = bool2;
        label34:
        Assertions.checkState(bool1);
        this.state = 2;
      }
      try
      {
        this.chunkSource.disable(this.mediaChunks);
        this.loadControl.unregister(this);
        if (this.loader.isLoading())
        {
          this.loader.cancelLoading();
          return;
          bool1 = false;
          continue;
          bool1 = false;
          break label34;
        }
        else
        {
          this.sampleQueue.clear();
          this.mediaChunks.clear();
          clearCurrentLoadable();
          this.loadControl.trimAllocator();
          return;
        }
      }
      finally
      {
        this.loadControl.unregister(this);
        if (!this.loader.isLoading()) {
          break label154;
        }
      }
    }
    this.loader.cancelLoading();
    for (;;)
    {
      throw ((Throwable)localObject);
      label154:
      this.sampleQueue.clear();
      this.mediaChunks.clear();
      clearCurrentLoadable();
      this.loadControl.trimAllocator();
    }
  }
  
  public void enable(int paramInt, long paramLong)
  {
    boolean bool2 = true;
    if (this.state == 2)
    {
      bool1 = true;
      Assertions.checkState(bool1);
      int i = this.enabledTrackCount;
      this.enabledTrackCount = (i + 1);
      if (i != 0) {
        break label113;
      }
    }
    label113:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      Assertions.checkState(bool1);
      this.state = 3;
      this.chunkSource.enable(paramInt);
      this.loadControl.register(this, this.bufferSizeContribution);
      this.downstreamFormat = null;
      this.downstreamMediaFormat = null;
      this.downstreamPositionUs = paramLong;
      this.lastSeekPositionUs = paramLong;
      this.pendingDiscontinuity = false;
      restartFrom(paramLong);
      return;
      bool1 = false;
      break;
    }
  }
  
  public long getBufferedPositionUs()
  {
    boolean bool;
    long l1;
    if (this.state == 3)
    {
      bool = true;
      Assertions.checkState(bool);
      if (!isPendingReset()) {
        break label33;
      }
      l1 = this.pendingResetPositionUs;
    }
    label33:
    long l2;
    do
    {
      return l1;
      bool = false;
      break;
      if (this.loadingFinished) {
        return -3L;
      }
      l2 = this.sampleQueue.getLargestParsedTimestampUs();
      l1 = l2;
    } while (l2 != Long.MIN_VALUE);
    return this.downstreamPositionUs;
  }
  
  public MediaFormat getFormat(int paramInt)
  {
    if ((this.state == 2) || (this.state == 3)) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      return this.chunkSource.getFormat(paramInt);
    }
  }
  
  public int getTrackCount()
  {
    if ((this.state == 2) || (this.state == 3)) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      return this.chunkSource.getTrackCount();
    }
  }
  
  public void maybeThrowError()
    throws IOException
  {
    if ((this.currentLoadableException != null) && (this.currentLoadableExceptionCount > this.minLoadableRetryCount)) {
      throw this.currentLoadableException;
    }
    if (this.currentLoadableHolder.chunk == null) {
      this.chunkSource.maybeThrowError();
    }
  }
  
  public void onLoadCanceled(Loader.Loadable paramLoadable)
  {
    notifyLoadCanceled(this.currentLoadableHolder.chunk.bytesLoaded());
    clearCurrentLoadable();
    if (this.state == 3)
    {
      restartFrom(this.pendingResetPositionUs);
      return;
    }
    this.sampleQueue.clear();
    this.mediaChunks.clear();
    clearCurrentLoadable();
    this.loadControl.trimAllocator();
  }
  
  public void onLoadCompleted(Loader.Loadable paramLoadable)
  {
    long l1 = SystemClock.elapsedRealtime();
    long l2 = l1 - this.currentLoadStartTimeMs;
    paramLoadable = this.currentLoadableHolder.chunk;
    this.chunkSource.onChunkLoadCompleted(paramLoadable);
    if (isMediaChunk(paramLoadable))
    {
      BaseMediaChunk localBaseMediaChunk = (BaseMediaChunk)paramLoadable;
      notifyLoadCompleted(paramLoadable.bytesLoaded(), localBaseMediaChunk.type, localBaseMediaChunk.trigger, localBaseMediaChunk.format, localBaseMediaChunk.startTimeUs, localBaseMediaChunk.endTimeUs, l1, l2);
    }
    for (;;)
    {
      clearCurrentLoadable();
      updateLoadControl();
      return;
      notifyLoadCompleted(paramLoadable.bytesLoaded(), paramLoadable.type, paramLoadable.trigger, paramLoadable.format, -1L, -1L, l1, l2);
    }
  }
  
  public void onLoadError(Loader.Loadable paramLoadable, IOException paramIOException)
  {
    this.currentLoadableException = paramIOException;
    this.currentLoadableExceptionCount += 1;
    this.currentLoadableExceptionTimestamp = SystemClock.elapsedRealtime();
    notifyLoadError(paramIOException);
    this.chunkSource.onChunkLoadError(this.currentLoadableHolder.chunk, paramIOException);
    updateLoadControl();
  }
  
  protected void onSampleRead(MediaChunk paramMediaChunk, SampleHolder paramSampleHolder) {}
  
  public boolean prepare(long paramLong)
  {
    if ((this.state == 1) || (this.state == 2)) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      if (this.state != 2) {
        break;
      }
      return true;
    }
    if (!this.chunkSource.prepare()) {
      return false;
    }
    if (this.chunkSource.getTrackCount() > 0) {
      this.loader = new Loader("Loader:" + this.chunkSource.getFormat(0).mimeType);
    }
    this.state = 2;
    return true;
  }
  
  public int readData(int paramInt, long paramLong, MediaFormatHolder paramMediaFormatHolder, SampleHolder paramSampleHolder)
  {
    if (this.state == 3) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      this.downstreamPositionUs = paramLong;
      if ((!this.pendingDiscontinuity) && (!isPendingReset())) {
        break;
      }
      return -2;
    }
    if (!this.sampleQueue.isEmpty()) {}
    BaseMediaChunk localBaseMediaChunk;
    for (paramInt = 1;; paramInt = 0) {
      for (localBaseMediaChunk = (BaseMediaChunk)this.mediaChunks.getFirst(); (paramInt != 0) && (this.mediaChunks.size() > 1) && (((BaseMediaChunk)this.mediaChunks.get(1)).getFirstSampleIndex() <= this.sampleQueue.getReadIndex()); localBaseMediaChunk = (BaseMediaChunk)this.mediaChunks.getFirst()) {
        this.mediaChunks.removeFirst();
      }
    }
    Object localObject = localBaseMediaChunk.format;
    if (!((Format)localObject).equals(this.downstreamFormat)) {
      notifyDownstreamFormatChanged((Format)localObject, localBaseMediaChunk.trigger, localBaseMediaChunk.startTimeUs);
    }
    this.downstreamFormat = ((Format)localObject);
    if ((paramInt != 0) || (localBaseMediaChunk.isMediaFormatFinal))
    {
      localObject = localBaseMediaChunk.getMediaFormat();
      if (!((MediaFormat)localObject).equals(this.downstreamMediaFormat))
      {
        paramMediaFormatHolder.format = ((MediaFormat)localObject);
        paramMediaFormatHolder.drmInitData = localBaseMediaChunk.getDrmInitData();
        this.downstreamMediaFormat = ((MediaFormat)localObject);
        return -4;
      }
      this.downstreamMediaFormat = ((MediaFormat)localObject);
    }
    if (paramInt == 0)
    {
      if (this.loadingFinished) {
        return -1;
      }
      return -2;
    }
    if (this.sampleQueue.getSample(paramSampleHolder))
    {
      int i;
      if (paramSampleHolder.timeUs < this.lastSeekPositionUs)
      {
        paramInt = 1;
        i = paramSampleHolder.flags;
        if (paramInt == 0) {
          break label322;
        }
      }
      label322:
      for (paramInt = 134217728;; paramInt = 0)
      {
        paramSampleHolder.flags = (paramInt | i);
        onSampleRead(localBaseMediaChunk, paramSampleHolder);
        return -3;
        paramInt = 0;
        break;
      }
    }
    return -2;
  }
  
  public long readDiscontinuity(int paramInt)
  {
    if (this.pendingDiscontinuity)
    {
      this.pendingDiscontinuity = false;
      return this.lastSeekPositionUs;
    }
    return Long.MIN_VALUE;
  }
  
  public SampleSource.SampleSourceReader register()
  {
    if (this.state == 0) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      this.state = 1;
      return this;
    }
  }
  
  public void release()
  {
    if (this.state != 3) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      if (this.loader != null)
      {
        this.loader.release();
        this.loader = null;
      }
      this.state = 0;
      return;
    }
  }
  
  public void seekToUs(long paramLong)
  {
    boolean bool;
    if (this.state == 3)
    {
      bool = true;
      Assertions.checkState(bool);
      if (!isPendingReset()) {
        break label53;
      }
    }
    label53:
    for (long l = this.pendingResetPositionUs;; l = this.downstreamPositionUs)
    {
      this.downstreamPositionUs = paramLong;
      this.lastSeekPositionUs = paramLong;
      if (l != paramLong) {
        break label62;
      }
      return;
      bool = false;
      break;
    }
    label62:
    int i;
    if ((!isPendingReset()) && (this.sampleQueue.skipToKeyframeBefore(paramLong)))
    {
      i = 1;
      if (i == 0) {
        break label158;
      }
      if (this.sampleQueue.isEmpty()) {
        break label153;
      }
      i = 1;
    }
    for (;;)
    {
      if ((i == 0) || (this.mediaChunks.size() <= 1) || (((BaseMediaChunk)this.mediaChunks.get(1)).getFirstSampleIndex() > this.sampleQueue.getReadIndex())) {
        break label163;
      }
      this.mediaChunks.removeFirst();
      continue;
      i = 0;
      break;
      label153:
      i = 0;
    }
    label158:
    restartFrom(paramLong);
    label163:
    this.pendingDiscontinuity = true;
  }
  
  protected final long usToMs(long paramLong)
  {
    return paramLong / 1000L;
  }
  
  public static abstract interface EventListener
    extends BaseChunkSampleSourceEventListener
  {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/chunk/ChunkSampleSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */