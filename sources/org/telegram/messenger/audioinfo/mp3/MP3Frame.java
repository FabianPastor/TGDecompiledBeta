package org.telegram.messenger.audioinfo.mp3;

public class MP3Frame {
    private final byte[] bytes;
    private final Header header;

    static final class CRCLASSNAME {
        private short crc = (short) -1;

        CRCLASSNAME() {
        }

        public void update(int i, int i2) {
            i2 = 1 << (i2 - 1);
            do {
                int i3 = 0;
                int i4 = (this.crc & 32768) == 0 ? 1 : 0;
                if ((i & i2) == 0) {
                    i3 = 1;
                }
                if ((i4 ^ i3) != 0) {
                    this.crc = (short) (this.crc << 1);
                    this.crc = (short) (this.crc ^ 32773);
                } else {
                    this.crc = (short) (this.crc << 1);
                }
                i2 >>>= 1;
            } while (i2 != 0);
        }

        public void update(byte b) {
            update(b, 8);
        }

        public short getValue() {
            return this.crc;
        }

        public void reset() {
            this.crc = (short) -1;
        }
    }

    public static class Header {
        private static final int[][] BITRATES = new int[][]{new int[]{0, 0, 0, 0, 0}, new int[]{32000, 32000, 32000, 32000, 8000}, new int[]{64000, 48000, 40000, 48000, 16000}, new int[]{96000, 56000, 48000, 56000, 24000}, new int[]{128000, 64000, 56000, 64000, 32000}, new int[]{160000, 80000, 64000, 80000, 40000}, new int[]{192000, 96000, 80000, 96000, 48000}, new int[]{224000, 112000, 96000, 112000, 56000}, new int[]{256000, 128000, 112000, 128000, 64000}, new int[]{288000, 160000, 128000, 144000, 80000}, new int[]{320000, 192000, 160000, 160000, 96000}, new int[]{352000, 224000, 192000, 176000, 112000}, new int[]{384000, 256000, 224000, 192000, 128000}, new int[]{416000, 320000, 256000, 224000, 144000}, new int[]{448000, 384000, 320000, 256000, 160000}, new int[]{-1, -1, -1, -1, -1}};
        private static final int[][] BITRATES_COLUMN = new int[][]{new int[]{-1, 4, 4, 3}, new int[]{-1, -1, -1, -1}, new int[]{-1, 4, 4, 3}, new int[]{-1, 2, 1, 0}};
        private static final int[][] FREQUENCIES = new int[][]{new int[]{11025, -1, 22050, 44100}, new int[]{12000, -1, 24000, 48000}, new int[]{8000, -1, 16000, 32000}, new int[]{-1, -1, -1, -1}};
        private static final int MPEG_BITRATE_FREE = 0;
        private static final int MPEG_BITRATE_RESERVED = 15;
        public static final int MPEG_CHANNEL_MODE_MONO = 3;
        private static final int MPEG_FRQUENCY_RESERVED = 3;
        public static final int MPEG_LAYER_1 = 3;
        public static final int MPEG_LAYER_2 = 2;
        public static final int MPEG_LAYER_3 = 1;
        private static final int MPEG_LAYER_RESERVED = 0;
        public static final int MPEG_PROTECTION_CRC = 0;
        public static final int MPEG_VERSION_1 = 3;
        public static final int MPEG_VERSION_2 = 2;
        public static final int MPEG_VERSION_2_5 = 0;
        private static final int MPEG_VERSION_RESERVED = 1;
        private static final int[][] SIDE_INFO_SIZES = new int[][]{new int[]{17, -1, 17, 32}, new int[]{17, -1, 17, 32}, new int[]{17, -1, 17, 32}, new int[]{9, -1, 9, 17}};
        private static final int[][] SIZE_COEFFICIENTS = new int[][]{new int[]{-1, 72, 144, 12}, new int[]{-1, -1, -1, -1}, new int[]{-1, 72, 144, 12}, new int[]{-1, 144, 144, 12}};
        private static final int[] SLOT_SIZES = new int[]{-1, 1, 1, 4};
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
            this.version = (i >> 3) & 3;
            if (this.version != 1) {
                this.layer = (i >> 1) & 3;
                if (this.layer != 0) {
                    this.bitrate = (i2 >> 4) & 15;
                    int i4 = this.bitrate;
                    if (i4 == 15) {
                        throw new MP3Exception("Reserved bitrate");
                    } else if (i4 != 0) {
                        this.frequency = (i2 >> 2) & 3;
                        if (this.frequency != 3) {
                            i4 = 6;
                            this.channelMode = (i3 >> 6) & 3;
                            this.padding = (i2 >> 1) & 1;
                            this.protection = i & 1;
                            if (this.protection != 0) {
                                i4 = 4;
                            }
                            if (this.layer == 1) {
                                i4 += getSideInfoSize();
                            }
                            if (getFrameSize() < i4) {
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("Frame size must be at least ");
                                stringBuilder.append(i4);
                                throw new MP3Exception(stringBuilder.toString());
                            }
                            return;
                        }
                        throw new MP3Exception("Reserved frequency");
                    } else {
                        throw new MP3Exception("Free bitrate");
                    }
                }
                throw new MP3Exception("Reserved layer");
            }
            throw new MP3Exception("Reserved version");
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

