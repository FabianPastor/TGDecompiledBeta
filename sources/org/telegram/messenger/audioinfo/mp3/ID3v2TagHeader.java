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

    public ID3v2TagHeader(InputStream inputStream) throws IOException, ID3v2Exception {
        this(new PositionInputStream(inputStream));
    }

    ID3v2TagHeader(PositionInputStream positionInputStream) throws IOException, ID3v2Exception {
        boolean z = false;
        this.version = 0;
        this.revision = 0;
        this.headerSize = 0;
        this.totalTagSize = 0;
        this.paddingSize = 0;
        this.footerSize = 0;
        long position = positionInputStream.getPosition();
        ID3v2DataInput iD3v2DataInput = new ID3v2DataInput(positionInputStream);
        String str = new String(iD3v2DataInput.readFully(3), "ISO-8859-1");
        if ("ID3".equals(str)) {
            this.version = iD3v2DataInput.readByte();
            if (this.version == 2 || this.version == 3 || this.version == 4) {
                this.revision = iD3v2DataInput.readByte();
                byte readByte = iD3v2DataInput.readByte();
                this.totalTagSize = iD3v2DataInput.readSyncsafeInt() + 10;
                if (this.version == 2) {
                    this.unsynchronization = (readByte & 128) != 0;
                    if ((readByte & 64) != 0) {
                        z = true;
                    }
                    this.compression = z;
                } else {
                    if ((readByte & 128) != 0) {
                        z = true;
                    }
                    this.unsynchronization = z;
                    if ((readByte & 64) != 0) {
                        if (this.version == 3) {
                            int readInt = iD3v2DataInput.readInt();
                            iD3v2DataInput.readByte();
                            iD3v2DataInput.readByte();
                            this.paddingSize = iD3v2DataInput.readInt();
                            iD3v2DataInput.skipFully((long) (readInt - 6));
                        } else {
                            iD3v2DataInput.skipFully((long) (iD3v2DataInput.readSyncsafeInt() - 4));
                        }
                    }
                    if (this.version >= 4 && (readByte & 16) != 0) {
                        this.footerSize = 10;
                        this.totalTagSize += 10;
                    }
                }
                this.headerSize = (int) (positionInputStream.getPosition() - position);
                return;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unsupported ID3v2 version: ");
            stringBuilder.append(this.version);
            throw new ID3v2Exception(stringBuilder.toString());
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append("Invalid ID3 identifier: ");
        stringBuilder.append(str);
        throw new ID3v2Exception(stringBuilder.toString());
    }

    public ID3v2TagBody tagBody(InputStream inputStream) throws IOException, ID3v2Exception {
        if (this.compression) {
            throw new ID3v2Exception("Tag compression is not supported");
        } else if (this.version >= 4 || !this.unsynchronization) {
            return new ID3v2TagBody(inputStream, (long) this.headerSize, (this.totalTagSize - this.headerSize) - this.footerSize, this);
        } else {
            byte[] readFully = new ID3v2DataInput(inputStream).readFully(this.totalTagSize - this.headerSize);
            int length = readFully.length;
            int i = 0;
            int i2 = i;
            int i3 = i2;
            while (i < length) {
                byte b = readFully[i];
                if (i2 == 0 || b != (byte) 0) {
                    i2 = i3 + 1;
                    readFully[i3] = b;
                    i3 = i2;
                }
                i2 = b == (byte) -1 ? 1 : 0;
                i++;
            }
            return new ID3v2TagBody(new ByteArrayInputStream(readFully, 0, i3), (long) this.headerSize, i3, this);
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
