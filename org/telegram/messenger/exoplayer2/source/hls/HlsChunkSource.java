package org.telegram.messenger.exoplayer2.source.hls;

import android.net.Uri;
import android.os.SystemClock;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.source.BehindLiveWindowException;
import org.telegram.messenger.exoplayer2.source.TrackGroup;
import org.telegram.messenger.exoplayer2.source.chunk.Chunk;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil;
import org.telegram.messenger.exoplayer2.source.chunk.DataChunk;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist.Segment;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsPlaylistTracker;
import org.telegram.messenger.exoplayer2.trackselection.BaseTrackSelection;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.util.TimestampAdjuster;
import org.telegram.messenger.exoplayer2.util.UriUtil;
import org.telegram.messenger.exoplayer2.util.Util;

class HlsChunkSource
{
  private final DataSource encryptionDataSource;
  private byte[] encryptionIv;
  private String encryptionIvString;
  private byte[] encryptionKey;
  private Uri encryptionKeyUri;
  private HlsMasterPlaylist.HlsUrl expectedPlaylistUrl;
  private final HlsExtractorFactory extractorFactory;
  private IOException fatalError;
  private boolean independentSegments;
  private boolean isTimestampMaster;
  private long liveEdgeTimeUs;
  private final DataSource mediaDataSource;
  private final List<Format> muxedCaptionFormats;
  private final HlsPlaylistTracker playlistTracker;
  private byte[] scratchSpace;
  private final TimestampAdjusterProvider timestampAdjusterProvider;
  private final TrackGroup trackGroup;
  private TrackSelection trackSelection;
  private final HlsMasterPlaylist.HlsUrl[] variants;
  
  public HlsChunkSource(HlsExtractorFactory paramHlsExtractorFactory, HlsPlaylistTracker paramHlsPlaylistTracker, HlsMasterPlaylist.HlsUrl[] paramArrayOfHlsUrl, HlsDataSourceFactory paramHlsDataSourceFactory, TimestampAdjusterProvider paramTimestampAdjusterProvider, List<Format> paramList)
  {
    this.extractorFactory = paramHlsExtractorFactory;
    this.playlistTracker = paramHlsPlaylistTracker;
    this.variants = paramArrayOfHlsUrl;
    this.timestampAdjusterProvider = paramTimestampAdjusterProvider;
    this.muxedCaptionFormats = paramList;
    this.liveEdgeTimeUs = -9223372036854775807L;
    paramHlsExtractorFactory = new Format[paramArrayOfHlsUrl.length];
    paramHlsPlaylistTracker = new int[paramArrayOfHlsUrl.length];
    for (int i = 0; i < paramArrayOfHlsUrl.length; i++)
    {
      paramHlsExtractorFactory[i] = paramArrayOfHlsUrl[i].format;
      paramHlsPlaylistTracker[i] = i;
    }
    this.mediaDataSource = paramHlsDataSourceFactory.createDataSource(1);
    this.encryptionDataSource = paramHlsDataSourceFactory.createDataSource(3);
    this.trackGroup = new TrackGroup(paramHlsExtractorFactory);
    this.trackSelection = new InitializationTrackSelection(this.trackGroup, paramHlsPlaylistTracker);
  }
  
  private void clearEncryptionData()
  {
    this.encryptionKeyUri = null;
    this.encryptionKey = null;
    this.encryptionIvString = null;
    this.encryptionIv = null;
  }
  
  private EncryptionKeyChunk newEncryptionKeyChunk(Uri paramUri, String paramString, int paramInt1, int paramInt2, Object paramObject)
  {
    paramUri = new DataSpec(paramUri, 0L, -1L, null, 1);
    return new EncryptionKeyChunk(this.encryptionDataSource, paramUri, this.variants[paramInt1].format, paramInt2, paramObject, this.scratchSpace, paramString);
  }
  
  private long resolveTimeToLiveEdgeUs(long paramLong)
  {
    long l = -9223372036854775807L;
    if (this.liveEdgeTimeUs != -9223372036854775807L) {}
    for (int i = 1;; i = 0)
    {
      if (i != 0) {
        l = this.liveEdgeTimeUs - paramLong;
      }
      return l;
    }
  }
  
  private void setEncryptionData(Uri paramUri, String paramString, byte[] paramArrayOfByte)
  {
    Object localObject;
    byte[] arrayOfByte;
    if (Util.toLowerInvariant(paramString).startsWith("0x"))
    {
      localObject = paramString.substring(2);
      arrayOfByte = new BigInteger((String)localObject, 16).toByteArray();
      localObject = new byte[16];
      if (arrayOfByte.length <= 16) {
        break label110;
      }
    }
    label110:
    for (int i = arrayOfByte.length - 16;; i = 0)
    {
      System.arraycopy(arrayOfByte, i, localObject, localObject.length - arrayOfByte.length + i, arrayOfByte.length - i);
      this.encryptionKeyUri = paramUri;
      this.encryptionKey = paramArrayOfByte;
      this.encryptionIvString = paramString;
      this.encryptionIv = ((byte[])localObject);
      return;
      localObject = paramString;
      break;
    }
  }
  
  private void updateLiveEdgeTimeUs(HlsMediaPlaylist paramHlsMediaPlaylist)
  {
    if (paramHlsMediaPlaylist.hasEndTag) {}
    for (long l = -9223372036854775807L;; l = paramHlsMediaPlaylist.getEndTimeUs())
    {
      this.liveEdgeTimeUs = l;
      return;
    }
  }
  
