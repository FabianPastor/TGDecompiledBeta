package org.telegram.messenger.exoplayer2.source.hls;

import android.net.Uri;
import android.os.SystemClock;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.drm.DrmInitData;
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

class HlsChunkSource {
    private final DataSource encryptionDataSource;
    private byte[] encryptionIv;
    private String encryptionIvString;
    private byte[] encryptionKey;
    private Uri encryptionKeyUri;
    private HlsUrl expectedPlaylistUrl;
    private final HlsExtractorFactory extractorFactory;
    private IOException fatalError;
    private boolean independentSegments;
    private boolean isTimestampMaster;
    private long liveEdgeTimeUs = C0542C.TIME_UNSET;
    private final DataSource mediaDataSource;
    private final List<Format> muxedCaptionFormats;
    private final HlsPlaylistTracker playlistTracker;
    private byte[] scratchSpace;
    private final TimestampAdjusterProvider timestampAdjusterProvider;
    private final TrackGroup trackGroup;
    private TrackSelection trackSelection;
    private final HlsUrl[] variants;

    public static final class HlsChunkHolder {
        public Chunk chunk;
        public boolean endOfStream;
        public HlsUrl playlist;

        public HlsChunkHolder() {
            clear();
        }

        public void clear() {
            this.chunk = null;
            this.endOfStream = false;
            this.playlist = null;
        }
    }

    private static final class InitializationTrackSelection extends BaseTrackSelection {
        private int selectedIndex;

        public InitializationTrackSelection(TrackGroup group, int[] tracks) {
            super(group, tracks);
            this.selectedIndex = indexOf(group.getFormat(0));
        }

        public void updateSelectedTrack(long playbackPositionUs, long bufferedDurationUs, long availableDurationUs) {
            long nowMs = SystemClock.elapsedRealtime();
            if (isBlacklisted(this.selectedIndex, nowMs)) {
                int i = this.length - 1;
                while (i >= 0) {
                    if (isBlacklisted(i, nowMs)) {
                        i--;
                    } else {
                        this.selectedIndex = i;
                        return;
                    }
                }
                throw new IllegalStateException();
            }
        }

        public int getSelectedIndex() {
            return this.selectedIndex;
        }

        public int getSelectionReason() {
            return 0;
        }

        public Object getSelectionData() {
            return null;
        }
    }

    private static final class EncryptionKeyChunk extends DataChunk {
        public final String iv;
        private byte[] result;

        public EncryptionKeyChunk(DataSource dataSource, DataSpec dataSpec, Format trackFormat, int trackSelectionReason, Object trackSelectionData, byte[] scratchSpace, String iv) {
            super(dataSource, dataSpec, 3, trackFormat, trackSelectionReason, trackSelectionData, scratchSpace);
            this.iv = iv;
        }

        protected void consume(byte[] data, int limit) throws IOException {
            this.result = Arrays.copyOf(data, limit);
        }

        public byte[] getResult() {
            return this.result;
        }
    }

    public HlsChunkSource(HlsExtractorFactory extractorFactory, HlsPlaylistTracker playlistTracker, HlsUrl[] variants, HlsDataSourceFactory dataSourceFactory, TimestampAdjusterProvider timestampAdjusterProvider, List<Format> muxedCaptionFormats) {
        this.extractorFactory = extractorFactory;
        this.playlistTracker = playlistTracker;
        this.variants = variants;
        this.timestampAdjusterProvider = timestampAdjusterProvider;
        this.muxedCaptionFormats = muxedCaptionFormats;
        Format[] variantFormats = new Format[variants.length];
        int[] initialTrackSelection = new int[variants.length];
        for (int i = 0; i < variants.length; i++) {
            variantFormats[i] = variants[i].format;
            initialTrackSelection[i] = i;
        }
        this.mediaDataSource = dataSourceFactory.createDataSource(1);
        this.encryptionDataSource = dataSourceFactory.createDataSource(3);
        this.trackGroup = new TrackGroup(variantFormats);
        this.trackSelection = new InitializationTrackSelection(this.trackGroup, initialTrackSelection);
    }

    public void maybeThrowError() throws IOException {
        if (this.fatalError != null) {
            throw this.fatalError;
        } else if (this.expectedPlaylistUrl != null) {
            this.playlistTracker.maybeThrowPlaylistRefreshError(this.expectedPlaylistUrl);
        }
    }

