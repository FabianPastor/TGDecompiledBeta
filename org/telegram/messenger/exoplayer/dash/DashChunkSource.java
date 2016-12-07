package org.telegram.messenger.exoplayer.dash;

import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import com.mp4parser.iso14496.part30.WebVTTSampleEntry;
import com.mp4parser.iso14496.part30.XMLSubtitleSampleEntry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.telegram.messenger.exoplayer.BehindLiveWindowException;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.TimeRange;
import org.telegram.messenger.exoplayer.TimeRange.DynamicTimeRange;
import org.telegram.messenger.exoplayer.TimeRange.StaticTimeRange;
import org.telegram.messenger.exoplayer.chunk.Chunk;
import org.telegram.messenger.exoplayer.chunk.ChunkExtractorWrapper;
import org.telegram.messenger.exoplayer.chunk.ChunkOperationHolder;
import org.telegram.messenger.exoplayer.chunk.ChunkSource;
import org.telegram.messenger.exoplayer.chunk.ContainerMediaChunk;
import org.telegram.messenger.exoplayer.chunk.Format;
import org.telegram.messenger.exoplayer.chunk.Format.DecreasingBandwidthComparator;
import org.telegram.messenger.exoplayer.chunk.FormatEvaluator;
import org.telegram.messenger.exoplayer.chunk.FormatEvaluator.Evaluation;
import org.telegram.messenger.exoplayer.chunk.InitializationChunk;
import org.telegram.messenger.exoplayer.chunk.MediaChunk;
import org.telegram.messenger.exoplayer.chunk.SingleSampleMediaChunk;
import org.telegram.messenger.exoplayer.dash.DashTrackSelector.Output;
import org.telegram.messenger.exoplayer.dash.mpd.AdaptationSet;
import org.telegram.messenger.exoplayer.dash.mpd.ContentProtection;
import org.telegram.messenger.exoplayer.dash.mpd.MediaPresentationDescription;
import org.telegram.messenger.exoplayer.dash.mpd.Period;
import org.telegram.messenger.exoplayer.dash.mpd.RangedUri;
import org.telegram.messenger.exoplayer.dash.mpd.Representation;
import org.telegram.messenger.exoplayer.drm.DrmInitData;
import org.telegram.messenger.exoplayer.drm.DrmInitData.Mapped;
import org.telegram.messenger.exoplayer.extractor.ChunkIndex;
import org.telegram.messenger.exoplayer.extractor.mp4.FragmentedMp4Extractor;
import org.telegram.messenger.exoplayer.extractor.webm.WebmExtractor;
import org.telegram.messenger.exoplayer.hls.HlsChunkSource;
import org.telegram.messenger.exoplayer.upstream.DataSource;
import org.telegram.messenger.exoplayer.upstream.DataSpec;
import org.telegram.messenger.exoplayer.util.Clock;
import org.telegram.messenger.exoplayer.util.ManifestFetcher;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.messenger.exoplayer.util.SystemClock;

public class DashChunkSource implements ChunkSource, Output {
    private static final String TAG = "DashChunkSource";
    private final FormatEvaluator adaptiveFormatEvaluator;
    private TimeRange availableRange;
    private final long[] availableRangeValues;
    private MediaPresentationDescription currentManifest;
    private final DataSource dataSource;
    private final long elapsedRealtimeOffsetUs;
    private ExposedTrack enabledTrack;
    private final Evaluation evaluation;
    private final Handler eventHandler;
    private final EventListener eventListener;
    private final int eventSourceId;
    private IOException fatalError;
    private boolean lastChunkWasInitialization;
    private final boolean live;
    private final long liveEdgeLatencyUs;
    private final ManifestFetcher<MediaPresentationDescription> manifestFetcher;
    private int nextPeriodHolderIndex;
    private final SparseArray<PeriodHolder> periodHolders;
    private boolean prepareCalled;
    private MediaPresentationDescription processedManifest;
    private boolean startAtLiveEdge;
    private final Clock systemClock;
    private final DashTrackSelector trackSelector;
    private final ArrayList<ExposedTrack> tracks;

    public interface EventListener {
        void onAvailableRangeChanged(int i, TimeRange timeRange);
    }

    protected static final class ExposedTrack {
        private final int adaptationSetIndex;
        private final Format[] adaptiveFormats;
        public final int adaptiveMaxHeight;
        public final int adaptiveMaxWidth;
        private final Format fixedFormat;
        public final MediaFormat trackFormat;

        public ExposedTrack(MediaFormat trackFormat, int adaptationSetIndex, Format fixedFormat) {
            this.trackFormat = trackFormat;
            this.adaptationSetIndex = adaptationSetIndex;
            this.fixedFormat = fixedFormat;
            this.adaptiveFormats = null;
            this.adaptiveMaxWidth = -1;
            this.adaptiveMaxHeight = -1;
        }

        public ExposedTrack(MediaFormat trackFormat, int adaptationSetIndex, Format[] adaptiveFormats, int maxWidth, int maxHeight) {
            this.trackFormat = trackFormat;
            this.adaptationSetIndex = adaptationSetIndex;
            this.adaptiveFormats = adaptiveFormats;
            this.adaptiveMaxWidth = maxWidth;
            this.adaptiveMaxHeight = maxHeight;
            this.fixedFormat = null;
        }

        public boolean isAdaptive() {
            return this.adaptiveFormats != null;
        }
    }

    public static class NoAdaptationSetException extends IOException {
        public NoAdaptationSetException(String message) {
            super(message);
        }
    }

    protected static final class PeriodHolder {
        private long availableEndTimeUs;
        private long availableStartTimeUs;
        private DrmInitData drmInitData;
        private boolean indexIsExplicit;
        private boolean indexIsUnbounded;
        public final int localIndex;
        public final HashMap<String, RepresentationHolder> representationHolders;
        private final int[] representationIndices;
        public final long startTimeUs;

        public PeriodHolder(int localIndex, MediaPresentationDescription manifest, int manifestIndex, ExposedTrack selectedTrack) {
            this.localIndex = localIndex;
            Period period = manifest.getPeriod(manifestIndex);
            long periodDurationUs = getPeriodDurationUs(manifest, manifestIndex);
            AdaptationSet adaptationSet = (AdaptationSet) period.adaptationSets.get(selectedTrack.adaptationSetIndex);
            List<Representation> representations = adaptationSet.representations;
            this.startTimeUs = period.startMs * 1000;
            this.drmInitData = getDrmInitData(adaptationSet);
            if (selectedTrack.isAdaptive()) {
                this.representationIndices = new int[selectedTrack.adaptiveFormats.length];
                for (int j = 0; j < selectedTrack.adaptiveFormats.length; j++) {
                    this.representationIndices[j] = getRepresentationIndex(representations, selectedTrack.adaptiveFormats[j].id);
                }
            } else {
                this.representationIndices = new int[]{getRepresentationIndex(representations, selectedTrack.fixedFormat.id)};
            }
            this.representationHolders = new HashMap();
            for (int i : this.representationIndices) {
                Representation representation = (Representation) representations.get(i);
                this.representationHolders.put(representation.format.id, new RepresentationHolder(this.startTimeUs, periodDurationUs, representation));
            }
            updateRepresentationIndependentProperties(periodDurationUs, (Representation) representations.get(this.representationIndices[0]));
        }

        public void updatePeriod(MediaPresentationDescription manifest, int manifestIndex, ExposedTrack selectedTrack) throws BehindLiveWindowException {
            Period period = manifest.getPeriod(manifestIndex);
            long periodDurationUs = getPeriodDurationUs(manifest, manifestIndex);
            List<Representation> representations = ((AdaptationSet) period.adaptationSets.get(selectedTrack.adaptationSetIndex)).representations;
            for (int i : this.representationIndices) {
                Representation representation = (Representation) representations.get(i);
                ((RepresentationHolder) this.representationHolders.get(representation.format.id)).updateRepresentation(periodDurationUs, representation);
            }
            updateRepresentationIndependentProperties(periodDurationUs, (Representation) representations.get(this.representationIndices[0]));
        }

        public long getAvailableStartTimeUs() {
            return this.availableStartTimeUs;
        }

        public long getAvailableEndTimeUs() {
            if (!isIndexUnbounded()) {
                return this.availableEndTimeUs;
            }
            throw new IllegalStateException("Period has unbounded index");
        }

        public boolean isIndexUnbounded() {
            return this.indexIsUnbounded;
        }

        public boolean isIndexExplicit() {
            return this.indexIsExplicit;
        }

        public DrmInitData getDrmInitData() {
            return this.drmInitData;
        }

        private void updateRepresentationIndependentProperties(long periodDurationUs, Representation arbitaryRepresentation) {
            boolean z = true;
            DashSegmentIndex segmentIndex = arbitaryRepresentation.getIndex();
            if (segmentIndex != null) {
                int firstSegmentNum = segmentIndex.getFirstSegmentNum();
                int lastSegmentNum = segmentIndex.getLastSegmentNum(periodDurationUs);
                if (lastSegmentNum != -1) {
                    z = false;
                }
                this.indexIsUnbounded = z;
                this.indexIsExplicit = segmentIndex.isExplicit();
                this.availableStartTimeUs = this.startTimeUs + segmentIndex.getTimeUs(firstSegmentNum);
                if (!this.indexIsUnbounded) {
                    this.availableEndTimeUs = (this.startTimeUs + segmentIndex.getTimeUs(lastSegmentNum)) + segmentIndex.getDurationUs(lastSegmentNum, periodDurationUs);
                    return;
                }
                return;
            }
            this.indexIsUnbounded = false;
            this.indexIsExplicit = true;
            this.availableStartTimeUs = this.startTimeUs;
            this.availableEndTimeUs = this.startTimeUs + periodDurationUs;
        }

        private static int getRepresentationIndex(List<Representation> representations, String formatId) {
            for (int i = 0; i < representations.size(); i++) {
                if (formatId.equals(((Representation) representations.get(i)).format.id)) {
                    return i;
                }
            }
            throw new IllegalStateException("Missing format id: " + formatId);
        }

        private static DrmInitData getDrmInitData(AdaptationSet adaptationSet) {
            if (adaptationSet.contentProtections.isEmpty()) {
                return null;
            }
            DrmInitData drmInitData = null;
            for (int i = 0; i < adaptationSet.contentProtections.size(); i++) {
                ContentProtection contentProtection = (ContentProtection) adaptationSet.contentProtections.get(i);
                if (!(contentProtection.uuid == null || contentProtection.data == null)) {
                    if (drmInitData == null) {
                        drmInitData = new Mapped();
                    }
                    drmInitData.put(contentProtection.uuid, contentProtection.data);
                }
            }
            return drmInitData;
        }

        private static long getPeriodDurationUs(MediaPresentationDescription manifest, int index) {
            long durationMs = manifest.getPeriodDuration(index);
            if (durationMs == -1) {
                return -1;
            }
            return 1000 * durationMs;
        }
    }

    protected static final class RepresentationHolder {
        public final ChunkExtractorWrapper extractorWrapper;
        public MediaFormat mediaFormat;
        public final boolean mimeTypeIsRawText;
        private long periodDurationUs;
        private final long periodStartTimeUs;
        public Representation representation;
        public DashSegmentIndex segmentIndex;
        private int segmentNumShift;

        public RepresentationHolder(long periodStartTimeUs, long periodDurationUs, Representation representation) {
            ChunkExtractorWrapper chunkExtractorWrapper;
            this.periodStartTimeUs = periodStartTimeUs;
            this.periodDurationUs = periodDurationUs;
            this.representation = representation;
            String mimeType = representation.format.mimeType;
            this.mimeTypeIsRawText = DashChunkSource.mimeTypeIsRawText(mimeType);
            if (this.mimeTypeIsRawText) {
                chunkExtractorWrapper = null;
            } else {
                chunkExtractorWrapper = new ChunkExtractorWrapper(DashChunkSource.mimeTypeIsWebm(mimeType) ? new WebmExtractor() : new FragmentedMp4Extractor());
            }
            this.extractorWrapper = chunkExtractorWrapper;
            this.segmentIndex = representation.getIndex();
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

        public int getSegmentNum(long positionUs) {
            return this.segmentIndex.getSegmentNum(positionUs - this.periodStartTimeUs, this.periodDurationUs) + this.segmentNumShift;
        }

        public long getSegmentStartTimeUs(int segmentNum) {
            return this.segmentIndex.getTimeUs(segmentNum - this.segmentNumShift) + this.periodStartTimeUs;
        }

        public long getSegmentEndTimeUs(int segmentNum) {
            return getSegmentStartTimeUs(segmentNum) + this.segmentIndex.getDurationUs(segmentNum - this.segmentNumShift, this.periodDurationUs);
        }

        public int getLastSegmentNum() {
            return this.segmentIndex.getLastSegmentNum(this.periodDurationUs);
        }

        public boolean isBeyondLastSegment(int segmentNum) {
            int lastSegmentNum = getLastSegmentNum();
            if (lastSegmentNum != -1 && segmentNum > this.segmentNumShift + lastSegmentNum) {
                return true;
            }
            return false;
        }

        public int getFirstAvailableSegmentNum() {
            return this.segmentIndex.getFirstSegmentNum() + this.segmentNumShift;
        }

        public RangedUri getSegmentUrl(int segmentNum) {
            return this.segmentIndex.getSegmentUrl(segmentNum - this.segmentNumShift);
        }
    }

