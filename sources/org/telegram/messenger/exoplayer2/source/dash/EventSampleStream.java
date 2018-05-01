package org.telegram.messenger.exoplayer2.source.dash;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.FormatHolder;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.metadata.emsg.EventMessageEncoder;
import org.telegram.messenger.exoplayer2.source.SampleStream;
import org.telegram.messenger.exoplayer2.source.dash.manifest.EventStream;
import org.telegram.messenger.exoplayer2.util.Util;

final class EventSampleStream implements SampleStream {
    private int currentIndex;
    private final EventMessageEncoder eventMessageEncoder = new EventMessageEncoder();
    private EventStream eventStream;
    private boolean eventStreamUpdatable;
    private long[] eventTimesUs;
    private boolean isFormatSentDownstream;
    private long pendingSeekPositionUs = C0542C.TIME_UNSET;
    private final Format upstreamFormat;

    public boolean isReady() {
        return true;
    }

    public void maybeThrowError() throws IOException {
    }

    EventSampleStream(EventStream eventStream, Format format, boolean z) {
        this.upstreamFormat = format;
        updateEventStream(eventStream, z);
    }

    void updateEventStream(EventStream eventStream, boolean z) {
        long j = this.currentIndex == 0 ? C0542C.TIME_UNSET : this.eventTimesUs[this.currentIndex - 1];
        this.eventStreamUpdatable = z;
        this.eventStream = eventStream;
        this.eventTimesUs = eventStream.presentationTimesUs;
        if (this.pendingSeekPositionUs != -922337203NUM) {
            seekToUs(this.pendingSeekPositionUs);
        } else if (j != C0542C.TIME_UNSET) {
            this.currentIndex = Util.binarySearchCeil(this.eventTimesUs, j, false, false);
        }
    }

    String eventStreamId() {
        return this.eventStream.id();
    }

    public int readData(FormatHolder formatHolder, DecoderInputBuffer decoderInputBuffer, boolean z) {
        if (!z) {
            if (this.isFormatSentDownstream) {
                if (this.currentIndex != this.eventTimesUs.length) {
                    formatHolder = this.currentIndex;
                    this.currentIndex = formatHolder + 1;
                    z = this.eventMessageEncoder.encode(this.eventStream.events[formatHolder], this.eventStream.timescale);
                    if (!z) {
                        return -3;
                    }
                    decoderInputBuffer.ensureSpaceForWrite(z.length);
                    decoderInputBuffer.setFlags(1);
                    decoderInputBuffer.data.put(z);
                    decoderInputBuffer.timeUs = this.eventTimesUs[formatHolder];
                    return -4;
                } else if (this.eventStreamUpdatable != null) {
                    return -3;
                } else {
                    decoderInputBuffer.setFlags(4);
                    return -4;
                }
            }
        }
        formatHolder.format = this.upstreamFormat;
        this.isFormatSentDownstream = true;
        return -5;
    }

    public int skipData(long j) {
        j = Math.max(this.currentIndex, Util.binarySearchCeil(this.eventTimesUs, j, true, false));
        int i = j - this.currentIndex;
        this.currentIndex = j;
        return i;
    }

    public void seekToUs(long j) {
        boolean z = false;
        this.currentIndex = Util.binarySearchCeil(this.eventTimesUs, j, true, false);
        if (this.eventStreamUpdatable && this.currentIndex == this.eventTimesUs.length) {
            z = true;
        }
        if (!z) {
            j = C0542C.TIME_UNSET;
        }
        this.pendingSeekPositionUs = j;
    }
}
