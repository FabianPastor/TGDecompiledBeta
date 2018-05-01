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

        public PpsData(int i, int i2, boolean z) {
            this.picParameterSetId = i;
            this.seqParameterSetId = i2;
            this.bottomFieldPicOrderInFramePresentFlag = z;
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

        public SpsData(int i, int i2, int i3, float f, boolean z, boolean z2, int i4, int i5, int i6, boolean z3) {
            this.seqParameterSetId = i;
            this.width = i2;
            this.height = i3;
            this.pixelWidthAspectRatio = f;
            this.separateColorPlaneFlag = z;
            this.frameMbsOnlyFlag = z2;
            this.frameNumLength = i4;
            this.picOrderCountType = i5;
            this.picOrderCntLsbLength = i6;
            this.deltaPicOrderAlwaysZeroFlag = z3;
        }
    }

    public static int unescapeStream(byte[] bArr, int i) {
        synchronized (scratchEscapePositionsLock) {
            int i2;
            int i3 = 0;
            int i4 = i3;
            while (i3 < i) {
                i3 = findNextUnescapeIndex(bArr, i3, i);
                if (i3 < i) {
                    if (scratchEscapePositions.length <= i4) {
                        scratchEscapePositions = Arrays.copyOf(scratchEscapePositions, scratchEscapePositions.length * 2);
                    }
                    i2 = i4 + 1;
                    scratchEscapePositions[i4] = i3;
                    i3 += 3;
                    i4 = i2;
                }
            }
            i -= i4;
            i3 = 0;
            int i5 = i3;
            i2 = i5;
            while (i3 < i4) {
                int i6 = scratchEscapePositions[i3] - i2;
                System.arraycopy(bArr, i2, bArr, i5, i6);
                i5 += i6;
                int i7 = i5 + 1;
                bArr[i5] = (byte) 0;
                i5 = i7 + 1;
                bArr[i7] = (byte) 0;
                i2 += i6 + 3;
                i3++;
            }
            System.arraycopy(bArr, i2, bArr, i5, i - i5);
        }
        return i;
    }

    public static void discardToSps(ByteBuffer byteBuffer) {
        int position = byteBuffer.position();
        int i = 0;
        int i2 = i;
        while (true) {
            int i3 = i + 1;
            if (i3 < position) {
                int i4 = byteBuffer.get(i) & 255;
                if (i2 == 3) {
                    if (i4 == 1 && (byteBuffer.get(i3) & 31) == 7) {
                        ByteBuffer duplicate = byteBuffer.duplicate();
                        duplicate.position(i - 3);
                        duplicate.limit(position);
                        byteBuffer.position(0);
                        byteBuffer.put(duplicate);
                        return;
                    }
                } else if (i4 == 0) {
                    i2++;
                }
                if (i4 != 0) {
                    i2 = 0;
                }
                i = i3;
            } else {
                byteBuffer.clear();
                return;
            }
        }
    }

    public static boolean isNalUnitSei(String str, byte b) {
        if ("video/avc".equals(str) && (b & 31) == 6) {
            return true;
        }
        if (MimeTypes.VIDEO_H265.equals(str) == null || ((b & 126) >> 1) != (byte) 39) {
            return false;
        }
        return true;
    }

    public static int getNalUnitType(byte[] bArr, int i) {
        return bArr[i + 3] & 31;
    }

    public static int getH265NalUnitType(byte[] bArr, int i) {
        return (bArr[i + 3] & 126) >> 1;
    }

    public static SpsData parseSpsNalUnit(byte[] bArr, int i, int i2) {
        boolean z;
        int readUnsignedExpGolombCodedInt;
        int readUnsignedExpGolombCodedInt2;
        int i3;
        boolean z2;
        int readUnsignedExpGolombCodedInt3;
        boolean readBit;
        long readUnsignedExpGolombCodedInt4;
        int i4;
        int readUnsignedExpGolombCodedInt5;
        boolean readBit2;
        int i5;
        int i6;
        int i7;
        float f;
        int readBits;
        int readBits2;
        float f2;
        ParsableNalUnitBitArray parsableNalUnitBitArray = new ParsableNalUnitBitArray(bArr, i, i2);
        parsableNalUnitBitArray.skipBits(8);
        int readBits3 = parsableNalUnitBitArray.readBits(8);
        parsableNalUnitBitArray.skipBits(16);
        int readUnsignedExpGolombCodedInt6 = parsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
        int i8 = 1;
        if (!(readBits3 == 100 || readBits3 == 110 || readBits3 == 122 || readBits3 == 244 || readBits3 == 44 || readBits3 == 83 || readBits3 == 86 || readBits3 == 118 || readBits3 == 128)) {
            if (readBits3 != TsExtractor.TS_STREAM_TYPE_DTS) {
                z = false;
                readBits3 = 1;
                readUnsignedExpGolombCodedInt = parsableNalUnitBitArray.readUnsignedExpGolombCodedInt() + 4;
                readUnsignedExpGolombCodedInt2 = parsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
                if (readUnsignedExpGolombCodedInt2 == 0) {
                    i3 = readUnsignedExpGolombCodedInt6;
                    z2 = false;
                    readUnsignedExpGolombCodedInt3 = parsableNalUnitBitArray.readUnsignedExpGolombCodedInt() + 4;
                } else if (readUnsignedExpGolombCodedInt2 != 1) {
                    readBit = parsableNalUnitBitArray.readBit();
                    parsableNalUnitBitArray.readSignedExpGolombCodedInt();
                    parsableNalUnitBitArray.readSignedExpGolombCodedInt();
                    readUnsignedExpGolombCodedInt4 = (long) parsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
                    i3 = readUnsignedExpGolombCodedInt6;
                    for (i4 = 0; ((long) i4) < readUnsignedExpGolombCodedInt4; i4++) {
                        parsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
                    }
                    readUnsignedExpGolombCodedInt3 = 0;
                    z2 = readBit;
                } else {
                    i3 = readUnsignedExpGolombCodedInt6;
                    readUnsignedExpGolombCodedInt3 = 0;
                    z2 = readUnsignedExpGolombCodedInt3;
                }
                parsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
                parsableNalUnitBitArray.skipBit();
                readUnsignedExpGolombCodedInt5 = parsableNalUnitBitArray.readUnsignedExpGolombCodedInt() + 1;
                readUnsignedExpGolombCodedInt6 = parsableNalUnitBitArray.readUnsignedExpGolombCodedInt() + 1;
                readBit2 = parsableNalUnitBitArray.readBit();
                i5 = (2 - readBit2) * readUnsignedExpGolombCodedInt6;
                if (!readBit2) {
                    parsableNalUnitBitArray.skipBit();
                }
                parsableNalUnitBitArray.skipBit();
                readUnsignedExpGolombCodedInt5 *= 16;
                i5 *= 16;
                if (parsableNalUnitBitArray.readBit()) {
                    readUnsignedExpGolombCodedInt6 = parsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
                    int readUnsignedExpGolombCodedInt7 = parsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
                    int readUnsignedExpGolombCodedInt8 = parsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
                    int readUnsignedExpGolombCodedInt9 = parsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
                    if (readBits3 != 0) {
                        i6 = 2 - readBit2;
                        i7 = 1;
                    } else {
                        i7 = readBits3 != 3 ? 1 : 2;
                        if (readBits3 == 1) {
                            i8 = 2;
                        }
                        i6 = (2 - readBit2) * i8;
                    }
                    readUnsignedExpGolombCodedInt5 -= (readUnsignedExpGolombCodedInt6 + readUnsignedExpGolombCodedInt7) * i7;
                    i5 -= (readUnsignedExpGolombCodedInt8 + readUnsignedExpGolombCodedInt9) * i6;
                }
                i6 = readUnsignedExpGolombCodedInt5;
                i8 = i5;
                f = 1.0f;
                if (parsableNalUnitBitArray.readBit() && parsableNalUnitBitArray.readBit()) {
                    readBits = parsableNalUnitBitArray.readBits(8);
                    if (readBits == 255) {
                        readBits = parsableNalUnitBitArray.readBits(16);
                        readBits2 = parsableNalUnitBitArray.readBits(16);
                        if (!(readBits == 0 || readBits2 == 0)) {
                            f = ((float) readBits) / ((float) readBits2);
                        }
                    } else if (readBits >= ASPECT_RATIO_IDC_VALUES.length) {
                        f2 = ASPECT_RATIO_IDC_VALUES[readBits];
                        return new SpsData(i3, i6, i8, f2, z, readBit2, readUnsignedExpGolombCodedInt, readUnsignedExpGolombCodedInt2, readUnsignedExpGolombCodedInt3, z2);
                    } else {
                        String str = TAG;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Unexpected aspect_ratio_idc value: ");
                        stringBuilder.append(readBits);
                        Log.w(str, stringBuilder.toString());
                    }
                }
                f2 = f;
                return new SpsData(i3, i6, i8, f2, z, readBit2, readUnsignedExpGolombCodedInt, readUnsignedExpGolombCodedInt2, readUnsignedExpGolombCodedInt3, z2);
            }
        }
        readBits3 = parsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
        readBit = readBits3 == 3 ? parsableNalUnitBitArray.readBit() : false;
        parsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
        parsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
        parsableNalUnitBitArray.skipBit();
        if (parsableNalUnitBitArray.readBit()) {
            int i9 = readBits3 != 3 ? 8 : 12;
            i4 = 0;
            while (i4 < i9) {
                if (parsableNalUnitBitArray.readBit()) {
                    skipScalingList(parsableNalUnitBitArray, i4 < 6 ? 16 : 64);
                }
                i4++;
            }
        }
        z = readBit;
        readUnsignedExpGolombCodedInt = parsableNalUnitBitArray.readUnsignedExpGolombCodedInt() + 4;
        readUnsignedExpGolombCodedInt2 = parsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
        if (readUnsignedExpGolombCodedInt2 == 0) {
            i3 = readUnsignedExpGolombCodedInt6;
            z2 = false;
            readUnsignedExpGolombCodedInt3 = parsableNalUnitBitArray.readUnsignedExpGolombCodedInt() + 4;
        } else if (readUnsignedExpGolombCodedInt2 != 1) {
            i3 = readUnsignedExpGolombCodedInt6;
            readUnsignedExpGolombCodedInt3 = 0;
            z2 = readUnsignedExpGolombCodedInt3;
        } else {
            readBit = parsableNalUnitBitArray.readBit();
            parsableNalUnitBitArray.readSignedExpGolombCodedInt();
            parsableNalUnitBitArray.readSignedExpGolombCodedInt();
            readUnsignedExpGolombCodedInt4 = (long) parsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
            i3 = readUnsignedExpGolombCodedInt6;
            for (i4 = 0; ((long) i4) < readUnsignedExpGolombCodedInt4; i4++) {
                parsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
            }
            readUnsignedExpGolombCodedInt3 = 0;
            z2 = readBit;
        }
        parsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
        parsableNalUnitBitArray.skipBit();
        readUnsignedExpGolombCodedInt5 = parsableNalUnitBitArray.readUnsignedExpGolombCodedInt() + 1;
        readUnsignedExpGolombCodedInt6 = parsableNalUnitBitArray.readUnsignedExpGolombCodedInt() + 1;
        readBit2 = parsableNalUnitBitArray.readBit();
        i5 = (2 - readBit2) * readUnsignedExpGolombCodedInt6;
        if (readBit2) {
            parsableNalUnitBitArray.skipBit();
        }
        parsableNalUnitBitArray.skipBit();
        readUnsignedExpGolombCodedInt5 *= 16;
        i5 *= 16;
        if (parsableNalUnitBitArray.readBit()) {
            readUnsignedExpGolombCodedInt6 = parsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
            int readUnsignedExpGolombCodedInt72 = parsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
            int readUnsignedExpGolombCodedInt82 = parsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
            int readUnsignedExpGolombCodedInt92 = parsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
            if (readBits3 != 0) {
                if (readBits3 != 3) {
                }
                if (readBits3 == 1) {
                    i8 = 2;
                }
                i6 = (2 - readBit2) * i8;
            } else {
                i6 = 2 - readBit2;
                i7 = 1;
            }
            readUnsignedExpGolombCodedInt5 -= (readUnsignedExpGolombCodedInt6 + readUnsignedExpGolombCodedInt72) * i7;
            i5 -= (readUnsignedExpGolombCodedInt82 + readUnsignedExpGolombCodedInt92) * i6;
        }
        i6 = readUnsignedExpGolombCodedInt5;
        i8 = i5;
        f = 1.0f;
        readBits = parsableNalUnitBitArray.readBits(8);
        if (readBits == 255) {
            readBits = parsableNalUnitBitArray.readBits(16);
            readBits2 = parsableNalUnitBitArray.readBits(16);
            f = ((float) readBits) / ((float) readBits2);
        } else if (readBits >= ASPECT_RATIO_IDC_VALUES.length) {
            String str2 = TAG;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Unexpected aspect_ratio_idc value: ");
            stringBuilder2.append(readBits);
            Log.w(str2, stringBuilder2.toString());
        } else {
            f2 = ASPECT_RATIO_IDC_VALUES[readBits];
            return new SpsData(i3, i6, i8, f2, z, readBit2, readUnsignedExpGolombCodedInt, readUnsignedExpGolombCodedInt2, readUnsignedExpGolombCodedInt3, z2);
        }
        f2 = f;
        return new SpsData(i3, i6, i8, f2, z, readBit2, readUnsignedExpGolombCodedInt, readUnsignedExpGolombCodedInt2, readUnsignedExpGolombCodedInt3, z2);
    }

    public static PpsData parsePpsNalUnit(byte[] bArr, int i, int i2) {
        ParsableNalUnitBitArray parsableNalUnitBitArray = new ParsableNalUnitBitArray(bArr, i, i2);
        parsableNalUnitBitArray.skipBits(8);
        bArr = parsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
        i = parsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
        parsableNalUnitBitArray.skipBit();
        return new PpsData(bArr, i, parsableNalUnitBitArray.readBit());
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int findNalUnit(byte[] bArr, int i, int i2, boolean[] zArr) {
        int i3 = i2 - i;
        boolean z = false;
        Assertions.checkState(i3 >= 0);
        if (i3 == 0) {
            return i2;
        }
        if (zArr != null) {
            if (zArr[0]) {
                clearPrefixFlags(zArr);
                return i - 3;
            } else if (i3 > 1 && zArr[1] && bArr[i] == (byte) 1) {
                clearPrefixFlags(zArr);
                return i - 2;
            } else if (i3 > 2 && zArr[2] && bArr[i] == (byte) 0 && bArr[i + 1] == (byte) 1) {
                clearPrefixFlags(zArr);
                return i - 1;
            }
        }
        int i4 = i2 - 1;
        i += 2;
        while (i < i4) {
            if ((bArr[i] & 254) == 0) {
                int i5 = i - 2;
                if (bArr[i5] == (byte) 0 && bArr[i - 1] == (byte) 0 && bArr[i] == (byte) 1) {
                    if (zArr != null) {
                        clearPrefixFlags(zArr);
                    }
                    return i5;
                }
                i -= 2;
            }
            i += 3;
        }
        if (zArr != null) {
            if (i3 > 2) {
                if (bArr[i2 - 3] == 0) {
                    if (bArr[i2 - 2] == 0) {
                    }
                }
                i = 0;
                zArr[0] = i;
                if (i3 <= 1) {
                    if (bArr[i2 - 2] == 0) {
                    }
                    i = 0;
                    zArr[1] = i;
                    if (bArr[i4] == null) {
                        z = true;
                    }
                    zArr[2] = z;
                } else {
                    if (zArr[2] != 0 && bArr[i4] == 0) {
                    }
                    i = 0;
                    zArr[1] = i;
                    if (bArr[i4] == null) {
                        z = true;
                    }
                    zArr[2] = z;
                }
                i = 1;
                zArr[1] = i;
                if (bArr[i4] == null) {
                    z = true;
                }
                zArr[2] = z;
            } else if (i3 == 2) {
                if (zArr[2] != 0 && bArr[i2 - 2] == 0 && bArr[i4] == 1) {
                }
                i = 0;
                zArr[0] = i;
                if (i3 <= 1) {
                    if (bArr[i2 - 2] == 0) {
                    }
                    i = 0;
                    zArr[1] = i;
                    if (bArr[i4] == null) {
                        z = true;
                    }
                    zArr[2] = z;
                }
                i = 1;
                zArr[1] = i;
                if (bArr[i4] == null) {
                    z = true;
                }
                zArr[2] = z;
            } else {
                if (zArr[1] != 0 && bArr[i4] == 1) {
                }
                i = 0;
                zArr[0] = i;
                if (i3 <= 1) {
                    if (bArr[i2 - 2] == 0) {
                    }
                    i = 0;
                    zArr[1] = i;
                    if (bArr[i4] == null) {
                        z = true;
                    }
                    zArr[2] = z;
                }
                i = 1;
                zArr[1] = i;
                if (bArr[i4] == null) {
                    z = true;
                }
                zArr[2] = z;
            }
            i = 1;
            zArr[0] = i;
            if (i3 <= 1) {
                if (bArr[i2 - 2] == 0) {
                }
                i = 0;
                zArr[1] = i;
                if (bArr[i4] == null) {
                    z = true;
                }
                zArr[2] = z;
            }
            i = 1;
            zArr[1] = i;
            if (bArr[i4] == null) {
                z = true;
            }
            zArr[2] = z;
        }
        return i2;
    }

    public static void clearPrefixFlags(boolean[] zArr) {
        zArr[0] = false;
        zArr[1] = false;
        zArr[2] = false;
    }

    private static int findNextUnescapeIndex(byte[] bArr, int i, int i2) {
        while (i < i2 - 2) {
            if (bArr[i] == (byte) 0 && bArr[i + 1] == (byte) 0 && bArr[i + 2] == (byte) 3) {
                return i;
            }
            i++;
        }
        return i2;
    }

    private static void skipScalingList(ParsableNalUnitBitArray parsableNalUnitBitArray, int i) {
        int i2 = 8;
        int i3 = 8;
        for (int i4 = 0; i4 < i; i4++) {
            if (i2 != 0) {
                i2 = ((parsableNalUnitBitArray.readSignedExpGolombCodedInt() + i3) + 256) % 256;
            }
            if (i2 != 0) {
                i3 = i2;
            }
        }
    }

    private NalUnitUtil() {
    }
}
