package org.telegram.messenger.exoplayer2.extractor.mp4;

import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

final class Sniffer {
    private static final int[] COMPATIBLE_BRANDS = new int[]{Util.getIntegerCodeForString("isom"), Util.getIntegerCodeForString("iso2"), Util.getIntegerCodeForString("iso3"), Util.getIntegerCodeForString("iso4"), Util.getIntegerCodeForString("iso5"), Util.getIntegerCodeForString("iso6"), Util.getIntegerCodeForString(VisualSampleEntry.TYPE3), Util.getIntegerCodeForString(VisualSampleEntry.TYPE6), Util.getIntegerCodeForString(VisualSampleEntry.TYPE7), Util.getIntegerCodeForString("mp41"), Util.getIntegerCodeForString("mp42"), Util.getIntegerCodeForString("3g2a"), Util.getIntegerCodeForString("3g2b"), Util.getIntegerCodeForString("3gr6"), Util.getIntegerCodeForString("3gs6"), Util.getIntegerCodeForString("3ge6"), Util.getIntegerCodeForString("3gg6"), Util.getIntegerCodeForString("M4V "), Util.getIntegerCodeForString("M4A "), Util.getIntegerCodeForString("f4v "), Util.getIntegerCodeForString("kddi"), Util.getIntegerCodeForString("M4VP"), Util.getIntegerCodeForString("qt  "), Util.getIntegerCodeForString("MSNV")};
    private static final int SEARCH_LENGTH = 4096;

    public static boolean sniffFragmented(ExtractorInput extractorInput) throws IOException, InterruptedException {
        return sniffInternal(extractorInput, true);
    }

    public static boolean sniffUnfragmented(ExtractorInput extractorInput) throws IOException, InterruptedException {
        return sniffInternal(extractorInput, false);
    }

    private static boolean sniffInternal(ExtractorInput extractorInput, boolean z) throws IOException, InterruptedException {
        boolean z2;
        boolean z3;
        ExtractorInput extractorInput2 = extractorInput;
        long length = extractorInput.getLength();
        long j = -1;
        if (length == -1 || length > 4096) {
            length = 4096;
        }
        int i = (int) length;
        ParsableByteArray parsableByteArray = new ParsableByteArray(64);
        boolean z4 = false;
        int i2 = 0;
        int i3 = i2;
        while (i2 < i) {
            parsableByteArray.reset(8);
            extractorInput2.peekFully(parsableByteArray.data, 0, 8);
            long readUnsignedInt = parsableByteArray.readUnsignedInt();
            int readInt = parsableByteArray.readInt();
            int i4 = 16;
            if (readUnsignedInt == 1) {
                extractorInput2.peekFully(parsableByteArray.data, 8, 8);
                parsableByteArray.setLimit(16);
                readUnsignedInt = parsableByteArray.readUnsignedLongToLong();
            } else {
                if (readUnsignedInt == 0) {
                    long length2 = extractorInput.getLength();
                    if (length2 != j) {
                        readUnsignedInt = (length2 - extractorInput.getPosition()) + ((long) 8);
                    }
                }
                i4 = 8;
            }
            long j2 = (long) i4;
            if (readUnsignedInt >= j2) {
                i2 += i4;
                if (readInt != Atom.TYPE_moov) {
                    if (readInt != Atom.TYPE_moof) {
                        if (readInt != Atom.TYPE_mvex) {
                            if ((((long) i2) + readUnsignedInt) - j2 >= ((long) i)) {
                                break;
                            }
                            int i5 = (int) (readUnsignedInt - j2);
                            i2 += i5;
                            if (readInt == Atom.TYPE_ftyp) {
                                if (i5 < 8) {
                                    return false;
                                }
                                parsableByteArray.reset(i5);
                                extractorInput2.peekFully(parsableByteArray.data, 0, i5);
                                i5 /= 4;
                                for (int i6 = 0; i6 < i5; i6++) {
                                    if (i6 == 1) {
                                        parsableByteArray.skipBytes(4);
                                    } else if (isCompatibleBrand(parsableByteArray.readInt())) {
                                        i3 = 1;
                                        break;
                                    }
                                }
                                if (i3 == 0) {
                                    return false;
                                }
                            } else if (i5 != 0) {
                                extractorInput2.advancePeekPosition(i5);
                            }
                            j = -1;
                        }
                    }
                    z2 = true;
                    z3 = true;
                    break;
                }
            } else {
                return false;
            }
        }
        z2 = true;
        z3 = false;
        if (i3 != 0 && z == r0) {
            z4 = z2;
        }
        return z4;
    }

    private static boolean isCompatibleBrand(int i) {
        if ((i >>> 8) == Util.getIntegerCodeForString("3gp")) {
            return true;
        }
        for (int i2 : COMPATIBLE_BRANDS) {
            if (i2 == i) {
                return true;
            }
        }
        return false;
    }

    private Sniffer() {
    }
}