    public TrackGroup getTrackGroup() {
        return this.trackGroup;
    }

    public void selectTracks(TrackSelection trackSelection) {
        this.trackSelection = trackSelection;
    }

    public TrackSelection getTrackSelection() {
        return this.trackSelection;
    }

    public void reset() {
        this.fatalError = null;
    }

    public void setIsTimestampMaster(boolean isTimestampMaster) {
        this.isTimestampMaster = isTimestampMaster;
    }

    public void getNextChunk(HlsMediaChunk previous, long playbackPositionUs, long loadPositionUs, HlsChunkHolder out) {
        int i;
        long bufferedDurationUs;
        long timeToLiveEdgeUs;
        long subtractedDurationUs;
        HlsChunkSource hlsChunkSource = this;
        HlsMediaChunk hlsMediaChunk = previous;
        long j = playbackPositionUs;
        HlsChunkHolder hlsChunkHolder = out;
        if (hlsMediaChunk == null) {
            i = -1;
        } else {
            i = hlsChunkSource.trackGroup.indexOf(hlsMediaChunk.trackFormat);
        }
        int oldVariantIndex = i;
        hlsChunkSource.expectedPlaylistUrl = null;
        long bufferedDurationUs2 = loadPositionUs - j;
        long timeToLiveEdgeUs2 = resolveTimeToLiveEdgeUs(j);
        if (hlsMediaChunk == null || hlsChunkSource.independentSegments) {
            bufferedDurationUs = bufferedDurationUs2;
            timeToLiveEdgeUs = timeToLiveEdgeUs2;
        } else {
            subtractedDurationUs = previous.getDurationUs();
            long bufferedDurationUs3 = Math.max(0, bufferedDurationUs2 - subtractedDurationUs);
            if (timeToLiveEdgeUs2 != C0542C.TIME_UNSET) {
                bufferedDurationUs = bufferedDurationUs3;
                timeToLiveEdgeUs = Math.max(0, timeToLiveEdgeUs2 - subtractedDurationUs);
            } else {
                bufferedDurationUs = bufferedDurationUs3;
                timeToLiveEdgeUs = timeToLiveEdgeUs2;
            }
        }
        hlsChunkSource.trackSelection.updateSelectedTrack(j, bufferedDurationUs, timeToLiveEdgeUs);
        i = hlsChunkSource.trackSelection.getSelectedIndexInTrackGroup();
        boolean switchingVariant = oldVariantIndex != i;
        HlsUrl hlsUrl = hlsChunkSource.variants[i];
        if (hlsChunkSource.playlistTracker.isSnapshotValid(hlsUrl)) {
            int chunkMediaSequence;
            int selectedVariantIndex;
            HlsUrl selectedUrl;
            HlsMediaPlaylist mediaPlaylist;
            int chunkMediaSequence2;
            int chunkIndex;
            Segment segment;
            Uri keyUri;
            DataSpec initDataSpec;
            Segment initSegment;
            long startTimeUs;
            int discontinuitySequence;
            TimestampAdjuster timestampAdjuster;
            int chunkMediaSequence3;
            HlsUrl selectedUrl2;
            DataSpec dataSpec;
            HlsExtractorFactory hlsExtractorFactory;
            DataSource dataSource;
            List list;
            int selectionReason;
            Object selectionData;
            long j2;
            boolean z;
            DrmInitData drmInitData;
            List list2;
            DrmInitData drmInitData2;
            HlsChunkHolder hlsChunkHolder2;
            boolean z2;
            List list3;
            Object valueOf;
            boolean z3;
            HlsMediaPlaylist mediaPlaylist2;
            HlsMediaPlaylist playlistSnapshot = hlsChunkSource.playlistTracker.getPlaylistSnapshot(hlsUrl);
            hlsChunkSource.independentSegments = playlistSnapshot.hasIndependentSegmentsTag;
            updateLiveEdgeTimeUs(playlistSnapshot);
            if (hlsMediaChunk != null) {
                if (!switchingVariant) {
                    chunkMediaSequence = previous.getNextChunkIndex();
                    selectedVariantIndex = i;
                    selectedUrl = hlsUrl;
                    mediaPlaylist = playlistSnapshot;
                    chunkMediaSequence2 = chunkMediaSequence;
                    if (chunkMediaSequence2 >= mediaPlaylist.mediaSequence) {
                        hlsChunkSource.fatalError = new BehindLiveWindowException();
                        return;
                    }
                    chunkIndex = chunkMediaSequence2 - mediaPlaylist.mediaSequence;
                    if (chunkIndex < mediaPlaylist.segments.size()) {
                        if (mediaPlaylist.hasEndTag) {
                            hlsChunkHolder.playlist = selectedUrl;
                            hlsChunkSource.expectedPlaylistUrl = selectedUrl;
                        } else {
                            hlsChunkHolder.endOfStream = true;
                        }
                        return;
                    }
                    segment = (Segment) mediaPlaylist.segments.get(chunkIndex);
                    if (segment.fullSegmentEncryptionKeyUri == null) {
                        keyUri = UriUtil.resolveToUri(mediaPlaylist.baseUri, segment.fullSegmentEncryptionKeyUri);
                        if (keyUri.equals(hlsChunkSource.encryptionKeyUri)) {
                            hlsChunkHolder.chunk = newEncryptionKeyChunk(keyUri, segment.encryptionIV, selectedVariantIndex, hlsChunkSource.trackSelection.getSelectionReason(), hlsChunkSource.trackSelection.getSelectionData());
                            return;
                        }
                        if (!Util.areEqual(segment.encryptionIV, hlsChunkSource.encryptionIvString)) {
                            setEncryptionData(keyUri, segment.encryptionIV, hlsChunkSource.encryptionKey);
                        }
                    } else {
                        clearEncryptionData();
                    }
                    initDataSpec = null;
                    initSegment = mediaPlaylist.initializationSegment;
                    if (initSegment == null) {
                        initDataSpec = new DataSpec(UriUtil.resolveToUri(mediaPlaylist.baseUri, initSegment.url), initSegment.byterangeOffset, initSegment.byterangeLength, null);
                    }
                    startTimeUs = mediaPlaylist.startTimeUs + segment.relativeStartTimeUs;
                    discontinuitySequence = mediaPlaylist.discontinuitySequence + segment.relativeDiscontinuitySequence;
                    timestampAdjuster = hlsChunkSource.timestampAdjusterProvider.getAdjuster(discontinuitySequence);
                    chunkMediaSequence3 = chunkMediaSequence2;
                    selectedUrl2 = selectedUrl;
                    dataSpec = new DataSpec(UriUtil.resolveToUri(mediaPlaylist.baseUri, segment.url), segment.byterangeOffset, segment.byterangeLength, null);
                    hlsExtractorFactory = hlsChunkSource.extractorFactory;
                    dataSource = hlsChunkSource.mediaDataSource;
                    list = hlsChunkSource.muxedCaptionFormats;
                    selectionReason = hlsChunkSource.trackSelection.getSelectionReason();
                    selectionData = hlsChunkSource.trackSelection.getSelectionData();
                    j2 = startTimeUs + segment.durationUs;
                    z = hlsChunkSource.isTimestampMaster;
                    drmInitData = mediaPlaylist.drmInitData;
                    list2 = list;
                    drmInitData2 = drmInitData;
                    hlsChunkHolder2 = out;
                    z2 = z;
                    hlsChunkHolder2.chunk = new HlsMediaChunk(hlsExtractorFactory, dataSource, dataSpec, initDataSpec, selectedUrl2, list2, selectionReason, selectionData, startTimeUs, j2, chunkMediaSequence3, discontinuitySequence, z2, timestampAdjuster, previous, drmInitData2, hlsChunkSource.encryptionKey, hlsChunkSource.encryptionIv);
                    return;
                }
            }
            if (hlsMediaChunk != null) {
                if (!hlsChunkSource.independentSegments) {
                    subtractedDurationUs = hlsMediaChunk.startTimeUs;
                    if (!playlistSnapshot.hasEndTag || subtractedDurationUs < playlistSnapshot.getEndTimeUs()) {
                        list3 = playlistSnapshot.segments;
                        valueOf = Long.valueOf(subtractedDurationUs - playlistSnapshot.startTimeUs);
                        if (hlsChunkSource.playlistTracker.isLive()) {
                            if (hlsMediaChunk == null) {
                                z3 = false;
                                chunkMediaSequence = Util.binarySearchFloor(list3, valueOf, true, z3) + playlistSnapshot.mediaSequence;
                                if (chunkMediaSequence < playlistSnapshot.mediaSequence && hlsMediaChunk != null) {
                                    i = oldVariantIndex;
                                    hlsUrl = hlsChunkSource.variants[i];
                                    mediaPlaylist2 = hlsChunkSource.playlistTracker.getPlaylistSnapshot(hlsUrl);
                                    chunkMediaSequence = previous.getNextChunkIndex();
                                    playlistSnapshot = mediaPlaylist2;
                                }
                            }
                        }
                        z3 = true;
                        chunkMediaSequence = Util.binarySearchFloor(list3, valueOf, true, z3) + playlistSnapshot.mediaSequence;
                        i = oldVariantIndex;
                        hlsUrl = hlsChunkSource.variants[i];
                        mediaPlaylist2 = hlsChunkSource.playlistTracker.getPlaylistSnapshot(hlsUrl);
                        chunkMediaSequence = previous.getNextChunkIndex();
                        playlistSnapshot = mediaPlaylist2;
                    } else {
                        chunkMediaSequence = playlistSnapshot.mediaSequence + playlistSnapshot.segments.size();
                    }
                    selectedVariantIndex = i;
                    selectedUrl = hlsUrl;
                    mediaPlaylist = playlistSnapshot;
                    chunkMediaSequence2 = chunkMediaSequence;
                    if (chunkMediaSequence2 >= mediaPlaylist.mediaSequence) {
                        chunkIndex = chunkMediaSequence2 - mediaPlaylist.mediaSequence;
                        if (chunkIndex < mediaPlaylist.segments.size()) {
                            segment = (Segment) mediaPlaylist.segments.get(chunkIndex);
                            if (segment.fullSegmentEncryptionKeyUri == null) {
                                clearEncryptionData();
                            } else {
                                keyUri = UriUtil.resolveToUri(mediaPlaylist.baseUri, segment.fullSegmentEncryptionKeyUri);
                                if (keyUri.equals(hlsChunkSource.encryptionKeyUri)) {
                                    if (Util.areEqual(segment.encryptionIV, hlsChunkSource.encryptionIvString)) {
                                        setEncryptionData(keyUri, segment.encryptionIV, hlsChunkSource.encryptionKey);
                                    }
                                } else {
                                    hlsChunkHolder.chunk = newEncryptionKeyChunk(keyUri, segment.encryptionIV, selectedVariantIndex, hlsChunkSource.trackSelection.getSelectionReason(), hlsChunkSource.trackSelection.getSelectionData());
                                    return;
                                }
                            }
                            initDataSpec = null;
                            initSegment = mediaPlaylist.initializationSegment;
                            if (initSegment == null) {
                            } else {
                                initDataSpec = new DataSpec(UriUtil.resolveToUri(mediaPlaylist.baseUri, initSegment.url), initSegment.byterangeOffset, initSegment.byterangeLength, null);
                            }
                            startTimeUs = mediaPlaylist.startTimeUs + segment.relativeStartTimeUs;
                            discontinuitySequence = mediaPlaylist.discontinuitySequence + segment.relativeDiscontinuitySequence;
                            timestampAdjuster = hlsChunkSource.timestampAdjusterProvider.getAdjuster(discontinuitySequence);
                            chunkMediaSequence3 = chunkMediaSequence2;
                            selectedUrl2 = selectedUrl;
                            dataSpec = new DataSpec(UriUtil.resolveToUri(mediaPlaylist.baseUri, segment.url), segment.byterangeOffset, segment.byterangeLength, null);
                            hlsExtractorFactory = hlsChunkSource.extractorFactory;
                            dataSource = hlsChunkSource.mediaDataSource;
                            list = hlsChunkSource.muxedCaptionFormats;
                            selectionReason = hlsChunkSource.trackSelection.getSelectionReason();
                            selectionData = hlsChunkSource.trackSelection.getSelectionData();
                            j2 = startTimeUs + segment.durationUs;
                            z = hlsChunkSource.isTimestampMaster;
                            drmInitData = mediaPlaylist.drmInitData;
                            list2 = list;
                            drmInitData2 = drmInitData;
                            hlsChunkHolder2 = out;
                            z2 = z;
                            hlsChunkHolder2.chunk = new HlsMediaChunk(hlsExtractorFactory, dataSource, dataSpec, initDataSpec, selectedUrl2, list2, selectionReason, selectionData, startTimeUs, j2, chunkMediaSequence3, discontinuitySequence, z2, timestampAdjuster, previous, drmInitData2, hlsChunkSource.encryptionKey, hlsChunkSource.encryptionIv);
                            return;
                        }
                        if (mediaPlaylist.hasEndTag) {
                            hlsChunkHolder.playlist = selectedUrl;
                            hlsChunkSource.expectedPlaylistUrl = selectedUrl;
                        } else {
                            hlsChunkHolder.endOfStream = true;
                        }
                        return;
                    }
                    hlsChunkSource.fatalError = new BehindLiveWindowException();
                    return;
                }
            }
            subtractedDurationUs = loadPositionUs;
            if (playlistSnapshot.hasEndTag) {
            }
            list3 = playlistSnapshot.segments;
            valueOf = Long.valueOf(subtractedDurationUs - playlistSnapshot.startTimeUs);
            if (hlsChunkSource.playlistTracker.isLive()) {
                if (hlsMediaChunk == null) {
                    z3 = false;
                    chunkMediaSequence = Util.binarySearchFloor(list3, valueOf, true, z3) + playlistSnapshot.mediaSequence;
                    i = oldVariantIndex;
                    hlsUrl = hlsChunkSource.variants[i];
                    mediaPlaylist2 = hlsChunkSource.playlistTracker.getPlaylistSnapshot(hlsUrl);
                    chunkMediaSequence = previous.getNextChunkIndex();
                    playlistSnapshot = mediaPlaylist2;
                    selectedVariantIndex = i;
                    selectedUrl = hlsUrl;
                    mediaPlaylist = playlistSnapshot;
                    chunkMediaSequence2 = chunkMediaSequence;
                    if (chunkMediaSequence2 >= mediaPlaylist.mediaSequence) {
                        hlsChunkSource.fatalError = new BehindLiveWindowException();
                        return;
                    }
                    chunkIndex = chunkMediaSequence2 - mediaPlaylist.mediaSequence;
                    if (chunkIndex < mediaPlaylist.segments.size()) {
                        if (mediaPlaylist.hasEndTag) {
                            hlsChunkHolder.endOfStream = true;
                        } else {
                            hlsChunkHolder.playlist = selectedUrl;
                            hlsChunkSource.expectedPlaylistUrl = selectedUrl;
                        }
                        return;
                    }
                    segment = (Segment) mediaPlaylist.segments.get(chunkIndex);
                    if (segment.fullSegmentEncryptionKeyUri == null) {
                        keyUri = UriUtil.resolveToUri(mediaPlaylist.baseUri, segment.fullSegmentEncryptionKeyUri);
                        if (keyUri.equals(hlsChunkSource.encryptionKeyUri)) {
                            hlsChunkHolder.chunk = newEncryptionKeyChunk(keyUri, segment.encryptionIV, selectedVariantIndex, hlsChunkSource.trackSelection.getSelectionReason(), hlsChunkSource.trackSelection.getSelectionData());
                            return;
                        }
                        if (Util.areEqual(segment.encryptionIV, hlsChunkSource.encryptionIvString)) {
                            setEncryptionData(keyUri, segment.encryptionIV, hlsChunkSource.encryptionKey);
                        }
                    } else {
                        clearEncryptionData();
                    }
                    initDataSpec = null;
                    initSegment = mediaPlaylist.initializationSegment;
                    if (initSegment == null) {
                        initDataSpec = new DataSpec(UriUtil.resolveToUri(mediaPlaylist.baseUri, initSegment.url), initSegment.byterangeOffset, initSegment.byterangeLength, null);
                    }
                    startTimeUs = mediaPlaylist.startTimeUs + segment.relativeStartTimeUs;
                    discontinuitySequence = mediaPlaylist.discontinuitySequence + segment.relativeDiscontinuitySequence;
                    timestampAdjuster = hlsChunkSource.timestampAdjusterProvider.getAdjuster(discontinuitySequence);
                    chunkMediaSequence3 = chunkMediaSequence2;
                    selectedUrl2 = selectedUrl;
                    dataSpec = new DataSpec(UriUtil.resolveToUri(mediaPlaylist.baseUri, segment.url), segment.byterangeOffset, segment.byterangeLength, null);
                    hlsExtractorFactory = hlsChunkSource.extractorFactory;
                    dataSource = hlsChunkSource.mediaDataSource;
                    list = hlsChunkSource.muxedCaptionFormats;
                    selectionReason = hlsChunkSource.trackSelection.getSelectionReason();
                    selectionData = hlsChunkSource.trackSelection.getSelectionData();
                    j2 = startTimeUs + segment.durationUs;
                    z = hlsChunkSource.isTimestampMaster;
                    drmInitData = mediaPlaylist.drmInitData;
                    list2 = list;
                    drmInitData2 = drmInitData;
                    hlsChunkHolder2 = out;
                    z2 = z;
                    hlsChunkHolder2.chunk = new HlsMediaChunk(hlsExtractorFactory, dataSource, dataSpec, initDataSpec, selectedUrl2, list2, selectionReason, selectionData, startTimeUs, j2, chunkMediaSequence3, discontinuitySequence, z2, timestampAdjuster, previous, drmInitData2, hlsChunkSource.encryptionKey, hlsChunkSource.encryptionIv);
                    return;
                }
            }
            z3 = true;
            chunkMediaSequence = Util.binarySearchFloor(list3, valueOf, true, z3) + playlistSnapshot.mediaSequence;
            i = oldVariantIndex;
            hlsUrl = hlsChunkSource.variants[i];
            mediaPlaylist2 = hlsChunkSource.playlistTracker.getPlaylistSnapshot(hlsUrl);
            chunkMediaSequence = previous.getNextChunkIndex();
            playlistSnapshot = mediaPlaylist2;
            selectedVariantIndex = i;
            selectedUrl = hlsUrl;
            mediaPlaylist = playlistSnapshot;
            chunkMediaSequence2 = chunkMediaSequence;
            if (chunkMediaSequence2 >= mediaPlaylist.mediaSequence) {
                chunkIndex = chunkMediaSequence2 - mediaPlaylist.mediaSequence;
                if (chunkIndex < mediaPlaylist.segments.size()) {
                    segment = (Segment) mediaPlaylist.segments.get(chunkIndex);
                    if (segment.fullSegmentEncryptionKeyUri == null) {
                        clearEncryptionData();
                    } else {
                        keyUri = UriUtil.resolveToUri(mediaPlaylist.baseUri, segment.fullSegmentEncryptionKeyUri);
                        if (keyUri.equals(hlsChunkSource.encryptionKeyUri)) {
                            if (Util.areEqual(segment.encryptionIV, hlsChunkSource.encryptionIvString)) {
                                setEncryptionData(keyUri, segment.encryptionIV, hlsChunkSource.encryptionKey);
                            }
                        } else {
                            hlsChunkHolder.chunk = newEncryptionKeyChunk(keyUri, segment.encryptionIV, selectedVariantIndex, hlsChunkSource.trackSelection.getSelectionReason(), hlsChunkSource.trackSelection.getSelectionData());
                            return;
                        }
                    }
                    initDataSpec = null;
                    initSegment = mediaPlaylist.initializationSegment;
                    if (initSegment == null) {
                    } else {
                        initDataSpec = new DataSpec(UriUtil.resolveToUri(mediaPlaylist.baseUri, initSegment.url), initSegment.byterangeOffset, initSegment.byterangeLength, null);
                    }
                    startTimeUs = mediaPlaylist.startTimeUs + segment.relativeStartTimeUs;
                    discontinuitySequence = mediaPlaylist.discontinuitySequence + segment.relativeDiscontinuitySequence;
                    timestampAdjuster = hlsChunkSource.timestampAdjusterProvider.getAdjuster(discontinuitySequence);
                    chunkMediaSequence3 = chunkMediaSequence2;
                    selectedUrl2 = selectedUrl;
                    dataSpec = new DataSpec(UriUtil.resolveToUri(mediaPlaylist.baseUri, segment.url), segment.byterangeOffset, segment.byterangeLength, null);
                    hlsExtractorFactory = hlsChunkSource.extractorFactory;
                    dataSource = hlsChunkSource.mediaDataSource;
                    list = hlsChunkSource.muxedCaptionFormats;
                    selectionReason = hlsChunkSource.trackSelection.getSelectionReason();
                    selectionData = hlsChunkSource.trackSelection.getSelectionData();
                    j2 = startTimeUs + segment.durationUs;
                    z = hlsChunkSource.isTimestampMaster;
                    drmInitData = mediaPlaylist.drmInitData;
                    list2 = list;
                    drmInitData2 = drmInitData;
                    hlsChunkHolder2 = out;
                    z2 = z;
                    hlsChunkHolder2.chunk = new HlsMediaChunk(hlsExtractorFactory, dataSource, dataSpec, initDataSpec, selectedUrl2, list2, selectionReason, selectionData, startTimeUs, j2, chunkMediaSequence3, discontinuitySequence, z2, timestampAdjuster, previous, drmInitData2, hlsChunkSource.encryptionKey, hlsChunkSource.encryptionIv);
                    return;
                }
                if (mediaPlaylist.hasEndTag) {
                    hlsChunkHolder.playlist = selectedUrl;
                    hlsChunkSource.expectedPlaylistUrl = selectedUrl;
                } else {
                    hlsChunkHolder.endOfStream = true;
                }
                return;
            }
            hlsChunkSource.fatalError = new BehindLiveWindowException();
            return;
        }
        hlsChunkHolder.playlist = hlsUrl;
        hlsChunkSource.expectedPlaylistUrl = hlsUrl;
    }

