package org.telegram.messenger.exoplayer2.source.hls.playlist;

import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil;
import org.telegram.messenger.exoplayer2.source.hls.HlsDataSourceFactory;
import org.telegram.messenger.exoplayer2.upstream.Loader;
import org.telegram.messenger.exoplayer2.upstream.Loader.Callback;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable.Parser;
import org.telegram.messenger.exoplayer2.util.UriUtil;

public final class HlsPlaylistTracker
  implements Loader.Callback<ParsingLoadable<HlsPlaylist>>
{
  private static final double PLAYLIST_STUCK_TARGET_DURATION_COEFFICIENT = 3.5D;
  private final HlsDataSourceFactory dataSourceFactory;
  private final MediaSourceEventListener.EventDispatcher eventDispatcher;
  private final Loader initialPlaylistLoader;
  private final Uri initialPlaylistUri;
  private boolean isLive;
  private final List<PlaylistEventListener> listeners;
  private HlsMasterPlaylist masterPlaylist;
  private final int minRetryCount;
  private final IdentityHashMap<HlsMasterPlaylist.HlsUrl, MediaPlaylistBundle> playlistBundles;
  private final ParsingLoadable.Parser<HlsPlaylist> playlistParser;
  private final Handler playlistRefreshHandler;
  private HlsMasterPlaylist.HlsUrl primaryHlsUrl;
  private final PrimaryPlaylistListener primaryPlaylistListener;
  private HlsMediaPlaylist primaryUrlSnapshot;
  
  public HlsPlaylistTracker(Uri paramUri, HlsDataSourceFactory paramHlsDataSourceFactory, MediaSourceEventListener.EventDispatcher paramEventDispatcher, int paramInt, PrimaryPlaylistListener paramPrimaryPlaylistListener, ParsingLoadable.Parser<HlsPlaylist> paramParser)
  {
    this.initialPlaylistUri = paramUri;
    this.dataSourceFactory = paramHlsDataSourceFactory;
    this.eventDispatcher = paramEventDispatcher;
    this.minRetryCount = paramInt;
    this.primaryPlaylistListener = paramPrimaryPlaylistListener;
    this.playlistParser = paramParser;
    this.listeners = new ArrayList();
    this.initialPlaylistLoader = new Loader("HlsPlaylistTracker:MasterPlaylist");
    this.playlistBundles = new IdentityHashMap();
    this.playlistRefreshHandler = new Handler();
  }
  
  private void createBundles(List<HlsMasterPlaylist.HlsUrl> paramList)
  {
    int i = paramList.size();
    for (int j = 0; j < i; j++)
    {
      HlsMasterPlaylist.HlsUrl localHlsUrl = (HlsMasterPlaylist.HlsUrl)paramList.get(j);
      MediaPlaylistBundle localMediaPlaylistBundle = new MediaPlaylistBundle(localHlsUrl);
      this.playlistBundles.put(localHlsUrl, localMediaPlaylistBundle);
    }
  }
  
  private static HlsMediaPlaylist.Segment getFirstOldOverlappingSegment(HlsMediaPlaylist paramHlsMediaPlaylist1, HlsMediaPlaylist paramHlsMediaPlaylist2)
  {
    int i = paramHlsMediaPlaylist2.mediaSequence - paramHlsMediaPlaylist1.mediaSequence;
    paramHlsMediaPlaylist1 = paramHlsMediaPlaylist1.segments;
    if (i < paramHlsMediaPlaylist1.size()) {}
    for (paramHlsMediaPlaylist1 = (HlsMediaPlaylist.Segment)paramHlsMediaPlaylist1.get(i);; paramHlsMediaPlaylist1 = null) {
      return paramHlsMediaPlaylist1;
    }
  }
  
  private HlsMediaPlaylist getLatestPlaylistSnapshot(HlsMediaPlaylist paramHlsMediaPlaylist1, HlsMediaPlaylist paramHlsMediaPlaylist2)
  {
    if (!paramHlsMediaPlaylist2.isNewerThan(paramHlsMediaPlaylist1))
    {
      localHlsMediaPlaylist = paramHlsMediaPlaylist1;
      if (!paramHlsMediaPlaylist2.hasEndTag) {}
    }
    for (HlsMediaPlaylist localHlsMediaPlaylist = paramHlsMediaPlaylist1.copyWithEndTag();; localHlsMediaPlaylist = paramHlsMediaPlaylist2.copyWith(getLoadedPlaylistStartTimeUs(paramHlsMediaPlaylist1, paramHlsMediaPlaylist2), getLoadedPlaylistDiscontinuitySequence(paramHlsMediaPlaylist1, paramHlsMediaPlaylist2))) {
      return localHlsMediaPlaylist;
    }
  }
  
  private int getLoadedPlaylistDiscontinuitySequence(HlsMediaPlaylist paramHlsMediaPlaylist1, HlsMediaPlaylist paramHlsMediaPlaylist2)
  {
    int i;
    if (paramHlsMediaPlaylist2.hasDiscontinuitySequence)
    {
      i = paramHlsMediaPlaylist2.discontinuitySequence;
      return i;
    }
    if (this.primaryUrlSnapshot != null) {}
    for (int j = this.primaryUrlSnapshot.discontinuitySequence;; j = 0)
    {
      i = j;
      if (paramHlsMediaPlaylist1 == null) {
        break;
      }
      HlsMediaPlaylist.Segment localSegment = getFirstOldOverlappingSegment(paramHlsMediaPlaylist1, paramHlsMediaPlaylist2);
      i = j;
      if (localSegment == null) {
        break;
      }
      i = paramHlsMediaPlaylist1.discontinuitySequence + localSegment.relativeDiscontinuitySequence - ((HlsMediaPlaylist.Segment)paramHlsMediaPlaylist2.segments.get(0)).relativeDiscontinuitySequence;
      break;
    }
  }
  
  private long getLoadedPlaylistStartTimeUs(HlsMediaPlaylist paramHlsMediaPlaylist1, HlsMediaPlaylist paramHlsMediaPlaylist2)
  {
    long l1;
    if (paramHlsMediaPlaylist2.hasProgramDateTime) {
      l1 = paramHlsMediaPlaylist2.startTimeUs;
    }
    for (;;)
    {
      return l1;
      if (this.primaryUrlSnapshot != null) {}
      int i;
      for (long l2 = this.primaryUrlSnapshot.startTimeUs;; l2 = 0L)
      {
        l1 = l2;
        if (paramHlsMediaPlaylist1 == null) {
          break;
        }
        i = paramHlsMediaPlaylist1.segments.size();
        HlsMediaPlaylist.Segment localSegment = getFirstOldOverlappingSegment(paramHlsMediaPlaylist1, paramHlsMediaPlaylist2);
        if (localSegment == null) {
          break label80;
        }
        l1 = paramHlsMediaPlaylist1.startTimeUs + localSegment.relativeStartTimeUs;
        break;
      }
      label80:
      l1 = l2;
      if (i == paramHlsMediaPlaylist2.mediaSequence - paramHlsMediaPlaylist1.mediaSequence) {
        l1 = paramHlsMediaPlaylist1.getEndTimeUs();
      }
    }
  }
  
  private boolean maybeSelectNewPrimaryUrl()
  {
    List localList = this.masterPlaylist.variants;
    int i = localList.size();
    long l = SystemClock.elapsedRealtime();
    int j = 0;
    if (j < i)
    {
      MediaPlaylistBundle localMediaPlaylistBundle = (MediaPlaylistBundle)this.playlistBundles.get(localList.get(j));
      if (l > localMediaPlaylistBundle.blacklistUntilMs)
      {
        this.primaryHlsUrl = localMediaPlaylistBundle.playlistUrl;
        localMediaPlaylistBundle.loadPlaylist();
      }
    }
    for (boolean bool = true;; bool = false)
    {
      return bool;
      j++;
      break;
    }
  }
  
  private void maybeSetPrimaryUrl(HlsMasterPlaylist.HlsUrl paramHlsUrl)
  {
    if ((paramHlsUrl == this.primaryHlsUrl) || (!this.masterPlaylist.variants.contains(paramHlsUrl)) || ((this.primaryUrlSnapshot != null) && (this.primaryUrlSnapshot.hasEndTag))) {}
    for (;;)
    {
      return;
      this.primaryHlsUrl = paramHlsUrl;
      ((MediaPlaylistBundle)this.playlistBundles.get(this.primaryHlsUrl)).loadPlaylist();
    }
  }
  
  private void notifyPlaylistBlacklisting(HlsMasterPlaylist.HlsUrl paramHlsUrl, long paramLong)
  {
    int i = this.listeners.size();
    for (int j = 0; j < i; j++) {
      ((PlaylistEventListener)this.listeners.get(j)).onPlaylistBlacklisted(paramHlsUrl, paramLong);
    }
  }
  
  private void onPlaylistUpdated(HlsMasterPlaylist.HlsUrl paramHlsUrl, HlsMediaPlaylist paramHlsMediaPlaylist)
  {
    if (paramHlsUrl == this.primaryHlsUrl) {
      if (this.primaryUrlSnapshot == null) {
        if (paramHlsMediaPlaylist.hasEndTag) {
          break label90;
        }
      }
    }
    label90:
    for (boolean bool = true;; bool = false)
    {
      this.isLive = bool;
      this.primaryUrlSnapshot = paramHlsMediaPlaylist;
      this.primaryPlaylistListener.onPrimaryPlaylistRefreshed(paramHlsMediaPlaylist);
      int i = this.listeners.size();
      for (int j = 0; j < i; j++) {
        ((PlaylistEventListener)this.listeners.get(j)).onPlaylistChanged();
      }
    }
  }
  
  public void addListener(PlaylistEventListener paramPlaylistEventListener)
  {
    this.listeners.add(paramPlaylistEventListener);
  }
  
  public HlsMasterPlaylist getMasterPlaylist()
  {
    return this.masterPlaylist;
  }
  
  public HlsMediaPlaylist getPlaylistSnapshot(HlsMasterPlaylist.HlsUrl paramHlsUrl)
  {
    HlsMediaPlaylist localHlsMediaPlaylist = ((MediaPlaylistBundle)this.playlistBundles.get(paramHlsUrl)).getPlaylistSnapshot();
    if (localHlsMediaPlaylist != null) {
      maybeSetPrimaryUrl(paramHlsUrl);
    }
    return localHlsMediaPlaylist;
  }
  
  public boolean isLive()
  {
    return this.isLive;
  }
  
  public boolean isSnapshotValid(HlsMasterPlaylist.HlsUrl paramHlsUrl)
  {
    return ((MediaPlaylistBundle)this.playlistBundles.get(paramHlsUrl)).isSnapshotValid();
  }
  
  public void maybeThrowPlaylistRefreshError(HlsMasterPlaylist.HlsUrl paramHlsUrl)
    throws IOException
  {
    ((MediaPlaylistBundle)this.playlistBundles.get(paramHlsUrl)).maybeThrowPlaylistRefreshError();
  }
  
  public void maybeThrowPrimaryPlaylistRefreshError()
    throws IOException
  {
    this.initialPlaylistLoader.maybeThrowError();
    if (this.primaryHlsUrl != null) {
      maybeThrowPlaylistRefreshError(this.primaryHlsUrl);
    }
  }
  
  public void onLoadCanceled(ParsingLoadable<HlsPlaylist> paramParsingLoadable, long paramLong1, long paramLong2, boolean paramBoolean)
  {
    this.eventDispatcher.loadCanceled(paramParsingLoadable.dataSpec, 4, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded());
  }
  
  public void onLoadCompleted(ParsingLoadable<HlsPlaylist> paramParsingLoadable, long paramLong1, long paramLong2)
  {
    HlsPlaylist localHlsPlaylist = (HlsPlaylist)paramParsingLoadable.getResult();
    boolean bool = localHlsPlaylist instanceof HlsMediaPlaylist;
    Object localObject;
    if (bool)
    {
      localObject = HlsMasterPlaylist.createSingleVariantMasterPlaylist(localHlsPlaylist.baseUri);
      this.masterPlaylist = ((HlsMasterPlaylist)localObject);
      this.primaryHlsUrl = ((HlsMasterPlaylist.HlsUrl)((HlsMasterPlaylist)localObject).variants.get(0));
      ArrayList localArrayList = new ArrayList();
      localArrayList.addAll(((HlsMasterPlaylist)localObject).variants);
      localArrayList.addAll(((HlsMasterPlaylist)localObject).audios);
      localArrayList.addAll(((HlsMasterPlaylist)localObject).subtitles);
      createBundles(localArrayList);
      localObject = (MediaPlaylistBundle)this.playlistBundles.get(this.primaryHlsUrl);
      if (!bool) {
        break label164;
      }
      ((MediaPlaylistBundle)localObject).processLoadedPlaylist((HlsMediaPlaylist)localHlsPlaylist);
    }
    for (;;)
    {
      this.eventDispatcher.loadCompleted(paramParsingLoadable.dataSpec, 4, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded());
      return;
      localObject = (HlsMasterPlaylist)localHlsPlaylist;
      break;
      label164:
      ((MediaPlaylistBundle)localObject).loadPlaylist();
    }
  }
  
  public int onLoadError(ParsingLoadable<HlsPlaylist> paramParsingLoadable, long paramLong1, long paramLong2, IOException paramIOException)
  {
    boolean bool = paramIOException instanceof ParserException;
    this.eventDispatcher.loadError(paramParsingLoadable.dataSpec, 4, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded(), paramIOException, bool);
    if (bool) {}
    for (int i = 3;; i = 0) {
      return i;
    }
  }
  
  public void refreshPlaylist(HlsMasterPlaylist.HlsUrl paramHlsUrl)
  {
    ((MediaPlaylistBundle)this.playlistBundles.get(paramHlsUrl)).loadPlaylist();
  }
  
  public void release()
  {
    this.initialPlaylistLoader.release();
    Iterator localIterator = this.playlistBundles.values().iterator();
    while (localIterator.hasNext()) {
      ((MediaPlaylistBundle)localIterator.next()).release();
    }
    this.playlistRefreshHandler.removeCallbacksAndMessages(null);
    this.playlistBundles.clear();
  }
  
  public void removeListener(PlaylistEventListener paramPlaylistEventListener)
  {
    this.listeners.remove(paramPlaylistEventListener);
  }
  
  public void start()
  {
    ParsingLoadable localParsingLoadable = new ParsingLoadable(this.dataSourceFactory.createDataSource(4), this.initialPlaylistUri, 4, this.playlistParser);
    this.initialPlaylistLoader.startLoading(localParsingLoadable, this, this.minRetryCount);
  }
  
  private final class MediaPlaylistBundle
    implements Runnable, Loader.Callback<ParsingLoadable<HlsPlaylist>>
  {
    private long blacklistUntilMs;
    private long earliestNextLoadTimeMs;
    private long lastSnapshotChangeMs;
    private long lastSnapshotLoadMs;
    private boolean loadPending;
    private final ParsingLoadable<HlsPlaylist> mediaPlaylistLoadable;
    private final Loader mediaPlaylistLoader;
    private IOException playlistError;
    private HlsMediaPlaylist playlistSnapshot;
    private final HlsMasterPlaylist.HlsUrl playlistUrl;
    
    public MediaPlaylistBundle(HlsMasterPlaylist.HlsUrl paramHlsUrl)
    {
      this.playlistUrl = paramHlsUrl;
      this.mediaPlaylistLoader = new Loader("HlsPlaylistTracker:MediaPlaylist");
      this.mediaPlaylistLoadable = new ParsingLoadable(HlsPlaylistTracker.this.dataSourceFactory.createDataSource(4), UriUtil.resolveToUri(HlsPlaylistTracker.this.masterPlaylist.baseUri, paramHlsUrl.url), 4, HlsPlaylistTracker.this.playlistParser);
    }
    
    private boolean blacklistPlaylist()
    {
      this.blacklistUntilMs = (SystemClock.elapsedRealtime() + 60000L);
      HlsPlaylistTracker.this.notifyPlaylistBlacklisting(this.playlistUrl, 60000L);
      if ((HlsPlaylistTracker.this.primaryHlsUrl == this.playlistUrl) && (!HlsPlaylistTracker.this.maybeSelectNewPrimaryUrl())) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    private void loadPlaylistImmediately()
    {
      this.mediaPlaylistLoader.startLoading(this.mediaPlaylistLoadable, this, HlsPlaylistTracker.this.minRetryCount);
    }
    
    private void processLoadedPlaylist(HlsMediaPlaylist paramHlsMediaPlaylist)
    {
      HlsMediaPlaylist localHlsMediaPlaylist = this.playlistSnapshot;
      long l1 = SystemClock.elapsedRealtime();
      this.lastSnapshotLoadMs = l1;
      this.playlistSnapshot = HlsPlaylistTracker.this.getLatestPlaylistSnapshot(localHlsMediaPlaylist, paramHlsMediaPlaylist);
      if (this.playlistSnapshot != localHlsMediaPlaylist)
      {
        this.playlistError = null;
        this.lastSnapshotChangeMs = l1;
        HlsPlaylistTracker.this.onPlaylistUpdated(this.playlistUrl, this.playlistSnapshot);
        if (this.playlistSnapshot == localHlsMediaPlaylist) {
          break label226;
        }
      }
      label226:
      for (long l2 = this.playlistSnapshot.targetDurationUs;; l2 = this.playlistSnapshot.targetDurationUs / 2L)
      {
        this.earliestNextLoadTimeMs = (C.usToMs(l2) + l1);
        if ((this.playlistUrl == HlsPlaylistTracker.this.primaryHlsUrl) && (!this.playlistSnapshot.hasEndTag)) {
          loadPlaylist();
        }
        return;
        if (this.playlistSnapshot.hasEndTag) {
          break;
        }
        if (paramHlsMediaPlaylist.mediaSequence + paramHlsMediaPlaylist.segments.size() < this.playlistSnapshot.mediaSequence)
        {
          this.playlistError = new HlsPlaylistTracker.PlaylistResetException(this.playlistUrl.url, null);
          break;
        }
        if (l1 - this.lastSnapshotChangeMs <= C.usToMs(this.playlistSnapshot.targetDurationUs) * 3.5D) {
          break;
        }
        this.playlistError = new HlsPlaylistTracker.PlaylistStuckException(this.playlistUrl.url, null);
        blacklistPlaylist();
        break;
      }
    }
    
    public HlsMediaPlaylist getPlaylistSnapshot()
    {
      return this.playlistSnapshot;
    }
    
    public boolean isSnapshotValid()
    {
      boolean bool = false;
      if (this.playlistSnapshot == null) {}
      for (;;)
      {
        return bool;
        long l1 = SystemClock.elapsedRealtime();
        long l2 = Math.max(30000L, C.usToMs(this.playlistSnapshot.durationUs));
        if ((this.playlistSnapshot.hasEndTag) || (this.playlistSnapshot.playlistType == 2) || (this.playlistSnapshot.playlistType == 1) || (this.lastSnapshotLoadMs + l2 > l1)) {
          bool = true;
        }
      }
    }
    
    public void loadPlaylist()
    {
      this.blacklistUntilMs = 0L;
      if ((this.loadPending) || (this.mediaPlaylistLoader.isLoading())) {}
      for (;;)
      {
        return;
        long l = SystemClock.elapsedRealtime();
        if (l < this.earliestNextLoadTimeMs)
        {
          this.loadPending = true;
          HlsPlaylistTracker.this.playlistRefreshHandler.postDelayed(this, this.earliestNextLoadTimeMs - l);
        }
        else
        {
          loadPlaylistImmediately();
        }
      }
    }
    
    public void maybeThrowPlaylistRefreshError()
      throws IOException
    {
      this.mediaPlaylistLoader.maybeThrowError();
      if (this.playlistError != null) {
        throw this.playlistError;
      }
    }
    
    public void onLoadCanceled(ParsingLoadable<HlsPlaylist> paramParsingLoadable, long paramLong1, long paramLong2, boolean paramBoolean)
    {
      HlsPlaylistTracker.this.eventDispatcher.loadCanceled(paramParsingLoadable.dataSpec, 4, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded());
    }
    
    public void onLoadCompleted(ParsingLoadable<HlsPlaylist> paramParsingLoadable, long paramLong1, long paramLong2)
    {
      HlsPlaylist localHlsPlaylist = (HlsPlaylist)paramParsingLoadable.getResult();
      if ((localHlsPlaylist instanceof HlsMediaPlaylist))
      {
        processLoadedPlaylist((HlsMediaPlaylist)localHlsPlaylist);
        HlsPlaylistTracker.this.eventDispatcher.loadCompleted(paramParsingLoadable.dataSpec, 4, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded());
      }
      for (;;)
      {
        return;
        this.playlistError = new ParserException("Loaded playlist has unexpected type.");
      }
    }
    
    public int onLoadError(ParsingLoadable<HlsPlaylist> paramParsingLoadable, long paramLong1, long paramLong2, IOException paramIOException)
    {
      boolean bool = paramIOException instanceof ParserException;
      HlsPlaylistTracker.this.eventDispatcher.loadError(paramParsingLoadable.dataSpec, 4, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded(), paramIOException, bool);
      int i;
      if (bool) {
        i = 3;
      }
      for (;;)
      {
        return i;
        bool = true;
        if (ChunkedTrackBlacklistUtil.shouldBlacklist(paramIOException)) {
          bool = blacklistPlaylist();
        }
        if (bool) {
          i = 0;
        } else {
          i = 2;
        }
      }
    }
    
    public void release()
    {
      this.mediaPlaylistLoader.release();
    }
    
    public void run()
    {
      this.loadPending = false;
      loadPlaylistImmediately();
    }
  }
  
  public static abstract interface PlaylistEventListener
  {
    public abstract void onPlaylistBlacklisted(HlsMasterPlaylist.HlsUrl paramHlsUrl, long paramLong);
    
    public abstract void onPlaylistChanged();
  }
  
  public static final class PlaylistResetException
    extends IOException
  {
    public final String url;
    
    private PlaylistResetException(String paramString)
    {
      this.url = paramString;
    }
  }
  
  public static final class PlaylistStuckException
    extends IOException
  {
    public final String url;
    
    private PlaylistStuckException(String paramString)
    {
      this.url = paramString;
    }
  }
  
  public static abstract interface PrimaryPlaylistListener
  {
    public abstract void onPrimaryPlaylistRefreshed(HlsMediaPlaylist paramHlsMediaPlaylist);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/hls/playlist/HlsPlaylistTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */