package org.telegram.messenger.exoplayer2.source.hls.playlist;

import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import java.io.IOException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil;
import org.telegram.messenger.exoplayer2.source.hls.HlsDataSourceFactory;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist.Segment;
import org.telegram.messenger.exoplayer2.upstream.Loader;
import org.telegram.messenger.exoplayer2.upstream.Loader.Callback;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable.Parser;
import org.telegram.messenger.exoplayer2.util.UriUtil;

public final class HlsPlaylistTracker implements Callback<ParsingLoadable<HlsPlaylist>> {
    private static final double PLAYLIST_STUCK_TARGET_DURATION_COEFFICIENT = 3.5d;
    private final HlsDataSourceFactory dataSourceFactory;
    private final EventDispatcher eventDispatcher;
    private final Loader initialPlaylistLoader = new Loader("HlsPlaylistTracker:MasterPlaylist");
    private final Uri initialPlaylistUri;
    private boolean isLive;
    private final List<PlaylistEventListener> listeners = new ArrayList();
    private HlsMasterPlaylist masterPlaylist;
    private final int minRetryCount;
    private final IdentityHashMap<HlsUrl, MediaPlaylistBundle> playlistBundles = new IdentityHashMap();
    private final Parser<HlsPlaylist> playlistParser;
    private final Handler playlistRefreshHandler = new Handler();
    private HlsUrl primaryHlsUrl;
    private final PrimaryPlaylistListener primaryPlaylistListener;
    private HlsMediaPlaylist primaryUrlSnapshot;

    public interface PlaylistEventListener {
        void onPlaylistBlacklisted(HlsUrl hlsUrl, long j);

        void onPlaylistChanged();
    }

    public static final class PlaylistResetException extends IOException {
        public final String url;

        private PlaylistResetException(String str) {
            this.url = str;
        }
    }

    public static final class PlaylistStuckException extends IOException {
        public final String url;

        private PlaylistStuckException(String str) {
            this.url = str;
        }
    }

    public interface PrimaryPlaylistListener {
        void onPrimaryPlaylistRefreshed(HlsMediaPlaylist hlsMediaPlaylist);
    }

    private final class MediaPlaylistBundle implements Runnable, Callback<ParsingLoadable<HlsPlaylist>> {
        private long blacklistUntilMs;
        private long earliestNextLoadTimeMs;
        private long lastSnapshotChangeMs;
        private long lastSnapshotLoadMs;
        private boolean loadPending;
        private final ParsingLoadable<HlsPlaylist> mediaPlaylistLoadable;
        private final Loader mediaPlaylistLoader = new Loader("HlsPlaylistTracker:MediaPlaylist");
        private IOException playlistError;
        private HlsMediaPlaylist playlistSnapshot;
        private final HlsUrl playlistUrl;

        public MediaPlaylistBundle(HlsUrl hlsUrl) {
            this.playlistUrl = hlsUrl;
            this.mediaPlaylistLoadable = new ParsingLoadable(HlsPlaylistTracker.this.dataSourceFactory.createDataSource(4), UriUtil.resolveToUri(HlsPlaylistTracker.this.masterPlaylist.baseUri, hlsUrl.url), 4, HlsPlaylistTracker.this.playlistParser);
        }

        public HlsMediaPlaylist getPlaylistSnapshot() {
            return this.playlistSnapshot;
        }

        public boolean isSnapshotValid() {
            boolean z = false;
            if (this.playlistSnapshot == null) {
                return false;
            }
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long max = Math.max(30000, C0542C.usToMs(this.playlistSnapshot.durationUs));
            if (this.playlistSnapshot.hasEndTag || this.playlistSnapshot.playlistType == 2 || this.playlistSnapshot.playlistType == 1 || this.lastSnapshotLoadMs + max > elapsedRealtime) {
                z = true;
            }
            return z;
        }

        public void release() {
            this.mediaPlaylistLoader.release();
        }

