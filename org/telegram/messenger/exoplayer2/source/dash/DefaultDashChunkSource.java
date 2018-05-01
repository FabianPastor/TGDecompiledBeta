package org.telegram.messenger.exoplayer2.source.dash;

import android.os.SystemClock;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.SeekParameters;
import org.telegram.messenger.exoplayer2.extractor.ChunkIndex;
import org.telegram.messenger.exoplayer2.extractor.SeekMap;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.extractor.mkv.MatroskaExtractor;
import org.telegram.messenger.exoplayer2.extractor.mp4.FragmentedMp4Extractor;
import org.telegram.messenger.exoplayer2.extractor.rawcc.RawCcExtractor;
import org.telegram.messenger.exoplayer2.source.BehindLiveWindowException;
import org.telegram.messenger.exoplayer2.source.chunk.Chunk;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkExtractorWrapper;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkHolder;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil;
import org.telegram.messenger.exoplayer2.source.chunk.ContainerMediaChunk;
import org.telegram.messenger.exoplayer2.source.chunk.InitializationChunk;
import org.telegram.messenger.exoplayer2.source.chunk.MediaChunk;
import org.telegram.messenger.exoplayer2.source.chunk.SingleSampleMediaChunk;
import org.telegram.messenger.exoplayer2.source.dash.manifest.AdaptationSet;
import org.telegram.messenger.exoplayer2.source.dash.manifest.DashManifest;
import org.telegram.messenger.exoplayer2.source.dash.manifest.Period;
import org.telegram.messenger.exoplayer2.source.dash.manifest.RangedUri;
import org.telegram.messenger.exoplayer2.source.dash.manifest.Representation;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSource.Factory;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.HttpDataSource.InvalidResponseCodeException;
import org.telegram.messenger.exoplayer2.upstream.LoaderErrorThrower;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.Util;

