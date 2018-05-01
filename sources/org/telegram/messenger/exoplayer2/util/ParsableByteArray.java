package org.telegram.messenger.exoplayer2.util;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.extractor.ts.PsExtractor;

public final class ParsableByteArray {
    public byte[] data;
    private int limit;
    private int position;

    public ParsableByteArray(int i) {
        this.data = new byte[i];
        this.limit = i;
    }

    public ParsableByteArray(byte[] bArr) {
        this.data = bArr;
        this.limit = bArr.length;
    }

    public ParsableByteArray(byte[] bArr, int i) {
        this.data = bArr;
        this.limit = i;
    }

    public void reset(int i) {
        reset(capacity() < i ? new byte[i] : this.data, i);
    }

    public void reset(byte[] bArr, int i) {
        this.data = bArr;
        this.limit = i;
        this.position = null;
    }

    public void reset() {
        this.position = 0;
        this.limit = 0;
    }

    public int bytesLeft() {
        return this.limit - this.position;
    }

    public int limit() {
        return this.limit;
    }

    public void setLimit(int i) {
        boolean z = i >= 0 && i <= this.data.length;
        Assertions.checkArgument(z);
        this.limit = i;
    }

    public int getPosition() {
        return this.position;
    }

    public int capacity() {
        return this.data == null ? 0 : this.data.length;
    }

    public void setPosition(int i) {
        boolean z = i >= 0 && i <= this.limit;
        Assertions.checkArgument(z);
        this.position = i;
    }

    public void skipBytes(int i) {
        setPosition(this.position + i);
    }

    public void readBytes(ParsableBitArray parsableBitArray, int i) {
        readBytes(parsableBitArray.data, 0, i);
        parsableBitArray.setPosition(0);
    }

    public void readBytes(byte[] bArr, int i, int i2) {
        System.arraycopy(this.data, this.position, bArr, i, i2);
        this.position += i2;
    }

    public void readBytes(ByteBuffer byteBuffer, int i) {
        byteBuffer.put(this.data, this.position, i);
        this.position += i;
    }

    public int peekUnsignedByte() {
        return this.data[this.position] & 255;
    }

    public char peekChar() {
        return (char) (((this.data[this.position] & 255) << 8) | (this.data[this.position + 1] & 255));
    }

    public int readUnsignedByte() {
        byte[] bArr = this.data;
        int i = this.position;
        this.position = i + 1;
        return bArr[i] & 255;
    }

    public int readUnsignedShort() {
        byte[] bArr = this.data;
        int i = this.position;
        this.position = i + 1;
        int i2 = (bArr[i] & 255) << 8;
        byte[] bArr2 = this.data;
        int i3 = this.position;
        this.position = i3 + 1;
        return i2 | (bArr2[i3] & 255);
    }

    public int readLittleEndianUnsignedShort() {
        byte[] bArr = this.data;
        int i = this.position;
        this.position = i + 1;
        int i2 = bArr[i] & 255;
        byte[] bArr2 = this.data;
        int i3 = this.position;
        this.position = i3 + 1;
        return i2 | ((bArr2[i3] & 255) << 8);
    }

    public short readShort() {
        byte[] bArr = this.data;
        int i = this.position;
        this.position = i + 1;
        int i2 = (bArr[i] & 255) << 8;
        byte[] bArr2 = this.data;
        int i3 = this.position;
        this.position = i3 + 1;
        return (short) (i2 | (bArr2[i3] & 255));
    }

    public short readLittleEndianShort() {
        byte[] bArr = this.data;
        int i = this.position;
        this.position = i + 1;
        int i2 = bArr[i] & 255;
        byte[] bArr2 = this.data;
        int i3 = this.position;
        this.position = i3 + 1;
        return (short) (i2 | ((bArr2[i3] & 255) << 8));
    }

    public int readUnsignedInt24() {
        byte[] bArr = this.data;
        int i = this.position;
        this.position = i + 1;
        int i2 = (bArr[i] & 255) << 16;
        byte[] bArr2 = this.data;
        int i3 = this.position;
        this.position = i3 + 1;
        i2 |= (bArr2[i3] & 255) << 8;
        bArr2 = this.data;
        i3 = this.position;
        this.position = i3 + 1;
        return i2 | (bArr2[i3] & 255);
    }

    public int readInt24() {
        byte[] bArr = this.data;
        int i = this.position;
        this.position = i + 1;
        int i2 = ((bArr[i] & 255) << 24) >> 8;
        byte[] bArr2 = this.data;
        int i3 = this.position;
        this.position = i3 + 1;
        i2 |= (bArr2[i3] & 255) << 8;
        bArr2 = this.data;
        i3 = this.position;
        this.position = i3 + 1;
        return i2 | (bArr2[i3] & 255);
    }