        public void loadPlaylist() {
            this.blacklistUntilMs = 0;
            if (!this.loadPending) {
                if (!this.mediaPlaylistLoader.isLoading()) {
                    long elapsedRealtime = SystemClock.elapsedRealtime();
                    if (elapsedRealtime < this.earliestNextLoadTimeMs) {
                        this.loadPending = true;
                        HlsPlaylistTracker.this.playlistRefreshHandler.postDelayed(this, this.earliestNextLoadTimeMs - elapsedRealtime);
                    } else {
                        loadPlaylistImmediately();
                    }
                }
            }
        }

        public void maybeThrowPlaylistRefreshError() throws IOException {
            this.mediaPlaylistLoader.maybeThrowError();
            if (this.playlistError != null) {
                throw this.playlistError;
            }
        }

        public void onLoadCompleted(ParsingLoadable<HlsPlaylist> parsingLoadable, long j, long j2) {
            HlsPlaylist hlsPlaylist = (HlsPlaylist) parsingLoadable.getResult();
            if (hlsPlaylist instanceof HlsMediaPlaylist) {
                processLoadedPlaylist((HlsMediaPlaylist) hlsPlaylist);
                HlsPlaylistTracker.this.eventDispatcher.loadCompleted(parsingLoadable.dataSpec, 4, j, j2, parsingLoadable.bytesLoaded());
                return;
            }
            this.playlistError = new ParserException("Loaded playlist has unexpected type.");
        }

        public void onLoadCanceled(ParsingLoadable<HlsPlaylist> parsingLoadable, long j, long j2, boolean z) {
            HlsPlaylistTracker.this.eventDispatcher.loadCanceled(parsingLoadable.dataSpec, 4, j, j2, parsingLoadable.bytesLoaded());
        }

        public int onLoadError(ParsingLoadable<HlsPlaylist> parsingLoadable, long j, long j2, IOException iOException) {
            IOException iOException2 = iOException;
            boolean z = iOException2 instanceof ParserException;
            ParsingLoadable<HlsPlaylist> parsingLoadable2 = parsingLoadable;
            HlsPlaylistTracker.this.eventDispatcher.loadError(parsingLoadable2.dataSpec, 4, j, j2, parsingLoadable2.bytesLoaded(), iOException2, z);
            if (z) {
                return 3;
            }
            boolean z2 = true;
            if (ChunkedTrackBlacklistUtil.shouldBlacklist(iOException)) {
                z2 = blacklistPlaylist();
            }
            return z2 ? 0 : 2;
        }

        public void run() {
            this.loadPending = false;
            loadPlaylistImmediately();
        }

        private void loadPlaylistImmediately() {
            this.mediaPlaylistLoader.startLoading(this.mediaPlaylistLoadable, this, HlsPlaylistTracker.this.minRetryCount);
        }

        private void processLoadedPlaylist(HlsMediaPlaylist hlsMediaPlaylist) {
            HlsMediaPlaylist hlsMediaPlaylist2 = this.playlistSnapshot;
            long elapsedRealtime = SystemClock.elapsedRealtime();
            this.lastSnapshotLoadMs = elapsedRealtime;
            this.playlistSnapshot = HlsPlaylistTracker.this.getLatestPlaylistSnapshot(hlsMediaPlaylist2, hlsMediaPlaylist);
            if (this.playlistSnapshot != hlsMediaPlaylist2) {
                this.playlistError = null;
                this.lastSnapshotChangeMs = elapsedRealtime;
                HlsPlaylistTracker.this.onPlaylistUpdated(this.playlistUrl, this.playlistSnapshot);
            } else if (!this.playlistSnapshot.hasEndTag) {
                if (hlsMediaPlaylist.mediaSequence + hlsMediaPlaylist.segments.size() < this.playlistSnapshot.mediaSequence) {
                    this.playlistError = new PlaylistResetException(this.playlistUrl.url);
                } else if (((double) (elapsedRealtime - this.lastSnapshotChangeMs)) > ((double) C0542C.usToMs(this.playlistSnapshot.targetDurationUs)) * HlsPlaylistTracker.PLAYLIST_STUCK_TARGET_DURATION_COEFFICIENT) {
                    this.playlistError = new PlaylistStuckException(this.playlistUrl.url);
                    blacklistPlaylist();
                }
            }
            this.earliestNextLoadTimeMs = elapsedRealtime + C0542C.usToMs(this.playlistSnapshot != hlsMediaPlaylist2 ? this.playlistSnapshot.targetDurationUs : this.playlistSnapshot.targetDurationUs / 2);
            if (this.playlistUrl == HlsPlaylistTracker.this.primaryHlsUrl && this.playlistSnapshot.hasEndTag == null) {
                loadPlaylist();
            }
        }

