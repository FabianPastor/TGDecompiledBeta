package org.telegram.messenger.exoplayer2.source;

import android.net.Uri;
import android.os.Handler;
import java.io.IOException;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.FormatHolder;
import org.telegram.messenger.exoplayer2.SeekParameters;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.extractor.DefaultExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.PositionHolder;
import org.telegram.messenger.exoplayer2.extractor.SeekMap;
import org.telegram.messenger.exoplayer2.extractor.SeekMap.SeekPoints;
import org.telegram.messenger.exoplayer2.extractor.SeekPoint;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.Loader;
import org.telegram.messenger.exoplayer2.upstream.Loader.Callback;
import org.telegram.messenger.exoplayer2.upstream.Loader.Loadable;
import org.telegram.messenger.exoplayer2.upstream.Loader.ReleaseCallback;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.ConditionVariable;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.Util;

final class ExtractorMediaPeriod
  implements ExtractorOutput, MediaPeriod, SampleQueue.UpstreamFormatChangedListener, Loader.Callback<ExtractingLoadable>, Loader.ReleaseCallback
{
  private static final long DEFAULT_LAST_SAMPLE_DURATION_US = 10000L;
  private int actualMinLoadableRetryCount;
  private final Allocator allocator;
  private MediaPeriod.Callback callback;
  private final long continueLoadingCheckIntervalBytes;
  private final String customCacheKey;
  private final DataSource dataSource;
  private long durationUs;
  private int enabledTrackCount;
  private final MediaSourceEventListener.EventDispatcher eventDispatcher;
  private int extractedSamplesCountAtStartOfLoad;
  private final ExtractorHolder extractorHolder;
  private final Handler handler;
  private boolean haveAudioVideoTracks;
  private long lastSeekPositionUs;
  private long length;
  private final Listener listener;
  private final ConditionVariable loadCondition;
  private final Loader loader;
  private boolean loadingFinished;
  private final Runnable maybeFinishPrepareRunnable;
  private final int minLoadableRetryCount;
  private boolean notifyDiscontinuity;
  private final Runnable onContinueLoadingRequestedRunnable;
  private boolean pendingDeferredRetry;
  private long pendingResetPositionUs;
  private boolean prepared;
  private boolean released;
  private int[] sampleQueueTrackIds;
  private SampleQueue[] sampleQueues;
  private boolean sampleQueuesBuilt;
  private SeekMap seekMap;
  private boolean seenFirstTrackSelection;
  private boolean[] trackEnabledStates;
  private boolean[] trackFormatNotificationSent;
  private boolean[] trackIsAudioVideoFlags;
  private TrackGroupArray tracks;
  private final Uri uri;
  
  public ExtractorMediaPeriod(Uri paramUri, DataSource paramDataSource, Extractor[] paramArrayOfExtractor, int paramInt1, MediaSourceEventListener.EventDispatcher paramEventDispatcher, Listener paramListener, Allocator paramAllocator, String paramString, int paramInt2)
  {
    this.uri = paramUri;
    this.dataSource = paramDataSource;
    this.minLoadableRetryCount = paramInt1;
    this.eventDispatcher = paramEventDispatcher;
    this.listener = paramListener;
    this.allocator = paramAllocator;
    this.customCacheKey = paramString;
    this.continueLoadingCheckIntervalBytes = paramInt2;
    this.loader = new Loader("Loader:ExtractorMediaPeriod");
    this.extractorHolder = new ExtractorHolder(paramArrayOfExtractor, this);
    this.loadCondition = new ConditionVariable();
    this.maybeFinishPrepareRunnable = new Runnable()
    {
      public void run()
      {
        ExtractorMediaPeriod.this.maybeFinishPrepare();
      }
    };
    this.onContinueLoadingRequestedRunnable = new Runnable()
    {
      public void run()
      {
        if (!ExtractorMediaPeriod.this.released) {
          ExtractorMediaPeriod.this.callback.onContinueLoadingRequested(ExtractorMediaPeriod.this);
        }
      }
    };
    this.handler = new Handler();
    this.sampleQueueTrackIds = new int[0];
    this.sampleQueues = new SampleQueue[0];
    this.pendingResetPositionUs = -9223372036854775807L;
    this.length = -1L;
    this.durationUs = -9223372036854775807L;
    paramInt2 = paramInt1;
    if (paramInt1 == -1) {
      paramInt2 = 3;
    }
    this.actualMinLoadableRetryCount = paramInt2;
  }
  
  private boolean configureRetry(ExtractingLoadable paramExtractingLoadable, int paramInt)
  {
    int i = 0;
    boolean bool = false;
    if ((this.length != -1L) || ((this.seekMap != null) && (this.seekMap.getDurationUs() != -9223372036854775807L)))
    {
      this.extractedSamplesCountAtStartOfLoad = paramInt;
      bool = true;
    }
    for (;;)
    {
      return bool;
      if ((this.prepared) && (!suppressRead()))
      {
        this.pendingDeferredRetry = true;
      }
      else
      {
        this.notifyDiscontinuity = this.prepared;
        this.lastSeekPositionUs = 0L;
        this.extractedSamplesCountAtStartOfLoad = 0;
        SampleQueue[] arrayOfSampleQueue = this.sampleQueues;
        int j = arrayOfSampleQueue.length;
        for (paramInt = i; paramInt < j; paramInt++) {
          arrayOfSampleQueue[paramInt].reset();
        }
        paramExtractingLoadable.setLoadPosition(0L, 0L);
        bool = true;
      }
    }
  }
  
  private void copyLengthFromLoader(ExtractingLoadable paramExtractingLoadable)
  {
    if (this.length == -1L) {
      this.length = paramExtractingLoadable.length;
    }
  }
  
  private int getExtractedSamplesCount()
  {
    int i = 0;
    SampleQueue[] arrayOfSampleQueue = this.sampleQueues;
    int j = arrayOfSampleQueue.length;
    for (int k = 0; k < j; k++) {
      i += arrayOfSampleQueue[k].getWriteIndex();
    }
    return i;
  }
  
  private long getLargestQueuedTimestampUs()
  {
    long l = Long.MIN_VALUE;
    SampleQueue[] arrayOfSampleQueue = this.sampleQueues;
    int i = arrayOfSampleQueue.length;
    for (int j = 0; j < i; j++) {
      l = Math.max(l, arrayOfSampleQueue[j].getLargestQueuedTimestampUs());
    }
    return l;
  }
  
  private static boolean isLoadableExceptionFatal(IOException paramIOException)
  {
    return paramIOException instanceof UnrecognizedInputFormatException;
  }
  
  private boolean isPendingReset()
  {
    if (this.pendingResetPositionUs != -9223372036854775807L) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private void maybeFinishPrepare()
  {
    if ((this.released) || (this.prepared) || (this.seekMap == null) || (!this.sampleQueuesBuilt)) {}
    for (;;)
    {
      return;
      Object localObject1 = this.sampleQueues;
      int i = localObject1.length;
      for (int j = 0;; j++)
      {
        if (j >= i) {
          break label59;
        }
        if (localObject1[j].getUpstreamFormat() == null) {
          break;
        }
      }
      label59:
      this.loadCondition.close();
      i = this.sampleQueues.length;
      localObject1 = new TrackGroup[i];
      this.trackIsAudioVideoFlags = new boolean[i];
      this.trackEnabledStates = new boolean[i];
      this.trackFormatNotificationSent = new boolean[i];
      this.durationUs = this.seekMap.getDurationUs();
      j = 0;
      if (j < i)
      {
        Object localObject2 = this.sampleQueues[j].getUpstreamFormat();
        localObject1[j] = new TrackGroup(new Format[] { localObject2 });
        localObject2 = ((Format)localObject2).sampleMimeType;
        if ((MimeTypes.isVideo((String)localObject2)) || (MimeTypes.isAudio((String)localObject2))) {}
        for (int k = 1;; k = 0)
        {
          this.trackIsAudioVideoFlags[j] = k;
          this.haveAudioVideoTracks |= k;
          j++;
          break;
        }
      }
      this.tracks = new TrackGroupArray((TrackGroup[])localObject1);
      if ((this.minLoadableRetryCount == -1) && (this.length == -1L) && (this.seekMap.getDurationUs() == -9223372036854775807L)) {
        this.actualMinLoadableRetryCount = 6;
      }
      this.prepared = true;
      this.listener.onSourceInfoRefreshed(this.durationUs, this.seekMap.isSeekable());
      this.callback.onPrepared(this);
    }
  }
  
  private void maybeNotifyTrackFormat(int paramInt)
  {
    if (this.trackFormatNotificationSent[paramInt] == 0)
    {
      Format localFormat = this.tracks.get(paramInt).getFormat(0);
      this.eventDispatcher.downstreamFormatChanged(MimeTypes.getTrackType(localFormat.sampleMimeType), localFormat, 0, null, this.lastSeekPositionUs);
      this.trackFormatNotificationSent[paramInt] = true;
    }
  }
  
  private void maybeStartDeferredRetry(int paramInt)
  {
    int i = 0;
    if ((!this.pendingDeferredRetry) || (this.trackIsAudioVideoFlags[paramInt] == 0) || (this.sampleQueues[paramInt].hasNextSample())) {}
    for (;;)
    {
      return;
      this.pendingResetPositionUs = 0L;
      this.pendingDeferredRetry = false;
      this.notifyDiscontinuity = true;
      this.lastSeekPositionUs = 0L;
      this.extractedSamplesCountAtStartOfLoad = 0;
      SampleQueue[] arrayOfSampleQueue = this.sampleQueues;
      int j = arrayOfSampleQueue.length;
      for (paramInt = i; paramInt < j; paramInt++) {
        arrayOfSampleQueue[paramInt].reset();
      }
      this.callback.onContinueLoadingRequested(this);
    }
  }
  
  private boolean seekInsideBufferUs(long paramLong)
  {
    boolean bool1 = false;
    int i = this.sampleQueues.length;
    int j = 0;
    int k;
    if (j < i)
    {
      SampleQueue localSampleQueue = this.sampleQueues[j];
      localSampleQueue.rewind();
      if (localSampleQueue.advanceTo(paramLong, true, false) != -1)
      {
        k = 1;
        label48:
        if (k != 0) {
          break label85;
        }
        bool2 = bool1;
        if (this.trackIsAudioVideoFlags[j] == 0) {
          if (this.haveAudioVideoTracks) {
            break label85;
          }
        }
      }
    }
    for (boolean bool2 = bool1;; bool2 = true)
    {
      return bool2;
      k = 0;
      break label48;
      label85:
      j++;
      break;
    }
  }
  
  private void startLoading()
  {
    ExtractingLoadable localExtractingLoadable = new ExtractingLoadable(this.uri, this.dataSource, this.extractorHolder, this.loadCondition);
    if (this.prepared)
    {
      Assertions.checkState(isPendingReset());
      if ((this.durationUs != -9223372036854775807L) && (this.pendingResetPositionUs >= this.durationUs))
      {
        this.loadingFinished = true;
        this.pendingResetPositionUs = -9223372036854775807L;
      }
    }
    for (;;)
    {
      return;
      localExtractingLoadable.setLoadPosition(this.seekMap.getSeekPoints(this.pendingResetPositionUs).first.position, this.pendingResetPositionUs);
      this.pendingResetPositionUs = -9223372036854775807L;
      this.extractedSamplesCountAtStartOfLoad = getExtractedSamplesCount();
      long l = this.loader.startLoading(localExtractingLoadable, this, this.actualMinLoadableRetryCount);
      this.eventDispatcher.loadStarted(localExtractingLoadable.dataSpec, 1, -1, null, 0, null, localExtractingLoadable.seekTimeUs, this.durationUs, l);
    }
  }
  
  private boolean suppressRead()
  {
    if ((this.notifyDiscontinuity) || (isPendingReset())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean continueLoading(long paramLong)
  {
    boolean bool;
    if ((this.loadingFinished) || (this.pendingDeferredRetry) || ((this.prepared) && (this.enabledTrackCount == 0))) {
      bool = false;
    }
    for (;;)
    {
      return bool;
      bool = this.loadCondition.open();
      if (!this.loader.isLoading())
      {
        startLoading();
        bool = true;
      }
    }
  }
  
  public void discardBuffer(long paramLong, boolean paramBoolean)
  {
    int i = this.sampleQueues.length;
    for (int j = 0; j < i; j++) {
      this.sampleQueues[j].discardTo(paramLong, paramBoolean, this.trackEnabledStates[j]);
    }
  }
  
  public void endTracks()
  {
    this.sampleQueuesBuilt = true;
    this.handler.post(this.maybeFinishPrepareRunnable);
  }
  
  public long getAdjustedSeekPositionUs(long paramLong, SeekParameters paramSeekParameters)
  {
    if (!this.seekMap.isSeekable()) {}
    SeekMap.SeekPoints localSeekPoints;
    for (paramLong = 0L;; paramLong = Util.resolveSeekPositionUs(paramLong, paramSeekParameters, localSeekPoints.first.timeUs, localSeekPoints.second.timeUs))
    {
      return paramLong;
      localSeekPoints = this.seekMap.getSeekPoints(paramLong);
    }
  }
  
  public long getBufferedPositionUs()
  {
    long l1;
    if (this.loadingFinished) {
      l1 = Long.MIN_VALUE;
    }
    for (;;)
    {
      return l1;
      if (isPendingReset())
      {
        l1 = this.pendingResetPositionUs;
      }
      else
      {
        if (this.haveAudioVideoTracks)
        {
          l1 = Long.MAX_VALUE;
          int i = this.sampleQueues.length;
          int j = 0;
          for (;;)
          {
            l2 = l1;
            if (j >= i) {
              break;
            }
            l2 = l1;
            if (this.trackIsAudioVideoFlags[j] != 0) {
              l2 = Math.min(l1, this.sampleQueues[j].getLargestQueuedTimestampUs());
            }
            j++;
            l1 = l2;
          }
        }
        long l2 = getLargestQueuedTimestampUs();
        l1 = l2;
        if (l2 == Long.MIN_VALUE) {
          l1 = this.lastSeekPositionUs;
        }
      }
    }
  }
  
  public long getNextLoadPositionUs()
  {
    if (this.enabledTrackCount == 0) {}
    for (long l = Long.MIN_VALUE;; l = getBufferedPositionUs()) {
      return l;
    }
  }
  
  public TrackGroupArray getTrackGroups()
  {
    return this.tracks;
  }
  
  boolean isReady(int paramInt)
  {
    if ((!suppressRead()) && ((this.loadingFinished) || (this.sampleQueues[paramInt].hasNextSample()))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  void maybeThrowError()
    throws IOException
  {
    this.loader.maybeThrowError(this.actualMinLoadableRetryCount);
  }
  
  public void maybeThrowPrepareError()
    throws IOException
  {
    maybeThrowError();
  }
  
  public void onLoadCanceled(ExtractingLoadable paramExtractingLoadable, long paramLong1, long paramLong2, boolean paramBoolean)
  {
    this.eventDispatcher.loadCanceled(paramExtractingLoadable.dataSpec, 1, -1, null, 0, null, paramExtractingLoadable.seekTimeUs, this.durationUs, paramLong1, paramLong2, paramExtractingLoadable.bytesLoaded);
    if (!paramBoolean)
    {
      copyLengthFromLoader(paramExtractingLoadable);
      paramExtractingLoadable = this.sampleQueues;
      int i = paramExtractingLoadable.length;
      for (int j = 0; j < i; j++) {
        paramExtractingLoadable[j].reset();
      }
      if (this.enabledTrackCount > 0) {
        this.callback.onContinueLoadingRequested(this);
      }
    }
  }
  
  public void onLoadCompleted(ExtractingLoadable paramExtractingLoadable, long paramLong1, long paramLong2)
  {
    if (this.durationUs == -9223372036854775807L)
    {
      l = getLargestQueuedTimestampUs();
      if (l != Long.MIN_VALUE) {
        break label109;
      }
    }
    label109:
    for (long l = 0L;; l = 10000L + l)
    {
      this.durationUs = l;
      this.listener.onSourceInfoRefreshed(this.durationUs, this.seekMap.isSeekable());
      this.eventDispatcher.loadCompleted(paramExtractingLoadable.dataSpec, 1, -1, null, 0, null, paramExtractingLoadable.seekTimeUs, this.durationUs, paramLong1, paramLong2, paramExtractingLoadable.bytesLoaded);
      copyLengthFromLoader(paramExtractingLoadable);
      this.loadingFinished = true;
      this.callback.onContinueLoadingRequested(this);
      return;
    }
  }
  
  public int onLoadError(ExtractingLoadable paramExtractingLoadable, long paramLong1, long paramLong2, IOException paramIOException)
  {
    boolean bool = isLoadableExceptionFatal(paramIOException);
    this.eventDispatcher.loadError(paramExtractingLoadable.dataSpec, 1, -1, null, 0, null, paramExtractingLoadable.seekTimeUs, this.durationUs, paramLong1, paramLong2, paramExtractingLoadable.bytesLoaded, paramIOException, bool);
    copyLengthFromLoader(paramExtractingLoadable);
    int i;
    if (bool) {
      i = 3;
    }
    for (;;)
    {
      return i;
      int j = getExtractedSamplesCount();
      if (j > this.extractedSamplesCountAtStartOfLoad) {
        i = 1;
      }
      for (;;)
      {
        if (configureRetry(paramExtractingLoadable, j))
        {
          if (i != 0)
          {
            i = 1;
            break;
            i = 0;
            continue;
          }
          i = 0;
          break;
        }
      }
      i = 2;
    }
  }
  
  public void onLoaderReleased()
  {
    this.extractorHolder.release();
    SampleQueue[] arrayOfSampleQueue = this.sampleQueues;
    int i = arrayOfSampleQueue.length;
    for (int j = 0; j < i; j++) {
      arrayOfSampleQueue[j].reset();
    }
  }
  
  public void onUpstreamFormatChanged(Format paramFormat)
  {
    this.handler.post(this.maybeFinishPrepareRunnable);
  }
  
  public void prepare(MediaPeriod.Callback paramCallback, long paramLong)
  {
    this.callback = paramCallback;
    this.loadCondition.open();
    startLoading();
  }
  
  int readData(int paramInt, FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer, boolean paramBoolean)
  {
    int i;
    if (suppressRead()) {
      i = -3;
    }
    for (;;)
    {
      return i;
      int j = this.sampleQueues[paramInt].read(paramFormatHolder, paramDecoderInputBuffer, paramBoolean, this.loadingFinished, this.lastSeekPositionUs);
      if (j == -4)
      {
        maybeNotifyTrackFormat(paramInt);
        i = j;
      }
      else
      {
        i = j;
        if (j == -3)
        {
          maybeStartDeferredRetry(paramInt);
          i = j;
        }
      }
    }
  }
  
  public long readDiscontinuity()
  {
    if ((this.notifyDiscontinuity) && ((this.loadingFinished) || (getExtractedSamplesCount() > this.extractedSamplesCountAtStartOfLoad))) {
      this.notifyDiscontinuity = false;
    }
    for (long l = this.lastSeekPositionUs;; l = -9223372036854775807L) {
      return l;
    }
  }
  
  public void reevaluateBuffer(long paramLong) {}
  
  public void release()
  {
    boolean bool = this.loader.release(this);
    if ((this.prepared) && (!bool))
    {
      SampleQueue[] arrayOfSampleQueue = this.sampleQueues;
      int i = arrayOfSampleQueue.length;
      for (int j = 0; j < i; j++) {
        arrayOfSampleQueue[j].discardToEnd();
      }
    }
    this.handler.removeCallbacksAndMessages(null);
    this.released = true;
  }
  
  public void seekMap(SeekMap paramSeekMap)
  {
    this.seekMap = paramSeekMap;
    this.handler.post(this.maybeFinishPrepareRunnable);
  }
  
  public long seekToUs(long paramLong)
  {
    int i = 0;
    if (this.seekMap.isSeekable())
    {
      this.lastSeekPositionUs = paramLong;
      this.notifyDiscontinuity = false;
      if ((isPendingReset()) || (!seekInsideBufferUs(paramLong))) {
        break label46;
      }
    }
    for (;;)
    {
      return paramLong;
      paramLong = 0L;
      break;
      label46:
      this.pendingDeferredRetry = false;
      this.pendingResetPositionUs = paramLong;
      this.loadingFinished = false;
      if (this.loader.isLoading())
      {
        this.loader.cancelLoading();
      }
      else
      {
        SampleQueue[] arrayOfSampleQueue = this.sampleQueues;
        int j = arrayOfSampleQueue.length;
        while (i < j)
        {
          arrayOfSampleQueue[i].reset();
          i++;
        }
      }
    }
  }
  
  public long selectTracks(TrackSelection[] paramArrayOfTrackSelection, boolean[] paramArrayOfBoolean1, SampleStream[] paramArrayOfSampleStream, boolean[] paramArrayOfBoolean2, long paramLong)
  {
    Assertions.checkState(this.prepared);
    int i = this.enabledTrackCount;
    int k;
    for (int j = 0; j < paramArrayOfTrackSelection.length; j++) {
      if ((paramArrayOfSampleStream[j] != null) && ((paramArrayOfTrackSelection[j] == null) || (paramArrayOfBoolean1[j] == 0)))
      {
        k = ((SampleStreamImpl)paramArrayOfSampleStream[j]).track;
        Assertions.checkState(this.trackEnabledStates[k]);
        this.enabledTrackCount -= 1;
        this.trackEnabledStates[k] = false;
        paramArrayOfSampleStream[j] = null;
      }
    }
    label117:
    boolean bool;
    if (this.seenFirstTrackSelection) {
      if (i == 0)
      {
        j = 1;
        i = 0;
        k = j;
        if (i >= paramArrayOfTrackSelection.length) {
          break label360;
        }
        j = k;
        if (paramArrayOfSampleStream[i] == null)
        {
          j = k;
          if (paramArrayOfTrackSelection[i] != null)
          {
            paramArrayOfBoolean1 = paramArrayOfTrackSelection[i];
            if (paramArrayOfBoolean1.length() != 1) {
              break label336;
            }
            bool = true;
            label164:
            Assertions.checkState(bool);
            if (paramArrayOfBoolean1.getIndexInTrackGroup(0) != 0) {
              break label342;
            }
            bool = true;
            label182:
            Assertions.checkState(bool);
            int m = this.tracks.indexOf(paramArrayOfBoolean1.getTrackGroup());
            if (this.trackEnabledStates[m] != 0) {
              break label348;
            }
            bool = true;
            label215:
            Assertions.checkState(bool);
            this.enabledTrackCount += 1;
            this.trackEnabledStates[m] = true;
            paramArrayOfSampleStream[i] = new SampleStreamImpl(m);
            paramArrayOfBoolean2[i] = true;
            j = k;
            if (k == 0)
            {
              paramArrayOfBoolean1 = this.sampleQueues[m];
              paramArrayOfBoolean1.rewind();
              if ((paramArrayOfBoolean1.advanceTo(paramLong, true, true) != -1) || (paramArrayOfBoolean1.getReadIndex() == 0)) {
                break label354;
              }
            }
          }
        }
      }
    }
    label336:
    label342:
    label348:
    label354:
    for (j = 1;; j = 0)
    {
      i++;
      k = j;
      break label117;
      j = 0;
      break;
      if (paramLong != 0L)
      {
        j = 1;
        break;
      }
      j = 0;
      break;
      bool = false;
      break label164;
      bool = false;
      break label182;
      bool = false;
      break label215;
    }
    label360:
    long l;
    if (this.enabledTrackCount == 0)
    {
      this.pendingDeferredRetry = false;
      this.notifyDiscontinuity = false;
      if (this.loader.isLoading())
      {
        paramArrayOfTrackSelection = this.sampleQueues;
        i = paramArrayOfTrackSelection.length;
        for (j = 0; j < i; j++) {
          paramArrayOfTrackSelection[j].discardToEnd();
        }
        this.loader.cancelLoading();
        l = paramLong;
      }
    }
    do
    {
      this.seenFirstTrackSelection = true;
      return l;
      paramArrayOfTrackSelection = this.sampleQueues;
      i = paramArrayOfTrackSelection.length;
      for (j = 0;; j++)
      {
        l = paramLong;
        if (j >= i) {
          break;
        }
        paramArrayOfTrackSelection[j].reset();
      }
      l = paramLong;
    } while (k == 0);
    paramLong = seekToUs(paramLong);
    for (j = 0;; j++)
    {
      l = paramLong;
      if (j >= paramArrayOfSampleStream.length) {
        break;
      }
      if (paramArrayOfSampleStream[j] != null) {
        paramArrayOfBoolean2[j] = true;
      }
    }
  }
  
  int skipData(int paramInt, long paramLong)
  {
    int i;
    if (suppressRead()) {
      i = 0;
    }
    for (;;)
    {
      return i;
      SampleQueue localSampleQueue = this.sampleQueues[paramInt];
      if ((this.loadingFinished) && (paramLong > localSampleQueue.getLargestQueuedTimestampUs())) {
        i = localSampleQueue.advanceToEnd();
      }
      for (;;)
      {
        if (i <= 0) {
          break label84;
        }
        maybeNotifyTrackFormat(paramInt);
        break;
        int j = localSampleQueue.advanceTo(paramLong, true, true);
        i = j;
        if (j == -1) {
          i = 0;
        }
      }
      label84:
      maybeStartDeferredRetry(paramInt);
    }
  }
  
  public TrackOutput track(int paramInt1, int paramInt2)
  {
    int i = this.sampleQueues.length;
    paramInt2 = 0;
    SampleQueue localSampleQueue;
    if (paramInt2 < i) {
      if (this.sampleQueueTrackIds[paramInt2] == paramInt1) {
        localSampleQueue = this.sampleQueues[paramInt2];
      }
    }
    for (;;)
    {
      return localSampleQueue;
      paramInt2++;
      break;
      localSampleQueue = new SampleQueue(this.allocator);
      localSampleQueue.setUpstreamFormatChangeListener(this);
      this.sampleQueueTrackIds = Arrays.copyOf(this.sampleQueueTrackIds, i + 1);
      this.sampleQueueTrackIds[i] = paramInt1;
      this.sampleQueues = ((SampleQueue[])Arrays.copyOf(this.sampleQueues, i + 1));
      this.sampleQueues[i] = localSampleQueue;
    }
  }
  
  final class ExtractingLoadable
    implements Loader.Loadable
  {
    private long bytesLoaded;
    private final DataSource dataSource;
    private DataSpec dataSpec;
    private final ExtractorMediaPeriod.ExtractorHolder extractorHolder;
    private long length;
    private volatile boolean loadCanceled;
    private final ConditionVariable loadCondition;
    private boolean pendingExtractorSeek;
    private final PositionHolder positionHolder;
    private long seekTimeUs;
    private final Uri uri;
    
    public ExtractingLoadable(Uri paramUri, DataSource paramDataSource, ExtractorMediaPeriod.ExtractorHolder paramExtractorHolder, ConditionVariable paramConditionVariable)
    {
      this.uri = ((Uri)Assertions.checkNotNull(paramUri));
      this.dataSource = ((DataSource)Assertions.checkNotNull(paramDataSource));
      this.extractorHolder = ((ExtractorMediaPeriod.ExtractorHolder)Assertions.checkNotNull(paramExtractorHolder));
      this.loadCondition = paramConditionVariable;
      this.positionHolder = new PositionHolder();
      this.pendingExtractorSeek = true;
      this.length = -1L;
    }
    
    public void cancelLoad()
    {
      this.loadCanceled = true;
    }
    
    public boolean isLoadCanceled()
    {
      return this.loadCanceled;
    }
    
    public void load()
      throws IOException, InterruptedException
    {
      int i = 0;
      if ((i == 0) && (!this.loadCanceled)) {
        for (;;)
        {
          try
          {
            long l1 = this.positionHolder.position;
            localObject1 = new org/telegram/messenger/exoplayer2/upstream/DataSpec;
            ((DataSpec)localObject1).<init>(this.uri, l1, -1L, ExtractorMediaPeriod.this.customCacheKey);
            this.dataSpec = ((DataSpec)localObject1);
            this.length = this.dataSource.open(this.dataSpec);
            if (this.length != -1L) {
              this.length += l1;
            }
            localObject1 = new org/telegram/messenger/exoplayer2/extractor/DefaultExtractorInput;
            ((DefaultExtractorInput)localObject1).<init>(this.dataSource, l1, this.length);
            int j = i;
            try
            {
              Extractor localExtractor = this.extractorHolder.selectExtractor((ExtractorInput)localObject1, this.dataSource.getUri());
              long l2 = l1;
              k = i;
              j = i;
              if (this.pendingExtractorSeek)
              {
                j = i;
                localExtractor.seek(l1, this.seekTimeUs);
                j = i;
                this.pendingExtractorSeek = false;
                k = i;
                l2 = l1;
              }
              if (k != 0) {
                continue;
              }
              j = k;
              if (this.loadCanceled) {
                continue;
              }
              j = k;
              this.loadCondition.block();
              j = k;
              i = localExtractor.read((ExtractorInput)localObject1, this.positionHolder);
              k = i;
              j = i;
              if (((ExtractorInput)localObject1).getPosition() <= ExtractorMediaPeriod.this.continueLoadingCheckIntervalBytes + l2) {
                continue;
              }
              j = i;
              l2 = ((ExtractorInput)localObject1).getPosition();
              j = i;
              this.loadCondition.close();
              j = i;
              ExtractorMediaPeriod.this.handler.post(ExtractorMediaPeriod.this.onContinueLoadingRequestedRunnable);
              k = i;
              continue;
              if (i != 1) {
                continue;
              }
            }
            finally
            {
              i = j;
            }
          }
          finally
          {
            int k;
            Object localObject1 = null;
            continue;
          }
          Util.closeQuietly(this.dataSource);
          throw ((Throwable)localObject2);
          if (k == 1)
          {
            i = 0;
            Util.closeQuietly(this.dataSource);
            break;
          }
          i = k;
          if (localObject1 != null)
          {
            this.positionHolder.position = ((ExtractorInput)localObject1).getPosition();
            this.bytesLoaded = (this.positionHolder.position - this.dataSpec.absoluteStreamPosition);
            i = k;
            continue;
            if (localObject1 != null)
            {
              this.positionHolder.position = ((ExtractorInput)localObject1).getPosition();
              this.bytesLoaded = (this.positionHolder.position - this.dataSpec.absoluteStreamPosition);
            }
          }
        }
      }
    }
    
    public void setLoadPosition(long paramLong1, long paramLong2)
    {
      this.positionHolder.position = paramLong1;
      this.seekTimeUs = paramLong2;
      this.pendingExtractorSeek = true;
    }
  }
  
  private static final class ExtractorHolder
  {
    private Extractor extractor;
    private final ExtractorOutput extractorOutput;
    private final Extractor[] extractors;
    
    public ExtractorHolder(Extractor[] paramArrayOfExtractor, ExtractorOutput paramExtractorOutput)
    {
      this.extractors = paramArrayOfExtractor;
      this.extractorOutput = paramExtractorOutput;
    }
    
    public void release()
    {
      if (this.extractor != null)
      {
        this.extractor.release();
        this.extractor = null;
      }
    }
    
    /* Error */
    public Extractor selectExtractor(ExtractorInput paramExtractorInput, Uri paramUri)
      throws IOException, InterruptedException
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 26	org/telegram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractorHolder:extractor	Lorg/telegram/messenger/exoplayer2/extractor/Extractor;
      //   4: ifnull +10 -> 14
      //   7: aload_0
      //   8: getfield 26	org/telegram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractorHolder:extractor	Lorg/telegram/messenger/exoplayer2/extractor/Extractor;
      //   11: astore_1
      //   12: aload_1
      //   13: areturn
      //   14: aload_0
      //   15: getfield 20	org/telegram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractorHolder:extractors	[Lorg/telegram/messenger/exoplayer2/extractor/Extractor;
      //   18: astore_3
      //   19: aload_3
      //   20: arraylength
      //   21: istore 4
      //   23: iconst_0
      //   24: istore 5
      //   26: iload 5
      //   28: iload 4
      //   30: if_icmpge +32 -> 62
      //   33: aload_3
      //   34: iload 5
      //   36: aaload
      //   37: astore 6
      //   39: aload 6
      //   41: aload_1
      //   42: invokeinterface 42 2 0
      //   47: ifeq +61 -> 108
      //   50: aload_0
      //   51: aload 6
      //   53: putfield 26	org/telegram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractorHolder:extractor	Lorg/telegram/messenger/exoplayer2/extractor/Extractor;
      //   56: aload_1
      //   57: invokeinterface 47 1 0
      //   62: aload_0
      //   63: getfield 26	org/telegram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractorHolder:extractor	Lorg/telegram/messenger/exoplayer2/extractor/Extractor;
      //   66: ifnonnull +74 -> 140
      //   69: new 49	org/telegram/messenger/exoplayer2/source/UnrecognizedInputFormatException
      //   72: dup
      //   73: new 51	java/lang/StringBuilder
      //   76: dup
      //   77: invokespecial 52	java/lang/StringBuilder:<init>	()V
      //   80: ldc 54
      //   82: invokevirtual 58	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   85: aload_0
      //   86: getfield 20	org/telegram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractorHolder:extractors	[Lorg/telegram/messenger/exoplayer2/extractor/Extractor;
      //   89: invokestatic 64	org/telegram/messenger/exoplayer2/util/Util:getCommaDelimitedSimpleClassNames	([Ljava/lang/Object;)Ljava/lang/String;
      //   92: invokevirtual 58	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   95: ldc 66
      //   97: invokevirtual 58	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   100: invokevirtual 70	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   103: aload_2
      //   104: invokespecial 73	org/telegram/messenger/exoplayer2/source/UnrecognizedInputFormatException:<init>	(Ljava/lang/String;Landroid/net/Uri;)V
      //   107: athrow
      //   108: aload_1
      //   109: invokeinterface 47 1 0
      //   114: iinc 5 1
      //   117: goto -91 -> 26
      //   120: astore 6
      //   122: aload_1
      //   123: invokeinterface 47 1 0
      //   128: goto -14 -> 114
      //   131: astore_2
      //   132: aload_1
      //   133: invokeinterface 47 1 0
      //   138: aload_2
      //   139: athrow
      //   140: aload_0
      //   141: getfield 26	org/telegram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractorHolder:extractor	Lorg/telegram/messenger/exoplayer2/extractor/Extractor;
      //   144: aload_0
      //   145: getfield 22	org/telegram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractorHolder:extractorOutput	Lorg/telegram/messenger/exoplayer2/extractor/ExtractorOutput;
      //   148: invokeinterface 77 2 0
      //   153: aload_0
      //   154: getfield 26	org/telegram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractorHolder:extractor	Lorg/telegram/messenger/exoplayer2/extractor/Extractor;
      //   157: astore_1
      //   158: goto -146 -> 12
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	161	0	this	ExtractorHolder
      //   0	161	1	paramExtractorInput	ExtractorInput
      //   0	161	2	paramUri	Uri
      //   18	16	3	arrayOfExtractor	Extractor[]
      //   21	10	4	i	int
      //   24	91	5	j	int
      //   37	15	6	localExtractor	Extractor
      //   120	1	6	localEOFException	java.io.EOFException
      // Exception table:
      //   from	to	target	type
      //   39	56	120	java/io/EOFException
      //   39	56	131	finally
    }
  }
  
  static abstract interface Listener
  {
    public abstract void onSourceInfoRefreshed(long paramLong, boolean paramBoolean);
  }
  
  private final class SampleStreamImpl
    implements SampleStream
  {
    private final int track;
    
    public SampleStreamImpl(int paramInt)
    {
      this.track = paramInt;
    }
    
    public boolean isReady()
    {
      return ExtractorMediaPeriod.this.isReady(this.track);
    }
    
    public void maybeThrowError()
      throws IOException
    {
      ExtractorMediaPeriod.this.maybeThrowError();
    }
    
    public int readData(FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer, boolean paramBoolean)
    {
      return ExtractorMediaPeriod.this.readData(this.track, paramFormatHolder, paramDecoderInputBuffer, paramBoolean);
    }
    
    public int skipData(long paramLong)
    {
      return ExtractorMediaPeriod.this.skipData(this.track, paramLong);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/ExtractorMediaPeriod.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */