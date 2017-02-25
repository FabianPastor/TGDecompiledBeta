package org.telegram.messenger.exoplayer2.source.dash;

import android.os.SystemClock;
import com.google.android.gms.wallet.WalletConstants;
import java.io.IOException;
import java.util.List;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.ChunkIndex;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.SeekMap;
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
    private final int adaptationSetIndex;
    private final DataSource dataSource;
    private final long elapsedRealtimeOffsetMs;
    private IOException fatalError;
    private DashManifest manifest;
    private final LoaderErrorThrower manifestLoaderErrorThrower;
    private final int maxSegmentsPerLoad;
    private boolean missingLastSegment;
    private int periodIndex;
    private final RepresentationHolder[] representationHolders;
    private final TrackSelection trackSelection;

    protected static final class RepresentationHolder {
        public final ChunkExtractorWrapper extractorWrapper;
        private long periodDurationUs;
        public Representation representation;
        public Format sampleFormat;
        public DashSegmentIndex segmentIndex;
        private int segmentNumShift;

        public RepresentationHolder(long periodDurationUs, Representation representation) {
            this.periodDurationUs = periodDurationUs;
            this.representation = representation;
            String containerMimeType = representation.format.containerMimeType;
            if (mimeTypeIsRawText(containerMimeType)) {
                this.extractorWrapper = null;
            } else {
                Extractor extractor;
                boolean resendFormatOnInit = false;
                if (MimeTypes.APPLICATION_RAWCC.equals(containerMimeType)) {
                    extractor = new RawCcExtractor(representation.format);
                    resendFormatOnInit = true;
                } else if (mimeTypeIsWebm(containerMimeType)) {
                    extractor = new MatroskaExtractor();
                } else {
                    extractor = new FragmentedMp4Extractor();
                }
                this.extractorWrapper = new ChunkExtractorWrapper(extractor, representation.format, true, resendFormatOnInit);
            }
            this.segmentIndex = representation.getIndex();
        }

        public void setSampleFormat(Format sampleFormat) {
            this.sampleFormat = sampleFormat;
        }

        public void updateRepresentation(long newPeriodDurationUs, Representation newRepresentation) throws BehindLiveWindowException {
            DashSegmentIndex oldIndex = this.representation.getIndex();
            DashSegmentIndex newIndex = newRepresentation.getIndex();
            this.periodDurationUs = newPeriodDurationUs;
            this.representation = newRepresentation;
            if (oldIndex != null) {
                this.segmentIndex = newIndex;
                if (oldIndex.isExplicit()) {
                    int oldIndexLastSegmentNum = oldIndex.getLastSegmentNum(this.periodDurationUs);
                    long oldIndexEndTimeUs = oldIndex.getTimeUs(oldIndexLastSegmentNum) + oldIndex.getDurationUs(oldIndexLastSegmentNum, this.periodDurationUs);
                    int newIndexFirstSegmentNum = newIndex.getFirstSegmentNum();
                    long newIndexStartTimeUs = newIndex.getTimeUs(newIndexFirstSegmentNum);
                    if (oldIndexEndTimeUs == newIndexStartTimeUs) {
                        this.segmentNumShift += (oldIndex.getLastSegmentNum(this.periodDurationUs) + 1) - newIndexFirstSegmentNum;
                    } else if (oldIndexEndTimeUs < newIndexStartTimeUs) {
                        throw new BehindLiveWindowException();
                    } else {
                        this.segmentNumShift += oldIndex.getSegmentNum(newIndexStartTimeUs, this.periodDurationUs) - newIndexFirstSegmentNum;
                    }
                }
            }
        }

        public int getFirstSegmentNum() {
            return this.segmentIndex.getFirstSegmentNum() + this.segmentNumShift;
        }

        public int getLastSegmentNum() {
            int lastSegmentNum = this.segmentIndex.getLastSegmentNum(this.periodDurationUs);
            if (lastSegmentNum == -1) {
                return -1;
            }
            return this.segmentNumShift + lastSegmentNum;
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
            return mimeType.startsWith(MimeTypes.VIDEO_WEBM) || mimeType.startsWith(MimeTypes.AUDIO_WEBM) || mimeType.startsWith(MimeTypes.APPLICATION_WEBM);
        }

        private static boolean mimeTypeIsRawText(String mimeType) {
            return MimeTypes.isText(mimeType) || MimeTypes.APPLICATION_TTML.equals(mimeType);
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

        public DashChunkSource createDashChunkSource(LoaderErrorThrower manifestLoaderErrorThrower, DashManifest manifest, int periodIndex, int adaptationSetIndex, TrackSelection trackSelection, long elapsedRealtimeOffsetMs) {
            return new DefaultDashChunkSource(manifestLoaderErrorThrower, manifest, periodIndex, adaptationSetIndex, trackSelection, this.dataSourceFactory.createDataSource(), elapsedRealtimeOffsetMs, this.maxSegmentsPerLoad);
        }
    }

    public DefaultDashChunkSource(LoaderErrorThrower manifestLoaderErrorThrower, DashManifest manifest, int periodIndex, int adaptationSetIndex, TrackSelection trackSelection, DataSource dataSource, long elapsedRealtimeOffsetMs, int maxSegmentsPerLoad) {
        this.manifestLoaderErrorThrower = manifestLoaderErrorThrower;
        this.manifest = manifest;
        this.adaptationSetIndex = adaptationSetIndex;
        this.trackSelection = trackSelection;
        this.dataSource = dataSource;
        this.periodIndex = periodIndex;
        this.elapsedRealtimeOffsetMs = elapsedRealtimeOffsetMs;
        this.maxSegmentsPerLoad = maxSegmentsPerLoad;
        long periodDurationUs = manifest.getPeriodDurationUs(periodIndex);
        List<Representation> representations = getRepresentations();
        this.representationHolders = new RepresentationHolder[trackSelection.length()];
        for (int i = 0; i < this.representationHolders.length; i++) {
            this.representationHolders[i] = new RepresentationHolder(periodDurationUs, (Representation) representations.get(trackSelection.getIndexInTrackGroup(i)));
        }
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
        if (this.fatalError != null || this.trackSelection.length() < 2) {
            return queue.size();
        }
        return this.trackSelection.evaluateQueueSize(playbackPositionUs, queue);
    }

    public final void getNextChunk(MediaChunk previous, long playbackPositionUs, ChunkHolder out) {
        if (this.fatalError == null) {
            this.trackSelection.updateSelectedTrack(previous != null ? previous.endTimeUs - playbackPositionUs : 0);
            RepresentationHolder representationHolder = this.representationHolders[this.trackSelection.getSelectedIndex()];
            Representation selectedRepresentation = representationHolder.representation;
            DashSegmentIndex segmentIndex = representationHolder.segmentIndex;
            RangedUri pendingInitializationUri = null;
            RangedUri pendingIndexUri = null;
            Format sampleFormat = representationHolder.sampleFormat;
            if (sampleFormat == null) {
                pendingInitializationUri = selectedRepresentation.getInitializationUri();
            }
            if (segmentIndex == null) {
                pendingIndexUri = selectedRepresentation.getIndexUri();
            }
            if (pendingInitializationUri == null && pendingIndexUri == null) {
                int segmentNum;
                long nowUs = getNowUnixTimeUs();
                int firstAvailableSegmentNum = representationHolder.getFirstSegmentNum();
                int lastAvailableSegmentNum = representationHolder.getLastSegmentNum();
                if (lastAvailableSegmentNum == -1) {
                    long liveEdgeTimeInPeriodUs = (nowUs - (this.manifest.availabilityStartTime * 1000)) - (this.manifest.getPeriod(this.periodIndex).startMs * 1000);
                    if (this.manifest.timeShiftBufferDepth != C.TIME_UNSET) {
                        firstAvailableSegmentNum = Math.max(firstAvailableSegmentNum, representationHolder.getSegmentNum(liveEdgeTimeInPeriodUs - (this.manifest.timeShiftBufferDepth * 1000)));
                    }
                    lastAvailableSegmentNum = representationHolder.getSegmentNum(liveEdgeTimeInPeriodUs) - 1;
                }
                if (previous == null) {
                    segmentNum = Util.constrainValue(representationHolder.getSegmentNum(playbackPositionUs), firstAvailableSegmentNum, lastAvailableSegmentNum);
                } else {
                    segmentNum = previous.getNextChunkIndex();
                    if (segmentNum < firstAvailableSegmentNum) {
                        this.fatalError = new BehindLiveWindowException();
                        return;
                    }
                }
                if (segmentNum > lastAvailableSegmentNum || (this.missingLastSegment && segmentNum >= lastAvailableSegmentNum)) {
                    boolean z;
                    if (!this.manifest.dynamic || this.periodIndex < this.manifest.getPeriodCount() - 1) {
                        z = true;
                    } else {
                        z = false;
                    }
                    out.endOfStream = z;
                    return;
                }
                out.chunk = newMediaChunk(representationHolder, this.dataSource, this.trackSelection.getSelectedFormat(), this.trackSelection.getSelectionReason(), this.trackSelection.getSelectionData(), sampleFormat, segmentNum, Math.min(this.maxSegmentsPerLoad, (lastAvailableSegmentNum - segmentNum) + 1));
                return;
            }
            out.chunk = newInitializationChunk(representationHolder, this.dataSource, this.trackSelection.getSelectedFormat(), this.trackSelection.getSelectionReason(), this.trackSelection.getSelectionData(), pendingInitializationUri, pendingIndexUri);
        }
    }

    public void onChunkLoadCompleted(Chunk chunk) {
        if (chunk instanceof InitializationChunk) {
            InitializationChunk initializationChunk = (InitializationChunk) chunk;
            RepresentationHolder representationHolder = this.representationHolders[this.trackSelection.indexOf(initializationChunk.trackFormat)];
            Format sampleFormat = initializationChunk.getSampleFormat();
            if (sampleFormat != null) {
                representationHolder.setSampleFormat(sampleFormat);
            }
            if (representationHolder.segmentIndex == null) {
                SeekMap seekMap = initializationChunk.getSeekMap();
                if (seekMap != null) {
                    representationHolder.segmentIndex = new DashWrappingSegmentIndex((ChunkIndex) seekMap, initializationChunk.dataSpec.uri.toString());
                }
            }
        }
    }

    public boolean onChunkLoadError(Chunk chunk, boolean cancelable, Exception e) {
        if (!cancelable) {
            return false;
        }
        if (this.manifest.dynamic || !(chunk instanceof MediaChunk) || !(e instanceof InvalidResponseCodeException) || ((InvalidResponseCodeException) e).responseCode != WalletConstants.ERROR_CODE_INVALID_PARAMETERS || ((MediaChunk) chunk).getNextChunkIndex() <= this.representationHolders[this.trackSelection.indexOf(chunk.trackFormat)].getLastSegmentNum()) {
            return ChunkedTrackBlacklistUtil.maybeBlacklistTrack(this.trackSelection, this.trackSelection.indexOf(chunk.trackFormat), e);
        }
        this.missingLastSegment = true;
        return true;
    }

    private List<Representation> getRepresentations() {
        return ((AdaptationSet) this.manifest.getPeriod(this.periodIndex).adaptationSets.get(this.adaptationSetIndex)).representations;
    }

    private long getNowUnixTimeUs() {
        if (this.elapsedRealtimeOffsetMs != 0) {
            return (SystemClock.elapsedRealtime() + this.elapsedRealtimeOffsetMs) * 1000;
        }
        return System.currentTimeMillis() * 1000;
    }

    private static Chunk newInitializationChunk(RepresentationHolder representationHolder, DataSource dataSource, Format trackFormat, int trackSelectionReason, Object trackSelectionData, RangedUri initializationUri, RangedUri indexUri) {
        RangedUri requestUri;
        String baseUrl = representationHolder.representation.baseUrl;
        if (initializationUri != null) {
            requestUri = initializationUri.attemptMerge(indexUri, baseUrl);
            if (requestUri == null) {
                requestUri = initializationUri;
            }
        } else {
            requestUri = indexUri;
        }
        return new InitializationChunk(dataSource, new DataSpec(requestUri.resolveUri(baseUrl), requestUri.start, requestUri.length, representationHolder.representation.getCacheKey()), trackFormat, trackSelectionReason, trackSelectionData, representationHolder.extractorWrapper);
    }

    private static Chunk newMediaChunk(RepresentationHolder representationHolder, DataSource dataSource, Format trackFormat, int trackSelectionReason, Object trackSelectionData, Format sampleFormat, int firstSegmentNum, int maxSegmentCount) {
        Representation representation = representationHolder.representation;
        long startTimeUs = representationHolder.getSegmentStartTimeUs(firstSegmentNum);
        RangedUri segmentUri = representationHolder.getSegmentUrl(firstSegmentNum);
        String baseUrl = representation.baseUrl;
        if (representationHolder.extractorWrapper == null) {
            return new SingleSampleMediaChunk(dataSource, new DataSpec(segmentUri.resolveUri(baseUrl), segmentUri.start, segmentUri.length, representation.getCacheKey()), trackFormat, trackSelectionReason, trackSelectionData, startTimeUs, representationHolder.getSegmentEndTimeUs(firstSegmentNum), firstSegmentNum, trackFormat);
        }
        int segmentCount = 1;
        for (int i = 1; i < maxSegmentCount; i++) {
            RangedUri mergedSegmentUri = segmentUri.attemptMerge(representationHolder.getSegmentUrl(firstSegmentNum + i), baseUrl);
            if (mergedSegmentUri == null) {
                break;
            }
            segmentUri = mergedSegmentUri;
            segmentCount++;
        }
        long endTimeUs = representationHolder.getSegmentEndTimeUs((firstSegmentNum + segmentCount) - 1);
        return new ContainerMediaChunk(dataSource, new DataSpec(segmentUri.resolveUri(baseUrl), segmentUri.start, segmentUri.length, representation.getCacheKey()), trackFormat, trackSelectionReason, trackSelectionData, startTimeUs, endTimeUs, firstSegmentNum, segmentCount, -representation.presentationTimeOffsetUs, representationHolder.extractorWrapper, sampleFormat);
    }
}
