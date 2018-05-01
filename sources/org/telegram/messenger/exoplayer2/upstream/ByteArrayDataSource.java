package org.telegram.messenger.exoplayer2.upstream;

import android.net.Uri;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class ByteArrayDataSource implements DataSource {
    private int bytesRemaining;
    private final byte[] data;
    private int readPosition;
    private Uri uri;

    public ByteArrayDataSource(byte[] bArr) {
        Assertions.checkNotNull(bArr);
        Assertions.checkArgument(bArr.length > 0);
        this.data = bArr;
    }

    public long open(DataSpec dataSpec) throws IOException {
        this.uri = dataSpec.uri;
        this.readPosition = (int) dataSpec.position;
        this.bytesRemaining = (int) (dataSpec.length == -1 ? ((long) this.data.length) - dataSpec.position : dataSpec.length);
        if (this.bytesRemaining > 0) {
            if (this.readPosition + this.bytesRemaining <= this.data.length) {
                return (long) this.bytesRemaining;
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unsatisfiable range: [");
        stringBuilder.append(this.readPosition);
        stringBuilder.append(", ");
        stringBuilder.append(dataSpec.length);
        stringBuilder.append("], length: ");
        stringBuilder.append(this.data.length);
        throw new IOException(stringBuilder.toString());
    }

    public int read(byte[] bArr, int i, int i2) throws IOException {
        if (i2 == 0) {
            return null;
        }
        if (this.bytesRemaining == 0) {
            return -1;
        }
        i2 = Math.min(i2, this.bytesRemaining);
        System.arraycopy(this.data, this.readPosition, bArr, i, i2);
        this.readPosition += i2;
        this.bytesRemaining -= i2;
        return i2;
    }

    public Uri getUri() {
        return this.uri;
    }

    public void close() throws IOException {
        this.uri = null;
    }
}
