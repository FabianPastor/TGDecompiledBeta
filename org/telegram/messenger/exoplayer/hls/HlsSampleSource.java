package org.telegram.messenger.exoplayer.hls;

import android.os.Handler;
import android.os.SystemClock;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import org.telegram.messenger.exoplayer.LoadControl;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.MediaFormatHolder;
import org.telegram.messenger.exoplayer.SampleHolder;
import org.telegram.messenger.exoplayer.SampleSource;
import org.telegram.messenger.exoplayer.SampleSource.SampleSourceReader;
import org.telegram.messenger.exoplayer.chunk.BaseChunkSampleSourceEventListener;
import org.telegram.messenger.exoplayer.chunk.Chunk;
import org.telegram.messenger.exoplayer.chunk.ChunkOperationHolder;
import org.telegram.messenger.exoplayer.chunk.Format;
import org.telegram.messenger.exoplayer.upstream.DataSpec;
import org.telegram.messenger.exoplayer.upstream.Loader;
import org.telegram.messenger.exoplayer.upstream.Loader.Callback;
import org.telegram.messenger.exoplayer.upstream.Loader.Loadable;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.MimeTypes;

public final class HlsSampleSource
  implements SampleSource, SampleSource.SampleSourceReader, Loader.Callback
{
  public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT = 3;
  private static final long NO_RESET_PENDING = Long.MIN_VALUE;
  private static final int PRIMARY_TYPE_AUDIO = 2;
  private static final int PRIMARY_TYPE_NONE = 0;
  private static final int PRIMARY_TYPE_TEXT = 1;
  private static final int PRIMARY_TYPE_VIDEO = 3;
  private final int bufferSizeContribution;
  private final ChunkOperationHolder chunkOperationHolder;
  private final HlsChunkSource chunkSource;
  private int[] chunkSourceTrackIndices;
  private long currentLoadStartTimeMs;
  private Chunk currentLoadable;
  private IOException currentLoadableException;
  private int currentLoadableExceptionCount;
  private long currentLoadableExceptionTimestamp;
  private TsChunk currentTsLoadable;
  private Format downstreamFormat;
  private MediaFormat[] downstreamMediaFormats;
  private long downstreamPositionUs;
  private int enabledTrackCount;
  private final Handler eventHandler;
  private final EventListener eventListener;
  private final int eventSourceId;
  private boolean[] extractorTrackEnabledStates;
  private int[] extractorTrackIndices;
  private final LinkedList<HlsExtractorWrapper> extractors;
  private long lastSeekPositionUs;
  private final LoadControl loadControl;
  private boolean loadControlRegistered;
  private Loader loader;
  private boolean loadingFinished;
  private final int minLoadableRetryCount;
  private boolean[] pendingDiscontinuities;
  private long pendingResetPositionUs;
  private boolean prepared;
  private TsChunk previousTsLoadable;
  private int remainingReleaseCount;
  private int trackCount;
  private boolean[] trackEnabledStates;
  private MediaFormat[] trackFormats;
  
  public HlsSampleSource(HlsChunkSource paramHlsChunkSource, LoadControl paramLoadControl, int paramInt)
  {
    this(paramHlsChunkSource, paramLoadControl, paramInt, null, null, 0);
  }
  
  public HlsSampleSource(HlsChunkSource paramHlsChunkSource, LoadControl paramLoadControl, int paramInt1, Handler paramHandler, EventListener paramEventListener, int paramInt2)
  {
    this(paramHlsChunkSource, paramLoadControl, paramInt1, paramHandler, paramEventListener, paramInt2, 3);
  }
  
  public HlsSampleSource(HlsChunkSource paramHlsChunkSource, LoadControl paramLoadControl, int paramInt1, Handler paramHandler, EventListener paramEventListener, int paramInt2, int paramInt3)
  {
    this.chunkSource = paramHlsChunkSource;
    this.loadControl = paramLoadControl;
    this.bufferSizeContribution = paramInt1;
    this.minLoadableRetryCount = paramInt3;
    this.eventHandler = paramHandler;
    this.eventListener = paramEventListener;
    this.eventSourceId = paramInt2;
    this.pendingResetPositionUs = Long.MIN_VALUE;
    this.extractors = new LinkedList();
    this.chunkOperationHolder = new ChunkOperationHolder();
  }
  
  private void buildTracks(HlsExtractorWrapper paramHlsExtractorWrapper)
  {
    int n = 0;
    int m = -1;
    int i2 = paramHlsExtractorWrapper.getTrackCount();
    int j = 0;
    String str;
    int i;
    label40:
    int k;
    if (j < i2)
    {
      str = paramHlsExtractorWrapper.getMediaFormat(j).mimeType;
      int i1;
      if (MimeTypes.isVideo(str))
      {
        i = 3;
        if (i <= n) {
          break label98;
        }
        k = j;
        i1 = i;
      }
      for (;;)
      {
        j += 1;
        m = k;
        n = i1;
        break;
        if (MimeTypes.isAudio(str))
        {
          i = 2;
          break label40;
        }
        if (MimeTypes.isText(str))
        {
          i = 1;
          break label40;
        }
        i = 0;
        break label40;
        label98:
        k = m;
        i1 = n;
        if (i == n)
        {
          k = m;
          i1 = n;
          if (m != -1)
          {
            k = -1;
            i1 = n;
          }
        }
      }
    }
    n = this.chunkSource.getTrackCount();
    MediaFormat localMediaFormat;
    label300:
    label309:
    MediaFormat[] arrayOfMediaFormat;
    if (m != -1)
    {
      i = 1;
      this.trackCount = i2;
      if (i != 0) {
        this.trackCount += n - 1;
      }
      this.trackFormats = new MediaFormat[this.trackCount];
      this.trackEnabledStates = new boolean[this.trackCount];
      this.pendingDiscontinuities = new boolean[this.trackCount];
      this.downstreamMediaFormats = new MediaFormat[this.trackCount];
      this.chunkSourceTrackIndices = new int[this.trackCount];
      this.extractorTrackIndices = new int[this.trackCount];
      this.extractorTrackEnabledStates = new boolean[i2];
      long l = this.chunkSource.getDurationUs();
      j = 0;
      i = 0;
      if (j >= i2) {
        return;
      }
      localMediaFormat = paramHlsExtractorWrapper.getMediaFormat(j).copyWithDurationUs(l);
      str = null;
      if (!MimeTypes.isAudio(localMediaFormat.mimeType)) {
        break label385;
      }
      str = this.chunkSource.getMuxedAudioLanguage();
      if (j != m) {
        break label434;
      }
      k = 0;
      if (k >= n) {
        break label427;
      }
      this.extractorTrackIndices[i] = j;
      this.chunkSourceTrackIndices[i] = k;
      localObject = this.chunkSource.getFixedTrackVariant(k);
      arrayOfMediaFormat = this.trackFormats;
      if (localObject != null) {
        break label410;
      }
    }
    label385:
    label410:
    for (Object localObject = localMediaFormat.copyAsAdaptive(null);; localObject = copyWithFixedTrackInfo(localMediaFormat, ((Variant)localObject).format, str))
    {
      arrayOfMediaFormat[i] = localObject;
      k += 1;
      i += 1;
      break label309;
      i = 0;
      break;
      if (!"application/eia-608".equals(localMediaFormat.mimeType)) {
        break label300;
      }
      str = this.chunkSource.getMuxedCaptionLanguage();
      break label300;
    }
    for (;;)
    {
      label427:
      j += 1;
      break;
      label434:
      this.extractorTrackIndices[i] = j;
      this.chunkSourceTrackIndices[i] = -1;
      localObject = this.trackFormats;
      k = i + 1;
      localObject[i] = localMediaFormat.copyWithLanguage(str);
      i = k;
    }
  }
  
  private void clearCurrentLoadable()
  {
    this.currentTsLoadable = null;
    this.currentLoadable = null;
    this.currentLoadableException = null;
    this.currentLoadableExceptionCount = 0;
  }
  
  private void clearState()
  {
    int i = 0;
    while (i < this.extractors.size())
    {
      ((HlsExtractorWrapper)this.extractors.get(i)).clear();
      i += 1;
    }
    this.extractors.clear();
    clearCurrentLoadable();
    this.previousTsLoadable = null;
  }
  
  private static MediaFormat copyWithFixedTrackInfo(MediaFormat paramMediaFormat, Format paramFormat, String paramString)
  {
    int i;
    int j;
    if (paramFormat.width == -1)
    {
      i = -1;
      if (paramFormat.height != -1) {
        break label55;
      }
      j = -1;
      label21:
      if (paramString != null) {
        break label64;
      }
      paramString = paramFormat.language;
    }
    label55:
    label64:
    for (;;)
    {
      return paramMediaFormat.copyWithFixedTrackInfo(paramFormat.id, paramFormat.bitrate, i, j, paramString);
      i = paramFormat.width;
      break;
      j = paramFormat.height;
      break label21;
    }
  }
  
  private void discardSamplesForDisabledTracks(HlsExtractorWrapper paramHlsExtractorWrapper, long paramLong)
  {
    if (!paramHlsExtractorWrapper.isPrepared()) {}
    for (;;)
    {
      return;
      int i = 0;
      while (i < this.extractorTrackEnabledStates.length)
      {
        if (this.extractorTrackEnabledStates[i] == 0) {
          paramHlsExtractorWrapper.discardUntil(i, paramLong);
        }
        i += 1;
      }
    }
  }
  
  private HlsExtractorWrapper getCurrentExtractor()
  {
    for (HlsExtractorWrapper localHlsExtractorWrapper = (HlsExtractorWrapper)this.extractors.getFirst(); (this.extractors.size() > 1) && (!haveSamplesForEnabledTracks(localHlsExtractorWrapper)); localHlsExtractorWrapper = (HlsExtractorWrapper)this.extractors.getFirst()) {
      ((HlsExtractorWrapper)this.extractors.removeFirst()).clear();
    }
    return localHlsExtractorWrapper;
  }
  
  private long getNextLoadPositionUs()
  {
    if (isPendingReset()) {
      return this.pendingResetPositionUs;
    }
    if ((this.loadingFinished) || ((this.prepared) && (this.enabledTrackCount == 0))) {
      return -1L;
    }
    if (this.currentTsLoadable != null) {
      return this.currentTsLoadable.endTimeUs;
    }
    return this.previousTsLoadable.endTimeUs;
  }
  
  private long getRetryDelayMillis(long paramLong)
  {
    return Math.min((paramLong - 1L) * 1000L, 5000L);
  }
  
  private boolean haveSamplesForEnabledTracks(HlsExtractorWrapper paramHlsExtractorWrapper)
  {
    if (!paramHlsExtractorWrapper.isPrepared()) {}
    for (;;)
    {
      return false;
      int i = 0;
      while (i < this.extractorTrackEnabledStates.length)
      {
        if ((this.extractorTrackEnabledStates[i] != 0) && (paramHlsExtractorWrapper.hasSamples(i))) {
          return true;
        }
        i += 1;
      }
    }
  }
  
  private boolean isPendingReset()
  {
    return this.pendingResetPositionUs != Long.MIN_VALUE;
  }
  
  private boolean isTsChunk(Chunk paramChunk)
  {
    return paramChunk instanceof TsChunk;
  }
  
  private void maybeStartLoading()
  {
    long l2 = SystemClock.elapsedRealtime();
    long l1 = getNextLoadPositionUs();
    int i;
    boolean bool;
    if (this.currentLoadableException != null)
    {
      i = 1;
      if ((!this.loader.isLoading()) && (i == 0)) {
        break label102;
      }
      bool = true;
      label36:
      bool = this.loadControl.update(this, this.downstreamPositionUs, l1, bool);
      if (i == 0) {
        break label108;
      }
      if (l2 - this.currentLoadableExceptionTimestamp >= getRetryDelayMillis(this.currentLoadableExceptionCount))
      {
        this.currentLoadableException = null;
        this.loader.startLoading(this.currentLoadable, this);
      }
    }
    label102:
    label108:
    Object localObject1;
    Object localObject2;
    do
    {
      do
      {
        return;
        i = 0;
        break;
        bool = false;
        break label36;
      } while ((this.loader.isLoading()) || (!bool) || ((this.prepared) && (this.enabledTrackCount == 0)));
      localObject1 = this.chunkSource;
      localObject2 = this.previousTsLoadable;
      if (this.pendingResetPositionUs != Long.MIN_VALUE) {}
      for (l1 = this.pendingResetPositionUs;; l1 = this.downstreamPositionUs)
      {
        ((HlsChunkSource)localObject1).getChunkOperation((TsChunk)localObject2, l1, this.chunkOperationHolder);
        bool = this.chunkOperationHolder.endOfStream;
        localObject1 = this.chunkOperationHolder.chunk;
        this.chunkOperationHolder.clear();
        if (!bool) {
          break;
        }
        this.loadingFinished = true;
        this.loadControl.update(this, this.downstreamPositionUs, -1L, false);
        return;
      }
    } while (localObject1 == null);
    this.currentLoadStartTimeMs = l2;
    this.currentLoadable = ((Chunk)localObject1);
    if (isTsChunk(this.currentLoadable))
    {
      localObject1 = (TsChunk)this.currentLoadable;
      if (isPendingReset()) {
        this.pendingResetPositionUs = Long.MIN_VALUE;
      }
      localObject2 = ((TsChunk)localObject1).extractorWrapper;
      if ((this.extractors.isEmpty()) || (this.extractors.getLast() != localObject2))
      {
        ((HlsExtractorWrapper)localObject2).init(this.loadControl.getAllocator());
        this.extractors.addLast(localObject2);
      }
      notifyLoadStarted(((TsChunk)localObject1).dataSpec.length, ((TsChunk)localObject1).type, ((TsChunk)localObject1).trigger, ((TsChunk)localObject1).format, ((TsChunk)localObject1).startTimeUs, ((TsChunk)localObject1).endTimeUs);
      this.currentTsLoadable = ((TsChunk)localObject1);
    }
    for (;;)
    {
      this.loader.startLoading(this.currentLoadable, this);
      return;
      notifyLoadStarted(this.currentLoadable.dataSpec.length, this.currentLoadable.type, this.currentLoadable.trigger, this.currentLoadable.format, -1L, -1L);
    }
  }
  
  private void notifyDownstreamFormatChanged(final Format paramFormat, final int paramInt, final long paramLong)
  {
    if ((this.eventHandler != null) && (this.eventListener != null)) {
      this.eventHandler.post(new Runnable()
      {
        public void run()
        {
          HlsSampleSource.this.eventListener.onDownstreamFormatChanged(HlsSampleSource.this.eventSourceId, paramFormat, paramInt, HlsSampleSource.this.usToMs(paramLong));
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
          HlsSampleSource.this.eventListener.onLoadCanceled(HlsSampleSource.this.eventSourceId, paramLong);
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
          HlsSampleSource.this.eventListener.onLoadCompleted(HlsSampleSource.this.eventSourceId, paramLong1, paramInt2, paramFormat, paramLong2, HlsSampleSource.this.usToMs(paramLong3), HlsSampleSource.this.usToMs(paramLong5), this.val$elapsedRealtimeMs, this.val$loadDurationMs);
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
          HlsSampleSource.this.eventListener.onLoadError(HlsSampleSource.this.eventSourceId, paramIOException);
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
          HlsSampleSource.this.eventListener.onLoadStarted(HlsSampleSource.this.eventSourceId, paramLong1, paramInt2, paramFormat, paramLong2, HlsSampleSource.this.usToMs(paramLong3), HlsSampleSource.this.usToMs(this.val$mediaEndTimeUs));
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
    clearState();
    maybeStartLoading();
  }
  
  private void seekToInternal(long paramLong)
  {
    this.lastSeekPositionUs = paramLong;
    this.downstreamPositionUs = paramLong;
    Arrays.fill(this.pendingDiscontinuities, true);
    this.chunkSource.seek();
    restartFrom(paramLong);
  }
  
  private void setTrackEnabledState(int paramInt, boolean paramBoolean)
  {
    boolean bool2 = false;
    int i = 1;
    boolean bool1;
    int j;
    if (this.trackEnabledStates[paramInt] != paramBoolean)
    {
      bool1 = true;
      Assertions.checkState(bool1);
      j = this.extractorTrackIndices[paramInt];
      bool1 = bool2;
      if (this.extractorTrackEnabledStates[j] != paramBoolean) {
        bool1 = true;
      }
      Assertions.checkState(bool1);
      this.trackEnabledStates[paramInt] = paramBoolean;
      this.extractorTrackEnabledStates[j] = paramBoolean;
      j = this.enabledTrackCount;
      if (!paramBoolean) {
        break label96;
      }
    }
    label96:
    for (paramInt = i;; paramInt = -1)
    {
      this.enabledTrackCount = (j + paramInt);
      return;
      bool1 = false;
      break;
    }
  }
  
  public boolean continueBuffering(int paramInt, long paramLong)
  {
    Assertions.checkState(this.prepared);
    Assertions.checkState(this.trackEnabledStates[paramInt]);
    this.downstreamPositionUs = paramLong;
    if (!this.extractors.isEmpty()) {
      discardSamplesForDisabledTracks(getCurrentExtractor(), this.downstreamPositionUs);
    }
    maybeStartLoading();
    if (this.loadingFinished) {
      return true;
    }
    if ((isPendingReset()) || (this.extractors.isEmpty())) {
      return false;
    }
    int i = 0;
    for (;;)
    {
      HlsExtractorWrapper localHlsExtractorWrapper;
      if (i < this.extractors.size())
      {
        localHlsExtractorWrapper = (HlsExtractorWrapper)this.extractors.get(i);
        if (localHlsExtractorWrapper.isPrepared()) {}
      }
      else
      {
        return false;
      }
      if (localHlsExtractorWrapper.hasSamples(this.extractorTrackIndices[paramInt])) {
        break;
      }
      i += 1;
    }
  }
  
  public void disable(int paramInt)
  {
    Assertions.checkState(this.prepared);
    setTrackEnabledState(paramInt, false);
    if (this.enabledTrackCount == 0)
    {
      this.chunkSource.reset();
      this.downstreamPositionUs = Long.MIN_VALUE;
      if (this.loadControlRegistered)
      {
        this.loadControl.unregister(this);
        this.loadControlRegistered = false;
      }
      if (this.loader.isLoading()) {
        this.loader.cancelLoading();
      }
    }
    else
    {
      return;
    }
    clearState();
    this.loadControl.trimAllocator();
  }
  
  public void enable(int paramInt, long paramLong)
  {
    Assertions.checkState(this.prepared);
    setTrackEnabledState(paramInt, true);
    this.downstreamMediaFormats[paramInt] = null;
    this.pendingDiscontinuities[paramInt] = false;
    this.downstreamFormat = null;
    boolean bool = this.loadControlRegistered;
    if (!this.loadControlRegistered)
    {
      this.loadControl.register(this, this.bufferSizeContribution);
      this.loadControlRegistered = true;
    }
    if (this.chunkSource.isLive()) {
      paramLong = 0L;
    }
    paramInt = this.chunkSourceTrackIndices[paramInt];
    if ((paramInt != -1) && (paramInt != this.chunkSource.getSelectedTrackIndex()))
    {
      this.chunkSource.selectTrack(paramInt);
      seekToInternal(paramLong);
    }
    while (this.enabledTrackCount != 1) {
      return;
    }
    this.lastSeekPositionUs = paramLong;
    if ((bool) && (this.downstreamPositionUs == paramLong))
    {
      maybeStartLoading();
      return;
    }
    this.downstreamPositionUs = paramLong;
    restartFrom(paramLong);
  }
  
  public long getBufferedPositionUs()
  {
    Assertions.checkState(this.prepared);
    boolean bool;
    long l2;
    if (this.enabledTrackCount > 0)
    {
      bool = true;
      Assertions.checkState(bool);
      if (!isPendingReset()) {
        break label41;
      }
      l2 = this.pendingResetPositionUs;
    }
    label41:
    long l1;
    do
    {
      return l2;
      bool = false;
      break;
      if (this.loadingFinished) {
        return -3L;
      }
      l2 = ((HlsExtractorWrapper)this.extractors.getLast()).getLargestParsedTimestampUs();
      l1 = l2;
      if (this.extractors.size() > 1) {
        l1 = Math.max(l2, ((HlsExtractorWrapper)this.extractors.get(this.extractors.size() - 2)).getLargestParsedTimestampUs());
      }
      l2 = l1;
    } while (l1 != Long.MIN_VALUE);
    return this.downstreamPositionUs;
  }
  
  public MediaFormat getFormat(int paramInt)
  {
    Assertions.checkState(this.prepared);
    return this.trackFormats[paramInt];
  }
  
  public int getTrackCount()
  {
    Assertions.checkState(this.prepared);
    return this.trackCount;
  }
  
  public void maybeThrowError()
    throws IOException
  {
    if ((this.currentLoadableException != null) && (this.currentLoadableExceptionCount > this.minLoadableRetryCount)) {
      throw this.currentLoadableException;
    }
    if (this.currentLoadable == null) {
      this.chunkSource.maybeThrowError();
    }
  }
  
  public void onLoadCanceled(Loader.Loadable paramLoadable)
  {
    notifyLoadCanceled(this.currentLoadable.bytesLoaded());
    if (this.enabledTrackCount > 0)
    {
      restartFrom(this.pendingResetPositionUs);
      return;
    }
    clearState();
    this.loadControl.trimAllocator();
  }
  
  public void onLoadCompleted(Loader.Loadable paramLoadable)
  {
    boolean bool2 = true;
    boolean bool1;
    long l1;
    long l2;
    if (paramLoadable == this.currentLoadable)
    {
      bool1 = true;
      Assertions.checkState(bool1);
      l1 = SystemClock.elapsedRealtime();
      l2 = l1 - this.currentLoadStartTimeMs;
      this.chunkSource.onChunkLoadCompleted(this.currentLoadable);
      if (!isTsChunk(this.currentLoadable)) {
        break label146;
      }
      if (this.currentLoadable != this.currentTsLoadable) {
        break label141;
      }
      bool1 = bool2;
      label65:
      Assertions.checkState(bool1);
      this.previousTsLoadable = this.currentTsLoadable;
      notifyLoadCompleted(this.currentLoadable.bytesLoaded(), this.currentTsLoadable.type, this.currentTsLoadable.trigger, this.currentTsLoadable.format, this.currentTsLoadable.startTimeUs, this.currentTsLoadable.endTimeUs, l1, l2);
    }
    for (;;)
    {
      clearCurrentLoadable();
      maybeStartLoading();
      return;
      bool1 = false;
      break;
      label141:
      bool1 = false;
      break label65;
      label146:
      notifyLoadCompleted(this.currentLoadable.bytesLoaded(), this.currentLoadable.type, this.currentLoadable.trigger, this.currentLoadable.format, -1L, -1L, l1, l2);
    }
  }
  
  public void onLoadError(Loader.Loadable paramLoadable, IOException paramIOException)
  {
    if (this.chunkSource.onChunkLoadError(this.currentLoadable, paramIOException))
    {
      if ((this.previousTsLoadable == null) && (!isPendingReset())) {
        this.pendingResetPositionUs = this.lastSeekPositionUs;
      }
      clearCurrentLoadable();
    }
    for (;;)
    {
      notifyLoadError(paramIOException);
      maybeStartLoading();
      return;
      this.currentLoadableException = paramIOException;
      this.currentLoadableExceptionCount += 1;
      this.currentLoadableExceptionTimestamp = SystemClock.elapsedRealtime();
    }
  }
  
  public boolean prepare(long paramLong)
  {
    if (this.prepared) {
      return true;
    }
    if (!this.chunkSource.prepare()) {
      return false;
    }
    if (!this.extractors.isEmpty()) {
      for (;;)
      {
        HlsExtractorWrapper localHlsExtractorWrapper = (HlsExtractorWrapper)this.extractors.getFirst();
        if (localHlsExtractorWrapper.isPrepared())
        {
          buildTracks(localHlsExtractorWrapper);
          this.prepared = true;
          maybeStartLoading();
          return true;
        }
        if (this.extractors.size() <= 1) {
          break;
        }
        ((HlsExtractorWrapper)this.extractors.removeFirst()).clear();
      }
    }
    if (this.loader == null)
    {
      this.loader = new Loader("Loader:HLS");
      this.loadControl.register(this, this.bufferSizeContribution);
      this.loadControlRegistered = true;
    }
    if (!this.loader.isLoading())
    {
      this.pendingResetPositionUs = paramLong;
      this.downstreamPositionUs = paramLong;
    }
    maybeStartLoading();
    return false;
  }
  
  public int readData(int paramInt, long paramLong, MediaFormatHolder paramMediaFormatHolder, SampleHolder paramSampleHolder)
  {
    Assertions.checkState(this.prepared);
    this.downstreamPositionUs = paramLong;
    if ((this.pendingDiscontinuities[paramInt] != 0) || (isPendingReset())) {
      return -2;
    }
    Object localObject1 = getCurrentExtractor();
    if (!((HlsExtractorWrapper)localObject1).isPrepared()) {
      return -2;
    }
    Object localObject2 = ((HlsExtractorWrapper)localObject1).format;
    if (!((Format)localObject2).equals(this.downstreamFormat)) {
      notifyDownstreamFormatChanged((Format)localObject2, ((HlsExtractorWrapper)localObject1).trigger, ((HlsExtractorWrapper)localObject1).startTimeUs);
    }
    this.downstreamFormat = ((Format)localObject2);
    if (this.extractors.size() > 1) {
      ((HlsExtractorWrapper)localObject1).configureSpliceTo((HlsExtractorWrapper)this.extractors.get(1));
    }
    int j = this.extractorTrackIndices[paramInt];
    int i = 0;
    while ((this.extractors.size() > i + 1) && (!((HlsExtractorWrapper)localObject1).hasSamples(j)))
    {
      localObject1 = this.extractors;
      i += 1;
      localObject2 = (HlsExtractorWrapper)((LinkedList)localObject1).get(i);
      localObject1 = localObject2;
      if (!((HlsExtractorWrapper)localObject2).isPrepared()) {
        return -2;
      }
    }
    localObject2 = ((HlsExtractorWrapper)localObject1).getMediaFormat(j);
    if (localObject2 != null)
    {
      if (!((MediaFormat)localObject2).equals(this.downstreamMediaFormats[paramInt]))
      {
        paramMediaFormatHolder.format = ((MediaFormat)localObject2);
        this.downstreamMediaFormats[paramInt] = localObject2;
        return -4;
      }
      this.downstreamMediaFormats[paramInt] = localObject2;
    }
    if (((HlsExtractorWrapper)localObject1).getSample(j, paramSampleHolder))
    {
      if (paramSampleHolder.timeUs < this.lastSeekPositionUs)
      {
        paramInt = 1;
        i = paramSampleHolder.flags;
        if (paramInt == 0) {
          break label303;
        }
      }
      label303:
      for (paramInt = 134217728;; paramInt = 0)
      {
        paramSampleHolder.flags = (paramInt | i);
        return -3;
        paramInt = 0;
        break;
      }
    }
    if (this.loadingFinished) {
      return -1;
    }
    return -2;
  }
  
  public long readDiscontinuity(int paramInt)
  {
    if (this.pendingDiscontinuities[paramInt] != 0)
    {
      this.pendingDiscontinuities[paramInt] = false;
      return this.lastSeekPositionUs;
    }
    return Long.MIN_VALUE;
  }
  
  public SampleSource.SampleSourceReader register()
  {
    this.remainingReleaseCount += 1;
    return this;
  }
  
  public void release()
  {
    if (this.remainingReleaseCount > 0) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      int i = this.remainingReleaseCount - 1;
      this.remainingReleaseCount = i;
      if ((i == 0) && (this.loader != null))
      {
        if (this.loadControlRegistered)
        {
          this.loadControl.unregister(this);
          this.loadControlRegistered = false;
        }
        this.loader.release();
        this.loader = null;
      }
      return;
    }
  }
  
  public void seekToUs(long paramLong)
  {
    Assertions.checkState(this.prepared);
    boolean bool;
    if (this.enabledTrackCount > 0)
    {
      bool = true;
      Assertions.checkState(bool);
      if (this.chunkSource.isLive()) {
        paramLong = 0L;
      }
      if (!isPendingReset()) {
        break label69;
      }
    }
    label69:
    for (long l = this.pendingResetPositionUs;; l = this.downstreamPositionUs)
    {
      this.downstreamPositionUs = paramLong;
      this.lastSeekPositionUs = paramLong;
      if (l != paramLong) {
        break label77;
      }
      return;
      bool = false;
      break;
    }
    label77:
    seekToInternal(paramLong);
  }
  
  long usToMs(long paramLong)
  {
    return paramLong / 1000L;
  }
  
  public static abstract interface EventListener
    extends BaseChunkSampleSourceEventListener
  {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/hls/HlsSampleSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */