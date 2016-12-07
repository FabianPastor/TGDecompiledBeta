package org.telegram.messenger.exoplayer2.source;

import android.net.Uri;
import android.os.Handler;
import android.util.SparseArray;
import java.io.EOFException;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.FormatHolder;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.extractor.DefaultExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.DefaultTrackOutput;
import org.telegram.messenger.exoplayer2.extractor.DefaultTrackOutput.UpstreamFormatChangedListener;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.PositionHolder;
import org.telegram.messenger.exoplayer2.extractor.SeekMap;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.source.ExtractorMediaSource.EventListener;
import org.telegram.messenger.exoplayer2.source.ExtractorMediaSource.UnrecognizedInputFormatException;
import org.telegram.messenger.exoplayer2.source.MediaSource.Listener;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.Loader;
import org.telegram.messenger.exoplayer2.upstream.Loader.Callback;
import org.telegram.messenger.exoplayer2.upstream.Loader.Loadable;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.ConditionVariable;
import org.telegram.messenger.exoplayer2.util.MimeTypes;

final class ExtractorMediaPeriod implements MediaPeriod, ExtractorOutput, Callback<ExtractingLoadable>, UpstreamFormatChangedListener {
    private static final long DEFAULT_LAST_SAMPLE_DURATION_US = 10000;
    private final Allocator allocator;
    private MediaPeriod.Callback callback;
    private final DataSource dataSource;
    private long durationUs;
    private int enabledTrackCount;
    private final Handler eventHandler;
    private final EventListener eventListener;
    private int extractedSamplesCountAtStartOfLoad;
    private final ExtractorHolder extractorHolder;
    private final Handler handler;
    private boolean haveAudioVideoTracks;
    private long lastSeekPositionUs;
    private long length;
    private final ConditionVariable loadCondition;
    private final Loader loader = new Loader("Loader:ExtractorMediaPeriod");
    private boolean loadingFinished;
    private final Runnable maybeFinishPrepareRunnable;
    private final int minLoadableRetryCount;
    private boolean notifyReset;
    private final Runnable onContinueLoadingRequestedRunnable;
    private long pendingResetPositionUs;
    private boolean prepared;
    private boolean released;
    private final SparseArray<DefaultTrackOutput> sampleQueues;
    private SeekMap seekMap;
    private boolean seenFirstTrackSelection;
    private final Listener sourceListener;
    private boolean[] trackEnabledStates;
    private boolean[] trackIsAudioVideoFlags;
    private TrackGroupArray tracks;
    private boolean tracksBuilt;
    private final Uri uri;

    private static final class ExtractorHolder {
        private Extractor extractor;
        private final ExtractorOutput extractorOutput;
        private final Extractor[] extractors;

        public ExtractorHolder(Extractor[] extractors, ExtractorOutput extractorOutput) {
            this.extractors = extractors;
            this.extractorOutput = extractorOutput;
        }

        public Extractor selectExtractor(ExtractorInput input) throws IOException, InterruptedException {
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
                throw new UnrecognizedInputFormatException(this.extractors);
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

    final class ExtractingLoadable implements Loadable {
        private static final int CONTINUE_LOADING_CHECK_INTERVAL_BYTES = 1048576;
        private final DataSource dataSource;
        private final ExtractorHolder extractorHolder;
        private long length = -1;
        private volatile boolean loadCanceled;
        private final ConditionVariable loadCondition;
        private boolean pendingExtractorSeek = true;
        private final PositionHolder positionHolder = new PositionHolder();
        private final Uri uri;

        public ExtractingLoadable(Uri uri, DataSource dataSource, ExtractorHolder extractorHolder, ConditionVariable loadCondition) {
            this.uri = (Uri) Assertions.checkNotNull(uri);
            this.dataSource = (DataSource) Assertions.checkNotNull(dataSource);
            this.extractorHolder = (ExtractorHolder) Assertions.checkNotNull(extractorHolder);
            this.loadCondition = loadCondition;
        }

        public void setLoadPosition(long position) {
            this.positionHolder.position = position;
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
                    this.length = this.dataSource.open(new DataSpec(this.uri, position, -1, null));
                    if (this.length != -1) {
                        this.length += position;
                    }
                    input = new DefaultExtractorInput(this.dataSource, position, this.length);
                    try {
                        Extractor extractor = this.extractorHolder.selectExtractor(input);
                        if (this.pendingExtractorSeek) {
                            extractor.seek(position);
                            this.pendingExtractorSeek = false;
                        }
                        while (result == 0 && !this.loadCanceled) {
                            this.loadCondition.block();
                            result = extractor.read(input, this.positionHolder);
                            if (input.getPosition() > 1048576 + position) {
                                position = input.getPosition();
                                this.loadCondition.close();
                                ExtractorMediaPeriod.this.handler.post(ExtractorMediaPeriod.this.onContinueLoadingRequestedRunnable);
                            }
                        }
                        if (result == 1) {
                            result = 0;
                        } else if (input != null) {
                            this.positionHolder.position = input.getPosition();
                        }
                        this.dataSource.close();
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
                }
            }
            this.dataSource.close();
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

        public int readData(FormatHolder formatHolder, DecoderInputBuffer buffer) {
            return ExtractorMediaPeriod.this.readData(this.track, formatHolder, buffer);
        }

        public void skipToKeyframeBefore(long timeUs) {
            ((DefaultTrackOutput) ExtractorMediaPeriod.this.sampleQueues.valueAt(this.track)).skipToKeyframeBefore(timeUs);
        }
    }

    public ExtractorMediaPeriod(Uri uri, DataSource dataSource, Extractor[] extractors, int minLoadableRetryCount, Handler eventHandler, EventListener eventListener, Listener sourceListener, Allocator allocator) {
        this.uri = uri;
        this.dataSource = dataSource;
        this.minLoadableRetryCount = minLoadableRetryCount;
        this.eventHandler = eventHandler;
        this.eventListener = eventListener;
        this.sourceListener = sourceListener;
        this.allocator = allocator;
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
        this.pendingResetPositionUs = C.TIME_UNSET;
        this.sampleQueues = new SparseArray();
        this.length = -1;
    }

    public void release() {
        final ExtractorHolder extractorHolder = this.extractorHolder;
        this.loader.release(new Runnable() {
            public void run() {
                extractorHolder.release();
                int trackCount = ExtractorMediaPeriod.this.sampleQueues.size();
                for (int i = 0; i < trackCount; i++) {
                    ((DefaultTrackOutput) ExtractorMediaPeriod.this.sampleQueues.valueAt(i)).disable();
                }
            }
        });
        this.handler.removeCallbacksAndMessages(null);
        this.released = true;
    }

    public void prepare(MediaPeriod.Callback callback) {
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

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long selectTracks(TrackSelection[] selections, boolean[] mayRetainStreamFlags, SampleStream[] streams, boolean[] streamResetFlags, long positionUs) {
        Assertions.checkState(this.prepared);
        int i = 0;
        while (i < selections.length) {
            if (streams[i] != null && (selections[i] == null || !mayRetainStreamFlags[i])) {
                int track = ((SampleStreamImpl) streams[i]).track;
                Assertions.checkState(this.trackEnabledStates[track]);
                this.enabledTrackCount--;
                this.trackEnabledStates[track] = false;
                ((DefaultTrackOutput) this.sampleQueues.valueAt(track)).disable();
                streams[i] = null;
            }
            i++;
        }
        boolean selectedNewTracks = false;
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
                selectedNewTracks = true;
            }
            i++;
        }
        if (!this.seenFirstTrackSelection) {
            int trackCount = this.sampleQueues.size();
            for (i = 0; i < trackCount; i++) {
                if (!this.trackEnabledStates[i]) {
                    ((DefaultTrackOutput) this.sampleQueues.valueAt(i)).disable();
                }
            }
        }
        if (this.enabledTrackCount == 0) {
            this.notifyReset = false;
            if (this.loader.isLoading()) {
                this.loader.cancelLoading();
            }
        } else {
            if (!this.seenFirstTrackSelection) {
                if (positionUs != 0) {
                }
            }
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

    public boolean continueLoading(long playbackPositionUs) {
        if (this.loadingFinished || (this.prepared && this.enabledTrackCount == 0)) {
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
        return getBufferedPositionUs();
    }

    public long readDiscontinuity() {
        if (!this.notifyReset) {
            return C.TIME_UNSET;
        }
        this.notifyReset = false;
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
            int trackCount = this.sampleQueues.size();
            for (int i = 0; i < trackCount; i++) {
                if (this.trackIsAudioVideoFlags[i]) {
                    largestQueuedTimestampUs = Math.min(largestQueuedTimestampUs, ((DefaultTrackOutput) this.sampleQueues.valueAt(i)).getLargestQueuedTimestampUs());
                }
            }
        } else {
            largestQueuedTimestampUs = getLargestQueuedTimestampUs();
        }
        return largestQueuedTimestampUs == Long.MIN_VALUE ? this.lastSeekPositionUs : largestQueuedTimestampUs;
    }

    public long seekToUs(long positionUs) {
        boolean seekInsideBuffer;
        if (!this.seekMap.isSeekable()) {
            positionUs = 0;
        }
        this.lastSeekPositionUs = positionUs;
        int trackCount = this.sampleQueues.size();
        if (isPendingReset()) {
            seekInsideBuffer = false;
        } else {
            seekInsideBuffer = true;
        }
        int i = 0;
        while (seekInsideBuffer && i < trackCount) {
            if (this.trackEnabledStates[i]) {
                seekInsideBuffer = ((DefaultTrackOutput) this.sampleQueues.valueAt(i)).skipToKeyframeBefore(positionUs);
            }
            i++;
        }
        if (!seekInsideBuffer) {
            this.pendingResetPositionUs = positionUs;
            this.loadingFinished = false;
            if (this.loader.isLoading()) {
                this.loader.cancelLoading();
            } else {
                for (i = 0; i < trackCount; i++) {
                    ((DefaultTrackOutput) this.sampleQueues.valueAt(i)).reset(this.trackEnabledStates[i]);
                }
            }
        }
        this.notifyReset = false;
        return positionUs;
    }

    boolean isReady(int track) {
        return this.loadingFinished || !(isPendingReset() || ((DefaultTrackOutput) this.sampleQueues.valueAt(track)).isEmpty());
    }

    void maybeThrowError() throws IOException {
        this.loader.maybeThrowError();
    }

    int readData(int track, FormatHolder formatHolder, DecoderInputBuffer buffer) {
        if (this.notifyReset || isPendingReset()) {
            return -3;
        }
        return ((DefaultTrackOutput) this.sampleQueues.valueAt(track)).readData(formatHolder, buffer, this.loadingFinished, this.lastSeekPositionUs);
    }

    public void onLoadCompleted(ExtractingLoadable loadable, long elapsedRealtimeMs, long loadDurationMs) {
        copyLengthFromLoader(loadable);
        this.loadingFinished = true;
        if (this.durationUs == C.TIME_UNSET) {
            long largestQueuedTimestampUs = getLargestQueuedTimestampUs();
            this.durationUs = largestQueuedTimestampUs == Long.MIN_VALUE ? 0 : DEFAULT_LAST_SAMPLE_DURATION_US + largestQueuedTimestampUs;
            this.sourceListener.onSourceInfoRefreshed(new SinglePeriodTimeline(this.durationUs, this.seekMap.isSeekable()), null);
        }
    }

    public void onLoadCanceled(ExtractingLoadable loadable, long elapsedRealtimeMs, long loadDurationMs, boolean released) {
        copyLengthFromLoader(loadable);
        if (!released && this.enabledTrackCount > 0) {
            int trackCount = this.sampleQueues.size();
            for (int i = 0; i < trackCount; i++) {
                ((DefaultTrackOutput) this.sampleQueues.valueAt(i)).reset(this.trackEnabledStates[i]);
            }
            this.callback.onContinueLoadingRequested(this);
        }
    }

    public int onLoadError(ExtractingLoadable loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error) {
        copyLengthFromLoader(loadable);
        notifyLoadError(error);
        if (isLoadableExceptionFatal(error)) {
            return 3;
        }
        boolean madeProgress;
        if (getExtractedSamplesCount() > this.extractedSamplesCountAtStartOfLoad) {
            madeProgress = true;
        } else {
            madeProgress = false;
        }
        configureRetry(loadable);
        this.extractedSamplesCountAtStartOfLoad = getExtractedSamplesCount();
        if (madeProgress) {
            return 1;
        }
        return 0;
    }

    public TrackOutput track(int id) {
        DefaultTrackOutput trackOutput = (DefaultTrackOutput) this.sampleQueues.get(id);
        if (trackOutput != null) {
            return trackOutput;
        }
        trackOutput = new DefaultTrackOutput(this.allocator);
        trackOutput.setUpstreamFormatChangeListener(this);
        this.sampleQueues.put(id, trackOutput);
        return trackOutput;
    }

    public void endTracks() {
        this.tracksBuilt = true;
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
        if (!this.released && !this.prepared && this.seekMap != null && this.tracksBuilt) {
            int trackCount = this.sampleQueues.size();
            int i = 0;
            while (i < trackCount) {
                if (((DefaultTrackOutput) this.sampleQueues.valueAt(i)).getUpstreamFormat() != null) {
                    i++;
                } else {
                    return;
                }
            }
            this.loadCondition.close();
            TrackGroup[] trackArray = new TrackGroup[trackCount];
            this.trackIsAudioVideoFlags = new boolean[trackCount];
            this.trackEnabledStates = new boolean[trackCount];
            this.durationUs = this.seekMap.getDurationUs();
            for (i = 0; i < trackCount; i++) {
                boolean isAudioVideo;
                trackArray[i] = new TrackGroup(((DefaultTrackOutput) this.sampleQueues.valueAt(i)).getUpstreamFormat());
                String mimeType = trackFormat.sampleMimeType;
                if (MimeTypes.isVideo(mimeType) || MimeTypes.isAudio(mimeType)) {
                    isAudioVideo = true;
                } else {
                    isAudioVideo = false;
                }
                this.trackIsAudioVideoFlags[i] = isAudioVideo;
                this.haveAudioVideoTracks |= isAudioVideo;
            }
            this.tracks = new TrackGroupArray(trackArray);
            this.prepared = true;
            this.sourceListener.onSourceInfoRefreshed(new SinglePeriodTimeline(this.durationUs, this.seekMap.isSeekable()), null);
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
                loadable.setLoadPosition(this.seekMap.getPosition(this.pendingResetPositionUs));
                this.pendingResetPositionUs = C.TIME_UNSET;
            } else {
                this.loadingFinished = true;
                this.pendingResetPositionUs = C.TIME_UNSET;
                return;
            }
        }
        this.extractedSamplesCountAtStartOfLoad = getExtractedSamplesCount();
        int minRetryCount = this.minLoadableRetryCount;
        if (minRetryCount == -1) {
            minRetryCount = (this.prepared && this.length == -1 && (this.seekMap == null || this.seekMap.getDurationUs() == C.TIME_UNSET)) ? 6 : 3;
        }
        this.loader.startLoading(loadable, this, minRetryCount);
    }

    private void configureRetry(ExtractingLoadable loadable) {
        if (this.length != -1) {
            return;
        }
        if (this.seekMap == null || this.seekMap.getDurationUs() == C.TIME_UNSET) {
            this.lastSeekPositionUs = 0;
            this.notifyReset = this.prepared;
            int trackCount = this.sampleQueues.size();
            for (int i = 0; i < trackCount; i++) {
                DefaultTrackOutput defaultTrackOutput = (DefaultTrackOutput) this.sampleQueues.valueAt(i);
                boolean z = !this.prepared || this.trackEnabledStates[i];
                defaultTrackOutput.reset(z);
            }
            loadable.setLoadPosition(0);
        }
    }

    private int getExtractedSamplesCount() {
        int extractedSamplesCount = 0;
        for (int i = 0; i < this.sampleQueues.size(); i++) {
            extractedSamplesCount += ((DefaultTrackOutput) this.sampleQueues.valueAt(i)).getWriteIndex();
        }
        return extractedSamplesCount;
    }

    private long getLargestQueuedTimestampUs() {
        long largestQueuedTimestampUs = Long.MIN_VALUE;
        int trackCount = this.sampleQueues.size();
        for (int i = 0; i < trackCount; i++) {
            largestQueuedTimestampUs = Math.max(largestQueuedTimestampUs, ((DefaultTrackOutput) this.sampleQueues.valueAt(i)).getLargestQueuedTimestampUs());
        }
        return largestQueuedTimestampUs;
    }

    private boolean isPendingReset() {
        return this.pendingResetPositionUs != C.TIME_UNSET;
    }

    private boolean isLoadableExceptionFatal(IOException e) {
        return e instanceof UnrecognizedInputFormatException;
    }

    private void notifyLoadError(final IOException error) {
        if (this.eventHandler != null && this.eventListener != null) {
            this.eventHandler.post(new Runnable() {
                public void run() {
                    ExtractorMediaPeriod.this.eventListener.onLoadError(error);
                }
            });
        }
    }
}