  public void getNextChunk(HlsMediaChunk paramHlsMediaChunk, long paramLong1, long paramLong2, HlsChunkHolder paramHlsChunkHolder)
  {
    int i;
    int j;
    int k;
    label136:
    Object localObject1;
    if (paramHlsMediaChunk == null)
    {
      i = -1;
      this.expectedPlaylistUrl = null;
      long l1 = paramLong2 - paramLong1;
      long l2 = resolveTimeToLiveEdgeUs(paramLong1);
      long l3 = l1;
      long l4 = l2;
      if (paramHlsMediaChunk != null)
      {
        l3 = l1;
        l4 = l2;
        if (!this.independentSegments)
        {
          long l5 = paramHlsMediaChunk.getDurationUs();
          l1 = Math.max(0L, l1 - l5);
          l3 = l1;
          l4 = l2;
          if (l2 != -9223372036854775807L)
          {
            l4 = Math.max(0L, l2 - l5);
            l3 = l1;
          }
        }
      }
      this.trackSelection.updateSelectedTrack(paramLong1, l3, l4);
      j = this.trackSelection.getSelectedIndexInTrackGroup();
      if (i == j) {
        break label187;
      }
      k = 1;
      localObject1 = this.variants[j];
      if (this.playlistTracker.isSnapshotValid((HlsMasterPlaylist.HlsUrl)localObject1)) {
        break label193;
      }
      paramHlsChunkHolder.playlist = ((HlsMasterPlaylist.HlsUrl)localObject1);
      this.expectedPlaylistUrl = ((HlsMasterPlaylist.HlsUrl)localObject1);
    }
    label187:
    label193:
    Object localObject2;
    label242:
    Object localObject3;
    Object localObject4;
    for (;;)
    {
      return;
      i = this.trackGroup.indexOf(paramHlsMediaChunk.trackFormat);
      break;
      k = 0;
      break label136;
      localObject2 = this.playlistTracker.getPlaylistSnapshot((HlsMasterPlaylist.HlsUrl)localObject1);
      this.independentSegments = ((HlsMediaPlaylist)localObject2).hasIndependentSegmentsTag;
      updateLiveEdgeTimeUs((HlsMediaPlaylist)localObject2);
      int m;
      if ((paramHlsMediaChunk == null) || (k != 0)) {
        if ((paramHlsMediaChunk == null) || (this.independentSegments))
        {
          paramLong1 = paramLong2;
          if ((((HlsMediaPlaylist)localObject2).hasEndTag) || (paramLong1 < ((HlsMediaPlaylist)localObject2).getEndTimeUs())) {
            break label322;
          }
          k = ((HlsMediaPlaylist)localObject2).mediaSequence + ((HlsMediaPlaylist)localObject2).segments.size();
          localObject3 = localObject2;
          localObject4 = localObject1;
          m = j;
        }
      }
      for (;;)
      {
        if (k >= ((HlsMediaPlaylist)localObject3).mediaSequence) {
          break label482;
        }
        this.fatalError = new BehindLiveWindowException();
        break;
        paramLong1 = paramHlsMediaChunk.startTimeUs;
        break label242;
        label322:
        localObject4 = ((HlsMediaPlaylist)localObject2).segments;
        paramLong2 = ((HlsMediaPlaylist)localObject2).startTimeUs;
        if ((!this.playlistTracker.isLive()) || (paramHlsMediaChunk == null)) {}
        for (boolean bool = true;; bool = false)
        {
          int n = Util.binarySearchFloor((List)localObject4, Long.valueOf(paramLong1 - paramLong2), true, bool) + ((HlsMediaPlaylist)localObject2).mediaSequence;
          m = j;
          localObject4 = localObject1;
          k = n;
          localObject3 = localObject2;
          if (n >= ((HlsMediaPlaylist)localObject2).mediaSequence) {
            break;
          }
          m = j;
          localObject4 = localObject1;
          k = n;
          localObject3 = localObject2;
          if (paramHlsMediaChunk == null) {
            break;
          }
          localObject4 = this.variants[i];
          localObject3 = this.playlistTracker.getPlaylistSnapshot((HlsMasterPlaylist.HlsUrl)localObject4);
          k = paramHlsMediaChunk.getNextChunkIndex();
          m = i;
          break;
        }
        k = paramHlsMediaChunk.getNextChunkIndex();
        m = j;
        localObject4 = localObject1;
        localObject3 = localObject2;
      }
      label482:
      i = k - ((HlsMediaPlaylist)localObject3).mediaSequence;
      if (i >= ((HlsMediaPlaylist)localObject3).segments.size())
      {
        if (((HlsMediaPlaylist)localObject3).hasEndTag)
        {
          paramHlsChunkHolder.endOfStream = true;
        }
        else
        {
          paramHlsChunkHolder.playlist = ((HlsMasterPlaylist.HlsUrl)localObject4);
          this.expectedPlaylistUrl = ((HlsMasterPlaylist.HlsUrl)localObject4);
        }
      }
      else
      {
        localObject2 = (HlsMediaPlaylist.Segment)((HlsMediaPlaylist)localObject3).segments.get(i);
        if (((HlsMediaPlaylist.Segment)localObject2).fullSegmentEncryptionKeyUri == null) {
          break label861;
        }
        localObject1 = UriUtil.resolveToUri(((HlsMediaPlaylist)localObject3).baseUri, ((HlsMediaPlaylist.Segment)localObject2).fullSegmentEncryptionKeyUri);
        if (((Uri)localObject1).equals(this.encryptionKeyUri)) {
          break label631;
        }
        paramHlsChunkHolder.chunk = newEncryptionKeyChunk((Uri)localObject1, ((HlsMediaPlaylist.Segment)localObject2).encryptionIV, m, this.trackSelection.getSelectionReason(), this.trackSelection.getSelectionData());
      }
    }
    label631:
    if (!Util.areEqual(((HlsMediaPlaylist.Segment)localObject2).encryptionIV, this.encryptionIvString)) {
      setEncryptionData((Uri)localObject1, ((HlsMediaPlaylist.Segment)localObject2).encryptionIV, this.encryptionKey);
    }
    for (;;)
    {
      localObject1 = null;
      Object localObject5 = ((HlsMediaPlaylist)localObject3).initializationSegment;
      if (localObject5 != null) {
        localObject1 = new DataSpec(UriUtil.resolveToUri(((HlsMediaPlaylist)localObject3).baseUri, ((HlsMediaPlaylist.Segment)localObject5).url), ((HlsMediaPlaylist.Segment)localObject5).byterangeOffset, ((HlsMediaPlaylist.Segment)localObject5).byterangeLength, null);
      }
      paramLong1 = ((HlsMediaPlaylist)localObject3).startTimeUs + ((HlsMediaPlaylist.Segment)localObject2).relativeStartTimeUs;
      i = ((HlsMediaPlaylist)localObject3).discontinuitySequence + ((HlsMediaPlaylist.Segment)localObject2).relativeDiscontinuitySequence;
      TimestampAdjuster localTimestampAdjuster = this.timestampAdjusterProvider.getAdjuster(i);
      localObject5 = new DataSpec(UriUtil.resolveToUri(((HlsMediaPlaylist)localObject3).baseUri, ((HlsMediaPlaylist.Segment)localObject2).url), ((HlsMediaPlaylist.Segment)localObject2).byterangeOffset, ((HlsMediaPlaylist.Segment)localObject2).byterangeLength, null);
      paramHlsChunkHolder.chunk = new HlsMediaChunk(this.extractorFactory, this.mediaDataSource, (DataSpec)localObject5, (DataSpec)localObject1, (HlsMasterPlaylist.HlsUrl)localObject4, this.muxedCaptionFormats, this.trackSelection.getSelectionReason(), this.trackSelection.getSelectionData(), paramLong1, paramLong1 + ((HlsMediaPlaylist.Segment)localObject2).durationUs, k, i, this.isTimestampMaster, localTimestampAdjuster, paramHlsMediaChunk, ((HlsMediaPlaylist)localObject3).drmInitData, this.encryptionKey, this.encryptionIv);
      break;
      label861:
      clearEncryptionData();
    }
  }
  