    public void onChunkLoadCompleted(Chunk chunk) {
        if (chunk instanceof EncryptionKeyChunk) {
            EncryptionKeyChunk encryptionKeyChunk = (EncryptionKeyChunk) chunk;
            this.scratchSpace = encryptionKeyChunk.getDataHolder();
            setEncryptionData(encryptionKeyChunk.dataSpec.uri, encryptionKeyChunk.iv, encryptionKeyChunk.getResult());
        }
    }

    public boolean onChunkLoadError(Chunk chunk, boolean cancelable, IOException error) {
        return cancelable && ChunkedTrackBlacklistUtil.maybeBlacklistTrack(this.trackSelection, this.trackSelection.indexOf(this.trackGroup.indexOf(chunk.trackFormat)), error);
    }

    public void onPlaylistBlacklisted(HlsUrl url, long blacklistMs) {
        int trackGroupIndex = this.trackGroup.indexOf(url.format);
        if (trackGroupIndex != -1) {
            int trackSelectionIndex = this.trackSelection.indexOf(trackGroupIndex);
            if (trackSelectionIndex != -1) {
                this.trackSelection.blacklist(trackSelectionIndex, blacklistMs);
            }
        }
    }

    private long resolveTimeToLiveEdgeUs(long playbackPositionUs) {
        if (this.liveEdgeTimeUs != C0542C.TIME_UNSET) {
            return this.liveEdgeTimeUs - playbackPositionUs;
        }
        return C0542C.TIME_UNSET;
    }

