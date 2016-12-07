package org.telegram.messenger.exoplayer.chunk;

import java.io.IOException;

public interface BaseChunkSampleSourceEventListener {
    void onDownstreamFormatChanged(int i, Format format, int i2, long j);

    void onLoadCanceled(int i, long j);

    void onLoadCompleted(int i, long j, int i2, int i3, Format format, long j2, long j3, long j4, long j5);

    void onLoadError(int i, IOException iOException);

    void onLoadStarted(int i, long j, int i2, int i3, Format format, long j2, long j3);

    void onUpstreamDiscarded(int i, long j, long j2);
}
