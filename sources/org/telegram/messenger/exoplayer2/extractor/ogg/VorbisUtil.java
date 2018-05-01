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

        public CodeBook(int i, int i2, long[] jArr, int i3, boolean z) {
            this.dimensions = i;
            this.entries = i2;
            this.lengthMap = jArr;
            this.lookupType = i3;
            this.isOrdered = z;
        }
    }

    public static final class CommentHeader {
        public final String[] comments;
        public final int length;
        public final String vendor;

        public CommentHeader(String str, String[] strArr, int i) {
            this.vendor = str;
            this.comments = strArr;
            this.length = i;
        }
    }

    public static final class Mode {
        public final boolean blockFlag;
        public final int mapping;
        public final int transformType;
        public final int windowType;

        public Mode(boolean z, int i, int i2, int i3) {
            this.blockFlag = z;
            this.windowType = i;
            this.transformType = i2;
            this.mapping = i3;
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

        public VorbisIdHeader(long j, int i, long j2, int i2, int i3, int i4, int i5, int i6, boolean z, byte[] bArr) {
            this.version = j;
            this.channels = i;
            this.sampleRate = j2;
            this.bitrateMax = i2;
            this.bitrateNominal = i3;
            this.bitrateMin = i4;
            this.blockSize0 = i5;
            this.blockSize1 = i6;
            this.framingFlag = z;
            this.data = bArr;
        }

        public int getApproximateBitrate() {
            return this.bitrateNominal == 0 ? (this.bitrateMin + this.bitrateMax) / 2 : this.bitrateNominal;
        }
    }

    public static int iLog(int i) {
        int i2 = 0;
        while (i > 0) {
            i2++;
            i >>>= 1;
        }
        return i2;
    }

    VorbisUtil() {
    }

    public static VorbisIdHeader readVorbisIdentificationHeader(ParsableByteArray parsableByteArray) throws ParserException {
        ParsableByteArray parsableByteArray2 = parsableByteArray;
        verifyVorbisHeaderCapturePattern(1, parsableByteArray2, false);
        long readLittleEndianUnsignedInt = parsableByteArray.readLittleEndianUnsignedInt();
        int readUnsignedByte = parsableByteArray.readUnsignedByte();
        long readLittleEndianUnsignedInt2 = parsableByteArray.readLittleEndianUnsignedInt();
        int readLittleEndianInt = parsableByteArray.readLittleEndianInt();
        int readLittleEndianInt2 = parsableByteArray.readLittleEndianInt();
        int readLittleEndianInt3 = parsableByteArray.readLittleEndianInt();
        int readUnsignedByte2 = parsableByteArray.readUnsignedByte();
        return new VorbisIdHeader(readLittleEndianUnsignedInt, readUnsignedByte, readLittleEndianUnsignedInt2, readLittleEndianInt, readLittleEndianInt2, readLittleEndianInt3, (int) Math.pow(2.0d, (double) (readUnsignedByte2 & 15)), (int) Math.pow(2.0d, (double) ((readUnsignedByte2 & PsExtractor.VIDEO_STREAM_MASK) >> 4)), (parsableByteArray.readUnsignedByte() & 1) > 0, Arrays.copyOf(parsableByteArray2.data, parsableByteArray.limit()));
    }

    public static CommentHeader readVorbisCommentHeader(ParsableByteArray parsableByteArray) throws ParserException {
        int i = 0;
        verifyVorbisHeaderCapturePattern(3, parsableByteArray, false);
        String readString = parsableByteArray.readString((int) parsableByteArray.readLittleEndianUnsignedInt());
        int length = 11 + readString.length();
        long readLittleEndianUnsignedInt = parsableByteArray.readLittleEndianUnsignedInt();
        String[] strArr = new String[((int) readLittleEndianUnsignedInt)];
        length += 4;
        while (((long) i) < readLittleEndianUnsignedInt) {
            length += 4;
            strArr[i] = parsableByteArray.readString((int) parsableByteArray.readLittleEndianUnsignedInt());
            length += strArr[i].length();
            i++;
        }
        if ((parsableByteArray.readUnsignedByte() & 1) != null) {
            return new CommentHeader(readString, strArr, length + 1);
        }
        throw new ParserException("framing bit expected to be set");
    }

    public static boolean verifyVorbisHeaderCapturePattern(int i, ParsableByteArray parsableByteArray, boolean z) throws ParserException {
        if (parsableByteArray.bytesLeft() < 7) {
            if (z) {
                return false;
            }
            z = new StringBuilder();
            z.append("too short header: ");
            z.append(parsableByteArray.bytesLeft());
            throw new ParserException(z.toString());
        } else if (parsableByteArray.readUnsignedByte() == i) {
            if (parsableByteArray.readUnsignedByte() == 118 && parsableByteArray.readUnsignedByte() == 111 && parsableByteArray.readUnsignedByte() == 114 && parsableByteArray.readUnsignedByte() == 98 && parsableByteArray.readUnsignedByte() == 105) {
                if (parsableByteArray.readUnsignedByte() == 115) {
                    return true;
                }
            }
            if (z) {
                return false;
            }
            throw new ParserException("expected characters 'vorbis'");
        } else if (z) {
            return false;
        } else {
            z = new StringBuilder();
            z.append("expected header type ");
            z.append(Integer.toHexString(i));
            throw new ParserException(z.toString());
        }
    }

    public static Mode[] readVorbisModes(ParsableByteArray parsableByteArray, int i) throws ParserException {
        int i2 = 0;
        verifyVorbisHeaderCapturePattern(5, parsableByteArray, false);
        int readUnsignedByte = parsableByteArray.readUnsignedByte() + 1;
        VorbisBitArray vorbisBitArray = new VorbisBitArray(parsableByteArray.data);
        vorbisBitArray.skipBits(parsableByteArray.getPosition() * 8);
        for (parsableByteArray = null; parsableByteArray < readUnsignedByte; parsableByteArray++) {
            readBook(vorbisBitArray);
        }
        parsableByteArray = vorbisBitArray.readBits(6) + 1;
        while (i2 < parsableByteArray) {
            if (vorbisBitArray.readBits(16) != 0) {
                throw new ParserException("placeholder of time domain transforms not zeroed out");
            }
            i2++;
        }
        readFloors(vorbisBitArray);
        readResidues(vorbisBitArray);
        readMappings(i, vorbisBitArray);
        parsableByteArray = readModes(vorbisBitArray);
        if (vorbisBitArray.readBit() != 0) {
            return parsableByteArray;
        }
        throw new ParserException("framing bit after modes not set as expected");
    }

    private static Mode[] readModes(VorbisBitArray vorbisBitArray) {
        int readBits = vorbisBitArray.readBits(6) + 1;
        Mode[] modeArr = new Mode[readBits];
        for (int i = 0; i < readBits; i++) {
            modeArr[i] = new Mode(vorbisBitArray.readBit(), vorbisBitArray.readBits(16), vorbisBitArray.readBits(16), vorbisBitArray.readBits(8));
        }
        return modeArr;
    }

    private static void readMappings(int i, VorbisBitArray vorbisBitArray) throws ParserException {
        int readBits = vorbisBitArray.readBits(6) + 1;
        for (int i2 = 0; i2 < readBits; i2++) {
            int readBits2 = vorbisBitArray.readBits(16);
            if (readBits2 != 0) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("mapping type other than 0 not supported: ");
                stringBuilder.append(readBits2);
                Log.e(str, stringBuilder.toString());
            } else {
                int readBits3;
                readBits2 = vorbisBitArray.readBit() ? vorbisBitArray.readBits(4) + 1 : 1;
                if (vorbisBitArray.readBit()) {
                    readBits3 = vorbisBitArray.readBits(8) + 1;
                    for (int i3 = 0; i3 < readBits3; i3++) {
                        int i4 = i - 1;
                        vorbisBitArray.skipBits(iLog(i4));
                        vorbisBitArray.skipBits(iLog(i4));
                    }
                }
                if (vorbisBitArray.readBits(2) != 0) {
                    throw new ParserException("to reserved bits must be zero after mapping coupling steps");
                }
                if (readBits2 > 1) {
                    for (readBits3 = 0; readBits3 < i; readBits3++) {
                        vorbisBitArray.skipBits(4);
                    }
                }
                for (int i5 = 0; i5 < readBits2; i5++) {
                    vorbisBitArray.skipBits(8);
                    vorbisBitArray.skipBits(8);
                    vorbisBitArray.skipBits(8);
                }
            }
        }
    }

    private static void readResidues(VorbisBitArray vorbisBitArray) throws ParserException {
        int readBits = vorbisBitArray.readBits(6) + 1;
        for (int i = 0; i < readBits; i++) {
            if (vorbisBitArray.readBits(16) > 2) {
                throw new ParserException("residueType greater than 2 is not decodable");
            }
            int i2;
            vorbisBitArray.skipBits(24);
            vorbisBitArray.skipBits(24);
            vorbisBitArray.skipBits(24);
            int readBits2 = vorbisBitArray.readBits(6) + 1;
            vorbisBitArray.skipBits(8);
            int[] iArr = new int[readBits2];
            for (i2 = 0; i2 < readBits2; i2++) {
                iArr[i2] = ((vorbisBitArray.readBit() ? vorbisBitArray.readBits(5) : 0) * 8) + vorbisBitArray.readBits(3);
            }
            for (i2 = 0; i2 < readBits2; i2++) {
                for (int i3 = 0; i3 < 8; i3++) {
                    if ((iArr[i2] & (1 << i3)) != 0) {
                        vorbisBitArray.skipBits(8);
                    }
                }
            }
        }
    }

    private static void readFloors(VorbisBitArray vorbisBitArray) throws ParserException {
        int readBits = vorbisBitArray.readBits(6) + 1;
        for (int i = 0; i < readBits; i++) {
            int readBits2 = vorbisBitArray.readBits(16);
            int readBits3;
            switch (readBits2) {
                case 0:
                    vorbisBitArray.skipBits(8);
                    vorbisBitArray.skipBits(16);
                    vorbisBitArray.skipBits(16);
                    vorbisBitArray.skipBits(6);
                    vorbisBitArray.skipBits(8);
                    readBits3 = vorbisBitArray.readBits(4) + 1;
                    for (readBits2 = 0; readBits2 < readBits3; readBits2++) {
                        vorbisBitArray.skipBits(8);
                    }
                    break;
                case 1:
                    int readBits4;
                    readBits3 = vorbisBitArray.readBits(5);
                    int[] iArr = new int[readBits3];
                    int i2 = -1;
                    for (readBits2 = 0; readBits2 < readBits3; readBits2++) {
                        iArr[readBits2] = vorbisBitArray.readBits(4);
                        if (iArr[readBits2] > i2) {
                            i2 = iArr[readBits2];
                        }
                    }
                    int[] iArr2 = new int[(i2 + 1)];
                    for (i2 = 0; i2 < iArr2.length; i2++) {
                        iArr2[i2] = vorbisBitArray.readBits(3) + 1;
                        readBits4 = vorbisBitArray.readBits(2);
                        if (readBits4 > 0) {
                            vorbisBitArray.skipBits(8);
                        }
                        for (int i3 = 0; i3 < (1 << readBits4); i3++) {
                            vorbisBitArray.skipBits(8);
                        }
                    }
                    vorbisBitArray.skipBits(2);
                    int readBits5 = vorbisBitArray.readBits(4);
                    int i4 = 0;
                    i2 = i4;
                    readBits4 = i2;
                    while (i4 < readBits3) {
                        i2 += iArr2[iArr[i4]];
                        while (readBits4 < i2) {
                            vorbisBitArray.skipBits(readBits5);
                            readBits4++;
                        }
                        i4++;
                    }
                    break;
                default:
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("floor type greater than 1 not decodable: ");
                    stringBuilder.append(readBits2);
                    throw new ParserException(stringBuilder.toString());
            }
        }
    }

    private static CodeBook readBook(VorbisBitArray vorbisBitArray) throws ParserException {
        if (vorbisBitArray.readBits(24) != 5653314) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("expected code book to start with [0x56, 0x43, 0x42] at ");
            stringBuilder.append(vorbisBitArray.getPosition());
            throw new ParserException(stringBuilder.toString());
        }
        int i;
        int readBits = vorbisBitArray.readBits(16);
        int readBits2 = vorbisBitArray.readBits(24);
        long[] jArr = new long[readBits2];
        boolean readBit = vorbisBitArray.readBit();
        long j = 0;
        int i2 = 0;
        if (readBit) {
            int readBits3 = vorbisBitArray.readBits(5) + 1;
            i = 0;
            while (i < jArr.length) {
                int readBits4 = vorbisBitArray.readBits(iLog(readBits2 - i));
                int i3 = i;
                for (i = 0; i < readBits4 && i3 < jArr.length; i++) {
                    jArr[i3] = (long) readBits3;
                    i3++;
                }
                readBits3++;
                i = i3;
            }
        } else {
            boolean readBit2 = vorbisBitArray.readBit();
            while (i2 < jArr.length) {
                if (!readBit2) {
                    jArr[i2] = (long) (vorbisBitArray.readBits(5) + 1);
                } else if (vorbisBitArray.readBit()) {
                    jArr[i2] = (long) (vorbisBitArray.readBits(5) + 1);
                } else {
                    jArr[i2] = 0;
                }
                i2++;
            }
        }
        i2 = vorbisBitArray.readBits(4);
        if (i2 > 2) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("lookup type greater than 2 not decodable: ");
            stringBuilder2.append(i2);
            throw new ParserException(stringBuilder2.toString());
        }
        if (i2 == 1 || i2 == 2) {
            vorbisBitArray.skipBits(32);
            vorbisBitArray.skipBits(32);
            i = vorbisBitArray.readBits(4) + 1;
            vorbisBitArray.skipBits(1);
            if (i2 != 1) {
                j = (long) (readBits2 * readBits);
            } else if (readBits != 0) {
                j = mapType1QuantValues((long) readBits2, (long) readBits);
            }
            vorbisBitArray.skipBits((int) (j * ((long) i)));
        }
        return new CodeBook(readBits, readBits2, jArr, i2, readBit);
    }

    private static long mapType1QuantValues(long j, long j2) {
        return (long) Math.floor(Math.pow((double) j, 1.0d / ((double) j2)));
    }
}
