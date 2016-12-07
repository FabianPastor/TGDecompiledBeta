package org.telegram.messenger.exoplayer.upstream;

import java.io.IOException;

public interface DataSource {
    void close() throws IOException;

    long open(DataSpec dataSpec) throws IOException;

    int read(byte[] bArr, int i, int i2) throws IOException;
}
