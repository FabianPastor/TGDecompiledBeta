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

        private Ac3SyncFrameInfo(String mimeType, int streamType, int channelCount, int sampleRate, int frameSize, int sampleCount) {
            this.mimeType = mimeType;
            this.streamType = streamType;
            this.channelCount = channelCount;
            this.sampleRate = sampleRate;
            this.frameSize = frameSize;
            this.sampleCount = sampleCount;
        }
    }

    public static Format parseAc3AnnexFFormat(ParsableByteArray data, String trackId, String language, DrmInitData drmInitData) {
        int sampleRate = SAMPLE_RATE_BY_FSCOD[(data.readUnsignedByte() & PsExtractor.AUDIO_STREAM) >> 6];
        int nextByte = data.readUnsignedByte();
        int channelCount = CHANNEL_COUNT_BY_ACMOD[(nextByte & 56) >> 3];
        if ((nextByte & 4) != 0) {
            channelCount++;
        }
        return Format.createAudioSampleFormat(trackId, MimeTypes.AUDIO_AC3, null, -1, -1, channelCount, sampleRate, null, drmInitData, 0, language);
    }

    public static Format parseEAc3AnnexFFormat(ParsableByteArray data, String trackId, String language, DrmInitData drmInitData) {
        data.skipBytes(2);
        int sampleRate = SAMPLE_RATE_BY_FSCOD[(data.readUnsignedByte() & PsExtractor.AUDIO_STREAM) >> 6];
        int nextByte = data.readUnsignedByte();
        int channelCount = CHANNEL_COUNT_BY_ACMOD[(nextByte & 14) >> 1];
        if ((nextByte & 1) != 0) {
            channelCount++;
        }
        if (((data.readUnsignedByte() & 30) >> 1) > 0 && (data.readUnsignedByte() & 2) != 0) {
            channelCount += 2;
        }
        String mimeType = MimeTypes.AUDIO_E_AC3;
        if (data.bytesLeft() > 0 && (data.readUnsignedByte() & 1) != 0) {
            mimeType = MimeTypes.AUDIO_E_AC3_JOC;
        }
        return Format.createAudioSampleFormat(trackId, mimeType, null, -1, -1, channelCount, sampleRate, null, drmInitData, 0, language);
    }

    public static Ac3SyncFrameInfo parseAc3SyncframeInfo(ParsableBitArray data) {
        int frameSize;
        int sampleRate;
        int sampleCount;
        int channelCount;
        String mimeType;
        int initialPosition = data.getPosition();
        data.skipBits(40);
        boolean isEac3 = data.readBits(5) == 16;
        data.setPosition(initialPosition);
        int streamType = -1;
        int fscod;
        int acmod;
        if (isEac3) {
            int numblkscod;
            int audioBlocks;
            data.skipBits(16);
            streamType = data.readBits(2);
            data.skipBits(3);
            frameSize = (data.readBits(11) + 1) * 2;
            fscod = data.readBits(2);
            if (fscod == 3) {
                numblkscod = 3;
                sampleRate = SAMPLE_RATE_BY_FSCOD2[data.readBits(2)];
                audioBlocks = 6;
            } else {
                numblkscod = data.readBits(2);
                audioBlocks = BLOCKS_PER_SYNCFRAME_BY_NUMBLKSCOD[numblkscod];
                sampleRate = SAMPLE_RATE_BY_FSCOD[fscod];
            }
            sampleCount = audioBlocks * 256;
            acmod = data.readBits(3);
            boolean lfeon = data.readBit();
            channelCount = CHANNEL_COUNT_BY_ACMOD[acmod] + (lfeon ? 1 : 0);
            data.skipBits(10);
            if (data.readBit()) {
                data.skipBits(8);
            }
            if (acmod == 0) {
                data.skipBits(5);
                if (data.readBit()) {
                    data.skipBits(8);
                }
            }
            if (streamType == 1 && data.readBit()) {
                data.skipBits(16);
            }
            if (data.readBit()) {
                if (acmod > 2) {
                    data.skipBits(2);
                }
                if ((acmod & 1) != 0 && acmod > 2) {
                    data.skipBits(6);
                }
                if ((acmod & 4) != 0) {
                    data.skipBits(6);
                }
                if (lfeon && data.readBit()) {
                    data.skipBits(5);
                }
                if (streamType == 0) {
                    if (data.readBit()) {
                        data.skipBits(6);
                    }
                    if (acmod == 0 && data.readBit()) {
                        data.skipBits(6);
                    }
                    if (data.readBit()) {
                        data.skipBits(6);
                    }
                    int mixdef = data.readBits(2);
                    if (mixdef == 1) {
                        data.skipBits(5);
                    } else if (mixdef == 2) {
                        data.skipBits(12);
                    } else if (mixdef == 3) {
                        int mixdeflen = data.readBits(5);
                        if (data.readBit()) {
                            data.skipBits(5);
                            if (data.readBit()) {
                                data.skipBits(4);
                            }
                            if (data.readBit()) {
                                data.skipBits(4);
                            }
                            if (data.readBit()) {
                                data.skipBits(4);
                            }
                            if (data.readBit()) {
                                data.skipBits(4);
                            }
                            if (data.readBit()) {
                                data.skipBits(4);
                            }
                            if (data.readBit()) {
                                data.skipBits(4);
                            }
                            if (data.readBit()) {
                                data.skipBits(4);
                            }
                            if (data.readBit()) {
                                if (data.readBit()) {
                                    data.skipBits(4);
                                }
                                if (data.readBit()) {
                                    data.skipBits(4);
                                }
                            }
                        }
                        if (data.readBit()) {
                            data.skipBits(5);
                            if (data.readBit()) {
                                data.skipBits(7);
                                if (data.readBit()) {
                                    data.skipBits(8);
                                }
                            }
                        }
                        data.skipBits((mixdeflen + 2) * 8);
                        data.byteAlign();
                    }
                    if (acmod < 2) {
                        if (data.readBit()) {
                            data.skipBits(14);
                        }
                        if (acmod == 0 && data.readBit()) {
                            data.skipBits(14);
                        }
                    }
                    if (data.readBit()) {
                        if (numblkscod == 0) {
                            data.skipBits(5);
                        } else {
                            for (int blk = 0; blk < audioBlocks; blk++) {
                                if (data.readBit()) {
                                    data.skipBits(5);
                                }
                            }
                        }
                    }
                }
            }
            if (data.readBit()) {
                data.skipBits(5);
                if (acmod == 2) {
                    data.skipBits(4);
                }
                if (acmod >= 6) {
                    data.skipBits(2);
                }
                if (data.readBit()) {
                    data.skipBits(8);
                }
                if (acmod == 0 && data.readBit()) {
                    data.skipBits(8);
                }
                if (fscod < 3) {
                    data.skipBit();
                }
            }
            if (streamType == 0 && numblkscod != 3) {
                data.skipBit();
            }
            if (streamType == 2 && (numblkscod == 3 || data.readBit())) {
                data.skipBits(6);
            }
            mimeType = MimeTypes.AUDIO_E_AC3;
            if (data.readBit() && data.readBits(6) == 1 && data.readBits(8) == 1) {
                mimeType = MimeTypes.AUDIO_E_AC3_JOC;
            }
        } else {
            mimeType = MimeTypes.AUDIO_AC3;
            data.skipBits(32);
            fscod = data.readBits(2);
            frameSize = getAc3SyncframeSize(fscod, data.readBits(6));
            data.skipBits(8);
            acmod = data.readBits(3);
            if (!((acmod & 1) == 0 || acmod == 1)) {
                data.skipBits(2);
            }
            if ((acmod & 4) != 0) {
                data.skipBits(2);
            }
            if (acmod == 2) {
                data.skipBits(2);
            }
            sampleRate = SAMPLE_RATE_BY_FSCOD[fscod];
            sampleCount = AC3_SYNCFRAME_AUDIO_SAMPLE_COUNT;
            channelCount = CHANNEL_COUNT_BY_ACMOD[acmod] + (data.readBit() ? 1 : 0);
        }
        return new Ac3SyncFrameInfo(mimeType, streamType, channelCount, sampleRate, frameSize, sampleCount);
    }

    public static int parseAc3SyncframeSize(byte[] data) {
        if (data.length < 5) {
            return -1;
        }
        return getAc3SyncframeSize((data[4] & PsExtractor.AUDIO_STREAM) >> 6, data[4] & 63);
    }

    public static int getAc3SyncframeAudioSampleCount() {
        return AC3_SYNCFRAME_AUDIO_SAMPLE_COUNT;
    }

    public static int parseEAc3SyncframeAudioSampleCount(ByteBuffer buffer) {
        int i;
        if (((buffer.get(buffer.position() + 4) & PsExtractor.AUDIO_STREAM) >> 6) == 3) {
            i = 6;
        } else {
            i = BLOCKS_PER_SYNCFRAME_BY_NUMBLKSCOD[(buffer.get(buffer.position() + 4) & 48) >> 4];
        }
        return i * 256;
    }

    public static int parseTrueHdSyncframeAudioSampleCount(byte[] syncframe) {
        if (syncframe[4] == (byte) -8 && syncframe[5] == (byte) 114 && syncframe[6] == (byte) 111 && syncframe[7] == (byte) -70) {
            return 40 << (syncframe[8] & 7);
        }
        return 0;
    }

    public static int parseTrueHdSyncframeAudioSampleCount(ByteBuffer buffer) {
        if (buffer.getInt(buffer.position() + 4) != -NUM) {
            return 0;
        }
        return 40 << (buffer.get(buffer.position() + 8) & 7);
    }

    private static int getAc3SyncframeSize(int fscod, int frmsizecod) {
        int halfFrmsizecod = frmsizecod / 2;
        if (fscod < 0 || fscod >= SAMPLE_RATE_BY_FSCOD.length || frmsizecod < 0 || halfFrmsizecod >= SYNCFRAME_SIZE_WORDS_BY_HALF_FRMSIZECOD_44_1.length) {
            return -1;
        }
        int sampleRate = SAMPLE_RATE_BY_FSCOD[fscod];
        if (sampleRate == 44100) {
            return (SYNCFRAME_SIZE_WORDS_BY_HALF_FRMSIZECOD_44_1[halfFrmsizecod] + (frmsizecod % 2)) * 2;
        }
        int bitrate = BITRATE_BY_HALF_FRMSIZECOD[halfFrmsizecod];
        if (sampleRate == 32000) {
            return bitrate * 6;
        }
        return bitrate * 4;
    }

    private Ac3Util() {
    }
}
