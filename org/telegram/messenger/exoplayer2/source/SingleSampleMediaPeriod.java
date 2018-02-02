package org.telegram.messenger.exoplayer2.source;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.FormatHolder;
import org.telegram.messenger.exoplayer2.SeekParameters;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSource.Factory;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.Loader;
import org.telegram.messenger.exoplayer2.upstream.Loader.Callback;
import org.telegram.messenger.exoplayer2.upstream.Loader.Loadable;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.Util;

final class SingleSampleMediaPeriod implements MediaPeriod, Callback<SourceLoadable> {
    private static final int INITIAL_SAMPLE_SIZE = 1024;
    private final Factory dataSourceFactory;
    private final DataSpec dataSpec;
    private final long durationUs;
    private int errorCount;
    private final EventDispatcher eventDispatcher;
    final Format format;
    final Loader loader = new Loader("Loader:SingleSampleMediaPeriod");
    boolean loadingFinished;
    boolean loadingSucceeded;
    private final int minLoadableRetryCount;
    byte[] sampleData;
    int sampleSize;
    private final ArrayList<SampleStreamImpl> sampleStreams = new ArrayList();
    private final TrackGroupArray tracks;
    final boolean treatLoadErrorsAsEndOfStream;

    private final class SampleStreamImpl implements SampleStream {
        private static final int STREAM_STATE_END_OF_STREAM = 2;
        private static final int STREAM_STATE_SEND_FORMAT = 0;
        private static final int STREAM_STATE_SEND_SAMPLE = 1;
        private boolean formatSent;
        private int streamState;

        private SampleStreamImpl() {
        }

        public void reset() {
            if (this.streamState == 2) {
                this.streamState = 1;
            }
        }

        public boolean isReady() {
            return SingleSampleMediaPeriod.this.loadingFinished;
        }

        public void maybeThrowError() throws IOException {
            if (!SingleSampleMediaPeriod.this.treatLoadErrorsAsEndOfStream) {
                SingleSampleMediaPeriod.this.loader.maybeThrowError();
            }
        }

        public int readData(FormatHolder formatHolder, DecoderInputBuffer buffer, boolean requireFormat) {
            if (this.streamState == 2) {
                buffer.addFlag(4);
                return -4;
            } else if (requireFormat || this.streamState == 0) {
                formatHolder.format = SingleSampleMediaPeriod.this.format;
                this.streamState = 1;
                return -5;
            } else if (!SingleSampleMediaPeriod.this.loadingFinished) {
                return -3;
            } else {
                if (SingleSampleMediaPeriod.this.loadingSucceeded) {
                    buffer.timeUs = 0;
                    buffer.addFlag(1);
                    buffer.ensureSpaceForWrite(SingleSampleMediaPeriod.this.sampleSize);
                    buffer.data.put(SingleSampleMediaPeriod.this.sampleData, 0, SingleSampleMediaPeriod.this.sampleSize);
                    sendFormat();
                } else {
                    buffer.addFlag(4);
                }
                this.streamState = 2;
                return -4;
            }
        }

        public int skipData(long positionUs) {
            if (positionUs <= 0 || this.streamState == 2) {
                return 0;
            }
            this.streamState = 2;
            sendFormat();
            return 1;
        }

        private void sendFormat() {
            if (!this.formatSent) {
                SingleSampleMediaPeriod.this.eventDispatcher.downstreamFormatChanged(MimeTypes.getTrackType(SingleSampleMediaPeriod.this.format.sampleMimeType), SingleSampleMediaPeriod.this.format, 0, null, 0);
                this.formatSent = true;
            }
        }
    }

    static final class SourceLoadable implements Loadable {
        private final DataSource dataSource;
        public final DataSpec dataSpec;
        private byte[] sampleData;
        private int sampleSize;

        public SourceLoadable(DataSpec dataSpec, DataSource dataSource) {
            this.dataSpec = dataSpec;
            this.dataSource = dataSource;
        }

        public void cancelLoad() {
        }

        public boolean isLoadCanceled() {
            return false;
        }

        public void load() throws IOException, InterruptedException {
            this.sampleSize = 0;
            try {
                this.dataSource.open(this.dataSpec);
                int result = 0;
                while (result != -1) {
                    this.sampleSize += result;
                    if (this.sampleData == null) {
                        this.sampleData = new byte[1024];
                    } else if (this.sampleSize == this.sampleData.length) {
                        this.sampleData = Arrays.copyOf(this.sampleData, this.sampleData.length * 2);
                    }
                    result = this.dataSource.read(this.sampleData, this.sampleSize, this.sampleData.length - this.sampleSize);
                }
            } finally {
                Util.closeQuietly(this.dataSource);
            }
        }
    }

