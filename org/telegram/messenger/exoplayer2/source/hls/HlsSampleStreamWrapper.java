package org.telegram.messenger.exoplayer2.source.hls;

import android.os.Handler;
import android.util.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.FormatHolder;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.extractor.DummyTrackOutput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.SeekMap;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.source.SampleQueue;
import org.telegram.messenger.exoplayer2.source.SampleQueue.UpstreamFormatChangedListener;
import org.telegram.messenger.exoplayer2.source.SampleStream;
import org.telegram.messenger.exoplayer2.source.SequenceableLoader;
import org.telegram.messenger.exoplayer2.source.SequenceableLoader.Callback;
import org.telegram.messenger.exoplayer2.source.TrackGroup;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.source.chunk.Chunk;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.upstream.Loader;
import org.telegram.messenger.exoplayer2.upstream.Loader.Callback;
import org.telegram.messenger.exoplayer2.upstream.Loader.Loadable;
import org.telegram.messenger.exoplayer2.upstream.Loader.ReleaseCallback;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.Util;

final class HlsSampleStreamWrapper
  implements ExtractorOutput, SampleQueue.UpstreamFormatChangedListener, SequenceableLoader, Loader.Callback<Chunk>, Loader.ReleaseCallback
{
  private static final int PRIMARY_TYPE_AUDIO = 2;
  private static final int PRIMARY_TYPE_NONE = 0;
  private static final int PRIMARY_TYPE_TEXT = 1;
  private static final int PRIMARY_TYPE_VIDEO = 3;
  private static final String TAG = "HlsSampleStreamWrapper";
  private final Allocator allocator;
  private int audioSampleQueueIndex;
  private boolean audioSampleQueueMappingDone;
  private final Callback callback;
  private final HlsChunkSource chunkSource;
  private Format downstreamTrackFormat;
  private int enabledTrackGroupCount;
  private final MediaSourceEventListener.EventDispatcher eventDispatcher;
  private final Handler handler;
  private boolean haveAudioVideoSampleQueues;
  private long lastSeekPositionUs;
  private final Loader loader;
  private boolean loadingFinished;
  private final Runnable maybeFinishPrepareRunnable;
  private final ArrayList<HlsMediaChunk> mediaChunks;
  private final int minLoadableRetryCount;
  private final Format muxedAudioFormat;
  private final HlsChunkSource.HlsChunkHolder nextChunkHolder;
  private final Runnable onTracksEndedRunnable;
  private long pendingResetPositionUs;
  private boolean pendingResetUpstreamFormats;
  private boolean prepared;
  private int primaryTrackGroupIndex;
  private boolean released;
  private long sampleOffsetUs;
  private boolean[] sampleQueueIsAudioVideoFlags;
  private int[] sampleQueueTrackIds;
  private SampleQueue[] sampleQueues;
  private boolean sampleQueuesBuilt;
  private boolean[] sampleQueuesEnabledStates;
  private boolean seenFirstTrackSelection;
  private int[] trackGroupToSampleQueueIndex;
  private TrackGroupArray trackGroups;
  private final int trackType;
  private boolean tracksEnded;
  private int videoSampleQueueIndex;
  private boolean videoSampleQueueMappingDone;
  
  public HlsSampleStreamWrapper(int paramInt1, Callback paramCallback, HlsChunkSource paramHlsChunkSource, Allocator paramAllocator, long paramLong, Format paramFormat, int paramInt2, MediaSourceEventListener.EventDispatcher paramEventDispatcher)
  {
    this.trackType = paramInt1;
    this.callback = paramCallback;
    this.chunkSource = paramHlsChunkSource;
    this.allocator = paramAllocator;
    this.muxedAudioFormat = paramFormat;
    this.minLoadableRetryCount = paramInt2;
    this.eventDispatcher = paramEventDispatcher;
    this.loader = new Loader("Loader:HlsSampleStreamWrapper");
    this.nextChunkHolder = new HlsChunkSource.HlsChunkHolder();
    this.sampleQueueTrackIds = new int[0];
    this.audioSampleQueueIndex = -1;
    this.videoSampleQueueIndex = -1;
    this.sampleQueues = new SampleQueue[0];
    this.sampleQueueIsAudioVideoFlags = new boolean[0];
    this.sampleQueuesEnabledStates = new boolean[0];
    this.mediaChunks = new ArrayList();
    this.maybeFinishPrepareRunnable = new Runnable()
    {
      public void run()
      {
        HlsSampleStreamWrapper.this.maybeFinishPrepare();
      }
    };
    this.onTracksEndedRunnable = new Runnable()
    {
      public void run()
      {
        HlsSampleStreamWrapper.this.onTracksEnded();
      }
    };
    this.handler = new Handler();
    this.lastSeekPositionUs = paramLong;
    this.pendingResetPositionUs = paramLong;
  }
  
  private void buildTracks()
  {
    int i = 0;
    int j = -1;
    int k = this.sampleQueues.length;
    int m = 0;
    Object localObject;
    if (m < k)
    {
      localObject = this.sampleQueues[m].getUpstreamFormat().sampleMimeType;
      label45:
      int i2;
      if (MimeTypes.isVideo((String)localObject))
      {
        n = 3;
        if (n <= i) {
          break label105;
        }
        i1 = m;
        i2 = n;
      }
      for (;;)
      {
        m++;
        j = i1;
        i = i2;
        break;
        if (MimeTypes.isAudio((String)localObject))
        {
          n = 2;
          break label45;
        }
        if (MimeTypes.isText((String)localObject))
        {
          n = 1;
          break label45;
        }
        n = 0;
        break label45;
        label105:
        i1 = j;
        i2 = i;
        if (n == i)
        {
          i1 = j;
          i2 = i;
          if (j != -1)
          {
            i1 = -1;
            i2 = i;
          }
        }
      }
    }
    TrackGroup localTrackGroup = this.chunkSource.getTrackGroup();
    int i1 = localTrackGroup.length;
    this.primaryTrackGroupIndex = -1;
    this.trackGroupToSampleQueueIndex = new int[k];
    for (int n = 0; n < k; n++) {
      this.trackGroupToSampleQueueIndex[n] = n;
    }
    TrackGroup[] arrayOfTrackGroup = new TrackGroup[k];
    n = 0;
    while (n < k)
    {
      Format localFormat = this.sampleQueues[n].getUpstreamFormat();
      if (n == j)
      {
        localObject = new Format[i1];
        for (m = 0; m < i1; m++) {
          localObject[m] = deriveFormat(localTrackGroup.getFormat(m), localFormat, true);
        }
        arrayOfTrackGroup[n] = new TrackGroup((Format[])localObject);
        this.primaryTrackGroupIndex = n;
        n++;
      }
      else
      {
        if ((i == 3) && (MimeTypes.isAudio(localFormat.sampleMimeType))) {}
        for (localObject = this.muxedAudioFormat;; localObject = null)
        {
          arrayOfTrackGroup[n] = new TrackGroup(new Format[] { deriveFormat((Format)localObject, localFormat, false) });
          break;
        }
      }
    }
    this.trackGroups = new TrackGroupArray(arrayOfTrackGroup);
  }
  
  private static DummyTrackOutput createDummyTrackOutput(int paramInt1, int paramInt2)
  {
    Log.w("HlsSampleStreamWrapper", "Unmapped track with id " + paramInt1 + " of type " + paramInt2);
    return new DummyTrackOutput();
  }
  
  private static Format deriveFormat(Format paramFormat1, Format paramFormat2, boolean paramBoolean)
  {
    if (paramFormat1 == null) {
      return paramFormat2;
    }
    if (paramBoolean) {}
    for (int i = paramFormat1.bitrate;; i = -1)
    {
      int j = MimeTypes.getTrackType(paramFormat2.sampleMimeType);
      String str1 = Util.getCodecsOfType(paramFormat1.codecs, j);
      String str2 = MimeTypes.getMediaMimeType(str1);
      String str3 = str2;
      if (str2 == null) {
        str3 = paramFormat2.sampleMimeType;
      }
      paramFormat2 = paramFormat2.copyWithContainerInfo(paramFormat1.id, str3, str1, i, paramFormat1.width, paramFormat1.height, paramFormat1.selectionFlags, paramFormat1.language);
      break;
    }
  }
  
  private boolean finishedReadingChunk(HlsMediaChunk paramHlsMediaChunk)
  {
    int i = paramHlsMediaChunk.uid;
    int j = this.sampleQueues.length;
    int k = 0;
    if (k < j) {
      if ((this.sampleQueuesEnabledStates[k] == 0) || (this.sampleQueues[k].peekSourceId() != i)) {}
    }
    for (boolean bool = false;; bool = true)
    {
      return bool;
      k++;
      break;
    }
  }
  
  private static boolean formatsMatch(Format paramFormat1, Format paramFormat2)
  {
    boolean bool1 = true;
    String str1 = paramFormat1.sampleMimeType;
    String str2 = paramFormat2.sampleMimeType;
    int i = MimeTypes.getTrackType(str1);
    boolean bool2;
    if (i != 3) {
      if (i == MimeTypes.getTrackType(str2)) {
        bool2 = bool1;
      }
    }
    for (;;)
    {
      return bool2;
      bool2 = false;
      continue;
      if (!Util.areEqual(str1, str2))
      {
        bool2 = false;
      }
      else if (!"application/cea-608".equals(str1))
      {
        bool2 = bool1;
        if (!"application/cea-708".equals(str1)) {}
      }
      else
      {
        bool2 = bool1;
        if (paramFormat1.accessibilityChannel != paramFormat2.accessibilityChannel) {
          bool2 = false;
        }
      }
    }
  }
  
  private HlsMediaChunk getLastMediaChunk()
  {
    return (HlsMediaChunk)this.mediaChunks.get(this.mediaChunks.size() - 1);
  }
  
  private static boolean isMediaChunk(Chunk paramChunk)
  {
    return paramChunk instanceof HlsMediaChunk;
  }
  
  private boolean isPendingReset()
  {
    if (this.pendingResetPositionUs != -9223372036854775807L) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private void mapSampleQueuesToMatchTrackGroups()
  {
    int i = this.trackGroups.length;
    this.trackGroupToSampleQueueIndex = new int[i];
    Arrays.fill(this.trackGroupToSampleQueueIndex, -1);
    int j = 0;
    if (j < i) {
      for (int k = 0;; k++) {
        if (k < this.sampleQueues.length)
        {
          if (formatsMatch(this.sampleQueues[k].getUpstreamFormat(), this.trackGroups.get(j).getFormat(0))) {
            this.trackGroupToSampleQueueIndex[j] = k;
          }
        }
        else
        {
          j++;
          break;
        }
      }
    }
  }
  
  private void maybeFinishPrepare()
  {
    if ((this.released) || (this.trackGroupToSampleQueueIndex != null) || (!this.sampleQueuesBuilt)) {}
    for (;;)
    {
      return;
      SampleQueue[] arrayOfSampleQueue = this.sampleQueues;
      int i = arrayOfSampleQueue.length;
      for (int j = 0;; j++)
      {
        if (j >= i) {
          break label52;
        }
        if (arrayOfSampleQueue[j].getUpstreamFormat() == null) {
          break;
        }
      }
      label52:
      if (this.trackGroups != null)
      {
        mapSampleQueuesToMatchTrackGroups();
      }
      else
      {
        buildTracks();
        this.prepared = true;
        this.callback.onPrepared();
      }
    }
  }
  
  private void onTracksEnded()
  {
    this.sampleQueuesBuilt = true;
    maybeFinishPrepare();
  }
  
  private void resetSampleQueues()
  {
    SampleQueue[] arrayOfSampleQueue = this.sampleQueues;
    int i = arrayOfSampleQueue.length;
    for (int j = 0; j < i; j++) {
      arrayOfSampleQueue[j].reset(this.pendingResetUpstreamFormats);
    }
    this.pendingResetUpstreamFormats = false;
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
        if (this.sampleQueueIsAudioVideoFlags[j] == 0) {
          if (this.haveAudioVideoSampleQueues) {
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
  
  public int bindSampleQueueToSampleStream(int paramInt)
  {
    if (!isMappingFinished()) {
      paramInt = -1;
    }
    for (;;)
    {
      return paramInt;
      paramInt = this.trackGroupToSampleQueueIndex[paramInt];
      if (paramInt == -1) {
        paramInt = -1;
      } else if (this.sampleQueuesEnabledStates[paramInt] != 0) {
        paramInt = -1;
      } else {
        this.sampleQueuesEnabledStates[paramInt] = true;
      }
    }
  }
  
  public boolean continueLoading(long paramLong)
  {
    boolean bool;
    if ((this.loadingFinished) || (this.loader.isLoading())) {
      bool = false;
    }
    for (;;)
    {
      return bool;
      Object localObject1;
      if (isPendingReset()) {
        localObject1 = null;
      }
      Object localObject2;
      for (long l = this.pendingResetPositionUs;; l = ((HlsMediaChunk)localObject1).endTimeUs)
      {
        this.chunkSource.getNextChunk((HlsMediaChunk)localObject1, paramLong, l, this.nextChunkHolder);
        bool = this.nextChunkHolder.endOfStream;
        localObject1 = this.nextChunkHolder.chunk;
        localObject2 = this.nextChunkHolder.playlist;
        this.nextChunkHolder.clear();
        if (!bool) {
          break label123;
        }
        this.pendingResetPositionUs = -9223372036854775807L;
        this.loadingFinished = true;
        bool = true;
        break;
        localObject1 = getLastMediaChunk();
      }
      label123:
      if (localObject1 == null)
      {
        if (localObject2 != null) {
          this.callback.onPlaylistRefreshRequired((HlsMasterPlaylist.HlsUrl)localObject2);
        }
        bool = false;
      }
      else
      {
        if (isMediaChunk((Chunk)localObject1))
        {
          this.pendingResetPositionUs = -9223372036854775807L;
          localObject2 = (HlsMediaChunk)localObject1;
          ((HlsMediaChunk)localObject2).init(this);
          this.mediaChunks.add(localObject2);
        }
        paramLong = this.loader.startLoading((Loader.Loadable)localObject1, this, this.minLoadableRetryCount);
        this.eventDispatcher.loadStarted(((Chunk)localObject1).dataSpec, ((Chunk)localObject1).type, this.trackType, ((Chunk)localObject1).trackFormat, ((Chunk)localObject1).trackSelectionReason, ((Chunk)localObject1).trackSelectionData, ((Chunk)localObject1).startTimeUs, ((Chunk)localObject1).endTimeUs, paramLong);
        bool = true;
      }
    }
  }
  
  public void continuePreparing()
  {
    if (!this.prepared) {
      continueLoading(this.lastSeekPositionUs);
    }
  }
  
  public void discardBuffer(long paramLong, boolean paramBoolean)
  {
    if (!this.sampleQueuesBuilt) {}
    for (;;)
    {
      return;
      int i = this.sampleQueues.length;
      for (int j = 0; j < i; j++) {
        this.sampleQueues[j].discardTo(paramLong, paramBoolean, this.sampleQueuesEnabledStates[j]);
      }
    }
  }
  
  public void endTracks()
  {
    this.tracksEnded = true;
    this.handler.post(this.onTracksEndedRunnable);
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
    l1 = this.lastSeekPositionUs;
    Object localObject = getLastMediaChunk();
    if (((HlsMediaChunk)localObject).isLoadCompleted()) {}
    for (;;)
    {
      long l2 = l1;
      if (localObject != null) {
        l2 = Math.max(l1, ((HlsMediaChunk)localObject).endTimeUs);
      }
      l1 = l2;
      if (!this.sampleQueuesBuilt) {
        break;
      }
      localObject = this.sampleQueues;
      int i = localObject.length;
      for (int j = 0;; j++)
      {
        l1 = l2;
        if (j >= i) {
          break;
        }
        l2 = Math.max(l2, localObject[j].getLargestQueuedTimestampUs());
      }
      if (this.mediaChunks.size() > 1) {
        localObject = (HlsMediaChunk)this.mediaChunks.get(this.mediaChunks.size() - 2);
      } else {
        localObject = null;
      }
    }
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
  
  public TrackGroupArray getTrackGroups()
  {
    return this.trackGroups;
  }
  
  public void init(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = 0;
    if (!paramBoolean2)
    {
      this.audioSampleQueueMappingDone = false;
      this.videoSampleQueueMappingDone = false;
    }
    SampleQueue[] arrayOfSampleQueue = this.sampleQueues;
    int j = arrayOfSampleQueue.length;
    for (int k = 0; k < j; k++) {
      arrayOfSampleQueue[k].sourceId(paramInt);
    }
    if (paramBoolean1)
    {
      arrayOfSampleQueue = this.sampleQueues;
      k = arrayOfSampleQueue.length;
      for (paramInt = i; paramInt < k; paramInt++) {
        arrayOfSampleQueue[paramInt].splice();
      }
    }
  }
  
  public boolean isMappingFinished()
  {
    if (this.trackGroupToSampleQueueIndex != null) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isReady(int paramInt)
  {
    if ((this.loadingFinished) || ((!isPendingReset()) && (this.sampleQueues[paramInt].hasNextSample()))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void maybeThrowError()
    throws IOException
  {
    this.loader.maybeThrowError();
    this.chunkSource.maybeThrowError();
  }
  
  public void maybeThrowPrepareError()
    throws IOException
  {
    maybeThrowError();
  }
  
  public void onLoadCanceled(Chunk paramChunk, long paramLong1, long paramLong2, boolean paramBoolean)
  {
    this.eventDispatcher.loadCanceled(paramChunk.dataSpec, paramChunk.type, this.trackType, paramChunk.trackFormat, paramChunk.trackSelectionReason, paramChunk.trackSelectionData, paramChunk.startTimeUs, paramChunk.endTimeUs, paramLong1, paramLong2, paramChunk.bytesLoaded());
    if (!paramBoolean)
    {
      resetSampleQueues();
      if (this.enabledTrackGroupCount > 0) {
        this.callback.onContinueLoadingRequested(this);
      }
    }
  }
  
  public void onLoadCompleted(Chunk paramChunk, long paramLong1, long paramLong2)
  {
    this.chunkSource.onChunkLoadCompleted(paramChunk);
    this.eventDispatcher.loadCompleted(paramChunk.dataSpec, paramChunk.type, this.trackType, paramChunk.trackFormat, paramChunk.trackSelectionReason, paramChunk.trackSelectionData, paramChunk.startTimeUs, paramChunk.endTimeUs, paramLong1, paramLong2, paramChunk.bytesLoaded());
    if (!this.prepared) {
      continueLoading(this.lastSeekPositionUs);
    }
    for (;;)
    {
      return;
      this.callback.onContinueLoadingRequested(this);
    }
  }
  
  public int onLoadError(Chunk paramChunk, long paramLong1, long paramLong2, IOException paramIOException)
  {
    long l = paramChunk.bytesLoaded();
    boolean bool1 = isMediaChunk(paramChunk);
    boolean bool2;
    label76:
    label173:
    int i;
    if ((!bool1) || (l == 0L))
    {
      bool2 = true;
      boolean bool3 = false;
      if (this.chunkSource.onChunkLoadError(paramChunk, bool2, paramIOException))
      {
        if (bool1)
        {
          if ((HlsMediaChunk)this.mediaChunks.remove(this.mediaChunks.size() - 1) != paramChunk) {
            break label185;
          }
          bool2 = true;
          Assertions.checkState(bool2);
          if (this.mediaChunks.isEmpty()) {
            this.pendingResetPositionUs = this.lastSeekPositionUs;
          }
        }
        bool3 = true;
      }
      this.eventDispatcher.loadError(paramChunk.dataSpec, paramChunk.type, this.trackType, paramChunk.trackFormat, paramChunk.trackSelectionReason, paramChunk.trackSelectionData, paramChunk.startTimeUs, paramChunk.endTimeUs, paramLong1, paramLong2, paramChunk.bytesLoaded(), paramIOException, bool3);
      if (!bool3) {
        break label204;
      }
      if (this.prepared) {
        break label191;
      }
      continueLoading(this.lastSeekPositionUs);
      i = 2;
    }
    for (;;)
    {
      return i;
      bool2 = false;
      break;
      label185:
      bool2 = false;
      break label76;
      label191:
      this.callback.onContinueLoadingRequested(this);
      break label173;
      label204:
      if ((paramIOException instanceof ParserException)) {
        i = 3;
      } else {
        i = 0;
      }
    }
  }
  
  public void onLoaderReleased()
  {
    resetSampleQueues();
  }
  
  public void onPlaylistBlacklisted(HlsMasterPlaylist.HlsUrl paramHlsUrl, long paramLong)
  {
    this.chunkSource.onPlaylistBlacklisted(paramHlsUrl, paramLong);
  }
  
  public void onUpstreamFormatChanged(Format paramFormat)
  {
    this.handler.post(this.maybeFinishPrepareRunnable);
  }
  
  public void prepareWithMasterPlaylistInfo(TrackGroupArray paramTrackGroupArray, int paramInt)
  {
    this.prepared = true;
    this.trackGroups = paramTrackGroupArray;
    this.primaryTrackGroupIndex = paramInt;
    this.callback.onPrepared();
  }
  
  public int readData(int paramInt, FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer, boolean paramBoolean)
  {
    if (isPendingReset()) {}
    for (paramInt = -3;; paramInt = this.sampleQueues[paramInt].read(paramFormatHolder, paramDecoderInputBuffer, paramBoolean, this.loadingFinished, this.lastSeekPositionUs))
    {
      return paramInt;
      if (!this.mediaChunks.isEmpty())
      {
        for (int i = 0; (i < this.mediaChunks.size() - 1) && (finishedReadingChunk((HlsMediaChunk)this.mediaChunks.get(i))); i++) {}
        if (i > 0) {
          Util.removeRange(this.mediaChunks, 0, i);
        }
        HlsMediaChunk localHlsMediaChunk = (HlsMediaChunk)this.mediaChunks.get(0);
        Format localFormat = localHlsMediaChunk.trackFormat;
        if (!localFormat.equals(this.downstreamTrackFormat)) {
          this.eventDispatcher.downstreamFormatChanged(this.trackType, localFormat, localHlsMediaChunk.trackSelectionReason, localHlsMediaChunk.trackSelectionData, localHlsMediaChunk.startTimeUs);
        }
        this.downstreamTrackFormat = localFormat;
      }
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
  
  public void seekMap(SeekMap paramSeekMap) {}
  
  public boolean seekToUs(long paramLong, boolean paramBoolean)
  {
    boolean bool = false;
    this.lastSeekPositionUs = paramLong;
    if ((this.sampleQueuesBuilt) && (!paramBoolean) && (!isPendingReset()) && (seekInsideBufferUs(paramLong)))
    {
      paramBoolean = bool;
      return paramBoolean;
    }
    this.pendingResetPositionUs = paramLong;
    this.loadingFinished = false;
    this.mediaChunks.clear();
    if (this.loader.isLoading()) {
      this.loader.cancelLoading();
    }
    for (;;)
    {
      paramBoolean = true;
      break;
      resetSampleQueues();
    }
  }
  
  public boolean selectTracks(TrackSelection[] paramArrayOfTrackSelection, boolean[] paramArrayOfBoolean1, SampleStream[] paramArrayOfSampleStream, boolean[] paramArrayOfBoolean2, long paramLong, boolean paramBoolean)
  {
    Assertions.checkState(this.prepared);
    int i = this.enabledTrackGroupCount;
    for (int j = 0; j < paramArrayOfTrackSelection.length; j++) {
      if ((paramArrayOfSampleStream[j] != null) && ((paramArrayOfTrackSelection[j] == null) || (paramArrayOfBoolean1[j] == 0)))
      {
        this.enabledTrackGroupCount -= 1;
        ((HlsSampleStream)paramArrayOfSampleStream[j]).unbindSampleQueue();
        paramArrayOfSampleStream[j] = null;
      }
    }
    boolean bool1;
    label95:
    TrackSelection localTrackSelection;
    if (!paramBoolean)
    {
      if (this.seenFirstTrackSelection) {
        if (i != 0) {
          break label313;
        }
      }
    }
    else
    {
      bool1 = true;
      localTrackSelection = this.chunkSource.getTrackSelection();
      paramArrayOfBoolean1 = localTrackSelection;
      j = 0;
      label110:
      if (j >= paramArrayOfTrackSelection.length) {
        break label328;
      }
      localObject = paramArrayOfBoolean1;
      bool2 = bool1;
      if (paramArrayOfSampleStream[j] == null)
      {
        localObject = paramArrayOfBoolean1;
        bool2 = bool1;
        if (paramArrayOfTrackSelection[j] != null)
        {
          this.enabledTrackGroupCount += 1;
          localObject = paramArrayOfTrackSelection[j];
          i = this.trackGroups.indexOf(((TrackSelection)localObject).getTrackGroup());
          if (i == this.primaryTrackGroupIndex)
          {
            paramArrayOfBoolean1 = (boolean[])localObject;
            this.chunkSource.selectTracks((TrackSelection)localObject);
          }
          paramArrayOfSampleStream[j] = new HlsSampleStream(this, i);
          paramArrayOfBoolean2[j] = true;
          localObject = paramArrayOfBoolean1;
          bool2 = bool1;
          if (this.sampleQueuesBuilt)
          {
            localObject = paramArrayOfBoolean1;
            bool2 = bool1;
            if (!bool1)
            {
              localObject = this.sampleQueues[this.trackGroupToSampleQueueIndex[i]];
              ((SampleQueue)localObject).rewind();
              if ((((SampleQueue)localObject).advanceTo(paramLong, true, true) != -1) || (((SampleQueue)localObject).getReadIndex() == 0)) {
                break label319;
              }
              bool2 = true;
            }
          }
        }
      }
    }
    for (Object localObject = paramArrayOfBoolean1;; localObject = paramArrayOfBoolean1)
    {
      j++;
      paramArrayOfBoolean1 = (boolean[])localObject;
      bool1 = bool2;
      break label110;
      if (paramLong != this.lastSeekPositionUs) {
        break;
      }
      label313:
      bool1 = false;
      break label95;
      label319:
      bool2 = false;
    }
    label328:
    if (this.enabledTrackGroupCount == 0)
    {
      this.chunkSource.reset();
      this.downstreamTrackFormat = null;
      this.mediaChunks.clear();
      if (this.loader.isLoading())
      {
        if (this.sampleQueuesBuilt)
        {
          paramArrayOfTrackSelection = this.sampleQueues;
          i = paramArrayOfTrackSelection.length;
          for (j = 0; j < i; j++) {
            paramArrayOfTrackSelection[j].discardToEnd();
          }
        }
        this.loader.cancelLoading();
      }
      for (;;)
      {
        this.seenFirstTrackSelection = true;
        return bool1;
        resetSampleQueues();
      }
    }
    boolean bool2 = bool1;
    boolean bool3 = paramBoolean;
    long l;
    if (!this.mediaChunks.isEmpty())
    {
      bool2 = bool1;
      bool3 = paramBoolean;
      if (!Util.areEqual(paramArrayOfBoolean1, localTrackSelection))
      {
        j = 0;
        if (this.seenFirstTrackSelection) {
          break label609;
        }
        if (paramLong >= 0L) {
          break label603;
        }
        l = -paramLong;
        label482:
        paramArrayOfBoolean1.updateSelectedTrack(paramLong, l, -9223372036854775807L);
        i = this.chunkSource.getTrackGroup().indexOf(getLastMediaChunk().trackFormat);
        if (paramArrayOfBoolean1.getSelectedIndexInTrackGroup() == i) {}
      }
    }
    label603:
    label609:
    for (j = 1;; j = 1)
    {
      bool2 = bool1;
      bool3 = paramBoolean;
      if (j != 0)
      {
        bool3 = true;
        bool2 = true;
        this.pendingResetUpstreamFormats = true;
      }
      bool1 = bool2;
      if (!bool2) {
        break;
      }
      seekToUs(paramLong, bool3);
      for (j = 0;; j++)
      {
        bool1 = bool2;
        if (j >= paramArrayOfSampleStream.length) {
          break;
        }
        if (paramArrayOfSampleStream[j] != null) {
          paramArrayOfBoolean2[j] = true;
        }
      }
      l = 0L;
      break label482;
    }
  }
  
  public void setIsTimestampMaster(boolean paramBoolean)
  {
    this.chunkSource.setIsTimestampMaster(paramBoolean);
  }
  
  public void setSampleOffsetUs(long paramLong)
  {
    this.sampleOffsetUs = paramLong;
    SampleQueue[] arrayOfSampleQueue = this.sampleQueues;
    int i = arrayOfSampleQueue.length;
    for (int j = 0; j < i; j++) {
      arrayOfSampleQueue[j].setSampleOffsetUs(paramLong);
    }
  }
  
  public int skipData(int paramInt, long paramLong)
  {
    int i = 0;
    if (isPendingReset()) {
      paramInt = i;
    }
    for (;;)
    {
      return paramInt;
      SampleQueue localSampleQueue = this.sampleQueues[paramInt];
      if ((this.loadingFinished) && (paramLong > localSampleQueue.getLargestQueuedTimestampUs()))
      {
        paramInt = localSampleQueue.advanceToEnd();
      }
      else
      {
        i = localSampleQueue.advanceTo(paramLong, true, true);
        paramInt = i;
        if (i == -1) {
          paramInt = 0;
        }
      }
    }
  }
  
  public TrackOutput track(int paramInt1, int paramInt2)
  {
    int i = this.sampleQueues.length;
    if (paramInt2 == 1) {
      if (this.audioSampleQueueIndex != -1) {
        if (this.audioSampleQueueMappingDone) {
          if (this.sampleQueueTrackIds[this.audioSampleQueueIndex] == paramInt1) {
            localObject = this.sampleQueues[this.audioSampleQueueIndex];
          }
        }
      }
    }
    for (;;)
    {
      return (TrackOutput)localObject;
      localObject = createDummyTrackOutput(paramInt1, paramInt2);
      continue;
      this.audioSampleQueueMappingDone = true;
      this.sampleQueueTrackIds[this.audioSampleQueueIndex] = paramInt1;
      localObject = this.sampleQueues[this.audioSampleQueueIndex];
      continue;
      if (!this.tracksEnded) {
        break;
      }
      localObject = createDummyTrackOutput(paramInt1, paramInt2);
      continue;
      if (paramInt2 == 2)
      {
        if (this.videoSampleQueueIndex != -1)
        {
          if (this.videoSampleQueueMappingDone)
          {
            if (this.sampleQueueTrackIds[this.videoSampleQueueIndex] == paramInt1) {
              localObject = this.sampleQueues[this.videoSampleQueueIndex];
            } else {
              localObject = createDummyTrackOutput(paramInt1, paramInt2);
            }
          }
          else
          {
            this.videoSampleQueueMappingDone = true;
            this.sampleQueueTrackIds[this.videoSampleQueueIndex] = paramInt1;
            localObject = this.sampleQueues[this.videoSampleQueueIndex];
          }
        }
        else
        {
          if (!this.tracksEnded) {
            break;
          }
          localObject = createDummyTrackOutput(paramInt1, paramInt2);
        }
      }
      else
      {
        for (int j = 0;; j++)
        {
          if (j >= i) {
            break label250;
          }
          if (this.sampleQueueTrackIds[j] == paramInt1)
          {
            localObject = this.sampleQueues[j];
            break;
          }
        }
        label250:
        if (!this.tracksEnded) {
          break;
        }
        localObject = createDummyTrackOutput(paramInt1, paramInt2);
      }
    }
    Object localObject = new SampleQueue(this.allocator);
    ((SampleQueue)localObject).setSampleOffsetUs(this.sampleOffsetUs);
    ((SampleQueue)localObject).setUpstreamFormatChangeListener(this);
    this.sampleQueueTrackIds = Arrays.copyOf(this.sampleQueueTrackIds, i + 1);
    this.sampleQueueTrackIds[i] = paramInt1;
    this.sampleQueues = ((SampleQueue[])Arrays.copyOf(this.sampleQueues, i + 1));
    this.sampleQueues[i] = localObject;
    this.sampleQueueIsAudioVideoFlags = Arrays.copyOf(this.sampleQueueIsAudioVideoFlags, i + 1);
    boolean[] arrayOfBoolean = this.sampleQueueIsAudioVideoFlags;
    int k;
    if ((paramInt2 == 1) || (paramInt2 == 2))
    {
      k = 1;
      label374:
      arrayOfBoolean[i] = k;
      this.haveAudioVideoSampleQueues |= this.sampleQueueIsAudioVideoFlags[i];
      if (paramInt2 != 1) {
        break label433;
      }
      this.audioSampleQueueMappingDone = true;
      this.audioSampleQueueIndex = i;
    }
    for (;;)
    {
      this.sampleQueuesEnabledStates = Arrays.copyOf(this.sampleQueuesEnabledStates, i + 1);
      break;
      k = 0;
      break label374;
      label433:
      if (paramInt2 == 2)
      {
        this.videoSampleQueueMappingDone = true;
        this.videoSampleQueueIndex = i;
      }
    }
  }
  
  public void unbindSampleQueue(int paramInt)
  {
    paramInt = this.trackGroupToSampleQueueIndex[paramInt];
    Assertions.checkState(this.sampleQueuesEnabledStates[paramInt]);
    this.sampleQueuesEnabledStates[paramInt] = false;
  }
  
  public static abstract interface Callback
    extends SequenceableLoader.Callback<HlsSampleStreamWrapper>
  {
    public abstract void onPlaylistRefreshRequired(HlsMasterPlaylist.HlsUrl paramHlsUrl);
    
    public abstract void onPrepared();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/hls/HlsSampleStreamWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */