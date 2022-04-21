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

        Buffer(int initialLength) {
            this.bytes = new byte[initialLength];
        }

        /* access modifiers changed from: package-private */
        public byte[] bytes(int minLength) {
            byte[] bArr = this.bytes;
            if (minLength > bArr.length) {
                int length = bArr.length * 2;
                while (minLength > length) {
                    length *= 2;
                }
                this.bytes = new byte[length];
            }
            return this.bytes;
        }
    }

    ID3v2FrameBody(InputStream delegate, long position, int dataLength, ID3v2TagHeader tagHeader2, ID3v2FrameHeader frameHeader2) throws IOException {
        RangeInputStream rangeInputStream = new RangeInputStream(delegate, position, (long) dataLength);
        this.input = rangeInputStream;
        this.data = new ID3v2DataInput(rangeInputStream);
        this.tagHeader = tagHeader2;
        this.frameHeader = frameHeader2;
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
            while (true) {
                if (i < length) {
                    if (bytes[offset + i] != 0 || (encoding == ID3v2Encoding.UTF_16 && zeros == 0 && (offset + i) % 2 != 0)) {
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
        }
        try {
            String string = new String(bytes, offset, length, encoding.getCharset().name());
            if (string.length() <= 0 || string.charAt(0) != 65279) {
                return string;
            }
            return string.substring(1);
        } catch (Exception e) {
            return "";
        }
    }

    public String readZeroTerminatedString(int maxLength, ID3v2Encoding encoding) throws IOException, ID3v2Exception {
        int zeros = 0;
        int length = Math.min(maxLength, (int) getRemainingLength());
        byte[] bytes = textBuffer.get().bytes(length);
        for (int i = 0; i < length; i++) {
            byte readByte = this.data.readByte();
            bytes[i] = readByte;
            if (readByte != 0 || (encoding == ID3v2Encoding.UTF_16 && zeros == 0 && i % 2 != 0)) {
                zeros = 0;
            } else {
                zeros++;
                if (zeros == encoding.getZeroBytes()) {
                    return extractString(bytes, 0, (i + 1) - encoding.getZeroBytes(), encoding, false);
                }
            }
        }
        throw new ID3v2Exception("Could not read zero-termiated string");
    }

    public String readFixedLengthString(int length, ID3v2Encoding encoding) throws IOException, ID3v2Exception {
        if (((long) length) <= getRemainingLength()) {
            byte[] bytes = textBuffer.get().bytes(length);
            this.data.readFully(bytes, 0, length);
            return extractString(bytes, 0, length, encoding, true);
        }
        throw new ID3v2Exception("Could not read fixed-length string of length: " + length);
    }

    public ID3v2Encoding readEncoding() throws IOException, ID3v2Exception {
        byte value = this.data.readByte();
        switch (value) {
            case 0:
                return ID3v2Encoding.ISO_8859_1;
            case 1:
                return ID3v2Encoding.UTF_16;
            case 2:
                return ID3v2Encoding.UTF_16BE;
            case 3:
                return ID3v2Encoding.UTF_8;
            default:
                throw new ID3v2Exception("Invalid encoding: " + value);
        }
    }

    public String toString() {
        return "id3v2frame[pos=" + getPosition() + ", " + getRemainingLength() + " left]";
    }
}
