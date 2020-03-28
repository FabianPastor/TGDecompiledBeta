package org.telegram.messenger.audioinfo.mp3;

public class MP3Frame {
    private final byte[] bytes;
    private final Header header;

    static final class CRCLASSNAME {
        private short crc = -1;

        CRCLASSNAME() {
        }

        public void update(int i, int i2) {
            int i3 = 1 << (i2 - 1);
            do {
                boolean z = false;
                boolean z2 = (this.crc & 32768) == 0;
                if ((i & i3) == 0) {
                    z = true;
                }
                if (z2 ^ z) {
                    short s = (short) (this.crc << 1);
                    this.crc = s;
                    this.crc = (short) (s ^ 32773);
                } else {
                    this.crc = (short) (this.crc << 1);
                }
                i3 >>>= 1;
            } while (i3 != 0);
        }

        public void update(byte b) {
            update(b, 8);
        }

        public short getValue() {
            return this.crc;
        }
    }

    public static class Header {
        private static final int[][] BITRATES = {new int[]{0, 0, 0, 0, 0}, new int[]{32000, 32000, 32000, 32000, 8000}, new int[]{64000, 48000, 40000, 48000, 16000}, new int[]{96000, 56000, 48000, 56000, 24000}, new int[]{128000, 64000, 56000, 64000, 32000}, new int[]{160000, 80000, 64000, 80000, 40000}, new int[]{192000, 96000, 80000, 96000, 48000}, new int[]{224000, 112000, 96000, 112000, 56000}, new int[]{256000, 128000, 112000, 128000, 64000}, new int[]{288000, 160000, 128000, 144000, 80000}, new int[]{320000, 192000, 160000, 160000, 96000}, new int[]{352000, 224000, 192000, 176000, 112000}, new int[]{384000, 256000, 224000, 192000, 128000}, new int[]{416000, 320000, 256000, 224000, 144000}, new int[]{448000, 384000, 320000, 256000, 160000}, new int[]{-1, -1, -1, -1, -1}};
        private static final int[][] BITRATES_COLUMN = {new int[]{-1, 4, 4, 3}, new int[]{-1, -1, -1, -1}, new int[]{-1, 4, 4, 3}, new int[]{-1, 2, 1, 0}};
        private static final int[][] FREQUENCIES = {new int[]{11025, -1, 22050, 44100}, new int[]{12000, -1, 24000, 48000}, new int[]{8000, -1, 16000, 32000}, new int[]{-1, -1, -1, -1}};
        private static final int[][] SIDE_INFO_SIZES = {new int[]{17, -1, 17, 32}, new int[]{17, -1, 17, 32}, new int[]{17, -1, 17, 32}, new int[]{9, -1, 9, 17}};
        private static final int[][] SIZE_COEFFICIENTS = {new int[]{-1, 72, 144, 12}, new int[]{-1, -1, -1, -1}, new int[]{-1, 72, 144, 12}, new int[]{-1, 144, 144, 12}};
        private static final int[] SLOT_SIZES = {-1, 1, 1, 4};
        private final int bitrate;
        private final int channelMode;
        private final int frequency;
        private final int layer;
        private final int padding;
        private final int protection;
        private final int version;

        public int getVBRIOffset() {
            return 36;
        }

        public Header(int i, int i2, int i3) throws MP3Exception {
            int i4 = (i >> 3) & 3;
            this.version = i4;
            if (i4 != 1) {
                int i5 = (i >> 1) & 3;
                this.layer = i5;
                if (i5 != 0) {
                    int i6 = (i2 >> 4) & 15;
                    this.bitrate = i6;
                    if (i6 == 15) {
                        throw new MP3Exception("Reserved bitrate");
                    } else if (i6 != 0) {
                        int i7 = (i2 >> 2) & 3;
                        this.frequency = i7;
                        if (i7 != 3) {
                            int i8 = 6;
                            this.channelMode = (i3 >> 6) & 3;
                            this.padding = (i2 >> 1) & 1;
                            int i9 = i & 1;
                            this.protection = i9;
                            i8 = i9 != 0 ? 4 : i8;
                            i8 = this.layer == 1 ? i8 + getSideInfoSize() : i8;
                            if (getFrameSize() < i8) {
                                throw new MP3Exception("Frame size must be at least " + i8);
                            }
                            return;
                        }
                        throw new MP3Exception("Reserved frequency");
                    } else {
                        throw new MP3Exception("Free bitrate");
                    }
                } else {
                    throw new MP3Exception("Reserved layer");
                }
            } else {
                throw new MP3Exception("Reserved version");
            }
        }

        public int getVersion() {
            return this.version;
        }

