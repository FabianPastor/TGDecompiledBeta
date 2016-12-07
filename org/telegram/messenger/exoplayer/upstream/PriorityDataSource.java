package org.telegram.messenger.exoplayer.upstream;

import java.io.IOException;
import org.telegram.messenger.exoplayer.util.Assertions;

public final class PriorityDataSource implements DataSource {
    private final int priority;
    private final DataSource upstream;

    public PriorityDataSource(int priority, DataSource upstream) {
        this.priority = priority;
        this.upstream = (DataSource) Assertions.checkNotNull(upstream);
    }

    public long open(DataSpec dataSpec) throws IOException {
        NetworkLock.instance.proceedOrThrow(this.priority);
        return this.upstream.open(dataSpec);
    }

    public int read(byte[] buffer, int offset, int max) throws IOException {
        NetworkLock.instance.proceedOrThrow(this.priority);
        return this.upstream.read(buffer, offset, max);
    }

    public void close() throws IOException {
        this.upstream.close();
    }
}
