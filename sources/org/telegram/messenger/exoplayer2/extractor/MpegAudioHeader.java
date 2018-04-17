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

    public static int getFrameSize(int header) {
        if ((header & -2097152) != -2097152) {
            return -1;
        }
        int version = (header >>> 19) & 3;
        if (version == 1) {
            return -1;
        }
        int layer = (header >>> 17) & 3;
        if (layer == 0) {
            return -1;
        }
        int bitrateIndex = (header >>> 12) & 15;
        if (bitrateIndex != 0) {
            if (bitrateIndex != 15) {
                int samplingRateIndex = (header >>> 10) & 3;
                if (samplingRateIndex == 3) {
                    return -1;
                }
                int samplingRate = SAMPLING_RATE_V1[samplingRateIndex];
                if (version == 2) {
                    samplingRate /= 2;
                } else if (version == 0) {
                    samplingRate /= 4;
                }
                int padding = (header >>> 9) & 1;
                if (layer == 3) {
                    return (((12000 * (version == 3 ? BITRATE_V1_L1[bitrateIndex - 1] : BITRATE_V2_L1[bitrateIndex - 1])) / samplingRate) + padding) * 4;
                }
                int bitrate;
                if (version == 3) {
                    bitrate = layer == 2 ? BITRATE_V1_L2[bitrateIndex - 1] : BITRATE_V1_L3[bitrateIndex - 1];
                } else {
                    bitrate = BITRATE_V2[bitrateIndex - 1];
                }
                int i = 144000;
                if (version == 3) {
                    return ((144000 * bitrate) / samplingRate) + padding;
                }
                if (layer == 1) {
                    i = DefaultOggSeeker.MATCH_RANGE;
                }
                return ((i * bitrate) / samplingRate) + padding;
            }
        }
        return -1;
    }

    public static boolean populateHeader(int headerData, MpegAudioHeader header) {
        if ((headerData & -2097152) != -2097152) {
            return false;
        }
        int version = (headerData >>> 19) & 3;
        if (version == 1) {
            return false;
        }
        int layer = (headerData >>> 17) & 3;
        if (layer == 0) {
            return false;
        }
        int bitrateIndex = (headerData >>> 12) & 15;
        if (bitrateIndex != 0) {
            if (bitrateIndex != 15) {
                int samplingRateIndex = (headerData >>> 10) & 3;
                if (samplingRateIndex == 3) {
                    return false;
                }
                int bitrate;
                int samplesPerFrame;
                int frameSize;
                int sampleRate = SAMPLING_RATE_V1[samplingRateIndex];
                if (version == 2) {
                    sampleRate /= 2;
                } else if (version == 0) {
                    sampleRate /= 4;
                }
                int padding = (headerData >>> 9) & 1;
                int bitrate2;
                if (layer == 3) {
                    bitrate2 = version == 3 ? BITRATE_V1_L1[bitrateIndex - 1] : BITRATE_V2_L1[bitrateIndex - 1];
                    bitrate = bitrate2;
                    samplesPerFrame = 384;
                    frameSize = (((12000 * bitrate2) / sampleRate) + padding) * 4;
                } else {
                    int bitrate3;
                    bitrate2 = 144000;
                    if (version == 3) {
                        bitrate3 = layer == 2 ? BITRATE_V1_L2[bitrateIndex - 1] : BITRATE_V1_L3[bitrateIndex - 1];
                        frameSize = 1152;
                        bitrate2 = ((144000 * bitrate3) / sampleRate) + padding;
                    } else {
                        bitrate3 = BITRATE_V2[bitrateIndex - 1];
                        frameSize = layer == 1 ? 576 : 1152;
                        if (layer == 1) {
                            bitrate2 = DefaultOggSeeker.MATCH_RANGE;
                        }
                        bitrate2 = ((bitrate2 * bitrate3) / sampleRate) + padding;
                    }
                    bitrate = bitrate3;
                    samplesPerFrame = frameSize;
                    frameSize = bitrate2;
                }
                header.setValues(version, MIME_TYPE_BY_LAYER[3 - layer], frameSize, sampleRate, ((headerData >> 6) & 3) == 3 ? 1 : 2, bitrate * 1000, samplesPerFrame);
                return true;
            }
        }
        return false;
    }

    private void setValues(int version, String mimeType, int frameSize, int sampleRate, int channels, int bitrate, int samplesPerFrame) {
        this.version = version;
        this.mimeType = mimeType;
        this.frameSize = frameSize;
        this.sampleRate = sampleRate;
        this.channels = channels;
        this.bitrate = bitrate;
        this.samplesPerFrame = samplesPerFrame;
    }
}
