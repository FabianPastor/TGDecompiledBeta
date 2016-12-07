package org.telegram.messenger.exoplayer2.source.hls.playlist;

import android.net.Uri;
import android.os.Handler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.source.AdaptiveMediaSourceEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist.Segment;
import org.telegram.messenger.exoplayer2.upstream.DataSource.Factory;
import org.telegram.messenger.exoplayer2.upstream.Loader;
import org.telegram.messenger.exoplayer2.upstream.Loader.Callback;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable;
import org.telegram.messenger.exoplayer2.util.UriUtil;

public final class HlsPlaylistTracker implements Callback<ParsingLoadable<HlsPlaylist>> {
    private static final long PLAYLIST_REFRESH_PERIOD_MS = 5000;
    private static final long TIMESTAMP_ADJUSTMENT_THRESHOLD_US = 500000;
    private final Factory dataSourceFactory;
    private final EventDispatcher eventDispatcher;
    private final Loader initialPlaylistLoader = new Loader("HlsPlaylistTracker:MasterPlaylist");
    private final Uri initialPlaylistUri;
    private boolean isLive;
    private HlsMasterPlaylist masterPlaylist;
    private final int minRetryCount;
    private final IdentityHashMap<HlsUrl, MediaPlaylistBundle> playlistBundles = new IdentityHashMap();
    private final HlsPlaylistParser playlistParser = new HlsPlaylistParser();
    private final Handler playlistRefreshHandler = new Handler();
    private HlsUrl primaryHlsUrl;
    private final PrimaryPlaylistListener primaryPlaylistListener;

    public interface PlaylistRefreshCallback {
        void onPlaylistChanged();

        void onPlaylistLoadError(HlsUrl hlsUrl, IOException iOException);
    }

    public interface PrimaryPlaylistListener {
        void onPrimaryPlaylistRefreshed(HlsMediaPlaylist hlsMediaPlaylist);
    }

    private final class MediaPlaylistBundle implements Callback<ParsingLoadable<HlsPlaylist>>, Runnable {
        private PlaylistRefreshCallback callback;
        private HlsMediaPlaylist latestPlaylistSnapshot;
        private final ParsingLoadable<HlsPlaylist> mediaPlaylistLoadable;
        private final Loader mediaPlaylistLoader;
        private final HlsUrl playlistUrl;

        public MediaPlaylistBundle(HlsPlaylistTracker hlsPlaylistTracker, HlsUrl playlistUrl) {
            this(playlistUrl, null);
        }

        public MediaPlaylistBundle(HlsUrl playlistUrl, HlsMediaPlaylist initialSnapshot) {
            this.playlistUrl = playlistUrl;
            this.latestPlaylistSnapshot = initialSnapshot;
            this.mediaPlaylistLoader = new Loader("HlsPlaylistTracker:MediaPlaylist");
            this.mediaPlaylistLoadable = new ParsingLoadable(HlsPlaylistTracker.this.dataSourceFactory.createDataSource(), UriUtil.resolveToUri(HlsPlaylistTracker.this.masterPlaylist.baseUri, playlistUrl.url), 4, HlsPlaylistTracker.this.playlistParser);
        }

        public void release() {
            this.mediaPlaylistLoader.release();
        }

        public void loadPlaylist() {
            if (!this.mediaPlaylistLoader.isLoading()) {
                this.mediaPlaylistLoader.startLoading(this.mediaPlaylistLoadable, this, HlsPlaylistTracker.this.minRetryCount);
            }
        }

        public void setCallback(PlaylistRefreshCallback callback) {
            this.callback = callback;
        }

        public void adjustTimestampsOfPlaylist(int chunkMediaSequence, long adjustedStartTimeUs) {
            ArrayList<Segment> segments = new ArrayList(this.latestPlaylistSnapshot.segments);
            int indexOfChunk = chunkMediaSequence - this.latestPlaylistSnapshot.mediaSequence;
            if (indexOfChunk >= 0) {
                Segment actualSegment = (Segment) segments.get(indexOfChunk);
                if (Math.abs(actualSegment.startTimeUs - adjustedStartTimeUs) >= HlsPlaylistTracker.TIMESTAMP_ADJUSTMENT_THRESHOLD_US) {
                    int i;
                    Segment segment;
                    segments.set(indexOfChunk, actualSegment.copyWithStartTimeUs(adjustedStartTimeUs));
                    for (i = indexOfChunk - 1; i >= 0; i--) {
                        segment = (Segment) segments.get(i);
                        segments.set(i, segment.copyWithStartTimeUs(((Segment) segments.get(i + 1)).startTimeUs - segment.durationUs));
                    }
                    int segmentsSize = segments.size();
                    for (i = indexOfChunk + 1; i < segmentsSize; i++) {
                        segment = (Segment) segments.get(i);
                        segments.set(i, segment.copyWithStartTimeUs(((Segment) segments.get(i - 1)).startTimeUs + segment.durationUs));
                    }
                    this.latestPlaylistSnapshot = this.latestPlaylistSnapshot.copyWithSegments(segments);
                }
            }
        }

        public void onLoadCompleted(ParsingLoadable<HlsPlaylist> loadable, long elapsedRealtimeMs, long loadDurationMs) {
            processLoadedPlaylist((HlsMediaPlaylist) loadable.getResult());
            HlsPlaylistTracker.this.eventDispatcher.loadCompleted(loadable.dataSpec, 4, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
        }

        public void onLoadCanceled(ParsingLoadable<HlsPlaylist> loadable, long elapsedRealtimeMs, long loadDurationMs, boolean released) {
            HlsPlaylistTracker.this.eventDispatcher.loadCanceled(loadable.dataSpec, 4, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
        }

        public int onLoadError(ParsingLoadable<HlsPlaylist> loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error) {
            boolean isFatal = error instanceof ParserException;
            HlsPlaylistTracker.this.eventDispatcher.loadError(loadable.dataSpec, 4, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded(), error, isFatal);
            if (this.callback != null) {
                this.callback.onPlaylistLoadError(this.playlistUrl, error);
            }
            if (isFatal) {
                return 3;
            }
            return HlsPlaylistTracker.this.primaryHlsUrl == this.playlistUrl ? 0 : 2;
        }

        public void run() {
            loadPlaylist();
        }

        private void processLoadedPlaylist(HlsMediaPlaylist loadedMediaPlaylist) {
            boolean shouldScheduleRefresh;
            boolean z = true;
            HlsMediaPlaylist oldPlaylist = this.latestPlaylistSnapshot;
            this.latestPlaylistSnapshot = HlsPlaylistTracker.this.adjustPlaylistTimestamps(oldPlaylist, loadedMediaPlaylist);
            if (oldPlaylist != this.latestPlaylistSnapshot) {
                if (this.callback != null) {
                    this.callback.onPlaylistChanged();
                    this.callback = null;
                }
                HlsPlaylistTracker hlsPlaylistTracker = HlsPlaylistTracker.this;
                HlsUrl hlsUrl = this.playlistUrl;
                HlsMediaPlaylist hlsMediaPlaylist = this.latestPlaylistSnapshot;
                if (oldPlaylist != null) {
                    z = false;
                }
                shouldScheduleRefresh = hlsPlaylistTracker.onPlaylistUpdated(hlsUrl, hlsMediaPlaylist, z);
            } else {
                shouldScheduleRefresh = !loadedMediaPlaylist.hasEndTag;
            }
            if (shouldScheduleRefresh) {
                HlsPlaylistTracker.this.playlistRefreshHandler.postDelayed(this, 5000);
            }
        }
    }

    public HlsPlaylistTracker(Uri initialPlaylistUri, Factory dataSourceFactory, EventDispatcher eventDispatcher, int minRetryCount, PrimaryPlaylistListener primaryPlaylistListener) {
        this.initialPlaylistUri = initialPlaylistUri;
        this.dataSourceFactory = dataSourceFactory;
        this.eventDispatcher = eventDispatcher;
        this.minRetryCount = minRetryCount;
        this.primaryPlaylistListener = primaryPlaylistListener;
    }

    public void start() {
        this.initialPlaylistLoader.startLoading(new ParsingLoadable(this.dataSourceFactory.createDataSource(), this.initialPlaylistUri, 4, this.playlistParser), this, this.minRetryCount);
    }

    public HlsMasterPlaylist getMasterPlaylist() {
        return this.masterPlaylist;
    }

    public HlsMediaPlaylist getPlaylistSnapshot(HlsUrl url) {
        return ((MediaPlaylistBundle) this.playlistBundles.get(url)).latestPlaylistSnapshot;
    }

    public void release() {
        this.initialPlaylistLoader.release();
        for (MediaPlaylistBundle bundle : this.playlistBundles.values()) {
            bundle.release();
        }
        this.playlistRefreshHandler.removeCallbacksAndMessages(null);
        this.playlistBundles.clear();
    }

    public void maybeThrowPrimaryPlaylistRefreshError() throws IOException {
        this.initialPlaylistLoader.maybeThrowError();
        if (this.primaryHlsUrl != null) {
            ((MediaPlaylistBundle) this.playlistBundles.get(this.primaryHlsUrl)).mediaPlaylistLoader.maybeThrowError();
        }
    }

    public void refreshPlaylist(HlsUrl key, PlaylistRefreshCallback callback) {
        MediaPlaylistBundle bundle = (MediaPlaylistBundle) this.playlistBundles.get(key);
        bundle.setCallback(callback);
        bundle.loadPlaylist();
    }

    public boolean isLive() {
        return this.isLive;
    }

    public void onChunkLoaded(HlsUrl hlsUrl, int chunkMediaSequence, long adjustedStartTimeUs) {
        ((MediaPlaylistBundle) this.playlistBundles.get(hlsUrl)).adjustTimestampsOfPlaylist(chunkMediaSequence, adjustedStartTimeUs);
    }

    public void onLoadCompleted(ParsingLoadable<HlsPlaylist> loadable, long elapsedRealtimeMs, long loadDurationMs) {
        HlsMasterPlaylist masterPlaylist;
        HlsPlaylist result = (HlsPlaylist) loadable.getResult();
        boolean isMediaPlaylist = result instanceof HlsMediaPlaylist;
        if (isMediaPlaylist) {
            masterPlaylist = HlsMasterPlaylist.createSingleVariantMasterPlaylist(result.baseUri);
        } else {
            masterPlaylist = (HlsMasterPlaylist) result;
        }
        this.masterPlaylist = masterPlaylist;
        this.primaryHlsUrl = (HlsUrl) masterPlaylist.variants.get(0);
        ArrayList<HlsUrl> urls = new ArrayList();
        urls.addAll(masterPlaylist.variants);
        urls.addAll(masterPlaylist.audios);
        urls.addAll(masterPlaylist.subtitles);
        createBundles(urls);
        MediaPlaylistBundle primaryBundle = (MediaPlaylistBundle) this.playlistBundles.get(this.primaryHlsUrl);
        if (isMediaPlaylist) {
            primaryBundle.processLoadedPlaylist((HlsMediaPlaylist) result);
        } else {
            primaryBundle.loadPlaylist();
        }
        this.eventDispatcher.loadCompleted(loadable.dataSpec, 4, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
    }

    public void onLoadCanceled(ParsingLoadable<HlsPlaylist> loadable, long elapsedRealtimeMs, long loadDurationMs, boolean released) {
        this.eventDispatcher.loadCanceled(loadable.dataSpec, 4, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
    }

    public int onLoadError(ParsingLoadable<HlsPlaylist> loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error) {
        boolean isFatal = error instanceof ParserException;
        this.eventDispatcher.loadError(loadable.dataSpec, 4, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded(), error, isFatal);
        return isFatal ? 3 : 0;
    }

    private void createBundles(List<HlsUrl> urls) {
        int listSize = urls.size();
        for (int i = 0; i < listSize; i++) {
            this.playlistBundles.put(urls.get(i), new MediaPlaylistBundle(this, (HlsUrl) urls.get(i)));
        }
    }

    private boolean onPlaylistUpdated(HlsUrl url, HlsMediaPlaylist newSnapshot, boolean isFirstSnapshot) {
        if (url != this.primaryHlsUrl) {
            return false;
        }
        if (isFirstSnapshot) {
            this.isLive = !newSnapshot.hasEndTag;
        }
        this.primaryPlaylistListener.onPrimaryPlaylistRefreshed(newSnapshot);
        if (newSnapshot.hasEndTag) {
            return false;
        }
        return true;
    }

    private HlsMediaPlaylist adjustPlaylistTimestamps(HlsMediaPlaylist oldPlaylist, HlsMediaPlaylist newPlaylist) {
        HlsMediaPlaylist primaryPlaylistSnapshot = ((MediaPlaylistBundle) this.playlistBundles.get(this.primaryHlsUrl)).latestPlaylistSnapshot;
        if (oldPlaylist != null) {
            List<Segment> oldSegments = oldPlaylist.segments;
            int oldPlaylistSize = oldSegments.size();
            int newPlaylistSize = newPlaylist.segments.size();
            int mediaSequenceOffset = newPlaylist.mediaSequence - oldPlaylist.mediaSequence;
            if ((newPlaylistSize == oldPlaylistSize && mediaSequenceOffset == 0 && oldPlaylist.hasEndTag == newPlaylist.hasEndTag) || mediaSequenceOffset < 0) {
                return oldPlaylist;
            }
            if (mediaSequenceOffset <= oldPlaylistSize) {
                int i;
                ArrayList<Segment> newSegments = new ArrayList(newPlaylistSize);
                for (i = mediaSequenceOffset; i < oldPlaylistSize; i++) {
                    newSegments.add(oldSegments.get(i));
                }
                Segment lastSegment = (Segment) oldSegments.get(oldPlaylistSize - 1);
                for (i = newSegments.size(); i < newPlaylistSize; i++) {
                    lastSegment = ((Segment) newPlaylist.segments.get(i)).copyWithStartTimeUs(lastSegment.startTimeUs + lastSegment.durationUs);
                    newSegments.add(lastSegment);
                }
                return newPlaylist.copyWithSegments(newSegments);
            }
            return newPlaylist.copyWithStartTimeUs(primaryPlaylistSnapshot.getStartTimeUs());
        } else if (primaryPlaylistSnapshot == null) {
            return newPlaylist;
        } else {
            return newPlaylist.copyWithStartTimeUs(primaryPlaylistSnapshot.getStartTimeUs());
        }
    }
}
