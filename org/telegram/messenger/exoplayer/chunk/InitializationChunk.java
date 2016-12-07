package org.telegram.messenger.exoplayer.chunk;

import java.io.IOException;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.chunk.ChunkExtractorWrapper.SingleTrackOutput;
import org.telegram.messenger.exoplayer.drm.DrmInitData;
import org.telegram.messenger.exoplayer.extractor.DefaultExtractorInput;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.extractor.SeekMap;
import org.telegram.messenger.exoplayer.upstream.DataSource;
import org.telegram.messenger.exoplayer.upstream.DataSpec;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;
import org.telegram.messenger.exoplayer.util.Util;

public final class InitializationChunk extends Chunk implements SingleTrackOutput {
    private volatile int bytesLoaded;
    private DrmInitData drmInitData;
    private final ChunkExtractorWrapper extractorWrapper;
    private volatile boolean loadCanceled;
    private MediaFormat mediaFormat;
    private SeekMap seekMap;

    public InitializationChunk(DataSource dataSource, DataSpec dataSpec, int trigger, Format format, ChunkExtractorWrapper extractorWrapper) {
        this(dataSource, dataSpec, trigger, format, extractorWrapper, -1);
    }

    public InitializationChunk(DataSource dataSource, DataSpec dataSpec, int trigger, Format format, ChunkExtractorWrapper extractorWrapper, int parentId) {
        super(dataSource, dataSpec, 2, trigger, format, parentId);
        this.extractorWrapper = extractorWrapper;
    }

    public long bytesLoaded() {
        return (long) this.bytesLoaded;
    }

    public boolean hasFormat() {
        return this.mediaFormat != null;
    }

    public MediaFormat getFormat() {
        return this.mediaFormat;
    }

    public boolean hasDrmInitData() {
        return this.drmInitData != null;
    }

    public DrmInitData getDrmInitData() {
        return this.drmInitData;
    }

    public boolean hasSeekMap() {
        return this.seekMap != null;
    }

    public SeekMap getSeekMap() {
        return this.seekMap;
    }

    public void seekMap(SeekMap seekMap) {
        this.seekMap = seekMap;
    }

    public void drmInitData(DrmInitData drmInitData) {
        this.drmInitData = drmInitData;
    }

    public void format(MediaFormat mediaFormat) {
        this.mediaFormat = mediaFormat;
    }

    public int sampleData(ExtractorInput input, int length, boolean allowEndOfInput) throws IOException, InterruptedException {
        throw new IllegalStateException("Unexpected sample data in initialization chunk");
    }

    public void sampleData(ParsableByteArray data, int length) {
        throw new IllegalStateException("Unexpected sample data in initialization chunk");
    }

    public void sampleMetadata(long timeUs, int flags, int size, int offset, byte[] encryptionKey) {
        throw new IllegalStateException("Unexpected sample data in initialization chunk");
    }

    public void cancelLoad() {
        this.loadCanceled = true;
    }

    public boolean isLoadCanceled() {
        return this.loadCanceled;
    }

    public void load() throws IOException, InterruptedException {
        DataSpec loadDataSpec = Util.getRemainderDataSpec(this.dataSpec, this.bytesLoaded);
        ExtractorInput input;
        try {
            input = new DefaultExtractorInput(this.dataSource, loadDataSpec.absoluteStreamPosition, this.dataSource.open(loadDataSpec));
            if (this.bytesLoaded == 0) {
                this.extractorWrapper.init(this);
            }
            int result = 0;
            while (result == 0) {
                if (!this.loadCanceled) {
                    result = this.extractorWrapper.read(input);
                }
            }
            this.bytesLoaded = (int) (input.getPosition() - this.dataSpec.absoluteStreamPosition);
            this.dataSource.close();
        } catch (Throwable th) {
            this.dataSource.close();
        }
    }
}
