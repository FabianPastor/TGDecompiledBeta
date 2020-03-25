package org.telegram.messenger.audioinfo.mp3;

import java.io.IOException;
import java.io.InputStream;
import org.telegram.messenger.audioinfo.util.RangeInputStream;

public class ID3v2FrameBody {
    static final ThreadLocal<Buffer> textBuffer = new ThreadLocal<Buffer>() {
        /* access modifiers changed from: protected */
        public Buffer initialValue() {
            return new Buffer(4096);
        }
    };
    private final ID3v2DataInput data;
    private final ID3v2FrameHeader frameHeader;
    private final RangeInputStream input;
    private final ID3v2TagHeader tagHeader;

    static final class Buffer {
        byte[] bytes;

        Buffer(int i) {
            this.bytes = new byte[i];
        }

        /* access modifiers changed from: package-private */
        public byte[] bytes(int i) {
            byte[] bArr = this.bytes;
            if (i > bArr.length) {
                int length = bArr.length;
                do {
                    length *= 2;
                } while (i > length);
                this.bytes = new byte[length];
            }
            return this.bytes;
        }
    }

    ID3v2FrameBody(InputStream inputStream, long j, int i, ID3v2TagHeader iD3v2TagHeader, ID3v2FrameHeader iD3v2FrameHeader) throws IOException {
        RangeInputStream rangeInputStream = new RangeInputStream(inputStream, j, (long) i);
        this.input = rangeInputStream;
        this.data = new ID3v2DataInput(rangeInputStream);
        this.tagHeader = iD3v2TagHeader;
        this.frameHeader = iD3v2FrameHeader;
    }

    public ID3v2DataInput getData() {
        return this.data;
    }

    public long getPosition() {
        return this.input.getPosition();
    }

    public long getRemainingLength() {
        return this.input.getRemainingLength();
    }

    public ID3v2TagHeader getTagHeader() {
        return this.tagHeader;
    }

    public ID3v2FrameHeader getFrameHeader() {
        return this.frameHeader;
    }

    private String extractString(byte[] bArr, int i, int i2, ID3v2Encoding iD3v2Encoding, boolean z) {
        if (z) {
            int i3 = 0;
            int i4 = 0;
            while (true) {
                if (i3 < i2) {
                    int i5 = i + i3;
                    if (bArr[i5] != 0 || (iD3v2Encoding == ID3v2Encoding.UTF_16 && i4 == 0 && i5 % 2 != 0)) {
                        i4 = 0;
                    } else {
                        i4++;
                        if (i4 == iD3v2Encoding.getZeroBytes()) {
                            i2 = (i3 + 1) - iD3v2Encoding.getZeroBytes();
                            break;
                        }
                    }
                    i3++;
                }
            }
        }
        try {
            String str = new String(bArr, i, i2, iD3v2Encoding.getCharset().name());
            return (str.length() <= 0 || str.charAt(0) != 65279) ? str : str.substring(1);
        } catch (Exception unused) {
            return "";
        }
    }

    public String readZeroTerminatedString(int i, ID3v2Encoding iD3v2Encoding) throws IOException, ID3v2Exception {
        int min = Math.min(i, (int) getRemainingLength());
        byte[] bytes = textBuffer.get().bytes(min);
        int i2 = 0;
        for (int i3 = 0; i3 < min; i3++) {
            byte readByte = this.data.readByte();
            bytes[i3] = readByte;
            if (readByte != 0 || (iD3v2Encoding == ID3v2Encoding.UTF_16 && i2 == 0 && i3 % 2 != 0)) {
                i2 = 0;
            } else {
                i2++;
                if (i2 == iD3v2Encoding.getZeroBytes()) {
                    return extractString(bytes, 0, (i3 + 1) - iD3v2Encoding.getZeroBytes(), iD3v2Encoding, false);
                }
            }
        }
        throw new ID3v2Exception("Could not read zero-termiated string");
    }

    public String readFixedLengthString(int i, ID3v2Encoding iD3v2Encoding) throws IOException, ID3v2Exception {
        if (((long) i) <= getRemainingLength()) {
            byte[] bytes = textBuffer.get().bytes(i);
            this.data.readFully(bytes, 0, i);
            return extractString(bytes, 0, i, iD3v2Encoding, true);
        }
        throw new ID3v2Exception("Could not read fixed-length string of length: " + i);
    }

    public ID3v2Encoding readEncoding() throws IOException, ID3v2Exception {
        byte readByte = this.data.readByte();
        if (readByte == 0) {
            return ID3v2Encoding.ISO_8859_1;
        }
        if (readByte == 1) {
            return ID3v2Encoding.UTF_16;
        }
        if (readByte == 2) {
            return ID3v2Encoding.UTF_16BE;
        }
        if (readByte == 3) {
            return ID3v2Encoding.UTF_8;
        }
        throw new ID3v2Exception("Invalid encoding: " + readByte);
    }

    public String toString() {
        return "id3v2frame[pos=" + getPosition() + ", " + getRemainingLength() + " left]";
    }
}
