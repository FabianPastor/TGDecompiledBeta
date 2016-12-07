package org.telegram.messenger.exoplayer2.source;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.upstream.Allocator;

public interface MediaSource {

    public interface Listener {
        void onSourceInfoRefreshed(Timeline timeline, Object obj);
    }

    MediaPeriod createPeriod(int i, Allocator allocator, long j);

    void maybeThrowSourceInfoRefreshError() throws IOException;

    void prepareSource(Listener listener);

    void releasePeriod(MediaPeriod mediaPeriod);

    void releaseSource();
}
