package org.telegram.messenger.exoplayer2.extractor.ogg;

import android.util.Log;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.extractor.ts.PsExtractor;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

final class VorbisUtil {
    private static final String TAG = "VorbisUtil";

    public static final class CodeBook {
        public final int dimensions;
        public final int entries;
        public final boolean isOrdered;
        public final long[] lengthMap;
        public final int lookupType;

        public CodeBook(int dimensions, int entries, long[] lengthMap, int lookupType, boolean isOrdered) {
            this.dimensions = dimensions;
            this.entries = entries;
            this.lengthMap = lengthMap;
            this.lookupType = lookupType;
            this.isOrdered = isOrdered;
        }
    }

    public static final class CommentHeader {
        public final String[] comments;
        public final int length;
        public final String vendor;

        public CommentHeader(String vendor, String[] comments, int length) {
            this.vendor = vendor;
            this.comments = comments;
            this.length = length;
        }
    }

    public static final class Mode {
        public final boolean blockFlag;
        public final int mapping;
        public final int transformType;
        public final int windowType;

        public Mode(boolean blockFlag, int windowType, int transformType, int mapping) {
            this.blockFlag = blockFlag;
            this.windowType = windowType;
            this.transformType = transformType;
            this.mapping = mapping;
        }
    }

    public static final class VorbisIdHeader {
        public final int bitrateMax;
        public final int bitrateMin;
        public final int bitrateNominal;
        public final int blockSize0;
        public final int blockSize1;
        public final int channels;
        public final byte[] data;
        public final boolean framingFlag;
        public final long sampleRate;
        public final long version;

        public VorbisIdHeader(long version, int channels, long sampleRate, int bitrateMax, int bitrateNominal, int bitrateMin, int blockSize0, int blockSize1, boolean framingFlag, byte[] data) {
            this.version = version;
            this.channels = channels;
            this.sampleRate = sampleRate;
            this.bitrateMax = bitrateMax;
            this.bitrateNominal = bitrateNominal;
            this.bitrateMin = bitrateMin;
            this.blockSize0 = blockSize0;
            this.blockSize1 = blockSize1;
            this.framingFlag = framingFlag;
            this.data = data;
        }

        public int getApproximateBitrate() {
            return this.bitrateNominal == 0 ? (this.bitrateMin + this.bitrateMax) / 2 : this.bitrateNominal;
        }
    }

    VorbisUtil() {
    }

    public static int iLog(int x) {
        int val = 0;
        while (x > 0) {
            val++;
            x >>>= 1;
        }
        return val;
    }

    public static VorbisIdHeader readVorbisIdentificationHeader(ParsableByteArray headerData) throws ParserException {
        ParsableByteArray parsableByteArray = headerData;
        verifyVorbisHeaderCapturePattern(1, parsableByteArray, false);
        long version = headerData.readLittleEndianUnsignedInt();
        int channels = headerData.readUnsignedByte();
        long sampleRate = headerData.readLittleEndianUnsignedInt();
        int bitrateMax = headerData.readLittleEndianInt();
        int bitrateNominal = headerData.readLittleEndianInt();
        int bitrateMin = headerData.readLittleEndianInt();
        int blockSize = headerData.readUnsignedByte();
        int blockSize0 = (int) Math.pow(2.0d, (double) (blockSize & 15));
        return new VorbisIdHeader(version, channels, sampleRate, bitrateMax, bitrateNominal, bitrateMin, blockSize0, (int) Math.pow(2.0d, (double) ((blockSize & PsExtractor.VIDEO_STREAM_MASK) >> 4)), (headerData.readUnsignedByte() & 1) > 0, Arrays.copyOf(parsableByteArray.data, headerData.limit()));
    }

    public static CommentHeader readVorbisCommentHeader(ParsableByteArray headerData) throws ParserException {
        int i = 0;
        verifyVorbisHeaderCapturePattern(3, headerData, false);
        int length = 7 + 4;
        String vendor = headerData.readString((int) headerData.readLittleEndianUnsignedInt());
        length += vendor.length();
        long commentListLen = headerData.readLittleEndianUnsignedInt();
        String[] comments = new String[((int) commentListLen)];
        length += 4;
        while (((long) i) < commentListLen) {
            length += 4;
            comments[i] = headerData.readString((int) headerData.readLittleEndianUnsignedInt());
            length += comments[i].length();
            i++;
        }
        if ((headerData.readUnsignedByte() & 1) != 0) {
            return new CommentHeader(vendor, comments, length + 1);
        }
        throw new ParserException("framing bit expected to be set");
    }

    public static boolean verifyVorbisHeaderCapturePattern(int headerType, ParsableByteArray header, boolean quiet) throws ParserException {
        StringBuilder stringBuilder;
        if (header.bytesLeft() < 7) {
            if (quiet) {
                return false;
            }
            stringBuilder = new StringBuilder();
            stringBuilder.append("too short header: ");
            stringBuilder.append(header.bytesLeft());
            throw new ParserException(stringBuilder.toString());
        } else if (header.readUnsignedByte() == headerType) {
            if (header.readUnsignedByte() == 118 && header.readUnsignedByte() == 111 && header.readUnsignedByte() == 114 && header.readUnsignedByte() == 98 && header.readUnsignedByte() == 105) {
                if (header.readUnsignedByte() == 115) {
                    return true;
                }
            }
            if (quiet) {
                return false;
            }
            throw new ParserException("expected characters 'vorbis'");
        } else if (quiet) {
            return false;
        } else {
            stringBuilder = new StringBuilder();
            stringBuilder.append("expected header type ");
            stringBuilder.append(Integer.toHexString(headerType));
            throw new ParserException(stringBuilder.toString());
        }
    }

    public static Mode[] readVorbisModes(ParsableByteArray headerData, int channels) throws ParserException {
        int i;
        int i2 = 0;
        verifyVorbisHeaderCapturePattern(5, headerData, false);
        int numberOfBooks = headerData.readUnsignedByte() + 1;
        VorbisBitArray bitArray = new VorbisBitArray(headerData.data);
        bitArray.skipBits(headerData.getPosition() * 8);
        for (i = 0; i < numberOfBooks; i++) {
            readBook(bitArray);
        }
        i = bitArray.readBits(6) + 1;
        while (i2 < i) {
            if (bitArray.readBits(16) != 0) {
                throw new ParserException("placeholder of time domain transforms not zeroed out");
            }
            i2++;
        }
        readFloors(bitArray);
        readResidues(bitArray);
        readMappings(channels, bitArray);
        Mode[] modes = readModes(bitArray);
        if (bitArray.readBit()) {
            return modes;
        }
        throw new ParserException("framing bit after modes not set as expected");
    }

    private static Mode[] readModes(VorbisBitArray bitArray) {
        int modeCount = bitArray.readBits(6) + 1;
        Mode[] modes = new Mode[modeCount];
        for (int i = 0; i < modeCount; i++) {
            modes[i] = new Mode(bitArray.readBit(), bitArray.readBits(16), bitArray.readBits(16), bitArray.readBits(8));
        }
        return modes;
    }

    private static void readMappings(int channels, VorbisBitArray bitArray) throws ParserException {
        int mappingsCount = bitArray.readBits(6) + 1;
        for (int i = 0; i < mappingsCount; i++) {
            int mappingType = bitArray.readBits(16);
            if (mappingType != 0) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("mapping type other than 0 not supported: ");
                stringBuilder.append(mappingType);
                Log.e(str, stringBuilder.toString());
            } else {
                int submaps;
                int couplingSteps;
                if (bitArray.readBit()) {
                    submaps = bitArray.readBits(4) + 1;
                } else {
                    submaps = 1;
                }
                if (bitArray.readBit()) {
                    couplingSteps = bitArray.readBits(8) + 1;
                    for (int j = 0; j < couplingSteps; j++) {
                        bitArray.skipBits(iLog(channels - 1));
                        bitArray.skipBits(iLog(channels - 1));
                    }
                }
                if (bitArray.readBits(2) != 0) {
                    throw new ParserException("to reserved bits must be zero after mapping coupling steps");
                }
                if (submaps > 1) {
                    for (couplingSteps = 0; couplingSteps < channels; couplingSteps++) {
                        bitArray.skipBits(4);
                    }
                }
                for (int j2 = 0; j2 < submaps; j2++) {
                    bitArray.skipBits(8);
                    bitArray.skipBits(8);
                    bitArray.skipBits(8);
                }
            }
        }
    }

    private static void readResidues(VorbisBitArray bitArray) throws ParserException {
        int residueCount = bitArray.readBits(6) + 1;
        for (int i = 0; i < residueCount; i++) {
            if (bitArray.readBits(16) > 2) {
                throw new ParserException("residueType greater than 2 is not decodable");
            }
            int j;
            bitArray.skipBits(24);
            bitArray.skipBits(24);
            bitArray.skipBits(24);
            int classifications = bitArray.readBits(6) + 1;
            bitArray.skipBits(8);
            int[] cascade = new int[classifications];
            for (j = 0; j < classifications; j++) {
                int highBits = 0;
                int lowBits = bitArray.readBits(3);
                if (bitArray.readBit()) {
                    highBits = bitArray.readBits(5);
                }
                cascade[j] = (highBits * 8) + lowBits;
            }
            for (j = 0; j < classifications; j++) {
                for (highBits = 0; highBits < 8; highBits++) {
                    if ((cascade[j] & (1 << highBits)) != 0) {
                        bitArray.skipBits(8);
                    }
                }
            }
        }
    }

    private static void readFloors(VorbisBitArray bitArray) throws ParserException {
        VorbisBitArray vorbisBitArray = bitArray;
        int floorCount = vorbisBitArray.readBits(6) + 1;
        for (int i = 0; i < floorCount; i++) {
            int floorType = vorbisBitArray.readBits(16);
            int floorNumberOfBooks;
            int j;
            switch (floorType) {
                case 0:
                    vorbisBitArray.skipBits(8);
                    vorbisBitArray.skipBits(16);
                    vorbisBitArray.skipBits(16);
                    vorbisBitArray.skipBits(6);
                    vorbisBitArray.skipBits(8);
                    floorNumberOfBooks = vorbisBitArray.readBits(4) + 1;
                    for (j = 0; j < floorNumberOfBooks; j++) {
                        vorbisBitArray.skipBits(8);
                    }
                    break;
                case 1:
                    int j2;
                    j = vorbisBitArray.readBits(5);
                    int[] partitionClassList = new int[j];
                    int maximumClass = -1;
                    for (int j3 = 0; j3 < j; j3++) {
                        partitionClassList[j3] = vorbisBitArray.readBits(4);
                        if (partitionClassList[j3] > maximumClass) {
                            maximumClass = partitionClassList[j3];
                        }
                    }
                    int[] classDimensions = new int[(maximumClass + 1)];
                    for (j2 = 0; j2 < classDimensions.length; j2++) {
                        classDimensions[j2] = vorbisBitArray.readBits(3) + 1;
                        int classSubclasses = vorbisBitArray.readBits(2);
                        if (classSubclasses > 0) {
                            vorbisBitArray.skipBits(8);
                        }
                        for (int k = 0; k < (1 << classSubclasses); k++) {
                            vorbisBitArray.skipBits(8);
                        }
                    }
                    vorbisBitArray.skipBits(2);
                    floorNumberOfBooks = vorbisBitArray.readBits(4);
                    j2 = 0;
                    int k2 = 0;
                    for (int j4 = 0; j4 < j; j4++) {
                        j2 += classDimensions[partitionClassList[j4]];
                        while (k2 < j2) {
                            vorbisBitArray.skipBits(floorNumberOfBooks);
                            k2++;
                        }
                    }
                    break;
                default:
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("floor type greater than 1 not decodable: ");
                    stringBuilder.append(floorType);
                    throw new ParserException(stringBuilder.toString());
            }
        }
    }

    private static CodeBook readBook(VorbisBitArray bitArray) throws ParserException {
        if (bitArray.readBits(24) != 5653314) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("expected code book to start with [0x56, 0x43, 0x42] at ");
            stringBuilder.append(bitArray.getPosition());
            throw new ParserException(stringBuilder.toString());
        }
        int i;
        int dimensions = bitArray.readBits(16);
        int entries = bitArray.readBits(24);
        long[] lengthMap = new long[entries];
        boolean isOrdered = bitArray.readBit();
        int i2 = 0;
        if (isOrdered) {
            int length = bitArray.readBits(5) + 1;
            i = 0;
            while (i < lengthMap.length) {
                int num = bitArray.readBits(iLog(entries - i));
                int i3 = i;
                for (i = 0; i < num && i3 < lengthMap.length; i++) {
                    lengthMap[i3] = (long) length;
                    i3++;
                }
                length++;
                i = i3;
            }
        } else {
            boolean isSparse = bitArray.readBit();
            while (i2 < lengthMap.length) {
                if (!isSparse) {
                    lengthMap[i2] = (long) (bitArray.readBits(5) + 1);
                } else if (bitArray.readBit()) {
                    lengthMap[i2] = (long) (bitArray.readBits(5) + 1);
                } else {
                    lengthMap[i2] = 0;
                }
                i2++;
            }
        }
        int lookupType = bitArray.readBits(4);
        if (lookupType > 2) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("lookup type greater than 2 not decodable: ");
            stringBuilder2.append(lookupType);
            throw new ParserException(stringBuilder2.toString());
        }
        if (lookupType == 1 || lookupType == 2) {
            long lookupValuesCount;
            bitArray.skipBits(32);
            bitArray.skipBits(32);
            i = bitArray.readBits(4) + 1;
            bitArray.skipBits(1);
            if (lookupType != 1) {
                lookupValuesCount = (long) (entries * dimensions);
            } else if (dimensions != 0) {
                lookupValuesCount = mapType1QuantValues((long) entries, (long) dimensions);
            } else {
                lookupValuesCount = 0;
            }
            bitArray.skipBits((int) (((long) i) * lookupValuesCount));
        }
        return new CodeBook(dimensions, entries, lengthMap, lookupType, isOrdered);
    }

    private static long mapType1QuantValues(long entries, long dimension) {
        return (long) Math.floor(Math.pow((double) entries, 1.0d / ((double) dimension)));
    }
}
