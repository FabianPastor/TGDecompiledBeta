package org.telegram.messenger.exoplayer.upstream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.telegram.messenger.exoplayer.util.Assertions;

public final class ByteArrayDataSink implements DataSink {
    private ByteArrayOutputStream stream;

    public DataSink open(DataSpec dataSpec) throws IOException {
        if (dataSpec.length == -1) {
            this.stream = new ByteArrayOutputStream();
        } else {
            Assertions.checkArgument(dataSpec.length <= 2147483647L);
            this.stream = new ByteArrayOutputStream((int) dataSpec.length);
        }
        return this;
    }

    public void close() throws IOException {
        this.stream.close();
    }

    public void write(byte[] buffer, int offset, int length) throws IOException {
        this.stream.write(buffer, offset, length);
    }

    public byte[] getData() {
        return this.stream == null ? null : this.stream.toByteArray();
    }
}
