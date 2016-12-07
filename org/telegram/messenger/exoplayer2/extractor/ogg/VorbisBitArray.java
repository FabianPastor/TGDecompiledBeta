package org.telegram.messenger.exoplayer2.extractor.ogg;

import org.telegram.messenger.exoplayer2.util.Assertions;

final class VorbisBitArray {
    private int bitOffset;
    private int byteOffset;
    public final byte[] data;
    private final int limit;

    public VorbisBitArray(byte[] data) {
        this(data, data.length);
    }

    public VorbisBitArray(byte[] data, int limit) {
        this.data = data;
        this.limit = limit * 8;
    }

    public void reset() {
        this.byteOffset = 0;
        this.bitOffset = 0;
    }

    public boolean readBit() {
        return readBits(1) == 1;
    }

    public int readBits(int numBits) {
        Assertions.checkState(getPosition() + numBits <= this.limit);
        if (numBits == 0) {
            return 0;
        }
        int result = 0;
        int bitCount = 0;
        if (this.bitOffset != 0) {
            bitCount = Math.min(numBits, 8 - this.bitOffset);
            result = (this.data[this.byteOffset] >>> this.bitOffset) & (255 >>> (8 - bitCount));
            this.bitOffset += bitCount;
            if (this.bitOffset == 8) {
                this.byteOffset++;
                this.bitOffset = 0;
            }
        }
        if (numBits - bitCount > 7) {
            int numBytes = (numBits - bitCount) / 8;
            for (int i = 0; i < numBytes; i++) {
                long j = (long) result;
                byte[] bArr = this.data;
                int i2 = this.byteOffset;
                this.byteOffset = i2 + 1;
                result = (int) (j | ((((long) bArr[i2]) & 255) << bitCount));
                bitCount += 8;
            }
        }
        if (numBits > bitCount) {
            int bitsOnNextByte = numBits - bitCount;
            result |= (this.data[this.byteOffset] & (255 >>> (8 - bitsOnNextByte))) << bitCount;
            this.bitOffset += bitsOnNextByte;
        }
        return result;
    }

    public void skipBits(int numberOfBits) {
        Assertions.checkState(getPosition() + numberOfBits <= this.limit);
        this.byteOffset += numberOfBits / 8;
        this.bitOffset += numberOfBits % 8;
        if (this.bitOffset > 7) {
            this.byteOffset++;
            this.bitOffset -= 8;
        }
    }

    public int getPosition() {
        return (this.byteOffset * 8) + this.bitOffset;
    }

    public void setPosition(int position) {
        boolean z = position < this.limit && position >= 0;
        Assertions.checkArgument(z);
        this.byteOffset = position / 8;
        this.bitOffset = position - (this.byteOffset * 8);
    }

    public int bitsLeft() {
        return this.limit - getPosition();
    }

    public int limit() {
        return this.limit;
    }
}
