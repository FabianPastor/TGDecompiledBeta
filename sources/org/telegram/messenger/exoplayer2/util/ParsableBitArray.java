package org.telegram.messenger.exoplayer2.util;

public final class ParsableBitArray {
    private int bitOffset;
    private int byteLimit;
    private int byteOffset;
    public byte[] data;

    public ParsableBitArray(byte[] data) {
        this(data, data.length);
    }

    public ParsableBitArray(byte[] data, int limit) {
        this.data = data;
        this.byteLimit = limit;
    }

    public void reset(byte[] data) {
        reset(data, data.length);
    }

    public void reset(ParsableByteArray parsableByteArray) {
        reset(parsableByteArray.data, parsableByteArray.limit());
        setPosition(parsableByteArray.getPosition() * 8);
    }

    public void reset(byte[] data, int limit) {
        this.data = data;
        this.byteOffset = 0;
        this.bitOffset = 0;
        this.byteLimit = limit;
    }

    public int bitsLeft() {
        return ((this.byteLimit - this.byteOffset) * 8) - this.bitOffset;
    }

    public int getPosition() {
        return (this.byteOffset * 8) + this.bitOffset;
    }

    public int getBytePosition() {
        Assertions.checkState(this.bitOffset == 0);
        return this.byteOffset;
    }

    public void setPosition(int position) {
        this.byteOffset = position / 8;
        this.bitOffset = position - (this.byteOffset * 8);
        assertValidOffset();
    }

    public void skipBit() {
        int i = this.bitOffset + 1;
        this.bitOffset = i;
        if (i == 8) {
            this.bitOffset = 0;
            this.byteOffset++;
        }
        assertValidOffset();
    }

    public void skipBits(int numBits) {
        int numBytes = numBits / 8;
        this.byteOffset += numBytes;
        this.bitOffset += numBits - (numBytes * 8);
        if (this.bitOffset > 7) {
            this.byteOffset++;
            this.bitOffset -= 8;
        }
        assertValidOffset();
    }

    public boolean readBit() {
        boolean returnValue = (this.data[this.byteOffset] & (128 >> this.bitOffset)) != 0;
        skipBit();
        return returnValue;
    }

    public int readBits(int numBits) {
        if (numBits == 0) {
            return 0;
        }
        int returnValue = 0;
        this.bitOffset += numBits;
        while (this.bitOffset > 8) {
            this.bitOffset -= 8;
            byte[] bArr = this.data;
            int i = this.byteOffset;
            this.byteOffset = i + 1;
            returnValue |= (bArr[i] & 255) << this.bitOffset;
        }
        returnValue = (returnValue | ((this.data[this.byteOffset] & 255) >> (8 - this.bitOffset))) & (-1 >>> (32 - numBits));
        if (this.bitOffset == 8) {
            this.bitOffset = 0;
            this.byteOffset++;
        }
        assertValidOffset();
        return returnValue;
    }

    public void readBits(byte[] buffer, int offset, int numBits) {
        int to = offset + (numBits >> 3);
        for (int i = offset; i < to; i++) {
            byte[] bArr = this.data;
            int i2 = this.byteOffset;
            this.byteOffset = i2 + 1;
            buffer[i] = (byte) (bArr[i2] << this.bitOffset);
            buffer[i] = (byte) (buffer[i] | ((this.data[this.byteOffset] & 255) >> (8 - this.bitOffset)));
        }
        int bitsLeft = numBits & 7;
        if (bitsLeft != 0) {
            buffer[to] = (byte) (buffer[to] & (255 >> bitsLeft));
            if (this.bitOffset + bitsLeft > 8) {
                byte b = buffer[to];
                byte[] bArr2 = this.data;
                int i3 = this.byteOffset;
                this.byteOffset = i3 + 1;
                buffer[to] = (byte) (b | ((byte) ((bArr2[i3] & 255) << this.bitOffset)));
                this.bitOffset -= 8;
            }
            this.bitOffset += bitsLeft;
            buffer[to] = (byte) (buffer[to] | ((byte) (((this.data[this.byteOffset] & 255) >> (8 - this.bitOffset)) << (8 - bitsLeft))));
            if (this.bitOffset == 8) {
                this.bitOffset = 0;
                this.byteOffset++;
            }
            assertValidOffset();
        }
    }

    public void byteAlign() {
        if (this.bitOffset != 0) {
            this.bitOffset = 0;
            this.byteOffset++;
            assertValidOffset();
        }
    }

    public void readBytes(byte[] buffer, int offset, int length) {
        Assertions.checkState(this.bitOffset == 0);
        System.arraycopy(this.data, this.byteOffset, buffer, offset, length);
        this.byteOffset += length;
        assertValidOffset();
    }

    public void skipBytes(int length) {
        Assertions.checkState(this.bitOffset == 0);
        this.byteOffset += length;
        assertValidOffset();
    }

    public void putInt(int value, int numBits) {
        int remainingBitsToRead = numBits;
        if (numBits < 32) {
            value &= (1 << numBits) - 1;
        }
        int firstByteReadSize = Math.min(8 - this.bitOffset, numBits);
        int firstByteRightPaddingSize = (8 - this.bitOffset) - firstByteReadSize;
        int firstByteBitmask = (65280 >> this.bitOffset) | ((1 << firstByteRightPaddingSize) - 1);
        byte[] bArr = this.data;
        int i = this.byteOffset;
        bArr[i] = (byte) (bArr[i] & firstByteBitmask);
        int firstByteInputBits = value >>> (numBits - firstByteReadSize);
        bArr = this.data;
        i = this.byteOffset;
        bArr[i] = (byte) (bArr[i] | (firstByteInputBits << firstByteRightPaddingSize));
        remainingBitsToRead -= firstByteReadSize;
        int currentByteIndex = this.byteOffset + 1;
        while (remainingBitsToRead > 8) {
            int currentByteIndex2 = currentByteIndex + 1;
            this.data[currentByteIndex] = (byte) (value >>> (remainingBitsToRead - 8));
            remainingBitsToRead -= 8;
            currentByteIndex = currentByteIndex2;
        }
        int lastByteRightPaddingSize = 8 - remainingBitsToRead;
        bArr = this.data;
        bArr[currentByteIndex] = (byte) (bArr[currentByteIndex] & ((1 << lastByteRightPaddingSize) - 1));
        int lastByteInput = value & ((1 << remainingBitsToRead) - 1);
        bArr = this.data;
        bArr[currentByteIndex] = (byte) (bArr[currentByteIndex] | (lastByteInput << lastByteRightPaddingSize));
        skipBits(numBits);
        assertValidOffset();
    }

    private void assertValidOffset() {
        boolean z = this.byteOffset >= 0 && (this.byteOffset < this.byteLimit || (this.byteOffset == this.byteLimit && this.bitOffset == 0));
        Assertions.checkState(z);
    }
}
