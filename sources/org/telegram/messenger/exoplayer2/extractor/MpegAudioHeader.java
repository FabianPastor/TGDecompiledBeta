package org.telegram.messenger.exoplayer2.extractor;

import org.telegram.messenger.exoplayer2.extractor.ts.PsExtractor;
import org.telegram.messenger.exoplayer2.util.MimeTypes;

public final class MpegAudioHeader {
    private static final int[] BITRATE_V1_L1 = new int[]{32, 64, 96, 128, 160, PsExtractor.AUDIO_STREAM, 224, 256, 288, 320, 352, 384, 416, 448};
    private static final int[] BITRATE_V1_L2 = new int[]{32, 48, 56, 64, 80, 96, 112, 128, 160, PsExtractor.AUDIO_STREAM, 224, 256, 320, 384};
    private static final int[] BITRATE_V1_L3 = new int[]{32, 40, 48, 56, 64, 80, 96, 112, 128, 160, PsExtractor.AUDIO_STREAM, 224, 256, 320};
    private static final int[] BITRATE_V2 = new int[]{8, 16, 24, 32, 40, 48, 56, 64, 80, 96, 112, 128, 144, 160};
    private static final int[] BITRATE_V2_L1 = new int[]{32, 48, 56, 64, 80, 96, 112, 128, 144, 160, 176, PsExtractor.AUDIO_STREAM, 224, 256};
    public static final int MAX_FRAME_SIZE_BYTES = 4096;
    private static final String[] MIME_TYPE_BY_LAYER = new String[]{MimeTypes.AUDIO_MPEG_L1, MimeTypes.AUDIO_MPEG_L2, MimeTypes.AUDIO_MPEG};
    private static final int[] SAMPLING_RATE_V1 = new int[]{44100, 48000, 32000};
    public int bitrate;
    public int channels;
    public int frameSize;
    public String mimeType;
    public int sampleRate;
    public int samplesPerFrame;
    public int version;

    public static int getFrameSize(int i) {
        if ((i & -2097152) != -2097152) {
            return -1;
        }
        int i2 = (i >>> 19) & 3;
        if (i2 == 1) {
            return -1;
        }
        int i3 = (i >>> 17) & 3;
        if (i3 == 0) {
            return -1;
        }
        int i4 = (i >>> 12) & 15;
        if (i4 != 0) {
            if (i4 != 15) {
                int i5 = (i >>> 10) & 3;
                if (i5 == 3) {
                    return -1;
                }
                int i6 = SAMPLING_RATE_V1[i5];
                if (i2 == 2) {
                    i6 /= 2;
                } else if (i2 == 0) {
                    i6 /= 4;
                }
                i = (i >>> 9) & 1;
                if (i3 == 3) {
                    return (((12000 * (i2 == 3 ? BITRATE_V1_L1[i4 - 1] : BITRATE_V2_L1[i4 - 1])) / i6) + i) * 4;
                }
                i4 = i2 == 3 ? i3 == 2 ? BITRATE_V1_L2[i4 - 1] : BITRATE_V1_L3[i4 - 1] : BITRATE_V2[i4 - 1];
                i5 = 144000;
                if (i2 == 3) {
                    return ((144000 * i4) / i6) + i;
                }
                if (i3 == 1) {
                    i5 = DefaultOggSeeker.MATCH_RANGE;
                }
                return ((i5 * i4) / i6) + i;
            }
        }
        return -1;
    }

    public static boolean populateHeader(int i, MpegAudioHeader mpegAudioHeader) {
        if ((i & -2097152) != -2097152) {
            return false;
        }
        int i2 = (i >>> 19) & 3;
        if (i2 == 1) {
            return false;
        }
        int i3 = (i >>> 17) & 3;
        if (i3 == 0) {
            return false;
        }
        int i4 = (i >>> 12) & 15;
        if (i4 != 0) {
            if (i4 != 15) {
                int i5 = (i >>> 10) & 3;
                if (i5 == 3) {
                    return false;
                }
                int i6;
                int i7;
                int i8 = SAMPLING_RATE_V1[i5];
                if (i2 == 2) {
                    i8 /= 2;
                } else if (i2 == 0) {
                    i8 /= 4;
                }
                int i9 = i8;
                i8 = (i >>> 9) & 1;
                int i10 = 1152;
                if (i3 == 3) {
                    i4 = i2 == 3 ? BITRATE_V1_L1[i4 - 1] : BITRATE_V2_L1[i4 - 1];
                    i6 = 384;
                    i7 = (((12000 * i4) / i9) + i8) * 4;
                } else {
                    i7 = 144000;
                    if (i2 == 3) {
                        i4 = i3 == 2 ? BITRATE_V1_L2[i4 - 1] : BITRATE_V1_L3[i4 - 1];
                        i7 = ((144000 * i4) / i9) + i8;
                    } else {
                        i4 = BITRATE_V2[i4 - 1];
                        if (i3 == 1) {
                            i10 = 576;
                        }
                        if (i3 == 1) {
                            i7 = DefaultOggSeeker.MATCH_RANGE;
                        }
                        i7 = ((i7 * i4) / i9) + i8;
                    }
                    i6 = i10;
                }
                mpegAudioHeader.setValues(i2, MIME_TYPE_BY_LAYER[3 - i3], i7, i9, ((i >> 6) & 3) == 3 ? 1 : 2, i4 * 1000, i6);
                return true;
            }
        }
        return false;
    }

    private void setValues(int i, String str, int i2, int i3, int i4, int i5, int i6) {
        this.version = i;
        this.mimeType = str;
        this.frameSize = i2;
        this.sampleRate = i3;
        this.channels = i4;
        this.bitrate = i5;
        this.samplesPerFrame = i6;
    }
}
