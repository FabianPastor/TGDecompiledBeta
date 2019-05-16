package org.telegram.messenger.audioinfo.mp3;

import java.io.IOException;

public class ID3v2FrameHeader {
    private int bodySize;
    private boolean compression;
    private int dataLengthIndicator;
    private boolean encryption;
    private String frameId;
    private int headerSize;
    private boolean unsynchronization;

    public ID3v2FrameHeader(ID3v2TagBody iD3v2TagBody) throws IOException, ID3v2Exception {
        long position = iD3v2TagBody.getPosition();
        ID3v2DataInput data = iD3v2TagBody.getData();
        String str = "ISO-8859-1";
        int i = 2;
        if (iD3v2TagBody.getTagHeader().getVersion() == 2) {
            this.frameId = new String(data.readFully(3), str);
        } else {
            this.frameId = new String(data.readFully(4), str);
        }
        int i2 = 8;
        if (iD3v2TagBody.getTagHeader().getVersion() == 2) {
            this.bodySize = (((data.readByte() & 255) << 16) | ((data.readByte() & 255) << 8)) | (data.readByte() & 255);
        } else if (iD3v2TagBody.getTagHeader().getVersion() == 3) {
            this.bodySize = data.readInt();
        } else {
            this.bodySize = data.readSyncsafeInt();
        }
        if (iD3v2TagBody.getTagHeader().getVersion() > 2) {
            int i3;
            int i4;
            data.readByte();
            byte readByte = data.readByte();
            int i5 = 64;
            boolean z = false;
            if (iD3v2TagBody.getTagHeader().getVersion() == 3) {
                i2 = 128;
                i = 0;
                i3 = 32;
                i4 = 0;
            } else {
                i3 = 64;
                i5 = 4;
                i4 = 1;
            }
            this.compression = (i2 & readByte) != 0;
            this.unsynchronization = (readByte & i) != 0;
            if ((readByte & i5) != 0) {
                z = true;
            }
            this.encryption = z;
            if (iD3v2TagBody.getTagHeader().getVersion() == 3) {
                if (this.compression) {
                    this.dataLengthIndicator = data.readInt();
                    this.bodySize -= 4;
                }
                if (this.encryption) {
                    data.readByte();
                    this.bodySize--;
                }
                if ((readByte & i3) != 0) {
                    data.readByte();
                    this.bodySize--;
                }
            } else {
                if ((readByte & i3) != 0) {
                    data.readByte();
                    this.bodySize--;
                }
                if (this.encryption) {
                    data.readByte();
                    this.bodySize--;
                }
                if ((readByte & i4) != 0) {
                    this.dataLengthIndicator = data.readSyncsafeInt();
                    this.bodySize -= 4;
                }
            }
        }
        this.headerSize = (int) (iD3v2TagBody.getPosition() - position);
    }

    public String getFrameId() {
        return this.frameId;
    }

    public int getHeaderSize() {
        return this.headerSize;
    }

    public int getBodySize() {
        return this.bodySize;
    }

    public boolean isCompression() {
        return this.compression;
    }

    public boolean isEncryption() {
        return this.encryption;
    }

    public boolean isUnsynchronization() {
        return this.unsynchronization;
    }

    public int getDataLengthIndicator() {
        return this.dataLengthIndicator;
    }

    public boolean isValid() {
        boolean z = false;
        int i = 0;
        while (i < this.frameId.length()) {
            if ((this.frameId.charAt(i) < 'A' || this.frameId.charAt(i) > 'Z') && (this.frameId.charAt(i) < '0' || this.frameId.charAt(i) > '9')) {
                return false;
            }
            i++;
        }
        if (this.bodySize > 0) {
            z = true;
        }
        return z;
    }

    public boolean isPadding() {
        boolean z = false;
        for (int i = 0; i < this.frameId.length(); i++) {
            if (this.frameId.charAt(0) != 0) {
                return false;
            }
        }
        if (this.bodySize == 0) {
            z = true;
        }
        return z;
    }

    public String toString() {
        return String.format("%s[id=%s, bodysize=%d]", new Object[]{ID3v2FrameHeader.class.getSimpleName(), this.frameId, Integer.valueOf(this.bodySize)});
    }
}
