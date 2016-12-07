package org.telegram.messenger.exoplayer.smoothstreaming;

import android.net.Uri;
import android.os.SystemClock;
import android.util.Base64;
import android.util.SparseArray;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer.BehindLiveWindowException;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.chunk.Chunk;
import org.telegram.messenger.exoplayer.chunk.ChunkExtractorWrapper;
import org.telegram.messenger.exoplayer.chunk.ChunkOperationHolder;
import org.telegram.messenger.exoplayer.chunk.ChunkSource;
import org.telegram.messenger.exoplayer.chunk.ContainerMediaChunk;
import org.telegram.messenger.exoplayer.chunk.Format;
import org.telegram.messenger.exoplayer.chunk.Format.DecreasingBandwidthComparator;
import org.telegram.messenger.exoplayer.chunk.FormatEvaluator;
import org.telegram.messenger.exoplayer.chunk.FormatEvaluator.Evaluation;
import org.telegram.messenger.exoplayer.chunk.MediaChunk;
import org.telegram.messenger.exoplayer.drm.DrmInitData;
import org.telegram.messenger.exoplayer.drm.DrmInitData.Mapped;
import org.telegram.messenger.exoplayer.drm.DrmInitData.SchemeInitData;
import org.telegram.messenger.exoplayer.extractor.mp4.FragmentedMp4Extractor;
import org.telegram.messenger.exoplayer.extractor.mp4.Track;
import org.telegram.messenger.exoplayer.extractor.mp4.TrackEncryptionBox;
import org.telegram.messenger.exoplayer.hls.HlsChunkSource;
import org.telegram.messenger.exoplayer.smoothstreaming.SmoothStreamingManifest.ProtectionElement;
import org.telegram.messenger.exoplayer.smoothstreaming.SmoothStreamingManifest.StreamElement;
import org.telegram.messenger.exoplayer.smoothstreaming.SmoothStreamingManifest.TrackElement;
import org.telegram.messenger.exoplayer.smoothstreaming.SmoothStreamingTrackSelector.Output;
import org.telegram.messenger.exoplayer.upstream.DataSource;
import org.telegram.messenger.exoplayer.upstream.DataSpec;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.CodecSpecificDataUtil;
import org.telegram.messenger.exoplayer.util.ManifestFetcher;
import org.telegram.messenger.exoplayer.util.MimeTypes;

public class SmoothStreamingChunkSource implements ChunkSource, Output {
    private static final int INITIALIZATION_VECTOR_SIZE = 8;
    private static final int MINIMUM_MANIFEST_REFRESH_PERIOD_MS = 5000;
    private final FormatEvaluator adaptiveFormatEvaluator;
    private SmoothStreamingManifest currentManifest;
    private int currentManifestChunkOffset;
    private final DataSource dataSource;
    private final Mapped drmInitData;
    private ExposedTrack enabledTrack;
    private final Evaluation evaluation;
    private final SparseArray<ChunkExtractorWrapper> extractorWrappers;
    private IOException fatalError;
    private final boolean live;
    private final long liveEdgeLatencyUs;
    private final ManifestFetcher<SmoothStreamingManifest> manifestFetcher;
    private final SparseArray<MediaFormat> mediaFormats;
    private boolean needManifestRefresh;
    private boolean prepareCalled;
    private final TrackEncryptionBox[] trackEncryptionBoxes;
    private final SmoothStreamingTrackSelector trackSelector;
    private final ArrayList<ExposedTrack> tracks;

    private static final class ExposedTrack {
        private final Format[] adaptiveFormats;
        private final int adaptiveMaxHeight;
        private final int adaptiveMaxWidth;
        private final int elementIndex;
        private final Format fixedFormat;
        public final MediaFormat trackFormat;

        public ExposedTrack(MediaFormat trackFormat, int elementIndex, Format fixedFormat) {
            this.trackFormat = trackFormat;
            this.elementIndex = elementIndex;
            this.fixedFormat = fixedFormat;
            this.adaptiveFormats = null;
            this.adaptiveMaxWidth = -1;
            this.adaptiveMaxHeight = -1;
        }