    MP3Frame(Header header, byte[] bArr) {
        this.header = header;
        this.bytes = bArr;
    }

    /* Access modifiers changed, original: 0000 */
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

    /* Access modifiers changed, original: 0000 */
    public boolean isXingFrame() {
        int xingOffset = this.header.getXingOffset();
        byte[] bArr = this.bytes;
        if (bArr.length >= xingOffset + 12 && xingOffset >= 0 && bArr.length >= xingOffset + 8) {
            if (bArr[xingOffset] == (byte) 88 && bArr[xingOffset + 1] == (byte) 105 && bArr[xingOffset + 2] == (byte) 110 && bArr[xingOffset + 3] == (byte) 103) {
                return true;
            }
            bArr = this.bytes;
            if (bArr[xingOffset] == (byte) 73 && bArr[xingOffset + 1] == (byte) 110 && bArr[xingOffset + 2] == (byte) 102 && bArr[xingOffset + 3] == (byte) 111) {
                return true;
            }
            return false;
        }
        return false;
    }

    /* Access modifiers changed, original: 0000 */
    public boolean isVBRIFrame() {
        int vBRIOffset = this.header.getVBRIOffset();
        byte[] bArr = this.bytes;
        boolean z = false;
        if (bArr.length < vBRIOffset + 26) {
            return false;
        }
        if (bArr[vBRIOffset] == (byte) 86 && bArr[vBRIOffset + 1] == (byte) 66 && bArr[vBRIOffset + 2] == (byte) 82 && bArr[vBRIOffset + 3] == (byte) 73) {
            z = true;
        }
        return z;
    }

    public int getNumberOfFrames() {
        int xingOffset;
        int i;
        byte[] bArr;
        if (isXingFrame()) {
            xingOffset = this.header.getXingOffset();
            bArr = this.bytes;
            if ((bArr[xingOffset + 7] & 1) != 0) {
                i = (((bArr[xingOffset + 8] & 255) << 24) | ((bArr[xingOffset + 9] & 255) << 16)) | ((bArr[xingOffset + 10] & 255) << 8);
                xingOffset = bArr[xingOffset + 11];
            }
            return -1;
        }
        if (isVBRIFrame()) {
            xingOffset = this.header.getVBRIOffset();
            bArr = this.bytes;
            i = (((bArr[xingOffset + 14] & 255) << 24) | ((bArr[xingOffset + 15] & 255) << 16)) | ((bArr[xingOffset + 16] & 255) << 8);
            xingOffset = bArr[xingOffset + 17];
        }
        return -1;
        return (xingOffset & 255) | i;
    }
}
