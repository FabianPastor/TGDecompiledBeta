package org.telegram.messenger.exoplayer.dash;

import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.telegram.messenger.exoplayer.BehindLiveWindowException;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.TimeRange;
import org.telegram.messenger.exoplayer.TimeRange.DynamicTimeRange;
import org.telegram.messenger.exoplayer.TimeRange.StaticTimeRange;
import org.telegram.messenger.exoplayer.chunk.Chunk;
import org.telegram.messenger.exoplayer.chunk.ChunkExtractorWrapper;
import org.telegram.messenger.exoplayer.chunk.ChunkOperationHolder;
import org.telegram.messenger.exoplayer.chunk.ChunkSource;
import org.telegram.messenger.exoplayer.chunk.ContainerMediaChunk;
import org.telegram.messenger.exoplayer.chunk.Format;
import org.telegram.messenger.exoplayer.chunk.Format.DecreasingBandwidthComparator;
import org.telegram.messenger.exoplayer.chunk.FormatEvaluator;
import org.telegram.messenger.exoplayer.chunk.FormatEvaluator.Evaluation;
import org.telegram.messenger.exoplayer.chunk.InitializationChunk;
import org.telegram.messenger.exoplayer.chunk.MediaChunk;
import org.telegram.messenger.exoplayer.chunk.SingleSampleMediaChunk;
import org.telegram.messenger.exoplayer.dash.mpd.AdaptationSet;
import org.telegram.messenger.exoplayer.dash.mpd.ContentProtection;
import org.telegram.messenger.exoplayer.dash.mpd.MediaPresentationDescription;
import org.telegram.messenger.exoplayer.dash.mpd.Period;
import org.telegram.messenger.exoplayer.dash.mpd.RangedUri;
import org.telegram.messenger.exoplayer.dash.mpd.Representation;
import org.telegram.messenger.exoplayer.drm.DrmInitData;
import org.telegram.messenger.exoplayer.drm.DrmInitData.Mapped;
import org.telegram.messenger.exoplayer.extractor.ChunkIndex;
import org.telegram.messenger.exoplayer.extractor.Extractor;
import org.telegram.messenger.exoplayer.extractor.mp4.FragmentedMp4Extractor;
import org.telegram.messenger.exoplayer.extractor.webm.WebmExtractor;
import org.telegram.messenger.exoplayer.upstream.DataSource;
import org.telegram.messenger.exoplayer.upstream.DataSpec;
import org.telegram.messenger.exoplayer.util.Clock;
import org.telegram.messenger.exoplayer.util.ManifestFetcher;
import org.telegram.messenger.exoplayer.util.MimeTypes;

