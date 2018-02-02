package org.telegram.messenger.exoplayer2.source.hls;

import android.os.Handler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.SeekParameters;
import org.telegram.messenger.exoplayer2.source.CompositeSequenceableLoaderFactory;
import org.telegram.messenger.exoplayer2.source.MediaPeriod;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.source.SampleStream;
import org.telegram.messenger.exoplayer2.source.SequenceableLoader;
import org.telegram.messenger.exoplayer2.source.TrackGroup;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.source.hls.HlsSampleStreamWrapper.Callback;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsPlaylistTracker;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsPlaylistTracker.PlaylistEventListener;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.Util;

public final class HlsMediaPeriod implements MediaPeriod, Callback, PlaylistEventListener {
    private final Allocator allocator;
    private final boolean allowChunklessPreparation;
    private MediaPeriod.Callback callback;
    private SequenceableLoader compositeSequenceableLoader;
    private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
    private final Handler continueLoadingHandler = new Handler();
    private final HlsDataSourceFactory dataSourceFactory;
    private HlsSampleStreamWrapper[] enabledSampleStreamWrappers = new HlsSampleStreamWrapper[0];
    private final EventDispatcher eventDispatcher;
    private final HlsExtractorFactory extractorFactory;
    private final int minLoadableRetryCount;
    private int pendingPrepareCount;
    private final HlsPlaylistTracker playlistTracker;
    private HlsSampleStreamWrapper[] sampleStreamWrappers = new HlsSampleStreamWrapper[0];
    private final IdentityHashMap<SampleStream, Integer> streamWrapperIndices = new IdentityHashMap();
    private final TimestampAdjusterProvider timestampAdjusterProvider = new TimestampAdjusterProvider();
    private TrackGroupArray trackGroups;

    public HlsMediaPeriod(HlsExtractorFactory extractorFactory, HlsPlaylistTracker playlistTracker, HlsDataSourceFactory dataSourceFactory, int minLoadableRetryCount, EventDispatcher eventDispatcher, Allocator allocator, CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory, boolean allowChunklessPreparation) {
        this.extractorFactory = extractorFactory;
        this.playlistTracker = playlistTracker;
        this.dataSourceFactory = dataSourceFactory;
        this.minLoadableRetryCount = minLoadableRetryCount;
        this.eventDispatcher = eventDispatcher;
        this.allocator = allocator;
        this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory;
        this.allowChunklessPreparation = allowChunklessPreparation;
    }

    public void release() {
        this.playlistTracker.removeListener(this);
        this.continueLoadingHandler.removeCallbacksAndMessages(null);
        for (HlsSampleStreamWrapper sampleStreamWrapper : this.sampleStreamWrappers) {
            sampleStreamWrapper.release();
        }
    }

    public void prepare(MediaPeriod.Callback callback, long positionUs) {
        this.callback = callback;
        this.playlistTracker.addListener(this);
        buildAndPrepareSampleStreamWrappers(positionUs);
    }

    public void maybeThrowPrepareError() throws IOException {
        for (HlsSampleStreamWrapper sampleStreamWrapper : this.sampleStreamWrappers) {
            sampleStreamWrapper.maybeThrowPrepareError();
        }
    }

    public TrackGroupArray getTrackGroups() {
        return this.trackGroups;
    }

