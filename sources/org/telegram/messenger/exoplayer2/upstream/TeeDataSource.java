package org.telegram.messenger.exoplayer2.upstream;

import android.net.Uri;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class TeeDataSource implements DataSource {
    private final DataSink dataSink;
    private final DataSource upstream;

    public TeeDataSource(DataSource dataSource, DataSink dataSink) {
        this.upstream = (DataSource) Assertions.checkNotNull(dataSource);
        this.dataSink = (DataSink) Assertions.checkNotNull(dataSink);
    }

    public long open(DataSpec dataSpec) throws IOException {
        long open = this.upstream.open(dataSpec);
        if (dataSpec.length == -1 && open != -1) {
            dataSpec = new DataSpec(dataSpec.uri, dataSpec.absoluteStreamPosition, dataSpec.position, open, dataSpec.key, dataSpec.flags);
        }
        this.dataSink.open(dataSpec);
        return open;
    }

    public int read(byte[] bArr, int i, int i2) throws IOException {
        i2 = this.upstream.read(bArr, i, i2);
        if (i2 > 0) {
            this.dataSink.write(bArr, i, i2);
        }
        return i2;
    }

    public Uri getUri() {
        return this.upstream.getUri();
    }

    public void close() throws IOException {
        try {
            this.upstream.close();
        } finally {
            this.dataSink.close();
        }
    }
}
