package org.telegram.messenger.exoplayer2.util;

import android.util.Log;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;

public final class NalUnitUtil {
    public static final float[] ASPECT_RATIO_IDC_VALUES = new float[]{1.0f, 1.0f, 1.0909091f, 0.90909094f, 1.4545455f, 1.2121212f, 2.1818182f, 1.8181819f, 2.909091f, 2.4242425f, 1.6363636f, 1.3636364f, 1.939394f, 1.6161616f, 1.3333334f, 1.5f, 2.0f};
    public static final int EXTENDED_SAR = 255;
    private static final int H264_NAL_UNIT_TYPE_SEI = 6;
    private static final int H264_NAL_UNIT_TYPE_SPS = 7;
    private static final int H265_NAL_UNIT_TYPE_PREFIX_SEI = 39;
    public static final byte[] NAL_START_CODE = new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 1};
    private static final String TAG = "NalUnitUtil";
    private static int[] scratchEscapePositions = new int[10];
    private static final Object scratchEscapePositionsLock = new Object();

    public static final class PpsData {
        public final boolean bottomFieldPicOrderInFramePresentFlag;
        public final int picParameterSetId;
        public final int seqParameterSetId;

        public PpsData(int picParameterSetId, int seqParameterSetId, boolean bottomFieldPicOrderInFramePresentFlag) {
            this.picParameterSetId = picParameterSetId;
            this.seqParameterSetId = seqParameterSetId;
            this.bottomFieldPicOrderInFramePresentFlag = bottomFieldPicOrderInFramePresentFlag;
        }
    }

    public static final class SpsData {
        public final boolean deltaPicOrderAlwaysZeroFlag;
        public final boolean frameMbsOnlyFlag;
        public final int frameNumLength;
        public final int height;
        public final int picOrderCntLsbLength;
        public final int picOrderCountType;
        public final float pixelWidthAspectRatio;
        public final boolean separateColorPlaneFlag;
        public final int seqParameterSetId;
        public final int width;

        public SpsData(int seqParameterSetId, int width, int height, float pixelWidthAspectRatio, boolean separateColorPlaneFlag, boolean frameMbsOnlyFlag, int frameNumLength, int picOrderCountType, int picOrderCntLsbLength, boolean deltaPicOrderAlwaysZeroFlag) {
            this.seqParameterSetId = seqParameterSetId;
            this.width = width;
            this.height = height;
            this.pixelWidthAspectRatio = pixelWidthAspectRatio;
            this.separateColorPlaneFlag = separateColorPlaneFlag;
            this.frameMbsOnlyFlag = frameMbsOnlyFlag;
            this.frameNumLength = frameNumLength;
            this.picOrderCountType = picOrderCountType;
            this.picOrderCntLsbLength = picOrderCntLsbLength;
            this.deltaPicOrderAlwaysZeroFlag = deltaPicOrderAlwaysZeroFlag;
        }
    }

    public static int unescapeStream(byte[] data, int limit) {
        int unescapedLength;
        synchronized (scratchEscapePositionsLock) {
            int position = 0;
            int scratchEscapeCount = 0;
            while (position < limit) {
                position = findNextUnescapeIndex(data, position, limit);
                if (position < limit) {
                    if (scratchEscapePositions.length <= scratchEscapeCount) {
                        scratchEscapePositions = Arrays.copyOf(scratchEscapePositions, scratchEscapePositions.length * 2);
                    }
                    int scratchEscapeCount2 = scratchEscapeCount + 1;
                    scratchEscapePositions[scratchEscapeCount] = position;
                    position += 3;
                    scratchEscapeCount = scratchEscapeCount2;
                }
            }
            unescapedLength = limit - scratchEscapeCount;
            int unescapedPosition = 0;
            int escapedPosition = 0;
            for (scratchEscapeCount2 = 0; scratchEscapeCount2 < scratchEscapeCount; scratchEscapeCount2++) {
                int copyLength = scratchEscapePositions[scratchEscapeCount2] - escapedPosition;
                System.arraycopy(data, escapedPosition, data, unescapedPosition, copyLength);
                unescapedPosition += copyLength;
                int unescapedPosition2 = unescapedPosition + 1;
                data[unescapedPosition] = (byte) 0;
                unescapedPosition = unescapedPosition2 + 1;
                data[unescapedPosition2] = (byte) 0;
                escapedPosition += copyLength + 3;
            }
            System.arraycopy(data, escapedPosition, data, unescapedPosition, unescapedLength - unescapedPosition);
        }
        return unescapedLength;
    }

    public static void discardToSps(ByteBuffer data) {
        int length = data.position();
        int consecutiveZeros = 0;
        int offset = 0;
        while (offset + 1 < length) {
            int value = data.get(offset) & 255;
            if (consecutiveZeros == 3) {
                if (value == 1 && (data.get(offset + 1) & 31) == 7) {
                    ByteBuffer offsetData = data.duplicate();
                    offsetData.position(offset - 3);
                    offsetData.limit(length);
                    data.position(0);
                    data.put(offsetData);
                    return;
                }
            } else if (value == 0) {
                consecutiveZeros++;
            }
            if (value != 0) {
                consecutiveZeros = 0;
            }
            offset++;
        }
        data.clear();
    }

    public static boolean isNalUnitSei(String mimeType, byte nalUnitHeaderFirstByte) {
        if (("video/avc".equals(mimeType) && (nalUnitHeaderFirstByte & 31) == 6) || (MimeTypes.VIDEO_H265.equals(mimeType) && ((nalUnitHeaderFirstByte & 126) >> 1) == H265_NAL_UNIT_TYPE_PREFIX_SEI)) {
            return true;
        }
        return false;
    }

    public static int getNalUnitType(byte[] data, int offset) {
        return data[offset + 3] & 31;
    }

    public static int getH265NalUnitType(byte[] data, int offset) {
        return (data[offset + 3] & 126) >> 1;
    }

    public static SpsData parseSpsNalUnit(byte[] nalData, int nalOffset, int nalLimit) {
        int limit;
        int i;
        boolean deltaPicOrderAlwaysZeroFlag;
        int picOrderCntLsbLength;
        int cropUnitX;
        ParsableNalUnitBitArray data = new ParsableNalUnitBitArray(nalData, nalOffset, nalLimit);
        data.skipBits(8);
        int profileIdc = data.readBits(8);
        data.skipBits(16);
        int seqParameterSetId = data.readUnsignedExpGolombCodedInt();
        int chromaFormatIdc = 1;
        boolean separateColorPlaneFlag = false;
        int i2 = 0;
        if (profileIdc == 100 || profileIdc == 110 || profileIdc == 122 || profileIdc == 244 || profileIdc == 44 || profileIdc == 83 || profileIdc == 86 || profileIdc == 118 || profileIdc == 128 || profileIdc == TsExtractor.TS_STREAM_TYPE_DTS) {
            chromaFormatIdc = data.readUnsignedExpGolombCodedInt();
            if (chromaFormatIdc == 3) {
                separateColorPlaneFlag = data.readBit();
            }
            data.readUnsignedExpGolombCodedInt();
            data.readUnsignedExpGolombCodedInt();
            data.skipBit();
            if (data.readBit()) {
                limit = chromaFormatIdc != 3 ? 8 : 12;
                i = 0;
                while (i < limit) {
                    if (data.readBit()) {
                        skipScalingList(data, i < 6 ? 16 : 64);
                    }
                    i++;
                }
            }
        }
        int chromaFormatIdc2 = chromaFormatIdc;
        boolean separateColorPlaneFlag2 = separateColorPlaneFlag;
        int frameNumLength = data.readUnsignedExpGolombCodedInt() + 4;
        int picOrderCntType = data.readUnsignedExpGolombCodedInt();
        chromaFormatIdc = 0;
        int subHeightC = 1;
        if (picOrderCntType == 0) {
            deltaPicOrderAlwaysZeroFlag = false;
            picOrderCntLsbLength = data.readUnsignedExpGolombCodedInt() + 4;
        } else if (picOrderCntType == 1) {
            separateColorPlaneFlag = data.readBit();
            data.readSignedExpGolombCodedInt();
            data.readSignedExpGolombCodedInt();
            long numRefFramesInPicOrderCntCycle = (long) data.readUnsignedExpGolombCodedInt();
            while (true) {
                picOrderCntLsbLength = chromaFormatIdc;
                if (((long) i2) >= numRefFramesInPicOrderCntCycle) {
                    break;
                }
                data.readUnsignedExpGolombCodedInt();
                i2++;
                chromaFormatIdc = picOrderCntLsbLength;
            }
            deltaPicOrderAlwaysZeroFlag = separateColorPlaneFlag;
        } else {
            picOrderCntLsbLength = 0;
            deltaPicOrderAlwaysZeroFlag = false;
        }
        data.readUnsignedExpGolombCodedInt();
        data.skipBit();
        int picWidthInMbs = data.readUnsignedExpGolombCodedInt() + 1;
        int picHeightInMapUnits = data.readUnsignedExpGolombCodedInt() + 1;
        boolean frameMbsOnlyFlag = data.readBit();
        int frameHeightInMbs = (2 - frameMbsOnlyFlag) * picHeightInMapUnits;
        if (!frameMbsOnlyFlag) {
            data.skipBit();
        }
        data.skipBit();
        int frameWidth = picWidthInMbs * 16;
        i2 = frameHeightInMbs * 16;
        if (data.readBit()) {
            limit = data.readUnsignedExpGolombCodedInt();
            i = data.readUnsignedExpGolombCodedInt();
            int frameCropTopOffset = data.readUnsignedExpGolombCodedInt();
            int frameCropBottomOffset = data.readUnsignedExpGolombCodedInt();
            if (chromaFormatIdc2 == 0) {
                chromaFormatIdc = 2 - frameMbsOnlyFlag;
                cropUnitX = 1;
            } else {
                int subWidthC = chromaFormatIdc2 == 3 ? 1 : 2;
                if (chromaFormatIdc2 == 1) {
                    subHeightC = 2;
                }
                cropUnitX = subWidthC;
                chromaFormatIdc = (2 - frameMbsOnlyFlag) * subHeightC;
            }
            frameWidth -= (limit + i) * cropUnitX;
            i2 -= (frameCropTopOffset + frameCropBottomOffset) * chromaFormatIdc;
        }
        cropUnitX = frameWidth;
        int frameHeight = i2;
        float pixelWidthHeightRatio = 1.0f;
        if (data.readBit() && data.readBit()) {
            int aspectRatioIdc = data.readBits(8);
            if (aspectRatioIdc == 255) {
                subWidthC = data.readBits(16);
                i2 = data.readBits(16);
                if (!(subWidthC == 0 || i2 == 0)) {
                    pixelWidthHeightRatio = ((float) subWidthC) / ((float) i2);
                }
            } else if (aspectRatioIdc < ASPECT_RATIO_IDC_VALUES.length) {
                pixelWidthHeightRatio = ASPECT_RATIO_IDC_VALUES[aspectRatioIdc];
            } else {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unexpected aspect_ratio_idc value: ");
                stringBuilder.append(aspectRatioIdc);
                Log.w(str, stringBuilder.toString());
            }
        }
        return new SpsData(seqParameterSetId, cropUnitX, frameHeight, pixelWidthHeightRatio, separateColorPlaneFlag2, frameMbsOnlyFlag, frameNumLength, picOrderCntType, picOrderCntLsbLength, deltaPicOrderAlwaysZeroFlag);
    }

    public static PpsData parsePpsNalUnit(byte[] nalData, int nalOffset, int nalLimit) {
        ParsableNalUnitBitArray data = new ParsableNalUnitBitArray(nalData, nalOffset, nalLimit);
        data.skipBits(8);
        int picParameterSetId = data.readUnsignedExpGolombCodedInt();
        int seqParameterSetId = data.readUnsignedExpGolombCodedInt();
        data.skipBit();
        return new PpsData(picParameterSetId, seqParameterSetId, data.readBit());
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int findNalUnit(byte[] data, int startOffset, int endOffset, boolean[] prefixFlags) {
        int length = endOffset - startOffset;
        boolean z = false;
        Assertions.checkState(length >= 0);
        if (length == 0) {
            return endOffset;
        }
        if (prefixFlags != null) {
            if (prefixFlags[0]) {
                clearPrefixFlags(prefixFlags);
                return startOffset - 3;
            } else if (length > 1 && prefixFlags[1] && data[startOffset] == (byte) 1) {
                clearPrefixFlags(prefixFlags);
                return startOffset - 2;
            } else if (length > 2 && prefixFlags[2] && data[startOffset] == (byte) 0 && data[startOffset + 1] == (byte) 1) {
                clearPrefixFlags(prefixFlags);
                return startOffset - 1;
            }
        }
        int limit = endOffset - 1;
        int i = startOffset + 2;
        while (i < limit) {
            if ((data[i] & 254) == 0) {
                if (data[i - 2] == (byte) 0 && data[i - 1] == (byte) 0 && data[i] == (byte) 1) {
                    if (prefixFlags != null) {
                        clearPrefixFlags(prefixFlags);
                    }
                    return i - 2;
                }
                i -= 2;
            }
            i += 3;
        }
        if (prefixFlags != null) {
            boolean z2;
            if (length > 2) {
                if (data[endOffset - 3] == (byte) 0) {
                    if (data[endOffset - 2] == (byte) 0) {
                    }
                }
                z2 = false;
                prefixFlags[0] = z2;
                if (length <= 1) {
                    if (data[endOffset - 2] == (byte) 0) {
                    }
                    z2 = false;
                    prefixFlags[1] = z2;
                    if (data[endOffset - 1] == (byte) 0) {
                        z = true;
                    }
                    prefixFlags[2] = z;
                } else {
                    if (prefixFlags[2] && data[endOffset - 1] == (byte) 0) {
                    }
                    z2 = false;
                    prefixFlags[1] = z2;
                    if (data[endOffset - 1] == (byte) 0) {
                        z = true;
                    }
                    prefixFlags[2] = z;
                }
                z2 = true;
                prefixFlags[1] = z2;
                if (data[endOffset - 1] == (byte) 0) {
                    z = true;
                }
                prefixFlags[2] = z;
            } else if (length == 2) {
                if (prefixFlags[2] && data[endOffset - 2] == (byte) 0 && data[endOffset - 1] == (byte) 1) {
                }
                z2 = false;
                prefixFlags[0] = z2;
                if (length <= 1) {
                    if (data[endOffset - 2] == (byte) 0) {
                    }
                    z2 = false;
                    prefixFlags[1] = z2;
                    if (data[endOffset - 1] == (byte) 0) {
                        z = true;
                    }
                    prefixFlags[2] = z;
                }
                z2 = true;
                prefixFlags[1] = z2;
                if (data[endOffset - 1] == (byte) 0) {
                    z = true;
                }
                prefixFlags[2] = z;
            } else {
                if (prefixFlags[1] && data[endOffset - 1] == (byte) 1) {
                }
                z2 = false;
                prefixFlags[0] = z2;
                if (length <= 1) {
                    if (data[endOffset - 2] == (byte) 0) {
                    }
                    z2 = false;
                    prefixFlags[1] = z2;
                    if (data[endOffset - 1] == (byte) 0) {
                        z = true;
                    }
                    prefixFlags[2] = z;
                }
                z2 = true;
                prefixFlags[1] = z2;
                if (data[endOffset - 1] == (byte) 0) {
                    z = true;
                }
                prefixFlags[2] = z;
            }
            z2 = true;
            prefixFlags[0] = z2;
            if (length <= 1) {
                if (data[endOffset - 2] == (byte) 0) {
                }
                z2 = false;
                prefixFlags[1] = z2;
                if (data[endOffset - 1] == (byte) 0) {
                    z = true;
                }
                prefixFlags[2] = z;
            }
            z2 = true;
            prefixFlags[1] = z2;
            if (data[endOffset - 1] == (byte) 0) {
                z = true;
            }
            prefixFlags[2] = z;
        }
        return endOffset;
    }

    public static void clearPrefixFlags(boolean[] prefixFlags) {
        prefixFlags[0] = false;
        prefixFlags[1] = false;
        prefixFlags[2] = false;
    }

    private static int findNextUnescapeIndex(byte[] bytes, int offset, int limit) {
        int i = offset;
        while (i < limit - 2) {
            if (bytes[i] == (byte) 0 && bytes[i + 1] == (byte) 0 && bytes[i + 2] == (byte) 3) {
                return i;
            }
            i++;
        }
        return limit;
    }

    private static void skipScalingList(ParsableNalUnitBitArray bitArray, int size) {
        int lastScale = 8;
        int nextScale = 8;
        for (int i = 0; i < size; i++) {
            if (nextScale != 0) {
                nextScale = ((lastScale + bitArray.readSignedExpGolombCodedInt()) + 256) % 256;
            }
            lastScale = nextScale == 0 ? lastScale : nextScale;
        }
    }

    private NalUnitUtil() {
    }
}