    public long selectTracks(TrackSelection[] selections, boolean[] mayRetainStreamFlags, SampleStream[] streams, boolean[] streamResetFlags, long positionUs) {
        int i;
        int[] streamChildIndices = new int[selections.length];
        int[] selectionChildIndices = new int[selections.length];
        for (i = 0; i < selections.length; i++) {
            int i2;
            int j;
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
        boolean forceReset = false;
        this.streamWrapperIndices.clear();
        SampleStream[] newStreams = new SampleStream[selections.length];
        SampleStream[] childStreams = new SampleStream[selections.length];
        TrackSelection[] childSelections = new TrackSelection[selections.length];
        int newEnabledSampleStreamWrapperCount = 0;
        HlsSampleStreamWrapper[] newEnabledSampleStreamWrappers = new HlsSampleStreamWrapper[this.sampleStreamWrappers.length];
        i = 0;
        while (i < this.sampleStreamWrappers.length) {
            j = 0;
            while (j < selections.length) {
                childStreams[j] = streamChildIndices[j] == i ? streams[j] : null;
                childSelections[j] = selectionChildIndices[j] == i ? selections[j] : null;
                j++;
            }
            HlsSampleStreamWrapper sampleStreamWrapper = this.sampleStreamWrappers[i];
            boolean wasReset = sampleStreamWrapper.selectTracks(childSelections, mayRetainStreamFlags, childStreams, streamResetFlags, positionUs, forceReset);
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
                newEnabledSampleStreamWrappers[newEnabledSampleStreamWrapperCount] = sampleStreamWrapper;
                int newEnabledSampleStreamWrapperCount2 = newEnabledSampleStreamWrapperCount + 1;
                if (newEnabledSampleStreamWrapperCount == 0) {
                    sampleStreamWrapper.setIsTimestampMaster(true);
                    if (wasReset || this.enabledSampleStreamWrappers.length == 0 || sampleStreamWrapper != this.enabledSampleStreamWrappers[0]) {
                        this.timestampAdjusterProvider.reset();
                        forceReset = true;
                        newEnabledSampleStreamWrapperCount = newEnabledSampleStreamWrapperCount2;
                    }
                } else {
                    sampleStreamWrapper.setIsTimestampMaster(false);
                }
                newEnabledSampleStreamWrapperCount = newEnabledSampleStreamWrapperCount2;
            }
            i++;
        }
        System.arraycopy(newStreams, 0, streams, 0, newStreams.length);
        this.enabledSampleStreamWrappers = (HlsSampleStreamWrapper[]) Arrays.copyOf(newEnabledSampleStreamWrappers, newEnabledSampleStreamWrapperCount);
        this.compositeSequenceableLoader = this.compositeSequenceableLoaderFactory.createCompositeSequenceableLoader(this.enabledSampleStreamWrappers);
        return positionUs;
    }

    public void discardBuffer(long positionUs, boolean toKeyframe) {
        for (HlsSampleStreamWrapper sampleStreamWrapper : this.enabledSampleStreamWrappers) {
            sampleStreamWrapper.discardBuffer(positionUs, toKeyframe);
        }
    }

    public void reevaluateBuffer(long positionUs) {
        this.compositeSequenceableLoader.reevaluateBuffer(positionUs);
    }

    public boolean continueLoading(long positionUs) {
        return this.compositeSequenceableLoader.continueLoading(positionUs);
    }

    public long getNextLoadPositionUs() {
        return this.compositeSequenceableLoader.getNextLoadPositionUs();
    }

    public long readDiscontinuity() {
        return C.TIME_UNSET;
    }

    public long getBufferedPositionUs() {
        return this.compositeSequenceableLoader.getBufferedPositionUs();
    }

    public long seekToUs(long positionUs) {
        if (this.enabledSampleStreamWrappers.length > 0) {
            boolean forceReset = this.enabledSampleStreamWrappers[0].seekToUs(positionUs, false);
            for (int i = 1; i < this.enabledSampleStreamWrappers.length; i++) {
                this.enabledSampleStreamWrappers[i].seekToUs(positionUs, forceReset);
            }
            if (forceReset) {
                this.timestampAdjusterProvider.reset();
            }
        }
        return positionUs;
    }

    public long getAdjustedSeekPositionUs(long positionUs, SeekParameters seekParameters) {
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
        this.playlistTracker.refreshPlaylist(url);
    }

    public void onContinueLoadingRequested(HlsSampleStreamWrapper sampleStreamWrapper) {
        if (this.trackGroups != null) {
            this.callback.onContinueLoadingRequested(this);
        }
    }

    public void onPlaylistChanged() {
        continuePreparingOrLoading();
    }

    public void onPlaylistBlacklisted(HlsUrl url, long blacklistMs) {
        for (HlsSampleStreamWrapper streamWrapper : this.sampleStreamWrappers) {
            streamWrapper.onPlaylistBlacklisted(url, blacklistMs);
        }
        continuePreparingOrLoading();
    }

