package org.telegram.messenger.exoplayer2.util;

public final class ParsableNalUnitBitArray {
    private int bitOffset;
    private int byteLimit;
    private int byteOffset;
    private byte[] data;

    public ParsableNalUnitBitArray(byte[] bArr, int i, int i2) {
        reset(bArr, i, i2);
    }

    public void reset(byte[] bArr, int i, int i2) {
        this.data = bArr;
        this.byteOffset = i;
        this.byteLimit = i2;
        this.bitOffset = null;
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

    public void skipBits(int i) {
        int i2 = this.byteOffset;
        int i3 = i / 8;
        this.byteOffset += i3;
        this.bitOffset += i - (i3 * 8);
        if (this.bitOffset > 7) {
            this.byteOffset++;
            this.bitOffset -= 8;
        }
        while (true) {
            i2++;
            if (i2 > this.byteOffset) {
                assertValidOffset();
                return;
            } else if (shouldSkipByte(i2) != 0) {
                this.byteOffset++;
                i2 += 2;
            }
        }
    }

    public boolean canReadBits(int i) {
        int i2 = this.byteOffset;
        int i3 = i / 8;
        int i4 = this.byteOffset + i3;
        int i5 = (this.bitOffset + i) - (i3 * 8);
        if (i5 > 7) {
            i4++;
            i5 -= 8;
        }
        while (true) {
            i2++;
            if (i2 <= i4 && i4 < this.byteLimit) {
                if (shouldSkipByte(i2)) {
                    i4++;
                    i2 += 2;
                }
            }
        }
        if (i4 < this.byteLimit) {
            return true;
        }
        if (i4 == this.byteLimit && r3 == 0) {
            return true;
        }
        return false;
    }

    public boolean readBit() {
        boolean z = (this.data[this.byteOffset] & (128 >> this.bitOffset)) != 0;
        skipBit();
        return z;
    }

    public int readBits(int i) {
        this.bitOffset += i;
        int i2 = 0;
        while (true) {
            int i3 = 2;
            if (this.bitOffset <= 8) {
                break;
            }
            this.bitOffset -= 8;
            i2 |= (this.data[this.byteOffset] & 255) << this.bitOffset;
            int i4 = this.byteOffset;
            if (!shouldSkipByte(this.byteOffset + 1)) {
                i3 = 1;
            }
            this.byteOffset = i4 + i3;
        }
        i = (-1 >>> (32 - i)) & (i2 | ((this.data[this.byteOffset] & 255) >> (8 - this.bitOffset)));
        if (this.bitOffset == 8) {
            this.bitOffset = 0;
            int i5 = this.byteOffset;
            if (!shouldSkipByte(this.byteOffset + 1)) {
                i3 = 1;
            }
            this.byteOffset = i5 + i3;
        }
        assertValidOffset();
        return i;
    }

    public boolean canReadExpGolombCodedNum() {
        int i = this.byteOffset;
        int i2 = this.bitOffset;
        int i3 = 0;
        while (this.byteOffset < this.byteLimit && !readBit()) {
            i3++;
        }
        int i4 = this.byteOffset == this.byteLimit ? 1 : false;
        this.byteOffset = i;
        this.bitOffset = i2;
        if (i4 == 0 && canReadBits((i3 * 2) + 1)) {
            return true;
        }
        return false;
    }

    public int readUnsignedExpGolombCodedInt() {
        return readExpGolombCodeNum();
    }

    public int readSignedExpGolombCodedInt() {
        int readExpGolombCodeNum = readExpGolombCodeNum();
        return (readExpGolombCodeNum % 2 == 0 ? -1 : 1) * ((readExpGolombCodeNum + 1) / 2);
    }

    private int readExpGolombCodeNum() {
        int i = 0;
        int i2 = 0;
        while (!readBit()) {
            i2++;
        }
        int i3 = (1 << i2) - 1;
        if (i2 > 0) {
            i = readBits(i2);
        }
        return i3 + i;
    }

    private boolean shouldSkipByte(int i) {
        return 2 <= i && i < this.byteLimit && this.data[i] == (byte) 3 && this.data[i - 2] == (byte) 0 && this.data[i - 1] == 0;
    }

    private void assertValidOffset() {
        boolean z = this.byteOffset >= 0 && (this.byteOffset < this.byteLimit || (this.byteOffset == this.byteLimit && this.bitOffset == 0));
        Assertions.checkState(z);
    }
}