        public ExposedTrack(MediaFormat trackFormat, int elementIndex, Format[] adaptiveFormats, int adaptiveMaxWidth, int adaptiveMaxHeight) {
            this.trackFormat = trackFormat;
            this.elementIndex = elementIndex;
            this.adaptiveFormats = adaptiveFormats;
            this.adaptiveMaxWidth = adaptiveMaxWidth;
            this.adaptiveMaxHeight = adaptiveMaxHeight;
            this.fixedFormat = null;
        }

        public boolean isAdaptive() {
            return this.adaptiveFormats != null;
        }
    }

    public SmoothStreamingChunkSource(ManifestFetcher<SmoothStreamingManifest> manifestFetcher, SmoothStreamingTrackSelector trackSelector, DataSource dataSource, FormatEvaluator adaptiveFormatEvaluator, long liveEdgeLatencyMs) {
        this(manifestFetcher, (SmoothStreamingManifest) manifestFetcher.getManifest(), trackSelector, dataSource, adaptiveFormatEvaluator, liveEdgeLatencyMs);
    }

    public SmoothStreamingChunkSource(SmoothStreamingManifest manifest, SmoothStreamingTrackSelector trackSelector, DataSource dataSource, FormatEvaluator adaptiveFormatEvaluator) {
        this(null, manifest, trackSelector, dataSource, adaptiveFormatEvaluator, 0);
    }

    private SmoothStreamingChunkSource(ManifestFetcher<SmoothStreamingManifest> manifestFetcher, SmoothStreamingManifest initialManifest, SmoothStreamingTrackSelector trackSelector, DataSource dataSource, FormatEvaluator adaptiveFormatEvaluator, long liveEdgeLatencyMs) {
        this.manifestFetcher = manifestFetcher;
        this.currentManifest = initialManifest;
        this.trackSelector = trackSelector;
        this.dataSource = dataSource;
        this.adaptiveFormatEvaluator = adaptiveFormatEvaluator;
        this.liveEdgeLatencyUs = 1000 * liveEdgeLatencyMs;
        this.evaluation = new Evaluation();
        this.tracks = new ArrayList();
        this.extractorWrappers = new SparseArray();
        this.mediaFormats = new SparseArray();
        this.live = initialManifest.isLive;
        ProtectionElement protectionElement = initialManifest.protectionElement;
        if (protectionElement != null) {
            byte[] keyId = getProtectionElementKeyId(protectionElement.data);
            this.trackEncryptionBoxes = new TrackEncryptionBox[1];
            this.trackEncryptionBoxes[0] = new TrackEncryptionBox(true, 8, keyId);
            this.drmInitData = new Mapped();
            this.drmInitData.put(protectionElement.uuid, new SchemeInitData(MimeTypes.VIDEO_MP4, protectionElement.data));
            return;
        }
        this.trackEncryptionBoxes = null;
        this.drmInitData = null;
    }

    public void maybeThrowError() throws IOException {
        if (this.fatalError != null) {
            throw this.fatalError;
        }
        this.manifestFetcher.maybeThrowError();
    }

    public boolean prepare() {
        if (!this.prepareCalled) {
            this.prepareCalled = true;
            try {
                this.trackSelector.selectTracks(this.currentManifest, this);
            } catch (IOException e) {
                this.fatalError = e;
            }
        }
        if (this.fatalError == null) {
            return true;
        }
        return false;
    }

    public int getTrackCount() {
        return this.tracks.size();
    }

    public final MediaFormat getFormat(int track) {
        return ((ExposedTrack) this.tracks.get(track)).trackFormat;
    }

    public void enable(int track) {
        this.enabledTrack = (ExposedTrack) this.tracks.get(track);
        if (this.enabledTrack.isAdaptive()) {
            this.adaptiveFormatEvaluator.enable();
        }
        if (this.manifestFetcher != null) {
            this.manifestFetcher.enable();
        }
    }

