package org.telegram.messenger.audioinfo.mp3;

import java.io.IOException;
import java.io.InputStream;
import org.telegram.messenger.audioinfo.util.RangeInputStream;

public class ID3v2FrameBody {
    static final ThreadLocal<Buffer> textBuffer = new ThreadLocal<Buffer>() {
        /* Access modifiers changed, original: protected */
        public Buffer initialValue() {
            return new Buffer(4096);
        }
    };
    private final ID3v2DataInput data = new ID3v2DataInput(this.input);
    private final ID3v2FrameHeader frameHeader;
    private final RangeInputStream input;
    private final ID3v2TagHeader tagHeader;

    static final class Buffer {
        byte[] bytes;

        Buffer(int i) {
            this.bytes = new byte[i];
        }

        /* Access modifiers changed, original: 0000 */
        public byte[] bytes(int i) {
            byte[] bArr = this.bytes;
            if (i > bArr.length) {
                int length = bArr.length;
                while (true) {
                    length *= 2;
                    if (i <= length) {
                        break;
                    }
                }
                this.bytes = new byte[length];
            }
            return this.bytes;
        }
    }

    ID3v2FrameBody(InputStream inputStream, long j, int i, ID3v2TagHeader iD3v2TagHeader, ID3v2FrameHeader iD3v2FrameHeader) throws IOException {
        this.input = new RangeInputStream(inputStream, j, (long) i);
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
            for (int i4 = 0; i4 < i2; i4++) {
                int i5 = i + i4;
                if (bArr[i5] != (byte) 0 || (iD3v2Encoding == ID3v2Encoding.UTF_16 && i3 == 0 && i5 % 2 != 0)) {
                    i3 = 0;
                } else {
                    i3++;
                    if (i3 == iD3v2Encoding.getZeroBytes()) {
                        i2 = (i4 + 1) - iD3v2Encoding.getZeroBytes();
                        break;
                    }
                }
            }
        }
        try {
            String str = new String(bArr, i, i2, iD3v2Encoding.getCharset().name());
            if (str.length() > 0 && str.charAt(0) == 65279) {
                str = str.substring(1);
            }
            return str;
        } catch (Exception unused) {
            return "";
        }
    }

    public String readZeroTerminatedString(int i, ID3v2Encoding iD3v2Encoding) throws IOException, ID3v2Exception {
        i = Math.min(i, (int) getRemainingLength());
        byte[] bytes = ((Buffer) textBuffer.get()).bytes(i);
        int i2 = 0;
        int i3 = 0;
        while (i2 < i) {
            byte readByte = this.data.readByte();
            bytes[i2] = readByte;
            if (readByte != (byte) 0 || (iD3v2Encoding == ID3v2Encoding.UTF_16 && i3 == 0 && i2 % 2 != 0)) {
                i3 = 0;
            } else {
                i3++;
                if (i3 == iD3v2Encoding.getZeroBytes()) {
                    return extractString(bytes, 0, (i2 + 1) - iD3v2Encoding.getZeroBytes(), iD3v2Encoding, false);
                }
            }
            i2++;
        }
        throw new ID3v2Exception("Could not read zero-termiated string");
    }

    public String readFixedLengthString(int i, ID3v2Encoding iD3v2Encoding) throws IOException, ID3v2Exception {
        if (((long) i) <= getRemainingLength()) {
            byte[] bytes = ((Buffer) textBuffer.get()).bytes(i);
            this.data.readFully(bytes, 0, i);
            return extractString(bytes, 0, i, iD3v2Encoding, true);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Could not read fixed-length string of length: ");
        stringBuilder.append(i);
        throw new ID3v2Exception(stringBuilder.toString());
    }

    public ID3v2Encoding readEncoding() throws IOException, ID3v2Exception {
        byte readByte = this.data.readByte();
        if (readByte == (byte) 0) {
            return ID3v2Encoding.ISO_8859_1;
        }
        if (readByte == (byte) 1) {
            return ID3v2Encoding.UTF_16;
        }
        if (readByte == (byte) 2) {
            return ID3v2Encoding.UTF_16BE;
        }
        if (readByte == (byte) 3) {
            return ID3v2Encoding.UTF_8;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Invalid encoding: ");
        stringBuilder.append(readByte);
        throw new ID3v2Exception(stringBuilder.toString());
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("id3v2frame[pos=");
        stringBuilder.append(getPosition());
        stringBuilder.append(", ");
        stringBuilder.append(getRemainingLength());
        stringBuilder.append(" left]");
        return stringBuilder.toString();
    }
}
