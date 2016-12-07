package org.telegram.messenger.exoplayer.chunk;

import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.drm.DrmInitData;
import org.telegram.messenger.exoplayer.extractor.DefaultTrackOutput;
import org.telegram.messenger.exoplayer.upstream.DataSource;
import org.telegram.messenger.exoplayer.upstream.DataSpec;

public abstract class BaseMediaChunk extends MediaChunk {
    private int firstSampleIndex;
    public final boolean isMediaFormatFinal;
    private DefaultTrackOutput output;

    public abstract DrmInitData getDrmInitData();

    public abstract MediaFormat getMediaFormat();

    public BaseMediaChunk(DataSource dataSource, DataSpec dataSpec, int trigger, Format format, long startTimeUs, long endTimeUs, int chunkIndex, boolean isMediaFormatFinal, int parentId) {
        super(dataSource, dataSpec, trigger, format, startTimeUs, endTimeUs, chunkIndex, parentId);
        this.isMediaFormatFinal = isMediaFormatFinal;
    }

    public void init(DefaultTrackOutput output) {
        this.output = output;
        this.firstSampleIndex = output.getWriteIndex();
    }

    public final int getFirstSampleIndex() {
        return this.firstSampleIndex;
    }

    protected final DefaultTrackOutput getOutput() {
        return this.output;
    }
}
