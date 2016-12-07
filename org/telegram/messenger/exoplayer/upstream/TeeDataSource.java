package org.telegram.messenger.exoplayer.upstream;

import java.io.IOException;
import org.telegram.messenger.exoplayer.util.Assertions;

public final class TeeDataSource implements DataSource {
    private final DataSink dataSink;
    private final DataSource upstream;

    public TeeDataSource(DataSource upstream, DataSink dataSink) {
        this.upstream = (DataSource) Assertions.checkNotNull(upstream);
        this.dataSink = (DataSink) Assertions.checkNotNull(dataSink);
    }

    public long open(DataSpec dataSpec) throws IOException {
        long dataLength = this.upstream.open(dataSpec);
        if (dataSpec.length == -1 && dataLength != -1) {
            dataSpec = new DataSpec(dataSpec.uri, dataSpec.absoluteStreamPosition, dataSpec.position, dataLength, dataSpec.key, dataSpec.flags);
        }
        this.dataSink.open(dataSpec);
        return dataLength;
    }

    public int read(byte[] buffer, int offset, int max) throws IOException {
        int num = this.upstream.read(buffer, offset, max);
        if (num > 0) {
            this.dataSink.write(buffer, offset, num);
        }
        return num;
    }

    public void close() throws IOException {
        try {
            this.upstream.close();
        } finally {
            this.dataSink.close();
        }
    }
}