        private boolean blacklistPlaylist() {
            this.blacklistUntilMs = SystemClock.elapsedRealtime() + ChunkedTrackBlacklistUtil.DEFAULT_TRACK_BLACKLIST_MS;
            HlsPlaylistTracker.this.notifyPlaylistBlacklisting(this.playlistUrl, ChunkedTrackBlacklistUtil.DEFAULT_TRACK_BLACKLIST_MS);
            return HlsPlaylistTracker.this.primaryHlsUrl == this.playlistUrl && !HlsPlaylistTracker.this.maybeSelectNewPrimaryUrl();
        }
    }

    public HlsPlaylistTracker(Uri uri, HlsDataSourceFactory hlsDataSourceFactory, EventDispatcher eventDispatcher, int i, PrimaryPlaylistListener primaryPlaylistListener, Parser<HlsPlaylist> parser) {
        this.initialPlaylistUri = uri;
        this.dataSourceFactory = hlsDataSourceFactory;
        this.eventDispatcher = eventDispatcher;
        this.minRetryCount = i;
        this.primaryPlaylistListener = primaryPlaylistListener;
        this.playlistParser = parser;
    }

    public void addListener(PlaylistEventListener playlistEventListener) {
        this.listeners.add(playlistEventListener);
    }

    public void removeListener(PlaylistEventListener playlistEventListener) {
        this.listeners.remove(playlistEventListener);
    }

    public void start() {
        this.initialPlaylistLoader.startLoading(new ParsingLoadable(this.dataSourceFactory.createDataSource(4), this.initialPlaylistUri, 4, this.playlistParser), this, this.minRetryCount);
    }

    public HlsMasterPlaylist getMasterPlaylist() {
        return this.masterPlaylist;
    }

    public HlsMediaPlaylist getPlaylistSnapshot(HlsUrl hlsUrl) {
        HlsMediaPlaylist playlistSnapshot = ((MediaPlaylistBundle) this.playlistBundles.get(hlsUrl)).getPlaylistSnapshot();
        if (playlistSnapshot != null) {
            maybeSetPrimaryUrl(hlsUrl);
        }
        return playlistSnapshot;
    }

    public boolean isSnapshotValid(HlsUrl hlsUrl) {
        return ((MediaPlaylistBundle) this.playlistBundles.get(hlsUrl)).isSnapshotValid();
    }

    public void release() {
        this.initialPlaylistLoader.release();
        for (MediaPlaylistBundle release : this.playlistBundles.values()) {
            release.release();
        }
        this.playlistRefreshHandler.removeCallbacksAndMessages(null);
        this.playlistBundles.clear();
    }

    public void maybeThrowPrimaryPlaylistRefreshError() throws IOException {
        this.initialPlaylistLoader.maybeThrowError();
        if (this.primaryHlsUrl != null) {
            maybeThrowPlaylistRefreshError(this.primaryHlsUrl);
        }
    }

    public void maybeThrowPlaylistRefreshError(HlsUrl hlsUrl) throws IOException {
        ((MediaPlaylistBundle) this.playlistBundles.get(hlsUrl)).maybeThrowPlaylistRefreshError();
    }

    public void refreshPlaylist(HlsUrl hlsUrl) {
        ((MediaPlaylistBundle) this.playlistBundles.get(hlsUrl)).loadPlaylist();
    }

    public boolean isLive() {
        return this.isLive;
    }