    private void buildAndPrepareSampleStreamWrappers(long positionUs) {
        HlsMasterPlaylist masterPlaylist = this.playlistTracker.getMasterPlaylist();
        List<HlsUrl> audioRenditions = masterPlaylist.audios;
        List<HlsUrl> subtitleRenditions = masterPlaylist.subtitles;
        int wrapperCount = (audioRenditions.size() + 1) + subtitleRenditions.size();
        this.sampleStreamWrappers = new HlsSampleStreamWrapper[wrapperCount];
        this.pendingPrepareCount = wrapperCount;
        buildAndPrepareMainSampleStreamWrapper(masterPlaylist, positionUs);
        int currentWrapperIndex = 1;
        int i = 0;
        while (i < audioRenditions.size()) {
            HlsSampleStreamWrapper sampleStreamWrapper = buildSampleStreamWrapper(1, new HlsUrl[]{(HlsUrl) audioRenditions.get(i)}, null, Collections.emptyList(), positionUs);
            int currentWrapperIndex2 = currentWrapperIndex + 1;
            this.sampleStreamWrappers[currentWrapperIndex] = sampleStreamWrapper;
            Format renditionFormat = audioRendition.format;
            if (!this.allowChunklessPreparation || renditionFormat.codecs == null) {
                sampleStreamWrapper.continuePreparing();
            } else {
                TrackGroup[] trackGroupArr = new TrackGroup[1];
                trackGroupArr[0] = new TrackGroup(audioRendition.format);
                sampleStreamWrapper.prepareWithMasterPlaylistInfo(new TrackGroupArray(trackGroupArr), 0);
            }
            i++;
            currentWrapperIndex = currentWrapperIndex2;
        }
        i = 0;
        while (i < subtitleRenditions.size()) {
            HlsUrl url = (HlsUrl) subtitleRenditions.get(i);
            sampleStreamWrapper = buildSampleStreamWrapper(3, new HlsUrl[]{url}, null, Collections.emptyList(), positionUs);
            currentWrapperIndex2 = currentWrapperIndex + 1;
            this.sampleStreamWrappers[currentWrapperIndex] = sampleStreamWrapper;
            trackGroupArr = new TrackGroup[1];
            trackGroupArr[0] = new TrackGroup(url.format);
            sampleStreamWrapper.prepareWithMasterPlaylistInfo(new TrackGroupArray(trackGroupArr), 0);
            i++;
            currentWrapperIndex = currentWrapperIndex2;
        }
        this.enabledSampleStreamWrappers = this.sampleStreamWrappers;
    }

