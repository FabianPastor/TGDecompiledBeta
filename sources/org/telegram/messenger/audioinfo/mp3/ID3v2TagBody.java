package org.telegram.messenger.audioinfo.mp3;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;
import org.telegram.messenger.audioinfo.util.RangeInputStream;

public class ID3v2TagBody {
    private final ID3v2DataInput data;
    private final RangeInputStream input;
    private final ID3v2TagHeader tagHeader;

    ID3v2TagBody(InputStream delegate, long position, int length, ID3v2TagHeader tagHeader2) throws IOException {
        RangeInputStream rangeInputStream = new RangeInputStream(delegate, position, (long) length);
        this.input = rangeInputStream;
        this.data = new ID3v2DataInput(rangeInputStream);
        this.tagHeader = tagHeader2;
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
        InputStream input2 = this.input;
        if (frameHeader.isUnsynchronization()) {
            byte[] bytes = this.data.readFully(frameHeader.getBodySize());
            boolean ff = false;
            int len = 0;
            int length = bytes.length;
            for (int i = 0; i < length; i++) {
                byte b = bytes[i];
                if (!ff || b != 0) {
                    bytes[len] = b;
                    len++;
                }
                ff = b == -1;
            }
            dataLength = len;
            input2 = new ByteArrayInputStream(bytes, 0, len);
        }
        if (!frameHeader.isEncryption()) {
            if (frameHeader.isCompression()) {
                dataLength = frameHeader.getDataLengthIndicator();
                input2 = new InflaterInputStream(input2);
            }
            return new ID3v2FrameBody(input2, (long) frameHeader.getHeaderSize(), dataLength, this.tagHeader, frameHeader);
        }
        throw new ID3v2Exception("Frame encryption is not supported");
    }

    public String toString() {
        return "id3v2tag[pos=" + getPosition() + ", " + getRemainingLength() + " left]";
    }
}
