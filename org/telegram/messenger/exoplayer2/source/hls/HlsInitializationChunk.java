package org.telegram.messenger.exoplayer2.source.hls;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.DefaultExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.source.chunk.Chunk;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.util.Util;

final class HlsInitializationChunk extends Chunk {
    private int bytesLoaded;
    public final Extractor extractor;
    public final Format format;
    private volatile boolean loadCanceled;

    public HlsInitializationChunk(DataSource dataSource, DataSpec dataSpec, int trackSelectionReason, Object trackSelectionData, Extractor extractor, Format format) {
        super(dataSource, dataSpec, 0, null, trackSelectionReason, trackSelectionData, C.TIME_UNSET, C.TIME_UNSET);
        this.extractor = extractor;
        this.format = format;
    }

    public void init(HlsSampleStreamWrapper output) {
        this.extractor.init(output);
    }

    public long bytesLoaded() {
        return (long) this.bytesLoaded;
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
            int result = 0;
            while (result == 0) {
                if (!this.loadCanceled) {
                    result = this.extractor.read(input, null);
                }
            }
            this.bytesLoaded = (int) (input.getPosition() - this.dataSpec.absoluteStreamPosition);
            this.dataSource.close();
        } catch (Throwable th) {
            this.dataSource.close();
        }
    }
}
