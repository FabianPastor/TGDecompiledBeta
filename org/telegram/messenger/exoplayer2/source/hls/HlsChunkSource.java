package org.telegram.messenger.exoplayer2.source.hls;

import android.net.Uri;
import android.os.SystemClock;
import android.text.TextUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Locale;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.TimestampAdjuster;
import org.telegram.messenger.exoplayer2.extractor.mp3.Mp3Extractor;
import org.telegram.messenger.exoplayer2.extractor.mp4.FragmentedMp4Extractor;
import org.telegram.messenger.exoplayer2.extractor.ts.Ac3Extractor;
import org.telegram.messenger.exoplayer2.extractor.ts.AdtsExtractor;
import org.telegram.messenger.exoplayer2.extractor.ts.DefaultStreamReaderFactory;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;
import org.telegram.messenger.exoplayer2.source.BehindLiveWindowException;
import org.telegram.messenger.exoplayer2.source.TrackGroup;
import org.telegram.messenger.exoplayer2.source.chunk.Chunk;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil;
import org.telegram.messenger.exoplayer2.source.chunk.DataChunk;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist.Segment;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsPlaylistParser;
import org.telegram.messenger.exoplayer2.trackselection.BaseTrackSelection;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.UriUtil;
import org.telegram.messenger.exoplayer2.util.Util;

class HlsChunkSource {
    private static final String AAC_FILE_EXTENSION = ".aac";
    private static final String AC3_FILE_EXTENSION = ".ac3";
    public static final long DEFAULT_PLAYLIST_BLACKLIST_MS = 60000;
    private static final String EC3_FILE_EXTENSION = ".ec3";
    private static final double LIVE_VARIANT_SWITCH_SAFETY_EXTRA_SECS = 2.0d;
    private static final String MP3_FILE_EXTENSION = ".mp3";
    private static final String MP4_FILE_EXTENSION = ".mp4";
    private static final String VTT_FILE_EXTENSION = ".vtt";
    private static final String WEBVTT_FILE_EXTENSION = ".webvtt";
    private final String baseUri;
    private final DataSource dataSource;
    private long durationUs;
    private byte[] encryptionIv;
    private String encryptionIvString;
    private byte[] encryptionKey;
    private Uri encryptionKeyUri;
    private IOException fatalError;
    private HlsInitializationChunk lastLoadedInitializationChunk;
    private boolean live;
    private final HlsPlaylistParser playlistParser = new HlsPlaylistParser();
    private byte[] scratchSpace;
    private final TimestampAdjusterProvider timestampAdjusterProvider;
    private final TrackGroup trackGroup;
    private TrackSelection trackSelection;
    private final long[] variantLastPlaylistLoadTimesMs;
    private final HlsMediaPlaylist[] variantPlaylists;
    private final HlsUrl[] variants;

    public static final class HlsChunkHolder {
        public Chunk chunk;
        public boolean endOfStream;
        public long retryInMs;

        public HlsChunkHolder() {
            clear();
        }

        public void clear() {
            this.chunk = null;
            this.endOfStream = false;
            this.retryInMs = C.TIME_UNSET;
        }
    }

    private static final class InitializationTrackSelection extends BaseTrackSelection {
        private int selectedIndex;

        public InitializationTrackSelection(TrackGroup group, int[] tracks) {
            super(group, tracks);
            this.selectedIndex = indexOf(group.getFormat(0));
        }

