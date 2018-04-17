package org.telegram.messenger.exoplayer2.source.dash;

import android.os.SystemClock;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.SeekParameters;
import org.telegram.messenger.exoplayer2.extractor.ChunkIndex;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.SeekMap;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.extractor.mkv.MatroskaExtractor;
import org.telegram.messenger.exoplayer2.extractor.mp4.FragmentedMp4Extractor;
import org.telegram.messenger.exoplayer2.extractor.rawcc.RawCcExtractor;
import org.telegram.messenger.exoplayer2.source.BehindLiveWindowException;
import org.telegram.messenger.exoplayer2.source.chunk.Chunk;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkExtractorWrapper;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkHolder;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil;
import org.telegram.messenger.exoplayer2.source.chunk.ContainerMediaChunk;
import org.telegram.messenger.exoplayer2.source.chunk.InitializationChunk;
import org.telegram.messenger.exoplayer2.source.chunk.MediaChunk;
import org.telegram.messenger.exoplayer2.source.chunk.SingleSampleMediaChunk;
import org.telegram.messenger.exoplayer2.source.dash.PlayerEmsgHandler.PlayerTrackEmsgHandler;
import org.telegram.messenger.exoplayer2.source.dash.manifest.AdaptationSet;
import org.telegram.messenger.exoplayer2.source.dash.manifest.DashManifest;
import org.telegram.messenger.exoplayer2.source.dash.manifest.RangedUri;
import org.telegram.messenger.exoplayer2.source.dash.manifest.Representation;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.HttpDataSource.InvalidResponseCodeException;
import org.telegram.messenger.exoplayer2.upstream.LoaderErrorThrower;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.Util;

public class DefaultDashChunkSource implements DashChunkSource {
    private final int[] adaptationSetIndices;
    private final DataSource dataSource;
    private final long elapsedRealtimeOffsetMs;
    private IOException fatalError;
    private long liveEdgeTimeUs = C0542C.TIME_UNSET;
    private DashManifest manifest;
    private final LoaderErrorThrower manifestLoaderErrorThrower;
    private final int maxSegmentsPerLoad;
    private boolean missingLastSegment;
    private int periodIndex;
    private final PlayerTrackEmsgHandler playerTrackEmsgHandler;
    protected final RepresentationHolder[] representationHolders;
    private final TrackSelection trackSelection;
    private final int trackType;

    protected static final class RepresentationHolder {
        final ChunkExtractorWrapper extractorWrapper;
        private long periodDurationUs;
        public Representation representation;
        public DashSegmentIndex segmentIndex;
        private int segmentNumShift;

        RepresentationHolder(long periodDurationUs, int trackType, Representation representation, boolean enableEventMessageTrack, boolean enableCea608Track, TrackOutput playerEmsgTrackOutput) {
            Representation representation2 = representation;
            this.periodDurationUs = periodDurationUs;
            this.representation = representation2;
            String containerMimeType = representation2.format.containerMimeType;
            if (mimeTypeIsRawText(containerMimeType)) {
                r0.extractorWrapper = null;
                int i = trackType;
            } else {
                Extractor extractor;
                if (MimeTypes.APPLICATION_RAWCC.equals(containerMimeType)) {
                    extractor = new RawCcExtractor(representation2.format);
                } else if (mimeTypeIsWebm(containerMimeType)) {
                    extractor = new MatroskaExtractor(1);
                } else {
                    List<Format> singletonList;
                    int flags = 0;
                    if (enableEventMessageTrack) {
                        flags = 0 | 4;
                    }
                    if (enableCea608Track) {
                        singletonList = Collections.singletonList(Format.createTextSampleFormat(null, MimeTypes.APPLICATION_CEA608, 0, null));
                    } else {
                        singletonList = Collections.emptyList();
                    }
                    extractor = new FragmentedMp4Extractor(flags, null, null, null, singletonList, playerEmsgTrackOutput);
                    r0.extractorWrapper = new ChunkExtractorWrapper(extractor, trackType, representation2.format);
                }
                r0.extractorWrapper = new ChunkExtractorWrapper(extractor, trackType, representation2.format);
            }
            r0.segmentIndex = representation.getIndex();
        }

        void updateRepresentation(long newPeriodDurationUs, Representation newRepresentation) throws BehindLiveWindowException {
            DashSegmentIndex oldIndex = this.representation.getIndex();
            DashSegmentIndex newIndex = newRepresentation.getIndex();
            this.periodDurationUs = newPeriodDurationUs;
            this.representation = newRepresentation;
            if (oldIndex != null) {
                this.segmentIndex = newIndex;
                if (oldIndex.isExplicit()) {
                    int oldIndexSegmentCount = oldIndex.getSegmentCount(this.periodDurationUs);
                    if (oldIndexSegmentCount != 0) {
                        int oldIndexLastSegmentNum = (oldIndex.getFirstSegmentNum() + oldIndexSegmentCount) - 1;
                        long oldIndexEndTimeUs = oldIndex.getTimeUs(oldIndexLastSegmentNum) + oldIndex.getDurationUs(oldIndexLastSegmentNum, this.periodDurationUs);
                        int newIndexFirstSegmentNum = newIndex.getFirstSegmentNum();
                        long newIndexStartTimeUs = newIndex.getTimeUs(newIndexFirstSegmentNum);
                        if (oldIndexEndTimeUs == newIndexStartTimeUs) {
                            this.segmentNumShift += (oldIndexLastSegmentNum + 1) - newIndexFirstSegmentNum;
                        } else if (oldIndexEndTimeUs < newIndexStartTimeUs) {
                            throw new BehindLiveWindowException();
                        } else {
                            this.segmentNumShift += oldIndex.getSegmentNum(newIndexStartTimeUs, this.periodDurationUs) - newIndexFirstSegmentNum;
                        }
                    }
                }
            }
        }

        public int getFirstSegmentNum() {
            return this.segmentIndex.getFirstSegmentNum() + this.segmentNumShift;
        }

        public int getSegmentCount() {
            return this.segmentIndex.getSegmentCount(this.periodDurationUs);
        }

        public long getSegmentStartTimeUs(int segmentNum) {
            return this.segmentIndex.getTimeUs(segmentNum - this.segmentNumShift);
        }

        public long getSegmentEndTimeUs(int segmentNum) {
            return getSegmentStartTimeUs(segmentNum) + this.segmentIndex.getDurationUs(segmentNum - this.segmentNumShift, this.periodDurationUs);
        }

        public int getSegmentNum(long positionUs) {
            return this.segmentIndex.getSegmentNum(positionUs, this.periodDurationUs) + this.segmentNumShift;
        }

        public RangedUri getSegmentUrl(int segmentNum) {
            return this.segmentIndex.getSegmentUrl(segmentNum - this.segmentNumShift);
        }

        private static boolean mimeTypeIsWebm(String mimeType) {
            if (!(mimeType.startsWith(MimeTypes.VIDEO_WEBM) || mimeType.startsWith(MimeTypes.AUDIO_WEBM))) {
                if (!mimeType.startsWith(MimeTypes.APPLICATION_WEBM)) {
                    return false;
                }
            }
            return true;
        }

        private static boolean mimeTypeIsRawText(String mimeType) {
            if (!MimeTypes.isText(mimeType)) {
                if (!MimeTypes.APPLICATION_TTML.equals(mimeType)) {
                    return false;
                }
            }
            return true;
        }
    }

    public static final class Factory implements org.telegram.messenger.exoplayer2.source.dash.DashChunkSource.Factory {
        private final org.telegram.messenger.exoplayer2.upstream.DataSource.Factory dataSourceFactory;
        private final int maxSegmentsPerLoad;

        public Factory(org.telegram.messenger.exoplayer2.upstream.DataSource.Factory dataSourceFactory) {
            this(dataSourceFactory, 1);
        }

        public Factory(org.telegram.messenger.exoplayer2.upstream.DataSource.Factory dataSourceFactory, int maxSegmentsPerLoad) {
            this.dataSourceFactory = dataSourceFactory;
            this.maxSegmentsPerLoad = maxSegmentsPerLoad;
        }

        public DashChunkSource createDashChunkSource(LoaderErrorThrower manifestLoaderErrorThrower, DashManifest manifest, int periodIndex, int[] adaptationSetIndices, TrackSelection trackSelection, int trackType, long elapsedRealtimeOffsetMs, boolean enableEventMessageTrack, boolean enableCea608Track, PlayerTrackEmsgHandler playerEmsgHandler) {
            return new DefaultDashChunkSource(manifestLoaderErrorThrower, manifest, periodIndex, adaptationSetIndices, trackSelection, trackType, this.dataSourceFactory.createDataSource(), elapsedRealtimeOffsetMs, this.maxSegmentsPerLoad, enableEventMessageTrack, enableCea608Track, playerEmsgHandler);
        }
    }

    public DefaultDashChunkSource(LoaderErrorThrower manifestLoaderErrorThrower, DashManifest manifest, int periodIndex, int[] adaptationSetIndices, TrackSelection trackSelection, int trackType, DataSource dataSource, long elapsedRealtimeOffsetMs, int maxSegmentsPerLoad, boolean enableEventMessageTrack, boolean enableCea608Track, PlayerTrackEmsgHandler playerTrackEmsgHandler) {
        TrackSelection trackSelection2 = trackSelection;
        this.manifestLoaderErrorThrower = manifestLoaderErrorThrower;
        this.manifest = manifest;
        this.adaptationSetIndices = adaptationSetIndices;
        this.trackSelection = trackSelection2;
        int i = trackType;
        this.trackType = i;
        this.dataSource = dataSource;
        this.periodIndex = periodIndex;
        this.elapsedRealtimeOffsetMs = elapsedRealtimeOffsetMs;
        this.maxSegmentsPerLoad = maxSegmentsPerLoad;
        this.playerTrackEmsgHandler = playerTrackEmsgHandler;
        long periodDurationUs = manifest.getPeriodDurationUs(periodIndex);
        List<Representation> representations = getRepresentations();
        this.representationHolders = new RepresentationHolder[trackSelection.length()];
        int i2 = 0;
        while (true) {
            int i3 = i2;
            if (i3 < r0.representationHolders.length) {
                int i4 = i3;
                List<Representation> representations2 = representations;
                r0.representationHolders[i4] = new RepresentationHolder(periodDurationUs, i, (Representation) representations.get(trackSelection2.getIndexInTrackGroup(i3)), enableEventMessageTrack, enableCea608Track, playerTrackEmsgHandler);
                i2 = i4 + 1;
                long j = elapsedRealtimeOffsetMs;
                int i5 = maxSegmentsPerLoad;
                PlayerTrackEmsgHandler playerTrackEmsgHandler2 = playerTrackEmsgHandler;
                representations = representations2;
            } else {
                return;
            }
        }
    }

    public long getAdjustedSeekPositionUs(long positionUs, SeekParameters seekParameters) {
        long firstSyncUs;
        for (RepresentationHolder representationHolder : this.representationHolders) {
            if (representationHolder.segmentIndex != null) {
                int segmentNum = representationHolder.getSegmentNum(positionUs);
                firstSyncUs = representationHolder.getSegmentStartTimeUs(segmentNum);
                long secondSyncUs = (firstSyncUs >= positionUs || segmentNum >= representationHolder.getSegmentCount() - 1) ? firstSyncUs : representationHolder.getSegmentStartTimeUs(segmentNum + 1);
                return Util.resolveSeekPositionUs(positionUs, seekParameters, firstSyncUs, secondSyncUs);
            }
        }
        return positionUs;
    }

