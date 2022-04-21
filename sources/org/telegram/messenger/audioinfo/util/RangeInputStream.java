package org.telegram.messenger.audioinfo.util;

import java.io.IOException;
import java.io.InputStream;

public class RangeInputStream extends PositionInputStream {
    private final long endPosition;

    public RangeInputStream(InputStream delegate, long position, long length) throws IOException {
        super(delegate, position);
        this.endPosition = position + length;
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

    public int read(byte[] b, int off, int len) throws IOException {
        long position = getPosition() + ((long) len);
        long j = this.endPosition;
        if (position <= j || (len = (int) (j - getPosition())) != 0) {
            return super.read(b, off, len);
        }
        return -1;
    }

    public long skip(long n) throws IOException {
        long j = this.endPosition;
        if (getPosition() + n > j) {
            n = (long) ((int) (j - getPosition()));
        }
        return super.skip(n);
    }
}
