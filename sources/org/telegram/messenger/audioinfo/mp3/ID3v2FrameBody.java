package org.telegram.messenger.audioinfo.mp3;

import java.io.IOException;
import java.io.InputStream;
import org.telegram.messenger.audioinfo.util.RangeInputStream;

public class ID3v2FrameBody {
    static final ThreadLocal<Buffer> textBuffer = new C05181();
    private final ID3v2DataInput data = new ID3v2DataInput(this.input);
    private final ID3v2FrameHeader frameHeader;
    private final RangeInputStream input;
    private final ID3v2TagHeader tagHeader;

    /* renamed from: org.telegram.messenger.audioinfo.mp3.ID3v2FrameBody$1 */
    static class C05181 extends ThreadLocal<Buffer> {
        C05181() {
        }

        protected Buffer initialValue() {
            return new Buffer(4096);
        }
    }

    static final class Buffer {
        byte[] bytes;

        Buffer(int initialLength) {
            this.bytes = new byte[initialLength];
        }

        byte[] bytes(int minLength) {
            if (minLength > this.bytes.length) {
                int length = this.bytes.length * 2;
                while (minLength > length) {
                    length *= 2;
                }
                this.bytes = new byte[length];
            }
            return this.bytes;
        }
    }

    ID3v2FrameBody(InputStream delegate, long position, int dataLength, ID3v2TagHeader tagHeader, ID3v2FrameHeader frameHeader) throws IOException {
        this.input = new RangeInputStream(delegate, position, (long) dataLength);
        this.tagHeader = tagHeader;
        this.frameHeader = frameHeader;
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

    private String extractString(byte[] bytes, int offset, int length, ID3v2Encoding encoding, boolean searchZeros) {
        if (searchZeros) {
            int zeros = 0;
            int i = 0;
            while (i < length) {
                if (bytes[offset + i] != (byte) 0 || (encoding == ID3v2Encoding.UTF_16 && zeros == 0 && (offset + i) % 2 != 0)) {
                    zeros = 0;
                } else {
                    zeros++;
                    if (zeros == encoding.getZeroBytes()) {
                        length = (i + 1) - encoding.getZeroBytes();
                        break;
                    }
                }
                i++;
            }
        }
        try {
            String string = new String(bytes, offset, length, encoding.getCharset().name());
            if (string.length() > 0 && string.charAt(0) == '\ufeff') {
                string = string.substring(1);
            }
            return string;
        } catch (Exception e) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
    }

    public String readZeroTerminatedString(int maxLength, ID3v2Encoding encoding) throws IOException, ID3v2Exception {
        int zeros = 0;
        int length = Math.min(maxLength, (int) getRemainingLength());
        byte[] bytes = ((Buffer) textBuffer.get()).bytes(length);
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < length) {
                byte readByte = this.data.readByte();
                bytes[i2] = readByte;
                if (readByte != (byte) 0 || (encoding == ID3v2Encoding.UTF_16 && zeros == 0 && i2 % 2 != 0)) {
                    zeros = 0;
                } else {
                    zeros++;
                    if (zeros == encoding.getZeroBytes()) {
                        return extractString(bytes, 0, (i2 + 1) - encoding.getZeroBytes(), encoding, false);
                    }
                }
                i = i2 + 1;
            } else {
                throw new ID3v2Exception("Could not read zero-termiated string");
            }
        }
    }

    public String readFixedLengthString(int length, ID3v2Encoding encoding) throws IOException, ID3v2Exception {
        if (((long) length) > getRemainingLength()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Could not read fixed-length string of length: ");
            stringBuilder.append(length);
            throw new ID3v2Exception(stringBuilder.toString());
        }
        byte[] bytes = ((Buffer) textBuffer.get()).bytes(length);
        this.data.readFully(bytes, 0, length);
        return extractString(bytes, 0, length, encoding, true);
    }

    public ID3v2Encoding readEncoding() throws IOException, ID3v2Exception {
        byte value = this.data.readByte();
        switch (value) {
            case (byte) 0:
                return ID3v2Encoding.ISO_8859_1;
            case (byte) 1:
                return ID3v2Encoding.UTF_16;
            case (byte) 2:
                return ID3v2Encoding.UTF_16BE;
            case (byte) 3:
                return ID3v2Encoding.UTF_8;
            default:
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Invalid encoding: ");
                stringBuilder.append(value);
                throw new ID3v2Exception(stringBuilder.toString());
        }
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