    public void updateManifest(DashManifest newManifest, int newPeriodIndex) {
        try {
            this.manifest = newManifest;
            this.periodIndex = newPeriodIndex;
            long periodDurationUs = this.manifest.getPeriodDurationUs(this.periodIndex);
            List<Representation> representations = getRepresentations();
            for (int i = 0; i < this.representationHolders.length; i++) {
                this.representationHolders[i].updateRepresentation(periodDurationUs, (Representation) representations.get(this.trackSelection.getIndexInTrackGroup(i)));
            }
        } catch (BehindLiveWindowException e) {
            this.fatalError = e;
        }
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

    public void getNextChunk(MediaChunk previous, long playbackPositionUs, long loadPositionUs, ChunkHolder out) {
        long j = playbackPositionUs;
        long j2 = loadPositionUs;
        ChunkHolder chunkHolder = out;
        if (this.fatalError == null) {
            long bufferedDurationUs = j2 - j;
            long timeToLiveEdgeUs = resolveTimeToLiveEdgeUs(j);
            long presentationPositionUs = (C0542C.msToUs(r0.manifest.availabilityStartTimeMs) + C0542C.msToUs(r0.manifest.getPeriod(r0.periodIndex).startMs)) + j2;
            if (r0.playerTrackEmsgHandler == null || !r0.playerTrackEmsgHandler.maybeRefreshManifestBeforeLoadingNextChunk(presentationPositionUs)) {
                r0.trackSelection.updateSelectedTrack(j, bufferedDurationUs, timeToLiveEdgeUs);
                RepresentationHolder representationHolder = r0.representationHolders[r0.trackSelection.getSelectedIndex()];
                if (representationHolder.extractorWrapper != null) {
                    Representation selectedRepresentation = representationHolder.representation;
                    RangedUri pendingInitializationUri = null;
                    RangedUri pendingIndexUri = null;
                    if (representationHolder.extractorWrapper.getSampleFormats() == null) {
                        pendingInitializationUri = selectedRepresentation.getInitializationUri();
                    }
                    if (representationHolder.segmentIndex == null) {
                        pendingIndexUri = selectedRepresentation.getIndexUri();
                    }
                    if (!(pendingInitializationUri == null && pendingIndexUri == null)) {
                        chunkHolder.chunk = newInitializationChunk(representationHolder, r0.dataSource, r0.trackSelection.getSelectedFormat(), r0.trackSelection.getSelectionReason(), r0.trackSelection.getSelectionData(), pendingInitializationUri, pendingIndexUri);
                        return;
                    }
                }
                int availableSegmentCount = representationHolder.getSegmentCount();
                if (availableSegmentCount == 0) {
                    boolean z;
                    if (r0.manifest.dynamic) {
                        if (r0.periodIndex >= r0.manifest.getPeriodCount() - 1) {
                            z = false;
                            chunkHolder.endOfStream = z;
                            return;
                        }
                    }
                    z = true;
                    chunkHolder.endOfStream = z;
                    return;
                }
                int max;
                int lastAvailableSegmentNum;
                boolean z2;
                int firstAvailableSegmentNum = representationHolder.getFirstSegmentNum();
                if (availableSegmentCount == -1) {
                    int firstAvailableSegmentNum2 = firstAvailableSegmentNum;
                    long liveEdgeTimeUs = getNowUnixTimeUs() - C0542C.msToUs(r0.manifest.availabilityStartTimeMs);
                    long periodStartUs = C0542C.msToUs(r0.manifest.getPeriod(r0.periodIndex).startMs);
                    firstAvailableSegmentNum = liveEdgeTimeUs - periodStartUs;
                    if (r0.manifest.timeShiftBufferDepthMs != C0542C.TIME_UNSET) {
                        max = Math.max(firstAvailableSegmentNum2, representationHolder.getSegmentNum(firstAvailableSegmentNum - C0542C.msToUs(r0.manifest.timeShiftBufferDepthMs)));
                    } else {
                        max = firstAvailableSegmentNum2;
                    }
                    lastAvailableSegmentNum = representationHolder.getSegmentNum(firstAvailableSegmentNum) - 1;
                } else {
                    max = firstAvailableSegmentNum;
                    lastAvailableSegmentNum = (max + availableSegmentCount) - 1;
                }
                updateLiveEdgeTimeUs(representationHolder, lastAvailableSegmentNum);
                if (previous == null) {
                    firstAvailableSegmentNum = Util.constrainValue(representationHolder.getSegmentNum(j2), max, lastAvailableSegmentNum);
                } else {
                    firstAvailableSegmentNum = previous.getNextChunkIndex();
                    if (firstAvailableSegmentNum < max) {
                        r0.fatalError = new BehindLiveWindowException();
                        return;
                    }
                }
                if (firstAvailableSegmentNum > lastAvailableSegmentNum) {
                } else if (!r0.missingLastSegment || firstAvailableSegmentNum < lastAvailableSegmentNum) {
                    int maxSegmentCount = Math.min(r0.maxSegmentsPerLoad, (lastAvailableSegmentNum - firstAvailableSegmentNum) + 1);
                    DataSource dataSource = r0.dataSource;
                    chunkHolder.chunk = newMediaChunk(representationHolder, dataSource, r0.trackType, r0.trackSelection.getSelectedFormat(), r0.trackSelection.getSelectionReason(), r0.trackSelection.getSelectionData(), firstAvailableSegmentNum, maxSegmentCount);
                    return;
                } else {
                    int i = availableSegmentCount;
                }
                if (r0.manifest.dynamic) {
                    z2 = true;
                    if (r0.periodIndex >= r0.manifest.getPeriodCount() - 1) {
                        z2 = false;
                    }
                } else {
                    z2 = true;
                }
                chunkHolder.endOfStream = z2;
            }
        }
    }

    public void onChunkLoadCompleted(Chunk chunk) {
        if (chunk instanceof InitializationChunk) {
            RepresentationHolder representationHolder = this.representationHolders[this.trackSelection.indexOf(((InitializationChunk) chunk).trackFormat)];
            if (representationHolder.segmentIndex == null) {
                SeekMap seekMap = representationHolder.extractorWrapper.getSeekMap();
                if (seekMap != null) {
                    representationHolder.segmentIndex = new DashWrappingSegmentIndex((ChunkIndex) seekMap);
                }
            }
        }
        if (this.playerTrackEmsgHandler != null) {
            this.playerTrackEmsgHandler.onChunkLoadCompleted(chunk);
        }
    }

    public boolean onChunkLoadError(Chunk chunk, boolean cancelable, Exception e) {
        if (!cancelable) {
            return false;
        }
        if (this.playerTrackEmsgHandler != null && this.playerTrackEmsgHandler.maybeRefreshManifestOnLoadingError(chunk)) {
            return true;
        }
        if (!this.manifest.dynamic && (chunk instanceof MediaChunk) && (e instanceof InvalidResponseCodeException) && ((InvalidResponseCodeException) e).responseCode == 404) {
            RepresentationHolder representationHolder = this.representationHolders[this.trackSelection.indexOf(chunk.trackFormat)];
            int segmentCount = representationHolder.getSegmentCount();
            if (!(segmentCount == -1 || segmentCount == 0 || ((MediaChunk) chunk).getNextChunkIndex() <= (representationHolder.getFirstSegmentNum() + segmentCount) - 1)) {
                this.missingLastSegment = true;
                return true;
            }
        }
        return ChunkedTrackBlacklistUtil.maybeBlacklistTrack(this.trackSelection, this.trackSelection.indexOf(chunk.trackFormat), e);
    }

    private ArrayList<Representation> getRepresentations() {
        List<AdaptationSet> manifestAdapationSets = this.manifest.getPeriod(this.periodIndex).adaptationSets;
        ArrayList<Representation> representations = new ArrayList();
        for (int adaptationSetIndex : this.adaptationSetIndices) {
            representations.addAll(((AdaptationSet) manifestAdapationSets.get(adaptationSetIndex)).representations);
        }
        return representations;
    }

    private void updateLiveEdgeTimeUs(RepresentationHolder representationHolder, int lastAvailableSegmentNum) {
        this.liveEdgeTimeUs = this.manifest.dynamic ? representationHolder.getSegmentEndTimeUs(lastAvailableSegmentNum) : C0542C.TIME_UNSET;
    }

    private long getNowUnixTimeUs() {
        if (this.elapsedRealtimeOffsetMs != 0) {
            return (SystemClock.elapsedRealtime() + this.elapsedRealtimeOffsetMs) * 1000;
        }
        return System.currentTimeMillis() * 1000;
    }

    private long resolveTimeToLiveEdgeUs(long playbackPositionUs) {
        boolean resolveTimeToLiveEdgePossible = this.manifest.dynamic && this.liveEdgeTimeUs != C0542C.TIME_UNSET;
        if (resolveTimeToLiveEdgePossible) {
            return this.liveEdgeTimeUs - playbackPositionUs;
        }
        return C0542C.TIME_UNSET;
    }

    protected static Chunk newInitializationChunk(RepresentationHolder representationHolder, DataSource dataSource, Format trackFormat, int trackSelectionReason, Object trackSelectionData, RangedUri initializationUri, RangedUri indexUri) {
        RangedUri requestUri;
        RepresentationHolder representationHolder2 = representationHolder;
        RangedUri rangedUri = initializationUri;
        String baseUrl = representationHolder2.representation.baseUrl;
        if (rangedUri != null) {
            requestUri = rangedUri.attemptMerge(indexUri, baseUrl);
            if (requestUri == null) {
                requestUri = rangedUri;
            }
        } else {
            requestUri = indexUri;
        }
        return new InitializationChunk(dataSource, new DataSpec(requestUri.resolveUri(baseUrl), requestUri.start, requestUri.length, representationHolder2.representation.getCacheKey()), trackFormat, trackSelectionReason, trackSelectionData, representationHolder2.extractorWrapper);
    }

    protected static Chunk newMediaChunk(RepresentationHolder representationHolder, DataSource dataSource, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, int firstSegmentNum, int maxSegmentCount) {
        RepresentationHolder representationHolder2 = representationHolder;
        int i = firstSegmentNum;
        Representation representation = representationHolder2.representation;
        long startTimeUs = representationHolder2.getSegmentStartTimeUs(i);
        RangedUri segmentUri = representationHolder2.getSegmentUrl(i);
        String baseUrl = representation.baseUrl;
        if (representationHolder2.extractorWrapper == null) {
            String representation2 = baseUrl;
            return new SingleSampleMediaChunk(dataSource, new DataSpec(segmentUri.resolveUri(baseUrl), segmentUri.start, segmentUri.length, representation.getCacheKey()), trackFormat, trackSelectionReason, trackSelectionData, startTimeUs, representationHolder2.getSegmentEndTimeUs(i), i, trackType, trackFormat);
        }
        Representation representation3 = representation;
        representation2 = baseUrl;
        int segmentCount = 1;
        for (int i2 = 1; i2 < maxSegmentCount; i2++) {
            RangedUri mergedSegmentUri = segmentUri.attemptMerge(representationHolder2.getSegmentUrl(i + i2), representation2);
            if (mergedSegmentUri == null) {
                break;
            }
            segmentUri = mergedSegmentUri;
            segmentCount++;
        }
        long endTimeUs = representationHolder2.getSegmentEndTimeUs((i + segmentCount) - 1);
        Representation representation4 = representation3;
        long sampleOffsetUs = -representation4.presentationTimeOffsetUs;
        return new ContainerMediaChunk(dataSource, new DataSpec(segmentUri.resolveUri(representation2), segmentUri.start, segmentUri.length, representation4.getCacheKey()), trackFormat, trackSelectionReason, trackSelectionData, startTimeUs, endTimeUs, i, segmentCount, sampleOffsetUs, representationHolder2.extractorWrapper);
    }
}