    public SingleSampleMediaPeriod(DataSpec dataSpec, Factory dataSourceFactory, Format format, long durationUs, int minLoadableRetryCount, EventDispatcher eventDispatcher, boolean treatLoadErrorsAsEndOfStream) {
        this.dataSpec = dataSpec;
        this.dataSourceFactory = dataSourceFactory;
        this.format = format;
        this.durationUs = durationUs;
        this.minLoadableRetryCount = minLoadableRetryCount;
        this.eventDispatcher = eventDispatcher;
        this.treatLoadErrorsAsEndOfStream = treatLoadErrorsAsEndOfStream;
        TrackGroup[] trackGroupArr = new TrackGroup[1];
        trackGroupArr[0] = new TrackGroup(format);
        this.tracks = new TrackGroupArray(trackGroupArr);
    }

    public void release() {
        this.loader.release();
    }

    public void prepare(MediaPeriod.Callback callback, long positionUs) {
        callback.onPrepared(this);
    }

    public void maybeThrowPrepareError() throws IOException {
    }

    public TrackGroupArray getTrackGroups() {
        return this.tracks;
    }

    public long selectTracks(TrackSelection[] selections, boolean[] mayRetainStreamFlags, SampleStream[] streams, boolean[] streamResetFlags, long positionUs) {
        int i = 0;
        while (i < selections.length) {
            if (streams[i] != null && (selections[i] == null || !mayRetainStreamFlags[i])) {
                this.sampleStreams.remove(streams[i]);
                streams[i] = null;
            }
            if (streams[i] == null && selections[i] != null) {
                SampleStreamImpl stream = new SampleStreamImpl();
                this.sampleStreams.add(stream);
                streams[i] = stream;
                streamResetFlags[i] = true;
            }
            i++;
        }
        return positionUs;
    }

    public void discardBuffer(long positionUs, boolean toKeyframe) {
    }

    public void reevaluateBuffer(long positionUs) {
    }

    public boolean continueLoading(long positionUs) {
        if (this.loadingFinished || this.loader.isLoading()) {
            return false;
        }
        this.eventDispatcher.loadStarted(this.dataSpec, 1, -1, this.format, 0, null, 0, this.durationUs, this.loader.startLoading(new SourceLoadable(this.dataSpec, this.dataSourceFactory.createDataSource()), this, this.minLoadableRetryCount));
        return true;
    }

    public long readDiscontinuity() {
        return C.TIME_UNSET;
    }

    public long getNextLoadPositionUs() {
        return (this.loadingFinished || this.loader.isLoading()) ? Long.MIN_VALUE : 0;
    }

    public long getBufferedPositionUs() {
        return this.loadingFinished ? Long.MIN_VALUE : 0;
    }

    public long seekToUs(long positionUs) {
        for (int i = 0; i < this.sampleStreams.size(); i++) {
            ((SampleStreamImpl) this.sampleStreams.get(i)).reset();
        }
        return positionUs;
    }

    public long getAdjustedSeekPositionUs(long positionUs, SeekParameters seekParameters) {
        return positionUs;
    }

    public void onLoadCompleted(SourceLoadable loadable, long elapsedRealtimeMs, long loadDurationMs) {
        this.eventDispatcher.loadCompleted(loadable.dataSpec, 1, -1, this.format, 0, null, 0, this.durationUs, elapsedRealtimeMs, loadDurationMs, (long) loadable.sampleSize);
        this.sampleSize = loadable.sampleSize;
        this.sampleData = loadable.sampleData;
        this.loadingFinished = true;
        this.loadingSucceeded = true;
    }

    public void onLoadCanceled(SourceLoadable loadable, long elapsedRealtimeMs, long loadDurationMs, boolean released) {
        this.eventDispatcher.loadCanceled(loadable.dataSpec, 1, -1, null, 0, null, 0, this.durationUs, elapsedRealtimeMs, loadDurationMs, (long) loadable.sampleSize);
    }

    public int onLoadError(SourceLoadable loadable, long elapsedRealtimeMs, long loadDurationMs, IOException error) {
        this.errorCount++;
        boolean cancel = this.treatLoadErrorsAsEndOfStream && this.errorCount >= this.minLoadableRetryCount;
        this.eventDispatcher.loadError(loadable.dataSpec, 1, -1, this.format, 0, null, 0, this.durationUs, elapsedRealtimeMs, loadDurationMs, (long) loadable.sampleSize, error, cancel);
        if (!cancel) {
            return 0;
        }
        this.loadingFinished = true;
        return 2;
    }
}
