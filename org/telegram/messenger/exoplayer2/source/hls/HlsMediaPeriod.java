package org.telegram.messenger.exoplayer2.source.hls;

import android.os.Handler;
import android.text.TextUtils;
import com.coremedia.iso.boxes.sampleentry.AudioSampleEntry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.source.AdaptiveMediaSourceEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.source.CompositeSequenceableLoader;
import org.telegram.messenger.exoplayer2.source.MediaPeriod;
import org.telegram.messenger.exoplayer2.source.SampleStream;
import org.telegram.messenger.exoplayer2.source.TrackGroup;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.source.hls.HlsSampleStreamWrapper.Callback;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsPlaylistTracker;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsPlaylistTracker.PlaylistRefreshCallback;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.upstream.DataSource.Factory;
import org.telegram.messenger.exoplayer2.upstream.Loader;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class HlsMediaPeriod implements MediaPeriod, Callback, PlaylistRefreshCallback {
    private final Allocator allocator;
    private MediaPeriod.Callback callback;
    private final Handler continueLoadingHandler = new Handler();
    private final Factory dataSourceFactory;
    private HlsSampleStreamWrapper[] enabledSampleStreamWrappers;
    private final EventDispatcher eventDispatcher;
    private final Loader manifestFetcher = new Loader("Loader:ManifestFetcher");
    private final int minLoadableRetryCount;
    private int pendingPrepareCount;
    private final HlsPlaylistTracker playlistTracker;
    private final long preparePositionUs;
    private HlsSampleStreamWrapper[] sampleStreamWrappers;
    private boolean seenFirstTrackSelection;
    private CompositeSequenceableLoader sequenceableLoader;
    private final IdentityHashMap<SampleStream, Integer> streamWrapperIndices = new IdentityHashMap();
    private final TimestampAdjusterProvider timestampAdjusterProvider = new TimestampAdjusterProvider();
    private TrackGroupArray trackGroups;

    public HlsMediaPeriod(HlsPlaylistTracker playlistTracker, Factory dataSourceFactory, int minLoadableRetryCount, EventDispatcher eventDispatcher, Allocator allocator, long positionUs) {
        this.playlistTracker = playlistTracker;
        this.dataSourceFactory = dataSourceFactory;
        this.minLoadableRetryCount = minLoadableRetryCount;
        this.eventDispatcher = eventDispatcher;
        this.allocator = allocator;
        this.preparePositionUs = positionUs;
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
        buildAndPrepareSampleStreamWrappers();
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
        if (this.enabledSampleStreamWrappers.length > 0) {
            this.enabledSampleStreamWrappers[0].setIsTimestampMaster(true);
            for (i = 1; i < this.enabledSampleStreamWrappers.length; i++) {
                this.enabledSampleStreamWrappers[i].setIsTimestampMaster(false);
            }
        }
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
        this.timestampAdjusterProvider.reset();
        for (HlsSampleStreamWrapper sampleStreamWrapper : this.enabledSampleStreamWrappers) {
            sampleStreamWrapper.seekTo(positionUs);
        }
        return positionUs;
    }

    public void onPrepared() {
        int i = 0;
        int i2 = this.pendingPrepareCount - 1;
        this.pendingPrepareCount = i2;
        if (i2 <= 0) {
            HlsSampleStreamWrapper sampleStreamWrapper;
            int totalTrackGroupCount = 0;
            for (HlsSampleStreamWrapper sampleStreamWrapper2 : this.sampleStreamWrappers) {
                totalTrackGroupCount += sampleStreamWrapper2.getTrackGroups().length;
            }
            TrackGroup[] trackGroupArray = new TrackGroup[totalTrackGroupCount];
            int trackGroupIndex = 0;
            HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr = this.sampleStreamWrappers;
            int length = hlsSampleStreamWrapperArr.length;
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
        }
    }

    public void onPlaylistRefreshRequired(HlsUrl url) {
        this.playlistTracker.refreshPlaylist(url, this);
    }

    public void onContinueLoadingRequested(HlsSampleStreamWrapper sampleStreamWrapper) {
        if (this.trackGroups != null) {
            this.callback.onContinueLoadingRequested(this);
        }
    }

    public void onPlaylistChanged() {
        if (this.trackGroups != null) {
            this.callback.onContinueLoadingRequested(this);
            return;
        }
        for (HlsSampleStreamWrapper wrapper : this.sampleStreamWrappers) {
            wrapper.continuePreparing();
        }
    }

    public void onPlaylistLoadError(HlsUrl url, IOException error) {
        for (HlsSampleStreamWrapper sampleStreamWrapper : this.enabledSampleStreamWrappers) {
            sampleStreamWrapper.onPlaylistLoadError(url, error);
        }
        this.callback.onContinueLoadingRequested(this);
    }

    private void buildAndPrepareSampleStreamWrappers() {
        int i;
        HlsMasterPlaylist masterPlaylist = this.playlistTracker.getMasterPlaylist();
        List<HlsUrl> selectedVariants = new ArrayList(masterPlaylist.variants);
        ArrayList<HlsUrl> definiteVideoVariants = new ArrayList();
        ArrayList<HlsUrl> definiteAudioOnlyVariants = new ArrayList();
        for (i = 0; i < selectedVariants.size(); i++) {
            HlsUrl variant = (HlsUrl) selectedVariants.get(i);
            if (variant.format.height > 0 || variantHasExplicitCodecWithPrefix(variant, "avc")) {
                definiteVideoVariants.add(variant);
            } else if (variantHasExplicitCodecWithPrefix(variant, AudioSampleEntry.TYPE3)) {
                definiteAudioOnlyVariants.add(variant);
            }
        }
        if (definiteVideoVariants.isEmpty()) {
            if (definiteAudioOnlyVariants.size() < selectedVariants.size()) {
                selectedVariants.removeAll(definiteAudioOnlyVariants);
            }
        } else {
            selectedVariants = definiteVideoVariants;
        }
        List<HlsUrl> audioRenditions = masterPlaylist.audios;
        List<HlsUrl> subtitleRenditions = masterPlaylist.subtitles;
        this.sampleStreamWrappers = new HlsSampleStreamWrapper[((audioRenditions.size() + 1) + subtitleRenditions.size())];
        this.pendingPrepareCount = this.sampleStreamWrappers.length;
        Assertions.checkArgument(!selectedVariants.isEmpty());
        HlsUrl[] variants = new HlsUrl[selectedVariants.size()];
        selectedVariants.toArray(variants);
        HlsSampleStreamWrapper sampleStreamWrapper = buildSampleStreamWrapper(0, variants, masterPlaylist.muxedAudioFormat, masterPlaylist.muxedCaptionFormat);
        int currentWrapperIndex = 0 + 1;
        this.sampleStreamWrappers[0] = sampleStreamWrapper;
        sampleStreamWrapper.setIsTimestampMaster(true);
        sampleStreamWrapper.continuePreparing();
        i = 0;
        int currentWrapperIndex2 = currentWrapperIndex;
        while (i < audioRenditions.size()) {
            sampleStreamWrapper = buildSampleStreamWrapper(1, new HlsUrl[]{(HlsUrl) audioRenditions.get(i)}, null, null);
            currentWrapperIndex = currentWrapperIndex2 + 1;
            this.sampleStreamWrappers[currentWrapperIndex2] = sampleStreamWrapper;
            sampleStreamWrapper.continuePreparing();
            i++;
            currentWrapperIndex2 = currentWrapperIndex;
        }
        i = 0;
        while (i < subtitleRenditions.size()) {
            sampleStreamWrapper = buildSampleStreamWrapper(3, new HlsUrl[]{(HlsUrl) subtitleRenditions.get(i)}, null, null);
            sampleStreamWrapper.prepareSingleTrack(url.format);
            currentWrapperIndex = currentWrapperIndex2 + 1;
            this.sampleStreamWrappers[currentWrapperIndex2] = sampleStreamWrapper;
            i++;
            currentWrapperIndex2 = currentWrapperIndex;
        }
    }

    private HlsSampleStreamWrapper buildSampleStreamWrapper(int trackType, HlsUrl[] variants, Format muxedAudioFormat, Format muxedCaptionFormat) {
        return new HlsSampleStreamWrapper(trackType, this, new HlsChunkSource(this.playlistTracker, variants, this.dataSourceFactory.createDataSource(), this.timestampAdjusterProvider), this.allocator, this.preparePositionUs, muxedAudioFormat, muxedCaptionFormat, this.minLoadableRetryCount, this.eventDispatcher);
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