    private void updateLiveEdgeTimeUs(HlsMediaPlaylist mediaPlaylist) {
        this.liveEdgeTimeUs = mediaPlaylist.hasEndTag ? C0542C.TIME_UNSET : mediaPlaylist.getEndTimeUs();
    }

    private EncryptionKeyChunk newEncryptionKeyChunk(Uri keyUri, String iv, int variantIndex, int trackSelectionReason, Object trackSelectionData) {
        return new EncryptionKeyChunk(this.encryptionDataSource, new DataSpec(keyUri, 0, -1, null, 1), this.variants[variantIndex].format, trackSelectionReason, trackSelectionData, this.scratchSpace, iv);
    }

    private void setEncryptionData(Uri keyUri, String iv, byte[] secretKey) {
        String trimmedIv;
        if (Util.toLowerInvariant(iv).startsWith("0x")) {
            trimmedIv = iv.substring(2);
        } else {
            trimmedIv = iv;
        }
        byte[] ivData = new BigInteger(trimmedIv, 16).toByteArray();
        byte[] ivDataWithPadding = new byte[16];
        int offset = ivData.length > 16 ? ivData.length - 16 : 0;
        System.arraycopy(ivData, offset, ivDataWithPadding, (ivDataWithPadding.length - ivData.length) + offset, ivData.length - offset);
        this.encryptionKeyUri = keyUri;
        this.encryptionKey = secretKey;
        this.encryptionIvString = iv;
        this.encryptionIv = ivDataWithPadding;
    }

    private void clearEncryptionData() {
        this.encryptionKeyUri = null;
        this.encryptionKey = null;
        this.encryptionIvString = null;
        this.encryptionIv = null;
    }
}