  public TrackGroup getTrackGroup()
  {
    return this.trackGroup;
  }
  
  public TrackSelection getTrackSelection()
  {
    return this.trackSelection;
  }
  
  public void maybeThrowError()
    throws IOException
  {
    if (this.fatalError != null) {
      throw this.fatalError;
    }
    if (this.expectedPlaylistUrl != null) {
      this.playlistTracker.maybeThrowPlaylistRefreshError(this.expectedPlaylistUrl);
    }
  }
  
  public void onChunkLoadCompleted(Chunk paramChunk)
  {
    if ((paramChunk instanceof EncryptionKeyChunk))
    {
      paramChunk = (EncryptionKeyChunk)paramChunk;
      this.scratchSpace = paramChunk.getDataHolder();
      setEncryptionData(paramChunk.dataSpec.uri, paramChunk.iv, paramChunk.getResult());
    }
  }
  
  public boolean onChunkLoadError(Chunk paramChunk, boolean paramBoolean, IOException paramIOException)
  {
    if ((paramBoolean) && (ChunkedTrackBlacklistUtil.maybeBlacklistTrack(this.trackSelection, this.trackSelection.indexOf(this.trackGroup.indexOf(paramChunk.trackFormat)), paramIOException))) {}
    for (paramBoolean = true;; paramBoolean = false) {
      return paramBoolean;
    }
  }
  
  public void onPlaylistBlacklisted(HlsMasterPlaylist.HlsUrl paramHlsUrl, long paramLong)
  {
    int i = this.trackGroup.indexOf(paramHlsUrl.format);
    if (i != -1)
    {
      i = this.trackSelection.indexOf(i);
      if (i != -1) {
        this.trackSelection.blacklist(i, paramLong);
      }
    }
  }
  
  public void reset()
  {
    this.fatalError = null;
  }
  
  public void selectTracks(TrackSelection paramTrackSelection)
  {
    this.trackSelection = paramTrackSelection;
  }
  
  public void setIsTimestampMaster(boolean paramBoolean)
  {
    this.isTimestampMaster = paramBoolean;
  }
  
  private static final class EncryptionKeyChunk
    extends DataChunk
  {
    public final String iv;
    private byte[] result;
    
    public EncryptionKeyChunk(DataSource paramDataSource, DataSpec paramDataSpec, Format paramFormat, int paramInt, Object paramObject, byte[] paramArrayOfByte, String paramString)
    {
      super(paramDataSpec, 3, paramFormat, paramInt, paramObject, paramArrayOfByte);
      this.iv = paramString;
    }
    
    protected void consume(byte[] paramArrayOfByte, int paramInt)
      throws IOException
    {
      this.result = Arrays.copyOf(paramArrayOfByte, paramInt);
    }
    
    public byte[] getResult()
    {
      return this.result;
    }
  }
  
  public static final class HlsChunkHolder
  {
    public Chunk chunk;
    public boolean endOfStream;
    public HlsMasterPlaylist.HlsUrl playlist;
    
    public HlsChunkHolder()
    {
      clear();
    }
    
    public void clear()
    {
      this.chunk = null;
      this.endOfStream = false;
      this.playlist = null;
    }
  }
  
  private static final class InitializationTrackSelection
    extends BaseTrackSelection
  {
    private int selectedIndex = indexOf(paramTrackGroup.getFormat(0));
    
    public InitializationTrackSelection(TrackGroup paramTrackGroup, int[] paramArrayOfInt)
    {
      super(paramArrayOfInt);
    }
    
    public int getSelectedIndex()
    {
      return this.selectedIndex;
    }
    
    public Object getSelectionData()
    {
      return null;
    }
    
    public int getSelectionReason()
    {
      return 0;
    }
    
    public void updateSelectedTrack(long paramLong1, long paramLong2, long paramLong3)
    {
      paramLong1 = SystemClock.elapsedRealtime();
      if (!isBlacklisted(this.selectedIndex, paramLong1)) {
        return;
      }
      for (int i = this.length - 1;; i--)
      {
        if (i < 0) {
          break label55;
        }
        if (!isBlacklisted(i, paramLong1))
        {
          this.selectedIndex = i;
          break;
        }
      }
      label55:
      throw new IllegalStateException();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/hls/HlsChunkSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */