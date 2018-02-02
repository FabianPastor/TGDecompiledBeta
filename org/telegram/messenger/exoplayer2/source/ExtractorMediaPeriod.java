package org.telegram.messenger.exoplayer2.source;

import android.net.Uri;
import android.os.Handler;
import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.FormatHolder;
import org.telegram.messenger.exoplayer2.SeekParameters;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.extractor.DefaultExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.PositionHolder;
import org.telegram.messenger.exoplayer2.extractor.SeekMap;
import org.telegram.messenger.exoplayer2.extractor.SeekMap.SeekPoints;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.source.SampleQueue.UpstreamFormatChangedListener;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.Loader;
import org.telegram.messenger.exoplayer2.upstream.Loader.Callback;
import org.telegram.messenger.exoplayer2.upstream.Loader.Loadable;
import org.telegram.messenger.exoplayer2.upstream.Loader.ReleaseCallback;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.ConditionVariable;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.Util;

final class ExtractorMediaPeriod implements ExtractorOutput, MediaPeriod, UpstreamFormatChangedListener, Callback<ExtractingLoadable>, ReleaseCallback {
    private static final long DEFAULT_LAST_SAMPLE_DURATION_US = 10000;
    private int actualMinLoadableRetryCount;
    private final Allocator allocator;
    private MediaPeriod.Callback callback;
    private final long continueLoadingCheckIntervalBytes;
    private final String customCacheKey;
    private final DataSource dataSource;
    private long durationUs;
    private int enabledTrackCount;
    private final EventDispatcher eventDispatcher;
    private int extractedSamplesCountAtStartOfLoad;
    private final ExtractorHolder extractorHolder;
    private final Handler handler;
    private boolean haveAudioVideoTracks;
    private long lastSeekPositionUs;
    private long length;
    private final Listener listener;
    private final ConditionVariable loadCondition;
    private final Loader loader = new Loader("Loader:ExtractorMediaPeriod");
    private boolean loadingFinished;
    private final Runnable maybeFinishPrepareRunnable;
    private final int minLoadableRetryCount;
    private boolean notifyDiscontinuity;
    private final Runnable onContinueLoadingRequestedRunnable;
    private boolean pendingDeferredRetry;
    private long pendingResetPositionUs;
    private boolean prepared;
    private boolean released;
    private int[] sampleQueueTrackIds;
    private SampleQueue[] sampleQueues;
    private boolean sampleQueuesBuilt;
    private SeekMap seekMap;
    private boolean seenFirstTrackSelection;
    private boolean[] trackEnabledStates;
    private boolean[] trackFormatNotificationSent;
    private boolean[] trackIsAudioVideoFlags;
    private TrackGroupArray tracks;
    private final Uri uri;

    private static final class ExtractorHolder {
        private Extractor extractor;
        private final ExtractorOutput extractorOutput;
        private final Extractor[] extractors;

        public ExtractorHolder(Extractor[] extractors, ExtractorOutput extractorOutput) {
            this.extractors = extractors;
            this.extractorOutput = extractorOutput;
        }

        public Extractor selectExtractor(ExtractorInput input, Uri uri) throws IOException, InterruptedException {
            if (this.extractor != null) {
                return this.extractor;
            }
            Extractor[] extractorArr = this.extractors;
            int length = extractorArr.length;
            int i = 0;
            loop0:
            while (i < length) {
                Extractor extractor = extractorArr[i];
                try {
                    if (extractor.sniff(input)) {
                        this.extractor = extractor;
                        input.resetPeekPosition();
                        break loop0;
                    }
                    i++;
                } catch (EOFException e) {
                    i++;
                } finally {
                    input.resetPeekPosition();
                }
            }
            if (this.extractor == null) {
                throw new UnrecognizedInputFormatException("None of the available extractors (" + Util.getCommaDelimitedSimpleClassNames(this.extractors) + ") could read the stream.", uri);
            }
            this.extractor.init(this.extractorOutput);
            return this.extractor;
        }

        public void release() {
            if (this.extractor != null) {
                this.extractor.release();
                this.extractor = null;
            }
        }
    }

    interface Listener {
        void onSourceInfoRefreshed(long j, boolean z);
    }

    final class ExtractingLoadable implements Loadable {
        private long bytesLoaded;
        private final DataSource dataSource;
        private DataSpec dataSpec;
        private final ExtractorHolder extractorHolder;
        private long length = -1;
        private volatile boolean loadCanceled;
        private final ConditionVariable loadCondition;
        private boolean pendingExtractorSeek = true;
        private final PositionHolder positionHolder = new PositionHolder();
        private long seekTimeUs;
        private final Uri uri;

        public ExtractingLoadable(Uri uri, DataSource dataSource, ExtractorHolder extractorHolder, ConditionVariable loadCondition) {
            this.uri = (Uri) Assertions.checkNotNull(uri);
            this.dataSource = (DataSource) Assertions.checkNotNull(dataSource);
            this.extractorHolder = (ExtractorHolder) Assertions.checkNotNull(extractorHolder);
            this.loadCondition = loadCondition;
        }

        public void setLoadPosition(long position, long timeUs) {
            this.positionHolder.position = position;
            this.seekTimeUs = timeUs;
            this.pendingExtractorSeek = true;
        }

        public void cancelLoad() {
            this.loadCanceled = true;
        }

        public boolean isLoadCanceled() {
            return this.loadCanceled;
        }

        public void load() throws IOException, InterruptedException {
            Throwable th;
            int result = 0;
            while (result == 0 && !this.loadCanceled) {
                ExtractorInput input;
                try {
                    long position = this.positionHolder.position;
                    this.dataSpec = new DataSpec(this.uri, position, -1, ExtractorMediaPeriod.this.customCacheKey);
                    this.length = this.dataSource.open(this.dataSpec);
                    if (this.length != -1) {
                        this.length += position;
                    }
                    input = new DefaultExtractorInput(this.dataSource, position, this.length);
                    try {
                        Extractor extractor = this.extractorHolder.selectExtractor(input, this.dataSource.getUri());
                        if (this.pendingExtractorSeek) {
                            extractor.seek(position, this.seekTimeUs);
                            this.pendingExtractorSeek = false;
                        }
                        while (result == 0 && !this.loadCanceled) {
                            this.loadCondition.block();
                            result = extractor.read(input, this.positionHolder);
                            if (input.getPosition() > ExtractorMediaPeriod.this.continueLoadingCheckIntervalBytes + position) {
                                position = input.getPosition();
                                this.loadCondition.close();
                                ExtractorMediaPeriod.this.handler.post(ExtractorMediaPeriod.this.onContinueLoadingRequestedRunnable);
                            }
                        }
                        if (result == 1) {
                            result = 0;
                        } else if (input != null) {
                            this.positionHolder.position = input.getPosition();
                            this.bytesLoaded = this.positionHolder.position - this.dataSpec.absoluteStreamPosition;
                        }
                        Util.closeQuietly(this.dataSource);
                    } catch (Throwable th2) {
                        th = th2;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    input = null;
                }
            }
            return;
            if (result != 1) {
                if (input != null) {
                    this.positionHolder.position = input.getPosition();
                    this.bytesLoaded = this.positionHolder.position - this.dataSpec.absoluteStreamPosition;
                }
            }
            Util.closeQuietly(this.dataSource);
            throw th;
        }
    }

    private final class SampleStreamImpl implements SampleStream {
        private final int track;

        public SampleStreamImpl(int track) {
            this.track = track;
        }

        public boolean isReady() {
            return ExtractorMediaPeriod.this.isReady(this.track);
        }

        public void maybeThrowError() throws IOException {
            ExtractorMediaPeriod.this.maybeThrowError();
        }

        public int readData(FormatHolder formatHolder, DecoderInputBuffer buffer, boolean formatRequired) {
            return ExtractorMediaPeriod.this.readData(this.track, formatHolder, buffer, formatRequired);
        }

        public int skipData(long positionUs) {
            return ExtractorMediaPeriod.this.skipData(this.track, positionUs);
        }
    }

    public ExtractorMediaPeriod(Uri uri, DataSource dataSource, Extractor[] extractors, int minLoadableRetryCount, EventDispatcher eventDispatcher, Listener listener, Allocator allocator, String customCacheKey, int continueLoadingCheckIntervalBytes) {
        this.uri = uri;
        this.dataSource = dataSource;
        this.minLoadableRetryCount = minLoadableRetryCount;
        this.eventDispatcher = eventDispatcher;
        this.listener = listener;
        this.allocator = allocator;
        this.customCacheKey = customCacheKey;
        this.continueLoadingCheckIntervalBytes = (long) continueLoadingCheckIntervalBytes;
        this.extractorHolder = new ExtractorHolder(extractors, this);
        this.loadCondition = new ConditionVariable();
        this.maybeFinishPrepareRunnable = new Runnable() {
            public void run() {
                ExtractorMediaPeriod.this.maybeFinishPrepare();
            }
        };
        this.onContinueLoadingRequestedRunnable = new Runnable() {
            public void run() {
                if (!ExtractorMediaPeriod.this.released) {
                    ExtractorMediaPeriod.this.callback.onContinueLoadingRequested(ExtractorMediaPeriod.this);
                }
            }
        };
        this.handler = new Handler();
        this.sampleQueueTrackIds = new int[0];
        this.sampleQueues = new SampleQueue[0];
        this.pendingResetPositionUs = C.TIME_UNSET;
        this.length = -1;
        this.durationUs = C.TIME_UNSET;
        if (minLoadableRetryCount == -1) {
            minLoadableRetryCount = 3;
        }
        this.actualMinLoadableRetryCount = minLoadableRetryCount;
    }

    public void release() {
        boolean releasedSynchronously = this.loader.release(this);
        if (this.prepared && !releasedSynchronously) {
            for (SampleQueue sampleQueue : this.sampleQueues) {
                sampleQueue.discardToEnd();
            }
        }
        this.handler.removeCallbacksAndMessages(null);
        this.released = true;
    }

    public void onLoaderReleased() {
        this.extractorHolder.release();
        for (SampleQueue sampleQueue : this.sampleQueues) {
            sampleQueue.reset();
        }
    }

    public void prepare(MediaPeriod.Callback callback, long positionUs) {
        this.callback = callback;
        this.loadCondition.open();
        startLoading();
    }

    public void maybeThrowPrepareError() throws IOException {
        maybeThrowError();
    }

    public TrackGroupArray getTrackGroups() {
        return this.tracks;
    }

    public long selectTracks(TrackSelection[] selections, boolean[] mayRetainStreamFlags, SampleStream[] streams, boolean[] streamResetFlags, long positionUs) {
        SampleQueue sampleQueue;
        Assertions.checkState(this.prepared);
        int oldEnabledTrackCount = this.enabledTrackCount;
        int i = 0;
        while (i < selections.length) {
            if (streams[i] != null && (selections[i] == null || !mayRetainStreamFlags[i])) {
                int track = ((SampleStreamImpl) streams[i]).track;
                Assertions.checkState(this.trackEnabledStates[track]);
                this.enabledTrackCount--;
                this.trackEnabledStates[track] = false;
                streams[i] = null;
            }
            i++;
        }
        boolean seekRequired = this.seenFirstTrackSelection ? oldEnabledTrackCount == 0 : positionUs != 0;
        i = 0;
        while (i < selections.length) {
            if (streams[i] == null && selections[i] != null) {
                TrackSelection selection = selections[i];
                Assertions.checkState(selection.length() == 1);
                Assertions.checkState(selection.getIndexInTrackGroup(0) == 0);
                track = this.tracks.indexOf(selection.getTrackGroup());
                Assertions.checkState(!this.trackEnabledStates[track]);
                this.enabledTrackCount++;
                this.trackEnabledStates[track] = true;
                streams[i] = new SampleStreamImpl(track);
                streamResetFlags[i] = true;
                if (!seekRequired) {
                    sampleQueue = this.sampleQueues[track];
                    sampleQueue.rewind();
                    if (sampleQueue.advanceTo(positionUs, true, true) != -1 || sampleQueue.getReadIndex() == 0) {
                        seekRequired = false;
                    } else {
                        seekRequired = true;
                    }
                }
            }
            i++;
        }
        if (this.enabledTrackCount == 0) {
            this.pendingDeferredRetry = false;
            this.notifyDiscontinuity = false;
            if (this.loader.isLoading()) {
                for (SampleQueue sampleQueue2 : this.sampleQueues) {
                    sampleQueue2.discardToEnd();
                }
                this.loader.cancelLoading();
            } else {
                for (SampleQueue sampleQueue22 : this.sampleQueues) {
                    sampleQueue22.reset();
                }
            }
        } else if (seekRequired) {
            positionUs = seekToUs(positionUs);
            for (i = 0; i < streams.length; i++) {
                if (streams[i] != null) {
                    streamResetFlags[i] = true;
                }
            }
        }
        this.seenFirstTrackSelection = true;
        return positionUs;
    }

