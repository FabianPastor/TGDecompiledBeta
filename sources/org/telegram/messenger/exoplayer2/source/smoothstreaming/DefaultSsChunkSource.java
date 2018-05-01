package org.telegram.messenger.exoplayer2.source.smoothstreaming;

import android.net.Uri;
import java.io.IOException;
import java.util.List;
import org.telegram.messenger.exoplayer2.C0542C;
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

        public Factory(org.telegram.messenger.exoplayer2.upstream.DataSource.Factory factory) {
            this.dataSourceFactory = factory;
        }

        public SsChunkSource createChunkSource(LoaderErrorThrower loaderErrorThrower, SsManifest ssManifest, int i, TrackSelection trackSelection, TrackEncryptionBox[] trackEncryptionBoxArr) {
            return new DefaultSsChunkSource(loaderErrorThrower, ssManifest, i, trackSelection, this.dataSourceFactory.createDataSource(), trackEncryptionBoxArr);
        }
    }

    public void onChunkLoadCompleted(Chunk chunk) {
    }

    public DefaultSsChunkSource(LoaderErrorThrower loaderErrorThrower, SsManifest ssManifest, int i, TrackSelection trackSelection, DataSource dataSource, TrackEncryptionBox[] trackEncryptionBoxArr) {
        SsManifest ssManifest2 = ssManifest;
        int i2 = i;
        TrackSelection trackSelection2 = trackSelection;
        this.manifestLoaderErrorThrower = loaderErrorThrower;
        this.manifest = ssManifest2;
        this.streamElementIndex = i2;
        this.trackSelection = trackSelection2;
        this.dataSource = dataSource;
        StreamElement streamElement = ssManifest2.streamElements[i2];
        this.extractorWrappers = new ChunkExtractorWrapper[trackSelection.length()];
        int i3 = 0;
        while (i3 < r0.extractorWrappers.length) {
            int indexInTrackGroup = trackSelection2.getIndexInTrackGroup(i3);
            Format format = streamElement.formats[indexInTrackGroup];
            int i4 = i3;
            Track track = r7;
            Track track2 = new Track(indexInTrackGroup, streamElement.type, streamElement.timescale, C0542C.TIME_UNSET, ssManifest2.durationUs, format, 0, trackEncryptionBoxArr, streamElement.type == 2 ? 4 : 0, null, null);
            r0.extractorWrappers[i4] = new ChunkExtractorWrapper(new FragmentedMp4Extractor(3, null, track, null), streamElement.type, format);
            i3 = i4 + 1;
        }
    }

    public long getAdjustedSeekPositionUs(long j, SeekParameters seekParameters) {
        StreamElement streamElement = this.manifest.streamElements[this.streamElementIndex];
        int chunkIndex = streamElement.getChunkIndex(j);
        long startTimeUs = streamElement.getStartTimeUs(chunkIndex);
        long startTimeUs2 = (startTimeUs >= j || chunkIndex >= streamElement.chunkCount - 1) ? startTimeUs : streamElement.getStartTimeUs(chunkIndex + 1);
        return Util.resolveSeekPositionUs(j, seekParameters, startTimeUs, startTimeUs2);
    }

    public void updateManifest(SsManifest ssManifest) {
        StreamElement streamElement = this.manifest.streamElements[this.streamElementIndex];
        int i = streamElement.chunkCount;
        StreamElement streamElement2 = ssManifest.streamElements[this.streamElementIndex];
        if (i != 0) {
            if (streamElement2.chunkCount != 0) {
                int i2 = i - 1;
                long startTimeUs = streamElement.getStartTimeUs(i2) + streamElement.getChunkDurationUs(i2);
                long startTimeUs2 = streamElement2.getStartTimeUs(0);
                if (startTimeUs <= startTimeUs2) {
                    this.currentManifestChunkOffset += i;
                } else {
                    this.currentManifestChunkOffset += streamElement.getChunkIndex(startTimeUs2);
                }
                this.manifest = ssManifest;
            }
        }
        this.currentManifestChunkOffset += i;
        this.manifest = ssManifest;
    }

    public void maybeThrowError() throws IOException {
        if (this.fatalError != null) {
            throw this.fatalError;
        }
        this.manifestLoaderErrorThrower.maybeThrowError();
    }

    public int getPreferredQueueSize(long j, List<? extends MediaChunk> list) {
        if (this.fatalError == null) {
            if (this.trackSelection.length() >= 2) {
                return this.trackSelection.evaluateQueueSize(j, list);
            }
        }
        return list.size();
    }

    public final void getNextChunk(MediaChunk mediaChunk, long j, long j2, ChunkHolder chunkHolder) {
        long j3 = j;
        long j4 = j2;
        ChunkHolder chunkHolder2 = chunkHolder;
        if (this.fatalError == null) {
            StreamElement streamElement = r0.manifest.streamElements[r0.streamElementIndex];
            if (streamElement.chunkCount == 0) {
                chunkHolder2.endOfStream = r0.manifest.isLive ^ 1;
                return;
            }
            int chunkIndex;
            if (mediaChunk == null) {
                chunkIndex = streamElement.getChunkIndex(j4);
            } else {
                chunkIndex = mediaChunk.getNextChunkIndex() - r0.currentManifestChunkOffset;
                if (chunkIndex < 0) {
                    r0.fatalError = new BehindLiveWindowException();
                    return;
                }
            }
            int i = chunkIndex;
            if (i >= streamElement.chunkCount) {
                chunkHolder2.endOfStream = r0.manifest.isLive ^ 1;
                return;
            }
            r0.trackSelection.updateSelectedTrack(j3, j4 - j3, resolveTimeToLiveEdgeUs(j3));
            long startTimeUs = streamElement.getStartTimeUs(i);
            long chunkDurationUs = startTimeUs + streamElement.getChunkDurationUs(i);
            int i2 = i + r0.currentManifestChunkOffset;
            chunkIndex = r0.trackSelection.getSelectedIndex();
            ChunkExtractorWrapper chunkExtractorWrapper = r0.extractorWrappers[chunkIndex];
            chunkHolder2.chunk = newMediaChunk(r0.trackSelection.getSelectedFormat(), r0.dataSource, streamElement.buildRequestUri(r0.trackSelection.getIndexInTrackGroup(chunkIndex), i), null, i2, startTimeUs, chunkDurationUs, r0.trackSelection.getSelectionReason(), r0.trackSelection.getSelectionData(), chunkExtractorWrapper);
        }
    }

    public boolean onChunkLoadError(Chunk chunk, boolean z, Exception exception) {
        return (!z || ChunkedTrackBlacklistUtil.maybeBlacklistTrack(this.trackSelection, this.trackSelection.indexOf(chunk.trackFormat), exception) == null) ? null : true;
    }

    private static MediaChunk newMediaChunk(Format format, DataSource dataSource, Uri uri, String str, int i, long j, long j2, int i2, Object obj, ChunkExtractorWrapper chunkExtractorWrapper) {
        return new ContainerMediaChunk(dataSource, new DataSpec(uri, 0, -1, str), format, i2, obj, j, j2, i, 1, j, chunkExtractorWrapper);
    }

    private long resolveTimeToLiveEdgeUs(long j) {
        if (!this.manifest.isLive) {
            return C0542C.TIME_UNSET;
        }
        StreamElement streamElement = this.manifest.streamElements[this.streamElementIndex];
        int i = streamElement.chunkCount - 1;
        return (streamElement.getStartTimeUs(i) + streamElement.getChunkDurationUs(i)) - j;
    }
}
