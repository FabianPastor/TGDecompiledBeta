package org.telegram.messenger.exoplayer2.source.smoothstreaming;

import android.net.Uri;
import java.io.IOException;
import java.util.List;
import org.telegram.messenger.exoplayer2.C0539C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.SeekParameters;
import org.telegram.messenger.exoplayer2.extractor.mp4.FragmentedMp4Extractor;
import org.telegram.messenger.exoplayer2.extractor.mp4.Track;
import org.telegram.messenger.exoplayer2.extractor.mp4.TrackEncryptionBox;
import org.telegram.messenger.exoplayer2.source.BehindLiveWindowException;
import org.telegram.messenger.exoplayer2.source.chunk.Chunk;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkExtractorWrapper;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkHolder;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil;
import org.telegram.messenger.exoplayer2.source.chunk.ContainerMediaChunk;
import org.telegram.messenger.exoplayer2.source.chunk.MediaChunk;
import org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest.SsManifest;
import org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest.SsManifest.StreamElement;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.LoaderErrorThrower;
import org.telegram.messenger.exoplayer2.util.Util;

public class DefaultSsChunkSource implements SsChunkSource {
    private int currentManifestChunkOffset;
    private final DataSource dataSource;
    private final ChunkExtractorWrapper[] extractorWrappers;
    private IOException fatalError;
    private SsManifest manifest;
    private final LoaderErrorThrower manifestLoaderErrorThrower;
    private final int streamElementIndex;
    private final TrackSelection trackSelection;

    public static final class Factory implements org.telegram.messenger.exoplayer2.source.smoothstreaming.SsChunkSource.Factory {
        private final org.telegram.messenger.exoplayer2.upstream.DataSource.Factory dataSourceFactory;

        public Factory(org.telegram.messenger.exoplayer2.upstream.DataSource.Factory dataSourceFactory) {
            this.dataSourceFactory = dataSourceFactory;
        }

        public SsChunkSource createChunkSource(LoaderErrorThrower manifestLoaderErrorThrower, SsManifest manifest, int elementIndex, TrackSelection trackSelection, TrackEncryptionBox[] trackEncryptionBoxes) {
            return new DefaultSsChunkSource(manifestLoaderErrorThrower, manifest, elementIndex, trackSelection, this.dataSourceFactory.createDataSource(), trackEncryptionBoxes);
        }
    }

    public DefaultSsChunkSource(LoaderErrorThrower manifestLoaderErrorThrower, SsManifest manifest, int streamElementIndex, TrackSelection trackSelection, DataSource dataSource, TrackEncryptionBox[] trackEncryptionBoxes) {
        SsManifest ssManifest = manifest;
        int i = streamElementIndex;
        TrackSelection trackSelection2 = trackSelection;
        this.manifestLoaderErrorThrower = manifestLoaderErrorThrower;
        this.manifest = ssManifest;
        this.streamElementIndex = i;
        this.trackSelection = trackSelection2;
        this.dataSource = dataSource;
        StreamElement streamElement = ssManifest.streamElements[i];
        this.extractorWrappers = new ChunkExtractorWrapper[trackSelection.length()];
        for (int i2 = 0; i2 < r0.extractorWrappers.length; i2++) {
            int manifestTrackIndex = trackSelection2.getIndexInTrackGroup(i2);
            Format format = streamElement.formats[manifestTrackIndex];
            r0.extractorWrappers[i2] = new ChunkExtractorWrapper(new FragmentedMp4Extractor(3, null, new Track(manifestTrackIndex, streamElement.type, streamElement.timescale, C0539C.TIME_UNSET, ssManifest.durationUs, format, 0, trackEncryptionBoxes, streamElement.type == 2 ? 4 : 0, null, null), null), streamElement.type, format);
        }
    }

    public long getAdjustedSeekPositionUs(long positionUs, SeekParameters seekParameters) {
        StreamElement streamElement = this.manifest.streamElements[this.streamElementIndex];
        int chunkIndex = streamElement.getChunkIndex(positionUs);
        long firstSyncUs = streamElement.getStartTimeUs(chunkIndex);
        long secondSyncUs = (firstSyncUs >= positionUs || chunkIndex >= streamElement.chunkCount - 1) ? firstSyncUs : streamElement.getStartTimeUs(chunkIndex + 1);
        return Util.resolveSeekPositionUs(positionUs, seekParameters, firstSyncUs, secondSyncUs);
    }

