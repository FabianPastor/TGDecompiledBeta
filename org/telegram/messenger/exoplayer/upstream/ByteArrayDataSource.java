package org.telegram.messenger.exoplayer.upstream;

import java.io.IOException;
import org.telegram.messenger.exoplayer.util.Assertions;

public final class ByteArrayDataSource implements DataSource {
    private final byte[] data;
    private int readPosition;
    private int remainingBytes;

    public ByteArrayDataSource(byte[] data) {
        Assertions.checkNotNull(data);
        Assertions.checkArgument(data.length > 0);
        this.data = data;
    }

    public long open(DataSpec dataSpec) throws IOException {
        this.readPosition = (int) dataSpec.position;
        this.remainingBytes = (int) (dataSpec.length == -1 ? ((long) this.data.length) - dataSpec.position : dataSpec.length);
        if (this.remainingBytes > 0 && this.readPosition + this.remainingBytes <= this.data.length) {
            return (long) this.remainingBytes;
        }
        throw new IOException("Unsatisfiable range: [" + this.readPosition + ", " + dataSpec.length + "], length: " + this.data.length);
    }

    public void close() throws IOException {
    }

    public int read(byte[] buffer, int offset, int length) throws IOException {
        if (this.remainingBytes == 0) {
            return -1;
        }
        length = Math.min(length, this.remainingBytes);
        System.arraycopy(this.data, this.readPosition, buffer, offset, length);
        this.readPosition += length;
        this.remainingBytes -= length;
        return length;
    }
}