    private void buildAndPrepareMainSampleStreamWrapper(HlsMasterPlaylist masterPlaylist, long positionUs) {
        int i;
        List<HlsUrl> selectedVariants;
        List<HlsUrl> arrayList = new ArrayList(masterPlaylist.variants);
        ArrayList<HlsUrl> definiteVideoVariants = new ArrayList();
        ArrayList<HlsUrl> definiteAudioOnlyVariants = new ArrayList();
        for (i = 0; i < arrayList.size(); i++) {
            HlsUrl variant = (HlsUrl) arrayList.get(i);
            Format format = variant.format;
            if (format.height > 0 || Util.getCodecsOfType(format.codecs, 2) != null) {
                definiteVideoVariants.add(variant);
            } else if (Util.getCodecsOfType(format.codecs, 1) != null) {
                definiteAudioOnlyVariants.add(variant);
            }
        }
        if (!definiteVideoVariants.isEmpty()) {
            selectedVariants = definiteVideoVariants;
        } else if (definiteAudioOnlyVariants.size() < arrayList.size()) {
            arrayList.removeAll(definiteAudioOnlyVariants);
        }
        Assertions.checkArgument(!selectedVariants.isEmpty());
        HlsUrl[] variants = (HlsUrl[]) selectedVariants.toArray(new HlsUrl[0]);
        String codecs = variants[0].format.codecs;
        HlsSampleStreamWrapper sampleStreamWrapper = buildSampleStreamWrapper(0, variants, masterPlaylist.muxedAudioFormat, masterPlaylist.muxedCaptionFormats, positionUs);
        this.sampleStreamWrappers[0] = sampleStreamWrapper;
        if (!this.allowChunklessPreparation || codecs == null) {
            sampleStreamWrapper.setIsTimestampMaster(true);
            sampleStreamWrapper.continuePreparing();
            return;
        }
        boolean variantsContainVideoCodecs = Util.getCodecsOfType(codecs, 2) != null;
        boolean variantsContainAudioCodecs = Util.getCodecsOfType(codecs, 1) != null;
        List<TrackGroup> muxedTrackGroups = new ArrayList();
        if (variantsContainVideoCodecs) {
            Format[] videoFormats = new Format[selectedVariants.size()];
            for (i = 0; i < videoFormats.length; i++) {
                videoFormats[i] = deriveVideoFormat(variants[i].format);
            }
            muxedTrackGroups.add(new TrackGroup(videoFormats));
            if (variantsContainAudioCodecs && (masterPlaylist.muxedAudioFormat != null || masterPlaylist.audios.isEmpty())) {
                muxedTrackGroups.add(new TrackGroup(deriveMuxedAudioFormat(variants[0].format, masterPlaylist.muxedAudioFormat, -1)));
            }
            List<Format> ccFormats = masterPlaylist.muxedCaptionFormats;
            if (ccFormats != null) {
                for (i = 0; i < ccFormats.size(); i++) {
                    muxedTrackGroups.add(new TrackGroup((Format) ccFormats.get(i)));
                }
            }
        } else if (variantsContainAudioCodecs) {
            Format[] audioFormats = new Format[selectedVariants.size()];
            for (i = 0; i < audioFormats.length; i++) {
                Format variantFormat = variants[i].format;
                audioFormats[i] = deriveMuxedAudioFormat(variantFormat, masterPlaylist.muxedAudioFormat, variantFormat.bitrate);
            }
            muxedTrackGroups.add(new TrackGroup(audioFormats));
        } else {
            throw new IllegalArgumentException("Unexpected codecs attribute: " + codecs);
        }
        sampleStreamWrapper.prepareWithMasterPlaylistInfo(new TrackGroupArray((TrackGroup[]) muxedTrackGroups.toArray(new TrackGroup[0])), 0);
    }

    private HlsSampleStreamWrapper buildSampleStreamWrapper(int trackType, HlsUrl[] variants, Format muxedAudioFormat, List<Format> muxedCaptionFormats, long positionUs) {
        return new HlsSampleStreamWrapper(trackType, this, new HlsChunkSource(this.extractorFactory, this.playlistTracker, variants, this.dataSourceFactory, this.timestampAdjusterProvider, muxedCaptionFormats), this.allocator, positionUs, muxedAudioFormat, this.minLoadableRetryCount, this.eventDispatcher);
    }

    private void continuePreparingOrLoading() {
        if (this.trackGroups != null) {
            this.callback.onContinueLoadingRequested(this);
            return;
        }
        for (HlsSampleStreamWrapper wrapper : this.sampleStreamWrappers) {
            wrapper.continuePreparing();
        }
    }

    private static Format deriveVideoFormat(Format variantFormat) {
        String codecs = Util.getCodecsOfType(variantFormat.codecs, 2);
        return Format.createVideoSampleFormat(variantFormat.id, MimeTypes.getMediaMimeType(codecs), codecs, variantFormat.bitrate, -1, variantFormat.width, variantFormat.height, variantFormat.frameRate, null, null);
    }

    private static Format deriveMuxedAudioFormat(Format variantFormat, Format mediaTagFormat, int bitrate) {
        String codecs;
        int channelCount = -1;
        int selectionFlags = 0;
        String language = null;
        if (mediaTagFormat != null) {
            codecs = mediaTagFormat.codecs;
            channelCount = mediaTagFormat.channelCount;
            selectionFlags = mediaTagFormat.selectionFlags;
            language = mediaTagFormat.language;
        } else {
            codecs = Util.getCodecsOfType(variantFormat.codecs, 1);
        }
        return Format.createAudioSampleFormat(variantFormat.id, MimeTypes.getMediaMimeType(codecs), codecs, bitrate, -1, channelCount, -1, null, null, selectionFlags, language);
    }
}