    public void onLoadCompleted(ParsingLoadable<HlsPlaylist> parsingLoadable, long j, long j2) {
        HlsMasterPlaylist createSingleVariantMasterPlaylist;
        HlsPlaylistTracker hlsPlaylistTracker = this;
        HlsPlaylist hlsPlaylist = (HlsPlaylist) parsingLoadable.getResult();
        boolean z = hlsPlaylist instanceof HlsMediaPlaylist;
        if (z) {
            createSingleVariantMasterPlaylist = HlsMasterPlaylist.createSingleVariantMasterPlaylist(hlsPlaylist.baseUri);
        } else {
            createSingleVariantMasterPlaylist = (HlsMasterPlaylist) hlsPlaylist;
        }
        hlsPlaylistTracker.masterPlaylist = createSingleVariantMasterPlaylist;
        hlsPlaylistTracker.primaryHlsUrl = (HlsUrl) createSingleVariantMasterPlaylist.variants.get(0);
        List arrayList = new ArrayList();
        arrayList.addAll(createSingleVariantMasterPlaylist.variants);
        arrayList.addAll(createSingleVariantMasterPlaylist.audios);
        arrayList.addAll(createSingleVariantMasterPlaylist.subtitles);
        createBundles(arrayList);
        MediaPlaylistBundle mediaPlaylistBundle = (MediaPlaylistBundle) hlsPlaylistTracker.playlistBundles.get(hlsPlaylistTracker.primaryHlsUrl);
        if (z) {
            mediaPlaylistBundle.processLoadedPlaylist((HlsMediaPlaylist) hlsPlaylist);
        } else {
            mediaPlaylistBundle.loadPlaylist();
        }
        ParsingLoadable<HlsPlaylist> parsingLoadable2 = parsingLoadable;
        hlsPlaylistTracker.eventDispatcher.loadCompleted(parsingLoadable2.dataSpec, 4, j, j2, parsingLoadable2.bytesLoaded());
    }

    public void onLoadCanceled(ParsingLoadable<HlsPlaylist> parsingLoadable, long j, long j2, boolean z) {
        this.eventDispatcher.loadCanceled(parsingLoadable.dataSpec, 4, j, j2, parsingLoadable.bytesLoaded());
    }

    public int onLoadError(ParsingLoadable<HlsPlaylist> parsingLoadable, long j, long j2, IOException iOException) {
        IOException iOException2 = iOException;
        boolean z = iOException2 instanceof ParserException;
        ParsingLoadable<HlsPlaylist> parsingLoadable2 = parsingLoadable;
        this.eventDispatcher.loadError(parsingLoadable2.dataSpec, 4, j, j2, parsingLoadable2.bytesLoaded(), iOException2, z);
        return z ? 3 : 0;
    }

    private boolean maybeSelectNewPrimaryUrl() {
        List list = this.masterPlaylist.variants;
        int size = list.size();
        long elapsedRealtime = SystemClock.elapsedRealtime();
        for (int i = 0; i < size; i++) {
            MediaPlaylistBundle mediaPlaylistBundle = (MediaPlaylistBundle) this.playlistBundles.get(list.get(i));
            if (elapsedRealtime > mediaPlaylistBundle.blacklistUntilMs) {
                this.primaryHlsUrl = mediaPlaylistBundle.playlistUrl;
                mediaPlaylistBundle.loadPlaylist();
                return true;
            }
        }
        return false;
    }

    private void maybeSetPrimaryUrl(HlsUrl hlsUrl) {
        if (hlsUrl != this.primaryHlsUrl && this.masterPlaylist.variants.contains(hlsUrl)) {
            if (this.primaryUrlSnapshot == null || !this.primaryUrlSnapshot.hasEndTag) {
                this.primaryHlsUrl = hlsUrl;
                ((MediaPlaylistBundle) this.playlistBundles.get(this.primaryHlsUrl)).loadPlaylist();
            }
        }
    }

