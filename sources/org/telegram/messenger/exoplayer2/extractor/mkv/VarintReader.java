package org.telegram.messenger.exoplayer2.extractor.mkv;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;

final class VarintReader {
    private static final int STATE_BEGIN_READING = 0;
    private static final int STATE_READ_CONTENTS = 1;
    private static final long[] VARINT_LENGTH_MASKS = new long[]{128, 64, 32, 16, 8, 4, 2, 1};
    private int length;
    private final byte[] scratch = new byte[8];
    private int state;

    public void reset() {
        this.state = 0;
        this.length = 0;
    }

    public long readUnsignedVarint(ExtractorInput extractorInput, boolean z, boolean z2, int i) throws IOException, InterruptedException {
        if (this.state == 0) {
            if (!extractorInput.readFully(this.scratch, 0, 1, z)) {
                return -1;
            }
            this.length = parseUnsignedVarintLength(this.scratch[0] & 255);
            if (this.length) {
                throw new IllegalStateException("No valid varint length mask found");
            }
            this.state = 1;
        }
        if (this.length > i) {
            this.state = 0;
            return -2;
        }
        if (!this.length) {
            extractorInput.readFully(this.scratch, 1, this.length - 1);
        }
        this.state = 0;
        return assembleVarint(this.scratch, this.length, z2);
    }

    public int getLastLength() {
        return this.length;
    }

    public static int parseUnsignedVarintLength(int i) {
        for (int i2 = 0; i2 < VARINT_LENGTH_MASKS.length; i2++) {
            if ((VARINT_LENGTH_MASKS[i2] & ((long) i)) != 0) {
                return i2 + 1;
            }
        }
        return -1;
    }

    public static long assembleVarint(byte[] bArr, int i, boolean z) {
        long j = ((long) bArr[0]) & 255;
        long j2 = z ? j & (VARINT_LENGTH_MASKS[i - 1] ^ -1) : j;
        z = true;
        while (z < i) {
            z++;
            j2 = (j2 << 8) | (((long) bArr[z]) & 255);
        }
        return j2;
    }
}
