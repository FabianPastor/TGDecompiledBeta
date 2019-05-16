package org.telegram.messenger.audioinfo.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PositionInputStream extends FilterInputStream {
    private long position;
    private long positionMark;

    public PositionInputStream(InputStream inputStream) {
        this(inputStream, 0);
    }

    public PositionInputStream(InputStream inputStream, long j) {
        super(inputStream);
        this.position = j;
    }

    public synchronized void mark(int i) {
        this.positionMark = this.position;
        super.mark(i);
    }

    public synchronized void reset() throws IOException {
        super.reset();
        this.position = this.positionMark;
    }

    public int read() throws IOException {
        int read = super.read();
        if (read >= 0) {
            this.position++;
        }
        return read;
    }

    public int read(byte[] bArr, int i, int i2) throws IOException {
        long j = this.position;
        int read = super.read(bArr, i, i2);
        if (read > 0) {
            this.position = j + ((long) read);
        }
        return read;
    }

    public final int read(byte[] bArr) throws IOException {
        return read(bArr, 0, bArr.length);
    }

    public long skip(long j) throws IOException {
        long j2 = this.position;
        j = super.skip(j);
        this.position = j2 + j;
        return j;
    }

    public long getPosition() {
        return this.position;
    }
}
