package org.telegram.messenger.exoplayer2.util;

public final class ParsableNalUnitBitArray {
    private int bitOffset;
    private int byteLimit;
    private int byteOffset;
    private byte[] data;

    public ParsableNalUnitBitArray(byte[] data, int offset, int limit) {
        reset(data, offset, limit);
    }

    public void reset(byte[] data, int offset, int limit) {
        this.data = data;
        this.byteOffset = offset;
        this.byteLimit = limit;
        this.bitOffset = 0;
        assertValidOffset();
    }

    public void skipBit() {
        int i = 1;
        int i2 = this.bitOffset + 1;
        this.bitOffset = i2;
        if (i2 == 8) {
            this.bitOffset = 0;
            i2 = this.byteOffset;
            if (shouldSkipByte(this.byteOffset + 1)) {
                i = 2;
            }
            this.byteOffset = i2 + i;
        }
        assertValidOffset();
    }

    public void skipBits(int numBits) {
        int oldByteOffset = this.byteOffset;
        int numBytes = numBits / 8;
        this.byteOffset += numBytes;
        this.bitOffset += numBits - (numBytes * 8);
        if (this.bitOffset > 7) {
            this.byteOffset++;
            this.bitOffset -= 8;
        }
        int i = oldByteOffset + 1;
        while (i <= this.byteOffset) {
            if (shouldSkipByte(i)) {
                this.byteOffset++;
                i += 2;
            }
            i++;
        }
        assertValidOffset();
    }

    public boolean canReadBits(int numBits) {
        int oldByteOffset = this.byteOffset;
        int numBytes = numBits / 8;
        int newByteOffset = this.byteOffset + numBytes;
        int newBitOffset = (this.bitOffset + numBits) - (numBytes * 8);
        if (newBitOffset > 7) {
            newByteOffset++;
            newBitOffset -= 8;
        }
        int i = oldByteOffset + 1;
        while (i <= newByteOffset && newByteOffset < this.byteLimit) {
            if (shouldSkipByte(i)) {
                newByteOffset++;
                i += 2;
            }
            i++;
        }
        if (newByteOffset < this.byteLimit) {
            return true;
        }
        if (newByteOffset == this.byteLimit && newBitOffset == 0) {
            return true;
        }
        return false;
    }

    public boolean readBit() {
        boolean returnValue = (this.data[this.byteOffset] & (128 >> this.bitOffset)) != 0;
        skipBit();
        return returnValue;
    }

    public int readBits(int numBits) {
        int returnValue = 0;
        this.bitOffset += numBits;
        while (true) {
            int i = 2;
            if (this.bitOffset <= 8) {
                break;
            }
            this.bitOffset -= 8;
            returnValue |= (this.data[this.byteOffset] & 255) << this.bitOffset;
            int i2 = this.byteOffset;
            if (!shouldSkipByte(this.byteOffset + 1)) {
                i = 1;
            }
            this.byteOffset = i2 + i;
        }
        returnValue = (returnValue | ((this.data[this.byteOffset] & 255) >> (8 - this.bitOffset))) & (-1 >>> (32 - numBits));
        if (this.bitOffset == 8) {
            this.bitOffset = 0;
            i2 = this.byteOffset;
            if (!shouldSkipByte(this.byteOffset + 1)) {
                i = 1;
            }
            this.byteOffset = i2 + i;
        }
        assertValidOffset();
        return returnValue;
    }

    public boolean canReadExpGolombCodedNum() {
        int initialByteOffset = this.byteOffset;
        int initialBitOffset = this.bitOffset;
        int leadingZeros = 0;
        while (this.byteOffset < this.byteLimit && !readBit()) {
            leadingZeros++;
        }
        boolean hitLimit = this.byteOffset == this.byteLimit;
        this.byteOffset = initialByteOffset;
        this.bitOffset = initialBitOffset;
        if (hitLimit || !canReadBits((leadingZeros * 2) + 1)) {
            return false;
        }
        return true;
    }

    public int readUnsignedExpGolombCodedInt() {
        return readExpGolombCodeNum();
    }

    public int readSignedExpGolombCodedInt() {
        int codeNum = readExpGolombCodeNum();
        return (codeNum % 2 == 0 ? -1 : 1) * ((codeNum + 1) / 2);
    }

    private int readExpGolombCodeNum() {
        int i = 0;
        int leadingZeros = 0;
        while (!readBit()) {
            leadingZeros++;
        }
        int i2 = (1 << leadingZeros) - 1;
        if (leadingZeros > 0) {
            i = readBits(leadingZeros);
        }
        return i2 + i;
    }

    private boolean shouldSkipByte(int offset) {
        return 2 <= offset && offset < this.byteLimit && this.data[offset] == (byte) 3 && this.data[offset - 2] == (byte) 0 && this.data[offset - 1] == (byte) 0;
    }

    private void assertValidOffset() {
        boolean z = this.byteOffset >= 0 && (this.byteOffset < this.byteLimit || (this.byteOffset == this.byteLimit && this.bitOffset == 0));
        Assertions.checkState(z);
    }
}