    public void updateManifest(SsManifest newManifest) {
        StreamElement currentElement = this.manifest.streamElements[this.streamElementIndex];
        int currentElementChunkCount = currentElement.chunkCount;
        StreamElement newElement = newManifest.streamElements[this.streamElementIndex];
        if (currentElementChunkCount != 0) {
            if (newElement.chunkCount != 0) {
                long currentElementEndTimeUs = currentElement.getStartTimeUs(currentElementChunkCount - 1) + currentElement.getChunkDurationUs(currentElementChunkCount - 1);
                long newElementStartTimeUs = newElement.getStartTimeUs(0);
                if (currentElementEndTimeUs <= newElementStartTimeUs) {
                    this.currentManifestChunkOffset += currentElementChunkCount;
                } else {
                    this.currentManifestChunkOffset += currentElement.getChunkIndex(newElementStartTimeUs);
                }
                this.manifest = newManifest;
            }
        }
        this.currentManifestChunkOffset += currentElementChunkCount;
        this.manifest = newManifest;
    }

    public void maybeThrowError() throws IOException {
        if (this.fatalError != null) {
            throw this.fatalError;
        }
        this.manifestLoaderErrorThrower.maybeThrowError();
    }

    public int getPreferredQueueSize(long playbackPositionUs, List<? extends MediaChunk> queue) {
        if (this.fatalError == null) {
            if (this.trackSelection.length() >= 2) {
                return this.trackSelection.evaluateQueueSize(playbackPositionUs, queue);
            }
        }
        return queue.size();
    }

    public final void getNextChunk(MediaChunk previous, long playbackPositionUs, long loadPositionUs, ChunkHolder out) {
        long j = playbackPositionUs;
        long j2 = loadPositionUs;
        ChunkHolder chunkHolder = out;
        if (this.fatalError == null) {
            StreamElement streamElement = r0.manifest.streamElements[r0.streamElementIndex];
            if (streamElement.chunkCount == 0) {
                chunkHolder.endOfStream = r0.manifest.isLive ^ 1;
                return;
            }
            int chunkIndex;
            if (previous == null) {
                chunkIndex = streamElement.getChunkIndex(j2);
            } else {
                chunkIndex = previous.getNextChunkIndex() - r0.currentManifestChunkOffset;
                if (chunkIndex < 0) {
                    r0.fatalError = new BehindLiveWindowException();
                    return;
                }
            }
            int chunkIndex2 = chunkIndex;
            if (chunkIndex2 >= streamElement.chunkCount) {
                chunkHolder.endOfStream = r0.manifest.isLive ^ 1;
                return;
            }
            r0.trackSelection.updateSelectedTrack(j, j2 - j, resolveTimeToLiveEdgeUs(j));
            long chunkStartTimeUs = streamElement.getStartTimeUs(chunkIndex2);
            long chunkEndTimeUs = chunkStartTimeUs + streamElement.getChunkDurationUs(chunkIndex2);
            int currentAbsoluteChunkIndex = r0.currentManifestChunkOffset + chunkIndex2;
            int trackSelectionIndex = r0.trackSelection.getSelectedIndex();
            ChunkExtractorWrapper extractorWrapper = r0.extractorWrappers[trackSelectionIndex];
            int manifestTrackIndex = r0.trackSelection.getIndexInTrackGroup(trackSelectionIndex);
            Uri uri = streamElement.buildRequestUri(manifestTrackIndex, chunkIndex2);
            chunkHolder.chunk = newMediaChunk(r0.trackSelection.getSelectedFormat(), r0.dataSource, uri, null, currentAbsoluteChunkIndex, chunkStartTimeUs, chunkEndTimeUs, r0.trackSelection.getSelectionReason(), r0.trackSelection.getSelectionData(), extractorWrapper);
        }
    }

    public void onChunkLoadCompleted(Chunk chunk) {
    }

    public boolean onChunkLoadError(Chunk chunk, boolean cancelable, Exception e) {
        return cancelable && ChunkedTrackBlacklistUtil.maybeBlacklistTrack(this.trackSelection, this.trackSelection.indexOf(chunk.trackFormat), e);
    }

    private static MediaChunk newMediaChunk(Format format, DataSource dataSource, Uri uri, String cacheKey, int chunkIndex, long chunkStartTimeUs, long chunkEndTimeUs, int trackSelectionReason, Object trackSelectionData, ChunkExtractorWrapper extractorWrapper) {
        return new ContainerMediaChunk(dataSource, new DataSpec(uri, 0, -1, cacheKey), format, trackSelectionReason, trackSelectionData, chunkStartTimeUs, chunkEndTimeUs, chunkIndex, 1, chunkStartTimeUs, extractorWrapper);
    }

    private long resolveTimeToLiveEdgeUs(long playbackPositionUs) {
        if (!this.manifest.isLive) {
            return C0539C.TIME_UNSET;
        }
        StreamElement currentElement = this.manifest.streamElements[this.streamElementIndex];
        int lastChunkIndex = currentElement.chunkCount - 1;
        return (currentElement.getStartTimeUs(lastChunkIndex) + currentElement.getChunkDurationUs(lastChunkIndex)) - playbackPositionUs;
    }
}