    public void discardBuffer(long positionUs, boolean toKeyframe) {
        int trackCount = this.sampleQueues.length;
        for (int i = 0; i < trackCount; i++) {
            this.sampleQueues[i].discardTo(positionUs, toKeyframe, this.trackEnabledStates[i]);
        }
    }

    public void reevaluateBuffer(long positionUs) {
    }

    public boolean continueLoading(long playbackPositionUs) {
        if (this.loadingFinished || this.pendingDeferredRetry || (this.prepared && this.enabledTrackCount == 0)) {
            return false;
        }
        boolean continuedLoading = this.loadCondition.open();
        if (this.loader.isLoading()) {
            return continuedLoading;
        }
        startLoading();
        return true;
    }

    public long getNextLoadPositionUs() {
        return this.enabledTrackCount == 0 ? Long.MIN_VALUE : getBufferedPositionUs();
    }

    public long readDiscontinuity() {
        if (!this.notifyDiscontinuity || (!this.loadingFinished && getExtractedSamplesCount() <= this.extractedSamplesCountAtStartOfLoad)) {
            return C.TIME_UNSET;
        }
        this.notifyDiscontinuity = false;
        return this.lastSeekPositionUs;
    }

    public long getBufferedPositionUs() {
        if (this.loadingFinished) {
            return Long.MIN_VALUE;
        }
        if (isPendingReset()) {
            return this.pendingResetPositionUs;
        }
        long largestQueuedTimestampUs;
        if (this.haveAudioVideoTracks) {
            largestQueuedTimestampUs = Long.MAX_VALUE;
            int trackCount = this.sampleQueues.length;
            for (int i = 0; i < trackCount; i++) {
                if (this.trackIsAudioVideoFlags[i]) {
                    largestQueuedTimestampUs = Math.min(largestQueuedTimestampUs, this.sampleQueues[i].getLargestQueuedTimestampUs());
                }
            }
        } else {
            largestQueuedTimestampUs = getLargestQueuedTimestampUs();
        }
        return largestQueuedTimestampUs == Long.MIN_VALUE ? this.lastSeekPositionUs : largestQueuedTimestampUs;
    }

    public long seekToUs(long positionUs) {
        int i = 0;
        if (!this.seekMap.isSeekable()) {
            positionUs = 0;
        }
        this.lastSeekPositionUs = positionUs;
        this.notifyDiscontinuity = false;
        if (isPendingReset() || !seekInsideBufferUs(positionUs)) {
            this.pendingDeferredRetry = false;
            this.pendingResetPositionUs = positionUs;
            this.loadingFinished = false;
            if (this.loader.isLoading()) {
                this.loader.cancelLoading();
            } else {
                SampleQueue[] sampleQueueArr = this.sampleQueues;
                int length = sampleQueueArr.length;
                while (i < length) {
                    sampleQueueArr[i].reset();
                    i++;
                }
            }
        }
        return positionUs;
    }

