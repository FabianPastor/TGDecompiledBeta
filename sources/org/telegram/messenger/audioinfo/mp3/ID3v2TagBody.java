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

    ID3v2TagBody(InputStream inputStream, long j, int i, ID3v2TagHeader iD3v2TagHeader) throws IOException {
        this.input = new RangeInputStream(inputStream, j, (long) i);
        this.tagHeader = iD3v2TagHeader;
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

    public ID3v2FrameBody frameBody(ID3v2FrameHeader iD3v2FrameHeader) throws IOException, ID3v2Exception {
        int bodySize = iD3v2FrameHeader.getBodySize();
        InputStream inputStream = this.input;
        if (iD3v2FrameHeader.isUnsynchronization()) {
            byte[] readFully = this.data.readFully(iD3v2FrameHeader.getBodySize());
            int i = 0;
            Object obj = null;
            for (byte b : readFully) {
                if (obj == null || b != (byte) 0) {
                    int i2 = i + 1;
                    readFully[i] = b;
                    i = i2;
                }
                obj = b == (byte) -1 ? 1 : null;
            }
            inputStream = new ByteArrayInputStream(readFully, 0, i);
            bodySize = i;
        }
        if (iD3v2FrameHeader.isEncryption()) {
            throw new ID3v2Exception("Frame encryption is not supported");
        }
        int dataLengthIndicator;
        InputStream inflaterInputStream;
        if (iD3v2FrameHeader.isCompression()) {
            dataLengthIndicator = iD3v2FrameHeader.getDataLengthIndicator();
            inflaterInputStream = new InflaterInputStream(inputStream);
        } else {
            dataLengthIndicator = bodySize;
            inflaterInputStream = inputStream;
        }
        return new ID3v2FrameBody(inflaterInputStream, (long) iD3v2FrameHeader.getHeaderSize(), dataLengthIndicator, this.tagHeader, iD3v2FrameHeader);
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
