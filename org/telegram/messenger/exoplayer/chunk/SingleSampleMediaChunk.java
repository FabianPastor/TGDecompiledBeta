package org.telegram.messenger.exoplayer.chunk;

import java.io.IOException;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.drm.DrmInitData;
import org.telegram.messenger.exoplayer.upstream.DataSource;
import org.telegram.messenger.exoplayer.upstream.DataSpec;
import org.telegram.messenger.exoplayer.util.Util;
import org.telegram.tgnet.ConnectionsManager;

public final class SingleSampleMediaChunk extends BaseMediaChunk {
    private volatile int bytesLoaded;
    private volatile boolean loadCanceled;
    private final DrmInitData sampleDrmInitData;
    private final MediaFormat sampleFormat;

    public SingleSampleMediaChunk(DataSource dataSource, DataSpec dataSpec, int trigger, Format format, long startTimeUs, long endTimeUs, int chunkIndex, MediaFormat sampleFormat, DrmInitData sampleDrmInitData, int parentId) {
        super(dataSource, dataSpec, trigger, format, startTimeUs, endTimeUs, chunkIndex, true, parentId);
        this.sampleFormat = sampleFormat;
        this.sampleDrmInitData = sampleDrmInitData;
    }

    public long bytesLoaded() {
        return (long) this.bytesLoaded;
    }

    public MediaFormat getMediaFormat() {
        return this.sampleFormat;
    }

    public DrmInitData getDrmInitData() {
        return this.sampleDrmInitData;
    }

    public void cancelLoad() {
        this.loadCanceled = true;
    }

    public boolean isLoadCanceled() {
        return this.loadCanceled;
    }

    public void load() throws IOException, InterruptedException {
        try {
            this.dataSource.open(Util.getRemainderDataSpec(this.dataSpec, this.bytesLoaded));
            int result = 0;
            while (result != -1) {
                this.bytesLoaded += result;
                result = getOutput().sampleData(this.dataSource, (int) ConnectionsManager.DEFAULT_DATACENTER_ID, true);
            }
            getOutput().sampleMetadata(this.startTimeUs, 1, this.bytesLoaded, 0, null);
        } finally {
            this.dataSource.close();
        }
    }
}