    public long getAdjustedSeekPositionUs(long positionUs, SeekParameters seekParameters) {
        if (!this.seekMap.isSeekable()) {
            return 0;
        }
        SeekPoints seekPoints = this.seekMap.getSeekPoints(positionUs);
        return Util.resolveSeekPositionUs(positionUs, seekParameters, seekPoints.first.timeUs, seekPoints.second.timeUs);
    }

    boolean isReady(int track) {
        return !suppressRead() && (this.loadingFinished || this.sampleQueues[track].hasNextSample());
    }

    void maybeThrowError() throws IOException {
        this.loader.maybeThrowError(this.actualMinLoadableRetryCount);
    }

    int readData(int track, FormatHolder formatHolder, DecoderInputBuffer buffer, boolean formatRequired) {
        if (suppressRead()) {
            return -3;
        }
        int result = this.sampleQueues[track].read(formatHolder, buffer, formatRequired, this.loadingFinished, this.lastSeekPositionUs);
        if (result == -4) {
            maybeNotifyTrackFormat(track);
            return result;
        } else if (result != -3) {
            return result;
        } else {
            maybeStartDeferredRetry(track);
            return result;
        }
    }

    int skipData(int track, long positionUs) {
        if (suppressRead()) {
            return 0;
        }
        int skipCount;
        SampleQueue sampleQueue = this.sampleQueues[track];
        if (!this.loadingFinished || positionUs <= sampleQueue.getLargestQueuedTimestampUs()) {
            skipCount = sampleQueue.advanceTo(positionUs, true, true);
            if (skipCount == -1) {
                skipCount = 0;
            }
        } else {
            skipCount = sampleQueue.advanceToEnd();
        }
        if (skipCount > 0) {
            maybeNotifyTrackFormat(track);
            return skipCount;
        }
        maybeStartDeferredRetry(track);
        return skipCount;
    }

    private void maybeNotifyTrackFormat(int track) {
        if (!this.trackFormatNotificationSent[track]) {
            Format trackFormat = this.tracks.get(track).getFormat(0);
            this.eventDispatcher.downstreamFormatChanged(MimeTypes.getTrackType(trackFormat.sampleMimeType), trackFormat, 0, null, this.lastSeekPositionUs);
            this.trackFormatNotificationSent[track] = true;
        }
    }

    private void maybeStartDeferredRetry(int track) {
        int i = 0;
        if (this.pendingDeferredRetry && this.trackIsAudioVideoFlags[track] && !this.sampleQueues[track].hasNextSample()) {
            this.pendingResetPositionUs = 0;
            this.pendingDeferredRetry = false;
            this.notifyDiscontinuity = true;
            this.lastSeekPositionUs = 0;
            this.extractedSamplesCountAtStartOfLoad = 0;
            SampleQueue[] sampleQueueArr = this.sampleQueues;
            int length = sampleQueueArr.length;
            while (i < length) {
                sampleQueueArr[i].reset();
                i++;
            }
            this.callback.onContinueLoadingRequested(this);
        }
    }

    private boolean suppressRead() {
        return this.notifyDiscontinuity || isPendingReset();
    }

    public void onLoadCompleted(ExtractingLoadable loadable, long elapsedRealtimeMs, long loadDurationMs) {
        if (this.durationUs == C.TIME_UNSET) {
            long largestQueuedTimestampUs = getLargestQueuedTimestampUs();
            this.durationUs = largestQueuedTimestampUs == Long.MIN_VALUE ? 0 : DEFAULT_LAST_SAMPLE_DURATION_US + largestQueuedTimestampUs;
            this.listener.onSourceInfoRefreshed(this.durationUs, this.seekMap.isSeekable());
        }
        this.eventDispatcher.loadCompleted(loadable.dataSpec, 1, -1, null, 0, null, loadable.seekTimeUs, this.durationUs, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded);
        copyLengthFromLoader(loadable);
        this.loadingFinished = true;
        this.callback.onContinueLoadingRequested(this);
    }