    public DashChunkSource(DashTrackSelector trackSelector, DataSource dataSource, FormatEvaluator adaptiveFormatEvaluator, long durationMs, int adaptationSetType, Representation... representations) {
        this(trackSelector, dataSource, adaptiveFormatEvaluator, durationMs, adaptationSetType, Arrays.asList(representations));
    }

    public DashChunkSource(DashTrackSelector trackSelector, DataSource dataSource, FormatEvaluator adaptiveFormatEvaluator, long durationMs, int adaptationSetType, List<Representation> representations) {
        this(buildManifest(durationMs, adaptationSetType, representations), trackSelector, dataSource, adaptiveFormatEvaluator);
    }

    public DashChunkSource(MediaPresentationDescription manifest, DashTrackSelector trackSelector, DataSource dataSource, FormatEvaluator adaptiveFormatEvaluator) {
        this(null, manifest, trackSelector, dataSource, adaptiveFormatEvaluator, new SystemClock(), 0, 0, false, null, null, 0);
    }

    public DashChunkSource(ManifestFetcher<MediaPresentationDescription> manifestFetcher, DashTrackSelector trackSelector, DataSource dataSource, FormatEvaluator adaptiveFormatEvaluator, long liveEdgeLatencyMs, long elapsedRealtimeOffsetMs, Handler eventHandler, EventListener eventListener, int eventSourceId) {
        this(manifestFetcher, (MediaPresentationDescription) manifestFetcher.getManifest(), trackSelector, dataSource, adaptiveFormatEvaluator, new SystemClock(), liveEdgeLatencyMs * 1000, elapsedRealtimeOffsetMs * 1000, true, eventHandler, eventListener, eventSourceId);
    }

    public DashChunkSource(ManifestFetcher<MediaPresentationDescription> manifestFetcher, DashTrackSelector trackSelector, DataSource dataSource, FormatEvaluator adaptiveFormatEvaluator, long liveEdgeLatencyMs, long elapsedRealtimeOffsetMs, boolean startAtLiveEdge, Handler eventHandler, EventListener eventListener, int eventSourceId) {
        this(manifestFetcher, (MediaPresentationDescription) manifestFetcher.getManifest(), trackSelector, dataSource, adaptiveFormatEvaluator, new SystemClock(), liveEdgeLatencyMs * 1000, elapsedRealtimeOffsetMs * 1000, startAtLiveEdge, eventHandler, eventListener, eventSourceId);
    }

    DashChunkSource(ManifestFetcher<MediaPresentationDescription> manifestFetcher, MediaPresentationDescription initialManifest, DashTrackSelector trackSelector, DataSource dataSource, FormatEvaluator adaptiveFormatEvaluator, Clock systemClock, long liveEdgeLatencyUs, long elapsedRealtimeOffsetUs, boolean startAtLiveEdge, Handler eventHandler, EventListener eventListener, int eventSourceId) {
        this.manifestFetcher = manifestFetcher;
        this.currentManifest = initialManifest;
        this.trackSelector = trackSelector;
        this.dataSource = dataSource;
        this.adaptiveFormatEvaluator = adaptiveFormatEvaluator;
        this.systemClock = systemClock;
        this.liveEdgeLatencyUs = liveEdgeLatencyUs;
        this.elapsedRealtimeOffsetUs = elapsedRealtimeOffsetUs;
        this.startAtLiveEdge = startAtLiveEdge;
        this.eventHandler = eventHandler;
        this.eventListener = eventListener;
        this.eventSourceId = eventSourceId;
        this.evaluation = new Evaluation();
        this.availableRangeValues = new long[2];
        this.periodHolders = new SparseArray();
        this.tracks = new ArrayList();
        this.live = initialManifest.dynamic;
    }

    public void maybeThrowError() throws IOException {
        if (this.fatalError != null) {
            throw this.fatalError;
        } else if (this.manifestFetcher != null) {
            this.manifestFetcher.maybeThrowError();
        }
    }

