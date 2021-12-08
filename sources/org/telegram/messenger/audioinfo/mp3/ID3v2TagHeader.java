package org.telegram.messenger.audioinfo.mp3;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.telegram.messenger.audioinfo.util.PositionInputStream;

public class ID3v2TagHeader {
    private boolean compression;
    private int footerSize;
    private int headerSize;
    private int paddingSize;
    private int revision;
    private int totalTagSize;
    private boolean unsynchronization;
    private int version;

    public ID3v2TagHeader(InputStream input) throws IOException, ID3v2Exception {
        this(new PositionInputStream(input));
    }

    ID3v2TagHeader(PositionInputStream input) throws IOException, ID3v2Exception {
        boolean z = false;
        this.version = 0;
        this.revision = 0;
        this.headerSize = 0;
        this.totalTagSize = 0;
        this.paddingSize = 0;
        this.footerSize = 0;
        long startPosition = input.getPosition();
        ID3v2DataInput data = new ID3v2DataInput(input);
        String id = new String(data.readFully(3), "ISO-8859-1");
        if ("ID3".equals(id)) {
            byte readByte = data.readByte();
            this.version = readByte;
            if (readByte == 2 || readByte == 3 || readByte == 4) {
                this.revision = data.readByte();
                byte flags = data.readByte();
                this.totalTagSize = data.readSyncsafeInt() + 10;
                int i = this.version;
                if (i == 2) {
                    this.unsynchronization = (flags & 128) != 0;
                    this.compression = (flags & 64) != 0 ? true : z;
                } else {
                    this.unsynchronization = (flags & 128) != 0 ? true : z;
                    if ((flags & 64) != 0) {
                        if (i == 3) {
                            int extendedHeaderSize = data.readInt();
                            data.readByte();
                            data.readByte();
                            this.paddingSize = data.readInt();
                            data.skipFully((long) (extendedHeaderSize - 6));
                        } else {
                            data.skipFully((long) (data.readSyncsafeInt() - 4));
                        }
                    }
                    if (this.version >= 4 && (flags & 16) != 0) {
                        this.footerSize = 10;
                        this.totalTagSize += 10;
                    }
                }
                this.headerSize = (int) (input.getPosition() - startPosition);
                return;
            }
            throw new ID3v2Exception("Unsupported ID3v2 version: " + this.version);
        }
        throw new ID3v2Exception("Invalid ID3 identifier: " + id);
    }

    public ID3v2TagBody tagBody(InputStream input) throws IOException, ID3v2Exception {
        if (this.compression) {
            throw new ID3v2Exception("Tag compression is not supported");
        } else if (this.version >= 4 || !this.unsynchronization) {
            int i = this.headerSize;
            return new ID3v2TagBody(input, (long) i, (this.totalTagSize - i) - this.footerSize, this);
        } else {
            byte[] bytes = new ID3v2DataInput(input).readFully(this.totalTagSize - this.headerSize);
            int length = bytes.length;
            boolean ff = false;
            int len = 0;
            for (int i2 = 0; i2 < length; i2++) {
                byte b = bytes[i2];
                if (!ff || b != 0) {
                    bytes[len] = b;
                    len++;
                }
                ff = b == -1;
            }
            return new ID3v2TagBody(new ByteArrayInputStream(bytes, 0, len), (long) this.headerSize, len, this);
        }
    }

    public int getVersion() {
        return this.version;
    }

    public int getRevision() {
        return this.revision;
    }

    public int getTotalTagSize() {
        return this.totalTagSize;
    }

    public boolean isUnsynchronization() {
        return this.unsynchronization;
    }

    public boolean isCompression() {
        return this.compression;
    }

    public int getHeaderSize() {
        return this.headerSize;
    }

    public int getFooterSize() {
        return this.footerSize;
    }

    public int getPaddingSize() {
        return this.paddingSize;
    }

    public String toString() {
        return String.format("%s[version=%s, totalTagSize=%d]", new Object[]{getClass().getSimpleName(), Integer.valueOf(this.version), Integer.valueOf(this.totalTagSize)});
    }
}