    public void onLoadCanceled(ExtractingLoadable loadable, long elapsedRealtimeMs, long loadDurationMs, boolean released) {
        this.eventDispatcher.loadCanceled(loadable.dataSpec, 1, -1, null, 0, null, loadable.seekTimeUs, this.durationUs, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded);
        if (!released) {
            copyLengthFromLoader(loadable);
            for (SampleQueue sampleQueue : this.sampleQueues) {
                sampleQueue.reset();
            }
            if (this.enabledTrackCount > 0) {
                this.callback.onContinueLoadingRequested(this);
            }
        }
    }

    public int onLoadError(ExtractingLoadable loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error) {
        boolean isErrorFatal = isLoadableExceptionFatal(error);
        this.eventDispatcher.loadError(loadable.dataSpec, 1, -1, null, 0, null, loadable.seekTimeUs, this.durationUs, elapsedRealtimeMs, loadDurationMs, loadable.bytesLoaded, error, isErrorFatal);
        copyLengthFromLoader(loadable);
        if (isErrorFatal) {
            return 3;
        }
        int extractedSamplesCount = getExtractedSamplesCount();
        boolean madeProgress = extractedSamplesCount > this.extractedSamplesCountAtStartOfLoad;
        if (configureRetry(loadable, extractedSamplesCount)) {
            return madeProgress ? 1 : 0;
        } else {
            return 2;
        }
    }

    public TrackOutput track(int id, int type) {
        int trackCount = this.sampleQueues.length;
        for (int i = 0; i < trackCount; i++) {
            if (this.sampleQueueTrackIds[i] == id) {
                return this.sampleQueues[i];
            }
        }
        TrackOutput trackOutput = new SampleQueue(this.allocator);
        trackOutput.setUpstreamFormatChangeListener(this);
        this.sampleQueueTrackIds = Arrays.copyOf(this.sampleQueueTrackIds, trackCount + 1);
        this.sampleQueueTrackIds[trackCount] = id;
        this.sampleQueues = (SampleQueue[]) Arrays.copyOf(this.sampleQueues, trackCount + 1);
        this.sampleQueues[trackCount] = trackOutput;
        return trackOutput;
    }

    public void endTracks() {
        this.sampleQueuesBuilt = true;
        this.handler.post(this.maybeFinishPrepareRunnable);
    }

    public void seekMap(SeekMap seekMap) {
        this.seekMap = seekMap;
        this.handler.post(this.maybeFinishPrepareRunnable);
    }

    public void onUpstreamFormatChanged(Format format) {
        this.handler.post(this.maybeFinishPrepareRunnable);
    }

    private void maybeFinishPrepare() {
        if (!this.released && !this.prepared && this.seekMap != null && this.sampleQueuesBuilt) {
            SampleQueue[] sampleQueueArr = this.sampleQueues;
            int length = sampleQueueArr.length;
            int i = 0;
            while (i < length) {
                if (sampleQueueArr[i].getUpstreamFormat() != null) {
                    i++;
                } else {
                    return;
                }
            }
            this.loadCondition.close();
            int trackCount = this.sampleQueues.length;
            TrackGroup[] trackArray = new TrackGroup[trackCount];
            this.trackIsAudioVideoFlags = new boolean[trackCount];
            this.trackEnabledStates = new boolean[trackCount];
            this.trackFormatNotificationSent = new boolean[trackCount];
            this.durationUs = this.seekMap.getDurationUs();
            for (int i2 = 0; i2 < trackCount; i2++) {
                boolean isAudioVideo;
                trackArray[i2] = new TrackGroup(this.sampleQueues[i2].getUpstreamFormat());
                String mimeType = trackFormat.sampleMimeType;
                if (MimeTypes.isVideo(mimeType) || MimeTypes.isAudio(mimeType)) {
                    isAudioVideo = true;
                } else {
                    isAudioVideo = false;
                }
                this.trackIsAudioVideoFlags[i2] = isAudioVideo;
                this.haveAudioVideoTracks |= isAudioVideo;
            }
            this.tracks = new TrackGroupArray(trackArray);
            if (this.minLoadableRetryCount == -1 && this.length == -1 && this.seekMap.getDurationUs() == C.TIME_UNSET) {
                this.actualMinLoadableRetryCount = 6;
            }
            this.prepared = true;
            this.listener.onSourceInfoRefreshed(this.durationUs, this.seekMap.isSeekable());
            this.callback.onPrepared(this);
        }
    }

    private void copyLengthFromLoader(ExtractingLoadable loadable) {
        if (this.length == -1) {
            this.length = loadable.length;
        }
    }

    private void startLoading() {
        ExtractingLoadable loadable = new ExtractingLoadable(this.uri, this.dataSource, this.extractorHolder, this.loadCondition);
        if (this.prepared) {
            Assertions.checkState(isPendingReset());
            if (this.durationUs == C.TIME_UNSET || this.pendingResetPositionUs < this.durationUs) {
                loadable.setLoadPosition(this.seekMap.getSeekPoints(this.pendingResetPositionUs).first.position, this.pendingResetPositionUs);
                this.pendingResetPositionUs = C.TIME_UNSET;
            } else {
                this.loadingFinished = true;
                this.pendingResetPositionUs = C.TIME_UNSET;
                return;
            }
        }
        this.extractedSamplesCountAtStartOfLoad = getExtractedSamplesCount();
        this.eventDispatcher.loadStarted(loadable.dataSpec, 1, -1, null, 0, null, loadable.seekTimeUs, this.durationUs, this.loader.startLoading(loadable, this, this.actualMinLoadableRetryCount));
    }

    private boolean configureRetry(ExtractingLoadable loadable, int currentExtractedSampleCount) {
        int i = 0;
        if (this.length != -1 || (this.seekMap != null && this.seekMap.getDurationUs() != C.TIME_UNSET)) {
            this.extractedSamplesCountAtStartOfLoad = currentExtractedSampleCount;
            return true;
        } else if (!this.prepared || suppressRead()) {
            this.notifyDiscontinuity = this.prepared;
            this.lastSeekPositionUs = 0;
            this.extractedSamplesCountAtStartOfLoad = 0;
            SampleQueue[] sampleQueueArr = this.sampleQueues;
            int length = sampleQueueArr.length;
            while (i < length) {
                sampleQueueArr[i].reset();
                i++;
            }
            loadable.setLoadPosition(0, 0);
            return true;
        } else {
            this.pendingDeferredRetry = true;
            return false;
        }
    }

    private boolean seekInsideBufferUs(long positionUs) {
        int trackCount = this.sampleQueues.length;
        int i = 0;
        while (i < trackCount) {
            boolean seekInsideQueue;
            SampleQueue sampleQueue = this.sampleQueues[i];
            sampleQueue.rewind();
            if (sampleQueue.advanceTo(positionUs, true, false) != -1) {
                seekInsideQueue = true;
            } else {
                seekInsideQueue = false;
            }
            if (!seekInsideQueue && (this.trackIsAudioVideoFlags[i] || !this.haveAudioVideoTracks)) {
                return false;
            }
            i++;
        }
        return true;
    }

    private int getExtractedSamplesCount() {
        int extractedSamplesCount = 0;
        for (SampleQueue sampleQueue : this.sampleQueues) {
            extractedSamplesCount += sampleQueue.getWriteIndex();
        }
        return extractedSamplesCount;
    }

    private long getLargestQueuedTimestampUs() {
        long largestQueuedTimestampUs = Long.MIN_VALUE;
        for (SampleQueue sampleQueue : this.sampleQueues) {
            largestQueuedTimestampUs = Math.max(largestQueuedTimestampUs, sampleQueue.getLargestQueuedTimestampUs());
        }
        return largestQueuedTimestampUs;
    }

    private boolean isPendingReset() {
        return this.pendingResetPositionUs != C.TIME_UNSET;
    }

    private static boolean isLoadableExceptionFatal(IOException e) {
        return e instanceof UnrecognizedInputFormatException;
    }
}
