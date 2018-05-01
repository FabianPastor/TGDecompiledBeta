package org.telegram.messenger.exoplayer2.source.hls;

import android.net.Uri;
import android.os.SystemClock;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import org.telegram.messenger.exoplayer2.C0542C;
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
    private long liveEdgeTimeUs = 1;
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

        public Object getSelectionData() {
            return null;
        }

        public int getSelectionReason() {
            return 0;
        }

        public InitializationTrackSelection(TrackGroup trackGroup, int[] iArr) {
            super(trackGroup, iArr);
            this.selectedIndex = indexOf((Format) trackGroup.getFormat(null));
        }

        public void updateSelectedTrack(long j, long j2, long j3) {
            j = SystemClock.elapsedRealtime();
            if (isBlacklisted(this.selectedIndex, j) != null) {
                j2 = this.length - 1;
                while (j2 >= null) {
                    if (isBlacklisted(j2, j)) {
                        j2--;
                    } else {
                        this.selectedIndex = j2;
                        return;
                    }
                }
                throw new IllegalStateException();
            }
        }

        public int getSelectedIndex() {
            return this.selectedIndex;
        }
    }

    private static final class EncryptionKeyChunk extends DataChunk {
        public final String iv;
        private byte[] result;

        public EncryptionKeyChunk(DataSource dataSource, DataSpec dataSpec, Format format, int i, Object obj, byte[] bArr, String str) {
            super(dataSource, dataSpec, 3, format, i, obj, bArr);
            this.iv = str;
        }

        protected void consume(byte[] bArr, int i) throws IOException {
            this.result = Arrays.copyOf(bArr, i);
        }

        public byte[] getResult() {
            return this.result;
        }
    }

    public HlsChunkSource(HlsExtractorFactory hlsExtractorFactory, HlsPlaylistTracker hlsPlaylistTracker, HlsUrl[] hlsUrlArr, HlsDataSourceFactory hlsDataSourceFactory, TimestampAdjusterProvider timestampAdjusterProvider, List<Format> list) {
        this.extractorFactory = hlsExtractorFactory;
        this.playlistTracker = hlsPlaylistTracker;
        this.variants = hlsUrlArr;
        this.timestampAdjusterProvider = timestampAdjusterProvider;
        this.muxedCaptionFormats = list;
        hlsPlaylistTracker = new Format[hlsUrlArr.length];
        timestampAdjusterProvider = new int[hlsUrlArr.length];
        for (hlsExtractorFactory = null; hlsExtractorFactory < hlsUrlArr.length; hlsExtractorFactory++) {
            hlsPlaylistTracker[hlsExtractorFactory] = hlsUrlArr[hlsExtractorFactory].format;
            timestampAdjusterProvider[hlsExtractorFactory] = hlsExtractorFactory;
        }
        this.mediaDataSource = hlsDataSourceFactory.createDataSource(1);
        this.encryptionDataSource = hlsDataSourceFactory.createDataSource(3);
        this.trackGroup = new TrackGroup(hlsPlaylistTracker);
        this.trackSelection = new InitializationTrackSelection(this.trackGroup, timestampAdjusterProvider);
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

    public void setIsTimestampMaster(boolean z) {
        this.isTimestampMaster = z;
    }

    public void getNextChunk(HlsMediaChunk hlsMediaChunk, long j, long j2, HlsChunkHolder hlsChunkHolder) {
        int i;
        long j3;
        long j4;
        HlsChunkSource hlsChunkSource = this;
        HlsMediaChunk hlsMediaChunk2 = hlsMediaChunk;
        long j5 = j;
        HlsChunkHolder hlsChunkHolder2 = hlsChunkHolder;
        if (hlsMediaChunk2 == null) {
            i = -1;
        } else {
            i = hlsChunkSource.trackGroup.indexOf(hlsMediaChunk2.trackFormat);
        }
        hlsChunkSource.expectedPlaylistUrl = null;
        long j6 = j2 - j5;
        long resolveTimeToLiveEdgeUs = resolveTimeToLiveEdgeUs(j5);
        if (hlsMediaChunk2 == null || hlsChunkSource.independentSegments) {
            j3 = resolveTimeToLiveEdgeUs;
            j4 = j6;
        } else {
            long durationUs = hlsMediaChunk.getDurationUs();
            long max = Math.max(0, j6 - durationUs);
            if (resolveTimeToLiveEdgeUs != C0542C.TIME_UNSET) {
                j4 = max;
                j3 = Math.max(0, resolveTimeToLiveEdgeUs - durationUs);
            } else {
                j4 = max;
                j3 = resolveTimeToLiveEdgeUs;
            }
        }
        hlsChunkSource.trackSelection.updateSelectedTrack(j5, j4, j3);
        int selectedIndexInTrackGroup = hlsChunkSource.trackSelection.getSelectedIndexInTrackGroup();
        boolean z = false;
        boolean z2 = i != selectedIndexInTrackGroup;
        HlsUrl hlsUrl = hlsChunkSource.variants[selectedIndexInTrackGroup];
        if (hlsChunkSource.playlistTracker.isSnapshotValid(hlsUrl)) {
            int nextChunkIndex;
            HlsUrl hlsUrl2;
            Segment segment;
            Uri resolveToUri;
            Segment segment2;
            DataSpec dataSpec;
            long j7;
            TimestampAdjuster adjuster;
            HlsChunkHolder hlsChunkHolder3;
            List list;
            Object valueOf;
            HlsMediaPlaylist playlistSnapshot;
            HlsMediaPlaylist playlistSnapshot2 = hlsChunkSource.playlistTracker.getPlaylistSnapshot(hlsUrl);
            hlsChunkSource.independentSegments = playlistSnapshot2.hasIndependentSegmentsTag;
            updateLiveEdgeTimeUs(playlistSnapshot2);
            if (hlsMediaChunk2 != null) {
                if (!z2) {
                    nextChunkIndex = hlsMediaChunk.getNextChunkIndex();
                    i = selectedIndexInTrackGroup;
                    hlsUrl2 = hlsUrl;
                    if (nextChunkIndex >= playlistSnapshot2.mediaSequence) {
                        hlsChunkSource.fatalError = new BehindLiveWindowException();
                        return;
                    }
                    selectedIndexInTrackGroup = nextChunkIndex - playlistSnapshot2.mediaSequence;
                    if (selectedIndexInTrackGroup < playlistSnapshot2.segments.size()) {
                        if (playlistSnapshot2.hasEndTag) {
                            hlsChunkHolder2.playlist = hlsUrl2;
                            hlsChunkSource.expectedPlaylistUrl = hlsUrl2;
                        } else {
                            hlsChunkHolder2.endOfStream = true;
                        }
                        return;
                    }
                    segment = (Segment) playlistSnapshot2.segments.get(selectedIndexInTrackGroup);
                    if (segment.fullSegmentEncryptionKeyUri == null) {
                        resolveToUri = UriUtil.resolveToUri(playlistSnapshot2.baseUri, segment.fullSegmentEncryptionKeyUri);
                        if (resolveToUri.equals(hlsChunkSource.encryptionKeyUri)) {
                            hlsChunkHolder2.chunk = newEncryptionKeyChunk(resolveToUri, segment.encryptionIV, i, hlsChunkSource.trackSelection.getSelectionReason(), hlsChunkSource.trackSelection.getSelectionData());
                            return;
                        } else if (!Util.areEqual(segment.encryptionIV, hlsChunkSource.encryptionIvString)) {
                            setEncryptionData(resolveToUri, segment.encryptionIV, hlsChunkSource.encryptionKey);
                        }
                    } else {
                        clearEncryptionData();
                    }
                    segment2 = playlistSnapshot2.initializationSegment;
                    dataSpec = segment2 == null ? new DataSpec(UriUtil.resolveToUri(playlistSnapshot2.baseUri, segment2.url), segment2.byterangeOffset, segment2.byterangeLength, null) : null;
                    j7 = playlistSnapshot2.startTimeUs + segment.relativeStartTimeUs;
                    i = playlistSnapshot2.discontinuitySequence + segment.relativeDiscontinuitySequence;
                    adjuster = hlsChunkSource.timestampAdjusterProvider.getAdjuster(i);
                    hlsChunkHolder3 = hlsChunkHolder2;
                    hlsChunkHolder3.chunk = new HlsMediaChunk(hlsChunkSource.extractorFactory, hlsChunkSource.mediaDataSource, new DataSpec(UriUtil.resolveToUri(playlistSnapshot2.baseUri, segment.url), segment.byterangeOffset, segment.byterangeLength, null), dataSpec, hlsUrl2, hlsChunkSource.muxedCaptionFormats, hlsChunkSource.trackSelection.getSelectionReason(), hlsChunkSource.trackSelection.getSelectionData(), j7, j7 + segment.durationUs, nextChunkIndex, i, hlsChunkSource.isTimestampMaster, adjuster, hlsMediaChunk, playlistSnapshot2.drmInitData, hlsChunkSource.encryptionKey, hlsChunkSource.encryptionIv);
                    return;
                }
            }
            if (hlsMediaChunk2 != null) {
                if (!hlsChunkSource.independentSegments) {
                    resolveTimeToLiveEdgeUs = hlsMediaChunk2.startTimeUs;
                    if (!playlistSnapshot2.hasEndTag || resolveTimeToLiveEdgeUs < playlistSnapshot2.getEndTimeUs()) {
                        list = playlistSnapshot2.segments;
                        valueOf = Long.valueOf(resolveTimeToLiveEdgeUs - playlistSnapshot2.startTimeUs);
                        if (!hlsChunkSource.playlistTracker.isLive() || hlsMediaChunk2 == null) {
                            z = true;
                        }
                        nextChunkIndex = Util.binarySearchFloor(list, valueOf, true, z) + playlistSnapshot2.mediaSequence;
                        if (nextChunkIndex < playlistSnapshot2.mediaSequence && hlsMediaChunk2 != null) {
                            hlsUrl = hlsChunkSource.variants[i];
                            playlistSnapshot = hlsChunkSource.playlistTracker.getPlaylistSnapshot(hlsUrl);
                            nextChunkIndex = hlsMediaChunk.getNextChunkIndex();
                            playlistSnapshot2 = playlistSnapshot;
                            selectedIndexInTrackGroup = i;
                        }
                        i = selectedIndexInTrackGroup;
                        hlsUrl2 = hlsUrl;
                        if (nextChunkIndex >= playlistSnapshot2.mediaSequence) {
                            selectedIndexInTrackGroup = nextChunkIndex - playlistSnapshot2.mediaSequence;
                            if (selectedIndexInTrackGroup < playlistSnapshot2.segments.size()) {
                                segment = (Segment) playlistSnapshot2.segments.get(selectedIndexInTrackGroup);
                                if (segment.fullSegmentEncryptionKeyUri == null) {
                                    clearEncryptionData();
                                } else {
                                    resolveToUri = UriUtil.resolveToUri(playlistSnapshot2.baseUri, segment.fullSegmentEncryptionKeyUri);
                                    if (resolveToUri.equals(hlsChunkSource.encryptionKeyUri)) {
                                        hlsChunkHolder2.chunk = newEncryptionKeyChunk(resolveToUri, segment.encryptionIV, i, hlsChunkSource.trackSelection.getSelectionReason(), hlsChunkSource.trackSelection.getSelectionData());
                                        return;
                                    } else if (Util.areEqual(segment.encryptionIV, hlsChunkSource.encryptionIvString)) {
                                        setEncryptionData(resolveToUri, segment.encryptionIV, hlsChunkSource.encryptionKey);
                                    }
                                }
                                segment2 = playlistSnapshot2.initializationSegment;
                                if (segment2 == null) {
                                }
                                j7 = playlistSnapshot2.startTimeUs + segment.relativeStartTimeUs;
                                i = playlistSnapshot2.discontinuitySequence + segment.relativeDiscontinuitySequence;
                                adjuster = hlsChunkSource.timestampAdjusterProvider.getAdjuster(i);
                                hlsChunkHolder3 = hlsChunkHolder2;
                                hlsChunkHolder3.chunk = new HlsMediaChunk(hlsChunkSource.extractorFactory, hlsChunkSource.mediaDataSource, new DataSpec(UriUtil.resolveToUri(playlistSnapshot2.baseUri, segment.url), segment.byterangeOffset, segment.byterangeLength, null), dataSpec, hlsUrl2, hlsChunkSource.muxedCaptionFormats, hlsChunkSource.trackSelection.getSelectionReason(), hlsChunkSource.trackSelection.getSelectionData(), j7, j7 + segment.durationUs, nextChunkIndex, i, hlsChunkSource.isTimestampMaster, adjuster, hlsMediaChunk, playlistSnapshot2.drmInitData, hlsChunkSource.encryptionKey, hlsChunkSource.encryptionIv);
                                return;
                            }
                            if (playlistSnapshot2.hasEndTag) {
                                hlsChunkHolder2.playlist = hlsUrl2;
                                hlsChunkSource.expectedPlaylistUrl = hlsUrl2;
                            } else {
                                hlsChunkHolder2.endOfStream = true;
                            }
                            return;
                        }
                        hlsChunkSource.fatalError = new BehindLiveWindowException();
                        return;
                    }
                    nextChunkIndex = playlistSnapshot2.mediaSequence + playlistSnapshot2.segments.size();
                    i = selectedIndexInTrackGroup;
                    hlsUrl2 = hlsUrl;
                    if (nextChunkIndex >= playlistSnapshot2.mediaSequence) {
                        hlsChunkSource.fatalError = new BehindLiveWindowException();
                        return;
                    }
                    selectedIndexInTrackGroup = nextChunkIndex - playlistSnapshot2.mediaSequence;
                    if (selectedIndexInTrackGroup < playlistSnapshot2.segments.size()) {
                        if (playlistSnapshot2.hasEndTag) {
                            hlsChunkHolder2.endOfStream = true;
                        } else {
                            hlsChunkHolder2.playlist = hlsUrl2;
                            hlsChunkSource.expectedPlaylistUrl = hlsUrl2;
                        }
                        return;
                    }
                    segment = (Segment) playlistSnapshot2.segments.get(selectedIndexInTrackGroup);
                    if (segment.fullSegmentEncryptionKeyUri == null) {
                        resolveToUri = UriUtil.resolveToUri(playlistSnapshot2.baseUri, segment.fullSegmentEncryptionKeyUri);
                        if (resolveToUri.equals(hlsChunkSource.encryptionKeyUri)) {
                            hlsChunkHolder2.chunk = newEncryptionKeyChunk(resolveToUri, segment.encryptionIV, i, hlsChunkSource.trackSelection.getSelectionReason(), hlsChunkSource.trackSelection.getSelectionData());
                            return;
                        } else if (Util.areEqual(segment.encryptionIV, hlsChunkSource.encryptionIvString)) {
                            setEncryptionData(resolveToUri, segment.encryptionIV, hlsChunkSource.encryptionKey);
                        }
                    } else {
                        clearEncryptionData();
                    }
                    segment2 = playlistSnapshot2.initializationSegment;
                    if (segment2 == null) {
                    }
                    j7 = playlistSnapshot2.startTimeUs + segment.relativeStartTimeUs;
                    i = playlistSnapshot2.discontinuitySequence + segment.relativeDiscontinuitySequence;
                    adjuster = hlsChunkSource.timestampAdjusterProvider.getAdjuster(i);
                    hlsChunkHolder3 = hlsChunkHolder2;
                    hlsChunkHolder3.chunk = new HlsMediaChunk(hlsChunkSource.extractorFactory, hlsChunkSource.mediaDataSource, new DataSpec(UriUtil.resolveToUri(playlistSnapshot2.baseUri, segment.url), segment.byterangeOffset, segment.byterangeLength, null), dataSpec, hlsUrl2, hlsChunkSource.muxedCaptionFormats, hlsChunkSource.trackSelection.getSelectionReason(), hlsChunkSource.trackSelection.getSelectionData(), j7, j7 + segment.durationUs, nextChunkIndex, i, hlsChunkSource.isTimestampMaster, adjuster, hlsMediaChunk, playlistSnapshot2.drmInitData, hlsChunkSource.encryptionKey, hlsChunkSource.encryptionIv);
                    return;
                }
            }
            resolveTimeToLiveEdgeUs = j2;
            if (playlistSnapshot2.hasEndTag) {
            }
            list = playlistSnapshot2.segments;
            valueOf = Long.valueOf(resolveTimeToLiveEdgeUs - playlistSnapshot2.startTimeUs);
            z = true;
            nextChunkIndex = Util.binarySearchFloor(list, valueOf, true, z) + playlistSnapshot2.mediaSequence;
            hlsUrl = hlsChunkSource.variants[i];
            playlistSnapshot = hlsChunkSource.playlistTracker.getPlaylistSnapshot(hlsUrl);
            nextChunkIndex = hlsMediaChunk.getNextChunkIndex();
            playlistSnapshot2 = playlistSnapshot;
            selectedIndexInTrackGroup = i;
            i = selectedIndexInTrackGroup;
            hlsUrl2 = hlsUrl;
            if (nextChunkIndex >= playlistSnapshot2.mediaSequence) {
                selectedIndexInTrackGroup = nextChunkIndex - playlistSnapshot2.mediaSequence;
                if (selectedIndexInTrackGroup < playlistSnapshot2.segments.size()) {
                    segment = (Segment) playlistSnapshot2.segments.get(selectedIndexInTrackGroup);
                    if (segment.fullSegmentEncryptionKeyUri == null) {
                        clearEncryptionData();
                    } else {
                        resolveToUri = UriUtil.resolveToUri(playlistSnapshot2.baseUri, segment.fullSegmentEncryptionKeyUri);
                        if (resolveToUri.equals(hlsChunkSource.encryptionKeyUri)) {
                            hlsChunkHolder2.chunk = newEncryptionKeyChunk(resolveToUri, segment.encryptionIV, i, hlsChunkSource.trackSelection.getSelectionReason(), hlsChunkSource.trackSelection.getSelectionData());
                            return;
                        } else if (Util.areEqual(segment.encryptionIV, hlsChunkSource.encryptionIvString)) {
                            setEncryptionData(resolveToUri, segment.encryptionIV, hlsChunkSource.encryptionKey);
                        }
                    }
                    segment2 = playlistSnapshot2.initializationSegment;
                    if (segment2 == null) {
                    }
                    j7 = playlistSnapshot2.startTimeUs + segment.relativeStartTimeUs;
                    i = playlistSnapshot2.discontinuitySequence + segment.relativeDiscontinuitySequence;
                    adjuster = hlsChunkSource.timestampAdjusterProvider.getAdjuster(i);
                    hlsChunkHolder3 = hlsChunkHolder2;
                    hlsChunkHolder3.chunk = new HlsMediaChunk(hlsChunkSource.extractorFactory, hlsChunkSource.mediaDataSource, new DataSpec(UriUtil.resolveToUri(playlistSnapshot2.baseUri, segment.url), segment.byterangeOffset, segment.byterangeLength, null), dataSpec, hlsUrl2, hlsChunkSource.muxedCaptionFormats, hlsChunkSource.trackSelection.getSelectionReason(), hlsChunkSource.trackSelection.getSelectionData(), j7, j7 + segment.durationUs, nextChunkIndex, i, hlsChunkSource.isTimestampMaster, adjuster, hlsMediaChunk, playlistSnapshot2.drmInitData, hlsChunkSource.encryptionKey, hlsChunkSource.encryptionIv);
                    return;
                }
                if (playlistSnapshot2.hasEndTag) {
                    hlsChunkHolder2.playlist = hlsUrl2;
                    hlsChunkSource.expectedPlaylistUrl = hlsUrl2;
                } else {
                    hlsChunkHolder2.endOfStream = true;
                }
                return;
            }
            hlsChunkSource.fatalError = new BehindLiveWindowException();
            return;
        }
        hlsChunkHolder2.playlist = hlsUrl;
        hlsChunkSource.expectedPlaylistUrl = hlsUrl;
    }

    public void onChunkLoadCompleted(Chunk chunk) {
        if (chunk instanceof EncryptionKeyChunk) {
            EncryptionKeyChunk encryptionKeyChunk = (EncryptionKeyChunk) chunk;
            this.scratchSpace = encryptionKeyChunk.getDataHolder();
            setEncryptionData(encryptionKeyChunk.dataSpec.uri, encryptionKeyChunk.iv, encryptionKeyChunk.getResult());
        }
    }

    public boolean onChunkLoadError(Chunk chunk, boolean z, IOException iOException) {
        return (!z || ChunkedTrackBlacklistUtil.maybeBlacklistTrack(this.trackSelection, this.trackSelection.indexOf(this.trackGroup.indexOf(chunk.trackFormat)), iOException) == null) ? null : true;
    }

    public void onPlaylistBlacklisted(HlsUrl hlsUrl, long j) {
        int indexOf = this.trackGroup.indexOf(hlsUrl.format);
        if (indexOf != -1) {
            hlsUrl = this.trackSelection.indexOf(indexOf);
            if (hlsUrl != -1) {
                this.trackSelection.blacklist(hlsUrl, j);
            }
        }
    }

    private long resolveTimeToLiveEdgeUs(long j) {
        if ((this.liveEdgeTimeUs != C0542C.TIME_UNSET ? 1 : null) != null) {
            return this.liveEdgeTimeUs - j;
        }
        return C0542C.TIME_UNSET;
    }

    private void updateLiveEdgeTimeUs(HlsMediaPlaylist hlsMediaPlaylist) {
        this.liveEdgeTimeUs = hlsMediaPlaylist.hasEndTag ? C0542C.TIME_UNSET : hlsMediaPlaylist.getEndTimeUs();
    }

    private EncryptionKeyChunk newEncryptionKeyChunk(Uri uri, String str, int i, int i2, Object obj) {
        return new EncryptionKeyChunk(this.encryptionDataSource, new DataSpec(uri, 0, -1, null, 1), this.variants[i].format, i2, obj, this.scratchSpace, str);
    }

    private void setEncryptionData(Uri uri, String str, byte[] bArr) {
        Object toByteArray = new BigInteger(Util.toLowerInvariant(str).startsWith("0x") ? str.substring(2) : str, 16).toByteArray();
        Object obj = new byte[16];
        int length = toByteArray.length > 16 ? toByteArray.length - 16 : 0;
        System.arraycopy(toByteArray, length, obj, (obj.length - toByteArray.length) + length, toByteArray.length - length);
        this.encryptionKeyUri = uri;
        this.encryptionKey = bArr;
        this.encryptionIvString = str;
        this.encryptionIv = obj;
    }

    private void clearEncryptionData() {
        this.encryptionKeyUri = null;
        this.encryptionKey = null;
        this.encryptionIvString = null;
        this.encryptionIv = null;
    }
}
