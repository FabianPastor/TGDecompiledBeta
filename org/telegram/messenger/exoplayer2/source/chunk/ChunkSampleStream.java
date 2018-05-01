package org.telegram.messenger.exoplayer2.source.chunk;

import android.util.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.FormatHolder;
import org.telegram.messenger.exoplayer2.SeekParameters;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.source.SampleQueue;
import org.telegram.messenger.exoplayer2.source.SampleStream;
import org.telegram.messenger.exoplayer2.source.SequenceableLoader;
import org.telegram.messenger.exoplayer2.source.SequenceableLoader.Callback;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.upstream.Loader;
import org.telegram.messenger.exoplayer2.upstream.Loader.Callback;
import org.telegram.messenger.exoplayer2.upstream.Loader.Loadable;
import org.telegram.messenger.exoplayer2.upstream.Loader.ReleaseCallback;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

public class ChunkSampleStream<T extends ChunkSource>
  implements SampleStream, SequenceableLoader, Loader.Callback<Chunk>, Loader.ReleaseCallback
{
  private static final String TAG = "ChunkSampleStream";
  private final SequenceableLoader.Callback<ChunkSampleStream<T>> callback;
  private final T chunkSource;
  long decodeOnlyUntilPositionUs;
  private final SampleQueue[] embeddedSampleQueues;
  private final Format[] embeddedTrackFormats;
  private final int[] embeddedTrackTypes;
  private final boolean[] embeddedTracksSelected;
  private final MediaSourceEventListener.EventDispatcher eventDispatcher;
  private long lastSeekPositionUs;
  private final Loader loader;
  boolean loadingFinished;
  private final BaseMediaChunkOutput mediaChunkOutput;
  private final ArrayList<BaseMediaChunk> mediaChunks;
  private final int minLoadableRetryCount;
  private final ChunkHolder nextChunkHolder;
  private long pendingResetPositionUs;
  private Format primaryDownstreamTrackFormat;
  private final SampleQueue primarySampleQueue;
  public final int primaryTrackType;
  private final List<BaseMediaChunk> readOnlyMediaChunks;
  private ReleaseCallback<T> releaseCallback;
  
  public ChunkSampleStream(int paramInt1, int[] paramArrayOfInt, Format[] paramArrayOfFormat, T paramT, SequenceableLoader.Callback<ChunkSampleStream<T>> paramCallback, Allocator paramAllocator, long paramLong, int paramInt2, MediaSourceEventListener.EventDispatcher paramEventDispatcher)
  {
    this.primaryTrackType = paramInt1;
    this.embeddedTrackTypes = paramArrayOfInt;
    this.embeddedTrackFormats = paramArrayOfFormat;
    this.chunkSource = paramT;
    this.callback = paramCallback;
    this.eventDispatcher = paramEventDispatcher;
    this.minLoadableRetryCount = paramInt2;
    this.loader = new Loader("Loader:ChunkSampleStream");
    this.nextChunkHolder = new ChunkHolder();
    this.mediaChunks = new ArrayList();
    this.readOnlyMediaChunks = Collections.unmodifiableList(this.mediaChunks);
    if (paramArrayOfInt == null) {}
    for (paramInt2 = 0;; paramInt2 = paramArrayOfInt.length)
    {
      this.embeddedSampleQueues = new SampleQueue[paramInt2];
      this.embeddedTracksSelected = new boolean[paramInt2];
      paramArrayOfFormat = new int[paramInt2 + 1];
      paramCallback = new SampleQueue[paramInt2 + 1];
      this.primarySampleQueue = new SampleQueue(paramAllocator);
      paramArrayOfFormat[0] = paramInt1;
      paramCallback[0] = this.primarySampleQueue;
      for (paramInt1 = 0; paramInt1 < paramInt2; paramInt1++)
      {
        paramT = new SampleQueue(paramAllocator);
        this.embeddedSampleQueues[paramInt1] = paramT;
        paramCallback[(paramInt1 + 1)] = paramT;
        paramArrayOfFormat[(paramInt1 + 1)] = paramArrayOfInt[paramInt1];
      }
    }
    this.mediaChunkOutput = new BaseMediaChunkOutput(paramArrayOfFormat, paramCallback);
    this.pendingResetPositionUs = paramLong;
    this.lastSeekPositionUs = paramLong;
  }
  
  private void discardDownstreamMediaChunks(int paramInt)
  {
    paramInt = primaryStreamIndexToMediaChunkIndex(paramInt, 0);
    if (paramInt > 0) {
      Util.removeRange(this.mediaChunks, 0, paramInt);
    }
  }
  
  private BaseMediaChunk discardUpstreamMediaChunksFromIndex(int paramInt)
  {
    BaseMediaChunk localBaseMediaChunk = (BaseMediaChunk)this.mediaChunks.get(paramInt);
    Util.removeRange(this.mediaChunks, paramInt, this.mediaChunks.size());
    this.primarySampleQueue.discardUpstreamSamples(localBaseMediaChunk.getFirstSampleIndex(0));
    for (paramInt = 0; paramInt < this.embeddedSampleQueues.length; paramInt++) {
      this.embeddedSampleQueues[paramInt].discardUpstreamSamples(localBaseMediaChunk.getFirstSampleIndex(paramInt + 1));
    }
    return localBaseMediaChunk;
  }
  
  private BaseMediaChunk getLastMediaChunk()
  {
    return (BaseMediaChunk)this.mediaChunks.get(this.mediaChunks.size() - 1);
  }
  
  private boolean haveReadFromMediaChunk(int paramInt)
  {
    boolean bool1 = true;
    BaseMediaChunk localBaseMediaChunk = (BaseMediaChunk)this.mediaChunks.get(paramInt);
    boolean bool2;
    if (this.primarySampleQueue.getReadIndex() > localBaseMediaChunk.getFirstSampleIndex(0)) {
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      for (paramInt = 0;; paramInt++)
      {
        if (paramInt >= this.embeddedSampleQueues.length) {
          break label74;
        }
        bool2 = bool1;
        if (this.embeddedSampleQueues[paramInt].getReadIndex() > localBaseMediaChunk.getFirstSampleIndex(paramInt + 1)) {
          break;
        }
      }
      label74:
      bool2 = false;
    }
  }
  
  private boolean isMediaChunk(Chunk paramChunk)
  {
    return paramChunk instanceof BaseMediaChunk;
  }
  
  private void maybeNotifyPrimaryTrackFormatChanged(int paramInt)
  {
    BaseMediaChunk localBaseMediaChunk = (BaseMediaChunk)this.mediaChunks.get(paramInt);
    Format localFormat = localBaseMediaChunk.trackFormat;
    if (!localFormat.equals(this.primaryDownstreamTrackFormat)) {
      this.eventDispatcher.downstreamFormatChanged(this.primaryTrackType, localFormat, localBaseMediaChunk.trackSelectionReason, localBaseMediaChunk.trackSelectionData, localBaseMediaChunk.startTimeUs);
    }
    this.primaryDownstreamTrackFormat = localFormat;
  }
  
  private void maybeNotifyPrimaryTrackFormatChanged(int paramInt1, int paramInt2)
  {
    int i = primaryStreamIndexToMediaChunkIndex(paramInt1 - paramInt2, 0);
    if (paramInt2 == 1) {
      paramInt1 = i;
    }
    while (i <= paramInt1)
    {
      maybeNotifyPrimaryTrackFormatChanged(i);
      i++;
      continue;
      paramInt1 = primaryStreamIndexToMediaChunkIndex(paramInt1 - 1, i);
    }
  }
  
  private int primaryStreamIndexToMediaChunkIndex(int paramInt1, int paramInt2)
  {
    
    if (paramInt2 < this.mediaChunks.size()) {
      if (((BaseMediaChunk)this.mediaChunks.get(paramInt2)).getFirstSampleIndex(0) <= paramInt1) {}
    }
    for (paramInt1 = paramInt2 - 1;; paramInt1 = this.mediaChunks.size() - 1)
    {
      return paramInt1;
      paramInt2++;
      break;
    }
  }
  
  public boolean continueLoading(long paramLong)
  {
    boolean bool1;
    if ((this.loadingFinished) || (this.loader.isLoading())) {
      bool1 = false;
    }
    boolean bool2;
    Object localObject;
    for (;;)
    {
      return bool1;
      bool2 = isPendingReset();
      if (bool2) {
        localObject = null;
      }
      for (long l = this.pendingResetPositionUs;; l = ((MediaChunk)localObject).endTimeUs)
      {
        this.chunkSource.getNextChunk((MediaChunk)localObject, paramLong, l, this.nextChunkHolder);
        bool1 = this.nextChunkHolder.endOfStream;
        localObject = this.nextChunkHolder.chunk;
        this.nextChunkHolder.clear();
        if (!bool1) {
          break label120;
        }
        this.pendingResetPositionUs = -9223372036854775807L;
        this.loadingFinished = true;
        bool1 = true;
        break;
        localObject = getLastMediaChunk();
      }
      label120:
      if (localObject != null) {
        break;
      }
      bool1 = false;
    }
    BaseMediaChunk localBaseMediaChunk;
    int i;
    if (isMediaChunk((Chunk)localObject))
    {
      localBaseMediaChunk = (BaseMediaChunk)localObject;
      if (bool2)
      {
        if (localBaseMediaChunk.startTimeUs != this.pendingResetPositionUs) {
          break label274;
        }
        i = 1;
        label167:
        if (i == 0) {
          break label280;
        }
      }
    }
    label274:
    label280:
    for (paramLong = Long.MIN_VALUE;; paramLong = this.pendingResetPositionUs)
    {
      this.decodeOnlyUntilPositionUs = paramLong;
      this.pendingResetPositionUs = -9223372036854775807L;
      localBaseMediaChunk.init(this.mediaChunkOutput);
      this.mediaChunks.add(localBaseMediaChunk);
      paramLong = this.loader.startLoading((Loader.Loadable)localObject, this, this.minLoadableRetryCount);
      this.eventDispatcher.loadStarted(((Chunk)localObject).dataSpec, ((Chunk)localObject).type, this.primaryTrackType, ((Chunk)localObject).trackFormat, ((Chunk)localObject).trackSelectionReason, ((Chunk)localObject).trackSelectionData, ((Chunk)localObject).startTimeUs, ((Chunk)localObject).endTimeUs, paramLong);
      bool1 = true;
      break;
      i = 0;
      break label167;
    }
  }
  
  public void discardBuffer(long paramLong, boolean paramBoolean)
  {
    int i = this.primarySampleQueue.getFirstIndex();
    this.primarySampleQueue.discardTo(paramLong, paramBoolean, true);
    int j = this.primarySampleQueue.getFirstIndex();
    if (j > i)
    {
      paramLong = this.primarySampleQueue.getFirstTimestampUs();
      for (i = 0; i < this.embeddedSampleQueues.length; i++) {
        this.embeddedSampleQueues[i].discardTo(paramLong, paramBoolean, this.embeddedTracksSelected[i]);
      }
      discardDownstreamMediaChunks(j);
    }
  }
  
  public long getAdjustedSeekPositionUs(long paramLong, SeekParameters paramSeekParameters)
  {
    return this.chunkSource.getAdjustedSeekPositionUs(paramLong, paramSeekParameters);
  }
  
  public long getBufferedPositionUs()
  {
    if (this.loadingFinished) {}
    for (long l1 = Long.MIN_VALUE;; l1 = this.pendingResetPositionUs)
    {
      return l1;
      if (!isPendingReset()) {
        break;
      }
    }
    long l2 = this.lastSeekPositionUs;
    BaseMediaChunk localBaseMediaChunk = getLastMediaChunk();
    if (localBaseMediaChunk.isLoadCompleted()) {}
    for (;;)
    {
      l1 = l2;
      if (localBaseMediaChunk != null) {
        l1 = Math.max(l2, localBaseMediaChunk.endTimeUs);
      }
      l1 = Math.max(l1, this.primarySampleQueue.getLargestQueuedTimestampUs());
      break;
      if (this.mediaChunks.size() > 1) {
        localBaseMediaChunk = (BaseMediaChunk)this.mediaChunks.get(this.mediaChunks.size() - 2);
      } else {
        localBaseMediaChunk = null;
      }
    }
  }
  
  public T getChunkSource()
  {
    return this.chunkSource;
  }
  
  public long getNextLoadPositionUs()
  {
    long l;
    if (isPendingReset()) {
      l = this.pendingResetPositionUs;
    }
    for (;;)
    {
      return l;
      if (this.loadingFinished) {
        l = Long.MIN_VALUE;
      } else {
        l = getLastMediaChunk().endTimeUs;
      }
    }
  }
  
  boolean isPendingReset()
  {
    if (this.pendingResetPositionUs != -9223372036854775807L) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isReady()
  {
    if ((this.loadingFinished) || ((!isPendingReset()) && (this.primarySampleQueue.hasNextSample()))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void maybeThrowError()
    throws IOException
  {
    this.loader.maybeThrowError();
    if (!this.loader.isLoading()) {
      this.chunkSource.maybeThrowError();
    }
  }
  
  public void onLoadCanceled(Chunk paramChunk, long paramLong1, long paramLong2, boolean paramBoolean)
  {
    this.eventDispatcher.loadCanceled(paramChunk.dataSpec, paramChunk.type, this.primaryTrackType, paramChunk.trackFormat, paramChunk.trackSelectionReason, paramChunk.trackSelectionData, paramChunk.startTimeUs, paramChunk.endTimeUs, paramLong1, paramLong2, paramChunk.bytesLoaded());
    if (!paramBoolean)
    {
      this.primarySampleQueue.reset();
      paramChunk = this.embeddedSampleQueues;
      int i = paramChunk.length;
      for (int j = 0; j < i; j++) {
        paramChunk[j].reset();
      }
      this.callback.onContinueLoadingRequested(this);
    }
  }
  
  public void onLoadCompleted(Chunk paramChunk, long paramLong1, long paramLong2)
  {
    this.chunkSource.onChunkLoadCompleted(paramChunk);
    this.eventDispatcher.loadCompleted(paramChunk.dataSpec, paramChunk.type, this.primaryTrackType, paramChunk.trackFormat, paramChunk.trackSelectionReason, paramChunk.trackSelectionData, paramChunk.startTimeUs, paramChunk.endTimeUs, paramLong1, paramLong2, paramChunk.bytesLoaded());
    this.callback.onContinueLoadingRequested(this);
  }
  
  public int onLoadError(Chunk paramChunk, long paramLong1, long paramLong2, IOException paramIOException)
  {
    long l = paramChunk.bytesLoaded();
    boolean bool1 = isMediaChunk(paramChunk);
    int i = this.mediaChunks.size() - 1;
    boolean bool2;
    boolean bool4;
    if ((l == 0L) || (!bool1) || (!haveReadFromMediaChunk(i)))
    {
      bool2 = true;
      boolean bool3 = false;
      bool4 = bool3;
      if (this.chunkSource.onChunkLoadError(paramChunk, bool2, paramIOException))
      {
        if (bool2) {
          break label165;
        }
        Log.w("ChunkSampleStream", "Ignoring attempt to cancel non-cancelable load.");
        bool4 = bool3;
      }
      label90:
      this.eventDispatcher.loadError(paramChunk.dataSpec, paramChunk.type, this.primaryTrackType, paramChunk.trackFormat, paramChunk.trackSelectionReason, paramChunk.trackSelectionData, paramChunk.startTimeUs, paramChunk.endTimeUs, paramLong1, paramLong2, l, paramIOException, bool4);
      if (!bool4) {
        break label230;
      }
      this.callback.onContinueLoadingRequested(this);
    }
    label165:
    label230:
    for (i = 2;; i = 0)
    {
      return i;
      bool2 = false;
      break;
      bool2 = true;
      bool4 = bool2;
      if (!bool1) {
        break label90;
      }
      if (discardUpstreamMediaChunksFromIndex(i) == paramChunk) {}
      for (bool4 = true;; bool4 = false)
      {
        Assertions.checkState(bool4);
        bool4 = bool2;
        if (!this.mediaChunks.isEmpty()) {
          break;
        }
        this.pendingResetPositionUs = this.lastSeekPositionUs;
        bool4 = bool2;
        break;
      }
    }
  }
  
  public void onLoaderReleased()
  {
    this.primarySampleQueue.reset();
    SampleQueue[] arrayOfSampleQueue = this.embeddedSampleQueues;
    int i = arrayOfSampleQueue.length;
    for (int j = 0; j < i; j++) {
      arrayOfSampleQueue[j].reset();
    }
    if (this.releaseCallback != null) {
      this.releaseCallback.onSampleStreamReleased(this);
    }
  }
  
  public int readData(FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer, boolean paramBoolean)
  {
    int i;
    if (isPendingReset()) {
      i = -3;
    }
    for (;;)
    {
      return i;
      int j = this.primarySampleQueue.read(paramFormatHolder, paramDecoderInputBuffer, paramBoolean, this.loadingFinished, this.decodeOnlyUntilPositionUs);
      i = j;
      if (j == -4)
      {
        maybeNotifyPrimaryTrackFormatChanged(this.primarySampleQueue.getReadIndex(), 1);
        i = j;
      }
    }
  }
  
  public void reevaluateBuffer(long paramLong)
  {
    if ((this.loader.isLoading()) || (isPendingReset())) {}
    int i;
    int j;
    do
    {
      return;
      i = this.mediaChunks.size();
      j = this.chunkSource.getPreferredQueueSize(paramLong, this.readOnlyMediaChunks);
    } while (i <= j);
    int k = i;
    for (;;)
    {
      int m = k;
      if (j < i)
      {
        if (!haveReadFromMediaChunk(j)) {
          m = j;
        }
      }
      else
      {
        if (m == i) {
          break;
        }
        paramLong = getLastMediaChunk().endTimeUs;
        BaseMediaChunk localBaseMediaChunk = discardUpstreamMediaChunksFromIndex(m);
        if (this.mediaChunks.isEmpty()) {
          this.pendingResetPositionUs = this.lastSeekPositionUs;
        }
        this.loadingFinished = false;
        this.eventDispatcher.upstreamDiscarded(this.primaryTrackType, localBaseMediaChunk.startTimeUs, paramLong);
        break;
      }
      j++;
    }
  }
  
  public void release()
  {
    release(null);
  }
  
  public void release(ReleaseCallback<T> paramReleaseCallback)
  {
    this.releaseCallback = paramReleaseCallback;
    if (!this.loader.release(this))
    {
      this.primarySampleQueue.discardToEnd();
      paramReleaseCallback = this.embeddedSampleQueues;
      int i = paramReleaseCallback.length;
      for (int j = 0; j < i; j++) {
        paramReleaseCallback[j].discardToEnd();
      }
    }
  }
  
  public void seekToUs(long paramLong)
  {
    this.lastSeekPositionUs = paramLong;
    this.primarySampleQueue.rewind();
    boolean bool;
    Object localObject;
    if (isPendingReset())
    {
      bool = false;
      if (bool) {
        for (localObject : this.embeddedSampleQueues)
        {
          ((SampleQueue)localObject).rewind();
          ((SampleQueue)localObject).advanceTo(paramLong, true, false);
        }
      }
    }
    else
    {
      ??? = null;
      for (??? = 0;; ???++)
      {
        localObject = ???;
        long l;
        if (??? < this.mediaChunks.size())
        {
          localObject = (BaseMediaChunk)this.mediaChunks.get(???);
          l = ((BaseMediaChunk)localObject).startTimeUs;
          if (l != paramLong) {
            break label152;
          }
        }
        label152:
        do
        {
          if (localObject == null) {
            break label169;
          }
          bool = this.primarySampleQueue.setReadPosition(((BaseMediaChunk)localObject).getFirstSampleIndex(0));
          this.decodeOnlyUntilPositionUs = Long.MIN_VALUE;
          break;
          localObject = ???;
        } while (l > paramLong);
      }
      label169:
      localObject = this.primarySampleQueue;
      if (paramLong < getNextLoadPositionUs())
      {
        bool = true;
        label186:
        if (((SampleQueue)localObject).advanceTo(paramLong, true, bool) == -1) {
          break label216;
        }
      }
      label216:
      for (bool = true;; bool = false)
      {
        this.decodeOnlyUntilPositionUs = this.lastSeekPositionUs;
        break;
        bool = false;
        break label186;
      }
    }
    this.pendingResetPositionUs = paramLong;
    this.loadingFinished = false;
    this.mediaChunks.clear();
    if (this.loader.isLoading()) {
      this.loader.cancelLoading();
    }
    for (;;)
    {
      return;
      this.primarySampleQueue.reset();
      localObject = this.embeddedSampleQueues;
      ??? = localObject.length;
      for (??? = 0; ??? < ???; ???++) {
        localObject[???].reset();
      }
    }
  }
  
  public ChunkSampleStream<T>.EmbeddedSampleStream selectEmbeddedTrack(long paramLong, int paramInt)
  {
    for (int i = 0; i < this.embeddedSampleQueues.length; i++) {
      if (this.embeddedTrackTypes[i] == paramInt)
      {
        if (this.embeddedTracksSelected[i] == 0) {}
        for (boolean bool = true;; bool = false)
        {
          Assertions.checkState(bool);
          this.embeddedTracksSelected[i] = true;
          this.embeddedSampleQueues[i].rewind();
          this.embeddedSampleQueues[i].advanceTo(paramLong, true, true);
          return new EmbeddedSampleStream(this, this.embeddedSampleQueues[i], i);
        }
      }
    }
    throw new IllegalStateException();
  }
  
  public int skipData(long paramLong)
  {
    int i;
    if (isPendingReset())
    {
      i = 0;
      return i;
    }
    int j;
    if ((this.loadingFinished) && (paramLong > this.primarySampleQueue.getLargestQueuedTimestampUs())) {
      j = this.primarySampleQueue.advanceToEnd();
    }
    for (;;)
    {
      i = j;
      if (j <= 0) {
        break;
      }
      maybeNotifyPrimaryTrackFormatChanged(this.primarySampleQueue.getReadIndex(), j);
      i = j;
      break;
      i = this.primarySampleQueue.advanceTo(paramLong, true, true);
      j = i;
      if (i == -1) {
        j = 0;
      }
    }
  }
  
  public final class EmbeddedSampleStream
    implements SampleStream
  {
    private boolean formatNotificationSent;
    private final int index;
    public final ChunkSampleStream<T> parent;
    private final SampleQueue sampleQueue;
    
    public EmbeddedSampleStream(SampleQueue paramSampleQueue, int paramInt)
    {
      this.parent = paramSampleQueue;
      this.sampleQueue = paramInt;
      int i;
      this.index = i;
    }
    
    private void maybeNotifyTrackFormatChanged()
    {
      if (!this.formatNotificationSent)
      {
        ChunkSampleStream.this.eventDispatcher.downstreamFormatChanged(ChunkSampleStream.this.embeddedTrackTypes[this.index], ChunkSampleStream.this.embeddedTrackFormats[this.index], 0, null, ChunkSampleStream.this.lastSeekPositionUs);
        this.formatNotificationSent = true;
      }
    }
    
    public boolean isReady()
    {
      if ((ChunkSampleStream.this.loadingFinished) || ((!ChunkSampleStream.this.isPendingReset()) && (this.sampleQueue.hasNextSample()))) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public void maybeThrowError()
      throws IOException
    {}
    
    public int readData(FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer, boolean paramBoolean)
    {
      int i;
      if (ChunkSampleStream.this.isPendingReset()) {
        i = -3;
      }
      for (;;)
      {
        return i;
        int j = this.sampleQueue.read(paramFormatHolder, paramDecoderInputBuffer, paramBoolean, ChunkSampleStream.this.loadingFinished, ChunkSampleStream.this.decodeOnlyUntilPositionUs);
        i = j;
        if (j == -4)
        {
          maybeNotifyTrackFormatChanged();
          i = j;
        }
      }
    }
    
    public void release()
    {
      Assertions.checkState(ChunkSampleStream.this.embeddedTracksSelected[this.index]);
      ChunkSampleStream.this.embeddedTracksSelected[this.index] = 0;
    }
    
    public int skipData(long paramLong)
    {
      int i;
      if ((ChunkSampleStream.this.loadingFinished) && (paramLong > this.sampleQueue.getLargestQueuedTimestampUs())) {
        i = this.sampleQueue.advanceToEnd();
      }
      for (;;)
      {
        if (i > 0) {
          maybeNotifyTrackFormatChanged();
        }
        return i;
        int j = this.sampleQueue.advanceTo(paramLong, true, true);
        i = j;
        if (j == -1) {
          i = 0;
        }
      }
    }
  }
  
  public static abstract interface ReleaseCallback<T extends ChunkSource>
  {
    public abstract void onSampleStreamReleased(ChunkSampleStream<T> paramChunkSampleStream);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/chunk/ChunkSampleStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */