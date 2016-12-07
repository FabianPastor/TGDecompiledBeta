package org.telegram.messenger.exoplayer2.source.hls;

import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.source.AdaptiveMediaSourceEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.source.CompositeSequenceableLoader;
import org.telegram.messenger.exoplayer2.source.MediaPeriod;
import org.telegram.messenger.exoplayer2.source.MediaSource.Listener;
import org.telegram.messenger.exoplayer2.source.SampleStream;
import org.telegram.messenger.exoplayer2.source.SinglePeriodTimeline;
import org.telegram.messenger.exoplayer2.source.TrackGroup;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsPlaylist;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsPlaylistParser;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.upstream.DataSource.Factory;
import org.telegram.messenger.exoplayer2.upstream.Loader;
import org.telegram.messenger.exoplayer2.upstream.Loader.Callback;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable;
import org.telegram.messenger.exoplayer2.util.Assertions;

final class HlsMediaPeriod implements MediaPeriod, Callback<ParsingLoadable<HlsPlaylist>>, HlsSampleStreamWrapper.Callback {
    private final Allocator allocator;
    private MediaPeriod.Callback callback;
    private final Handler continueLoadingHandler = new Handler();
    private final Runnable continueLoadingRunnable;
    private final Factory dataSourceFactory;
    private long durationUs;
    private HlsSampleStreamWrapper[] enabledSampleStreamWrappers;
    private final EventDispatcher eventDispatcher;
    private boolean isLive;
    private final Loader manifestFetcher = new Loader("Loader:ManifestFetcher");
    private final HlsPlaylistParser manifestParser = new HlsPlaylistParser();
    private final Uri manifestUri;
    private final int minLoadableRetryCount;
    private int pendingPrepareCount;
    private HlsPlaylist playlist;
    private final long preparePositionUs;
    private HlsSampleStreamWrapper[] sampleStreamWrappers;
    private boolean seenFirstTrackSelection;
    private CompositeSequenceableLoader sequenceableLoader;
    private final Listener sourceListener;
    private final IdentityHashMap<SampleStream, Integer> streamWrapperIndices = new IdentityHashMap();
    private final TimestampAdjusterProvider timestampAdjusterProvider = new TimestampAdjusterProvider();
    private TrackGroupArray trackGroups;

    public HlsMediaPeriod(Uri manifestUri, Factory dataSourceFactory, int minLoadableRetryCount, EventDispatcher eventDispatcher, Listener sourceListener, Allocator allocator, long positionUs) {
        this.manifestUri = manifestUri;
        this.dataSourceFactory = dataSourceFactory;
        this.minLoadableRetryCount = minLoadableRetryCount;
        this.eventDispatcher = eventDispatcher;
        this.sourceListener = sourceListener;
        this.allocator = allocator;
        this.preparePositionUs = positionUs;
        this.continueLoadingRunnable = new Runnable() {
            public void run() {
                HlsMediaPeriod.this.callback.onContinueLoadingRequested(HlsMediaPeriod.this);
            }
        };
    }

    public void release() {
        this.continueLoadingHandler.removeCallbacksAndMessages(null);
        this.manifestFetcher.release();
        if (this.sampleStreamWrappers != null) {
            for (HlsSampleStreamWrapper sampleStreamWrapper : this.sampleStreamWrappers) {
                sampleStreamWrapper.release();
            }
        }
    }

    public void prepare(MediaPeriod.Callback callback) {
        this.callback = callback;
        ParsingLoadable<HlsPlaylist> loadable = new ParsingLoadable(this.dataSourceFactory.createDataSource(), this.manifestUri, 4, this.manifestParser);
        this.eventDispatcher.loadStarted(loadable.dataSpec, loadable.type, this.manifestFetcher.startLoading(loadable, this, this.minLoadableRetryCount));
    }

    public void maybeThrowPrepareError() throws IOException {
        if (this.sampleStreamWrappers == null) {
            this.manifestFetcher.maybeThrowError();
            return;
        }
        for (HlsSampleStreamWrapper sampleStreamWrapper : this.sampleStreamWrappers) {
            sampleStreamWrapper.maybeThrowPrepareError();
        }
    }

    public TrackGroupArray getTrackGroups() {
        return this.trackGroups;
    }

    public long selectTracks(TrackSelection[] selections, boolean[] mayRetainStreamFlags, SampleStream[] streams, boolean[] streamResetFlags, long positionUs) {
        int i;
        int j;
        int[] streamChildIndices = new int[selections.length];
        int[] selectionChildIndices = new int[selections.length];
        for (i = 0; i < selections.length; i++) {
            int i2;
            if (streams[i] == null) {
                i2 = -1;
            } else {
                i2 = ((Integer) this.streamWrapperIndices.get(streams[i])).intValue();
            }
            streamChildIndices[i] = i2;
            selectionChildIndices[i] = -1;
            if (selections[i] != null) {
                TrackGroup trackGroup = selections[i].getTrackGroup();
                for (j = 0; j < this.sampleStreamWrappers.length; j++) {
                    if (this.sampleStreamWrappers[j].getTrackGroups().indexOf(trackGroup) != -1) {
                        selectionChildIndices[i] = j;
                        break;
                    }
                }
            }
        }
        boolean selectedNewTracks = false;
        this.streamWrapperIndices.clear();
        SampleStream[] newStreams = new SampleStream[selections.length];
        SampleStream[] childStreams = new SampleStream[selections.length];
        TrackSelection[] childSelections = new TrackSelection[selections.length];
        ArrayList<HlsSampleStreamWrapper> enabledSampleStreamWrapperList = new ArrayList(this.sampleStreamWrappers.length);
        i = 0;
        while (i < this.sampleStreamWrappers.length) {
            j = 0;
            while (j < selections.length) {
                childStreams[j] = streamChildIndices[j] == i ? streams[j] : null;
                childSelections[j] = selectionChildIndices[j] == i ? selections[j] : null;
                j++;
            }
            selectedNewTracks |= this.sampleStreamWrappers[i].selectTracks(childSelections, mayRetainStreamFlags, childStreams, streamResetFlags, !this.seenFirstTrackSelection);
            boolean wrapperEnabled = false;
            for (j = 0; j < selections.length; j++) {
                if (selectionChildIndices[j] == i) {
                    Assertions.checkState(childStreams[j] != null);
                    newStreams[j] = childStreams[j];
                    wrapperEnabled = true;
                    this.streamWrapperIndices.put(childStreams[j], Integer.valueOf(i));
                } else if (streamChildIndices[j] == i) {
                    Assertions.checkState(childStreams[j] == null);
                }
            }
            if (wrapperEnabled) {
                enabledSampleStreamWrapperList.add(this.sampleStreamWrappers[i]);
            }
            i++;
        }
        System.arraycopy(newStreams, 0, streams, 0, newStreams.length);
        this.enabledSampleStreamWrappers = new HlsSampleStreamWrapper[enabledSampleStreamWrapperList.size()];
        enabledSampleStreamWrapperList.toArray(this.enabledSampleStreamWrappers);
        this.sequenceableLoader = new CompositeSequenceableLoader(this.enabledSampleStreamWrappers);
        if (this.seenFirstTrackSelection && selectedNewTracks) {
            seekToUs(positionUs);
            for (i = 0; i < selections.length; i++) {
                if (streams[i] != null) {
                    streamResetFlags[i] = true;
                }
            }
        }
        this.seenFirstTrackSelection = true;
        return positionUs;
    }

    public boolean continueLoading(long positionUs) {
        return this.sequenceableLoader.continueLoading(positionUs);
    }

    public long getNextLoadPositionUs() {
        return this.sequenceableLoader.getNextLoadPositionUs();
    }

    public long readDiscontinuity() {
        return C.TIME_UNSET;
    }

    public long getBufferedPositionUs() {
        long bufferedPositionUs = Long.MAX_VALUE;
        for (HlsSampleStreamWrapper sampleStreamWrapper : this.enabledSampleStreamWrappers) {
            long rendererBufferedPositionUs = sampleStreamWrapper.getBufferedPositionUs();
            if (rendererBufferedPositionUs != Long.MIN_VALUE) {
                bufferedPositionUs = Math.min(bufferedPositionUs, rendererBufferedPositionUs);
            }
        }
        return bufferedPositionUs == Long.MAX_VALUE ? Long.MIN_VALUE : bufferedPositionUs;
    }

    public long seekToUs(long positionUs) {
        if (this.isLive) {
            positionUs = 0;
        }
        this.timestampAdjusterProvider.reset();
        for (HlsSampleStreamWrapper sampleStreamWrapper : this.enabledSampleStreamWrappers) {
            sampleStreamWrapper.seekTo(positionUs);
        }
        return positionUs;
    }

    public void onLoadCompleted(ParsingLoadable<HlsPlaylist> loadable, long elapsedRealtimeMs, long loadDurationMs) {
        this.eventDispatcher.loadCompleted(loadable.dataSpec, loadable.type, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
        this.playlist = (HlsPlaylist) loadable.getResult();
        buildAndPrepareSampleStreamWrappers();
    }

    public void onLoadCanceled(ParsingLoadable<HlsPlaylist> loadable, long elapsedRealtimeMs, long loadDurationMs, boolean released) {
        this.eventDispatcher.loadCompleted(loadable.dataSpec, loadable.type, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded());
    }

    public int onLoadError(ParsingLoadable<HlsPlaylist> loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error) {
        boolean isFatal = error instanceof ParserException;
        this.eventDispatcher.loadError(loadable.dataSpec, loadable.type, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded(), error, isFatal);
        return isFatal ? 3 : 0;
    }

    public void onPrepared() {
        int i = this.pendingPrepareCount - 1;
        this.pendingPrepareCount = i;
        if (i <= 0) {
            HlsSampleStreamWrapper sampleStreamWrapper;
            this.durationUs = this.sampleStreamWrappers[0].getDurationUs();
            this.isLive = this.sampleStreamWrappers[0].isLive();
            int totalTrackGroupCount = 0;
            for (HlsSampleStreamWrapper sampleStreamWrapper2 : this.sampleStreamWrappers) {
                totalTrackGroupCount += sampleStreamWrapper2.getTrackGroups().length;
            }
            TrackGroup[] trackGroupArray = new TrackGroup[totalTrackGroupCount];
            int trackGroupIndex = 0;
            HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr = this.sampleStreamWrappers;
            int length = hlsSampleStreamWrapperArr.length;
            i = 0;
            while (i < length) {
                sampleStreamWrapper2 = hlsSampleStreamWrapperArr[i];
                int wrapperTrackGroupCount = sampleStreamWrapper2.getTrackGroups().length;
                int j = 0;
                int trackGroupIndex2 = trackGroupIndex;
                while (j < wrapperTrackGroupCount) {
                    trackGroupIndex = trackGroupIndex2 + 1;
                    trackGroupArray[trackGroupIndex2] = sampleStreamWrapper2.getTrackGroups().get(j);
                    j++;
                    trackGroupIndex2 = trackGroupIndex;
                }
                i++;
                trackGroupIndex = trackGroupIndex2;
            }
            this.trackGroups = new TrackGroupArray(trackGroupArray);
            this.callback.onPrepared(this);
            this.sourceListener.onSourceInfoRefreshed(new SinglePeriodTimeline(this.durationUs, this.durationUs, 0, 0, !this.isLive, this.isLive), this.playlist);
        }
    }

    public void onContinueLoadingRequiredInMs(HlsSampleStreamWrapper sampleStreamWrapper, long delayMs) {
        this.continueLoadingHandler.postDelayed(this.continueLoadingRunnable, delayMs);
    }

    public void onContinueLoadingRequested(HlsSampleStreamWrapper sampleStreamWrapper) {
        if (this.trackGroups != null) {
            this.callback.onContinueLoadingRequested(this);
        }
    }

    private void buildAndPrepareSampleStreamWrappers() {
        String baseUri = this.playlist.baseUri;
        if (this.playlist instanceof HlsMediaPlaylist) {
            HlsUrl[] variants = new HlsUrl[]{HlsUrl.createMediaPlaylistHlsUrl(this.playlist.baseUri)};
            this.sampleStreamWrappers = new HlsSampleStreamWrapper[]{buildSampleStreamWrapper(0, baseUri, variants, null, null)};
            this.pendingPrepareCount = 1;
            this.sampleStreamWrappers[0].continuePreparing();
            return;
        }
        int i;
        List<HlsUrl> selectedVariants;
        HlsMasterPlaylist masterPlaylist = (HlsMasterPlaylist) this.playlist;
        List<HlsUrl> arrayList = new ArrayList(masterPlaylist.variants);
        ArrayList<HlsUrl> definiteVideoVariants = new ArrayList();
        ArrayList<HlsUrl> definiteAudioOnlyVariants = new ArrayList();
        for (i = 0; i < arrayList.size(); i++) {
            HlsUrl variant = (HlsUrl) arrayList.get(i);
            if (variant.format.height <= 0) {
                if (!variantHasExplicitCodecWithPrefix(variant, "avc")) {
                    if (variantHasExplicitCodecWithPrefix(variant, AudioSampleEntry.TYPE3)) {
                        definiteAudioOnlyVariants.add(variant);
                    }
                }
            }
            definiteVideoVariants.add(variant);
        }
        if (!definiteVideoVariants.isEmpty()) {
            selectedVariants = definiteVideoVariants;
        } else if (definiteAudioOnlyVariants.size() < arrayList.size()) {
            arrayList.removeAll(definiteAudioOnlyVariants);
        }
        List<HlsUrl> audioVariants = masterPlaylist.audios;
        List<HlsUrl> subtitleVariants = masterPlaylist.subtitles;
        this.sampleStreamWrappers = new HlsSampleStreamWrapper[(((selectedVariants.isEmpty() ? 0 : 1) + audioVariants.size()) + subtitleVariants.size())];
        int i2 = 0;
        this.pendingPrepareCount = this.sampleStreamWrappers.length;
        if (!selectedVariants.isEmpty()) {
            variants = new HlsUrl[selectedVariants.size()];
            selectedVariants.toArray(variants);
            HlsSampleStreamWrapper sampleStreamWrapper = buildSampleStreamWrapper(0, baseUri, variants, masterPlaylist.muxedAudioFormat, masterPlaylist.muxedCaptionFormat);
            int currentWrapperIndex = 0 + 1;
            this.sampleStreamWrappers[0] = sampleStreamWrapper;
            sampleStreamWrapper.continuePreparing();
            i2 = currentWrapperIndex;
        }
        i = 0;
        while (i < audioVariants.size()) {
            sampleStreamWrapper = buildSampleStreamWrapper(1, baseUri, new HlsUrl[]{(HlsUrl) audioVariants.get(i)}, null, null);
            currentWrapperIndex = i2 + 1;
            this.sampleStreamWrappers[i2] = sampleStreamWrapper;
            sampleStreamWrapper.continuePreparing();
            i++;
            i2 = currentWrapperIndex;
        }
        i = 0;
        while (i < subtitleVariants.size()) {
            sampleStreamWrapper = buildSampleStreamWrapper(3, baseUri, new HlsUrl[]{(HlsUrl) subtitleVariants.get(i)}, null, null);
            sampleStreamWrapper.prepareSingleTrack(url.format);
            currentWrapperIndex = i2 + 1;
            this.sampleStreamWrappers[i2] = sampleStreamWrapper;
            i++;
            i2 = currentWrapperIndex;
        }
    }

    private HlsSampleStreamWrapper buildSampleStreamWrapper(int trackType, String baseUri, HlsUrl[] variants, Format muxedAudioFormat, Format muxedCaptionFormat) {
        return new HlsSampleStreamWrapper(trackType, this, new HlsChunkSource(baseUri, variants, this.dataSourceFactory.createDataSource(), this.timestampAdjusterProvider), this.allocator, this.preparePositionUs, muxedAudioFormat, muxedCaptionFormat, this.minLoadableRetryCount, this.eventDispatcher);
    }

    private static boolean variantHasExplicitCodecWithPrefix(HlsUrl variant, String prefix) {
        String codecs = variant.format.codecs;
        if (TextUtils.isEmpty(codecs)) {
            return false;
        }
        for (String codec : codecs.split("(\\s*,\\s*)|(\\s*$)")) {
            if (codec.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