    public void continueBuffering(long playbackPositionUs) {
        if (this.manifestFetcher != null && this.currentManifest.isLive && this.fatalError == null) {
            SmoothStreamingManifest newManifest = (SmoothStreamingManifest) this.manifestFetcher.getManifest();
            if (!(this.currentManifest == newManifest || newManifest == null)) {
                StreamElement currentElement = this.currentManifest.streamElements[this.enabledTrack.elementIndex];
                int currentElementChunkCount = currentElement.chunkCount;
                StreamElement newElement = newManifest.streamElements[this.enabledTrack.elementIndex];
                if (currentElementChunkCount == 0 || newElement.chunkCount == 0) {
                    this.currentManifestChunkOffset += currentElementChunkCount;
                } else {
                    long currentElementEndTimeUs = currentElement.getStartTimeUs(currentElementChunkCount - 1) + currentElement.getChunkDurationUs(currentElementChunkCount - 1);
                    long newElementStartTimeUs = newElement.getStartTimeUs(0);
                    if (currentElementEndTimeUs <= newElementStartTimeUs) {
                        this.currentManifestChunkOffset += currentElementChunkCount;
                    } else {
                        this.currentManifestChunkOffset += currentElement.getChunkIndex(newElementStartTimeUs);
                    }
                }
                this.currentManifest = newManifest;
                this.needManifestRefresh = false;
            }
            if (this.needManifestRefresh && SystemClock.elapsedRealtime() > this.manifestFetcher.getManifestLoadStartTimestamp() + HlsChunkSource.DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS) {
                this.manifestFetcher.requestRefresh();
            }
        }
    }

    public final void getChunkOperation(List<? extends MediaChunk> queue, long playbackPositionUs, ChunkOperationHolder out) {
        if (this.fatalError != null) {
            out.chunk = null;
            return;
        }
        this.evaluation.queueSize = queue.size();
        if (this.enabledTrack.isAdaptive()) {
            this.adaptiveFormatEvaluator.evaluate(queue, playbackPositionUs, this.enabledTrack.adaptiveFormats, this.evaluation);
        } else {
            this.evaluation.format = this.enabledTrack.fixedFormat;
            this.evaluation.trigger = 2;
        }
        Format selectedFormat = this.evaluation.format;
        out.queueSize = this.evaluation.queueSize;
        if (selectedFormat == null) {
            out.chunk = null;
        } else if (out.queueSize != queue.size() || out.chunk == null || !out.chunk.format.equals(selectedFormat)) {
            out.chunk = null;
            StreamElement streamElement = this.currentManifest.streamElements[this.enabledTrack.elementIndex];
            if (streamElement.chunkCount != 0) {
                int chunkIndex;
                if (queue.isEmpty()) {
                    if (this.live) {
                        playbackPositionUs = getLiveSeekPosition(this.currentManifest, this.liveEdgeLatencyUs);
                    }
                    chunkIndex = streamElement.getChunkIndex(playbackPositionUs);
                } else {
                    chunkIndex = (((MediaChunk) queue.get(out.queueSize - 1)).chunkIndex + 1) - this.currentManifestChunkOffset;
                }
                if (!this.live || chunkIndex >= 0) {
                    long chunkEndTimeUs;
                    if (this.currentManifest.isLive) {
                        if (chunkIndex >= streamElement.chunkCount) {
                            this.needManifestRefresh = true;
                            return;
                        } else if (chunkIndex == streamElement.chunkCount - 1) {
                            this.needManifestRefresh = true;
                        }
                    } else if (chunkIndex >= streamElement.chunkCount) {
                        out.endOfStream = true;
                        return;
                    }
                    boolean isLastChunk = !this.currentManifest.isLive && chunkIndex == streamElement.chunkCount - 1;
                    long chunkStartTimeUs = streamElement.getStartTimeUs(chunkIndex);
                    if (isLastChunk) {
                        chunkEndTimeUs = -1;
                    } else {
                        chunkEndTimeUs = chunkStartTimeUs + streamElement.getChunkDurationUs(chunkIndex);
                    }
                    int currentAbsoluteChunkIndex = chunkIndex + this.currentManifestChunkOffset;
                    int manifestTrackIndex = getManifestTrackIndex(streamElement, selectedFormat);
                    int manifestTrackKey = getManifestTrackKey(this.enabledTrack.elementIndex, manifestTrackIndex);
                    out.chunk = newMediaChunk(selectedFormat, streamElement.buildRequestUri(manifestTrackIndex, chunkIndex), null, (ChunkExtractorWrapper) this.extractorWrappers.get(manifestTrackKey), this.drmInitData, this.dataSource, currentAbsoluteChunkIndex, chunkStartTimeUs, chunkEndTimeUs, this.evaluation.trigger, (MediaFormat) this.mediaFormats.get(manifestTrackKey), this.enabledTrack.adaptiveMaxWidth, this.enabledTrack.adaptiveMaxHeight);
                    return;
                }
                this.fatalError = new BehindLiveWindowException();
            } else if (this.currentManifest.isLive) {
                this.needManifestRefresh = true;
            } else {
                out.endOfStream = true;
            }
        }
    }

