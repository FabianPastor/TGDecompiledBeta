package org.telegram.messenger.exoplayer.upstream;

import java.io.IOException;

public interface DataSink {
    void close() throws IOException;

    DataSink open(DataSpec dataSpec) throws IOException;

    void write(byte[] bArr, int i, int i2) throws IOException;
}
