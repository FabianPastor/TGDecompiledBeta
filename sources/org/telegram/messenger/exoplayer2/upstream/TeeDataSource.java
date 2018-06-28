package org.telegram.messenger.exoplayer2.upstream;

import android.net.Uri;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class TeeDataSource implements DataSource {
    private long bytesRemaining;
    private final DataSink dataSink;
    private boolean dataSinkNeedsClosing;
    private final DataSource upstream;

    public TeeDataSource(DataSource upstream, DataSink dataSink) {
        this.upstream = (DataSource) Assertions.checkNotNull(upstream);
        this.dataSink = (DataSink) Assertions.checkNotNull(dataSink);
    }

    public long open(DataSpec dataSpec) throws IOException {
        this.bytesRemaining = this.upstream.open(dataSpec);
        if (this.bytesRemaining == 0) {
            return 0;
        }
        if (dataSpec.length == -1 && this.bytesRemaining != -1) {
            dataSpec = new DataSpec(dataSpec.uri, dataSpec.absoluteStreamPosition, dataSpec.position, this.bytesRemaining, dataSpec.key, dataSpec.flags);
        }
        this.dataSinkNeedsClosing = true;
        this.dataSink.open(dataSpec);
        return this.bytesRemaining;
    }

    public int read(byte[] buffer, int offset, int max) throws IOException {
        if (this.bytesRemaining == 0) {
            return -1;
        }
        int bytesRead = this.upstream.read(buffer, offset, max);
        if (bytesRead <= 0) {
            return bytesRead;
        }
        this.dataSink.write(buffer, offset, bytesRead);
        if (this.bytesRemaining == -1) {
            return bytesRead;
        }
        this.bytesRemaining -= (long) bytesRead;
        return bytesRead;
    }

    public Uri getUri() {
        return this.upstream.getUri();
    }

    public void close() throws IOException {
        try {
            this.upstream.close();
        } finally {
            if (this.dataSinkNeedsClosing) {
                this.dataSinkNeedsClosing = false;
                this.dataSink.close();
            }
        }
    }
}
