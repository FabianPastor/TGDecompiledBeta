package org.telegram.messenger.exoplayer;

import java.io.IOException;

public interface SampleSource {
    public static final int END_OF_STREAM = -1;
    public static final int FORMAT_READ = -4;
    public static final int NOTHING_READ = -2;
    public static final long NO_DISCONTINUITY = Long.MIN_VALUE;
    public static final int SAMPLE_READ = -3;

    public interface SampleSourceReader {
        boolean continueBuffering(int i, long j);

        void disable(int i);

        void enable(int i, long j);

        long getBufferedPositionUs();

        MediaFormat getFormat(int i);

        int getTrackCount();

        void maybeThrowError() throws IOException;

        boolean prepare(long j);

        int readData(int i, long j, MediaFormatHolder mediaFormatHolder, SampleHolder sampleHolder);

        long readDiscontinuity(int i);

        void release();

        void seekToUs(long j);
    }

    SampleSourceReader register();
}
