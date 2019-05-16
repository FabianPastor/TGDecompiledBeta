package org.telegram.messenger.audioinfo.util;

import java.io.IOException;
import java.io.InputStream;

public class RangeInputStream extends PositionInputStream {
    private final long endPosition;

    public RangeInputStream(InputStream inputStream, long j, long j2) throws IOException {
        super(inputStream, j);
        this.endPosition = j + j2;
    }

    public long getRemainingLength() {
        return this.endPosition - getPosition();
    }

    public int read() throws IOException {
        if (getPosition() == this.endPosition) {
            return -1;
        }
        return super.read();
    }

    public int read(byte[] bArr, int i, int i2) throws IOException {
        long position = getPosition() + ((long) i2);
        long j = this.endPosition;
        if (position > j) {
            i2 = (int) (j - getPosition());
            if (i2 == 0) {
                return -1;
            }
        }
        return super.read(bArr, i, i2);
    }

    public long skip(long j) throws IOException {
        long position = getPosition() + j;
        long j2 = this.endPosition;
        if (position > j2) {
            j = (long) ((int) (j2 - getPosition()));
        }
        return super.skip(j);
    }
}
