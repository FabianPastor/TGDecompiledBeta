package org.telegram.messenger.exoplayer2.extractor.mp4;

import com.coremedia.iso.boxes.sampleentry.VisualSampleEntry;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

final class Sniffer {
    private static final int[] COMPATIBLE_BRANDS = new int[]{Util.getIntegerCodeForString("isom"), Util.getIntegerCodeForString("iso2"), Util.getIntegerCodeForString("iso3"), Util.getIntegerCodeForString("iso4"), Util.getIntegerCodeForString("iso5"), Util.getIntegerCodeForString("iso6"), Util.getIntegerCodeForString(VisualSampleEntry.TYPE3), Util.getIntegerCodeForString(VisualSampleEntry.TYPE6), Util.getIntegerCodeForString(VisualSampleEntry.TYPE7), Util.getIntegerCodeForString("mp41"), Util.getIntegerCodeForString("mp42"), Util.getIntegerCodeForString("3g2a"), Util.getIntegerCodeForString("3g2b"), Util.getIntegerCodeForString("3gr6"), Util.getIntegerCodeForString("3gs6"), Util.getIntegerCodeForString("3ge6"), Util.getIntegerCodeForString("3gg6"), Util.getIntegerCodeForString("M4V "), Util.getIntegerCodeForString("M4A "), Util.getIntegerCodeForString("f4v "), Util.getIntegerCodeForString("kddi"), Util.getIntegerCodeForString("M4VP"), Util.getIntegerCodeForString("qt  "), Util.getIntegerCodeForString("MSNV")};
    private static final int SEARCH_LENGTH = 4096;

    public static boolean sniffFragmented(ExtractorInput input) throws IOException, InterruptedException {
        return sniffInternal(input, true);
    }

    public static boolean sniffUnfragmented(ExtractorInput input) throws IOException, InterruptedException {
        return sniffInternal(input, false);
    }

    private static boolean sniffInternal(ExtractorInput input, boolean fragmented) throws IOException, InterruptedException {
        boolean z;
        ExtractorInput extractorInput = input;
        long inputLength = input.getLength();
        long j = -1;
        long j2 = 4096;
        if (inputLength != -1) {
            if (inputLength <= 4096) {
                j2 = inputLength;
            }
        }
        int bytesToSearch = (int) j2;
        ParsableByteArray buffer = new ParsableByteArray(64);
        boolean z2 = false;
        boolean foundGoodFileType = false;
        int bytesSearched = 0;
        boolean isFragmented = false;
        while (bytesSearched < bytesToSearch) {
            int headerSize = 8;
            buffer.reset(8);
            extractorInput.peekFully(buffer.data, z2, 8);
            long atomSize = buffer.readUnsignedInt();
            int atomType = buffer.readInt();
            if (atomSize == 1) {
                headerSize = 16;
                extractorInput.peekFully(buffer.data, 8, 8);
                buffer.setLimit(16);
                atomSize = buffer.readUnsignedLongToLong();
            } else if (atomSize == 0) {
                long endPosition = input.getLength();
                if (endPosition != j) {
                    atomSize = (endPosition - input.getPosition()) + ((long) 8);
                }
            }
            if (atomSize >= ((long) headerSize)) {
                bytesSearched += headerSize;
                if (atomType != Atom.TYPE_moov) {
                    int i;
                    if (atomType == Atom.TYPE_moof) {
                        i = headerSize;
                        z2 = false;
                        z = true;
                    } else if (atomType == Atom.TYPE_mvex) {
                        i = headerSize;
                        z2 = false;
                        z = true;
                    } else if ((((long) bytesSearched) + atomSize) - ((long) headerSize) >= ((long) bytesToSearch)) {
                        z2 = false;
                        break;
                    } else {
                        int atomDataSize = (int) (atomSize - ((long) headerSize));
                        bytesSearched += atomDataSize;
                        if (atomType != Atom.TYPE_ftyp) {
                            z2 = false;
                            if (atomDataSize != 0) {
                                extractorInput.advancePeekPosition(atomDataSize);
                            }
                        } else if (atomDataSize < 8) {
                            return false;
                        } else {
                            buffer.reset(atomDataSize);
                            extractorInput.peekFully(buffer.data, 0, atomDataSize);
                            int brandsCount = atomDataSize / 4;
                            for (int i2 = 0; i2 < brandsCount; i2++) {
                                if (i2 == 1) {
                                    buffer.skipBytes(4);
                                } else if (isCompatibleBrand(buffer.readInt())) {
                                    foundGoodFileType = true;
                                    break;
                                }
                            }
                            if (!foundGoodFileType) {
                                return false;
                            }
                            z2 = false;
                        }
                        j = -1;
                    }
                    isFragmented = true;
                    break;
                }
                j = -1;
                z2 = false;
            } else {
                return false;
            }
        }
        z = true;
        if (!foundGoodFileType) {
            boolean z3 = fragmented;
        } else if (fragmented == isFragmented) {
            z2 = z;
        }
        return z2;
    }

    private static boolean isCompatibleBrand(int brand) {
        if ((brand >>> 8) == Util.getIntegerCodeForString("3gp")) {
            return true;
        }
        for (int compatibleBrand : COMPATIBLE_BRANDS) {
            if (compatibleBrand == brand) {
                return true;
            }
        }
        return false;
    }

    private Sniffer() {
    }
}