    public void onChunkLoadCompleted(Chunk chunk) {
    }

    public void onChunkLoadError(Chunk chunk, Exception e) {
    }

    public void disable(List<? extends MediaChunk> list) {
        if (this.enabledTrack.isAdaptive()) {
            this.adaptiveFormatEvaluator.disable();
        }
        if (this.manifestFetcher != null) {
            this.manifestFetcher.disable();
        }
        this.evaluation.format = null;
        this.fatalError = null;
    }

    public void adaptiveTrack(SmoothStreamingManifest manifest, int element, int[] trackIndices) {
        if (this.adaptiveFormatEvaluator != null) {
            MediaFormat maxHeightMediaFormat = null;
            StreamElement streamElement = manifest.streamElements[element];
            int maxWidth = -1;
            int maxHeight = -1;
            Format[] formats = new Format[trackIndices.length];
            for (int i = 0; i < formats.length; i++) {
                int manifestTrackIndex = trackIndices[i];
                formats[i] = streamElement.tracks[manifestTrackIndex].format;
                MediaFormat mediaFormat = initManifestTrack(manifest, element, manifestTrackIndex);
                if (maxHeightMediaFormat == null || mediaFormat.height > maxHeight) {
                    maxHeightMediaFormat = mediaFormat;
                }
                maxWidth = Math.max(maxWidth, mediaFormat.width);
                maxHeight = Math.max(maxHeight, mediaFormat.height);
            }
            Arrays.sort(formats, new DecreasingBandwidthComparator());
            this.tracks.add(new ExposedTrack(maxHeightMediaFormat.copyAsAdaptive(null), element, formats, maxWidth, maxHeight));
        }
    }

    public void fixedTrack(SmoothStreamingManifest manifest, int element, int trackIndex) {
        this.tracks.add(new ExposedTrack(initManifestTrack(manifest, element, trackIndex), element, manifest.streamElements[element].tracks[trackIndex].format));
    }