    private void createBundles(List<HlsUrl> list) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            HlsUrl hlsUrl = (HlsUrl) list.get(i);
            this.playlistBundles.put(hlsUrl, new MediaPlaylistBundle(hlsUrl));
        }
    }

    private void onPlaylistUpdated(HlsUrl hlsUrl, HlsMediaPlaylist hlsMediaPlaylist) {
        if (hlsUrl == this.primaryHlsUrl) {
            if (this.primaryUrlSnapshot == null) {
                this.isLive = hlsMediaPlaylist.hasEndTag ^ 1;
            }
            this.primaryUrlSnapshot = hlsMediaPlaylist;
            this.primaryPlaylistListener.onPrimaryPlaylistRefreshed(hlsMediaPlaylist);
        }
        HlsMediaPlaylist size = this.listeners.size();
        for (hlsMediaPlaylist = null; hlsMediaPlaylist < size; hlsMediaPlaylist++) {
            ((PlaylistEventListener) this.listeners.get(hlsMediaPlaylist)).onPlaylistChanged();
        }
    }

    private void notifyPlaylistBlacklisting(HlsUrl hlsUrl, long j) {
        int size = this.listeners.size();
        for (int i = 0; i < size; i++) {
            ((PlaylistEventListener) this.listeners.get(i)).onPlaylistBlacklisted(hlsUrl, j);
        }
    }

    private HlsMediaPlaylist getLatestPlaylistSnapshot(HlsMediaPlaylist hlsMediaPlaylist, HlsMediaPlaylist hlsMediaPlaylist2) {
        if (hlsMediaPlaylist2.isNewerThan(hlsMediaPlaylist)) {
            return hlsMediaPlaylist2.copyWith(getLoadedPlaylistStartTimeUs(hlsMediaPlaylist, hlsMediaPlaylist2), getLoadedPlaylistDiscontinuitySequence(hlsMediaPlaylist, hlsMediaPlaylist2));
        }
        return hlsMediaPlaylist2.hasEndTag != null ? hlsMediaPlaylist.copyWithEndTag() : hlsMediaPlaylist;
    }

    private long getLoadedPlaylistStartTimeUs(HlsMediaPlaylist hlsMediaPlaylist, HlsMediaPlaylist hlsMediaPlaylist2) {
        if (hlsMediaPlaylist2.hasProgramDateTime) {
            return hlsMediaPlaylist2.startTimeUs;
        }
        long j = this.primaryUrlSnapshot != null ? this.primaryUrlSnapshot.startTimeUs : 0;
        if (hlsMediaPlaylist == null) {
            return j;
        }
        int size = hlsMediaPlaylist.segments.size();
        Segment firstOldOverlappingSegment = getFirstOldOverlappingSegment(hlsMediaPlaylist, hlsMediaPlaylist2);
        if (firstOldOverlappingSegment != null) {
            return hlsMediaPlaylist.startTimeUs + firstOldOverlappingSegment.relativeStartTimeUs;
        }
        return size == hlsMediaPlaylist2.mediaSequence - hlsMediaPlaylist.mediaSequence ? hlsMediaPlaylist.getEndTimeUs() : j;
    }

    private int getLoadedPlaylistDiscontinuitySequence(HlsMediaPlaylist hlsMediaPlaylist, HlsMediaPlaylist hlsMediaPlaylist2) {
        if (hlsMediaPlaylist2.hasDiscontinuitySequence) {
            return hlsMediaPlaylist2.discontinuitySequence;
        }
        int i = this.primaryUrlSnapshot != null ? this.primaryUrlSnapshot.discontinuitySequence : 0;
        if (hlsMediaPlaylist == null) {
            return i;
        }
        Segment firstOldOverlappingSegment = getFirstOldOverlappingSegment(hlsMediaPlaylist, hlsMediaPlaylist2);
        return firstOldOverlappingSegment != null ? (hlsMediaPlaylist.discontinuitySequence + firstOldOverlappingSegment.relativeDiscontinuitySequence) - ((Segment) hlsMediaPlaylist2.segments.get(0)).relativeDiscontinuitySequence : i;
    }

    private static Segment getFirstOldOverlappingSegment(HlsMediaPlaylist hlsMediaPlaylist, HlsMediaPlaylist hlsMediaPlaylist2) {
        hlsMediaPlaylist2 = hlsMediaPlaylist2.mediaSequence - hlsMediaPlaylist.mediaSequence;
        hlsMediaPlaylist = hlsMediaPlaylist.segments;
        return hlsMediaPlaylist2 < hlsMediaPlaylist.size() ? (Segment) hlsMediaPlaylist.get(hlsMediaPlaylist2) : null;
    }
}