        public int getLayer() {
            return this.layer;
        }

        public int getFrequency() {
            return FREQUENCIES[this.frequency][this.version];
        }

        public int getChannelMode() {
            return this.channelMode;
        }

        public int getProtection() {
            return this.protection;
        }

        public int getSampleCount() {
            return this.layer == 3 ? 384 : 1152;
        }

        public int getFrameSize() {
            return (((SIZE_COEFFICIENTS[this.version][this.layer] * getBitrate()) / getFrequency()) + this.padding) * SLOT_SIZES[this.layer];
        }

        public int getBitrate() {
            return BITRATES[this.bitrate][BITRATES_COLUMN[this.version][this.layer]];
        }

        public int getDuration() {
            return (int) getTotalDuration((long) getFrameSize());
        }

        public long getTotalDuration(long j) {
            long sampleCount = ((((long) getSampleCount()) * j) * 1000) / ((long) (getFrameSize() * getFrequency()));
            return (getVersion() == 3 || getChannelMode() != 3) ? sampleCount : sampleCount / 2;
        }

        public boolean isCompatible(Header header) {
            return this.layer == header.layer && this.version == header.version && this.frequency == header.frequency && this.channelMode == header.channelMode;
        }

        public int getSideInfoSize() {
            return SIDE_INFO_SIZES[this.channelMode][this.version];
        }

        public int getXingOffset() {
            return getSideInfoSize() + 4;
        }
    }

    MP3Frame(Header header2, byte[] bArr) {
        this.header = header2;
        this.bytes = bArr;
    }

    /* access modifiers changed from: package-private */
    public boolean isChecksumError() {
        if (this.header.getProtection() != 0 || this.header.getLayer() != 1) {
            return false;
        }
        CRCLASSNAME crCLASSNAME = new CRCLASSNAME();
        crCLASSNAME.update(this.bytes[2]);
        crCLASSNAME.update(this.bytes[3]);
        int sideInfoSize = this.header.getSideInfoSize();
        for (int i = 0; i < sideInfoSize; i++) {
            crCLASSNAME.update(this.bytes[i + 6]);
        }
        byte[] bArr = this.bytes;
        if (((bArr[5] & 255) | ((bArr[4] & 255) << 8)) != crCLASSNAME.getValue()) {
            return true;
        }
        return false;
    }

    public int getSize() {
        return this.bytes.length;
    }

    public Header getHeader() {
        return this.header;
    }

    /* access modifiers changed from: package-private */
    public boolean isXingFrame() {
        int xingOffset = this.header.getXingOffset();
        byte[] bArr = this.bytes;
        if (bArr.length >= xingOffset + 12 && xingOffset >= 0 && bArr.length >= xingOffset + 8) {
            if (bArr[xingOffset] == 88 && bArr[xingOffset + 1] == 105 && bArr[xingOffset + 2] == 110 && bArr[xingOffset + 3] == 103) {
                return true;
            }
            byte[] bArr2 = this.bytes;
            if (bArr2[xingOffset] == 73 && bArr2[xingOffset + 1] == 110 && bArr2[xingOffset + 2] == 102 && bArr2[xingOffset + 3] == 111) {
                return true;
            }
            return false;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean isVBRIFrame() {
        int vBRIOffset = this.header.getVBRIOffset();
        byte[] bArr = this.bytes;
        if (bArr.length >= vBRIOffset + 26 && bArr[vBRIOffset] == 86 && bArr[vBRIOffset + 1] == 66 && bArr[vBRIOffset + 2] == 82 && bArr[vBRIOffset + 3] == 73) {
            return true;
        }
        return false;
    }

    public int getNumberOfFrames() {
        byte b;
        byte b2;
        if (isXingFrame()) {
            int xingOffset = this.header.getXingOffset();
            byte[] bArr = this.bytes;
            if ((bArr[xingOffset + 7] & 1) == 0) {
                return -1;
            }
            b = ((bArr[xingOffset + 8] & 255) << 24) | ((bArr[xingOffset + 9] & 255) << 16) | ((bArr[xingOffset + 10] & 255) << 8);
            b2 = bArr[xingOffset + 11];
        } else if (!isVBRIFrame()) {
            return -1;
        } else {
            int vBRIOffset = this.header.getVBRIOffset();
            byte[] bArr2 = this.bytes;
            b = ((bArr2[vBRIOffset + 14] & 255) << 24) | ((bArr2[vBRIOffset + 15] & 255) << 16) | ((bArr2[vBRIOffset + 16] & 255) << 8);
            b2 = bArr2[vBRIOffset + 17];
        }
        return (b2 & 255) | b;
    }
}
