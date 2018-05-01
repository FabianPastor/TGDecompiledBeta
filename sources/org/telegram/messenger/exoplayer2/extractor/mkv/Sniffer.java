package org.telegram.messenger.exoplayer2.extractor.mkv;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

final class Sniffer {
    private static final int ID_EBML = 440786851;
    private static final int SEARCH_LENGTH = 1024;
    private int peekLength;
    private final ParsableByteArray scratch = new ParsableByteArray(8);

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean sniff(ExtractorInput extractorInput) throws IOException, InterruptedException {
        long readUnsignedInt;
        Sniffer sniffer = this;
        ExtractorInput extractorInput2 = extractorInput;
        long length = extractorInput.getLength();
        long j = 1024;
        if (length != -1) {
            if (length <= 1024) {
                j = length;
            }
        }
        int i = (int) j;
        extractorInput2.peekFully(sniffer.scratch.data, 0, 4);
        sniffer.peekLength = 4;
        for (readUnsignedInt = sniffer.scratch.readUnsignedInt(); readUnsignedInt != 440786851; readUnsignedInt = ((readUnsignedInt << 8) & -256) | ((long) (sniffer.scratch.data[0] & 255))) {
            int i2 = sniffer.peekLength + 1;
            sniffer.peekLength = i2;
            if (i2 == i) {
                return false;
            }
            extractorInput2.peekFully(sniffer.scratch.data, 0, 1);
        }
        readUnsignedInt = readUint(extractorInput);
        long j2 = (long) sniffer.peekLength;
        if (readUnsignedInt != Long.MIN_VALUE) {
            if (length == -1 || j2 + readUnsignedInt < length) {
                long j3;
                while (true) {
                    j3 = j2 + readUnsignedInt;
                    if (((long) sniffer.peekLength) >= j3) {
                        break;
                    } else if (readUint(extractorInput) == Long.MIN_VALUE) {
                        return false;
                    } else {
                        length = readUint(extractorInput);
                        if (length < 0) {
                            break;
                        } else if (length > 2147483647L) {
                            break;
                        } else if (length != 0) {
                            extractorInput2.advancePeekPosition((int) length);
                            sniffer.peekLength = (int) (((long) sniffer.peekLength) + length);
                        }
                    }
                }
                return ((long) sniffer.peekLength) == j3;
            }
        }
        return false;
    }

    private long readUint(ExtractorInput extractorInput) throws IOException, InterruptedException {
        int i = 0;
        extractorInput.peekFully(this.scratch.data, 0, 1);
        int i2 = this.scratch.data[0] & 255;
        if (i2 == 0) {
            return Long.MIN_VALUE;
        }
        int i3 = 128;
        int i4 = 0;
        while ((i2 & i3) == 0) {
            i3 >>= 1;
            i4++;
        }
        i2 &= i3 ^ -1;
        extractorInput.peekFully(this.scratch.data, 1, i4);
        while (i < i4) {
            i++;
            i2 = (this.scratch.data[i] & 255) + (i2 << 8);
        }
        this.peekLength += i4 + 1;
        return (long) i2;
    }
}