    private MediaFormat initManifestTrack(SmoothStreamingManifest manifest, int elementIndex, int trackIndex) {
        int manifestTrackKey = getManifestTrackKey(elementIndex, trackIndex);
        MediaFormat mediaFormat = (MediaFormat) this.mediaFormats.get(manifestTrackKey);
        if (mediaFormat != null) {
            return mediaFormat;
        }
        int mp4TrackType;
        long durationUs = this.live ? -1 : manifest.durationUs;
        StreamElement element = manifest.streamElements[elementIndex];
        Format format = element.tracks[trackIndex].format;
        byte[][] csdArray = element.tracks[trackIndex].csd;
        switch (element.type) {
            case 0:
                List<byte[]> csd;
                if (csdArray != null) {
                    csd = Arrays.asList(csdArray);
                } else {
                    csd = Collections.singletonList(CodecSpecificDataUtil.buildAacAudioSpecificConfig(format.audioSamplingRate, format.audioChannels));
                }
                mediaFormat = MediaFormat.createAudioFormat(format.id, format.mimeType, format.bitrate, -1, durationUs, format.audioChannels, format.audioSamplingRate, csd, format.language);
                mp4TrackType = Track.TYPE_soun;
                break;
            case 1:
                mediaFormat = MediaFormat.createVideoFormat(format.id, format.mimeType, format.bitrate, -1, durationUs, format.width, format.height, Arrays.asList(csdArray));
                mp4TrackType = Track.TYPE_vide;
                break;
            case 2:
                mediaFormat = MediaFormat.createTextFormat(format.id, format.mimeType, format.bitrate, durationUs, format.language);
                mp4TrackType = Track.TYPE_text;
                break;
            default:
                throw new IllegalStateException("Invalid type: " + element.type);
        }
        FragmentedMp4Extractor fragmentedMp4Extractor = new FragmentedMp4Extractor(3, new Track(trackIndex, mp4TrackType, element.timescale, -1, durationUs, mediaFormat, this.trackEncryptionBoxes, mp4TrackType == Track.TYPE_vide ? 4 : -1, null, null));
        this.mediaFormats.put(manifestTrackKey, mediaFormat);
        this.extractorWrappers.put(manifestTrackKey, new ChunkExtractorWrapper(fragmentedMp4Extractor));
        return mediaFormat;
    }

    private static long getLiveSeekPosition(SmoothStreamingManifest manifest, long liveEdgeLatencyUs) {
        long liveEdgeTimestampUs = Long.MIN_VALUE;
        for (StreamElement streamElement : manifest.streamElements) {
            if (streamElement.chunkCount > 0) {
                liveEdgeTimestampUs = Math.max(liveEdgeTimestampUs, streamElement.getStartTimeUs(streamElement.chunkCount - 1) + streamElement.getChunkDurationUs(streamElement.chunkCount - 1));
            }
        }
        return liveEdgeTimestampUs - liveEdgeLatencyUs;
    }

    private static int getManifestTrackIndex(StreamElement element, Format format) {
        TrackElement[] tracks = element.tracks;
        for (int i = 0; i < tracks.length; i++) {
            if (tracks[i].format.equals(format)) {
                return i;
            }
        }
        throw new IllegalStateException("Invalid format: " + format);
    }

    private static MediaChunk newMediaChunk(Format formatInfo, Uri uri, String cacheKey, ChunkExtractorWrapper extractorWrapper, DrmInitData drmInitData, DataSource dataSource, int chunkIndex, long chunkStartTimeUs, long chunkEndTimeUs, int trigger, MediaFormat mediaFormat, int adaptiveMaxWidth, int adaptiveMaxHeight) {
        return new ContainerMediaChunk(dataSource, new DataSpec(uri, 0, -1, cacheKey), trigger, formatInfo, chunkStartTimeUs, chunkEndTimeUs, chunkIndex, chunkStartTimeUs, extractorWrapper, mediaFormat, adaptiveMaxWidth, adaptiveMaxHeight, drmInitData, true, -1);
    }

    private static int getManifestTrackKey(int elementIndex, int trackIndex) {
        boolean z = elementIndex <= 65536 && trackIndex <= 65536;
        Assertions.checkState(z);
        return (elementIndex << 16) | trackIndex;
    }

    private static byte[] getProtectionElementKeyId(byte[] initData) {
        StringBuilder initDataStringBuilder = new StringBuilder();
        for (int i = 0; i < initData.length; i += 2) {
            initDataStringBuilder.append((char) initData[i]);
        }
        String initDataString = initDataStringBuilder.toString();
        byte[] keyId = Base64.decode(initDataString.substring(initDataString.indexOf("<KID>") + 5, initDataString.indexOf("</KID>")), 0);
        swap(keyId, 0, 3);
        swap(keyId, 1, 2);
        swap(keyId, 4, 5);
        swap(keyId, 6, 7);
        return keyId;
    }

    private static void swap(byte[] data, int firstPosition, int secondPosition) {
        byte temp = data[firstPosition];
        data[firstPosition] = data[secondPosition];
        data[secondPosition] = temp;
    }
}