public class DashChunkSource
  implements ChunkSource, DashTrackSelector.Output
{
  private static final String TAG = "DashChunkSource";
  private final FormatEvaluator adaptiveFormatEvaluator;
  private TimeRange availableRange;
  private final long[] availableRangeValues;
  private MediaPresentationDescription currentManifest;
  private final DataSource dataSource;
  private final long elapsedRealtimeOffsetUs;
  private ExposedTrack enabledTrack;
  private final FormatEvaluator.Evaluation evaluation;
  private final Handler eventHandler;
  private final EventListener eventListener;
  private final int eventSourceId;
  private IOException fatalError;
  private boolean lastChunkWasInitialization;
  private final boolean live;
  private final long liveEdgeLatencyUs;
  private final ManifestFetcher<MediaPresentationDescription> manifestFetcher;
  private int nextPeriodHolderIndex;
  private final SparseArray<PeriodHolder> periodHolders;
  private boolean prepareCalled;
  private MediaPresentationDescription processedManifest;
  private boolean startAtLiveEdge;
  private final Clock systemClock;
  private final DashTrackSelector trackSelector;
  private final ArrayList<ExposedTrack> tracks;
  
  public DashChunkSource(DashTrackSelector paramDashTrackSelector, DataSource paramDataSource, FormatEvaluator paramFormatEvaluator, long paramLong, int paramInt, List<Representation> paramList)
  {
    this(buildManifest(paramLong, paramInt, paramList), paramDashTrackSelector, paramDataSource, paramFormatEvaluator);
  }
  
  public DashChunkSource(DashTrackSelector paramDashTrackSelector, DataSource paramDataSource, FormatEvaluator paramFormatEvaluator, long paramLong, int paramInt, Representation... paramVarArgs)
  {
    this(paramDashTrackSelector, paramDataSource, paramFormatEvaluator, paramLong, paramInt, Arrays.asList(paramVarArgs));
  }
  
  public DashChunkSource(MediaPresentationDescription paramMediaPresentationDescription, DashTrackSelector paramDashTrackSelector, DataSource paramDataSource, FormatEvaluator paramFormatEvaluator)
  {
    this(null, paramMediaPresentationDescription, paramDashTrackSelector, paramDataSource, paramFormatEvaluator, new org.telegram.messenger.exoplayer.util.SystemClock(), 0L, 0L, false, null, null, 0);
  }
  
  public DashChunkSource(ManifestFetcher<MediaPresentationDescription> paramManifestFetcher, DashTrackSelector paramDashTrackSelector, DataSource paramDataSource, FormatEvaluator paramFormatEvaluator, long paramLong1, long paramLong2, Handler paramHandler, EventListener paramEventListener, int paramInt)
  {
    this(paramManifestFetcher, (MediaPresentationDescription)paramManifestFetcher.getManifest(), paramDashTrackSelector, paramDataSource, paramFormatEvaluator, new org.telegram.messenger.exoplayer.util.SystemClock(), paramLong1 * 1000L, paramLong2 * 1000L, true, paramHandler, paramEventListener, paramInt);
  }
  
  public DashChunkSource(ManifestFetcher<MediaPresentationDescription> paramManifestFetcher, DashTrackSelector paramDashTrackSelector, DataSource paramDataSource, FormatEvaluator paramFormatEvaluator, long paramLong1, long paramLong2, boolean paramBoolean, Handler paramHandler, EventListener paramEventListener, int paramInt)
  {
    this(paramManifestFetcher, (MediaPresentationDescription)paramManifestFetcher.getManifest(), paramDashTrackSelector, paramDataSource, paramFormatEvaluator, new org.telegram.messenger.exoplayer.util.SystemClock(), paramLong1 * 1000L, paramLong2 * 1000L, paramBoolean, paramHandler, paramEventListener, paramInt);
  }
  
  DashChunkSource(ManifestFetcher<MediaPresentationDescription> paramManifestFetcher, MediaPresentationDescription paramMediaPresentationDescription, DashTrackSelector paramDashTrackSelector, DataSource paramDataSource, FormatEvaluator paramFormatEvaluator, Clock paramClock, long paramLong1, long paramLong2, boolean paramBoolean, Handler paramHandler, EventListener paramEventListener, int paramInt)
  {
    this.manifestFetcher = paramManifestFetcher;
    this.currentManifest = paramMediaPresentationDescription;
    this.trackSelector = paramDashTrackSelector;
    this.dataSource = paramDataSource;
    this.adaptiveFormatEvaluator = paramFormatEvaluator;
    this.systemClock = paramClock;
    this.liveEdgeLatencyUs = paramLong1;
    this.elapsedRealtimeOffsetUs = paramLong2;
    this.startAtLiveEdge = paramBoolean;
    this.eventHandler = paramHandler;
    this.eventListener = paramEventListener;
    this.eventSourceId = paramInt;
    this.evaluation = new FormatEvaluator.Evaluation();
    this.availableRangeValues = new long[2];
    this.periodHolders = new SparseArray();
    this.tracks = new ArrayList();
    this.live = paramMediaPresentationDescription.dynamic;
  }
  
  private static MediaPresentationDescription buildManifest(long paramLong, int paramInt, List<Representation> paramList)
  {
    return new MediaPresentationDescription(-1L, paramLong, -1L, false, -1L, -1L, null, null, Collections.singletonList(new Period(null, 0L, Collections.singletonList(new AdaptationSet(0, paramInt, paramList)))));
  }
  
  private PeriodHolder findPeriodHolder(long paramLong)
  {
    if (paramLong < ((PeriodHolder)this.periodHolders.valueAt(0)).getAvailableStartTimeUs()) {
      return (PeriodHolder)this.periodHolders.valueAt(0);
    }
    int i = 0;
    while (i < this.periodHolders.size() - 1)
    {
      PeriodHolder localPeriodHolder = (PeriodHolder)this.periodHolders.valueAt(i);
      if (paramLong < localPeriodHolder.getAvailableEndTimeUs()) {
        return localPeriodHolder;
      }
      i += 1;
    }
    return (PeriodHolder)this.periodHolders.valueAt(this.periodHolders.size() - 1);
  }
  
  private TimeRange getAvailableRange(long paramLong)
  {
    PeriodHolder localPeriodHolder1 = (PeriodHolder)this.periodHolders.valueAt(0);
    PeriodHolder localPeriodHolder2 = (PeriodHolder)this.periodHolders.valueAt(this.periodHolders.size() - 1);
    if ((!this.currentManifest.dynamic) || (localPeriodHolder2.isIndexExplicit())) {
      return new TimeRange.StaticTimeRange(localPeriodHolder1.getAvailableStartTimeUs(), localPeriodHolder2.getAvailableEndTimeUs());
    }
    long l3 = localPeriodHolder1.getAvailableStartTimeUs();
    long l1;
    long l4;
    long l5;
    if (localPeriodHolder2.isIndexUnbounded())
    {
      l1 = Long.MAX_VALUE;
      l4 = this.systemClock.elapsedRealtime();
      l5 = this.currentManifest.availabilityStartTime;
      if (this.currentManifest.timeShiftBufferDepth != -1L) {
        break label169;
      }
    }
    label169:
    for (long l2 = -1L;; l2 = this.currentManifest.timeShiftBufferDepth * 1000L)
    {
      return new TimeRange.DynamicTimeRange(l3, l1, l4 * 1000L - (paramLong - l5 * 1000L), l2, this.systemClock);
      l1 = localPeriodHolder2.getAvailableEndTimeUs();
      break;
    }
  }
  
  private static String getMediaMimeType(Format paramFormat)
  {
    String str2 = paramFormat.mimeType;
    String str1;
    if (MimeTypes.isAudio(str2)) {
      str1 = MimeTypes.getAudioMediaMimeType(paramFormat.codecs);
    }
    do
    {
      return str1;
      if (MimeTypes.isVideo(str2)) {
        return MimeTypes.getVideoMediaMimeType(paramFormat.codecs);
      }
      str1 = str2;
    } while (mimeTypeIsRawText(str2));
    if ("application/mp4".equals(str2))
    {
      if ("stpp".equals(paramFormat.codecs)) {
        return "application/ttml+xml";
      }
      if ("wvtt".equals(paramFormat.codecs)) {
        return "application/x-mp4vtt";
      }
    }
    return null;
  }
  
  private long getNowUnixTimeUs()
  {
    if (this.elapsedRealtimeOffsetUs != 0L) {
      return this.systemClock.elapsedRealtime() * 1000L + this.elapsedRealtimeOffsetUs;
    }
    return System.currentTimeMillis() * 1000L;
  }
  
  private static MediaFormat getTrackFormat(int paramInt, Format paramFormat, String paramString, long paramLong)
  {
    switch (paramInt)
    {
    default: 
      return null;
    case 0: 
      return MediaFormat.createVideoFormat(paramFormat.id, paramString, paramFormat.bitrate, -1, paramLong, paramFormat.width, paramFormat.height, null);
    case 1: 
      return MediaFormat.createAudioFormat(paramFormat.id, paramString, paramFormat.bitrate, -1, paramLong, paramFormat.audioChannels, paramFormat.audioSamplingRate, null, paramFormat.language);
    }
    return MediaFormat.createTextFormat(paramFormat.id, paramString, paramFormat.bitrate, paramLong, paramFormat.language);
  }
  
  static boolean mimeTypeIsRawText(String paramString)
  {
    return ("text/vtt".equals(paramString)) || ("application/ttml+xml".equals(paramString));
  }
  
  static boolean mimeTypeIsWebm(String paramString)
  {
    return (paramString.startsWith("video/webm")) || (paramString.startsWith("audio/webm")) || (paramString.startsWith("application/webm"));
  }
  
  private Chunk newInitializationChunk(RangedUri paramRangedUri1, RangedUri paramRangedUri2, Representation paramRepresentation, ChunkExtractorWrapper paramChunkExtractorWrapper, DataSource paramDataSource, int paramInt1, int paramInt2)
  {
    if (paramRangedUri1 != null)
    {
      RangedUri localRangedUri = paramRangedUri1.attemptMerge(paramRangedUri2);
      paramRangedUri2 = localRangedUri;
      if (localRangedUri == null) {
        paramRangedUri2 = paramRangedUri1;
      }
    }
    for (;;)
    {
      return new InitializationChunk(paramDataSource, new DataSpec(paramRangedUri2.getUri(), paramRangedUri2.start, paramRangedUri2.length, paramRepresentation.getCacheKey()), paramInt2, paramRepresentation.format, paramChunkExtractorWrapper, paramInt1);
    }
  }
  
  private void notifyAvailableRangeChanged(final TimeRange paramTimeRange)
  {
    if ((this.eventHandler != null) && (this.eventListener != null)) {
      this.eventHandler.post(new Runnable()
      {
        public void run()
        {
          DashChunkSource.this.eventListener.onAvailableRangeChanged(DashChunkSource.this.eventSourceId, paramTimeRange);
        }
      });
    }
  }
  
  private void processManifest(MediaPresentationDescription paramMediaPresentationDescription)
  {
    Object localObject = paramMediaPresentationDescription.getPeriod(0);
    while ((this.periodHolders.size() > 0) && (((PeriodHolder)this.periodHolders.valueAt(0)).startTimeUs < ((Period)localObject).startMs * 1000L))
    {
      PeriodHolder localPeriodHolder = (PeriodHolder)this.periodHolders.valueAt(0);
      this.periodHolders.remove(localPeriodHolder.localIndex);
    }
    if (this.periodHolders.size() > paramMediaPresentationDescription.getPeriodCount()) {
      return;
    }
    try
    {
      int i = this.periodHolders.size();
      if (i > 0)
      {
        ((PeriodHolder)this.periodHolders.valueAt(0)).updatePeriod(paramMediaPresentationDescription, 0, this.enabledTrack);
        if (i > 1)
        {
          i -= 1;
          ((PeriodHolder)this.periodHolders.valueAt(i)).updatePeriod(paramMediaPresentationDescription, i, this.enabledTrack);
        }
      }
      i = this.periodHolders.size();
      while (i < paramMediaPresentationDescription.getPeriodCount())
      {
        localObject = new PeriodHolder(this.nextPeriodHolderIndex, paramMediaPresentationDescription, i, this.enabledTrack);
        this.periodHolders.put(this.nextPeriodHolderIndex, localObject);
        this.nextPeriodHolderIndex += 1;
        i += 1;
      }
      localObject = getAvailableRange(getNowUnixTimeUs());
    }
    catch (BehindLiveWindowException paramMediaPresentationDescription)
    {
      this.fatalError = paramMediaPresentationDescription;
      return;
    }
    if ((this.availableRange == null) || (!this.availableRange.equals(localObject)))
    {
      this.availableRange = ((TimeRange)localObject);
      notifyAvailableRangeChanged(this.availableRange);
    }
    this.currentManifest = paramMediaPresentationDescription;
  }
  
  public void adaptiveTrack(MediaPresentationDescription paramMediaPresentationDescription, int paramInt1, int paramInt2, int[] paramArrayOfInt)
  {
    if (this.adaptiveFormatEvaluator == null)
    {
      Log.w("DashChunkSource", "Skipping adaptive track (missing format evaluator)");
      return;
    }
    AdaptationSet localAdaptationSet = (AdaptationSet)paramMediaPresentationDescription.getPeriod(paramInt1).adaptationSets.get(paramInt2);
    int j = 0;
    int i = 0;
    Object localObject = null;
    Format[] arrayOfFormat = new Format[paramArrayOfInt.length];
    paramInt1 = 0;
    while (paramInt1 < arrayOfFormat.length)
    {
      Format localFormat = ((Representation)localAdaptationSet.representations.get(paramArrayOfInt[paramInt1])).format;
      if ((localObject == null) || (localFormat.height > i)) {
        localObject = localFormat;
      }
      j = Math.max(j, localFormat.width);
      i = Math.max(i, localFormat.height);
      arrayOfFormat[paramInt1] = localFormat;
      paramInt1 += 1;
    }
    Arrays.sort(arrayOfFormat, new Format.DecreasingBandwidthComparator());
    if (this.live) {}
    for (long l = -1L;; l = paramMediaPresentationDescription.duration * 1000L)
    {
      paramMediaPresentationDescription = getMediaMimeType((Format)localObject);
      if (paramMediaPresentationDescription != null) {
        break;
      }
      Log.w("DashChunkSource", "Skipped adaptive track (unknown media mime type)");
      return;
    }
    paramMediaPresentationDescription = getTrackFormat(localAdaptationSet.type, (Format)localObject, paramMediaPresentationDescription, l);
    if (paramMediaPresentationDescription == null)
    {
      Log.w("DashChunkSource", "Skipped adaptive track (unknown media format)");
      return;
    }
    this.tracks.add(new ExposedTrack(paramMediaPresentationDescription.copyAsAdaptive(null), paramInt2, arrayOfFormat, j, i));
  }
  
  public void continueBuffering(long paramLong)
  {
    if ((this.manifestFetcher == null) || (!this.currentManifest.dynamic) || (this.fatalError != null)) {}
    do
    {
      return;
      MediaPresentationDescription localMediaPresentationDescription = (MediaPresentationDescription)this.manifestFetcher.getManifest();
      if ((localMediaPresentationDescription != null) && (localMediaPresentationDescription != this.processedManifest))
      {
        processManifest(localMediaPresentationDescription);
        this.processedManifest = localMediaPresentationDescription;
      }
      long l = this.currentManifest.minUpdatePeriod;
      paramLong = l;
      if (l == 0L) {
        paramLong = 5000L;
      }
    } while (android.os.SystemClock.elapsedRealtime() <= this.manifestFetcher.getManifestLoadStartTimestamp() + paramLong);
    this.manifestFetcher.requestRefresh();
  }
  
  public void disable(List<? extends MediaChunk> paramList)
  {
    if (this.enabledTrack.isAdaptive()) {
      this.adaptiveFormatEvaluator.disable();
    }
    if (this.manifestFetcher != null) {
      this.manifestFetcher.disable();
    }
    this.periodHolders.clear();
    this.evaluation.format = null;
    this.availableRange = null;
    this.fatalError = null;
    this.enabledTrack = null;
  }
  
  public void enable(int paramInt)
  {
    this.enabledTrack = ((ExposedTrack)this.tracks.get(paramInt));
    if (this.enabledTrack.isAdaptive()) {
      this.adaptiveFormatEvaluator.enable();
    }
    if (this.manifestFetcher != null)
    {
      this.manifestFetcher.enable();
      processManifest((MediaPresentationDescription)this.manifestFetcher.getManifest());
      return;
    }
    processManifest(this.currentManifest);
  }
  
  public void fixedTrack(MediaPresentationDescription paramMediaPresentationDescription, int paramInt1, int paramInt2, int paramInt3)
  {
    AdaptationSet localAdaptationSet = (AdaptationSet)paramMediaPresentationDescription.getPeriod(paramInt1).adaptationSets.get(paramInt2);
    Format localFormat = ((Representation)localAdaptationSet.representations.get(paramInt3)).format;
    String str = getMediaMimeType(localFormat);
    if (str == null)
    {
      Log.w("DashChunkSource", "Skipped track " + localFormat.id + " (unknown media mime type)");
      return;
    }
    paramInt1 = localAdaptationSet.type;
    if (paramMediaPresentationDescription.dynamic) {}
    for (long l = -1L;; l = paramMediaPresentationDescription.duration * 1000L)
    {
      paramMediaPresentationDescription = getTrackFormat(paramInt1, localFormat, str, l);
      if (paramMediaPresentationDescription != null) {
        break;
      }
      Log.w("DashChunkSource", "Skipped track " + localFormat.id + " (unknown media format)");
      return;
    }
    this.tracks.add(new ExposedTrack(paramMediaPresentationDescription, paramInt2, localFormat));
  }
  
  TimeRange getAvailableRange()
  {
    return this.availableRange;
  }
  
  public final void getChunkOperation(List<? extends MediaChunk> paramList, long paramLong, ChunkOperationHolder paramChunkOperationHolder)
  {
    if (this.fatalError != null) {
      paramChunkOperationHolder.chunk = null;
    }
    Object localObject3;
    label109:
    do
    {
      return;
      this.evaluation.queueSize = paramList.size();
      if ((this.evaluation.format == null) || (!this.lastChunkWasInitialization))
      {
        if (!this.enabledTrack.isAdaptive()) {
          break label109;
        }
        this.adaptiveFormatEvaluator.evaluate(paramList, paramLong, this.enabledTrack.adaptiveFormats, this.evaluation);
      }
      for (;;)
      {
        localObject3 = this.evaluation.format;
        paramChunkOperationHolder.queueSize = this.evaluation.queueSize;
        if (localObject3 != null) {
          break;
        }
        paramChunkOperationHolder.chunk = null;
        return;
        this.evaluation.format = this.enabledTrack.fixedFormat;
        this.evaluation.trigger = 2;
      }
    } while ((paramChunkOperationHolder.queueSize == paramList.size()) && (paramChunkOperationHolder.chunk != null) && (paramChunkOperationHolder.chunk.format.equals(localObject3)));
    paramChunkOperationHolder.chunk = null;
    this.availableRange.getCurrentBoundsUs(this.availableRangeValues);
    long l;
    label251:
    Object localObject1;
    int i;
    if (paramList.isEmpty())
    {
      l = paramLong;
      if (this.live)
      {
        if (paramLong != 0L) {
          this.startAtLiveEdge = false;
        }
        if (this.startAtLiveEdge) {
          l = Math.max(this.availableRangeValues[0], this.availableRangeValues[1] - this.liveEdgeLatencyUs);
        }
      }
      else
      {
        localObject1 = findPeriodHolder(l);
        i = 1;
      }
    }
    Object localObject4;
    MediaFormat localMediaFormat;
    for (;;)
    {
      localObject4 = (RepresentationHolder)((PeriodHolder)localObject1).representationHolders.get(((Format)localObject3).id);
      Representation localRepresentation = ((RepresentationHolder)localObject4).representation;
      Object localObject2 = null;
      localObject3 = null;
      localMediaFormat = ((RepresentationHolder)localObject4).mediaFormat;
      if (localMediaFormat == null) {
        localObject2 = localRepresentation.getInitializationUri();
      }
      if (((RepresentationHolder)localObject4).segmentIndex == null) {
        localObject3 = localRepresentation.getIndexUri();
      }
      if ((localObject2 == null) && (localObject3 == null)) {
        break label713;
      }
      paramList = newInitializationChunk((RangedUri)localObject2, (RangedUri)localObject3, localRepresentation, ((RepresentationHolder)localObject4).extractorWrapper, this.dataSource, ((PeriodHolder)localObject1).localIndex, this.evaluation.trigger);
      this.lastChunkWasInitialization = true;
      paramChunkOperationHolder.chunk = paramList;
      return;
      l = Math.max(Math.min(paramLong, this.availableRangeValues[1] - 1L), this.availableRangeValues[0]);
      break label251;
      if (this.startAtLiveEdge) {
        this.startAtLiveEdge = false;
      }
      localObject4 = (MediaChunk)paramList.get(paramChunkOperationHolder.queueSize - 1);
      l = ((MediaChunk)localObject4).endTimeUs;
      if ((this.live) && (l < this.availableRangeValues[0]))
      {
        this.fatalError = new BehindLiveWindowException();
        return;
      }
      if ((this.currentManifest.dynamic) && (l >= this.availableRangeValues[1])) {
        break;
      }
      localObject1 = (PeriodHolder)this.periodHolders.valueAt(this.periodHolders.size() - 1);
      if ((((MediaChunk)localObject4).parentId == ((PeriodHolder)localObject1).localIndex) && (((RepresentationHolder)((PeriodHolder)localObject1).representationHolders.get(((MediaChunk)localObject4).format.id)).isBeyondLastSegment(((MediaChunk)localObject4).getNextChunkIndex())))
      {
        if (this.currentManifest.dynamic) {
          break;
        }
        paramChunkOperationHolder.endOfStream = true;
        return;
      }
      int j = 0;
      localObject2 = (PeriodHolder)this.periodHolders.get(((MediaChunk)localObject4).parentId);
      if (localObject2 == null)
      {
        localObject1 = (PeriodHolder)this.periodHolders.valueAt(0);
        i = 1;
        l = paramLong;
      }
      else
      {
        localObject1 = localObject2;
        i = j;
        l = paramLong;
        if (!((PeriodHolder)localObject2).isIndexUnbounded())
        {
          localObject1 = localObject2;
          i = j;
          l = paramLong;
          if (((RepresentationHolder)((PeriodHolder)localObject2).representationHolders.get(((MediaChunk)localObject4).format.id)).isBeyondLastSegment(((MediaChunk)localObject4).getNextChunkIndex()))
          {
            localObject1 = (PeriodHolder)this.periodHolders.get(((MediaChunk)localObject4).parentId + 1);
            i = 1;
            l = paramLong;
          }
        }
      }
    }
    label713:
    if (paramList.isEmpty()) {
      i = ((RepresentationHolder)localObject4).getSegmentNum(l);
    }
    for (;;)
    {
      paramList = newMediaChunk((PeriodHolder)localObject1, (RepresentationHolder)localObject4, this.dataSource, localMediaFormat, this.enabledTrack, i, this.evaluation.trigger);
      this.lastChunkWasInitialization = false;
      paramChunkOperationHolder.chunk = paramList;
      return;
      if (i != 0) {
        i = ((RepresentationHolder)localObject4).getFirstAvailableSegmentNum();
      } else {
        i = ((MediaChunk)paramList.get(paramChunkOperationHolder.queueSize - 1)).getNextChunkIndex();
      }
    }
  }
  
  public final MediaFormat getFormat(int paramInt)
  {
    return ((ExposedTrack)this.tracks.get(paramInt)).trackFormat;
  }
  
  public int getTrackCount()
  {
    return this.tracks.size();
  }
  
  public void maybeThrowError()
    throws IOException
  {
    if (this.fatalError != null) {
      throw this.fatalError;
    }
    if (this.manifestFetcher != null) {
      this.manifestFetcher.maybeThrowError();
    }
  }
  
  protected Chunk newMediaChunk(PeriodHolder paramPeriodHolder, RepresentationHolder paramRepresentationHolder, DataSource paramDataSource, MediaFormat paramMediaFormat, ExposedTrack paramExposedTrack, int paramInt1, int paramInt2)
  {
    Representation localRepresentation = paramRepresentationHolder.representation;
    Format localFormat = localRepresentation.format;
    long l1 = paramRepresentationHolder.getSegmentStartTimeUs(paramInt1);
    long l2 = paramRepresentationHolder.getSegmentEndTimeUs(paramInt1);
    Object localObject = paramRepresentationHolder.getSegmentUrl(paramInt1);
    localObject = new DataSpec(((RangedUri)localObject).getUri(), ((RangedUri)localObject).start, ((RangedUri)localObject).length, localRepresentation.getCacheKey());
    long l3 = paramPeriodHolder.startTimeUs;
    long l4 = localRepresentation.presentationTimeOffsetUs;
    if (mimeTypeIsRawText(localFormat.mimeType)) {
      return new SingleSampleMediaChunk(paramDataSource, (DataSpec)localObject, 1, localFormat, l1, l2, paramInt1, paramExposedTrack.trackFormat, null, paramPeriodHolder.localIndex);
    }
    if (paramMediaFormat != null) {}
    for (boolean bool = true;; bool = false) {
      return new ContainerMediaChunk(paramDataSource, (DataSpec)localObject, paramInt2, localFormat, l1, l2, paramInt1, l3 - l4, paramRepresentationHolder.extractorWrapper, paramMediaFormat, paramExposedTrack.adaptiveMaxWidth, paramExposedTrack.adaptiveMaxHeight, paramPeriodHolder.drmInitData, bool, paramPeriodHolder.localIndex);
    }
  }
  
  public void onChunkLoadCompleted(Chunk paramChunk)
  {
    Object localObject;
    PeriodHolder localPeriodHolder;
    if ((paramChunk instanceof InitializationChunk))
    {
      paramChunk = (InitializationChunk)paramChunk;
      localObject = paramChunk.format.id;
      localPeriodHolder = (PeriodHolder)this.periodHolders.get(paramChunk.parentId);
      if (localPeriodHolder != null) {
        break label40;
      }
    }
    label40:
    do
    {
      return;
      localObject = (RepresentationHolder)localPeriodHolder.representationHolders.get(localObject);
      if (paramChunk.hasFormat()) {
        ((RepresentationHolder)localObject).mediaFormat = paramChunk.getFormat();
      }
      if ((((RepresentationHolder)localObject).segmentIndex == null) && (paramChunk.hasSeekMap())) {
        ((RepresentationHolder)localObject).segmentIndex = new DashWrappingSegmentIndex((ChunkIndex)paramChunk.getSeekMap(), paramChunk.dataSpec.uri.toString());
      }
    } while ((localPeriodHolder.drmInitData != null) || (!paramChunk.hasDrmInitData()));
    PeriodHolder.access$202(localPeriodHolder, paramChunk.getDrmInitData());
  }
  
  public void onChunkLoadError(Chunk paramChunk, Exception paramException) {}
  
  public boolean prepare()
  {
    if (!this.prepareCalled) {
      this.prepareCalled = true;
    }
    try
    {
      this.trackSelector.selectTracks(this.currentManifest, 0, this);
      if (this.fatalError == null) {
        return true;
      }
    }
    catch (IOException localIOException)
    {
      for (;;)
      {
        this.fatalError = localIOException;
      }
    }
    return false;
  }
  
  public static abstract interface EventListener
  {
    public abstract void onAvailableRangeChanged(int paramInt, TimeRange paramTimeRange);
  }
  
  protected static final class ExposedTrack
  {
    private final int adaptationSetIndex;
    private final Format[] adaptiveFormats;
    public final int adaptiveMaxHeight;
    public final int adaptiveMaxWidth;
    private final Format fixedFormat;
    public final MediaFormat trackFormat;
    
    public ExposedTrack(MediaFormat paramMediaFormat, int paramInt, Format paramFormat)
    {
      this.trackFormat = paramMediaFormat;
      this.adaptationSetIndex = paramInt;
      this.fixedFormat = paramFormat;
      this.adaptiveFormats = null;
      this.adaptiveMaxWidth = -1;
      this.adaptiveMaxHeight = -1;
    }
    
    public ExposedTrack(MediaFormat paramMediaFormat, int paramInt1, Format[] paramArrayOfFormat, int paramInt2, int paramInt3)
    {
      this.trackFormat = paramMediaFormat;
      this.adaptationSetIndex = paramInt1;
      this.adaptiveFormats = paramArrayOfFormat;
      this.adaptiveMaxWidth = paramInt2;
      this.adaptiveMaxHeight = paramInt3;
      this.fixedFormat = null;
    }
    
    public boolean isAdaptive()
    {
      return this.adaptiveFormats != null;
    }
  }
  
  public static class NoAdaptationSetException
    extends IOException
  {
    public NoAdaptationSetException(String paramString)
    {
      super();
    }
  }
  
  protected static final class PeriodHolder
  {
    private long availableEndTimeUs;
    private long availableStartTimeUs;
    private DrmInitData drmInitData;
    private boolean indexIsExplicit;
    private boolean indexIsUnbounded;
    public final int localIndex;
    public final HashMap<String, DashChunkSource.RepresentationHolder> representationHolders;
    private final int[] representationIndices;
    public final long startTimeUs;
    
    public PeriodHolder(int paramInt1, MediaPresentationDescription paramMediaPresentationDescription, int paramInt2, DashChunkSource.ExposedTrack paramExposedTrack)
    {
      this.localIndex = paramInt1;
      Object localObject = paramMediaPresentationDescription.getPeriod(paramInt2);
      long l = getPeriodDurationUs(paramMediaPresentationDescription, paramInt2);
      AdaptationSet localAdaptationSet = (AdaptationSet)((Period)localObject).adaptationSets.get(paramExposedTrack.adaptationSetIndex);
      paramMediaPresentationDescription = localAdaptationSet.representations;
      this.startTimeUs = (((Period)localObject).startMs * 1000L);
      this.drmInitData = getDrmInitData(localAdaptationSet);
      if (!paramExposedTrack.isAdaptive()) {
        this.representationIndices = new int[] { getRepresentationIndex(paramMediaPresentationDescription, paramExposedTrack.fixedFormat.id) };
      }
      for (;;)
      {
        this.representationHolders = new HashMap();
        paramInt1 = 0;
        while (paramInt1 < this.representationIndices.length)
        {
          paramExposedTrack = (Representation)paramMediaPresentationDescription.get(this.representationIndices[paramInt1]);
          localObject = new DashChunkSource.RepresentationHolder(this.startTimeUs, l, paramExposedTrack);
          this.representationHolders.put(paramExposedTrack.format.id, localObject);
          paramInt1 += 1;
        }
        this.representationIndices = new int[paramExposedTrack.adaptiveFormats.length];
        paramInt1 = 0;
        while (paramInt1 < paramExposedTrack.adaptiveFormats.length)
        {
          this.representationIndices[paramInt1] = getRepresentationIndex(paramMediaPresentationDescription, paramExposedTrack.adaptiveFormats[paramInt1].id);
          paramInt1 += 1;
        }
      }
      updateRepresentationIndependentProperties(l, (Representation)paramMediaPresentationDescription.get(this.representationIndices[0]));
    }
    
    private static DrmInitData getDrmInitData(AdaptationSet paramAdaptationSet)
    {
      Object localObject2;
      if (paramAdaptationSet.contentProtections.isEmpty())
      {
        localObject2 = null;
        return (DrmInitData)localObject2;
      }
      Object localObject1 = null;
      int i = 0;
      for (;;)
      {
        localObject2 = localObject1;
        if (i >= paramAdaptationSet.contentProtections.size()) {
          break;
        }
        ContentProtection localContentProtection = (ContentProtection)paramAdaptationSet.contentProtections.get(i);
        localObject2 = localObject1;
        if (localContentProtection.uuid != null)
        {
          localObject2 = localObject1;
          if (localContentProtection.data != null)
          {
            localObject2 = localObject1;
            if (localObject1 == null) {
              localObject2 = new DrmInitData.Mapped();
            }
            ((DrmInitData.Mapped)localObject2).put(localContentProtection.uuid, localContentProtection.data);
          }
        }
        i += 1;
        localObject1 = localObject2;
      }
    }
    
    private static long getPeriodDurationUs(MediaPresentationDescription paramMediaPresentationDescription, int paramInt)
    {
      long l = paramMediaPresentationDescription.getPeriodDuration(paramInt);
      if (l == -1L) {
        return -1L;
      }
      return 1000L * l;
    }
    
    private static int getRepresentationIndex(List<Representation> paramList, String paramString)
    {
      int i = 0;
      while (i < paramList.size())
      {
        if (paramString.equals(((Representation)paramList.get(i)).format.id)) {
          return i;
        }
        i += 1;
      }
      throw new IllegalStateException("Missing format id: " + paramString);
    }
    
    private void updateRepresentationIndependentProperties(long paramLong, Representation paramRepresentation)
    {
      boolean bool = true;
      paramRepresentation = paramRepresentation.getIndex();
      if (paramRepresentation != null)
      {
        int i = paramRepresentation.getFirstSegmentNum();
        int j = paramRepresentation.getLastSegmentNum(paramLong);
        if (j == -1) {}
        for (;;)
        {
          this.indexIsUnbounded = bool;
          this.indexIsExplicit = paramRepresentation.isExplicit();
          this.availableStartTimeUs = (this.startTimeUs + paramRepresentation.getTimeUs(i));
          if (!this.indexIsUnbounded) {
            this.availableEndTimeUs = (this.startTimeUs + paramRepresentation.getTimeUs(j) + paramRepresentation.getDurationUs(j, paramLong));
          }
          return;
          bool = false;
        }
      }
      this.indexIsUnbounded = false;
      this.indexIsExplicit = true;
      this.availableStartTimeUs = this.startTimeUs;
      this.availableEndTimeUs = (this.startTimeUs + paramLong);
    }
    
    public long getAvailableEndTimeUs()
    {
      if (isIndexUnbounded()) {
        throw new IllegalStateException("Period has unbounded index");
      }
      return this.availableEndTimeUs;
    }
    
    public long getAvailableStartTimeUs()
    {
      return this.availableStartTimeUs;
    }
    
    public DrmInitData getDrmInitData()
    {
      return this.drmInitData;
    }
    
    public boolean isIndexExplicit()
    {
      return this.indexIsExplicit;
    }
    
    public boolean isIndexUnbounded()
    {
      return this.indexIsUnbounded;
    }
    
    public void updatePeriod(MediaPresentationDescription paramMediaPresentationDescription, int paramInt, DashChunkSource.ExposedTrack paramExposedTrack)
      throws BehindLiveWindowException
    {
      Period localPeriod = paramMediaPresentationDescription.getPeriod(paramInt);
      long l = getPeriodDurationUs(paramMediaPresentationDescription, paramInt);
      paramMediaPresentationDescription = ((AdaptationSet)localPeriod.adaptationSets.get(paramExposedTrack.adaptationSetIndex)).representations;
      paramInt = 0;
      while (paramInt < this.representationIndices.length)
      {
        paramExposedTrack = (Representation)paramMediaPresentationDescription.get(this.representationIndices[paramInt]);
        ((DashChunkSource.RepresentationHolder)this.representationHolders.get(paramExposedTrack.format.id)).updateRepresentation(l, paramExposedTrack);
        paramInt += 1;
      }
      updateRepresentationIndependentProperties(l, (Representation)paramMediaPresentationDescription.get(this.representationIndices[0]));
    }
  }
  
  protected static final class RepresentationHolder
  {
    public final ChunkExtractorWrapper extractorWrapper;
    public MediaFormat mediaFormat;
    public final boolean mimeTypeIsRawText;
    private long periodDurationUs;
    private final long periodStartTimeUs;
    public Representation representation;
    public DashSegmentIndex segmentIndex;
    private int segmentNumShift;
    
    public RepresentationHolder(long paramLong1, long paramLong2, Representation paramRepresentation)
    {
      this.periodStartTimeUs = paramLong1;
      this.periodDurationUs = paramLong2;
      this.representation = paramRepresentation;
      Object localObject = paramRepresentation.format.mimeType;
      this.mimeTypeIsRawText = DashChunkSource.mimeTypeIsRawText((String)localObject);
      if (this.mimeTypeIsRawText)
      {
        localObject = null;
        this.extractorWrapper = ((ChunkExtractorWrapper)localObject);
        this.segmentIndex = paramRepresentation.getIndex();
        return;
      }
      if (DashChunkSource.mimeTypeIsWebm((String)localObject)) {}
      for (localObject = new WebmExtractor();; localObject = new FragmentedMp4Extractor())
      {
        localObject = new ChunkExtractorWrapper((Extractor)localObject);
        break;
      }
    }
    
    public int getFirstAvailableSegmentNum()
    {
      return this.segmentIndex.getFirstSegmentNum() + this.segmentNumShift;
    }
    
    public int getLastSegmentNum()
    {
      return this.segmentIndex.getLastSegmentNum(this.periodDurationUs);
    }
    
    public long getSegmentEndTimeUs(int paramInt)
    {
      return getSegmentStartTimeUs(paramInt) + this.segmentIndex.getDurationUs(paramInt - this.segmentNumShift, this.periodDurationUs);
    }
    
    public int getSegmentNum(long paramLong)
    {
      return this.segmentIndex.getSegmentNum(paramLong - this.periodStartTimeUs, this.periodDurationUs) + this.segmentNumShift;
    }
    
    public long getSegmentStartTimeUs(int paramInt)
    {
      return this.segmentIndex.getTimeUs(paramInt - this.segmentNumShift) + this.periodStartTimeUs;
    }
    
    public RangedUri getSegmentUrl(int paramInt)
    {
      return this.segmentIndex.getSegmentUrl(paramInt - this.segmentNumShift);
    }
    
    public boolean isBeyondLastSegment(int paramInt)
    {
      int i = getLastSegmentNum();
      if (i == -1) {}
      while (paramInt <= this.segmentNumShift + i) {
        return false;
      }
      return true;
    }
    
    public void updateRepresentation(long paramLong, Representation paramRepresentation)
      throws BehindLiveWindowException
    {
      DashSegmentIndex localDashSegmentIndex1 = this.representation.getIndex();
      DashSegmentIndex localDashSegmentIndex2 = paramRepresentation.getIndex();
      this.periodDurationUs = paramLong;
      this.representation = paramRepresentation;
      if (localDashSegmentIndex1 == null) {}
      do
      {
        return;
        this.segmentIndex = localDashSegmentIndex2;
      } while (!localDashSegmentIndex1.isExplicit());
      int i = localDashSegmentIndex1.getLastSegmentNum(this.periodDurationUs);
      paramLong = localDashSegmentIndex1.getTimeUs(i) + localDashSegmentIndex1.getDurationUs(i, this.periodDurationUs);
      i = localDashSegmentIndex2.getFirstSegmentNum();
      long l = localDashSegmentIndex2.getTimeUs(i);
      if (paramLong == l)
      {
        this.segmentNumShift += localDashSegmentIndex1.getLastSegmentNum(this.periodDurationUs) + 1 - i;
        return;
      }
      if (paramLong < l) {
        throw new BehindLiveWindowException();
      }
      this.segmentNumShift += localDashSegmentIndex1.getSegmentNum(l, this.periodDurationUs) - i;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/dash/DashChunkSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */