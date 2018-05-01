package org.telegram.messenger.exoplayer2.util;

import android.util.Pair;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.exoplayer2.ParserException;

public final class CodecSpecificDataUtil {
    private static final int AUDIO_OBJECT_TYPE_AAC_LC = 2;
    private static final int AUDIO_OBJECT_TYPE_ER_BSAC = 22;
    private static final int AUDIO_OBJECT_TYPE_ESCAPE = 31;
    private static final int AUDIO_OBJECT_TYPE_PS = 29;
    private static final int AUDIO_OBJECT_TYPE_SBR = 5;
    private static final int AUDIO_SPECIFIC_CONFIG_CHANNEL_CONFIGURATION_INVALID = -1;
    private static final int[] AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE = new int[]{0, 1, 2, 3, 4, 5, 6, 8, -1, -1, -1, 7, 8, -1, 8, -1};
    private static final int AUDIO_SPECIFIC_CONFIG_FREQUENCY_INDEX_ARBITRARY = 15;
    private static final int[] AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE = new int[]{96000, 88200, 64000, 48000, 44100, 32000, 24000, 22050, 16000, 12000, 11025, 8000, 7350};
    private static final byte[] NAL_START_CODE = new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 1};

    private CodecSpecificDataUtil() {
    }

    public static Pair<Integer, Integer> parseAacAudioSpecificConfig(byte[] bArr) throws ParserException {
        return parseAacAudioSpecificConfig(new ParsableBitArray(bArr), null);
    }

    public static Pair<Integer, Integer> parseAacAudioSpecificConfig(ParsableBitArray parsableBitArray, boolean z) throws ParserException {
        boolean aacAudioObjectType = getAacAudioObjectType(parsableBitArray);
        int aacSamplingFrequency = getAacSamplingFrequency(parsableBitArray);
        int readBits = parsableBitArray.readBits(4);
        if (aacAudioObjectType || aacAudioObjectType == AUDIO_OBJECT_TYPE_PS) {
            aacSamplingFrequency = getAacSamplingFrequency(parsableBitArray);
            aacAudioObjectType = getAacAudioObjectType(parsableBitArray);
            if (aacAudioObjectType) {
                readBits = parsableBitArray.readBits(4);
            }
        }
        if (z) {
            if (!aacAudioObjectType) {
                switch (aacAudioObjectType) {
                    case true:
                    case true:
                    case true:
                    case true:
                        break;
                    default:
                        switch (aacAudioObjectType) {
                            case true:
                            case true:
                                break;
                            default:
                                switch (aacAudioObjectType) {
                                    case true:
                                    case true:
                                    case true:
                                    case true:
                                    case true:
                                        break;
                                    default:
                                        z = new StringBuilder();
                                        z.append("Unsupported audio object type: ");
                                        z.append(aacAudioObjectType);
                                        throw new ParserException(z.toString());
                                }
                        }
                }
            }
            parseGaSpecificConfig(parsableBitArray, aacAudioObjectType, readBits);
            switch (aacAudioObjectType) {
                case true:
                case true:
                case true:
                case true:
                case true:
                case true:
                    parsableBitArray = parsableBitArray.readBits(2);
                    if (parsableBitArray == 2 || parsableBitArray == true) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Unsupported epConfig: ");
                        stringBuilder.append(parsableBitArray);
                        throw new ParserException(stringBuilder.toString());
                    }
                default:
                    break;
            }
        }
        parsableBitArray = AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE[readBits];
        Assertions.checkArgument(parsableBitArray != true);
        return Pair.create(Integer.valueOf(aacSamplingFrequency), Integer.valueOf(parsableBitArray));
    }

    public static byte[] buildAacLcAudioSpecificConfig(int i, int i2) {
        int i3;
        int i4 = 0;
        int i5 = -1;
        for (i3 = 0; i3 < AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE.length; i3++) {
            if (i == AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE[i3]) {
                i5 = i3;
            }
        }
        i3 = -1;
        while (i4 < AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE.length) {
            if (i2 == AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE[i4]) {
                i3 = i4;
            }
            i4++;
        }
        if (i != -1) {
            if (i3 != -1) {
                return buildAacAudioSpecificConfig(2, i5, i3);
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Invalid sample rate or number of channels: ");
        stringBuilder.append(i);
        stringBuilder.append(", ");
        stringBuilder.append(i2);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public static byte[] buildAacAudioSpecificConfig(int i, int i2, int i3) {
        return new byte[]{(byte) (((i << 3) & 248) | ((i2 >> 1) & 7)), (byte) (((i2 << 7) & 128) | ((i3 << 3) & 120))};
    }

    public static byte[] buildNalUnit(byte[] bArr, int i, int i2) {
        Object obj = new byte[(NAL_START_CODE.length + i2)];
        System.arraycopy(NAL_START_CODE, 0, obj, 0, NAL_START_CODE.length);
        System.arraycopy(bArr, i, obj, NAL_START_CODE.length, i2);
        return obj;
    }

    public static byte[][] splitNalUnits(byte[] bArr) {
        if (!isNalStartCode(bArr, 0)) {
            return (byte[][]) null;
        }
        List arrayList = new ArrayList();
        int i = 0;
        do {
            arrayList.add(Integer.valueOf(i));
            i = findNalStartCode(bArr, i + NAL_START_CODE.length);
        } while (i != -1);
        byte[][] bArr2 = new byte[arrayList.size()][];
        int i2 = 0;
        while (i2 < arrayList.size()) {
            int intValue = ((Integer) arrayList.get(i2)).intValue();
            Object obj = new byte[((i2 < arrayList.size() + -1 ? ((Integer) arrayList.get(i2 + 1)).intValue() : bArr.length) - intValue)];
            System.arraycopy(bArr, intValue, obj, 0, obj.length);
            bArr2[i2] = obj;
            i2++;
        }
        return bArr2;
    }

    private static int findNalStartCode(byte[] bArr, int i) {
        int length = bArr.length - NAL_START_CODE.length;
        while (i <= length) {
            if (isNalStartCode(bArr, i)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    private static boolean isNalStartCode(byte[] bArr, int i) {
        if (bArr.length - i <= NAL_START_CODE.length) {
            return false;
        }
        for (int i2 = 0; i2 < NAL_START_CODE.length; i2++) {
            if (bArr[i + i2] != NAL_START_CODE[i2]) {
                return false;
            }
        }
        return 1;
    }

    private static int getAacAudioObjectType(ParsableBitArray parsableBitArray) {
        int readBits = parsableBitArray.readBits(5);
        return readBits == AUDIO_OBJECT_TYPE_ESCAPE ? 32 + parsableBitArray.readBits(6) : readBits;
    }

    private static int getAacSamplingFrequency(ParsableBitArray parsableBitArray) {
        int readBits = parsableBitArray.readBits(4);
        if (readBits == 15) {
            return parsableBitArray.readBits(24);
        }
        Assertions.checkArgument(readBits < 13 ? true : null);
        return AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE[readBits];
    }

    private static void parseGaSpecificConfig(ParsableBitArray parsableBitArray, int i, int i2) {
        parsableBitArray.skipBits(1);
        if (parsableBitArray.readBit()) {
            parsableBitArray.skipBits(14);
        }
        boolean readBit = parsableBitArray.readBit();
        if (i2 == 0) {
            throw new UnsupportedOperationException();
        }
        if (i == 6 || i == 20) {
            parsableBitArray.skipBits(3);
        }
        if (readBit) {
            if (i == 22) {
                parsableBitArray.skipBits(16);
            }
            if (i == 17 || i == 19 || i == 20 || i == 23) {
                parsableBitArray.skipBits(3);
            }
            parsableBitArray.skipBits(1);
        }
    }
}
