package org.telegram.messenger.audioinfo.mp3;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;
import org.telegram.messenger.audioinfo.util.RangeInputStream;

public class ID3v2TagBody {
    private final ID3v2DataInput data = new ID3v2DataInput(this.input);
    private final RangeInputStream input;
    private final ID3v2TagHeader tagHeader;

    ID3v2TagBody(InputStream delegate, long position, int length, ID3v2TagHeader tagHeader) throws IOException {
        this.input = new RangeInputStream(delegate, position, (long) length);
        this.tagHeader = tagHeader;
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

    public ID3v2FrameBody frameBody(ID3v2FrameHeader frameHeader) throws IOException, ID3v2Exception {
        int dataLength = frameHeader.getBodySize();
        InputStream input = this.input;
        if (frameHeader.isUnsynchronization()) {
            byte[] bytes = this.data.readFully(frameHeader.getBodySize());
            int len = 0;
            boolean ff = false;
            for (byte b : bytes) {
                if (!(ff && b == (byte) 0)) {
                    int len2 = len + 1;
                    bytes[len] = b;
                    len = len2;
                }
                ff = b == (byte) -1;
            }
            dataLength = len;
            input = new ByteArrayInputStream(bytes, 0, len);
        }
        if (frameHeader.isEncryption()) {
            throw new ID3v2Exception("Frame encryption is not supported");
        }
        if (frameHeader.isCompression()) {
            dataLength = frameHeader.getDataLengthIndicator();
            input = new InflaterInputStream(input);
        }
        return new ID3v2FrameBody(input, (long) frameHeader.getHeaderSize(), dataLength, this.tagHeader, frameHeader);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("id3v2tag[pos=");
        stringBuilder.append(getPosition());
        stringBuilder.append(", ");
        stringBuilder.append(getRemainingLength());
        stringBuilder.append(" left]");
        return stringBuilder.toString();
    }
}
