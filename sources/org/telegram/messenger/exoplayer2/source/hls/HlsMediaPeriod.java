package org.telegram.messenger.exoplayer2.source.hls;

import android.os.Handler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import org.telegram.messenger.exoplayer2.C0539C;
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
        int j;
        TrackSelection[] childSelections;
        HlsMediaPeriod hlsMediaPeriod = this;
        TrackSelection[] trackSelectionArr = selections;
        HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr = streams;
        int[] streamChildIndices = new int[trackSelectionArr.length];
        int[] selectionChildIndices = new int[trackSelectionArr.length];
        for (i = 0; i < trackSelectionArr.length; i++) {
            streamChildIndices[i] = hlsSampleStreamWrapperArr[i] == null ? -1 : ((Integer) hlsMediaPeriod.streamWrapperIndices.get(hlsSampleStreamWrapperArr[i])).intValue();
            selectionChildIndices[i] = -1;
            if (trackSelectionArr[i] != null) {
                TrackGroup trackGroup = trackSelectionArr[i].getTrackGroup();
                for (j = 0; j < hlsMediaPeriod.sampleStreamWrappers.length; j++) {
                    if (hlsMediaPeriod.sampleStreamWrappers[j].getTrackGroups().indexOf(trackGroup) != -1) {
                        selectionChildIndices[i] = j;
                        break;
                    }
                }
            }
        }
        hlsMediaPeriod.streamWrapperIndices.clear();
        SampleStream[] newStreams = new SampleStream[trackSelectionArr.length];
        SampleStream[] childStreams = new SampleStream[trackSelectionArr.length];
        TrackSelection[] childSelections2 = new TrackSelection[trackSelectionArr.length];
        HlsSampleStreamWrapper[] newEnabledSampleStreamWrappers = new HlsSampleStreamWrapper[hlsMediaPeriod.sampleStreamWrappers.length];
        boolean forceReset = false;
        int newEnabledSampleStreamWrapperCount = 0;
        i = 0;
        while (i < hlsMediaPeriod.sampleStreamWrappers.length) {
            j = 0;
            while (j < trackSelectionArr.length) {
                TrackSelection trackSelection = null;
                childStreams[j] = streamChildIndices[j] == i ? hlsSampleStreamWrapperArr[j] : null;
                if (selectionChildIndices[j] == i) {
                    trackSelection = trackSelectionArr[j];
                }
                childSelections2[j] = trackSelection;
                j++;
            }
            HlsSampleStreamWrapper sampleStreamWrapper = hlsMediaPeriod.sampleStreamWrappers[i];
            HlsSampleStreamWrapper sampleStreamWrapper2 = sampleStreamWrapper;
            int newEnabledSampleStreamWrapperCount2 = newEnabledSampleStreamWrapperCount;
            childSelections = childSelections2;
            hlsSampleStreamWrapperArr = newEnabledSampleStreamWrappers;
            boolean wasReset = sampleStreamWrapper.selectTracks(childSelections2, mayRetainStreamFlags, childStreams, streamResetFlags, positionUs, forceReset);
            boolean wrapperEnabled = false;
            int j2 = 0;
            while (true) {
                boolean z = true;
                if (j2 >= trackSelectionArr.length) {
                    break;
                }
                if (selectionChildIndices[j2] == i) {
                    if (childStreams[j2] == null) {
                        z = false;
                    }
                    Assertions.checkState(z);
                    newStreams[j2] = childStreams[j2];
                    wrapperEnabled = true;
                    hlsMediaPeriod.streamWrapperIndices.put(childStreams[j2], Integer.valueOf(i));
                } else if (streamChildIndices[j2] == i) {
                    if (childStreams[j2] != null) {
                        z = false;
                    }
                    Assertions.checkState(z);
                }
                j2++;
            }
            if (wrapperEnabled) {
                hlsSampleStreamWrapperArr[newEnabledSampleStreamWrapperCount2] = sampleStreamWrapper2;
                newEnabledSampleStreamWrapperCount = newEnabledSampleStreamWrapperCount2 + 1;
                if (newEnabledSampleStreamWrapperCount2 == 0) {
                    sampleStreamWrapper2.setIsTimestampMaster(true);
                    if (wasReset || hlsMediaPeriod.enabledSampleStreamWrappers.length == 0 || sampleStreamWrapper2 != hlsMediaPeriod.enabledSampleStreamWrappers[0]) {
                        hlsMediaPeriod.timestampAdjusterProvider.reset();
                        forceReset = true;
                    }
                } else {
                    sampleStreamWrapper2.setIsTimestampMaster(false);
                }
            } else {
                newEnabledSampleStreamWrapperCount = newEnabledSampleStreamWrapperCount2;
            }
            i++;
            newEnabledSampleStreamWrappers = hlsSampleStreamWrapperArr;
            childSelections2 = childSelections;
            Object[] newEnabledSampleStreamWrappers2 = streams;
        }
        childSelections = childSelections2;
        hlsSampleStreamWrapperArr = newEnabledSampleStreamWrappers;
        j = newEnabledSampleStreamWrapperCount;
        System.arraycopy(newStreams, 0, streams, 0, newStreams.length);
        hlsMediaPeriod.enabledSampleStreamWrappers = (HlsSampleStreamWrapper[]) Arrays.copyOf(hlsSampleStreamWrapperArr, j);
        hlsMediaPeriod.compositeSequenceableLoader = hlsMediaPeriod.compositeSequenceableLoaderFactory.createCompositeSequenceableLoader(hlsMediaPeriod.enabledSampleStreamWrappers);
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
        return C0539C.TIME_UNSET;
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
        int i = this.pendingPrepareCount - 1;
        this.pendingPrepareCount = i;
        if (i <= 0) {
            int totalTrackGroupCount = 0;
            for (HlsSampleStreamWrapper sampleStreamWrapper : this.sampleStreamWrappers) {
                totalTrackGroupCount += sampleStreamWrapper.getTrackGroups().length;
            }
            TrackGroup[] trackGroupArray = new TrackGroup[totalTrackGroupCount];
            HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr = this.sampleStreamWrappers;
            int length = hlsSampleStreamWrapperArr.length;
            int trackGroupIndex = 0;
            int trackGroupIndex2 = 0;
            while (trackGroupIndex2 < length) {
                HlsSampleStreamWrapper sampleStreamWrapper2 = hlsSampleStreamWrapperArr[trackGroupIndex2];
                int wrapperTrackGroupCount = sampleStreamWrapper2.getTrackGroups().length;
                int trackGroupIndex3 = trackGroupIndex;
                trackGroupIndex = 0;
                while (trackGroupIndex < wrapperTrackGroupCount) {
                    int trackGroupIndex4 = trackGroupIndex3 + 1;
                    trackGroupArray[trackGroupIndex3] = sampleStreamWrapper2.getTrackGroups().get(trackGroupIndex);
                    trackGroupIndex++;
                    trackGroupIndex3 = trackGroupIndex4;
                }
                trackGroupIndex2++;
                trackGroupIndex = trackGroupIndex3;
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
        int i = 1;
        int wrapperCount = (audioRenditions.size() + 1) + subtitleRenditions.size();
        this.sampleStreamWrappers = new HlsSampleStreamWrapper[wrapperCount];
        this.pendingPrepareCount = wrapperCount;
        long j = positionUs;
        buildAndPrepareMainSampleStreamWrapper(masterPlaylist, j);
        int i2 = 0;
        int currentWrapperIndex = 1;
        int i3 = 0;
        while (true) {
            int i4 = i3;
            if (i4 >= audioRenditions.size()) {
                break;
            }
            HlsUrl audioRendition = (HlsUrl) audioRenditions.get(i4);
            HlsUrl[] hlsUrlArr = new HlsUrl[i];
            hlsUrlArr[i2] = audioRendition;
            int i5 = i4;
            HlsUrl audioRendition2 = audioRendition;
            HlsSampleStreamWrapper sampleStreamWrapper = buildSampleStreamWrapper(1, hlsUrlArr, null, Collections.emptyList(), j);
            int currentWrapperIndex2 = currentWrapperIndex + 1;
            r7.sampleStreamWrappers[currentWrapperIndex] = sampleStreamWrapper;
            Format renditionFormat = audioRendition2.format;
            if (!r7.allowChunklessPreparation || renditionFormat.codecs == null) {
                sampleStreamWrapper.continuePreparing();
            } else {
                TrackGroup[] trackGroupArr = new TrackGroup[i];
                Format[] formatArr = new Format[i];
                formatArr[0] = audioRendition2.format;
                trackGroupArr[0] = new TrackGroup(formatArr);
                sampleStreamWrapper.prepareWithMasterPlaylistInfo(new TrackGroupArray(trackGroupArr), 0);
            }
            i3 = i5 + 1;
            currentWrapperIndex = currentWrapperIndex2;
            i = 1;
            i2 = 0;
        }
        i3 = 0;
        while (true) {
            i = i3;
            if (i < subtitleRenditions.size()) {
                sampleStreamWrapper = buildSampleStreamWrapper(3, new HlsUrl[]{(HlsUrl) subtitleRenditions.get(i)}, null, Collections.emptyList(), j);
                currentWrapperIndex2 = currentWrapperIndex + 1;
                r7.sampleStreamWrappers[currentWrapperIndex] = sampleStreamWrapper;
                trackGroupArr = new TrackGroup[1];
                formatArr = new Format[1];
                int currentWrapperIndex3 = currentWrapperIndex2;
                formatArr[0] = audioRendition2.format;
                trackGroupArr[0] = new TrackGroup(formatArr);
                sampleStreamWrapper.prepareWithMasterPlaylistInfo(new TrackGroupArray(trackGroupArr), 0);
                i3 = i + 1;
                currentWrapperIndex = currentWrapperIndex3;
            } else {
                r7.enabledSampleStreamWrappers = r7.sampleStreamWrappers;
                return;
            }
        }
    }

    private void buildAndPrepareMainSampleStreamWrapper(HlsMasterPlaylist masterPlaylist, long positionUs) {
        HlsMediaPeriod hlsMediaPeriod = this;
        HlsMasterPlaylist hlsMasterPlaylist = masterPlaylist;
        List<HlsUrl> selectedVariants = new ArrayList(hlsMasterPlaylist.variants);
        ArrayList<HlsUrl> definiteVideoVariants = new ArrayList();
        ArrayList<HlsUrl> definiteAudioOnlyVariants = new ArrayList();
        for (int i = 0; i < selectedVariants.size(); i++) {
            HlsUrl variant = (HlsUrl) selectedVariants.get(i);
            Format format = variant.format;
            if (format.height <= 0) {
                if (Util.getCodecsOfType(format.codecs, 2) == null) {
                    if (Util.getCodecsOfType(format.codecs, 1) != null) {
                        definiteAudioOnlyVariants.add(variant);
                    }
                }
            }
            definiteVideoVariants.add(variant);
        }
        if (!definiteVideoVariants.isEmpty()) {
            selectedVariants = definiteVideoVariants;
        } else if (definiteAudioOnlyVariants.size() < selectedVariants.size()) {
            selectedVariants.removeAll(definiteAudioOnlyVariants);
        }
        List<HlsUrl> selectedVariants2 = selectedVariants;
        Assertions.checkArgument(selectedVariants2.isEmpty() ^ true);
        HlsUrl[] variants = (HlsUrl[]) selectedVariants2.toArray(new HlsUrl[0]);
        String codecs = variants[0].format.codecs;
        HlsSampleStreamWrapper sampleStreamWrapper = buildSampleStreamWrapper(0, variants, hlsMasterPlaylist.muxedAudioFormat, hlsMasterPlaylist.muxedCaptionFormats, positionUs);
        hlsMediaPeriod.sampleStreamWrappers[0] = sampleStreamWrapper;
        if (!hlsMediaPeriod.allowChunklessPreparation || codecs == null) {
            sampleStreamWrapper.setIsTimestampMaster(true);
            sampleStreamWrapper.continuePreparing();
            return;
        }
        boolean variantsContainVideoCodecs = Util.getCodecsOfType(codecs, 2) != null;
        boolean variantsContainAudioCodecs = Util.getCodecsOfType(codecs, 1) != null;
        List<TrackGroup> muxedTrackGroups = new ArrayList();
        if (variantsContainVideoCodecs) {
            int i2;
            Format[] videoFormats = new Format[selectedVariants2.size()];
            for (i2 = 0; i2 < videoFormats.length; i2++) {
                videoFormats[i2] = deriveVideoFormat(variants[i2].format);
            }
            muxedTrackGroups.add(new TrackGroup(videoFormats));
            if (variantsContainAudioCodecs) {
                if (hlsMasterPlaylist.muxedAudioFormat == null) {
                    if (!hlsMasterPlaylist.audios.isEmpty()) {
                        boolean z = variantsContainVideoCodecs;
                    }
                }
                Format[] formatArr = new Format[1];
                formatArr[0] = deriveMuxedAudioFormat(variants[0].format, hlsMasterPlaylist.muxedAudioFormat, true);
                muxedTrackGroups.add(new TrackGroup(formatArr));
            }
            variantsContainVideoCodecs = hlsMasterPlaylist.muxedCaptionFormats;
            if (variantsContainVideoCodecs) {
                for (i2 = 0; i2 < variantsContainVideoCodecs.size(); i2++) {
                    muxedTrackGroups.add(new TrackGroup((Format) variantsContainVideoCodecs.get(i2)));
                }
            }
        } else {
            if (variantsContainAudioCodecs) {
                Format[] audioFormats = new Format[selectedVariants2.size()];
                for (int i3 = 0; i3 < audioFormats.length; i3++) {
                    Format variantFormat = variants[i3].format;
                    audioFormats[i3] = deriveMuxedAudioFormat(variantFormat, hlsMasterPlaylist.muxedAudioFormat, variantFormat.bitrate);
                }
                muxedTrackGroups.add(new TrackGroup(audioFormats));
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unexpected codecs attribute: ");
                stringBuilder.append(codecs);
                throw new IllegalArgumentException(stringBuilder.toString());
            }
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
        Format format = variantFormat;
        Format format2 = mediaTagFormat;
        int channelCount = -1;
        int selectionFlags = 0;
        String language = null;
        if (format2 != null) {
            codecs = format2.codecs;
            channelCount = format2.channelCount;
            selectionFlags = format2.selectionFlags;
            language = format2.language;
        } else {
            codecs = Util.getCodecsOfType(format.codecs, 1);
        }
        return Format.createAudioSampleFormat(format.id, MimeTypes.getMediaMimeType(codecs), codecs, bitrate, -1, channelCount, -1, null, null, selectionFlags, language);
    }
}
