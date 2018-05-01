package org.telegram.messenger.exoplayer2.source.hls;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.FormatHolder;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.source.SampleStream;

final class HlsSampleStream implements SampleStream {
    private int sampleQueueIndex = -1;
    private final HlsSampleStreamWrapper sampleStreamWrapper;
    private final int trackGroupIndex;

    public HlsSampleStream(HlsSampleStreamWrapper hlsSampleStreamWrapper, int i) {
        this.sampleStreamWrapper = hlsSampleStreamWrapper;
        this.trackGroupIndex = i;
    }

    public void unbindSampleQueue() {
        if (this.sampleQueueIndex != -1) {
            this.sampleStreamWrapper.unbindSampleQueue(this.trackGroupIndex);
            this.sampleQueueIndex = -1;
        }
    }

    public boolean isReady() {
        return ensureBoundSampleQueue() && this.sampleStreamWrapper.isReady(this.sampleQueueIndex);
    }

    public void maybeThrowError() throws IOException {
        if (ensureBoundSampleQueue() || !this.sampleStreamWrapper.isMappingFinished()) {
            this.sampleStreamWrapper.maybeThrowError();
            return;
        }
        throw new SampleQueueMappingException(this.sampleStreamWrapper.getTrackGroups().get(this.trackGroupIndex).getFormat(0).sampleMimeType);
    }

    public int readData(FormatHolder formatHolder, DecoderInputBuffer decoderInputBuffer, boolean z) {
        if (ensureBoundSampleQueue()) {
            return this.sampleStreamWrapper.readData(this.sampleQueueIndex, formatHolder, decoderInputBuffer, z);
        }
        return -3;
    }

    public int skipData(long j) {
        if (ensureBoundSampleQueue()) {
            return this.sampleStreamWrapper.skipData(this.sampleQueueIndex, j);
        }
        return 0;
    }

    private boolean ensureBoundSampleQueue() {
        boolean z = true;
        if (this.sampleQueueIndex != -1) {
            return true;
        }
        this.sampleQueueIndex = this.sampleStreamWrapper.bindSampleQueueToSampleStream(this.trackGroupIndex);
        if (this.sampleQueueIndex == -1) {
            z = false;
        }
        return z;
    }
}