    public int readLittleEndianInt24() {
        byte[] bArr = this.data;
        int i = this.position;
        this.position = i + 1;
        int i2 = bArr[i] & 255;
        byte[] bArr2 = this.data;
        int i3 = this.position;
        this.position = i3 + 1;
        i2 |= (bArr2[i3] & 255) << 8;
        bArr2 = this.data;
        i3 = this.position;
        this.position = i3 + 1;
        return i2 | ((bArr2[i3] & 255) << 16);
    }

    public int readLittleEndianUnsignedInt24() {
        byte[] bArr = this.data;
        int i = this.position;
        this.position = i + 1;
        int i2 = bArr[i] & 255;
        byte[] bArr2 = this.data;
        int i3 = this.position;
        this.position = i3 + 1;
        i2 |= (bArr2[i3] & 255) << 8;
        bArr2 = this.data;
        i3 = this.position;
        this.position = i3 + 1;
        return i2 | ((bArr2[i3] & 255) << 16);
    }

    public long readUnsignedInt() {
        byte[] bArr = this.data;
        int i = this.position;
        this.position = i + 1;
        long j = (((long) bArr[i]) & 255) << 24;
        byte[] bArr2 = this.data;
        int i2 = this.position;
        this.position = i2 + 1;
        long j2 = j | ((((long) bArr2[i2]) & 255) << 16);
        bArr = this.data;
        i = this.position;
        this.position = i + 1;
        long j3 = j2 | ((((long) bArr[i]) & 255) << 8);
        bArr = this.data;
        i = this.position;
        this.position = i + 1;
        return j3 | (((long) bArr[i]) & 255);
    }

    public long readLittleEndianUnsignedInt() {
        byte[] bArr = this.data;
        int i = this.position;
        this.position = i + 1;
        long j = ((long) bArr[i]) & 255;
        bArr = this.data;
        i = this.position;
        this.position = i + 1;
        long j2 = j | ((((long) bArr[i]) & 255) << 8);
        bArr = this.data;
        i = this.position;
        this.position = i + 1;
        j = j2 | ((((long) bArr[i]) & 255) << 16);
        bArr = this.data;
        i = this.position;
        this.position = i + 1;
        return j | ((((long) bArr[i]) & 255) << 24);
    }

    public int readInt() {
        byte[] bArr = this.data;
        int i = this.position;
        this.position = i + 1;
        int i2 = (bArr[i] & 255) << 24;
        byte[] bArr2 = this.data;
        int i3 = this.position;
        this.position = i3 + 1;
        i2 |= (bArr2[i3] & 255) << 16;
        bArr2 = this.data;
        i3 = this.position;
        this.position = i3 + 1;
        i2 |= (bArr2[i3] & 255) << 8;
        bArr2 = this.data;
        i3 = this.position;
        this.position = i3 + 1;
        return i2 | (bArr2[i3] & 255);
    }

    public int readLittleEndianInt() {
        byte[] bArr = this.data;
        int i = this.position;
        this.position = i + 1;
        int i2 = bArr[i] & 255;
        byte[] bArr2 = this.data;
        int i3 = this.position;
        this.position = i3 + 1;
        i2 |= (bArr2[i3] & 255) << 8;
        bArr2 = this.data;
        i3 = this.position;
        this.position = i3 + 1;
        i2 |= (bArr2[i3] & 255) << 16;
        bArr2 = this.data;
        i3 = this.position;
        this.position = i3 + 1;
        return i2 | ((bArr2[i3] & 255) << 24);
    }

    public long readLong() {
        byte[] bArr = this.data;
        int i = this.position;
        this.position = i + 1;
        long j = (((long) bArr[i]) & 255) << 56;
        byte[] bArr2 = this.data;
        int i2 = this.position;
        this.position = i2 + 1;
        long j2 = j | ((((long) bArr2[i2]) & 255) << 48);
        bArr = this.data;
        i = this.position;
        this.position = i + 1;
        long j3 = j2 | ((((long) bArr[i]) & 255) << 40);
        bArr = this.data;
        i = this.position;
        this.position = i + 1;
        j2 = j3 | ((((long) bArr[i]) & 255) << 32);
        bArr = this.data;
        i = this.position;
        this.position = i + 1;
        j3 = j2 | ((((long) bArr[i]) & 255) << 24);
        bArr = this.data;
        i = this.position;
        this.position = i + 1;
        j2 = j3 | ((((long) bArr[i]) & 255) << 16);
        bArr = this.data;
        i = this.position;
        this.position = i + 1;
        j3 = j2 | ((((long) bArr[i]) & 255) << 8);
        bArr = this.data;
        i = this.position;
        this.position = i + 1;
        return j3 | (((long) bArr[i]) & 255);
    }

    public long readLittleEndianLong() {
        byte[] bArr = this.data;
        int i = this.position;
        this.position = i + 1;
        long j = ((long) bArr[i]) & 255;
        bArr = this.data;
        i = this.position;
        this.position = i + 1;
        long j2 = j | ((((long) bArr[i]) & 255) << 8);
        bArr = this.data;
        i = this.position;
        this.position = i + 1;
        j = j2 | ((((long) bArr[i]) & 255) << 16);
        bArr = this.data;
        i = this.position;
        this.position = i + 1;
        j2 = j | ((((long) bArr[i]) & 255) << 24);
        bArr = this.data;
        i = this.position;
        this.position = i + 1;
        j = j2 | ((((long) bArr[i]) & 255) << 32);
        bArr = this.data;
        i = this.position;
        this.position = i + 1;
        j2 = j | ((((long) bArr[i]) & 255) << 40);
        bArr = this.data;
        i = this.position;
        this.position = i + 1;
        j = j2 | ((((long) bArr[i]) & 255) << 48);
        bArr = this.data;
        i = this.position;
        this.position = i + 1;
        return j | ((((long) bArr[i]) & 255) << 56);
    }

    public int readUnsignedFixedPoint1616() {
        byte[] bArr = this.data;
        int i = this.position;
        this.position = i + 1;
        int i2 = (bArr[i] & 255) << 8;
        byte[] bArr2 = this.data;
        int i3 = this.position;
        this.position = i3 + 1;
        i2 |= bArr2[i3] & 255;
        this.position += 2;
        return i2;
    }

    public int readSynchSafeInt() {
        return (((readUnsignedByte() << 21) | (readUnsignedByte() << 14)) | (readUnsignedByte() << 7)) | readUnsignedByte();
    }

    public int readUnsignedIntToInt() {
        int readInt = readInt();
        if (readInt >= 0) {
            return readInt;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Top bit not zero: ");
        stringBuilder.append(readInt);
        throw new IllegalStateException(stringBuilder.toString());
    }

    public int readLittleEndianUnsignedIntToInt() {
        int readLittleEndianInt = readLittleEndianInt();
        if (readLittleEndianInt >= 0) {
            return readLittleEndianInt;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Top bit not zero: ");
        stringBuilder.append(readLittleEndianInt);
        throw new IllegalStateException(stringBuilder.toString());
    }

    public long readUnsignedLongToLong() {
        long readLong = readLong();
        if (readLong >= 0) {
            return readLong;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Top bit not zero: ");
        stringBuilder.append(readLong);
        throw new IllegalStateException(stringBuilder.toString());
    }

    public float readFloat() {
        return Float.intBitsToFloat(readInt());
    }

    public double readDouble() {
        return Double.longBitsToDouble(readLong());
    }

    public String readString(int i) {
        return readString(i, Charset.forName(C0542C.UTF8_NAME));
    }

    public String readString(int i, Charset charset) {
        String str = new String(this.data, this.position, i, charset);
        this.position += i;
        return str;
    }

    public String readNullTerminatedString(int i) {
        if (i == 0) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        int i2 = (this.position + i) - 1;
        i2 = (i2 >= this.limit || this.data[i2] != (byte) 0) ? i : i - 1;
        String str = new String(this.data, this.position, i2);
        this.position += i;
        return str;
    }

    public String readNullTerminatedString() {
        if (bytesLeft() == 0) {
            return null;
        }
        int i = this.position;
        while (i < this.limit && this.data[i] != (byte) 0) {
            i++;
        }
        String str = new String(this.data, this.position, i - this.position);
        this.position = i;
        if (this.position < this.limit) {
            this.position++;
        }
        return str;
    }

    public String readLine() {
        if (bytesLeft() == 0) {
            return null;
        }
        int i = this.position;
        while (i < this.limit && !Util.isLinebreak(this.data[i])) {
            i++;
        }
        if (i - this.position >= 3 && this.data[this.position] == (byte) -17 && this.data[this.position + 1] == (byte) -69 && this.data[this.position + 2] == (byte) -65) {
            this.position += 3;
        }
        String str = new String(this.data, this.position, i - this.position);
        this.position = i;
        if (this.position == this.limit) {
            return str;
        }
        if (this.data[this.position] == (byte) 13) {
            this.position++;
            if (this.position == this.limit) {
                return str;
            }
        }
        if (this.data[this.position] == (byte) 10) {
            this.position++;
        }
        return str;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long readUtf8EncodedLong() {
        long j = (long) this.data[this.position];
        int i = 7;
        while (true) {
            int i2 = 1;
            if (i < 0) {
                break;
            }
            int i3 = 1 << i;
            if ((j & ((long) i3)) == 0) {
                break;
            }
            i--;
        }
        long j2 = j;
        int i4 = 0;
        if (i4 == 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Invalid UTF-8 sequence first byte: ");
            stringBuilder.append(j2);
            throw new NumberFormatException(stringBuilder.toString());
        }
        while (i2 < i4) {
            byte b = this.data[this.position + i2];
            if ((b & PsExtractor.AUDIO_STREAM) != 128) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("Invalid UTF-8 sequence continuation byte: ");
                stringBuilder.append(j2);
                throw new NumberFormatException(stringBuilder.toString());
            }
            j2 = (j2 << 6) | ((long) (b & 63));
            i2++;
        }
        this.position += i4;
        return j2;
    }
}