    public boolean prepare() {
        if (!this.prepareCalled) {
            this.prepareCalled = true;
            try {
                this.trackSelector.selectTracks(this.currentManifest, 0, this);
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
            processManifest((MediaPresentationDescription) this.manifestFetcher.getManifest());
            return;
        }
        processManifest(this.currentManifest);
    }

    public void continueBuffering(long playbackPositionUs) {
        if (this.manifestFetcher != null && this.currentManifest.dynamic && this.fatalError == null) {
            MediaPresentationDescription newManifest = (MediaPresentationDescription) this.manifestFetcher.getManifest();
            if (!(newManifest == null || newManifest == this.processedManifest)) {
                processManifest(newManifest);
                this.processedManifest = newManifest;
            }
            long minUpdatePeriod = this.currentManifest.minUpdatePeriod;
            if (minUpdatePeriod == 0) {
                minUpdatePeriod = HlsChunkSource.DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS;
            }
            if (android.os.SystemClock.elapsedRealtime() > this.manifestFetcher.getManifestLoadStartTimestamp() + minUpdatePeriod) {
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
        if (this.evaluation.format == null || !this.lastChunkWasInitialization) {
            if (this.enabledTrack.isAdaptive()) {
                this.adaptiveFormatEvaluator.evaluate(queue, playbackPositionUs, this.enabledTrack.adaptiveFormats, this.evaluation);
            } else {
                this.evaluation.format = this.enabledTrack.fixedFormat;
                this.evaluation.trigger = 2;
            }
        }
        Format selectedFormat = this.evaluation.format;
        out.queueSize = this.evaluation.queueSize;
        if (selectedFormat == null) {
            out.chunk = null;
        } else if (out.queueSize != queue.size() || out.chunk == null || !out.chunk.format.equals(selectedFormat)) {
            PeriodHolder periodHolder;
            boolean startingNewPeriod;
            out.chunk = null;
            this.availableRange.getCurrentBoundsUs(this.availableRangeValues);
            if (queue.isEmpty()) {
                if (this.live) {
                    if (playbackPositionUs != 0) {
                        this.startAtLiveEdge = false;
                    }
                    if (this.startAtLiveEdge) {
                        playbackPositionUs = Math.max(this.availableRangeValues[0], this.availableRangeValues[1] - this.liveEdgeLatencyUs);
                    } else {
                        playbackPositionUs = Math.max(Math.min(playbackPositionUs, this.availableRangeValues[1] - 1), this.availableRangeValues[0]);
                    }
                }
                periodHolder = findPeriodHolder(playbackPositionUs);
                startingNewPeriod = true;
            } else {
                if (this.startAtLiveEdge) {
                    this.startAtLiveEdge = false;
                }
                MediaChunk previous = (MediaChunk) queue.get(out.queueSize - 1);
                long nextSegmentStartTimeUs = previous.endTimeUs;
                if (this.live && nextSegmentStartTimeUs < this.availableRangeValues[0]) {
                    this.fatalError = new BehindLiveWindowException();
                    return;
                } else if (!this.currentManifest.dynamic || nextSegmentStartTimeUs < this.availableRangeValues[1]) {
                    PeriodHolder lastPeriodHolder = (PeriodHolder) this.periodHolders.valueAt(this.periodHolders.size() - 1);
                    if (previous.parentId != lastPeriodHolder.localIndex || !((RepresentationHolder) lastPeriodHolder.representationHolders.get(previous.format.id)).isBeyondLastSegment(previous.getNextChunkIndex())) {
                        startingNewPeriod = false;
                        periodHolder = (PeriodHolder) this.periodHolders.get(previous.parentId);
                        if (periodHolder == null) {
                            periodHolder = (PeriodHolder) this.periodHolders.valueAt(0);
                            startingNewPeriod = true;
                        } else if (!periodHolder.isIndexUnbounded() && ((RepresentationHolder) periodHolder.representationHolders.get(previous.format.id)).isBeyondLastSegment(previous.getNextChunkIndex())) {
                            periodHolder = (PeriodHolder) this.periodHolders.get(previous.parentId + 1);
                            startingNewPeriod = true;
                        }
                    } else if (!this.currentManifest.dynamic) {
                        out.endOfStream = true;
                        return;
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            }
            RepresentationHolder representationHolder = (RepresentationHolder) periodHolder.representationHolders.get(selectedFormat.id);
            Representation selectedRepresentation = representationHolder.representation;
            RangedUri pendingInitializationUri = null;
            RangedUri pendingIndexUri = null;
            MediaFormat mediaFormat = representationHolder.mediaFormat;
            if (mediaFormat == null) {
                pendingInitializationUri = selectedRepresentation.getInitializationUri();
            }
            if (representationHolder.segmentIndex == null) {
                pendingIndexUri = selectedRepresentation.getIndexUri();
            }
            if (pendingInitializationUri == null && pendingIndexUri == null) {
                int segmentNum;
                if (queue.isEmpty()) {
                    segmentNum = representationHolder.getSegmentNum(playbackPositionUs);
                } else if (startingNewPeriod) {
                    segmentNum = representationHolder.getFirstAvailableSegmentNum();
                } else {
                    segmentNum = ((MediaChunk) queue.get(out.queueSize - 1)).getNextChunkIndex();
                }
                Chunk nextMediaChunk = newMediaChunk(periodHolder, representationHolder, this.dataSource, mediaFormat, this.enabledTrack, segmentNum, this.evaluation.trigger);
                this.lastChunkWasInitialization = false;
                out.chunk = nextMediaChunk;
                return;
            }
            Chunk initializationChunk = newInitializationChunk(pendingInitializationUri, pendingIndexUri, selectedRepresentation, representationHolder.extractorWrapper, this.dataSource, periodHolder.localIndex, this.evaluation.trigger);
            this.lastChunkWasInitialization = true;
            out.chunk = initializationChunk;
        }
    }

    public void onChunkLoadCompleted(Chunk chunk) {
        if (chunk instanceof InitializationChunk) {
            InitializationChunk initializationChunk = (InitializationChunk) chunk;
            String formatId = initializationChunk.format.id;
            PeriodHolder periodHolder = (PeriodHolder) this.periodHolders.get(initializationChunk.parentId);
            if (periodHolder != null) {
                RepresentationHolder representationHolder = (RepresentationHolder) periodHolder.representationHolders.get(formatId);
                if (initializationChunk.hasFormat()) {
                    representationHolder.mediaFormat = initializationChunk.getFormat();
                }
                if (representationHolder.segmentIndex == null && initializationChunk.hasSeekMap()) {
                    representationHolder.segmentIndex = new DashWrappingSegmentIndex((ChunkIndex) initializationChunk.getSeekMap(), initializationChunk.dataSpec.uri.toString());
                }
                if (periodHolder.drmInitData == null && initializationChunk.hasDrmInitData()) {
                    periodHolder.drmInitData = initializationChunk.getDrmInitData();
                }
            }
        }
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
        this.periodHolders.clear();
        this.evaluation.format = null;
        this.availableRange = null;
        this.fatalError = null;
        this.enabledTrack = null;
    }

    public void adaptiveTrack(MediaPresentationDescription manifest, int periodIndex, int adaptationSetIndex, int[] representationIndices) {
        if (this.adaptiveFormatEvaluator == null) {
            Log.w(TAG, "Skipping adaptive track (missing format evaluator)");
            return;
        }
        AdaptationSet adaptationSet = (AdaptationSet) manifest.getPeriod(periodIndex).adaptationSets.get(adaptationSetIndex);
        int maxWidth = 0;
        int maxHeight = 0;
        Format maxHeightRepresentationFormat = null;
        Format[] representationFormats = new Format[representationIndices.length];
        for (int i = 0; i < representationFormats.length; i++) {
            Format format = ((Representation) adaptationSet.representations.get(representationIndices[i])).format;
            if (maxHeightRepresentationFormat == null || format.height > maxHeight) {
                maxHeightRepresentationFormat = format;
            }
            maxWidth = Math.max(maxWidth, format.width);
            maxHeight = Math.max(maxHeight, format.height);
            representationFormats[i] = format;
        }
        Arrays.sort(representationFormats, new DecreasingBandwidthComparator());
        long trackDurationUs = this.live ? -1 : manifest.duration * 1000;
        String mediaMimeType = getMediaMimeType(maxHeightRepresentationFormat);
        if (mediaMimeType == null) {
            Log.w(TAG, "Skipped adaptive track (unknown media mime type)");
            return;
        }
        MediaFormat trackFormat = getTrackFormat(adaptationSet.type, maxHeightRepresentationFormat, mediaMimeType, trackDurationUs);
        if (trackFormat == null) {
            Log.w(TAG, "Skipped adaptive track (unknown media format)");
        } else {
            this.tracks.add(new ExposedTrack(trackFormat.copyAsAdaptive(null), adaptationSetIndex, representationFormats, maxWidth, maxHeight));
        }
    }

    public void fixedTrack(MediaPresentationDescription manifest, int periodIndex, int adaptationSetIndex, int representationIndex) {
        AdaptationSet adaptationSet = (AdaptationSet) manifest.getPeriod(periodIndex).adaptationSets.get(adaptationSetIndex);
        Format representationFormat = ((Representation) adaptationSet.representations.get(representationIndex)).format;
        String mediaMimeType = getMediaMimeType(representationFormat);
        if (mediaMimeType == null) {
            Log.w(TAG, "Skipped track " + representationFormat.id + " (unknown media mime type)");
            return;
        }
        MediaFormat trackFormat = getTrackFormat(adaptationSet.type, representationFormat, mediaMimeType, manifest.dynamic ? -1 : manifest.duration * 1000);
        if (trackFormat == null) {
            Log.w(TAG, "Skipped track " + representationFormat.id + " (unknown media format)");
        } else {
            this.tracks.add(new ExposedTrack(trackFormat, adaptationSetIndex, representationFormat));
        }
    }

    TimeRange getAvailableRange() {
        return this.availableRange;
    }

    private static MediaPresentationDescription buildManifest(long durationMs, int adaptationSetType, List<Representation> representations) {
        return new MediaPresentationDescription(-1, durationMs, -1, false, -1, -1, null, null, Collections.singletonList(new Period(null, 0, Collections.singletonList(new AdaptationSet(0, adaptationSetType, representations)))));
    }

    private static MediaFormat getTrackFormat(int adaptationSetType, Format format, String mediaMimeType, long durationUs) {
        switch (adaptationSetType) {
            case 0:
                return MediaFormat.createVideoFormat(format.id, mediaMimeType, format.bitrate, -1, durationUs, format.width, format.height, null);
            case 1:
                return MediaFormat.createAudioFormat(format.id, mediaMimeType, format.bitrate, -1, durationUs, format.audioChannels, format.audioSamplingRate, null, format.language);
            case 2:
                return MediaFormat.createTextFormat(format.id, mediaMimeType, format.bitrate, durationUs, format.language);
            default:
                return null;
        }
    }

    private static String getMediaMimeType(Format format) {
        String formatMimeType = format.mimeType;
        if (MimeTypes.isAudio(formatMimeType)) {
            return MimeTypes.getAudioMediaMimeType(format.codecs);
        }
        if (MimeTypes.isVideo(formatMimeType)) {
            return MimeTypes.getVideoMediaMimeType(format.codecs);
        }
        if (mimeTypeIsRawText(formatMimeType)) {
            return formatMimeType;
        }
        if (MimeTypes.APPLICATION_MP4.equals(formatMimeType)) {
            if (XMLSubtitleSampleEntry.TYPE.equals(format.codecs)) {
                return MimeTypes.APPLICATION_TTML;
            }
            if (WebVTTSampleEntry.TYPE.equals(format.codecs)) {
                return MimeTypes.APPLICATION_MP4VTT;
            }
        }
        return null;
    }

    static boolean mimeTypeIsWebm(String mimeType) {
        return mimeType.startsWith(MimeTypes.VIDEO_WEBM) || mimeType.startsWith(MimeTypes.AUDIO_WEBM) || mimeType.startsWith(MimeTypes.APPLICATION_WEBM);
    }

    static boolean mimeTypeIsRawText(String mimeType) {
        return MimeTypes.TEXT_VTT.equals(mimeType) || MimeTypes.APPLICATION_TTML.equals(mimeType);
    }

    private Chunk newInitializationChunk(RangedUri initializationUri, RangedUri indexUri, Representation representation, ChunkExtractorWrapper extractor, DataSource dataSource, int manifestIndex, int trigger) {
        RangedUri requestUri;
        if (initializationUri != null) {
            requestUri = initializationUri.attemptMerge(indexUri);
            if (requestUri == null) {
                requestUri = initializationUri;
            }
        } else {
            requestUri = indexUri;
        }
        return new InitializationChunk(dataSource, new DataSpec(requestUri.getUri(), requestUri.start, requestUri.length, representation.getCacheKey()), trigger, representation.format, extractor, manifestIndex);
    }

    protected Chunk newMediaChunk(PeriodHolder periodHolder, RepresentationHolder representationHolder, DataSource dataSource, MediaFormat mediaFormat, ExposedTrack enabledTrack, int segmentNum, int trigger) {
        Representation representation = representationHolder.representation;
        Format format = representation.format;
        long startTimeUs = representationHolder.getSegmentStartTimeUs(segmentNum);
        long endTimeUs = representationHolder.getSegmentEndTimeUs(segmentNum);
        RangedUri segmentUri = representationHolder.getSegmentUrl(segmentNum);
        DataSpec dataSpec = new DataSpec(segmentUri.getUri(), segmentUri.start, segmentUri.length, representation.getCacheKey());
        long sampleOffsetUs = periodHolder.startTimeUs - representation.presentationTimeOffsetUs;
        if (mimeTypeIsRawText(format.mimeType)) {
            return new SingleSampleMediaChunk(dataSource, dataSpec, 1, format, startTimeUs, endTimeUs, segmentNum, enabledTrack.trackFormat, null, periodHolder.localIndex);
        }
        return new ContainerMediaChunk(dataSource, dataSpec, trigger, format, startTimeUs, endTimeUs, segmentNum, sampleOffsetUs, representationHolder.extractorWrapper, mediaFormat, enabledTrack.adaptiveMaxWidth, enabledTrack.adaptiveMaxHeight, periodHolder.drmInitData, mediaFormat != null, periodHolder.localIndex);
    }

    private long getNowUnixTimeUs() {
        if (this.elapsedRealtimeOffsetUs != 0) {
            return (this.systemClock.elapsedRealtime() * 1000) + this.elapsedRealtimeOffsetUs;
        }
        return System.currentTimeMillis() * 1000;
    }

    private PeriodHolder findPeriodHolder(long positionUs) {
        if (positionUs < ((PeriodHolder) this.periodHolders.valueAt(0)).getAvailableStartTimeUs()) {
            return (PeriodHolder) this.periodHolders.valueAt(0);
        }
        for (int i = 0; i < this.periodHolders.size() - 1; i++) {
            PeriodHolder periodHolder = (PeriodHolder) this.periodHolders.valueAt(i);
            if (positionUs < periodHolder.getAvailableEndTimeUs()) {
                return periodHolder;
            }
        }
        return (PeriodHolder) this.periodHolders.valueAt(this.periodHolders.size() - 1);
    }

    private void processManifest(MediaPresentationDescription manifest) {
        Period firstPeriod = manifest.getPeriod(0);
        while (this.periodHolders.size() > 0 && ((PeriodHolder) this.periodHolders.valueAt(0)).startTimeUs < firstPeriod.startMs * 1000) {
            this.periodHolders.remove(((PeriodHolder) this.periodHolders.valueAt(0)).localIndex);
        }
        if (this.periodHolders.size() <= manifest.getPeriodCount()) {
            try {
                int periodHolderCount = this.periodHolders.size();
                if (periodHolderCount > 0) {
                    ((PeriodHolder) this.periodHolders.valueAt(0)).updatePeriod(manifest, 0, this.enabledTrack);
                    if (periodHolderCount > 1) {
                        int lastIndex = periodHolderCount - 1;
                        ((PeriodHolder) this.periodHolders.valueAt(lastIndex)).updatePeriod(manifest, lastIndex, this.enabledTrack);
                    }
                }
                for (int i = this.periodHolders.size(); i < manifest.getPeriodCount(); i++) {
                    this.periodHolders.put(this.nextPeriodHolderIndex, new PeriodHolder(this.nextPeriodHolderIndex, manifest, i, this.enabledTrack));
                    this.nextPeriodHolderIndex++;
                }
                TimeRange newAvailableRange = getAvailableRange(getNowUnixTimeUs());
                if (this.availableRange == null || !this.availableRange.equals(newAvailableRange)) {
                    this.availableRange = newAvailableRange;
                    notifyAvailableRangeChanged(this.availableRange);
                }
                this.currentManifest = manifest;
            } catch (BehindLiveWindowException e) {
                this.fatalError = e;
            }
        }
    }

    private TimeRange getAvailableRange(long nowUnixTimeUs) {
        PeriodHolder firstPeriod = (PeriodHolder) this.periodHolders.valueAt(0);
        PeriodHolder lastPeriod = (PeriodHolder) this.periodHolders.valueAt(this.periodHolders.size() - 1);
        if (!this.currentManifest.dynamic || lastPeriod.isIndexExplicit()) {
            return new StaticTimeRange(firstPeriod.getAvailableStartTimeUs(), lastPeriod.getAvailableEndTimeUs());
        }
        long maxEndPositionUs;
        long minStartPositionUs = firstPeriod.getAvailableStartTimeUs();
        if (lastPeriod.isIndexUnbounded()) {
            maxEndPositionUs = Long.MAX_VALUE;
        } else {
            maxEndPositionUs = lastPeriod.getAvailableEndTimeUs();
        }
        return new DynamicTimeRange(minStartPositionUs, maxEndPositionUs, (this.systemClock.elapsedRealtime() * 1000) - (nowUnixTimeUs - (this.currentManifest.availabilityStartTime * 1000)), this.currentManifest.timeShiftBufferDepth == -1 ? -1 : this.currentManifest.timeShiftBufferDepth * 1000, this.systemClock);
    }

    private void notifyAvailableRangeChanged(final TimeRange seekRange) {
        if (this.eventHandler != null && this.eventListener != null) {
            this.eventHandler.post(new Runnable() {
                public void run() {
                    DashChunkSource.this.eventListener.onAvailableRangeChanged(DashChunkSource.this.eventSourceId, seekRange);
                }
            });
        }
    }
}