        public void updateSelectedTrack(long bufferedDurationUs) {
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

    private static final class MediaPlaylistChunk extends DataChunk {
        private final HlsPlaylistParser playlistParser;
        private final Uri playlistUri;
        private HlsMediaPlaylist result;
        public final int variantIndex;

        public MediaPlaylistChunk(DataSource dataSource, DataSpec dataSpec, Format trackFormat, int trackSelectionReason, Object trackSelectionData, byte[] scratchSpace, HlsPlaylistParser playlistParser, int variantIndex, Uri playlistUri) {
            super(dataSource, dataSpec, 4, trackFormat, trackSelectionReason, trackSelectionData, scratchSpace);
            this.variantIndex = variantIndex;
            this.playlistParser = playlistParser;
            this.playlistUri = playlistUri;
        }

        protected void consume(byte[] data, int limit) throws IOException {
            this.result = (HlsMediaPlaylist) this.playlistParser.parse(this.playlistUri, new ByteArrayInputStream(data, 0, limit));
        }

        public HlsMediaPlaylist getResult() {
            return this.result;
        }
    }

    public HlsChunkSource(String baseUri, HlsUrl[] variants, DataSource dataSource, TimestampAdjusterProvider timestampAdjusterProvider) {
        this.baseUri = baseUri;
        this.variants = variants;
        this.dataSource = dataSource;
        this.timestampAdjusterProvider = timestampAdjusterProvider;
        this.variantPlaylists = new HlsMediaPlaylist[variants.length];
        this.variantLastPlaylistLoadTimesMs = new long[variants.length];
        Format[] variantFormats = new Format[variants.length];
        int[] initialTrackSelection = new int[variants.length];
        for (int i = 0; i < variants.length; i++) {
            variantFormats[i] = variants[i].format;
            initialTrackSelection[i] = i;
        }
        this.trackGroup = new TrackGroup(variantFormats);
        this.trackSelection = new InitializationTrackSelection(this.trackGroup, initialTrackSelection);
    }

    public void maybeThrowError() throws IOException {
        if (this.fatalError != null) {
            throw this.fatalError;
        }
    }

    public boolean isLive() {
        return this.live;
    }

    public long getDurationUs() {
        return this.durationUs;
    }

    public TrackGroup getTrackGroup() {
        return this.trackGroup;
    }

    public void selectTracks(TrackSelection trackSelection) {
        this.trackSelection = trackSelection;
    }

    public void reset() {
        this.fatalError = null;
    }

    public void getNextChunk(HlsMediaChunk previous, long playbackPositionUs, HlsChunkHolder out) {
        int oldVariantIndex;
        long bufferedDurationUs;
        if (previous == null) {
            oldVariantIndex = -1;
        } else {
            oldVariantIndex = this.trackGroup.indexOf(previous.trackFormat);
        }
        if (previous == null) {
            bufferedDurationUs = 0;
        } else {
            bufferedDurationUs = Math.max(0, previous.getAdjustedStartTimeUs() - playbackPositionUs);
        }
        this.trackSelection.updateSelectedTrack(bufferedDurationUs);
        int newVariantIndex = this.trackSelection.getSelectedIndexInTrackGroup();
        boolean switchingVariant = oldVariantIndex != newVariantIndex;
        HlsMediaPlaylist mediaPlaylist = this.variantPlaylists[newVariantIndex];
        if (mediaPlaylist == null) {
            out.chunk = newMediaPlaylistChunk(newVariantIndex, this.trackSelection.getSelectionReason(), this.trackSelection.getSelectionData());
            return;
        }
        int chunkMediaSequence;
        if (this.live) {
            if (previous == null) {
                chunkMediaSequence = Math.max(0, mediaPlaylist.segments.size() - 3) + mediaPlaylist.mediaSequence;
            } else {
                chunkMediaSequence = getLiveNextChunkSequenceNumber(previous.chunkIndex, oldVariantIndex, newVariantIndex);
                if (chunkMediaSequence < mediaPlaylist.mediaSequence) {
                    newVariantIndex = oldVariantIndex;
                    mediaPlaylist = this.variantPlaylists[newVariantIndex];
                    chunkMediaSequence = getLiveNextChunkSequenceNumber(previous.chunkIndex, oldVariantIndex, newVariantIndex);
                    if (chunkMediaSequence < mediaPlaylist.mediaSequence) {
                        this.fatalError = new BehindLiveWindowException();
                        return;
                    }
                }
            }
        } else if (previous == null) {
            chunkMediaSequence = Util.binarySearchFloor(mediaPlaylist.segments, Long.valueOf(playbackPositionUs), true, true) + mediaPlaylist.mediaSequence;
        } else if (switchingVariant) {
            chunkMediaSequence = Util.binarySearchFloor(mediaPlaylist.segments, Long.valueOf(previous.startTimeUs), true, true) + mediaPlaylist.mediaSequence;
        } else {
            chunkMediaSequence = previous.getNextChunkIndex();
        }
        int chunkIndex = chunkMediaSequence - mediaPlaylist.mediaSequence;
        if (chunkIndex < mediaPlaylist.segments.size()) {
            long startTimeUs;
            Extractor extractor;
            Segment segment = (Segment) mediaPlaylist.segments.get(chunkIndex);
            if (segment.isEncrypted) {
                Uri keyUri = UriUtil.resolveToUri(mediaPlaylist.baseUri, segment.encryptionKeyUri);
                if (!keyUri.equals(this.encryptionKeyUri)) {
                    out.chunk = newEncryptionKeyChunk(keyUri, segment.encryptionIV, newVariantIndex, this.trackSelection.getSelectionReason(), this.trackSelection.getSelectionData());
                    return;
                } else if (!Util.areEqual(segment.encryptionIV, this.encryptionIvString)) {
                    setEncryptionData(keyUri, segment.encryptionIV, this.encryptionKey);
                }
            } else {
                clearEncryptionData();
            }
            if (!this.live) {
                startTimeUs = segment.startTimeUs;
            } else if (previous == null) {
                startTimeUs = 0;
            } else if (switchingVariant) {
                startTimeUs = previous.getAdjustedStartTimeUs();
            } else {
                startTimeUs = previous.getAdjustedEndTimeUs();
            }
            long endTimeUs = startTimeUs + ((long) (segment.durationSecs * 1000000.0d));
            Format format = this.variants[newVariantIndex].format;
            Uri chunkUri = UriUtil.resolveToUri(mediaPlaylist.baseUri, segment.url);
            boolean useInitializedExtractor = this.lastLoadedInitializationChunk != null && this.lastLoadedInitializationChunk.format == format;
            boolean needNewExtractor = (previous != null && previous.discontinuitySequenceNumber == segment.discontinuitySequenceNumber && format == previous.trackFormat) ? false : true;
            boolean extractorNeedsInit = true;
            boolean isTimestampMaster = false;
            TimestampAdjuster timestampAdjuster = null;
            String lastPathSegment = chunkUri.getLastPathSegment();
            Extractor adtsExtractor;
            if (lastPathSegment.endsWith(AAC_FILE_EXTENSION)) {
                adtsExtractor = new AdtsExtractor(startTimeUs);
            } else {
                if (!lastPathSegment.endsWith(AC3_FILE_EXTENSION)) {
                    if (!lastPathSegment.endsWith(EC3_FILE_EXTENSION)) {
                        if (lastPathSegment.endsWith(MP3_FILE_EXTENSION)) {
                            adtsExtractor = new Mp3Extractor(startTimeUs);
                        } else {
                            if (!lastPathSegment.endsWith(WEBVTT_FILE_EXTENSION)) {
                                if (!lastPathSegment.endsWith(VTT_FILE_EXTENSION)) {
                                    if (lastPathSegment.endsWith(MP4_FILE_EXTENSION)) {
                                        isTimestampMaster = true;
                                        if (!needNewExtractor) {
                                            extractor = previous.extractor;
                                        } else if (useInitializedExtractor) {
                                            extractor = this.lastLoadedInitializationChunk.extractor;
                                        } else {
                                            timestampAdjuster = this.timestampAdjusterProvider.getAdjuster(segment.discontinuitySequenceNumber, startTimeUs);
                                            adtsExtractor = new FragmentedMp4Extractor(0, timestampAdjuster);
                                        }
                                    } else if (needNewExtractor) {
                                        isTimestampMaster = true;
                                        if (useInitializedExtractor) {
                                            extractor = this.lastLoadedInitializationChunk.extractor;
                                        } else {
                                            timestampAdjuster = this.timestampAdjusterProvider.getAdjuster(segment.discontinuitySequenceNumber, startTimeUs);
                                            int esReaderFactoryFlags = 0;
                                            String codecs = this.variants[newVariantIndex].format.codecs;
                                            if (!TextUtils.isEmpty(codecs)) {
                                                if (!MimeTypes.AUDIO_AAC.equals(MimeTypes.getAudioMediaMimeType(codecs))) {
                                                    esReaderFactoryFlags = 0 | 2;
                                                }
                                                if (!"video/avc".equals(MimeTypes.getVideoMediaMimeType(codecs))) {
                                                    esReaderFactoryFlags |= 4;
                                                }
                                            }
                                            adtsExtractor = new TsExtractor(timestampAdjuster, new DefaultStreamReaderFactory(esReaderFactoryFlags), true);
                                        }
                                    } else {
                                        extractor = previous.extractor;
                                        extractorNeedsInit = false;
                                    }
                                }
                            }
                            timestampAdjuster = this.timestampAdjusterProvider.getAdjuster(segment.discontinuitySequenceNumber, startTimeUs);
                            adtsExtractor = new WebvttExtractor(format.language, timestampAdjuster);
                        }
                    }
                }
                adtsExtractor = new Ac3Extractor(startTimeUs);
            }
            if (!needNewExtractor || mediaPlaylist.initializationSegment == null || useInitializedExtractor) {
                this.lastLoadedInitializationChunk = null;
                out.chunk = new HlsMediaChunk(this.dataSource, new DataSpec(chunkUri, segment.byterangeOffset, segment.byterangeLength, null), format, this.trackSelection.getSelectionReason(), this.trackSelection.getSelectionData(), startTimeUs, endTimeUs, chunkMediaSequence, segment.discontinuitySequenceNumber, isTimestampMaster, timestampAdjuster, extractor, extractorNeedsInit, switchingVariant, this.encryptionKey, this.encryptionIv);
                return;
            }
            out.chunk = buildInitializationChunk(mediaPlaylist, extractor, format);
        } else if (mediaPlaylist.live) {
            long msToRerequestLiveMediaPlaylist = msToRerequestLiveMediaPlaylist(newVariantIndex);
            if (msToRerequestLiveMediaPlaylist <= 0) {
                out.chunk = newMediaPlaylistChunk(newVariantIndex, this.trackSelection.getSelectionReason(), this.trackSelection.getSelectionData());
            } else {
                out.retryInMs = 10 + msToRerequestLiveMediaPlaylist;
            }
        } else {
            out.endOfStream = true;
        }
    }

    private int getLiveNextChunkSequenceNumber(int previousChunkIndex, int oldVariantIndex, int newVariantIndex) {
        if (oldVariantIndex == newVariantIndex) {
            return previousChunkIndex + 1;
        }
        int i;
        HlsMediaPlaylist oldMediaPlaylist = this.variantPlaylists[oldVariantIndex];
        HlsMediaPlaylist newMediaPlaylist = this.variantPlaylists[newVariantIndex];
        double offsetToLiveInstantSecs = 0.0d;
        for (i = previousChunkIndex - oldMediaPlaylist.mediaSequence; i < oldMediaPlaylist.segments.size(); i++) {
            offsetToLiveInstantSecs += ((Segment) oldMediaPlaylist.segments.get(i)).durationSecs;
        }
        long currentTimeMs = SystemClock.elapsedRealtime();
        offsetToLiveInstantSecs = ((offsetToLiveInstantSecs + (((double) (currentTimeMs - this.variantLastPlaylistLoadTimesMs[oldVariantIndex])) / 1000.0d)) + LIVE_VARIANT_SWITCH_SAFETY_EXTRA_SECS) - (((double) (currentTimeMs - this.variantLastPlaylistLoadTimesMs[newVariantIndex])) / 1000.0d);
        if (offsetToLiveInstantSecs < 0.0d) {
            return (newMediaPlaylist.mediaSequence + newMediaPlaylist.segments.size()) + 1;
        }
        for (i = newMediaPlaylist.segments.size() - 1; i >= 0; i--) {
            offsetToLiveInstantSecs -= ((Segment) newMediaPlaylist.segments.get(i)).durationSecs;
            if (offsetToLiveInstantSecs < 0.0d) {
                return newMediaPlaylist.mediaSequence + i;
            }
        }
        return newMediaPlaylist.mediaSequence - 1;
    }

    public void onChunkLoadCompleted(Chunk chunk) {
        if (chunk instanceof HlsInitializationChunk) {
            this.lastLoadedInitializationChunk = (HlsInitializationChunk) chunk;
        } else if (chunk instanceof MediaPlaylistChunk) {
            MediaPlaylistChunk mediaPlaylistChunk = (MediaPlaylistChunk) chunk;
            this.scratchSpace = mediaPlaylistChunk.getDataHolder();
            setMediaPlaylist(mediaPlaylistChunk.variantIndex, mediaPlaylistChunk.getResult());
        } else if (chunk instanceof EncryptionKeyChunk) {
            EncryptionKeyChunk encryptionKeyChunk = (EncryptionKeyChunk) chunk;
            this.scratchSpace = encryptionKeyChunk.getDataHolder();
            setEncryptionData(encryptionKeyChunk.dataSpec.uri, encryptionKeyChunk.iv, encryptionKeyChunk.getResult());
        }
    }

    public boolean onChunkLoadError(Chunk chunk, boolean cancelable, IOException e) {
        return cancelable && ChunkedTrackBlacklistUtil.maybeBlacklistTrack(this.trackSelection, this.trackSelection.indexOf(this.trackGroup.indexOf(chunk.trackFormat)), e);
    }

    private HlsInitializationChunk buildInitializationChunk(HlsMediaPlaylist mediaPlaylist, Extractor extractor, Format format) {
        Segment initSegment = mediaPlaylist.initializationSegment;
        DataSpec initDataSpec = new DataSpec(UriUtil.resolveToUri(mediaPlaylist.baseUri, initSegment.url), initSegment.byterangeOffset, initSegment.byterangeLength, null);
        return new HlsInitializationChunk(this.dataSource, initDataSpec, this.trackSelection.getSelectionReason(), this.trackSelection.getSelectionData(), extractor, format);
    }

    private long msToRerequestLiveMediaPlaylist(int variantIndex) {
        return ((long) ((this.variantPlaylists[variantIndex].targetDurationSecs * 1000) / 2)) - (SystemClock.elapsedRealtime() - this.variantLastPlaylistLoadTimesMs[variantIndex]);
    }

    private MediaPlaylistChunk newMediaPlaylistChunk(int variantIndex, int trackSelectionReason, Object trackSelectionData) {
        Uri mediaPlaylistUri = UriUtil.resolveToUri(this.baseUri, this.variants[variantIndex].url);
        return new MediaPlaylistChunk(this.dataSource, new DataSpec(mediaPlaylistUri, 0, -1, null, 1), this.variants[variantIndex].format, trackSelectionReason, trackSelectionData, this.scratchSpace, this.playlistParser, variantIndex, mediaPlaylistUri);
    }

    private EncryptionKeyChunk newEncryptionKeyChunk(Uri keyUri, String iv, int variantIndex, int trackSelectionReason, Object trackSelectionData) {
        return new EncryptionKeyChunk(this.dataSource, new DataSpec(keyUri, 0, -1, null, 1), this.variants[variantIndex].format, trackSelectionReason, trackSelectionData, this.scratchSpace, iv);
    }

    private void setEncryptionData(Uri keyUri, String iv, byte[] secretKey) {
        String trimmedIv;
        if (iv.toLowerCase(Locale.getDefault()).startsWith("0x")) {
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

    private void setMediaPlaylist(int variantIndex, HlsMediaPlaylist mediaPlaylist) {
        this.variantLastPlaylistLoadTimesMs[variantIndex] = SystemClock.elapsedRealtime();
        this.variantPlaylists[variantIndex] = mediaPlaylist;
        this.live |= mediaPlaylist.live;
        this.durationUs = this.live ? C.TIME_UNSET : mediaPlaylist.durationUs;
    }
}