public class DefaultDashChunkSource
  implements DashChunkSource
{
  private final int[] adaptationSetIndices;
  private final DataSource dataSource;
  private final long elapsedRealtimeOffsetMs;
  private IOException fatalError;
  private long liveEdgeTimeUs;
  private DashManifest manifest;
  private final LoaderErrorThrower manifestLoaderErrorThrower;
  private final int maxSegmentsPerLoad;
  private boolean missingLastSegment;
  private int periodIndex;
  private final PlayerEmsgHandler.PlayerTrackEmsgHandler playerTrackEmsgHandler;
  protected final RepresentationHolder[] representationHolders;
  private final TrackSelection trackSelection;
  private final int trackType;
  
  public DefaultDashChunkSource(LoaderErrorThrower paramLoaderErrorThrower, DashManifest paramDashManifest, int paramInt1, int[] paramArrayOfInt, TrackSelection paramTrackSelection, int paramInt2, DataSource paramDataSource, long paramLong, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, PlayerEmsgHandler.PlayerTrackEmsgHandler paramPlayerTrackEmsgHandler)
  {
    this.manifestLoaderErrorThrower = paramLoaderErrorThrower;
    this.manifest = paramDashManifest;
    this.adaptationSetIndices = paramArrayOfInt;
    this.trackSelection = paramTrackSelection;
    this.trackType = paramInt2;
    this.dataSource = paramDataSource;
    this.periodIndex = paramInt1;
    this.elapsedRealtimeOffsetMs = paramLong;
    this.maxSegmentsPerLoad = paramInt3;
    this.playerTrackEmsgHandler = paramPlayerTrackEmsgHandler;
    paramLong = paramDashManifest.getPeriodDurationUs(paramInt1);
    this.liveEdgeTimeUs = -9223372036854775807L;
    paramDashManifest = getRepresentations();
    this.representationHolders = new RepresentationHolder[paramTrackSelection.length()];
    for (paramInt1 = 0; paramInt1 < this.representationHolders.length; paramInt1++)
    {
      paramLoaderErrorThrower = (Representation)paramDashManifest.get(paramTrackSelection.getIndexInTrackGroup(paramInt1));
      this.representationHolders[paramInt1] = new RepresentationHolder(paramLong, paramInt2, paramLoaderErrorThrower, paramBoolean1, paramBoolean2, paramPlayerTrackEmsgHandler);
    }
  }
  
  private long getNowUnixTimeUs()
  {
    if (this.elapsedRealtimeOffsetMs != 0L) {}
    for (long l = (SystemClock.elapsedRealtime() + this.elapsedRealtimeOffsetMs) * 1000L;; l = System.currentTimeMillis() * 1000L) {
      return l;
    }
  }
  
  private ArrayList<Representation> getRepresentations()
  {
    List localList = this.manifest.getPeriod(this.periodIndex).adaptationSets;
    ArrayList localArrayList = new ArrayList();
    int[] arrayOfInt = this.adaptationSetIndices;
    int i = arrayOfInt.length;
    for (int j = 0; j < i; j++) {
      localArrayList.addAll(((AdaptationSet)localList.get(arrayOfInt[j])).representations);
    }
    return localArrayList;
  }
  
  protected static Chunk newInitializationChunk(RepresentationHolder paramRepresentationHolder, DataSource paramDataSource, Format paramFormat, int paramInt, Object paramObject, RangedUri paramRangedUri1, RangedUri paramRangedUri2)
  {
    String str = paramRepresentationHolder.representation.baseUrl;
    if (paramRangedUri1 != null)
    {
      RangedUri localRangedUri = paramRangedUri1.attemptMerge(paramRangedUri2, str);
      paramRangedUri2 = localRangedUri;
      if (localRangedUri == null) {
        paramRangedUri2 = paramRangedUri1;
      }
    }
    for (;;)
    {
      return new InitializationChunk(paramDataSource, new DataSpec(paramRangedUri2.resolveUri(str), paramRangedUri2.start, paramRangedUri2.length, paramRepresentationHolder.representation.getCacheKey()), paramFormat, paramInt, paramObject, paramRepresentationHolder.extractorWrapper);
    }
  }
  
  protected static Chunk newMediaChunk(RepresentationHolder paramRepresentationHolder, DataSource paramDataSource, int paramInt1, Format paramFormat, int paramInt2, Object paramObject, int paramInt3, int paramInt4)
  {
    Representation localRepresentation = paramRepresentationHolder.representation;
    long l1 = paramRepresentationHolder.getSegmentStartTimeUs(paramInt3);
    Object localObject = paramRepresentationHolder.getSegmentUrl(paramInt3);
    String str = localRepresentation.baseUrl;
    long l2;
    if (paramRepresentationHolder.extractorWrapper == null)
    {
      l2 = paramRepresentationHolder.getSegmentEndTimeUs(paramInt3);
      paramRepresentationHolder = new SingleSampleMediaChunk(paramDataSource, new DataSpec(((RangedUri)localObject).resolveUri(str), ((RangedUri)localObject).start, ((RangedUri)localObject).length, localRepresentation.getCacheKey()), paramFormat, paramInt2, paramObject, l1, l2, paramInt3, paramInt1, paramFormat);
      return paramRepresentationHolder;
    }
    int i = 1;
    for (paramInt1 = 1;; paramInt1++)
    {
      RangedUri localRangedUri;
      if (paramInt1 < paramInt4)
      {
        localRangedUri = ((RangedUri)localObject).attemptMerge(paramRepresentationHolder.getSegmentUrl(paramInt3 + paramInt1), str);
        if (localRangedUri != null) {}
      }
      else
      {
        l2 = paramRepresentationHolder.getSegmentEndTimeUs(paramInt3 + i - 1);
        paramRepresentationHolder = new ContainerMediaChunk(paramDataSource, new DataSpec(((RangedUri)localObject).resolveUri(str), ((RangedUri)localObject).start, ((RangedUri)localObject).length, localRepresentation.getCacheKey()), paramFormat, paramInt2, paramObject, l1, l2, paramInt3, i, -localRepresentation.presentationTimeOffsetUs, paramRepresentationHolder.extractorWrapper);
        break;
      }
      localObject = localRangedUri;
      i++;
    }
  }
  
  private long resolveTimeToLiveEdgeUs(long paramLong)
  {
    long l = -9223372036854775807L;
    if ((this.manifest.dynamic) && (this.liveEdgeTimeUs != -9223372036854775807L)) {}
    for (int i = 1;; i = 0)
    {
      if (i != 0) {
        l = this.liveEdgeTimeUs - paramLong;
      }
      return l;
    }
  }
  
  private void updateLiveEdgeTimeUs(RepresentationHolder paramRepresentationHolder, int paramInt)
  {
    if (this.manifest.dynamic) {}
    for (long l = paramRepresentationHolder.getSegmentEndTimeUs(paramInt);; l = -9223372036854775807L)
    {
      this.liveEdgeTimeUs = l;
      return;
    }
  }
  
  public long getAdjustedSeekPositionUs(long paramLong, SeekParameters paramSeekParameters)
  {
    RepresentationHolder[] arrayOfRepresentationHolder = this.representationHolders;
    int i = arrayOfRepresentationHolder.length;
    for (int j = 0;; j++)
    {
      long l1 = paramLong;
      RepresentationHolder localRepresentationHolder;
      long l2;
      if (j < i)
      {
        localRepresentationHolder = arrayOfRepresentationHolder[j];
        if (localRepresentationHolder.segmentIndex == null) {
          continue;
        }
        j = localRepresentationHolder.getSegmentNum(paramLong);
        l2 = localRepresentationHolder.getSegmentStartTimeUs(j);
        if ((l2 >= paramLong) || (j >= localRepresentationHolder.getSegmentCount() - 1)) {
          break label100;
        }
      }
      label100:
      for (l1 = localRepresentationHolder.getSegmentStartTimeUs(j + 1);; l1 = l2)
      {
        l1 = Util.resolveSeekPositionUs(paramLong, paramSeekParameters, l2, l1);
        return l1;
      }
    }
  }
  
  public void getNextChunk(MediaChunk paramMediaChunk, long paramLong1, long paramLong2, ChunkHolder paramChunkHolder)
  {
    if (this.fatalError != null) {}
    for (;;)
    {
      return;
      long l1 = resolveTimeToLiveEdgeUs(paramLong1);
      long l2 = C.msToUs(this.manifest.availabilityStartTimeMs);
      long l3 = C.msToUs(this.manifest.getPeriod(this.periodIndex).startMs);
      if ((this.playerTrackEmsgHandler == null) || (!this.playerTrackEmsgHandler.maybeRefreshManifestBeforeLoadingNextChunk(l2 + l3 + paramLong2)))
      {
        this.trackSelection.updateSelectedTrack(paramLong1, paramLong2 - paramLong1, l1);
        RepresentationHolder localRepresentationHolder = this.representationHolders[this.trackSelection.getSelectedIndex()];
        if (localRepresentationHolder.extractorWrapper != null)
        {
          Representation localRepresentation = localRepresentationHolder.representation;
          RangedUri localRangedUri1 = null;
          RangedUri localRangedUri2 = null;
          if (localRepresentationHolder.extractorWrapper.getSampleFormats() == null) {
            localRangedUri1 = localRepresentation.getInitializationUri();
          }
          if (localRepresentationHolder.segmentIndex == null) {
            localRangedUri2 = localRepresentation.getIndexUri();
          }
          if ((localRangedUri1 != null) || (localRangedUri2 != null))
          {
            paramChunkHolder.chunk = newInitializationChunk(localRepresentationHolder, this.dataSource, this.trackSelection.getSelectedFormat(), this.trackSelection.getSelectionReason(), this.trackSelection.getSelectionData(), localRangedUri1, localRangedUri2);
            continue;
          }
        }
        int i = localRepresentationHolder.getSegmentCount();
        if (i == 0)
        {
          if ((!this.manifest.dynamic) || (this.periodIndex < this.manifest.getPeriodCount() - 1)) {}
          for (bool = true;; bool = false)
          {
            paramChunkHolder.endOfStream = bool;
            break;
          }
        }
        int j = localRepresentationHolder.getFirstSegmentNum();
        label371:
        int k;
        if (i == -1)
        {
          paramLong1 = getNowUnixTimeUs() - C.msToUs(this.manifest.availabilityStartTimeMs) - C.msToUs(this.manifest.getPeriod(this.periodIndex).startMs);
          i = j;
          if (this.manifest.timeShiftBufferDepthMs != -9223372036854775807L) {
            i = Math.max(j, localRepresentationHolder.getSegmentNum(paramLong1 - C.msToUs(this.manifest.timeShiftBufferDepthMs)));
          }
          j = localRepresentationHolder.getSegmentNum(paramLong1) - 1;
          updateLiveEdgeTimeUs(localRepresentationHolder, j);
          if (paramMediaChunk != null) {
            break label479;
          }
          k = Util.constrainValue(localRepresentationHolder.getSegmentNum(paramLong2), i, j);
          label399:
          if ((k <= j) && ((!this.missingLastSegment) || (k < j))) {
            break label516;
          }
          if ((this.manifest.dynamic) && (this.periodIndex >= this.manifest.getPeriodCount() - 1)) {
            break label510;
          }
        }
        label479:
        label510:
        for (boolean bool = true;; bool = false)
        {
          paramChunkHolder.endOfStream = bool;
          break;
          k = j + i - 1;
          i = j;
          j = k;
          break label371;
          int m = paramMediaChunk.getNextChunkIndex();
          k = m;
          if (m >= i) {
            break label399;
          }
          this.fatalError = new BehindLiveWindowException();
          break;
        }
        label516:
        i = Math.min(this.maxSegmentsPerLoad, j - k + 1);
        paramChunkHolder.chunk = newMediaChunk(localRepresentationHolder, this.dataSource, this.trackType, this.trackSelection.getSelectedFormat(), this.trackSelection.getSelectionReason(), this.trackSelection.getSelectionData(), k, i);
      }
    }
  }
  
  public int getPreferredQueueSize(long paramLong, List<? extends MediaChunk> paramList)
  {
    if ((this.fatalError != null) || (this.trackSelection.length() < 2)) {}
    for (int i = paramList.size();; i = this.trackSelection.evaluateQueueSize(paramLong, paramList)) {
      return i;
    }
  }
  
  public void maybeThrowError()
    throws IOException
  {
    if (this.fatalError != null) {
      throw this.fatalError;
    }
    this.manifestLoaderErrorThrower.maybeThrowError();
  }
  
  public void onChunkLoadCompleted(Chunk paramChunk)
  {
    if ((paramChunk instanceof InitializationChunk))
    {
      Object localObject = (InitializationChunk)paramChunk;
      localObject = this.representationHolders[this.trackSelection.indexOf(localObject.trackFormat)];
      if (((RepresentationHolder)localObject).segmentIndex == null)
      {
        SeekMap localSeekMap = ((RepresentationHolder)localObject).extractorWrapper.getSeekMap();
        if (localSeekMap != null) {
          ((RepresentationHolder)localObject).segmentIndex = new DashWrappingSegmentIndex((ChunkIndex)localSeekMap);
        }
      }
    }
    if (this.playerTrackEmsgHandler != null) {
      this.playerTrackEmsgHandler.onChunkLoadCompleted(paramChunk);
    }
  }
  
  public boolean onChunkLoadError(Chunk paramChunk, boolean paramBoolean, Exception paramException)
  {
    if (!paramBoolean) {
      paramBoolean = false;
    }
    for (;;)
    {
      return paramBoolean;
      if ((this.playerTrackEmsgHandler != null) && (this.playerTrackEmsgHandler.maybeRefreshManifestOnLoadingError(paramChunk)))
      {
        paramBoolean = true;
      }
      else
      {
        if ((!this.manifest.dynamic) && ((paramChunk instanceof MediaChunk)) && ((paramException instanceof HttpDataSource.InvalidResponseCodeException)) && (((HttpDataSource.InvalidResponseCodeException)paramException).responseCode == 404))
        {
          RepresentationHolder localRepresentationHolder = this.representationHolders[this.trackSelection.indexOf(paramChunk.trackFormat)];
          int i = localRepresentationHolder.getSegmentCount();
          if ((i != -1) && (i != 0))
          {
            int j = localRepresentationHolder.getFirstSegmentNum();
            if (((MediaChunk)paramChunk).getNextChunkIndex() > j + i - 1)
            {
              this.missingLastSegment = true;
              paramBoolean = true;
              continue;
            }
          }
        }
        paramBoolean = ChunkedTrackBlacklistUtil.maybeBlacklistTrack(this.trackSelection, this.trackSelection.indexOf(paramChunk.trackFormat), paramException);
      }
    }
  }
  
  public void updateManifest(DashManifest paramDashManifest, int paramInt)
  {
    try
    {
      this.manifest = paramDashManifest;
      this.periodIndex = paramInt;
      long l = this.manifest.getPeriodDurationUs(this.periodIndex);
      ArrayList localArrayList = getRepresentations();
      for (paramInt = 0; paramInt < this.representationHolders.length; paramInt++)
      {
        paramDashManifest = (Representation)localArrayList.get(this.trackSelection.getIndexInTrackGroup(paramInt));
        this.representationHolders[paramInt].updateRepresentation(l, paramDashManifest);
      }
      return;
    }
    catch (BehindLiveWindowException paramDashManifest)
    {
      this.fatalError = paramDashManifest;
    }
  }
  
  public static final class Factory
    implements DashChunkSource.Factory
  {
    private final DataSource.Factory dataSourceFactory;
    private final int maxSegmentsPerLoad;
    
    public Factory(DataSource.Factory paramFactory)
    {
      this(paramFactory, 1);
    }
    
    public Factory(DataSource.Factory paramFactory, int paramInt)
    {
      this.dataSourceFactory = paramFactory;
      this.maxSegmentsPerLoad = paramInt;
    }
    
    public DashChunkSource createDashChunkSource(LoaderErrorThrower paramLoaderErrorThrower, DashManifest paramDashManifest, int paramInt1, int[] paramArrayOfInt, TrackSelection paramTrackSelection, int paramInt2, long paramLong, boolean paramBoolean1, boolean paramBoolean2, PlayerEmsgHandler.PlayerTrackEmsgHandler paramPlayerTrackEmsgHandler)
    {
      return new DefaultDashChunkSource(paramLoaderErrorThrower, paramDashManifest, paramInt1, paramArrayOfInt, paramTrackSelection, paramInt2, this.dataSourceFactory.createDataSource(), paramLong, this.maxSegmentsPerLoad, paramBoolean1, paramBoolean2, paramPlayerTrackEmsgHandler);
    }
  }
  
  protected static final class RepresentationHolder
  {
    final ChunkExtractorWrapper extractorWrapper;
    private long periodDurationUs;
    public Representation representation;
    public DashSegmentIndex segmentIndex;
    private int segmentNumShift;
    
    RepresentationHolder(long paramLong, int paramInt, Representation paramRepresentation, boolean paramBoolean1, boolean paramBoolean2, TrackOutput paramTrackOutput)
    {
      this.periodDurationUs = paramLong;
      this.representation = paramRepresentation;
      Object localObject = paramRepresentation.format.containerMimeType;
      if (mimeTypeIsRawText((String)localObject))
      {
        this.extractorWrapper = null;
        this.segmentIndex = paramRepresentation.getIndex();
        return;
      }
      if ("application/x-rawcc".equals(localObject)) {}
      for (paramTrackOutput = new RawCcExtractor(paramRepresentation.format);; paramTrackOutput = new MatroskaExtractor(1))
      {
        this.extractorWrapper = new ChunkExtractorWrapper(paramTrackOutput, paramInt, paramRepresentation.format);
        break;
        if (!mimeTypeIsWebm((String)localObject)) {
          break label115;
        }
      }
      label115:
      int i = 0;
      if (paramBoolean1) {
        i = 0x0 | 0x4;
      }
      if (paramBoolean2) {}
      for (localObject = Collections.singletonList(Format.createTextSampleFormat(null, "application/cea-608", 0, null));; localObject = Collections.emptyList())
      {
        paramTrackOutput = new FragmentedMp4Extractor(i, null, null, null, (List)localObject, paramTrackOutput);
        break;
      }
    }
    
    private static boolean mimeTypeIsRawText(String paramString)
    {
      if ((MimeTypes.isText(paramString)) || ("application/ttml+xml".equals(paramString))) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    private static boolean mimeTypeIsWebm(String paramString)
    {
      if ((paramString.startsWith("video/webm")) || (paramString.startsWith("audio/webm")) || (paramString.startsWith("application/webm"))) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public int getFirstSegmentNum()
    {
      return this.segmentIndex.getFirstSegmentNum() + this.segmentNumShift;
    }
    
    public int getSegmentCount()
    {
      return this.segmentIndex.getSegmentCount(this.periodDurationUs);
    }
    
    public long getSegmentEndTimeUs(int paramInt)
    {
      return getSegmentStartTimeUs(paramInt) + this.segmentIndex.getDurationUs(paramInt - this.segmentNumShift, this.periodDurationUs);
    }
    
    public int getSegmentNum(long paramLong)
    {
      return this.segmentIndex.getSegmentNum(paramLong, this.periodDurationUs) + this.segmentNumShift;
    }
    
    public long getSegmentStartTimeUs(int paramInt)
    {
      return this.segmentIndex.getTimeUs(paramInt - this.segmentNumShift);
    }
    
    public RangedUri getSegmentUrl(int paramInt)
    {
      return this.segmentIndex.getSegmentUrl(paramInt - this.segmentNumShift);
    }
    
    void updateRepresentation(long paramLong, Representation paramRepresentation)
      throws BehindLiveWindowException
    {
      DashSegmentIndex localDashSegmentIndex1 = this.representation.getIndex();
      DashSegmentIndex localDashSegmentIndex2 = paramRepresentation.getIndex();
      this.periodDurationUs = paramLong;
      this.representation = paramRepresentation;
      if (localDashSegmentIndex1 == null) {}
      for (;;)
      {
        return;
        this.segmentIndex = localDashSegmentIndex2;
        if (localDashSegmentIndex1.isExplicit())
        {
          int i = localDashSegmentIndex1.getSegmentCount(this.periodDurationUs);
          if (i != 0)
          {
            i = localDashSegmentIndex1.getFirstSegmentNum() + i - 1;
            paramLong = localDashSegmentIndex1.getTimeUs(i) + localDashSegmentIndex1.getDurationUs(i, this.periodDurationUs);
            int j = localDashSegmentIndex2.getFirstSegmentNum();
            long l = localDashSegmentIndex2.getTimeUs(j);
            if (paramLong == l)
            {
              this.segmentNumShift += i + 1 - j;
            }
            else
            {
              if (paramLong < l) {
                throw new BehindLiveWindowException();
              }
              this.segmentNumShift += localDashSegmentIndex1.getSegmentNum(l, this.periodDurationUs) - j;
            }
          }
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/dash/DefaultDashChunkSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */