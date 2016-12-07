package org.telegram.messenger.exoplayer.hls;

import android.util.SparseArray;
import java.io.IOException;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.SampleHolder;
import org.telegram.messenger.exoplayer.chunk.Format;
import org.telegram.messenger.exoplayer.drm.DrmInitData;
import org.telegram.messenger.exoplayer.extractor.DefaultTrackOutput;
import org.telegram.messenger.exoplayer.extractor.Extractor;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer.extractor.SeekMap;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.upstream.Allocator;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.MimeTypes;

public final class HlsExtractorWrapper implements ExtractorOutput {
    private final int adaptiveMaxHeight;
    private final int adaptiveMaxWidth;
    private Allocator allocator;
    private final Extractor extractor;
    public final Format format;
    private boolean prepared;
    private MediaFormat[] sampleQueueFormats;
    private final SparseArray<DefaultTrackOutput> sampleQueues = new SparseArray();
    private final boolean shouldSpliceIn;
    private boolean spliceConfigured;
    public final long startTimeUs;
    private volatile boolean tracksBuilt;
    public final int trigger;

    public HlsExtractorWrapper(int trigger, Format format, long startTimeUs, Extractor extractor, boolean shouldSpliceIn, int adaptiveMaxWidth, int adaptiveMaxHeight) {
        this.trigger = trigger;
        this.format = format;
        this.startTimeUs = startTimeUs;
        this.extractor = extractor;
        this.shouldSpliceIn = shouldSpliceIn;
        this.adaptiveMaxWidth = adaptiveMaxWidth;
        this.adaptiveMaxHeight = adaptiveMaxHeight;
    }

    public void init(Allocator allocator) {
        this.allocator = allocator;
        this.extractor.init(this);
    }

    public boolean isPrepared() {
        if (!this.prepared && this.tracksBuilt) {
            int i;
            for (i = 0; i < this.sampleQueues.size(); i++) {
                if (!((DefaultTrackOutput) this.sampleQueues.valueAt(i)).hasFormat()) {
                    return false;
                }
            }
            this.prepared = true;
            this.sampleQueueFormats = new MediaFormat[this.sampleQueues.size()];
            for (i = 0; i < this.sampleQueueFormats.length; i++) {
                MediaFormat format = ((DefaultTrackOutput) this.sampleQueues.valueAt(i)).getFormat();
                if (MimeTypes.isVideo(format.mimeType) && !(this.adaptiveMaxWidth == -1 && this.adaptiveMaxHeight == -1)) {
                    format = format.copyWithMaxVideoDimensions(this.adaptiveMaxWidth, this.adaptiveMaxHeight);
                }
                this.sampleQueueFormats[i] = format;
            }
        }
        return this.prepared;
    }

    public void clear() {
        for (int i = 0; i < this.sampleQueues.size(); i++) {
            ((DefaultTrackOutput) this.sampleQueues.valueAt(i)).clear();
        }
    }

    public long getLargestParsedTimestampUs() {
        long largestParsedTimestampUs = Long.MIN_VALUE;
        for (int i = 0; i < this.sampleQueues.size(); i++) {
            largestParsedTimestampUs = Math.max(largestParsedTimestampUs, ((DefaultTrackOutput) this.sampleQueues.valueAt(i)).getLargestParsedTimestampUs());
        }
        return largestParsedTimestampUs;
    }

    public final void configureSpliceTo(HlsExtractorWrapper nextExtractor) {
        Assertions.checkState(isPrepared());
        if (!this.spliceConfigured && nextExtractor.shouldSpliceIn && nextExtractor.isPrepared()) {
            boolean spliceConfigured = true;
            for (int i = 0; i < getTrackCount(); i++) {
                spliceConfigured &= ((DefaultTrackOutput) this.sampleQueues.valueAt(i)).configureSpliceTo((DefaultTrackOutput) nextExtractor.sampleQueues.valueAt(i));
            }
            this.spliceConfigured = spliceConfigured;
        }
    }

    public int getTrackCount() {
        Assertions.checkState(isPrepared());
        return this.sampleQueues.size();
    }

    public MediaFormat getMediaFormat(int track) {
        Assertions.checkState(isPrepared());
        return this.sampleQueueFormats[track];
    }

    public boolean getSample(int track, SampleHolder holder) {
        Assertions.checkState(isPrepared());
        return ((DefaultTrackOutput) this.sampleQueues.valueAt(track)).getSample(holder);
    }

    public void discardUntil(int track, long timeUs) {
        Assertions.checkState(isPrepared());
        ((DefaultTrackOutput) this.sampleQueues.valueAt(track)).discardUntil(timeUs);
    }

    public boolean hasSamples(int track) {
        Assertions.checkState(isPrepared());
        return !((DefaultTrackOutput) this.sampleQueues.valueAt(track)).isEmpty();
    }

    public int read(ExtractorInput input) throws IOException, InterruptedException {
        boolean z = true;
        int result = this.extractor.read(input, null);
        if (result == 1) {
            z = false;
        }
        Assertions.checkState(z);
        return result;
    }

    public long getAdjustedEndTimeUs() {
        long largestAdjustedPtsParsed = Long.MIN_VALUE;
        for (int i = 0; i < this.sampleQueues.size(); i++) {
            largestAdjustedPtsParsed = Math.max(largestAdjustedPtsParsed, ((DefaultTrackOutput) this.sampleQueues.valueAt(i)).getLargestParsedTimestampUs());
        }
        return largestAdjustedPtsParsed;
    }

    public TrackOutput track(int id) {
        DefaultTrackOutput sampleQueue = new DefaultTrackOutput(this.allocator);
        this.sampleQueues.put(id, sampleQueue);
        return sampleQueue;
    }

    public void endTracks() {
        this.tracksBuilt = true;
    }

    public void seekMap(SeekMap seekMap) {
    }

    public void drmInitData(DrmInitData drmInit) {
    }
}
