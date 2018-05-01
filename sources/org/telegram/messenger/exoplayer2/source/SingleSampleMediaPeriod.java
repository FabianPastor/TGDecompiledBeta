package org.telegram.messenger.exoplayer2.source;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.C0542C;
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

        public int readData(FormatHolder formatHolder, DecoderInputBuffer decoderInputBuffer, boolean z) {
            if (this.streamState == 2) {
                decoderInputBuffer.addFlag(4);
                return -4;
            }
            if (!z) {
                if (this.streamState) {
                    if (SingleSampleMediaPeriod.this.loadingFinished == null) {
                        return -3;
                    }
                    if (SingleSampleMediaPeriod.this.loadingSucceeded != null) {
                        decoderInputBuffer.timeUs = 0;
                        decoderInputBuffer.addFlag(1);
                        decoderInputBuffer.ensureSpaceForWrite(SingleSampleMediaPeriod.this.sampleSize);
                        decoderInputBuffer.data.put(SingleSampleMediaPeriod.this.sampleData, false, SingleSampleMediaPeriod.this.sampleSize);
                        sendFormat();
                    } else {
                        decoderInputBuffer.addFlag(4);
                    }
                    this.streamState = 2;
                    return -4;
                }
            }
            formatHolder.format = SingleSampleMediaPeriod.this.format;
            this.streamState = 1;
            return -5;
        }

        public int skipData(long j) {
            if (j <= 0 || this.streamState == 2) {
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

        public void cancelLoad() {
        }

        public boolean isLoadCanceled() {
            return false;
        }

        public SourceLoadable(DataSpec dataSpec, DataSource dataSource) {
            this.dataSpec = dataSpec;
            this.dataSource = dataSource;
        }

        public void load() throws IOException, InterruptedException {
            int i = 0;
            this.sampleSize = 0;
            try {
                this.dataSource.open(this.dataSpec);
                while (i != -1) {
                    this.sampleSize += i;
                    if (this.sampleData == null) {
                        this.sampleData = new byte[1024];
                    } else if (this.sampleSize == this.sampleData.length) {
                        this.sampleData = Arrays.copyOf(this.sampleData, this.sampleData.length * 2);
                    }
                    i = this.dataSource.read(this.sampleData, this.sampleSize, this.sampleData.length - this.sampleSize);
                }
            } finally {
                Util.closeQuietly(this.dataSource);
            }
        }
    }

    public void discardBuffer(long j, boolean z) {
    }

    public long getAdjustedSeekPositionUs(long j, SeekParameters seekParameters) {
        return j;
    }

    public void maybeThrowPrepareError() throws IOException {
    }

    public long readDiscontinuity() {
        return C0542C.TIME_UNSET;
    }

    public void reevaluateBuffer(long j) {
    }

    public SingleSampleMediaPeriod(DataSpec dataSpec, Factory factory, Format format, long j, int i, EventDispatcher eventDispatcher, boolean z) {
        this.dataSpec = dataSpec;
        this.dataSourceFactory = factory;
        this.format = format;
        this.durationUs = j;
        this.minLoadableRetryCount = i;
        this.eventDispatcher = eventDispatcher;
        this.treatLoadErrorsAsEndOfStream = z;
        j = new TrackGroup[1];
        j[0] = new TrackGroup(new Format[]{format});
        this.tracks = new TrackGroupArray(j);
    }

    public void release() {
        this.loader.release();
    }

    public void prepare(MediaPeriod.Callback callback, long j) {
        callback.onPrepared(this);
    }

    public TrackGroupArray getTrackGroups() {
        return this.tracks;
    }

    public long selectTracks(TrackSelection[] trackSelectionArr, boolean[] zArr, SampleStream[] sampleStreamArr, boolean[] zArr2, long j) {
        int i = 0;
        while (i < trackSelectionArr.length) {
            if (sampleStreamArr[i] != null && (trackSelectionArr[i] == null || !zArr[i])) {
                this.sampleStreams.remove(sampleStreamArr[i]);
                sampleStreamArr[i] = null;
            }
            if (sampleStreamArr[i] == null && trackSelectionArr[i] != null) {
                SampleStreamImpl sampleStreamImpl = new SampleStreamImpl();
                this.sampleStreams.add(sampleStreamImpl);
                sampleStreamArr[i] = sampleStreamImpl;
                zArr2[i] = true;
            }
            i++;
        }
        return j;
    }

    public boolean continueLoading(long j) {
        if (!this.loadingFinished) {
            if (!r0.loader.isLoading()) {
                r0.eventDispatcher.loadStarted(r0.dataSpec, 1, -1, r0.format, 0, null, 0, r0.durationUs, r0.loader.startLoading(new SourceLoadable(r0.dataSpec, r0.dataSourceFactory.createDataSource()), r0, r0.minLoadableRetryCount));
                return true;
            }
        }
        return false;
    }

    public long getNextLoadPositionUs() {
        if (!this.loadingFinished) {
            if (!this.loader.isLoading()) {
                return 0;
            }
        }
        return Long.MIN_VALUE;
    }

    public long getBufferedPositionUs() {
        return this.loadingFinished ? Long.MIN_VALUE : 0;
    }

    public long seekToUs(long j) {
        for (int i = 0; i < this.sampleStreams.size(); i++) {
            ((SampleStreamImpl) this.sampleStreams.get(i)).reset();
        }
        return j;
    }

    public void onLoadCompleted(SourceLoadable sourceLoadable, long j, long j2) {
        this.eventDispatcher.loadCompleted(sourceLoadable.dataSpec, 1, -1, this.format, 0, null, 0, this.durationUs, j, j2, (long) sourceLoadable.sampleSize);
        this.sampleSize = sourceLoadable.sampleSize;
        this.sampleData = sourceLoadable.sampleData;
        this.loadingFinished = true;
        this.loadingSucceeded = true;
    }

    public void onLoadCanceled(SourceLoadable sourceLoadable, long j, long j2, boolean z) {
        this.eventDispatcher.loadCanceled(sourceLoadable.dataSpec, 1, -1, null, 0, null, 0, this.durationUs, j, j2, (long) sourceLoadable.sampleSize);
    }

    public int onLoadError(SourceLoadable sourceLoadable, long j, long j2, IOException iOException) {
        this.errorCount++;
        boolean z = this.treatLoadErrorsAsEndOfStream && r0.errorCount >= r0.minLoadableRetryCount;
        r0.eventDispatcher.loadError(sourceLoadable.dataSpec, 1, -1, r0.format, 0, null, 0, r0.durationUs, j, j2, (long) sourceLoadable.sampleSize, iOException, z);
        if (!z) {
            return 0;
        }
        r0.loadingFinished = true;
        return 2;
    }
}
