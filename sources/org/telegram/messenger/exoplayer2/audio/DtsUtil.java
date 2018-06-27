package org.telegram.messenger.exoplayer2.audio;

import java.nio.ByteBuffer;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.drm.DrmInitData;
import org.telegram.messenger.exoplayer2.extractor.ts.PsExtractor;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.ParsableBitArray;

public final class DtsUtil {
    private static final int[] CHANNELS_BY_AMODE = new int[]{1, 2, 2, 2, 2, 3, 3, 4, 4, 5, 6, 6, 6, 7, 8, 8};
    private static final byte FIRST_BYTE_14B_BE = (byte) 31;
    private static final byte FIRST_BYTE_14B_LE = (byte) -1;
    private static final byte FIRST_BYTE_BE = Byte.MAX_VALUE;
    private static final byte FIRST_BYTE_LE = (byte) -2;
    private static final int[] SAMPLE_RATE_BY_SFREQ = new int[]{-1, 8000, 16000, 32000, -1, -1, 11025, 22050, 44100, -1, -1, 12000, 24000, 48000, -1, -1};
    private static final int SYNC_VALUE_14B_BE = 536864768;
    private static final int SYNC_VALUE_14B_LE = -14745368;
    private static final int SYNC_VALUE_BE = NUM;
    private static final int SYNC_VALUE_LE = -25230976;
    private static final int[] TWICE_BITRATE_KBPS_BY_RATE = new int[]{64, 112, 128, PsExtractor.AUDIO_STREAM, 224, 256, 384, 448, 512, 640, 768, 896, 1024, 1152, 1280, 1536, 1920, 2048, 2304, 2560, 2688, 2816, 2823, 2944, 3072, 3840, 4096, 6144, 7680};

    public static boolean isSyncWord(int word) {
        return word == SYNC_VALUE_BE || word == SYNC_VALUE_LE || word == SYNC_VALUE_14B_BE || word == SYNC_VALUE_14B_LE;
    }

    public static Format parseDtsFormat(byte[] frame, String trackId, String language, DrmInitData drmInitData) {
        ParsableBitArray frameBits = getNormalizedFrameHeader(frame);
        frameBits.skipBits(60);
        int channelCount = CHANNELS_BY_AMODE[frameBits.readBits(6)];
        int sampleRate = SAMPLE_RATE_BY_SFREQ[frameBits.readBits(4)];
        int rate = frameBits.readBits(5);
        int bitrate = rate >= TWICE_BITRATE_KBPS_BY_RATE.length ? -1 : (TWICE_BITRATE_KBPS_BY_RATE[rate] * 1000) / 2;
        frameBits.skipBits(10);
        return Format.createAudioSampleFormat(trackId, MimeTypes.AUDIO_DTS, null, bitrate, -1, channelCount + (frameBits.readBits(2) > 0 ? 1 : 0), sampleRate, null, drmInitData, 0, language);
    }

    public static int parseDtsAudioSampleCount(byte[] data) {
        int nblks;
        switch (data[0]) {
            case (byte) -2:
                nblks = ((data[5] & 1) << 6) | ((data[4] & 252) >> 2);
                break;
            case (byte) -1:
                nblks = ((data[4] & 7) << 4) | ((data[7] & 60) >> 2);
                break;
            case (byte) 31:
                nblks = ((data[5] & 7) << 4) | ((data[6] & 60) >> 2);
                break;
            default:
                nblks = ((data[4] & 1) << 6) | ((data[5] & 252) >> 2);
                break;
        }
        return (nblks + 1) * 32;
    }

    public static int parseDtsAudioSampleCount(ByteBuffer buffer) {
        int nblks;
        int position = buffer.position();
        switch (buffer.get(position)) {
            case (byte) -2:
                nblks = ((buffer.get(position + 5) & 1) << 6) | ((buffer.get(position + 4) & 252) >> 2);
                break;
            case (byte) -1:
                nblks = ((buffer.get(position + 4) & 7) << 4) | ((buffer.get(position + 7) & 60) >> 2);
                break;
            case (byte) 31:
                nblks = ((buffer.get(position + 5) & 7) << 4) | ((buffer.get(position + 6) & 60) >> 2);
                break;
            default:
                nblks = ((buffer.get(position + 4) & 1) << 6) | ((buffer.get(position + 5) & 252) >> 2);
                break;
        }
        return (nblks + 1) * 32;
    }

    public static int getDtsFrameSize(byte[] data) {
        int fsize;
        boolean uses14BitPerWord = false;
        switch (data[0]) {
            case (byte) -2:
                fsize = ((((data[4] & 3) << 12) | ((data[7] & 255) << 4)) | ((data[6] & PsExtractor.VIDEO_STREAM_MASK) >> 4)) + 1;
                break;
            case (byte) -1:
                fsize = ((((data[7] & 3) << 12) | ((data[6] & 255) << 4)) | ((data[9] & 60) >> 2)) + 1;
                uses14BitPerWord = true;
                break;
            case (byte) 31:
                fsize = ((((data[6] & 3) << 12) | ((data[7] & 255) << 4)) | ((data[8] & 60) >> 2)) + 1;
                uses14BitPerWord = true;
                break;
            default:
                fsize = ((((data[5] & 3) << 12) | ((data[6] & 255) << 4)) | ((data[7] & PsExtractor.VIDEO_STREAM_MASK) >> 4)) + 1;
                break;
        }
        return uses14BitPerWord ? (fsize * 16) / 14 : fsize;
    }

    private static ParsableBitArray getNormalizedFrameHeader(byte[] frameHeader) {
        if (frameHeader[0] == FIRST_BYTE_BE) {
            return new ParsableBitArray(frameHeader);
        }
        frameHeader = Arrays.copyOf(frameHeader, frameHeader.length);
        if (isLittleEndianFrameHeader(frameHeader)) {
            for (int i = 0; i < frameHeader.length - 1; i += 2) {
                byte temp = frameHeader[i];
                frameHeader[i] = frameHeader[i + 1];
                frameHeader[i + 1] = temp;
            }
        }
        ParsableBitArray frameBits = new ParsableBitArray(frameHeader);
        if (frameHeader[0] == FIRST_BYTE_14B_BE) {
            ParsableBitArray scratchBits = new ParsableBitArray(frameHeader);
            while (scratchBits.bitsLeft() >= 16) {
                scratchBits.skipBits(2);
                frameBits.putInt(scratchBits.readBits(14), 14);
            }
        }
        frameBits.reset(frameHeader);
        return frameBits;
    }

    private static boolean isLittleEndianFrameHeader(byte[] frameHeader) {
        return frameHeader[0] == FIRST_BYTE_LE || frameHeader[0] == FIRST_BYTE_14B_LE;
    }

    private DtsUtil() {
    }
}
