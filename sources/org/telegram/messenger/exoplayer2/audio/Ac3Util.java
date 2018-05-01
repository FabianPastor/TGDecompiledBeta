package org.telegram.messenger.exoplayer2.audio;

import java.nio.ByteBuffer;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.drm.DrmInitData;
import org.telegram.messenger.exoplayer2.extractor.ts.PsExtractor;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.ParsableBitArray;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class Ac3Util {
    private static final int AC3_SYNCFRAME_AUDIO_SAMPLE_COUNT = 1536;
    private static final int AUDIO_SAMPLES_PER_AUDIO_BLOCK = 256;
    private static final int[] BITRATE_BY_HALF_FRMSIZECOD = new int[]{32, 40, 48, 56, 64, 80, 96, 112, 128, 160, PsExtractor.AUDIO_STREAM, 224, 256, 320, 384, 448, 512, 576, 640};
    private static final int[] BLOCKS_PER_SYNCFRAME_BY_NUMBLKSCOD = new int[]{1, 2, 3, 6};
    private static final int[] CHANNEL_COUNT_BY_ACMOD = new int[]{2, 1, 2, 3, 3, 4, 4, 5};
    private static final int[] SAMPLE_RATE_BY_FSCOD = new int[]{48000, 44100, 32000};
    private static final int[] SAMPLE_RATE_BY_FSCOD2 = new int[]{24000, 22050, 16000};
    private static final int[] SYNCFRAME_SIZE_WORDS_BY_HALF_FRMSIZECOD_44_1 = new int[]{69, 87, 104, 121, 139, 174, 208, 243, 278, 348, 417, 487, 557, 696, 835, 975, 1114, 1253, 1393};
    public static final int TRUEHD_RECHUNK_SAMPLE_COUNT = 8;
    public static final int TRUEHD_SYNCFRAME_PREFIX_LENGTH = 12;

    public static final class Ac3SyncFrameInfo {
        public static final int STREAM_TYPE_TYPE0 = 0;
        public static final int STREAM_TYPE_TYPE1 = 1;
        public static final int STREAM_TYPE_TYPE2 = 2;
        public static final int STREAM_TYPE_UNDEFINED = -1;
        public final int channelCount;
        public final int frameSize;
        public final String mimeType;
        public final int sampleCount;
        public final int sampleRate;
        public final int streamType;

        private Ac3SyncFrameInfo(String str, int i, int i2, int i3, int i4, int i5) {
            this.mimeType = str;
            this.streamType = i;
            this.channelCount = i2;
            this.sampleRate = i3;
            this.frameSize = i4;
            this.sampleCount = i5;
        }
    }

    public static int getAc3SyncframeAudioSampleCount() {
        return AC3_SYNCFRAME_AUDIO_SAMPLE_COUNT;
    }

    public static Format parseAc3AnnexFFormat(ParsableByteArray parsableByteArray, String str, String str2, DrmInitData drmInitData) {
        int i = SAMPLE_RATE_BY_FSCOD[(parsableByteArray.readUnsignedByte() & PsExtractor.AUDIO_STREAM) >> 6];
        int readUnsignedByte = parsableByteArray.readUnsignedByte();
        int i2 = CHANNEL_COUNT_BY_ACMOD[(readUnsignedByte & 56) >> 3];
        if ((readUnsignedByte & 4) != 0) {
            i2++;
        }
        return Format.createAudioSampleFormat(str, MimeTypes.AUDIO_AC3, null, -1, -1, i2, i, null, drmInitData, 0, str2);
    }

    public static Format parseEAc3AnnexFFormat(ParsableByteArray parsableByteArray, String str, String str2, DrmInitData drmInitData) {
        ParsableByteArray parsableByteArray2 = parsableByteArray;
        parsableByteArray2.skipBytes(2);
        int i = SAMPLE_RATE_BY_FSCOD[(parsableByteArray2.readUnsignedByte() & PsExtractor.AUDIO_STREAM) >> 6];
        int readUnsignedByte = parsableByteArray2.readUnsignedByte();
        int i2 = CHANNEL_COUNT_BY_ACMOD[(readUnsignedByte & 14) >> 1];
        if ((readUnsignedByte & 1) != 0) {
            i2++;
        }
        if (((parsableByteArray2.readUnsignedByte() & 30) >> 1) > 0 && (2 & parsableByteArray2.readUnsignedByte()) != 0) {
            i2 += 2;
        }
        int i3 = i2;
        String str3 = MimeTypes.AUDIO_E_AC3;
        if (parsableByteArray2.bytesLeft() > 0 && (parsableByteArray2.readUnsignedByte() & 1) != 0) {
            str3 = MimeTypes.AUDIO_E_AC3_JOC;
        }
        return Format.createAudioSampleFormat(str, str3, null, -1, -1, i3, i, null, drmInitData, 0, str2);
    }

    public static Ac3SyncFrameInfo parseAc3SyncframeInfo(ParsableBitArray parsableBitArray) {
        int i;
        int i2;
        int i3;
        int i4;
        String str;
        int i5;
        ParsableBitArray parsableBitArray2 = parsableBitArray;
        int position = parsableBitArray.getPosition();
        parsableBitArray2.skipBits(40);
        int i6 = parsableBitArray2.readBits(5) == 16 ? 1 : 0;
        parsableBitArray2.setPosition(position);
        int readBits;
        int readBits2;
        String str2;
        if (i6 != 0) {
            int i7;
            int i8;
            parsableBitArray2.skipBits(16);
            position = parsableBitArray2.readBits(2);
            parsableBitArray2.skipBits(3);
            i6 = (parsableBitArray2.readBits(11) + 1) * 2;
            int readBits3 = parsableBitArray2.readBits(2);
            if (readBits3 == 3) {
                i7 = 6;
                i = SAMPLE_RATE_BY_FSCOD2[parsableBitArray2.readBits(2)];
                i8 = 3;
            } else {
                i8 = parsableBitArray2.readBits(2);
                i7 = BLOCKS_PER_SYNCFRAME_BY_NUMBLKSCOD[i8];
                i = SAMPLE_RATE_BY_FSCOD[readBits3];
            }
            i2 = 256 * i7;
            readBits = parsableBitArray2.readBits(3);
            boolean readBit = parsableBitArray.readBit();
            i3 = CHANNEL_COUNT_BY_ACMOD[readBits] + readBit;
            parsableBitArray2.skipBits(10);
            if (parsableBitArray.readBit()) {
                parsableBitArray2.skipBits(8);
            }
            if (readBits == 0) {
                parsableBitArray2.skipBits(5);
                if (parsableBitArray.readBit()) {
                    parsableBitArray2.skipBits(8);
                }
            }
            if (position == 1 && parsableBitArray.readBit()) {
                parsableBitArray2.skipBits(16);
            }
            if (parsableBitArray.readBit()) {
                if (readBits > 2) {
                    parsableBitArray2.skipBits(2);
                }
                if ((readBits & 1) != 0 && readBits > 2) {
                    parsableBitArray2.skipBits(6);
                }
                if ((readBits & 4) != 0) {
                    parsableBitArray2.skipBits(6);
                }
                if (readBit && parsableBitArray.readBit()) {
                    parsableBitArray2.skipBits(5);
                }
                if (position == 0) {
                    if (parsableBitArray.readBit()) {
                        parsableBitArray2.skipBits(6);
                    }
                    if (readBits == 0 && parsableBitArray.readBit()) {
                        parsableBitArray2.skipBits(6);
                    }
                    if (parsableBitArray.readBit()) {
                        parsableBitArray2.skipBits(6);
                    }
                    readBits2 = parsableBitArray2.readBits(2);
                    if (readBits2 == 1) {
                        parsableBitArray2.skipBits(5);
                    } else if (readBits2 == 2) {
                        parsableBitArray2.skipBits(12);
                    } else if (readBits2 == 3) {
                        readBits2 = parsableBitArray2.readBits(5);
                        if (parsableBitArray.readBit()) {
                            parsableBitArray2.skipBits(5);
                            if (parsableBitArray.readBit()) {
                                parsableBitArray2.skipBits(4);
                            }
                            if (parsableBitArray.readBit()) {
                                parsableBitArray2.skipBits(4);
                            }
                            if (parsableBitArray.readBit()) {
                                parsableBitArray2.skipBits(4);
                            }
                            if (parsableBitArray.readBit()) {
                                parsableBitArray2.skipBits(4);
                            }
                            if (parsableBitArray.readBit()) {
                                parsableBitArray2.skipBits(4);
                            }
                            if (parsableBitArray.readBit()) {
                                parsableBitArray2.skipBits(4);
                            }
                            if (parsableBitArray.readBit()) {
                                parsableBitArray2.skipBits(4);
                            }
                            if (parsableBitArray.readBit()) {
                                if (parsableBitArray.readBit()) {
                                    parsableBitArray2.skipBits(4);
                                }
                                if (parsableBitArray.readBit()) {
                                    parsableBitArray2.skipBits(4);
                                }
                            }
                        }
                        if (parsableBitArray.readBit()) {
                            parsableBitArray2.skipBits(5);
                            if (parsableBitArray.readBit()) {
                                parsableBitArray2.skipBits(7);
                                if (parsableBitArray.readBit()) {
                                    parsableBitArray2.skipBits(8);
                                }
                            }
                        }
                        parsableBitArray2.skipBits((readBits2 + 2) * 8);
                        parsableBitArray.byteAlign();
                    }
                    if (readBits < 2) {
                        if (parsableBitArray.readBit()) {
                            parsableBitArray2.skipBits(14);
                        }
                        if (readBits == 0 && parsableBitArray.readBit()) {
                            parsableBitArray2.skipBits(14);
                        }
                    }
                    if (parsableBitArray.readBit()) {
                        if (i8 == 0) {
                            parsableBitArray2.skipBits(5);
                        } else {
                            for (readBits2 = 0; readBits2 < i7; readBits2++) {
                                if (parsableBitArray.readBit()) {
                                    parsableBitArray2.skipBits(5);
                                }
                            }
                        }
                    }
                }
            }
            int i9;
            if (parsableBitArray.readBit()) {
                parsableBitArray2.skipBits(5);
                if (readBits == 2) {
                    parsableBitArray2.skipBits(4);
                }
                if (readBits >= 6) {
                    parsableBitArray2.skipBits(2);
                }
                if (parsableBitArray.readBit()) {
                    parsableBitArray2.skipBits(8);
                }
                if (readBits == 0 && parsableBitArray.readBit()) {
                    parsableBitArray2.skipBits(8);
                }
                i9 = 3;
                if (readBits3 < 3) {
                    parsableBitArray.skipBit();
                }
            } else {
                i9 = 3;
            }
            if (position == 0 && i8 != r2) {
                parsableBitArray.skipBit();
            }
            if (position == 2 && (i8 == r2 || parsableBitArray.readBit())) {
                parsableBitArray2.skipBits(6);
            }
            str2 = MimeTypes.AUDIO_E_AC3;
            if (parsableBitArray.readBit() && parsableBitArray2.readBits(6) == 1 && parsableBitArray2.readBits(8) == 1) {
                str2 = MimeTypes.AUDIO_E_AC3_JOC;
            }
            i4 = position;
            str = str2;
            i5 = i6;
        } else {
            str2 = MimeTypes.AUDIO_AC3;
            parsableBitArray2.skipBits(32);
            i6 = parsableBitArray2.readBits(2);
            readBits = getAc3SyncframeSize(i6, parsableBitArray2.readBits(6));
            parsableBitArray2.skipBits(8);
            readBits2 = parsableBitArray2.readBits(3);
            if (!((readBits2 & 1) == 0 || readBits2 == 1)) {
                parsableBitArray2.skipBits(2);
            }
            if ((readBits2 & 4) != 0) {
                parsableBitArray2.skipBits(2);
            }
            if (readBits2 == 2) {
                parsableBitArray2.skipBits(2);
            }
            i = SAMPLE_RATE_BY_FSCOD[i6];
            i2 = AC3_SYNCFRAME_AUDIO_SAMPLE_COUNT;
            i3 = CHANNEL_COUNT_BY_ACMOD[readBits2] + parsableBitArray.readBit();
            i4 = -1;
            str = str2;
            i5 = readBits;
        }
        return new Ac3SyncFrameInfo(str, i4, i3, i, i5, i2);
    }

    public static int parseAc3SyncframeSize(byte[] bArr) {
        if (bArr.length < 5) {
            return -1;
        }
        return getAc3SyncframeSize((bArr[4] & PsExtractor.AUDIO_STREAM) >> 6, bArr[4] & 63);
    }

    public static int parseEAc3SyncframeAudioSampleCount(ByteBuffer byteBuffer) {
        int i = 6;
        if (((byteBuffer.get(byteBuffer.position() + 4) & PsExtractor.AUDIO_STREAM) >> 6) != 3) {
            i = BLOCKS_PER_SYNCFRAME_BY_NUMBLKSCOD[(byteBuffer.get(byteBuffer.position() + 4) & 48) >> 4];
        }
        return 256 * i;
    }

    public static int parseTrueHdSyncframeAudioSampleCount(byte[] bArr) {
        if (bArr[4] == (byte) -8 && bArr[5] == (byte) 114 && bArr[6] == (byte) 111) {
            if (bArr[7] == (byte) -70) {
                return 40 << (bArr[8] & 7);
            }
        }
        return null;
    }

    public static int parseTrueHdSyncframeAudioSampleCount(ByteBuffer byteBuffer) {
        if (byteBuffer.getInt(byteBuffer.position() + 4) != -NUM) {
            return null;
        }
        return 40 << (byteBuffer.get(byteBuffer.position() + 8) & 7);
    }

    private static int getAc3SyncframeSize(int i, int i2) {
        int i3 = i2 / 2;
        if (i >= 0 && i < SAMPLE_RATE_BY_FSCOD.length && i2 >= 0) {
            if (i3 < SYNCFRAME_SIZE_WORDS_BY_HALF_FRMSIZECOD_44_1.length) {
                i = SAMPLE_RATE_BY_FSCOD[i];
                if (i == 44100) {
                    return 2 * (SYNCFRAME_SIZE_WORDS_BY_HALF_FRMSIZECOD_44_1[i3] + (i2 % 2));
                }
                i2 = BITRATE_BY_HALF_FRMSIZECOD[i3];
                return i == 32000 ? 6 * i2 : 4 * i2;
            }
        }
        return -1;
    }

    private Ac3Util() {
    }
}
