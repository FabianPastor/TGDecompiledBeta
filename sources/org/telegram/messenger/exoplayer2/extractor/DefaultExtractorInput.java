package org.telegram.messenger.exoplayer2.extractor;

import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

public final class DefaultExtractorInput implements ExtractorInput {
    private static final int PEEK_MAX_FREE_SPACE = 524288;
    private static final int PEEK_MIN_FREE_SPACE_AFTER_RESIZE = 65536;
    private static final int SCRATCH_SPACE_SIZE = 4096;
    private final DataSource dataSource;
    private byte[] peekBuffer = new byte[65536];
    private int peekBufferLength;
    private int peekBufferPosition;
    private long position;
    private final byte[] scratchSpace = new byte[4096];
    private final long streamLength;

    public DefaultExtractorInput(DataSource dataSource, long j, long j2) {
        this.dataSource = dataSource;
        this.position = j;
        this.streamLength = j2;
    }

    public int read(byte[] bArr, int i, int i2) throws IOException, InterruptedException {
        int readFromPeekBuffer = readFromPeekBuffer(bArr, i, i2);
        if (readFromPeekBuffer == 0) {
            readFromPeekBuffer = readFromDataSource(bArr, i, i2, 0, true);
        }
        commitBytesRead(readFromPeekBuffer);
        return readFromPeekBuffer;
    }

    public boolean readFully(byte[] bArr, int i, int i2, boolean z) throws IOException, InterruptedException {
        int readFromPeekBuffer = readFromPeekBuffer(bArr, i, i2);
        while (readFromPeekBuffer < i2 && readFromPeekBuffer != -1) {
            readFromPeekBuffer = readFromDataSource(bArr, i, i2, readFromPeekBuffer, z);
        }
        commitBytesRead(readFromPeekBuffer);
        return readFromPeekBuffer != -1 ? 1 : null;
    }

    public void readFully(byte[] bArr, int i, int i2) throws IOException, InterruptedException {
        readFully(bArr, i, i2, false);
    }

    public int skip(int i) throws IOException, InterruptedException {
        int skipFromPeekBuffer = skipFromPeekBuffer(i);
        if (skipFromPeekBuffer == 0) {
            skipFromPeekBuffer = readFromDataSource(this.scratchSpace, 0, Math.min(i, this.scratchSpace.length), 0, true);
        }
        commitBytesRead(skipFromPeekBuffer);
        return skipFromPeekBuffer;
    }

    public boolean skipFully(int i, boolean z) throws IOException, InterruptedException {
        int skipFromPeekBuffer = skipFromPeekBuffer(i);
        while (skipFromPeekBuffer < i && skipFromPeekBuffer != -1) {
            skipFromPeekBuffer = readFromDataSource(this.scratchSpace, -skipFromPeekBuffer, Math.min(i, this.scratchSpace.length + skipFromPeekBuffer), skipFromPeekBuffer, z);
        }
        commitBytesRead(skipFromPeekBuffer);
        return skipFromPeekBuffer != -1;
    }

    public void skipFully(int i) throws IOException, InterruptedException {
        skipFully(i, false);
    }

    public boolean peekFully(byte[] bArr, int i, int i2, boolean z) throws IOException, InterruptedException {
        if (!advancePeekPosition(i2, z)) {
            return null;
        }
        System.arraycopy(this.peekBuffer, this.peekBufferPosition - i2, bArr, i, i2);
        return 1;
    }

    public void peekFully(byte[] bArr, int i, int i2) throws IOException, InterruptedException {
        peekFully(bArr, i, i2, false);
    }

    public boolean advancePeekPosition(int i, boolean z) throws IOException, InterruptedException {
        ensureSpaceForPeek(i);
        int min = Math.min(this.peekBufferLength - this.peekBufferPosition, i);
        while (min < i) {
            min = readFromDataSource(this.peekBuffer, this.peekBufferPosition, i, min, z);
            if (min == -1) {
                return false;
            }
        }
        this.peekBufferPosition += i;
        this.peekBufferLength = Math.max(this.peekBufferLength, this.peekBufferPosition);
        return true;
    }

    public void advancePeekPosition(int i) throws IOException, InterruptedException {
        advancePeekPosition(i, false);
    }

    public void resetPeekPosition() {
        this.peekBufferPosition = 0;
    }

    public long getPeekPosition() {
        return this.position + ((long) this.peekBufferPosition);
    }

    public long getPosition() {
        return this.position;
    }

    public long getLength() {
        return this.streamLength;
    }

    public <E extends Throwable> void setRetryPosition(long j, E e) throws Throwable {
        Assertions.checkArgument(j >= 0);
        this.position = j;
        throw e;
    }

    private void ensureSpaceForPeek(int i) {
        int i2 = this.peekBufferPosition + i;
        if (i2 > this.peekBuffer.length) {
            this.peekBuffer = Arrays.copyOf(this.peekBuffer, Util.constrainValue(this.peekBuffer.length * 2, 65536 + i2, i2 + PEEK_MAX_FREE_SPACE));
        }
    }

    private int skipFromPeekBuffer(int i) {
        i = Math.min(this.peekBufferLength, i);
        updatePeekBuffer(i);
        return i;
    }

    private int readFromPeekBuffer(byte[] bArr, int i, int i2) {
        if (this.peekBufferLength == 0) {
            return 0;
        }
        i2 = Math.min(this.peekBufferLength, i2);
        System.arraycopy(this.peekBuffer, 0, bArr, i, i2);
        updatePeekBuffer(i2);
        return i2;
    }

    private void updatePeekBuffer(int i) {
        this.peekBufferLength -= i;
        this.peekBufferPosition = 0;
        Object obj = this.peekBuffer;
        if (this.peekBufferLength < this.peekBuffer.length - PEEK_MAX_FREE_SPACE) {
            obj = new byte[(this.peekBufferLength + 65536)];
        }
        System.arraycopy(this.peekBuffer, i, obj, 0, this.peekBufferLength);
        this.peekBuffer = obj;
    }

    private int readFromDataSource(byte[] bArr, int i, int i2, int i3, boolean z) throws InterruptedException, IOException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        bArr = this.dataSource.read(bArr, i + i3, i2 - i3);
        if (bArr != -1) {
            return i3 + bArr;
        }
        if (i3 == 0 && z) {
            return -1;
        }
        throw new EOFException();
    }

    private void commitBytesRead(int i) {
        if (i != -1) {
            this.position += (long) i;
        }
    }
}
